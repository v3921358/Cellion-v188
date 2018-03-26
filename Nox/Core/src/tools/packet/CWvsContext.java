package tools.packet;

import client.*;
import client.MapleSpecialStats.MapleSpecialStatUpdateType;
import client.MapleStat.Temp;
import client.buddy.Buddy;
import client.buddy.BuddylistEntry;
import client.inventory.*;
import constants.GameConstants;
import constants.ServerConstants;
import handling.world.*;
import handling.world.MapleGeneralRanking.CandyRankingInfo;
import net.OutPacket;
import net.Packet;
import server.MapleFamiliar;
import server.MapleItemInformationProvider;
import server.Randomizer;
import server.life.MapleMonster;
import server.life.PlayerNPC;
import server.maps.objects.MapleCharacter;
import server.maps.objects.MaplePet;
import server.maps.objects.MonsterFamiliar;
import server.messages.ExpGainTypes;
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
import server.skills.VMatrixRecord;

public class CWvsContext {

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
    public static Packet getInventoryStatus() {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.InventoryOperation.getValue());
        oPacket.Encode(0);
        oPacket.Encode(0);
        oPacket.Encode(0);

        return oPacket.ToPacket();
    }

    public static Packet updateInventorySlot(MapleInventoryType type, Item item, boolean fromDrop) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.InventoryOperation.getValue());
        oPacket.Encode(fromDrop ? 1 : 0);
        oPacket.Encode(1);
        oPacket.Encode(0);

        oPacket.Encode(GameConstants.isInBag(item.getPosition(), type.getType()) ? 6 : 1);
        oPacket.Encode(type.getType());
        oPacket.EncodeShort(item.getPosition());
        oPacket.EncodeShort(item.getQuantity());

        return oPacket.ToPacket();
    }

    public static Packet inventoryOperation(boolean enableActions, List<ModifyInventory> modifications) {
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
    public static Packet getInventoryFull() {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.InventoryOperation.getValue());
        oPacket.Encode(1);
        oPacket.Encode(0);
        oPacket.Encode(0);

        return oPacket.ToPacket();
    }

    public static Packet inventoryOperation(boolean enableActions, List<ModifyInventory> modifications, boolean reloadItem) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.InventoryOperation.getValue());
        oPacket.Encode(enableActions);
        oPacket.Encode(modifications.size());
        oPacket.Encode(reloadItem); //bNotRemoveAddInfo

        boolean bAddMovementInfo = false;
        for (ModifyInventory mod : modifications) {
            oPacket.Encode(mod.getMode().getOpcode());
            oPacket.Encode(mod.getInventoryType());
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
                    oPacket.EncodeInteger(0); //nBagPos
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
            oPacket.Encode(0);
        }
        //if (bAddMovementInfo) {
        oPacket.Encode(bAddMovementInfo);
        //}
        return oPacket.ToPacket();
    }

    public static Packet pendantExpansionAvailable(boolean available) { //slot -59
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.SetBuyEquipExt.getValue());
        oPacket.Encode(available ? 1 : 0);

        return oPacket.ToPacket();
    }

    public static Packet getSlotUpdate(byte invType, byte newSlots) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.InventoryGrow.getValue());
        oPacket.Encode(invType);
        oPacket.Encode(newSlots);
        return oPacket.ToPacket();
    }

    public static Packet messagePacket(MessageInterface message) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.Message.getValue());
        message.messagePacket(oPacket);

        return oPacket.ToPacket();
    }

    public static Packet enableActions() {
        return updatePlayerStats(new EnumMap<>(MapleStat.class), true, null);
    }

    public static Packet updatePlayerStats(Map<MapleStat, Long> stats, MapleCharacter chr) {
        return updatePlayerStats(stats, false, chr);
    }

    public static Packet updatePlayerStats(Map<MapleStat, Long> mystats, boolean itemReaction, MapleCharacter chr) {
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.StatChanged.getValue());
        oPacket.Encode(itemReaction);
        long flag = 0;
        if (mystats != null) {
            for (MapleStat statupdate : mystats.keySet()) {
                flag |= statupdate.getValue();
            }
        }
        oPacket.EncodeLong(flag);

        if ((flag & MapleStat.SKIN.getValue()) != 0) {
            oPacket.Encode(mystats.get(MapleStat.SKIN).byteValue());
        }
        if ((flag & MapleStat.FACE.getValue()) != 0) {
            oPacket.EncodeInteger(mystats.get(MapleStat.FACE).intValue());
        }
        if ((flag & MapleStat.HAIR.getValue()) != 0) {
            oPacket.EncodeInteger(mystats.get(MapleStat.HAIR).intValue());
        }
        if ((flag & MapleStat.LEVEL.getValue()) != 0) {
            oPacket.Encode(mystats.get(MapleStat.LEVEL).byteValue());
        }
        if ((flag & MapleStat.JOB.getValue()) != 0) {
            oPacket.EncodeShort(mystats.get(MapleStat.JOB).shortValue());
            oPacket.EncodeShort(chr.getSubcategory());
        }
        if ((flag & MapleStat.STR.getValue()) != 0) {
            oPacket.EncodeShort(mystats.get(MapleStat.STR).shortValue());
        }
        if ((flag & MapleStat.DEX.getValue()) != 0) {
            oPacket.EncodeShort(mystats.get(MapleStat.DEX).shortValue());
        }
        if ((flag & MapleStat.INT.getValue()) != 0) {
            oPacket.EncodeShort(mystats.get(MapleStat.INT).shortValue());
        }
        if ((flag & MapleStat.LUK.getValue()) != 0) {
            oPacket.EncodeShort(mystats.get(MapleStat.LUK).shortValue());
        }
        if ((flag & MapleStat.HP.getValue()) != 0) {
            oPacket.EncodeInteger(mystats.get(MapleStat.HP).intValue());
        }
        if ((flag & MapleStat.MAXHP.getValue()) != 0) {
            oPacket.EncodeInteger(mystats.get(MapleStat.MAXHP).intValue());
        }
        if ((flag & MapleStat.MP.getValue()) != 0) {
            oPacket.EncodeInteger(mystats.get(MapleStat.MP).intValue());
        }
        if ((flag & MapleStat.IndieMMP.getValue()) != 0) {
            oPacket.EncodeInteger(mystats.get(MapleStat.IndieMMP).intValue());
        }
        if ((flag & MapleStat.AVAILABLEAP.getValue()) != 0) {
            oPacket.EncodeShort(mystats.get(MapleStat.AVAILABLEAP).shortValue());
        }
        if ((flag & MapleStat.AVAILABLESP.getValue()) != 0) {
            if (GameConstants.isExtendedSpJob(chr.getJob())) {
                oPacket.Encode(chr.getRemainingSpSize());
                for (int i = 0; i < chr.getRemainingSps().length; i++) {
                    if (chr.getRemainingSp(i) > 0) {
                        oPacket.Encode(i + 1);
                        oPacket.EncodeInteger(chr.getRemainingSp(i));
                    }
                }
            } else {
                oPacket.EncodeShort(chr.getRemainingSp());
            }
        }
        if ((flag & MapleStat.EXP.getValue()) != 0) {
            oPacket.EncodeLong(mystats.get(MapleStat.EXP));
        }
        if ((flag & MapleStat.FAME.getValue()) != 0) {
            oPacket.EncodeInteger(mystats.get(MapleStat.FAME).intValue());
        }
        if ((flag & MapleStat.MESO.getValue()) != 0) {
            oPacket.EncodeLong(mystats.get(MapleStat.MESO));
        }
        if ((flag & MapleStat.FATIGUE.getValue()) != 0) {
            oPacket.Encode(mystats.get(MapleStat.FATIGUE).byteValue());
        }
        if ((flag & MapleStat.CHARISMA.getValue()) != 0) {
            oPacket.EncodeInteger(mystats.get(MapleStat.CHARISMA).intValue());
        }
        if ((flag & MapleStat.INSIGHT.getValue()) != 0) {
            oPacket.EncodeInteger(mystats.get(MapleStat.INSIGHT).intValue());
        }
        if ((flag & MapleStat.WILL.getValue()) != 0) {
            oPacket.EncodeInteger(mystats.get(MapleStat.WILL).intValue());
        }
        if ((flag & MapleStat.CRAFT.getValue()) != 0) {
            oPacket.EncodeInteger(mystats.get(MapleStat.CRAFT).intValue());
        }
        if ((flag & MapleStat.SENSE.getValue()) != 0) {
            oPacket.EncodeInteger(mystats.get(MapleStat.SENSE).intValue());
        }
        if ((flag & MapleStat.CHARM.getValue()) != 0) {
            oPacket.EncodeInteger(mystats.get(MapleStat.CHARM).intValue());
        }
        if ((flag & MapleStat.TRAIT_LIMIT.getValue()) != 0) {
            oPacket.EncodeShort(mystats.get(MapleStat.CHARISMA).shortValue());
            oPacket.EncodeShort(mystats.get(MapleStat.INSIGHT).shortValue());
            oPacket.EncodeShort(mystats.get(MapleStat.WILL).shortValue());
            oPacket.EncodeShort(mystats.get(MapleStat.CRAFT).shortValue());
            oPacket.EncodeShort(mystats.get(MapleStat.SENSE).shortValue());
            oPacket.EncodeShort(mystats.get(MapleStat.CHARM).shortValue());
            oPacket.Encode(0);
            oPacket.EncodeLong(PacketHelper.getTime(-2));
        }
        if ((flag & MapleStat.ALBA_ACTIVITY.getValue()) != 0) {
            oPacket.Encode(0);//AlbaActivityID
            oPacket.EncodeInteger(0);//AlbaStartTime.dwHighDateTime
            oPacket.EncodeInteger(0);//AlbaStartTime.dwLowDateTime
            oPacket.EncodeInteger(0);//AlbaDuration
            oPacket.Encode(0);//AlbaSpecialReward
        }
        if ((flag & MapleStat.CHARACTER_CARD_UPDATE.getValue()) != 0) {
            chr.getCharacterCard().connectData(oPacket);
        }
        if ((flag & MapleStat.BATTLE_EXP.getValue()) != 0) {
            oPacket.EncodeInteger(0); //pvp exp
            oPacket.Encode(0);// grade
            oPacket.EncodeInteger(0); //point
        }
        if ((flag & MapleStat.BATTLE_RANK.getValue()) != 0) {
            oPacket.Encode(0); //nPvPModeLevel
            oPacket.Encode(0); //nPvPModeType
        }
        if ((flag & MapleStat.BATTLE_POINTS.getValue()) != 0) {
            oPacket.EncodeInteger(mystats.get(MapleStat.BATTLE_POINTS).intValue()); //EventPoint
        }
        oPacket.Encode(-1);//nMixBaseHairColor
        oPacket.Encode(0);//nMixAddHairColor
        oPacket.Encode(0);//nMixHairBaseProb
        if (flag == 0 && !itemReaction) {
            oPacket.Encode(1);
        }
        oPacket.Encode(0);
        /*
         if ( CInPacket::Decode1(iPacket) )
		  {
		    LOBYTE(aLevelQuest.a) = CInPacket::Decode1(iPacket);
		    if ( TSingleton<CUserLocal>::ms_pInstance._m_pStr )
		      CUserLocal::SetSecondaryStatChangedPoint(TSingleton<CUserLocal>::ms_pInstance._m_pStr, aLevelQuest.a);
		  }
         * */
        oPacket.Encode(0);
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
        return oPacket.ToPacket();
    }

    public static Packet setTemporaryStats(short str, short dex, short _int, short luk, short watk, short matk, short acc, short avoid, short speed, short jump) {
        Map<Temp, Integer> stats = new EnumMap<>(MapleStat.Temp.class);

        stats.put(MapleStat.Temp.STR, Integer.valueOf(str));
        stats.put(MapleStat.Temp.DEX, Integer.valueOf(dex));
        stats.put(MapleStat.Temp.INT, Integer.valueOf(_int));
        stats.put(MapleStat.Temp.LUK, Integer.valueOf(luk));
        stats.put(MapleStat.Temp.WATK, Integer.valueOf(watk));
        stats.put(MapleStat.Temp.MATK, Integer.valueOf(matk));
        stats.put(MapleStat.Temp.ACC, Integer.valueOf(acc));
        stats.put(MapleStat.Temp.EVA, Integer.valueOf(avoid));
        stats.put(MapleStat.Temp.SPEED, Integer.valueOf(speed));
        stats.put(MapleStat.Temp.JUMP, Integer.valueOf(jump));

        return temporaryStats(stats);
    }

    public static Packet temporaryStats_Aran() {
        Map<Temp, Integer> stats = new EnumMap<>(MapleStat.Temp.class);

        stats.put(MapleStat.Temp.STR, Integer.valueOf(999));
        stats.put(MapleStat.Temp.DEX, Integer.valueOf(999));
        stats.put(MapleStat.Temp.INT, Integer.valueOf(999));
        stats.put(MapleStat.Temp.LUK, Integer.valueOf(999));
        stats.put(MapleStat.Temp.WATK, Integer.valueOf(255));
        stats.put(MapleStat.Temp.ACC, Integer.valueOf(999));
        stats.put(MapleStat.Temp.EVA, Integer.valueOf(999));
        stats.put(MapleStat.Temp.SPEED, Integer.valueOf(140));
        stats.put(MapleStat.Temp.JUMP, Integer.valueOf(120));

        return temporaryStats(stats);
    }

    public static Packet temporaryStats_Balrog(MapleCharacter chr) {
        Map<Temp, Integer> stats = new EnumMap<>(MapleStat.Temp.class);

        int offset = 1 + (chr.getLevel() - 90) / 20;
        stats.put(MapleStat.Temp.STR, Integer.valueOf(chr.getStat().getTotalStr() / offset));
        stats.put(MapleStat.Temp.DEX, Integer.valueOf(chr.getStat().getTotalDex() / offset));
        stats.put(MapleStat.Temp.INT, Integer.valueOf(chr.getStat().getTotalInt() / offset));
        stats.put(MapleStat.Temp.LUK, Integer.valueOf(chr.getStat().getTotalLuk() / offset));
        stats.put(MapleStat.Temp.WATK, Integer.valueOf(chr.getStat().getTotalWatk() / offset));
        stats.put(MapleStat.Temp.MATK, Integer.valueOf(chr.getStat().getTotalMagic() / offset));

        return temporaryStats(stats);
    }

    public static Packet temporaryStats(Map<MapleStat.Temp, Integer> mystats) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.ForcedStatSet.getValue());
        int updateMask = 0;
        for (MapleStat.Temp statupdate : mystats.keySet()) {
            updateMask |= statupdate.getValue();
        }
        oPacket.EncodeInteger(updateMask);
        for (final Entry<MapleStat.Temp, Integer> statupdate : mystats.entrySet()) {
            switch (statupdate.getKey()) {
                case SPEED:
                case JUMP:
                case UNKNOWN:
                    oPacket.Encode((statupdate.getValue()).byteValue());
                    break;
                default:
                    oPacket.EncodeShort((statupdate.getValue()).shortValue());
            }
        }

        return oPacket.ToPacket();
    }

    public static Packet temporaryStats_Reset() {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.ForcedStatReset.getValue());

        return oPacket.ToPacket();
    }

    public static Packet updateSkills(Map<Skill, SkillEntry> update, boolean hyper) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.ChangeSkillRecordResult.getValue());

        oPacket.Encode(1); // setExclRequestSent()
        oPacket.Encode(0); // bShowResult 
        oPacket.Encode(0); // bRemoveLinkSkill

        oPacket.EncodeShort(update.size());

        for (Map.Entry<Skill, SkillEntry> z : update.entrySet()) {
            oPacket.EncodeInteger(z.getKey().getId());
            oPacket.EncodeInteger(z.getValue().skillevel);
            oPacket.EncodeInteger(z.getValue().masterlevel);
            PacketHelper.addExpirationTime(oPacket, z.getValue().expiration);
        }

        //oPacket.Encode(/*hyper ? 0x0C : */4); // hyperstat = 7 // original
        oPacket.Encode(hyper ? 0x0C : 4); // hmm?

        return oPacket.ToPacket();
    }

    public static Packet updateSkill(Map<Skill, SkillEntry> update, int skillid, int level, int masterlevel, long expiration) { // lmao this one isnt used 
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.ChangeSkillRecordResult.getValue());
        oPacket.Encode(1);
        oPacket.EncodeShort(0);//wasbyte142
        oPacket.EncodeShort(update.size());
        for (Map.Entry z : update.entrySet()) {
            oPacket.EncodeInteger(((Skill) z.getKey()).getId());
            oPacket.EncodeInteger(((SkillEntry) z.getValue()).skillevel);
            oPacket.EncodeInteger(((SkillEntry) z.getValue()).masterlevel);
            PacketHelper.addExpirationTime(oPacket, ((SkillEntry) z.getValue()).expiration);
        }
        oPacket.Encode(/*hyper ? 0x0C : */4); // hyperstat = 7
        return oPacket.ToPacket();
    }

    public static Packet giveFameErrorResponse(int op) {
        return OnFameResult(op, null, true, 0);
    }

    public static Packet OnFameResult(int op, String charname, boolean raise, int newFame) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.GivePopularityResult.getValue());
        oPacket.Encode(op);
        if ((op == 0) || (op == 5)) {
            oPacket.EncodeString(charname == null ? "" : charname);
            oPacket.Encode(raise ? 1 : 0);
            if (op == 0) {
                oPacket.EncodeInteger(newFame);
            }
        }

        return oPacket.ToPacket();
    }

    public static Packet bombLieDetector(boolean error, int mapid, int channel) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.AntiMacroResult.getValue());
        oPacket.Encode(error ? 2 : 1);
        oPacket.EncodeInteger(mapid);
        oPacket.EncodeInteger(channel);

        return oPacket.ToPacket();
    }

    public static Packet sendLieDetector(final byte[] image) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.AntiMacroResult.getValue());
        oPacket.Encode(6); // 1 = not attacking, 2 = tested, 3 = going through 

        oPacket.Encode(4); // 2 give invalid pointer (suppose to be admin macro) 
        oPacket.Encode(1); // the time >0 is always 1 minute 
        if (image == null) {
            oPacket.EncodeInteger(0);
            return oPacket.ToPacket();
        }
        oPacket.EncodeInteger(image.length);
        oPacket.Encode(image);

        return oPacket.ToPacket();
    }

    public static Packet LieDetectorResponse(final byte msg) {
        return LieDetectorResponse(msg, (byte) 0);
    }

    public static Packet LieDetectorResponse(final byte msg, final byte msg2) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.AntiMacroResult.getValue());
        oPacket.Encode(msg); // 1 = not attacking, 2 = tested, 3 = going through 
        oPacket.Encode(msg2);

        return oPacket.ToPacket();
    }

    public static Packet getLieDetector(byte type, String tester) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.AntiMacroResult.getValue()); // 2A 00 01 00 00 00  
        oPacket.Encode(type); // 1 = not attacking, 2 = tested, 3 = going through, 4 save screenshot 
        switch (type) {
            case 4: //save screen shot 
                oPacket.Encode(0);
                oPacket.EncodeString(""); // file name 
                break;
            case 5:
                oPacket.Encode(1); // 2 = save screen shot 
                oPacket.EncodeString(tester); // me or file name 
                break;
            case 6:
                oPacket.Encode(4); // 2 or anything else, 2 = with maple admin picture, basicaly manager's skill? 
                oPacket.Encode(1); // if > 0, then time = 60,000..maybe try < 0? 
                //oPacket.encodeInteger(size);
                //oPacket.encode(byte); // bytes 
                break;
            case 7://send this if failed 
                // 2 = You have been appointed as a auto BOT program user and will be restrained. 
                oPacket.Encode(4); // default 
                break;
            case 9:
                // 0 = passed lie detector test 
                // 1 = reward 5000 mesos for not botting. 
                // 2 = thank you for your cooperation with administrator. 
                oPacket.Encode(0);
                break;
            case 8: // save screen shot.. it appears that you may be using a macro-assisted program
                oPacket.Encode(0); // 2 or anything else , 2 = show msg, 0 = none 
                oPacket.EncodeString(""); // file name 
                break;
            case 10: // no save 
                oPacket.Encode(0); // 2 or anything else, 2 = show msg 
                oPacket.EncodeString(""); // ?? // hi_You have passed the lie detector test 
                break;
            default:
                oPacket.Encode(0);
                break;
        }
        return oPacket.ToPacket();
    }

    public static Packet lieDetector(byte mode, byte action, byte[] image, String str1, String str2, String str3) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.AntiMacroResult.getValue());
        oPacket.Encode(mode);
        oPacket.Encode(action); //2 = show msg/save screenshot/maple admin picture(mode 6)
        if (mode == 6) {
            oPacket.Encode(1); //if true time is 60:00
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

        return oPacket.ToPacket();
    }

    public static Packet report(int mode) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.ClaimResult.getValue());
        oPacket.Encode(mode);
        if (mode == 2) {
            oPacket.Encode(0);
            oPacket.EncodeInteger(1); //times left to report
        }

        return oPacket.ToPacket();
    }

    public static Packet OnSetClaimSvrAvailableTime(int from, int to) {
        OutPacket oPacket = new OutPacket(4);

        oPacket.EncodeShort(SendPacketOpcode.SetClaimSvrAvailableTime.getValue());
        oPacket.Encode(from);
        oPacket.Encode(to);

        return oPacket.ToPacket();
    }

    public static Packet OnClaimSvrStatusChanged(boolean enable) {
        OutPacket oPacket = new OutPacket(3);

        oPacket.EncodeShort(SendPacketOpcode.ClaimSvrStatusChanged.getValue());
        oPacket.Encode(enable ? 1 : 0);

        return oPacket.ToPacket();
    }

    public static Packet updateMount(MapleCharacter chr, boolean levelup) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.SetTamingMobInfo.getValue());
        oPacket.EncodeInteger(chr.getId());
        oPacket.EncodeInteger(chr.getMount().getLevel());
        oPacket.EncodeInteger(chr.getMount().getExp());
        oPacket.EncodeInteger(chr.getMount().getFatigue());
        oPacket.Encode(levelup ? 1 : 0);

        return oPacket.ToPacket();
    }

    public static Packet showQuestCompletion(int id) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.QuestClear.getValue());
        oPacket.EncodeInteger(id);

        return oPacket.ToPacket();
    }

    public static Packet useSkillBook(MapleCharacter chr, int skillid, int maxlevel, boolean canuse, boolean success) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.SkillLearnItemResult.getValue());
        oPacket.Encode(0);
        oPacket.EncodeInteger(chr.getId());
        oPacket.Encode(1);
        oPacket.EncodeInteger(skillid);
        oPacket.EncodeInteger(maxlevel);
        oPacket.Encode(canuse ? 1 : 0);
        oPacket.Encode(success ? 1 : 0);

        return oPacket.ToPacket();
    }

    public static Packet useAPSPReset(boolean spReset, int cid) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(spReset ? SendPacketOpcode.SkillResetItemResult.getValue() : SendPacketOpcode.AbilityResetItemResult.getValue());
        oPacket.Encode(1);
        oPacket.EncodeInteger(cid);
        oPacket.Encode(1);

        return oPacket.ToPacket();
    }

    public static Packet expandCharacterSlots(int mode) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.CharSlotIncItemResult.getValue());
        oPacket.EncodeInteger(mode);
        oPacket.Encode(0);

        return oPacket.ToPacket();
    }

    public static Packet finishedGather(int type) {
        return gatherSortItem(true, type);
    }

    public static Packet finishedSort(int type) {
        return gatherSortItem(false, type);
    }

    public static Packet gatherSortItem(boolean gather, int type) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(gather ? SendPacketOpcode.SortItemResult.getValue() : SendPacketOpcode.GatherItemResult.getValue());
        oPacket.Encode(1);
        oPacket.Encode(type);

        return oPacket.ToPacket();
    }

    public static Packet updateExpPotion(int mode, int id, int itemId, boolean firstTime, int level, int potionDstLevel) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.ExpItemGetResult.getValue());
        oPacket.Encode(mode);
        oPacket.Encode(1); //bool for get_update_time
        oPacket.EncodeInteger(id);
        if (id != 0) {
            oPacket.Encode(1); //not even being read how rude of nexon
            if (mode == 1) {
                oPacket.EncodeInteger(0);
            }
            if (mode == 2) {
                oPacket.Encode(firstTime ? 1 : 0); //1 on first time then it turns 0
                oPacket.EncodeInteger(itemId);
                if (itemId != 0) {
                    oPacket.EncodeInteger(level); //level, confirmed
                    oPacket.EncodeInteger(potionDstLevel); //max level with potion
                    oPacket.EncodeLong(384); //random, more like potion id
                }
            }
        }

        return oPacket.ToPacket();
    }

    public static Packet updateGender(MapleCharacter chr) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.UserGenderResult.getValue());
        oPacket.Encode(chr.getGender());

        return oPacket.ToPacket();
    }

    public static Packet charInfo(MapleCharacter chr, boolean isSelf) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.CharacterInfo.getValue());
        oPacket.EncodeInteger(chr.getId());
        oPacket.Encode(0);
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
        oPacket.Encode(chr.getLevel());
        oPacket.EncodeShort(chr.getJob());
        oPacket.EncodeShort(chr.getSubcategory());
        oPacket.Encode(chr.getStat().pvpRank);
        oPacket.EncodeInteger(chr.getFame());

        final MapleMarriage marriage = chr.getMarriage();
        oPacket.Encode(marriage != null && marriage.getId() != 0);
        if (marriage != null && marriage.getId() != 0) {
            oPacket.EncodeInteger(marriage.getId()); //marriage id
            oPacket.EncodeInteger(marriage.getHusbandId()); //husband char id
            oPacket.EncodeInteger(marriage.getWifeId()); //wife char id
            oPacket.EncodeShort(3); //msg type
            oPacket.EncodeInteger(chr.getMarriageItemId()); //ring id husband
            oPacket.EncodeInteger(chr.getMarriageItemId()); //ring id wife
            oPacket.EncodeString(marriage.getHusbandName(), 13); //husband name
            oPacket.EncodeString(marriage.getWifeName(), 13); //wife name
        }

        List<Integer> prof = chr.getProfessions();
        oPacket.Encode(prof.size());
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
        oPacket.Encode(-1);//nForcedPetIdx
        oPacket.Encode(!isSelf);
        oPacket.Encode(!chr.getSummonedPets().isEmpty());
        oPacket.Encode(!chr.getSummonedPets().isEmpty());
        if (!chr.getSummonedPets().isEmpty()) {
            for (MaplePet pet : chr.getSummonedPets()) {
                byte index = 1;
                oPacket.EncodeInteger(0); // this is the index of the pet.
                oPacket.EncodeInteger(pet.getItem().getItemId());
                oPacket.EncodeString(pet.getName());
                oPacket.Encode(pet.getLevel());
                oPacket.EncodeShort(pet.getCloseness());
                oPacket.Encode(pet.getFullness());
                oPacket.EncodeShort(0);
                Item inv = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) (byte) (index == 2 ? -130 : index == 1 ? -114 : -138));
                oPacket.EncodeInteger(inv == null ? 0 : inv.getItemId());
                oPacket.EncodeInteger(pet.getColor());
                oPacket.Encode(chr.getSummonedPets().size() > index);
                index++;
            }
        }

        final int wishlistSize = chr.getWishlistSize();
        oPacket.Encode(wishlistSize);
        if (wishlistSize > 0) {
            int[] wishlist = chr.getWishlist();
            for (int x = 0; x < wishlistSize; x++) {
                oPacket.EncodeInteger(wishlist[x]);
            }
        }
        // Medal
        final Item medal = chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) EquipSlotType.Medal.getSlot());
        oPacket.EncodeInteger(medal == null ? 0 : medal.getItemId());

        // Medal quests
        List<Pair<Integer, Long>> medalQuests = chr.getCompletedMedals();
        oPacket.EncodeShort(medalQuests.size());
        for (Pair<Integer, Long> x : medalQuests) {
            oPacket.EncodeInteger(x.left);
            oPacket.EncodeLong(x.right); // Time.
        }

        // Added on v176
        oPacket.Encode(1); // This is probably the size 
        oPacket.EncodeInteger(0);
        oPacket.EncodeInteger(2431965); // Basic damage skin id
        oPacket.Encode(0);
        oPacket.EncodeString("This is a basic Damage Skin.\r\n\r\n\r\n\r\n\r\n");
        oPacket.EncodeInteger(-1);
        oPacket.EncodeInteger(0);
        oPacket.Encode(1);
        oPacket.EncodeString("");
        oPacket.EncodeShort(0);
        oPacket.EncodeShort(0);
        // End

        for (MapleTrait.MapleTraitType t : MapleTrait.MapleTraitType.values()) {
            oPacket.Encode(chr.getTrait(t).getLevel());
        }

        oPacket.EncodeInteger(chr.getAccountID());
        PacketHelper.addFarmInfo(oPacket, chr.getClient(), 0);

        oPacket.EncodeInteger(1);
        oPacket.EncodeInteger(0);

        // Chairs
        final List<Integer> chairs = new ArrayList<>();
        for (Item i : chr.getInventory(MapleInventoryType.SETUP).newList()) {
            if (i.getItemId() / 10000 == 301 && !chairs.contains(i.getItemId())) {
                chairs.add(i.getItemId());
            }
        }
        oPacket.EncodeInteger(chairs.size());
        for (Integer chair : chairs) {
            oPacket.EncodeInteger(chair);
        }
        oPacket.EncodeInteger(0);
        oPacket.EncodeInteger(30); // No idea, or 0x1E
        oPacket.EncodeInteger(0);

        return oPacket.ToPacket();
    }

    public static Packet getMonsterBookInfo(MapleCharacter chr) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.ItemCollection_SendCollectionList.getValue());
        oPacket.EncodeInteger(chr.getId());
        oPacket.EncodeInteger(chr.getLevel());
        chr.getMonsterBook().writeCharInfoPacket(oPacket);

        return oPacket.ToPacket();
    }

    public static Packet spawnPortal(int townId, int targetId, int skillId, Point pos) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.TownPortal.getValue());
        oPacket.EncodeInteger(townId);
        oPacket.EncodeInteger(targetId);
        if ((townId != 999999999) && (targetId != 999999999)) {
            oPacket.EncodeInteger(skillId);
            oPacket.EncodePosition(pos);
        }

        return oPacket.ToPacket();
    }

    public static Packet mechPortal(Point pos) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.OpenGateCreated.getValue());
        oPacket.EncodePosition(pos);

        return oPacket.ToPacket();
    }

    public static Packet echoMegaphone(String name, String message) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.ECHO_MESSAGE.getValue());
        oPacket.Encode(0);
        oPacket.EncodeLong(PacketHelper.getTime(System.currentTimeMillis()));
        oPacket.EncodeString(name);
        oPacket.EncodeString(message);

        return oPacket.ToPacket();
    }

    public static Packet showQuestMsg(String msg) {
        return broadcastMsg(5, msg);
    }

    public static Packet Mulung_Pts(int recv, int total) {
        return showQuestMsg(new StringBuilder().append("You have received ").append(recv).append(" training points, for the accumulated total of ").append(total).append(" training points.").toString());
    }

    public static Packet broadcastMsg(String message) {
        return broadcastMessage(4, 0, message, false);
    }

    public static Packet broadcastMsg(int type, String message) {
        return broadcastMessage(type, 0, message, false);
    }

    public static Packet broadcastMsg(int type, int channel, String message) {
        return broadcastMessage(type, channel, message, false);
    }

    public static Packet broadcastMsg(int type, int channel, String message, boolean smegaEar) {
        return broadcastMessage(type, channel, message, smegaEar);
    }

    private static Packet broadcastMessage(int type, int channel, String message, boolean megaEar) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.BrodcastMsg.getValue());
        oPacket.Encode(type);
        if (type == 4) {
            oPacket.Encode(1);
        }
        if ((type != 23) && (type != 24)) {
            oPacket.EncodeString(message);
        }
        switch (type) {
            case 3:
            case 22:
            case 25:
            case 26:
                oPacket.Encode(channel - 1);
                oPacket.Encode(megaEar ? 1 : 0);
                break;
            case 9:
                oPacket.Encode(channel - 1);
                break;
            case 12:
                oPacket.EncodeInteger(channel);
                break;
            case 6:
            case 11:
            case 20:
                oPacket.EncodeInteger((channel >= 1000000) && (channel < 6000000) ? channel : 0);
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
        return oPacket.ToPacket();
    }

    public static Packet getGachaponMega(String name, String message, Item item, byte rareness, String gacha) {
        return getGachaponMega(name, message, item, rareness, false, gacha);
    }

    public static Packet getGachaponMega(String name, String message, Item item, byte rareness, boolean dragon, String gacha) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.BrodcastMsg.getValue());
        oPacket.Encode(13);
        oPacket.EncodeString(new StringBuilder().append(name).append(message).toString());
        if (!dragon) {
            oPacket.EncodeInteger(0);
            oPacket.EncodeInteger(item.getItemId());
        }
        oPacket.EncodeString(gacha);
        PacketHelper.addItemInfo(oPacket, item);

        return oPacket.ToPacket();
    }

    public static Packet getEventEnvelope(int questID, int time) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.BrodcastMsg.getValue());
        oPacket.Encode(23);
        oPacket.EncodeShort(questID);
        oPacket.EncodeInteger(time);

        return oPacket.ToPacket();
    }

    public static Packet tripleSmega(List<String> message, boolean ear, int channel) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.BrodcastMsg.getValue());
        oPacket.Encode(10);
        if (message.get(0) != null) {
            oPacket.EncodeString((String) message.get(0));
        }
        oPacket.Encode(message.size());
        for (int i = 1; i < message.size(); i++) {
            if (message.get(i) != null) {
                oPacket.EncodeString((String) message.get(i));
            }
        }
        oPacket.Encode(channel - 1);
        oPacket.Encode(ear ? 1 : 0);

        return oPacket.ToPacket();
    }

    public static Packet itemMegaphone(String msg, boolean whisper, int channel, Item item) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.BrodcastMsg.getValue());
        oPacket.Encode(9);
        oPacket.EncodeString(msg);
        oPacket.Encode(channel - 1);
        oPacket.Encode(whisper ? 1 : 0);
        PacketHelper.addItemPosition(oPacket, item, true, false);
        if (item != null) {
            PacketHelper.addItemInfo(oPacket, item);
        }

        return oPacket.ToPacket();
    }

    public static Packet getPeanutResult(int itemId, short quantity, int itemId2, short quantity2, int ourItem) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.IncubatorResult.getValue());
        oPacket.EncodeInteger(itemId);
        oPacket.EncodeShort(quantity);
        oPacket.EncodeInteger(ourItem);
        oPacket.EncodeInteger(itemId2);
        oPacket.EncodeInteger(quantity2);
        oPacket.Encode(0);
        oPacket.Encode(0);

        return oPacket.ToPacket();
    }

    public static Packet getOwlOpen() {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.ShopScannerResult.getValue());
        oPacket.Encode(9);
        oPacket.Encode(GameConstants.owlItems.length);
        for (int i : GameConstants.owlItems) {
            oPacket.EncodeInteger(i);
        }

        return oPacket.ToPacket();
    }

    public static Packet getOwlSearched(int itemSearch, List<HiredMerchant> hms) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.ShopScannerResult.getValue());
        oPacket.Encode(8);
        oPacket.EncodeInteger(0);
        oPacket.EncodeInteger(itemSearch);
        int size = 0;

        for (HiredMerchant hm : hms) {
            size += hm.searchItem(itemSearch).size();
        }

        oPacket.EncodeInteger(size);
        for (HiredMerchant hm : hms) {
            for (Iterator<HiredMerchant> i = hms.iterator(); i.hasNext();) {
                hm = (HiredMerchant) i.next();
                final List<MaplePlayerShopItem> items = hm.searchItem(itemSearch);
                for (MaplePlayerShopItem item : items) {
                    oPacket.EncodeString(hm.getOwnerName());
                    oPacket.EncodeInteger(hm.getMap().getId());
                    oPacket.EncodeString(hm.getDescription());
                    oPacket.EncodeInteger(item.item.getQuantity());
                    oPacket.EncodeInteger(item.bundles);
                    oPacket.EncodeInteger(item.price);
                    switch (2) {
                        case 0:
                            oPacket.EncodeInteger(hm.getOwnerId());
                            break;
                        case 1:
                            oPacket.EncodeInteger(hm.getStoreId());
                            break;
                        default:
                            oPacket.EncodeInteger(hm.getObjectId());
                    }

                    oPacket.Encode(hm.getFreeSlot() == -1 ? 1 : 0);
                    oPacket.Encode(GameConstants.getInventoryType(itemSearch).getType());
                    if (GameConstants.getInventoryType(itemSearch) == MapleInventoryType.EQUIP) {
                        PacketHelper.addItemInfo(oPacket, item.item);
                    }
                }
            }
        }
        return oPacket.ToPacket();
    }

    public static Packet getOwlMessage(int msg) {
        OutPacket oPacket = new OutPacket(3);

        oPacket.EncodeShort(SendPacketOpcode.ShopLinkResult.getValue());
        oPacket.Encode(msg);

        return oPacket.ToPacket();
    }

    public static Packet sendEngagementRequest(String name, int cid) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.MarriageRequest.getValue());

        oPacket.Encode(0);
        oPacket.EncodeString(name);
        oPacket.EncodeInteger(cid);

        return oPacket.ToPacket();
    }

    public static Packet sendEngagement(byte msg, int item, MapleCharacter male, MapleCharacter female) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.MarriageResult.getValue());
        oPacket.Encode(msg);
        if (msg == 9 || msg >= 11 && msg <= 14) {
            oPacket.EncodeInteger(0);
            oPacket.EncodeInteger(male.getId());
            oPacket.EncodeInteger(female.getId());
            oPacket.EncodeShort(1);
            oPacket.EncodeInteger(item);
            oPacket.EncodeInteger(item);
            oPacket.EncodeString(male.getName(), 13);
            oPacket.EncodeString(female.getName(), 13);
        } else if (msg == 10 || msg >= 15 && msg <= 16) {
            oPacket.EncodeString("Male", 13);
            oPacket.EncodeString("Female", 13);
            oPacket.EncodeShort(0);
        }

        return oPacket.ToPacket();
    }

    public static Packet sendWeddingGive() {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.WeddingGiftResult.getValue());
        oPacket.Encode(9);
        oPacket.Encode(0);

        return oPacket.ToPacket();
    }

    public static Packet sendWeddingReceive() {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.WeddingGiftResult.getValue());
        oPacket.Encode(10);
        oPacket.EncodeLong(-1L);
        oPacket.EncodeInteger(0);
        oPacket.Encode(0);

        return oPacket.ToPacket();
    }

    public static Packet giveWeddingItem() {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.WeddingGiftResult.getValue());
        oPacket.Encode(11);
        oPacket.Encode(0);
        oPacket.EncodeLong(0L);
        oPacket.Encode(0);

        return oPacket.ToPacket();
    }

    public static Packet receiveWeddingItem() {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.WeddingGiftResult.getValue());
        oPacket.Encode(15);
        oPacket.EncodeLong(0L);
        oPacket.Encode(0);

        return oPacket.ToPacket();
    }

    public static Packet sendCashPetFood(boolean success, byte index) {
        OutPacket oPacket = new OutPacket(3 + (success ? 1 : 0));

        oPacket.EncodeShort(SendPacketOpcode.CashPetFoodResult.getValue());
        oPacket.Encode(success ? 0 : 1);
        if (success) {
            oPacket.Encode(index);
        }

        return oPacket.ToPacket();
    }

    public static Packet yellowChat(String msg) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.SetWeekEventMessage.getValue());
        oPacket.Encode(-1);
        oPacket.EncodeString(msg);

        return oPacket.ToPacket();
    }

    public static Packet shopDiscount(int percent) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.SetPotionDiscountRate.getValue());
        oPacket.Encode(percent);

        return oPacket.ToPacket();
    }

    public static Packet catchMob(int mobid, int itemid, byte success) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.BridleMobCatchFail.getValue());
        oPacket.Encode(success);
        oPacket.EncodeInteger(itemid);
        oPacket.EncodeInteger(mobid);

        return oPacket.ToPacket();
    }

    public static Packet spawnPlayerNPC(PlayerNPC npc) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.ImitatedNPCDisableInfo.getValue());
        oPacket.Encode(1);
        oPacket.EncodeInteger(npc.getId());
        oPacket.EncodeString(npc.getName());
        PacketHelper.addCharLook(oPacket, npc, true, false);

        return oPacket.ToPacket();
    }

    public static Packet disabledNPC(List<Integer> ids) {
        OutPacket oPacket = new OutPacket(3 + ids.size() * 4);

        oPacket.EncodeShort(SendPacketOpcode.LimitedNPCDisableInfo.getValue());
        oPacket.Encode(ids.size());
        for (Integer i : ids) {
            oPacket.EncodeInteger(i.intValue());
        }

        return oPacket.ToPacket();
    }

    public static Packet getCard(int itemid, int level) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.MonsterBookSetCard.getValue());
        oPacket.Encode(itemid > 0 ? 1 : 0);
        if (itemid > 0) {
            oPacket.EncodeInteger(itemid);
            oPacket.EncodeInteger(level);
        }
        return oPacket.ToPacket();
    }

    public static Packet changeCardSet(int set) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.HourChanged.getValue());
        oPacket.EncodeInteger(set);

        return oPacket.ToPacket();
    }

    public static Packet upgradeBook(Item book, MapleCharacter chr) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.LimitedNPCDisableInfo.getValue());
        oPacket.EncodeInteger(book.getPosition());
        PacketHelper.addItemInfo(oPacket, book, chr);

        return oPacket.ToPacket();
    }

    public static Packet getCardDrops(int cardid, List<Integer> drops) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.CARD_DROPS.getValue());
        oPacket.EncodeInteger(cardid);
        oPacket.EncodeShort(drops == null ? 0 : drops.size());
        if (drops != null) {
            for (Integer de : drops) {
                oPacket.EncodeInteger(de);
            }
        }

        return oPacket.ToPacket();
    }

    public static Packet getFamiliarInfo(MapleCharacter chr) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.JournalAvatar.getValue());
        oPacket.EncodeInteger(chr.getFamiliars().size());
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
        oPacket.EncodeInteger(size.size());
        for (Pair<Integer, Long> s : size) {
            oPacket.EncodeInteger(chr.getId());
            oPacket.EncodeInteger(s.left);
            oPacket.EncodeLong(s.right);
            oPacket.Encode(0);
        }
        size.clear();

        return oPacket.ToPacket();
    }

    public static Packet updateWebBoard(boolean result) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.WebBoardAuthkeyUpdate.getValue());
        oPacket.Encode(result);

        return oPacket.ToPacket();
    }

    public static Packet MulungEnergy(int energy) {
        return sendPyramidEnergy("energy", String.valueOf(energy));
    }

    public static Packet sendPyramidEnergy(String type, String amount) {
        return sendString(1, type, amount);
    }

    public static Packet sendGhostPoint(String type, String amount) {
        return sendString(2, type, amount);
    }

    public static Packet sendGhostStatus(String type, String amount) {
        return sendString(3, type, amount);
    }

    public static Packet sendString(int type, String object, String amount) {
        OutPacket oPacket = new OutPacket(80);

        switch (type) {
            case 1:
                oPacket.EncodeShort(SendPacketOpcode.SessionValue.getValue());
                break;
            case 2:
                oPacket.EncodeShort(SendPacketOpcode.PartyValue.getValue());
                break;
            case 3:
                oPacket.EncodeShort(SendPacketOpcode.FieldValue.getValue());
        }

        oPacket.EncodeString(object);
        oPacket.EncodeString(amount);

        return oPacket.ToPacket();
    }

    public static Packet fairyPendantMessage(int termStart, int incExpR) {
        OutPacket oPacket = new OutPacket(14);

        oPacket.EncodeShort(SendPacketOpcode.BonusExpRateChanged.getValue());
        oPacket.EncodeInteger(17);
        oPacket.EncodeInteger(0);

        oPacket.EncodeInteger(incExpR);

        return oPacket.ToPacket();
    }

    public static Packet potionDiscountMessage(int type, int potionDiscR) {
        OutPacket oPacket = new OutPacket(10);

        oPacket.EncodeShort(SendPacketOpcode.SetPotionDiscountRate.getValue());
        oPacket.EncodeInteger(type);
        oPacket.EncodeInteger(potionDiscR);

        return oPacket.ToPacket();
    }

    public static Packet sendLevelup(boolean family, int level, String name) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.NotifyLevelUp.getValue());
        oPacket.Encode(family ? 1 : 2);
        oPacket.EncodeInteger(level);
        oPacket.EncodeString(name);

        return oPacket.ToPacket();
    }

    public static Packet sendMarriage(boolean family, String name) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.NotifyWedding.getValue());
        oPacket.Encode(family ? 1 : 0);
        oPacket.EncodeString(name);

        return oPacket.ToPacket();
    }

    //mark packet
    public static Packet giveMarkOfTheif(int cid, int oid, int skillid, List<MapleMonster> monsters, Point p1, Point p2, int javelin) {
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.ForceAtomCreate.getValue());
        oPacket.Encode(1);
        oPacket.EncodeInteger(cid);
        oPacket.EncodeInteger(oid);
        oPacket.EncodeInteger(11); //type
        oPacket.Encode(1);
        oPacket.EncodeInteger(monsters.size());
        for (MapleMonster monster : monsters) {
            oPacket.EncodeInteger(monster.getObjectId());
        }
        oPacket.EncodeInteger(skillid); //skillid
        for (int i = 0; i < monsters.size(); i++) {
            oPacket.Encode(1);
            oPacket.EncodeInteger(i + 2);
            oPacket.EncodeInteger(1);
            oPacket.EncodeInteger(Randomizer.rand(0x2A, 0x2B));
            oPacket.EncodeInteger(Randomizer.rand(0x03, 0x04));
            oPacket.EncodeInteger(Randomizer.rand(0x43, 0xF5));
            oPacket.EncodeInteger(200);
            oPacket.EncodeLong(0);
            oPacket.EncodeInteger(Randomizer.nextInt());
            oPacket.EncodeInteger(0);
        }
        oPacket.Encode(0);
        //for (Point p : pos) {
        oPacket.EncodeInteger(p1.x);
        oPacket.EncodeInteger(p1.y);
        oPacket.EncodeInteger(p2.x);
        oPacket.EncodeInteger(p2.y);
        //}
        oPacket.EncodeInteger(javelin);
        //System.out.println(packet.toString());

        oPacket.Fill(0, 69); //We might need this =p
        return oPacket.ToPacket();
    }

    //
    public static Packet sendJobup(boolean family, int jobid, String name) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.NotifyJobChange.getValue());
        oPacket.Encode(family ? 1 : 0);
        oPacket.EncodeInteger(jobid);
        oPacket.EncodeString(new StringBuilder().append(!family ? "> " : "").append(name).toString());

        return oPacket.ToPacket();
    }

    public static Packet getAvatarMega(MapleCharacter chr, int channel, int itemId, List<String> text, boolean ear) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.AvatarMegaphoneUpdateMessage.getValue());
        oPacket.EncodeInteger(itemId);
        oPacket.EncodeString(chr.getName());
        for (String i : text) {
            oPacket.EncodeString(i);
        }
        oPacket.EncodeInteger(channel - 1);
        oPacket.Encode(ear ? 1 : 0);
        CField.writeCharacterLook(oPacket, chr);

        return oPacket.ToPacket();
    }

    public static Packet GMPoliceMessage(boolean dc) {
        OutPacket oPacket = new OutPacket(3);

        oPacket.EncodeShort(SendPacketOpcode.GM_POLICE.getValue());
        oPacket.Encode(dc ? 10 : 0);

        return oPacket.ToPacket();
    }

    public static Packet followRequest(int chrid) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.SetPassenserRequest.getValue());
        oPacket.EncodeInteger(chrid);

        return oPacket.ToPacket();
    }

    public static Packet getTopMsg(String msg) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.ScriptProgressMessage.getValue());
        oPacket.EncodeString(msg);

        return oPacket.ToPacket();
    }

    public static Packet showMidMsg(String s, int l) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.SetStaticScreenMessage.getValue());
        oPacket.Encode(l);
        oPacket.EncodeString(s);
        oPacket.Encode(s.length() > 0 ? 0 : 1);

        return oPacket.ToPacket();
    }

    public static Packet getMidMsg(String msg, boolean keep, int index) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.SetStaticScreenMessage.getValue());
        oPacket.Encode(index);
        oPacket.EncodeString(msg);
        oPacket.Encode(keep ? 0 : 1);

        return oPacket.ToPacket();
    }

    public static Packet clearMidMsg() {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.OffStaticScreenMessage.getValue());

        return oPacket.ToPacket();
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
    public static Packet getSpecialMsg(String msg, int type, boolean show, int delay) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.WeatherEffectNotice.getValue());
        oPacket.EncodeString(msg);
        oPacket.EncodeInteger(type); // 1 = Festival, 2 = Winter, 3 = Chocolate, 4 = Rose, 5 = Candy, 6 = Maple, 7 = Fireworks, 8 = Sports, 9/0xA = Soccer?, 0xB = Ghost, 0xC = Plants? lol
        oPacket.EncodeInteger(show ? 0 : delay);
        oPacket.Encode(0); // added on v170~176, idk

        return oPacket.ToPacket();
    }

    public static Packet CakePieMsg() {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.WeatherEffectNoticeY.getValue());

        return oPacket.ToPacket();
    }

    public static Packet updateJaguar(MapleCharacter from) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.WildHunterInfo.getValue());
        PacketHelper.addJaguarInfo(oPacket, from);

        return oPacket.ToPacket();
    }

    public static Packet loadInformation(byte mode, int location, int birthday, int favoriteAction, int favoriteLocation, boolean success) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.AswanStateInfo.getValue());
        oPacket.Encode(mode);
        if (mode == 2) {
            oPacket.EncodeInteger(location);
            oPacket.EncodeInteger(birthday);
            oPacket.EncodeInteger(favoriteAction);
            oPacket.EncodeInteger(favoriteLocation);
        } else if (mode == 4) {
            oPacket.Encode(success ? 1 : 0);
        }

        return oPacket.ToPacket();
    }

    public static Packet saveInformation(boolean fail) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.AswanStateInfo.getValue());
        oPacket.Encode(4);
        oPacket.Encode(fail ? 0 : 1);

        return oPacket.ToPacket();
    }

    public static Packet myInfoResult() {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.AswanResult.getValue());
        oPacket.Encode(6);
        oPacket.EncodeInteger(0);
        oPacket.EncodeInteger(0);

        return oPacket.ToPacket();
    }

    public static Packet findFriendResult(byte mode, List<MapleCharacter> friends, int error, MapleCharacter chr) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.AswanResult.getValue());
        oPacket.Encode(mode);
        switch (mode) {
            case 6:
                oPacket.EncodeInteger(0);
                oPacket.EncodeInteger(0);
                break;
            case 8:
                oPacket.EncodeShort(friends.size());
                for (MapleCharacter mc : friends) {
                    oPacket.EncodeInteger(mc.getId());
                    oPacket.EncodeString(mc.getName());
                    oPacket.Encode(mc.getLevel());
                    oPacket.EncodeShort(mc.getJob());
                    oPacket.EncodeInteger(0);
                    oPacket.EncodeInteger(0);
                }
                break;
            case 9:
                oPacket.Encode(error);
                break;
            case 11:
                oPacket.EncodeInteger(chr.getId());
                CField.writeCharacterLook(oPacket, chr);
                break;
        }

        return oPacket.ToPacket();
    }

    public static Packet showBackgroundEffect(String eff, int value) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.ReadyForRespawn.getValue());
        oPacket.EncodeString(eff);
        oPacket.Encode(value);

        return oPacket.ToPacket();
    }

    public static Packet sendPinkBeanChoco() {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.ReadyForRespawnByPoint.getValue());
        oPacket.EncodeInteger(0);
        oPacket.Encode(1);
        oPacket.Encode(0);
        oPacket.Encode(0);
        oPacket.EncodeInteger(0);

        return oPacket.ToPacket();
    }

    public static Packet changeChannelMsg(int channel, String msg) {
        OutPacket oPacket = new OutPacket(8 + msg.length());

        oPacket.EncodeShort(SendPacketOpcode.CharacterHonorGift.getValue());
        oPacket.EncodeInteger(channel);
        oPacket.EncodeString(msg);

        return oPacket.ToPacket();
    }

    public static Packet pamSongUI() {
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.OpenReadyForRespawnUI.getValue());
        return oPacket.ToPacket();
    }

    public static Packet ultimateExplorer() {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.OpenUICreatePremiumAdventurer.getValue());

        return oPacket.ToPacket();
    }

    public static Packet professionInfo(String skil, int level1, int level2, int chance) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.ResultInstanceTable.getValue());
        oPacket.EncodeString(skil);
        oPacket.EncodeInteger(level1);
        oPacket.EncodeInteger(level2);
        oPacket.Encode(1);
        oPacket.EncodeInteger((skil.startsWith("9200")) || (skil.startsWith("9201")) ? 100 : chance);

        return oPacket.ToPacket();
    }

    public static Packet updateAzwanFame(int level, int fame, boolean levelup) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.CharacterHonorExp.getValue());
        oPacket.EncodeInteger(level);
        oPacket.EncodeInteger(fame);
        oPacket.Encode(levelup ? 1 : 0);

        return oPacket.ToPacket();
    }

    public static Packet showSilentCrusadeMsg(byte type, short chapter) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.CrossHunterCompleteResult.getValue());
        oPacket.Encode(type);
        oPacket.EncodeShort(chapter - 1);

        /* type:
         * 0 - open ui (short is chapter)
         * 2 - not enough inventory space
         * 3 - failed due to unknown error
         */
        return oPacket.ToPacket();
    }

    public static Packet getSilentCrusadeMsg(byte type) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.CrossHunterShopResult.getValue());
        oPacket.Encode(type);

        return oPacket.ToPacket();
    }

    public static Packet showSCShopMsg(byte type) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.CrossHunterShopResult.getValue());
        oPacket.Encode(type);

        return oPacket.ToPacket();
    }

    public static Packet updateImpTime() {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.CoolTimeSet.getValue());
        oPacket.EncodeInteger(0);
        oPacket.EncodeLong(0L);

        return oPacket.ToPacket();
    }

    public static Packet updateImp(MapleImp imp, int mask, int index, boolean login) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.ItemPotChange.getValue());
        oPacket.Encode(login ? 0 : 1);
        oPacket.EncodeInteger(index + 1);
        oPacket.EncodeInteger(mask);
        if ((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) {
            Pair<?, ?> i = MapleItemInformationProvider.getInstance().getPot(imp.getItemId());
            if (i == null) {
                return enableActions();
            }
            oPacket.EncodeInteger(((Integer) i.left).intValue());
            oPacket.Encode(imp.getLevel());
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.STATE.getValue()) != 0)) {
            oPacket.Encode(imp.getState());
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.FULLNESS.getValue()) != 0)) {
            oPacket.EncodeInteger(imp.getFullness());
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.CLOSENESS.getValue()) != 0)) {
            oPacket.EncodeInteger(imp.getCloseness());
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.CLOSENESS_LEFT.getValue()) != 0)) {
            oPacket.EncodeInteger(1);
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.MINUTES_LEFT.getValue()) != 0)) {
            oPacket.EncodeInteger(0);
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.LEVEL.getValue()) != 0)) {
            oPacket.Encode(1);
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.FULLNESS_2.getValue()) != 0)) {
            oPacket.EncodeInteger(imp.getFullness());
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
            oPacket.EncodeInteger(100);
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.MAX_DELAY.getValue()) != 0)) {
            oPacket.EncodeInteger(1000);
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.MAX_FULLNESS.getValue()) != 0)) {
            oPacket.EncodeInteger(1000);
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.MAX_ALIVE.getValue()) != 0)) {
            oPacket.EncodeInteger(1);
        }
        if (((mask & MapleImp.ImpFlag.SUMMONED.getValue()) != 0) || ((mask & MapleImp.ImpFlag.MAX_MINUTES.getValue()) != 0)) {
            oPacket.EncodeInteger(10);
        }
        oPacket.Encode(0);

        return oPacket.ToPacket();
    }

    public static Packet getMulungRanking(MapleCharacter chr) {
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.DojangRanking.getValue());
        oPacket.EncodeInteger(0);
        oPacket.Encode(1); //nType

        oPacket.EncodeInteger(chr.getJob());
        oPacket.EncodeInteger(chr.getLevel());
        oPacket.EncodeInteger(chr.getPoints());
        oPacket.EncodeInteger(0);//dojo rank
        oPacket.EncodeInteger(0); //percent
        oPacket.EncodeInteger(0); //nLastPoint
        oPacket.EncodeInteger(0); //nLastRank
        oPacket.EncodeInteger(0); //nLastPercent

        oPacket.Encode(1); //nTypeID

        DojoRankingsData data = DojoRankingsData.loadLeaderboard();
        oPacket.EncodeInteger(data.totalCharacters); // size
        for (int i = 0; i < data.totalCharacters; i++) {
            oPacket.EncodeInteger(0); // Rank user job ID
            oPacket.EncodeInteger(0); // Rank user job Level
            oPacket.EncodeInteger(0); // Rank user points
            oPacket.EncodeInteger(data.ranks[i]); // rank
            oPacket.EncodeString(data.names[i]); // Character name

            oPacket.Encode(0); //bPackedCharacterLook
            //packedCharacterLook here. so no thank you.
        }
        return oPacket.ToPacket();
    }

    public static Packet getMulungMessage(boolean dc, String msg) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.UserUseNaviFlyingResult.getValue());
        oPacket.Encode(dc ? 1 : 0);
        oPacket.EncodeString(msg);

        return oPacket.ToPacket();
    }

    public static Packet getCandyRanking(MapleClient c, List<CandyRankingInfo> all) {
        OutPacket oPacket = new OutPacket(10);

        oPacket.EncodeShort(SendPacketOpcode.HalloweenCandyRankingResult.getValue());
        oPacket.EncodeInteger(all.size());
        for (CandyRankingInfo info : all) {
            oPacket.EncodeShort(info.getRank());
            oPacket.EncodeString(info.getName());
        }
        return oPacket.ToPacket();
    }

    public static Packet showForeignDamageSkin(MapleCharacter chr, int skinid) {
        OutPacket packet = new OutPacket(80);
        packet.EncodeShort(SendPacketOpcode.UserSetDamageSkin.getValue());
        packet.EncodeInteger(chr.getId());
        packet.EncodeInteger(skinid);
        return packet.ToPacket();
    }

    public static class AlliancePacket {

        public static Packet getAllianceInfo(MapleGuildAlliance alliance) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.AllianceResult.getValue());
            oPacket.Encode(12);
            oPacket.Encode(alliance == null ? 0 : 1);
            if (alliance != null) {
                addAllianceInfo(oPacket, alliance);
            }

            return oPacket.ToPacket();
        }

        private static void addAllianceInfo(OutPacket oPacket, MapleGuildAlliance alliance) {
            oPacket.EncodeInteger(alliance.getId());
            oPacket.EncodeString(alliance.getName());
            for (int i = 1; i <= 5; i++) {
                oPacket.EncodeString(alliance.getRank(i));
            }
            oPacket.Encode(alliance.getNoGuilds());
            for (int i = 0; i < alliance.getNoGuilds(); i++) {
                oPacket.EncodeInteger(alliance.getGuildId(i));
            }
            oPacket.EncodeInteger(alliance.getCapacity());
            oPacket.EncodeString(alliance.getNotice());
        }

        public static Packet getGuildAlliance(MapleGuildAlliance alliance) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.AllianceResult.getValue());
            oPacket.Encode(13);
            if (alliance == null) {
                oPacket.EncodeInteger(0);
                return oPacket.ToPacket();
            }
            int noGuilds = alliance.getNoGuilds();
            MapleGuild[] g = new MapleGuild[noGuilds];
            for (int i = 0; i < alliance.getNoGuilds(); i++) {
                g[i] = World.Guild.getGuild(alliance.getGuildId(i));
                if (g[i] == null) {
                    return CWvsContext.enableActions();
                }
            }
            oPacket.EncodeInteger(noGuilds);
            for (MapleGuild gg : g) {
                CWvsContext.GuildPacket.getGuildInfo(oPacket, gg);
            }
            return oPacket.ToPacket();
        }

        public static Packet allianceMemberOnline(int alliance, int gid, int id, boolean online) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.AllianceResult.getValue());
            oPacket.Encode(14);
            oPacket.EncodeInteger(alliance);
            oPacket.EncodeInteger(gid);
            oPacket.EncodeInteger(id);
            oPacket.Encode(online ? 1 : 0);

            return oPacket.ToPacket();
        }

        public static Packet removeGuildFromAlliance(MapleGuildAlliance alliance, MapleGuild expelledGuild, boolean expelled) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.AllianceResult.getValue());
            oPacket.Encode(16);
            addAllianceInfo(oPacket, alliance);
            CWvsContext.GuildPacket.getGuildInfo(oPacket, expelledGuild);
            oPacket.Encode(expelled ? 1 : 0);

            return oPacket.ToPacket();
        }

        public static Packet addGuildToAlliance(MapleGuildAlliance alliance, MapleGuild newGuild) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.AllianceResult.getValue());
            oPacket.Encode(18);
            addAllianceInfo(oPacket, alliance);
            oPacket.EncodeInteger(newGuild.getId());
            CWvsContext.GuildPacket.getGuildInfo(oPacket, newGuild);
            oPacket.Encode(0);

            return oPacket.ToPacket();
        }

        public static Packet sendAllianceInvite(String allianceName, MapleCharacter inviter) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.AllianceResult.getValue());
            oPacket.Encode(3);
            oPacket.EncodeInteger(inviter.getGuildId());
            oPacket.EncodeString(inviter.getName());
            oPacket.EncodeString(allianceName);

            return oPacket.ToPacket();
        }

        public static Packet getAllianceUpdate(MapleGuildAlliance alliance) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.AllianceResult.getValue());
            oPacket.Encode(23);
            addAllianceInfo(oPacket, alliance);

            return oPacket.ToPacket();
        }

        public static Packet createGuildAlliance(MapleGuildAlliance alliance) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.AllianceResult.getValue());
            oPacket.Encode(15);
            addAllianceInfo(oPacket, alliance);
            int noGuilds = alliance.getNoGuilds();
            MapleGuild[] g = new MapleGuild[noGuilds];
            for (int i = 0; i < alliance.getNoGuilds(); i++) {
                g[i] = World.Guild.getGuild(alliance.getGuildId(i));
                if (g[i] == null) {
                    return CWvsContext.enableActions();
                }
            }
            for (MapleGuild gg : g) {
                CWvsContext.GuildPacket.getGuildInfo(oPacket, gg);
            }
            return oPacket.ToPacket();
        }

        public static Packet updateAlliance(MapleGuildCharacter mgc, int allianceid) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.AllianceResult.getValue());
            oPacket.Encode(24);
            oPacket.EncodeInteger(allianceid);
            oPacket.EncodeInteger(mgc.getGuildId());
            oPacket.EncodeInteger(mgc.getId());
            oPacket.EncodeInteger(mgc.getLevel());
            oPacket.EncodeInteger(mgc.getJobId());

            return oPacket.ToPacket();
        }

        public static Packet updateAllianceLeader(int allianceid, int newLeader, int oldLeader) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.AllianceResult.getValue());
            oPacket.Encode(25);
            oPacket.EncodeInteger(allianceid);
            oPacket.EncodeInteger(oldLeader);
            oPacket.EncodeInteger(newLeader);

            return oPacket.ToPacket();
        }

        public static Packet allianceRankChange(int aid, String[] ranks) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.GuildResult.getValue());
            oPacket.Encode(26);
            oPacket.EncodeInteger(aid);
            for (String r : ranks) {
                oPacket.EncodeString(r);
            }

            return oPacket.ToPacket();
        }

        public static Packet updateAllianceRank(MapleGuildCharacter mgc) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.AllianceResult.getValue());
            oPacket.Encode(27);
            oPacket.EncodeInteger(mgc.getId());
            oPacket.Encode(mgc.getAllianceRank());

            return oPacket.ToPacket();
        }

        public static Packet changeAllianceNotice(int allianceid, String notice) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.AllianceResult.getValue());
            oPacket.Encode(28);
            oPacket.EncodeInteger(allianceid);
            oPacket.EncodeString(notice);

            return oPacket.ToPacket();
        }

        public static Packet disbandAlliance(int alliance) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.AllianceResult.getValue());
            oPacket.Encode(29);
            oPacket.EncodeInteger(alliance);

            return oPacket.ToPacket();
        }

        public static Packet changeAlliance(MapleGuildAlliance alliance, boolean in) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.AllianceResult.getValue());
            oPacket.Encode(1);
            oPacket.Encode(in ? 1 : 0);
            oPacket.EncodeInteger(in ? alliance.getId() : 0);
            int noGuilds = alliance.getNoGuilds();
            MapleGuild[] g = new MapleGuild[noGuilds];
            for (int i = 0; i < noGuilds; i++) {
                g[i] = World.Guild.getGuild(alliance.getGuildId(i));
                if (g[i] == null) {
                    return CWvsContext.enableActions();
                }
            }
            oPacket.Encode(noGuilds);
            for (int i = 0; i < noGuilds; i++) {
                oPacket.EncodeInteger(g[i].getId());

                Collection<MapleGuildCharacter> members = g[i].getMembers();
                oPacket.EncodeInteger(members.size());
                for (MapleGuildCharacter mgc : members) {
                    oPacket.EncodeInteger(mgc.getId());
                    oPacket.Encode(in ? mgc.getAllianceRank() : 0);
                }
            }

            return oPacket.ToPacket();
        }

        public static Packet changeAllianceLeader(int allianceid, int newLeader, int oldLeader) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.AllianceResult.getValue());
            oPacket.Encode(2);
            oPacket.EncodeInteger(allianceid);
            oPacket.EncodeInteger(oldLeader);
            oPacket.EncodeInteger(newLeader);

            return oPacket.ToPacket();
        }

        public static Packet changeGuildInAlliance(MapleGuildAlliance alliance, MapleGuild guild, boolean add) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.AllianceResult.getValue());
            oPacket.Encode(4);
            oPacket.EncodeInteger(add ? alliance.getId() : 0);
            oPacket.EncodeInteger(guild.getId());
            Collection<MapleGuildCharacter> members = guild.getMembers();
            oPacket.EncodeInteger(members.size());
            for (MapleGuildCharacter mgc : members) {
                oPacket.EncodeInteger(mgc.getId());
                oPacket.Encode(add ? mgc.getAllianceRank() : 0);
            }

            return oPacket.ToPacket();
        }

        public static Packet changeAllianceRank(int allianceid, MapleGuildCharacter player) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.AllianceResult.getValue());
            oPacket.Encode(5);
            oPacket.EncodeInteger(allianceid);
            oPacket.EncodeInteger(player.getId());
            oPacket.EncodeInteger(player.getAllianceRank());

            return oPacket.ToPacket();
        }
    }

    public static class FamilyPacket {

        public static Packet getFamilyData() {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.FamilyPrivilegeList.getValue());
            MapleFamilyBuff[] entries = MapleFamilyBuff.values();
            oPacket.EncodeInteger(entries.length);

            for (MapleFamilyBuff entry : entries) {
                oPacket.Encode(entry.type);
                oPacket.EncodeInteger(entry.rep);
                oPacket.EncodeInteger(1);
                oPacket.EncodeString(entry.name);
                oPacket.EncodeString(entry.desc);
            }
            return oPacket.ToPacket();
        }

        public static Packet getFamilyInfo(MapleCharacter chr) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.FamilyChartResult.getValue());
            oPacket.EncodeInteger(chr.getCurrentRep());
            oPacket.EncodeInteger(chr.getTotalRep());
            oPacket.EncodeInteger(chr.getTotalRep());
            oPacket.EncodeShort(chr.getNoJuniors());
            oPacket.EncodeShort(2);
            oPacket.EncodeShort(chr.getNoJuniors());
            MapleFamily family = World.Family.getFamily(chr.getFamilyId());
            if (family != null) {
                oPacket.EncodeInteger(family.getLeaderId());
                oPacket.EncodeString(family.getLeaderName());
                oPacket.EncodeString(family.getNotice());
            } else {
                oPacket.EncodeLong(0L);
            }
            List<?> b = chr.usedBuffs();
            oPacket.EncodeInteger(b.size());
            for (Iterator<?> i$ = b.iterator(); i$.hasNext();) {
                int ii = ((Integer) i$.next()).intValue();
                oPacket.EncodeInteger(ii);
                oPacket.EncodeInteger(1);
            }

            return oPacket.ToPacket();
        }

        public static void addFamilyCharInfo(MapleFamilyCharacter ldr, OutPacket oPacket) {
            oPacket.EncodeInteger(ldr.getId());
            oPacket.EncodeInteger(ldr.getSeniorId());
            oPacket.EncodeShort(ldr.getJobId());
            oPacket.EncodeShort(0);
            oPacket.Encode(ldr.getLevel());
            oPacket.Encode(ldr.isOnline() ? 1 : 0);
            oPacket.EncodeInteger(ldr.getCurrentRep());
            oPacket.EncodeInteger(ldr.getTotalRep());
            oPacket.EncodeInteger(ldr.getTotalRep());
            oPacket.EncodeInteger(ldr.getTotalRep());
            oPacket.EncodeInteger(Math.max(ldr.getChannel(), 0));
            oPacket.EncodeInteger(0);
            oPacket.EncodeString(ldr.getName());
        }

        public static Packet getFamilyPedigree(MapleCharacter chr) {
            OutPacket oPacket = new OutPacket(80);
            oPacket.EncodeShort(SendPacketOpcode.FamilyPrivilegeList.getValue());
            oPacket.EncodeInteger(chr.getId());
            MapleFamily family = World.Family.getFamily(chr.getFamilyId());

            int descendants = 2;
            int gens = 0;
            int generations = 0;
            if (family == null) {
                oPacket.EncodeInteger(2);
                addFamilyCharInfo(new MapleFamilyCharacter(chr, 0, 0, 0, 0), oPacket);
            } else {
                oPacket.EncodeInteger(family.getMFC(chr.getId()).getPedigree().size() + 1);
                addFamilyCharInfo(family.getMFC(family.getLeaderId()), oPacket);

                if (chr.getSeniorId() > 0) {
                    MapleFamilyCharacter senior = family.getMFC(chr.getSeniorId());
                    if (senior != null) {
                        if (senior.getSeniorId() > 0) {
                            addFamilyCharInfo(family.getMFC(senior.getSeniorId()), oPacket);
                        }
                        addFamilyCharInfo(senior, oPacket);
                    }
                }
            }
            addFamilyCharInfo(chr.getMFC() == null ? new MapleFamilyCharacter(chr, 0, 0, 0, 0) : chr.getMFC(), oPacket);
            if (family != null) {
                if (chr.getSeniorId() > 0) {
                    MapleFamilyCharacter senior = family.getMFC(chr.getSeniorId());
                    if (senior != null) {
                        if ((senior.getJunior1() > 0) && (senior.getJunior1() != chr.getId())) {
                            addFamilyCharInfo(family.getMFC(senior.getJunior1()), oPacket);
                        } else if ((senior.getJunior2() > 0) && (senior.getJunior2() != chr.getId())) {
                            addFamilyCharInfo(family.getMFC(senior.getJunior2()), oPacket);
                        }

                    }

                }

                if (chr.getJunior1() > 0) {
                    MapleFamilyCharacter junior = family.getMFC(chr.getJunior1());
                    if (junior != null) {
                        addFamilyCharInfo(junior, oPacket);
                    }
                }
                if (chr.getJunior2() > 0) {
                    MapleFamilyCharacter junior = family.getMFC(chr.getJunior2());
                    if (junior != null) {
                        addFamilyCharInfo(junior, oPacket);
                    }
                }
                if (chr.getJunior1() > 0) {
                    MapleFamilyCharacter junior = family.getMFC(chr.getJunior1());
                    if (junior != null) {
                        if ((junior.getJunior1() > 0) && (family.getMFC(junior.getJunior1()) != null)) {
                            gens++;
                            addFamilyCharInfo(family.getMFC(junior.getJunior1()), oPacket);
                        }
                        if ((junior.getJunior2() > 0) && (family.getMFC(junior.getJunior2()) != null)) {
                            gens++;
                            addFamilyCharInfo(family.getMFC(junior.getJunior2()), oPacket);
                        }
                    }
                }
                if (chr.getJunior2() > 0) {
                    MapleFamilyCharacter junior = family.getMFC(chr.getJunior2());
                    if (junior != null) {
                        if ((junior.getJunior1() > 0) && (family.getMFC(junior.getJunior1()) != null)) {
                            gens++;
                            addFamilyCharInfo(family.getMFC(junior.getJunior1()), oPacket);
                        }
                        if ((junior.getJunior2() > 0) && (family.getMFC(junior.getJunior2()) != null)) {
                            gens++;
                            addFamilyCharInfo(family.getMFC(junior.getJunior2()), oPacket);
                        }
                    }
                }
                generations = family.getMemberSize();
            }
            oPacket.EncodeLong(gens);
            oPacket.EncodeInteger(0);
            oPacket.EncodeInteger(-1);
            oPacket.EncodeInteger(generations);

            if (family != null) {
                if (chr.getJunior1() > 0) {
                    MapleFamilyCharacter junior = family.getMFC(chr.getJunior1());
                    if (junior != null) {
                        if ((junior.getJunior1() > 0) && (family.getMFC(junior.getJunior1()) != null)) {
                            oPacket.EncodeInteger(junior.getJunior1());
                            oPacket.EncodeInteger(family.getMFC(junior.getJunior1()).getDescendants());
                        } else {
                            oPacket.EncodeInteger(0);
                        }
                        if ((junior.getJunior2() > 0) && (family.getMFC(junior.getJunior2()) != null)) {
                            oPacket.EncodeInteger(junior.getJunior2());
                            oPacket.EncodeInteger(family.getMFC(junior.getJunior2()).getDescendants());
                        } else {
                            oPacket.EncodeInteger(0);
                        }
                    }
                }
                if (chr.getJunior2() > 0) {
                    MapleFamilyCharacter junior = family.getMFC(chr.getJunior2());
                    if (junior != null) {
                        if ((junior.getJunior1() > 0) && (family.getMFC(junior.getJunior1()) != null)) {
                            oPacket.EncodeInteger(junior.getJunior1());
                            oPacket.EncodeInteger(family.getMFC(junior.getJunior1()).getDescendants());
                        } else {
                            oPacket.EncodeInteger(0);
                        }
                        if ((junior.getJunior2() > 0) && (family.getMFC(junior.getJunior2()) != null)) {
                            oPacket.EncodeInteger(junior.getJunior2());
                            oPacket.EncodeInteger(family.getMFC(junior.getJunior2()).getDescendants());
                        } else {
                            oPacket.EncodeInteger(0);
                        }
                    }
                }
            }

            List<?> b = chr.usedBuffs();
            oPacket.EncodeInteger(b.size());
            for (Iterator<?> i$ = b.iterator(); i$.hasNext();) {
                int ii = ((Integer) i$.next()).intValue();
                oPacket.EncodeInteger(ii);
                oPacket.EncodeInteger(1);
            }
            oPacket.EncodeShort(2);

            return oPacket.ToPacket();
        }

        public static Packet getFamilyMsg(byte type, int meso) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.FamilyInfoResult.getValue());
            oPacket.EncodeInteger(type);
            oPacket.EncodeInteger(meso);

            return oPacket.ToPacket();
        }

        public static Packet sendFamilyInvite(int cid, int otherLevel, int otherJob, String inviter) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.FamilyResult.getValue());
            oPacket.EncodeInteger(cid);
            oPacket.EncodeInteger(otherLevel);
            oPacket.EncodeInteger(otherJob);
            oPacket.EncodeInteger(0);
            oPacket.EncodeString(inviter);
            return oPacket.ToPacket();
        }

        public static Packet sendFamilyJoinResponse(boolean accepted, String added) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.FamilyJoinRequest.getValue());
            oPacket.Encode(accepted ? 1 : 0);
            oPacket.EncodeString(added);

            return oPacket.ToPacket();
        }

        public static Packet getSeniorMessage(String name) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.FamilyJoinAccepted.getValue());
            oPacket.EncodeString(name);

            return oPacket.ToPacket();
        }

        public static Packet changeRep(int r, String name) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.FamilyFamousPointIncResult.getValue());
            oPacket.EncodeInteger(r);
            oPacket.EncodeString(name);

            return oPacket.ToPacket();
        }

        public static Packet familyLoggedIn(boolean online, String name) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.FamilyNotifyLoginOrLogout.getValue());
            oPacket.Encode(online ? 1 : 0);
            oPacket.EncodeString(name);

            return oPacket.ToPacket();
        }

        public static Packet familyBuff(int type, int buffnr, int amount, int time) {
            OutPacket oPacket = new OutPacket(80);
            oPacket.EncodeShort(SendPacketOpcode.FamilySetPrivilege.getValue());
            oPacket.Encode(type);
            if ((type >= 2) && (type <= 4)) {
                oPacket.EncodeInteger(buffnr);

                oPacket.EncodeInteger(type == 3 ? 0 : amount);
                oPacket.EncodeInteger(type == 2 ? 0 : amount);
                oPacket.Encode(0);
                oPacket.EncodeInteger(time);
            }
            return oPacket.ToPacket();
        }

        public static Packet cancelFamilyBuff() {
            return familyBuff(0, 0, 0, 0);
        }

        public static Packet familySummonRequest(String name, String mapname) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.FamilySummonRequest.getValue());
            oPacket.EncodeString(name);
            oPacket.EncodeString(mapname);

            return oPacket.ToPacket();
        }
    }

    /**
     * This packet handles all of the functions for buddies
     *
     * @param buddy
     */
    public static Packet buddylistMessage(Buddy buddy) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.FriendResult.getValue());
        oPacket.Encode(buddy.getResult().getId());
        switch (buddy.getResult()) {
            case LOAD_FRIENDS:
                oPacket.EncodeInteger(buddy.getEntries().size());
                for (BuddylistEntry entry : buddy.getEntries()) {
                    buddyListEntry(oPacket, entry);
                }
                break;
            case NOTIFY_CHANGE_FRIEND_INFO: //update blocked friends
                oPacket.EncodeInteger(buddy.getEntry().getCharacterId()); //dwFriendID
                oPacket.EncodeInteger(buddy.getEntry().getAccountId()); //dwFriendAccountID
                buddyListEntry(oPacket, buddy.getEntry());
                break;
            case INVITE:
                BuddylistEntry entry = buddy.getEntry();
                oPacket.Encode(entry.isAccountFriend()); //bInShopOld (char id arrary bool of chars in the cs)
                oPacket.EncodeInteger(entry.getCharacterId()); //dwFriendID
                oPacket.EncodeInteger(entry.getAccountId());//dwFriendAccountID
                oPacket.EncodeString(entry.getName()); //sFriendName
                oPacket.EncodeInteger(buddy.getLevel()); //nLevel
                oPacket.EncodeInteger(buddy.getJob()); //nJobCode
                oPacket.EncodeInteger(buddy.getSubJob()); //nSubJob
                buddyListEntry(oPacket, entry);
                break;
            case SET_FRIEND_DONE:
                oPacket.encodeString("", false); //sMsg
                break;
            case SET_MESSENGER_MODE:
                oPacket.EncodeInteger(0); //m_nMessengerMode
                break;
            case SEND_SINGLE_FRIEND_INFO:
                buddyListEntry(oPacket, buddy.getEntry());
                break;
            case DELETE_FRIEND_DONE:
                oPacket.Encode(buddy.getEntry().isAccountFriend()); //account friend boolean 1= remove account friend
                int id = buddy.getEntry().isAccountFriend() ? buddy.getEntry().getAccountId() : buddy.getEntry().getCharacterId();
                oPacket.EncodeInteger(id); //account or friend id to remove
                break;
            case NOTIFY:
                BuddylistEntry ble = buddy.getEntry();
                oPacket.EncodeInteger(ble.getCharacterId());//dwFriendID
                oPacket.EncodeInteger(ble.getAccountId());//dwFriendAccountID
                oPacket.Encode(ble.isAccountFriend());//sFriendName (must be account friend)
                oPacket.EncodeInteger(ble.getChannel()); //m_channelID 
                oPacket.Encode(ble.isAccountFriend()); //sMsg  v93
                oPacket.Encode(ble.getCharacterId()); //dwFriendID
                if (ble.isAccountFriend()) {
                    oPacket.EncodeString(ble.getName());//sCharacterName
                }
                break;
            case CAPACITY:
                oPacket.Encode(buddy.getCapacity()); //v53->nFriendMax
                break;
            case NOTICE_DELETED:
                oPacket.EncodeString(""); //sTargetName
                break;
            case INVITE_EVENT_BESTFRIEND:
                oPacket.EncodeString(""); //dwFriendID
                break;
            case REFUSE_EVENT_BESTFRIEND:
                oPacket.EncodeInteger(0); //dwFriendID
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
        return oPacket.ToPacket();
    }

    /**
     * This is where the entry of each buddy is defined
     *
     * @param oPacket
     * @param BuddylistEntry
     */
    private static void buddyListEntry(OutPacket oPacket, BuddylistEntry ble) {
        oPacket.EncodeInteger(ble.getCharacterId());
        oPacket.EncodeString(ble.getName(), 13);
        oPacket.Encode(ble.getFlag());
        oPacket.EncodeInteger(ble.getChannel() - 1);
        oPacket.EncodeString(ble.getGroup(), 17);
        oPacket.Encode(0);//nMobile
        oPacket.EncodeInteger(ble.getAccountId());
        oPacket.EncodeString(ble.getNickname(), 13);
        oPacket.EncodeString(ble.getMemo(), 256);
        oPacket.EncodeInteger(0); //bInShop
    }

    public static Packet giveKilling(int x) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.TemporaryStatSet.getValue());
        //PacketHelper.writeSingleMask(oPacket, CharacterTemporaryStat.KILL_COUNT);
//        oPacket.encodeInteger(0);
//        oPacket.encode(0);
//        oPacket.encodeInteger(x);
//        oPacket.fill(0, 6);
        oPacket.EncodeShort(0);
        oPacket.Encode(0);
        oPacket.EncodeInteger(x);
        return oPacket.ToPacket();
    }

    public static class ExpeditionPacket {

        public static Packet expeditionStatus(MapleExpedition me, boolean created, boolean silent) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.ExpedtionResult.getValue());
            oPacket.Encode(created ? 86 : silent ? 72 : 76);//74
            oPacket.EncodeInteger(me.getType().exped);
            oPacket.EncodeInteger(0);
            for (int i = 0; i < 6; i++) {
                if (i < me.getParties().size()) {
                    MapleParty party = World.Party.getParty((me.getParties().get(i)).intValue());

                    CWvsContext.PartyPacket.addPartyStatus(-1, party, oPacket, false, true);
                } else {
                    CWvsContext.PartyPacket.addPartyStatus(-1, null, oPacket, false, true);
                }

            }

            return oPacket.ToPacket();
        }

        public static Packet expeditionError(int errcode, String name) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.ExpedtionResult.getValue());
            oPacket.Encode(100);//88
            oPacket.EncodeInteger(errcode);
            oPacket.EncodeString(name);

            return oPacket.ToPacket();
        }

        public static Packet expeditionMessage(int code) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.ExpedtionResult.getValue());
            oPacket.Encode(code);

            return oPacket.ToPacket();
        }

        public static Packet expeditionJoined(String name) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.ExpedtionResult.getValue());
            oPacket.Encode(87);//75
            oPacket.EncodeString(name);

            return oPacket.ToPacket();
        }

        public static Packet expeditionLeft(String name) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.ExpedtionResult.getValue());
            oPacket.Encode(91);//79
            oPacket.EncodeString(name);

            return oPacket.ToPacket();
        }

        public static Packet expeditionLeaderChanged(int newLeader) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.ExpedtionResult.getValue());
            oPacket.Encode(96);//84
            oPacket.EncodeInteger(newLeader);

            return oPacket.ToPacket();
        }

        public static Packet expeditionUpdate(int partyIndex, MapleParty party) {
            OutPacket oPacket = new OutPacket(80);
            oPacket.EncodeShort(SendPacketOpcode.ExpedtionResult.getValue());
            oPacket.Encode(97);//85
            oPacket.EncodeInteger(0);
            oPacket.EncodeInteger(partyIndex);

            CWvsContext.PartyPacket.addPartyStatus(-1, party, oPacket, false, true);

            return oPacket.ToPacket();
        }

        public static Packet expeditionInvite(MapleCharacter from, int exped) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.ExpedtionResult.getValue());
            oPacket.Encode(99);//87
            oPacket.EncodeInteger(from.getLevel());
            oPacket.EncodeInteger(from.getJob());
            oPacket.EncodeInteger(0);
            oPacket.EncodeString(from.getName());
            oPacket.EncodeInteger(exped);

            return oPacket.ToPacket();
        }
    }

    public static class PartyPacket {

        class PartyOperations {

            public static final int userUpdate = 0x10;
            public static final int createParty = 0x11;
            public static final int disbandParty = 0x16;
            public static final int joinParty = 0x19;
            public static final int leaderChange = 0x31;
            public static final int createPortal = 0x40;
            public static final int changePartySettings = 0x4E;
        }

        public static Packet changePartySettings(String newName, boolean isPrivate) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.PartyResult.getValue());
            oPacket.Encode(PartyOperations.changePartySettings);
            oPacket.Encode(isPrivate);
            oPacket.EncodeString(newName);

            return oPacket.ToPacket();
        }

        public static Packet partyCreated(MapleParty party) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.PartyResult.getValue());
            oPacket.Encode(PartyOperations.createParty);
            oPacket.EncodeInteger(party.getId());
            oPacket.EncodeInteger(party.getLeader().getDoorTown());
            oPacket.EncodeInteger(party.getLeader().getDoorTarget());
            oPacket.EncodeInteger(0); //nGrade
            oPacket.EncodeShort(party.getLeader().getDoorPosition().x);
            oPacket.EncodeShort(party.getLeader().getDoorPosition().y);
            oPacket.Encode((byte) party.getLeader().getId());
            oPacket.Encode(party.isPrivate());
            oPacket.Encode(0);
            oPacket.EncodeString(party.getName());

            return oPacket.ToPacket();
        }

        public static Packet partyInvite(MapleCharacter from) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.PartyResult.getValue());
            oPacket.Encode(4);
            oPacket.EncodeInteger(from.getParty() == null ? 0 : from.getParty().getId());
            oPacket.EncodeString(from.getName());
            oPacket.EncodeInteger(from.getLevel());
            oPacket.EncodeInteger(from.getJob());
            oPacket.EncodeInteger(0);
            oPacket.Encode(0);
            oPacket.Encode(0);
            return oPacket.ToPacket();
        }

        public static Packet partyRequestInvite(MapleCharacter from) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.PartyResult.getValue());
            oPacket.Encode(7);
            oPacket.EncodeInteger(from.getId());
            oPacket.EncodeString(from.getName());
            oPacket.EncodeInteger(from.getLevel());
            oPacket.EncodeInteger(from.getJob());

            return oPacket.ToPacket();
        }

        public static Packet partyStatusMessage(int message, String charname) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.PartyResult.getValue());
            oPacket.Encode(message);
            if ((message == 34) || (message == 56)) {
                oPacket.EncodeString(charname);
            } else if (message == 49) {
                oPacket.Encode(0);
            }

            return oPacket.ToPacket();
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
                oPacket.EncodeInteger(partychar.getId());
            }
            for (MaplePartyCharacter partychar : partymembers) {
                oPacket.EncodeString(partychar.getName(), 13);
            }
            for (MaplePartyCharacter partychar : partymembers) {
                oPacket.EncodeInteger(partychar.getJobId());
            }
            for (MaplePartyCharacter partychar : partymembers) { // this is subjob rofl
                oPacket.EncodeInteger(0);
            }
            for (MaplePartyCharacter partychar : partymembers) {
                oPacket.EncodeInteger(partychar.getLevel());
            }
            for (MaplePartyCharacter partychar : partymembers) {
                oPacket.EncodeInteger(partychar.isOnline() ? partychar.getChannel() - 1 : -2);
            }
            for (MaplePartyCharacter partychar : partymembers) { // bAccountShutdown
                oPacket.EncodeInteger(0);
            }
            for (MaplePartyCharacter partychar : partymembers) { // Unknown
                oPacket.EncodeInteger(0);
            }
            oPacket.EncodeInteger(party == null ? 0 : party.getLeader().getId());
            if (exped) {
                return;
            }
            for (MaplePartyCharacter partychar : partymembers) {
                oPacket.EncodeInteger(partychar.getChannel() == forchannel ? partychar.getMapid() : 999999999);
            }
            for (MaplePartyCharacter partychar : partymembers) {
                if (partychar.getChannel() == forchannel && !leaving) {
                    oPacket.EncodeInteger(partychar.getDoorTown());
                    oPacket.EncodeInteger(partychar.getDoorTarget());
                    oPacket.EncodeInteger(partychar.getDoorSkill());
                    oPacket.EncodeInteger(partychar.getDoorPosition().x);
                    oPacket.EncodeInteger(partychar.getDoorPosition().y);
                } else {
                    oPacket.EncodeInteger(leaving ? 999999999 : 0);
                    oPacket.EncodeLong(leaving ? 999999999 : 0);
                    oPacket.EncodeLong(leaving ? -1 : 0);
                }
            }
            oPacket.Encode(party.isPrivate());
            oPacket.Encode(false); // Unknown
            oPacket.EncodeString(party.getName());
        }

        public static Packet updateParty(int forChannel, MapleParty party, PartyOperation op, MaplePartyCharacter target) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.PartyResult.getValue());
            switch (op) {
                case DISBAND:
                case EXPEL:
                case LEAVE:
                    oPacket.Encode(PartyOperations.disbandParty);
                    oPacket.EncodeInteger(party.getId());
                    oPacket.EncodeInteger(target.getId());
                    oPacket.Encode(op == PartyOperation.DISBAND ? 0 : 1);
                    if (op == PartyOperation.DISBAND) {
                        break;
                    }
                    oPacket.Encode(op == PartyOperation.EXPEL ? 1 : 0);
                    oPacket.EncodeString(target.getName());
                    addPartyStatus(forChannel, party, oPacket, op == PartyOperation.LEAVE);
                    break;
                case JOIN:
                    oPacket.Encode(PartyOperations.joinParty);
                    oPacket.EncodeInteger(party.getId());
                    oPacket.EncodeString(target.getName());
                    oPacket.Encode(0);
                    oPacket.EncodeInteger(0);
                    addPartyStatus(forChannel, party, oPacket, false);
                    break;
                case SILENT_UPDATE:
                case LOG_ONOFF:
                    oPacket.Encode(PartyOperations.userUpdate);
                    oPacket.EncodeInteger(party.getId());
                    addPartyStatus(forChannel, party, oPacket, op == PartyOperation.LOG_ONOFF);
                    break;
                case CHANGE_LEADER:
                case CHANGE_LEADER_DC:
                    oPacket.Encode(PartyOperations.leaderChange);
                    oPacket.EncodeInteger(target.getId());
                    oPacket.Encode(op == PartyOperation.CHANGE_LEADER_DC ? 1 : 0);
                    break;
            }
            
            oPacket.Fill(0, 19);
            
            return oPacket.ToPacket();
        }

        public static Packet partyPortal(int townId, int targetId, int skillId, Point position, boolean animation) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.PartyResult.getValue());
            oPacket.Encode(PartyOperations.createPortal);
            oPacket.Encode(animation ? 0 : 1);
            oPacket.EncodeInteger(townId);
            oPacket.EncodeInteger(targetId);
            oPacket.EncodeInteger(skillId);
            oPacket.EncodePosition(position);

            return oPacket.ToPacket();
        }

        public static Packet getPartyListing(PartySearchType pst) {
            OutPacket oPacket = new OutPacket(80);
            oPacket.EncodeShort(SendPacketOpcode.PartyResult.getValue());
            oPacket.Encode(147);
            oPacket.EncodeInteger(pst.id);
            final List<PartySearch> parties = World.Party.searchParty(pst);
            oPacket.EncodeInteger(parties.size());
            for (PartySearch party : parties) {
                oPacket.EncodeInteger(0);
                oPacket.EncodeInteger(2);
                if (pst.exped) {
                    MapleExpedition me = World.Party.getExped(party.getId());
                    oPacket.EncodeInteger(me.getType().maxMembers);
                    oPacket.EncodeInteger(party.getId());
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
                    oPacket.EncodeInteger(0);
                    oPacket.EncodeInteger(party.getId());
                    oPacket.EncodeString(party.getName(), 48);
                    addPartyStatus(-1, World.Party.getParty(party.getId()), oPacket, false, true);
                }

                oPacket.EncodeShort(0);
            }

            return oPacket.ToPacket();
        }

        public static Packet partyListingAdded(PartySearch ps) {
            OutPacket oPacket = new OutPacket(80);
            oPacket.EncodeShort(SendPacketOpcode.PartyResult.getValue());
            oPacket.Encode(93);
            oPacket.EncodeInteger(ps.getType().id);
            oPacket.EncodeInteger(0);
            oPacket.EncodeInteger(1);
            if (ps.getType().exped) {
                MapleExpedition me = World.Party.getExped(ps.getId());
                oPacket.EncodeInteger(me.getType().maxMembers);
                oPacket.EncodeInteger(ps.getId());
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
                oPacket.EncodeInteger(0);
                oPacket.EncodeInteger(ps.getId());
                oPacket.EncodeString(ps.getName(), 48);
                addPartyStatus(-1, World.Party.getParty(ps.getId()), oPacket, false, true);
            }
            oPacket.EncodeShort(0);

            return oPacket.ToPacket();
        }

        public static Packet showMemberSearch(List<MapleCharacter> chr) {
            OutPacket oPacket = new OutPacket(80);
            oPacket.EncodeShort(SendPacketOpcode.PartyMemberCandidateResult.getValue());
            oPacket.Encode(chr.size());
            for (MapleCharacter c : chr) {
                oPacket.EncodeInteger(c.getId());
                oPacket.EncodeString(c.getName());
                oPacket.EncodeShort(c.getJob());
                oPacket.Encode(c.getLevel());
            }
            return oPacket.ToPacket();
        }

        public static Packet showPartySearch(List<MapleParty> chr) {
            OutPacket oPacket = new OutPacket(80);
            oPacket.EncodeShort(SendPacketOpcode.PartyCandidateResult.getValue());
            oPacket.Encode(chr.size());
            for (MapleParty c : chr) {
                oPacket.EncodeInteger(c.getId());
                oPacket.EncodeString(c.getLeader().getName());
                oPacket.Encode(c.getLeader().getLevel());
                oPacket.Encode(c.getLeader().isOnline() ? 1 : 0);
                oPacket.Encode(c.getMembers().size());
                for (MaplePartyCharacter ch : c.getMembers()) {
                    oPacket.EncodeInteger(ch.getId());
                    oPacket.EncodeString(ch.getName());
                    oPacket.EncodeShort(ch.getJobId());
                    oPacket.Encode(ch.getLevel());
                    oPacket.Encode(ch.isOnline() ? 1 : 0);
                }
            }
            return oPacket.ToPacket();
        }
    }

    public static class GuildPacket {

        public static class GuildResult {

            //[2016-09-15 08:50:09.528][Outbound] [018A] 04 0A 00 4B 61 7A 49 73 41 4E 75 62 62
            //[2016-09-15 08:50:09.614][Inbound ] [007F] 05 0A 00 4B 61 7A 49 73 41 4E 75 62 62
            public static final int SendGuildInvite = 7;
            public static final int GuildWaitingList = 0x2F;
            public static final int LoadGuild_Done = 48;
            public static final int FindGuild_Done = 49;
            public static final int CheckGuildName_Available = 50;
            public static final int CheckGuildName_AlreadyUsed = 51;
            public static final int CheckGuildName_Unknown = 52;
            public static final int CreateGuildAgree_Reply = 53;
            public static final int CreateGuildAgree_Unknown = 54;
            public static final int CreateNewGuild_Done = 55;
            public static final int CreateNewGuild_AlreayJoined = 56;
            public static final int CreateNewGuild_GuildNameAlreayExist = 57;
            public static final int CreateNewGuild_Beginner = 58;
            public static final int CreateNewGuild_Disagree = 59;
            public static final int CreateNewGuild_NotFullParty = 60;
            public static final int CreateNewGuild_Unknown = 61;
            public static final int JoinGuild_Done = 62;
            public static final int JoinGuild_AlreadyJoined = 63;
            public static final int JoinGuild_AlreadyFull = 64;
            public static final int JoinGuild_UnknownUser = 65;
            public static final int JoinGuild_NonRequestFindUser = 67;
            public static final int JoinGuild_Unknown = 68;
            public static final int JoinRequest_Done = 69;
            public static final int JoinRequest_DoneToUser = 70;
            public static final int JoinRequest_AlreadyFull = 0x47;
            public static final int JoinRequest_LimitTime = 0x48;
            public static final int JoinRequest_Unknown = 0x49;
            public static final int JoinCancelRequest_Done = 0x4A;
            public static final int WithdrawGuild_Done = 0x4B;
            public static final int WithdrawGuild_NotJoined = 0x4C;
            public static final int WithdrawGuild_Unknown = 0x4D;
            public static final int KickGuild_Done = 0x4E;
            public static final int KickGuild_NotJoined = 0x4F;
            public static final int KickGuild_Unknown = 0x4F;//NOT IN GMS v170 (so anything after is -1)
            public static final int RemoveGuild_Done = 0x51;//-1
            public static final int RemoveGuild_NotExist = 0x52;//-1
            public static final int RemoveGuild_Unknown = 0x53;//-1
            public static final int RemoveRequestGuild_Done = 0x54;//-1
            public static final int InviteGuild_BlockedUser = 0x55;//-1
            public static final int InviteGuild_AlreadyInvited = 0x56;//-1
            public static final int InviteGuild_Rejected = 0x57;//-1
            public static final int AdminCannotCreate = 0x58;//-1
            public static final int AdminCannotInvite = 0x59;//-1
            public static final int IncMaxMemberNum_Done = 0x5A;//-1
            public static final int IncMaxMemberNum_Unknown = 0x5B;//-1
            public static final int ChangeMemberName = 0x5C;//-1
            public static final int ChangeRequestUserName = 0x5D;//NOT IN GMS v170 (so anything after is -2)
            public static final int ChangeLevelOrJob = 0x5E;//-2
            public static final int NotifyLoginOrLogout = 0x5F;//-2 
            public static final int SetGradeName_Done = 0x60;//-2 
            public static final int SetGradeName_Unknown = 0x61;//-2
            public static final int SetMemberGrade_Done = 0x62;//-2
            public static final int SetMemberGrade_Unknown = 0x63;//-2
            public static final int SetMemberCommitment_Done = 0x64;//-2 
            public static final int SetMark_Done = 0x65;//-2 
            public static final int SetMark_Unknown = 0x66;//-2
            public static final int SetNotice_Done = 0x67;//-2
            public static final int InsertQuest = 0x68;//-2
            public static final int NoticeQuestWaitingOrder = 0x69;//-2
            public static final int SetGuildCanEnterQuest = 0x6A;//-2
            public static final int IncPoint_Done = 0x6B;//-2 
            public static final int ShowGuildRanking = 0x6C;//-2
            public static final int SetGGP_Done = 0x6D;//-2 
            public static final int SetIGP_Done = 0x6E;//-2
            public static final int GuildQuest_NotEnoughUser = 0x6F;//-2
            public static final int GuildQuest_RegisterDisconnected = 0x70;//-2
            public static final int GuildQuest_NoticeOrder = 0x71;//-2
            public static final int Authkey_Update = 0x72;//-2
            public static final int SetSkill_Done = 0x73;//-2
            public static final int SetSkill_Extend_Unknown = 0x74;//-2
            public static final int SetSkill_LevelSet_Unknown = 0x75;//-2
            public static final int SetSkill_ResetBattleSkill = 0x76;//-2
            public static final int UseSkill_Success = 0x77;//-2
            public static final int UseSkill_Err = 0x78;//-2
            public static final int ChangeName_Done = 0x79;//-2
            public static final int ChangeName_Unknown = 0x7A;//-2
            public static final int ChangeMaster_Done = 0x7B;//-2
            public static final int ChangeMaster_Unknown = 0x7C;//-2
            public static final int BlockedBehaviorCreate = 0x7D;//-2
            public static final int BlockedBehaviorJoin = 0x7E;//-2
            public static final int BattleSkillOpen = 0x7F;//-2
            public static final int GetData = 0x80;//-2
            public static final int Rank_Reflash = 0x81;//-2
            public static final int FindGuild_Error = 0x82;//-2
            public static final int ChangeMaster_Pinkbean = 0x83;//-2
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

        public static Packet guildInvite(int gid, String charName, int levelFrom, int jobFrom) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.GuildResult.getValue());
            oPacket.Encode(GuildResult.SendGuildInvite);//?
            oPacket.EncodeInteger(gid);
            oPacket.EncodeString(charName);
            oPacket.EncodeInteger(levelFrom);
            oPacket.EncodeInteger(jobFrom);
            oPacket.EncodeInteger(0);
            return oPacket.ToPacket();
        }

        public static Packet loadGuild_Done(MapleCharacter c) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.GuildResult.getValue());
            oPacket.Encode(GuildResult.LoadGuild_Done);//32
            if ((c == null) || (c.getMGC() == null)) {
                oPacket.Encode(0);
                return oPacket.ToPacket();
            }
            MapleGuild g = World.Guild.getGuild(c.getGuildId());
            if (g == null) {
                oPacket.Encode(0);
                return oPacket.ToPacket();
            }
            oPacket.Encode(0);//new 149
            oPacket.Encode(1);
            getGuildInfo(oPacket, g);
            oPacket.EncodeInteger(GameConstants.guildexp.length);//new 149
            for (int i : GameConstants.guildexp) {
                oPacket.EncodeInteger(i);
            }
            return oPacket.ToPacket();
        }

        public static Packet findGuild_Done(MapleCharacter c, int guildId) {
            OutPacket oPacket = new OutPacket(80);
            oPacket.EncodeShort(SendPacketOpcode.GuildResult.getValue());
            oPacket.Encode(GuildResult.FindGuild_Done);
            oPacket.EncodeInteger(guildId);
            getGuildInfo(oPacket, World.Guild.getGuild(guildId));
            return oPacket.ToPacket();
        }

        public static void getGuildInfo(OutPacket oPacket, MapleGuild guild) {
            oPacket.EncodeInteger(guild.getId());
            oPacket.EncodeString(guild.getName());
            for (int i = 1; i <= 5; i++) {
                oPacket.EncodeString(guild.getRankTitle(i));
            }
            guild.addMemberData(oPacket);
            guild.addPendingMemberData(oPacket);
            oPacket.EncodeInteger(guild.getCapacity());
            oPacket.EncodeShort(guild.getLogoBG());
            oPacket.Encode(guild.getLogoBGColor());
            oPacket.EncodeShort(guild.getLogo());
            oPacket.Encode(guild.getLogoColor());
            oPacket.EncodeString(guild.getNotice());
            oPacket.EncodeInteger(guild.getGP());//point
            oPacket.EncodeInteger(guild.getGP());//seasonpoint
            oPacket.EncodeInteger(guild.getAllianceId() > 0 ? guild.getAllianceId() : 0);
            oPacket.Encode(guild.getLevel());
            oPacket.EncodeShort(0);//guildRank
            oPacket.EncodeInteger(guild.getGP());//new 149 - GP
            oPacket.EncodeShort(guild.getSkills().size());
            for (MapleGuildSkill i : guild.getSkills()) {
                oPacket.EncodeInteger(i.skillID);
                oPacket.EncodeShort(i.level);
                oPacket.EncodeLong(PacketHelper.getTime(i.timestamp));
                oPacket.EncodeString(i.purchaser);
                oPacket.EncodeString(i.activator);
            }
            boolean hasJoinRequirements = false;
            oPacket.Encode(hasJoinRequirements);
            if (hasJoinRequirements) {
                oPacket.Encode(0);//nJoinSetting
                oPacket.EncodeInteger(0);//nReqLevel
            }
        }

        public static Packet createNewGuild(MapleCharacter c) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.GuildResult.getValue());
            oPacket.Encode(GuildResult.CreateNewGuild_Done);
            if ((c == null) || (c.getMGC() == null)) {
                return genericGuildMessage((byte) GuildResult.CheckGuildName_AlreadyUsed);
            }
            MapleGuild g = World.Guild.getGuild(c.getGuildId());
            if (g == null) {
                return genericGuildMessage((byte) GuildResult.CheckGuildName_AlreadyUsed);
            }
            getGuildInfo(oPacket, g);

            return oPacket.ToPacket();
        }

        public static void guildMemberEncode(OutPacket oPacket, MapleGuildCharacter mgc) {
            oPacket.EncodeString(mgc.getName(), 13);
            oPacket.EncodeInteger(mgc.getJobId());
            oPacket.EncodeInteger(mgc.getLevel());
            oPacket.EncodeInteger(mgc.getGuildRank());
            oPacket.EncodeInteger(mgc.isOnline() ? 1 : 0);
            oPacket.EncodeInteger(mgc.getAllianceRank());
            oPacket.EncodeInteger(mgc.getGuildContribution());//igp
            oPacket.EncodeInteger(mgc.getGuildContribution());//daily commitment
            oPacket.EncodeInteger(mgc.getGuildContribution());//nggp
            oPacket.EncodeLong(PacketHelper.getTime(-1L));//commitmentIncTime
        }

        public static Packet joinGuild(MapleGuildCharacter mgc) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.GuildResult.getValue());
            oPacket.Encode(GuildResult.JoinGuild_Done);
            oPacket.EncodeInteger(mgc.getGuildId());
            oPacket.EncodeInteger(mgc.getId());//shld be master charid
            guildMemberEncode(oPacket, mgc);

            return oPacket.ToPacket();
        }

        public static Packet disbandGuild(int guildId) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.GuildResult.getValue());
            oPacket.Encode(GuildResult.RemoveGuild_Done);
            oPacket.EncodeInteger(guildId);

            return oPacket.ToPacket();
        }

        public static Packet removeMember(MapleGuildCharacter mgc, boolean bExpelled) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.GuildResult.getValue());
            oPacket.Encode(GuildResult.RemoveGuild_Done);
            //oPacket.Encode(bExpelled ? GuildResult.KickGuild_Unknown : GuildResult.RemoveGuild_Done);
            oPacket.EncodeInteger(mgc.getGuildId());
            oPacket.EncodeInteger(mgc.getId());
            oPacket.EncodeString(mgc.getName());

            return oPacket.ToPacket();
        }

        public static Packet guildDisbanded(int gid) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.GuildResult.getValue());
            oPacket.Encode(GuildResult.RemoveGuild_NotExist);
            oPacket.EncodeInteger(gid);

            return oPacket.ToPacket();
        }

        public static Packet increaseMaxMemberNum(int gid, int capacity) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.GuildResult.getValue());
            oPacket.Encode(GuildResult.IncMaxMemberNum_Done);
            oPacket.EncodeInteger(gid);
            oPacket.Encode(capacity);

            return oPacket.ToPacket();
        }

        public static Packet setMemberCommitment(int gid, int cid, int c, int igp) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.GuildResult.getValue());
            oPacket.Encode(GuildResult.SetMemberCommitment_Done);
            oPacket.EncodeInteger(gid);
            oPacket.EncodeInteger(cid);//GuildMemberId (charid)
            oPacket.EncodeInteger(c);//nCommitment
            oPacket.EncodeInteger(c);//nDayCommitMent (based on chardata, stored in questData)
            oPacket.EncodeInteger(igp);//nIGP
            oPacket.EncodeLong(PacketHelper.getTime(System.currentTimeMillis()));

            return oPacket.ToPacket();
        }

        public static Packet setIGP(int gid, int cid, int igp) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.GuildResult.getValue());
            oPacket.Encode(GuildResult.SetIGP_Done);
            oPacket.EncodeInteger(gid);
            oPacket.EncodeInteger(igp);//nIGP
            oPacket.EncodeInteger(cid);//GuildMemberId (charid)

            return oPacket.ToPacket();
        }

        public static Packet setGP(int gid, int gp) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.GuildResult.getValue());
            oPacket.Encode(GuildResult.SetGGP_Done);
            oPacket.EncodeInteger(gid);
            oPacket.EncodeInteger(gp);//nIGP

            return oPacket.ToPacket();
        }

        public static Packet setMemberGrade(MapleGuildCharacter mgc) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.GuildResult.getValue());
            oPacket.Encode(GuildResult.SetMemberGrade_Done);//rank
            oPacket.EncodeInteger(mgc.getGuildId());
            oPacket.EncodeInteger(mgc.getId());
            oPacket.Encode(mgc.getGuildRank());

            return oPacket.ToPacket();
        }

        public static Packet setGradeName(int gid, String[] ranks) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.GuildResult.getValue());
            oPacket.Encode(GuildResult.SetGradeName_Done);
            oPacket.EncodeInteger(gid);
            for (String r : ranks) {
                oPacket.EncodeString(r);
            }

            return oPacket.ToPacket();
        }

        public static Packet setMark(int gid, short bg, byte bgcolor, short logo, byte logocolor) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.GuildResult.getValue());
            oPacket.Encode(GuildResult.SetMark_Done);
            oPacket.EncodeInteger(gid);
            oPacket.EncodeShort(bg);
            oPacket.Encode(bgcolor);
            oPacket.EncodeShort(logo);
            oPacket.Encode(logocolor);

            return oPacket.ToPacket();
        }

        public static Packet increaseGuildPoint(int gid, int GP, int glevel) {
            OutPacket oPacket = new OutPacket(80);
            //something here's wrong in my order. :/
            oPacket.EncodeShort(SendPacketOpcode.GuildResult.getValue());
            oPacket.Encode(GuildResult.IncPoint_Done);
            oPacket.EncodeInteger(gid);
            oPacket.EncodeInteger(GP);
            oPacket.EncodeInteger(glevel);
            oPacket.EncodeInteger(glevel);//somethingelse

            return oPacket.ToPacket();
        }

        public static Packet setNotice(int gid, String notice) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.GuildResult.getValue());
            oPacket.Encode(GuildResult.SetNotice_Done);
            oPacket.EncodeInteger(gid);
            oPacket.EncodeString(notice);

            return oPacket.ToPacket();
        }

        public static Packet guildMemberLevelJobUpdate(MapleGuildCharacter mgc) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.GuildResult.getValue());
            oPacket.Encode(GuildResult.ChangeLevelOrJob);
            oPacket.EncodeInteger(mgc.getGuildId());
            oPacket.EncodeInteger(mgc.getId());
            oPacket.EncodeInteger(mgc.getLevel());
            oPacket.EncodeInteger(mgc.getJobId());

            return oPacket.ToPacket();
        }

        public static Packet notifyLogin(int gid, int cid, boolean bOnline) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.GuildResult.getValue());
            oPacket.Encode(GuildResult.NotifyLoginOrLogout);
            oPacket.EncodeInteger(gid);
            oPacket.EncodeInteger(cid);
            oPacket.Encode(bOnline ? 1 : 0);
            oPacket.Encode(bOnline ? 1 : 0);//some extra bool, dunno what it does.

            return oPacket.ToPacket();
        }

        public static Packet showGuildRanking(int npcid, List<MapleGuildRanking.GuildRankingInfo> all) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.GuildResult.getValue());
            oPacket.Encode(GuildResult.ShowGuildRanking);
            oPacket.EncodeInteger(npcid);
            oPacket.EncodeInteger(all.size());
            for (MapleGuildRanking.GuildRankingInfo info : all) {
                oPacket.EncodeShort(0);//rank
                oPacket.EncodeString(info.getName());
                oPacket.EncodeInteger(info.getGP());
                oPacket.EncodeInteger(info.getLogo());
                oPacket.EncodeInteger(info.getLogoColor());
                oPacket.EncodeInteger(info.getLogoBg());
                oPacket.EncodeInteger(info.getLogoBgColor());
            }

            return oPacket.ToPacket();
        }

        public static Packet setSkill(int gid, int sid, int level, long expiration, String purchase, String activate) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.GuildResult.getValue());
            oPacket.Encode(GuildResult.SetSkill_Done);
            oPacket.EncodeInteger(gid);
            oPacket.EncodeInteger(sid);
            oPacket.EncodeShort(level);
            oPacket.EncodeLong(PacketHelper.getTime(expiration));
            oPacket.EncodeString(purchase);
            oPacket.EncodeString(activate);

            return oPacket.ToPacket();
        }

        public static Packet changeMaster(int gid, int oldLeader, int newLeader, int allianceId) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.GuildResult.getValue());
            oPacket.Encode(GuildResult.ChangeMaster_Done);
            oPacket.EncodeInteger(gid);
            oPacket.EncodeInteger(oldLeader);
            oPacket.EncodeInteger(newLeader);
            oPacket.Encode(1);
            oPacket.EncodeInteger(allianceId);

            return oPacket.ToPacket();
        }

        public static Packet rejectInvite(String charname) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.GuildResult.getValue());
            oPacket.Encode(GuildResult.InviteGuild_Rejected);
            oPacket.EncodeString(charname);

            return oPacket.ToPacket();
        }

        public static Packet guildSearchResult(MapleClient c, Map<Integer, MapleGuild> guilds) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.GuildSearchResult.getValue());
            oPacket.EncodeInteger(guilds.size());
            for (Entry<Integer, MapleGuild> gs : guilds.entrySet()) {
                MapleGuild guild = gs.getValue();
                int avgLevel = gs.getKey();
                int guildMaster = guild.getLeaderId();
                MapleCharacter master = c.loadCharacterById(guildMaster);
                java.util.Collection<MapleGuildCharacter> members = guild.getMembers();
                int guildSize = members.size();

                oPacket.EncodeInteger(guild.getId());
                oPacket.EncodeInteger(guild.getLevel());
                oPacket.EncodeString(guild.getName());
                oPacket.EncodeString(master.getName());
                oPacket.EncodeInteger(guildSize);
                oPacket.EncodeInteger(avgLevel);
            }
            return oPacket.ToPacket();
        }

        public static Packet joinGuildRequest(MapleGuild guild, MapleCharacter chr, int operation) {
            OutPacket oPacket = new OutPacket(80);
            oPacket.EncodeShort(SendPacketOpcode.GuildResult.getValue());
            switch (operation) {
                case GuildResult.JoinRequest_Done://is sent to the owner of the guild you request to join, the info is based on the joining char!
                    oPacket.Encode(GuildResult.JoinRequest_Done);
                    oPacket.EncodeInteger(guild.getId());//maybe swap these two
                    oPacket.EncodeInteger(chr.getId());
                    guildMemberEncode(oPacket, new MapleGuildCharacter(chr));
                    break;
                case GuildResult.JoinRequest_DoneToUser:
                    oPacket.Encode(GuildResult.JoinRequest_DoneToUser);
                    break;
                case GuildResult.JoinRequest_AlreadyFull:
                    oPacket.Encode(GuildResult.JoinRequest_AlreadyFull);
                    break;
                case GuildResult.JoinRequest_LimitTime:
                    oPacket.Encode(GuildResult.JoinRequest_LimitTime);
                    break;
                case GuildResult.JoinCancelRequest_Done://sent when you cancel your join-guild-request
                    oPacket.Encode(GuildResult.JoinCancelRequest_Done);
                    oPacket.EncodeInteger(1246849);
                    break;
                //48 = restriction period msg
                default:
                    oPacket.Encode(GuildResult.JoinRequest_Unknown);
                    break;
            }

            return oPacket.ToPacket();
        }

        public static Packet updateJoinRequestClientInfo(String guildname) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.Message.getValue());
            oPacket.Encode(0x0D);//UpdateQuestInfoEx
            oPacket.EncodeInteger(26015);//qId
            String formattedName = "";
            if (!guildname.isEmpty()) {
                formattedName = "name=" + guildname;
            }
            oPacket.EncodeString(formattedName.isEmpty() ? guildname : formattedName);
            if (guildname.isEmpty()) {
                oPacket.Encode(0);
            }

            return oPacket.ToPacket();
        }

        public static Packet genericGuildMessage(byte code) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.GuildResult.getValue());
            oPacket.Encode(code);//30 = cant find in ch
            if (code == 87) {
                oPacket.EncodeInteger(0);
            }
            if ((code == 59) || (code == 60) || (code == 61) || (code == 84) || (code == 87)) {
                oPacket.EncodeString("");
            }

            return oPacket.ToPacket();
        }

        //idk
        public static Packet BBSThreadList(List<MapleBBSThread> bbs, int start) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.GuildBBSResult.getValue());
            oPacket.Encode(6);
            if (bbs == null) {
                oPacket.Encode(0);
                oPacket.EncodeLong(0L);
                return oPacket.ToPacket();
            }
            int threadCount = bbs.size();
            MapleBBSThread notice = null;
            for (MapleBBSThread b : bbs) {
                if (b.isNotice()) {
                    notice = b;
                    break;
                }
            }
            oPacket.Encode(notice == null ? 0 : 1);
            if (notice != null) {
                addThread(oPacket, notice);
            }
            if (threadCount < start) {
                start = 0;
            }
            oPacket.EncodeInteger(threadCount);
            int pages = Math.min(10, threadCount - start);
            oPacket.EncodeInteger(pages);
            for (int i = 0; i < pages; i++) {
                addThread(oPacket, (MapleBBSThread) bbs.get(start + i));
            }

            return oPacket.ToPacket();
        }

        private static void addThread(OutPacket oPacket, MapleBBSThread rs) {
            oPacket.EncodeInteger(rs.localthreadID);
            oPacket.EncodeInteger(rs.ownerID);
            oPacket.EncodeString(rs.name);
            oPacket.EncodeLong(PacketHelper.getKoreanTimestamp(rs.timestamp));
            oPacket.EncodeInteger(rs.icon);
            oPacket.EncodeInteger(rs.getReplyCount());
        }

        public static Packet showThread(MapleBBSThread thread) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.GuildBBSResult.getValue());
            oPacket.Encode(7);
            oPacket.EncodeInteger(thread.localthreadID);
            oPacket.EncodeInteger(thread.ownerID);
            oPacket.EncodeLong(PacketHelper.getKoreanTimestamp(thread.timestamp));
            oPacket.EncodeString(thread.name);
            oPacket.EncodeString(thread.text);
            oPacket.EncodeInteger(thread.icon);
            oPacket.EncodeInteger(thread.getReplyCount());
            for (MapleBBSThread.MapleBBSReply reply : thread.replies.values()) {
                oPacket.EncodeInteger(reply.replyid);
                oPacket.EncodeInteger(reply.ownerID);
                oPacket.EncodeLong(PacketHelper.getKoreanTimestamp(reply.timestamp));
                oPacket.EncodeString(reply.content);
            }

            return oPacket.ToPacket();
        }

        public static Packet sendSetGuildNameMsg(MapleCharacter chr) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.UserGuildNameChanged.getValue());
            oPacket.EncodeInteger(chr.getId());
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

            return oPacket.ToPacket();
        }

        public static Packet sendSetGuildMarkMsg(MapleCharacter chr) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.UserGuildMarkChanged.getValue());
            oPacket.EncodeInteger(chr.getId());
            if (chr.getGuildId() <= 0) {
                oPacket.Fill(0, 6);
            } else {
                MapleGuild gs = World.Guild.getGuild(chr.getGuildId());
                if (gs != null) {
                    oPacket.EncodeShort(gs.getLogoBG());
                    oPacket.Encode(gs.getLogoBGColor());
                    oPacket.EncodeShort(gs.getLogo());
                    oPacket.Encode(gs.getLogoColor());
                } else {
                    oPacket.Fill(0, 6);
                }
            }

            return oPacket.ToPacket();
        }
    }

    public static class InfoPacket {

        public static Packet showMesoGain(long gain, boolean inChat) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.Message.getValue());
            if (!inChat) {
                oPacket.Encode(0);
                oPacket.Encode(1);
                oPacket.Encode(0);
                oPacket.EncodeLong(gain);
                oPacket.EncodeShort(0);
            } else {
                oPacket.Encode(7);
                oPacket.EncodeLong(gain);
                oPacket.EncodeInteger(-1);
            }

            return oPacket.ToPacket();
        }

        public static Packet getShowInventoryStatus(int mode) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.Message.getValue());
            oPacket.Encode(0);
            oPacket.Encode(mode);
            oPacket.EncodeInteger(0);
            oPacket.EncodeInteger(0);

            return oPacket.ToPacket();
        }

        public static Packet getShowItemGain(int itemId, short quantity) {
            return getShowItemGain(itemId, quantity, false);
        }

        public static Packet getShowItemGain(int itemId, short quantity, boolean inChat) {
            OutPacket oPacket = new OutPacket(80);

            if (inChat) {
                oPacket.EncodeShort(SendPacketOpcode.UserEffectRemote.getValue());
                oPacket.Encode(UserEffectCodes.Quest.getEffectId());
                oPacket.Encode(1);
                oPacket.EncodeInteger(itemId);
                oPacket.EncodeInteger(quantity);
            } else {
                oPacket.EncodeShort(SendPacketOpcode.Message.getValue());
                oPacket.EncodeShort(0);
                oPacket.EncodeInteger(itemId);
                oPacket.EncodeInteger(quantity);
            }

            return oPacket.ToPacket();
        }

        /**
         * Shows the name and image of the equipment dropped by the monster which the player have picked up at the center-top of the screen
         *
         * @param item
         * @return
         */
        public static Packet showEquipmentPickedUp(Equip item) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.UserEffectRemote.getValue());
            oPacket.Encode(UserEffectCodes.PickUpItem.getEffectId());
            PacketHelper.addItemInfo(oPacket, item);

            return oPacket.ToPacket();
        }

        public static Packet updateQuestMobKills(MapleQuestStatus status) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.Message.getValue());
            oPacket.Encode(1);
            oPacket.EncodeInteger(status.getQuest().getId()); // Version 174, this is an integer 
            oPacket.Encode(1);
            StringBuilder sb = new StringBuilder();
            for (Iterator<?> i$ = status.getMobKills().values().iterator(); i$.hasNext();) {
                int kills = (Integer) i$.next();
                sb.append(StringUtil.getLeftPaddedStr(String.valueOf(kills), '0', 3));
            }
            oPacket.EncodeString(sb.toString());
            oPacket.EncodeLong(0L);

            return oPacket.ToPacket();
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
        public static Packet gainExp(int nIncExp, boolean bIsLastHit, boolean bOnQuest, byte questBonusEXPRate, int burningFieldBonusEXPRate, EnumMap<ExpGainTypes, Integer> expIncreaseStats) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.Message.getValue());
            ExpIncreaseMessage message = new ExpIncreaseMessage(bIsLastHit, nIncExp, bOnQuest, questBonusEXPRate, burningFieldBonusEXPRate, expIncreaseStats);
            message.messagePacket(oPacket);

            return oPacket.ToPacket();
        }
    }

    public static Packet updateSpecialStat(MapleSpecialStatUpdateType stat, int requestType, int requestValue, int amount) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.ResultInstanceTable.getValue());
        oPacket.EncodeString(stat.getString());
        oPacket.EncodeInteger(requestType);
        oPacket.EncodeInteger(requestValue);
        oPacket.Encode(1);
        oPacket.EncodeInteger(amount);

        return oPacket.ToPacket();
    }

    public static Packet updateMaplePoint(int mp) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.SetMaplePoint.getValue());
        oPacket.EncodeInteger(mp);

        return oPacket.ToPacket();
    }

    public static Packet updateCrowns(int[] titles) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.EventNameTagInfo.getValue());
        for (int i = 0; i < 5; i++) {
            oPacket.EncodeString("");
            if (titles.length < i + 1) {
                oPacket.Encode(-1);
            } else {
                oPacket.Encode(titles[i]);
            }
        }

        return oPacket.ToPacket();
    }

    public static Packet magicWheel(int type, List<Integer> items, String data, int endSlot) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.MAGIC_WHEEL.getValue());
        oPacket.Encode(type);
        switch (type) {
            case 3:
                oPacket.Encode(items.size());
                for (int item : items) {
                    oPacket.EncodeInteger(item);
                }
                oPacket.EncodeString(data); // nexon encrypt the item and then send the string
                oPacket.Encode(endSlot);
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

        return oPacket.ToPacket();

    }

    public static class Reward {

        public static Packet receiveReward(int id, byte mode, int quantity) {
            System.out.println("[Debug] REWARD MODE: " + mode);
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.REWARD.getValue());
            oPacket.Encode(mode); // mode
            switch (mode) { // mode
                case 9:
                    oPacket.EncodeInteger(0);
                    break;
                case 0x0B:
                    oPacket.EncodeInteger(id);
                    oPacket.EncodeInteger(quantity); //quantity
                    //Popup: You have received the Maple Points.\r\n( %d maple point )
                    break;
                case 0x0C:
                    oPacket.EncodeInteger(id);
                    //Popup You have received the Game item.
                    break;
                case 0x0E:
                    oPacket.EncodeInteger(id);
                    oPacket.EncodeInteger(quantity); //quantity
                    //Popup: You have received the Mesos.\r\n( %d meso )
                    break;
                case 0x0F:
                    oPacket.EncodeInteger(id);
                    oPacket.EncodeInteger(quantity); //quantity
                    //Popup: You have received the Exp.\r\n( %d exp )
                    break;
                case 0x14:
                    //Popup: Failed to receive the Maple Points.
                    break;
                case 0x15:
                    oPacket.Encode(0);
                    //Popup: Failed to receive the Game Item.
                    break;
                case 0x16:
                    oPacket.Encode(0);
                    //Popup: Failed to receive the Game Item.
                    break;
                case 0x17:
                    //Popup: Failed to receive the Mesos.
                    break;
                case 0x18:
                    //Popup: Failed to receive the Exp.
                    break;
                case 0x21:
                    oPacket.Encode(0); //66
                    //No inventory space
                    break;
            }

            return oPacket.ToPacket();
        }

        public static Packet updateReward(int id, byte mode, List<MapleReward> rewards, int option) {
            OutPacket oPacket = new OutPacket(80);

            oPacket.EncodeShort(SendPacketOpcode.REWARD.getValue());
            oPacket.Encode(mode); // mode
            switch (mode) { // mode
                case 9:
                    oPacket.EncodeInteger(rewards.size());
                    if (rewards.size() > 0) {
                        for (int i = 0; i < rewards.size(); i++) {
                            MapleReward reward = rewards.get(i);
                            boolean empty = reward.getId() < 1;
                            oPacket.EncodeInteger(empty ? 0 : reward.getId()); // 0 = blank 1+ = gift
                            if (!empty) {
                                if ((option & 1) != 0) {
                                    oPacket.EncodeLong(reward.getReceiveDate()); //start time
                                    oPacket.EncodeLong(reward.getExpireDate()); //end time
                                    oPacket.EncodeLong(reward.getReceiveDate()); //start time
                                    oPacket.EncodeLong(reward.getExpireDate()); //end time
                                }
                                if ((option & 2) != 0) { //nexon do here a3 & 2 when a3 is 9
                                    oPacket.EncodeInteger(0);
                                    oPacket.EncodeInteger(0);
                                    oPacket.EncodeInteger(0);
                                    oPacket.EncodeInteger(0);
                                    oPacket.EncodeInteger(0);
                                    oPacket.EncodeInteger(0);
                                    oPacket.EncodeString("");
                                    oPacket.EncodeString("");
                                    oPacket.EncodeString("");
                                }
                                oPacket.EncodeInteger(reward.getType()); //type 3 = maple point 4 = mesos 5 = exp
                                oPacket.EncodeInteger(reward.getItem()); // item id
                                oPacket.EncodeInteger(/*itemQ*/reward.getItem() > 0 ? 1 : 0); // item quantity (?)
                                oPacket.EncodeInteger(0);
                                oPacket.EncodeLong(0L);
                                oPacket.EncodeInteger(0);
                                oPacket.EncodeInteger(reward.getMaplePoints()); // maple point amount
                                oPacket.EncodeInteger(reward.getMeso()); // mesos amount
                                oPacket.EncodeInteger(reward.getExp()); // exp amount
                                oPacket.EncodeInteger(0);
                                oPacket.EncodeInteger(0);
                                oPacket.EncodeString("");
                                oPacket.EncodeString("");
                                oPacket.EncodeString("");
                                oPacket.EncodeString(reward.getDesc());
                            }
                        }
                    }
                    break;
                case 0x0B:
                    oPacket.EncodeInteger(id);
                    oPacket.EncodeInteger(0); //quantity
                    //Popup: You have received the Maple Points.\r\n( %d maple point )
                    break;
                case 0x0C:
                    oPacket.EncodeInteger(id);
                    //Popup You have received the Game item.
                    break;
                case 0x0E:
                    oPacket.EncodeInteger(id);
                    oPacket.EncodeInteger(0); //quantity
                    //Popup: You have received the Mesos.\r\n( %d meso )
                    break;
                case 0x0F:
                    oPacket.EncodeInteger(id);
                    oPacket.EncodeInteger(0); //quantity
                    //Popup: You have received the Exp.\r\n( %d exp )
                    break;
                case 0x14:
                    //Popup: Failed to receive the Maple Points.
                    break;
                case 0x15:
                    oPacket.Encode(0);
                    //Popup: Failed to receive the Game Item.
                    break;
                case 0x16:
                    oPacket.Encode(0);
                    //Popup: Failed to receive the Game Item.
                    break;
                case 0x17:
                    //Popup: Failed to receive the Mesos.
                    break;
                case 0x18:
                    //Popup: Failed to receive the Exp.
                    break;
                case 0x21:
                    oPacket.Encode(0); //66
                    //No inventory space
                    break;
            }

            return oPacket.ToPacket();
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
    public static Packet enchantmentSystem(Enchant enchant) {
        OutPacket oPacket = new OutPacket(80);
        oPacket.EncodeShort(SendPacketOpcode.EquipmentEnchantDisplay.getValue());
        
        //final List<EquipSpecialStat> eqSpecialStats = EquipHelper.calculateEquipSpecialStatsForEncoding(enchant.getOldEquip());
        
        if (enchant.getAction() == enchant.getAction().STARFORCE) {
            oPacket.Encode(0);
        }
        
        oPacket.Encode(enchant.getAction().getAction());
        switch (enchant.getAction()) {
            case SCROLLLIST:
                oPacket.Encode(ServerConstants.FEVER_TIME);
                oPacket.Encode(enchant.getScrolls().size());
                for (EnchantmentScroll scroll : enchant.getScrolls()) {
                    oPacket.EncodeInteger(scroll.getType());
                    oPacket.EncodeString(scroll.getName());
                    oPacket.EncodeInteger(0);
                    oPacket.EncodeInteger(0);
                    oPacket.EncodeInteger(scroll.getMask());
                    for (Entry<EnchantmentStats, Integer> stats : scroll.getStats().entrySet()) {
                        oPacket.EncodeInteger(stats.getValue());
                    }
                    oPacket.EncodeInteger(scroll.getCost());
                    oPacket.Encode(scroll.willPass());
                }
                break;
            case FEVER_TIME:
                oPacket.Encode(enchant.isFeverTime()); //TSingleton<CWvsContext>::ms_pInstance.m_Data[2621].m_str if it != 0 (Fever Time)
                break;
            case STARFORCE:
                //oPacket.Encode(0);
                oPacket.Encode(enchant.canDowngrade()); //m_bDowngradable
                oPacket.EncodeLong(enchant.getCost()); //m_nMeso
                oPacket.EncodeInteger(enchant.getPerMille());//nPermille
                oPacket.EncodeInteger(enchant.getDestroyChance());//v2->m_bDestroyable
                oPacket.Encode(enchant.isChanceTime()); //v2->m_bChanceTime
                oPacket.EncodeInteger(enchant.getMask()); //mask
                for (Entry<EnchantmentStats, Short> star : enchant.getStarForce().entrySet()) {
                    oPacket.EncodeInteger(star.getValue());
                }
                break;
            case MINI_GAME:
                oPacket.Encode(enchant.getLevel()); //nLevel
                oPacket.EncodeInteger(enchant.getSeed()); //v2->m_nSeed
                break;
            case SCROLL_RESULT:
                oPacket.Encode(enchant.isFeverTime()); //bIsEffectOn (Fever Time)
                oPacket.EncodeInteger(enchant.hasPassed()); //pass
                oPacket.EncodeShort(0);//String desc?
                PacketHelper.addItemInfo(oPacket, enchant.getOldEquip());
                PacketHelper.addItemInfo(oPacket, enchant.getNewEquip());
                break;
            case STARFORCE_RESULT:
                oPacket.EncodeInteger(enchant.hasPassed());
                oPacket.Encode(0);
                PacketHelper.addItemInfo(oPacket, enchant.getOldEquip());
                PacketHelper.addItemInfo(oPacket, enchant.getNewEquip());
                break;
            case VESTIGE_COMPENSATION_RESULT:
                oPacket.EncodeInteger(0);
                break;
            case TRANSFER_HAMMER_RESULT:
                PacketHelper.addItemInfo(oPacket, enchant.getOldEquip());
                PacketHelper.addItemInfo(oPacket, enchant.getNewEquip());
                break;
            case UNKOWN_FAILURE_RESULT:
                oPacket.Encode(0);
                break;
            default:
                break;
        }
        
        oPacket.Fill(0, 19);
        
        return oPacket.ToPacket();
    }

    public static Packet OnVMatrixUpdate(List<VMatrixRecord> aVMatrixRecord, boolean bUpdate, int nUpdateType, int nPOS) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.VMatrixUpdate.getValue());
        oPacket.EncodeInteger(aVMatrixRecord.size());
        for (VMatrixRecord pMatrix : aVMatrixRecord) {
            pMatrix.Encode(oPacket);
        }
        oPacket.Encode(bUpdate);
        if (bUpdate) {
            oPacket.EncodeInteger(nUpdateType);
            if (nUpdateType == VMatrixRecord.Enable || nUpdateType == VMatrixRecord.Disable) {
                oPacket.EncodeInteger(nPOS);
            }
        }
        return oPacket.ToPacket();
    }

    public static Packet OnNodeStoneResult(int nCoreID, int nSkillID1, int nSkillID2, int nSkillID3) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.NodeStoneResult.getValue());
        oPacket.EncodeInteger(nCoreID);
        oPacket.EncodeInteger(1);
        oPacket.EncodeInteger(nSkillID1);
        oPacket.EncodeInteger(nSkillID2);
        oPacket.EncodeInteger(nSkillID3);
        return oPacket.ToPacket();
    }

    public static Packet OnNodeEnhanceResult(int nRecordID, int nEXP, int nSLV1, int nSLV2) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.NodeEnhanceResult.getValue());
        oPacket.EncodeInteger(nRecordID);
        oPacket.EncodeInteger(nEXP);
        oPacket.EncodeInteger(nSLV1);
        oPacket.EncodeInteger(nSLV2);
        return oPacket.ToPacket();
    }

    public static Packet OnNodeCraftResult(int nCoreID, int nSkillID1, int nSkillID2, int nSkillID3) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.NodeCraftResult.getValue());
        oPacket.EncodeInteger(nCoreID);
        oPacket.EncodeInteger(1);
        oPacket.EncodeInteger(nSkillID1);
        oPacket.EncodeInteger(nSkillID2);
        oPacket.EncodeInteger(nSkillID3);
        return oPacket.ToPacket();
    }

    public static Packet OnNodeShardResult(int nAmount) {
        OutPacket oPacket = new OutPacket(80);

        oPacket.EncodeShort(SendPacketOpcode.NodeShardResult.getValue());
        oPacket.EncodeInteger(nAmount);
        return oPacket.ToPacket();
    }
}
