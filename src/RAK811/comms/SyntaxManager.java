package RAK811.comms;

import RAK811.gui.MainApplication;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Level;

import java.lang.annotation.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;


public class SyntaxManager {

    //In order to fill in hashMap of cmds
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD})
    public @interface cmdEnum {
        public String name();
        public eCmdType value();
    }



    public final String at = "at+";
    public  enum eCmdType {

        join, //           lora_join
        version, //        lora_version
        band, //lora_band
        reset,     //      lora_reset,
        boot,      //      start_boot,
        signal,   //       lora_signal,
        link_check,  //   lora_link_check,
        get_config,  //    lora_read_config,
        reload,      //    lora_reload,
        set_config,   //   lora_write_config,
        rf_config,     //get/set p2p config
        rxc,        //enable report to host
        txc,        //enable report to host
        tx_stop,
        rx_stop,
        send,     //       lora_send,
        uart,       //     lora_uart_config,
        sleep,     //      lora_sleep,
        mode,
        dr,           //       lora_tx_dr,
        status;
    }

    public final String at_recv = "at+recv=";
    public  enum eRecvType {
        STATUS_RECV_DATA("0,0,0"),
        STATUS_TX_COMFIRMED  ("1,0,0"),
        STATUS_TX_UNCOMFIRMED  ("2,0,0"),
        STATUS_JOINED_SUCCESS  ("3,0,0"),
        STATUS_JOINED_FAILED  ("4,0,0"),
        STATUS_TX_TIMEOUT  ("5,0,0"),
        STATUS_RX2_TIMEOUT  ("6,0,0"),
        STATUS_DOWNLINK_REPEATED  ("7,0,0"),
        STATUS_WAKE_UP  ("8,0,0"),
        STATUS_P2PTX_COMPLETE  ("9,0,0"),
        STATUS_UNKNOWN  ("100,0,0");
        private String values;
          eRecvType(String type){
              this.values=type;
          }

        public String getValues() {
            return values;
        }
    }


    static final int LoRaWAN = 0;
    static final int LoRaP2P = 1;
    static final int OTAA = 0;
    static final int ABP = 1;

    //method signature --> method
    private HashMap<String, Method> callbackMethodMap;
    //method name --> parameters
    private HashMap<String, LinkedHashMap<String, Parameter>> callbackParameterMap;
    //uses annotations
    //method name (annotation) --> cmdType(annotation) *
    private HashMap<String,eCmdType> cmdMethodMap;
    //Used for list of commands *
    //method signature --> method name (annotation)
    private HashMap<String,String> listCommands;
    //method signature --> parameter string *
    private HashMap<String,String> listParameters;

    //for combo box
    public Set<String> getListCommands() { return listCommands.keySet(); }

    //for text label
    public String getCmdType(String cmd){
        return cmdMethodMap.get(listCommands.get(cmd)).toString();
    }
    //For text field
    public String getCmdParameters(String cmd){
        return listParameters.get(cmd);
    }


    public ArrayList<Object> buildRAKParameterValues( String... parameterValues){
        ArrayList<Object> List = new ArrayList<>();
        for(String s: parameterValues){
            List.add(s);
        }
        return List;
    }

    public String callRAKCmd(String signature, Object... parameterValues){
        Method method =  callbackMethodMap.get(signature);
         String message = "";
        try {
           if (parameterValues!=null) {
               message = (String) method.invoke(this, parameterValues);
               writeLog(Level.INFO, "callRAKCmd called for:"+method.getName()+" Parameters: "+ parameterValues.toString());
           }
           else {
               message = (String) method.invoke(this);
               writeLog(Level.INFO, "callRAKCmd called for:"+method.getName()+" No Parameters ");

           }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return message;
    }

    /** The logger we shall use */
    private final static Logger logger =  LogManager.getLogger(CommsManager.class);

    public void writeLog(org.apache.logging.log4j.Level messageLevel,String message){ logger.log(messageLevel,"[Log]:"+message); }


    private MainApplication m_mainApplication;
    public SyntaxManager(MainApplication mainApplication) {
        callbackMethodMap = new HashMap<>();
        callbackParameterMap = new HashMap<>();
        cmdMethodMap = new HashMap<>();
        listCommands = new HashMap<>();
        listParameters = new HashMap<>();
       m_mainApplication=mainApplication;

    }
    public void initialize() {
        Method[] methods = this.getClass().getMethods();
        for(Method method: methods){
            String signature= ReflectUtil.getSignature(method);
            String parametersString= ReflectUtil.getParametersNamesAsString(method);
            if(signature.startsWith("RAK_")) {
                callbackMethodMap.put(signature,method);
                LinkedHashMap<String,Parameter> listOfPairs = new LinkedHashMap<>();
                for(Parameter p:method.getParameters()){
                   listOfPairs.put(p.getName(),p);
                }
                callbackParameterMap.put(method.getName(),listOfPairs);
                Annotation annotation = method.getAnnotation(cmdEnum.class);
                cmdMethodMap.put( ((cmdEnum) annotation).name(), ((cmdEnum) annotation).value());
                listCommands.put(signature,((cmdEnum) annotation).name());
                listParameters.put(signature,parametersString);
            }


        }
         writeLog(Level.INFO, "List of commands initialized");
    }


    /*
     * Gets the firmware version number of the module.
     * Only applies to the firmware that the module programmed for the RAK811 AT command.
     */
    @cmdEnum(name="RAK_getVersion",
            value = eCmdType.version)
    public final String RAK_getVersion() {
        String ret = at+eCmdType.version.name();
        return ret;
    }

    /*
     * Get the frequency band of the module.
     * This feature is only supported after firmware version 1.0.2.6.
     */
    @cmdEnum(name="RAK_getBand",  value = eCmdType.band)
    public final String RAK_getBand() {
        String ret = at+eCmdType.band;
        return ret;
    }

    /*
     * Let the module enter the ultra low power sleep mode.
     * When the module is in sleep mode, the host can send any character to wake it up.
     * When the module is awakened, the event response will automatically return through the serial information.
     */
    @cmdEnum(name="RAK_sleep",  value = eCmdType.sleep)
    public final String  RAK_sleep() {
        String ret = at+eCmdType.sleep;
        return ret;

    }

    /*
     * Reset the module or reset the LoRaWAN or LoRaP2P protocol stack.
     * mode  = 0: Reset and restart module.
     * mode  = 1: Reset LoRaWAN or LoraP2P stack and Module will reload LoRa configuration from EEPROM.
     */
    @cmdEnum(name="RAK_reset",  value = eCmdType.reset)
    public final String RAK_reset(String mode) {
        String ret="";
        if (mode.equals("1") || mode.equals("0")) {
            ret = at+eCmdType.reset+"="+mode;
        } else {
             writeLog(Level.INFO, "The mode set Error,the mode is '0'or '1'.");
        }
        return ret;
    }

    /*
     * Reload the default parameters of LoraWAN or LoraP2P setting.
     * This command is used to restore the module's initial state.
     */
    @cmdEnum(name="RAK_reload",  value = eCmdType.reload)
    public final String RAK_reload() {
        String ret = at+eCmdType.reload;
        return ret;

    }

    /*
     * Use to change the next send data rate temporary when adr function is off.
     * It will not be save to internal flash.
     * rate : If your Band is EU868 from 0 to 7.
     *        If your Band is US915 from 0 to 4.
     */
    @cmdEnum(name="RAK_setRate",  value = eCmdType.dr)
    public final String RAK_setRate(String rate) {
        String ret = at+eCmdType.dr+"="+rate;
        return ret;
    }

    /*
     * Set the module work mode, the module defaults to LoRaWAN mode..
     * mode  = 0: Set the module to LoRaWAN mode.
     * mode  = 1: Set the module to LoRaP2P mode.
     */
    @cmdEnum(name="RAK_setWorkingMode",  value = eCmdType.mode)
    public final String RAK_setWorkingMode(String mode) {
        String ret="";
        switch (mode) {
            case "0":
            case "1":
                ret = at+eCmdType.mode+"="+mode; //Set LoRaWAN Mode.
                break;
            default:
                 writeLog(Level.INFO, "The Workingmode set Error,the mode is '0'or '1'.");
                break;
        }
        return ret;
    }

    /*
     * Initialize the module parameter, which is the parameter that the module must use when adding the OTAA to the network.
     * devEUI : Device EUI as a HEX string. Example "60C5A8FFFE000001"
     * appEUI : Application EUI as a HEX string. Example "70B3D57EF00047C0"
     * appKEY : Application key as a HEX string. Example "5D833B4696D5E01E2F8DC880E30BA5FE"
     */
    @cmdEnum(name="RAK_initOTAA",  value = eCmdType.set_config)
    public final String RAK_initOTAA(String devEUI, String appEUI, String appKEY) {
        String sdevEUI ="";
        String sAppEUI = "";
        String sAppsKEY = "";
        if (devEUI.length() == 16) {
            sdevEUI = devEUI;
        } else {
             writeLog(Level.INFO, "The parameter devEUI is set incorrectly!");
        }
        if (appEUI.length() == 16) {
            sAppEUI = appEUI;
        } else {
             writeLog(Level.INFO, "The parameter appEUI is set incorrectly!");
        }
        if (appKEY.length() == 32) {
            sAppsKEY = appKEY;
        } else {
             writeLog(Level.INFO, "The parameter appKEY is set incorrectly!");
        }
        String ret = at+eCmdType.set_config+"=dev_eui:" + sdevEUI + "&app_eui:" + sAppEUI + "&app_key:" + sAppsKEY;
        System.out.println(ret);
        return ret;
    }

    /*
     * Initialize the module parameter, which is the parameter that the module must use when adding the ABP to the network.
     * devADDR : The device address as a HEX string. Example "00112233"
     * nwksKEY : Network Session Key as a HEX string. Example "3432567afde4525e7890cfea234a5821"
     * appsKEY : Application Session Key as a HEX string. Example "a48adfc393a0de458319236537a11d90"
     */
    @cmdEnum(name="RAK_initABP",  value = eCmdType.set_config)
    public final String RAK_initABP(String devADDR, String nwksKEY, String appsKEY) {
        String command = "";
        String sDevADDR ="";
        String sNwksKEY = "";
        String sAppsKEY = "";
        if (devADDR.length() == 8) {
            sDevADDR = devADDR;
        } else {
             writeLog(Level.INFO, "The parameter devADDR is set incorrectly!");
        }
        if (nwksKEY.length() == 32) {
            sNwksKEY = nwksKEY;
        } else {
             writeLog(Level.INFO, "The parameter nwksKEY is set incorrectly!");
        }
        if (appsKEY.length() == 32) {
            sAppsKEY = appsKEY;
        } else {
             writeLog(Level.INFO, "The parameter appsKEY is set incorrectly!");
        }
        command = at+eCmdType.set_config+"=dev_addr:" + sDevADDR + "&nwks_key:" + sNwksKEY + "&apps_key:" + sAppsKEY;
        System.out.println(command);
        return command;
    }

    /*
     * Set the activation mode to join the network.And join the network.
     * mode  = 0: join a network using over the air activation..
     * mode  = 1: join a network using personalization.
     * Before using this command, you must call one of the initOTAA and initABP functions
     */
    @cmdEnum(name="RAK_joinLoRaNetwork",  value = eCmdType.join)
    public String RAK_joinLoRaNetwork(String mode) {
        String ret="";
        switch (mode) {
            case "0":
                ret = at+eCmdType.join+"=otaa"; //join Network through OTAA mode.
                break;
            case "1":
                ret = at+eCmdType.join+"=abp"; //join Network through ABP mode.
                break;
            default:
                 writeLog(Level.INFO, "The joinLoRaNetwork set Error,the mode is '0'or '1'.");
                break;
        }
        return ret;
    }


    @cmdEnum(name="RAK_sendData",  value = eCmdType.send)
    public String RAK_sendData(String type, String port, String buffer) {

        String ret = at+eCmdType.send+"="+ type + "," + port + "," + buffer.getBytes(StandardCharsets.UTF_8);
        return ret;
    }

    @cmdEnum(name="RAK_setConfig",  value = eCmdType.set_config)
    public String RAK_setConfig(String Key, String Value) {

        String ret = at+eCmdType.set_config+"="+ Key + ":" + Value;
        return ret;

    }
    @cmdEnum(name="RAK_getConfig",  value = eCmdType.get_config)
    public String RAK_getConfig(String Key)
    {
        String ret = at+eCmdType.get_config+"=" + Key;

        return ret;
    }
    @cmdEnum(name="RAK_getP2PConfig",  value = eCmdType.rf_config)
    public String RAK_getP2PConfig()
    {
        String ret = at+eCmdType.rf_config;
        ret.trim();
        return ret;
    }
    @cmdEnum(name="RAK_initP2P",  value = eCmdType.rf_config)
    public String RAK_initP2P(String FREQ, String SF, String BW, String CR, String PRlen, String PWR)
    {

        String ret = at+eCmdType.rf_config+"="+ FREQ + "," + SF + "," + BW + "," + CR + "," + PRlen + "," + PWR;
        return ret;
    }
    @cmdEnum(name="RAK_recvP2PData",  value = eCmdType.rxc)
    public String RAK_recvP2PData(String report_en)
    {
        String ret=at+eCmdType.rxc+"="+report_en;
        return ret;
    }
    @cmdEnum(name="RAK_sendP2PData",  value = eCmdType.txc)
    public String RAK_sendP2PData(String CNTS, String interver, String DATAHex)
    {
        String ret = at+eCmdType.txc+"="+ CNTS + "," + interver + "," + DATAHex.getBytes(StandardCharsets.UTF_8);
        return ret;
    }
    @cmdEnum(name="RAK_stopSendP2PData",  value = eCmdType.tx_stop)
    public String RAK_stopSendP2PData()
    {
        String ret = at+eCmdType.tx_stop;
        return ret;

    }
    @cmdEnum(name="RAK_stopRecvP2PData",  value = eCmdType.rx_stop)
    public String RAK_stopRecvP2PData()
    {
        String ret = at+eCmdType.rx_stop;
        return ret;
    }
    @cmdEnum(name="RAK_checkStatusStatistics",  value = eCmdType.status)
    public String RAK_checkStatusStatistics(String mode)
    {
        String ret="";
        switch (mode) {
            case "0":
            case "1":
                ret = at+eCmdType.status+"="+mode;
                break;
            default:
                 writeLog(Level.INFO, "The checkStatusStatistics set/get Error,the mode is '0'or '1'.");
                break;
        }
        return ret;

    }

    @cmdEnum(name="RAK_setUARTConfig",  value = eCmdType.uart)
    public String RAK_setUARTConfig(String Baud, String Data_bits, String Parity, String Stop_bits, String Flow_ctrl)
    {
        String ret = at+eCmdType.uart+"=" + Baud + "," + Data_bits + "," + Parity + "," + Stop_bits + "," + Flow_ctrl;
        return ret;
    }

    public String sendRawCommand(String command)
    {
        return command;
    }



}
