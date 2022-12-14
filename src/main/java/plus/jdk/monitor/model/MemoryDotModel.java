package plus.jdk.monitor.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import plus.jdk.monitor.annotation.MonitorDotComponent;
import plus.jdk.monitor.common.IMemoryDotCallback;

@Data
@AllArgsConstructor
public class MemoryDotModel {

    private IMemoryDotCallback dotCallback;

    private MonitorDotComponent dotService;
}
