package handling.game;

import client.ClientSocket;
import server.maps.objects.User;
import server.maps.objects.RuneStone;
import server.maps.objects.RuneStone.MapleRuneStoneType;
import net.InPacket;
import tools.packet.CField;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class RuneStoneStartHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        iPacket.DecodeInt();
        MapleRuneStoneType type = MapleRuneStoneType.getFromInt(iPacket.DecodeInt());

        User chr = c.getPlayer();
        RuneStone runeInMap = c.getPlayer().getMap().getRuneStone();

        if (type != null && runeInMap != null/*
                && runeInMap.getRuneType() == type // check if what the client reported is the same as what we have on the server
                && ((chr.getLevel() >= runeInMap.getFieldMonsterMinLevel() - 20 && chr.getLevel() <= runeInMap.getFieldMonsterMaxLevel() + 20) || chr.isGM()) // 10 lvl range
                && chr.getLevel() >= 30*/) { // min Lv of 30 to take the rune

            long cTime = System.currentTimeMillis();

            c.SendPacket(CField.RunePacket.runeMsg(5, 0)); // Shows Arrow

            //       if (cTime >= LASTRUNETIME) {
            //           c.write(CField.runeMsg(5, 0));
            //       } else {
            //          c.write(CField.runeMsg(2, LASTRUNETIME - cTime));
            //      }
            return;
        }
        // Fail
        c.SendPacket(CField.RunePacket.runeMsg(4, 0)); // Shows 'that rune is too strong'
    }
}
