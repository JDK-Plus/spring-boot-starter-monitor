package plus.jdk.monitor.global;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.SmartLifecycle;

@Slf4j
public class GarbageMonitorLifecycle implements SmartLifecycle {

    private boolean running = false;

    private final GarbageMonitorDispatcher garbageMonitorDispatcher;

    public GarbageMonitorLifecycle(GarbageMonitorDispatcher garbageMonitorDispatcher) {
        this.garbageMonitorDispatcher = garbageMonitorDispatcher;
    }

    @SneakyThrows
    @Override
    public void start() {
        garbageMonitorDispatcher.findGarbageDotCallbackService();
        garbageMonitorDispatcher.registerGarbageNotificationListener();
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
