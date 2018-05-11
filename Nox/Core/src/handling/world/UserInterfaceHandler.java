package handling.world;

import client.Client;
import net.InPacket;
import scripting.provider.NPCScriptManager;

public class UserInterfaceHandler {

    public static final void CygnusSummon_NPCRequest(final Client c) {
        if (c.getPlayer().getJob() == 2000) {
            NPCScriptManager.getInstance().start(c, 1202000, null);
        } else if (c.getPlayer().getJob() == 1000) {
            NPCScriptManager.getInstance().start(c, 1101008, null);
        }
    }

    public static final void InGame_Poll(final InPacket iPacket, final Client c) {
        final boolean PollEnabled = false;
        final String Poll_Question = "Are you mudkiz?";
        final String[] Poll_Answers = {"test1", "test2", "test3"};
        if (PollEnabled) {
            c.getPlayer().updateTick(iPacket.DecodeInt());
            final int selection = iPacket.DecodeInt();

            if (selection >= 0 && selection <= Poll_Answers.length) {
                //if (MapleCharacterUtil.SetPoll(c.getAccID(), selection)) {
                //c.write(CField.getPollReply("Thank you.")); //idk what goes here lol
                //}
            }
        }
    }
}
