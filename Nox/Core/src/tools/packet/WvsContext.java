package tools.packet;

import enums.ModifyInventoryOperation;
import enums.InventoryType;
import enums.EquipSlotType;
import enums.EnchantmentStats;
import enums.Stat;
import client.*;
import client.SpecialStats.MapleSpecialStatUpdateType;
import enums.Stat.Temp;
import client.buddy.Buddy;
import client.buddy.BuddyList;
import client.buddy.BuddylistEntry;
import client.inventory.*;
import constants.GameConstants;
import constants.ServerConstants;
import handling.world.*;
import handling.world.MapleGeneralRanking.CandyRankingInfo;
import net.OutPacket;

import server.MapleFamiliar;
import server.MapleItemInformationProvider;
import server.Randomizer;
import server.life.Mob;
import server.life.PlayerNPC;
import server.maps.objects.User;
import server.maps.objects.Pet;
import server.maps.objects.MonsterFamiliar;
import enums.ExpType;
import server.messages.ExpIncreaseMessage;
import server.messages.MessageInterface;
import server.stores.HiredMerchant;
import server.stores.MaplePlayerShopItem;
import service.SendPacketOpcode;
import tools.Pair;
import tools.StringUtil;
import tools.packet.CField.EffectPacket.UserEffectCodes;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;
import server.messages.DropPickUpMessage;
import server.skills.VMatrixRecord;

public class WvsContext {

    /**
     *
     * This is the inventory modification packet, it handles inventory operations and item bag operations
     *
     * @param enableActions
     * @param modifications List<ModifyInventory> modification - the operations being done on the inventory ChangeLog_NewItem = 0x0,
     * ChangeLog_ItemNumber = 0x1, ChangeLog_Position = 0x2, ChangeLog_DelItem = 0x3, ChangeLog_EXP = 0x4, ChangeLogBag_RelateInven = 0x5,
     * ChangeLogBag_ItemNumber = 0x6, ChangeLogBag_DelItem = 0x7, ChangeLogBag_BagToBag = 0x8, ChangeLogBag_NewItem = 0x9,
     * ChangeLogBag_DelSlot = 0xA,
     *
     * if ( ItemID / 10000 == 223 && !bNotRemoveAddInfo ) CharacterData::ResetExpConsumeItem(cd, v99);
     * @return
     */
    public static OutPacket getInventoryStatus() {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.InventoryOperation.getValue());
        oPacket.EncodeByte(0);
        oPacket.EncodeByte(0);
        oPacket.EncodeByte(0);

        return oPacket;
    }

    public static OutPacket updateInventorySlot(InventoryType type, Item item, boolean fromDrop) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.InventoryOperation.getValue());
        oPacket.EncodeByte(fromDrop ? 1 : 0);
        oPacket.EncodeByte(1);
        oPacket.EncodeByte(0);

        oPacket.EncodeByte(GameConstants.isInBag(item.getPosition(), type.getType()) ? 6 : 1);
        oPacket.EncodeByte(type.getType());
        oPacket.EncodeShort(item.getPosition());
        oPacket.EncodeShort(item.getQuantity());

        return oPacket;
    }

    public static OutPacket inventoryOperation(boolean enableActions, List<ModifyInventory> modifications) {
        return inventoryOperation(enableActions, modifications, false);
    }

    /**
     * This is the inventory modification packet, it handles inventory operations and item bag operations
     *
     * @param enableActions
     * @param modifications
     * @param reloadItem reloadItem - This reloads the item with new information without removing it from the inventory ChangeLog_NewItem =
     * 0x0, ChangeLog_ItemNumber = 0x1, ChangeLog_Position = 0x2, ChangeLog_DelItem = 0x3, ChangeLog_EXP = 0x4, ChangeLogBag_RelateInven =
     * 0x5, ChangeLogBag_ItemNumber = 0x6, ChangeLogBag_DelItem = 0x7, ChangeLogBag_BagToBag = 0x8, ChangeLogBag_NewItem = 0x9,
     * ChangeLogBag_DelSlot = 0xA,
     *
     * if ( ItemID / 10000 == 223 && !bNotRemoveAddInfo ) CharacterData::ResetExpConsumeItem(cd, v99);
     *
     * @return
     */
    public static OutPacket getInventoryFull() {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.InventoryOperation.getValue());
        oPacket.EncodeByte(1);
        oPacket.EncodeByte(0);
        oPacket.EncodeByte(0);

        return oPacket;
    }

    public static OutPacket inventoryOperation(boolean enableActions, List<ModifyInventory> modifications, boolean reloadItem) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.InventoryOperation.getValue());
        oPacket.EncodeBool(enableActions);
        oPacket.EncodeByte(modifications.size());
        oPacket.EncodeBool(reloadItem); //bNotRemoveAddInfo

        boolean bAddMovementInfo = false;
        for (ModifyInventory mod : modifications) {
            oPacket.EncodeByte(mod.getMode().getOpcode());
            oPacket.EncodeByte(mod.getInventoryType());
            short pos = mod.getPosition();

            if (mod.getMode() == ModifyInventoryOperation.Move || mod.getMode() == ModifyInventoryOperation.NBagPos
                    || mod.getMode() == ModifyInventoryOperation.UpdateBagPosition) {
                pos = mod.getOldPosition();
            }
            oPacket.EncodeShort(pos);
            switch (mod.getMode()) {
                case AddItem: //add item
                    PacketHelper.addItemInfo(oPacket, mod.getItem());
                    break;
                case UpdateQuantity: //update quantity
                    oPacket.EncodeShort(mod.getQuantity()); //nNumber
                    break;
                case Move: //move
                    oPacket.EncodeShort(mod.getPosition()); //nPos
                    if (mod.getPosition() < 0 || mod.getOldPosition() < 0) {
                        bAddMovementInfo = true;
                    }
                    break;
                case Remove: //remove
                    if (mod.getInventoryType() == 1) {
                        if (mod.getPosition() < 0) {
                            bAddMovementInfo = true;
                        }
                    }
                    break;
                case NExp:
                    oPacket.EncodeLong(mod.getItem().getExp());//nEXP
                    break;
                case NBagPos:
                    oPacket.EncodeInt(0); //nBagPos
                    break;
                case UpdateBagQuantity:
                    oPacket.EncodeShort(mod.getQuantity()); //quantity inside bag
                    break;
                case Unk1:
                    break;
                case UpdateBagPosition:
                    oPacket.EncodeShort(0); //nBagPos
                    break;
                case UpdateEqpItemInformation:
                    PacketHelper.addItemInfo(oPacket, mod.getItem());
                    break;
                case Unk2:
                    break;
            }
            oPacket.EncodeByte(0);
        }
        //if (bAddMovementInfo) {
        oPacket.EncodeBool(bAddMovementInfo);
        //}
        return oPacket;
    }

    public static OutPacket pendantExpansionAvailable(boolean available) { //slot -59

        OutPacket oPacket = new OutPacket(SendPacketOpcode.SetBuyEquipExt.getValue());
        oPacket.EncodeByte(available ? 1 : 0);

        return oPacket;
    }

    public static OutPacket getSlotUpdate(byte invType, byte newSlots) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.InventoryGrow.getValue());
        oPacket.EncodeByte(invType);
        oPacket.EncodeByte(newSlots);
        return oPacket;
    }

    public static OutPacket messagePacket(MessageInterface message) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.Message.getValue());
        message.messagePacket(oPacket);

        return oPacket;
    }

    public static OutPacket oldEnableActions() {
        return updatePlayerStats(new EnumMap<>(Stat.class), true, null);
    }

    /**
     * StatChanged Packet
     * Updates the characters stats in the game client.
     * 
     * @param pPlayer
     * @param aStats
     * @param bExclRequest
     * @param nMixBaseHairColor
     * @param nMixAddHairColor
     * @param nMixHairBaseProb
     * @param nCharm
     * @param bUpdateCovery
     * @param nHPRecovery
     * @param nMPRecovery
     */
    public static OutPacket OnPlayerStatChanged(User pPlayer, Map<Stat, Long> aStats, boolean bExclRequest, byte nMixBaseHairColor, 
        byte nMixAddHairColor, byte nMixHairBaseProb, byte nCharm, boolean bUpdateCovery, int nHPRecovery, int nMPRecovery) {
        
        OutPacket oPacket = new OutPacket(SendPacketOpcode.StatChanged.getValue());
        oPacket.EncodeBool(bExclRequest);
        long nFlag = 0;
        if (aStats != null) {
            for (Stat pStatUpdate : aStats.keySet()) {
                nFlag |= pStatUpdate.getValue();
            }
        }
        oPacket.EncodeLong(nFlag);
        
        if ((nFlag & Stat.Skin.getValue()) != 0) {
            oPacket.EncodeByte(aStats.get(Stat.Skin).byteValue());
        }
        if ((nFlag & Stat.Face.getValue()) != 0) {
            oPacket.EncodeInt(aStats.get(Stat.Face).intValue());
        }
        if ((nFlag & Stat.Hair.getValue()) != 0) {
            oPacket.EncodeInt(aStats.get(Stat.Hair).intValue());
        }
        if ((nFlag & Stat.Level.getValue()) != 0) {
            oPacket.EncodeByte(aStats.get(Stat.Level).byteValue());
        }
        if ((nFlag & Stat.Job.getValue()) != 0) {
            oPacket.EncodeShort(aStats.get(Stat.Job).shortValue());
            oPacket.EncodeShort(pPlayer.getSubcategory());
        }
        if ((nFlag & Stat.STR.getValue()) != 0) {
            oPacket.EncodeShort(aStats.get(Stat.STR).shortValue());
        }
        if ((nFlag & Stat.DEX.getValue()) != 0) {
            oPacket.EncodeShort(aStats.get(Stat.DEX).shortValue());
        }
        if ((nFlag & Stat.INT.getValue()) != 0) {
            oPacket.EncodeShort(aStats.get(Stat.INT).shortValue());
        }
        if ((nFlag & Stat.LUK.getValue()) != 0) {
            oPacket.EncodeShort(aStats.get(Stat.LUK).shortValue());
        }
        if ((nFlag & Stat.HP.getValue()) != 0) {
            oPacket.EncodeInt(aStats.get(Stat.HP).intValue());
        }
        if ((nFlag & Stat.MaxHP.getValue()) != 0) {
            oPacket.EncodeInt(aStats.get(Stat.MaxHP).intValue());
        }
        if ((nFlag & Stat.MP.getValue()) != 0) {
            oPacket.EncodeInt(aStats.get(Stat.MP).intValue());
        }
        if ((nFlag & Stat.MaxMP.getValue()) != 0) {
            oPacket.EncodeInt(aStats.get(Stat.MaxMP).intValue());
        }
        if ((nFlag & Stat.AP.getValue()) != 0) {
            oPacket.EncodeShort(aStats.get(Stat.AP).shortValue());
        }
        if ((nFlag & Stat.SP.getValue()) != 0) {
            if (GameConstants.isExtendedSpJob(pPlayer.getJob())) {
                oPacket.EncodeByte(pPlayer.getRemainingSpSize());
                for (int i = 0; i < pPlayer.getRemainingSps().length; i++) {
                    if (pPlayer.getRemainingSp(i) > 0) {
                        oPacket.EncodeByte(i + 1);
                        oPacket.EncodeInt(pPlayer.getRemainingSp(i));
                    }
                }
            } else {
                oPacket.EncodeShort(pPlayer.getRemainingSp());
            }
        }
        if ((nFlag & Stat.EXP.getValue()) != 0) {
            oPacket.EncodeLong(aStats.get(Stat.EXP));
        }
        if ((nFlag & Stat.Fame.getValue()) != 0) {
            oPacket.EncodeInt(aStats.get(Stat.Fame).intValue());
        }
        if ((nFlag & Stat.Meso.getValue()) != 0) {
            oPacket.EncodeLong(aStats.get(Stat.Meso));
        }
        if ((nFlag & Stat.Fatigue.getValue()) != 0) {
            oPacket.EncodeShort(aStats.get(Stat.Fatigue).byteValue());
        }
        if ((nFlag & Stat.CharismaEXP.getValue()) != 0) {
            oPacket.EncodeInt(aStats.get(Stat.CharismaEXP).intValue());
        }
        if ((nFlag & Stat.InsightEXP.getValue()) != 0) {
            oPacket.EncodeInt(aStats.get(Stat.InsightEXP).intValue());
        }
        if ((nFlag & Stat.WillEXP.getValue()) != 0) {
            oPacket.EncodeInt(aStats.get(Stat.WillEXP).intValue());
        }
        if ((nFlag & Stat.CraftEXP.getValue()) != 0) {
            oPacket.EncodeInt(aStats.get(Stat.CraftEXP).intValue());
        }
        if ((nFlag & Stat.SenseEXP.getValue()) != 0) {
            oPacket.EncodeInt(aStats.get(Stat.SenseEXP).intValue());
        }
        if ((nFlag & Stat.CharmEXP.getValue()) != 0) {
            oPacket.EncodeInt(aStats.get(Stat.CharmEXP).intValue());
        }
        if ((nFlag & Stat.DayLimit.getValue()) != 0) {
            oPacket.EncodeShort(aStats.get(Stat.CharismaEXP).shortValue());
            oPacket.EncodeShort(aStats.get(Stat.InsightEXP).shortValue());
            oPacket.EncodeShort(aStats.get(Stat.WillEXP).shortValue());
            oPacket.EncodeShort(aStats.get(Stat.CraftEXP).shortValue());
            oPacket.EncodeShort(aStats.get(Stat.SenseEXP).shortValue());
            oPacket.EncodeShort(aStats.get(Stat.CharmEXP).shortValue());
            oPacket.EncodeByte(0);
            oPacket.EncodeLong(PacketHelper.getTime(-2));
        }
        if ((nFlag & Stat.CharacterCard.getValue()) != 0) {
            pPlayer.getCharacterCard().connectData(oPacket);
        }
        if ((nFlag & Stat.PvpEXP.getValue()) != 0) {
            oPacket.EncodeInt(0);  // nExp
            oPacket.EncodeByte(0); // nGrade
            oPacket.EncodeInt(0);  // nPoint
        }
        if ((nFlag & Stat.PvpRank.getValue()) != 0) {
            oPacket.EncodeByte(0); // nPvPModeLevel
            oPacket.EncodeByte(0); // nPvPModeType
        }
        if ((nFlag & Stat.PvpPoints.getValue()) != 0) {
            oPacket.EncodeInt(aStats.get(Stat.PvpPoints).intValue()); // nEventPoint
        }
        
        oPacket.EncodeByte(nMixBaseHairColor);
        oPacket.EncodeByte(nMixAddHairColor);
        oPacket.EncodeByte(nMixHairBaseProb);
        
        oPacket.EncodeBool(nCharm > 0);
        if (nCharm > 0) {
            oPacket.EncodeByte(nCharm);
        }
        
        oPacket.EncodeBool(bUpdateCovery);
        if (bUpdateCovery) {
            oPacket.EncodeInt(nHPRecovery);
            oPacket.EncodeInt(nMPRecovery);
        }
                
        return oPacket;
    }
    
    public static OutPacket OnPlayerStatChanged(User pPlayer, Map<Stat, Long> aStats) {
        return OnPlayerStatChanged(pPlayer, aStats, false, (byte) 0, (byte) 0, (byte) 0, (byte) 0, false, 0, 0);
    }
    
    public static OutPacket enableActions() {
        return OnPlayerStatChanged(null, new EnumMap<>(Stat.class), true, (byte) 0, (byte) 0, (byte) 0, (byte) 0, false, 0, 0); 
    }
    
    /**
     * Old Packet for Player StatChanged
     * @param stats
     * @param chr
     * @return 
     */
    public static OutPacket updatePlayerStats(Map<Stat, Long> stats, User chr) {
        return updatePlayerStats(stats, false, chr);
    }

    public static OutPacket updatePlayerStats(Map<Stat, Long> aStats, boolean bItemReaction, User pPlayer) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.StatChanged.getValue());
        oPacket.EncodeBool(bItemReaction);
        long nFlag = 0;
        if (aStats != null) {
            for (Stat statupdate : aStats.keySet()) {
                nFlag |= statupdate.getValue();
            }
        }
        oPacket.EncodeLong(nFlag);

        if ((nFlag & Stat.Skin.getValue()) != 0) {
            oPacket.EncodeByte(aStats.get(Stat.Skin).byteValue());
        }
        if ((nFlag & Stat.Face.getValue()) != 0) {
            oPacket.EncodeInt(aStats.get(Stat.Face).intValue());
        }
        if ((nFlag & Stat.Hair.getValue()) != 0) {
            oPacket.EncodeInt(aStats.get(Stat.Hair).intValue());
        }
        if ((nFlag & Stat.Level.getValue()) != 0) {
            oPacket.EncodeByte(aStats.get(Stat.Level).byteValue());
        }
        if ((nFlag & Stat.Job.getValue()) != 0) {
            oPacket.EncodeShort(aStats.get(Stat.Job).shortValue());
            oPacket.EncodeShort(pPlayer.getSubcategory());
        }
        if ((nFlag & Stat.STR.getValue()) != 0) {
            oPacket.EncodeShort(aStats.get(Stat.STR).shortValue());
        }
        if ((nFlag & Stat.DEX.getValue()) != 0) {
            oPacket.EncodeShort(aStats.get(Stat.DEX).shortValue());
        }
        if ((nFlag & Stat.INT.getValue()) != 0) {
            oPacket.EncodeShort(aStats.get(Stat.INT).shortValue());
        }
        if ((nFlag & Stat.LUK.getValue()) != 0) {
            oPacket.EncodeShort(aStats.get(Stat.LUK).shortValue());
        }
        if ((nFlag & Stat.HP.getValue()) != 0) {
            oPacket.EncodeInt(aStats.get(Stat.HP).intValue());
        }
        if ((nFlag & Stat.MaxHP.getValue()) != 0) {
            oPacket.EncodeInt(aStats.get(Stat.MaxHP).intValue());
        }
        if ((nFlag & Stat.MP.getValue()) != 0) {
            oPacket.EncodeInt(aStats.get(Stat.MP).intValue());
        }
        if ((nFlag & Stat.MaxMP.getValue()) != 0) {
            oPacket.EncodeInt(aStats.get(Stat.MaxMP).intValue());
        }
        if ((nFlag & Stat.AP.getValue()) != 0) {
            oPacket.EncodeShort(aStats.get(Stat.AP).shortValue());
        }
        if ((nFlag & Stat.SP.getValue()) != 0) {
            if (GameConstants.isExtendedSpJob(pPlayer.getJob())) {
                oPacket.EncodeByte(pPlayer.getRemainingSpSize());
                for (int i = 0; i < pPlayer.getRemainingSps().length; i++) {
                    if (pPlayer.getRemainingSp(i) > 0) {
                        oPacket.EncodeByte(i + 1);
                        oPacket.EncodeInt(pPlayer.getRemainingSp(i));
                    }
                }
            } else {
                oPacket.EncodeShort(pPlayer.getRemainingSp());
            }
        }
        if ((nFlag & Stat.EXP.getValue()) != 0) {
            oPacket.EncodeLong(aStats.get(Stat.EXP));
        }
        if ((nFlag & Stat.Fame.getValue()) != 0) {
            oPacket.EncodeInt(aStats.get(Stat.Fame).intValue());
        }
        if ((nFlag & Stat.Meso.getValue()) != 0) {
            oPacket.EncodeLong(aStats.get(Stat.Meso));
        }
        if ((nFlag & Stat.Fatigue.getValue()) != 0) {
            oPacket.EncodeByte(aStats.get(Stat.Fatigue).byteValue());
        }
        if ((nFlag & Stat.CharismaEXP.getValue()) != 0) {
            oPacket.EncodeInt(aStats.get(Stat.CharismaEXP).intValue());
        }
        if ((nFlag & Stat.InsightEXP.getValue()) != 0) {
            oPacket.EncodeInt(aStats.get(Stat.InsightEXP).intValue());
        }
        if ((nFlag & Stat.WillEXP.getValue()) != 0) {
            oPacket.EncodeInt(aStats.get(Stat.WillEXP).intValue());
        }
        if ((nFlag & Stat.CraftEXP.getValue()) != 0) {
            oPacket.EncodeInt(aStats.get(Stat.CraftEXP).intValue());
        }
        if ((nFlag & Stat.SenseEXP.getValue()) != 0) {
            oPacket.EncodeInt(aStats.get(Stat.SenseEXP).intValue());
        }
        if ((nFlag & Stat.CharmEXP.getValue()) != 0) {
            oPacket.EncodeInt(aStats.get(Stat.CharmEXP).intValue());
        }
        if ((nFlag & Stat.DayLimit.getValue()) != 0) {
            oPacket.EncodeShort(aStats.get(Stat.CharismaEXP).shortValue());
            oPacket.EncodeShort(aStats.get(Stat.InsightEXP).shortValue());
            oPacket.EncodeShort(aStats.get(Stat.WillEXP).shortValue());
            oPacket.EncodeShort(aStats.get(Stat.CraftEXP).shortValue());
            oPacket.EncodeShort(aStats.get(Stat.SenseEXP).shortValue());
            oPacket.EncodeShort(aStats.get(Stat.CharmEXP).shortValue());
            oPacket.EncodeByte(0);
            oPacket.EncodeLong(PacketHelper.getTime(-2));
        }
        if ((nFlag & Stat.AlbaActivity.getValue()) != 0) {
            oPacket.EncodeByte(0);//AlbaActivityID
            oPacket.EncodeInt(0);//AlbaStartTime.dwHighDateTime
            oPacket.EncodeInt(0);//AlbaStartTime.dwLowDateTime
            oPacket.EncodeInt(0);//AlbaDuration
            oPacket.EncodeByte(0);//AlbaSpecialReward
        }
        if ((nFlag & Stat.CharacterCard.getValue()) != 0) {
            pPlayer.getCharacterCard().connectData(oPacket);
        }
        if ((nFlag & Stat.PvpEXP.getValue()) != 0) {
            oPacket.EncodeInt(0); //pvp exp
            oPacket.EncodeByte(0);// grade
            oPacket.EncodeInt(0); //point
        }
        if ((nFlag & Stat.PvpRank.getValue()) != 0) {
            oPacket.EncodeByte(0); //nPvPModeLevel
            oPacket.EncodeByte(0); //nPvPModeType
        }
        if ((nFlag & Stat.PvpPoints.getValue()) != 0) {
            oPacket.EncodeInt(aStats.get(Stat.PvpPoints).intValue()); //EventPoint
        }
        oPacket.EncodeByte(-1);//nMixBaseHairColor
        oPacket.EncodeByte(0);//nMixAddHairColor
        oPacket.EncodeByte(0);//nMixHairBaseProb
        if (nFlag == 0 && !bItemReaction) {
            oPacket.EncodeByte(1);
        }
        oPacket.EncodeByte(0);
        /*
         if ( CInPacket::Decode1(iPacket) )
		  {
		    LOBYTE(aLevelQuest.a) = CInPacket::Decode1(iPacket);
		    if ( TSingleton<CUserLocal>::ms_pInstance._m_pStr )
		      CUserLocal::SetSecondaryStatChangedPoint(TSingleton<CUserLocal>::ms_pInstance._m_pStr, aLevelQuest.a);
		  }
         * */
        oPacket.EncodeByte(0);
        /*
           if ( CInPacket::Decode1(iPacket) )
		  {
		    v46 = CInPacket::Decode4(iPacket);
		    v47 = CInPacket::Decode4(iPacket);
		    v48 = BasicStat::_ZtlSecureGet_nJob(v9);
		    if ( !is_no_mana_job(v48) )
		      CBattleRecordMan::SetBattleRecoveryInfo(
		        TSingleton<CBattleRecordMan>::ms_pInstance,
		        v46,
		        v47,
		        aHPRateQuest.a,
		        sVoiceUOL._m_pStr);
		  } 
         */
        return oPacket;
    }

    public static OutPacket setTemporaryStats(short str, short dex, short _int, short luk, short watk, short matk, short acc, short avoid, short speed, short jump) {
        Map<Temp, Integer> stats = new EnumMap<>(Stat.Temp.class);

        stats.put(Stat.Temp.STR, Integer.valueOf(str));
        stats.put(Stat.Temp.DEX, Integer.valueOf(dex));
        stats.put(Stat.Temp.INT, Integer.valueOf(_int));
        stats.put(Stat.Temp.LUK, Integer.valueOf(luk));
        stats.put(Stat.Temp.WATK, Integer.valueOf(watk));
        stats.put(Stat.Temp.MATK, Integer.valueOf(matk));
        stats.put(Stat.Temp.ACC, Integer.valueOf(acc));
        stats.put(Stat.Temp.EVA, Integer.valueOf(avoid));
        stats.put(Stat.Temp.SPEED, Integer.valueOf(speed));
        stats.put(Stat.Temp.JUMP, Integer.valueOf(jump));

        return temporaryStats(stats);
    }

    public static OutPacket temporaryStats_Aran() {
        Map<Temp, Integer> stats = new EnumMap<>(Stat.Temp.class);

        stats.put(Stat.Temp.STR, Integer.valueOf(999));
        stats.put(Stat.Temp.DEX, Integer.valueOf(999));
        stats.put(Stat.Temp.INT, Integer.valueOf(999));
        stats.put(Stat.Temp.LUK, Integer.valueOf(999));
        stats.put(Stat.Temp.WATK, Integer.valueOf(255));
        stats.put(Stat.Temp.ACC, Integer.valueOf(999));
        stats.put(Stat.Temp.EVA, Integer.valueOf(999));
        stats.put(Stat.Temp.SPEED, Integer.valueOf(140));
        stats.put(Stat.Temp.JUMP, Integer.valueOf(120));

        return temporaryStats(stats);
    }

    public static OutPacket temporaryStats_Balrog(User chr) {
        Map<Temp, Integer> stats = new EnumMap<>(Stat.Temp.class);

        int offset = 1 + (chr.getLevel() - 90) / 20;
        stats.put(Stat.Temp.STR, Integer.valueOf(chr.getStat().getTotalStr() / offset));
        stats.put(Stat.Temp.DEX, Integer.valueOf(chr.getStat().getTotalDex() / offset));
        stats.put(Stat.Temp.INT, Integer.valueOf(chr.getStat().getTotalInt() / offset));
        stats.put(Stat.Temp.LUK, Integer.valueOf(chr.getStat().getTotalLuk() / offset));
        stats.put(Stat.Temp.WATK, Integer.valueOf(chr.getStat().getTotalWatk() / offset));
        stats.put(Stat.Temp.MATK, Integer.valueOf(chr.getStat().getTotalMagic() / offset));

        return temporaryStats(stats);
    }

    public static OutPacket temporaryStats(Map<Stat.Temp, Integer> mystats) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.ForcedStatSet.getValue());
        int updateMask = 0;
        for (Stat.Temp statupdate : mystats.keySet()) {
            updateMask |= statupdate.getValue();
        }
        oPacket.EncodeInt(updateMask);
        for (final Entry<Stat.Temp, Integer> statupdate : mystats.entrySet()) {
            switch (statupdate.getKey()) {
                case SPEED:
                case JUMP:
                case UNKNOWN:
                    oPacket.EncodeByte((statupdate.getValue()).byteValue());
                    break;
                default:
                    oPacket.EncodeShort((statupdate.getValue()).shortValue());
            }
        }

        return oPacket;
    }

    public static OutPacket temporaryStats_Reset() {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.ForcedStatReset.getValue());

        return oPacket;
    }

    public static OutPacket updateSkills(Map<Skill, SkillEntry> update, boolean hyper) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.ChangeSkillRecordResult.getValue());

        oPacket.EncodeByte(1); // setExclRequestSent()
        oPacket.EncodeByte(0); // bShowResult 
        oPacket.EncodeByte(0); // bRemoveLinkSkill

        oPacket.EncodeShort(update.size());

        for (Map.Entry<Skill, SkillEntry> z : update.entrySet()) {
            oPacket.EncodeInt(z.getKey().getId());
            oPacket.EncodeInt(z.getValue().skillevel);
            oPacket.EncodeInt(z.getValue().masterlevel);
            PacketHelper.addExpirationTime(oPacket, z.getValue().expiration);
        }

        //oPacket.Encode(/*hyper ? 0x0C : */4); // hyperstat = 7 // original
        oPacket.EncodeByte(hyper ? 0x0C : 4); // nMovementSerialNumber

        return oPacket;
    }

    public static OutPacket updateSkill(Map<Skill, SkillEntry> update, int skillid, int level, int masterlevel, long expiration) { // lmao this one isnt used 

        OutPacket oPacket = new OutPacket(SendPacketOpcode.ChangeSkillRecordResult.getValue());
        oPacket.EncodeByte(1);
        oPacket.EncodeShort(0);//wasbyte142
        oPacket.EncodeShort(update.size());
        for (Map.Entry z : update.entrySet()) {
            oPacket.EncodeInt(((Skill) z.getKey()).getId());
            oPacket.EncodeInt(((SkillEntry) z.getValue()).skillevel);
            oPacket.EncodeInt(((SkillEntry) z.getValue()).masterlevel);
            PacketHelper.addExpirationTime(oPacket, ((SkillEntry) z.getValue()).expiration);
        }
        oPacket.EncodeByte(/*hyper ? 0x0C : */4); // hyperstat = 7
        return oPacket;
    }

    public static OutPacket giveFameErrorResponse(int op) {
        return OnFameResult(op, null, true, 0);
    }

    public static OutPacket OnFameResult(int op, String charname, boolean raise, int newFame) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.GivePopularityResult.getValue());
        oPacket.EncodeByte(op);
        if ((op == 0) || (op == 5)) {
            oPacket.EncodeString(charname == null ? "" : charname);
            oPacket.EncodeByte(raise ? 1 : 0);
            if (op == 0) {
                oPacket.EncodeInt(newFame);
            }
        }

        return oPacket;
    }

    public static OutPacket bombLieDetector(boolean error, int mapid, int channel) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.AntiMacroResult.getValue());
        oPacket.EncodeByte(error ? 2 : 1);
        oPacket.EncodeInt(mapid);
        oPacket.EncodeInt(channel);

        return oPacket;
    }

    public static OutPacket sendLieDetector(final byte[] image) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.AntiMacroResult.getValue());
        oPacket.EncodeByte(6); // 1 = not attacking, 2 = tested, 3 = going through 

        oPacket.EncodeByte(4); // 2 give invalid pointer (suppose to be admin macro) 
        oPacket.EncodeByte(1); // the time >0 is always 1 minute 
        if (image == null) {
            oPacket.EncodeInt(0);
            return oPacket;
        }
        oPacket.EncodeInt(image.length);
        oPacket.Encode(image);

        return oPacket;
    }

    public static OutPacket LieDetectorResponse(final byte msg) {
        return LieDetectorResponse(msg, (byte) 0);
    }

    public static OutPacket LieDetectorResponse(final byte msg, final byte msg2) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.AntiMacroResult.getValue());
        oPacket.EncodeByte(msg); // 1 = not attacking, 2 = tested, 3 = going through 
        oPacket.EncodeByte(msg2);

        return oPacket;
    }

    public static OutPacket getLieDetector(byte type, String tester) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.AntiMacroResult.getValue()); // 2A 00 01 00 00 00  
        oPacket.EncodeByte(type); // 1 = not attacking, 2 = tested, 3 = going through, 4 save screenshot 
        switch (type) {
            case 4: //save screen shot 
                oPacket.EncodeByte(0);
                oPacket.EncodeString(""); // file name 
                break;
            case 5:
                oPacket.EncodeByte(1); // 2 = save screen shot 
                oPacket.EncodeString(tester); // me or file name 
                break;
            case 6:
                oPacket.EncodeByte(4); // 2 or anything else, 2 = with maple admin picture, basicaly manager's skill? 
                oPacket.EncodeByte(1); // if > 0, then time = 60,000..maybe try < 0? 
                //oPacket.EncodeInt(size);
                //oPacket.encode(byte); // bytes 
                break;
            case 7://send this if failed 
                // 2 = You have been appointed as a auto BOT program user and will be restrained. 
                oPacket.EncodeByte(4); // default 
                break;
            case 9:
                // 0 = passed lie detector test 
                // 1 = reward 5000 mesos for not botting. 
                // 2 = thank you for your cooperation with administrator. 
                oPacket.EncodeByte(0);
                break;
            case 8: // save screen shot.. it appears that you may be using a macro-assisted program
                oPacket.EncodeByte(0); // 2 or anything else , 2 = show msg, 0 = none 
                oPacket.EncodeString(""); // file name 
                break;
            case 10: // no save 
                oPacket.EncodeByte(0); // 2 or anything else, 2 = show msg 
                oPacket.EncodeString(""); // ?? // hi_You have passed the lie detector test 
                break;
            default:
                oPacket.EncodeByte(0);
                break;
        }
        return oPacket;
    }

    public static OutPacket lieDetector(byte mode, byte action, byte[] image, String str1, String str2, String str3) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.AntiMacroResult.getValue());
        oPacket.EncodeByte(mode);
        oPacket.EncodeByte(action); //2 = show msg/save screenshot/maple admin picture(mode 6)
        if (mode == 6) {
            oPacket.EncodeByte(1); //if true time is 60:00
            PacketHelper.addImageInfo(oPacket, image);
        }
        if (mode == 7 || mode == 9) {
        }
        if (mode == 4) { //save screenshot
            oPacket.EncodeString(str1); //file name
        }
        if (mode != 5) {
            if (mode == 10) {
                oPacket.EncodeString(str2); //passed lie detector message
            } else {
                if (mode != 8) {
                }
                oPacket.EncodeString(str2); //failed lie detector, file name (for screenshot)
            }
        }
        oPacket.EncodeString(str3); //file name for screenshot

        return oPacket;
    }

    public static OutPacket report(int mode) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.ClaimResult.getValue());
        oPacket.EncodeByte(mode);
        if (mode == 2) {
            oPacket.EncodeByte(0);
            oPacket.EncodeInt(1); //times left to report
        }

        return oPacket;
    }

    public static OutPacket OnSetClaimSvrAvailableTime(int from, int to) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.SetClaimSvrAvailableTime.getValue());
        oPacket.EncodeByte(from);
        oPacket.EncodeByte(to);

        return oPacket;
    }

    public static OutPacket OnClaimSvrStatusChanged(boolean enable) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.ClaimSvrStatusChanged.getValue());
        oPacket.EncodeByte(enable ? 1 : 0);

        return oPacket;
    }

    public static OutPacket updateMount(User chr, boolean levelup) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.SetTamingMobInfo.getValue());
        oPacket.EncodeInt(chr.getId());
        oPacket.EncodeInt(chr.getMount().getLevel());
        oPacket.EncodeInt(chr.getMount().getExp());
        oPacket.EncodeInt(chr.getMount().getFatigue());
        oPacket.EncodeByte(levelup ? 1 : 0);

        return oPacket;
    }

    public static OutPacket showQuestCompletion(int id) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.QuestClear.getValue());
        oPacket.EncodeInt(id);

        return oPacket;
    }

    public static OutPacket useSkillBook(User chr, int skillid, int maxlevel, boolean canuse, boolean success) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.SkillLearnItemResult.getValue());
        oPacket.EncodeByte(0);
        oPacket.EncodeInt(chr.getId());
        oPacket.EncodeByte(1);
        oPacket.EncodeInt(skillid);
        oPacket.EncodeInt(maxlevel);
        oPacket.EncodeByte(canuse ? 1 : 0);
        oPacket.EncodeByte(success ? 1 : 0);

        return oPacket;
    }

    public static OutPacket useAPSPReset(boolean spReset, int cid) {

        OutPacket oPacket = new OutPacket(spReset ? SendPacketOpcode.SkillResetItemResult.getValue() : SendPacketOpcode.AbilityResetItemResult.getValue());
        oPacket.EncodeByte(1);
        oPacket.EncodeInt(cid);
        oPacket.EncodeByte(1);

        return oPacket;
    }

    public static OutPacket expandCharacterSlots(int mode) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.CharSlotIncItemResult.getValue());
        oPacket.EncodeInt(mode);
        oPacket.EncodeByte(0);

        return oPacket;
    }

    public static OutPacket finishedGather(int type) {
        return gatherSortItem(true, type);
    }

    public static OutPacket finishedSort(int type) {
        return gatherSortItem(false, type);
    }

    public static OutPacket gatherSortItem(boolean gather, int type) {

        OutPacket oPacket = new OutPacket(gather ? SendPacketOpcode.SortItemResult.getValue() : SendPacketOpcode.GatherItemResult.getValue());
        oPacket.EncodeByte(1);
        oPacket.EncodeByte(type);

        return oPacket;
    }

    public static OutPacket updateExpPotion(int mode, int id, int itemId, boolean firstTime, int level, int potionDstLevel) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.ExpItemGetResult.getValue());
        oPacket.EncodeByte(mode);
        oPacket.EncodeByte(1); //bool for get_update_time
        oPacket.EncodeInt(id);
        if (id != 0) {
            oPacket.EncodeByte(1); //not even being read how rude of nexon
            if (mode == 1) {
                oPacket.EncodeInt(0);
            }
            if (mode == 2) {
                oPacket.EncodeByte(firstTime ? 1 : 0); //1 on first time then it turns 0
                oPacket.EncodeInt(itemId);
                if (itemId != 0) {
                    oPacket.EncodeInt(level); //level, confirmed
                    oPacket.EncodeInt(potionDstLevel); //max level with potion
                    oPacket.EncodeLong(384); //random, more like potion id
                }
            }
        }

        return oPacket;
    }

    public static OutPacket updateGender(User chr) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserGenderResult.getValue());
        oPacket.EncodeByte(chr.getGender());

        return oPacket;
    }

    public static OutPacket charInfo(User chr, boolean isSelf) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.CharacterInfo.getValue());
        oPacket.EncodeInt(chr.getId());
        oPacket.EncodeByte(0);
        /*
        if above is true
	    v4 = CInPacket::Decode1(iPacket);
	    v5 = v4 != 0;
	    LOBYTE(bFromStarUserList) = v4 != 0;
	    LOBYTE(bOnline) = CInPacket::Decode1(iPacket) != 0;
	    CInPacket::DecodeStr(iPacket, &sUserName);
	    v66 = 0;
	    if ( !v5 || TSingleton<CUIStarPlanetUserList>::ms_pInstance )
	    {
	      CWvsContext::UI_Close(v2, 234);
	      v48 = bOnline;
	      v47 = bFromStarUserList;
	      nSense = &v46;
	      v46._m_pStr = 0;
	      ZXString<char>::operator=(&v46, &sUserName);
	      v6 = v2->m_dwCharacterID;
	      LOBYTE(v66) = 1;
	      v45 = v3 == v6;
	      v7 = TSingleton<CUIUserInfo_StarPlanet>::CreateInstance();
	      LOBYTE(v66) = 0;
	      CUIUserInfo_StarPlanet::SetUserInfo(v7, v45, v3, v46, v47, v48);
	      if ( v5 )
	      {
	        CUIStarPlanetUserList::AttachUserInfo(TSingleton<CUIStarPlanetUserList>::ms_pInstance, 1);
	        v8 = TSingleton<CUIUserInfo_StarPlanet>::ms_pInstance;
	        v9 = TSingleton<CUIUserInfo_StarPlanet>::ms_pInstance->vfptr;
	        v10 = &TSingleton<CUIStarPlanetUserList>::ms_pInstance->vfptr;
	        v11 = TSingleton<CUIStarPlanetUserList>::ms_pInstance->vfptr;
	        bOnline = TSingleton<CUIStarPlanetUserList>::ms_pInstance->m_width;
	        v12 = v11->GetAbsTop(&TSingleton<CUIStarPlanetUserList>::ms_pInstance->vfptr);
	        v48 = v12;
	        v13 = (*(*v10 + 60))(v10, v12);
	        v48 = bFromStarUserList + v13;
	        (v9[11].Update)(v8, bFromStarUserList + v13);
	      }
	      CUIUserInfo_StarPlanet::OnPacket(TSingleton<CUIUserInfo_StarPlanet>::ms_pInstance, iPacket);
	    }
	    v66 = -1;
	    if ( sUserName._m_pStr )
	    {
	      v14 = sUserName._m_pStr - 12;
	      if ( InterlockedDecrement(sUserName._m_pStr - 3) <= 0 )
	        ZAllocEx<ZAllocStrSelector<char>>::Free(&ZAllocEx<ZAllocStrSelector<char>>::_s_alloc, v14);
	    }
	    return;
	  }
         */
        oPacket.EncodeByte(chr.getLevel());
        oPacket.EncodeShort(chr.getJob());
        oPacket.EncodeShort(chr.getSubcategory());
        oPacket.EncodeByte(chr.getStat().pvpRank);
        oPacket.EncodeInt(chr.getFame());

        final Marriage marriage = chr.getMarriage();
        oPacket.EncodeBool(marriage != null && marriage.getId() != 0);
        if (marriage != null && marriage.getId() != 0) {
            oPacket.EncodeInt(marriage.getId()); //marriage id
            oPacket.EncodeInt(marriage.getHusbandId()); //husband char id
            oPacket.EncodeInt(marriage.getWifeId()); //wife char id
            oPacket.EncodeShort(3); //msg type
            oPacket.EncodeInt(chr.getMarriageItemId()); //ring id husband
            oPacket.EncodeInt(chr.getMarriageItemId()); //ring id wife
            oPacket.EncodeString(marriage.getHusbandName(), 13); //husband name
            oPacket.EncodeString(marriage.getWifeName(), 13); //wife name
        }

        List<Integer> prof = chr.getProfessions();
        oPacket.EncodeByte(prof.size());
        for (Integer profession : prof) {
            oPacket.EncodeShort(profession);
        }
        MapleGuild gs = World.Guild.getGuild(chr.getGuildId());
        if (gs != null) {
            MapleGuildAlliance allianceName = World.Alliance.getAlliance(gs.getAllianceId());
            oPacket.EncodeString(gs.getName());
            oPacket.EncodeString(allianceName != null ? allianceName.getName() : "");
        } else {
            oPacket.EncodeString("-");
            oPacket.EncodeString("");
        }
        oPacket.EncodeByte(-1);//nForcedPetIdx
        oPacket.EncodeBool(!isSelf);
        oPacket.EncodeBool(!chr.getSummonedPets().isEmpty());
        oPacket.EncodeBool(!chr.getSummonedPets().isEmpty());
        if (!chr.getSummonedPets().isEmpty()) {
            for (Pet pet : chr.getSummonedPets()) {
                byte index = 1;
                oPacket.EncodeInt(0); // this is the index of the pet.
                oPacket.EncodeInt(pet.getItem().getItemId());
                oPacket.EncodeString(pet.getName());
                oPacket.EncodeByte(pet.getLevel());
                oPacket.EncodeShort(pet.getCloseness());
                oPacket.EncodeByte(pet.getFullness());
                oPacket.EncodeShort(0);
                Item inv = chr.getInventory(InventoryType.EQUIPPED).getItem((short) (byte) (index == 2 ? -130 : index == 1 ? -114 : -138));
                oPacket.EncodeInt(inv == null ? 0 : inv.getItemId());
                oPacket.EncodeInt(pet.getColor());
                oPacket.EncodeBool(chr.getSummonedPets().size() > index);
                index++;
            }
        }

        final int wishlistSize = chr.getWishlistSize();
        oPacket.EncodeByte(wishlistSize);
        if (wishlistSize > 0) {
            int[] wishlist = chr.getWishlist();
            for (int x = 0; x < wishlistSize; x++) {
                oPacket.EncodeInt(wishlist[x]);
            }
        }
        // Medal
        final Item medal = chr.getInventory(InventoryType.EQUIPPED).getItem((short) EquipSlotType.Medal.getSlot());
        oPacket.EncodeInt(medal == null ? 0 : medal.getItemId());

        // Medal quests
        List<Pair<Integer, Long>> medalQuests = chr.getCompletedMedals();
        oPacket.EncodeShort(medalQuests.size());
        for (Pair<Integer, Long> x : medalQuests) {
            oPacket.EncodeInt(x.left);
            oPacket.EncodeLong(x.right); // Time.
        }

        // Added on v176
        oPacket.EncodeByte(1); // This is probably the size 
        oPacket.EncodeInt(0);
        oPacket.EncodeInt(2431965); // Basic damage skin id
        oPacket.EncodeByte(0);
        oPacket.EncodeString("This is a basic Damage Skin.\r\n\r\n\r\n\r\n\r\n");
        oPacket.EncodeInt(-1);
        oPacket.EncodeInt(0);
        oPacket.EncodeByte(1);
        oPacket.EncodeString("");
        oPacket.EncodeShort(0);
        oPacket.EncodeShort(0);
        // End

        for (Trait.MapleTraitType t : Trait.MapleTraitType.values()) {
            oPacket.EncodeByte(chr.getTrait(t).getLevel());
        }

        oPacket.EncodeInt(chr.getAccountID());
        PacketHelper.addFarmInfo(oPacket, chr.getClient(), 0);

        oPacket.EncodeInt(1);
        oPacket.EncodeInt(0);

        // Chairs
        final List<Integer> chairs = new ArrayList<>();
        for (Item i : chr.getInventory(InventoryType.SETUP).newList()) {
            if (i.getItemId() / 10000 == 301 && !chairs.contains(i.getItemId())) {
                chairs.add(i.getItemId());
            }
        }
        oPacket.EncodeInt(chairs.size());
        for (Integer chair : chairs) {
            oPacket.EncodeInt(chair);
        }
        oPacket.EncodeInt(0);
        oPacket.EncodeInt(30); // No idea, or 0x1E
        oPacket.EncodeInt(0);

        return oPacket;
    }

    public static OutPacket getMonsterBookInfo(User chr) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.ItemCollection_SendCollectionList.getValue());
        oPacket.EncodeInt(chr.getId());
        oPacket.EncodeInt(chr.getLevel());
        chr.getMonsterBook().writeCharInfoPacket(oPacket);

        return oPacket;
    }

    public static OutPacket spawnPortal(int townId, int targetId, int skillId, Point pos) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.TownPortal.getValue());
        oPacket.EncodeInt(townId);
        oPacket.EncodeInt(targetId);
        if ((townId != 999999999) && (targetId != 999999999)) {
            oPacket.EncodeInt(skillId);
            oPacket.EncodePosition(pos);
        }

        return oPacket;
    }

    public static OutPacket mechPortal(Point pos) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.OpenGateCreated.getValue());
        oPacket.EncodePosition(pos);

        return oPacket;
    }

    /*public static OutPacket echoMegaphone(String name, String message) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.ECHO_MESSAGE.getValue());
        oPacket.EncodeByte(0);
        oPacket.EncodeLong(PacketHelper.getTime(System.currentTimeMillis()));
        oPacket.EncodeString(name);
        oPacket.EncodeString(message);

        return oPacket;
    }*/

    public static OutPacket showQuestMsg(String msg) {
        return broadcastMsg(5, msg);
    }

    public static OutPacket Mulung_Pts(int recv, int total) {
        return showQuestMsg(new StringBuilder().append("You have received ").append(recv).append(" training points, for the accumulated total of ").append(total).append(" training points.").toString());
    }

    public static OutPacket broadcastMsg(String message) {
        return broadcastMessage(4, 0, message, false);
    }

    public static OutPacket broadcastMsg(int type, String message) {
        return broadcastMessage(type, 0, message, false);
    }

    public static OutPacket broadcastMsg(int type, int channel, String message) {
        return broadcastMessage(type, channel, message, false);
    }

    public static OutPacket broadcastMsg(int type, int channel, String message, boolean smegaEar) {
        return broadcastMessage(type, channel, message, smegaEar);
    }

    private static OutPacket broadcastMessage(int type, int channel, String message, boolean megaEar) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.BrodcastMsg.getValue());
        oPacket.EncodeByte(type);
        if (type == 4) {
            oPacket.EncodeByte(1);
        }
        if ((type != 23) && (type != 24)) {
            oPacket.EncodeString(message);
        }
        switch (type) {
            case 3:
            case 22:
            case 25:
            case 26:
                oPacket.EncodeByte(channel - 1);
                oPacket.EncodeByte(megaEar ? 1 : 0);
                break;
            case 9:
                oPacket.EncodeByte(channel - 1);
                break;
            case 12:
                oPacket.EncodeInt(channel);
                break;
            case 6:
            case 11:
            case 20:
                oPacket.EncodeInt((channel >= 1000000) && (channel < 6000000) ? channel : 0);
                break;
            /*case 24:
                oPacket.encodeShort(0);
                break;*/
            case 4:
            case 5:
            case 7:
            case 8:
            case 10:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 21:
            case 23:
                break;
        }
        return oPacket;
    }

    public static OutPacket getGachaponMega(String name, String message, Item item, byte rareness, String gacha) {
        return getGachaponMega(name, message, item, rareness, false, gacha);
    }

    public static OutPacket getGachaponMega(String name, String message, Item item, byte rareness, boolean dragon, String gacha) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.BrodcastMsg.getValue());
        oPacket.EncodeByte(13);
        oPacket.EncodeString(new StringBuilder().append(name).append(message).toString());
        if (!dragon) {
            oPacket.EncodeInt(0);
            oPacket.EncodeInt(item.getItemId());
        }
        oPacket.EncodeString(gacha);
        PacketHelper.addItemInfo(oPacket, item);

        return oPacket;
    }

    public static OutPacket getEventEnvelope(int questID, int time) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.BrodcastMsg.getValue());
        oPacket.EncodeByte(23);
        oPacket.EncodeShort(questID);
        oPacket.EncodeInt(time);

        return oPacket;
    }

    public static OutPacket tripleSmega(List<String> message, boolean ear, int channel) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.BrodcastMsg.getValue());
        oPacket.EncodeByte(10);
        if (message.get(0) != null) {
            oPacket.EncodeString((String) message.get(0));
        }
        oPacket.EncodeByte(message.size());
        for (int i = 1; i < message.size(); i++) {
            if (message.get(i) != null) {
                oPacket.EncodeString((String) message.get(i));
            }
        }
        oPacket.EncodeByte(channel - 1);
        oPacket.EncodeByte(ear ? 1 : 0);

        return oPacket;
    }

    public static OutPacket handleItemMegaphone(String sMessage, boolean bWhisper, int nChannelID, Item pItem) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.BrodcastMsg.getValue());
        oPacket.EncodeByte(8);
        oPacket.EncodeString(sMessage);
        oPacket.EncodeByte(nChannelID);
        oPacket.EncodeBool(bWhisper);
        PacketHelper.addItemPosition(oPacket, pItem, true, false);
        if (pItem != null) {
            PacketHelper.addItemInfo(oPacket, pItem);
        }
 
        oPacket.Fill(0, 19);

        return oPacket;
    }

    public static OutPacket getPeanutResult(int itemId, short quantity, int itemId2, short quantity2, int ourItem) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.IncubatorResult.getValue());
        oPacket.EncodeInt(itemId);
        oPacket.EncodeShort(quantity);
        oPacket.EncodeInt(ourItem);
        oPacket.EncodeInt(itemId2);
        oPacket.EncodeInt(quantity2);
        oPacket.EncodeByte(0);
        oPacket.EncodeByte(0);

        return oPacket;
    }

    public static OutPacket getOwlOpen() {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.ShopScannerResult.getValue());
        oPacket.EncodeByte(9);
        oPacket.EncodeByte(GameConstants.owlItems.length);
        for (int i : GameConstants.owlItems) {
            oPacket.EncodeInt(i);
        }

        return oPacket;
    }

    public static OutPacket getOwlSearched(int itemSearch, List<HiredMerchant> hms) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.ShopScannerResult.getValue());
        oPacket.EncodeByte(8);
        oPacket.EncodeInt(0);
        oPacket.EncodeInt(itemSearch);
        int size = 0;

        for (HiredMerchant hm : hms) {
            size += hm.searchItem(itemSearch).size();
        }

        oPacket.EncodeInt(size);
        for (HiredMerchant hm : hms) {
            for (Iterator<HiredMerchant> i = hms.iterator(); i.hasNext();) {
                hm = (HiredMerchant) i.next();
                final List<MaplePlayerShopItem> items = hm.searchItem(itemSearch);
                for (MaplePlayerShopItem item : items) {
                    oPacket.EncodeString(hm.getOwnerName());
                    oPacket.EncodeInt(hm.getMap().getId());
                    oPacket.EncodeString(hm.getDescription());
                    oPacket.EncodeInt(item.item.getQuantity());
                    oPacket.EncodeInt(item.bundles);
                    oPacket.EncodeInt(item.price);
                    switch (2) {
                        case 0:
                            oPacket.EncodeInt(hm.getOwnerId());
                            break;
                        case 1:
                            oPacket.EncodeInt(hm.getStoreId());
                            break;
                        default:
                            oPacket.EncodeInt(hm.getObjectId());
                    }

                    oPacket.EncodeByte(hm.getFreeSlot() == -1 ? 1 : 0);
                    oPacket.EncodeByte(GameConstants.getInventoryType(itemSearch).getType());
                    if (GameConstants.getInventoryType(itemSearch) == InventoryType.EQUIP) {
                        PacketHelper.addItemInfo(oPacket, item.item);
                    }
                }
            }
        }
        return oPacket;
    }

    public static OutPacket getOwlMessage(int msg) {
        OutPacket oPacket = new OutPacket(SendPacketOpcode.ShopLinkResult.getValue());
        oPacket.EncodeByte(msg);

        return oPacket;
    }

    public static OutPacket sendEngagementRequest(String name, int cid) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MarriageRequest.getValue());

        oPacket.EncodeByte(0);
        oPacket.EncodeString(name);
        oPacket.EncodeInt(cid);

        return oPacket;
    }

    public static OutPacket sendEngagement(byte msg, int item, User male, User female) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MarriageResult.getValue());
        oPacket.EncodeByte(msg);
        if (msg == 9 || msg >= 11 && msg <= 14) {
            oPacket.EncodeInt(0);
            oPacket.EncodeInt(male.getId());
            oPacket.EncodeInt(female.getId());
            oPacket.EncodeShort(1);
            oPacket.EncodeInt(item);
            oPacket.EncodeInt(item);
            oPacket.EncodeString(male.getName(), 13);
            oPacket.EncodeString(female.getName(), 13);
        } else if (msg == 10 || msg >= 15 && msg <= 16) {
            oPacket.EncodeString("Male", 13);
            oPacket.EncodeString("Female", 13);
            oPacket.EncodeShort(0);
        }

        return oPacket;
    }

    public static OutPacket sendWeddingGive() {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.WeddingGiftResult.getValue());
        oPacket.EncodeByte(9);
        oPacket.EncodeByte(0);

        return oPacket;
    }

    public static OutPacket sendWeddingReceive() {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.WeddingGiftResult.getValue());
        oPacket.EncodeByte(10);
        oPacket.EncodeLong(-1L);
        oPacket.EncodeInt(0);
        oPacket.EncodeByte(0);

        return oPacket;
    }

    public static OutPacket giveWeddingItem() {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.WeddingGiftResult.getValue());
        oPacket.EncodeByte(11);
        oPacket.EncodeByte(0);
        oPacket.EncodeLong(0L);
        oPacket.EncodeByte(0);

        return oPacket;
    }

    public static OutPacket receiveWeddingItem() {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.WeddingGiftResult.getValue());
        oPacket.EncodeByte(15);
        oPacket.EncodeLong(0L);
        oPacket.EncodeByte(0);

        return oPacket;
    }

    public static OutPacket sendCashPetFood(boolean success, byte index) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.CashPetFoodResult.getValue());
        oPacket.EncodeByte(success ? 0 : 1);
        if (success) {
            oPacket.EncodeByte(index);
        }

        return oPacket;
    }

    public static OutPacket yellowChat(String msg) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.SetWeekEventMessage.getValue());
        oPacket.EncodeByte(-1);
        oPacket.EncodeString(msg);

        return oPacket;
    }

    public static OutPacket shopDiscount(int percent) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.SetPotionDiscountRate.getValue());
        oPacket.EncodeByte(percent);

        return oPacket;
    }

    public static OutPacket catchMob(int mobid, int itemid, byte success) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.BridleMobCatchFail.getValue());
        oPacket.EncodeByte(success);
        oPacket.EncodeInt(itemid);
        oPacket.EncodeInt(mobid);

        return oPacket;
    }

    public static OutPacket spawnPlayerNPC(PlayerNPC npc) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.ImitatedNPCDisableInfo.getValue());
        oPacket.EncodeByte(1);
        oPacket.EncodeInt(npc.getId());
        oPacket.EncodeString(npc.getName());
        PacketHelper.addCharLook(oPacket, npc, true, false);

        return oPacket;
    }

    public static OutPacket disabledNPC(List<Integer> ids) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.LimitedNPCDisableInfo.getValue());
        oPacket.EncodeByte(ids.size());
        for (Integer i : ids) {
            oPacket.EncodeInt(i.intValue());
        }

        return oPacket;
    }

    public static OutPacket getCard(int itemid, int level) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MonsterBookSetCard.getValue());
        oPacket.EncodeByte(itemid > 0 ? 1 : 0);
        if (itemid > 0) {
            oPacket.EncodeInt(itemid);
            oPacket.EncodeInt(level);
        }
        return oPacket;
    }

    public static OutPacket changeCardSet(int set) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.HourChanged.getValue());
        oPacket.EncodeInt(set);

        return oPacket;
    }

    public static OutPacket upgradeBook(Item book, User chr) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.LimitedNPCDisableInfo.getValue());
        oPacket.EncodeInt(book.getPosition());
        PacketHelper.addItemInfo(oPacket, book, chr);

        return oPacket;
    }

    public static OutPacket getCardDrops(int cardid, List<Integer> drops) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MonsterBookCardData.getValue());
        oPacket.EncodeInt(cardid);
        oPacket.EncodeShort(drops == null ? 0 : drops.size());
        if (drops != null) {
            for (Integer de : drops) {
                oPacket.EncodeInt(de);
            }
        }

        return oPacket;
    }

    /*public static OutPacket getFamiliarInfo(User chr) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.JournalAvatar.getValue());
        oPacket.EncodeInt(chr.getFamiliars().size());
        for (MonsterFamiliar mf : chr.getFamiliars().values()) {
            mf.writeRegisterPacket(oPacket, true);
        }
        List<Pair<Integer, Long>> size = new ArrayList<>();
        for (Item i : chr.getInventory(MapleInventoryType.USE).list()) {
            if (i.getItemId() / 10000 == 287) {
                MapleFamiliar f = MapleItemInformationProvider.getInstance().getFamiliarByItem(i.getItemId());
                if (f != null) {
                    size.add(new Pair<>(f.getFamiliar(), i.getInventoryId()));
                }
            }
        }
        oPacket.EncodeInt(size.size());
        for (Pair<Integer, Long> s : size) {
            oPacket.EncodeInt(chr.getId());
            oPacket.EncodeInt(s.left);
            oPacket.EncodeLong(s.right);
            oPacket.EncodeByte(0);
        }
        size.clear();

        return oPacket;
    }*/

    public static OutPacket updateWebBoard(boolean result) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.WebBoardAuthkeyUpdate.getValue());
        oPacket.EncodeBool(result);

        return oPacket;
    }

    public static OutPacket MulungEnergy(int energy) {
        return sendPyramidEnergy("energy", String.valueOf(energy));
    }

    public static OutPacket sendPyramidEnergy(String type, String amount) {
        return sendString(1, type, amount);
    }

    public static OutPacket sendGhostPoint(String type, String amount) {
        return sendString(2, type, amount);
    }

    public static OutPacket sendGhostStatus(String type, String amount) {
        return sendString(3, type, amount);
    }

    public static OutPacket sendString(int type, String object, String amount) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.SessionValue.getValue());
        switch (type) {
            case 1:
                oPacket = new OutPacket(SendPacketOpcode.SessionValue.getValue());
                break;
            case 2:
                oPacket = new OutPacket(SendPacketOpcode.PartyValue.getValue());
                break;
            case 3:
                oPacket = new OutPacket(SendPacketOpcode.FieldValue.getValue());
        }

        oPacket.EncodeString(object);
        oPacket.EncodeString(amount);

        return oPacket;
    }

    public static OutPacket fairyPendantMessage(int termStart, int incExpR) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.BonusExpRateChanged.getValue());
        oPacket.EncodeInt(17);
        oPacket.EncodeInt(0);

        oPacket.EncodeInt(incExpR);

        return oPacket;
    }

    public static OutPacket potionDiscountMessage(int type, int potionDiscR) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.SetPotionDiscountRate.getValue());
        oPacket.EncodeInt(type);
        oPacket.EncodeInt(potionDiscR);

        return oPacket;
    }

    public static OutPacket sendLevelup(boolean family, int level, String name) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.NotifyLevelUp.getValue());
        oPacket.EncodeByte(family ? 1 : 2);
        oPacket.EncodeInt(level);
        oPacket.EncodeString(name);

        return oPacket;
    }

    public static OutPacket sendMarriage(boolean family, String name) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.NotifyWedding.getValue());
        oPacket.EncodeByte(family ? 1 : 0);
        oPacket.EncodeString(name);

        return oPacket;
    }

    //mark packet
    public static OutPacket giveMarkOfTheif(int cid, int oid, int skillid, List<Mob> monsters, Point p1, Point p2, int javelin) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.ForceAtomCreate.getValue());
        oPacket.EncodeByte(1);
        oPacket.EncodeInt(cid);
        oPacket.EncodeInt(oid);
        oPacket.EncodeInt(11); //type
        oPacket.EncodeByte(1);
        oPacket.EncodeInt(monsters.size());
        for (Mob monster : monsters) {
            oPacket.EncodeInt(monster.getObjectId());
        }
        oPacket.EncodeInt(skillid); //skillid
        for (int i = 0; i < monsters.size(); i++) {
            oPacket.EncodeByte(1);
            oPacket.EncodeInt(i + 2);
            oPacket.EncodeInt(1);
            oPacket.EncodeInt(Randomizer.rand(0x2A, 0x2B));
            oPacket.EncodeInt(Randomizer.rand(0x03, 0x04));
            oPacket.EncodeInt(Randomizer.rand(0x43, 0xF5));
            oPacket.EncodeInt(200);
            oPacket.EncodeLong(0);
            oPacket.EncodeInt(Randomizer.nextInt());
            oPacket.EncodeInt(0);
        }
        oPacket.EncodeByte(0);
        //for (Point p : pos) {
        oPacket.EncodeInt(p1.x);
        oPacket.EncodeInt(p1.y);
        oPacket.EncodeInt(p2.x);
        oPacket.EncodeInt(p2.y);
        //}
        oPacket.EncodeInt(javelin);
        //System.out.println(packet.toString());

        oPacket.Fill(0, 69); //We might need this =p
        return oPacket;
    }

    //
    public static OutPacket sendJobup(boolean family, int jobid, String name) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.NotifyJobChange.getValue());
        oPacket.EncodeByte(family ? 1 : 0);
        oPacket.EncodeInt(jobid);
        oPacket.EncodeString(new StringBuilder().append(!family ? "> " : "").append(name).toString());

        return oPacket;
    }

    public static OutPacket getAvatarMega(User chr, int channel, int itemId, List<String> text, boolean ear) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.AvatarMegaphoneUpdateMessage.getValue());
        oPacket.EncodeInt(itemId);
        oPacket.EncodeString(chr.getName());
        for (String i : text) {
            oPacket.EncodeString(i);
        }
        oPacket.EncodeInt(channel - 1);
        oPacket.EncodeByte(ear ? 1 : 0);
        CField.writeCharacterLook(oPacket, chr);

        return oPacket;
    }

    public static OutPacket GMPoliceMessage(boolean dc) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.GMPolice.getValue());
        oPacket.EncodeByte(dc ? 10 : 0);

        return oPacket;
    }

    public static OutPacket followRequest(int chrid) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.SetPassenserRequest.getValue());
        oPacket.EncodeInt(chrid);

        return oPacket;
    }

    public static OutPacket getTopMsg(String msg) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.ScriptProgressMessage.getValue());
        oPacket.EncodeString(msg);

        return oPacket;
    }

    public static OutPacket showMidMsg(String s, int l) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.SetStaticScreenMessage.getValue());
        oPacket.EncodeByte(l);
        oPacket.EncodeString(s);
        oPacket.EncodeByte(s.length() > 0 ? 0 : 1);

        return oPacket;
    }

    public static OutPacket getMidMsg(String msg, boolean keep, int index) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.SetStaticScreenMessage.getValue());
        oPacket.EncodeByte(index);
        oPacket.EncodeString(msg);
        oPacket.EncodeByte(keep ? 0 : 1);

        return oPacket;
    }

    public static OutPacket clearMidMsg() {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.OffStaticScreenMessage.getValue());

        return oPacket;
    }

    /**
     * Shows a special message at the center of the screen.
     *
     * @param msg "Current Festival Progress: 33%. Everyone should lend a hand!" <br/>
     * @param type <br/>1 = Festival<br/>2 = Winter<br/>3 = Chocolate<br/>4 = Rose<br/>5 = Candy<br/>6 = Maple<br/>7 = Fireworks<br/>8 =
     * Sports<br/>9/0xA = Soccer?<br/>0xB = Ghost<br/>0xC = Plants? lol<br/><br/>
     * @param show
     * @param delay - 0 = Show until request to cancel, otherwise it is the time in millis
     * @return
     */
    public static OutPacket getSpecialMsg(String msg, int type, boolean show, int delay) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.WeatherEffectNotice.getValue());
        oPacket.EncodeString(msg);
        oPacket.EncodeInt(type); // 1 = Festival, 2 = Winter, 3 = Chocolate, 4 = Rose, 5 = Candy, 6 = Maple, 7 = Fireworks, 8 = Sports, 9/0xA = Soccer?, 0xB = Ghost, 0xC = Plants? lol
        oPacket.EncodeInt(show ? 0 : delay);
        oPacket.EncodeByte(0); // added on v170~176, idk

        return oPacket;
    }

    public static OutPacket CakePieMsg() {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.WeatherEffectNoticeY.getValue());

        return oPacket;
    }

    public static OutPacket updateJaguar(User from) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.WildHunterInfo.getValue());
        PacketHelper.addJaguarInfo(oPacket, from);

        return oPacket;
    }

    public static OutPacket showBackgroundEffect(String eff, int value) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.ReadyForRespawn.getValue());
        oPacket.EncodeString(eff);
        oPacket.EncodeByte(value);

        return oPacket;
    }

    public static OutPacket sendPinkBeanChoco() {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.ReadyForRespawnByPoint.getValue());
        oPacket.EncodeInt(0);
        oPacket.EncodeByte(1);
        oPacket.EncodeByte(0);
        oPacket.EncodeByte(0);
        oPacket.EncodeInt(0);

        return oPacket;
    }

    public static OutPacket changeChannelMsg(int channel, String msg) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.CharacterHonorGift.getValue());
        oPacket.EncodeInt(channel);
        oPacket.EncodeString(msg);

        return oPacket;
    }

    public static OutPacket pamSongUI() {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.OpenReadyForRespawnUI.getValue());
        return oPacket;
    }

    public static OutPacket ultimateExplorer() {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.OpenUICreatePremiumAdventurer.getValue());

        return oPacket;
    }

    public static OutPacket professionInfo(String skil, int level1, int level2, int chance) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.ResultInstanceTable.getValue());
        oPacket.EncodeString(skil);
        oPacket.EncodeInt(level1);
        oPacket.EncodeInt(level2);
        oPacket.EncodeByte(1);
        oPacket.EncodeInt((skil.startsWith("9200")) || (skil.startsWith("9201")) ? 100 : chance);

        return oPacket;
    }

    public static OutPacket updateAzwanFame(int level, int fame, boolean levelup) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.CharacterHonorExp.getValue());
        oPacket.EncodeInt(level);
        oPacket.EncodeInt(fame);
        oPacket.EncodeByte(levelup ? 1 : 0);

        return oPacket;
    }

    public static OutPacket showSilentCrusadeMsg(byte type, short chapter) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.CrossHunterCompleteResult.getValue());
        oPacket.EncodeByte(type);
        oPacket.EncodeShort(chapter - 1);

        /* type:
         * 0 - open ui (short is chapter)
         * 2 - not enough inventory space
         * 3 - failed due to unknown error
         */
        return oPacket;
    }

    public static OutPacket getSilentCrusadeMsg(byte type) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.CrossHunterShopResult.getValue());
        oPacket.EncodeByte(type);

        return oPacket;
    }

    public static OutPacket showSCShopMsg(byte type) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.CrossHunterShopResult.getValue());
        oPacket.EncodeByte(type);

        return oPacket;
    }

    public static OutPacket updateImpTime() {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.CoolTimeSet.getValue());
        oPacket.EncodeInt(0);
        oPacket.EncodeLong(0L);

        return oPacket;
    }

    public static OutPacket updateImp(MapleImp imp, int mask, int index, boolean login) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.ItemPotChange.getValue());
        oPacket.EncodeByte(login ? 0 : 1);
        oPacket.EncodeInt(index + 1);
        oPacket.EncodeInt(mask);
        if ((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) {
            Pair<?, ?> i = MapleItemInformationProvider.getInstance().getPot(imp.getItemId());
            if (i == null) {
                return enableActions();
            }
            oPacket.EncodeInt(((Integer) i.left).intValue());
            oPacket.EncodeByte(imp.getLevel());
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.STATE.getValue()) != 0)) {
            oPacket.EncodeByte(imp.getState());
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.FULLNESS.getValue()) != 0)) {
            oPacket.EncodeInt(imp.getFullness());
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.CLOSENESS.getValue()) != 0)) {
            oPacket.EncodeInt(imp.getCloseness());
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.CLOSENESS_LEFT.getValue()) != 0)) {
            oPacket.EncodeInt(1);
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.MINUTES_LEFT.getValue()) != 0)) {
            oPacket.EncodeInt(0);
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.LEVEL.getValue()) != 0)) {
            oPacket.EncodeByte(1);
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.FULLNESS_2.getValue()) != 0)) {
            oPacket.EncodeInt(imp.getFullness());
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.UPDATE_TIME.getValue()) != 0)) {
            oPacket.EncodeLong(PacketHelper.getTime(System.currentTimeMillis()));
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.CREATE_TIME.getValue()) != 0)) {
            oPacket.EncodeLong(PacketHelper.getTime(System.currentTimeMillis()));
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.AWAKE_TIME.getValue()) != 0)) {
            oPacket.EncodeLong(PacketHelper.getTime(System.currentTimeMillis()));
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.SLEEP_TIME.getValue()) != 0)) {
            oPacket.EncodeLong(PacketHelper.getTime(System.currentTimeMillis()));
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.MAX_CLOSENESS.getValue()) != 0)) {
            oPacket.EncodeInt(100);
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.MAX_DELAY.getValue()) != 0)) {
            oPacket.EncodeInt(1000);
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.MAX_FULLNESS.getValue()) != 0)) {
            oPacket.EncodeInt(1000);
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.MAX_ALIVE.getValue()) != 0)) {
            oPacket.EncodeInt(1);
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.MAX_MINUTES.getValue()) != 0)) {
            oPacket.EncodeInt(10);
        }
        oPacket.EncodeByte(0);

        return oPacket;
    }

    public static OutPacket getMulungRanking(User chr) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.DojangRanking.getValue());
        oPacket.EncodeInt(0);
        oPacket.EncodeByte(1); //nType

        oPacket.EncodeInt(chr.getJob());
        oPacket.EncodeInt(chr.getLevel());
        oPacket.EncodeInt(chr.getPoints());
        oPacket.EncodeInt(0);//dojo rank
        oPacket.EncodeInt(0); //percent
        oPacket.EncodeInt(0); //nLastPoint
        oPacket.EncodeInt(0); //nLastRank
        oPacket.EncodeInt(0); //nLastPercent

        oPacket.EncodeByte(1); //nTypeID

        DojoRankingsData data = DojoRankingsData.loadLeaderboard();
        oPacket.EncodeInt(data.totalCharacters); // size
        for (int i = 0; i < data.totalCharacters; i++) {
            oPacket.EncodeInt(0); // Rank user job ID
            oPacket.EncodeInt(0); // Rank user job Level
            oPacket.EncodeInt(0); // Rank user points
            oPacket.EncodeInt(data.ranks[i]); // rank
            oPacket.EncodeString(data.names[i]); // Character name

            oPacket.EncodeByte(0); //bPackedCharacterLook
            //packedCharacterLook here. so no thank you.
        }
        return oPacket;
    }

    public static OutPacket getMulungMessage(boolean dc, String msg) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserUseNaviFlyingResult.getValue());
        oPacket.EncodeByte(dc ? 1 : 0);
        oPacket.EncodeString(msg);

        return oPacket;
    }

    public static OutPacket getCandyRanking(ClientSocket c, List<CandyRankingInfo> all) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.HalloweenCandyRankingResult.getValue());
        oPacket.EncodeInt(all.size());
        for (CandyRankingInfo info : all) {
            oPacket.EncodeShort(info.getRank());
            oPacket.EncodeString(info.getName());
        }
        return oPacket;
    }

    public static OutPacket showForeignDamageSkin(User chr, int skinid) {
        OutPacket oPacket = new OutPacket(SendPacketOpcode.UserSetDamageSkin.getValue());
        oPacket.EncodeInt(chr.getId());
        oPacket.EncodeInt(skinid);
        return oPacket;
    }

    public static class AlliancePacket {

        public static OutPacket getAllianceInfo(MapleGuildAlliance alliance) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.AllianceResult.getValue());
            oPacket.EncodeByte(12);
            oPacket.EncodeByte(alliance == null ? 0 : 1);
            if (alliance != null) {
                addAllianceInfo(oPacket, alliance);
            }

            return oPacket;
        }

        private static void addAllianceInfo(OutPacket oPacket, MapleGuildAlliance alliance) {
            oPacket.EncodeInt(alliance.getId());
            oPacket.EncodeString(alliance.getName());
            for (int i = 1; i <= 5; i++) {
                oPacket.EncodeString(alliance.getRank(i));
            }
            oPacket.EncodeByte(alliance.getNoGuilds());
            for (int i = 0; i < alliance.getNoGuilds(); i++) {
                oPacket.EncodeInt(alliance.getGuildId(i));
            }
            oPacket.EncodeInt(alliance.getCapacity());
            oPacket.EncodeString(alliance.getNotice());
        }

        public static OutPacket getGuildAlliance(MapleGuildAlliance alliance) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.AllianceResult.getValue());
            oPacket.EncodeByte(13);
            if (alliance == null) {
                oPacket.EncodeInt(0);
                return oPacket;
            }
            int noGuilds = alliance.getNoGuilds();
            MapleGuild[] g = new MapleGuild[noGuilds];
            for (int i = 0; i < alliance.getNoGuilds(); i++) {
                g[i] = World.Guild.getGuild(alliance.getGuildId(i));
                if (g[i] == null) {
                    return WvsContext.enableActions();
                }
            }
            oPacket.EncodeInt(noGuilds);
            for (MapleGuild gg : g) {
                WvsContext.GuildPacket.getGuildInfo(oPacket, gg);
            }
            return oPacket;
        }

        public static OutPacket allianceMemberOnline(int alliance, int gid, int id, boolean online) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.AllianceResult.getValue());
            oPacket.EncodeByte(14);
            oPacket.EncodeInt(alliance);
            oPacket.EncodeInt(gid);
            oPacket.EncodeInt(id);
            oPacket.EncodeByte(online ? 1 : 0);

            return oPacket;
        }

        public static OutPacket removeGuildFromAlliance(MapleGuildAlliance alliance, MapleGuild expelledGuild, boolean expelled) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.AllianceResult.getValue());
            oPacket.EncodeByte(16);
            addAllianceInfo(oPacket, alliance);
            WvsContext.GuildPacket.getGuildInfo(oPacket, expelledGuild);
            oPacket.EncodeByte(expelled ? 1 : 0);

            return oPacket;
        }

        public static OutPacket addGuildToAlliance(MapleGuildAlliance alliance, MapleGuild newGuild) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.AllianceResult.getValue());
            oPacket.EncodeByte(18);
            addAllianceInfo(oPacket, alliance);
            oPacket.EncodeInt(newGuild.getId());
            WvsContext.GuildPacket.getGuildInfo(oPacket, newGuild);
            oPacket.EncodeByte(0);

            return oPacket;
        }

        public static OutPacket sendAllianceInvite(String allianceName, User inviter) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.AllianceResult.getValue());
            oPacket.EncodeByte(3);
            oPacket.EncodeInt(inviter.getGuildId());
            oPacket.EncodeString(inviter.getName());
            oPacket.EncodeString(allianceName);

            return oPacket;
        }

        public static OutPacket getAllianceUpdate(MapleGuildAlliance alliance) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.AllianceResult.getValue());
            oPacket.EncodeByte(23);
            addAllianceInfo(oPacket, alliance);

            return oPacket;
        }

        public static OutPacket createGuildAlliance(MapleGuildAlliance alliance) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.AllianceResult.getValue());
            oPacket.EncodeByte(15);
            addAllianceInfo(oPacket, alliance);
            int noGuilds = alliance.getNoGuilds();
            MapleGuild[] g = new MapleGuild[noGuilds];
            for (int i = 0; i < alliance.getNoGuilds(); i++) {
                g[i] = World.Guild.getGuild(alliance.getGuildId(i));
                if (g[i] == null) {
                    return WvsContext.enableActions();
                }
            }
            for (MapleGuild gg : g) {
                WvsContext.GuildPacket.getGuildInfo(oPacket, gg);
            }
            return oPacket;
        }

        public static OutPacket updateAlliance(MapleGuildCharacter mgc, int allianceid) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.AllianceResult.getValue());
            oPacket.EncodeByte(24);
            oPacket.EncodeInt(allianceid);
            oPacket.EncodeInt(mgc.getGuildId());
            oPacket.EncodeInt(mgc.getId());
            oPacket.EncodeInt(mgc.getLevel());
            oPacket.EncodeInt(mgc.getJobId());

            return oPacket;
        }

        public static OutPacket updateAllianceLeader(int allianceid, int newLeader, int oldLeader) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.AllianceResult.getValue());
            oPacket.EncodeByte(25);
            oPacket.EncodeInt(allianceid);
            oPacket.EncodeInt(oldLeader);
            oPacket.EncodeInt(newLeader);

            return oPacket;
        }

        public static OutPacket allianceRankChange(int aid, String[] ranks) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.GuildResult.getValue());
            oPacket.EncodeByte(26);
            oPacket.EncodeInt(aid);
            for (String r : ranks) {
                oPacket.EncodeString(r);
            }

            return oPacket;
        }

        public static OutPacket updateAllianceRank(MapleGuildCharacter mgc) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.AllianceResult.getValue());
            oPacket.EncodeByte(27);
            oPacket.EncodeInt(mgc.getId());
            oPacket.EncodeByte(mgc.getAllianceRank());

            return oPacket;
        }

        public static OutPacket changeAllianceNotice(int allianceid, String notice) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.AllianceResult.getValue());
            oPacket.EncodeByte(28);
            oPacket.EncodeInt(allianceid);
            oPacket.EncodeString(notice);

            return oPacket;
        }

        public static OutPacket disbandAlliance(int alliance) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.AllianceResult.getValue());
            oPacket.EncodeByte(29);
            oPacket.EncodeInt(alliance);

            return oPacket;
        }

        public static OutPacket changeAlliance(MapleGuildAlliance alliance, boolean in) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.AllianceResult.getValue());
            oPacket.EncodeByte(1);
            oPacket.EncodeByte(in ? 1 : 0);
            oPacket.EncodeInt(in ? alliance.getId() : 0);
            int noGuilds = alliance.getNoGuilds();
            MapleGuild[] g = new MapleGuild[noGuilds];
            for (int i = 0; i < noGuilds; i++) {
                g[i] = World.Guild.getGuild(alliance.getGuildId(i));
                if (g[i] == null) {
                    return WvsContext.enableActions();
                }
            }
            oPacket.EncodeByte(noGuilds);
            for (int i = 0; i < noGuilds; i++) {
                oPacket.EncodeInt(g[i].getId());

                Collection<MapleGuildCharacter> members = g[i].getMembers();
                oPacket.EncodeInt(members.size());
                for (MapleGuildCharacter mgc : members) {
                    oPacket.EncodeInt(mgc.getId());
                    oPacket.EncodeByte(in ? mgc.getAllianceRank() : 0);
                }
            }

            return oPacket;
        }

        public static OutPacket changeAllianceLeader(int allianceid, int newLeader, int oldLeader) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.AllianceResult.getValue());
            oPacket.EncodeByte(2);
            oPacket.EncodeInt(allianceid);
            oPacket.EncodeInt(oldLeader);
            oPacket.EncodeInt(newLeader);

            return oPacket;
        }

        public static OutPacket changeGuildInAlliance(MapleGuildAlliance alliance, MapleGuild guild, boolean add) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.AllianceResult.getValue());
            oPacket.EncodeByte(4);
            oPacket.EncodeInt(add ? alliance.getId() : 0);
            oPacket.EncodeInt(guild.getId());
            Collection<MapleGuildCharacter> members = guild.getMembers();
            oPacket.EncodeInt(members.size());
            for (MapleGuildCharacter mgc : members) {
                oPacket.EncodeInt(mgc.getId());
                oPacket.EncodeByte(add ? mgc.getAllianceRank() : 0);
            }

            return oPacket;
        }

        public static OutPacket changeAllianceRank(int allianceid, MapleGuildCharacter player) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.AllianceResult.getValue());
            oPacket.EncodeByte(5);
            oPacket.EncodeInt(allianceid);
            oPacket.EncodeInt(player.getId());
            oPacket.EncodeInt(player.getAllianceRank());

            return oPacket;
        }
    }

    public static OutPacket OnLoadAccountIDOfCharacterFriendResult(BuddyList pBuddy) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.LoadAccountIDOfCharacterFriendResult.getValue());

        oPacket.EncodeInt(pBuddy.getBuddies().size());
        for (BuddylistEntry pEntry : pBuddy.getBuddies()) {
            oPacket.EncodeInt(pEntry.getCharacterId());
            oPacket.EncodeInt(pEntry.getAccountId());
        }

        return oPacket;
    }

    /**
     * This packet handles all of the functions for buddies
     *
     * @param buddy
     * @return 
     */
    public static OutPacket buddylistMessage(Buddy buddy) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.FriendResult.getValue());
        oPacket.EncodeByte(buddy.getResult().getId());
        switch (buddy.getResult()) {
            case LOAD_FRIENDS:
                oPacket.EncodeInt(buddy.getEntries().size());
                for (BuddylistEntry entry : buddy.getEntries()) {
                    buddyListEntry(oPacket, entry);
                }
                break;
            case SET_FRIEND_FULL_OTHER:
                
                break;
            case NOTIFY_CHANGE_FRIEND_INFO: //update blocked friends
                oPacket.EncodeInt(buddy.getEntry().getCharacterId()); //dwFriendID
                oPacket.EncodeInt(buddy.getEntry().getAccountId()); //dwFriendAccountID
                buddyListEntry(oPacket, buddy.getEntry());
                break;
            case INVITE:
                BuddylistEntry entry = buddy.getEntry();
                oPacket.EncodeBool(entry.isAccountFriend()); //bInShopOld (char id arrary bool of chars in the cs)
                oPacket.EncodeInt(entry.getCharacterId()); //dwFriendID
                oPacket.EncodeInt(entry.getAccountId());//dwFriendAccountID
                oPacket.EncodeString(entry.getName()); //sFriendName
                oPacket.EncodeInt(buddy.getLevel()); //nLevel
                oPacket.EncodeInt(buddy.getJob()); //nJobCode
                oPacket.EncodeInt(buddy.getSubJob()); //nSubJob
                buddyListEntry(oPacket, entry);
                break;
            case SET_FRIEND_DONE:
                oPacket.EncodeString(""); //sMsg
                break;
            case SET_MESSENGER_MODE:
                oPacket.EncodeInt(0); //m_nMessengerMode
                break;
            case SEND_SINGLE_FRIEND_INFO:
                buddyListEntry(oPacket, buddy.getEntry());
                break;
            case DELETE_FRIEND_DONE:
                oPacket.EncodeBool(buddy.getEntry().isAccountFriend()); //account friend boolean 1= remove account friend
                int id = buddy.getEntry().isAccountFriend() ? buddy.getEntry().getAccountId() : buddy.getEntry().getCharacterId();
                oPacket.EncodeInt(id); //account or friend id to remove
                break;
            case NOTIFY:
                BuddylistEntry ble = buddy.getEntry();
                oPacket.EncodeInt(ble.getCharacterId());//dwFriendID
                oPacket.EncodeInt(ble.getAccountId());//dwFriendAccountID
                oPacket.EncodeBool(ble.isAccountFriend());//sFriendName (must be account friend)
                oPacket.EncodeInt(ble.getChannel()); //m_channelID 
                oPacket.EncodeBool(ble.isAccountFriend()); //sMsg  v93
                oPacket.EncodeByte(ble.getCharacterId()); //dwFriendID
                if (ble.isAccountFriend()) {
                    oPacket.EncodeString(ble.getName());//sCharacterName
                }
                break;
            case CAPACITY:
                oPacket.EncodeByte(buddy.getCapacity()); //v53->nFriendMax
                break;
            case NOTICE_DELETED:
                oPacket.EncodeString(""); //sTargetName
                break;
            case INVITE_EVENT_BESTFRIEND:
                oPacket.EncodeString(""); //dwFriendID
                break;
            case REFUSE_EVENT_BESTFRIEND:
                oPacket.EncodeInt(0); //dwFriendID
                oPacket.EncodeString(""); //sFriendName
                break;
            /* case 55:
            	oPacket.encodeString(""); //sCharacterName
            	break;
            case 70:
            	oPacket.encodeString(""); //sTargetName
            	break;
            case SET_FRIEND_UNKNOWN:
		    case ACCEPT_FRIEND_UNKNOWN:
		    case DELETE_FRIEND_UNKNOWN:
		    case CAPACITY_UNKNOWN:
		    case 68:
		    case 73: 
		    	oPacket.encode(0);
		    	
		    	 if above == 0
		    	 	oPacket.encodeString
		    	
		    	break; */
            default:
                break;
        }
        return oPacket;
    }

    /**
     * This is where the entry of each buddy is defined
     *
     * @param oPacket
     * @param BuddylistEntry
     */
    private static void buddyListEntry(OutPacket oPacket, BuddylistEntry ble) {
        oPacket.EncodeInt(ble.getCharacterId());
        oPacket.EncodeString(ble.getName(), 13);
        oPacket.EncodeByte(ble.getFlag());
        oPacket.EncodeInt(ble.getChannel() - 1);
        oPacket.EncodeString(ble.getGroup(), 17);
        oPacket.EncodeByte(0);//nMobile
        oPacket.EncodeInt(ble.getAccountId());
        oPacket.EncodeString(ble.getNickname(), 13);
        oPacket.EncodeString(ble.getMemo(), 256);
        oPacket.EncodeInt(0); //bInShop
    }

    public static OutPacket giveKilling(int x) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.TemporaryStatSet.getValue());
        //PacketHelper.writeSingleMask(oPacket, CharacterTemporaryStat.KILL_COUNT);
//        oPacket.EncodeInt(0);
//        oPacket.encode(0);
//        oPacket.EncodeInt(x);
//        oPacket.fill(0, 6);
        oPacket.EncodeShort(0);
        oPacket.EncodeByte(0);
        oPacket.EncodeInt(x);
        return oPacket;
    }

    public static class ExpeditionPacket {

        public static OutPacket expeditionStatus(MapleExpedition me, boolean created, boolean silent) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.ExpedtionResult.getValue());
            oPacket.EncodeByte(created ? 86 : silent ? 72 : 76);//74
            oPacket.EncodeInt(me.getType().exped);
            oPacket.EncodeInt(0);
            for (int i = 0; i < 6; i++) {
                if (i < me.getParties().size()) {
                    MapleParty party = World.Party.getParty((me.getParties().get(i)).intValue());

                    WvsContext.PartyPacket.addPartyStatus(-1, party, oPacket, false, true);
                } else {
                    WvsContext.PartyPacket.addPartyStatus(-1, null, oPacket, false, true);
                }

            }

            return oPacket;
        }

        public static OutPacket expeditionError(int errcode, String name) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.ExpedtionResult.getValue());
            oPacket.EncodeByte(100);//88
            oPacket.EncodeInt(errcode);
            oPacket.EncodeString(name);

            return oPacket;
        }

        public static OutPacket expeditionMessage(int code) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.ExpedtionResult.getValue());
            oPacket.EncodeByte(code);

            return oPacket;
        }

        public static OutPacket expeditionJoined(String name) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.ExpedtionResult.getValue());
            oPacket.EncodeByte(87);//75
            oPacket.EncodeString(name);

            return oPacket;
        }

        public static OutPacket expeditionLeft(String name) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.ExpedtionResult.getValue());
            oPacket.EncodeByte(91);//79
            oPacket.EncodeString(name);

            return oPacket;
        }

        public static OutPacket expeditionLeaderChanged(int newLeader) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.ExpedtionResult.getValue());
            oPacket.EncodeByte(96);//84
            oPacket.EncodeInt(newLeader);

            return oPacket;
        }

        public static OutPacket expeditionUpdate(int partyIndex, MapleParty party) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.ExpedtionResult.getValue());
            oPacket.EncodeByte(97);//85
            oPacket.EncodeInt(0);
            oPacket.EncodeInt(partyIndex);

            WvsContext.PartyPacket.addPartyStatus(-1, party, oPacket, false, true);

            return oPacket;
        }

        public static OutPacket expeditionInvite(User from, int exped) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.ExpedtionResult.getValue());
            oPacket.EncodeByte(99);//87
            oPacket.EncodeInt(from.getLevel());
            oPacket.EncodeInt(from.getJob());
            oPacket.EncodeInt(0);
            oPacket.EncodeString(from.getName());
            oPacket.EncodeInt(exped);

            return oPacket;
        }
    }

    public static class PartyPacket {

        class PartyOperations {

            public static final int userUpdate = 19;
            public static final int createParty = 20;
            public static final int disbandParty = 25;
            public static final int joinParty = 28;
            public static final int leaderChange = 55;
            public static final int createPortal = 94;
            public static final int changePartySettings = 85;
        }

        public static OutPacket changePartySettings(String newName, boolean isPrivate) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.PartyResult.getValue());
            oPacket.EncodeByte(PartyOperations.changePartySettings);
            oPacket.EncodeBool(isPrivate);
            oPacket.EncodeString(newName);

            return oPacket;
        }

        public static OutPacket partyCreated(MapleParty party) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.PartyResult.getValue());
            oPacket.EncodeByte(PartyOperations.createParty);
            oPacket.EncodeInt(party.getId());
            oPacket.EncodeInt(party.getLeader().getDoorTown());
            oPacket.EncodeInt(party.getLeader().getDoorTarget());
            oPacket.EncodeInt(0); //nGrade
            oPacket.EncodeShort(party.getLeader().getDoorPosition().x);
            oPacket.EncodeShort(party.getLeader().getDoorPosition().y);
            oPacket.EncodeByte((byte) party.getLeader().getId());
            oPacket.EncodeByte(0);
            oPacket.EncodeBool(party.isPrivate());
            oPacket.EncodeByte(0);
            oPacket.EncodeString(party.getName());

            return oPacket;
        }

        public static OutPacket partyInvite(User from) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.PartyResult.getValue());
            oPacket.EncodeByte(4);
            oPacket.EncodeInt(from.getParty() == null ? 0 : from.getParty().getId());
            oPacket.EncodeString(from.getName());
            oPacket.EncodeInt(from.getLevel());
            oPacket.EncodeInt(from.getJob());
            oPacket.EncodeInt(0);
            oPacket.EncodeByte(0);
            oPacket.EncodeByte(0);
            return oPacket;
        }

        public static OutPacket partyRequestInvite(User from) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.PartyResult.getValue());
            oPacket.EncodeByte(7);
            oPacket.EncodeInt(from.getId());
            oPacket.EncodeString(from.getName());
            oPacket.EncodeInt(from.getLevel());
            oPacket.EncodeInt(from.getJob());

            return oPacket;
        }

        public static OutPacket partyStatusMessage(int message, String charname) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.PartyResult.getValue());
            oPacket.EncodeByte(message);
            if ((message == 34) || (message == 56)) {
                oPacket.EncodeString(charname);
            } else if (message == 49) {
                oPacket.EncodeByte(0);
            }

            return oPacket;
        }

        public static void addPartyStatus(int forchannel, MapleParty party, OutPacket lew, boolean leaving) {
            addPartyStatus(forchannel, party, lew, leaving, false);
        }

        public static void addPartyStatus(int forchannel, MapleParty party, OutPacket oPacket, boolean leaving, boolean exped) {
            List<MaplePartyCharacter> partymembers;
            if (party == null) {
                partymembers = new ArrayList<>();
            } else {
                partymembers = new ArrayList<>(party.getMembers());
            }
            while (partymembers.size() < 6) {
                partymembers.add(new MaplePartyCharacter());
            }
            for (MaplePartyCharacter partychar : partymembers) {
                oPacket.EncodeInt(partychar.getId());
            }
            for (MaplePartyCharacter partychar : partymembers) {
                oPacket.EncodeString(partychar.getName(), 13);
            }
            for (MaplePartyCharacter partychar : partymembers) {
                oPacket.EncodeInt(partychar.getJobId());
            }
            for (MaplePartyCharacter partychar : partymembers) { // this is subjob rofl
                oPacket.EncodeInt(0);
            }
            for (MaplePartyCharacter partychar : partymembers) {
                oPacket.EncodeInt(partychar.getLevel());
            }
            for (MaplePartyCharacter partychar : partymembers) {
                oPacket.EncodeInt(partychar.isOnline() ? partychar.getChannel() - 1 : -2);
            }
            for (MaplePartyCharacter partychar : partymembers) { // bAccountShutdown
                oPacket.EncodeInt(0);
            }
            for (MaplePartyCharacter partychar : partymembers) { // Unknown
                oPacket.EncodeInt(0);
            }
            oPacket.EncodeInt(party == null ? 0 : party.getLeader().getId());
            if (exped) {
                return;
            }
            for (MaplePartyCharacter partychar : partymembers) {
                oPacket.EncodeInt(partychar.getChannel() == forchannel ? partychar.getMapid() : 999999999);
            }
            for (MaplePartyCharacter partychar : partymembers) {
                if (partychar.getChannel() == forchannel && !leaving) {
                    oPacket.EncodeInt(partychar.getDoorTown());
                    oPacket.EncodeInt(partychar.getDoorTarget());
                    oPacket.EncodeInt(partychar.getDoorSkill());
                    oPacket.EncodeInt(partychar.getDoorPosition().x);
                    oPacket.EncodeInt(partychar.getDoorPosition().y);
                } else {
                    oPacket.EncodeInt(leaving ? 999999999 : 0);
                    oPacket.EncodeLong(leaving ? 999999999 : 0);
                    oPacket.EncodeLong(leaving ? -1 : 0);
                }
            }
            oPacket.EncodeBool(party.isPrivate());
            oPacket.EncodeBool(false); // Unknown
            oPacket.EncodeString(party.getName());
        }

        public static OutPacket updateParty(int forChannel, MapleParty party, PartyOperation op, MaplePartyCharacter target) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.PartyResult.getValue());
            switch (op) {
                case DISBAND:
                case EXPEL:
                case LEAVE:
                    oPacket.EncodeByte(PartyOperations.disbandParty);
                    oPacket.EncodeInt(party.getId());
                    oPacket.EncodeInt(target.getId());
                    oPacket.EncodeByte(op == PartyOperation.DISBAND ? 0 : 1);
                    if (op == PartyOperation.DISBAND) {
                        break;
                    }
                    oPacket.EncodeByte(op == PartyOperation.EXPEL ? 1 : 0);
                    oPacket.EncodeString(target.getName());
                    addPartyStatus(forChannel, party, oPacket, op == PartyOperation.LEAVE);
                    break;
                case JOIN:
                    oPacket.EncodeByte(PartyOperations.joinParty);
                    oPacket.EncodeInt(party.getId());
                    oPacket.EncodeString(target.getName());
                    oPacket.EncodeByte(0);
                    oPacket.EncodeInt(0);
                    addPartyStatus(forChannel, party, oPacket, false);
                    break;
                case SILENT_UPDATE:
                case LOG_ONOFF:
                    oPacket.EncodeByte(PartyOperations.userUpdate);
                    oPacket.EncodeInt(party.getId());
                    addPartyStatus(forChannel, party, oPacket, op == PartyOperation.LOG_ONOFF);
                    break;
                case CHANGE_LEADER:
                case CHANGE_LEADER_DC:
                    oPacket.EncodeByte(PartyOperations.leaderChange);
                    oPacket.EncodeInt(target.getId());
                    oPacket.EncodeByte(op == PartyOperation.CHANGE_LEADER_DC ? 1 : 0);
                    break;
            }

            oPacket.Fill(0, 19);

            return oPacket;
        }

        public static OutPacket partyPortal(int townId, int targetId, int skillId, Point position, boolean animation) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.PartyResult.getValue());
            oPacket.EncodeByte(PartyOperations.createPortal);
            oPacket.EncodeByte(animation ? 0 : 1);
            oPacket.EncodeInt(townId);
            oPacket.EncodeInt(targetId);
            oPacket.EncodeInt(skillId);
            oPacket.EncodePosition(position);

            return oPacket;
        }

        public static OutPacket getPartyListing(PartySearchType pst) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.PartyResult.getValue());
            oPacket.EncodeByte(147);
            oPacket.EncodeInt(pst.id);
            final List<PartySearch> parties = World.Party.searchParty(pst);
            oPacket.EncodeInt(parties.size());
            for (PartySearch party : parties) {
                oPacket.EncodeInt(0);
                oPacket.EncodeInt(2);
                if (pst.exped) {
                    MapleExpedition me = World.Party.getExped(party.getId());
                    oPacket.EncodeInt(me.getType().maxMembers);
                    oPacket.EncodeInt(party.getId());
                    oPacket.EncodeString(party.getName(), 48);
                    for (int i = 0; i < 5; i++) {
                        if (i < me.getParties().size()) {
                            MapleParty part = World.Party.getParty((me.getParties().get(i)).intValue());
                            if (part != null) {
                                addPartyStatus(-1, part, oPacket, false, true);
                            } else {
                                oPacket.Fill(0, 202);
                            }
                        } else {
                            oPacket.Fill(0, 202);
                        }
                    }
                } else {
                    oPacket.EncodeInt(0);
                    oPacket.EncodeInt(party.getId());
                    oPacket.EncodeString(party.getName(), 48);
                    addPartyStatus(-1, World.Party.getParty(party.getId()), oPacket, false, true);
                }

                oPacket.EncodeShort(0);
            }

            return oPacket;
        }

        public static OutPacket partyListingAdded(PartySearch ps) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.PartyResult.getValue());
            oPacket.EncodeByte(93);
            oPacket.EncodeInt(ps.getType().id);
            oPacket.EncodeInt(0);
            oPacket.EncodeInt(1);
            if (ps.getType().exped) {
                MapleExpedition me = World.Party.getExped(ps.getId());
                oPacket.EncodeInt(me.getType().maxMembers);
                oPacket.EncodeInt(ps.getId());
                oPacket.EncodeString(ps.getName(), 48);
                for (int i = 0; i < 5; i++) {
                    if (i < me.getParties().size()) {
                        MapleParty party = World.Party.getParty((me.getParties().get(i)).intValue());
                        if (party != null) {
                            addPartyStatus(-1, party, oPacket, false, true);
                        } else {
                            oPacket.Fill(0, 202);
                        }
                    } else {
                        oPacket.Fill(0, 202);
                    }
                }
            } else {
                oPacket.EncodeInt(0);
                oPacket.EncodeInt(ps.getId());
                oPacket.EncodeString(ps.getName(), 48);
                addPartyStatus(-1, World.Party.getParty(ps.getId()), oPacket, false, true);
            }
            oPacket.EncodeShort(0);

            return oPacket;
        }

        public static OutPacket showMemberSearch(List<User> chr) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.PartyMemberCandidateResult.getValue());
            oPacket.EncodeByte(chr.size());
            for (User c : chr) {
                oPacket.EncodeInt(c.getId());
                oPacket.EncodeString(c.getName());
                oPacket.EncodeShort(c.getJob());
                oPacket.EncodeByte(c.getLevel());
            }
            return oPacket;
        }

        public static OutPacket showPartySearch(List<MapleParty> chr) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.PartyCandidateResult.getValue());
            oPacket.EncodeByte(chr.size());
            for (MapleParty c : chr) {
                oPacket.EncodeInt(c.getId());
                oPacket.EncodeString(c.getLeader().getName());
                oPacket.EncodeByte(c.getLeader().getLevel());
                oPacket.EncodeByte(c.getLeader().isOnline() ? 1 : 0);
                oPacket.EncodeByte(c.getMembers().size());
                for (MaplePartyCharacter ch : c.getMembers()) {
                    oPacket.EncodeInt(ch.getId());
                    oPacket.EncodeString(ch.getName());
                    oPacket.EncodeShort(ch.getJobId());
                    oPacket.EncodeByte(ch.getLevel());
                    oPacket.EncodeByte(ch.isOnline() ? 1 : 0);
                }
            }
            return oPacket;
        }
    }

    public static class GuildPacket {

        public static class GuildResult {

            //[2016-09-15 08:50:09.528][Outbound] [018A] 04 0A 00 4B 61 7A 49 73 41 4E 75 62 62
            //[2016-09-15 08:50:09.614][Inbound ] [007F] 05 0A 00 4B 61 7A 49 73 41 4E 75 62 62
            public static final int SendGuildInvite = 7;
            public static final int GuildWaitingList = 0x2F;
            public static final int LoadGuild_Done = 48,
                FindGuild_Done = 49,
                CheckGuildName_Available = 50,
                CheckGuildName_AlreadyUsed = 51,
                CheckGuildName_Unknown = 52,
                CreateGuildAgree_Reply = 53,
                CreateGuildAgree_Unknown = 54,
                CreateNewGuild_Done = 55,
                CreateNewGuild_AlreayJoined = 56,
                CreateNewGuild_GuildNameAlreayExist = 57,
                CreateNewGuild_Beginner = 58,
                CreateNewGuild_Disagree = 59,
                CreateNewGuild_NotFullParty = 60,
                CreateNewGuild_Unknown = 61,
                JoinGuild_Done = 62,
                JoinGuild_AlreadyJoined = 63,
                JoinGuild_AlreadyFull = 64,
                JoinGuild_UnknownUser = 65,
                JoinGuild_NonRequestFindUser = 68,
                JoinGuild_Unknown = 69,
                JoinRequest_Done = 70,
                JoinRequest_DoneToUser = 71,
                JoinRequest_AlreadyFull = 72,
                JoinRequest_LimitTime = 73,
                JoinRequest_Unknown = 74,
                JoinCancelRequest_Done = 75,
                WithdrawGuild_Done = 76,
                WithdrawGuild_NotJoined = 77,
                WithdrawGuild_Unknown = 78,
                KickGuild_Done = 79,
                KickGuild_NotJoined = 80,
                KickGuild_Unknown = 81,
                RemoveGuild_Done = 82,
                RemoveGuild_NotExist = 83,
                RemoveGuild_Unknown = 84,
                RemoveRequestGuild_Done = 85,
                InviteGuild_BlockedUser = 86,
                InviteGuild_AlreadyInvited = 87,
                InviteGuild_Rejected = 88,
                AdminCannotCreate = 89,
                AdminCannotInvite = 90,
                IncMaxMemberNum_Done = 91,
                IncMaxMemberNum_Unknown = 92,
                ChangeMemberName = 93,
                ChangeRequestUserName = 94,
                ChangeLevelOrJob = 95,
                NotifyLoginOrLogout = 96,
                SetGradeName_Done = 97,
                SetGradeName_Unknown = 98,
                SetMemberGrade_Done = 99,
                SetMemberGrade_Unknown = 100,
                SetMemberCommitment_Done = 101,
                SetMark_Done = 102,
                SetMark_Unknown = 103,
                SetNotice_Done = 104,
                InsertQuest = 105,
                NoticeQuestWaitingOrder = 106,
                SetGuildCanEnterQuest = 107,
                IncPoint_Done = 108,
                ShowGuildRanking = 109,
                SetGGP_Done = 110,
                SetIGP_Done = 111,
                GuildQuest_NotEnoughUser = 112,
                GuildQuest_RegisterDisconnected = 113,
                GuildQuest_NoticeOrder = 114,
                Authkey_Update = 115,
                SetSkill_Done = 116,
                SetSkill_Extend_Unknown = 117,
                SetSkill_LevelSet_Unknown = 118,
                SetSkill_ResetBattleSkill = 119,
                UseSkill_Success = 120,
                UseSkill_Err = 121,
                ChangeName_Done = 122,
                ChangeName_Unknown = 123,
                ChangeMaster_Done = 124,
                ChangeMaster_Unknown = 125,
                BlockedBehaviorCreate = 126,
                BlockedBehaviorJoin = 127,
                BattleSkillOpen = 128,
                GetData = 129,
                Rank_Reflash = 130,
                FindGuild_Error = 131,
                ChangeMaster_Pinkbean = 132;
        }

        //Can be removed once these last few are figured out/done
        public enum guildOperations {

            CREATE_GUILD_NAME_INPUT_UI(3),
            SET_GUILD_MARK(0x16),
            CREATE_GUILD_ACCEPT_UI(5),
            CREATE_GUILD(7);
            private final int mode;

            private guildOperations(int mode) {
                this.mode = mode;
            }

            public int get() {
                return mode;
            }
        }

        public static OutPacket guildInvite(int gid, String charName, int levelFrom, int jobFrom) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.GuildResult.getValue());
            oPacket.EncodeByte(GuildResult.SendGuildInvite);//?
            oPacket.EncodeInt(gid);
            oPacket.EncodeString(charName);
            oPacket.EncodeInt(levelFrom);
            oPacket.EncodeInt(jobFrom);
            oPacket.EncodeInt(0);
            return oPacket;
        }

        public static OutPacket loadGuild_Done(User c) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.GuildResult.getValue());
            oPacket.EncodeByte(GuildResult.LoadGuild_Done);//32
            if ((c == null) || (c.getMGC() == null)) {
                oPacket.EncodeByte(0);
                return oPacket;
            }
            MapleGuild g = World.Guild.getGuild(c.getGuildId());
            if (g == null) {
                oPacket.EncodeByte(0);
                return oPacket;
            }
            oPacket.EncodeByte(0);//new 149
            oPacket.EncodeByte(1);
            getGuildInfo(oPacket, g);
            oPacket.EncodeInt(GameConstants.guildexp.length);//new 149
            for (int i : GameConstants.guildexp) {
                oPacket.EncodeInt(i);
            }
            return oPacket;
        }

        public static OutPacket findGuild_Done(User c, int guildId) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.GuildResult.getValue());
            oPacket.EncodeByte(GuildResult.FindGuild_Done);
            oPacket.EncodeInt(guildId);
            getGuildInfo(oPacket, World.Guild.getGuild(guildId));
            return oPacket;
        }

        public static void getGuildInfo(OutPacket oPacket, MapleGuild guild) {
            oPacket.EncodeInt(guild.getId());
            oPacket.EncodeString(guild.getName());
            for (int i = 1; i <= 5; i++) {
                oPacket.EncodeString(guild.getRankTitle(i));
            }
            guild.addMemberData(oPacket);
            guild.addPendingMemberData(oPacket);
            oPacket.EncodeInt(guild.getCapacity());
            oPacket.EncodeShort(guild.getLogoBG());
            oPacket.EncodeByte(guild.getLogoBGColor());
            oPacket.EncodeShort(guild.getLogo());
            oPacket.EncodeByte(guild.getLogoColor());
            oPacket.EncodeString(guild.getNotice());
            oPacket.EncodeInt(guild.getGP());//point
            oPacket.EncodeInt(guild.getGP());//seasonpoint
            oPacket.EncodeInt(guild.getAllianceId() > 0 ? guild.getAllianceId() : 0);
            oPacket.EncodeByte(guild.getLevel());
            oPacket.EncodeShort(0);//guildRank
            oPacket.EncodeInt(guild.getGP());//new 149 - GP
            oPacket.EncodeShort(guild.getSkills().size());
            for (MapleGuildSkill i : guild.getSkills()) {
                oPacket.EncodeInt(i.skillID);
                oPacket.EncodeShort(i.level);
                oPacket.EncodeLong(PacketHelper.getTime(i.timestamp));
                oPacket.EncodeString(i.purchaser);
                oPacket.EncodeString(i.activator);
            }
            boolean hasJoinRequirements = false;
            oPacket.EncodeBool(hasJoinRequirements);
            if (hasJoinRequirements) {
                oPacket.EncodeByte(0);//nJoinSetting
                oPacket.EncodeInt(0);//nReqLevel
            }
        }

        public static OutPacket createNewGuild(User c) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.GuildResult.getValue());
            oPacket.EncodeByte(GuildResult.CreateNewGuild_Done);
            if ((c == null) || (c.getMGC() == null)) {
                return genericGuildMessage((byte) GuildResult.CheckGuildName_AlreadyUsed);
            }
            MapleGuild g = World.Guild.getGuild(c.getGuildId());
            if (g == null) {
                return genericGuildMessage((byte) GuildResult.CheckGuildName_AlreadyUsed);
            }
            getGuildInfo(oPacket, g);

            return oPacket;
        }

        public static void guildMemberEncode(OutPacket oPacket, MapleGuildCharacter mgc) {
            oPacket.EncodeString(mgc.getName(), 13);
            oPacket.EncodeInt(mgc.getJobId());
            oPacket.EncodeInt(mgc.getLevel());
            oPacket.EncodeInt(mgc.getGuildRank());
            oPacket.EncodeInt(mgc.isOnline() ? 1 : 0);
            oPacket.EncodeInt(mgc.getAllianceRank());
            oPacket.EncodeInt(mgc.getGuildContribution());//igp
            oPacket.EncodeInt(mgc.getGuildContribution());//daily commitment
            oPacket.EncodeInt(mgc.getGuildContribution());//nggp
            oPacket.EncodeLong(PacketHelper.getTime(-1L));//commitmentIncTime
        }

        public static OutPacket joinGuild(MapleGuildCharacter mgc) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.GuildResult.getValue());
            oPacket.EncodeByte(GuildResult.JoinGuild_Done);
            oPacket.EncodeInt(mgc.getGuildId());
            oPacket.EncodeInt(mgc.getId());//shld be master charid
            guildMemberEncode(oPacket, mgc);

            return oPacket;
        }

        public static OutPacket disbandGuild(int guildId) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.GuildResult.getValue());
            oPacket.EncodeByte(GuildResult.RemoveGuild_Done);
            oPacket.EncodeInt(guildId);

            return oPacket;
        }

        public static OutPacket removeMember(MapleGuildCharacter mgc, boolean bExpelled) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.GuildResult.getValue());
            //oPacket.Encode(GuildResult.RemoveGuild_Done);
            oPacket.EncodeByte(bExpelled ? GuildResult.KickGuild_Done : GuildResult.RemoveGuild_Done);
            oPacket.EncodeInt(mgc.getGuildId());
            oPacket.EncodeInt(mgc.getId());
            oPacket.EncodeString(mgc.getName());

            oPacket.Fill(0, 19);
            
            return oPacket;
        }

        public static OutPacket guildDisbanded(int gid) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.GuildResult.getValue());
            oPacket.EncodeByte(GuildResult.RemoveGuild_NotExist);
            oPacket.EncodeInt(gid);

            return oPacket;
        }

        public static OutPacket increaseMaxMemberNum(int gid, int capacity) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.GuildResult.getValue());
            oPacket.EncodeByte(GuildResult.IncMaxMemberNum_Done);
            oPacket.EncodeInt(gid);
            oPacket.EncodeByte(capacity);

            return oPacket;
        }

        public static OutPacket setMemberCommitment(int gid, int cid, int c, int igp) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.GuildResult.getValue());
            oPacket.EncodeByte(GuildResult.SetMemberCommitment_Done);
            oPacket.EncodeInt(gid);
            oPacket.EncodeInt(cid);//GuildMemberId (charid)
            oPacket.EncodeInt(c);//nCommitment
            oPacket.EncodeInt(c);//nDayCommitMent (based on chardata, stored in questData)
            oPacket.EncodeInt(igp);//nIGP
            oPacket.EncodeLong(PacketHelper.getTime(System.currentTimeMillis()));

            return oPacket;
        }

        public static OutPacket setIGP(int gid, int cid, int igp) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.GuildResult.getValue());
            oPacket.EncodeByte(GuildResult.SetIGP_Done);
            oPacket.EncodeInt(gid);
            oPacket.EncodeInt(igp);//nIGP
            oPacket.EncodeInt(cid);//GuildMemberId (charid)

            return oPacket;
        }

        public static OutPacket setGP(int gid, int gp) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.GuildResult.getValue());
            oPacket.EncodeByte(GuildResult.SetGGP_Done);
            oPacket.EncodeInt(gid);
            oPacket.EncodeInt(gp);//nIGP

            return oPacket;
        }

        public static OutPacket setMemberGrade(MapleGuildCharacter mgc) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.GuildResult.getValue());
            oPacket.EncodeByte(GuildResult.SetMemberGrade_Done);//rank
            oPacket.EncodeInt(mgc.getGuildId());
            oPacket.EncodeInt(mgc.getId());
            oPacket.EncodeByte(mgc.getGuildRank());

            return oPacket;
        }

        public static OutPacket setGradeName(int gid, String[] ranks) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.GuildResult.getValue());
            oPacket.EncodeByte(GuildResult.SetGradeName_Done);
            oPacket.EncodeInt(gid);
            for (String r : ranks) {
                oPacket.EncodeString(r);
            }

            return oPacket;
        }

        public static OutPacket setMark(int gid, short bg, byte bgcolor, short logo, byte logocolor) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.GuildResult.getValue());
            oPacket.EncodeByte(GuildResult.SetMark_Done);
            oPacket.EncodeInt(gid);
            oPacket.EncodeShort(bg);
            oPacket.EncodeByte(bgcolor);
            oPacket.EncodeShort(logo);
            oPacket.EncodeByte(logocolor);

            return oPacket;
        }

        public static OutPacket increaseGuildPoint(int gid, int GP, int glevel) {

            //something here's wrong in my order. :/
            OutPacket oPacket = new OutPacket(SendPacketOpcode.GuildResult.getValue());
            oPacket.EncodeByte(GuildResult.IncPoint_Done);
            oPacket.EncodeInt(gid);
            oPacket.EncodeInt(GP);
            oPacket.EncodeInt(glevel);
            oPacket.EncodeInt(glevel);//somethingelse

            return oPacket;
        }

        public static OutPacket setNotice(int gid, String notice) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.GuildResult.getValue());
            oPacket.EncodeByte(GuildResult.SetNotice_Done);
            oPacket.EncodeInt(gid);
            oPacket.EncodeString(notice);

            return oPacket;
        }

        public static OutPacket guildMemberLevelJobUpdate(MapleGuildCharacter mgc) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.GuildResult.getValue());
            oPacket.EncodeByte(GuildResult.ChangeLevelOrJob);
            oPacket.EncodeInt(mgc.getGuildId());
            oPacket.EncodeInt(mgc.getId());
            oPacket.EncodeInt(mgc.getLevel());
            oPacket.EncodeInt(mgc.getJobId());

            return oPacket;
        }

        public static OutPacket notifyLogin(int gid, int cid, boolean bOnline) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.GuildResult.getValue());
            oPacket.EncodeByte(GuildResult.NotifyLoginOrLogout);
            oPacket.EncodeInt(gid);
            oPacket.EncodeInt(cid);
            oPacket.EncodeByte(bOnline ? 1 : 0);
            oPacket.EncodeByte(bOnline ? 1 : 0);//some extra bool, dunno what it does.

            return oPacket;
        }

        public static OutPacket showGuildRanking(int npcid, List<MapleGuildRanking.GuildRankingInfo> all) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.GuildResult.getValue());
            oPacket.EncodeByte(GuildResult.ShowGuildRanking);
            oPacket.EncodeInt(npcid);
            oPacket.EncodeInt(all.size());
            for (MapleGuildRanking.GuildRankingInfo info : all) {
                oPacket.EncodeShort(0);//rank
                oPacket.EncodeString(info.getName());
                oPacket.EncodeInt(info.getGP());
                oPacket.EncodeInt(info.getLogo());
                oPacket.EncodeInt(info.getLogoColor());
                oPacket.EncodeInt(info.getLogoBg());
                oPacket.EncodeInt(info.getLogoBgColor());
            }

            return oPacket;
        }

        public static OutPacket setSkill(int gid, int sid, int level, long expiration, String purchase, String activate) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.GuildResult.getValue());
            oPacket.EncodeByte(GuildResult.SetSkill_Done);
            oPacket.EncodeInt(gid);
            oPacket.EncodeInt(sid);
            oPacket.EncodeShort(level);
            oPacket.EncodeLong(PacketHelper.getTime(expiration));
            oPacket.EncodeString(purchase);
            oPacket.EncodeString(activate);

            return oPacket;
        }

        public static OutPacket changeMaster(int gid, int oldLeader, int newLeader, int allianceId) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.GuildResult.getValue());
            oPacket.EncodeByte(GuildResult.ChangeMaster_Done);
            oPacket.EncodeInt(gid);
            oPacket.EncodeInt(oldLeader);
            oPacket.EncodeInt(newLeader);
            oPacket.EncodeByte(1);
            oPacket.EncodeInt(allianceId);

            return oPacket;
        }

        public static OutPacket rejectInvite(String charname) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.GuildResult.getValue());
            oPacket.EncodeByte(GuildResult.InviteGuild_Rejected);
            oPacket.EncodeString(charname);

            return oPacket;
        }

        public static OutPacket guildSearchResult(ClientSocket c, Map<Integer, MapleGuild> guilds) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.GuildSearchResult.getValue());
            oPacket.EncodeInt(guilds.size());
            for (Entry<Integer, MapleGuild> gs : guilds.entrySet()) {
                MapleGuild guild = gs.getValue();
                int avgLevel = gs.getKey();
                int guildMaster = guild.getLeaderId();
                User master = c.loadCharacterById(guildMaster);
                java.util.Collection<MapleGuildCharacter> members = guild.getMembers();
                int guildSize = members.size();

                oPacket.EncodeInt(guild.getId());
                oPacket.EncodeInt(guild.getLevel());
                oPacket.EncodeString(guild.getName());
                oPacket.EncodeString(master.getName());
                oPacket.EncodeInt(guildSize);
                oPacket.EncodeInt(avgLevel);
            }
            return oPacket;
        }

        public static OutPacket joinGuildRequest(MapleGuild guild, User chr, int operation) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.GuildResult.getValue());
            switch (operation) {
                case GuildResult.JoinRequest_Done://is sent to the owner of the guild you request to join, the info is based on the joining char!
                    oPacket.EncodeByte(GuildResult.JoinRequest_Done);
                    oPacket.EncodeInt(guild.getId());//maybe swap these two
                    oPacket.EncodeInt(chr.getId());
                    guildMemberEncode(oPacket, new MapleGuildCharacter(chr));
                    break;
                case GuildResult.JoinRequest_DoneToUser:
                    oPacket.EncodeByte(GuildResult.JoinRequest_DoneToUser);
                    break;
                case GuildResult.JoinRequest_AlreadyFull:
                    oPacket.EncodeByte(GuildResult.JoinRequest_AlreadyFull);
                    break;
                case GuildResult.JoinRequest_LimitTime:
                    oPacket.EncodeByte(GuildResult.JoinRequest_LimitTime);
                    break;
                case GuildResult.JoinCancelRequest_Done://sent when you cancel your join-guild-request
                    oPacket.EncodeByte(GuildResult.JoinCancelRequest_Done);
                    oPacket.EncodeInt(1246849);
                    break;
                //48 = restriction period msg
                default:
                    oPacket.EncodeByte(GuildResult.JoinRequest_Unknown);
                    break;
            }

            return oPacket;
        }

        public static OutPacket updateJoinRequestClientInfo(String guildname) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.Message.getValue());
            oPacket.EncodeByte(0x0D);//UpdateQuestInfoEx
            oPacket.EncodeInt(26015);//qId
            String formattedName = "";
            if (!guildname.isEmpty()) {
                formattedName = "name=" + guildname;
            }
            oPacket.EncodeString(formattedName.isEmpty() ? guildname : formattedName);
            if (guildname.isEmpty()) {
                oPacket.EncodeByte(0);
            }

            return oPacket;
        }

        public static OutPacket genericGuildMessage(byte code) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.GuildResult.getValue());
            oPacket.EncodeByte(code);//30 = cant find in ch
            if (code == 87) {
                oPacket.EncodeInt(0);
            }
            if ((code == 59) || (code == 60) || (code == 61) || (code == 84) || (code == 87)) {
                oPacket.EncodeString("");
            }

            oPacket.Fill(0, 29);
            
            return oPacket;
        }

        //idk
        public static OutPacket BBSThreadList(List<MapleBBSThread> bbs, int start) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.GuildBBSResult.getValue());
            oPacket.EncodeByte(6);
            if (bbs == null) {
                oPacket.EncodeByte(0);
                oPacket.EncodeLong(0L);
                return oPacket;
            }
            int threadCount = bbs.size();
            MapleBBSThread notice = null;
            for (MapleBBSThread b : bbs) {
                if (b.isNotice()) {
                    notice = b;
                    break;
                }
            }
            oPacket.EncodeByte(notice == null ? 0 : 1);
            if (notice != null) {
                addThread(oPacket, notice);
            }
            if (threadCount < start) {
                start = 0;
            }
            oPacket.EncodeInt(threadCount);
            int pages = Math.min(10, threadCount - start);
            oPacket.EncodeInt(pages);
            for (int i = 0; i < pages; i++) {
                addThread(oPacket, (MapleBBSThread) bbs.get(start + i));
            }

            return oPacket;
        }

        private static void addThread(OutPacket oPacket, MapleBBSThread rs) {
            oPacket.EncodeInt(rs.localthreadID);
            oPacket.EncodeInt(rs.ownerID);
            oPacket.EncodeString(rs.name);
            oPacket.EncodeLong(PacketHelper.getKoreanTimestamp(rs.timestamp));
            oPacket.EncodeInt(rs.icon);
            oPacket.EncodeInt(rs.getReplyCount());
        }

        public static OutPacket showThread(MapleBBSThread thread) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.GuildBBSResult.getValue());
            oPacket.EncodeByte(7);
            oPacket.EncodeInt(thread.localthreadID);
            oPacket.EncodeInt(thread.ownerID);
            oPacket.EncodeLong(PacketHelper.getKoreanTimestamp(thread.timestamp));
            oPacket.EncodeString(thread.name);
            oPacket.EncodeString(thread.text);
            oPacket.EncodeInt(thread.icon);
            oPacket.EncodeInt(thread.getReplyCount());
            for (MapleBBSThread.MapleBBSReply reply : thread.replies.values()) {
                oPacket.EncodeInt(reply.replyid);
                oPacket.EncodeInt(reply.ownerID);
                oPacket.EncodeLong(PacketHelper.getKoreanTimestamp(reply.timestamp));
                oPacket.EncodeString(reply.content);
            }

            return oPacket;
        }

        public static OutPacket sendSetGuildNameMsg(User chr) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.UserGuildNameChanged.getValue());
            oPacket.EncodeInt(chr.getId());
            if (chr.getGuildId() <= 0) {
                oPacket.EncodeShort(0);
            } else {
                MapleGuild gs = World.Guild.getGuild(chr.getGuildId());
                if (gs != null) {
                    oPacket.EncodeString(gs.getName());
                } else {
                    oPacket.EncodeShort(0);
                }
            }

            return oPacket;
        }

        public static OutPacket sendSetGuildMarkMsg(User chr) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.UserGuildMarkChanged.getValue());
            oPacket.EncodeInt(chr.getId());
            if (chr.getGuildId() <= 0) {
                oPacket.Fill(0, 6);
            } else {
                MapleGuild gs = World.Guild.getGuild(chr.getGuildId());
                if (gs != null) {
                    oPacket.EncodeShort(gs.getLogoBG());
                    oPacket.EncodeByte(gs.getLogoBGColor());
                    oPacket.EncodeShort(gs.getLogo());
                    oPacket.EncodeByte(gs.getLogoColor());
                } else {
                    oPacket.Fill(0, 6);
                }
            }

            return oPacket;
        }
    }

    public static class InfoPacket {

        public static OutPacket showMesoGain(int gain, boolean inChat) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.Message.getValue());
            if (!inChat) {
                DropPickUpMessage pMessage = new DropPickUpMessage(1, 0, gain);
                pMessage.messagePacket(oPacket);
            } else {
                oPacket.EncodeByte(7);
                oPacket.EncodeLong(gain);
                oPacket.EncodeInt(-1);
            }

            return oPacket;
        }

        public static OutPacket getShowInventoryStatus(int mode) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.Message.getValue());
            oPacket.EncodeByte(0);
            oPacket.EncodeByte(mode);
            oPacket.EncodeInt(0);
            oPacket.EncodeInt(0);

            return oPacket;
        }

        public static OutPacket getShowItemGain(int itemId, short quantity) {
            return getShowItemGain(itemId, quantity, false);
        }

        public static OutPacket getShowItemGain(int itemId, short quantity, boolean inChat) {

            if (inChat) {
                OutPacket oPacket = new OutPacket(SendPacketOpcode.UserEffectRemote.getValue());
                oPacket.EncodeByte(UserEffectCodes.Quest.getEffectId());
                oPacket.EncodeByte(1);
                oPacket.EncodeInt(itemId);
                oPacket.EncodeInt(quantity);

                return oPacket;
            } else {
                DropPickUpMessage pMessage = new DropPickUpMessage(0, itemId, quantity);
                OutPacket oPacket = new OutPacket(SendPacketOpcode.Message.getValue());
                pMessage.messagePacket(oPacket);

                return oPacket;
            }
        }

        /**
         * Shows the name and image of the equipment dropped by the monster which the player have picked up at the center-top of the screen
         *
         * @param item
         * @return
         */
        public static OutPacket showEquipmentPickedUp(Equip item) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.UserEffectRemote.getValue());
            oPacket.EncodeByte(UserEffectCodes.PickUpItem.getEffectId());
            PacketHelper.addItemInfo(oPacket, item);

            return oPacket;
        }

        public static OutPacket updateQuestMobKills(QuestStatus status) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.Message.getValue());
            oPacket.EncodeByte(1);
            oPacket.EncodeInt(status.getQuest().getId()); // Version 174, this is an integer 
            oPacket.EncodeByte(1);
            StringBuilder sb = new StringBuilder();
            for (Iterator<?> i$ = status.getMobKills().values().iterator(); i$.hasNext();) {
                int kills = (Integer) i$.next();
                sb.append(StringUtil.getLeftPaddedStr(String.valueOf(kills), '0', 3));
            }
            oPacket.EncodeString(sb.toString());
            oPacket.EncodeLong(0L);

            return oPacket;
        }

        /**
         * Shows EXP gain from monster
         *
         * @param nIncExp
         * @param bIsLastHit
         * @param bOnQuest
         * @param questBonusEXPRate
         * @param expIncreaseStats
         * @return
         */
        public static OutPacket gainExp(int nIncExp, boolean bIsLastHit, boolean bOnQuest, byte questBonusEXPRate, int burningFieldBonusEXPRate, EnumMap<ExpType, Integer> expIncreaseStats) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.Message.getValue());
            ExpIncreaseMessage message = new ExpIncreaseMessage(bIsLastHit, nIncExp, bOnQuest, questBonusEXPRate, burningFieldBonusEXPRate, expIncreaseStats);
            message.messagePacket(oPacket);

            return oPacket;
        }
    }

    public static OutPacket updateSpecialStat(MapleSpecialStatUpdateType stat, int requestType, int requestValue, int amount) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.ResultInstanceTable.getValue());
        oPacket.EncodeString(stat.getString());
        oPacket.EncodeInt(requestType);
        oPacket.EncodeInt(requestValue);
        oPacket.EncodeByte(1);
        oPacket.EncodeInt(amount);

        return oPacket;
    }

    public static OutPacket updateMaplePoint(int mp) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.SetMaplePoint.getValue());
        oPacket.EncodeInt(mp);

        return oPacket;
    }

    public static OutPacket updateCrowns(int[] titles) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.EventNameTagInfo.getValue());
        for (int i = 0; i < 5; i++) {
            oPacket.EncodeString("");
            if (titles.length < i + 1) {
                oPacket.EncodeByte(-1);
            } else {
                oPacket.EncodeByte(titles[i]);
            }
        }

        return oPacket;
    }

    public static OutPacket magicWheel(int type, List<Integer> items, String data, int endSlot) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.MagicWheelResult.getValue());
        oPacket.EncodeByte(type);
        switch (type) {
            case 3:
                oPacket.EncodeByte(items.size());
                for (int item : items) {
                    oPacket.EncodeInt(item);
                }
                oPacket.EncodeString(data); // nexon encrypt the item and then send the string
                oPacket.EncodeByte(endSlot);
                break;
            case 5:
                //<Character Name> got <Item Name>.
                break;
            case 6:
                //You don't have a Magic Gachapon Wheel in your Inventory.
                break;
            case 7:
                //You don't have any Inventory Space.\r\n You must have 2 or more slots available\r\n in each of your tabs.
                break;
            case 8:
                //Please try this again later.
                break;
            case 9:
                //Failed to delete Magic Gachapon Wheel item.
                break;
            case 0xA:
                //Failed to receive Magic Gachapon Wheel item.
                break;
            case 0xB:
                //You cannot move while Magic Wheel window is open.
                break;
        }

        return oPacket;

    }

    public static class Reward {

        public static OutPacket receiveReward(int id, byte mode, int quantity) {
            System.out.println("[Debug] REWARD MODE: " + mode);

            OutPacket oPacket = new OutPacket(SendPacketOpcode.RewardResult.getValue());
            oPacket.EncodeByte(mode); // mode
            switch (mode) { // mode
                case 9:
                    oPacket.EncodeInt(0);
                    break;
                case 0x0B:
                    oPacket.EncodeInt(id);
                    oPacket.EncodeInt(quantity); //quantity
                    //Popup: You have received the Maple Points.\r\n( %d maple point )
                    break;
                case 0x0C:
                    oPacket.EncodeInt(id);
                    //Popup You have received the Game item.
                    break;
                case 0x0E:
                    oPacket.EncodeInt(id);
                    oPacket.EncodeInt(quantity); //quantity
                    //Popup: You have received the Mesos.\r\n( %d meso )
                    break;
                case 0x0F:
                    oPacket.EncodeInt(id);
                    oPacket.EncodeInt(quantity); //quantity
                    //Popup: You have received the Exp.\r\n( %d exp )
                    break;
                case 0x14:
                    //Popup: Failed to receive the Maple Points.
                    break;
                case 0x15:
                    oPacket.EncodeByte(0);
                    //Popup: Failed to receive the Game Item.
                    break;
                case 0x16:
                    oPacket.EncodeByte(0);
                    //Popup: Failed to receive the Game Item.
                    break;
                case 0x17:
                    //Popup: Failed to receive the Mesos.
                    break;
                case 0x18:
                    //Popup: Failed to receive the Exp.
                    break;
                case 0x21:
                    oPacket.EncodeByte(0); //66
                    //No inventory space
                    break;
            }

            return oPacket;
        }

        public static OutPacket updateReward(int id, byte mode, List<Rewards> rewards, int option) {

            OutPacket oPacket = new OutPacket(SendPacketOpcode.RewardResult.getValue());
            oPacket.EncodeByte(mode); // mode
            switch (mode) { // mode
                case 9:
                    oPacket.EncodeInt(rewards.size());
                    if (rewards.size() > 0) {
                        for (int i = 0; i < rewards.size(); i++) {
                            Rewards reward = rewards.get(i);
                            boolean empty = reward.getId() < 1;
                            oPacket.EncodeInt(empty ? 0 : reward.getId()); // 0 = blank 1+ = gift
                            if (!empty) {
                                if ((option & 1) != 0) {
                                    oPacket.EncodeLong(reward.getReceiveDate()); //start time
                                    oPacket.EncodeLong(reward.getExpireDate()); //end time
                                    oPacket.EncodeLong(reward.getReceiveDate()); //start time
                                    oPacket.EncodeLong(reward.getExpireDate()); //end time
                                }
                                if ((option & 2) != 0) { //nexon do here a3 & 2 when a3 is 9
                                    oPacket.EncodeInt(0);
                                    oPacket.EncodeInt(0);
                                    oPacket.EncodeInt(0);
                                    oPacket.EncodeInt(0);
                                    oPacket.EncodeInt(0);
                                    oPacket.EncodeInt(0);
                                    oPacket.EncodeString("");
                                    oPacket.EncodeString("");
                                    oPacket.EncodeString("");
                                }
                                oPacket.EncodeInt(reward.getType()); //type 3 = maple point 4 = mesos 5 = exp
                                oPacket.EncodeInt(reward.getItem()); // item id
                                oPacket.EncodeInt(/*itemQ*/reward.getItem() > 0 ? 1 : 0); // item quantity (?)
                                oPacket.EncodeInt(0);
                                oPacket.EncodeLong(0L);
                                oPacket.EncodeInt(0);
                                oPacket.EncodeInt(reward.getMaplePoints()); // maple point amount
                                oPacket.EncodeInt(reward.getMeso()); // mesos amount
                                oPacket.EncodeInt(reward.getExp()); // exp amount
                                oPacket.EncodeInt(0);
                                oPacket.EncodeInt(0);
                                oPacket.EncodeString("");
                                oPacket.EncodeString("");
                                oPacket.EncodeString("");
                                oPacket.EncodeString(reward.getDesc());
                            }
                        }
                    }
                    break;
                case 0x0B:
                    oPacket.EncodeInt(id);
                    oPacket.EncodeInt(0); //quantity
                    //Popup: You have received the Maple Points.\r\n( %d maple point )
                    break;
                case 0x0C:
                    oPacket.EncodeInt(id);
                    //Popup You have received the Game item.
                    break;
                case 0x0E:
                    oPacket.EncodeInt(id);
                    oPacket.EncodeInt(0); //quantity
                    //Popup: You have received the Mesos.\r\n( %d meso )
                    break;
                case 0x0F:
                    oPacket.EncodeInt(id);
                    oPacket.EncodeInt(0); //quantity
                    //Popup: You have received the Exp.\r\n( %d exp )
                    break;
                case 0x14:
                    //Popup: Failed to receive the Maple Points.
                    break;
                case 0x15:
                    oPacket.EncodeByte(0);
                    //Popup: Failed to receive the Game Item.
                    break;
                case 0x16:
                    oPacket.EncodeByte(0);
                    //Popup: Failed to receive the Game Item.
                    break;
                case 0x17:
                    //Popup: Failed to receive the Mesos.
                    break;
                case 0x18:
                    //Popup: Failed to receive the Exp.
                    break;
                case 0x21:
                    oPacket.EncodeByte(0); //66
                    //No inventory space
                    break;
            }

            return oPacket;
        }
    }

    /**
     * <<<<<<< Updated upstream This packet is the universal packet that handles enchantment system each case does something different
     * within the system itself.
     *
     * @param enchant - The entire enchantment helper object type: 50 - Shows the scroll list
     * (CEquipmentEnchantTabScroll::_OnScrollUpgradeDisplay) 51 - unknown 52 - The enhancement stuff
     * (CEquipmentEnchantTabHyper::_OnHyperUpgradeDisplay) 53 - (CEquipmentEnchantTabHyper::_OnMiniGameDisplay) 100 - Shows the result of an
     * scroll upgrade(CUIEquipmentEnchant::_ShowScrollUpgradeResult) 101 - Shows the result of the an enhance upgrade
     * (CUIEquipmentEnchant::_ShowHyperUpgradeResult) 102 - Shows the result if there is a compensation for the item
     * (CUIEquipmentEnchant::_ShowScrollVestigeCompensationResult) 103 - Shows the result of the transfer hammer
     * (CUIEquipmentEnchant::_ShowTransmissionResult) 104 - Shows the result of an unkown failure
     * (CUIEquipmentEnchant::_ShowUnknownFailResult)
     */
    public static OutPacket enchantmentSystem(Enchant enchant) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.EquipmentEnchantDisplay.getValue());

        //final List<EquipSpecialStat> eqSpecialStats = EquipHelper.calculateEquipSpecialStatsForEncoding(enchant.getOldEquip());
        oPacket.EncodeByte(enchant.getAction().getValue());
        switch (enchant.getAction()) {
            case SCROLLLIST:
                oPacket.EncodeBool(ServerConstants.FEVER_TIME);
                oPacket.EncodeByte(enchant.getScrolls().size());
                for (EnchantmentScroll scroll : enchant.getScrolls()) {
                    oPacket.EncodeInt(scroll.getType());
                    oPacket.EncodeString(scroll.getName());
                    oPacket.EncodeInt(0);
                    oPacket.EncodeInt(0);
                    oPacket.EncodeInt(scroll.getMask());
                    for (Entry<EnchantmentStats, Integer> stats : scroll.getStats().entrySet()) {
                        oPacket.EncodeInt(stats.getValue());
                    }
                    oPacket.EncodeInt(scroll.getCost());
                    oPacket.EncodeInt(scroll.getCost());
                    oPacket.EncodeBool(scroll.willPass());
                }
                break;
            case FEVER_TIME:
                oPacket.EncodeBool(enchant.isFeverTime()); //TSingleton<CWvsContext>::ms_pInstance.m_Data[2621].m_str if it != 0 (Fever Time)
                break;
            case STARFORCE:
                //oPacket.Encode(0);
                oPacket.EncodeBool(enchant.canDowngrade()); //m_bDowngradable
                oPacket.EncodeLong(enchant.getCost()); //m_nMeso
                //oPacket.EncodeInt(enchant.getPerMille());//nPermille
                //oPacket.EncodeInt(enchant.getDestroyChance());//v2->m_bDestroyable
                //oPacket.Encode(enchant.isChanceTime()); //v2->m_bChanceTime
                oPacket.EncodeInt(0);
                oPacket.EncodeInt(0);
                oPacket.EncodeInt(0);
                oPacket.EncodeInt(0);
                oPacket.EncodeBool(false); // bMVPDiscount
                oPacket.EncodeBool(false); // bPC
                oPacket.EncodeInt(0); // nSuccessRate * 10
                oPacket.EncodeInt(0); // nDestroyRate: (100 - nSuccessRate) * 10 [Percentage of Fail Rate * 10]
                oPacket.EncodeInt(0); // nOriginalSuccessRate * 10 (Special Success Rate: Strikethrough original rate)
                oPacket.EncodeInt(0);
                oPacket.EncodeBool(false); // bChanceTime
                oPacket.EncodeInt(enchant.getMask()); //mask
                for (Entry<EnchantmentStats, Short> star : enchant.getStarForce().entrySet()) {
                    oPacket.EncodeInt(star.getValue());
                }
                break;
            case MINI_GAME:
                oPacket.EncodeByte(enchant.getLevel()); //nLevel
                oPacket.EncodeInt(enchant.getSeed()); //v2->m_nSeed
                break;
            case SCROLL_RESULT:
                oPacket.EncodeBool(enchant.isFeverTime()); //bIsEffectOn (Fever Time)
                oPacket.EncodeInt(enchant.hasPassed()); //pass
                oPacket.EncodeShort(0);//String desc?
                PacketHelper.addItemInfo(oPacket, enchant.getOldEquip());
                PacketHelper.addItemInfo(oPacket, enchant.getNewEquip());
                break;
            case STARFORCE_RESULT:
                oPacket.EncodeInt(enchant.hasPassed());
                oPacket.EncodeByte(0);
                PacketHelper.addItemInfo(oPacket, enchant.getOldEquip());
                PacketHelper.addItemInfo(oPacket, enchant.getNewEquip());
                break;
            case VESTIGE_COMPENSATION_RESULT:
                oPacket.EncodeInt(0);
                break;
            case TRANSFER_HAMMER_RESULT:
                PacketHelper.addItemInfo(oPacket, enchant.getOldEquip());
                PacketHelper.addItemInfo(oPacket, enchant.getNewEquip());
                break;
            case UNKOWN_FAILURE_RESULT:
                oPacket.EncodeByte(0);
                break;
            default:
                break;
        }

        oPacket.Fill(0, 19);

        return oPacket;
    }

    public static OutPacket OnVMatrixUpdate(List<VMatrixRecord> aVMatrixRecord, boolean bUpdate, int nUpdateType, int nPOS) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.VMatrixUpdate.getValue());
        oPacket.EncodeInt(aVMatrixRecord.size());
        for (VMatrixRecord pMatrix : aVMatrixRecord) {
            pMatrix.Encode(oPacket);
        }
        oPacket.EncodeBool(bUpdate);
        if (bUpdate) {
            oPacket.EncodeInt(nUpdateType);
            if (nUpdateType == VMatrixRecord.Enable || nUpdateType == VMatrixRecord.Disable) {
                oPacket.EncodeInt(nPOS);
            }
        }
        return oPacket;
    }

    public static OutPacket OnNodeStoneResult(int nCoreID, int nSkillID1, int nSkillID2, int nSkillID3) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.NodeStoneResult.getValue());
        oPacket.EncodeInt(nCoreID);
        oPacket.EncodeInt(1);
        oPacket.EncodeInt(nSkillID1);
        oPacket.EncodeInt(nSkillID2);
        oPacket.EncodeInt(nSkillID3);
        return oPacket;
    }

    public static OutPacket OnNodeEnhanceResult(int nRecordID, int nEXP, int nSLV1, int nSLV2) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.NodeEnhanceResult.getValue());
        oPacket.EncodeInt(nRecordID);
        oPacket.EncodeInt(nEXP);
        oPacket.EncodeInt(nSLV1);
        oPacket.EncodeInt(nSLV2);
        return oPacket;
    }

    public static OutPacket OnNodeCraftResult(int nCoreID, int nSkillID1, int nSkillID2, int nSkillID3) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.NodeCraftResult.getValue());
        oPacket.EncodeInt(nCoreID);
        oPacket.EncodeInt(1);
        oPacket.EncodeInt(nSkillID1);
        oPacket.EncodeInt(nSkillID2);
        oPacket.EncodeInt(nSkillID3);
        return oPacket;
    }

    public static OutPacket OnNodeShardResult(int nAmount) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.NodeShardResult.getValue());
        oPacket.EncodeInt(nAmount);
        return oPacket;
    }

    public static OutPacket OnResultStealSkillList(int bOnExclRequest, int dwStealTargetCID, int nPhantomStealResult, int nTargetJob, List<Integer> aSkill) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.ResultStealSkillList.getValue());

        oPacket.EncodeByte(bOnExclRequest);
        oPacket.EncodeInt(dwStealTargetCID);
        oPacket.EncodeInt(nPhantomStealResult);
        if (nPhantomStealResult == 4) {
            oPacket.EncodeInt(nTargetJob);
            oPacket.EncodeInt(aSkill.size());
            for (int nSkillID : aSkill) {
                oPacket.EncodeInt(nSkillID);
            }
        }
        return oPacket;
    }

    public static OutPacket OnChangeStealMemoryResult(int bOnExclRequest, int nResult, int nSlotID, int nPOS, int nStealSkillID, int nStealSLV, int nStealSMLV) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.ChangeStealMemoryResult.getValue());

        oPacket.EncodeByte(bOnExclRequest);
        oPacket.EncodeByte(nResult);
        switch (nResult) {
            case 0: // Steal
                oPacket.EncodeInt(nSlotID);
                oPacket.EncodeInt(nPOS);
                oPacket.EncodeInt(nStealSkillID);
                oPacket.EncodeInt(nStealSLV);
                oPacket.EncodeInt(nStealSMLV);
                break;
            case 1: // NoTarget
            case 2: // Unknown
                break;
            case 3: // Remove
                oPacket.EncodeInt(nSlotID);
                oPacket.EncodeInt(nPOS);
                break;
            case 4: // RemoveSlotAll
                oPacket.EncodeInt(nSlotID);
                break;
            case 5: // RemoveAll
                break;
        }
        return oPacket;
    }

    public static OutPacket OnResultSetStealSkill(int bOnExclRequest, boolean bSet, int nSlotSkillID, int nStealSkillID) {

        OutPacket oPacket = new OutPacket(SendPacketOpcode.ResultSetStealSkill.getValue());

        oPacket.EncodeByte(bOnExclRequest);
        oPacket.EncodeBool(bSet);
        oPacket.EncodeInt(nSlotSkillID);
        if (bSet) {
            oPacket.EncodeInt(nStealSkillID);
        }
        return oPacket;
    }
    
    public static OutPacket OnIssueReloginCookie(String sUsername) {
        OutPacket oPacket = new OutPacket(SendPacketOpcode.IssueReloginCookie.getValue());
        oPacket.EncodeString("sUsername");
        return oPacket;
    }
}
