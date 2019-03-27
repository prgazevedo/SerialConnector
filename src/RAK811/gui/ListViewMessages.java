package RAK811.gui;

import javafx.collections.ObservableList;

public class ListViewMessages {

    private ObservableList<String> messageList;
    private int maxNumberOfMessages;

    /**
     *
     * @param frameList
     */
    public ListViewMessages(ObservableList<String> frameList, int maxNumberOfMessages) {

        this.messageList = frameList;
        this.maxNumberOfMessages = maxNumberOfMessages;

    }

    /**
     *
     * @param message
     */
    public void addMessage(String message) {

        int s = messageList.size();
        if (s >= maxNumberOfMessages) {
            // Remove oldest element.
            messageList.remove(s - 1);
        }
        try {
            // Add new element.
            messageList.add(0, message);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
