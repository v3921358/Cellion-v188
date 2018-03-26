package handling.world;

import client.*;
import client.anticheat.CheatTracker;
import client.anticheat.CheatingOffense;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import constants.ItemConstants;
import constants.ServerConstants;
import constants.skills.*;
import handling.jobs.Cygnus;
import handling.jobs.Cygnus.NightWalkerHandler;
import handling.jobs.Cygnus.ThunderBreakerHandler;
import handling.jobs.Explorer.HeroHandler;
import handling.jobs.Explorer.ShadowerHandler;
import handling.jobs.Hero.AranHandler;
import handling.jobs.Hero.PhantomHandler;
import handling.jobs.Kinesis.KinesisHandler;
import net.InPacket;
import server.MapleStatEffect;
import server.Randomizer;
import server.life.*;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.objects.MapleCharacter;
import server.messages.StylishKillMessage;
import server.messages.StylishKillMessage.StylishKillMessageType;
import tools.Pair;
import tools.packet.CWvsContext;

import java.awt.*;
import java.util.*;
import java.util.List;
import service.RecvPacketOpcode;
import tools.packet.CField;
import tools.packet.JobPacket;
import tools.packet.JobPacket.ShadowerPacket;
import tools.packet.MobPacket;

public class DamageParse {

    public static void applyAttack(AttackInfo attack, Skill theSkill, MapleCharacter pPlayer, int attackCount, double maxDamagePerMonster, MapleStatEffect effect, AttackType attackType) {
        if (!pPlayer.isAlive()) {
            pPlayer.getCheatTracker().registerOffense(CheatingOffense.ATTACKING_WHILE_DEAD);
            return;
        } else if (!pPlayer.getMap().getSharedMapResources().noSkillInfo.isSkillUsable(pPlayer.getJob(), attack.skill)) {
            pPlayer.getCheatTracker().registerOffense(CheatingOffense.ATTACKING_IN_UNAVAILABLE_MAP);
            return;
        } else if (attack.skill == 80001593) { // Anti-cheat.
            pPlayer.yellowMessage("[AntiCheat] Please remember hacking and the use of 3rd party modifications go against our ToS.");
            return;
        }

        if (attack.real && GameConstants.getAttackDelay(attack.skill, theSkill) >= 400) { // Was 100
            pPlayer.getCheatTracker().checkAttack(attack.skill, attack.lastAttackTickCount);
        }

        if (attack.skill != 0 && effect != null) {
            if (GameConstants.isMulungSkill(attack.skill)) {
                if (pPlayer.getMapId() / 10000 != 92502) {
                    return;
                }
                if (pPlayer.getMulungEnergy() < 10000) {
                    return;
                }
                pPlayer.mulungEnergyModifier(false);
            } else if (GameConstants.isPyramidSkill(attack.skill)) {
                if (pPlayer.getMapId() / 1000000 != 926) {
                    return;
                }
                if (pPlayer.getPyramidSubway() != null && pPlayer.getPyramidSubway().onSkillUse(pPlayer)) {
                    //TODO: Do something here
                }
            } else if (GameConstants.isInflationSkill(attack.skill)) {
                if (pPlayer.getBuffedValue(CharacterTemporaryStat.Inflation) != null) {
                    //TODO: Do something here
                }
            } else if (attack.mobCount > effect.getMobCount() && !GameConstants.isMismatchingBulletSkill(attack.skill)) { //&& attack.skill != Paladin.ADVANCED_CHARGE && attack.skill != 22110025) {
                pPlayer.getCheatTracker().registerOffense(CheatingOffense.MISMATCHING_BULLETCOUNT);
                if (pPlayer.isIntern()) {
                    pPlayer.yellowMessage("[Warning] Mismatching bullet count for skill ID " + attack.skill + ".");
                }
                //return;
            }
        }

        if (ServerConstants.ADMIN_MODE) {
            pPlayer.dropMessage(-1, new StringBuilder().append("Animation: ").append(Integer.toHexString((attack.display & 0x8000) != 0 ? attack.display - 32768 : attack.display)).toString());
        }
        boolean useAttackCount = attack.skill != Marksman.SNIPE && attack.skill != Mercedes.LIGHTNING_EDGE;

        if (attack.numberOfHits > 0 && attack.mobCount > 0) {
            if (!pPlayer.getStat().checkEquipDurabilitys(pPlayer, -1)) {
                pPlayer.dropMessage(5, "An item has run out of durability but has no inventory room to go to.");
                return;
            }
        }
        int totDamage = 0;
        MapleMap map = pPlayer.getMap();
        int totDamageToOneMonster = 0;
        long hpMob = 0L;
        PlayerStats stats = pPlayer.getStat();

        final int playerDamageCap = GameConstants.damageCap + pPlayer.getStat().damageCapIncrease; // The damage cap the player is allowed to hit.
        int CriticalDamage = stats.passive_sharpeye_percent();
        int ShdowPartnerAttackPercentage = 0;
        if (attackType == AttackType.RANGED_WITH_ShadowPartner || attackType == AttackType.NON_RANGED_WITH_MIRROR) {
            MapleStatEffect shadowPartnerEffect = pPlayer.getStatForBuff(CharacterTemporaryStat.ShadowPartner);
            if (shadowPartnerEffect != null) {
                ShdowPartnerAttackPercentage += shadowPartnerEffect.getX();
            }
            attackCount /= 2;
        }
        ShdowPartnerAttackPercentage *= (CriticalDamage + 100) / 100;
        if (attack.skill == Shadower.ASSASSINATE) {
            ShdowPartnerAttackPercentage *= 10;
        }

        double maxDamagePerHit = 0.0D;

        for (AttackMonster oned : attack.allDamage) {
            MapleMonster monster = oned.getMonster();
            if (monster == null || monster.getId() != oned.getMonsterId()) {
                continue;
            }

            if (monster.getLinkCID() < 1) {
                totDamageToOneMonster = 0;
                hpMob = monster.getMobMaxHp();
                MapleMonsterStats monsterstats = monster.getStats();
                int fixeddmg = monsterstats.getFixedDamage();
                boolean Tempest = monster.getStatusSourceID(MonsterStatus.FREEZE) == Paladin.HEAVENS_HAMMER;

                if (!Tempest && !pPlayer.isGM()) {
                    if ((pPlayer.getJob() >= MapleJob.BATTLE_MAGE_1.getId() && pPlayer.getJob() <= MapleJob.BATTLE_MAGE_4.getId() && !monster.isBuffed(MonsterStatus.DAMAGE_IMMUNITY)
                            && !monster.isBuffed(MonsterStatus.MAGIC_IMMUNITY)
                            && !monster.isBuffed(MonsterStatus.MAGIC_DAMAGE_REFLECT)) || attack.skill == Marksman.SNIPE
                            || attack.skill == Mercedes.LIGHTNING_EDGE || ((pPlayer.getJob() < MapleJob.BATTLE_MAGE_1.getId() || pPlayer.getJob() > MapleJob.BATTLE_MAGE_4.getId()) && !monster.isBuffed(MonsterStatus.DAMAGE_IMMUNITY)
                            && !monster.isBuffed(MonsterStatus.WEAPON_IMMUNITY) && !monster.isBuffed(MonsterStatus.WEAPON_DAMAGE_REFLECT))) {
                        maxDamagePerHit = calculateMaxWeaponDamagePerHit(pPlayer, monster, attack, theSkill, effect, maxDamagePerMonster, CriticalDamage);
                    } else {
                        maxDamagePerHit = 1.0D;
                    }
                }
                if (monsterstats.isBoss()) {
                    maxDamagePerHit *= stats.bossdam_r / 100d;
                } else {
                    maxDamagePerHit *= stats.dam_r / 100d;
                }

                byte overallAttackCount = 0;

                int criticals = 0;
                for (Pair eachde : oned.getAttacks()) {
                    long eachd = (Long) eachde.left;
                    overallAttackCount = (byte) (overallAttackCount + 1);
                    if (((Boolean) eachde.right)) {
                        criticals++;
                    }
                    if ((useAttackCount) && (overallAttackCount - 1 == attackCount)) {
                        maxDamagePerHit = maxDamagePerHit / 100.0D * (ShdowPartnerAttackPercentage / 100.0D);
                    }

                    if (fixeddmg != -1) {
                        if (monsterstats.getOnlyNoramlAttack()) {
                            eachd = attack.skill != 0 ? 0 : fixeddmg;
                        } else {
                            eachd = fixeddmg;
                        }
                    } else if (monsterstats.getOnlyNoramlAttack()) {
                        eachd = attack.skill != 0 ? 0 : Math.min(eachd, (int) maxDamagePerHit);
                    } else if (!pPlayer.isGM()) {
                        if (Tempest) {
                            if (eachd > monster.getMobMaxHp()) {
                                eachd = (int) Math.min(monster.getMobMaxHp(), Integer.MAX_VALUE);
                                pPlayer.getCheatTracker().registerOffense(CheatingOffense.HIGH_DAMAGE);
                            }
                        } else if (((pPlayer.getJob() >= 3200) && (pPlayer.getJob() <= 3212) && (!monster.isBuffed(MonsterStatus.DAMAGE_IMMUNITY)) && (!monster.isBuffed(MonsterStatus.MAGIC_IMMUNITY)) && (!monster.isBuffed(MonsterStatus.MAGIC_DAMAGE_REFLECT))) || (attack.skill == 23121003) || (((pPlayer.getJob() < 3200) || (pPlayer.getJob() > 3212)) && (!monster.isBuffed(MonsterStatus.DAMAGE_IMMUNITY)) && (!monster.isBuffed(MonsterStatus.WEAPON_IMMUNITY)) && (!monster.isBuffed(MonsterStatus.WEAPON_DAMAGE_REFLECT)))) {
                            if (eachd > maxDamagePerHit) {
                                pPlayer.getCheatTracker().registerOffense(CheatingOffense.HIGH_DAMAGE, new StringBuilder().append("[Damage: ").append(eachd).append(", Expected: ").append(maxDamagePerHit).append(", Mob: ").append(monster.getId()).append("] [Job: ").append(pPlayer.getJob()).append(", Level: ").append(pPlayer.getLevel()).append(", Skill: ").append(attack.skill).append("]").toString());
                                if (attack.real) {
                                    pPlayer.getCheatTracker().checkSameDamage(eachd, maxDamagePerHit);
                                }
                                if (eachd > maxDamagePerHit * 2.0D) {
                                    pPlayer.getCheatTracker().registerOffense(CheatingOffense.HIGH_DAMAGE_2, new StringBuilder().append("[Damage: ").append(eachd).append(", Expected: ").append(maxDamagePerHit).append(", Mob: ").append(monster.getId()).append("] [Job: ").append(pPlayer.getJob()).append(", Level: ").append(pPlayer.getLevel()).append(", Skill: ").append(attack.skill).append("]").toString());
                                    eachd = (int) (maxDamagePerHit * 2.0D);
                                    if (eachd >= playerDamageCap) {
                                        pPlayer.getClient().close();
                                    }
                                }
                            }

                        } else if (eachd > maxDamagePerHit) {
                            eachd = (int) maxDamagePerHit;
                        }

                    }
                    totDamageToOneMonster += eachd;

                    if ((eachd == 0 || monster.getId() == 9700021) && pPlayer.getPyramidSubway() != null) {
                        pPlayer.getPyramidSubway().onMiss(pPlayer);
                    }
                }
                totDamage += totDamageToOneMonster;

                // Paragon Level Bonuses
                if (ServerConstants.PARAGON_SYSTEM) {
                    if (pPlayer.getReborns() >= 2) { // Paragon Level 2+
                        totDamageToOneMonster *= 1.05; // +5% Increased Damage
                    }
                    if (pPlayer.getReborns() >= 10) { // Paragon Level 10+
                        totDamageToOneMonster *= 1.05; // +5% Increased Damage
                    }
                    if (pPlayer.getReborns() >= 6) { // Paragon Level 6+
                        pPlayer.addHP((int) (totDamageToOneMonster * 0.01)); // +1% Life Leech
                    }
                    if (pPlayer.getReborns() >= 6) { // Paragon Level 7+
                        pPlayer.addMP((int) (totDamageToOneMonster * 0.01)); // +1% Mana Leech
                    }
                }
                
                /*
                 *  Damage Correction Handler
                 *  @purpose 
                 */
                if(ServerConstants.DAMAGE_CORRECTION) {
                    int nDamageChange = (int) GameConstants.damageCorrectRequest(pPlayer, attack.skill, totDamageToOneMonster);
                    totDamageToOneMonster += nDamageChange;
                }

                // Apply attack to the monster hit.
                if (pPlayer.isDeveloper()) {
                    pPlayer.yellowMessage("[Debug] Skill ID (" + attack.skill + ") - Damage (" + totDamageToOneMonster + ")");
                }
                monster.damage(pPlayer, totDamageToOneMonster, true, attack.skill);

                // Monster is still alive after being hit.
                if (monster.isAlive()) {
                    pPlayer.checkMonsterAggro(monster);
                } else {
                    attack.after_NumMobsKilled++;
                }

                if (pPlayer.getSkillLevel(36110005) > 0) {
                    Skill skill = SkillFactory.getSkill(36110005);
                    MapleStatEffect eff = skill.getEffect(pPlayer.getSkillLevel(skill));
                    if (pPlayer.getLastComboTime() + 5000 < System.currentTimeMillis()) {
                        monster.setTriangulation(0);
                        //player.clearDamageMeters();
                    }
                    if (eff.makeChanceResult()) {
                        pPlayer.setLastCombo(System.currentTimeMillis());
                        if (monster.getTriangulation() < 3) {
                            monster.setTriangulation(monster.getTriangulation() + 1);
                        }
                        monster.applyStatus(pPlayer, new MonsterStatusEffect(MonsterStatus.DARKNESS, eff.getX(), eff.getSourceId(), null, false), false, eff.getY() * 1000, true, eff);
                        monster.applyStatus(pPlayer, new MonsterStatusEffect(MonsterStatus.TRIANGULATION, monster.getTriangulation(), eff.getSourceId(), null, false), false, eff.getY() * 1000, true, eff);
                    }
                }

                if (pPlayer.getBuffedValue(CharacterTemporaryStat.PickPocket) != null) {
                    handlePickPocket(pPlayer, monster, oned);
                }

                /*
                 *  Final Attack Handling
                 *  @author Mazen
                 * 
                 *  @purpose Handle Final Attack system without the need for the packet. 
                 */
                if (pPlayer.hasSkill(pPlayer.getFinalAttackSkill())) {
                    // TODO: Handle nPropChance and nDamage based on skill level.
                    int nPropChance = 30;
                    long nFinalAttackDamage = (long) (totDamageToOneMonster * 0.2);
                    for (AttackMonster pAttack : attack.allDamage) {
                        if (Randomizer.nextInt(100) < nPropChance) {
                            //pPlayer.getClient().write(CField.finalAttackRequest(pPlayer, attack.skill, pPlayer.getFinalAttackSkill(), 0, monster.getId(), (int) System.currentTimeMillis()));
                            new java.util.Timer().schedule(new java.util.TimerTask() {
                                @Override
                                public void run() {
                                    monster.damage(pPlayer, nFinalAttackDamage, true, pPlayer.getFinalAttackSkill());
                                    pPlayer.getClient().write(MobPacket.showMonsterHP(monster.getObjectId(), monster.getHPPercent()));
                                    cancel();
                                }
                            }, 500); // 0.5 Second Delay
                        }
                    }
                }

                // Additional Skill Monster Status Handling
                switch (attack.skill) {
                    case 4001002:
                    case 4001334:
                    case 4001344:
                    case 4111005:
                    case 4121007:
                    case 4201005:
                    case 4211002:
                    case 4221001:
                    case 4221007:
                    case 4301001:
                    case 4311002:
                    case 4311003:
                    case 4331000:
                    case 4331004:
                    case 4331005:
                    case 4331006:
                    case 4341002:
                    case 4341004:
                    case 4341005:
                    case 4341009:
                    case 14001004:
                    case 14111022:
                    case 14111023:
                    case 14121001:
                    case 14121004:
                    case 14111002:
                    case 14111005:
                        int[] skills = {4120005, 4220005, 4340001, 14110004};
                        for (int i : skills) {
                            Skill skill = SkillFactory.getSkill(i);
                            if (pPlayer.getTotalSkillLevel(skill) > 0) {
                                MapleStatEffect venomEffect = skill.getEffect(pPlayer.getTotalSkillLevel(skill));
                                if (!venomEffect.makeChanceResult()) {
                                    break;
                                }
                                monster.applyStatus(pPlayer, new MonsterStatusEffect(MonsterStatus.POISON, 1, i, null, false), true, venomEffect.getDuration(), true, venomEffect);
                                break;
                            }
                        }
                        break;
                    case 4121017:
                        Skill skill = SkillFactory.getSkill(4121017);
                        if (pPlayer.getTotalSkillLevel(skill) > 0) {
                            MapleStatEffect showdown = skill.getEffect(pPlayer.getTotalSkillLevel(skill));
                            monster.applyStatus(pPlayer, new MonsterStatusEffect(MonsterStatus.SHOWDOWN, showdown.getX(), 4121017, null, false), false, showdown.getDuration(), false, showdown);
                        }
                        break;
                    case 4201004:
                        monster.handleSteal(pPlayer);
                        break;
                    case 21000002:
                    case 21100001:
                    case 21100002:
                    case 21100004:
                    case 21110002:
                    case 21110003:
                    case 21110004:
                    case 21110006:
                    case 21110007:
                    case 21110008:
                    case 21120002:
                    case 21120005:
                    case 21120006:
                    case 21120009:
                    case 21120010:
                        if ((pPlayer.getBuffedValue(CharacterTemporaryStat.WeaponCharge) != null) && (!monster.getStats().isBoss())) {
                            MapleStatEffect eff = pPlayer.getStatForBuff(CharacterTemporaryStat.WeaponCharge);
                            if (eff != null) {
                                monster.applyStatus(pPlayer, new MonsterStatusEffect(MonsterStatus.SPEED, eff.getX(), eff.getSourceId(), null, false), false, eff.getY() * 1000, true, eff);
                            }
                        }
                        if ((pPlayer.getBuffedValue(CharacterTemporaryStat.BodyPressure) != null) && (!monster.getStats().isBoss())) {
                            MapleStatEffect eff = pPlayer.getStatForBuff(CharacterTemporaryStat.BodyPressure);

                            if ((eff != null) && (eff.makeChanceResult()) && (!monster.isBuffed(MonsterStatus.NEUTRALISE))) {
                                monster.applyStatus(pPlayer, new MonsterStatusEffect(MonsterStatus.NEUTRALISE, 1, eff.getSourceId(), null, false), false, eff.getX() * 1000, true, eff);
                            }
                        }
                        break;
                }

                // Megido Flame Custom Handling
                if (pPlayer.getSkillLevel(SkillFactory.getSkill(2121055)) > 0 || pPlayer.getSkillLevel(SkillFactory.getSkill(2121052)) > 0/* || player.getJob() == 1211 || player.getJob() == 1212*/) {
                    int percent = 45;
                    int percent2 = 25;
                    for (AttackMonster at : attack.allDamage) {
                        MapleMonster mob = map.getMonsterByOid(at.getObjectId());
                        if (map.getMonsterByOid(at.getObjectId()).getStats().isBoss()) {
                            if (Randomizer.nextInt(100) < percent) {
                                if (mob != null) {
                                    pPlayer.getClient().write(JobPacket.XenonPacket.MegidoFlameRe(pPlayer.getId(), mob.getObjectId()));
                                }
                            }
                        } else {
                            if (Randomizer.nextInt(100) < percent2) {
                                if (mob != null) {
                                    pPlayer.getClient().write(JobPacket.XenonPacket.MegidoFlameRe(pPlayer.getId(), mob.getObjectId()));
                                }
                            }
                        }
                    }
                }

                if ((totDamageToOneMonster > 0) || (attack.skill == 1221011) || (attack.skill == 21120006)) {

                    // Kinesis Psychic Points handling.
                    if (GameConstants.isKinesis(pPlayer.getJob())) {
                        KinesisHandler.handlePsychicPoint(pPlayer, attack.skill);
                    }
                    if (GameConstants.isShade(pPlayer.getJob())) {
                        if (pPlayer.hasBuff(CharacterTemporaryStat.ChangeFoxMan)) {
                            for (AttackMonster at : attack.allDamage) {
                                int nPercent = 35;
                                if (Randomizer.nextInt(100) < nPercent && attack.skill != 25100010 && attack.skill != 25100010) { 
                                    pPlayer.getMap().broadcastMessage(JobPacket.ShadePacket.FoxSpirit(pPlayer, at));
                                }
                            }
                        }
                    }
                    if (GameConstants.isAran(pPlayer.getJob())) {
                        switch (attack.skill) {
                            case Aran.SMASH_SWING:
                            case Aran.SMASH_SWING_1:
                            case Aran.SMASH_SWING_2:
                                AranHandler.handleSwingStudies(pPlayer);
                                break;
                        }
                    }
                    if (GameConstants.isThunderBreakerCygnus(pPlayer.getJob())) {
                        ThunderBreakerHandler.handleLightningBuff(pPlayer);
                    }
                    if (GameConstants.isDemonSlayer(pPlayer.getJob())) {
                        pPlayer.handleForceGain(monster.getObjectId(), attack.skill);
                    }
                    if ((GameConstants.isPhantom(pPlayer.getJob())) && (attack.skill != 24120002) && (attack.skill != 24100003)) {
                        if(pPlayer.hasSkill(Phantom.CARTE_BLANCHE)) {
                            //for (AttackMonster at : attack.allDamage) {
                                if (Randomizer.nextInt(100) < 20) { 
                                    pPlayer.getMap().broadcastMessage(JobPacket.PhantomPacket.ThrowCarte(pPlayer, 0/*at.getObjectId()*/));
                                    PhantomHandler.handleDeck(pPlayer);
                                }
                            //}
                        }
                    }
                    if (GameConstants.isXenon(pPlayer.getJob())) {
                        //for (AttackMonster at : attack.allDamage) {
                        if (pPlayer.hasBuff(CharacterTemporaryStat.HollowPointBullet)) {
                            if (Randomizer.nextInt(100) < 30) {
                                pPlayer.getMap().broadcastMessage(JobPacket.XenonPacket.EazisSystem(pPlayer.getId(), 0));
                            }
                        }
                        //}
                    }
                    if (GameConstants.isKaiser(pPlayer.getJob())) {
                        for (int i = 0; i < attack.mobCount; i++) {
                            pPlayer.handleKaiserCombo();
                        }
                    }
                    if (GameConstants.isNightWalkerCygnus(pPlayer.getJob())) {

                        if (pPlayer.hasBuff(CharacterTemporaryStat.NightWalkerBat)) {
                            for (AttackMonster at : attack.allDamage) {
                                if (Randomizer.nextInt(100) < 60) {
                                    pPlayer.getMap().broadcastMessage(JobPacket.NightWalkerPacket.ShadowBats(pPlayer.getId(), at.getObjectId()));
                                }
                            }
                        }
                        
                        if (attack.skill == NightWalker.DOMINION) {
                            //NightWalkerHandler.handleDominionBuff(pPlayer);
                        }

                        pPlayer.handleDarkElemental(); // Dark Elemental Stack Count Handler
                        for (int i = pPlayer.getDarkElementalCombo(); i > 0; i--) { // Damage Increase Handler for Dark Elemental Mark Stacks
                            totDamageToOneMonster += (totDamageToOneMonster * 0.8); // 80% Increase Damage per Stack
                        }
                    }
                    if (monster.isBuffed(MonsterStatus.WEAPON_DAMAGE_REFLECT)) {
                        pPlayer.addHP(-(7000 + Randomizer.nextInt(8000)));
                    }
                    pPlayer.onAttack(monster.getMobMaxHp(), monster.getMobMaxMp(), attack.skill, monster.getObjectId(), totDamage, 0);
                    switch (attack.skill) {
                        case 4001002:
                        case 4001334:
                        case 4001344:
                        case 4111005:
                        case 4121007:
                        case 4201005:
                        case 4211002:
                        case 4221001:
                        case 4221007:
                        case 4301001:
                        case 4311002:
                        case 4311003:
                        case 4331000:
                        case 4331004:
                        case 4331005:
                        case 4331006:
                        case 4341002:
                        case 4341004:
                        case 4341005:
                        case 4341009:
                        case 14001004:
                        case 14111002:
                        case 14111005:
                            int[] skills = {4120005, 4220005, 4340001, 14110004};
                            for (int i : skills) {
                                Skill skill = SkillFactory.getSkill(i);
                                if (pPlayer.getTotalSkillLevel(skill) > 0) {
                                    MapleStatEffect venomEffect = skill.getEffect(pPlayer.getTotalSkillLevel(skill));
                                    if (!venomEffect.makeChanceResult()) {
                                        break;
                                    }
                                    monster.applyStatus(pPlayer, new MonsterStatusEffect(MonsterStatus.POISON, Integer.valueOf(1), i, null, false), true, venomEffect.getDuration(), true, venomEffect);
                                    break;
                                }

                            }

                            break;
                        case 4201004:
                            monster.handleSteal(pPlayer);
                            break;
                        case 21000002:
                        case 21100001:
                        case 21100002:
                        case 21100004:
                        case 21110002:
                        case 21110003:
                        case 21110004:
                        case 21110006:
                        case 21110007:
                        case 21110008:
                        case 21120002:
                        case 21120005:
                        case 21120006:
                        case 21120009:
                        case 21120010:
                            if ((pPlayer.getBuffedValue(CharacterTemporaryStat.WeaponCharge) != null) && (!monster.getStats().isBoss())) {
                                MapleStatEffect eff = pPlayer.getStatForBuff(CharacterTemporaryStat.WeaponCharge);
                                if (eff != null) {
                                    monster.applyStatus(pPlayer, new MonsterStatusEffect(MonsterStatus.SPEED, Integer.valueOf(eff.getX()), eff.getSourceId(), null, false), false, eff.getY() * 1000, true, eff);
                                }
                            }
                            if ((pPlayer.getBuffedValue(CharacterTemporaryStat.BodyPressure) != null) && (!monster.getStats().isBoss())) {
                                MapleStatEffect eff = pPlayer.getStatForBuff(CharacterTemporaryStat.BodyPressure);

                                if ((eff != null) && (eff.makeChanceResult()) && (!monster.isBuffed(MonsterStatus.NEUTRALISE))) {
                                    monster.applyStatus(pPlayer, new MonsterStatusEffect(MonsterStatus.NEUTRALISE, Integer.valueOf(1), eff.getSourceId(), null, false), false, eff.getX() * 1000, true, eff);
                                }
                            }
                            break;
                    }
                    /*int randomDMG = Randomizer.nextInt(player.getDamage2() - player.getReborns() + 1) + player.getReborns();
                    monster.damage(player, randomDMG, true, attack.skill);
                    if (player.getshowdamage() == 1)
                        player.dropMessage(5, new StringBuilder().append("You have done ").append(randomDMG).append(" extra RB damage! (disable/enable this with @dmgnotice)").toString());
                    } else {
                    if (player.getDamage() > 2147483647L) {
                        long randomDMG = player.getDamage();
                        monster.damage(player, monster.getMobMaxHp(), true, attack.skill);
                        if (player.getshowdamage() == 1) {
                            player.dropMessage(5, new StringBuilder().append("You have done ").append(randomDMG).append(" extra RB damage! (disable/enable this with @dmgnotice)").toString());
                        }
                    }*/
                    if (totDamageToOneMonster > 0) {
                        Item weapon_ = pPlayer.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -11);
                        if (weapon_ != null) {
                            MonsterStatus stat = GameConstants.getStatFromWeapon(weapon_.getItemId());
                            if ((stat != null) && (Randomizer.nextInt(100) < GameConstants.getStatChance())) {
                                MonsterStatusEffect monsterStatusEffect = new MonsterStatusEffect(stat, Integer.valueOf(GameConstants.getXForStat(stat)), GameConstants.getSkillForStat(stat), null, false);
                                monster.applyStatus(pPlayer, monsterStatusEffect, false, 10000L, false, null);
                            }
                        }
                        if (pPlayer.getBuffedValue(CharacterTemporaryStat.Blind) != null) {
                            MapleStatEffect eff = pPlayer.getStatForBuff(CharacterTemporaryStat.Blind);

                            if ((eff != null) && (eff.makeChanceResult())) {
                                MonsterStatusEffect monsterStatusEffect = new MonsterStatusEffect(MonsterStatus.ACC, Integer.valueOf(eff.getX()), eff.getSourceId(), null, false);
                                monster.applyStatus(pPlayer, monsterStatusEffect, false, eff.getY() * 1000, true, eff);
                            }
                        }
                        if ((pPlayer.getJob() == 121) || (pPlayer.getJob() == 122)) {
                            Skill skill = SkillFactory.getSkill(1211006);
                            if (pPlayer.isBuffFrom(CharacterTemporaryStat.WeaponCharge, skill)) {
                                MapleStatEffect eff = skill.getEffect(pPlayer.getTotalSkillLevel(skill));
                                MonsterStatusEffect monsterStatusEffect = new MonsterStatusEffect(MonsterStatus.FREEZE, Integer.valueOf(1), skill.getId(), null, false);
                                monster.applyStatus(pPlayer, monsterStatusEffect, false, eff.getY() * 2000, true, eff);
                            }
                        }
                    }
                    if ((effect != null) && (effect.getMonsterStati().size() > 0) && (effect.makeChanceResult())) {
                        for (Map.Entry z : effect.getMonsterStati().entrySet()) {
                            monster.applyStatus(pPlayer, new MonsterStatusEffect((MonsterStatus) z.getKey(), (Integer) z.getValue(), theSkill.getId(), null, false), effect.isPoison(), effect.getDuration(), true, effect);
                        }
                    }
                }
                if (GameConstants.isLuminous(pPlayer.getJob())) {
                    //MagicAttack.handleLuminousState(player, attack); // Causes issue where damage is only dealt to one monster.
                    //pPlayer.handleLuminous(attack.skill);
                }

                // AntiCheat
                if (GameConstants.getAttackDelay(attack.skill, theSkill) >= 300 //Originally 100
                        && !GameConstants.isNoDelaySkill(attack.skill) && (attack.skill != 3101005) && (!monster.getStats().isBoss()) && (pPlayer.getTruePosition().distanceSq(monster.getTruePosition()) > GameConstants.getAttackRange(effect, pPlayer.getStat().defRange))) {
                    pPlayer.getCheatTracker().registerOffense(CheatingOffense.ATTACK_FARAWAY_MONSTER, new StringBuilder().append("[Distance: ").append(pPlayer.getTruePosition().distanceSq(monster.getTruePosition())).append(", Expected Distance: ").append(GameConstants.getAttackRange(effect, pPlayer.getStat().defRange)).append(" Job: ").append(pPlayer.getJob()).append("]").toString());
                }
            }
        }

        // Handle multi kills
        handleMultiKillsAndCombo(attack, pPlayer);

        // Handle Other Skills Below
        // Life Sap
        if (GameConstants.isDemonAvenger(pPlayer.getJob())) {
            if (pPlayer.getSkillLevel(31010002) > 0) {
                MapleStatEffect eff = SkillFactory.getSkill(31010002).getEffect(pPlayer.getSkillLevel(31010002));
                if (eff.makeChanceResult()) {
                    if (pPlayer.getExceed() / 2 > ((pPlayer.getSkillLevel(31210006) > 0 ? pPlayer.getSkillLevel(31210006) + 5 : 0) + eff.getX())) {
                        pPlayer.addHP((int) Math.min((totDamageToOneMonster * ((((pPlayer.getSkillLevel(31210006) > 0 ? pPlayer.getSkillLevel(31210006) + 5 : 0) + eff.getX()) - ((int) (pPlayer.getExceed() / 2))) / 100.0D)) * -1, pPlayer.getStat().getCurrentMaxHp() / 2));
                    } else {
                        pPlayer.addHP((int) Math.min((totDamageToOneMonster * ((((pPlayer.getSkillLevel(31210006) > 0 ? pPlayer.getSkillLevel(31210006) + 5 : 0) + eff.getX()) - ((int) (pPlayer.getExceed() / 2))) / 100.0D)), pPlayer.getStat().getCurrentMaxHp() / 2));
                    }
                }
            }
        }

        // Drain
        if (pPlayer.getBuffSource(CharacterTemporaryStat.AranDrain) == Aran.DRAIN) {
            Skill skill = SkillFactory.getSkill(Aran.DRAIN);
            pPlayer.addHP(Math.min(totDamage / 5, (totDamage * skill.getEffect(pPlayer.getSkillLevel(skill)).getX()) / 100));
        }
        if (pPlayer.hasSkill(DarkKnight.DARK_THIRST)) { // Hack fix for now.
            Skill skill = SkillFactory.getSkill(DarkKnight.DARK_THIRST);
            pPlayer.addHP(Math.min(totDamage / 5, (totDamage * skill.getEffect(pPlayer.getSkillLevel(skill)).getX()) / 100));
        }
        if (pPlayer.hasSkill(DemonAvenger.LIFE_SAP)) {
            Skill skill = SkillFactory.getSkill((DemonAvenger.LIFE_SAP));
            int nLifeGain = Math.min(totDamage / 2, (totDamage * skill.getEffect(pPlayer.getSkillLevel(skill)).getX()) / 100);
            if (pPlayer.hasSkill(DemonAvenger.ADVANCED_LIFE_SAP)) {
                nLifeGain *= 2;
            }
            pPlayer.addHP(nLifeGain);
        }

        // Prime Critical
        if (pPlayer.getJob() == 422) {
            int critical = pPlayer.acaneAim;
            if (attack.skill > 0) {
                //map.broadcastMessage(CField.CriticalGrowing(critical));
            }
            if (pPlayer.acaneAim <= 23) {
                pPlayer.acaneAim++;
            }
        }

        if (GameConstants.isLuminous(pPlayer.getJob())) {
            final Integer darkcrescendo_value = pPlayer.getBuffedValue(CharacterTemporaryStat.StackBuff);
            if (darkcrescendo_value != null && darkcrescendo_value != 1) {
                MapleStatEffect crescendo = SkillFactory.getSkill(27121005).getEffect(pPlayer.getSkillLevel(27121005));
                if (crescendo != null) {

                    if (crescendo.makeChanceResult()) {
                        pPlayer.setLastCombo(System.currentTimeMillis());
                        if (pPlayer.acaneAim <= 29) {
                            pPlayer.acaneAim++;
                            crescendo.applyTo(pPlayer);
                        }
                    }
                }
            }
        }

        if (pPlayer.getJob() >= 1500 && pPlayer.getJob() <= 1512) {
            MapleStatEffect crescendo = SkillFactory.getSkill(15001022).getEffect(pPlayer.getSkillLevel(15001022));
            if (crescendo != null) {

                if (crescendo.makeChanceResult()) {
                    pPlayer.setLastCombo(System.currentTimeMillis());
                    if (pPlayer.acaneAim <= 3) {
                        pPlayer.acaneAim++;
                        crescendo.applyTo(pPlayer);
                    }
                }
            }
        }

        // Combo
        if (pPlayer.getJob() >= 420 && pPlayer.getJob() <= 422) {
            MapleStatEffect crescendo = SkillFactory.getSkill(4200013).getEffect(pPlayer.getSkillLevel(4200013));
            if (crescendo != null) {

                if (crescendo.makeChanceResult()) {
                    pPlayer.setLastCombo(System.currentTimeMillis());
                    if (pPlayer.acaneAim <= 30) {
                        pPlayer.acaneAim++;
                        crescendo.applyTo(pPlayer);
                    }
                }
            }
        }
        if ((attack.skill == 4331003) && ((hpMob <= 0L) || (totDamageToOneMonster < hpMob))) {
            return;
        }
        if ((hpMob >= 0L) && (totDamageToOneMonster > 0)) {
            pPlayer.afterAttack(attack.mobCount, attack.numberOfHits, attack.skill);
        }
        if ((attack.skill != 0) && ((attack.mobCount > 0) || ((attack.skill != 4331003) && (attack.skill != 4341002))) && (!GameConstants.isNoDelaySkill(attack.skill))) {
            if (effect != null) {
                boolean applyTo = effect.applyTo(pPlayer, attack.position);
            }
        }
        if ((totDamage > 1) && (GameConstants.getAttackDelay(attack.skill, theSkill) >= 100)) {
            CheatTracker tracker = pPlayer.getCheatTracker();

            tracker.setAttacksWithoutHit(true);
            if (tracker.getAttacksWithoutHit() > 1000) {
                tracker.registerOffense(CheatingOffense.ATTACK_WITHOUT_GETTING_HIT, Integer.toString(tracker.getAttacksWithoutHit()));
            }
        }
        if (pPlayer.getSkillLevel(4100012) > 0) {
            MapleStatEffect eff = SkillFactory.getSkill(4100012).getEffect(pPlayer.getSkillLevel(4100012));
            if (eff.makeChanceResult()) {
                for (Map.Entry z : effect.getMonsterStati().entrySet()) {
                    for (AttackMonster ap : attack.allDamage) {
                        final MapleMonster monster = pPlayer.getMap().getMonsterByOid(ap.getObjectId());
                        monster.applyStatus(pPlayer,
                                new MonsterStatusEffect((MonsterStatus) z.getKey(), (Integer) z.getValue(),
                                        theSkill.getId(), null, false),
                                effect.isPoison(), effect.getDuration(), true, effect);
                        // }

                        // MonsterStatusEffect monsterStatusEffect = new
                        // MonsterStatusEffect(Collections.singletonMap(MonsterStatus.POISON,
                        // eff.getSourceId().getStats()),
                        // SkillFactory.getSkill(4100011), null, false);
                        // MonsterStatusEffect.setOwnerId(player.getId());
                        // monster.applyStatus(player, new
                        // MonsterStatusEffect(Collections.singletonMap(MonsterStatus.POISON,
                        // eff.getSourceId(), SkillFactory.getSkill(4100011),
                        // null, false), true, eff.getDuration(), false));
                        monster.applyStatus(pPlayer, new MonsterStatusEffect(MonsterStatus.POISON,
                                eff.getX(), eff.getSourceId(), null, false), false, eff.getY() * 1000,
                                true, eff);
                        monster.applyStatus(pPlayer,
                                new MonsterStatusEffect((MonsterStatus) z.getKey(), (Integer) z.getValue(),
                                        theSkill.getId(), null, false),
                                effect.isPoison(), effect.getDuration(), true, effect);
                    }
                }
            }

            int bulletCount = eff.getBulletCount();
            for (AttackMonster ap : attack.allDamage) {
                final MapleMonster source = pPlayer.getMap().getMonsterByOid(ap.getObjectId());

                // source.get
                final MonsterStatusEffect check = source.getBuff(MonsterStatus.POISON);

                // if (check != null && check.getSkill().getId() == 4100011 &&
                // check.getOwnerId() == player.getId()) {
                if (check != null && check.getSkill() == 4100011 && check.getOwnerId() == pPlayer.getId()) { // :3
                    final List<MapleMapObject> objs = pPlayer.getMap().getMapObjectsInRange(pPlayer.getPosition(), 500000,
                            Arrays.asList(MapleMapObjectType.MONSTER));
                    final List<MapleMonster> monsters = new ArrayList<>();
                    for (int i = 0; i < bulletCount; i++) {
                        int rand = Randomizer.rand(0, objs.size() - 1);
                        if (objs.size() < bulletCount) {
                            if (i < objs.size()) {
                                monsters.add((MapleMonster) objs.get(i));
                            }
                        } else {
                            monsters.add((MapleMonster) objs.get(rand));
                            objs.remove(rand);
                        }
                    }
                    if (monsters.size() <= 0) {
                        CWvsContext.enableActions();
                        return;
                    }
                    final List<Point> points = new ArrayList<>();
                    for (MapleMonster mob : monsters) {
                        points.add(mob.getPosition());
                    }
                    pPlayer.getMap().broadcastMessage(CWvsContext.giveMarkOfTheif(pPlayer.getId(), source.getObjectId(),
                            4100012, monsters, pPlayer.getPosition(), monsters.get(0).getPosition(), 2070005));
                }
            }
        }
        if (pPlayer.getJob() == 412) {
            for (AttackMonster ap : attack.allDamage) {
                // final MapleMonster source = player.getMap().getMonsterByOid(ap.getObjectId());

                final List<MapleMapObject> objs = pPlayer.getMap().getMapObjectsInRange(pPlayer.getPosition(), 500000,
                        Arrays.asList(MapleMapObjectType.MONSTER));

                final List<MapleMonster> monsters = new ArrayList<>();

                pPlayer.getMap().broadcastMessage(CWvsContext.giveMarkOfTheif(pPlayer.getId(), ap.getObjectId(),
                        4100012, monsters, pPlayer.getPosition(), ap.getPosition(), 2070005));
            }
        }
    }

    public static void applyAttackMagic(AttackInfo attack, Skill theSkill, MapleCharacter oPlayer, MapleStatEffect effect) {

        if (attack.real && GameConstants.getAttackDelay(attack.skill, theSkill) >= 100) {
            oPlayer.getCheatTracker().checkAttack(attack.skill, attack.lastAttackTickCount);
        }

        // Basic checks
        if (!oPlayer.isAlive()) {
            oPlayer.getCheatTracker().registerOffense(CheatingOffense.ATTACKING_WHILE_DEAD);
            return;
        } else if (!oPlayer.getMap().getSharedMapResources().noSkillInfo.isSkillUsable(oPlayer.getJob(), attack.skill)) {
            oPlayer.getCheatTracker().registerOffense(CheatingOffense.ATTACKING_IN_UNAVAILABLE_MAP);
            return;
        }

        if (effect != null) {
            if (effect.getBulletCount() > 1) {
                if ((attack.numberOfHits > effect.getBulletCount()) || (attack.mobCount > effect.getMobCount())) {
                    oPlayer.getCheatTracker().registerOffense(CheatingOffense.MISMATCHING_BULLETCOUNT);
                    return;
                }
            } else if (((attack.numberOfHits > effect.getAttackCount()) && (effect.getAttackCount() != 0)) || (attack.mobCount > effect.getMobCount())) {
                oPlayer.getCheatTracker().registerOffense(CheatingOffense.MISMATCHING_BULLETCOUNT);
                return;
            }

        }
        if ((attack.numberOfHits > 0) && (attack.mobCount > 0) && (!oPlayer.getStat().checkEquipDurabilitys(oPlayer, -1))) {
            oPlayer.dropMessage(5, "An item has run out of durability but has no inventory room to go to.");
            return;
        }

        // Max damage the player could deal
        double maxdamage = oPlayer.getStat().getCurrentMaxBaseDamage() * (effect.getDamage() + oPlayer.getStat().getDamageIncrease(attack.skill)) / 100.0D;

        if (theSkill != null && GameConstants.isBeginnerJob(theSkill.getId() / 10000) && theSkill.getId() % 10000 == 1000) {
            maxdamage = 40.0D;
        } else if (GameConstants.isMulungSkill(attack.skill)) {
            if (oPlayer.getMapId() / 10000 != 92502) {
                return;
            }
            if (oPlayer.getMulungEnergy() < 10000) {
                return;
            }
            oPlayer.mulungEnergyModifier(false);
        } else if (GameConstants.isPyramidSkill(attack.skill)) {
            maxdamage = 1.0D;

            if (oPlayer.getMapId() / 1000000 != 926) {
                return;
            }
            if ((oPlayer.getPyramidSubway() != null) && (oPlayer.getPyramidSubway().onSkillUse(oPlayer)));
        } else if ((GameConstants.isInflationSkill(attack.skill)) && (oPlayer.getBuffedValue(CharacterTemporaryStat.Inflation) == null)) {
            return;
        }

        if (ServerConstants.ADMIN_MODE) {
            oPlayer.dropMessage(-1, new StringBuilder().append("Animation: ").append(Integer.toHexString((attack.display & 0x8000) != 0 ? attack.display - 32768 : attack.display)).toString());
        }
        PlayerStats stats = oPlayer.getStat();

        Element element = null;
        if (oPlayer.getBuffedValue(CharacterTemporaryStat.ElementalReset) != null) {
            element = Element.NEUTRAL;
        } else if (theSkill != null) {
            element = theSkill.getElement();
        }

        double MaxDamagePerHit = 0.0D;
        int totDamage = 0;

        final int playerDamageCap = GameConstants.damageCap + oPlayer.getStat().damageCapIncrease; // The damage cap the player is allowed to hit.
        int CriticalDamage = stats.passive_sharpeye_percent();
        Skill eaterSkill = SkillFactory.getSkill(GameConstants.getMPEaterForJob(oPlayer.getJob()));
        int eaterLevel = oPlayer.getTotalSkillLevel(eaterSkill);

        for (AttackMonster oned : attack.allDamage) {
            final MapleMonster monster = oned.getMonster();
            if (monster == null || monster.getId() != oned.getMonsterId()) {
                continue;
            }

            if (monster.getLinkCID() <= 0) {
                boolean Tempest = (monster.getStatusSourceID(MonsterStatus.FREEZE) == 21120006) && (!monster.getStats().isBoss());
                int totDamageToOneMonster = 0;
                MapleMonsterStats monsterstats = monster.getStats();
                int fixeddmg = monsterstats.getFixedDamage();

                if (!Tempest && !oPlayer.isGM()) {
                    if ((!monster.isBuffed(MonsterStatus.MAGIC_IMMUNITY)) && (!monster.isBuffed(MonsterStatus.MAGIC_DAMAGE_REFLECT))) {
                        MaxDamagePerHit = calculateMaxMagicDamagePerHit(oPlayer, theSkill, monster, monsterstats, stats, element, CriticalDamage, maxdamage, effect);
                    } else {
                        MaxDamagePerHit = 1.0D;
                    }
                }

                byte overallAttackCount = 0;

                for (Pair eachde : oned.getAttacks()) {
                    long eachd = (Long) eachde.left;
                    overallAttackCount = (byte) (overallAttackCount + 1);
                    if (fixeddmg != -1) {
                        eachd = monsterstats.getOnlyNoramlAttack() ? 0 : fixeddmg;
                    } else if (monsterstats.getOnlyNoramlAttack()) {
                        eachd = 0;
                    } else if (!oPlayer.isGM()) {
                        if (Tempest) {
                            if (eachd > monster.getMobMaxHp()) {
                                eachd = (int) Math.min(monster.getMobMaxHp(), 2147483647L);
                                oPlayer.getCheatTracker().registerOffense(CheatingOffense.HIGH_DAMAGE_MAGIC);
                            }
                        } else if ((!monster.isBuffed(MonsterStatus.MAGIC_IMMUNITY)) && (!monster.isBuffed(MonsterStatus.MAGIC_DAMAGE_REFLECT))) {
                            if (eachd > MaxDamagePerHit) {
                                oPlayer.getCheatTracker().registerOffense(CheatingOffense.HIGH_DAMAGE_MAGIC, new StringBuilder().append("[Damage: ").append(eachd).append(", Expected: ").append(MaxDamagePerHit).append(", Mob: ").append(monster.getId()).append("] [Job: ").append(oPlayer.getJob()).append(", Level: ").append(oPlayer.getLevel()).append(", Skill: ").append(attack.skill).append("]").toString());
                                if (attack.real) {
                                    oPlayer.getCheatTracker().checkSameDamage(eachd, MaxDamagePerHit);
                                }
                                if (eachd > MaxDamagePerHit * 2.0D) {
                                    oPlayer.getCheatTracker().registerOffense(CheatingOffense.HIGH_DAMAGE_MAGIC_2, new StringBuilder().append("[Damage: ").append(eachd).append(", Expected: ").append(MaxDamagePerHit).append(", Mob: ").append(monster.getId()).append("] [Job: ").append(oPlayer.getJob()).append(", Level: ").append(oPlayer.getLevel()).append(", Skill: ").append(attack.skill).append("]").toString());
                                    eachd = (int) (MaxDamagePerHit * 2.0D);

                                    if (eachd >= playerDamageCap) {
                                        oPlayer.getClient().close();
                                    }

                                    // Attempt at Anti-Cheat
                                    if (overallAttackCount > 100) {
                                        oPlayer.yellowMessage("[AntiCheat: Protoype] Please remember hacking goes against our Terms of Service.");
                                        return;
                                    }
                                }
                            }

                        } else if (eachd > MaxDamagePerHit) {
                            eachd = (int) MaxDamagePerHit;
                        }

                    }

                    totDamageToOneMonster += eachd;
                }
                
                totDamage += totDamageToOneMonster;

                if ((GameConstants.getAttackDelay(attack.skill, theSkill) >= 100) && (!GameConstants.isNoDelaySkill(attack.skill)) && !GameConstants.isMismatchingBulletSkill(attack.skill) && (!monster.getStats().isBoss()) && (oPlayer.getTruePosition().distanceSq(monster.getTruePosition()) > GameConstants.getAttackRange(effect, oPlayer.getStat().defRange))) {
                    oPlayer.getCheatTracker().registerOffense(CheatingOffense.ATTACK_FARAWAY_MONSTER, new StringBuilder().append("[Distance: ").append(oPlayer.getTruePosition().distanceSq(monster.getTruePosition())).append(", Expected Distance: ").append(GameConstants.getAttackRange(effect, oPlayer.getStat().defRange)).append(" Job: ").append(oPlayer.getJob()).append("]").toString());
                    oPlayer.yellowMessage("[AntiCheat] Please remember hacking goes against our Terms of Service. Skill (" + attack.skill + ")");
                    return;
                }
                if ((attack.skill == 2301002) && (!monsterstats.getUndead())) {
                    oPlayer.getCheatTracker().registerOffense(CheatingOffense.HEAL_ATTACKING_UNDEAD);
                    return;
                }

                // Apply damage to monster
                monster.damage(oPlayer, totDamageToOneMonster, true, attack.skill);

                // Monster is still alive after being hit.
                if (monster.isAlive()) {
                    oPlayer.checkMonsterAggro(monster);
                } else {
                    attack.after_NumMobsKilled++;
                }

            }

            if (GameConstants.isLuminous(oPlayer.getJob())) {
                final Integer dark_cresendo_value = oPlayer.getBuffedValue(CharacterTemporaryStat.Larkness);

                if (dark_cresendo_value != null && dark_cresendo_value != 1) {
                    MapleStatEffect crescendo = SkillFactory.getSkill(27121005).getEffect(oPlayer.getSkillLevel(27121005));
                    if (crescendo != null) {

                        if (crescendo.makeChanceResult()) {
                            oPlayer.setLastCombo(System.currentTimeMillis());
                            if (oPlayer.acaneAim <= 29) {
                                oPlayer.acaneAim++;
                                crescendo.applyTo(oPlayer);
                            }
                        }
                    }
                }
            }

            if (oPlayer.getJob() >= 1500 && oPlayer.getJob() <= 1512) {
                MapleStatEffect crescendo = SkillFactory.getSkill(15001022).getEffect(oPlayer.getSkillLevel(15001022));
                if (crescendo != null) {

                    if (crescendo.makeChanceResult()) {
                        oPlayer.setLastCombo(System.currentTimeMillis());
                        if (oPlayer.acaneAim <= 3) {
                            oPlayer.acaneAim++;
                            crescendo.applyTo(oPlayer);
                        }
                    }
                }
            }

            if (attack.skill != 2301002) {
                effect.applyTo(oPlayer);
            }
            if (totDamage > 1 && GameConstants.getAttackDelay(attack.skill, theSkill) >= 100) {
                CheatTracker tracker = oPlayer.getCheatTracker();
                tracker.setAttacksWithoutHit(true);

                if (tracker.getAttacksWithoutHit() > 1000) {
                    tracker.registerOffense(CheatingOffense.ATTACK_WITHOUT_GETTING_HIT, Integer.toString(tracker.getAttacksWithoutHit()));
                }
            }
        }

        // Handle multi kills
        handleMultiKillsAndCombo(attack, oPlayer);
    }

    /**
     * Handles the multi kill display and additional EXP gain whenever the player kills > 1 monster in a single attack Handles the combos
     * gained for each attack that kills a monster.
     *
     * Added on You & I Update http://x3thearan59.weebly.com/you--i.html
     *
     * @param attack
     * @param player
     */
    private static void handleMultiKillsAndCombo(AttackInfo attack, MapleCharacter player) {
        if (attack.after_NumMobsKilled > 0) {
            if (attack.allDamage.isEmpty()) {
                return;
            }
            final Optional<AttackMonster> mob = attack.allDamage.stream().findFirst().filter(s -> !s.getMonster().isAlive());
            if (mob == null || !mob.isPresent()) {
                return;
            }

            // Handle multi kill
            if (attack.after_NumMobsKilled > 2) {
                int numMultiKills = Math.min(10, attack.after_NumMobsKilled);

                MapleMonsterStats stats = MapleLifeFactory.getMonsterStats(mob.get().getMonsterId());
                if (stats != null) {
                    final long monsterExp = stats.getExp();
                    final int additionalExpPercentage = 5 * (numMultiKills - 2);
                    double expRate_Server = player.getClient().getChannelServer().getExpRate(player.getWorld());

                    final long totalEXPGained = (long) (((long) (expRate_Server * monsterExp)) * (additionalExpPercentage * 0.01f));

                    player.gainExp(totalEXPGained, false, false, false);
                    player.getClient().write(CWvsContext.messagePacket(new StylishKillMessage(StylishKillMessageType.MultiKill, totalEXPGained, numMultiKills)));
                }

            }

            // Handle combo kill
            short combo = player.getCombo();
            long cTime = System.currentTimeMillis();

            if (cTime - player.getLastComboTime() > 15L * 1000L) { // 10 seconds
                combo = 0;
            } else {
                combo = (short) Math.min(1000, combo + 1);

                if (combo % 50 == 0) {
                    final int itemid;
                    if (combo <= 300) {
                        itemid = ItemConstants.COMBO_PARADE1;
                    } else if (combo <= 700) {
                        itemid = ItemConstants.COMBO_PARADE2;
                    } else {
                        itemid = ItemConstants.COMBO_PARADE3;
                    }
                    Item drop = new Item(itemid, (byte) 0, (short) 1, (byte) 0);

                    Point mobPos = mob.get().getPosition();
                    Point playerPos = player.getPosition();
                    Point pos = player.getMap().calcDropPos(
                            new Point(
                                    (int) mobPos.getX() + (playerPos.getX() > mobPos.getX() ? -50 : 50), // don't put it near player no matter what
                                    (int) mobPos.getY()), mobPos);

                    player.getMap().spawnItemDrop(player, player, drop, pos, false, true, true);

                    if (combo == 1000) {
                        combo = 0; // reset
                    }
                }
            }
            player.setLastCombo(cTime);
            player.setCombo(combo);

            player.getClient().write(CWvsContext.messagePacket(new StylishKillMessage(StylishKillMessage.StylishKillMessageType.Combo, combo, mob.get().getObjectId())));
        }
    }

    private static double calculateMaxMagicDamagePerHit(MapleCharacter chr, Skill skill, MapleMonster monster, MapleMonsterStats mobstats, PlayerStats stats, Element elem, Integer sharpEye, double maxDamagePerMonster, MapleStatEffect attackEffect) {
        int dLevel = Math.max(mobstats.getLevel() - chr.getLevel(), 0) * 2;
        int HitRate = Math.min((int) Math.floor(Math.sqrt(stats.getAccuracy())) - (int) Math.floor(Math.sqrt(mobstats.getEva())) + 100, 100);
        if (dLevel > HitRate) {
            HitRate = dLevel;
        }
        HitRate -= dLevel;
        if ((HitRate <= 0) && ((!GameConstants.isBeginnerJob(skill.getId() / 10000)) || (skill.getId() % 10000 != 1000))) {
            return 0.0D;
        }

        int CritPercent = sharpEye;
        ElementalEffectiveness ee = monster.getEffectiveness(elem);
        double elemMaxDamagePerMob;
        switch (ee) {
            case IMMUNE:
                elemMaxDamagePerMob = 1.0D;
                break;
            default:
                elemMaxDamagePerMob = elementalStaffAttackBonus(elem, maxDamagePerMonster * ee.getValue(), stats);
        }

        int MDRate = monster.getStats().getMDRate();
        MonsterStatusEffect pdr = monster.getBuff(MonsterStatus.MDD);
        if (pdr != null) {
            MDRate += pdr.getX();
        }
        elemMaxDamagePerMob -= elemMaxDamagePerMob * (Math.max(MDRate - stats.ignoreTargetDEF - attackEffect.getIgnoreMob(), 0) / 100.0D);

        elemMaxDamagePerMob += elemMaxDamagePerMob / 100.0D * CritPercent;

        elemMaxDamagePerMob *= (monster.getStats().isBoss() ? chr.getStat().bossdam_r : chr.getStat().dam_r) / 100.0D;
        MonsterStatusEffect imprint = monster.getBuff(MonsterStatus.IMPRINT);
        if (imprint != null) {
            elemMaxDamagePerMob += elemMaxDamagePerMob * imprint.getX() / 100.0D;
        }
        elemMaxDamagePerMob += elemMaxDamagePerMob * chr.getDamageIncrease(monster.getObjectId()) / 100.0D;
        if (GameConstants.isBeginnerJob(skill.getId() / 10000)) {
            switch (skill.getId() % 10000) {
                case 1000:
                    elemMaxDamagePerMob = 40.0D;
                    break;
                case 1020:
                    elemMaxDamagePerMob = 1.0D;
                    break;
                case 1009:
                    elemMaxDamagePerMob = monster.getStats().isBoss() ? monster.getMobMaxHp() / 30L * 100L : monster.getMobMaxHp();
            }
        }

        switch (skill.getId()) {
            case 32001000:
            case 32101000:
            case 32111002:
            case 32121002:
                elemMaxDamagePerMob *= 1.5D;
        }

        if (elemMaxDamagePerMob > GameConstants.damageCap) {
            elemMaxDamagePerMob = GameConstants.damageCap;

            elemMaxDamagePerMob *= stats.starForceDamageRate;

        }
        // cap it to 1 if below
        if (elemMaxDamagePerMob <= 0.0D) {
            elemMaxDamagePerMob = 1.0D;
        }

        return elemMaxDamagePerMob;
    }

    private static double elementalStaffAttackBonus(Element elem, double elemMaxDamagePerMob, PlayerStats stats) {
        switch (elem) {
            case FIRE:
                return elemMaxDamagePerMob / 100.0D * (stats.element_fire + stats.getElementBoost(elem));
            case ICE:
                return elemMaxDamagePerMob / 100.0D * (stats.element_ice + stats.getElementBoost(elem));
            case LIGHTING:
                return elemMaxDamagePerMob / 100.0D * (stats.element_light + stats.getElementBoost(elem));
            case POISON:
                return elemMaxDamagePerMob / 100.0D * (stats.element_psn + stats.getElementBoost(elem));
            case DARKNESS:
                break;
            case HOLY:
                break;
            case NEUTRAL:
                break;
            case PHYSICAL:
                break;
            default:
                break;
        }
        return elemMaxDamagePerMob / 100.0D * (stats.def + stats.getElementBoost(elem));
    }

    private static void handlePickPocket(MapleCharacter player, MapleMonster mob, AttackMonster oned) {
        if (Randomizer.nextInt(99) <= player.getStat().pickRate) {
            player.getMap().spawnMesoDrop(1, new Point((int) (mob.getTruePosition().getX() + Randomizer.nextInt(100) - 50.0D), (int) mob.getTruePosition().getY()), mob, player, false, (byte) 0);
        }
    }

    private static double calculateMaxWeaponDamagePerHit(MapleCharacter player, MapleMonster monster, AttackInfo attack, Skill theSkill, MapleStatEffect attackEffect, double maximumDamageToMonster, Integer CriticalDamagePercent) {
        int dLevel = Math.max(monster.getStats().getLevel() - player.getLevel(), 0) * 2;
        int HitRate = Math.min((int) Math.floor(Math.sqrt(player.getStat().getAccuracy())) - (int) Math.floor(Math.sqrt(monster.getStats().getEva())) + 100, 100);
        if (dLevel > HitRate) {
            HitRate = dLevel;
        }
        HitRate -= dLevel;
        if ((HitRate <= 0) && ((!GameConstants.isBeginnerJob(attack.skill / 10000)) || (attack.skill % 10000 != 1000)) && (!GameConstants.isPyramidSkill(attack.skill)) && (!GameConstants.isMulungSkill(attack.skill)) && (!GameConstants.isInflationSkill(attack.skill))) {
            return 0.0D;
        }
        if ((player.getMapId() / 1000000 == 914) || (player.getMapId() / 1000000 == 927)) {
            return 999999.0D;
        }

        List<Element> elements = new ArrayList<>();
        boolean defined = false;
        int CritPercent = CriticalDamagePercent;
        int PDRate = monster.getStats().getPDRate();
        MonsterStatusEffect pdr = monster.getBuff(MonsterStatus.PDD);
        if (pdr != null) {
            PDRate += pdr.getX();
        }
        if (theSkill != null) {
            elements.add(theSkill.getElement());
            if (GameConstants.isBeginnerJob(theSkill.getId() / 10000)) {
                switch (theSkill.getId() % 10000) {
                    case 1000:
                        maximumDamageToMonster = 40.0D;
                        defined = true;
                        break;
                    case 1020:
                        maximumDamageToMonster = 1.0D;
                        defined = true;
                        break;
                    case 1009:
                        maximumDamageToMonster = monster.getStats().isBoss() ? monster.getMobMaxHp() / 30L * 100L : monster.getMobMaxHp();
                        defined = true;
                }
            }
            if (attackEffect != null) {
                switch (theSkill.getId()) {
                    case 1311005:
                        PDRate = monster.getStats().isBoss() ? PDRate : 0;
                        break;
                    case 3221001:
                    case 33101001:
                        maximumDamageToMonster *= attackEffect.getMobCount();
                        defined = true;
                        break;
                    case 3101005:
                        defined = true;
                        break;
                    case 32001000:
                    case 32101000:
                    case 32111002:
                    case 32121002:
                        maximumDamageToMonster *= 1.5D;
                        break;
                    case 1221009:
                    case 3221007:
                    case 4331003:
                    case 23121003:
                        if (!monster.getStats().isBoss()) {
                            maximumDamageToMonster = monster.getMobMaxHp();
                            defined = true;
                        }
                        break;
                    case 1221011:
                    case 21120006:
                        maximumDamageToMonster = monster.getStats().isBoss() ? 500000.0D : monster.getHp() - 1L;
                        defined = true;
                        break;
                    case 3211006:
                        if (monster.getStatusSourceID(MonsterStatus.FREEZE) == 3211003) {
                            defined = true;
                            maximumDamageToMonster = 999999.0D;
                        }
                        break;
                }
            }
        }
        double elementalMaxDamagePerMonster = maximumDamageToMonster;
        if ((player.getJob() == 311) || (player.getJob() == 312) || (player.getJob() == 321) || (player.getJob() == 322)) {
            Skill mortal = SkillFactory.getSkill((player.getJob() == 311) || (player.getJob() == 312) ? 3110001 : 3210001);
            if (player.getTotalSkillLevel(mortal) > 0) {
                MapleStatEffect mort = mortal.getEffect(player.getTotalSkillLevel(mortal));
                if ((mort != null) && (monster.getHPPercent() < mort.getX())) {
                    elementalMaxDamagePerMonster = 999999.0D;
                    defined = true;
                    if (mort.getZ() > 0) {
                        player.addHP(player.getStat().getMaxHp() * mort.getZ() / 100);
                    }
                }
            }
        } else if ((player.getJob() == 221) || (player.getJob() == 222)) {
            Skill mortal = SkillFactory.getSkill(2210000);
            if (player.getTotalSkillLevel(mortal) > 0) {
                MapleStatEffect mort = mortal.getEffect(player.getTotalSkillLevel(mortal));
                if ((mort != null) && (monster.getHPPercent() < mort.getX())) {
                    elementalMaxDamagePerMonster = 999999.0D;
                    defined = true;
                }
            }
        }
        if ((!defined) || ((theSkill != null) && ((theSkill.getId() == 33101001) || (theSkill.getId() == 3221001)))) {
            if (player.getBuffedValue(CharacterTemporaryStat.WeaponCharge) != null) {
                int chargeSkillId = player.getBuffSource(CharacterTemporaryStat.WeaponCharge);

                switch (chargeSkillId) {
                    case 1211003:
                    case 1211004:
                        elements.add(Element.FIRE);
                        break;
                    case 1211005:
                    case 1211006:
                    case 21111005:
                        elements.add(Element.ICE);
                        break;
                    case 1211007:
                    case 1211008:
                    case 15101006:
                        elements.add(Element.LIGHTING);
                        break;
                    case 1221003:
                    case 1221004:
                    case 11111007:
                        elements.add(Element.HOLY);
                        break;
                    case 12101005:
                }

            }

            if (player.getBuffedValue(CharacterTemporaryStat.AssistCharge) != null) {
                elements.add(Element.LIGHTING);
            }
            if (player.getBuffedValue(CharacterTemporaryStat.ElementalReset) != null) {
                elements.clear();
            }
            double elementalEffect;
            if (elements.size() > 0) {
                switch (attack.skill) {
                    case 3111003:
                    case 3211003:
                        elementalEffect = attackEffect.getX() / 100.0D;
                        break;
                    default:
                        elementalEffect = 0.5D / elements.size();
                }

                for (Element element : elements) {
                    switch (monster.getEffectiveness(element)) {
                        case IMMUNE:
                            elementalMaxDamagePerMonster = 1.0D;
                            break;
                        case WEAK:
                            elementalMaxDamagePerMonster *= (1.0D + elementalEffect + player.getStat().getElementBoost(element));
                            break;
                        case STRONG:
                            elementalMaxDamagePerMonster *= (1.0D - elementalEffect - player.getStat().getElementBoost(element));
                        case NORMAL:
                            break;
                        default:
                            break;
                    }

                }

            }

            elementalMaxDamagePerMonster -= elementalMaxDamagePerMonster * (Math.max(PDRate - Math.max(player.getStat().ignoreTargetDEF, 0) - Math.max(attackEffect == null ? 0 : attackEffect.getIgnoreMob(), 0), 0) / 100.0D);

            elementalMaxDamagePerMonster += elementalMaxDamagePerMonster / 100.0D * CritPercent;

            MonsterStatusEffect imprint = monster.getBuff(MonsterStatus.IMPRINT);
            if (imprint != null) {
                elementalMaxDamagePerMonster += elementalMaxDamagePerMonster * imprint.getX() / 100.0D;
            }

            elementalMaxDamagePerMonster += elementalMaxDamagePerMonster * player.getDamageIncrease(monster.getObjectId()) / 100.0D;
            elementalMaxDamagePerMonster *= ((monster.getStats().isBoss()) && (attackEffect != null) ? player.getStat().bossdam_r + attackEffect.getBossDamage() : player.getStat().dam_r) / 100.0D;
        }
        if (elementalMaxDamagePerMonster > 999999.0D) {
            if (!defined) {
                elementalMaxDamagePerMonster = 999999.0D;
            }
        }
        elementalMaxDamagePerMonster *= player.getStat().starForceDamageRate;

        // cap it to 1 if below
        if (elementalMaxDamagePerMonster <= 0.0D) {
            elementalMaxDamagePerMonster = 1.0D;
        }
        return elementalMaxDamagePerMonster;
    }

    public static final AttackInfo divideAttack(final AttackInfo attack, final int rate) {
        attack.real = false;
        if (rate <= 1) {
            return attack; //lol
        }
        for (AttackMonster p : attack.allDamage) {
            if (p.getAttacks() != null) {
                for (Pair<Long, Boolean> eachd : p.getAttacks()) {
                    eachd.left /= rate; //too ex.
                }
            }
        }
        return attack;
    }

    public static final void modifyCriticalAttack(AttackInfo pAttack, MapleCharacter pPlayer, int nType, MapleStatEffect pEffect) {
        int nCriticalRate;
        boolean bShadow;
        List damages;
        boolean bCritical = false;
        List damage;
        if ((pAttack.skill != 4211006) && (pAttack.skill != 3211003) && (pAttack.skill != 4111004)) {
            nCriticalRate = pPlayer.getStat().passive_sharpeye_rate() + (pEffect == null ? 0 : pEffect.getCr());
            boolean bMirror = pPlayer.hasBuff(CharacterTemporaryStat.ShadowPartner) || pPlayer.hasBuff(CharacterTemporaryStat.ShadowServant); 
            bShadow = bMirror && ((nType == 1) || (nType == 2));
            damages = new ArrayList<>();
            damage = new ArrayList<>();

            for (AttackMonster p : pAttack.allDamage) {
                if (p.getAttacks() != null) {
                    int hit = 0;
                    int mid_att = bShadow ? p.getAttacks().size() / 2 : p.getAttacks().size();

                    int toCrit = (pAttack.skill == 4221001) || (pAttack.skill == 3221007) || (pAttack.skill == 23121003) || (pAttack.skill == 4341005) || (pAttack.skill == 4331006) || (pAttack.skill == 21120005) ? mid_att : 0;
                    if (toCrit == 0) {
                        for (Pair eachd : p.getAttacks()) {
                            if ((!(Boolean) eachd.right) && hit < mid_att) {
                                if (((Long) eachd.left > 999999) || (Randomizer.nextInt(100) < nCriticalRate)) {
                                    toCrit++;
                                }
                                damage.add(eachd.left);
                            }
                            hit++;
                        }
                        if (toCrit == 0) {
                            damage.clear();
                        } else {
                            Collections.sort(damage);
                            for (int i = damage.size(); i > damage.size() - toCrit; i--) {
                                damages.add(damage.get(i - 1));
                            }
                            damage.clear();
                        }
                    } else {
                        hit = 0;
                        for (Pair<Long, Boolean> eachd : p.getAttacks()) {
                            if (!eachd.right) {
                                if (pAttack.skill == 4221001) {
                                    eachd.right = Boolean.valueOf(hit == 3);
                                } else if ((pAttack.skill == 3221007) || (pAttack.skill == 23121003) || (pAttack.skill == 21120005) || (pAttack.skill == 4341005) || (pAttack.skill == 4331006) || (((Long) eachd.left).longValue() > 999999)) {
                                    eachd.right = Boolean.valueOf(true);
                                } else if (hit >= mid_att) {
                                    eachd.right = p.getAttacks().get(hit - mid_att).right;
                                } else {
                                    eachd.right = Boolean.valueOf(damages.contains(eachd.left));
                                }
                                if (eachd.right) {
                                    bCritical = true;
                                }
                            }
                            hit++;
                        }
                        damages.clear();
                    }
                }
                if (GameConstants.isThiefShadower(pPlayer.getJob())) {
                    if (Randomizer.nextInt(100) < 50) { // TODO: Handle on critical strike correctly instead.
                        pPlayer.getMap().broadcastMessage(ShadowerPacket.toggleFlipTheCoin(true));
                    }
                }
                
                if (bCritical) {
                    if (GameConstants.isThiefShadower(pPlayer.getJob())) {
                        pPlayer.getMap().broadcastMessage(ShadowerPacket.toggleFlipTheCoin(true));
                    }
                    /*if (chr.getJob() == 422 && chr.dualBrid == 0 && chr.acaneAim < 5) {
                        chr.getMap().broadcastMessage(CField.OnOffFlipTheCoin(true));
                        chr.acaneAim++;
                        chr.dualBrid = 1;
                    }*/
                }
            }
        }
    }

    public static AttackInfo OnAttack(RecvPacketOpcode eType, InPacket iPacket, MapleCharacter chr) {
        AttackInfo ret = new AttackInfo();
        if (eType == RecvPacketOpcode.UserShootAttack) {
            iPacket.DecodeByte();
        }
        if (eType == RecvPacketOpcode.UserNonTargetForceAtomAttack) {
            iPacket.DecodeInteger(); // nSkillID
            iPacket.DecodeInteger(); // Unknown
            iPacket.DecodeInteger(); // Unknown
        }
        int bFieldKey = iPacket.DecodeByte();
        ret.allDamage = new ArrayList<>();
        ret.tbyte = iPacket.DecodeByte();
        ret.mobCount = (ret.tbyte >>> 4 & 0xF);
        ret.numberOfHits = ((byte) (ret.tbyte & 0xF));
        ret.skill = iPacket.DecodeInteger();

        if (ServerConstants.DEVELOPER_DEBUG_MODE) {
            System.err.println("[Damage Operation] Skill (" + ret.skill + ")");
        }

        ret.skillLevel = iPacket.DecodeByte();
        boolean bAddAttackProc = false;
        if (eType != RecvPacketOpcode.UserMagicAttack && eType != RecvPacketOpcode.UserBodyAttack) {
            bAddAttackProc = iPacket.DecodeBoolean();
        }
        int nBulletItemID = 0;
        int nBulletCashItemID = 0;
        int nBulletItemPos = 0;
        int nBulletCashItemPos = 0;
        int nShootRange = 0;

        int dwMobCRC = iPacket.DecodeInteger();
        if (eType != RecvPacketOpcode.UserNonTargetForceAtomAttack) { // This is actually a sub, not sure what it does yet
            iPacket.DecodeByte(); // Unknown
            nBulletItemPos = iPacket.DecodeShort();
            iPacket.DecodeInteger(); // Unknown
        }
        ret.slot = (byte) nBulletItemPos;
        int tDelay = 0;
        int nBySummonedID = 0;
        if (eType == RecvPacketOpcode.UserMeleeAttack && (Skill.isKeydownSkill(ret.skill) || Skill.isSupernovaSkill(ret.skill))) {
            ret.charge = iPacket.DecodeInteger();
        } else if ((eType == RecvPacketOpcode.UserShootAttack || eType == RecvPacketOpcode.UserMagicAttack) && Skill.isKeydownSkill(ret.skill)) {
            ret.charge = iPacket.DecodeInteger();
        }
        int nGrenade = 0;
        if (eType == RecvPacketOpcode.UserMeleeAttack) {
            if (Skill.isRushBombSkill(ret.skill) || ret.skill == 5300007 || ret.skill == 27120211 || ret.skill == 14111023
                    || ret.skill == 400031003 || ret.skill == 400031004 || ret.skill == 80011389 || ret.skill == 80011390) {
                nGrenade = iPacket.DecodeInteger();
            }
        }
        boolean bZeroTag = false;
        if (Skill.isZeroSkill(ret.skill)) {
            bZeroTag = iPacket.DecodeBoolean();
        }
        if (Skill.IsUsercloneSummonableSkill(ret.skill)) {
            nBySummonedID = iPacket.DecodeInteger();
        }
        if (ret.skill == 400031010) {
            iPacket.DecodeInteger(); // Unknown
            iPacket.DecodeInteger(); // Unknown
        }
        if (ret.skill == 400041019) {
            iPacket.DecodeInteger(); // pRepeatSkill.ptAttackRefPoint.x
            iPacket.DecodeInteger(); // pRepeatSkill.ptAttackRefPoint.y
        }
        iPacket.DecodeByte(); // Unknown (Always 0)
        ret.attackFlag = iPacket.DecodeByte();
        if (eType == RecvPacketOpcode.UserShootAttack) {
            iPacket.DecodeInteger(); // Unknown
            iPacket.DecodeBoolean(); // bNextShootExJablin && CheckApplyExJablin(pSkill, nAction)
        }
        ret.display = iPacket.DecodeShort();
        int nPsdTargetPlus = iPacket.DecodeInteger();
        int nAttackActionType = iPacket.DecodeByte();
        if (Skill.IsEvanForceSkill(ret.skill)) {
            iPacket.DecodeByte(); // Unknown
        }
        if (ret.skill == 23111001 || ret.skill == 80001915 || ret.skill == 36111010) {
            iPacket.DecodeInteger(); // Unknown
            iPacket.DecodeInteger(); // Unknown
            iPacket.DecodeInteger(); // Unknown
        }
        boolean bBySteal = false;
        int nOption = 0;
        if (eType == RecvPacketOpcode.UserShootAttack) {
            bBySteal = iPacket.DecodeBoolean();
        } else {
            nOption = iPacket.DecodeByte();
        }
        int tAttackTime = iPacket.DecodeInteger();
        iPacket.DecodeInteger(); // Unknown

        int nLastSkillID = 0;
        if (eType == RecvPacketOpcode.UserMeleeAttack) {
            nLastSkillID = iPacket.DecodeInteger();
            if (ret.skill > 0 && nLastSkillID > 0) {
                iPacket.DecodeByte(); // Unknown
            }
            if (ret.skill == 5111009) {
                iPacket.DecodeByte(); // Unknown
            }
            if (ret.skill == 25111005) {
                iPacket.DecodeInteger(); // nSpiritCoreEnhance
            }
        }
        if (eType == RecvPacketOpcode.UserShootAttack) {
            iPacket.DecodeInteger(); // Unknown
            nBulletCashItemPos = iPacket.DecodeShort();
            nShootRange = iPacket.DecodeByte();
            /*nBulletItemPos = iPacket.DecodeShort();
             nBulletCashItemPos = iPacket.DecodeShort();
             nShootRange = iPacket.DecodeByte();
             if (!SkillAccessor.IsShootSkillNotConsumingBullet(nSkillID, bBySteal)) {
             nBulletItemID = iPacket.DecodeInt();
             }*/

            // Rect
            iPacket.DecodeShort();
            iPacket.DecodeShort();
            iPacket.DecodeShort();
            iPacket.DecodeShort();

            if (ret.skill == 2211007) { // hackfix atm
                iPacket.DecodeInteger();
            }
            if (ret.skill == 3111013) { // hackfix atm
                iPacket.DecodeLong();
            }
        }
        if (eType == RecvPacketOpcode.UserNonTargetForceAtomAttack) {
            iPacket.DecodeInteger(); // Always 0
        }

        ret.allDamage = new ArrayList<>();
        if (eType == RecvPacketOpcode.UserBodyAttack) {
            for (int i = 0; i < ret.mobCount; i++) {
                int dwMobID = iPacket.DecodeInteger();
                iPacket.DecodeInteger(); // Unknown
                Point ptHit = new Point();
                Point ptPosPrev = new Point();
                ptHit.x = iPacket.DecodeShort();
                ptHit.y = iPacket.DecodeShort();
                ptPosPrev.x = iPacket.DecodeShort();
                ptPosPrev.y = iPacket.DecodeShort();
                iPacket.DecodeShort(); // tDelay
                int nSkeletonResult = iPacket.DecodeByte();
                if (nSkeletonResult == 1) {
                    iPacket.DecodeString();
                    iPacket.DecodeString();
                    iPacket.DecodeInteger();
                    for (int s = 0; s < iPacket.DecodeInteger(); s++) {
                        iPacket.DecodeString();
                    }
                } else if (nSkeletonResult == 2) {
                    iPacket.DecodeString();
                    iPacket.DecodeString();
                    iPacket.DecodeInteger();
                }
                final MapleMonster monster = chr.getMap().getMonsterByOid(dwMobID);
                ret.allDamage.add(new AttackMonster(monster, dwMobID, monster.getId(), dwMobCRC, ptHit, ptPosPrev, null));
            }
        } else {
            for (int i = 0; i < ret.mobCount; i++) {
                int dwMobID = iPacket.DecodeInteger();
                if (ServerConstants.DEVELOPER_DEBUG_MODE) {
                    System.err.println("[Damage Operation] Mob Object (" + dwMobID + ")");
                }
                iPacket.DecodeByte(); // nHitAction
                iPacket.DecodeByte(); // Unknown
                iPacket.DecodeByte(); // Unknown
                int v37 = iPacket.DecodeByte();
                int nForeAction = (v37 & 0x7F);
                int bLeft = (byte) ((v37 >> 7) & 1);
                int nFrameIdx = iPacket.DecodeByte();
                iPacket.DecodeInteger(); // Unknown
                int v38 = iPacket.DecodeByte();
                int nCalcDamageStatIndex = (v38 & 0x7F);
                boolean bDoomed = ((v38 >> 7 & 1) > 0);
                Point ptHit = new Point();
                Point ptPosPrev = new Point();
                ptHit.x = iPacket.DecodeShort();
                ptHit.y = iPacket.DecodeShort();
                ret.position.x = ptHit.x;
                ret.position.y = ptHit.y;
                ptPosPrev.x = iPacket.DecodeShort();
                ptPosPrev.y = iPacket.DecodeShort();
                if (eType == RecvPacketOpcode.UserMagicAttack) {
                    iPacket.DecodeByte(); // HP Percentage Lost
                }
                List<Pair<Long, Boolean>> damageNumbers = new ArrayList<>();
                if (ret.skill == 80001835 || ret.skill == 42111002 || ret.skill == 80011050) {
                    int nAttackCount = iPacket.DecodeByte();
                    if (eType != RecvPacketOpcode.UserNonTargetForceAtomAttack) {
                        for (int j = 0; j < nAttackCount; j++) {
                            long nDamage = iPacket.DecodeLong();
                            damageNumbers.add(new Pair(nDamage, false));
                        }
                    }
                } else {
                    tDelay = iPacket.DecodeShort();
                    if (eType != RecvPacketOpcode.UserNonTargetForceAtomAttack) {
                        for (int j = 0; j < ret.numberOfHits; j++) {
                            long nDamage = iPacket.DecodeLong();
                            damageNumbers.add(new Pair(nDamage, false));
                            if (ServerConstants.DEVELOPER_DEBUG_MODE) {
                                System.err.println("[Damage Operation] Damage Line (" + nDamage + ")");
                            }
                        }
                    }
                }
                if (eType != RecvPacketOpcode.UserNonTargetForceAtomAttack) {
                    iPacket.DecodeInteger(); // pMob.GetMobUpDownYRange
                    iPacket.DecodeInteger(); // pMob.GetCrc
                }
                if (ret.skill == 37111005) {
                    iPacket.DecodeBoolean(); // bRWLiftPress
                }
                int nSkeletonResult = iPacket.DecodeByte();
                if (nSkeletonResult == 1) {
                    iPacket.DecodeString();
                    iPacket.DecodeString();
                    iPacket.DecodeInteger();
                    for (int s = 0; s < iPacket.DecodeInteger(); s++) {
                        iPacket.DecodeString();
                    }
                } else if (nSkeletonResult == 2) {
                    iPacket.DecodeString();
                    iPacket.DecodeString();
                    iPacket.DecodeInteger();
                }
                iPacket.DecodeByte(); // Unknown
                final MapleMonster monster = chr.getMap().getMonsterByOid(dwMobID);
                ret.allDamage.add(new AttackMonster(monster, dwMobID, monster.getId(), dwMobCRC, ptHit, ptPosPrev, damageNumbers));
            }
        }
        // TODO: See if can parse with just this.. the rest is so much and i dont think u use any of the vars
        return ret;
    }
}
