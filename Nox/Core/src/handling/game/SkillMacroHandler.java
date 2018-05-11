package handling.game;

import client.Client;
import client.SkillMacro;
import net.InPacket;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class SkillMacroHandler implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return true;
    }

    @Override
    public void Process(Client c, InPacket iPacket) {
        int num = iPacket.DecodeByte();

        for (int i = 0; i < num; i++) {
            String name = iPacket.DecodeString();
            int shout = iPacket.DecodeByte();
            int skill1 = iPacket.DecodeInt();
            int skill2 = iPacket.DecodeInt();
            int skill3 = iPacket.DecodeInt();

            SkillMacro macro = new SkillMacro(skill1, skill2, skill3, name, shout, i);
            c.getPlayer().updateMacros(i, macro);
        }
    }
}
