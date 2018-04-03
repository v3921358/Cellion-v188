package handling.game;

import client.MapleClient;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import handling.cashshop.CashShopOperation;
import service.ChannelServer;
import server.MapleInventoryManipulator;
import server.MaplePortal;
import server.maps.MapleMap;
import server.maps.objects.User;
import net.InPacket;
import tools.packet.CField;
import tools.packet.CWvsContext;
import netty.ProcessPacket;
import tools.packet.CField.SummonPacket;

public final class UserTransferFieldRequest implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        final User chr = c.getPlayer();
        if (chr == null) {
            return;
        }
        
        if (chr.getMap() == null) {
            CashShopOperation.LeaveCS(c, c.getPlayer());
        } else {
            if (iPacket.Available() != 0L) {
                iPacket.DecodeByte();
                int targetid = iPacket.DecodeInteger();
                iPacket.DecodeInteger();
                MaplePortal portal = chr.getMap().getPortal(iPacket.DecodeString());
                if (iPacket.Available() >= 7L) {
                    chr.updateTick(iPacket.DecodeInteger());
                }
                iPacket.Skip(1);
                boolean wheel = (iPacket.DecodeShort() > 0) && (!GameConstants.isEventMap(chr.getMapId())) && (chr.haveItem(5510000, 1, false, true)) && (chr.getMapId() / 1000000 != 925);

                if ((targetid != -1) && (!chr.isAlive())) {
                    chr.setStance(0);
                    if ((chr.getEventInstance() != null) && (chr.getEventInstance().revivePlayer(chr)) && (chr.isAlive())) {
                        return;
                    }
                    if (chr.getPyramidSubway() != null) {
                        chr.getStat().setHp(50, chr);
                        chr.getPyramidSubway().fail(chr);
                        return;
                    }

                    if (chr.getMapId() == 105200111) {
                        chr.getStat().setHp(500000, chr);
                        chr.getStat().setMp(500000, chr);
                    }

                    if (!wheel) {
                        chr.getStat().setHp(50, chr);

                        MapleMap to = chr.getMap().getReturnMap();
                        chr.changeMap(to, to.getPortal(0));
                    } else {
                        c.write(CField.EffectPacket.useWheel((byte) (chr.getInventory(MapleInventoryType.CASH).countById(5510000) - 1)));
                        chr.getStat().setHp(chr.getStat().getMaxHp() / 100 * 40, chr);
                        MapleInventoryManipulator.removeById(c, MapleInventoryType.CASH, 5510000, 1, true, false);

                        MapleMap to = chr.getMap();
                        chr.changeMap(to, to.getPortal(0));
                    }
                } else if ((targetid != -1) && (chr.isIntern())) {
                    MapleMap to = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(targetid);
                    if (to != null) {
                        chr.changeMap(to, to.getPortal(0));
                    } else {
                        chr.dropMessage(5, "Map is NULL. Use !warp <mapid> instead.");
                    }
                } else if ((targetid != -1) && (!chr.isIntern())) {
                    int divi = chr.getMapId() / 100;
                    boolean unlock = false;
                    boolean warp = false;
                    if (divi == 9130401) {
                        warp = (targetid / 100 == 9130400) || (targetid / 100 == 9130401);
                        if (targetid / 10000 != 91304) {
                            warp = true;
                            unlock = true;
                            targetid = 130030000;
                        }
                    } else if (divi == 9130400) {
                        warp = (targetid / 100 == 9130400) || (targetid / 100 == 9130401);
                        if (targetid / 10000 != 91304) {
                            warp = true;
                            unlock = true;
                            targetid = 130030000;
                        }
                    } else if (divi == 9140900) {
                        warp = (targetid == 914090011) || (targetid == 914090012) || (targetid == 914090013) || (targetid == 140090000);
                    } else if ((divi == 9120601) || (divi == 9140602) || (divi == 9140603) || (divi == 9140604) || (divi == 9140605)) {
                        warp = (targetid == 912060100) || (targetid == 912060200) || (targetid == 912060300) || (targetid == 912060400) || (targetid == 912060500) || (targetid == 3000100);
                        unlock = true;
                    } else if (divi == 9101500) {
                        warp = (targetid == 910150006) || (targetid == 101050010);
                        unlock = true;
                    } else if ((divi == 9140901) && (targetid == 140000000)) {
                        unlock = true;
                        warp = true;
                    } else if ((divi == 9240200) && (targetid == 924020000)) {
                        unlock = true;
                        warp = true;
                    } else if ((targetid == 980040000) && (divi >= 9800410) && (divi <= 9800450)) {
                        warp = true;
                    } else if ((divi == 9140902) && ((targetid == 140030000) || (targetid == 140000000))) {
                        unlock = true;
                        warp = true;
                    } else if ((divi == 9000900) && (targetid / 100 == 9000900) && (targetid > chr.getMapId())) {
                        warp = true;
                    } else if ((divi / 1000 == 9000) && (targetid / 100000 == 9000)) {
                        unlock = (targetid < 900090000) || (targetid > 900090004);
                        warp = true;
                    } else if ((divi / 10 == 1020) && (targetid == 1020000)) {
                        unlock = true;
                        warp = true;
                    } else if ((chr.getMapId() == 900090101) && (targetid == 100030100)) {
                        unlock = true;
                        warp = true;
                    } else if ((chr.getMapId() == 2010000) && (targetid == 104000000)) {
                        unlock = true;
                        warp = true;
                    } else if ((chr.getMapId() == 106020001) || (chr.getMapId() == 106020502)) {
                        if (targetid == chr.getMapId() - 1) {
                            unlock = true;
                            warp = true;
                        }
                    } else if ((chr.getMapId() == 0) && (targetid == 10000)) {
                        unlock = true;
                        warp = true;
                    } else if ((chr.getMapId() == 931000011) && (targetid == 931000012)) {
                        unlock = true;
                        warp = true;
                    } else if ((chr.getMapId() == 931000021) && (targetid == 931000030)) {
                        unlock = true;
                        warp = true;
                    } else if ((chr.getMapId() == 105040300) && (targetid == 105040000)) {
                        unlock = true;
                        warp = true;
                    }
                    if (unlock) {
                        c.write(CField.UIPacket.IntroEnableUI(false));
                        c.write(CField.UIPacket.IntroLock(false));
                        c.write(CWvsContext.enableActions());
                    }
                    if (warp) {
                        MapleMap to = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(targetid);
                        chr.changeMap(to, to.getPortal(0));
                    }
                } else if ((portal != null) && (!chr.hasBlockedInventory())) {
                    portal.enterPortal(c);
                } else {
                    c.write(CWvsContext.enableActions());
                }
            }
        }
    }
}
