package plus.jdk.monitor.global;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import plus.jdk.monitor.annotation.EnableMemoryMonitor;
import plus.jdk.monitor.properties.MonitorMemoryProperties;

@Slf4j
@Configuration
@EnableMemoryMonitor
@ConditionalOnProperty(prefix = "plus.jdk.monitor.memory", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(MonitorMemoryProperties.class)
public class MonitorMemoryAutoConfiguration {


}
