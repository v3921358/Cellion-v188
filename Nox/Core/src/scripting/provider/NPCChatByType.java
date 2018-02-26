package scripting.provider;

/**
 * Find a better name for this :(
 *
 * @author
 */
public enum NPCChatByType {
    NPC_Cancellable((byte) 0),
    NPC_UnCancellable((byte) 1),
    Self_Cancellable((byte) 2),
    Self_Uncancellable((byte) 3),
    NULL((byte) 0),;

    private final byte value;

    private NPCChatByType(byte value) {
        this.value = value;
    }

    public static final NPCChatByType fromInt(byte type) {
        for (NPCChatByType t : values()) {
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
