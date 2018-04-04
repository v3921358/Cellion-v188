/*
 * Rexion Development
 */
package client.anticheat;

import constants.ServerConstants;
import net.OutPacket;
import net.Packet;
import service.SendPacketOpcode;

/**
 * Anti-Cheat
 * @author Mazen
 */
public class AntiCheat {
    
    public static String[] aBannedProcessNames = {
        "cheatengine", 
        "MapleShark",
    };
    public static String[] aBannedWindowNames = {
        "Cheat Engine",
    };
    
    /**
     * Checks if the user is running banned process or windows.
     * @return 
     */
    public static Packet bannedProccessRequest() {
        OutPacket oPacket = new OutPacket(80);
        
        oPacket.EncodeShort(SendPacketOpcode.CheckProcess.getValue());
        
        oPacket.Encode(aBannedProcessNames.length);
        for (String sBannedProccess : aBannedProcessNames) {
            oPacket.EncodeString(sBannedProccess);
        }
        
        oPacket.Encode(aBannedWindowNames.length);
        for (String sBannedWindow : aBannedWindowNames) {
            oPacket.EncodeString(sBannedWindow);
        }
        
        System.out.println("[AntiCheat Debug] Banned Proccess Request");
        
        return oPacket.ToPacket();
    }
}
