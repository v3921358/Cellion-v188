package handling.game;

import client.MapleClient;
import client.Skill;
import client.SkillEntry;
import client.SkillFactory;
import java.util.HashMap;
import server.maps.objects.MapleCharacter;
import net.InPacket;
import tools.packet.CWvsContext;
import netty.ProcessPacket;

/**
 *
 * @author
 */
public class ResetHyperHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        MapleCharacter chr = c.getPlayer();

        chr.updateTick(iPacket.DecodeInteger());
        short times = iPacket.DecodeShort();
        if (times < 1 || times > 3) {
            times = 3;
        }
        long price = 10000L * (long) Math.pow(10, times);
        if (chr.getMeso() < price) {
            chr.dropMessage(1, "You do not have enough mesos for that.");
            c.write(CWvsContext.enableActions());
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
