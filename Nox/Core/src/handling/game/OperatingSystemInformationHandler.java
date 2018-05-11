package handling.game;

import client.ClientSocket;
import handling.PacketThrottleLimits;
import net.InPacket;
import net.ProcessPacket;

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
public class OperatingSystemInformationHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
    }

}
