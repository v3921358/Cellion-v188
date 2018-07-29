/*
 * Cellion Development
 */
package server.life.mob;

import enums.MobStat;
import client.Skill;
import client.SkillFactory;
import constants.skills.*;
import handling.world.AttackInfo;
import handling.world.AttackMonster;
import server.StatEffect;
import enums.StatInfo;
import server.life.Mob;
import server.life.MobAttackInfo;
import server.maps.objects.User;
import server.skills.Option;
import tools.Utility;

/**
 * Mob Status Handler
 *
 * @author Mazen Massoud
 */
public class MobStatRequest {

    /**
     * Handle Applying Mob Status
     *
     * @param pPlayer
     * @param pAttack
     * @param pEffect
     */
    public static void apply(User pPlayer, AttackInfo pAttack, StatEffect pEffect) {
        Option pOpt = new Option();

        for (AttackMonster pAttackMob : pAttack.allDamage) {
            Mob pMob = pAttackMob.getMonster();
            MobTemporaryStat pMobStat = pMob.getTemporaryStat();
            MobStat pStat = null;
            boolean bApplyStat = true;
            boolean bApplyBurn = true;

            switch (pAttack.skill) {

                /**
                 * Specific Skills
                 */
                case Zero.CRITICAL_BIND: {
                    pOpt.nOption = 1;
                    pOpt.rOption = pAttack.skill;
                    pOpt.tOption = 4;
                    pStat = MobStat.Freeze;
                    applyStat(pMobStat, pStat, pOpt);

                    pOpt.nOption = 1;
                    pOpt.rOption = pAttack.skill;
                    pOpt.tOption = 4;
                    pStat = MobStat.Stun;
                    applyStat(pMobStat, pStat, pOpt);

                    pOpt.nOption = pEffect.info.get(StatInfo.x);
                    pOpt.rOption = pAttack.skill;
                    pOpt.tOption = 4;
                    pStat = MobStat.AddDamParty;
                    break;
                }
                case Zero.ARMOR_SPLIT: {
                    int nAmount = 0;
                    if (pMobStat.hasCurrentMobStat(MobStat.MultiPMDR)) {
                        nAmount = pMobStat.getCurrentOptionsByMobStat(MobStat.MultiPMDR).cOption;
                        if (nAmount < pEffect.info.get(StatInfo.x)) {
                            nAmount++;
                        }
                    }
                    pOpt.nOption = pEffect.info.get(StatInfo.y) * nAmount;
                    pOpt.rOption = pAttack.skill;
                    pOpt.tOption = pEffect.info.get(StatInfo.time) / 1000;
                    pOpt.cOption = nAmount;
                    pStat = MobStat.MultiPMDR;
                    break;
                }
                case FirePoisonArchMage.FLAME_HAZE: {
                    pOpt.nOption = 1;
                    pOpt.rOption = pAttack.skill;
                    pOpt.tOption = pEffect.info.get(StatInfo.time) / 1000;
                    pStat = MobStat.Invincible;
                    applyStat(pMobStat, pStat, pOpt);

                    pOpt.nOption = 1;
                    pOpt.rOption = pAttack.skill;
                    pOpt.tOption = pEffect.info.get(StatInfo.time) / 1000;
                    pStat = MobStat.Speed;
                    break;
                }
                case NightLord.SHOWDOWN: {
                    pOpt.nOption = 1;
                    pOpt.rOption = pAttack.skill;
                    pOpt.tOption = pEffect.info.get(StatInfo.time) / 1000;
                    pStat = MobStat.Showdown;
                    break;
                }
                case AngelicBuster.FINALE_RIBBON: {
                    if (Utility.resultSuccess(pEffect.info.get(StatInfo.prop) > 0 ? pEffect.info.get(StatInfo.prop) : 10)) {
                        pOpt.nOption = 1;
                        pOpt.rOption = pAttack.skill;
                        pOpt.tOption = pEffect.info.get(StatInfo.time) / 1000;
                        pStat = MobStat.AddDamParty;
                    } else {
                        bApplyStat = false;
                    }
                    break;
                }
                case AngelicBuster.LOVELY_STING:
                case AngelicBuster.LOVELY_STING_1:
                case AngelicBuster.LOVELY_STING_2: {
                    if (Utility.resultSuccess(pEffect.info.get(StatInfo.prop) > 0 ? pEffect.info.get(StatInfo.prop) : 10)) {
                        pOpt.nOption = 1;
                        pOpt.rOption = pAttack.skill;
                        pOpt.tOption = pEffect.info.get(StatInfo.time) / 1000;
                        pStat = MobStat.Explosion;
                    } else {
                        bApplyStat = false;
                    }
                    break;
                }
                case Aran.JUDGMENT_DRAW:
                case Aran.JUDGMENT_DRAW_1:
                case Aran.JUDGMENT_DRAW_2:
                case Aran.JUDGMENT_DRAW_3: {
                    if (Utility.resultSuccess(5)) {
                        pOpt.nOption = 1;
                        pOpt.rOption = Aran.JUDGMENT_DRAW;
                        pOpt.tOption = 2;
                        pStat = MobStat.Freeze;
                    } else {
                        bApplyStat = false;
                    }
                    break;
                }
                case Aran.SNOW_CHARGE:
                case Aran.SNOW_CHARGE_1: {
                    pOpt.nOption = - 1;
                    pOpt.rOption = pAttack.skill;
                    pOpt.tOption = pEffect.info.get(StatInfo.y);
                    pOpt.mOption = 1;
                    pStat = MobStat.Speed;
                    break;
                }
                case DawnWarrior.IMPALING_RAYS: {
                    if (Utility.resultSuccess(pEffect.info.get(StatInfo.prop) > 0 ? pEffect.info.get(StatInfo.prop) : 10)) {
                        pOpt.nOption = 1;
                        pOpt.rOption = pAttack.skill;
                        pOpt.tOption = pEffect.info.get(StatInfo.time) / 1000;
                        pStat = MobStat.Stun;
                        applyStat(pMobStat, pStat, pOpt);

                        pOpt.nOption = 1;
                        pOpt.rOption = pAttack.skill;
                        pOpt.tOption = pEffect.info.get(StatInfo.time) / 1000;
                        pStat = MobStat.SeperateSoulC;
                    } else {
                        bApplyStat = false;
                    }
                    break;
                }
                case DemonSlayer.DEMON_CRY: {
                    pOpt.nOption = -pEffect.info.get(StatInfo.y);
                    pOpt.rOption = pAttack.skill;
                    pOpt.tOption = pEffect.info.get(StatInfo.time) / 1000;
                    applyStat(pMobStat, MobStat.PAD, pOpt);
                    applyStat(pMobStat, MobStat.PDR, pOpt);
                    applyStat(pMobStat, MobStat.MAD, pOpt);
                    applyStat(pMobStat, MobStat.MDR, pOpt);

                    pOpt.nOption = -pEffect.info.get(StatInfo.z);
                    pOpt.rOption = pAttack.skill;
                    pOpt.tOption = pEffect.info.get(StatInfo.time) / 1000;
                    pStat = MobStat.ACC;
                    break;
                }
                case DemonAvenger.NETHER_SLICE: {
                    pOpt.nOption = pEffect.info.get(StatInfo.x);
                    pOpt.rOption = pAttack.skill;
                    pOpt.tOption = 30;
                    pMobStat.addStatOptionsAndBroadcast(MobStat.PDR, pOpt);
                    pStat = MobStat.MDR;
                    break;
                }
                case Hayato.SUDDEN_STRIKE: {
                    pOpt.nOption = pEffect.info.get(StatInfo.u);
                    pOpt.rOption = pAttack.skill;
                    pOpt.tOption = pEffect.info.get(StatInfo.time) / 1000;
                    pStat = MobStat.AddDamParty;
                    break;
                }
                case Mercedes.UNICORN_SPIKE: {
                    if (Utility.resultSuccess(pEffect.info.get(StatInfo.prop) > 0 ? pEffect.info.get(StatInfo.prop) : 10)) {
                        pOpt.nOption = pEffect.info.get(StatInfo.x);
                        pOpt.rOption = pAttack.skill;
                        pOpt.tOption = pEffect.info.get(StatInfo.time) / 1000;
                        pStat = MobStat.AddDamSkill;
                    } else {
                        bApplyStat = false;
                    }
                    break;
                }
                case Mercedes.SPIKES_ROYALE_1: {
                    if (Utility.resultSuccess(pEffect.info.get(StatInfo.prop) > 0 ? pEffect.info.get(StatInfo.prop) : 10)) {
                        pOpt.nOption = pEffect.info.get(StatInfo.x);
                        pOpt.rOption = pAttack.skill;
                        pOpt.tOption = pEffect.info.get(StatInfo.time) / 1000;
                        pStat = MobStat.Weakness;
                    } else {
                        bApplyStat = false;
                    }
                    break;
                }
                case Mihile.FOURPOINT_ASSAULT:
                case Mihile.RADIANT_CROSS: {
                    if (Utility.resultSuccess(pEffect.info.get(StatInfo.prop) > 0 ? pEffect.info.get(StatInfo.prop) : 10)) {
                        pOpt.nOption = pEffect.info.get(StatInfo.x);
                        pOpt.rOption = pAttack.skill;
                        pOpt.tOption = pEffect.info.get(StatInfo.time) / 1000;
                        pStat = MobStat.ACC;
                    } else {
                        bApplyStat = false;
                    }
                    break;
                }
                case Shade.DEATH_MARK: {
                    int nHeal = pEffect.info.get(StatInfo.x);
                    pPlayer.addHP(nHeal);
                    pOpt.nOption = 1;
                    pOpt.rOption = pAttack.skill;
                    pOpt.tOption = pEffect.info.get(StatInfo.time) / 1000;
                    pStat = MobStat.DebuffHealing;
                    break;
                }
                case Paladin.DIVINE_CHARGE: {
                    if (Utility.resultSuccess(pEffect.info.get(StatInfo.prop) > 0 ? pEffect.info.get(StatInfo.prop) : 10)) {
                        pOpt.nOption = 1;
                        pOpt.rOption = pAttack.skill;
                        pOpt.tOption = pEffect.info.get(StatInfo.time) / 1000;
                        pStat = MobStat.Seal;
                    } else {
                        bApplyStat = false;
                    }
                    break;
                }
                case Crusader.SHOUT: {
                    if (pMob.getStats().isBoss()) {
                        pOpt.nOption = pEffect.info.get(StatInfo.x);
                        pOpt.rOption = pAttack.skill;
                        pOpt.tOption = pEffect.info.get(StatInfo.time) / 1000;
                        pStat = MobStat.Weakness;
                    } else {
                        pOpt.nOption = 1;
                        pOpt.rOption = pAttack.skill;
                        pOpt.tOption = pEffect.info.get(StatInfo.time) / 1000;
                        pStat = MobStat.Stun;
                    }
                    break;
                }
                case Hero.PUNCTURE: {
                    pOpt.nOption = pEffect.info.get(StatInfo.y);
                    pOpt.rOption = pAttack.skill;
                    pOpt.tOption = pEffect.info.get(StatInfo.time) / 1000;
                    pStat = MobStat.AddDamParty;
                    break;
                }
                case Paladin.SMITE_SHIELD: {
                    pOpt.nOption = 1;
                    pOpt.rOption = pAttack.skill;
                    pOpt.tOption = pEffect.info.get(StatInfo.time) / 1000;
                    pStat = MobStat.Smite;
                    break;
                }
                case WildHunter.DASH_N_SLASH_1: {//(33101115)  //Stun + Bite Debuff
                    if (Utility.resultSuccess(pEffect.info.get(StatInfo.prop) > 0 ? pEffect.info.get(StatInfo.prop) : 10)) {
                        int nAmount = 0;
                        if (pMobStat.hasCurrentMobStat(MobStat.JaguarBleeding)) {
                            nAmount = pMobStat.getCurrentOptionsByMobStat(MobStat.JaguarBleeding).nOption;
                        }
                        nAmount = nAmount + 1 > 3 ? 3 : nAmount + 1;
                        pOpt.nOption = nAmount;
                        pOpt.rOption = pAttack.skill;
                        pOpt.tOption = 5;
                        pStat = MobStat.JaguarBleeding;
                        applyStat(pMobStat, pStat, pOpt);

                        pOpt.nOption = 1;
                        pOpt.rOption = pAttack.skill;
                        pOpt.tOption = pEffect.info.get(StatInfo.time) / 1000;
                        pStat = MobStat.Stun;
                    } else {
                        pOpt.nOption = 1;
                        pOpt.rOption = pAttack.skill;
                        pOpt.tOption = pEffect.info.get(StatInfo.time) / 1000;
                        pStat = MobStat.Stun;
                    }
                    break;
                }
                case WildHunter.DASH_N_SLASH_2: { //(33101215)   //Stun Debuff
                    if (Utility.resultSuccess(pEffect.info.get(StatInfo.prop) > 0 ? pEffect.info.get(StatInfo.prop) : 10)) {
                        pOpt.nOption = 1;
                        pOpt.rOption = pAttack.skill;
                        pOpt.tOption = pEffect.info.get(StatInfo.time) / 1000;
                        pStat = MobStat.Stun;
                    } else {
                        bApplyStat = false;
                    }
                    break;
                }
                case WildHunter.SWIPE: {
                    if (Utility.resultSuccess(pEffect.info.get(StatInfo.prop) > 0 ? pEffect.info.get(StatInfo.prop) : 10)) {
                        int nAmount = 0;
                        if (pMobStat.hasCurrentMobStat(MobStat.JaguarBleeding)) {
                            nAmount = pMobStat.getCurrentOptionsByMobStat(MobStat.JaguarBleeding).nOption;
                        }
                        nAmount = nAmount + 1 > 3 ? 3 : nAmount + 1;
                        pOpt.nOption = nAmount;
                        pOpt.rOption = WildHunter.ANOTHER_BITE;
                        pOpt.tOption = 5;
                        pStat = MobStat.JaguarBleeding;
                    } else {
                        bApplyStat = false;
                    }
                    break;
                }
                case WildHunter.JAGUAR_SOUL: {
                    if (Utility.resultSuccess(pEffect.info.get(StatInfo.prop) > 0 ? pEffect.info.get(StatInfo.prop) : 10)) {
                        int nAmount = 0;
                        if (pMobStat.hasCurrentMobStat(MobStat.JaguarBleeding)) {
                            nAmount = pMobStat.getCurrentOptionsByMobStat(MobStat.JaguarBleeding).nOption;
                        }
                        nAmount = nAmount + 1 > 3 ? 3 : nAmount + 1;
                        pOpt.nOption = nAmount;
                        pOpt.rOption = WildHunter.ANOTHER_BITE;
                        pOpt.tOption = 5;
                        pStat = MobStat.JaguarBleeding;
                        applyStat(pMobStat, pStat, pOpt);

                        pOpt.nOption = 1;
                        pOpt.rOption = pAttack.skill;
                        pOpt.tOption = pEffect.info.get(StatInfo.time) / 1000;
                        pStat = MobStat.Stun;
                    } else {
                        bApplyStat = false;
                    }
                    break;
                }
                case Xenon.ENTANGLING_LASH: {
                    pOpt.nOption = 1;
                    pOpt.rOption = pAttack.skill;
                    pOpt.tOption = pEffect.info.get(StatInfo.time) / 1000;
                    applyStat(pMobStat, MobStat.Stun, pOpt);
                    pStat = MobStat.MagicCrash;
                    break;
                }
                case Xenon.ORBITAL_CATACLYSM: {
                    if (Utility.resultSuccess(pEffect.info.get(StatInfo.prop) > 0 ? pEffect.info.get(StatInfo.prop) : 10)) {
                        pOpt.nOption = pEffect.info.get(StatInfo.x);
                        pOpt.rOption = pAttack.skill;
                        pOpt.tOption = pEffect.info.get(StatInfo.time) / 1000;
                        pStat = MobStat.PDR;
                    } else {
                        bApplyStat = false;
                    }
                    break;
                }
                case NightWalker.DOMINION: {
                    pOpt.nOption = 1;
                    pOpt.rOption = pAttack.skill;
                    pOpt.tOption = pEffect.info.get(StatInfo.time) / 1000;
                    pStat = MobStat.Darkness;
                    break;
                }
                case NightWalker.SHADOW_STITCH: {
                    pOpt.nOption = 1;
                    pOpt.rOption = pAttack.skill;
                    pOpt.tOption = pEffect.info.get(StatInfo.time) / 1000;
                    pStat = MobStat.Stun;
                    break;
                }

                /**
                 * General Speed Modification
                 */
                case WhiteKnight.BLIZZARD_CHARGE:
                case Shade.GROUND_POUND:
                case Shade.GROUND_POUND_1:
                case Kaiser.GIGAS_WAVE:
                case Kaiser.GIGAS_WAVE_1:
                case Kaiser.GIGAS_WAVE_2:
                case Kaiser.WING_BEAT:
                case Kaiser.WING_BEAT_1:
                case Cannoneer.BLAST_BACK:
                case Bowmaster.BINDING_SHOT:
                case Crossbowman.NET_TOSS: {
                    if (Utility.resultSuccess(pEffect.info.get(StatInfo.prop) > 0 ? pEffect.info.get(StatInfo.prop) : 10)) {
                        if (pMob.getStats().isBoss()) {
                            pOpt.nOption = pEffect.info.get(StatInfo.x);
                            pOpt.tOption = (pEffect.info.get(StatInfo.time) / 1000) / 2;
                        } else {
                            pOpt.nOption = pEffect.info.get(StatInfo.y);
                            pOpt.tOption = pEffect.info.get(StatInfo.time) / 1000;
                        }
                        pOpt.rOption = pAttack.skill;
                        pStat = MobStat.Speed;
                    } else {
                        bApplyStat = false;
                    }
                    break;
                }

                /**
                 * General Freeze
                 */
                case Sniper.FREEZER:
                case IceLightningWizard.COLD_BEAM:
                case IceLightningMage.ICE_STRIKE:
                case IceLightningMage.GLACIER_CHAIN:
                case Mercedes.ELEMENTAL_KNIGHTS_2: {
                    pOpt.nOption = 5;
                    pOpt.rOption = pAttack.skill;
                    pOpt.tOption = pEffect.info.get(StatInfo.time) / 1000;
                    pStat = MobStat.Freeze;
                    break;
                }

                /**
                 * General Stun
                 */
                case Kanna.ETHER_PULSE_2:
                case Spearman.SPEAR_SWEEP:
                case Page.CLOSE_COMBAT:
                case ThunderBreaker.LIGHTNING_CHARGE:
                case Fighter.COMBO_FURY:
                case Shade.BOMB_PUNCH_100_10_1:
                case Mercedes.STUNNING_STRIKES:
                case Mercedes.STAGGERING_STRIKES:
                case Luminous.ARMAGEDDON:
                case Kanna.BINDING_TEMPEST:
                case Kanna.VERITABLE_PANDEMONIUM:
                case Kaiser.PRESSURE_CHAIN:
                case Kaiser.PIERCING_BLAZE:
                case Kaiser.PIERCING_BLAZE_1:
                case Hayato.TORNADO_BLADE:
                case DemonSlayer.CARRION_BREATH_1:
                case DemonSlayer.BINDING_DARKNESS:
                case DemonSlayer.CHAOS_LOCK:
                case DemonSlayer.VORTEX_OF_DOOM:
                case DemonAvenger.BLOOD_PRISON:
                case DemonAvenger.SHIELD_CHARGE:
                case BattleMage.DARK_CHAIN:
                case BattleMage.DARK_GENESIS:
                case Aran.FINAL_BLOW:
                case Aran.FINAL_BLOW_1:
                case Aran.FINAL_BLOW_2:
                case Aran.FINAL_BLOW_3:
                case Aran.FINAL_BLOW_4:
                case Aran.FINAL_BLOW_5:
                case Aran.FINAL_BLOW_6:
                case Aran.ROLLING_SPIN:
                case Aran.ROLLING_SPIN_1:
                case Aran.ROLLING_SPIN_2:
                case Aran.ROLLING_SPIN_3:
                case Aran.FINAL_CHARGE_1:
                case AngelicBuster.CELESTIAL_ROAR:
                case Shadower.BOOMERANG_STAB:
                case DualBlade.CHAINS_OF_HELL:
                case DualBlade.FLYING_ASSAULTER:
                case NightWalker.SHADOW_WEB:
                case CannonBlaster.MONKEY_WAVE:
                case Jett.STARLINE_TWO:
                case Kinesis.MENTAL_SHOCK:
                case Marksman.ARROW_ILLUSION:
                case Hunter.ARROW_BOMB:
                case Ranger.PHOENIX:
                case Zero.AIR_RIOT:
                case Zero.AIR_RIOT_1:
                case FirePoisonArchMage.PARALYZE:
                case FirePoisonMage.TELEPORT_MASTERY_3:
                case IceLightningArchMage.CHAIN_LIGHTNING:
                case Priest.SHINING_RAY: {
                    if (Utility.resultSuccess(pEffect.info.get(StatInfo.prop) > 0 ? pEffect.info.get(StatInfo.prop) : 10)) {
                        pOpt.nOption = 1;
                        pOpt.rOption = pAttack.skill;
                        pOpt.tOption = pEffect.info.get(StatInfo.time) / 1000;
                        pStat = MobStat.Stun;
                    } else {
                        bApplyStat = false;
                    }
                    break;
                }

                default: {
                    bApplyStat = false;
                    break;
                }
            }
            if (bApplyStat) {
                applyStat(pMobStat, pStat, pOpt);
            }

            /**
             * Burn Status Handler
             */
            switch (pAttack.skill) {
                case WindArcher.MONSOON:
                case FirePoisonArchMage.IFRIT:
                case FirePoisonArchMage.PARALYZE:
                case FirePoisonWizard.POISON_BREATH:
                case FirePoisonMage.TELEPORT_MASTERY_3:
                case DemonSlayer.BINDING_DARKNESS:
                case Hero.PUNCTURE: {
                    applyBurn(pMobStat, pEffect, pAttack.skill);
                    //pMobStat.createAndAddBurnedInfo(pPlayer.getId(), SkillFactory.getSkill(pAttack.skill), 1);
                    break;
                }
                case Aran.JUDGMENT_DRAW:
                case Aran.JUDGMENT_DRAW_1:
                case Aran.JUDGMENT_DRAW_2:
                case Aran.JUDGMENT_DRAW_3:
                case NightWalker.SHADOW_WEB:
                case ThunderBreaker.LIGHTNING_CHARGE: {
                    if (!bApplyStat) {
                        applyBurn(pMobStat, pEffect, pAttack.skill);
                    }
                    break;
                }
            }
        }
    }

    /**
     * Apply Mob Status
     *
     * @param pMobStat
     * @param pStat
     * @param pOpt
     */
    public static void applyStat(MobTemporaryStat pMobStat, MobStat pStat, Option pOpt) {
        pMobStat.addStatOptionsAndBroadcast(pStat, pOpt);
    }

    /**
     * Burn Simulator
     *
     * @param pMobStat
     * @param pEffect
     * @param nSkillID
     */
    public static void applyBurn(MobTemporaryStat pMobStat, StatEffect pEffect, int nSkillID) {
        Skill pSkill = SkillFactory.getSkill(nSkillID);
        Option pOpt = new Option();

        pOpt.nOption = pEffect.info.get(StatInfo.damage);
        pOpt.rOption = nSkillID;
        pOpt.tOption = pEffect.info.get(StatInfo.time) / 1000;
        pMobStat.addStatOptionsAndBroadcast(MobStat.Poison, pOpt); // Mask Poison as Burn for now.
    }
}
