package enums;

/**
 *
 * @author
 */
public enum ModifyInventoryOperation {

    AddItem((byte) 0),
    UpdateQuantity((byte) 1),
    Move((byte) 2),
    Remove((byte) 3),
    NExp((byte) 4),
    NBagPos((byte) 5),
    UpdateBagQuantity((byte) 6),
    Unk1((byte) 7),
    UpdateBagPosition((byte) 8),
    UpdateEqpItemInformation((byte) 9),
    Unk2((byte) 10);

    private final byte op;

    private ModifyInventoryOperation(byte op) {
        this.op = op;
    }

    public byte getOpcode() {
        return op;
    }
}
