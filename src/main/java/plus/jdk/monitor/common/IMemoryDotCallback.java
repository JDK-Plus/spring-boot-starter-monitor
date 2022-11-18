package plus.jdk.monitor.common;

import java.lang.management.MemoryPoolMXBean;

public interface IMemoryDotCallback {

    void doMonitorDot(MemoryPoolMXBean memoryPoolMXBean);
}
