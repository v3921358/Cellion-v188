package handling.game;

import client.Client;
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
public class OperatingSystemInformationHandler implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return true;
    }

    @Override
    public void Process(Client c, InPacket iPacket) {
    }

}
