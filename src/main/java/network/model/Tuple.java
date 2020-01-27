package network.model;

import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Custom tuple class
 * @author Alexandro Steinert
 */
public class Tuple implements Serializable {
    /**
     * Username
     */
    private final String name;
    /**
     * ObjectOutputStream of the User
     */
    private final transient ObjectOutputStream ooi;

    /**
     * Constructor
     * @param name username
     * @param ooi ObjectOutputStream
     */
    public Tuple(String name, ObjectOutputStream ooi){
        this.name = name;
        this.ooi = ooi;
    }

    /**
     * Getter Name
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Getter Stream
     * @return OOS
     */
    public ObjectOutputStream getOoi() {
        return ooi;
    }
}
