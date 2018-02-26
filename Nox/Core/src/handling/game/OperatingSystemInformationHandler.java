package handling.game;

import client.MapleClient;
import handling.PacketThrottleLimits;
import net.InPacket;
import netty.ProcessPacket;

/**
 *
 * @author
 */
@PacketThrottleLimits(
        FlagCount = 2,
        ResetTimeMillis = 1000 * 60 * 60, // 1 hour
        MinTimeMillisBetweenPackets = 30000,
        FunctionName = "OperatingSystemInformationHandler",
        BanType = PacketThrottleLimits.PacketThrottleBanType.PermanentBan)
public class OperatingSystemInformationHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
    }

}
