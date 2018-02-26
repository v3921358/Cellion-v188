package tools.packet;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import provider.data.HexTool;
import net.OutPacket;
import net.Packet;

/**
 *
 * @author Itzik
 */
public class LoadPacket {

    public static Packet createPacket() {
        Properties packetProps = new Properties();
        InputStreamReader is;
        try {
            is = new FileReader("CPacket.txt");
            packetProps.load(is);
            is.close();
        } catch (IOException ex) {
            System.out.println("Failed to load CPacket.txt");
        }
        OutPacket oPacket = new OutPacket(80);
        oPacket.Encode(HexTool.getByteArrayFromHexString(packetProps.getProperty("packet")));
        return oPacket.ToPacket();
    }
}
