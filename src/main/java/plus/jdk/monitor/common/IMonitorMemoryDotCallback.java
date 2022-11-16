package plus.jdk.monitor.common;

import java.lang.management.MemoryPoolMXBean;

public interface IMonitorMemoryDotCallback {

    void doDot(MemoryPoolMXBean memoryPoolMXBean);
}
