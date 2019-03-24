package RAK811.comms;

import com.fazecast.jSerialComm.SerialPort;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.OutputStream;




public class SerialWriter {


    /** The the serial port we shall use */
    private static SerialPort m_comPort=null;
    /** The output stream to the port */
    private static OutputStream m_outputStream=null;
    private static Logger log =  LogManager.getLogger(SerialListener.class);




    public SerialWriter(){

    }


    public void setComPort(SerialPort serialPort){
        try {
            m_comPort=serialPort;
            m_outputStream = m_comPort.getOutputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * WriteStream
     */
    public static synchronized void writeStream(String cmd) {
        if (m_comPort != null) {
            log.info("Writing to serial:"+cmd);
            try {
                m_outputStream.write(cmd.getBytes()); // Write to serial
                //m_outputStream.flush();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }









}
