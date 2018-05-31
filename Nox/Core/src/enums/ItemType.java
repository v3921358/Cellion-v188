package enums;

/**
 * The item type value, according to MapleStory client
 *
 * @author
 */
public enum ItemType {
    Equipment((byte) 1),
    Item((byte) 2),
    Pet((byte) 3);

    private final byte _type;

    private ItemType(byte _type) {
        this._type = _type;
    }

    public byte getTypeValue() {
        return _type;
    }
}
