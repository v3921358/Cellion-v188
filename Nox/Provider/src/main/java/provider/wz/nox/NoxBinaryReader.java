package provider.wz.nox;

import java.awt.Point;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import provider.data.HexTool;

/**
 * Proxy class around DataInputStream made just for our custom MapleStory binary
 * WZ needs :)
 *
 * All in little endian.
 *
 * @author Lloyd Korn
 */
public class NoxBinaryReader {

    private DataInputStream dis = null;
    private NoxFileHeader fileHeader;

    public NoxBinaryReader(String filedata) {
        final File file = new File(filedata);
        if (!file.exists()) {
            throw new RuntimeException(String.format("File %s do not exist!", filedata));
        }
        try (DataInputStream datainputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(file)))) {
            readFileMetadata(datainputStream);

            // Create encryption key
            byte[] encKey = fileHeader.getInitializationVector();

            byte[] blockCipher = new byte[1024];
            int readLen = 0, position = 0;
            byte[] decryptedBlocks = new byte[datainputStream.available()];
            while ((readLen = datainputStream.read(blockCipher, 0, blockCipher.length)) > 0) {
                // Decrypt
                for (int i = 0; i < readLen; i++) {
                    blockCipher[i] = (byte) (blockCipher[i] ^ encKey[i % encKey.length]);
                }
                System.arraycopy(blockCipher, 0, decryptedBlocks, position, readLen);

                // Transform the IV
                for (int i = 0; i < encKey.length; i++) {
                    if (fileHeader.getFileVersion() == 2) {
                        encKey[i] = (byte) (encKey[i] ^ fileHeader.getMapleVersion() ^ fileHeader.getFileVersion() ^ blockCipher[0]);
                    } else if (fileHeader.getFileVersion() == 1) {
                        encKey[i] = (byte) (encKey[i] ^ fileHeader.getMapleVersion() ^ fileHeader.getFileVersion());
                    }
                }
                position += readLen;
            }

            dis = new DataInputStream(new ByteArrayInputStream(decryptedBlocks));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads the file header information
     *
     * @throws IOException
     */
    private void readFileMetadata(DataInputStream dis) throws IOException {
        fileHeader = new NoxFileHeader();

        fileHeader.setParseType((byte) dis.readByte());
        fileHeader.setFileVersion(dis.readInt());
        fileHeader.setMapleVersion(dis.readInt());

        int len = read7BitEncodedInt(dis);
        byte[] rawbytes = new byte[len];
        dis.read(rawbytes, 0, len);
        fileHeader.setCopyright(new String(rawbytes));

        byte[] IV = new byte[10];
        dis.read(IV);
        fileHeader.setInitializationVector(IV);

        //   System.out.println(String.format("Type: %d, Ver: %d, MSVer: %d, C: %s", 
        //           fileHeader.getParseType(), fileHeader.getFileVersion(), fileHeader.getMapleVersion(), fileHeader.getCopyright()));
    }

    public final void close() throws IOException {
        dis.close();
    }

    public final int available() throws IOException {
        return dis.available();
    }

    /**
     * Skips over and discards <code>n</code> bytes of data from the input
     * stream. The <code>skip</code> method may, for a variety of reasons, end
     * up skipping over some smaller number of bytes, possibly <code>0</code>.
     * The actual number of bytes skipped is returned.
     * <p>
     * This method simply performs <code>in.skip(n)</code>.
     *
     * @param n the number of bytes to be skipped.
     * @exception IOException if the stream does not support seek, or if some
     * other I/O error occurs.
     */
    public final void skip(long n) throws IOException {
        dis.skip(n);
    }

    /**
     * See the general contract of the <code>skipBytes</code> method of
     * <code>DataInput</code>.
     * <p>
     * Bytes for this operation are read from the contained input stream.
     *
     * @param n the number of bytes to be skipped.
     * @return the actual number of bytes skipped.
     * @exception IOException if the contained input stream does not support
     * seek, or the stream has been closed and the contained input stream does
     * not support reading after close, or another I/O error occurs.
     */
    public final int skipBytes(int n) throws IOException {
        return dis.skipBytes(n);
    }

    /**
     * See the general contract of the <code>readBoolean</code> method of
     * <code>DataInput</code>.
     * <p>
     * Bytes for this operation are read from the contained input stream.
     *
     * @return the <code>boolean</code> value read.
     * @exception EOFException if this input stream has reached the end.
     * @exception IOException the stream has been closed and the contained input
     * stream does not support reading after close, or another I/O error occurs.
     * @see java.io.FilterInputStream#in
     */
    public final boolean readBoolean() throws IOException {
        return readByte() > 0;
    }

    /**
     * See the general contract of the <code>readByte</code> method of
     * <code>DataInput</code>.
     * <p>
     * Bytes for this operation are read from the contained input stream.
     *
     * @return the next byte of this input stream as a signed 8-bit
     * <code>byte</code>.
     * @exception EOFException if this input stream has reached the end.
     * @exception IOException the stream has been closed and the contained input
     * stream does not support reading after close, or another I/O error occurs.
     * @see java.io.FilterInputStream#in
     */
    public final int readByte() throws IOException {
        return (dis.readByte() & 0xFF);
    }

    /**
     * See the general contract of the <code>readUnsignedByte</code> method of
     * <code>DataInput</code>.
     * <p>
     * Bytes for this operation are read from the contained input stream.
     *
     * @return the next byte of this input stream, interpreted as an unsigned
     * 8-bit number.
     * @exception EOFException if this input stream has reached the end.
     * @exception IOException the stream has been closed and the contained input
     * stream does not support reading after close, or another I/O error occurs.
     * @see java.io.FilterInputStream#in
     */
    public final int readUnsignedByte() throws IOException {
        return dis.readUnsignedByte();
    }

    /**
     * See the general contract of the <code>readShort</code> method of
     * <code>DataInput</code>.
     * <p>
     * Bytes for this operation are read from the contained input stream.
     *
     * @return the next two bytes of this input stream, interpreted as a signed
     * 16-bit number.
     * @exception EOFException if this input stream reaches the end before
     * reading two bytes.
     * @exception IOException the stream has been closed and the contained input
     * stream does not support reading after close, or another I/O error occurs.
     * @see java.io.FilterInputStream#in
     */
    public final short readShort() throws IOException {
        final int byte1 = readByte();
        final int byte2 = readByte();

        return (short) ((byte2 << 8) + byte1);
    }

    public final Point readPoint() throws IOException {
        final int x = readShort();
        final int y = readShort();

        return new Point(x, y);
    }

    /**
     * See the general contract of the <code>readUnsignedShort</code> method of
     * <code>DataInput</code>.
     * <p>
     * Bytes for this operation are read from the contained input stream.
     *
     * @return the next two bytes of this input stream, interpreted as an
     * unsigned 16-bit integer.
     * @exception EOFException if this input stream reaches the end before
     * reading two bytes.
     * @exception IOException the stream has been closed and the contained input
     * stream does not support reading after close, or another I/O error occurs.
     * @see java.io.FilterInputStream#in
     */
    public final int readUnsignedShort() throws IOException {
        final int byte1 = readByte();
        final int byte2 = readByte();

        return ((byte2 << 8) + byte1);
    }

    /**
     * See the general contract of the <code>readChar</code> method of
     * <code>DataInput</code>.
     * <p>
     * Bytes for this operation are read from the contained input stream.
     *
     * @return the next two bytes of this input stream, interpreted as a
     * <code>char</code>.
     * @exception EOFException if this input stream reaches the end before
     * reading two bytes.
     * @exception IOException the stream has been closed and the contained input
     * stream does not support reading after close, or another I/O error occurs.
     * @see java.io.FilterInputStream#in
     */
    public final char readChar() throws IOException {
        return dis.readChar();
    }

    /**
     * See the general contract of the <code>readInt</code> method of
     * <code>DataInput</code>.
     * <p>
     * Bytes for this operation are read from the contained input stream.
     *
     * @return the next four bytes of this input stream, interpreted as an
     * <code>int</code>.
     * @exception EOFException if this input stream reaches the end before
     * reading four bytes.
     * @exception IOException the stream has been closed and the contained input
     * stream does not support reading after close, or another I/O error occurs.
     * @see java.io.FilterInputStream#in
     */
    public final int readInt() throws IOException {
        final int byte1 = readByte();
        final int byte2 = readByte();
        final int byte3 = readByte();
        final int byte4 = readByte();

        return (byte4 << 24) + (byte3 << 16) + (byte2 << 8) + byte1;
    }

    /**
     * See the general contract of the <code>readLong</code> method of
     * <code>DataInput</code>.
     * <p>
     * Bytes for this operation are read from the contained input stream.
     *
     * @return the next eight bytes of this input stream, interpreted as a
     * <code>long</code>.
     * @exception EOFException if this input stream reaches the end before
     * reading eight bytes.
     * @exception IOException the stream has been closed and the contained input
     * stream does not support reading after close, or another I/O error occurs.
     * @see java.io.FilterInputStream#in
     */
    public final long readLong() throws IOException {
        final int byte1 = readByte();
        final int byte2 = readByte();
        final int byte3 = readByte();
        final int byte4 = readByte();
        final int byte5 = readByte();
        final int byte6 = readByte();
        final int byte7 = readByte();
        final int byte8 = readByte();

        return (byte8 << 56) + (byte7 << 48) + (byte6 << 40) + (byte5 << 32) + (byte4 << 24) + (byte3 << 16)
                + (byte2 << 8) + byte1;
    }

    /// <summary>
    /// Reads in a 32-bit integer in compressed format.
    /// </summary>
    /// 
    /// <returns>
    /// A 32-bit integer in compressed format.
    /// </returns>
    /// <exception cref="T:System.IO.EndOfStreamException">The end of the stream is reached. </exception><exception cref="T:System.ObjectDisposedException">The stream is closed. </exception><exception cref="T:System.IO.IOException">An I/O error occurs. </exception><exception cref="T:System.FormatException">The stream is corrupted.</exception>
    public int read7BitEncodedInt() throws IOException {
        int num1 = 0;
        int num2 = 0;
        while (num2 != 35) {
            byte num3 = dis.readByte();
            num1 |= ((int) num3 & (int) Byte.MAX_VALUE) << num2;
            num2 += 7;
            if (((int) num3 & 128) == 0) {
                return num1;
            }
        }
        print(50);
        throw new NumberFormatException("Format_Bad7BitInt32");
    }

    public static int read7BitEncodedInt(DataInputStream dis) throws IOException {
        int num1 = 0;
        int num2 = 0;
        while (num2 != 35) {
            byte num3 = dis.readByte();
            num1 |= ((int) num3 & (int) Byte.MAX_VALUE) << num2;
            num2 += 7;
            if (((int) num3 & 128) == 0) {
                return num1;
            }
        }
        throw new NumberFormatException("Format_Bad7BitInt32");
    }

    public final String readAsciiString() throws IOException {
        int len = read7BitEncodedInt();
        // read the string as bytes of length = len
        byte[] rawbytes = new byte[len];
        dis.read(rawbytes, 0, len);

        return new String(rawbytes);
    }

    public final float readFloat() throws IOException {
        return Float.intBitsToFloat(readInt());
    }

    public void print(int count) throws IOException {
        if (dis.available() > 0) {
            count = Math.min((int) dis.available(), count);
            byte[] Read = new byte[count];
            for (int i = 0; i < count; i++) {
                int c = readByte();
                Read[i] = (byte) c;
            }
            System.out.println(HexTool.toString(Read));
        }
    }

    public void printAll() throws IOException {
        if (dis.available() > 0) {
            byte[] Read = new byte[dis.available()];
            for (int i = 0; i < dis.available(); i++) {
                int c = readByte();
                Read[i] = (byte) c;
            }
            System.out.println(HexTool.toString(Read));
        }
    }
}
