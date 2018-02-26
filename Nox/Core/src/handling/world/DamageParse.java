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
import handling.jobs.Cygnus.ThunderBreakerHandler;
import handling.jobs.Hero.AranHandler;
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

public class DamageParse {

    public static void applyAttack(AttackInfo attack, Skill theSkill, MapleCharacter oPlayer, int attackCount, double maxDamagePerMonster, MapleStatEffect effect, AttackType attackType) {
        if (!oPlayer.isAlive()) {
            oPlayer.getCheatTracker().registerOffense(CheatingOffense.ATTACKING_WHILE_DEAD);
            return;
        } else if (!oPlayer.getMap().getSharedMapResources().noSkillInfo.isSkillUsable(oPlayer.getJob(), attack.skill)) {
            oPlayer.getCheatTracker().registerOffense(CheatingOffense.ATTACKING_IN_UNAVAILABLE_MAP);
            return;
        } else if (attack.skill == 80001593) { // Anti-cheat.
            oPlayer.yellowMessage("[AntiCheat] Please remember hacking and the use of 3rd party modifications go against our ToS.");
            return;
        }
        
        if (attack.real && GameConstants.getAttackDelay(attack.skill, theSkill) >= 400) { // Was 100
            oPlayer.getCheatTracker().checkAttack(attack.skill, attack.lastAttackTickCount);
        }

        if (attack.skill != 0 && effect != null) {
            if (GameConstants.isMulungSkill(attack.skill)) {
                if (oPlayer.getMapId() / 10000 != 92502) {
                    return;
                }
                if (oPlayer.getMulungEnergy() < 10000) {
                    return;
                }
                oPlayer.mulungEnergyModifier(false);
            } else if (GameConstants.isPyramidSkill(attack.skill)) {
                if (oPlayer.getMapId() / 1000000 != 926) {
                    return;
                }
                if (oPlayer.getPyramidSubway() != null && oPlayer.getPyramidSubway().onSkillUse(oPlayer)) {
                    //TODO: Do something here
                }
            } else if (GameConstants.isInflationSkill(attack.skill)) {
                if (oPlayer.getBuffedValue(CharacterTemporaryStat.Inflation) != null) {
                    //TODO: Do something here
                }
            } else if (attack.mobCount > effect.getMobCount() && !GameConstants.isMismatchingBulletSkill(attack.skill)) { //&& attack.skill != Paladin.ADVANCED_CHARGE && attack.skill != 22110025) {
                oPlayer.getCheatTracker().registerOffense(CheatingOffense.MISMATCHING_BULLETCOUNT);
                oPlayer.yellowMessage("[Warning] Mismatching bullet count.");
                return;
            }
        }

        if (ServerConstants.ADMIN_MODE) {
            oPlayer.dropMessage(-1, new StringBuilder().append("Animation: ").append(Integer.toHexString((attack.display & 0x8000) != 0 ? attack.display - 32768 : attack.display)).toString());
        }
        boolean useAttackCount = attack.skill != Marksman.SNIPE && attack.skill != Mercedes.LIGHTNING_EDGE;

        if (attack.numberOfHits > 0 && attack.mobCount > 0) {
            if (!oPlayer.getStat().checkEquipDurabilitys(oPlayer, -1)) {
                oPlayer.dropMessage(5, "An item has run out of durability but has no inventory room to go to.");
                return;
            }
        }
        int totDamage = 0;
        MapleMap map = oPlayer.getMap();
        int totDamageToOneMonster = 0;
        long hpMob = 0L;
        PlayerStats stats = oPlayer.getStat();

        final int playerDamageCap = GameConstants.damageCap + oPlayer.getStat().damageCapIncrease; // The damage cap the player is allowed to hit.
        int CriticalDamage = stats.passive_sharpeye_percent();
        int ShdowPartnerAttackPercentage = 0;
        if (attackType == AttackType.RANGED_WITH_ShadowPartner || attackType == AttackType.NON_RANGED_WITH_MIRROR) {
            MapleStatEffect shadowPartnerEffect = oPlayer.getStatForBuff(CharacterTemporaryStat.ShadowPartner);
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

                if (!Tempest && !oPlayer.isGM()) {
                    if ((oPlayer.getJob() >= MapleJob.BATTLE_MAGE_1.getId() && oPlayer.getJob() <= MapleJob.BATTLE_MAGE_4.getId() && !monster.isBuffed(MonsterStatus.DAMAGE_IMMUNITY)
                            && !monster.isBuffed(MonsterStatus.MAGIC_IMMUNITY)
                            && !monster.isBuffed(MonsterStatus.MAGIC_DAMAGE_REFLECT)) || attack.skill == Marksman.SNIPE
                            || attack.skill == Mercedes.LIGHTNING_EDGE || ((oPlayer.getJob() < MapleJob.BATTLE_MAGE_1.getId() || oPlayer.getJob() > MapleJob.BATTLE_MAGE_4.getId()) && !monster.isBuffed(MonsterStatus.DAMAGE_IMMUNITY)
                            && !monster.isBuffed(MonsterStatus.WEAPON_IMMUNITY) && !monster.isBuffed(MonsterStatus.WEAPON_DAMAGE_REFLECT))) {
                        maxDamagePerHit = calculateMaxWeaponDamagePerHit(oPlayer, monster, attack, theSkill, effect, maxDamagePerMonster, CriticalDamage);
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
                    } else if (!oPlayer.isGM()) {
                        if (Tempest) {
                            if (eachd > monster.getMobMaxHp()) {
                                eachd = (int) Math.min(monster.getMobMaxHp(), Integer.MAX_VALUE);
                                oPlayer.getCheatTracker().registerOffense(CheatingOffense.HIGH_DAMAGE);
                            }
                        } else if (((oPlayer.getJob() >= 3200) && (oPlayer.getJob() <= 3212) && (!monster.isBuffed(MonsterStatus.DAMAGE_IMMUNITY)) && (!monster.isBuffed(MonsterStatus.MAGIC_IMMUNITY)) && (!monster.isBuffed(MonsterStatus.MAGIC_DAMAGE_REFLECT))) || (attack.skill == 23121003) || (((oPlayer.getJob() < 3200) || (oPlayer.getJob() > 3212)) && (!monster.isBuffed(MonsterStatus.DAMAGE_IMMUNITY)) && (!monster.isBuffed(MonsterStatus.WEAPON_IMMUNITY)) && (!monster.isBuffed(MonsterStatus.WEAPON_DAMAGE_REFLECT)))) {
                            if (eachd > maxDamagePerHit) {
                                oPlayer.getCheatTracker().registerOffense(CheatingOffense.HIGH_DAMAGE, new StringBuilder().append("[Damage: ").append(eachd).append(", Expected: ").append(maxDamagePerHit).append(", Mob: ").append(monster.getId()).append("] [Job: ").append(oPlayer.getJob()).append(", Level: ").append(oPlayer.getLevel()).append(", Skill: ").append(attack.skill).append("]").toString());
                                if (attack.real) {
                                    oPlayer.getCheatTracker().checkSameDamage(eachd, maxDamagePerHit);
                                }
                                if (eachd > maxDamagePerHit * 2.0D) {
                                    oPlayer.getCheatTracker().registerOffense(CheatingOffense.HIGH_DAMAGE_2, new StringBuilder().append("[Damage: ").append(eachd).append(", Expected: ").append(maxDamagePerHit).append(", Mob: ").append(monster.getId()).append("] [Job: ").append(oPlayer.getJob()).append(", Level: ").append(oPlayer.getLevel()).append(", Skill: ").append(attack.skill).append("]").toString());
                                    eachd = (int) (maxDamagePerHit * 2.0D);
                                    if (eachd >= playerDamageCap) {
                                        oPlayer.getClient().close();
                                    }
                                }
                            }

                        } else if (eachd > maxDamagePerHit) {
                            eachd = (int) maxDamagePerHit;
                        }

                    }
                    totDamageToOneMonster += eachd;

                    if ((eachd == 0 || monster.getId() == 9700021) && oPlayer.getPyramidSubway() != null) {
                        oPlayer.getPyramidSubway().onMiss(oPlayer);
                    }
                }
                totDamage += totDamageToOneMonster;
                
                // Paragon Level Bonuses
                if (ServerConstants.PARAGON_SYSTEM) {
                    if (oPlayer.getReborns() >= 2) { // Paragon Level 2+
                        totDamageToOneMonster *= 1.05; // +5% Increased Damage
                    }
                    if (oPlayer.getReborns() >= 10) { // Paragon Level 10+
                        totDamageToOneMonster *= 1.05; // +5% Increased Damage
                    }
                    if (oPlayer.getReborns() >= 6) { // Paragon Level 6+
                        oPlayer.addHP((int) (totDamageToOneMonster * 0.01)); // +1% Life Leech
                    }
                    if (oPlayer.getReborns() >= 6) { // Paragon Level 7+
                        oPlayer.addMP((int) (totDamageToOneMonster * 0.01)); // +1% Mana Leech
                    }
                }

                // Apply attack to the monster hit.
                if (oPlayer.isDeveloper()) {
                    oPlayer.yellowMessage("[Debug] Skill ID (" + attack.skill + ") - Damage (" + totDamageToOneMonster + ")");
                }
                monster.damage(oPlayer, totDamageToOneMonster, true, attack.skill);

                // Monster is still alive after being hit.
                if (monster.isAlive()) {
                    oPlayer.checkMonsterAggro(monster);
                } else {
                    attack.after_NumMobsKilled++;
                }

                if (oPlayer.getSkillLevel(36110005) > 0) {
                    Skill skill = SkillFactory.getSkill(36110005);
                    MapleStatEffect eff = skill.getEffect(oPlayer.getSkillLevel(skill));
                    if (oPlayer.getLastComboTime() + 5000 < System.currentTimeMillis()) {
                        monster.setTriangulation(0);
                        //player.clearDamageMeters();
                    }
                    if (eff.makeChanceResult()) {
                        oPlayer.setLastCombo(System.currentTimeMillis());
                        if (monster.getTriangulation() < 3) {
                            monster.setTriangulation(monster.getTriangulation() + 1);
                        }
                        monster.applyStatus(oPlayer, new MonsterStatusEffect(MonsterStatus.DARKNESS, eff.getX(), eff.getSourceId(), null, false), false, eff.getY() * 1000, true, eff);
                        monster.applyStatus(oPlayer, new MonsterStatusEffect(MonsterStatus.TRIANGULATION, monster.getTriangulation(), eff.getSourceId(), null, false), false, eff.getY() * 1000, true, eff);
                    }
                }

                if (oPlayer.getBuffedValue(CharacterTemporaryStat.PickPocket) != null) {
                    handlePickPocket(oPlayer, monster, oned);
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
                            if (oPlayer.getTotalSkillLevel(skill) > 0) {
                                MapleStatEffect venomEffect = skill.getEffect(oPlayer.getTotalSkillLevel(skill));
                                if (!venomEffect.makeChanceResult()) {
                                    break;
                                }
                                monster.applyStatus(oPlayer, new MonsterStatusEffect(MonsterStatus.POISON, 1, i, null, false), true, venomEffect.getDuration(), true, venomEffect);
                                break;
                            }
                        }
                        break;
                    case 4121017:
                        Skill skill = SkillFactory.getSkill(4121017);
                        if (oPlayer.getTotalSkillLevel(skill) > 0) {
                            MapleStatEffect showdown = skill.getEffect(oPlayer.getTotalSkillLevel(skill));
                            monster.applyStatus(oPlayer, new MonsterStatusEffect(MonsterStatus.SHOWDOWN, showdown.getX(), 4121017, null, false), false, showdown.getDuration(), false, showdown);
                        }
                        break;
                    case 4201004:
                        monster.handleSteal(oPlayer);
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
                        if ((oPlayer.getBuffedValue(CharacterTemporaryStat.WeaponCharge) != null) && (!monster.getStats().isBoss())) {
                            MapleStatEffect eff = oPlayer.getStatForBuff(CharacterTemporaryStat.WeaponCharge);
                            if (eff != null) {
                                monster.applyStatus(oPlayer, new MonsterStatusEffect(MonsterStatus.SPEED, eff.getX(), eff.getSourceId(), null, false), false, eff.getY() * 1000, true, eff);
                            }
                        }
                        if ((oPlayer.getBuffedValue(CharacterTemporaryStat.BodyPressure) != null) && (!monster.getStats().isBoss())) {
                            MapleStatEffect eff = oPlayer.getStatForBuff(CharacterTemporaryStat.BodyPressure);

                            if ((eff != null) && (eff.makeChanceResult()) && (!monster.isBuffed(MonsterStatus.NEUTRALISE))) {
                                monster.applyStatus(oPlayer, new MonsterStatusEffect(MonsterStatus.NEUTRALISE, 1, eff.getSourceId(), null, false), false, eff.getX() * 1000, true, eff);
                            }
                        }
                        break;
                }

                // Megido Flame Custom Handling
                if (oPlayer.getSkillLevel(SkillFactory.getSkill(2121055)) > 0 || oPlayer.getSkillLevel(SkillFactory.getSkill(2121052)) > 0/* || player.getJob() == 1211 || player.getJob() == 1212*/) {
                    int percent = 45;
                    int percent2 = 25;
                    for (AttackMonster at : attack.allDamage) {
                        MapleMonster mob = map.getMonsterByOid(at.getObjectId());
                        if (map.getMonsterByOid(at.getObjectId()).getStats().isBoss()) {
                            if (Randomizer.nextInt(100) < percent) {
                                if (mob != null) {
                                    oPlayer.getClient().write(JobPacket.XenonPacket.MegidoFlameRe(oPlayer.getId(), mob.getObjectId()));
                                }
                            }
                        } else {
                            if (Randomizer.nextInt(100) < percent2) {
                                if (mob != null) {
                                    oPlayer.getClient().write(JobPacket.XenonPacket.MegidoFlameRe(oPlayer.getId(), mob.getObjectId()));
                                }
                            }
                        }
                    }
                }

                if ((totDamageToOneMonster > 0) || (attack.skill == 1221011) || (attack.skill == 21120006)) {

                    // Kinesis Psychic Points handling.
                    if (GameConstants.isKinesis(oPlayer.getJob())) {
                       KinesisHandler.handlePsychicPoint(oPlayer, attack.skill);
                    }
                    if (GameConstants.isShade(oPlayer.getJob())) {
                        if (oPlayer.hasBuff(CharacterTemporaryStat.ChangeFoxMan)) {
                            for (AttackMonster at : attack.allDamage) {
                                int nPercent = 70;
                                if (Randomizer.nextInt(100) < nPercent) { 
                                    oPlayer.getMap().broadcastMessage(JobPacket.ShadePacket.FoxSpirit(oPlayer, at));
                                }
                            }
                        }
                    }
                    if (GameConstants.isAran(oPlayer.getJob())) {
                        switch (attack.skill) {
                            case Aran.SMASH_SWING_1:
                            case Aran.SMASH_SWING_2:
                            case Aran.SMASH_SWING_3:
                                AranHandler.handleSwingStudies(oPlayer);
                                break;
                        }
                    }
                    if (GameConstants.isThunderBreakerCygnus(oPlayer.getJob())) {
                        ThunderBreakerHandler.handleLightningBuff(oPlayer);
                    }
                    if (GameConstants.isDemonSlayer(oPlayer.getJob())) {
                        oPlayer.handleForceGain(monster.getObjectId(), attack.skill);
                    }
                    if ((GameConstants.isPhantom(oPlayer.getJob())) && (attack.skill != 24120002) && (attack.skill != 24100003)) {
                        oPlayer.handleCardStack();
                        for (AttackMonster at : attack.allDamage) {
                            if (Randomizer.nextInt(100) < 60) { 
                                oPlayer.getMap().broadcastMessage(JobPacket.PhantomPacket.ThrowCarte(oPlayer.getId(), at.getObjectId()));
                            }
                        }
                    }
                    if (GameConstants.isKaiser(oPlayer.getJob())) {
                        oPlayer.handleKaiserCombo();
                    }
                    if (oPlayer.getStatForBuff(CharacterTemporaryStat.ComboCounter) != null) { // Combo Attack
                        oPlayer.handleComboAttack();
                    }
                    if (GameConstants.isNightWalkerCygnus(oPlayer.getJob())) {

                        if (oPlayer.hasBuff(CharacterTemporaryStat.NightWalkerBat)) {
                            for (AttackMonster at : attack.allDamage) {
                                if (Randomizer.nextInt(100) < 60) { 
                                    oPlayer.getMap().broadcastMessage(JobPacket.NightWalkerPacket.ShadowBats(oPlayer.getId(), at.getObjectId()));
                                }
                            }
                        }

                        //player.handleShadowBat(monster.getObjectId(), attack.skill); // Shadow Bat Spawn Handler
                        oPlayer.handleDarkElemental(); // Dark Elemental Stack Count Handler

                        // Damage Increase Handler for Active Shadow Bats
                        if (oPlayer.getBatCount() > 0) {
                            int attackChance = Randomizer.rand(1, 5); // 1 in 5 Chance (20%)
                            if (attackChance == 1) {
                                totDamageToOneMonster += (totDamageToOneMonster * 1.5) * oPlayer.getBatCount(); // 150% Increased Damage per Bat
                                oPlayer.addHP((int) (totDamageToOneMonster * 0.1));
                                oPlayer.dropMessage(-1, "Damage Boost (Shadow Bat)");
                            }
                        }

                        // Damage Increase Handler for Dark Elemental Mark Stacks
                        for (int i = oPlayer.getDarkElementalCombo(); i > 0; i--) {
                            totDamageToOneMonster += (totDamageToOneMonster * 0.8); // 80% Increase Damage per Stack
                        }
                    }
                    if (monster.isBuffed(MonsterStatus.WEAPON_DAMAGE_REFLECT)) {
                        oPlayer.addHP(-(7000 + Randomizer.nextInt(8000)));
                    }
                    oPlayer.onAttack(monster.getMobMaxHp(), monster.getMobMaxMp(), attack.skill, monster.getObjectId(), totDamage, 0);
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
                                if (oPlayer.getTotalSkillLevel(skill) > 0) {
                                    MapleStatEffect venomEffect = skill.getEffect(oPlayer.getTotalSkillLevel(skill));
                                    if (!venomEffect.makeChanceResult()) {
                                        break;
                                    }
                                    monster.applyStatus(oPlayer, new MonsterStatusEffect(MonsterStatus.POISON, Integer.valueOf(1), i, null, false), true, venomEffect.getDuration(), true, venomEffect);
                                    break;
                                }

                            }

                            break;
                        case 4201004:
                            monster.handleSteal(oPlayer);
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
                            if ((oPlayer.getBuffedValue(CharacterTemporaryStat.WeaponCharge) != null) && (!monster.getStats().isBoss())) {
                                MapleStatEffect eff = oPlayer.getStatForBuff(CharacterTemporaryStat.WeaponCharge);
                                if (eff != null) {
                                    monster.applyStatus(oPlayer, new MonsterStatusEffect(MonsterStatus.SPEED, Integer.valueOf(eff.getX()), eff.getSourceId(), null, false), false, eff.getY() * 1000, true, eff);
                                }
                            }
                            if ((oPlayer.getBuffedValue(CharacterTemporaryStat.BodyPressure) != null) && (!monster.getStats().isBoss())) {
                                MapleStatEffect eff = oPlayer.getStatForBuff(CharacterTemporaryStat.BodyPressure);

                                if ((eff != null) && (eff.makeChanceResult()) && (!monster.isBuffed(MonsterStatus.NEUTRALISE))) {
                                    monster.applyStatus(oPlayer, new MonsterStatusEffect(MonsterStatus.NEUTRALISE, Integer.valueOf(1), eff.getSourceId(), null, false), false, eff.getX() * 1000, true, eff);
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
                        Item weapon_ = oPlayer.getInventory(MapleInventoryType.EQUIPPED).getItem((short) -11);
                        if (weapon_ != null) {
                            MonsterStatus stat = GameConstants.getStatFromWeapon(weapon_.getItemId());
                            if ((stat != null) && (Randomizer.nextInt(100) < GameConstants.getStatChance())) {
                                MonsterStatusEffect monsterStatusEffect = new MonsterStatusEffect(stat, Integer.valueOf(GameConstants.getXForStat(stat)), GameConstants.getSkillForStat(stat), null, false);
                                monster.applyStatus(oPlayer, monsterStatusEffect, false, 10000L, false, null);
                            }
                        }
                        if (oPlayer.getBuffedValue(CharacterTemporaryStat.Blind) != null) {
                            MapleStatEffect eff = oPlayer.getStatForBuff(CharacterTemporaryStat.Blind);

                            if ((eff != null) && (eff.makeChanceResult())) {
                                MonsterStatusEffect monsterStatusEffect = new MonsterStatusEffect(MonsterStatus.ACC, Integer.valueOf(eff.getX()), eff.getSourceId(), null, false);
                                monster.applyStatus(oPlayer, monsterStatusEffect, false, eff.getY() * 1000, true, eff);
                            }
                        }
                        if ((oPlayer.getJob() == 121) || (oPlayer.getJob() == 122)) {
                            Skill skill = SkillFactory.getSkill(1211006);
                            if (oPlayer.isBuffFrom(CharacterTemporaryStat.WeaponCharge, skill)) {
                                MapleStatEffect eff = skill.getEffect(oPlayer.getTotalSkillLevel(skill));
                                MonsterStatusEffect monsterStatusEffect = new MonsterStatusEffect(MonsterStatus.FREEZE, Integer.valueOf(1), skill.getId(), null, false);
                                monster.applyStatus(oPlayer, monsterStatusEffect, false, eff.getY() * 2000, true, eff);
                            }
                        }
                    }
                    if ((effect != null) && (effect.getMonsterStati().size() > 0) && (effect.makeChanceResult())) {
                        for (Map.Entry z : effect.getMonsterStati().entrySet()) {
                            monster.applyStatus(oPlayer, new MonsterStatusEffect((MonsterStatus) z.getKey(), (Integer) z.getValue(), theSkill.getId(), null, false), effect.isPoison(), effect.getDuration(), true, effect);
                        }
                    }
                }
                if (GameConstants.isLuminous(oPlayer.getJob())) {
                    //MagicAttack.handleLuminousState(player, attack); // Causes issue where damage is only dealt to one monster.
                    oPlayer.handleLuminous(attack.skill);
                }

                // AntiCheat
                if (GameConstants.getAttackDelay(attack.skill, theSkill) >= 300 //Originally 100
                        && !GameConstants.isNoDelaySkill(attack.skill) && (attack.skill != 3101005) && (!monster.getStats().isBoss()) && (oPlayer.getTruePosition().distanceSq(monster.getTruePosition()) > GameConstants.getAttackRange(effect, oPlayer.getStat().defRange))) {
                    oPlayer.getCheatTracker().registerOffense(CheatingOffense.ATTACK_FARAWAY_MONSTER, new StringBuilder().append("[Distance: ").append(oPlayer.getTruePosition().distanceSq(monster.getTruePosition())).append(", Expected Distance: ").append(GameConstants.getAttackRange(effect, oPlayer.getStat().defRange)).append(" Job: ").append(oPlayer.getJob()).append("]").toString());
                }
            }
        }

        // Handle multi kills
        handleMultiKillsAndCombo(attack, oPlayer);

        // Handle Other Skills Below
        // Life Sap
        if (GameConstants.isDemonAvenger(oPlayer.getJob())) {
            if (oPlayer.getSkillLevel(31010002) > 0) {
                MapleStatEffect eff = SkillFactory.getSkill(31010002).getEffect(oPlayer.getSkillLevel(31010002));
                if (eff.makeChanceResult()) {
                    if (oPlayer.getExceed() / 2 > ((oPlayer.getSkillLevel(31210006) > 0 ? oPlayer.getSkillLevel(31210006) + 5 : 0) + eff.getX())) {
                        oPlayer.addHP((int) Math.min((totDamageToOneMonster * ((((oPlayer.getSkillLevel(31210006) > 0 ? oPlayer.getSkillLevel(31210006) + 5 : 0) + eff.getX()) - ((int) (oPlayer.getExceed() / 2))) / 100.0D)) * -1, oPlayer.getStat().getCurrentMaxHp() / 2));
                    } else {
                        oPlayer.addHP((int) Math.min((totDamageToOneMonster * ((((oPlayer.getSkillLevel(31210006) > 0 ? oPlayer.getSkillLevel(31210006) + 5 : 0) + eff.getX()) - ((int) (oPlayer.getExceed() / 2))) / 100.0D)), oPlayer.getStat().getCurrentMaxHp() / 2));
                    }
                }
            }
        }

        // Drain
        if (oPlayer.getBuffSource(CharacterTemporaryStat.AranDrain) == Aran.DRAIN) {
            Skill skill = SkillFactory.getSkill(Aran.DRAIN);
            oPlayer.addHP(Math.min(totDamage / 5, (totDamage * skill.getEffect(oPlayer.getSkillLevel(skill)).getX()) / 100));
        }
        if (oPlayer.getSkillLevel(Darkknight.DARK_THIRST) > 0) { // Hack fix for now.
            Skill skill = SkillFactory.getSkill(Darkknight.DARK_THIRST);
            oPlayer.addHP(Math.min(totDamage / 5, (totDamage * skill.getEffect(oPlayer.getSkillLevel(skill)).getX()) / 100));
        }

        // Prime Critical
        if (oPlayer.getJob() == 422) {
            int critical = oPlayer.acaneAim;
            if (attack.skill > 0) {
                map.broadcastMessage(CField.CriticalGrowing(critical));
            }
            if (oPlayer.acaneAim <= 23) {
                oPlayer.acaneAim++;
            }
        }

        if (GameConstants.isLuminous(oPlayer.getJob())) {
            final Integer darkcrescendo_value = oPlayer.getBuffedValue(CharacterTemporaryStat.StackBuff);
            if (darkcrescendo_value != null && darkcrescendo_value != 1) {
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

        // Combo
        if (oPlayer.getJob() >= 420 && oPlayer.getJob() <= 422) {
            MapleStatEffect crescendo = SkillFactory.getSkill(4200013).getEffect(oPlayer.getSkillLevel(4200013));
            if (crescendo != null) {

                if (crescendo.makeChanceResult()) {
                    oPlayer.setLastCombo(System.currentTimeMillis());
                    if (oPlayer.acaneAim <= 30) {
                        oPlayer.acaneAim++;
                        crescendo.applyTo(oPlayer);
                    }
                }
            }
        }
        if ((attack.skill == 4331003) && ((hpMob <= 0L) || (totDamageToOneMonster < hpMob))) {
            return;
        }
        if ((hpMob >= 0L) && (totDamageToOneMonster > 0)) {
            oPlayer.afterAttack(attack.mobCount, attack.numberOfHits, attack.skill);
        }
        if ((attack.skill != 0) && ((attack.mobCount > 0) || ((attack.skill != 4331003) && (attack.skill != 4341002))) && (!GameConstants.isNoDelaySkill(attack.skill))) {
            if (effect != null) {
                boolean applyTo = effect.applyTo(oPlayer, attack.position);
            }
        }
        if ((totDamage > 1) && (GameConstants.getAttackDelay(attack.skill, theSkill) >= 100)) {
            CheatTracker tracker = oPlayer.getCheatTracker();

            tracker.setAttacksWithoutHit(true);
            if (tracker.getAttacksWithoutHit() > 1000) {
                tracker.registerOffense(CheatingOffense.ATTACK_WITHOUT_GETTING_HIT, Integer.toString(tracker.getAttacksWithoutHit()));
            }
        }
        if (oPlayer.getSkillLevel(4100012) > 0) {
            MapleStatEffect eff = SkillFactory.getSkill(4100012).getEffect(oPlayer.getSkillLevel(4100012));
            if (eff.makeChanceResult()) {
                for (Map.Entry z : effect.getMonsterStati().entrySet()) {
                    for (AttackMonster ap : attack.allDamage) {
                        final MapleMonster monster = oPlayer.getMap().getMonsterByOid(ap.getObjectId());
                        monster.applyStatus(oPlayer,
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
                        monster.applyStatus(oPlayer, new MonsterStatusEffect(MonsterStatus.POISON,
                                eff.getX(), eff.getSourceId(), null, false), false, eff.getY() * 1000,
                                true, eff);
                        monster.applyStatus(oPlayer,
                                new MonsterStatusEffect((MonsterStatus) z.getKey(), (Integer) z.getValue(),
                                        theSkill.getId(), null, false),
                                effect.isPoison(), effect.getDuration(), true, effect);
                    }
                }
            }

            int bulletCount = eff.getBulletCount();
            for (AttackMonster ap : attack.allDamage) {
                final MapleMonster source = oPlayer.getMap().getMonsterByOid(ap.getObjectId());

                // source.get
                final MonsterStatusEffect check = source.getBuff(MonsterStatus.POISON);

                // if (check != null && check.getSkill().getId() == 4100011 &&
                // check.getOwnerId() == player.getId()) {
                if (check != null && check.getSkill() == 4100011 && check.getOwnerId() == oPlayer.getId()) { // :3
                    final List<MapleMapObject> objs = oPlayer.getMap().getMapObjectsInRange(oPlayer.getPosition(), 500000,
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
                    oPlayer.getMap().broadcastMessage(CWvsContext.giveMarkOfTheif(oPlayer.getId(), source.getObjectId(),
                            4100012, monsters, oPlayer.getPosition(), monsters.get(0).getPosition(), 2070005));
                }
            }
        }
        if (oPlayer.getJob() == 412) {
            for (AttackMonster ap : attack.allDamage) {
                // final MapleMonster source = player.getMap().getMonsterByOid(ap.getObjectId());

                final List<MapleMapObject> objs = oPlayer.getMap().getMapObjectsInRange(oPlayer.getPosition(), 500000,
                        Arrays.asList(MapleMapObjectType.MONSTER));

                final List<MapleMonster> monsters = new ArrayList<>();

                oPlayer.getMap().broadcastMessage(CWvsContext.giveMarkOfTheif(oPlayer.getId(), ap.getObjectId(),
                        4100012, monsters, oPlayer.getPosition(), ap.getPosition(), 2070005));
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

            player.getClient().write(
                    CWvsContext.messagePacket(new StylishKillMessage(StylishKillMessage.StylishKillMessageType.Combo, combo, mob.get().getObjectId())));
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

    public static final void modifyCriticalAttack(AttackInfo attack, MapleCharacter chr, int type, MapleStatEffect effect) {
        int CriticalRate;
        boolean shadow;
        List damages;
        boolean isCritical = false;
        List damage;
        if ((attack.skill != 4211006) && (attack.skill != 3211003) && (attack.skill != 4111004)) {
            CriticalRate = chr.getStat().passive_sharpeye_rate() + (effect == null ? 0 : effect.getCr());
            boolean bMirror = chr.hasBuff(CharacterTemporaryStat.ShadowPartner) || chr.hasBuff(CharacterTemporaryStat.ShadowServant); 
            shadow = bMirror && ((type == 1) || (type == 2));
            damages = new ArrayList<>();
            damage = new ArrayList<>();

            for (AttackMonster p : attack.allDamage) {
                if (p.getAttacks() != null) {
                    int hit = 0;
                    int mid_att = shadow ? p.getAttacks().size() / 2 : p.getAttacks().size();

                    int toCrit = (attack.skill == 4221001) || (attack.skill == 3221007) || (attack.skill == 23121003) || (attack.skill == 4341005) || (attack.skill == 4331006) || (attack.skill == 21120005) ? mid_att : 0;
                    if (toCrit == 0) {
                        for (Pair eachd : p.getAttacks()) {
                            if ((!(Boolean) eachd.right) && hit < mid_att) {
                                if (((Long) eachd.left > 999999) || (Randomizer.nextInt(100) < CriticalRate)) {
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
                                if (attack.skill == 4221001) {
                                    eachd.right = Boolean.valueOf(hit == 3);
                                } else if ((attack.skill == 3221007) || (attack.skill == 23121003) || (attack.skill == 21120005) || (attack.skill == 4341005) || (attack.skill == 4331006) || (((Long) eachd.left).longValue() > 999999)) {
                                    eachd.right = Boolean.valueOf(true);
                                } else if (hit >= mid_att) {
                                    eachd.right = p.getAttacks().get(hit - mid_att).right;
                                } else {
                                    eachd.right = Boolean.valueOf(damages.contains(eachd.left));
                                }
                                if (eachd.right) {
                                    isCritical = true;
                                }
                            }
                            hit++;
                        }
                        damages.clear();
                    }
                }
                if (isCritical) {
                    if (chr.getJob() == 422 && chr.dualBrid == 0 && chr.acaneAim < 5) {
                        chr.getMap().broadcastMessage(CField.OnOffFlipTheCoin(true));
                        chr.acaneAim++;
                        chr.dualBrid = 1;
                    }
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
        
        if(ServerConstants.DEVELOPER_DEBUG_MODE) System.err.println("[Damage Operation] Skill (" + ret.skill + ")");
        
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
                if(ServerConstants.DEVELOPER_DEBUG_MODE) System.err.println("[Damage Operation] Mob Object (" + dwMobID + ")");
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
                            if(ServerConstants.DEVELOPER_DEBUG_MODE) System.err.println("[Damage Operation] Damage Line (" + nDamage + ")");
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
