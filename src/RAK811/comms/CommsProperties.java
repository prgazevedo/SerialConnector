package RAK811.comms;

public class CommsProperties {

    /** Cmd messages separators */
    public static final String CMD_SEPARATOR=",";
    public static final String CMD_TERMINATOR=";";
    /** Seconds to block while waiting  */
    public static final int DEFAULT_SLEEP = 1;
    /** Default bits per second for COM port. */
    public static final int DATA_RATE = 115200;
    /** Data bits to be sent: We shall use 8-N-1: one start bit, the eight data bits, and the one stop bit */
    public static final int DATABITS = 8;
    /** Parity: Acceptable values are NO_PARITY, EVEN_PARITY, ODD_PARITY, MARK_PARITY, and SPACE_PARITY. */
    public static final int PARITY = 0;
    /** Stop bits to be sent */
    public static final int STOPBITS = 1;
    /** Maximum size of message */
    public static final int SERIAL_DATA_MAX_SIZE = 1000;
    /** Acceptable Message splitter chars for serial message */
    private static final byte MESSAGE_SPLITTERS[] = {
            '\n',
            '\r'
    };







    public static int getSerialDataMaxSize() {
        return SERIAL_DATA_MAX_SIZE;
    }


    /** Returns true if byte is Message splitter in serial message */
    public static final boolean isMessageSplitter(byte b){
        for(int i=0; i<MESSAGE_SPLITTERS.length;i++)
        {
            if(b==MESSAGE_SPLITTERS[i]) return true;
        }
        return false;
    }
    public static final boolean isMessageOversize(int buffersize) {
        if (buffersize >= SERIAL_DATA_MAX_SIZE) return true;
        else return false;

    }
}
