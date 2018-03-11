package handling.game;

import client.MapleClient;
import client.MapleReward;
import constants.GameConstants;
import net.InPacket;
import server.MapleInventoryManipulator;
import tools.packet.CWvsContext;
import netty.ProcessPacket;
import tools.LogHelper;

public final class OnUserRewardClaimRequest implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        int id = iPacket.DecodeInteger();
        int type = iPacket.DecodeInteger();
        int itemId = iPacket.DecodeInteger();
        iPacket.DecodeInteger(); //might be item quantity
        iPacket.DecodeInteger(); //no idea
        iPacket.DecodeLong(); //no idea
        iPacket.DecodeInteger(); //no idea
        int mp = iPacket.DecodeInteger();
        int meso = iPacket.DecodeInteger();
        int exp = iPacket.DecodeInteger();
        iPacket.DecodeInteger(); //no idea
        iPacket.DecodeInteger(); //no idea
        iPacket.DecodeString(); //no idea
        iPacket.DecodeString(); //no idea
        iPacket.DecodeString(); //no idea
        byte mode = iPacket.DecodeByte();
        if (mode == 2) { //Decline
            c.getPlayer().deleteReward(id);
            c.write(CWvsContext.enableActions());
            return;
        } else if (mode == 1) { //Accept
            if (type < 0 || type > 5) {
                LogHelper.ANTI_HACK.get().info("[Hacking Attempt] " + c.getPlayer().getName() + " has tried to receive reward with unavailable type.");
                c.write(CWvsContext.enableActions());
                return;
            }
            MapleReward reward = c.getPlayer().getReward(id);
            if (reward == null) {
                c.write(CWvsContext.Reward.receiveReward(id, (byte) 0x15, 0));
                c.write(CWvsContext.enableActions());
                return;
            }
            if (reward.getType() != type || reward.getItem() != itemId
                    || reward.getMaplePoints() != mp || reward.getMeso() != meso
                    || reward.getExp() != exp) {

                LogHelper.ANTI_HACK.get().info("[Hacking Attempt] " + c.getPlayer().getName() + " has tried to exploit the reward receive.");
                c.write(CWvsContext.enableActions());
                return;
            }
            byte msg = 0x15;
            int quantity = 0;
            switch (type) {
                case 1:
                    if (MapleInventoryManipulator.checkSpace(c, itemId, 1, "")) {
                        c.getPlayer().gainItem(itemId, 1);
                        c.getPlayer().deleteReward(id);
                        quantity = 1;
                        msg = 0x0C;
                    } else {
                        msg = 0x16;
                    }
                    break;
                case 3:
                    if (c.getPlayer().getCSPoints(2) + mp >= 0) {
                        c.getPlayer().modifyCSPoints(2, mp, false);
                        c.getPlayer().deleteReward(id);
                        quantity = mp;
                        msg = 0x0B;
                    } else {
                        msg = 0x14;
                    }
                    break;
                case 4:
                    if (c.getPlayer().getMeso() + meso < Integer.MAX_VALUE
                            && c.getPlayer().getMeso() + meso > 0) {
                        c.getPlayer().gainMeso(meso, true, true);
                        c.getPlayer().deleteReward(id);
                        quantity = meso;
                        msg = 0x0E;
                    } else {
                        msg = 0x17;
                    }
                    break;
                case 5:
                    int maxlevel = GameConstants.isCygnusKnight(c.getPlayer().getJob()) ? 120 : 200;
                    if (c.getPlayer().getLevel() < maxlevel) {
                        c.getPlayer().gainExp(exp, true, true, true);
                        c.getPlayer().deleteReward(id);
                        quantity = exp;
                        msg = 0x0F;
                    } else {
                        msg = 0x18;
                    }
                    break;
                default:
                    System.out.println("New reward type found: " + type);
                    break;
            }
            c.write(CWvsContext.Reward.receiveReward(id, msg, quantity));
        }
        if (mode < 0 || mode > 2) {
            System.out.println("New reward mode found: " + mode);
        }
    }
}
