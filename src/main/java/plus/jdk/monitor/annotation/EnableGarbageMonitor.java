package plus.jdk.monitor.annotation;

import org.springframework.context.annotation.Import;
import plus.jdk.monitor.selector.JVMGarbageMonitorSelector;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Import(JVMGarbageMonitorSelector.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableGarbageMonitor {
}
