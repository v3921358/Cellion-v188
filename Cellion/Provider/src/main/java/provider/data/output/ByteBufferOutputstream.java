package provider.data.output;

import org.apache.mina.common.ByteBuffer;

/**
 * Uses a bytebuffer as an underlying storage method to hold a stream of bytes.
 *
 * @author Frz
 * @version 1.0
 * @since Revision 323
 */
public class ByteBufferOutputstream implements ByteOutputStream {

    private final ByteBuffer bb;

    /**
     * Class constructor - Wraps this instance around ByteBuffer <code>bb</code>
     *
     * @param bb The <code>org.apache.mina.common.ByteBuffer</code> to wrap this
     * stream around.
     */
    public ByteBufferOutputstream(final ByteBuffer bb) {
        super();
        this.bb = bb;
    }

    /**
     * Writes a byte to the underlying buffer.
     *
     * @param b The byte to write.
     * @see org.apache.mina.common.ByteBuffer#put(byte)
     */
    @Override
    public void writeByte(final byte b) {
        bb.put(b);
    }
}
