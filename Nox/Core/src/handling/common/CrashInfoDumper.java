package handling.common;

import client.MapleClient;
import handling.PacketThrottleLimits;
import tools.LogHelper;
import net.InPacket;
import netty.ProcessPacket;

@PacketThrottleLimits(
        FlagCount = 2,
        ResetTimeMillis = 180000,
        MinTimeMillisBetweenPackets = 60000,
        FunctionName = "ClientErrorDumper",
        BanType = PacketThrottleLimits.PacketThrottleBanType.PermanentBan)
public final class CrashInfoDumper implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        LogHelper.GENERAL_EXCEPTION.get().info(iPacket.DecodeString());
    }

}
