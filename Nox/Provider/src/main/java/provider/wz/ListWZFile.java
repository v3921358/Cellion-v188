package provider.wz;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import provider.MapleDataProviderFactory;
import provider.data.input.GenericLittleEndianAccessor;
import provider.data.input.InputStreamByteStream;
import provider.data.input.LittleEndianAccessor;


public class ListWZFile {
    private final LittleEndianAccessor lea;
    private List<String> entries = new ArrayList<>();
    private static Collection<String> modernImgs = new HashSet<>();

    public static byte[] xorBytes(byte[] a, byte[] b) {
        byte[] wusched = new byte[a.length];
        for (int i = 0; i < a.length; i++) {
            wusched[i] = (byte) (a[i] ^ b[i]);
        }
        return wusched;
    }

    public ListWZFile(File listwz) throws FileNotFoundException {
        lea = new GenericLittleEndianAccessor(new InputStreamByteStream(new BufferedInputStream(new FileInputStream(listwz))));
        while (lea.available() > 0) {
            int l = lea.readInt() * 2;
            byte[] chunk = new byte[l];
            for (int i = 0; i < chunk.length; i++) {
                chunk[i] = lea.readByte();
            }
            lea.readChar();
            final String value = String.valueOf(WZTool.readListString(chunk));
            entries.add(value);
        }
        entries = Collections.unmodifiableList(entries);
    }

    public List<String> getEntries() {
        return entries;
    }

    public static void init() {
        final String listWz = System.getProperty("listwz");
        if (listWz != null) {
            ListWZFile listwz;
            try {
                listwz = new ListWZFile(MapleDataProviderFactory.fileInWZPath("List.wz"));
                modernImgs = new HashSet<>(listwz.getEntries());
            } catch (FileNotFoundException e) {
            }
        }
    }

    public static boolean isModernImgFile(String path) {
        return modernImgs.contains(path);
    }
}
