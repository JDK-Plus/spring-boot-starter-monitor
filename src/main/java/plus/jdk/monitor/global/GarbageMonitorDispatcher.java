package plus.jdk.monitor.global;

import com.sun.management.GarbageCollectionNotificationInfo;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;
import plus.jdk.monitor.annotation.MonitorDotComponent;
import plus.jdk.monitor.common.GarbageNotificationFilter;
import plus.jdk.monitor.common.IGarbageDotCallback;
import plus.jdk.monitor.model.GarbageDotModel;
import plus.jdk.monitor.properties.MonitorGarbageProperties;

import javax.management.NotificationEmitter;
import javax.management.NotificationFilter;
import javax.management.openmbean.CompositeData;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@NoArgsConstructor
public class GarbageMonitorDispatcher {

    private ApplicationContext applicationContext;

    private final List<GarbageDotModel> garbageDotModels = new ArrayList<>();

    public GarbageMonitorDispatcher(MonitorGarbageProperties garbageProperties,
                                    ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void registerGarbageNotificationListener() {
        List<GarbageCollectorMXBean> garbageCollectorMXBeanList = ManagementFactory.getGarbageCollectorMXBeans();
        for(GarbageCollectorMXBean garbageCollectorMXBean:garbageCollectorMXBeanList) {
            NotificationEmitter emitter = (NotificationEmitter)garbageCollectorMXBean;
            NotificationFilter filter = new GarbageNotificationFilter();
            emitter.addNotificationListener((notification, handback) -> {
                GarbageCollectionNotificationInfo notificationInfo = GarbageCollectionNotificationInfo.from((CompositeData) notification.getUserData());
                for(GarbageDotModel garbageDotModel:garbageDotModels) {
                    try{
                        garbageDotModel.getDotCallback().doMonitorDot(notification, handback, notificationInfo);
                    }catch (Exception | Error e) {
                        log.info("handleNotification failed, message{}", e.getMessage());
                    }
                }
            }, filter, garbageCollectorMXBean);
        }
    }

    public void findGarbageDotCallbackService() {
        if (!CollectionUtils.isEmpty(garbageDotModels)) {
            return;
        }
        String[] beanNames =
                this.applicationContext.getBeanNamesForType(IGarbageDotCallback.class);
        for (String beanName : beanNames) {
            IGarbageDotCallback dotCallback = this.applicationContext.getBean(beanName, IGarbageDotCallback.class);
            MonitorDotComponent dotService = this.applicationContext.findAnnotationOnBean(beanName, MonitorDotComponent.class);
            if (dotService == null) {
                continue;
            }
            garbageDotModels.add(new GarbageDotModel(dotCallback, dotService));
        }
    }
}
