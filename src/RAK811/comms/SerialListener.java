/*
 * Copyright 2018 Pedro Azevedo (prgazevedo@gmail.com)
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package RAK811.comms;


import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortPacketListener;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class SerialListener implements SerialPortPacketListener
{

    /** The logger we shall use */
    private final static Logger logger =  LogManager.getLogger(CommsManager.class);

    public void writeLog(org.apache.logging.log4j.Level messageLevel,String message){ logger.log(messageLevel,message); }

    private CommsManager m_CommsManager;
    private SerialPort m_serialPort=null;

    StringBuilder rawMessage = new StringBuilder();
    private boolean isNameSet=false;



    public SerialListener(CommsManager commsManager) {
         m_CommsManager=commsManager;

    }

    public void initialize(SerialPort serial){
        m_serialPort = serial;

    }

    public final void initThreadName(){
        Thread.currentThread().setName("SerialListener");
        isNameSet=true;
    }

    @Override
    public int getListeningEvents() { return SerialPort.LISTENING_EVENT_DATA_AVAILABLE; }

    @Override
    public int getPacketSize() { return 80; }

    @Override
    public void serialEvent(SerialPortEvent event)
    {
        if(!isNameSet) initThreadName();
        if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
            return;
        }
        try{
            byte[] buffer = new byte[m_serialPort.bytesAvailable()];
            m_serialPort.readBytes(buffer, buffer.length);
            //byte[] newData = event.getReceivedData();
            writeLog(Level.INFO,"[SerialListener]: serialEvent called");
            if(buffer.length>0) readRawData(buffer);


        }
        catch (Exception rEx)
        {
            writeLog(Level.ERROR,"[SerialListener]:serialEvent - exception:"+rEx.toString());
        }
    }

    private void readRawData(byte[] buffer){
        writeLog(Level.INFO,"[SerialConnector]: data received. Size: "+buffer.length+ "\n");
        String prefix="[SerialListener]: readRawData from serial: ";
        String output="";
        try {

            for (byte b : buffer)
            {
                if (CommsProperties.isMessageSplitter(b) && rawMessage.length() > 0)
                {
                    String toProcess = rawMessage.toString();
                    writeLog(Level.INFO,"[SerialConnector]:Received a rawMessage:[{}]"+ toProcess);

                    m_CommsManager.receiveMessage(new String(buffer));

                    rawMessage.setLength(0);
                }
                else if (!CommsProperties.isMessageSplitter(b))
                {

                    rawMessage.append((char) b);
                }
                else if (CommsProperties.isMessageOversize(rawMessage.length() ) )
                {
                    writeLog(Level.INFO,"[SerialConnector]:Serial receive buffer size reached to MAX level[{} chars], " +
                                    "Now clearing the buffer. Existing data:[{}]"+
                            CommsProperties.getSerialDataMaxSize()+
                            rawMessage.toString());
                    rawMessage.setLength(0);
                }
                else {
                    writeLog(Level.INFO,"[SerialConnector]:Received MESSAGE_SPLITTER and current rawMessage length is ZERO! Nothing to do");
                }
            }

        }
        catch(Exception e){
            e.printStackTrace();
            writeLog(Level.ERROR,"[SerialConnector]:Exception: "+e);
            rawMessage.setLength(0);

        }
    }



}


