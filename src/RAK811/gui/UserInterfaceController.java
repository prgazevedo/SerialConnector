package RAK811.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import static javafx.scene.input.KeyCode.SEPARATOR;

public class UserInterfaceController implements javafx.fxml.Initializable, javafx.event.EventHandler<ActionEvent> {

    private ProcessAction m_processAction;


    public void setProcessAction(ProcessAction processAction) { m_processAction = processAction; }



    @FXML private ComboBox<String> serialPortCB;
    @FXML private ComboBox<String> commandCB;
    @FXML private Button exitB;
    @FXML private Button sendSetCommandBtn;
    @FXML private TextField UserCommands;
    @FXML private ListView<String> recFramesLV;
    @FXML private ListView<String> logMsgsLV;

    private ObservableList<String> serialPortList = FXCollections.observableArrayList();
    private void setOnActionserialPortCB(ActionEvent event){
        String serialName = serialPortCB.getSelectionModel().getSelectedItem();
        m_processAction.setSerialPortValue(serialName);
        System.out.println("ComboBoxAction selected: "+serialName);
    }

    private ObservableList<String> cmdList = FXCollections.observableArrayList();
    private void setOnActioncommandCB(ActionEvent event){
        String cmdName = commandCB.getSelectionModel().getSelectedItem();
        //@TODO m_processAction.setCmd(cmdName);
        System.out.println("ComboBoxAction selected: "+cmdName);
    }
    private void setOnActionexitB(ActionEvent event){
        // get a handle to the stage
        // Stage stage = (Stage) exitB.getScene().getWindow();
        //stage.close();
        System.out.println("exit Button pressed");
        Platform.exit();
    }




    private ObservableList<String> displayedFrames = FXCollections.observableArrayList();
    private ListViewMessages recFrames;

    private ObservableList<String> displayedLogMsgs= FXCollections.observableArrayList();
    private ListViewMessages logMsgs;


    private SimpleDateFormat timeFormat;

    /**
     * Called by FXML loader.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {


        //setup comboBox
        serialPortList.add("none");
        serialPortCB.setItems(serialPortList);
        cmdList.add("none");
        commandCB.setItems(cmdList);
        serialPortCB.setOnAction(this::setOnActionserialPortCB);
        timeFormat = new SimpleDateFormat("HH:mm:ss.SSS");
        System.out.println("initialize called");
        // action for button
        exitB.setOnAction(this::setOnActionexitB);

            /*
            // To display latest received frames.
            recFramesLV = new ListView<>(displayedFrames);
            recFramesLV.setItems(displayedFrames);
            recFrames = new ListViewMessages(displayedFrames, MAX_NB_FRAMES);

            // To display latest log messages.
            logMsgsLV = new ListView<>(displayedLogMsgs);
            logMsgsLV.setItems(displayedLogMsgs);
            logMsgs = new ListViewMessages(displayedLogMsgs, MAX_NB_LOGMSGS);



            */





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
     * To call once serial port is setup successfully.
     */
    public void enableActions(boolean enable) {

        sendSetCommandBtn.setDisable(!enable);

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
        recFrames.addMessage(s + SEPARATOR + frame);

    }

    public void displayLogMsg(String logMsg) {

        Date currentTime = new Date();
        String s = timeFormat.format(currentTime);
        //logMsgs.addMessage(s + SEPARATOR + logMsg);

    }






}

