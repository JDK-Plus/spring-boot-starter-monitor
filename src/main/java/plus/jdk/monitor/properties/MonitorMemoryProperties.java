package plus.jdk.monitor.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import plus.jdk.monitor.common.IMonitorMemoryDotCallback;

import java.util.ArrayList;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "plus.jdk.monitor")
public class MonitorMemoryProperties {

    /**
     * 是否开启该组件
     */
    private boolean enabled = false;

    /**
     * 每多少秒打点一次
     */
    private Integer fixRate = 1;
}
