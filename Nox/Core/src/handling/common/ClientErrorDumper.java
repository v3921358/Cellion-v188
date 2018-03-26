package handling.common;

import client.MapleClient;
import handling.PacketThrottleLimits;
import handling.PacketThrottleLimits.PacketThrottleBanType;
import provider.data.HexTool;
import tools.LogHelper;
import net.InPacket;
import netty.ProcessPacket;

@PacketThrottleLimits(
        FlagCount = 2,
        ResetTimeMillis = 180000,
        MinTimeMillisBetweenPackets = 60000,
        FunctionName = "ClientErrorDumper",
        BanType = PacketThrottleBanType.PermanentBan)

public final class ClientErrorDumper implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        if (iPacket.Available() < 8) {
            System.out.println(iPacket.toString());
            return;
        }
        short type = iPacket.DecodeShort();
        String type_str = "Unknown?!";
        switch (type) {
            case 0x01:
                type_str = "SendBackupPacket";
                break;
            case 0x02:
                type_str = "Crash Report";
                break;
            case 0x03:
                type_str = "Exception";
                break;
            default:
                break;
        }
        int errortype = iPacket.DecodeInteger(); // example error 38
        //if (errortype == 0) { // i don't wanna log error code 0 stuffs, (usually some bounceback to login)
        //    return;
        //}
        short data_length = iPacket.DecodeShort();
        iPacket.Skip(4);
        short opcodeheader = iPacket.DecodeShort();

        System.err.println("[Error 38] Thrown by Operation Code Header (" + opcodeheader + ") for Character Name (" + c.getPlayer().getName() + ")");

        LogHelper.GENERAL_EXCEPTION.get().info(String.format("Error Type: %d, Data Length: %d\r\nAccount: %s, Field: %d, Charname: %s\r\nOpcode: 0x%s/ %d\r\nData: %s",
                errortype, data_length,
                c.getAccountName(), c.getPlayer().getMapId(), c.getPlayer().getName(),
                HexTool.toString(opcodeheader).toUpperCase(), opcodeheader,
                HexTool.toString(iPacket.Decode((int) iPacket.Available()))
        ));
    }
}
