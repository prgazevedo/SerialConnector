package RAK811.gui;

import java.util.ArrayList;

public interface ProcessAction {

    /**
     * How to process actions.
     */
        public ArrayList<String> getSerialPortList();

        public ArrayList<String> getCommandList();

        public void setSerialPortValue(String serialPortName);

        public void sendMsg();


}
