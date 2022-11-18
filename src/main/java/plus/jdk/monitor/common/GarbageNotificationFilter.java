package plus.jdk.monitor.common;
import com.sun.management.GarbageCollectionNotificationInfo;

import javax.management.Notification;
import javax.management.NotificationFilter;

public class GarbageNotificationFilter implements NotificationFilter {

    /**
     * 除了类型为垃圾回收之外的通知都不会接收
     *
     * @param notification 通知
     * @return
     */
    public boolean isNotificationEnabled(Notification notification) {
        return GarbageCollectionNotificationInfo.GARBAGE_COLLECTION_NOTIFICATION.equals(notification.getType());
    }
}