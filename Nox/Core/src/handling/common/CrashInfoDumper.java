package handling.common;

import client.Client;
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
public final class CrashInfoDumper implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return true;
    }

    @Override
    public void Process(Client c, InPacket iPacket) {
        LogHelper.GENERAL_EXCEPTION.get().info(iPacket.DecodeString());
    }

}
