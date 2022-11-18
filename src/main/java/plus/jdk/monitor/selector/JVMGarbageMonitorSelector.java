package plus.jdk.monitor.selector;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.support.WebApplicationObjectSupport;
import plus.jdk.monitor.global.GarbageMonitorDispatcher;
import plus.jdk.monitor.global.GarbageMonitorLifecycle;
import plus.jdk.monitor.global.MemoryMonitorDispatcher;
import plus.jdk.monitor.global.MemoryMonitorLifecycle;
import plus.jdk.monitor.properties.MonitorGarbageProperties;
import plus.jdk.monitor.properties.MonitorMemoryProperties;

@Configuration
public class JVMGarbageMonitorSelector extends WebApplicationObjectSupport implements BeanFactoryAware {

    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Bean
    public GarbageMonitorDispatcher getJVMMonitorService(MonitorGarbageProperties properties) {
        return new GarbageMonitorDispatcher(properties, getApplicationContext());
    }

    @Bean
    public GarbageMonitorLifecycle getMemoryMonitorLifecycle(GarbageMonitorDispatcher dispatcher) {
        return new GarbageMonitorLifecycle(dispatcher);
    }
}
