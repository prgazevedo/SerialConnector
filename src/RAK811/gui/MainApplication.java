package RAK811.gui;


import RAK811.comms.CommsManager;
import RAK811.comms.RAK811Syntax;
import RAK811.properties.PropertiesManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;


public class MainApplication extends Application implements DisplayMessage, ProcessAction {

    /** The logger we shall use */
    private final static Logger logger =  LogManager.getLogger(CommsManager.class);

    public void writeLog(org.apache.logging.log4j.Level messageLevel,String message){ logger.log(messageLevel,"[Log]:"+message); }

    public PropertiesManager getM_PropertiesManager() {
        return m_PropertiesManager;
    }

    private PropertiesManager m_PropertiesManager;
    private UserInterfaceController m_controller;
    private CommsManager m_CommsManager;
    private RAK811Syntax m_SyntaxManager;


    @Override
    public void start(Stage primaryStage) {

        m_PropertiesManager = new PropertiesManager();
        m_CommsManager = new CommsManager(this);
        m_SyntaxManager = new RAK811Syntax( this);
        m_SyntaxManager.initialize();
        m_CommsManager.initialize();

        FXMLLoader fxmlLoader = null;

        try {
            fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource(m_PropertiesManager.FXML_NAME).openStream());
            primaryStage.setTitle("Serial Controller");
            Scene scene = new Scene(root,m_PropertiesManager.WIDTH,m_PropertiesManager.HEIGHT);
            scene.getStylesheets().add(getClass().getResource(m_PropertiesManager.CSS_NAME).toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.show();
            m_controller=fxmlLoader.getController();

        } catch (IOException e) {
            e.printStackTrace();
        }


        // Display version.
        m_controller.displayLogMsg(m_PropertiesManager.APPLICATION_NAME+"- Version: "+m_PropertiesManager.VERSION);

        m_controller.setProcessAction(this);
        m_controller.initializePorts();
        m_controller.initializeCmds();
/*

        try {
            fxmlLoader = new FXMLLoader();
            writeLog(Level.INFO, "Opening Properties Manager FXML file: "+ m_PropertiesManager.FXML_NAME);
            //Parent root = fxmlLoader.load(getClass().getResource(m_PropertiesManager.FXML_NAME).openStream());
            Parent root = fxmlLoader.load(getClass().getResource("UserInterfaceController.fxml").openStream());
            Scene scene = new Scene(root,m_PropertiesManager.WIDTH,m_PropertiesManager.HEIGHT);
            writeLog(Level.INFO, "Opening Properties Manager CSS file: "+ m_PropertiesManager.CSS_NAME);
            scene.getStylesheets().add(getClass().getResource(m_PropertiesManager.CSS_NAME).toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }

        try {
            m_controller = (UserInterfaceController)fxmlLoader.getController();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Display version.
        //m_controller.displayLogMsg(m_PropertiesManager.APPLICATION_NAME+"- Version: "+m_PropertiesManager.VERSION);

        m_controller.setProcessAction(this);
*/

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
    public ArrayList<String> getCommandList() {
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

                        m_controller.enableActions(true);

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
    public void sendMsg() {
        String message = "test";
        m_CommsManager.sendMessage( message);
        displayLog(message+" message sent");

    }



    //Do not use main in javafx use start
    public static void main(String[] args) {
        launch(args);
    }


}
