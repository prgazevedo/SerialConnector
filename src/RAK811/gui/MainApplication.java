package RAK811.gui;


import RAK811.comms.CommsManager;
import RAK811.comms.MessageRecord;
import RAK811.comms.MessageRecordQueue;
import RAK811.comms.SyntaxManager;
import RAK811.utils.FileManager;
import RAK811.utils.PropertiesManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;


public class MainApplication extends Application implements DisplayMessage, ProcessAction {


    public PropertiesManager getM_PropertiesManager() {
        return m_PropertiesManager;
    }

    private PropertiesManager m_PropertiesManager;
    private UserInterfaceController m_controller;
    private CommsManager m_CommsManager;
    private SyntaxManager m_SyntaxManager;
    private FileManager m_FileManager;
    private Stage m_primaryStage;

    /** The logger we shall use */
    private final static Logger logger =  LogManager.getLogger(CommsManager.class);

    public void writeLog(org.apache.logging.log4j.Level messageLevel,String message){ logger.log(messageLevel,"[Log]:"+message); }

    public void logInitialize(){
        Configurator.setAllLevels(LogManager.getRootLogger().getName(), PropertiesManager.LOG_LEVEL);
    }
    @Override
    public void start(Stage primaryStage) {
        logInitialize();
        m_PropertiesManager = new PropertiesManager();
        m_CommsManager = new CommsManager(this);
        m_SyntaxManager = new SyntaxManager( this);
        m_SyntaxManager.initialize();
        m_CommsManager.initialize();
        m_primaryStage=primaryStage;
        m_FileManager= new FileManager();
        FXMLLoader fxmlLoader = null;

        try {
            fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource(m_PropertiesManager.FXML_NAME).openStream());
            primaryStage.setTitle("Serial Controller");
            Scene scene = new Scene(root);
            //Scene scene = new Scene(root,m_PropertiesManager.WIDTH,m_PropertiesManager.HEIGHT);
            scene.getStylesheets().add(getClass().getResource(m_PropertiesManager.CSS_NAME).toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.show();
            m_controller=fxmlLoader.getController();

        } catch (IOException e) {
            e.printStackTrace();
        }



        m_controller.setProcessAction(this);
        m_controller.initializePorts();
        m_controller.initializeCmds();
        // Display version.
        m_controller.displayLogMsg(m_PropertiesManager.APPLICATION_NAME+"- Version: "+m_PropertiesManager.VERSION);


    }




    /**
     * For DisplayMessage interface.
     *
     * @param message
     */
    @Override
    public void displayLog(String message) {

        m_controller.displayLogMsg(message);

    }

    /**
     * For DisplayMessage interface.
     *
     */
    @Override
    public void displayLogLater(String message) {

        Platform.runLater(new Runnable() {

            @Override
            public void run() {

                m_controller.displayLogMsg(message);

            }

        });
    }

    /**
     * For DisplayMessage interface.
     *
     * @param message
     */
    @Override
    public void displayFrame(String message) {

        m_controller.displayFrame(message);

    }

    /**
     * For DisplayMessage interface.
     *
     */
    @Override
    public void displayFrameLater(String message) {

        Platform.runLater(new Runnable() {

            @Override
            public void run() {

                m_controller.displayFrame(message);

            }

        });
    }

    /**
     * For ProcessAction interface.
     *
     * @return ArrayList<String> with serial ports
     */

    public ArrayList<String> getSerialPortList() {
        // Display list of available serial ports.
        return m_CommsManager.getM_serialPortlistNames();

    }

    @Override
    public Set<String> getCommandList() {
        return m_SyntaxManager.getListCommands();

    }

    /**
     * For ProcessAction interface.
     *
     * @param serialPortName
     */
    @Override
    public void setSerialPortValue(String serialPortName) {

        boolean result = m_CommsManager.setCurrentSerialPortName(serialPortName);
        if (result ) {
            if(m_CommsManager.openPort(serialPortName)) {
                // Enable buttons.
                Platform.runLater(new Runnable() {

                    @Override
                    public void run() {

                        //m_controller.enableActions(true);

                    }

                });
            }
            else {
                writeLog(Level.INFO, "Error Opening the Serial Port");
            }
        }
        else{
            writeLog(Level.INFO, "Error Setting the Serial Port");
        }

    }

    /**
     * For ProcessAction interface.
     */
    @Override
    public String getCmd(String cmd) {
        return m_SyntaxManager.getCmdType(cmd);
    }
    /**
     * For ProcessAction interface.
     */
    @Override
    public String getCmdParameters(String cmd) {
        return m_SyntaxManager.getCmdParameters(cmd) ;
    }
    /**
     * For ProcessAction interface.
     */
    @Override
    public void sendMsg(String cmd, String... params) {
        String message;
        if(params!=null) {
           // m_SyntaxManager.buildRAKParameterValues(params).toArray()
        }

        message = m_SyntaxManager.callRAKCmd(cmd, params );
        writeLog(Level.INFO, "sendMsg: Message to the Serial Port"+message);
        m_CommsManager.sendMessage(message);
        displayLog(message+" message sent");
        writeLog(Level.INFO, "sendMsg: displayLog called"+message);

    }

    public void saveToFile(){

        FileChooser fileChooser = new FileChooser();

        //Set extension filter for text files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        //Show save file dialog
        File file = fileChooser.showSaveDialog(m_primaryStage);

        if (file != null) {
            String toSave = m_FileManager.exportToXML(m_CommsManager.getSentMessages());
            m_FileManager.saveToFile(toSave, file);
        }
    }

    @Override
    public ArrayList<MessageRecord> loadFromFile() {
        FileChooser fileChooser = new FileChooser();

        //Set extension filter for text files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        //Show save file dialog
        File file = fileChooser.showOpenDialog(m_primaryStage);
        MessageRecordQueue.MessageRecordList load = new MessageRecordQueue.MessageRecordList();
        if (file != null) {
            try {
                load =  m_FileManager.importFromXML(new FileReader(file));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        writeLog(Level.INFO, load.toString());

        return load.getList();

    }

    //Do not use main in javafx use start
    public static void main(String[] args) {
        launch(args);
    }


}
