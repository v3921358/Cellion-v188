package handling.common;

import client.Client;
import handling.PacketThrottleLimits;
import net.InPacket;
import net.ProcessPacket;

@PacketThrottleLimits(
        FlagCount = 5,
        ResetTimeMillis = 1000 * 60 * 60, // 1 hour 
        MinTimeMillisBetweenPackets = 3000,
        FunctionName = "KeepAliveHandler",
        BanType = PacketThrottleLimits.PacketThrottleBanType.Disconnect)
public final class KeepAliveHandler implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return true;
    }

    @Override
    public void Process(Client c, InPacket iPacket) {
        int GetTickCount = iPacket.DecodeInt(); // Win32 API for the time in millis since the computer started

        c.pongReceived();
    }

}
