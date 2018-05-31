package handling.game;

import client.ClientSocket;
import client.Rewards;
import constants.GameConstants;
import net.InPacket;
import server.MapleInventoryManipulator;
import tools.packet.WvsContext;
import net.ProcessPacket;
import tools.LogHelper;

public final class OnUserRewardClaimRequest implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        int id = iPacket.DecodeInt();
        int type = iPacket.DecodeInt();
        int itemId = iPacket.DecodeInt();
        iPacket.DecodeInt(); //might be item quantity
        iPacket.DecodeInt(); //no idea
        iPacket.DecodeLong(); //no idea
        iPacket.DecodeInt(); //no idea
        int mp = iPacket.DecodeInt();
        int meso = iPacket.DecodeInt();
        int exp = iPacket.DecodeInt();
        iPacket.DecodeInt(); //no idea
        iPacket.DecodeInt(); //no idea
        iPacket.DecodeString(); //no idea
        iPacket.DecodeString(); //no idea
        iPacket.DecodeString(); //no idea
        byte mode = iPacket.DecodeByte();
        if (mode == 2) { //Decline
            c.getPlayer().deleteReward(id);
            c.SendPacket(WvsContext.enableActions());
            return;
        } else if (mode == 1) { //Accept
            if (type < 0 || type > 5) {
                LogHelper.ANTI_HACK.get().info("[Hacking Attempt] " + c.getPlayer().getName() + " has tried to receive reward with unavailable type.");
                c.SendPacket(WvsContext.enableActions());
                return;
            }
            Rewards reward = c.getPlayer().getReward(id);
            if (reward == null) {
                c.SendPacket(WvsContext.Reward.receiveReward(id, (byte) 0x15, 0));
                c.SendPacket(WvsContext.enableActions());
                return;
            }
            if (reward.getType() != type || reward.getItem() != itemId
                    || reward.getMaplePoints() != mp || reward.getMeso() != meso
                    || reward.getExp() != exp) {

                LogHelper.ANTI_HACK.get().info("[Hacking Attempt] " + c.getPlayer().getName() + " has tried to exploit the reward receive.");
                c.SendPacket(WvsContext.enableActions());
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
            c.SendPacket(WvsContext.Reward.receiveReward(id, msg, quantity));
        }
        if (mode < 0 || mode > 2) {
            System.out.println("New reward mode found: " + mode);
        }
    }
}
