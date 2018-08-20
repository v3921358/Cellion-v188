package handling.game;

import client.ClientSocket;
import client.Skill;
import client.SkillEntry;
import client.SkillFactory;
import java.util.HashMap;
import server.maps.objects.User;
import net.InPacket;
import tools.packet.WvsContext;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class ResetHyperHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        User chr = c.getPlayer();

        chr.updateTick(iPacket.DecodeInt());
        short times = iPacket.DecodeShort();
        if (times < 1 || times > 3) {
            times = 3;
        }
        long price = 10000L * (long) Math.pow(10, times);
        if (chr.getMeso() < price) {
            chr.dropMessage(1, "You do not have enough mesos for that.");
            c.SendPacket(WvsContext.enableActions());
            return;
        }
        int ssp = 0;
        int spp = 0;
        int sap = 0;
        HashMap<Skill, SkillEntry> sa = new HashMap<>();
        for (Skill skil : SkillFactory.getAllSkills()) {
            if (skil.isHyper()) {
                sa.put(skil, new SkillEntry(0, (byte) 1, -1));
                switch (skil.getHyper()) {
                    case 1:
                        ssp++;
                        break;
                    case 2:
                        spp++;
                        break;
                    case 3:
                        sap++;
                        break;
                    default:
                        break;
                }
            }
        }
        chr.gainMeso(-price, false);
        chr.changeSkillsLevel(sa, true);
        chr.gainHyperSP(0, ssp);
        chr.gainHyperSP(1, spp);
        chr.gainHyperSP(2, sap);
    }

}
