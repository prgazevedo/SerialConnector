package RAK811.utils;

import RAK811.comms.CommsManager;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

public class PropertiesManager  {


    /** Application name */
    public static final String APPLICATION_NAME = "RAK811";
    public final static String SEPARATOR = " - ";
    public static final String VERSION = "0.1";
    /** Application size */
    public static final int WIDTH = 600;
    /** Application size */
    public static final int HEIGHT = 400;
    public static final String FXML_NAME = "UserInterfaceController.fxml";
    public static final String CSS_NAME = "application.css";


    /** Application debug level */
    public static final  org.apache.logging.log4j.Level LOG_LEVEL = Level.INFO ;

    private static String m_linuxPortName;
    private static String m_OSXPortName;


    private String m_WorkingDir;

    /** The logger we shall use */
    private final static Logger logger =  LogManager.getLogger(CommsManager.class);

    public void writeLog(org.apache.logging.log4j.Level messageLevel,String message){ logger.log(messageLevel,"[Raspberry]:"+message); }


    public void logInitialize(){
        Configurator.setAllLevels(LogManager.getRootLogger().getName(), LOG_LEVEL);
    }

    public void initialize() {

        logInitialize();
        readProperties();
        m_WorkingDir = System.getProperty("user.dir");
    }

    public PropertiesManager() {
        initialize();
    }

    public void readProperties(){
        /*
        Properties prop = new Properties();
        try {
            System.out.println("Working Directory = " + m_WorkingDir);
            File file = new File("./application_properties.xml");
            FileInputStream fileInputStream = new FileInputStream(file);
            System.out.println("To Read the file"+file.getCanonicalPath());
            prop.loadFromXML(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        m_linuxPortName = prop.getProperty("LinuxPortName");
        m_OSXPortName = prop.getProperty("OSXPortName");
        writeLog(Level.INFO,"OSXPortName:"+m_OSXPortName);
        writeLog(Level.INFO,"linuxPortName:"+m_linuxPortName);
        */
    }

    public String getM_WorkingDir() {
        return m_WorkingDir;
    }


    public  String getPortName() throws Exception{
        if(OSValidator.isMac()) return m_OSXPortName;
        else if(OSValidator.isUnix()) return m_linuxPortName;
        else {
            throw new Exception("OS could not be detected - portName for retrieved");

        }
    }
}
