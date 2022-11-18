package plus.jdk.monitor.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import plus.jdk.monitor.annotation.MonitorDotComponent;
import plus.jdk.monitor.common.IGarbageDotCallback;
import plus.jdk.monitor.common.IMemoryDotCallback;

@Data
@AllArgsConstructor
public class GarbageDotModel {

    private IGarbageDotCallback dotCallback;

    private MonitorDotComponent dotService;
}
