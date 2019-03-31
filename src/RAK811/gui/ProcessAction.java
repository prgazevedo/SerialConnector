package RAK811.gui;

import RAK811.comms.MessageRecord;

import java.util.ArrayList;
import java.util.Set;

public interface ProcessAction {

    /**
     * How to process actions.
     */
        public ArrayList<String> getSerialPortList();

        public Set<String> getCommandList();

        public void setSerialPortValue(String serialPortName);

        public void saveToFile();

        public ArrayList<MessageRecord> loadFromFile();

        public void sendMsg(String cmd, int nParams, String... params);

        public void sendMsgS(ArrayList<MessageRecord> cmd);

        public String getCmd(String cmdName);

        public String getCmdParameters(String cmdName);
}
