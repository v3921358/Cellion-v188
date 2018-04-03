package handling.game;

import client.MapleClient;
import client.SkillFactory;
import server.maps.objects.User;
import server.maps.objects.MapleRuneStone;
import net.InPacket;
import tools.packet.CWvsContext;
import netty.ProcessPacket;

/**
 *
 * @author
 */
public class RuneStoneCompleteArrowHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        // 58 03 01
        boolean sucess = iPacket.DecodeByte() > 0;

        User chr = c.getPlayer();
        MapleRuneStone rune = chr.getMap().getRuneStone();

        if (sucess
                && rune != null
                && !rune.getAbleToRespawn()) { // rune is still alive

            rune.completeRune(chr.getMap());

            //     LASTRUNETIME = System.currentTimeMillis() + 1000000; // 10 min
            SkillFactory.getSkill(rune.getRuneType().getPrimarySkill()).getEffect(1).applyTo(c.getPlayer());
            SkillFactory.getSkill(rune.getRuneType().getSecondarySkill()).getEffect(1).applyTo(c.getPlayer());
            return;
        }
        // Fail
        //   LASTRUNETIME = System.currentTimeMillis() + 10000; // 10 sec
        c.write(CWvsContext.enableActions());
    }
}
