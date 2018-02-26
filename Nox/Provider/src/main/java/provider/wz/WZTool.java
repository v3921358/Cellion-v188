package provider.wz;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import provider.data.input.LittleEndianAccessor;
import provider.data.input.SeekableLittleEndianAccessor;



/*
 * Ported Code, see WZFile.java for more info
 */
public class WZTool {
    private static final byte[] encKey;

    static {
        byte[] iv = new byte[]{(byte) 0x4d, (byte) 0x23, (byte) 0xc7, (byte) 0x2b,
            (byte) 0x4d, (byte) 0x23, (byte) 0xc7, (byte) 0x2b,
            (byte) 0x4d, (byte) 0x23, (byte) 0xc7, (byte) 0x2b,
            (byte) 0x4d, (byte) 0x23, (byte) 0xc7, (byte) 0x2b,};
        byte[] key = new byte[]{(byte) 0x13, 0x00, 0x00, 0x00,
            (byte) 0x08, 0x00, 0x00, 0x00,
            (byte) 0x06, 0x00, 0x00, 0x00,
            (byte) 0xB4, 0x00, 0x00, 0x00,
            (byte) 0x1B, 0x00, 0x00, 0x00,
            (byte) 0x0F, 0x00, 0x00, 0x00,
            (byte) 0x33, 0x00, 0x00, 0x00,
            (byte) 0x52, 0x00, 0x00, 0x00
        };
        Cipher cipher = null;
        SecretKeySpec skeySpec = new SecretKeySpec(key, "AES");
        try {
            cipher = Cipher.getInstance("AES");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
        }
        try {
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        } catch (InvalidKeyException e) {
        }
        encKey = new byte[0xFFFF];
        for (int i = 0; i < (0xFFFF / 16); i++) {
            try {
                iv = cipher.doFinal(iv);
            } catch (    IllegalBlockSizeException | BadPaddingException e) {
            }
            System.arraycopy(iv, 0, encKey, (i * 16), 16);
        }
        try {
            iv = cipher.doFinal(iv);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
        }
        System.arraycopy(iv, 0, encKey, 65520, 15);
    }

    public static byte[] readListString(byte[] str) {
        for (int i = 0; i < str.length; i++) {
            str[i] = (byte) (str[i] ^ encKey[i]);
        }
        return str;
    }

    public static String readDecodedString(LittleEndianAccessor llea) {
        int strLength;
        byte b = llea.readByte();
        if (b == 0x00) {
            return "";
        }
        if (b >= 0) {
            if (b == 0x7F) {
                strLength = llea.readInt();
            } else {
                strLength = (int) b;
            }
            if (strLength < 0) {
                return "";
            }
            byte str[] = new byte[strLength * 2];
            for (int i = 0; i < strLength * 2; i++) {
                str[i] = llea.readByte();
            }
            return DecryptUnicodeStr(str);
        } else {
            if (b == -128) {
                strLength = llea.readInt();
            } else {
                strLength = -b;
            }
            if (strLength < 0) {
                return "";
            }
            byte str[] = new byte[strLength];
            for (int i = 0; i < strLength; i++) {
                str[i] = llea.readByte();
            }
            return DecryptAsciiStr(str);
        }
    }

    public static String DecryptAsciiStr(byte[] str) {
        byte xorByte = (byte) 0xAA;
        for (int i = 0; i < str.length; i++) {
            str[i] = (byte) (str[i] ^ xorByte ^ encKey[i]);
            xorByte++;
        }
        return new String(str);
    }

    public static String DecryptUnicodeStr(byte[] str) {
        int xorByte = 0xAAAA;
        char[] charRet = new char[str.length / 2];
        for (int i = 0; i < str.length; i++) {
            str[i] = (byte) (str[i] ^ encKey[i]);
        }
        for (int i = 0; i < (str.length / 2); i++) {
            char toXor = (char) ((str[i] << 8) | str[i + 1]);
            charRet[i] = (char) (toXor ^ xorByte);
            xorByte++;
        }
        return String.valueOf(charRet);
    }

    public static String readDecodedStringAtOffset(SeekableLittleEndianAccessor slea, int offset) {
        slea.seek(offset);
        return readDecodedString(slea);
    }

    public static String readDecodedStringAtOffsetAndReset(SeekableLittleEndianAccessor slea, int offset) {
        long pos = 0;
        pos = slea.getPosition();
        slea.seek(offset);
        String ret = readDecodedString(slea);
        slea.seek(pos);
        return ret;
    }

    public static int readValue(LittleEndianAccessor lea) {
        byte b = lea.readByte();
        if (b == -128) {
            return lea.readInt();
        } else {
            return ((int) b);
        }
    }

    public static float readFloatValue(LittleEndianAccessor lea) {
        byte b = lea.readByte();
        if (b == -128) {
            return lea.readFloat();
        } else {
            return 0;
        }
    }
}