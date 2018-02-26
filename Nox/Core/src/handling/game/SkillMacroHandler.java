package handling.game;

import client.MapleClient;
import client.SkillMacro;
import net.InPacket;
import netty.ProcessPacket;

/**
 *
 * @author
 */
public class SkillMacroHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        int num = iPacket.DecodeByte();

        for (int i = 0; i < num; i++) {
            String name = iPacket.DecodeString();
            int shout = iPacket.DecodeByte();
            int skill1 = iPacket.DecodeInteger();
            int skill2 = iPacket.DecodeInteger();
            int skill3 = iPacket.DecodeInteger();

            SkillMacro macro = new SkillMacro(skill1, skill2, skill3, name, shout, i);
            c.getPlayer().updateMacros(i, macro);
        }
    }
}
