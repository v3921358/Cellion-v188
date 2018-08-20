package handling.common;

import client.ClientSocket;
import handling.PacketThrottleLimits;
import tools.LogHelper;
import net.InPacket;
import net.ProcessPacket;

@PacketThrottleLimits(
        FlagCount = 2,
        ResetTimeMillis = 180000,
        MinTimeMillisBetweenPackets = 60000,
        FunctionName = "ClientErrorDumper",
        BanType = PacketThrottleLimits.PacketThrottleBanType.PermanentBan)
public final class CrashInfoDumper implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        LogHelper.GENERAL_EXCEPTION.get().info(iPacket.DecodeString());
    }

}
