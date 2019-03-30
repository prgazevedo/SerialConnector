package RAK811.utils;

import RAK811.comms.CommsManager;
import RAK811.comms.MessageRecord;
import RAK811.comms.MessageRecordQueue;
import com.thoughtworks.xstream.XStream;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class FileManager {

    /** The logger we shall use */
    private final static Logger logger =  LogManager.getLogger(CommsManager.class);

    public FileManager() {
    }

    public void writeLog(org.apache.logging.log4j.Level messageLevel,String message){ logger.log(messageLevel,"[Log]:"+message); }

    public void logInitialize(){
        Configurator.setAllLevels(LogManager.getRootLogger().getName(), PropertiesManager.LOG_LEVEL);
    }

    public MessageRecordQueue.MessageRecordList importFromXML(FileReader fileReader){
        XStream xstream=new XStream();
        MessageRecordQueue.MessageRecordList arrayToLoad = new MessageRecordQueue.MessageRecordList();
        xstream.addImplicitCollection(arrayToLoad.getClass(), "arraytoLoad", MessageRecord.class);
        arrayToLoad = (MessageRecordQueue.MessageRecordList) xstream.fromXML(fileReader);
        return arrayToLoad;
    }

    public String exportToXML(MessageRecordQueue.MessageRecordList list){

        XStream xstream=new XStream();
        xstream.alias("MessageRecord", MessageRecord.class);
        xstream.alias("MessageRecordList", MessageRecordQueue.MessageRecordList.class);
        xstream.addImplicitCollection(MessageRecordQueue.MessageRecordList.class, "list");
        return xstream.toXML(list);


    }

    public FileReader loadFromFile(String filepath) {
        FileReader fileReader=null;
        try {
             fileReader = new FileReader(filepath);  // load our xml file
        } catch (IOException ex) {
            writeLog(Level.ERROR, "Error reading from file"+ex.toString());
        }
        return fileReader;
    }


    public void saveToFile(String content, File file) {
        try {
            PrintWriter writer;
            writer = new PrintWriter(file);
            writer.println(content);
            writer.close();
        } catch (IOException ex) {
            writeLog(Level.ERROR, "Error saving to file"+ex.toString());
        }
    }
}
