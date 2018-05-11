package handling.game;

import client.Client;
import client.MapleSpecialStats.MapleSpecialStatUpdateType;
import net.InPacket;
import tools.packet.CWvsContext;
import net.ProcessPacket;

public final class ProfessionInfo implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return true;
    }

    @Override
    public void Process(Client c, InPacket iPacket) {
        /*    String typeStr = iPacket.decodeString();
        MapleSpecialStatUpdateType type = MapleSpecialStatUpdateType.getFromString(typeStr);
        int level1 = iPacket.DecodeInt();
        int level2 = iPacket.DecodeInt();
        int rate;
        
        switch (type) {
            case Skill_9200:
            case Skill_9201:
                rate = 100;
                break;
            case UpdateHonor:
                c.write(CWvsContext.updateSpecialStat(type, level1, level2, c.getPlayer().getHonourNextExp()));
                return;
            case UpdateHyperSkills:
                c.write(CWvsContext.updateSpecialStat(type, level1, level2, 0));
                return;
            case Unknown:
                return;
            default:
                rate = Math.max(0, 100 - ((level1 + 1) - c.getPlayer().getProfessionLevel(Integer.parseInt(typeStr))) * 20);
                break;
        }
        c.write(CWvsContext.updateSpecialStat(type, level1, level2, rate));*/
    }
}
