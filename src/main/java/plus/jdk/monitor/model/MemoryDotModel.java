package plus.jdk.monitor.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import plus.jdk.monitor.annotation.MemoryMonitorDotCompont;
import plus.jdk.monitor.common.IMonitorMemoryDotCallback;

@Data
@AllArgsConstructor
public class MemoryDotModel {

    private IMonitorMemoryDotCallback dotCallback;

    private MemoryMonitorDotCompont dotService;
}
