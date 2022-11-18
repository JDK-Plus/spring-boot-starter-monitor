package plus.jdk.monitor.global;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import plus.jdk.monitor.annotation.EnableGarbageMonitor;
import plus.jdk.monitor.annotation.EnableMemoryMonitor;
import plus.jdk.monitor.properties.MonitorGarbageProperties;

@Slf4j
@Configuration
@EnableGarbageMonitor
@ConditionalOnProperty(prefix = "plus.jdk.monitor.garbage", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(MonitorGarbageProperties.class)
public class MonitorGarbageAutoConfiguration {


}
