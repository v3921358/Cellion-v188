package handling.game;

import client.ClientSocket;
import client.SkillFactory;
import server.maps.objects.User;
import server.maps.objects.MapleRuneStone;
import net.InPacket;
import tools.packet.WvsContext;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class RuneStoneCompleteArrowHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
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
        c.SendPacket(WvsContext.enableActions());
    }
}
