package plus.jdk.monitor.common;

import com.sun.management.GarbageCollectionNotificationInfo;

import javax.management.Notification;

public interface IGarbageDotCallback {

    void doMonitorDot(Notification notification, Object handback, GarbageCollectionNotificationInfo notificationInfo);
}
