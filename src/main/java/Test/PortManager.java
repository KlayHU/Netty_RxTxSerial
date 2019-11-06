package Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: KlayHu
 * @create: 2019/11/5 22:15
 **/
public class PortManager {
    private static GetSerialPorts getSerialPorts;

    public static Map<String, GetSerialPorts> portMap = new HashMap<>();

    public void run() {
        List<String> systemPort = GetSerialPorts.getSystemPort();
        for (int i = 0; i <= systemPort.size() / 2; ) {
            getSerialPorts = new GetSerialPorts(systemPort.get(i));
            getSerialPorts.run();
            portMap.put(systemPort.get(i), getSerialPorts);
            i += 2;
        }

    }

}
