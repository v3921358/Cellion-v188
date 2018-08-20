package enums;

/**
 * Find a better name for this :(
 *
 * @author
 */
public enum NPCInterfaceType {
    NPC_Cancellable((byte) 0),
    NPC_UnCancellable((byte) 1),
    Self_Cancellable((byte) 2),
    Self_Uncancellable((byte) 3),
    NULL((byte) 0),;

    private final byte value;

    private NPCInterfaceType(byte value) {
        this.value = value;
    }

    public static final NPCInterfaceType fromInt(byte type) {
        for (NPCInterfaceType t : values()) {
            if (t.getValue() == type) {
                return t;
            }
        }
        return NULL;
    }

    public byte getValue() {
        return value;
    }
}
