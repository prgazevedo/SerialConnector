package RAK811.gui;

import java.util.ArrayList;
import java.util.Set;

public interface ProcessAction {

    /**
     * How to process actions.
     */
        public ArrayList<String> getSerialPortList();

        public Set<String> getCommandList();

        public void setSerialPortValue(String serialPortName);

        public void sendMsg();

        public String getCmd(String cmdName);
}
