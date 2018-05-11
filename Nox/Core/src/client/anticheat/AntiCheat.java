/*
 * Cellion Development
 */
package client.anticheat;

import constants.ServerConstants;
import net.OutPacket;

import service.SendPacketOpcode;

/**
 * Anti-Cheat
 *
 * @author Mazen Massoud
 */
public class AntiCheat {

    public static String[] aBannedProcessNames = {
        "cheatengine",
        "MapleShark",};
    public static String[] aBannedWindowNames = {
        "Cheat Engine",};

    /**
     * Checks if the user is running banned process or windows.
     *
     * @return
     */
    public static OutPacket bannedProccessRequest() {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.CheckProcess.getValue());

        oPacket.EncodeByte(aBannedProcessNames.length);
        for (String sBannedProccess : aBannedProcessNames) {
            oPacket.EncodeString(sBannedProccess);
        }

        oPacket.EncodeByte(aBannedWindowNames.length);
        for (String sBannedWindow : aBannedWindowNames) {
            oPacket.EncodeString(sBannedWindow);
        }

        System.out.println("[AntiCheat Debug] Banned Proccess Request");

        return oPacket;
    }
}
