package plus.jdk.monitor.annotation;

import org.springframework.context.annotation.Import;
import plus.jdk.monitor.selector.JVMMemoryMonitorSelector;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Import(JVMMemoryMonitorSelector.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableMonitor {
}
