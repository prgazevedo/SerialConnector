package RAK811.comms;

import RAK811.gui.MainApplication;
import com.fazecast.jSerialComm.SerialPort;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CommsManager  {

    /** The the serial port we shall use */
    private SerialPort m_comPort=null;
    /** The Message queue that holds serial messages*/
    private  MessageRecordQueue m_queue=null;
    /** List of serial ports */
    private List<SerialPort> m_serialPortlist=null;
    /** Map of serial ports and names */
    private HashMap<String, SerialPort> m_portMap=null;


    public SerialPort getM_comPort() { return m_comPort; }

    public void setM_comPort(SerialPort m_comPort) {
        this.m_comPort = m_comPort;
    }

    public  MessageRecordQueue.MessageRecordList getSentMessages() { return m_queue.getAllSentMessageRecords(); }

    private MainApplication m_mainApplication;

    public SerialWriter getM_serialWriter() {
        return m_serialWriter;
    }

    private SerialWriter m_serialWriter;


    /** The logger we shall use */
    private final static Logger logger =  LogManager.getLogger(CommsManager.class);

    public void writeLog(org.apache.logging.log4j.Level messageLevel,String message){ logger.log(messageLevel,message); }



    public void initialize() {

        initializePortList();

    }

    public CommsManager(MainApplication mainApplication) {
        m_queue = new MessageRecordQueue(String.valueOf(this.toString()));
        m_portMap = new HashMap<String, SerialPort>();
        m_serialPortlist = new ArrayList<SerialPort>();
        m_mainApplication = mainApplication;
        initialize();
        m_serialWriter = new SerialWriter();
    }


    public List<SerialPort> getM_serialPortlist() {
        return m_serialPortlist;
    }

    public ArrayList<String> getM_serialPortlistNames() {
        return new ArrayList<>(m_portMap.keySet());
    }

    private void initializePortList(){
        m_serialPortlist = Arrays.asList(SerialPort.getCommPorts());
        for (SerialPort serialP : m_serialPortlist) {
            m_portMap.put(serialP.getSystemPortName(), serialP);
            writeLog(Level.INFO," Ports in Map: "+serialP.getSystemPortName() );
        }
    }


    public boolean setCurrentSerialPortName(String portName) {
        if (m_portMap.containsKey(portName)) {
            writeLog(Level.INFO, " setCurrentSerialPortName: Found port in portMap: " + portName);
            m_comPort = m_portMap.get(portName);
            return true;

        }else return false;
    }




    // NEW BEGIN
    private void initializePort() {
        //set params
        setPortDefaultParams(m_comPort);
        //add event listeners
        addEventListeners(m_comPort);
    }

    public void sendMessage(String cmd)
    {
        Message payload = new Message(cmd, Message.MessageType.CMD);
        m_serialWriter.writeStream(payload.getM_message());
        m_queue.writeinQueue(payload, true);
    }

    public void sendRawMessage(String cmd)
    {
        Message payload = new Message(cmd);
        m_serialWriter.writeStream(cmd);
        m_queue.writeinQueue(payload, true);
    }


    public void receiveMessage(String cmd)
    {

        Message message = new Message(cmd, Message.MessageType.None);
        if (message != null)
        {
            m_queue.writeinQueue(message,false);
        }
        if(!m_queue.isEmpty()) m_queue.logContents();
        //m_mainApplication.displayFrame(message.getM_messagePayload());
        m_mainApplication.displayFrameLater(message.getM_messagePayload());

    }

    //NEW END


    public boolean openPort( String portID)
    {
        SerialPort sPort;
        writeLog(Level.INFO," openPort: " + portID);
        if(m_portMap.containsKey(portID)){
            writeLog(Level.INFO," openPort: Found port in portMap: "+portID);
            sPort = m_portMap.get(portID);
            // Try to open port, terminate execution if not possible
            if (sPort.openPort()) {
                m_comPort=sPort;
                writeLog(Level.INFO," "+m_comPort.getSystemPortName() + " successfully opened.");
                initializePort();
                writeLog(Level.INFO," "+m_comPort.getSystemPortName() + " successfully initialized.");
                m_serialWriter.setComPort(m_comPort);
                writeLog(Level.INFO," SerialWriter has output Stream to: "+m_comPort.getSystemPortName());
                return true;

            } else {
                writeLog(Level.WARN, "failed to open: " +portID);
                return false;

            }
        }
        else
        {
            writeLog(Level.WARN, " openPort: Could not find port in portMap: "+portID);
            return false;

        }

    }


    public boolean isComPortOpen(){
        if(m_comPort!=null) {
            if (m_comPort.isOpen()) {
                return true;
            }
        }
        return false;
    }

    /**
     * This should be called when you stop using the port.
     * This will prevent port locking on platforms like Linux.
     */
    public synchronized void close() {
        if (m_comPort != null) {
            m_comPort.removeDataListener();
            m_comPort.closePort();
        }
    }



    private void setPortDefaultParams(SerialPort comPort)
    {

        writeLog(Level.INFO," setPortDefaultParams called");
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
        // set port parameters
        comPort.setBaudRate(CommsProperties.DATA_RATE);
        comPort.setNumDataBits(CommsProperties.DATABITS);
        comPort.setNumStopBits(CommsProperties.STOPBITS);
        comPort.setParity(CommsProperties.PARITY);
    }

    private void addEventListeners(SerialPort comPort)
    {
        writeLog(Level.INFO," addEventListeners called");
        SerialListener listener = new SerialListener(this);
        listener.initialize(comPort);
        comPort.addDataListener(listener);
        writeLog(Level.INFO," addEventListeners has new listener");

    }





}
