package exercises.server;

import java.io.ObjectOutputStream;

/**
 * Custom tuple class
 * @author Alexandro Steinert
 */
class Tuple {
    /**
     * Username
     */
    private final String name;
    /**
     * ObjectOutputStream of the User
     */
    private final ObjectOutputStream ooi;

    /**
     * Constructor
     * @param name username
     * @param ooi ObjectOutputStream
     */
    Tuple(String name, ObjectOutputStream ooi){
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
