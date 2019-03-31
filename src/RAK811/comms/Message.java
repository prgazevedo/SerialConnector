package RAK811.comms;




/**
 *This is a builder pattern implementation to build immutable Message objects:
 * This is the list of recognized commands.
 * @see MessageRecord
 *
 */



public class Message {

    public enum MessageType{
        CMD         , // sent message
        RESPONSE_OK              , // OK Response
        RESPONSE_ERROR          , //ERROR Response
        EVENT_RESPONSE    , // EVENT
        None, //No comand was identified
    } ;

    /** Acceptable Message splitter chars for serial message */
    public static final byte[] MESSAGE_END = {'\r','\n'};

    public String getM_messagePayload() {
        return m_messagePayload;
    }

    private  String m_messagePayload;
    private String m_message;
    private MessageType m_messageType;

    public Message(String messagePayload, MessageType messageType) {
        this.m_messagePayload = messagePayload;
        this.m_messageType = messageType;
        this.m_message = m_messagePayload+MESSAGE_END;

    }

    public Message(String message) {
        this.m_messagePayload = message;
        this.m_messageType = MessageType.CMD;
        this.m_message = message;

    }

    //The actual message to write in serial
    public String getM_message() {
        return m_message;
    }


    private  boolean isMessagePayloadACommand()
    {
        if(m_messageType.equals(MessageType.CMD)) return true;
        else return false;

    }


    private  boolean isMessagePayloadAResponse()
    {

        if(m_messageType.equals(MessageType.RESPONSE_OK) || m_messageType.equals(MessageType.RESPONSE_ERROR)) return true;
        else return false;

    }



    private  boolean isMessagePayloadAEvent()
    {

        if(m_messageType.equals(MessageType.EVENT_RESPONSE) ) return true;
        else return false;

    }

}
