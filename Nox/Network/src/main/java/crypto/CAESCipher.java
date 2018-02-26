package crypto;

import static crypto.BitTools.multiplyBytes;

/**
 * @author Novak (ported and modified)
 */
public final class CAESCipher {

    /**
     * AES cipher used for cryptography.
     *
     * @see MapleAES#crypt(byte[], byte[]) used in this method.
     */
    private AES cipher;
    /**
     * Used for checking the packets being sent and received. These are initialized at the starting part of the program by providing the
     * current version the server is running.
     *
     * @see MapleAES#checkPacket(byte[], byte[]) checking packet header using these.
     * @see MapleAES#checkPacket(int, byte[]) checking packet header using these.
     * @see MapleAES#initialize(short) where the values are initialized.
     */
    private static short gVersion, sVersion, rVersion;

    /**
     * Constructor for MapleAES. Creates the cipher that will be used for the cryptography segment as well as the lock used for the same
     * segment.
     */
    public CAESCipher(byte[] key) {
        cipher = new AES();
        cipher.setKey(key);
    }

    /**
     * Initializes the send and receive version values for packet checking using the current version.
     *
     * @param v the current version this service is running.
     */
    public static void Initialize(short v) {
        gVersion = v;
        sVersion = (short) ((((0xFFFF - v) >> 8) & 0xFF) | (((0xFFFF - v) << 8) & 0xFF00));
        rVersion = (short) (((v >> 8) & 0xFF) | ((v << 8) & 0xFF00));
    }

    /**
     * @see MapleAES#crypt(byte[], byte[]) same thing as this method, except that we convert the integer encoding sequence into an array.
     */
    public byte[] Crypt(byte[] delta, int pSrc) {
        byte[] pdwKey = new byte[]{
            (byte) (pSrc & 0xFF), (byte) ((pSrc >> 8) & 0xFF), (byte) ((pSrc >> 16) & 0xFF), (byte) ((pSrc >> 24) & 0xFF)
        };
        return Crypt(delta, pdwKey);
    }

    /**
     * Cryptography segment of MapleAESOFB.
     *
     * @param delta the input data to be put into stage for finalized encryption or to be finally decryption.
     * @param gamma the input seed for this cryptography stage. This value is renewed after each encryption by the corresponding encoder or
     * decoder.
     *
     * @return the bytes having been converted to a stage for encryption or being fully decrypted.
     */
    public byte[] Crypt(byte[] delta, byte[] gamma) {
        int a = delta.length;
        int b = 0x5B0;
        int c = 0;
        while (a > 0) {
            byte[] d = multiplyBytes(gamma, 4, 4);
            if (a < b) {
                b = a;
            }
            for (int e = c; e < (c + b); e++) {
                if ((e - c) % d.length == 0) {
                    try {
                        cipher.encrypt(d);
                    } catch (Exception ex) {
                        ex.printStackTrace(); // may eventually want to remove this
                    }
                }
                delta[e] ^= d[(e - c) % d.length];
            }
            c += b;
            a -= b;
            b = 0x5B4;
        }
        return delta;
    }

    /**
     * @see MapleAES#getHeader(byte[], byte[]) same thing as this method, except that we convert the integer encoding sequence into an
     * array.
     */
    public static byte[] GetHeader(int nLen, int pSrc) {
        byte[] pdwKey = new byte[] {
            (byte) (pSrc & 0xFF), (byte) ((pSrc >> 8) & 0xFF), (byte) ((pSrc >> 16) & 0xFF), (byte) ((pSrc >> 24) & 0xFF)
        };
        return GetHeader(nLen, pdwKey);
    }

    /**
     * Creates a header for the new packet to be sent to the opposite end of the session (Server to Channel for this engine). Contains
     * information about the packet: whether or not it is valid and the actual length of the packet.
     *
     * @param nLen the input packet length before adding the header.
     * @param dwKey the input sending seed before changing it.
     *
     * @return the header to be sent with this packet.
     */
    public static byte[] GetHeader(int nLen, byte[] dwKey) {
        int a = (dwKey[3]) & 0xFF;
        a |= (dwKey[2] << 8) & 0xFF00;
        a ^= sVersion;
        int b = ((nLen << 8) & 0xFF00) | (nLen >>> 8);
        int c = a ^ b;
        byte[] nRet = new byte[4];
        nRet[0] = (byte) ((a >>> 8) & 0xFF);
        nRet[1] = (byte) (a & 0xFF);
        nRet[2] = (byte) ((c >>> 8) & 0xFF);
        nRet[3] = (byte) (c & 0xFF);
        return nRet;
    }

    /**
     * Gets the length of the packet given the received header.
     *
     * @param delta the packet header to be used to find the packet length.
     *
     * @return the length of the received packet.
     */
    public static int GetLength(int delta) {
        int a = ((delta >>> 16) ^ (delta & 0xFFFF));
        a = ((a << 8) & 0xFF00) | ((a >>> 8) & 0xFF);
        return a;
    }

    /**
     * Checks the packet to make sure it is valid and that the session between the client and server is secure and legitimate.
     *
     * @param delta the packet header from the received packet (4 bytes in length).
     * @param gamma the current receive seed.
     *
     * @return whether or not the packet is valid (consequently, if not valid, the session is terminated usually).
     */
    public static boolean ValidateHeader(byte[] delta, byte[] gamma) {
        return ((((delta[0] ^ gamma[2]) & 0xFF) == ((rVersion >> 8) & 0xFF))
                && (((delta[1] ^ gamma[3]) & 0xFF) == (rVersion & 0xFF)));
    }

    /**
     * @see MapleAES#checkPacket(byte[], byte[]) same thing as this method, except that we convert the integer packet header (4 bytes
     * combined) into an array of the first 2 bytes of the integer packet header. and we convert the integer encoding sequence into an
     * array.
     */
    public static boolean ValidateHeader(int nDelta, int pSrc) {
        byte[] aDelta = new byte[2];
        aDelta[0] = (byte) ((nDelta >> 24) & 0xFF);
        aDelta[1] = (byte) ((nDelta >> 16) & 0xFF);
        byte[] pdwKey = new byte[]{
            (byte) (pSrc & 0xFF), (byte) ((pSrc >> 8) & 0xFF), (byte) ((pSrc >> 16) & 0xFF), (byte) ((pSrc >> 24) & 0xFF)
        };
        return CAESCipher.ValidateHeader(aDelta, pdwKey);
    }
}