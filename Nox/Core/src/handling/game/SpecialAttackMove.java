package handling.game;

import client.*;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import constants.skills.*;
import handling.jobs.Explorer;
import handling.jobs.Explorer.HeroHandler;
import static handling.jobs.Hero.AranHandler.handleAdrenalineRush;
import handling.jobs.Resistance.BlasterHandler;
import net.InPacket;
import netty.ProcessPacket;
import server.MapleInventoryManipulator;
import server.MapleStatEffect;
import server.Randomizer;
import server.events.MapleEvent;
import server.events.MapleEventType;
import server.life.MapleMonster;
import server.maps.FieldLimitType;
import server.maps.MapleMapItem;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.objects.MapleCharacter;
import server.maps.objects.MapleForceAtom;
import server.maps.objects.MapleForceAtomTypes;
import server.quest.MapleQuest;
import service.ChannelServer;
import tools.packet.*;
import tools.packet.CField.EffectPacket.UserEffectCodes;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.MapleStatInfo;
import server.Timer;
import server.Timer.MapTimer;
import server.maps.SummonMovementType;
import server.maps.objects.MapleSummon;
import tools.packet.JobPacket.BeastTamerPacket;
import tools.packet.JobPacket.BlasterPacket;

public final class SpecialAttackMove implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        MapleCharacter pPlayer = c.getPlayer();
        pPlayer.updateTick(iPacket.DecodeInteger());
        int nSkill = iPacket.DecodeInteger();
        
        switch (nSkill) {
            case 14110030:
            case 25111209:
                // These skills consistantly spam packets, they do not need to be handled here.
                break;
            default:
                if (pPlayer.isDeveloper()) {
                    pPlayer.dropMessage(5, "[SpecialAttackMove Debug] Skill ID : " + nSkill);
                }
                break;
        }
        
        if (GameConstants.isDisabledSkill(nSkill)) { // Just for broken skills that we want disabled.
            //pPlayer.dropMessage(5, "Sorry, this skill is currently disabled.");
            //c.write(CWvsContext.enableActions());
            //return;
        }

        // Special Case Toggle Skills
     // These toggles are technically buffs, but do not get handled by the buff manager. -Mazen
        switch (nSkill) {
            
            case 37000010: {
                BlasterHandler.handleCylinderReload(pPlayer);
                break;
            }
            
            case BeastTamer.BEAR_MODE:
            case BeastTamer.SNOW_LEOPARD_MODE:
            case BeastTamer.HAWK_MODE:
            case BeastTamer.CAT_MODE: {
                pPlayer.getClient().write(BeastTamerPacket.AnimalMode(nSkill));
                break;
            }
            
            case 14110030:{
                if (!GameConstants.isNightWalkerCygnus(pPlayer.getJob())) {
                    return;
                }
                final MapleStatEffect buffEffects = SkillFactory.getSkill(14110030).getEffect(pPlayer.getTotalSkillLevel(14110030));
                buffEffects.statups.put(CharacterTemporaryStat.DarknessAscension, 1);
                pPlayer.registerEffect(buffEffects, System.currentTimeMillis(), null, buffEffects.statups, false, 2100000000, pPlayer.getId());
                pPlayer.getClient().write(BuffPacket.giveBuff(pPlayer, 14110030, 2100000000, buffEffects.statups, buffEffects));
                break;
            }
            
            case Fighter.COMBO_ATTACK:{
                if (!GameConstants.isWarriorHero(pPlayer.getJob())) {
                    return;
                }
                final MapleStatEffect buffEffects = SkillFactory.getSkill(Fighter.COMBO_ATTACK).getEffect(pPlayer.getTotalSkillLevel(Fighter.COMBO_ATTACK));
                buffEffects.statups.put(CharacterTemporaryStat.ComboCounter, 0);
                pPlayer.registerEffect(buffEffects, System.currentTimeMillis(), null, buffEffects.statups, false, 2100000000, pPlayer.getId());
                pPlayer.getClient().write(BuffPacket.giveBuff(pPlayer, Fighter.COMBO_ATTACK, 2100000000, buffEffects.statups, buffEffects));
                HeroHandler.setComboAttack(pPlayer, 0);
                break;
            }
            
            case Xenon.PINPOINT_SALVO:{
                if (!GameConstants.isXenon(pPlayer.getJob())) {
                    return;
                }
                final MapleStatEffect buffEffects = SkillFactory.getSkill(Xenon.PINPOINT_SALVO).getEffect(pPlayer.getTotalSkillLevel(Xenon.PINPOINT_SALVO));
                buffEffects.statups.put(CharacterTemporaryStat.HollowPointBullet, buffEffects.info.get(MapleStatInfo.x));
                pPlayer.registerEffect(buffEffects, System.currentTimeMillis(), null, buffEffects.statups, false, 2100000000, pPlayer.getId());
                pPlayer.getClient().write(BuffPacket.giveBuff(pPlayer, Xenon.PINPOINT_SALVO, 2100000000, buffEffects.statups, buffEffects));
                break;
            }
            
            case Xenon.AEGIS_SYSTEM:
            case Xenon.AEGIS_SYSTEM_1 :{
                if (!GameConstants.isXenon(pPlayer.getJob())) {
                    return;
                }
                final MapleStatEffect buffEffects = SkillFactory.getSkill(Xenon.AEGIS_SYSTEM).getEffect(pPlayer.getTotalSkillLevel(Xenon.AEGIS_SYSTEM));
                buffEffects.statups.put(CharacterTemporaryStat.XenonAegisSystem, buffEffects.info.get(MapleStatInfo.x));
                pPlayer.registerEffect(buffEffects, System.currentTimeMillis(), null, buffEffects.statups, false, 2100000000, pPlayer.getId());
                pPlayer.getClient().write(BuffPacket.giveBuff(pPlayer, Xenon.AEGIS_SYSTEM, 2100000000, buffEffects.statups, buffEffects));
                break;
            }
            
            case Aran.BODY_PRESSURE:{
                if (!GameConstants.isAran(pPlayer.getJob())) {
                    return;
                }
                final MapleStatEffect buffEffects = SkillFactory.getSkill(Aran.BODY_PRESSURE).getEffect(pPlayer.getTotalSkillLevel(Aran.BODY_PRESSURE));
                buffEffects.statups.put(CharacterTemporaryStat.PowerGuard, buffEffects.info.get(MapleStatInfo.x));
                pPlayer.registerEffect(buffEffects, System.currentTimeMillis(), null, buffEffects.statups, false, 2100000000, pPlayer.getId());
                pPlayer.getClient().write(BuffPacket.giveBuff(pPlayer, Aran.BODY_PRESSURE, 2100000000, buffEffects.statups, buffEffects));
                break;
            }
            
            case BattleMage.CONDEMNATION:
            case BattleMage.GRIM_CONTRACT:
            case BattleMage.GRIM_CONTRACT_2:
            case BattleMage.GRIM_CONTRACT_3: {
                if (!GameConstants.isBattleMage(pPlayer.getJob())) {
                    return;
                }
                final MapleStatEffect buffEffects = SkillFactory.getSkill(BattleMage.CONDEMNATION).getEffect(pPlayer.getTotalSkillLevel(BattleMage.CONDEMNATION));
                buffEffects.statups.put(CharacterTemporaryStat.BMageDeath, buffEffects.info.get(MapleStatInfo.x));
                pPlayer.registerEffect(buffEffects, System.currentTimeMillis(), null, buffEffects.statups, false, 2100000000, pPlayer.getId());
                pPlayer.getClient().write(BuffPacket.giveBuff(pPlayer, BattleMage.CONDEMNATION, 2100000000, buffEffects.statups, buffEffects));
                break;
            }
            
            case NightWalker.SHADOW_BAT:
            case NightWalker.SHADOW_BAT_2:
            case NightWalker.SHADOW_BAT_3: {
                if (!GameConstants.isNightWalkerCygnus(pPlayer.getJob())) {
                    return;
                }
                if (!pPlayer.hasBuff(CharacterTemporaryStat.NightWalkerBat)) {
                    final MapleStatEffect buffEffects = SkillFactory.getSkill(NightWalker.SHADOW_BAT).getEffect(pPlayer.getTotalSkillLevel(NightWalker.SHADOW_BAT));
                    buffEffects.statups.put(CharacterTemporaryStat.NightWalkerBat, buffEffects.info.get(MapleStatInfo.x));
                    pPlayer.registerEffect(buffEffects, System.currentTimeMillis(), null, buffEffects.statups, false, 2100000000, pPlayer.getId());
                    pPlayer.getClient().write(BuffPacket.giveBuff(pPlayer, NightWalker.SHADOW_BAT, 2100000000, buffEffects.statups, buffEffects));
                } else {
                    pPlayer.dispelBuff(NightWalker.SHADOW_BAT);
                }
                break;
            }
            
            case Shade.FOX_SPIRITS:{
                if (!GameConstants.isShade(pPlayer.getJob())) {
                    return;
                }
                final MapleStatEffect buffEffects = SkillFactory.getSkill(Shade.FOX_SPIRITS).getEffect(pPlayer.getTotalSkillLevel(Shade.FOX_SPIRITS));
                buffEffects.statups.put(CharacterTemporaryStat.ChangeFoxMan, (int) 1);
                pPlayer.registerEffect(buffEffects, System.currentTimeMillis(), null, buffEffects.statups, false, 2100000000, pPlayer.getId());
                pPlayer.getClient().write(BuffPacket.giveBuff(pPlayer, Shade.FOX_SPIRITS, 2100000000, buffEffects.statups, buffEffects));
                break;
            }
            
            case WindArcher.TRIFLING_WIND_1:
            case WindArcher.TRIFLING_WIND_2:
            case WindArcher.TRIFLING_WIND_3:{
                if (!GameConstants.isWindArcherCygnus(pPlayer.getJob())) {
                    return;
                }
                final MapleStatEffect buffEffects = SkillFactory.getSkill(WindArcher.TRIFLING_WIND_1).getEffect(pPlayer.getTotalSkillLevel(WindArcher.TRIFLING_WIND_1));
                buffEffects.statups.put(CharacterTemporaryStat.TriflingWhimOnOff, buffEffects.info.get(MapleStatInfo.x));
                pPlayer.registerEffect(buffEffects, System.currentTimeMillis(), null, buffEffects.statups, false, 2100000000, pPlayer.getId());
                pPlayer.getClient().write(BuffPacket.giveBuff(pPlayer, WindArcher.TRIFLING_WIND_1, 2100000000, buffEffects.statups, buffEffects));
                break;
            }
        }

        // Kinesis Psychic Points handling.
        if (GameConstants.isKinesis(pPlayer.getJob())) {
           handling.jobs.Kinesis.KinesisHandler.handlePsychicPoint(pPlayer, nSkill);
        }

        if (nSkill >= 91000000 && nSkill < 100000000) {
            c.write(CWvsContext.enableActions());
            return;
        }
        if (nSkill == 23111008) {
            nSkill += Randomizer.nextInt(2);
        }
        int xy1 = 0;
        int xy2 = 0;
        if (nSkill == AngelicBuster.SOUL_SEEKER) {
            xy1 = iPacket.DecodeShort();
            xy2 = iPacket.DecodeShort();
            int soulnum = iPacket.DecodeByte();
            int scheck = 0;
            int scheck2 = 0;
            if (soulnum == 1) {
                scheck = iPacket.DecodeInteger();
            } else if (soulnum == 2) {
                scheck = iPacket.DecodeInteger();
                scheck2 = iPacket.DecodeInteger();
            }
            c.write(JobPacket.AngelicPacket.SoulSeeker(pPlayer, nSkill, soulnum, scheck, scheck2));
            c.write(JobPacket.AngelicPacket.unlockSkill());
            c.write(JobPacket.AngelicPacket.showRechargeEffect());
            c.write(CWvsContext.enableActions());
            return;
        }

        if (nSkill >= 100000000) {
            iPacket.DecodeByte(); //zero
        }
        int skillLevel = iPacket.DecodeByte();
        Skill skill = SkillFactory.getSkill(nSkill);
        if ((skill == null) || ((GameConstants.isAngel(nSkill)) && (pPlayer.getStat().equippedSummon % 10000 != nSkill % 10000)) || ((pPlayer.inPVP()) && (skill.isPVPDisabled()))) {
            c.write(CWvsContext.enableActions());
            return;
        }
        int levelCheckSkill = 0;
        if ((GameConstants.isPhantom(pPlayer.getJob())) && (!GameConstants.isPhantom(nSkill / 10000))) {
            int skillJob = nSkill / 10000;
            if (skillJob % 100 == 0) {
                levelCheckSkill = 24001001;
            } else if (skillJob % 10 == 0) {
                levelCheckSkill = 24101001;
            } else if (skillJob % 10 == 1) {
                levelCheckSkill = 24111001;
            } else {
                levelCheckSkill = 24121001;
            }
        }
        if ((levelCheckSkill == 0) && ((pPlayer.getTotalSkillLevel(GameConstants.getLinkedAttackSkill(nSkill)) <= 0) || (pPlayer.getTotalSkillLevel(GameConstants.getLinkedAttackSkill(nSkill)) != skillLevel))) {
            if ((!GameConstants.isMulungSkill(nSkill)) && (!GameConstants.isPyramidSkill(nSkill)) && (pPlayer.getTotalSkillLevel(GameConstants.getLinkedAttackSkill(nSkill)) <= 0) && !GameConstants.isAngel(nSkill)) {
                c.write(CWvsContext.enableActions());
                return;
            }
            if (GameConstants.isMulungSkill(nSkill)) {
                if (pPlayer.getMapId() / 10000 != 92502) {
                    return;
                }
                if (pPlayer.getMulungEnergy() < 10000) {
                    return;
                }
                pPlayer.mulungEnergyModifier(false);
            } else if ((GameConstants.isPyramidSkill(nSkill))
                    && (pPlayer.getMapId() / 10000 != 92602) && (pPlayer.getMapId() / 10000 != 92601)) {
                return;
            }
        }
        if (GameConstants.isEventMap(pPlayer.getMapId())) {
            for (MapleEventType t : MapleEventType.values()) {
                MapleEvent e = ChannelServer.getInstance(pPlayer.getClient().getChannel()).getEvent(t);
                if ((e.isRunning()) && (!pPlayer.isGM())) {
                    for (int i : e.getType().mapids) {
                        if (pPlayer.getMapId() == i) {
                            pPlayer.dropMessage(5, "You may not use that here.");
                            return;
                        }
                    }
                }
            }
        }
        skillLevel = pPlayer.getTotalSkillLevel(GameConstants.getLinkedAttackSkill(nSkill));
        MapleStatEffect effect = pPlayer.inPVP() ? skill.getPVPEffect(skillLevel) : skill.getEffect(skillLevel);
        if ((effect.isMPRecovery()) && (pPlayer.getStat().getHp() < pPlayer.getStat().getMaxHp() / 100 * 10)) {
            c.getPlayer().dropMessage(5, "You do not have the HP to use this skill.");
            c.write(CWvsContext.enableActions());
            return;
        }
        if (effect.getCooldown(pPlayer) > 0 && nSkill != 24121005) {
            if (pPlayer.skillisCooling(nSkill)) {
                c.write(CWvsContext.enableActions());
                return;
            }
            if ((nSkill != 5221006) && (nSkill != 35111002)) {
                pPlayer.addCooldown(nSkill, System.currentTimeMillis(), effect.getCooldown(pPlayer));
            }
        }
        int nMob;
        MapleMonster pMob;
        switch (nSkill) {
            
            case WildHunter.SOUL_ARROW_CROSSBOW: {
                c.write(CField.SummonPacket.jaguarActive(true));
                break;
            }
            
            case WildHunter.JAGUAR_RIDER:{
                /*final MapleStatEffect pEffect = SkillFactory.getSkill(WildHunter.JAGUAR_RIDER).getEffect(pPlayer.getTotalSkillLevel(WildHunter.JAGUAR_RIDER));
                MapleSummon pSummon = new MapleSummon(pPlayer, pEffect, pPlayer.getPosition(), SummonMovementType.SUMMON_JAGUAR, 2100000000);
                pSummon.setPosition(pPlayer.getPosition());
                pPlayer.getMap().spawnSummon(pSummon);
                pEffect.applyTo(pPlayer, pPlayer.getPosition());*/
                c.write(CField.SummonPacket.jaguarActive(true));
                break;
            }
            
            case SuperGM.RESURRECTION: {
                for (MapleCharacter pCharacter : pPlayer.getMap().getCharacters()) {
                    if (pCharacter != null) {
                        pCharacter.getStat().setHp(pCharacter.getStat().getMaxHp(), pCharacter);
                        pCharacter.updateSingleStat(MapleStat.HP, pCharacter.getStat().getMaxHp());
                        pCharacter.getStat().setMp(pCharacter.getStat().getMaxMp(), pCharacter);
                        pCharacter.updateSingleStat(MapleStat.MP, pCharacter.getStat().getMaxMp());
                        pCharacter.dispelDebuffs();
                    }
                }
                c.getPlayer().dropMessage(6, "You have resurrected all players in the current map.");
                break;
            }
            case Aran.ADRENALINE_BURST: {
                pPlayer.setLastCombo(System.currentTimeMillis());
                pPlayer.setComboStack((short) 1000);
                pPlayer.getClient().write(CField.updateCombo(1000));
                handleAdrenalineRush(pPlayer);
                break;
            }
            case Xenon.EMERGENCY_RESUPPLY: {
                pPlayer.gainXenonSurplus((short) 10);
                c.getPlayer().dropMessage(6, "You have recharged your surplus power supplies.");
                break;
            }
            case 1121001:
            case 1221001:
            case 1321001:
            case 9001020:
            case 9101020:
            case DemonSlayer.RAVEN_STORM:
                byte mobCount = iPacket.DecodeByte();
                iPacket.Skip(3);
                for (int i = 0; i < mobCount; i++) {
                    int mobId = iPacket.DecodeInteger();

                    pMob = pPlayer.getMap().getMonsterByOid(mobId);
                    if (pMob == null) {
                        continue;
                    }
                    pMob.switchController(pPlayer, pMob.isControllerHasAggro());
                    pMob.applyStatus(pPlayer, new MonsterStatusEffect(MonsterStatus.STUN, 1, nSkill, null, false), false, effect.getDuration(), true, effect);
                }

                pPlayer.getMap().broadcastMessage(pPlayer, CField.EffectPacket.showBuffeffect(pPlayer.getId(), nSkill, UserEffectCodes.SkillUse, pPlayer.getLevel(), skillLevel, iPacket.DecodeByte()), pPlayer.getTruePosition());
                break;

            /*case 33001011: {
                Point mpos = iPacket.DecodePosition();
                MapleSummon summon = new MapleSummon(pPlayer, effect, mpos, SummonMovementType.SUMMON_JAGUAR, effect.getDuration());
                summon.setPosition(mpos);
                pPlayer.getMap().spawnSummon(summon);
                MapleStatEffect buffeffect = SkillFactory.getSkill(33001007).getEffect(skillLevel);
                buffeffect.applyTo(pPlayer, null);
                break;
            }*/

            /*case NightWalker.SHADOW_BAT:
            case NightWalker.SHADOW_BAT_1:
            case NightWalker.SHADOW_BAT_2:
            case NightWalker.SHADOW_BAT_3: { 
                Point mpos = iPacket.DecodePosition();
                MapleSummon summon = new MapleSummon(chr, effect, mpos, SummonMovementType.FOLLOW, effect.getDuration());
                summon.setPosition(mpos);
                chr.getMap().spawnSummon(summon);
                MapleStatEffect buffeffect = SkillFactory.getSkill(NightWalker.SHADOW_BAT).getEffect(skillLevel);
                buffeffect.applyTo(chr, null);
                break;
            }*/
            
            case NightWalker.DARK_OMEN: {
                for (int i = 0; i <= 5; i++) {
                    pPlayer.getMap().broadcastMessage(JobPacket.NightWalkerPacket.ShadowBats(pPlayer.getId(), pPlayer.getObjectId()));
                }
                break;
            }
            
            case 4221054:
                pPlayer.getMap().broadcastMessage(CField.OnOffFlipTheCoin(false));
                pPlayer.dualBrid = 0;
                break;
            case Citizen.CAPTURE:
                nMob = iPacket.DecodeInteger();
                pMob = pPlayer.getMap().getMonsterByOid(nMob);
                if (pMob != null) {
                    boolean success = (pMob.getHp() <= pMob.getMobMaxHp() / 2L) && (pMob.getId() >= 9304000) && (pMob.getId() < 9305000);
                    pPlayer.getMap().broadcastMessage(pPlayer, CField.EffectPacket.showBuffeffect(pPlayer.getId(), nSkill, UserEffectCodes.SkillUse, pPlayer.getLevel(), skillLevel, (byte) (success ? 1 : 0)), pPlayer.getTruePosition());
                    if (success) {
                        pPlayer.getQuestNAdd(MapleQuest.getInstance(GameConstants.JAGUAR)).setCustomData(String.valueOf((pMob.getId() - 9303999) * 10));
                        pPlayer.getMap().killMonster(pMob, pPlayer, true, false, (byte) 1);
                        pPlayer.cancelEffectFromTemporaryStat(CharacterTemporaryStat.RideVehicle);
                        c.write(CWvsContext.updateJaguar(pPlayer));
                    } else {
                        pPlayer.dropMessage(5, "The monster has too much physical strength, so you cannot catch it.");
                    }
                }
                break;
            case Citizen.CALL_OF_THE_HUNTER:
                pPlayer.dropMessage(5, "No monsters can be summoned. Capture a monster first.");
                break;
            case DemonAvenger.NETHER_SHIELD:
            case Xenon.PINPOINT_SALVO:
            case FPArchmage.MEGIDDO_FLAME: {
                List<Integer> moblist = new ArrayList<>();
                byte count = iPacket.DecodeByte();
                for (byte i = 1; i <= count; i++) {
                    moblist.add(iPacket.DecodeInteger());
                }
                switch (nSkill) {
                    case DemonAvenger.NETHER_SHIELD:
                        c.write(JobPacket.XenonPacket.ShieldChacing(pPlayer.getId(), moblist, 31221014));
                        break;
                    case Xenon.PINPOINT_SALVO:
                        c.write(JobPacket.XenonPacket.PinPointRocket(pPlayer.getId(), moblist));
                        break;
                    case FPArchmage.MEGIDDO_FLAME:
                        c.write(JobPacket.XenonPacket.MegidoFlameRe(pPlayer.getId(), moblist.get(0)));
                        break;
                    default:
                        break;
                }
                break;
            }
            case WildHunter.CALL_OF_THE_WILD:
                nMob = pPlayer.getFirstLinkMid();
                pMob = pPlayer.getMap().getMonsterByOid(nMob);
                pPlayer.setKeyDownSkillTime(0L);
                pPlayer.getMap().broadcastMessage(pPlayer, CField.skillCancel(pPlayer, nSkill), false);
                if (pMob != null) {
                    boolean success = (pMob.getStats().getLevel() < pPlayer.getLevel()) && (pMob.getId() < 9000000) && (!pMob.getStats().isBoss());
                    if (success) {
                        pPlayer.getMap().broadcastMessage(MobPacket.suckMonster(pMob.getObjectId(), pPlayer.getId()));
                        pPlayer.getMap().killMonster(pMob, pPlayer, false, false, (byte) -1);
                    } else {
                        pPlayer.dropMessage(5, "The monster has too much physical strength, so you cannot catch it.");
                    }
                } else {
                    pPlayer.dropMessage(5, "No monster was sucked. The skill failed.");
                }
                break;
            case Luminous.SUNFIRE:
            case Luminous.ECLIPSE:
            case Luminous.EQUILIBRIUM_1:
            case Luminous.CHANGE_LIGHTDARK_MODE:
                pPlayer.changeLuminousMode();
                c.write(CWvsContext.enableActions());
                break;
            case DawnWarrior.FALLING_MOON:
            case DawnWarrior.RISING_SUN:
            case DawnWarrior.EQUINOX_CYCLE:
                pPlayer.changeWarriorStance(nSkill);
                break;
            case Xenon.AMARANTH_GENERATOR: {
                /*final EnumMap<CharacterTemporaryStat, Integer> stat = new EnumMap<>(CharacterTemporaryStat.class);
                stat.put(CharacterTemporaryStat.SurplusSupply, 0);
                c.write(BuffPacket.giveBuff(chr, 36121054, skillLevel, stat, effect));
                c.write(CWvsContext.enableActions());
                c.write(JobPacket.XenonPacket.giveAmaranthGenerator());*/

                // Imitates the effects of the Amaranth Generator.
                // Very hacky fix, but it works ;) -Mazen
                try {
                    pPlayer.handleXenonGenerator();
                } catch (Exception ex) {
                    Logger.getLogger(SpecialAttackMove.class.getName()).log(Level.SEVERE, null, ex);
                }
                c.write(CWvsContext.enableActions());
                break;
            }
            /*case 110001500:
            case 110001501:
            case 110001502:
            case 110001503:
            case 110001504: {
                //Change animal mode
                //iPacket.Skip(3);
                final EnumMap<CharacterTemporaryStat, Integer> stat = new EnumMap<>(CharacterTemporaryStat.class);
                stat.put(CharacterTemporaryStat.AnimalChange, 0);
                c.write(BuffPacket.giveBuff(pPlayer, nSkill, skillLevel, stat, effect));
                break;
            }*/
            case 4341003:
                pPlayer.setKeyDownSkillTime(0);
                pPlayer.getMap().broadcastMessage(pPlayer, CField.skillCancel(pPlayer, nSkill), false);
                break;
            case ChiefBandit.MESO_EXPLOSION:
                int counter = 0;
                MapleForceAtom atom = new MapleForceAtom(MapleForceAtomTypes.MESOEXPLOSION_BOTH);
                atom.setCharId(pPlayer.getId());
                atom.setByMob(false);
                atom.setToMob(true);
                atom.setSkillId(ChiefBandit.MESO_EXPLOSION_1);
                atom.getPosition().setLocation(pPlayer.getPosition()); //might be different for meso explosion, it might be the meso bag position
                for (MapleMapObject m : c.getPlayer().getMap().getMapObjectsInRange(c.getPlayer().getPosition(), 19990.0, Arrays.asList(MapleMapObjectType.ITEM))) {
                    MapleMapItem mapitem = (MapleMapItem) m;
                    mapitem.getLock().lock();
                    try {
                        if (counter <= 5 + skillLevel) {
                            if (mapitem.getMeso() > 0 && !mapitem.isPickedUp() && mapitem.isPickpocketDrop()) {
                                pPlayer.getMap().removeMapObject(mapitem);
                                mapitem.setPickedUp(true);
                                pPlayer.write(CField.removeItemFromMap(m.getObjectId(), 0, pPlayer.getId()));
                                pPlayer.getMap().broadcastMessage(CField.removeItemFromMap(m.getObjectId(), 0, pPlayer.getId()));
                                atom.getObjects().add(m.getObjectId());
                                counter++;
                            }
                        }
                    } finally {
                        mapitem.getLock().unlock();
                    }
                }
                if (counter > 0) {
                    atom.setAttackCount(counter);
                    c.write(JobPacket.createForceAtom(atom));
                }
                break;
            default: {
                Point pos = null;
                if ((iPacket.Available() == 5L) || (iPacket.Available() == 7L)) {
                    pos = iPacket.DecodePosition();
                }
                int mountid = MapleStatEffect.parseMountInfo(c.getPlayer(), skill.getId());

                if ((effect.isMagicDoor() && !FieldLimitType.UnableToUseMysticDoor.check(pPlayer.getMap())) // check magic door
                        // check mount req
                        || (mountid != 0 && (mountid != GameConstants.getMountItem(skill.getId(), c.getPlayer())) && (!c.getPlayer().isIntern()) && (c.getPlayer().getBuffedValue(CharacterTemporaryStat.RideVehicle) == null) && (c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -122) == null) && (!GameConstants.isMountItemAvailable(mountid, c.getPlayer().getJob())))) {
                    c.write(CWvsContext.enableActions());
                    return;
                }
                effect.applyTo(c.getPlayer(), pos);
            }
        }
        
        // Zero Cooldown Reduction Handling
        if (GameConstants.isZero(pPlayer.getJob())) {
            switch (nSkill) {
                case Zero.MOON_STRIKE:
                case Zero.FLASH_ASSAULT:
                case Zero.ROLLING_CROSS:
                case Zero.WIND_CUTTER: {
                    pPlayer.removeCooldown(Zero.RISING_SLASH);
                    pPlayer.removeCooldown(Zero.AIR_RAID);
                    pPlayer.removeCooldown(Zero.AIR_RIOT);
                    pPlayer.removeCooldown(Zero.FLASH_CUT);
                    pPlayer.removeCooldown(Zero.THROWING_WEAPON);
                    pPlayer.removeCooldown(Zero.ADVANCED_THROWING_WEAPON);
                    pPlayer.removeCooldown(Zero.SPIN_DRIVER);
                    pPlayer.removeCooldown(Zero.WHEEL_WIND);
                    pPlayer.removeCooldown(Zero.ADVANCED_WHEEL_WIND);
                    pPlayer.removeCooldown(Zero.GIGA_CRASH);
                    pPlayer.removeCooldown(Zero.FALLING_STAR);
                    pPlayer.removeCooldown(Zero.EARTH_BREAK);
                    pPlayer.removeCooldown(Zero.ADVANCED_EARTH_BREAK);
                    break;
                }
                case Zero.RISING_SLASH:
                case Zero.FLASH_CUT:
                case Zero.SPIN_DRIVER:
                case Zero.GIGA_CRASH: {
                    pPlayer.removeCooldown(Zero.MOON_STRIKE);
                    pPlayer.removeCooldown(Zero.PIERCING_THRUST);
                    pPlayer.removeCooldown(Zero.SHADOW_STRIKE);
                    pPlayer.removeCooldown(Zero.FLASH_ASSAULT);
                    pPlayer.removeCooldown(Zero.SPIN_CUTTER);
                    pPlayer.removeCooldown(Zero.ADVANCED_SPIN_CUTTER);
                    pPlayer.removeCooldown(Zero.ROLLING_CROSS);
                    pPlayer.removeCooldown(Zero.GRAND_ROLLING_CROSS);
                    pPlayer.removeCooldown(Zero.ROLLING_ASSAULT);
                    pPlayer.removeCooldown(Zero.ADVANCED_ROLLING_ASSAULT);
                    pPlayer.removeCooldown(Zero.WIND_CUTTER);
                    pPlayer.removeCooldown(Zero.WIND_STRIKER);
                    pPlayer.removeCooldown(Zero.STORM_BREAK);
                    pPlayer.removeCooldown(Zero.ADVANCED_STORM_BREAK);
                    break;
                }
            }
        }

        if (GameConstants.isAngelicBuster(pPlayer.getJob())) {
            int Recharge = effect.getOnActive();
            if (Recharge > -1) {
                if (Randomizer.isSuccess(Recharge)) {
                    c.write(JobPacket.AngelicPacket.unlockSkill());
                    c.write(JobPacket.AngelicPacket.showRechargeEffect());
                } else {
                    c.write(JobPacket.AngelicPacket.lockSkill(nSkill));
                }
            } else {
                c.write(JobPacket.AngelicPacket.lockSkill(nSkill));
            }
        }
        c.write(CWvsContext.enableActions());
    }
}
