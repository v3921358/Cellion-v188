package provider.data.output;

import java.awt.Point;
import java.awt.Rectangle;

/**
 * Provides an interface to a writer class that writes a little-endian sequence
 * of bytes.
 *
 * @author Frz
 * @version 1.0
 * @since Revision 323
 */
public interface LittleEndianWriter {

    /**
     * Write the number of zero bytes
     *
     * @param i
     */
    public void writeZeroBytes(final int i);

    /**
     * Write an array of bytes to the sequence.
     *
     * @param b The bytes to write.
     */
    public void write(final byte b[]);
    public void write(final boolean b);
    public void writeBoolean(final boolean b);

    /**
     * Write a byte to the sequence.
     *
     * @param b The byte to write.
     */
    public void write(final byte b);

    public void write(final int b);

    /**
     * Writes an integer to the sequence.
     *
     * @param i The integer to write.
     */
    public void writeInt(final int i);
    public void writeReversedInt(final int i);
    
    /**
     * Writes a 32 bit float to the sequence.
     *
     * @param val The float to write.
     */
    public void writeFloat(final float val);
    

    /**
     * Write a short integer to the sequence.
     *
     * @param s The short integer to write.
     */
    public void writeReversedShort(final short s);
    public void writeShort(final short s);
    public void writeReversedShort(final int s);
    public void writeShort(final int i);

    /**
     * Write a long integer to the sequence.
     *
     * @param l The long integer to write.
     */
    public void writeLong(final long l);
    public void writeReversedLong(final long l);

    /**
     * Writes an ASCII string the the sequence.
     *
     * @param s The ASCII string to write.
     */
    void writeAsciiString(final String s);

    void writeAsciiString(String s, final int max);

    /**
     * Writes a 2D 4 byte position information
     *
     * @param s The Point position to write.
     */
    void writePos(final Point s);

    /**
     * Writes a maple-convention ASCII string to the sequence.
     *
     * @param s The ASCII string to use maple-convention to write.
     */
    void writeMapleAsciiString(final String s);
    
    public void writeRect(final Rectangle s);
}
