package plus.jdk.monitor.global;

import com.sun.tools.attach.VirtualMachine;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.util.CollectionUtils;
import plus.jdk.monitor.annotation.MemoryMonitorDotCompont;
import plus.jdk.monitor.common.IMonitorMemoryDotCallback;
import plus.jdk.monitor.model.MemoryDotModel;
import plus.jdk.monitor.properties.MonitorMemoryProperties;
import sun.jvmstat.monitor.*;
import sun.tools.attach.HotSpotVirtualMachine;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.File;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@NoArgsConstructor
public class MemoryMonitorDispatcher implements SchedulingConfigurer {

    private MonitorMemoryProperties monitorMemoryProperties;

    private ApplicationContext applicationContext;

    private final List<MemoryDotModel> memoryDotModels = new ArrayList<>();

    private String currentProcessId = null;

    private String currentProcessJMXUri = null;

    public MemoryMonitorDispatcher(MonitorMemoryProperties monitorMemoryProperties,
                                   ApplicationContext applicationContext) {
        this.monitorMemoryProperties = monitorMemoryProperties;
        this.applicationContext = applicationContext;
    }

    public void monitorDot() {
        for (MemoryDotModel memoryDotModel : memoryDotModels) {
            try{
                doMemoryDot(memoryPoolMXBean -> memoryDotModel.getDotCallback().doDot(memoryPoolMXBean));
            }catch (Exception | Error e) {
                e.printStackTrace();
                log.error("{}", e.getMessage());
            }
        }
    }

    public void initProcessInfo() throws ClassNotFoundException {
        if(currentProcessId != null) {
            return;
        }
        Class<?> mainClass = getMainClass();
        currentProcessId = getProcessId(mainClass);
        currentProcessJMXUri = bindJMXAgentAndGetJMXUri(currentProcessId);
    }

    public void findDotCallbackService() {
        if(!CollectionUtils.isEmpty(memoryDotModels)) {
            return;
        }
        String[] beanNames =
                this.applicationContext.getBeanNamesForAnnotation(MemoryMonitorDotCompont.class);
        for (String beanName : beanNames) {
            IMonitorMemoryDotCallback dotCallback = this.applicationContext.getBean(beanName, IMonitorMemoryDotCallback.class);
            MemoryMonitorDotCompont dotService = this.applicationContext.findAnnotationOnBean(beanName, MemoryMonitorDotCompont.class);
            if (dotService == null) {
                continue;
            }
            memoryDotModels.add(new MemoryDotModel(dotCallback, dotService));
        }
    }

    public static String getStack(String pid) {
        VirtualMachine virtualMachine = null;
        InputStream is = null;
        try {
            virtualMachine = VirtualMachine.attach(pid);
            HotSpotVirtualMachine machine = (HotSpotVirtualMachine) virtualMachine;
            is = machine.remoteDataDump(new String[]{});
            return IOUtils.toString(is, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(is);
        }
        return null;
    }

    public String heapHisto(String pid) {
        VirtualMachine virtualMachine = null;
        InputStream is = null;
        try {
            virtualMachine = VirtualMachine.attach(pid);
            HotSpotVirtualMachine machine = (HotSpotVirtualMachine) virtualMachine;
            is = machine.heapHisto(new String[]{});
            return IOUtils.toString(is, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(is);
        }
        return null;
    }

    public void doMemoryDot(IMonitorMemoryDotCallback callback) {
        doMemoryDot(currentProcessJMXUri, callback);
    }

    public void doMemoryDot(String jmxAddress,IMonitorMemoryDotCallback callback) {
        try {
            JMXServiceURL target = new JMXServiceURL(jmxAddress);
            final JMXConnector connector = JMXConnectorFactory.connect(target);
            final MBeanServerConnection mBeanServerConnection = connector.getMBeanServerConnection();
            TimeUnit.SECONDS.sleep(1);
            List<MemoryPoolMXBean> memoryPoolMXBeans = ManagementFactory.getPlatformMXBeans(mBeanServerConnection, MemoryPoolMXBean.class);
            for (MemoryPoolMXBean memoryPoolMXBean : memoryPoolMXBeans) {
                try {
                    callback.doDot(memoryPoolMXBean);
                } catch (Exception | Error ignored) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized String bindJMXAgentAndGetJMXUri(String pid) {
        VirtualMachine virtualMachine = null;
        try {
            virtualMachine = VirtualMachine.attach(pid);
//            HotSpotVirtualMachine machine = (HotSpotVirtualMachine) virtualMachine;
            String javaHome = virtualMachine.getSystemProperties().getProperty("java.home");
            String jmxAgent = String.join(File.separator, new String[]{javaHome, "lib", "management-agent.jar"});
            virtualMachine.loadAgent(jmxAgent, "com.sun.management.jmxremote");
            Properties properties = virtualMachine.getAgentProperties();
            String address = (String) properties.get("com.sun.management.jmxremote.localConnectorAddress");
            virtualMachine.detach();
            return address;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getProcessId(Class<?> mainClass) {
        MonitoredHost monitoredHost;
        Set<Integer> activeVmProcessIds;
        try {
            monitoredHost = MonitoredHost.getMonitoredHost(new HostIdentifier((String) null));
            activeVmProcessIds = monitoredHost.activeVms();
            MonitoredVm mvm = null;
            for (Integer vmPid : activeVmProcessIds) {
                try {
                    mvm = monitoredHost.getMonitoredVm(new VmIdentifier(vmPid.toString()));
                    String mvmMainClass = MonitoredVmUtil.mainClass(mvm, true);
                    if (mainClass.getName().equals(mvmMainClass)) {
                        return String.valueOf(vmPid);
                    }
                } finally {
                    if (mvm != null) {
                        mvm.detach();
                    }
                }
            }
        } catch (URISyntaxException | MonitorException e) {
            throw new InternalError(e.getMessage());
        }
        return null;
    }

    public Class<?> getMainClass() throws ClassNotFoundException {
        StackTraceElement[] stackTraceElements = new RuntimeException().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTraceElements) {
            if ("main".equals(stackTraceElement.getMethodName())) {
                return Class.forName(stackTraceElement.getClassName());
            }
        }
        return null;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        long fixRate = monitorMemoryProperties.getFixRate() * 1000;
        Trigger eventTrigger = new PeriodicTrigger(fixRate);
        taskRegistrar.addTriggerTask(this::monitorDot, eventTrigger);
    }

    public static void main(String[] args) throws ClassNotFoundException {
        MemoryMonitorDispatcher dispatcher = new MemoryMonitorDispatcher();
        String pid = String.valueOf(dispatcher.getProcessId(dispatcher.getMainClass()));
        String jxmAddress = dispatcher.bindJMXAgentAndGetJMXUri(pid);
        dispatcher.doMemoryDot(jxmAddress, (memoryPoolMXBean) -> {
            String name = memoryPoolMXBean.getName();
            String[] memoryManagerNames = memoryPoolMXBean.getMemoryManagerNames();
            MemoryType type = memoryPoolMXBean.getType();
            MemoryUsage usage = memoryPoolMXBean.getUsage();
            MemoryUsage peakUsage = memoryPoolMXBean.getPeakUsage();
            memoryPoolMXBean.getCollectionUsage();
            System.out.println(name + ":");
            System.out.println("    managers: " + String.join(" , ", memoryManagerNames));
            System.out.println("    type: " + type.toString());
            System.out.print("    usage: " + usage.toString());
            System.out.println("    Percentage: " + ((double) usage.getUsed() / usage.getCommitted()));
            System.out.println("    peakUsage: " + peakUsage.toString());
            System.out.println("");
        });
        dispatcher.heapHisto(pid);
        dispatcher.getStack(pid);
    }
}
