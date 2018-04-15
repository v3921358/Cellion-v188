package handling.login;

import client.MapleClient;
import client.PartTimeJob;
import constants.ServerConstants;
import net.InPacket;
import server.maps.objects.User;
import tools.packet.CLogin;
import net.ProcessPacket;

public final class PartTimeJobHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        if (!ServerConstants.PART_TIME_JOB) { // Disables Part Time Jobs
            return;
        }

        if (c.getPlayer() != null || !c.isLoggedIn()) {
            c.Close();
            return;
        }
        final byte mode = iPacket.DecodeByte();
        final int cid = iPacket.DecodeInt();
        if (mode == 1) {
            final PartTimeJob partTime = User.getPartTime(cid);
            final byte job = iPacket.DecodeByte();
            if (/*chr.getLevel() < 30 || */job < 0 || job > 5 || partTime.getReward() > 0
                    || (partTime.getJob() > 0 && partTime.getJob() <= 5)) {
                c.Close();
                return;
            }
            partTime.setTime(System.currentTimeMillis());
            partTime.setJob(job);
            c.SendPacket(CLogin.updatePartTimeJob(partTime));
            User.removePartTime(cid);
            User.addPartTime(partTime);
        } else if (mode == 2) {
            final PartTimeJob partTime = User.getPartTime(cid);
            if (/*chr.getLevel() < 30 || */partTime.getReward() > 0
                    || partTime.getJob() < 0 || partTime.getJob() > 5) {
                c.Close();
                return;
            }
            final long distance = (System.currentTimeMillis() - partTime.getTime()) / (60 * 60 * 1000L);
            if (distance > 1) {
                partTime.setReward((int) (((partTime.getJob() + 1) * 1000L) + distance));
            } else {
                partTime.setJob((byte) 0);
                partTime.setReward(0);
            }
            partTime.setTime(System.currentTimeMillis());
            User.removePartTime(cid);
            User.addPartTime(partTime);
            c.SendPacket(CLogin.updatePartTimeJob(partTime));
        }
    }

}
