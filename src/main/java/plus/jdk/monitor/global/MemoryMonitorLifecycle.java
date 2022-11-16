package plus.jdk.monitor.global;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.SmartLifecycle;

@Slf4j
public class MemoryMonitorLifecycle implements SmartLifecycle {

    private boolean running = false;

    private MemoryMonitorDispatcher memoryMonitorDispatcher;

    public MemoryMonitorLifecycle(MemoryMonitorDispatcher memoryMonitorDispatcher) {
        this.memoryMonitorDispatcher = memoryMonitorDispatcher;
    }

    @SneakyThrows
    @Override
    public void start() {
        memoryMonitorDispatcher.findDotCallbackService();
        memoryMonitorDispatcher.initProcessInfo();
        running = true;
    }

    @Override
    public void stop() {
        running = false;
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}
