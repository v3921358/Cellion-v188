/*
 * Copyright (C) 2018 Kaz Voeten
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package util;

/**
 * Artifact from Invictus. Documentation is not going to be added since
 * the logic is fairly straightforward.
 * 
 * @author OdinMS (original code)
 * @author Zygon (modified code)
 */

public final class HexUtils {

    private static final char[] HEX_CHARS = {
        '0', '1', '2', '3',
        '4', '5', '6', '7',
        '8', '9', 'A', 'B',
        'C', 'D', 'E', 'F'
    };

    public static String ToHex(byte b) {
        return new StringBuilder().append(HEX_CHARS[((b << 8) >> 12) & 0x0F]).
                append(HEX_CHARS[((b << 8) >> 8) & 0x0F]).
                toString();
    }

    public static String ToHex(byte[] aData) {
        if (aData.length <= 0) return "";
        StringBuilder sRet = new StringBuilder();
        for (int i = 0; i < aData.length; i++) {
            sRet.append(ToHex(aData[i]));
            sRet.append(' ');
        }
        return sRet.substring(0, sRet.length() - 1);
    }

    public static byte[] ToBytes(String sData) {
        int nLen = sData.length();
        byte[] aData = new byte[nLen / 2];
        for (int i = 0; i < nLen; i += 2) {
            aData[i / 2] = (byte) ((Character.digit(sData.charAt(i), 16) << 4)
                    + Character.digit(sData.charAt(i + 1), 16));
        }
        return aData;
    }
}
