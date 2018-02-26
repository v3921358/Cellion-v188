package handling.game;

import client.*;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import constants.skills.*;
import static handling.jobs.Hero.AranHandler.handleAdrenalineRush;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import server.MapleStatInfo;
import server.Timer.MapTimer;
import server.maps.SummonMovementType;
import server.maps.objects.MapleSummon;

public final class SpecialAttackMove implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        MapleCharacter oPlayer = c.getPlayer();
        oPlayer.updateTick(iPacket.DecodeInteger());
        int nSkill = iPacket.DecodeInteger();
        
        if (oPlayer.isDeveloper()) {
            switch (nSkill) {
                case 14110030:
                case 25111209:
                    // Don't drop message.
                    break;
                default:
                    oPlayer.dropMessage(5, "[SpecialAttackMove Debug] Skill ID : " + nSkill);
                    break;
            }
        }
        
        if (GameConstants.isDisabledSkill(nSkill)) { // Just for broken skills that we want disabled.
            //oPlayer.dropMessage(5, "Sorry, this skill is currently disabled.");
            //c.write(CWvsContext.enableActions());
            //return;
        }

        
        
        // Special Case Toggle Skills
     // These toggles are technically buffs, but do not get handled by the buff manager. -Mazen
        switch (nSkill) {
            
            case Aran.BODY_PRESSURE:{
                if (!GameConstants.isAran(oPlayer.getJob())) {
                    return;
                }
                final MapleStatEffect buffEffects = SkillFactory.getSkill(Aran.BODY_PRESSURE).getEffect(oPlayer.getTotalSkillLevel(Aran.BODY_PRESSURE));
                buffEffects.statups.put(CharacterTemporaryStat.PowerGuard, buffEffects.info.get(MapleStatInfo.x));
                oPlayer.registerEffect(buffEffects, System.currentTimeMillis(), null, buffEffects.statups, false, 2100000000, oPlayer.getId());
                oPlayer.getClient().write(BuffPacket.giveBuff(oPlayer, Aran.BODY_PRESSURE, 2100000000, buffEffects.statups, buffEffects));
                break;
            }
            
            case BattleMage.CONDEMNATION:
            case BattleMage.GRIM_CONTRACT:
            case BattleMage.GRIM_CONTRACT_2:
            case BattleMage.GRIM_CONTRACT_3: {
                if (!GameConstants.isBattleMage(oPlayer.getJob())) {
                    return;
                }
                final MapleStatEffect buffEffects = SkillFactory.getSkill(BattleMage.CONDEMNATION).getEffect(oPlayer.getTotalSkillLevel(BattleMage.CONDEMNATION));
                buffEffects.statups.put(CharacterTemporaryStat.BMageDeath, buffEffects.info.get(MapleStatInfo.x));
                oPlayer.registerEffect(buffEffects, System.currentTimeMillis(), null, buffEffects.statups, false, 2100000000, oPlayer.getId());
                oPlayer.getClient().write(BuffPacket.giveBuff(oPlayer, BattleMage.CONDEMNATION, 2100000000, buffEffects.statups, buffEffects));
                break;
            }
            
            case NightWalker.SHADOW_BAT:
            case NightWalker.SHADOW_BAT_2:
            case NightWalker.SHADOW_BAT_3: {
                if (!GameConstants.isNightWalkerCygnus(oPlayer.getJob())) {
                    return;
                }
                if (!oPlayer.hasBuff(CharacterTemporaryStat.NightWalkerBat)) {
                    final MapleStatEffect buffEffects = SkillFactory.getSkill(NightWalker.SHADOW_BAT).getEffect(oPlayer.getTotalSkillLevel(NightWalker.SHADOW_BAT));
                    buffEffects.statups.put(CharacterTemporaryStat.NightWalkerBat, buffEffects.info.get(MapleStatInfo.x));
                    oPlayer.registerEffect(buffEffects, System.currentTimeMillis(), null, buffEffects.statups, false, 2100000000, oPlayer.getId());
                    oPlayer.getClient().write(BuffPacket.giveBuff(oPlayer, NightWalker.SHADOW_BAT, 2100000000, buffEffects.statups, buffEffects));
                } else {
                    oPlayer.dispelBuff(NightWalker.SHADOW_BAT);
                }
                break;
            }
            
            case Shade.FOX_SPIRITS:{
                if (!GameConstants.isShade(oPlayer.getJob())) {
                    return;
                }
                final MapleStatEffect buffEffects = SkillFactory.getSkill(Shade.FOX_SPIRITS).getEffect(oPlayer.getTotalSkillLevel(Shade.FOX_SPIRITS));
                buffEffects.statups.put(CharacterTemporaryStat.ChangeFoxMan, (int) 1);
                oPlayer.registerEffect(buffEffects, System.currentTimeMillis(), null, buffEffects.statups, false, 2100000000, oPlayer.getId());
                oPlayer.getClient().write(BuffPacket.giveBuff(oPlayer, Shade.FOX_SPIRITS, 2100000000, buffEffects.statups, buffEffects));
                break;
            }
            
            case WindArcher.TRIFLING_WIND_1:
            case WindArcher.TRIFLING_WIND_2:
            case WindArcher.TRIFLING_WIND_3:{
                if (!GameConstants.isWindArcherCygnus(oPlayer.getJob())) {
                    return;
                }
                final MapleStatEffect buffEffects = SkillFactory.getSkill(WindArcher.TRIFLING_WIND_1).getEffect(oPlayer.getTotalSkillLevel(WindArcher.TRIFLING_WIND_1));
                buffEffects.statups.put(CharacterTemporaryStat.TriflingWhimOnOff, buffEffects.info.get(MapleStatInfo.x));
                oPlayer.registerEffect(buffEffects, System.currentTimeMillis(), null, buffEffects.statups, false, 2100000000, oPlayer.getId());
                oPlayer.getClient().write(BuffPacket.giveBuff(oPlayer, WindArcher.TRIFLING_WIND_1, 2100000000, buffEffects.statups, buffEffects));
                break;
            }
        }

        // Kinesis Psychic Points handling.
        if (GameConstants.isKinesis(oPlayer.getJob())) {
           handling.jobs.Kinesis.KinesisHandler.handlePsychicPoint(oPlayer, nSkill);
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
            c.write(JobPacket.AngelicPacket.SoulSeeker(oPlayer, nSkill, soulnum, scheck, scheck2));
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
        if ((skill == null) || ((GameConstants.isAngel(nSkill)) && (oPlayer.getStat().equippedSummon % 10000 != nSkill % 10000)) || ((oPlayer.inPVP()) && (skill.isPVPDisabled()))) {
            c.write(CWvsContext.enableActions());
            return;
        }
        int levelCheckSkill = 0;
        if ((GameConstants.isPhantom(oPlayer.getJob())) && (!GameConstants.isPhantom(nSkill / 10000))) {
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
        if ((levelCheckSkill == 0) && ((oPlayer.getTotalSkillLevel(GameConstants.getLinkedAttackSkill(nSkill)) <= 0) || (oPlayer.getTotalSkillLevel(GameConstants.getLinkedAttackSkill(nSkill)) != skillLevel))) {
            if ((!GameConstants.isMulungSkill(nSkill)) && (!GameConstants.isPyramidSkill(nSkill)) && (oPlayer.getTotalSkillLevel(GameConstants.getLinkedAttackSkill(nSkill)) <= 0) && !GameConstants.isAngel(nSkill)) {
                c.write(CWvsContext.enableActions());
                return;
            }
            if (GameConstants.isMulungSkill(nSkill)) {
                if (oPlayer.getMapId() / 10000 != 92502) {
                    return;
                }
                if (oPlayer.getMulungEnergy() < 10000) {
                    return;
                }
                oPlayer.mulungEnergyModifier(false);
            } else if ((GameConstants.isPyramidSkill(nSkill))
                    && (oPlayer.getMapId() / 10000 != 92602) && (oPlayer.getMapId() / 10000 != 92601)) {
                return;
            }
        }
        if (GameConstants.isEventMap(oPlayer.getMapId())) {
            for (MapleEventType t : MapleEventType.values()) {
                MapleEvent e = ChannelServer.getInstance(oPlayer.getClient().getChannel()).getEvent(t);
                if ((e.isRunning()) && (!oPlayer.isGM())) {
                    for (int i : e.getType().mapids) {
                        if (oPlayer.getMapId() == i) {
                            oPlayer.dropMessage(5, "You may not use that here.");
                            return;
                        }
                    }
                }
            }
        }
        skillLevel = oPlayer.getTotalSkillLevel(GameConstants.getLinkedAttackSkill(nSkill));
        MapleStatEffect effect = oPlayer.inPVP() ? skill.getPVPEffect(skillLevel) : skill.getEffect(skillLevel);
        if ((effect.isMPRecovery()) && (oPlayer.getStat().getHp() < oPlayer.getStat().getMaxHp() / 100 * 10)) {
            c.getPlayer().dropMessage(5, "You do not have the HP to use this skill.");
            c.write(CWvsContext.enableActions());
            return;
        }
        if (effect.getCooldown(oPlayer) > 0) {
            if (oPlayer.skillisCooling(nSkill) && nSkill != 24121005) {
                c.write(CWvsContext.enableActions());
                return;
            }
            if ((nSkill != 5221006) && (nSkill != 35111002)) {
                oPlayer.addCooldown(nSkill, System.currentTimeMillis(), effect.getCooldown(oPlayer));
            }
        }
        int nMob;
        MapleMonster oMob;
        switch (nSkill) {
            case SuperGM.RESURRECTION: {
                for (MapleCharacter oCharacter : oPlayer.getMap().getCharacters()) {
                    if (oCharacter != null) {
                        oCharacter.getStat().setHp(oCharacter.getStat().getMaxHp(), oCharacter);
                        oCharacter.updateSingleStat(MapleStat.HP, oCharacter.getStat().getMaxHp());
                        oCharacter.getStat().setMp(oCharacter.getStat().getMaxMp(), oCharacter);
                        oCharacter.updateSingleStat(MapleStat.MP, oCharacter.getStat().getMaxMp());
                        oCharacter.dispelDebuffs();
                    }
                }
                c.getPlayer().dropMessage(6, "You have resurrected all players in the current map.");
                break;
            }
            case Aran.ADRENALINE_BURST: {
                oPlayer.setLastCombo(System.currentTimeMillis());
                oPlayer.setComboStack((short) 1000);
                oPlayer.getClient().write(CField.updateCombo(1000));
                handleAdrenalineRush(oPlayer);
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

                    oMob = oPlayer.getMap().getMonsterByOid(mobId);
                    if (oMob == null) {
                        continue;
                    }
                    oMob.switchController(oPlayer, oMob.isControllerHasAggro());
                    oMob.applyStatus(oPlayer, new MonsterStatusEffect(MonsterStatus.STUN, 1, nSkill, null, false), false, effect.getDuration(), true, effect);
                }

                oPlayer.getMap().broadcastMessage(oPlayer, CField.EffectPacket.showBuffeffect(oPlayer.getId(), nSkill, UserEffectCodes.SkillUse, oPlayer.getLevel(), skillLevel, iPacket.DecodeByte()), oPlayer.getTruePosition());
                break;

            case 33001011: {
                Point mpos = iPacket.DecodePosition();
                MapleSummon summon = new MapleSummon(oPlayer, effect, mpos, SummonMovementType.SUMMON_JAGUAR, effect.getDuration());
                summon.setPosition(mpos);
                oPlayer.getMap().spawnSummon(summon);
                MapleStatEffect buffeffect = SkillFactory.getSkill(33001007).getEffect(skillLevel);
                buffeffect.applyTo(oPlayer, null);
                break;
            }

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
                    oPlayer.getMap().broadcastMessage(JobPacket.NightWalkerPacket.ShadowBats(oPlayer.getId(), oPlayer.getObjectId()));
                }
                break;
            }
            
            case 4221054:
                oPlayer.getMap().broadcastMessage(CField.OnOffFlipTheCoin(false));
                oPlayer.dualBrid = 0;
                break;
            case Citizen.CAPTURE:
                nMob = iPacket.DecodeInteger();
                oMob = oPlayer.getMap().getMonsterByOid(nMob);
                if (oMob != null) {
                    boolean success = (oMob.getHp() <= oMob.getMobMaxHp() / 2L) && (oMob.getId() >= 9304000) && (oMob.getId() < 9305000);
                    oPlayer.getMap().broadcastMessage(oPlayer, CField.EffectPacket.showBuffeffect(oPlayer.getId(), nSkill, UserEffectCodes.SkillUse, oPlayer.getLevel(), skillLevel, (byte) (success ? 1 : 0)), oPlayer.getTruePosition());
                    if (success) {
                        oPlayer.getQuestNAdd(MapleQuest.getInstance(GameConstants.JAGUAR)).setCustomData(String.valueOf((oMob.getId() - 9303999) * 10));
                        oPlayer.getMap().killMonster(oMob, oPlayer, true, false, (byte) 1);
                        oPlayer.cancelEffectFromTemporaryStat(CharacterTemporaryStat.RideVehicle);
                        c.write(CWvsContext.updateJaguar(oPlayer));
                    } else {
                        oPlayer.dropMessage(5, "The monster has too much physical strength, so you cannot catch it.");
                    }
                }
                break;
            case Citizen.CALL_OF_THE_HUNTER:
                oPlayer.dropMessage(5, "No monsters can be summoned. Capture a monster first.");
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
                        c.write(JobPacket.XenonPacket.ShieldChacing(oPlayer.getId(), moblist, 31221014));
                        break;
                    case Xenon.PINPOINT_SALVO:
                        c.write(JobPacket.XenonPacket.PinPointRocket(oPlayer.getId(), moblist));
                        break;
                    case FPArchmage.MEGIDDO_FLAME:
                        c.write(JobPacket.XenonPacket.MegidoFlameRe(oPlayer.getId(), moblist.get(0)));
                        break;
                    default:
                        break;
                }
                break;
            }
            case WildHunter.CALL_OF_THE_WILD:
                nMob = oPlayer.getFirstLinkMid();
                oMob = oPlayer.getMap().getMonsterByOid(nMob);
                oPlayer.setKeyDownSkillTime(0L);
                oPlayer.getMap().broadcastMessage(oPlayer, CField.skillCancel(oPlayer, nSkill), false);
                if (oMob != null) {
                    boolean success = (oMob.getStats().getLevel() < oPlayer.getLevel()) && (oMob.getId() < 9000000) && (!oMob.getStats().isBoss());
                    if (success) {
                        oPlayer.getMap().broadcastMessage(MobPacket.suckMonster(oMob.getObjectId(), oPlayer.getId()));
                        oPlayer.getMap().killMonster(oMob, oPlayer, false, false, (byte) -1);
                    } else {
                        oPlayer.dropMessage(5, "The monster has too much physical strength, so you cannot catch it.");
                    }
                } else {
                    oPlayer.dropMessage(5, "No monster was sucked. The skill failed.");
                }
                break;
            case Luminous.SUNFIRE:
            case Luminous.ECLIPSE:
            case Luminous.EQUILIBRIUM_1:
            case Luminous.CHANGE_LIGHTDARK_MODE:
                oPlayer.changeLuminousMode();
                c.write(CWvsContext.enableActions());
                break;
            case DawnWarrior.FALLING_MOON:
            case DawnWarrior.RISING_SUN:
            case DawnWarrior.EQUINOX_CYCLE:
                oPlayer.changeWarriorStance(nSkill);
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
                    oPlayer.handleXenonGenerator();
                } catch (Exception ex) {
                    Logger.getLogger(SpecialAttackMove.class.getName()).log(Level.SEVERE, null, ex);
                }
                c.write(CWvsContext.enableActions());
                break;
            }
            case 110001500:
            case 110001501:
            case 110001502:
            case 110001503:
            case 110001504: {
                //Change animal mode
                iPacket.Skip(3);
                final EnumMap<CharacterTemporaryStat, Integer> stat = new EnumMap<>(CharacterTemporaryStat.class);
                stat.put(CharacterTemporaryStat.AnimalChange, 1);
                c.write(BuffPacket.giveBuff(oPlayer, 36121054, skillLevel, stat, effect));
                break;

            }
            case 4341003:
                oPlayer.setKeyDownSkillTime(0);
                oPlayer.getMap().broadcastMessage(oPlayer, CField.skillCancel(oPlayer, nSkill), false);
                break;
            case ChiefBandit.MESO_EXPLOSION:
                int counter = 0;
                MapleForceAtom atom = new MapleForceAtom(MapleForceAtomTypes.MESOEXPLOSION_BOTH);
                atom.setCharId(oPlayer.getId());
                atom.setByMob(false);
                atom.setToMob(true);
                atom.setSkillId(ChiefBandit.MESO_EXPLOSION_1);
                atom.getPosition().setLocation(oPlayer.getPosition()); //might be different for meso explosion, it might be the meso bag position
                for (MapleMapObject m : c.getPlayer().getMap().getMapObjectsInRange(c.getPlayer().getPosition(), 19990.0, Arrays.asList(MapleMapObjectType.ITEM))) {
                    MapleMapItem mapitem = (MapleMapItem) m;
                    mapitem.getLock().lock();
                    try {
                        if (counter <= 5 + skillLevel) {
                            if (mapitem.getMeso() > 0 && !mapitem.isPickedUp() && mapitem.isPickpocketDrop()) {
                                oPlayer.getMap().removeMapObject(mapitem);
                                mapitem.setPickedUp(true);
                                oPlayer.write(CField.removeItemFromMap(m.getObjectId(), 0, oPlayer.getId()));
                                oPlayer.getMap().broadcastMessage(CField.removeItemFromMap(m.getObjectId(), 0, oPlayer.getId()));
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

                if ((effect.isMagicDoor() && !FieldLimitType.UnableToUseMysticDoor.check(oPlayer.getMap())) // check magic door
                        // check mount req
                        || (mountid != 0 && (mountid != GameConstants.getMountItem(skill.getId(), c.getPlayer())) && (!c.getPlayer().isIntern()) && (c.getPlayer().getBuffedValue(CharacterTemporaryStat.RideVehicle) == null) && (c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -122) == null) && (!GameConstants.isMountItemAvailable(mountid, c.getPlayer().getJob())))) {
                    c.write(CWvsContext.enableActions());
                    return;
                }
                effect.applyTo(c.getPlayer(), pos);
            }
        }
        
        // Zero Cooldown Reduction Handling
        if (GameConstants.isZero(oPlayer.getJob())) {
            switch (nSkill) {
                case Zero.MOON_STRIKE:
                case Zero.FLASH_ASSAULT:
                case Zero.ROLLING_CROSS:
                case Zero.WIND_CUTTER: {
                    oPlayer.removeCooldown(Zero.RISING_SLASH);
                    oPlayer.removeCooldown(Zero.AIR_RAID);
                    oPlayer.removeCooldown(Zero.AIR_RIOT);
                    oPlayer.removeCooldown(Zero.FLASH_CUT);
                    oPlayer.removeCooldown(Zero.THROWING_WEAPON);
                    oPlayer.removeCooldown(Zero.ADVANCED_THROWING_WEAPON);
                    oPlayer.removeCooldown(Zero.SPIN_DRIVER);
                    oPlayer.removeCooldown(Zero.WHEEL_WIND);
                    oPlayer.removeCooldown(Zero.ADVANCED_WHEEL_WIND);
                    oPlayer.removeCooldown(Zero.GIGA_CRASH);
                    oPlayer.removeCooldown(Zero.FALLING_STAR);
                    oPlayer.removeCooldown(Zero.EARTH_BREAK);
                    oPlayer.removeCooldown(Zero.ADVANCED_EARTH_BREAK);
                    break;
                }
                case Zero.RISING_SLASH:
                case Zero.FLASH_CUT:
                case Zero.SPIN_DRIVER:
                case Zero.GIGA_CRASH: {
                    oPlayer.removeCooldown(Zero.MOON_STRIKE);
                    oPlayer.removeCooldown(Zero.PIERCING_THRUST);
                    oPlayer.removeCooldown(Zero.SHADOW_STRIKE);
                    oPlayer.removeCooldown(Zero.FLASH_ASSAULT);
                    oPlayer.removeCooldown(Zero.SPIN_CUTTER);
                    oPlayer.removeCooldown(Zero.ADVANCED_SPIN_CUTTER);
                    oPlayer.removeCooldown(Zero.ROLLING_CROSS);
                    oPlayer.removeCooldown(Zero.GRAND_ROLLING_CROSS);
                    oPlayer.removeCooldown(Zero.ROLLING_ASSAULT);
                    oPlayer.removeCooldown(Zero.ADVANCED_ROLLING_ASSAULT);
                    oPlayer.removeCooldown(Zero.WIND_CUTTER);
                    oPlayer.removeCooldown(Zero.WIND_STRIKER);
                    oPlayer.removeCooldown(Zero.STORM_BREAK);
                    oPlayer.removeCooldown(Zero.ADVANCED_STORM_BREAK);
                    break;
                }
            }
        }

        if (GameConstants.isAngelicBuster(oPlayer.getJob())) {
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
