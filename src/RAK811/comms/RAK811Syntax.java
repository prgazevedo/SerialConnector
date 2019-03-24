package RAK811.comms;

import RAK811.gui.MainApplication;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class RAK811Syntax {


    public  enum eCmdType2 {
        join;
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




    private ArrayList<String> listCommands;
    public ArrayList<String> getListCommands() { return listCommands; }
    private MainApplication m_mainApplication;
    public RAK811Syntax(  MainApplication mainApplication) {

       m_mainApplication=mainApplication;

    }
    public void initialize() {
        Method[] methods = this.getClass().getMethods();
        listCommands = new ArrayList<>();
        for(Method method: methods){
            listCommands.add(ReflectUtil.getSignature(method));
        }
        System.out.println("List of commands initialized");
    }


    /*
     * Gets the firmware version number of the module.
     * Only applies to the firmware that the module programmed for the RAK811 AT command.
     */
    public final String getVersion() {
        String ret = at+eCmdType.version;
        return ret;
    }

    /*
     * Get the frequency band of the module.
     * This feature is only supported after firmware version 1.0.2.6.
     */
    public final String getBand() {
        String ret = at+eCmdType.band;
        return ret;
    }

    /*
     * Let the module enter the ultra low power sleep mode.
     * When the module is in sleep mode, the host can send any character to wake it up.
     * When the module is awakened, the event response will automatically return through the serial information.
     */
    public final String  sleep() {
        String ret = at+eCmdType.sleep;
        return ret;

    }

    /*
     * Reset the module or reset the LoRaWAN or LoRaP2P protocol stack.
     * mode  = 0: Reset and restart module.
     * mode  = 1: Reset LoRaWAN or LoraP2P stack and Module will reload LoRa configuration from EEPROM.
     */
    public final String reset(int mode) {
        String ret="";
        if (mode == 1 || mode==0) {
            ret = at+eCmdType.reset+"="+mode;
        } else {
            System.out.println("The mode set Error,the mode is '0'or '1'.");
        }
        return ret;
    }

    /*
     * Reload the default parameters of LoraWAN or LoraP2P setting.
     * This command is used to restore the module's initial state.
     */
    public final String reload() {
        String ret = at+eCmdType.reload;
        return ret;

    }

    /*
     * Use to change the next send data rate temporary when adr function is off.
     * It will not be save to internal flash.
     * rate : If your Band is EU868 from 0 to 7.
     *        If your Band is US915 from 0 to 4.
     */
    public final String setRate(int rate) {
        String ret = at+eCmdType.dr+"="+rate;
        return ret;
    }

    /*
     * Set the module work mode, the module defaults to LoRaWAN mode..
     * mode  = 0: Set the module to LoRaWAN mode.
     * mode  = 1: Set the module to LoRaP2P mode.
     */
    public final String setWorkingMode(int mode) {
        String ret="";
        switch (mode) {
            case 0:
            case 1:
                ret = at+eCmdType.mode+"="+mode; //Set LoRaWAN Mode.
                break;
            default:
                System.out.println("The Workingmode set Error,the mode is '0'or '1'.");
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
    public final String initOTAA(String devEUI, String appEUI, String appKEY) {
        String sdevEUI ="";
        String sAppEUI = "";
        String sAppsKEY = "";
        if (devEUI.length() == 16) {
            sdevEUI = devEUI;
        } else {
            System.out.println("The parameter devEUI is set incorrectly!");
        }
        if (appEUI.length() == 16) {
            sAppEUI = appEUI;
        } else {
            System.out.println("The parameter appEUI is set incorrectly!");
        }
        if (appKEY.length() == 32) {
            sAppsKEY = appKEY;
        } else {
            System.out.println("The parameter appKEY is set incorrectly!");
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
    public final String initABP(String devADDR, String nwksKEY, String appsKEY) {
        String command = "";
        String sDevADDR ="";
        String sNwksKEY = "";
        String sAppsKEY = "";
        if (devADDR.length() == 8) {
            sDevADDR = devADDR;
        } else {
            System.out.println("The parameter devADDR is set incorrectly!");
        }
        if (nwksKEY.length() == 32) {
            sNwksKEY = nwksKEY;
        } else {
            System.out.println("The parameter nwksKEY is set incorrectly!");
        }
        if (appsKEY.length() == 32) {
            sAppsKEY = appsKEY;
        } else {
            System.out.println("The parameter appsKEY is set incorrectly!");
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
    public String joinLoRaNetwork(int mode) {
        String ret="";
        switch (mode) {
            case 0:
                ret = at+eCmdType.join+"=otaa"; //join Network through OTAA mode.
                break;
            case 1:
                ret = at+eCmdType.join+"=abp"; //join Network through ABP mode.
                break;
            default:
                System.out.println("The joinLoRaNetwork set Error,the mode is '0'or '1'.");
                break;
        }
        return ret;
    }



    public String sendData(int type, int port, byte[] buffer) {

        String ret = at+eCmdType.send+"="+ type + "," + port + "," + buffer;
        return ret;
    }


    public String setConfig(String Key, String Value) {

        String ret = at+eCmdType.set_config+"="+ Key + ":" + Value;
        return ret;

    }

    public String getConfig(String Key)
    {
        String ret = at+eCmdType.get_config+"=" + Key;

        return ret;
    }
    public String getP2PConfig()
    {
        String ret = at+eCmdType.rf_config;
        ret.trim();
        return ret;
    }
    public String initP2P(String FREQ, int SF, int BW, int CR, int PRlen, int PWR)
    {

        String ret = at+eCmdType.rf_config+"="+ FREQ + "," + SF + "," + BW + "," + CR + "," + PRlen + "," + PWR;
        return ret;
    }
    public String recvP2PData(int report_en)
    {
        String ret=at+eCmdType.rxc+"="+report_en;
        return ret;
    }
    public String sendP2PData(int CNTS, String interver, byte[] DATAHex)
    {
        String ret = at+eCmdType.txc+"="+ CNTS + "," + interver + "," + DATAHex;
        return ret;
    }
    public String stopSendP2PData()
    {
        String ret = at+eCmdType.tx_stop;
        return ret;

    }
    public String stopRecvP2PData()
    {
        String ret = at+eCmdType.rx_stop;
        return ret;
    }
    public String checkStatusStatistics(int mode)
    {
        String ret="";
        switch (mode) {
            case 0:
            case 1:
                ret = at+eCmdType.status+"="+mode;
                break;
            default:
                System.out.println("The checkStatusStatistics set/get Error,the mode is '0'or '1'.");
                break;
        }
        return ret;

    }


    public String setUARTConfig(int Baud, int Data_bits, int Parity, int Stop_bits, int Flow_ctrl)
    {
        String ret = at+eCmdType.uart+"=" + Baud + "," + Data_bits + "," + Parity + "," + Stop_bits + "," + Flow_ctrl;
        return ret;
    }

    public String sendRawCommand(String command)
    {
        return command;
    }



}
