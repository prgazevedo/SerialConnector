package RAK811.gui;

import RAK811.comms.CommsManager;
import RAK811.utils.PropertiesManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

public class UserInterfaceController implements javafx.fxml.Initializable, javafx.event.EventHandler<ActionEvent> {

    private ProcessAction m_processAction;


    public void setProcessAction(ProcessAction processAction) { m_processAction = processAction; }



    @FXML private ComboBox<String> serialPortCB;
    //selection of user command
    @FXML private ComboBox<String> commandCB;
    @FXML private Button exitB;
    @FXML private Button sendBtn;
    //user command plus parameters
    @FXML private ComboBox<String> userCommandsCB;
    private int m_expectedParams;
    @FXML private ListView<String> recFramesLV;
    @FXML private ListView<String> logMsgsLV;
    @FXML private Button saveB;
    @FXML private Button loadB;

    private ObservableList<String> serialPortList = FXCollections.observableArrayList();
    private void setOnActionserialPortCB(ActionEvent event){
        String serialName = serialPortCB.getSelectionModel().getSelectedItem();
        m_processAction.setSerialPortValue(serialName);
        writeLog(Level.INFO, "ComboBoxAction selected: "+serialName);
    }




    private ObservableList<String> cmdList = FXCollections.observableArrayList();
    private void setOnActioncommandCB(ActionEvent event){
        String methodName = commandCB.getSelectionModel().getSelectedItem();
        String cmdName = m_processAction.getCmd(methodName);
        String cmdParameters = m_processAction.getCmdParameters(methodName);
        if (cmdParameters.equals("")) m_expectedParams=0;
        else m_expectedParams = parseString(cmdParameters).length;
        writeLog(Level.INFO, "ComboBoxAction selected: "+cmdName+ " with parameters: "+cmdParameters+ "number of params is: "+ m_expectedParams);
    }

    private ObservableList<String> cmdInputList = FXCollections.observableArrayList();
    private void setOnActionUsercommandCB(ActionEvent event){
        String selectedCommand = userCommandsCB.getSelectionModel().getSelectedItem();
        writeLog(Level.INFO, "ComboBoxAction selected: "+selectedCommand);
    }

    private void setOnActionexitBtn(ActionEvent event){
        writeLog(Level.INFO, "exit Button pressed");
        Platform.exit();
    }

    private void setOnActionsaveB(ActionEvent actionEvent) {
        writeLog(Level.INFO, "save Button pressed");

        m_processAction.saveToFile();
    }

    private void setOnActionloadB(ActionEvent actionEvent) {
        writeLog(Level.INFO, "load Button pressed");

        m_processAction.sendMsgS(m_processAction.loadFromFile());

    }

    private void setOnActionsendCommandBtn(ActionEvent event){
        writeLog(Level.INFO, "SendCmd Button pressed");
        String methodName = commandCB.getSelectionModel().getSelectedItem();
        String userCommand = userCommandsCB.getEditor().getText();
        cmdInputList.add(userCommand);
        String parameters = userCommand;
        if(parameters.equals("")) {
            m_processAction.sendMsg(methodName, m_expectedParams, null);

        }
        else {
            m_processAction.sendMsg(methodName, m_expectedParams,parseString(parameters));
        }
        //userCommandsString.clear();
    }

    private String[] parseString(String stext){
        String delims = "[+\\<\\>\\-*/\\^ ]+"; // so the delimiters are:  + - * / ^ space
        String[] tokens = stext.trim().substring(1).split(delims);
        return tokens;
    }

    private ObservableList<String> displayedFrames = FXCollections.observableArrayList();
    private ListViewMessages recFrames;

    private ObservableList<String> displayedLogMsgs= FXCollections.observableArrayList();
    private ListViewMessages logMsgs;


    private SimpleDateFormat timeFormat;

    /** The logger we shall use */
    private final static Logger logger =  LogManager.getLogger(CommsManager.class);

    public void writeLog(org.apache.logging.log4j.Level messageLevel,String message){ logger.log(messageLevel,"[Log]:"+message); }


    /**
     * Called by FXML loader.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        writeLog(Level.INFO, "initialize called");
        //setup comboBox
        serialPortList.add("none");
        serialPortCB.setItems(serialPortList);
        serialPortCB.setOnAction(this::setOnActionserialPortCB);
        cmdList.add("none");
        commandCB.setItems(cmdList);
        commandCB.setOnAction(this::setOnActioncommandCB);
        userCommandsCB.setItems(cmdInputList);
        userCommandsCB.setOnAction(this::setOnActionUsercommandCB);
        // action for SendCommand button
        sendBtn.setOnAction(this::setOnActionsendCommandBtn);
        // action for exit button
        exitB.setOnAction(this::setOnActionexitBtn);
        //action for save button
        saveB.setOnAction(this::setOnActionsaveB);
        //action for load button
        loadB.setOnAction(this::setOnActionloadB);
        timeFormat = new SimpleDateFormat("HH:mm:ss.SSS");

        displayedFrames.add("Displayed Frames");
        // To display latest received frames.
        recFramesLV.setItems(displayedFrames);
        recFrames = new ListViewMessages(displayedFrames, 1000);
        recFramesLV.setMaxHeight(Double.MAX_VALUE);
        // To display latest log messages.
        logMsgsLV.setItems(displayedLogMsgs);
        logMsgs = new ListViewMessages(displayedLogMsgs, 1000);




    }



    public void initializePorts(){
        serialPortList.setAll(m_processAction.getSerialPortList());
        serialPortCB.setItems(serialPortList);
    }
    public void initializeCmds(){
        cmdList.setAll(m_processAction.getCommandList());
        commandCB.setItems(cmdList);
    }


    /**
     * For EventHandler<ActionEvent> interface.
     * Is called only by the combo box, when the user selects a serial port.
     */
    @Override
    public void handle(ActionEvent event) {
            /*
            if (m_processAction != null) {
                m_processAction.setSerialPortValue(serialPortCB.getValue());
                // User is no more allowed to modify serial port.
                serialPortCB.setDisable(true);
            } else {
                displayLogMsg("internal error: processAction is null");
            }
            */

    }


    /**
     *
     * @param frame
     */
    public void displayFrame(String frame) {

        Date currentTime = new Date();
        String s = timeFormat.format(currentTime);
        recFrames.addMessage(s + PropertiesManager.SEPARATOR + frame);

    }

    public void displayLogMsg(String logMsg) {

        Date currentTime = new Date();
        String s = timeFormat.format(currentTime);
        logMsgs.addMessage(s + PropertiesManager.SEPARATOR + logMsg);

    }






}

