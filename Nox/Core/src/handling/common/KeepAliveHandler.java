package handling.common;

import client.ClientSocket;
import handling.PacketThrottleLimits;
import net.InPacket;
import net.ProcessPacket;

@PacketThrottleLimits(
        FlagCount = 5,
        ResetTimeMillis = 1000 * 60 * 60, // 1 hour 
        MinTimeMillisBetweenPackets = 3000,
        FunctionName = "KeepAliveHandler",
        BanType = PacketThrottleLimits.PacketThrottleBanType.Disconnect)
public final class KeepAliveHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        int GetTickCount = iPacket.DecodeInt(); // Win32 API for the time in millis since the computer started

        c.pongReceived();
    }

}
