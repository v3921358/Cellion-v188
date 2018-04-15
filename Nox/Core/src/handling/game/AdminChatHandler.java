/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.game;

import client.MapleClient;
import handling.PacketThrottleLimits;
import handling.world.World;
import net.InPacket;
import net.OutPacket;

import tools.packet.CWvsContext;
import net.ProcessPacket;

/**
 *
 * @author Lloyd Korn
 */
@PacketThrottleLimits(
        FlagCount = 20,
        ResetTimeMillis = 20000,
        MinTimeMillisBetweenPackets = 500,
        FunctionName = "AdminChatHandler",
        BanType = PacketThrottleLimits.PacketThrottleBanType.Disconnect)
public class AdminChatHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        if (!c.getPlayer().isGM()) {//if ( (signed int)CWvsContext::GetAdminLevel((void *)v294) > 2 )
            c.Close();
            return;
        }
        byte mode = iPacket.DecodeByte();
        //not saving slides...
        OutPacket packet = CWvsContext.broadcastMsg(iPacket.DecodeByte(), iPacket.DecodeString());//maybe I should make a check for the iPacket.decodeByte()... but I just hope gm's don't fuck things up :)
        switch (mode) {
            case 0:// /alertall, /noticeall, /slideall
                World.Broadcast.broadcastMessage(packet);
                break;
            case 1:// /alertch, /noticech, /slidech
                c.getChannelServer().broadcastMessage(packet);
                break;
            case 2:// /alertm /alertmap, /noticem /noticemap, /slidem /slidemap
                c.getPlayer().getMap().broadcastMessage(packet);
                break;

        }
    }

}
