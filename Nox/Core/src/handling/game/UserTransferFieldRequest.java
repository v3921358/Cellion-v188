package handling.game;

import client.CharacterTemporaryStat;
import client.ClientSocket;
import client.jobs.Resistance.WildHunterHandler;
import enums.InventoryType;
import constants.GameConstants;
import handling.cashshop.CashShopOperation;
import service.ChannelServer;
import server.MapleInventoryManipulator;
import server.MaplePortal;
import server.maps.MapleMap;
import server.maps.objects.User;
import net.InPacket;
import tools.packet.CField;
import tools.packet.WvsContext;
import net.ProcessPacket;
import tools.Utility;

/**
 * UserTransferFieldRequest
 * @author Mazen Massoud
 */
public final class UserTransferFieldRequest implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        final User pPlayer = c.getPlayer();
        if (pPlayer == null) return;
        pPlayer.completeDispose();

        if (pPlayer.getMap() == null) {                                   // Leaving Cash Shop
            CashShopOperation.LeaveCS(c, c.getPlayer());
        } else {                                                          // Leaving Map
            
            if (pPlayer.hasBuff(CharacterTemporaryStat.JaguarSummoned)) { // Unsummon Jaguar to correct damage parse.
                pPlayer.cancelEffectFromTemporaryStat(CharacterTemporaryStat.JaguarSummoned);
                Utility.removeAllSummonsForCharacter(pPlayer.getId());
            }
            
            if (iPacket.GetRemainder() != 0L) {
                iPacket.DecodeByte();
                int nTargetID = iPacket.DecodeInt();
                iPacket.DecodeInt();
                MaplePortal pPortal = pPlayer.getMap().getPortal(iPacket.DecodeString());
                if (iPacket.GetRemainder() >= 7L) {
                    pPlayer.updateTick(iPacket.DecodeInt());
                }
                iPacket.Skip(1);
                boolean wheel = (iPacket.DecodeShort() > 0) && (!GameConstants.isEventMap(pPlayer.getMapId())) && (pPlayer.haveItem(5510000, 1, false, true)) && (pPlayer.getMapId() / 1000000 != 925);

                if ((nTargetID != -1) && (!pPlayer.isAlive())) {
                    pPlayer.setStance(0);
                    if ((pPlayer.getEventInstance() != null) && (pPlayer.getEventInstance().revivePlayer(pPlayer)) && (pPlayer.isAlive())) {
                        return;
                    }
                    if (pPlayer.getPyramidSubway() != null) {
                        pPlayer.getStat().setHp(50, pPlayer);
                        pPlayer.getPyramidSubway().fail(pPlayer);
                        return;
                    }

                    if (pPlayer.getMapId() == 105200111) {
                        pPlayer.getStat().setHp(500000, pPlayer);
                        pPlayer.getStat().setMp(500000, pPlayer);
                    }

                    if (!wheel) {
                        pPlayer.getStat().setHp(50, pPlayer);

                        MapleMap to = pPlayer.getMap().getReturnMap();
                        pPlayer.changeMap(to, to.getPortal(0));
                    } else {
                        c.SendPacket(CField.EffectPacket.useWheel((byte) (pPlayer.getInventory(InventoryType.CASH).countById(5510000) - 1)));
                        pPlayer.getStat().setHp(pPlayer.getStat().getMaxHp() / 100 * 40, pPlayer);
                        MapleInventoryManipulator.removeById(c, InventoryType.CASH, 5510000, 1, true, false);

                        MapleMap to = pPlayer.getMap();
                        pPlayer.changeMap(to, to.getPortal(0));
                    }
                } else if ((nTargetID != -1) && (pPlayer.isIntern())) {
                    MapleMap to = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(nTargetID);
                    if (to != null) {
                        pPlayer.changeMap(to, to.getPortal(0));
                    } else {
                        pPlayer.dropMessage(5, "Map is NULL. Use !warp <mapid> instead.");
                    }
                } else if ((nTargetID != -1) && (!pPlayer.isIntern())) {
                    int divi = pPlayer.getMapId() / 100;
                    boolean unlock = false;
                    boolean warp = false;
                    if (divi == 9130401) {
                        warp = (nTargetID / 100 == 9130400) || (nTargetID / 100 == 9130401);
                        if (nTargetID / 10000 != 91304) {
                            warp = true;
                            unlock = true;
                            nTargetID = 130030000;
                        }
                    } else if (divi == 9130400) {
                        warp = (nTargetID / 100 == 9130400) || (nTargetID / 100 == 9130401);
                        if (nTargetID / 10000 != 91304) {
                            warp = true;
                            unlock = true;
                            nTargetID = 130030000;
                        }
                    } else if (divi == 9140900) {
                        warp = (nTargetID == 914090011) || (nTargetID == 914090012) || (nTargetID == 914090013) || (nTargetID == 140090000);
                    } else if ((divi == 9120601) || (divi == 9140602) || (divi == 9140603) || (divi == 9140604) || (divi == 9140605)) {
                        warp = (nTargetID == 912060100) || (nTargetID == 912060200) || (nTargetID == 912060300) || (nTargetID == 912060400) || (nTargetID == 912060500) || (nTargetID == 3000100);
                        unlock = true;
                    } else if (divi == 9101500) {
                        warp = (nTargetID == 910150006) || (nTargetID == 101050010);
                        unlock = true;
                    } else if ((divi == 9140901) && (nTargetID == 140000000)) {
                        unlock = true;
                        warp = true;
                    } else if ((divi == 9240200) && (nTargetID == 924020000)) {
                        unlock = true;
                        warp = true;
                    } else if ((nTargetID == 980040000) && (divi >= 9800410) && (divi <= 9800450)) {
                        warp = true;
                    } else if ((divi == 9140902) && ((nTargetID == 140030000) || (nTargetID == 140000000))) {
                        unlock = true;
                        warp = true;
                    } else if ((divi == 9000900) && (nTargetID / 100 == 9000900) && (nTargetID > pPlayer.getMapId())) {
                        warp = true;
                    } else if ((divi / 1000 == 9000) && (nTargetID / 100000 == 9000)) {
                        unlock = (nTargetID < 900090000) || (nTargetID > 900090004);
                        warp = true;
                    } else if ((divi / 10 == 1020) && (nTargetID == 1020000)) {
                        unlock = true;
                        warp = true;
                    } else if ((pPlayer.getMapId() == 900090101) && (nTargetID == 100030100)) {
                        unlock = true;
                        warp = true;
                    } else if ((pPlayer.getMapId() == 2010000) && (nTargetID == 104000000)) {
                        unlock = true;
                        warp = true;
                    } else if ((pPlayer.getMapId() == 106020001) || (pPlayer.getMapId() == 106020502)) {
                        if (nTargetID == pPlayer.getMapId() - 1) {
                            unlock = true;
                            warp = true;
                        }
                    } else if ((pPlayer.getMapId() == 0) && (nTargetID == 10000)) {
                        unlock = true;
                        warp = true;
                    } else if ((pPlayer.getMapId() == 931000011) && (nTargetID == 931000012)) {
                        unlock = true;
                        warp = true;
                    } else if ((pPlayer.getMapId() == 931000021) && (nTargetID == 931000030)) {
                        unlock = true;
                        warp = true;
                    } else if ((pPlayer.getMapId() == 105040300) && (nTargetID == 105040000)) {
                        unlock = true;
                        warp = true;
                    }
                    if (unlock) {
                        c.SendPacket(CField.UIPacket.IntroEnableUI(false));
                        c.SendPacket(CField.UIPacket.IntroLock(false));
                        c.SendPacket(WvsContext.enableActions());
                    }
                    if (warp) {
                        MapleMap to = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(nTargetID);
                        pPlayer.changeMap(to, to.getPortal(0));
                    }
                } else if ((pPortal != null) && (!pPlayer.hasBlockedInventory())) {
                    pPortal.enterPortal(c);
                } else {
                    c.SendPacket(WvsContext.enableActions());
                }
            }
        }
    }
}
