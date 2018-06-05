package handling.game;

import enums.Stat;
import client.*;
import server.maps.objects.StopForceAtom;
import constants.GameConstants;
import constants.skills.*;
import client.jobs.Explorer.*;
import client.jobs.Hero.*;
import client.jobs.Kinesis.KinesisHandler;
import client.jobs.Nova;
import client.jobs.Nova.AngelicBusterHandler;
import client.jobs.Resistance.*;
import net.InPacket;
import net.ProcessPacket;
import server.StatEffect;
import server.Randomizer;
import server.events.MapleEvent;
import server.events.MapleEventType;
import server.life.Mob;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.objects.User;
import server.quest.Quest;
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
import java.util.stream.Collectors;
import enums.StatInfo;
import server.Timer;
import enums.MobStat;
import server.life.mob.MobTemporaryStat;
import enums.SummonMovementType;
import server.maps.objects.Summon;
import tools.packet.JobPacket.BeastTamerPacket;
import tools.packet.BuffPacket;
import tools.packet.CField.SummonPacket;

/**
 * UserSkillRequest
 * @author Mazen Massoud
 */
public final class SpecialAttackMove implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        User pPlayer = c.getPlayer();
        pPlayer.updateTick(iPacket.DecodeInt());
        int nSkill = iPacket.DecodeInt();

        if (nSkill >= 91000000 && nSkill < 100000000) {
            c.SendPacket(WvsContext.enableActions());
            return;
        }
        if (nSkill == Mercedes.ELEMENTAL_KNIGHTS_2) {
            nSkill += Randomizer.nextInt(2);
        }
        int nXY1 = 0;
        int nXY2 = 0;
        if (nSkill == AngelicBuster.SOUL_SEEKER) {
            nXY1 = iPacket.DecodeShort();
            nXY2 = iPacket.DecodeShort();
            int soulnum = iPacket.DecodeByte();
            int scheck = 0;
            int scheck2 = 0;
            if (soulnum == 1) {
                scheck = iPacket.DecodeInt();
            } else if (soulnum == 2) {
                scheck = iPacket.DecodeInt();
                scheck2 = iPacket.DecodeInt();
            }
            c.SendPacket(JobPacket.AngelicPacket.SoulSeeker(pPlayer, nSkill, soulnum, scheck, scheck2));
            c.SendPacket(JobPacket.AngelicPacket.unlockSkill());
            c.SendPacket(JobPacket.AngelicPacket.showRechargeEffect());
            c.SendPacket(WvsContext.enableActions());
            return;
        }
        if (nSkill >= 100000000) {
            iPacket.DecodeByte(); // Zero
        }

        int nSkillLevel = iPacket.DecodeByte();
        Skill pSkill = SkillFactory.getSkill(nSkill);
        nSkillLevel = pPlayer.getTotalSkillLevel(GameConstants.getLinkedAttackSkill(nSkill));
        StatEffect pEffect = pPlayer.inPVP() ? pSkill.getPVPEffect(nSkillLevel) : pSkill.getEffect(nSkillLevel);
        int nMobID;
        Mob pMob;

        if (((GameConstants.isAngel(nSkill)) && (pPlayer.getStat().equippedSummon % 10000 != nSkill % 10000)) || ((pPlayer.inPVP()) && (pSkill.isPVPDisabled()))) {
            c.SendPacket(WvsContext.enableActions());
            return;
        }
        int levelCheckSkill = 0;
        if ((GameConstants.isPhantom(pPlayer.getJob())) && (!GameConstants.isPhantom(nSkill / 10000))) {
            int skillJob = nSkill / 10000;
            if (skillJob % 100 == 0) {
                levelCheckSkill = Phantom.IMPECCABLE_MEMORY_I;
            } else if (skillJob % 10 == 0) {
                levelCheckSkill = Phantom.IMPECCABLE_MEMORY_II;
            } else if (skillJob % 10 == 1) {
                levelCheckSkill = Phantom.IMPECCABLE_MEMORY_III;
            } else {
                levelCheckSkill = Phantom.IMPECCABLE_MEMORY_4;
            }
        }
        if ((levelCheckSkill == 0) && ((pPlayer.getTotalSkillLevel(GameConstants.getLinkedAttackSkill(nSkill)) <= 0) || (pPlayer.getTotalSkillLevel(GameConstants.getLinkedAttackSkill(nSkill)) != nSkillLevel))) {
            if ((!GameConstants.isMulungSkill(nSkill)) && (!GameConstants.isPyramidSkill(nSkill)) && (pPlayer.getTotalSkillLevel(GameConstants.getLinkedAttackSkill(nSkill)) <= 0)
                    && !GameConstants.isAngel(nSkill) && !GameConstants.isJaguarSkill(nSkill) && !GameConstants.bypassLinkedAttackCheck(nSkill)) {
                c.SendPacket(WvsContext.enableActions());
                if (pPlayer.isDeveloper()) {
                    pPlayer.dropMessage(5, "[SpecialAttackMove] Returning Early / Skill ID : " + nSkill);
                }
                //return;
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
        if ((pEffect.isMPRecovery()) && (pPlayer.getStat().getHp() < pPlayer.getStat().getMaxHp() / 100 * 10)) {
            c.getPlayer().dropMessage(5, "You do not have the HP to use this skill.");
            c.SendPacket(WvsContext.enableActions());
            return;
        }
        if (pEffect.getCooldown(pPlayer) > 0 && nSkill != Phantom.TEMPEST) {
            if (pPlayer.skillisCooling(nSkill)) {
                c.SendPacket(WvsContext.enableActions());
                return;
            }
            if ((nSkill != Corsair.BATTLESHIP_2) && (nSkill != Mechanic.ROCK_N_SHOCK)) {
                pPlayer.addCooldown(nSkill, System.currentTimeMillis(), pEffect.getCooldown(pPlayer));
            }
        }

        /*Check & Debug*/
       /*Return early for broken skills and display debug output for other skills cast.*/
        switch (nSkill) {
            case Mechanic.OPEN_PORTAL_GX9:
                c.SendPacket(WvsContext.enableActions());
                // These skills are currently broken, so we can return here for now.
                return;
            default:
                if (pPlayer.isDeveloper()) pPlayer.dropMessage(5, "[SpecialAttackMove Debug] Skill ID : " + nSkill);
                break;
        }

        /*Buff Handler*/
       /*Add Character Temporary Stats & Apply Buff.*/
        boolean bApplyStats = true;
        switch (nSkill) {
            case Phantom.VOL_DAME: {
                Mob pTarget;
                MobStat pBuffFromMobStat = MobStat.Mystery; //Needs to be initialised
                MobStat[] mobStats = new MobStat[]{ // Ordered from Weakest to Strongest, since  the for loop will save the last MobsStat
                    MobStat.PCounter,   // Dmg Reflect 600%
                    MobStat.MCounter,   // Dmg Reflect 600%
                    MobStat.PImmune,    // Dmg Recv -40%
                    MobStat.MImmune,    // Dmg Recv -40%
                    MobStat.PowerUp,    // Attack +40
                    MobStat.MagicUp,    // Attack +40
                    MobStat.Invincible, // Invincible for short time
                };
                
                for (MapleMapObject pMobs : pPlayer.getMap().getMapObjectsInRange(c.getPlayer().getPosition(), 5000, Arrays.asList(MapleMapObjectType.MONSTER))) {
                    pTarget = (Mob) pMobs;
                    MobTemporaryStat pStat = pTarget.getTemporaryStat();
                    List<MobStat> currentMobStats = Arrays.stream(mobStats).filter(ms -> pStat.hasCurrentMobStat(ms)).collect(Collectors.toList());
                    for (MobStat pCurrentMobStat : currentMobStats) {
                        if (pStat.hasCurrentMobStat(pCurrentMobStat)) {
                            pStat.removeMobStat(pCurrentMobStat, true);
                            pBuffFromMobStat = pCurrentMobStat;
                        }
                    }
                }
                switch (pBuffFromMobStat) {
                    case PCounter:
                    case MCounter:
                        pEffect.statups.put(CharacterTemporaryStat.PowerGuard, pEffect.info.get(StatInfo.y));
                        pEffect.info.put(StatInfo.time, 30000);
                        break;
                    case PImmune:
                    case MImmune:
                        pEffect.statups.put(CharacterTemporaryStat.MagicGuard, pEffect.info.get(StatInfo.x));
                        pEffect.info.put(StatInfo.time, 30000);
                        break;
                    case PowerUp:
                    case MagicUp:
                        pEffect.statups.put(CharacterTemporaryStat.PAD, pEffect.info.get(StatInfo.epad));
                        pEffect.info.put(StatInfo.time, 30000);
                        break;
                    case Invincible:
                        pEffect.statups.put(CharacterTemporaryStat.NotDamaged, 1);
                        pEffect.info.put(StatInfo.time, 5000);
                        break;
                }
                break;
            }
            case Shade.SUMMON_OTHER_SPIRIT:
            case NightWalker.DARKNESS_ASCENDING: {
                pEffect.statups.put(CharacterTemporaryStat.ReviveOnce, 1);
                pEffect.info.put(StatInfo.time, 2100000000);
                break;
            }
            case Evan.MAGIC_GUARD_1:
            case Evan.MAGIC_GUARD_3: {
                pEffect.statups.put(CharacterTemporaryStat.MagicGuard, pEffect.info.get(StatInfo.x));
                pEffect.info.put(StatInfo.time, 2100000000);
                break;
            }
            case Assassin.ASSASSINS_MARK:
            case Assassin.ASSASSINS_MARK_1:
            case Assassin.ASSASSINS_MARK_2:
            case NightLord.NIGHT_LORDS_MARK:
            case NightLord.NIGHT_LORDS_MARK_1: {
                pEffect.statups.put(CharacterTemporaryStat.NightLordMark, 0);
                pEffect.info.put(StatInfo.time, 2100000000);
                break;
            }
            case Fighter.COMBO_ATTACK: {
                pEffect.statups.put(CharacterTemporaryStat.ComboCounter, 0);
                pEffect.info.put(StatInfo.time, 2100000000);
                HeroHandler.setComboAttack(pPlayer, 0);
                break;
            }
            case Xenon.PINPOINT_SALVO: {
                pEffect.statups.put(CharacterTemporaryStat.HollowPointBullet, pEffect.info.get(StatInfo.x));
                pEffect.info.put(StatInfo.time, 2100000000);
                break;
            }
            case Xenon.AEGIS_SYSTEM:
            case Xenon.AEGIS_SYSTEM_1: {
                pEffect.statups.put(CharacterTemporaryStat.XenonAegisSystem, pEffect.info.get(StatInfo.x));
                pEffect.info.put(StatInfo.time, 2100000000);
                break;
            }
            case Aran.BODY_PRESSURE: {
                pEffect.statups.put(CharacterTemporaryStat.PowerGuard, pEffect.info.get(StatInfo.x));
                pEffect.info.put(StatInfo.time, 2100000000);
                break;
            }
            case BattleMage.CONDEMNATION:
            case BattleMage.GRIM_CONTRACT:
            case BattleMage.GRIM_CONTRACT_II:
            case BattleMage.GRIM_CONTRACT_III: {
                pEffect.statups.put(CharacterTemporaryStat.BMageDeath, pEffect.info.get(StatInfo.x));
                pEffect.info.put(StatInfo.time, 2100000000);
                break;
            }
            case NightWalker.SHADOW_BAT:
            case NightWalker.SHADOW_BAT_2:
            case NightWalker.SHADOW_BAT_3: {
                pEffect.statups.put(CharacterTemporaryStat.NightWalkerBat, pEffect.info.get(StatInfo.x));
                pEffect.info.put(StatInfo.time, 2100000000);
                break;
            }
            case Shade.FOX_SPIRITS: {
                pEffect.statups.put(CharacterTemporaryStat.ChangeFoxMan, 1);
                pEffect.info.put(StatInfo.time, 2100000000);
                break;
            }
            case WindArcher.TRIFLING_WIND_I:
            case WindArcher.TRIFLING_WIND_II:
            case WindArcher.TRIFLING_WIND_III: {
                pEffect.statups.put(CharacterTemporaryStat.TriflingWhimOnOff, pEffect.info.get(StatInfo.x));
                pEffect.info.put(StatInfo.time, 2100000000);
                break;
            }
            case Xenon.MANIFEST_PROJECTOR: {
                pEffect.statups.put(CharacterTemporaryStat.ShadowPartner, 1);
                pEffect.info.put(StatInfo.time, 2100000000);
                break;
            }
            case NightWalker.DARK_SERVANT: {
                pEffect.statups.put(CharacterTemporaryStat.ShadowServant, 1);
                pEffect.info.put(StatInfo.time, 180000);
                break;
            }
            case Kaiser.FINAL_FORM:
            case Kaiser.FINAL_FORM_1:
            case Kaiser.FINAL_TRANCE: {
                pEffect.statups.put(CharacterTemporaryStat.CriticalBuff, pEffect.info.get(StatInfo.cr));
                pEffect.statups.put(CharacterTemporaryStat.IndiePMdR, pEffect.info.get(StatInfo.x));
                pEffect.statups.put(CharacterTemporaryStat.Stance, pEffect.info.get(StatInfo.prop));
                pEffect.info.put(StatInfo.time, 60000);
                break;
            }
            case WildHunter.JAGUAR_RIDER: {
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932215/*1932015*/);
                pEffect.info.put(StatInfo.time, 2100000000);
                pPlayer.setUnmountState(false);
                break;
            }
            case DawnWarrior.SOUL_SPEED:{
                pEffect.statups.put(CharacterTemporaryStat.IndieBooster, -20);
                break;
            }
            case DawnWarrior.FALLING_MOON:{
                pPlayer.dispelBuff(DawnWarrior.RISING_SUN);
                pEffect.statups.put(CharacterTemporaryStat.PoseType, 1);
                pEffect.info.put(StatInfo.time, 2100000000);
                break;
            }
            case DawnWarrior.RISING_SUN: {
                pPlayer.dispelBuff(DawnWarrior.FALLING_MOON);
                pEffect.statups.put(CharacterTemporaryStat.PoseType, 2);
                pEffect.info.put(StatInfo.time, 2100000000);
                break;
            }
            case DawnWarrior.EQUINOX_CYCLE: {
                pEffect.statups.put(CharacterTemporaryStat.GlimmeringTime, 1);
                break;
            }
            case Hayato.QUICK_DRAW: {
                pEffect.statups.put(CharacterTemporaryStat.Enrage, 1);
                pEffect.statups.put(CharacterTemporaryStat.BladeStance, 1);
                pEffect.statups.put(CharacterTemporaryStat.HayatoStance, 1);
                pEffect.statups.put(CharacterTemporaryStat.HayatoStanceBonus, 1);
                pEffect.info.put(StatInfo.time, 210000000);
                break;
            }
            case BeastTamer.FLY: {
                pEffect.statups.put(CharacterTemporaryStat.NewFlying, 1);
                pEffect.info.put(StatInfo.time, 40000);
                break;
            }
            case Mercedes.ELVISH_BLESSING:{
                pEffect.statups.put(CharacterTemporaryStat.PAD, pEffect.info.get(StatInfo.x));
                pEffect.info.put(StatInfo.time, 60000);
                break;
            }
            case Global.UNICORN:{
                pEffect.statups.put(CharacterTemporaryStat.RideVehicle, 1932018);
                pEffect.info.put(StatInfo.time, 210000000);
                break;
            }
            default: {
                bApplyStats = false;
                break;
            }
        }
        if (bApplyStats) {
            final StatEffect.CancelEffectAction pCancelAction = new StatEffect.CancelEffectAction(pPlayer, pEffect, System.currentTimeMillis(), pEffect.statups);
            final ScheduledFuture<?> tBuffSchedule = Timer.BuffTimer.getInstance().schedule(pCancelAction, pEffect.info.get(StatInfo.time));
            pPlayer.registerEffect(pEffect, System.currentTimeMillis(), tBuffSchedule, pEffect.statups, false, pEffect.info.get(StatInfo.time), pPlayer.getId());
            pPlayer.getClient().SendPacket(BuffPacket.giveBuff(pPlayer, nSkill, pEffect.info.get(StatInfo.time), pEffect.statups, pEffect));
        }

        /*Summons Handler*/
    /*Define summon effects and spawn the object to the player.*/
        boolean bSummon = true;
        SummonMovementType pMovement = SummonMovementType.FOLLOW;
        switch (nSkill) {
            case BlazeWizard.FIRES_OF_CREATION:
            case BlazeWizard.FIRES_OF_CREATION_1: {
                pEffect = SkillFactory.getSkill(nSkill).getEffect(c.getPlayer().getTotalSkillLevel(BlazeWizard.FIRES_OF_CREATION_2));
                pMovement = SummonMovementType.WEIRD_TELEPORT;
                break;
            }
            case Evan.SUMMON_ONYX_DRAGON:
            case Evan.SUMMON_ONYX_DRAGON_1: {
                pEffect = SkillFactory.getSkill(nSkill).getEffect(c.getPlayer().getTotalSkillLevel(Evan.SUMMON_ONYX_DRAGON));
                pMovement = SummonMovementType.WEIRD_TELEPORT;
                break;
            }
            case BeastTamer.LIL_FORT: {
                pEffect.info.put(StatInfo.time, 60000);
                pMovement = SummonMovementType.FOLLOW;
                break;
            }
            case CannonMaster.ROLLING_RAINBOW: {
                pMovement = SummonMovementType.FOLLOW;
                break;
            }
            default: {
                bSummon = false;
                break;
            }
        }
        if (bSummon) {
            Summon pSummon = new Summon(pPlayer, pEffect, pPlayer.getPosition(), pMovement, pEffect.getDuration());
            pSummon.setPosition(pPlayer.getPosition());
            pPlayer.getMap().spawnSummon(pSummon);
            pEffect.applyTo(pPlayer, null);
        }

        /*Additional Effect Handler*/
    /*Extra functions that occur when the respected skill is cast.*/
        switch (nSkill) {
            case Kaiser.TEMPEST_BLADES_1: // Normal Tempest Blades
            case Kaiser.TEMPEST_BLADES: // Normal Tempest Blades in Morph
            case Kaiser.ADVANCED_TEMPEST_BLADES_1: // Advanced Tempest Blades
            case Kaiser.ADVANCED_TEMPEST_BLADES: { // Advanced Tempest Blades in Morph
                final StatEffect pTempestBlades = SkillFactory.getSkill(Kaiser.TEMPEST_BLADES_1).getEffect(1);
                StopForceAtom pAtom = new StopForceAtom();
                List<Integer> aThree = Arrays.asList(0, 0, 0);
                List<Integer> aFive = Arrays.asList(0, 0, 0, 0, 0);
                int nCount = 3;
                int nID = 0;

                switch (nSkill) {
                    case Kaiser.TEMPEST_BLADES_1: // Normal Tempest Blades
                        nID = 2;
                        break;
                    case Kaiser.TEMPEST_BLADES: // Normal Tempest Blades in Morph
                        nID = 2;
                        break;
                    case Kaiser.ADVANCED_TEMPEST_BLADES_1: // Advanced Tempest Blades
                        nCount = 5;
                        nID = 2;
                        break;
                    case Kaiser.ADVANCED_TEMPEST_BLADES: // Advanced Tempest Blades in Morph
                        nCount = 5;
                        nID = 4;
                        break;
                }
                pAtom.setCount(nCount); // nAmount of Swords
                pAtom.setIdx(nID); // nID
                pAtom.setAngleInfo(nCount == 5 ? aFive : aThree); // Zeros
                pPlayer.setStopForceAtom(pAtom); // Set the Atom Info
                
                if (!pPlayer.hasBuff(CharacterTemporaryStat.StopForceAtomInfo)) {
                    pTempestBlades.statups.put(CharacterTemporaryStat.StopForceAtomInfo, nID);
                    pTempestBlades.info.put(StatInfo.time, 30000);
                    final StatEffect.CancelEffectAction pCancelAction = new StatEffect.CancelEffectAction(pPlayer, pTempestBlades, System.currentTimeMillis(), pTempestBlades.statups);
                    final ScheduledFuture<?> tBuffSchedule = Timer.BuffTimer.getInstance().schedule(pCancelAction, pTempestBlades.info.get(StatInfo.time));
                    pPlayer.registerEffect(pTempestBlades, System.currentTimeMillis(), tBuffSchedule, pTempestBlades.statups, false, pTempestBlades.info.get(StatInfo.time), pPlayer.getId());
                    pPlayer.getClient().SendPacket(BuffPacket.giveBuff(pPlayer, nSkill, pTempestBlades.info.get(StatInfo.time), pTempestBlades.statups, pTempestBlades));
                }
                break;
            }
            case Kaiser.FINAL_FORM:
            case Kaiser.FINAL_FORM_1:
            case Kaiser.FINAL_TRANCE: {
                final EnumMap<CharacterTemporaryStat, Integer> mMorphStat = new EnumMap<>(CharacterTemporaryStat.class);
                mMorphStat.put(CharacterTemporaryStat.Morph, 1201/*nSkill == Kaiser.FINAL_TRANCE ? 1201 : 1200*/);
                final StatEffect.CancelEffectAction pCancelAction = new StatEffect.CancelEffectAction(pPlayer, pEffect, System.currentTimeMillis(), mMorphStat);
                final ScheduledFuture<?> tBuffSchedule = Timer.BuffTimer.getInstance().schedule(pCancelAction, pEffect.info.get(StatInfo.time));
                pPlayer.registerEffect(pEffect, System.currentTimeMillis(), tBuffSchedule, mMorphStat, false, pEffect.info.get(StatInfo.time), pPlayer.getId());
                pPlayer.getClient().SendPacket(BuffPacket.giveBuff(pPlayer, nSkill, pEffect.info.get(StatInfo.time), mMorphStat, pEffect));
                break;
            }
            case Cannoneer.MONKEY_MAGIC: 
            case CannonMaster.MEGA_MONKEY_MAGIC: {
                final EnumMap<CharacterTemporaryStat, Integer> mBuffStat = new EnumMap<>(CharacterTemporaryStat.class);
                
                mBuffStat.put(CharacterTemporaryStat.IndieMHP, pEffect.info.get(StatInfo.indieMhp));
                mBuffStat.put(CharacterTemporaryStat.IndieMMP, pEffect.info.get(StatInfo.indieMmp));
                mBuffStat.put(CharacterTemporaryStat.IndieJump, pEffect.info.get(StatInfo.indieJump));
                mBuffStat.put(CharacterTemporaryStat.IndieSpeed, pEffect.info.get(StatInfo.indieSpeed));
                //mBuffStat.put(CharacterTemporaryStat.IndieAllStat, 30);
                
                //final StatEffect.CancelEffectAction pCancelAction = new StatEffect.CancelEffectAction(pPlayer, null, System.currentTimeMillis(), mBuffStat);
                //final ScheduledFuture<?> tBuffSchedule = Timer.BuffTimer.getInstance().schedule(pCancelAction, 20000);
                //pPlayer.registerEffect(null, System.currentTimeMillis(), tBuffSchedule, mBuffStat, false, 20000, pPlayer.getId());
                pPlayer.getClient().SendPacket(BuffPacket.giveBuff(pPlayer, nSkill, 20000, mBuffStat, null));
                pPlayer.completeDispose();
                return;
            }
            case BeastTamer.BEAR_MODE:
            case BeastTamer.SNOW_LEOPARD_MODE:
            case BeastTamer.HAWK_MODE:
            case BeastTamer.CAT_MODE: {
                c.SendPacket(BeastTamerPacket.AnimalMode(nSkill));
                break;
            }
            case Kinesis.PSYCHIC_CHARGER:
            case Kinesis.PSYCHIC_CHARGER_1: {
                int nCurrentPoints = pPlayer.getPrimaryStack();
                int nAwayFromMax = 30 - pPlayer.getPrimaryStack();
                int nToGain = nAwayFromMax == 1 ? 1 : (nAwayFromMax / 2);
                if (nAwayFromMax > 0) {
                    KinesisHandler.psychicPointResult(pPlayer, pPlayer.getPrimaryStack() + nToGain);
                }
                break;
            }
            case Blaster.RELOAD: {
                BlasterHandler.handleCylinderReload(pPlayer);
                break;
            }
            case Blaster.VITALITY_SHIELD: {
                BlasterHandler.requestVitalityShield(pPlayer);
                break;
            }
            case SuperGM.RESURRECTION: {
                for (User pCharacter : pPlayer.getMap().getCharacters()) {
                    if (pCharacter != null) {
                        pCharacter.getStat().setHp(pCharacter.getStat().getMaxHp(), pCharacter);
                        pCharacter.updateSingleStat(Stat.HP, pCharacter.getStat().getMaxHp());
                        pCharacter.getStat().setMp(pCharacter.getStat().getMaxMp(), pCharacter);
                        pCharacter.updateSingleStat(Stat.MP, pCharacter.getStat().getMaxMp());
                        pCharacter.dispelDebuffs();
                    }
                }
                c.getPlayer().dropMessage(6, "You have resurrected all players in the current map.");
                break;
            }
            case BeastTamer.MEOW_REVIVE: {
                for (User pCharacter : pPlayer.getMap().getCharacters()) {
                    if (pCharacter != null && pCharacter.getParty() == pPlayer.getParty()) {
                        pCharacter.getStat().setHp(pCharacter.getStat().getMaxHp(), pCharacter);
                        pCharacter.updateSingleStat(Stat.HP, pCharacter.getStat().getMaxHp());
                        pCharacter.getStat().setMp(pCharacter.getStat().getMaxMp(), pCharacter);
                        pCharacter.updateSingleStat(Stat.MP, pCharacter.getStat().getMaxMp());
                        pCharacter.dispelDebuffs();
                    }
                }
                break;
            }
            case BeastTamer.MEOW_HEAL: {
                for (User pCharacter : pPlayer.getMap().getCharacters()) {
                    if (pCharacter != null) {
                        int nHpRecovery = (int) (pCharacter.getStat().getMaxHp() * 0.2);
                        int nFinalHp = pCharacter.getStat().getHp() + nHpRecovery;

                        pCharacter.getStat().setHp(nFinalHp, pCharacter);
                        pCharacter.updateSingleStat(Stat.HP, nFinalHp);
                        pCharacter.dispelDebuffs();
                    }
                }
                break;
            }
            case Aran.ADRENALINE_BURST: {
                pPlayer.setLastCombo(System.currentTimeMillis());
                pPlayer.setPrimaryStack((short) 1000);
                pPlayer.getClient().SendPacket(CField.updateCombo(1000));
                AranHandler.handleAdrenalineRush(pPlayer);
                break;
            }
            case Xenon.EMERGENCY_RESUPPLY: {
                pPlayer.gainXenonSurplus((short) 10);
                c.getPlayer().dropMessage(6, "You have recharged your surplus power supplies.");
                break;
            }
            case Phantom.JUDGMENT_DRAW_5: // For Carte Blanche
            case Phantom.JUDGMENT_DRAW_4: { // For Carte Noire
                PhantomHandler.judgementDrawRequest(pPlayer, nSkill);
                break;
            }
            case 9001020:
            case 9101020:
            case DemonSlayer.DEMON_IMPACT:
            case Paladin.MONSTER_MAGNET:
            case DarkKnight.MONSTER_MAGNET_2:
            case DemonSlayer.RAVEN_STORM: {
                byte mobCount = iPacket.DecodeByte();
                iPacket.Skip(3);
                for (int i = 0; i < mobCount; i++) {
                    int mobId = iPacket.DecodeInt();

                    pMob = pPlayer.getMap().getMonsterByOid(mobId);
                    if (pMob == null) {
                        continue;
                    }
                    pMob.switchController(pPlayer, pMob.isControllerHasAggro());
                    pMob.applyStatus(pPlayer, new MonsterStatusEffect(MonsterStatus.STUN, 1, nSkill, null, false), false, pEffect.getDuration(), true, pEffect);
                }

                pPlayer.getMap().broadcastPacket(pPlayer, CField.EffectPacket.showBuffeffect(pPlayer.getId(), nSkill, UserEffectCodes.SkillUse, pPlayer.getLevel(), nSkillLevel, iPacket.DecodeByte()), pPlayer.getTruePosition());
                break;
            }
            case NightWalker.DARK_OMEN: {
                for (int i = 0; i <= 5; i++) {
                    pPlayer.getMap().broadcastPacket(JobPacket.NightWalkerPacket.ShadowBats(pPlayer.getId(), pPlayer.getObjectId()));
                }
                break;
            }
            case Shadower.SHADOWER_INSTINCT: {
                ShadowerHandler.handleShadowerInstinct(pPlayer);
                break;
            }
            case Shadower.FLIP_OF_THE_COIN: {
                ShadowerHandler.handleFlipTheCoin(pPlayer);
                pPlayer.dualBrid = 0;
                break;
            }
            case Xenon.AMARANTH_GENERATOR: {
                try {
                    pPlayer.handleXenonGenerator();
                } catch (Exception ex) {
                    Logger.getLogger(SpecialAttackMove.class.getName()).log(Level.SEVERE, null, ex);
                }
                c.SendPacket(WvsContext.enableActions());
                break;
            }
            case Citizen.CAPTURE_4:
                nMobID = iPacket.DecodeInt();
                pMob = pPlayer.getMap().getMonsterByOid(nMobID);
                if (pMob != null) {
                    boolean success = true; // (pMob.getHp() <= pMob.getMobMaxHp() / 2L) && (pMob.getId() >= 9304000) && (pMob.getId() < 9305000);
                    pPlayer.getMap().broadcastPacket(pPlayer, CField.EffectPacket.showBuffeffect(pPlayer.getId(), nSkill, UserEffectCodes.SkillUse, pPlayer.getLevel(), nSkillLevel, (byte) (success ? 1 : 0)), pPlayer.getTruePosition());
                    if (success) {
                        pPlayer.getQuestNAdd(Quest.getInstance(GameConstants.JAGUAR)).setCustomData(String.valueOf((pMob.getId() - 9303999) * 10));
                        pPlayer.getMap().killMonster(pMob, pPlayer, true, false, (byte) 1);
                        pPlayer.cancelEffectFromTemporaryStat(CharacterTemporaryStat.RideVehicle);
                        c.SendPacket(WvsContext.updateJaguar(pPlayer));
                    } else {
                        pPlayer.dropMessage(5, "The monster has too much physical strength, so you cannot catch it.");
                    }
                }
                break;

            // Reminder to recode the ones below here. -Mazen
            case Citizen.CALL_OF_THE_HUNTER_4:
                pPlayer.dropMessage(5, "No monsters can be summoned. Capture a monster first.");
                break;
            case DemonAvenger.NETHER_SHIELD:
            case Xenon.PINPOINT_SALVO:
            case FirePoisonArchMage.MEGIDDO_FLAME: {
                List<Integer> moblist = new ArrayList<>();
                byte count = iPacket.DecodeByte();
                for (byte i = 1; i <= count; i++) {
                    moblist.add(iPacket.DecodeInt());
                }
                switch (nSkill) {
                    case DemonAvenger.NETHER_SHIELD:
                        c.SendPacket(JobPacket.XenonPacket.ShieldChacing(pPlayer.getId(), moblist, 31221014));
                        break;
                    case Xenon.PINPOINT_SALVO:
                        c.SendPacket(JobPacket.XenonPacket.PinPointRocket(pPlayer.getId(), moblist));
                        break;
                    case FirePoisonArchMage.MEGIDDO_FLAME:
                        c.SendPacket(JobPacket.XenonPacket.MegidoFlameRe(pPlayer.getId(), moblist.get(0)));
                        break;
                    default:
                        break;
                }
                break;
            }
            case WildHunter.CALL_OF_THE_WILD:
                nMobID = pPlayer.getFirstLinkMid();
                pMob = pPlayer.getMap().getMonsterByOid(nMobID);
                pPlayer.setKeyDownSkillTime(0L);
                pPlayer.getMap().broadcastPacket(pPlayer, CField.skillCancel(pPlayer, nSkill), false);
                if (pMob != null) {
                    boolean success = (pMob.getStats().getLevel() < pPlayer.getLevel()) && (pMob.getId() < 9000000) && (!pMob.getStats().isBoss());
                    if (success) {
                        pPlayer.getMap().broadcastPacket(MobPacket.suckMonster(pMob.getObjectId(), pPlayer.getId()));
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
                c.SendPacket(WvsContext.enableActions());
                break;
            case DawnWarrior.FALLING_MOON:
            case DawnWarrior.RISING_SUN:
            case DawnWarrior.EQUINOX_CYCLE:
                pPlayer.changeWarriorStance(nSkill);
                break;
            case 4341003:
                pPlayer.setKeyDownSkillTime(0);
                pPlayer.getMap().broadcastPacket(pPlayer, CField.skillCancel(pPlayer, nSkill), false);
                break;
            /*case ChiefBandit.MESO_EXPLOSION:
                int counter = 0;
                MapleForceAtom atom = new MapleForceAtom(MapleForceAtomTypes.MesoExplosion);
                atom.setCharId(pPlayer.getId());
                atom.setByMob(false);
                atom.setToMob(true);
                atom.setSkillId(ChiefBandit.MESO_EXPLOSION_1);
                atom.getPosition().setLocation(pPlayer.getPosition()); //might be different for meso explosion, it might be the meso bag position
                for (MapleMapObject m : c.getPlayer().getMap().getMapObjectsInRange(c.getPlayer().getPosition(), 19990.0, Arrays.asList(MapleMapObjectType.ITEM))) {
                    MapleMapItem mapitem = (MapleMapItem) m;
                    mapitem.getLock().lock();
                    try {
                        if (counter <= 5 + nSkillLevel) {
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
                    c.write(JobPacket.encodeForceAtom(atom, pPlayer, null));
                }
                break;*/
            default: {
                Point pPOS = null;
                if ((iPacket.GetRemainder() == 5L) || (iPacket.GetRemainder() == 7L)) {
                    pPOS = iPacket.DecodePosition();
                }
                pEffect.applyTo(pPlayer, pPOS);
            }
        }

        /*Class Effect Handler*/
 /*Extra features or checks for certain classes.*/
        if (GameConstants.isKinesis(pPlayer.getJob())) { // Kinesis Psychic Points handling.
            client.jobs.Kinesis.KinesisHandler.handlePsychicPoint(pPlayer, nSkill);
        }
        if (GameConstants.isZero(pPlayer.getJob())) { // Zero Cooldown Reduction handling.
            switch (nSkill) {
                case Zero.MOON_STRIKE:
                case Zero.FLASH_ASSAULT:
                case Zero.ROLLING_CROSS:
                case Zero.WIND_CUTTER: {
                    pPlayer.removeCooldown(Zero.RISING_SLASH_2);
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
                case Zero.RISING_SLASH_2:
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
        if (GameConstants.isWildHunter(pPlayer.getJob())) {
            if (GameConstants.isJaguarSkill(nSkill)) { // Tell the client to display the Jaguar skills.
                pPlayer.getMap().broadcastPacket(SummonPacket.jaguarSkillRequest(nSkill));
                c.SendPacket(CField.SummonPacket.jaguarActive(true));
            }
        }
        if (GameConstants.isAngelicBuster(pPlayer.getJob())) {
            AngelicBusterHandler.handleRecharge(pPlayer, nSkill);
        }
        
        pPlayer.OnSkillCostRequest(pEffect);
        c.getPlayer().getStat().OnCalculateLocalStats(c.getPlayer());
        c.SendPacket(WvsContext.enableActions());
    }
}
