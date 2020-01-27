package network.model;

import java.io.Serializable;

/**
 * Custom Class for sending and receiving messages
 * @author Alexandro
 */
public class Message implements Serializable {
    /**
     * sender of the message
     */
    private final String sender;
    /**
     * message
     */
    private final String message;
    //all or specified username never null
    /**
     * Receiver of the messagge, either speceivied or "ALL"
     */
    private final String receiver;

    /**
     * Constructor of message
     *
     * @param sender   sender
     * @param message  message
     * @param receiver receiver
     */
    public Message(String sender, String message, String receiver) {
        this.sender = sender;
        this.message = message;
        this.receiver = receiver;

    }

    /**
     * Getter Message
     *
     * @return Message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Getter Receiver
     *
     * @return Receiver
     */
    public String getReceiver() {
        return receiver;
    }

    /**
     * Getter Sender
     *
     * @return Sender
     */
    public String getSender() {
        return sender;
    }
}

