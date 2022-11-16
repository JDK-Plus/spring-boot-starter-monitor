package plus.jdk.monitor.global;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import plus.jdk.monitor.annotation.EnableMonitor;
import plus.jdk.monitor.properties.MonitorMemoryProperties;

@Slf4j
@Configuration
@EnableMonitor
@ConditionalOnProperty(prefix = "plus.jdk.monitor", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(MonitorMemoryProperties.class)
public class MonitorAutoConfiguration {


}
