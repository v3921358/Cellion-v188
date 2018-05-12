package handling.game;

import java.awt.Point;
import java.awt.Rectangle;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import client.MapleCharacterUtil;
import client.ClientSocket;
import client.QuestStatus.QuestState;
import client.MapleStat;
import client.PlayerStats;
import client.Skill;
import client.SkillEntry;
import client.SkillFactory;
import client.inventory.Equip;
import client.inventory.Item;
import client.inventory.ItemFlag;
import client.inventory.ItemType;
import client.inventory.MapleInventoryType;
import client.inventory.ModifyInventory;
import client.inventory.ModifyInventoryOperation;
import constants.GameConstants;

import constants.ItemConstants;
import constants.ServerConstants;
import static handling.game.UseRewardItemHandler.UseRewardItem;
import static handling.game.UseScrollsHandler.UseUpgradeScroll;
import static handling.game._CommonPlayerOperationHandler.UsePetFood;
import static handling.game._CommonPlayerOperationHandler.UseTeleRock;

import handling.world.World;
import service.ChannelServer;
import scripting.provider.NPCChatByType;
import scripting.provider.NPCChatType;
import server.MapleFamiliar;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.RandomRewards;
import server.Randomizer;
import server.events.MapleEvent;
import server.events.MapleEventType;
import server.life.LifeFactory;
import server.maps.FieldLimitType;
import server.maps.MapleMap;
import server.maps.MapleMapObjectType;
import server.maps.objects.User;
import server.maps.objects.Mist;
import server.maps.objects.Pet;
import server.maps.objects.MonsterFamiliar;
import server.quest.Quest;
import server.shops.ShopFactory;
import server.stores.HiredMerchant;
import net.InPacket;
import server.maps.objects.User.CharacterTemporaryValues;
import server.potentials.ItemPotentialProvider;
import server.potentials.ItemPotentialTierType;
import tools.LogHelper;
import tools.Pair;
import tools.packet.CField;
import tools.packet.CSPacket;
import tools.packet.WvsContext;
import tools.packet.MiracleCubePacket;
import tools.packet.PetPacket;
import net.ProcessPacket;
import server.NebuliteGrade;
import server.maps.objects.Pet.PetFlag;
import server.potentials.ItemPotentialOption;

/**
 *
 * @author
 */
public class UseCashItemHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        User pPlayer = c.getPlayer();

        if (pPlayer == null || pPlayer.getMap() == null || pPlayer.inPVP()) {
            c.SendPacket(WvsContext.enableActions());
            return;
        }
        pPlayer.setScrolledPosition((short) 0);

        pPlayer.updateTick(iPacket.DecodeInt());
        final byte slot = (byte) iPacket.DecodeShort();
        final int itemId = iPacket.DecodeInt();
        final Item toUse = pPlayer.getInventory(MapleInventoryType.CASH).getItem(slot);

        if (toUse == null || toUse.getItemId() != itemId || toUse.getQuantity() < 1 || pPlayer.hasBlockedInventory()) {
            c.SendPacket(WvsContext.enableActions());
            return;
        }

        boolean bUsed = false, cc = false;

        switch (itemId) {
            case ItemConstants.RED_CUBE: {
                //c.getPlayer().sortInventory((byte) 1); // Sort equipment inventory to avoid the cube not finding the item.
                final Item pItem = pPlayer.getInventory(MapleInventoryType.EQUIP).getItem((byte) iPacket.DecodeInt());
                final Equip pEquip = (Equip) pItem;

                if (pItem != null && pPlayer.getInventory(MapleInventoryType.USE).getNumFreeSlot() >= 1) {
                    int nTierUpRate = ItemPotentialProvider.RATE_REDCUBE_TIERUP;
                    int nTierDownRate = 0;
                    int nFragmentID = 0;
                    ItemPotentialTierType pMaxTier = ItemPotentialTierType.Legendary;
                    nFragmentID = ItemConstants.RED_CUBE_FRAGMENT;
                    ItemPotentialTierType lastTierBeforeCube = pEquip.getPotentialTier();
                    boolean bHidePotentialAfterReset = false;

                    // Check Tier
                    if (lastTierBeforeCube.getValue() > pMaxTier.getValue()) {
                        LogHelper.PACKET_EDIT_HACK.get().info(
                                String.format("[UseCashItemHandler] %s [ChrID: %d; AccId %d] has tried to use a red cube for %s tier on a %s tier equipment. EquipmentID = %d, CubeID = %d",
                                        pPlayer.getName(), pPlayer.getId(), c.getAccID(),
                                        pMaxTier.toString(),
                                        lastTierBeforeCube.toString(),
                                        pItem.getItemId(), toUse.getItemId())
                        );
                        c.Close();
                        return;
                    }

                    final float nMiracleRate = ServerConstants.MIRACLE_CUBE_RATE;
                    final boolean bRenewedPotential = ItemPotentialProvider.resetPotential(pEquip, nTierUpRate, nTierDownRate, pMaxTier, bHidePotentialAfterReset, nMiracleRate);

                    if (bRenewedPotential) {
                        bUsed = true; // flag as used to be removed.
                        if (nFragmentID != 0) {
                            MapleInventoryManipulator.addById(c, nFragmentID, (short) 1, "Cube on " + LocalDateTime.now());
                        }

                        // Update Inventory Equipment 
                        List<ModifyInventory> modifications = new ArrayList<>();
                        modifications.add(new ModifyInventory(ModifyInventoryOperation.AddItem, pItem));
                        c.SendPacket(WvsContext.inventoryOperation(true, modifications));

                        if (!bHidePotentialAfterReset) {
                            c.SendPacket(MiracleCubePacket.onRedCubeResult(pPlayer.getId(), lastTierBeforeCube != pEquip.getPotentialTier(), pEquip.getPosition(), toUse.getItemId(), pEquip));
                        }
                    } else {
                        pPlayer.dropMessage(5, "This item's Potential cannot be reset.");
                    }

                    pPlayer.getMap().broadcastMessage(CField.showPotentialReset(pPlayer.getId(), bRenewedPotential, pItem.getItemId()));
                    c.SendPacket(CField.enchantResult(bRenewedPotential ? 0 : 0));

                    if (pPlayer.isDeveloper()) {
                        pPlayer.yellowMessage("[Potential] First Line ID : " + pEquip.getPotential1());
                        pPlayer.yellowMessage("[Potential] Second Line ID :" + pEquip.getPotential2());
                        pPlayer.yellowMessage("[Potential] Third Line ID : " + pEquip.getPotential3());
                    }
                }
                break;
            }
            case ItemConstants.BONUS_POTENTIAL_CUBE: {
                //c.getPlayer().sortInventory((byte) 1); // Sort equipment inventory to avoid the cube not finding the item.
                final Item pItem = pPlayer.getInventory(MapleInventoryType.EQUIP).getItem((byte) iPacket.DecodeInt());
                final Equip pEquip = (Equip) pItem;

                if (pItem != null && pPlayer.getInventory(MapleInventoryType.USE).getNumFreeSlot() >= 1) {
                    int nTierUpRate = ItemPotentialProvider.RATE_REDCUBE_TIERUP;
                    int nTierDownRate = 0;
                    int nFragmentID = 0;
                    ItemPotentialTierType pMaxTier = ItemPotentialTierType.Legendary;
                    nFragmentID = ItemConstants.BONUS_POTENTIAL_CUBE_FRAGMENT;
                    ItemPotentialTierType lastTierBeforeCube = pEquip.getPotentialBonusTier();
                    boolean bHidePotentialAfterReset = false;

                    // Check Tier
                    if (lastTierBeforeCube.getValue() > pMaxTier.getValue()) {
                        LogHelper.PACKET_EDIT_HACK.get().info(
                                String.format("[UseCashItemHandler] %s [ChrID: %d; AccId %d] has tried to use a red cube for %s tier on a %s tier equipment. EquipmentID = %d, CubeID = %d",
                                        pPlayer.getName(), pPlayer.getId(), c.getAccID(),
                                        pMaxTier.toString(),
                                        lastTierBeforeCube.toString(),
                                        pItem.getItemId(), toUse.getItemId())
                        );
                        c.Close();
                        return;
                    }

                    final float nMiracleRate = ServerConstants.MIRACLE_CUBE_RATE;
                    final boolean bRenewedPotential = ItemPotentialProvider.resetBonusPotential(pEquip, nTierUpRate, nTierDownRate, pMaxTier, bHidePotentialAfterReset, nMiracleRate);

                    if (bRenewedPotential) {
                        bUsed = true; // Flag the item as used.
                        if (nFragmentID != 0) {
                            MapleInventoryManipulator.addById(c, nFragmentID, (short) 1, "Cube on " + LocalDateTime.now());
                        }

                        // Update Inventory Equipment 
                        List<ModifyInventory> modifications = new ArrayList<>();
                        modifications.add(new ModifyInventory(ModifyInventoryOperation.AddItem, pItem));
                        c.SendPacket(WvsContext.inventoryOperation(true, modifications));

                        if (!bHidePotentialAfterReset) {
                            c.SendPacket(MiracleCubePacket.onBonusCubeResult(pPlayer.getId(), lastTierBeforeCube != pEquip.getPotentialBonusTier(), pEquip.getPosition(), toUse.getItemId(), pEquip));
                        }
                    } else {
                        pPlayer.dropMessage(5, "This item's Bonus Potential cannot be reset.");
                    }

                    pPlayer.getMap().broadcastMessage(CField.showPotentialReset(pPlayer.getId(), bRenewedPotential, pItem.getItemId()));
                    c.SendPacket(CField.enchantResult(bRenewedPotential ? 0 : 0));

                    if (pPlayer.isDeveloper()) {
                        pPlayer.yellowMessage("[Bonus Potential] First Line ID : " + pEquip.getBonusPotential1());
                        pPlayer.yellowMessage("[Bonus Potential] Second Line ID : " + pEquip.getBonusPotential2());
                        pPlayer.yellowMessage("[Bonus Potential] Third Line ID : " + pEquip.getBonusPotential3());
                    }
                }
                break;
            }

            case 5080000:  // kites
            case 5080001:
            case 5080002:
            case 5080003: {
                final String kiteMessage = iPacket.DecodeString();

                final Point position = c.getPlayer().getMap().calcPointBelow(c.getPlayer().getPosition());

                if (position != null
                        && c.getPlayer().getMap().getMapObjectsInRange(c.getPlayer().getTruePosition(), 20000, Arrays.asList(MapleMapObjectType.KITE, MapleMapObjectType.NPC)).isEmpty()
                        && kiteMessage.length() < 200) { // hacker 
                    bUsed = true;
                    c.getPlayer().getMap().spawnMapleKite(itemId, c.getPlayer().getName(), c.getPlayer().getMap(), kiteMessage, (byte) 0, c.getPlayer().getPosition());
                } else {
                    c.SendPacket(CField.spawnKiteError());

                }
                break;
            }
            case 5043001: // NPC Teleport Rock
            case 5043000: { // NPC Teleport Rock
                final short questid = iPacket.DecodeShort();
                final int npcid = iPacket.DecodeInt();
                final Quest quest = Quest.getInstance(questid);

                if (c.getPlayer().getQuest(quest).getStatus() == QuestState.Started && quest.canComplete(c.getPlayer(), npcid)) {
                    final int mapId = LifeFactory.getNPCLocation(npcid);
                    if (mapId != -1) {
                        final MapleMap map = c.getChannelServer().getMapFactory().getMap(mapId);
                        if (map.containsNPC(npcid) && !FieldLimitType.VipRock.checkFlag(c.getPlayer().getMap()) && !FieldLimitType.VipRock.checkFlag(map) && !c.getPlayer().isInBlockedMap()) {
                            c.getPlayer().changeMap(map, map.getPortal(0));
                        }
                        bUsed = true;
                    } else {
                        c.getPlayer().dropMessage(1, "Unknown error has occurred.");
                    }
                }
                break;
            }
            case 5041001:
            case 5040004:
            case 5040003:
            case 5040002:
            case 2320000: // The Teleport Rock
            case 5041000: // VIP Teleport Rock
            case 5040000: // The Teleport Rock
            case 5040001: { // Teleport Coke
                bUsed = UseTeleRock(iPacket, c, itemId);
                c.getPlayer().gainItem(itemId, 1); // Make them permanent by giving them the item again, ultra meme. -Mazen
                break;
            }
            case 5450005: {
                c.getPlayer().setConversation(User.MapleCharacterConversationType.Storage);
                c.getPlayer().getStorage().sendStorage(c, 1022005);
                break;
            }
            case 5050000: { // AP Reset
                Map<MapleStat, Long> statupdate = new EnumMap<>(MapleStat.class);
                final int apto = (int) iPacket.DecodeLong();
                final int apfrom = (int) iPacket.DecodeLong();

                if (apto == apfrom) {
                    break; // Hack
                }
                final int job = c.getPlayer().getJob();
                final PlayerStats playerst = c.getPlayer().getStat();
                bUsed = true;

                switch (apto) { // AP to
                    case 0x40: // str
                        if (playerst.getStr() >= 999) {
                            bUsed = false;
                        }
                        break;
                    case 0x80: // dex
                        if (playerst.getDex() >= 999) {
                            bUsed = false;
                        }
                        break;
                    case 0x100: // int
                        if (playerst.getInt() >= 999) {
                            bUsed = false;
                        }
                        break;
                    case 0x200: // luk
                        if (playerst.getLuk() >= 999) {
                            bUsed = false;
                        }
                        break;
                    case 0x800: // hp
                        if (playerst.getMaxHp() >= 500000) {
                            bUsed = false;
                        }
                        break;
                    case 0x2000: // mp
                        if (playerst.getMaxMp() >= 500000) {
                            bUsed = false;
                        }
                        break;
                }
                switch (apfrom) { // AP to
                    case 0x40: // str
                        if (playerst.getStr() <= 4 || (c.getPlayer().getJob() % 1000 / 100 == 1 && playerst.getStr() <= 35)) {
                            bUsed = false;
                        }
                        break;
                    case 0x80: // dex
                        if (playerst.getDex() <= 4 || (c.getPlayer().getJob() % 1000 / 100 == 3 && playerst.getDex() <= 25) || (c.getPlayer().getJob() % 1000 / 100 == 4 && playerst.getDex() <= 25) || (c.getPlayer().getJob() % 1000 / 100 == 5 && playerst.getDex() <= 20)) {
                            bUsed = false;
                        }
                        break;
                    case 0x100: // int
                        if (playerst.getInt() <= 4 || (c.getPlayer().getJob() % 1000 / 100 == 2 && playerst.getInt() <= 20)) {
                            bUsed = false;
                        }
                        break;
                    case 0x200: // luk
                        if (playerst.getLuk() <= 4) {
                            bUsed = false;
                        }
                        break;
                    case 0x800: // hp
                        if (/*playerst.getMaxMp() < ((c.getPlayer().getLevel() * 14) + 134) || */c.getPlayer().getHpApUsed() <= 0 || c.getPlayer().getHpApUsed() >= 10000) {
                            bUsed = false;
                            c.getPlayer().dropMessage(1, "You need points in HP or MP in order to take points out.");
                        }
                        break;
                    case 0x2000: // mp
                        if (/*playerst.getMaxMp() < ((c.getPlayer().getLevel() * 14) + 134) || */c.getPlayer().getHpApUsed() <= 0 || c.getPlayer().getHpApUsed() >= 10000) {
                            bUsed = false;
                            c.getPlayer().dropMessage(1, "You need points in HP or MP in order to take points out.");
                        }
                        break;
                }
                if (bUsed) {
                    switch (apto) { // AP to
                        case 0x40: { // str
                            final long toSet = playerst.getStr() + 1;
                            playerst.setStr((short) toSet, c.getPlayer());
                            statupdate.put(MapleStat.STR, toSet);
                            break;
                        }
                        case 0x80: { // dex
                            final long toSet = playerst.getDex() + 1;
                            playerst.setDex((short) toSet, c.getPlayer());
                            statupdate.put(MapleStat.DEX, toSet);
                            break;
                        }
                        case 0x100: { // int
                            final long toSet = playerst.getInt() + 1;
                            playerst.setInt((short) toSet, c.getPlayer());
                            statupdate.put(MapleStat.INT, toSet);
                            break;
                        }
                        case 0x200: { // luk
                            final long toSet = playerst.getLuk() + 1;
                            playerst.setLuk((short) toSet, c.getPlayer());
                            statupdate.put(MapleStat.LUK, toSet);
                            break;
                        }
                        case 0x800: // hp
                            int maxhp = playerst.getMaxHp();
                            maxhp += GameConstants.getHpApByJob((short) job);
                            c.getPlayer().setHpApUsed((short) (c.getPlayer().getHpApUsed() + 1));
                            playerst.setMaxHp(maxhp, c.getPlayer());
                            statupdate.put(MapleStat.MAXHP, (long) maxhp);
                            break;

                        case 0x2000: // mp
                            int maxmp = playerst.getMaxMp();
                            if (GameConstants.isDemonSlayer(job) || GameConstants.isAngelicBuster(job) || GameConstants.isDemonAvenger(job)) {
                                break;
                            }
                            maxmp += GameConstants.getMpApByJob((short) job);
                            maxmp = Math.min(500000, Math.abs(maxmp));
                            c.getPlayer().setHpApUsed((short) (c.getPlayer().getHpApUsed() + 1));
                            playerst.setMaxMp(maxmp, c.getPlayer());
                            statupdate.put(MapleStat.IndieMMP, (long) maxmp);
                            break;
                    }
                    switch (apfrom) { // AP from
                        case 0x40: { // str
                            final long toSet = playerst.getStr() - 1;
                            playerst.setStr((short) toSet, c.getPlayer());
                            statupdate.put(MapleStat.STR, toSet);
                            break;
                        }
                        case 0x80: { // dex
                            final long toSet = playerst.getDex() - 1;
                            playerst.setDex((short) toSet, c.getPlayer());
                            statupdate.put(MapleStat.DEX, toSet);
                            break;
                        }
                        case 0x100: { // int
                            final long toSet = playerst.getInt() - 1;
                            playerst.setInt((short) toSet, c.getPlayer());
                            statupdate.put(MapleStat.INT, toSet);
                            break;
                        }
                        case 0x200: { // luk
                            final long toSet = playerst.getLuk() - 1;
                            playerst.setLuk((short) toSet, c.getPlayer());
                            statupdate.put(MapleStat.LUK, toSet);
                            break;
                        }
                        case 0x800: // HP
                            int maxhp = playerst.getMaxHp();
                            maxhp -= GameConstants.getHpApByJob((short) job);
                            c.getPlayer().setHpApUsed((short) (c.getPlayer().getHpApUsed() - 1));
                            playerst.setMaxHp(maxhp, c.getPlayer());
                            statupdate.put(MapleStat.MAXHP, (long) maxhp);
                            break;
                        case 0x2000: // MP
                            int maxmp = playerst.getMaxMp();
                            if (GameConstants.isDemonSlayer(job) || GameConstants.isAngelicBuster(job) || GameConstants.isDemonAvenger(job)) {
                                break;
                            }
                            maxmp -= GameConstants.getMpApByJob((short) job);
                            c.getPlayer().setHpApUsed((short) (c.getPlayer().getHpApUsed() - 1));
                            playerst.setMaxMp(maxmp, c.getPlayer());
                            statupdate.put(MapleStat.IndieMMP, (long) maxmp);
                            break;
                    }
                    c.SendPacket(WvsContext.updatePlayerStats(statupdate, true, c.getPlayer()));
                }
                break;
            }
            //case 5051001: {
            //    
            //    break;
            //}
            case 5220083: {//starter pack
                bUsed = true;
                for (Map.Entry<Integer, MapleFamiliar> f : MapleItemInformationProvider.getInstance().getFamiliars().entrySet()) {
                    if (f.getValue().getItemid() == 2870055 || f.getValue().getItemid() == 2871002 || f.getValue().getItemid() == 2870235 || f.getValue().getItemid() == 2870019) {
                        MonsterFamiliar mf = c.getPlayer().getFamiliars().get(f.getKey());
                        if (mf != null) {
                            if (mf.getVitality() >= 3) {
                                mf.setExpiry(Math.min(System.currentTimeMillis() + 90 * 24 * 60 * 60000L, mf.getExpiry() + 30 * 24 * 60 * 60000L));
                            } else {
                                mf.setVitality(mf.getVitality() + 1);
                                mf.setExpiry(mf.getExpiry() + 30 * 24 * 60 * 60000L);
                            }
                        } else {
                            mf = new MonsterFamiliar(c.getPlayer().getId(), f.getKey(), System.currentTimeMillis() + 30 * 24 * 60 * 60000L);
                            c.getPlayer().getFamiliars().put(f.getKey(), mf);
                        }
                        c.SendPacket(CField.registerFamiliar(mf));
                    }
                }
                break;
            }
            case 5220084: {//booster pack
                if (c.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() < 3) {
                    c.getPlayer().dropMessage(5, "Make 3 USE space.");
                    break;
                }
                bUsed = true;
                int[] familiars = new int[3];
                while (true) {
                    for (int i = 0; i < familiars.length; i++) {
                        if (familiars[i] > 0) {
                            continue;
                        }
                        for (Map.Entry<Integer, MapleFamiliar> f : MapleItemInformationProvider.getInstance().getFamiliars().entrySet()) {
                            if (Randomizer.nextInt(500) == 0 && ((i < 2 && f.getValue().getGrade() == 0 || (i == 2 && f.getValue().getGrade() != 0)))) {
                                MapleInventoryManipulator.addById(c, f.getValue().getItemid(), (short) 1, "Booster Pack");
                                //c.write(CField.getBoosterFamiliar(c.getPlayer().getId(), f.getKey(), 0));
                                familiars[i] = f.getValue().getItemid();
                                break;
                            }
                        }
                    }
                    if (familiars[0] > 0 && familiars[1] > 0 && familiars[2] > 0) {
                        break;
                    }
                }
                c.SendPacket(CSPacket.getBoosterPack(familiars[0], familiars[1], familiars[2]));
                c.SendPacket(CSPacket.getBoosterPackClick());
                c.SendPacket(CSPacket.getBoosterPackReveal());
                break;
            }
            case 5050001: // SP Reset (1st job)
            case 5050002: // SP Reset (2nd job)
            case 5050003: // SP Reset (3rd job)
            case 5050004:  // SP Reset (4th job)
            case 5050005: //evan sp resets
            case 5050006:
            case 5050007:
            case 5050008:
            case 5050009: {
                if (itemId >= 5050005 && !GameConstants.isEvan(c.getPlayer().getJob())) {
                    c.getPlayer().dropMessage(1, "This reset is only for Evans.");
                    break;
                } //well i dont really care other than this o.o
                if (itemId < 5050005 && GameConstants.isEvan(c.getPlayer().getJob())) {
                    c.getPlayer().dropMessage(1, "This reset is only for non-Evans.");
                    break;
                } //well i dont really care other than this o.o
                int skill1 = iPacket.DecodeInt();
                int skill2 = iPacket.DecodeInt();
                for (int i : GameConstants.blockedSkills) {
                    if (skill1 == i) {
                        c.getPlayer().dropMessage(1, "You may not add this skill.");
                        return;
                    }
                }

                Skill skillSPTo = SkillFactory.getSkill(skill1);
                Skill skillSPFrom = SkillFactory.getSkill(skill2);

                if (skillSPTo.isBeginnerSkill() || skillSPFrom.isBeginnerSkill()) {
                    c.getPlayer().dropMessage(1, "You may not add beginner skills.");
                    break;
                }
                if (GameConstants.getSkillBookForSkill(skill1) != GameConstants.getSkillBookForSkill(skill2)) { //resistance evan
                    c.getPlayer().dropMessage(1, "You may not add different job skills.");
                    break;
                }
                //if (GameConstants.getJobNumber(skill1 / 10000) > GameConstants.getJobNumber(skill2 / 10000)) { //putting 3rd job skillpoints into 4th job for example
                //    c.getPlayer().dropMessage(1, "You may not add skillpoints to a higher job.");
                //    break;
                //}
                if ((c.getPlayer().getSkillLevel(skillSPTo) + 1 <= skillSPTo.getMaxLevel()) && c.getPlayer().getSkillLevel(skillSPFrom) > 0 && skillSPTo.canBeLearnedBy(c.getPlayer().getJob())) {
                    if (skillSPTo.isFourthJob() && (c.getPlayer().getSkillLevel(skillSPTo) + 1 > c.getPlayer().getMasterLevel(skillSPTo))) {
                        c.getPlayer().dropMessage(1, "You will exceed the master level.");
                        break;
                    }
                    if (itemId >= 5050005) {
                        if (GameConstants.getSkillBookForSkill(skill1) != (itemId - 5050005) * 2 && GameConstants.getSkillBookForSkill(skill1) != (itemId - 5050005) * 2 + 1) {
                            c.getPlayer().dropMessage(1, "You may not add this job SP using this reset.");
                            break;
                        }
                    } else {
                        int theJob = GameConstants.getJobNumber(skill2 / 10000);
                        switch (skill2 / 10000) {
                            case 430:
                                theJob = 1;
                                break;
                            case 432:
                            case 431:
                                theJob = 2;
                                break;
                            case 433:
                                theJob = 3;
                                break;
                            case 434:
                                theJob = 4;
                                break;
                        }
                        if (theJob != itemId - 5050000) { //you may only subtract from the skill if the ID matches Sp reset
                            c.getPlayer().dropMessage(1, "You may not subtract from this skill. Use the appropriate SP reset.");
                            break;
                        }
                    }
                    final Map<Skill, SkillEntry> sa = new HashMap<>();
                    sa.put(skillSPFrom, new SkillEntry((byte) (c.getPlayer().getSkillLevel(skillSPFrom) - 1), c.getPlayer().getMasterLevel(skillSPFrom), SkillFactory.getDefaultSExpiry(skillSPFrom)));
                    sa.put(skillSPTo, new SkillEntry((byte) (c.getPlayer().getSkillLevel(skillSPTo) + 1), c.getPlayer().getMasterLevel(skillSPTo), SkillFactory.getDefaultSExpiry(skillSPTo)));
                    c.getPlayer().changeSkillsLevel(sa);
                    bUsed = true;
                }
                break;
            }
            case 5500000: { // Magic Hourglass 1 day
                final Item item = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(iPacket.DecodeShort());
                final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                final int days = 1;
                if (item != null && !GameConstants.isAccessory(item.getItemId()) && item.getExpiration() > -1 && !ii.isCash(item.getItemId()) && System.currentTimeMillis() + (100 * 24 * 60 * 60 * 1000L) > item.getExpiration() + (days * 24 * 60 * 60 * 1000L)) {
                    item.setExpiration(item.getExpiration() + (days * 24 * 60 * 60 * 1000));
                    c.getPlayer().forceReAddItem(item, MapleInventoryType.EQUIPPED);
                    bUsed = true;
                }
                break;
            }
            case 5500001: { // Magic Hourglass 7 day
                final Item item = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(iPacket.DecodeShort());
                final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                final int days = 7;
                if (item != null && !GameConstants.isAccessory(item.getItemId()) && item.getExpiration() > -1 && !ii.isCash(item.getItemId()) && System.currentTimeMillis() + (100 * 24 * 60 * 60 * 1000L) > item.getExpiration() + (days * 24 * 60 * 60 * 1000L)) {
                    item.setExpiration(item.getExpiration() + (days * 24 * 60 * 60 * 1000));
                    c.getPlayer().forceReAddItem(item, MapleInventoryType.EQUIPPED);
                    bUsed = true;
                }
                break;
            }
            case 5500002: { // Magic Hourglass 20 day
                final Item item = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(iPacket.DecodeShort());
                final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                final int days = 20;
                if (item != null && !GameConstants.isAccessory(item.getItemId()) && item.getExpiration() > -1 && !ii.isCash(item.getItemId()) && System.currentTimeMillis() + (100 * 24 * 60 * 60 * 1000L) > item.getExpiration() + (days * 24 * 60 * 60 * 1000L)) {
                    item.setExpiration(item.getExpiration() + (days * 24 * 60 * 60 * 1000));
                    c.getPlayer().forceReAddItem(item, MapleInventoryType.EQUIPPED);
                    bUsed = true;
                }
                break;
            }
            case 5500005: { // Magic Hourglass 50 day
                final Item item = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(iPacket.DecodeShort());
                final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                final int days = 50;
                if (item != null && !GameConstants.isAccessory(item.getItemId()) && item.getExpiration() > -1 && !ii.isCash(item.getItemId()) && System.currentTimeMillis() + (100 * 24 * 60 * 60 * 1000L) > item.getExpiration() + (days * 24 * 60 * 60 * 1000L)) {
                    item.setExpiration(item.getExpiration() + (days * 24 * 60 * 60 * 1000));
                    c.getPlayer().forceReAddItem(item, MapleInventoryType.EQUIPPED);
                    bUsed = true;
                }
                break;
            }
            case 5500006: { // Magic Hourglass 99 day
                final Item item = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(iPacket.DecodeShort());
                final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                final int days = 99;
                if (item != null && !GameConstants.isAccessory(item.getItemId()) && item.getExpiration() > -1 && !ii.isCash(item.getItemId()) && System.currentTimeMillis() + (100 * 24 * 60 * 60 * 1000L) > item.getExpiration() + (days * 24 * 60 * 60 * 1000L)) {
                    item.setExpiration(item.getExpiration() + (days * 24 * 60 * 60 * 1000));
                    c.getPlayer().forceReAddItem(item, MapleInventoryType.EQUIPPED);
                    bUsed = true;
                }
                break;
            }
            case 5060000: { // Item Tag
                final Item item = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem(iPacket.DecodeShort());

                if (item != null && item.getOwner().equals("")) {
                    item.setOwner(c.getPlayer().getName());
                    c.getPlayer().forceReAddItem(item, MapleInventoryType.EQUIPPED);
                    bUsed = true;
                }
                break;
            }
            case 5680015: {
                if (c.getPlayer().getFatigue() > 0) {
                    c.getPlayer().setFatigue(0);
                    bUsed = true;
                }
                break;
            }  
            case ItemConstants.SUPER_MIRACLE_CUBE: {
                //c.getPlayer().sortInventory((byte) 1); // Sort equipment inventory to avoid the cube not finding the item.

                final Item item = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((byte) iPacket.DecodeInt());
                final Equip equip = (Equip) item;

                if (item != null && c.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() >= 1) {
                    int tierUpRate = ItemPotentialProvider.RATE_REDCUBE_TIERUP;
                    int tierDownRate = 0;
                    int fragmentGiven = 0;
                    ItemPotentialTierType maxTier = ItemPotentialTierType.Unique;
                    boolean hidePotentialAfterReset = false;

                    switch (toUse.getItemId()) {
                        case ItemConstants.SUPER_MIRACLE_CUBE:
                            maxTier = ItemPotentialTierType.Legendary;
                            tierUpRate = ItemPotentialProvider.RATE_SUPER_MIRACLECUBE_TIERUP;
                            fragmentGiven = ItemConstants.SUPER_MIRACLE_CUBE_FRAGMENT;
                            hidePotentialAfterReset = true;
                            break;
                        case ItemConstants.RED_CUBE:
                            maxTier = ItemPotentialTierType.Legendary;
                            fragmentGiven = ItemConstants.RED_CUBE_FRAGMENT;
                            break;
                    }
                    ItemPotentialTierType lastTierBeforeCube = equip.getPotentialTier();

                    // Check Tier
                    if (lastTierBeforeCube.getValue() > maxTier.getValue()) {
                        LogHelper.PACKET_EDIT_HACK.get().info(
                                String.format("[UseCashItemHandler] %s [ChrID: %d; AccId %d] has tried to use a red cube for %s tier on a %s tier equipment. EquipmentID = %d, CubeID = %d",
                                        pPlayer.getName(), pPlayer.getId(), c.getAccID(),
                                        maxTier.toString(),
                                        lastTierBeforeCube.toString(),
                                        item.getItemId(), toUse.getItemId())
                        );
                        c.Close();
                        return;
                    }

                    //float miracleRate = c.getChannelServer().getMiracleCubeRate(c.getWorld()); (null lel)
                    final float miracleRate = ServerConstants.MIRACLE_CUBE_RATE;
                    final boolean renewedPotential = ItemPotentialProvider.resetPotential(equip, tierUpRate, tierDownRate, maxTier, hidePotentialAfterReset, miracleRate);
                    if (renewedPotential) {
                        bUsed = true; // flag as used to be removed.
                        if (fragmentGiven != 0) {
                            MapleInventoryManipulator.addById(c, fragmentGiven, (short) 1, "Cube on " + LocalDateTime.now());
                        }
                        // Update inventory equipment 
                        List<ModifyInventory> modifications = new ArrayList<>();
                        modifications.add(new ModifyInventory(ModifyInventoryOperation.AddItem, item));
                        c.SendPacket(WvsContext.inventoryOperation(true, modifications));

                        if (!hidePotentialAfterReset) {
                            c.SendPacket(MiracleCubePacket.onRedCubeResult(pPlayer.getId(), lastTierBeforeCube != equip.getPotentialTier(), equip.getPosition(), toUse.getItemId(), equip));
                        }
                    } else {
                        pPlayer.dropMessage(5, "This item's Potential cannot be reset.");
                    }

                    // Show to map
                    pPlayer.getMap().broadcastMessage(CField.showPotentialReset(pPlayer.getId(), renewedPotential, item.getItemId()));
                    c.SendPacket(CField.enchantResult(renewedPotential ? 0 : 0));
                    pPlayer.yellowMessage("Potential ID (Line 1) : " + equip.getPotential1());
                    pPlayer.yellowMessage("Potential ID (Line 2) : " + equip.getPotential2());
                    pPlayer.yellowMessage("Potential ID (Line 3) : " + equip.getPotential3());
                }
                break;
            }

            case ItemConstants.MIRACLE_CUBE: {
                final Item item = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((byte) iPacket.DecodeInt());
                final Equip equip = (Equip) item;

                if (item != null && c.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() >= 1) {
                    int tierUpRate = ItemPotentialProvider.RATE_REDCUBE_TIERUP;
                    int tierDownRate = 0;
                    int fragmentGiven = 0;
                    ItemPotentialTierType maxTier = ItemPotentialTierType.Unique;
                    boolean hidePotentialAfterReset = false;

                    switch (toUse.getItemId()) {
                        case ItemConstants.SUPER_MIRACLE_CUBE:
                            maxTier = ItemPotentialTierType.Legendary;
                            tierUpRate = ItemPotentialProvider.RATE_SUPER_MIRACLECUBE_TIERUP;
                            fragmentGiven = ItemConstants.SUPER_MIRACLE_CUBE_FRAGMENT;
                            hidePotentialAfterReset = true;
                            break;
                        case ItemConstants.RED_CUBE:
                            maxTier = ItemPotentialTierType.Legendary;
                            fragmentGiven = ItemConstants.RED_CUBE_FRAGMENT;
                            break;
                        case ItemConstants.MIRACLE_CUBE:
                            maxTier = ItemPotentialTierType.Unique;
                            fragmentGiven = ItemConstants.MIRACLE_CUBE_FRAGMENT;
                            hidePotentialAfterReset = true;
                            break;
                    }
                    ItemPotentialTierType lastTierBeforeCube = equip.getPotentialTier();

                    // Check Tier
                    if (lastTierBeforeCube.getValue() > maxTier.getValue()) {
                        LogHelper.PACKET_EDIT_HACK.get().info(
                                String.format("[UseCashItemHandler] %s [ChrID: %d; AccId %d] has tried to use a red cube for %s tier on a %s tier equipment. EquipmentID = %d, CubeID = %d",
                                        pPlayer.getName(), pPlayer.getId(), c.getAccID(),
                                        maxTier.toString(),
                                        lastTierBeforeCube.toString(),
                                        item.getItemId(), toUse.getItemId())
                        );
                        c.Close();
                        return;
                    }

                    //float miracleRate = c.getChannelServer().getMiracleCubeRate(c.getWorld()); (null lel)
                    final float miracleRate = ServerConstants.MIRACLE_CUBE_RATE;
                    final boolean renewedPotential = ItemPotentialProvider.resetPotential(equip, tierUpRate, tierDownRate, maxTier, hidePotentialAfterReset, miracleRate);
                    if (renewedPotential) {
                        bUsed = true; // flag as used to be removed.
                        if (fragmentGiven != 0) {
                            MapleInventoryManipulator.addById(c, fragmentGiven, (short) 1, "Cube on " + LocalDateTime.now());
                        }
                        // Update inventory equipment 
                        List<ModifyInventory> modifications = new ArrayList<>();
                        modifications.add(new ModifyInventory(ModifyInventoryOperation.AddItem, item));
                        c.SendPacket(WvsContext.inventoryOperation(true, modifications));

                        if (!hidePotentialAfterReset) {
                            c.SendPacket(MiracleCubePacket.onRedCubeResult(pPlayer.getId(), lastTierBeforeCube != equip.getPotentialTier(), equip.getPosition(), toUse.getItemId(), equip));
                        }
                    } else {
                        pPlayer.dropMessage(5, "This item's Potential cannot be reset.");
                    }

                    // Show to map
                    pPlayer.getMap().broadcastMessage(CField.showPotentialReset(pPlayer.getId(), renewedPotential, item.getItemId()));
                    c.SendPacket(CField.enchantResult(renewedPotential ? 0 : 0));
                }
                break;
            }
            case ItemConstants.MEMORY_CUBE: {
                final Item item = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((byte) iPacket.DecodeInt());
                final Equip equip = (Equip) item;

                if (item != null && c.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() >= 1) {
                    int tierUpRate = ItemPotentialProvider.RATE_MEMORIAL_CUBE_TIERUP;
                    int tierDownRate = 0;
                    int fragmentGiven = 0;
                    ItemPotentialTierType maxTier = ItemPotentialTierType.Legendary;

                    ItemPotentialTierType lastTierBeforeCube = equip.getPotentialTier();

                    // Check Tier
                    if (lastTierBeforeCube.getValue() > maxTier.getValue()) {
                        LogHelper.PACKET_EDIT_HACK.get().info(
                                String.format("[UseCashItemHandler] %s [ChrID: %d; AccId %d] has tried to use a memorial cube for %s tier on a %s tier equipment. EquipmentID = %d, CubeID = %d",
                                        pPlayer.getName(), pPlayer.getId(), c.getAccID(),
                                        maxTier.toString(),
                                        lastTierBeforeCube.toString(),
                                        item.getItemId(), toUse.getItemId())
                        );
                        c.Close();
                        return;
                    }

                    final Equip equip_afterState = (Equip) equip.copy(); // memorial cube allows the user to select before and after state.
                    final float miracleRate = c.getChannelServer().getMiracleCubeRate(c.getWorld());
                    final boolean renewedPotential = ItemPotentialProvider.resetPotential(equip_afterState, tierUpRate, tierDownRate, maxTier, false, miracleRate);
                    if (renewedPotential) {
                        bUsed = true; // flag as used to be removed.
                        if (fragmentGiven != 0) {
                            MapleInventoryManipulator.addById(c, fragmentGiven, (short) 1, "Cube on " + LocalDateTime.now());
                        }

                        // Store the Equip to temporary value
                        pPlayer.getTemporaryValues().setTemporaryObject(CharacterTemporaryValues.KEY_MEMORIAL_CUBE, new Pair(equip, equip_afterState));

                        // Update inventory equipment 
                        List<ModifyInventory> modifications = new ArrayList<>();
                        modifications.add(new ModifyInventory(ModifyInventoryOperation.AddItem, item));
                        c.SendPacket(WvsContext.inventoryOperation(true, modifications));

                        c.SendPacket(MiracleCubePacket.onMemorialCubeResult(CharacterTemporaryValues.KEY_MEMORIAL_CUBE, lastTierBeforeCube != equip_afterState.getPotentialTier(), equip.getPosition(), toUse.getItemId(), equip_afterState));
                    } else {
                        pPlayer.dropMessage(5, "This item's Potential cannot be reset.");
                    }

                    // Show to map
                    pPlayer.getMap().broadcastMessage(CField.showPotentialReset(pPlayer.getId(), renewedPotential, item.getItemId()));
                    c.SendPacket(CField.enchantResult(renewedPotential ? 0 : 0));
                }
                break;
            }

            case ItemConstants.BLACK_CUBE: {
                final Item item = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((byte) iPacket.DecodeInt());
                final Equip equip = (Equip) item;

                if (item != null && c.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() >= 1) {
                    final int tierUpRate = ItemPotentialProvider.RATE_BLACK_CUBE_TIERUP;
                    final int tierDownRate = 0;
                    final int fragmentGiven = ItemConstants.BLACK_CUBE_FRAGMENT;
                    final ItemPotentialTierType maxTier = ItemPotentialTierType.Legendary;

                    final ItemPotentialTierType lastTierBeforeCube = equip.getPotentialTier();

                    // Check Tier
                    if (lastTierBeforeCube.getValue() > maxTier.getValue()) {
                        LogHelper.PACKET_EDIT_HACK.get().info(
                                String.format("[UseCashItemHandler] %s [ChrID: %d; AccId %d] has tried to use a black cube for %s tier on a %s tier equipment. EquipmentID = %d, CubeID = %d",
                                        pPlayer.getName(), pPlayer.getId(), c.getAccID(),
                                        maxTier.toString(),
                                        lastTierBeforeCube.toString(),
                                        item.getItemId(), toUse.getItemId())
                        );
                        c.Close();
                        return;
                    }

                    final Equip equip_afterState = (Equip) equip.copy(); // black cube allows the user to select before and after state.
                    final float miracleRate = c.getChannelServer().getMiracleCubeRate(c.getWorld());
                    final boolean renewedPotential = ItemPotentialProvider.resetPotential(equip_afterState, tierUpRate, tierDownRate, maxTier, false, miracleRate);
                    if (renewedPotential) {
                        bUsed = true; // flag as used to be removed.

                        // Store the Equip to temporary value
                        pPlayer.getTemporaryValues().setTemporaryObject(CharacterTemporaryValues.KEY_BLACK_CUBE, new Pair(equip, equip_afterState));

                        MapleInventoryManipulator.addById(c, fragmentGiven, (short) 1, "Cube on " + LocalDateTime.now());

                        // Update inventory equipment 
                        List<ModifyInventory> modifications = new ArrayList<>();
                        modifications.add(new ModifyInventory(ModifyInventoryOperation.AddItem, item));
                        c.SendPacket(WvsContext.inventoryOperation(true, modifications));

                        c.SendPacket(MiracleCubePacket.onBlackCubeResult(CharacterTemporaryValues.KEY_BLACK_CUBE, lastTierBeforeCube != equip_afterState.getPotentialTier(), equip.getPosition(), toUse.getItemId(), equip_afterState));
                    } else {
                        pPlayer.dropMessage(5, "This item's Potential cannot be reset.");
                    }

                    // Show to map
                    pPlayer.getMap().broadcastMessage(CField.showPotentialReset(pPlayer.getId(), renewedPotential, item.getItemId()));
                    c.SendPacket(CField.enchantResult(renewedPotential ? 0 : 0));
                }
                break;
            }
            case ItemConstants.PERFECT_POTENTIAL_STAMP: {
                final Item item = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((byte) iPacket.DecodeInt());
                if (item != null) {
                    final Equip eq = (Equip) item;

                    final boolean success = ItemPotentialProvider.useAwakeningStamp(eq);

                    if (success) {
                        List<ModifyInventory> modifications = new ArrayList<>();
                        modifications.add(new ModifyInventory(ModifyInventoryOperation.AddItem, item));
                        c.SendPacket(WvsContext.inventoryOperation(true, modifications));

                        c.getPlayer().getMap().broadcastMessage(CField.showPotentialReset(c.getPlayer().getId(), true, toUse.getItemId()));

                        bUsed = true;
                    }
                }
                break;
            }
            case 5062400:
            case 5062401:
            case 5062402:
            case 5062403: {
                short appearance = (short) iPacket.DecodeInt();
                short function = (short) iPacket.DecodeInt();
                Equip appear = (Equip) c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem(appearance);
                Equip equip = (Equip) c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem(function);
                if (equip.getFusionAnvil() != 0) {
                    return;
                } else if (GameConstants.isEquip(appear.getItemId()) || GameConstants.isEquip(equip.getItemId())) {
                    if (appear.getItemId() / 10000 != equip.getItemId() / 10000) {
                        return;
                    }
                } else if (appear.getItemId() / 100000 != equip.getItemId() / 100000) {
                    return;
                }
                equip.setFusionAnvil(appear.getItemId());
                c.getPlayer().forceReAddItemNoUpdate(equip, MapleInventoryType.EQUIP);
                bUsed = true;
                break;
            }
            case 5750000: { //alien cube
                if (c.getPlayer().getLevel() < 10) {
                    c.getPlayer().dropMessage(1, "You may not use this until level 10.");
                } else {
                    final Item item = c.getPlayer().getInventory(MapleInventoryType.SETUP).getItem((byte) iPacket.DecodeInt());
                    if (item != null && c.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() >= 1 && c.getPlayer().getInventory(MapleInventoryType.SETUP).getNumFreeSlot() >= 1) {
                        
                        NebuliteGrade currentGrade = GameConstants.getNebuliteGrade(item.getItemId());
                        if (currentGrade != NebuliteGrade.None && currentGrade != NebuliteGrade.GradeS) {
                            
                            final NebuliteGrade nextGrade = NebuliteGrade.getNextGrade(currentGrade); // can the item rank up further?
                            if (nextGrade != NebuliteGrade.None) {
                                // Some randomness to see if the user's lucky enough
                                final boolean rankUp = Randomizer.nextInt(100) < 7;
                                if (rankUp) {
                                    currentGrade = nextGrade;
                                }
                            }
                            ItemPotentialOption newOption = ItemPotentialProvider.getRandomNebulitePotential(currentGrade);
                            if (newOption != null) {
                                MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.SETUP, item.getPosition(), (short) 1, false);
                            
                                MapleInventoryManipulator.addById(c, newOption.getOptionId(), (short) 1, "Upgraded from alien cube on " + LocalDateTime.now());
                                MapleInventoryManipulator.addById(c, 2430691, (short) 1, "Alien Cube" + " on " + LocalDateTime.now());
                                bUsed = true;
                            }
                        } else {
                            c.getPlayer().dropMessage(1, "Grade S Nebulite cannot be added.");
                        }
                    } else {
                        c.getPlayer().dropMessage(5, "You do not have sufficient inventory slot.");
                    }
                }
                break;
            }
            case 5750001: { // socket diffuser
                if (c.getPlayer().getLevel() < 10) {
                    c.getPlayer().dropMessage(1, "You may not use this until level 10.");
                } else {
                    final Item item = c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((byte) iPacket.DecodeInt());
                    if (item != null) {
                        final Equip eq = (Equip) item;
                        if (eq.getSocket1() > 0) { // first slot only.
                            eq.setSocket1(0);

                            List<ModifyInventory> modifications = new ArrayList<>();
                            modifications.add(new ModifyInventory(ModifyInventoryOperation.AddItem, item));
                            c.SendPacket(WvsContext.inventoryOperation(true, modifications));

                            c.getPlayer().forceReAddItemNoUpdate(item, MapleInventoryType.EQUIP);
                            bUsed = true;
                        } else {
                            c.getPlayer().dropMessage(5, "This item do not have a socket.");
                        }
                    } else {
                        c.getPlayer().dropMessage(5, "This item's nebulite cannot be removed.");
                    }
                }
                break;
            }
            case 5521000: { // Karma
                final MapleInventoryType type = MapleInventoryType.getByType((byte) iPacket.DecodeInt());
                final Item item = c.getPlayer().getInventory(type).getItem((byte) iPacket.DecodeInt());

                if (item != null && !ItemFlag.KARMA_ACC.check(item.getFlag())
                        && !ItemFlag.KARMA_ACC_USE.check(item.getFlag())
                        && GameConstants.isEquip(item.getItemId())
                        && ((Equip) item).getKarmaCount() != 0) {
                    Equip eq = (Equip) item;
                    if (MapleItemInformationProvider.getInstance().isShareTagEnabled(item.getItemId())) {
                        short flag = item.getFlag();
                        if (ItemFlag.UNTRADABLE.check(flag)) {
                            flag -= ItemFlag.UNTRADABLE.getValue();
                        } else if (type == MapleInventoryType.EQUIP) {
                            flag |= ItemFlag.KARMA_ACC.getValue();
                        } else {
                            flag |= ItemFlag.KARMA_ACC_USE.getValue();
                        }
                        item.setFlag(flag);
                        eq.setKarmaCount((byte) (eq.getKarmaCount() - 1));
                        c.getPlayer().forceReAddItemNoUpdate(item, type);

                        List<ModifyInventory> modifications = new ArrayList<>();
                        modifications.add(new ModifyInventory(ModifyInventoryOperation.Remove, item));
                        modifications.add(new ModifyInventory(ModifyInventoryOperation.AddItem, item));
                        c.SendPacket(WvsContext.inventoryOperation(true, modifications));
                        bUsed = true;
                    }
                }
                break;
            }
            case 5520001: //p.karma
            case 5520000: { // Karma
                final MapleInventoryType type = MapleInventoryType.getByType((byte) iPacket.DecodeInt());
                final Item item = c.getPlayer().getInventory(type).getItem((byte) iPacket.DecodeInt());

                if (item != null && !ItemFlag.KARMA_EQ.check(item.getFlag())
                        && !ItemFlag.KARMA_USE.check(item.getFlag())
                        && GameConstants.isEquip(item.getItemId())
                        && ((Equip) item).getKarmaCount() != 0) {
                    Equip eq = (Equip) item;
                    if ((itemId == 5520000 && MapleItemInformationProvider.getInstance().isKarmaEnabled(item.getItemId())) || (itemId == 5520001 && MapleItemInformationProvider.getInstance().isPKarmaEnabled(item.getItemId()))) {
                        short flag = item.getFlag();
                        if (ItemFlag.UNTRADABLE.check(flag)) {
                            flag -= ItemFlag.UNTRADABLE.getValue();
                        } else if (type == MapleInventoryType.EQUIP) {
                            flag |= ItemFlag.KARMA_EQ.getValue();
                        } else {
                            flag |= ItemFlag.KARMA_USE.getValue();
                        }
                        item.setFlag(flag);
                        eq.setKarmaCount((byte) (eq.getKarmaCount() - 1));
                        c.getPlayer().forceReAddItemNoUpdate(item, type);

                        List<ModifyInventory> modifications = new ArrayList<>();
                        modifications.add(new ModifyInventory(ModifyInventoryOperation.Remove, item));
                        modifications.add(new ModifyInventory(ModifyInventoryOperation.AddItem, item));
                        c.SendPacket(WvsContext.inventoryOperation(true, modifications));
                        bUsed = true;
                    }
                }
                break;
            }
            case 5570000: { // Vicious Hammer
                iPacket.DecodeInt(); // Inventory type, Hammered eq is always EQ.
                final Equip item = (Equip) c.getPlayer().getInventory(MapleInventoryType.EQUIP).getItem((byte) iPacket.DecodeInt());
                // another int here, D3 49 DC 00
                if (item != null) {
                    if (GameConstants.canHammer(item.getItemId()) && MapleItemInformationProvider.getInstance().getSlots(item.getItemId()) > 0 && item.getViciousHammer() < 2) {
                        item.setViciousHammer((byte) (item.getViciousHammer() + 1));
                        item.setUpgradeSlots((byte) (item.getUpgradeSlots() + 1));
                        c.getPlayer().forceReAddItem(item, MapleInventoryType.EQUIP);
                        c.SendPacket(CSPacket.ViciousHammer(true, item.getViciousHammer()));
                        bUsed = true;
                    } else {
                        c.getPlayer().dropMessage(5, "You may not use it on this item.");
                        c.SendPacket(CSPacket.ViciousHammer(true, (byte) 0));
                    }
                }
                break;
            }
            case 5610001:
            case 5610000: { // Vega 30
                iPacket.DecodeInt(); // Inventory type, always eq
                final short dst = (short) iPacket.DecodeInt();
                iPacket.DecodeInt(); // Inventory type, always use
                final short src = (short) iPacket.DecodeInt();
                bUsed = UseUpgradeScroll(src, dst, (short) 2, c, c.getPlayer(), itemId, false); //cannot use ws with vega but we dont care
                cc = bUsed;
                break;
            }
            case 5060001: { // Sealing Lock
                final MapleInventoryType type = MapleInventoryType.getByType((byte) iPacket.DecodeInt());
                final Item item = c.getPlayer().getInventory(type).getItem((byte) iPacket.DecodeInt());
                // another int here, lock = 5A E5 F2 0A, 7 day = D2 30 F3 0A
                if (item != null && item.getExpiration() == -1) {
                    short flag = item.getFlag();
                    flag |= ItemFlag.LOCK.getValue();
                    item.setFlag(flag);

                    c.getPlayer().forceReAddItemFlag(item, type);
                    bUsed = true;
                }
                break;
            }
            case 5061000: { // Sealing Lock 7 days
                final MapleInventoryType type = MapleInventoryType.getByType((byte) iPacket.DecodeInt());
                final Item item = c.getPlayer().getInventory(type).getItem((byte) iPacket.DecodeInt());
                // another int here, lock = 5A E5 F2 0A, 7 day = D2 30 F3 0A
                if (item != null && item.getExpiration() == -1) {
                    short flag = item.getFlag();
                    flag |= ItemFlag.LOCK.getValue();
                    item.setFlag(flag);
                    item.setExpiration(System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000));

                    c.getPlayer().forceReAddItemFlag(item, type);
                    bUsed = true;
                }
                break;
            }
            case 5061001: { // Sealing Lock 30 days
                final MapleInventoryType type = MapleInventoryType.getByType((byte) iPacket.DecodeInt());
                final Item item = c.getPlayer().getInventory(type).getItem((byte) iPacket.DecodeInt());
                // another int here, lock = 5A E5 F2 0A, 7 day = D2 30 F3 0A
                if (item != null && item.getExpiration() == -1) {
                    short flag = item.getFlag();
                    flag |= ItemFlag.LOCK.getValue();
                    item.setFlag(flag);

                    item.setExpiration(System.currentTimeMillis() + (30 * 24 * 60 * 60 * 1000));

                    c.getPlayer().forceReAddItemFlag(item, type);
                    bUsed = true;
                }
                break;
            }
            case 5061002: { // Sealing Lock 90 days
                final MapleInventoryType type = MapleInventoryType.getByType((byte) iPacket.DecodeInt());
                final Item item = c.getPlayer().getInventory(type).getItem((byte) iPacket.DecodeInt());
                // another int here, lock = 5A E5 F2 0A, 7 day = D2 30 F3 0A
                if (item != null && item.getExpiration() == -1) {
                    short flag = item.getFlag();
                    flag |= ItemFlag.LOCK.getValue();
                    item.setFlag(flag);

                    item.setExpiration(System.currentTimeMillis() + (90 * 24 * 60 * 60 * 1000));

                    c.getPlayer().forceReAddItemFlag(item, type);
                    bUsed = true;
                }
                break;
            }
            case 5061003: { // Sealing Lock 365 days
                final MapleInventoryType type = MapleInventoryType.getByType((byte) iPacket.DecodeInt());
                final Item item = c.getPlayer().getInventory(type).getItem((byte) iPacket.DecodeInt());
                // another int here, lock = 5A E5 F2 0A, 7 day = D2 30 F3 0A
                if (item != null && item.getExpiration() == -1) {
                    short flag = item.getFlag();
                    flag |= ItemFlag.LOCK.getValue();
                    item.setFlag(flag);

                    item.setExpiration(System.currentTimeMillis() + (365 * 24 * 60 * 60 * 1000));

                    c.getPlayer().forceReAddItemFlag(item, type);
                    bUsed = true;
                }
                break;
            }
            case 5063000: {
                final MapleInventoryType type = MapleInventoryType.getByType((byte) iPacket.DecodeInt());
                final Item item = c.getPlayer().getInventory(type).getItem((byte) iPacket.DecodeInt());
                // another int here, lock = 5A E5 F2 0A, 7 day = D2 30 F3 0A
                if (item != null && item.getType() == ItemType.Equipment) { //equip
                    short flag = item.getFlag();
                    flag |= ItemFlag.LUCKY_DAY.getValue();
                    item.setFlag(flag);

                    c.getPlayer().forceReAddItemFlag(item, type);
                    bUsed = true;
                }
                break;
            }
            case 5064000: {
                final MapleInventoryType type = MapleInventoryType.getByType((byte) iPacket.DecodeInt());
                final Item item = c.getPlayer().getInventory(type).getItem((byte) iPacket.DecodeInt());
                // another int here, lock = 5A E5 F2 0A, 7 day = D2 30 F3 0A
                if (item != null && item.getType() == ItemType.Equipment) { //equip
                    if (((Equip) item).getEnhance() >= 12) {
                        break; //cannot be used
                    }
                    short flag = item.getFlag();
                    flag |= ItemFlag.SHIELD_WARD.getValue();
                    item.setFlag(flag);

                    c.getPlayer().forceReAddItemFlag(item, type);
                    bUsed = true;
                }
                break;
            }

            case 5060003:
            case 5060004:
            case 5060005:
            case 5060006:
            case 5060007: {
                Item item = c.getPlayer().getInventory(MapleInventoryType.ETC).findById(itemId == 5060003 ? 4170023 : 4170024);
                if (item == null || item.getQuantity() <= 0) { // hacking{
                    return;
                }
                if (getIncubatedItems(c, itemId)) {
                    MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.ETC, item.getPosition(), (short) 1, false);
                    bUsed = true;
                }
                break;
            }

            case 5070000: { // Megaphone
                if (c.getPlayer().getLevel() < 10) {
                    c.getPlayer().dropMessage(5, "Must be level 10 or higher.");
                    break;
                }
                if (c.getPlayer().getMapId() == GameConstants.JAIL) {
                    c.getPlayer().dropMessage(5, "Cannot be used here.");
                    break;
                }
                if (!c.getPlayer().getCheatTracker().canSmega()) {
                    c.getPlayer().dropMessage(5, "You may only use this every 15 seconds.");
                    break;
                }
                if (!c.getChannelServer().getMegaphoneMuteState()) {
                    final String message = iPacket.DecodeString();

                    if (message.length() > 65) {
                        break;
                    }
                    final StringBuilder sb = new StringBuilder();
                    addMedalString(c.getPlayer(), sb);
                    sb.append(c.getPlayer().getName());
                    sb.append(" : ");
                    sb.append(message);

                    c.getPlayer().getMap().broadcastMessage(WvsContext.broadcastMsg(2, sb.toString()));
                    bUsed = true;
                } else {
                    c.getPlayer().dropMessage(5, "The usage of Megaphone is currently disabled.");
                }
                break;
            }
            case 5071000: { // Megaphone
                if (c.getPlayer().getLevel() < 10) {
                    c.getPlayer().dropMessage(5, "Must be level 10 or higher.");
                    break;
                }
                if (c.getPlayer().getMapId() == GameConstants.JAIL) {
                    c.getPlayer().dropMessage(5, "Cannot be used here.");
                    break;
                }
                if (!c.getPlayer().getCheatTracker().canSmega()) {
                    c.getPlayer().dropMessage(5, "You may only use this every 15 seconds.");
                    break;
                }
                if (!c.getChannelServer().getMegaphoneMuteState()) {
                    final String message = iPacket.DecodeString();

                    if (message.length() > 65) {
                        break;
                    }
                    final StringBuilder sb = new StringBuilder();
                    addMedalString(c.getPlayer(), sb);
                    sb.append(c.getPlayer().getName());
                    sb.append(" : ");
                    sb.append(message);

                    c.getChannelServer().broadcastSmegaPacket(WvsContext.broadcastMsg(2, sb.toString()));
                    bUsed = true;
                } else {
                    c.getPlayer().dropMessage(5, "The usage of Megaphone is currently disabled.");
                }
                break;
            }
            case 5077000: { // 3 line Megaphone
                if (c.getPlayer().getLevel() < 10) {
                    c.getPlayer().dropMessage(5, "Must be level 10 or higher.");
                    break;
                }
                if (c.getPlayer().getMapId() == GameConstants.JAIL) {
                    c.getPlayer().dropMessage(5, "Cannot be used here.");
                    break;
                }
                if (!c.getPlayer().getCheatTracker().canSmega()) {
                    c.getPlayer().dropMessage(5, "You may only use this every 15 seconds.");
                    break;
                }
                if (!c.getChannelServer().getMegaphoneMuteState()) {
                    final byte numLines = iPacket.DecodeByte();
                    if (numLines > 3) {
                        return;
                    }
                    final List<String> messages = new LinkedList<>();
                    String message;
                    for (int i = 0; i < numLines; i++) {
                        message = iPacket.DecodeString();
                        if (message.length() > 65) {
                            break;
                        }
                        messages.add(c.getPlayer().getName() + " : " + message);
                    }
                    final boolean ear = iPacket.DecodeByte() > 0;

                    World.Broadcast.broadcastSmega(WvsContext.tripleSmega(messages, ear, c.getChannel()));
                    bUsed = true;
                } else {
                    c.getPlayer().dropMessage(5, "The usage of Megaphone is currently disabled.");
                }
                break;
            }
            case 5079004: { // Heart Megaphone
                if (c.getPlayer().getLevel() < 10) {
                    c.getPlayer().dropMessage(5, "Must be level 10 or higher.");
                    break;
                }
                if (c.getPlayer().getMapId() == GameConstants.JAIL) {
                    c.getPlayer().dropMessage(5, "Cannot be used here.");
                    break;
                }
                if (!c.getPlayer().getCheatTracker().canSmega()) {
                    c.getPlayer().dropMessage(5, "You may only use this every 15 seconds.");
                    break;
                }
                if (!c.getChannelServer().getMegaphoneMuteState()) {
                    final String message = iPacket.DecodeString();

                    if (message.length() > 65) {
                        break;
                    }
                    World.Broadcast.broadcastSmega(WvsContext.echoMegaphone(c.getPlayer().getName(), message));
                    bUsed = true;
                } else {
                    c.getPlayer().dropMessage(5, "The usage of Megaphone is currently disabled.");
                }
                break;
            }
            case 5073000: { // Heart Megaphone
                if (c.getPlayer().getLevel() < 10) {
                    c.getPlayer().dropMessage(5, "Must be level 10 or higher.");
                    break;
                }
                if (c.getPlayer().getMapId() == GameConstants.JAIL) {
                    c.getPlayer().dropMessage(5, "Cannot be used here.");
                    break;
                }
                if (!c.getPlayer().getCheatTracker().canSmega()) {
                    c.getPlayer().dropMessage(5, "You may only use this every 15 seconds.");
                    break;
                }
                if (!c.getChannelServer().getMegaphoneMuteState()) {
                    final String message = iPacket.DecodeString();

                    if (message.length() > 65) {
                        break;
                    }
                    final StringBuilder sb = new StringBuilder();
                    addMedalString(c.getPlayer(), sb);
                    sb.append(c.getPlayer().getName());
                    sb.append(" : ");
                    sb.append(message);

                    final boolean ear = iPacket.DecodeByte() != 0;
                    World.Broadcast.broadcastSmega(WvsContext.broadcastMsg(9, c.getChannel(), sb.toString(), ear));
                    bUsed = true;
                } else {
                    c.getPlayer().dropMessage(5, "The usage of Megaphone is currently disabled.");
                }
                break;
            }
            case 5074000: { // Skull Megaphone
                if (c.getPlayer().getLevel() < 10) {
                    c.getPlayer().dropMessage(5, "Must be level 10 or higher.");
                    break;
                }
                if (c.getPlayer().getMapId() == GameConstants.JAIL) {
                    c.getPlayer().dropMessage(5, "Cannot be used here.");
                    break;
                }
                if (!c.getPlayer().getCheatTracker().canSmega()) {
                    c.getPlayer().dropMessage(5, "You may only use this every 15 seconds.");
                    break;
                }
                if (!c.getChannelServer().getMegaphoneMuteState()) {
                    final String message = iPacket.DecodeString();

                    if (message.length() > 65) {
                        break;
                    }
                    final StringBuilder sb = new StringBuilder();
                    addMedalString(c.getPlayer(), sb);
                    sb.append(c.getPlayer().getName());
                    sb.append(" : ");
                    sb.append(message);

                    final boolean ear = iPacket.DecodeByte() != 0;

                    World.Broadcast.broadcastSmega(WvsContext.broadcastMsg(22, c.getChannel(), sb.toString(), ear));
                    bUsed = true;
                } else {
                    c.getPlayer().dropMessage(5, "The usage of Megaphone is currently disabled.");
                }
                break;
            }
            case 5072000: { // Super Megaphone
                if (c.getPlayer().getLevel() < 10) {
                    c.getPlayer().dropMessage(5, "Must be level 10 or higher.");
                    break;
                }
                if (c.getPlayer().getMapId() == GameConstants.JAIL) {
                    c.getPlayer().dropMessage(5, "Cannot be used here.");
                    break;
                }
                if (!c.getPlayer().getCheatTracker().canSmega()) {
                    c.getPlayer().dropMessage(5, "You may only use this every 15 seconds.");
                    break;
                }
                if (!c.getChannelServer().getMegaphoneMuteState()) {
                    final String message = iPacket.DecodeString();

                    if (message.length() > 65) {
                        break;
                    }
                    final StringBuilder sb = new StringBuilder();
                    addMedalString(c.getPlayer(), sb);
                    sb.append(c.getPlayer().getName());
                    sb.append(" : ");
                    sb.append(message);

                    final boolean ear = iPacket.DecodeByte() != 0;

                    World.Broadcast.broadcastSmega(WvsContext.broadcastMsg(3, c.getChannel(), sb.toString(), ear));
                    bUsed = true;
                } else {
                    c.getPlayer().dropMessage(5, "The usage of Megaphone is currently disabled.");
                }
                break;
            }
            case 5076000: { // Item Megaphone
                if (c.getPlayer().getLevel() < 10) {
                    c.getPlayer().dropMessage(5, "Must be level 10 or higher.");
                    break;
                }
                if (c.getPlayer().getMapId() == GameConstants.JAIL) {
                    c.getPlayer().dropMessage(5, "Cannot be used here.");
                    break;
                }
                if (!c.getPlayer().getCheatTracker().canSmega()) {
                    c.getPlayer().dropMessage(5, "You may only use this every 15 seconds.");
                    break;
                }
                if (!c.getChannelServer().getMegaphoneMuteState()) {
                    final String message = iPacket.DecodeString();

                    if (message.length() > 65) {
                        break;
                    }
                    final StringBuilder sb = new StringBuilder();
                    addMedalString(c.getPlayer(), sb);
                    sb.append(c.getPlayer().getName());
                    sb.append(" : ");
                    sb.append(message);

                    final boolean ear = iPacket.DecodeByte() > 0;

                    Item item = null;
                    if (iPacket.DecodeByte() == 1) { //item
                        byte invType = (byte) iPacket.DecodeInt();
                        byte pos = (byte) iPacket.DecodeInt();
                        if (pos <= 0) {
                            invType = -1;
                        }
                        item = c.getPlayer().getInventory(MapleInventoryType.getByType(invType)).getItem(pos);
                    }
                    World.Broadcast.broadcastSmega(WvsContext.handleItemMegaphone(sb.toString(), ear, c.getChannel(), item));
                    bUsed = true;
                } else {
                    c.getPlayer().dropMessage(5, "The usage of Megaphone is currently disabled.");
                }
                break;
            }
            case 5079000: {
                break;
            }
            case 5079001:
            case 5079002: {
                if (c.getPlayer().getLevel() < 10) {
                    c.getPlayer().dropMessage(5, "Must be level 10 or higher.");
                    break;
                }
                if (c.getPlayer().getMapId() == GameConstants.JAIL) {
                    c.getPlayer().dropMessage(5, "Cannot be used here.");
                    break;
                }
                if (!c.getPlayer().getCheatTracker().canSmega()) {
                    c.getPlayer().dropMessage(5, "You may only use this every 15 seconds.");
                    break;
                }
                if (!c.getChannelServer().getMegaphoneMuteState()) {
                    final String message = iPacket.DecodeString();

                    if (message.length() > 65) {
                        break;
                    }
                    final StringBuilder sb = new StringBuilder();
                    addMedalString(c.getPlayer(), sb);
                    sb.append(c.getPlayer().getName());
                    sb.append(" : ");
                    sb.append(message);

                    final boolean ear = iPacket.DecodeByte() != 0;

                    World.Broadcast.broadcastSmega(WvsContext.broadcastMsg(24 + itemId % 10, c.getChannel(), sb.toString(), ear));
                    bUsed = true;
                } else {
                    c.getPlayer().dropMessage(5, "The usage of Megaphone is currently disabled.");
                }
                break;
            }
            case 5075000: // MapleTV Messenger
            case 5075001: // MapleTV Star Messenger
            case 5075002: { // MapleTV Heart Messenger
                c.getPlayer().dropMessage(5, "There are no MapleTVs to broadcast the message to.");
                break;
            }
            case 5075003:
            case 5075004:
            case 5075005: {
                if (c.getPlayer().getLevel() < 10) {
                    c.getPlayer().dropMessage(5, "Must be level 10 or higher.");
                    break;
                }
                if (c.getPlayer().getMapId() == GameConstants.JAIL) {
                    c.getPlayer().dropMessage(5, "Cannot be used here.");
                    break;
                }
                if (!c.getPlayer().getCheatTracker().canSmega()) {
                    c.getPlayer().dropMessage(5, "You may only use this every 15 seconds.");
                    break;
                }
                int tvType = itemId % 10;
                if (tvType == 3) {
                    iPacket.DecodeByte(); //who knows
                }
                boolean ear = tvType != 1 && tvType != 2 && iPacket.DecodeByte() > 1; //for tvType 1/2, there is no byte. 
                User victim = tvType == 1 || tvType == 4 ? null : c.getChannelServer().getPlayerStorage().getCharacterByName(iPacket.DecodeString()); //for tvType 4, there is no string.
                if (tvType == 0 || tvType == 3) { //doesn't allow two
                    victim = null;
                } else if (victim == null) {
                    c.getPlayer().dropMessage(1, "That character is not in the channel.");
                    break;
                }
                String message = iPacket.DecodeString();
                World.Broadcast.broadcastSmega(WvsContext.broadcastMsg(3, c.getChannel(), c.getPlayer().getName() + " : " + message, ear));
                bUsed = true;
                break;
            }
            case 5090100: // Wedding Invitation Card
            case 5090000: { // Note
                final String sendTo = iPacket.DecodeString();
                final String msg = iPacket.DecodeString();
                if (c.getChannelServer().getPlayerStorage().getCharacterByName(sendTo) != null) {
                    c.SendPacket(CSPacket.OnMemoResult((byte) 5, (byte) 0));
                    break;
                }
                c.getPlayer().sendNote(sendTo, msg);
                c.SendPacket(CSPacket.OnMemoResult((byte) 4, (byte) 0));
                bUsed = true;
                break;
            }
            case 5100000: { // Congratulatory Song
                c.getPlayer().getMap().broadcastMessage(CField.musicChange("Jukebox/Congratulation"));
                bUsed = true;
                break;
            }
            case 5190001:
            case 5190002:
            case 5190003:
            case 5190004:
            case 5190005:
            case 5190006:
            case 5190007:
            case 5190008:
            case 5190000: { // Pet Flags
                final int uniqueid = (int) iPacket.DecodeLong();
                Pet pet = c.getPlayer().getPet(0);
                int slo = 0;

                if (pet == null) {
                    break;
                }
                if (pet.getItem().getUniqueId() != uniqueid) {
                    pet = c.getPlayer().getPet(1);
                    slo = 1;
                    if (pet != null) {
                        if (pet.getItem().getUniqueId() != uniqueid) {
                            pet = c.getPlayer().getPet(2);
                            slo = 2;
                            if (pet != null) {
                                if (pet.getItem().getUniqueId() != uniqueid) {
                                    break;
                                }
                            } else {
                                break;
                            }
                        }
                    } else {
                        break;
                    }
                }
                PetFlag zz = PetFlag.getByAddId(itemId);
                if (zz != null && !zz.check(pet.getItem().getFlag())) {
                    pet.getItem().setFlag((short) (pet.getItem().getFlag() - zz.getValue()));
                    c.SendPacket(PetPacket.updatePet(pet, c.getPlayer().getInventory(MapleInventoryType.CASH).getItem((byte) pet.getItem().getPosition()), false));
                    c.SendPacket(WvsContext.enableActions());
                    c.SendPacket(CSPacket.changePetFlag(uniqueid, true, zz.getValue()));
                    bUsed = true;
                }
                break;
            }
            case 5191001:
            case 5191002:
            case 5191003:
            case 5191004:
            case 5191000: { // Pet Flags
                final int uniqueid = (int) iPacket.DecodeLong();
                Pet pet = c.getPlayer().getPet(0);
                int slo = 0;

                if (pet == null) {
                    break;
                }
                if (pet.getItem().getUniqueId() != uniqueid) {
                    pet = c.getPlayer().getPet(1);
                    slo = 1;
                    if (pet != null) {
                        if (pet.getItem().getUniqueId() != uniqueid) {
                            pet = c.getPlayer().getPet(2);
                            slo = 2;
                            if (pet != null) {
                                if (pet.getItem().getUniqueId() != uniqueid) {
                                    break;
                                }
                            } else {
                                break;
                            }
                        }
                    } else {
                        break;
                    }
                }
                Pet.PetFlag zz = Pet.PetFlag.getByDelId(itemId);
                if (zz != null && zz.check(pet.getItem().getFlag())) {
                    pet.getItem().setFlag((short) (pet.getItem().getFlag() - zz.getValue()));
                    c.SendPacket(PetPacket.updatePet(pet, c.getPlayer().getInventory(MapleInventoryType.CASH).getItem((short) (byte) pet.getItem().getPosition()), false));
                    // c.getPlayer().forceUpdateItem(pet.getItem());
                    c.SendPacket(WvsContext.enableActions());
                    c.SendPacket(CSPacket.changePetFlag(uniqueid, false, zz.getValue()));
                    bUsed = true;
                }
                break;
            }
            case 5501001:
            case 5501002: { //expiry mount
                final Skill skil = SkillFactory.getSkill(iPacket.DecodeInt());
                if (skil == null || skil.getId() / 10000 != 8000 || c.getPlayer().getSkillLevel(skil) <= 0 || !skil.isTimeLimited()) {
                    break;
                }
                final long toAdd = (itemId == 5501001 ? 30 : 60) * 24 * 60 * 60 * 1000L;
                final long expire = c.getPlayer().getSkillExpiry(skil);
                if (expire < System.currentTimeMillis() || expire + toAdd >= System.currentTimeMillis() + (365 * 24 * 60 * 60 * 1000L)) {
                    break;
                }
                c.getPlayer().changeSingleSkillLevel(skil, c.getPlayer().getSkillLevel(skil), c.getPlayer().getMasterLevel(skil), expire + toAdd);
                bUsed = true;
                break;
            }
            case 5170000: { // Pet name change
                final int uniqueid = (int) iPacket.DecodeLong();
                Pet pet = c.getPlayer().getPet(0);
                int slo = 0;

                if (pet == null) {
                    break;
                }
                if (pet.getItem().getUniqueId() != uniqueid) {
                    pet = c.getPlayer().getPet(1);
                    slo = 1;
                    if (pet != null) {
                        if (pet.getItem().getUniqueId() != uniqueid) {
                            pet = c.getPlayer().getPet(2);
                            slo = 2;
                            if (pet != null) {
                                if (pet.getItem().getUniqueId() != uniqueid) {
                                    break;
                                }
                            } else {
                                break;
                            }
                        }
                    } else {
                        break;
                    }
                }
                String nName = iPacket.DecodeString();
                if (MapleCharacterUtil.canChangePetName(nName)) {
                    pet.setName(nName);
                    c.SendPacket(PetPacket.updatePet(pet, c.getPlayer().getInventory(MapleInventoryType.CASH).getItem((short) (byte) pet.getItem().getPosition()), false));
                    //c.getPlayer().forceUpdateItem(pet.getItem());
                    c.SendPacket(WvsContext.enableActions());
                    c.getPlayer().getMap().broadcastMessage(CSPacket.changePetName(c.getPlayer(), nName, slo));
                    bUsed = true;
                }
                break;
            }
            case 5700000: {
                iPacket.Skip(8);
                if (c.getPlayer().getAndroid() == null) {
                    break;
                }
                String nName = iPacket.DecodeString();
                if (MapleCharacterUtil.canChangePetName(nName)) {
                    c.getPlayer().getAndroid().setName(nName);
                    c.getPlayer().setAndroid(c.getPlayer().getAndroid()); //respawn it
                    bUsed = true;
                }
                break;
            }
            case 5230001:
            case 5230000: {// owl of minerva
                final int itemSearch = iPacket.DecodeInt();
                final List<HiredMerchant> hms = c.getChannelServer().searchMerchant(itemSearch);
                if (hms.size() > 0) {
                    c.SendPacket(WvsContext.getOwlSearched(itemSearch, hms));
                    bUsed = true;
                } else {
                    c.getPlayer().dropMessage(1, "Unable to find the item.");
                }
                break;
            }
            case 5281001: //idk, but probably
            case 5280001: // Gas Skill
            case 5281000: { // Passed gas
                Rectangle bounds = new Rectangle((int) c.getPlayer().getPosition().getX(), (int) c.getPlayer().getPosition().getY(), 1, 1);
                Mist mist = new Mist(bounds, c.getPlayer());
                c.getPlayer().getMap().spawnMist(mist, 10000, true);
                c.SendPacket(WvsContext.enableActions());
                bUsed = true;
                break;
            }
            case 5370001:
            case 5370000: { // Chalkboard
                for (MapleEventType t : MapleEventType.values()) {
                    final MapleEvent e = ChannelServer.getInstance(c.getChannel()).getEvent(t);
                    if (e.isRunning()) {
                        for (int i : e.getType().mapids) {
                            if (c.getPlayer().getMapId() == i) {
                                c.getPlayer().dropMessage(5, "You may not use that here.");
                                c.SendPacket(WvsContext.enableActions());
                                return;
                            }
                        }
                    }
                }
                c.getPlayer().setChalkboard(iPacket.DecodeString());
                break;
            }
            case 5390000: // Diablo Messenger
            case 5390001: // Cloud 9 Messenger
            case 5390002: // Loveholic Messenger
            case 5390003: // New Year Messenger 1
            case 5390004: // New Year Messenger 2
            case 5390005: // Cute Tiger Messenger
            case 5390006: // Tiger Roar's Messenger
            case 5390007:
            case 5390008:
            case 5390009: {
                if (c.getPlayer().getLevel() < 10) {
                    c.getPlayer().dropMessage(5, "Must be level 10 or higher.");
                    break;
                }
                if (c.getPlayer().getMapId() == GameConstants.JAIL) {
                    c.getPlayer().dropMessage(5, "Cannot be used here.");
                    break;
                }
                if (!c.getPlayer().getCheatTracker().canAvatarSmega()) {
                    c.getPlayer().dropMessage(5, "You may only use this every 5 minutes.");
                    break;
                }
                if (!c.getChannelServer().getMegaphoneMuteState()) {
                    final List<String> lines = new LinkedList<>();
                    if (itemId == 5390009) { //friend finder megaphone
                        lines.add("I'm looking for ");
                        lines.add("friends! Send a ");
                        lines.add("Friend Request if ");
                        lines.add("you're intetested!");
                    } else {
                        for (int i = 0; i < 4; i++) {
                            final String text = iPacket.DecodeString();
                            if (text.length() > 55) {
                                continue;
                            }
                            lines.add(text);
                        }
                    }
                    final boolean ear = iPacket.DecodeByte() != 0;
                    //World.Broadcast.broadcastSmega(CWvsContext.getAvatarMega(c.getPlayer(), c.getChannel(), itemId, lines, ear));
                    bUsed = true;
                } else {
                    c.getPlayer().dropMessage(5, "The usage of Megaphone is currently disabled.");
                }
                break;
            }
            case 5452001:
            case 5450006:
            case 5450007:
            case 5450013:
            case 5450003:
            case 5450000: { // Mu Mu the Travelling Merchant
                for (int i : GameConstants.blockedMaps) {
                    if (c.getPlayer().getMapId() == i) {
                        c.getPlayer().dropMessage(5, "You may not use this here.");
                        c.SendPacket(WvsContext.enableActions());
                        return;
                    }
                }
                if (c.getPlayer().getLevel() < 10) {
                    c.getPlayer().dropMessage(5, "You must be over level 10 to use this.");
                } else if (c.getPlayer().hasBlockedInventory() || c.getPlayer().getMap().getSquadByMap() != null || c.getPlayer().getEventInstance() != null || c.getPlayer().getMap().getEMByMap() != null || c.getPlayer().getMapId() >= 990000000) {
                    c.getPlayer().dropMessage(5, "You may not use this here.");
                } else if ((c.getPlayer().getMapId() >= 680000210 && c.getPlayer().getMapId() <= 680000502) || (c.getPlayer().getMapId() / 1000 == 980000 && c.getPlayer().getMapId() != 980000000) || (c.getPlayer().getMapId() / 100 == 1030008) || (c.getPlayer().getMapId() / 100 == 922010) || (c.getPlayer().getMapId() / 10 == 13003000)) {
                    c.getPlayer().dropMessage(5, "You may not use this here.");
                } else {
                    ShopFactory.getInstance().getShop(9090000).sendShop(c);
                }
                //used = true;
                break;
            }
            case 5300000:
            case 5300001:
            case 5300002: { // Cash morphs
                final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                ii.getItemEffect(itemId).applyTo(c.getPlayer());
                bUsed = true;
                break;
            }
            case 5781000: { //pet color dye
                iPacket.DecodeInt();
                iPacket.DecodeInt();
                int color = iPacket.DecodeInt();

                break;
            }
            default:
                if (itemId / 10000 == 524 || itemId / 10000 == 546) { //Pet food & snacks

                    bUsed = UsePetFood(c, itemId);
                    break;
                }
                if (itemId / 10000 == 512) {
                    if (!FieldLimitType.UnableToUseCashWeather.check(c.getPlayer().getMap())) {
                        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
                        String msg = ii.getMsg(itemId);
                        final String ourMsg = iPacket.DecodeString();
                        if (!msg.contains("%s")) {
                            msg = ourMsg;
                        } else {
                            msg = msg.replaceFirst("%s", c.getPlayer().getName());
                            if (!msg.contains("%s")) {
                                msg = ii.getMsg(itemId).replaceFirst("%s", ourMsg);
                            } else {
                                try {
                                    msg = msg.replaceFirst("%s", ourMsg);
                                } catch (Exception e) {
                                    msg = ii.getMsg(itemId).replaceFirst("%s", ourMsg);
                                }
                            }
                        }
                        c.getPlayer().getMap().startMapEffect(msg, itemId);

                        final int buff = ii.getStateChangeItem(itemId);
                        if (buff != 0) {
                            for (User mChar : c.getPlayer().getMap().getCharacters()) {
                                ii.getItemEffect(buff).applyTo(mChar);
                            }
                        }
                        bUsed = true;
                    }
                } else if (itemId / 10000 == 510) {
                    c.getPlayer().getMap().startJukebox(c.getPlayer().getName(), itemId);
                    bUsed = true;
                } else if (itemId / 10000 == 520) {
                    final int mesars = MapleItemInformationProvider.getInstance().getMeso(itemId);
                    if (mesars > 0 && c.getPlayer().getMeso() < (Integer.MAX_VALUE - mesars)) {
                        bUsed = true;
                        if (Math.random() > 0.1) {
                            final int gainmes = Randomizer.nextInt(mesars);
                            c.getPlayer().gainMeso(gainmes, false);
                            c.SendPacket(CSPacket.sendMesobagSuccess(gainmes));
                        } else {
                            c.SendPacket(CSPacket.sendMesobagFailed(false)); // not random
                        }
                    }
                } else if (itemId / 10000 == 562) {
                    if (UseSkillBookHandler.UseSkillBook(c, slot, itemId)) {
                        c.getPlayer().gainSP(1);
                    } //this should handle removing
                } else if (itemId / 10000 == 553) {
                    UseRewardItem(slot, itemId, false, c, c.getPlayer());// this too
                } else if (itemId / 10000 != 519) {
                    LogHelper.GENERAL_EXCEPTION.get().info("Unhandled CS item : " + itemId);
                }
                break;
        }

        if (bUsed) {
            MapleInventoryManipulator.removeFromSlot(c, MapleInventoryType.CASH, slot, (short) 1, false, true);
        }
        c.SendPacket(WvsContext.enableActions());
        if (cc) {
            if (!c.getPlayer().isAlive() || c.getPlayer().getEventInstance() != null || FieldLimitType.UnableToMigrate.check(c.getPlayer().getMap())) {
                c.getPlayer().dropMessage(1, "Auto relog failed.");
                return;
            }
            c.getPlayer().dropMessage(5, "Auto relogging. Please wait.");
            c.getPlayer().reloadUser();
            if (c.getPlayer().getScrolledPosition() != 0) {
                c.SendPacket(WvsContext.pamSongUI());
            }
        }
    }

    private static void addMedalString(final User c, final StringBuilder sb) {
        final Item medal = c.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -49);
        if (medal != null) { // Medal
            sb.append("<");
            sb.append(MapleItemInformationProvider.getInstance().getName(medal.getItemId()));
        }
        sb.append("> ");
    }

    private static boolean getIncubatedItems(ClientSocket c, int itemId) {
        if (c.getPlayer().getInventory(MapleInventoryType.EQUIP).getNumFreeSlot() < 2 || c.getPlayer().getInventory(MapleInventoryType.USE).getNumFreeSlot() < 2 || c.getPlayer().getInventory(MapleInventoryType.SETUP).getNumFreeSlot() < 2) {
            c.getPlayer().dropMessage(5, "Please make room in your inventory.");
            return false;
        }
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        int id1 = RandomRewards.getPeanutReward(), id2 = RandomRewards.getPeanutReward();
        while (!ii.itemExists(id1)) {
            id1 = RandomRewards.getPeanutReward();
        }
        while (!ii.itemExists(id2)) {
            id2 = RandomRewards.getPeanutReward();
        }
        c.SendPacket(WvsContext.getPeanutResult(id1, (short) 1, id2, (short) 1, itemId));
        MapleInventoryManipulator.addById(c, id1, (short) 1, ii.getName(itemId) + " on " + LocalDateTime.now());
        MapleInventoryManipulator.addById(c, id2, (short) 1, ii.getName(itemId) + " on " + LocalDateTime.now());
        c.SendPacket(CField.NPCPacket.getNPCTalk(1090000, NPCChatType.OK, "You have obtained the following items:\r\n#i" + id1 + "##z" + id1 + "#\r\n#i" + id2 + "##z" + id2 + "#", NPCChatByType.NPC_Cancellable));
        return true;
    }

    private static boolean magnifyEquip(final ClientSocket c, Item magnify, Item toReveal, byte eqSlot) {
        /*      final boolean insight = c.getPlayer().getTrait(MapleTrait.MapleTraitType.sense).getLevel() >= 30;
        final Equip eqq = (Equip) toReveal;
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        final int reqLevel = ii.getReqLevel(eqq.getItemId()) / 10;
        if (eqq.getState() == 1 && (insight || magnify.getItemId() == 2460003 || (magnify.getItemId() == 2460002 && reqLevel <= 12) || (magnify.getItemId() == 2460001 && reqLevel <= 7) || (magnify.getItemId() == 2460000 && reqLevel <= 3))) {
            final List<List<ItemPotentialOption>> pots = new LinkedList<>(ii.getAllPotentialInfo().values());
            int lockedLine = 0;
            int locked = 0;
            if (Math.abs(eqq.getPotential1()) / 100000 > 0) {
                lockedLine = 1;
                locked = Math.abs(eqq.getPotential1());
            } else if (Math.abs(eqq.getPotential2()) / 100000 > 0) {
                lockedLine = 2;
                locked = Math.abs(eqq.getPotential2());
            } else if (Math.abs(eqq.getPotential3()) / 100000 > 0) {
                lockedLine = 3;
                locked = Math.abs(eqq.getPotential3());
            }
            int new_state = Math.abs(eqq.getPotential1());
            if (lockedLine == 1) {
                new_state = locked / 10000 < 1 ? 17 : 16 + locked / 10000;
            }
            if (new_state > 20 || new_state < 17) { // incase overflow
                new_state = 17;
            }
            int lines = 2; // default
            if (eqq.getPotential2() != 0) {
                lines++;
            }
            while (eqq.getState() != new_state) {
                //31001 = haste, 31002 = door, 31003 = se, 31004 = hb, 41005 = combat orders, 41006 = advanced blessing, 41007 = speed infusion
                for (int i = 0; i < lines; i++) { // minimum 2 lines, max 3
                    boolean rewarded = false;
                    while (!rewarded) {
                        ItemPotentialOption pot = pots.get(Randomizer.nextInt(pots.size())).get(reqLevel);
                        if (pot != null && pot.reqLevel / 10 <= reqLevel && GameConstants.optionTypeFits(pot.optionType, eqq.getItemId()) && GameConstants.potentialIDFits(pot.opID, new_state, i)) { //optionType

                            //have to research optionType before making this truely official-like
                            if (isAllowedPotentialStat(eqq, pot.opID)) {
                                if (i == 0) {
                                    eqq.setPotential1(pot.opID);
                                } else if (i == 1) {
                                    eqq.setPotential2(pot.opID);
                                } else if (i == 2) {
                                    eqq.setPotential3(pot.opID);
                                } else if (i == 3) {
                                    eqq.setPotential4(pot.opID);
                                }
                                rewarded = true;
                            }
                        }
                    }
                }
            }
            switch (lockedLine) {
                case 1:
                    eqq.setPotential1(Math.abs(locked - lockedLine * 100000));
                    break;
                case 2:
                    eqq.setPotential2(Math.abs(locked - lockedLine * 100000));
                    break;
                case 3:
                    eqq.setPotential3(Math.abs(locked - lockedLine * 100000));
                    break;
            }
            c.getPlayer().getTrait(MapleTrait.MapleTraitType.insight).addExp((insight ? 10 : ((magnify.getItemId() + 2) - 2460000)) * 2, c.getPlayer());
            c.getPlayer().getMap().broadcastMessage(CField.showMagnifyingEffect(c.getPlayer().getId(), eqq.getPosition()));
            if (!insight) {
                List<ModifyInventory> modifications = new ArrayList<>();
                modifications.add(new ModifyInventory(ModifyInventoryOperation.Remove, magnify));
                modifications.add(new ModifyInventory(ModifyInventoryOperation.AddItem, toReveal));
                c.write(CWvsContext.inventoryOperation(true, modifications));
            } else {
                c.getPlayer().forceReAddItem(toReveal, eqSlot >= 0 ? MapleInventoryType.EQUIP : MapleInventoryType.EQUIPPED);
            }
            return true;
        } else {
            return false;
        }*/
        return false;
    }
}
