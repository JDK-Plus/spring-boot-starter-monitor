package plus.jdk.monitor.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "plus.jdk.monitor.garbage")
public class MonitorGarbageProperties {

    /**
     * 是否开启该组件
     */
    private boolean enabled = false;

    /**
     * 每多少秒打点一次
     */
    private Integer fixRate = 1;
}
