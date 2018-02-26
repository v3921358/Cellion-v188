package handling.game;

import client.*;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import client.inventory.ModifyInventory;
import client.inventory.ModifyInventoryOperation;
import constants.GameConstants;
import constants.skills.Outlaw;
import constants.skills.Ranger;
import handling.world.AttackInfo;
import handling.world.AttackMonster;
import handling.world.AttackType;
import handling.world.DamageParse;
import net.InPacket;
import netty.ProcessPacket;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MapleStatEffect;
import server.Randomizer;
import server.events.MapleEvent;
import server.events.MapleEventType;
import server.life.MapleMonster;
import server.maps.objects.MapleCharacter;
import service.ChannelServer;
import tools.packet.CField;
import tools.packet.CWvsContext;
import tools.packet.JobPacket;

import java.util.ArrayList;
import java.util.List;
import service.RecvPacketOpcode;

public final class RangedAttack implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        final MapleCharacter chr = c.getPlayer();
        if (chr == null) {
            return;
        }
        if ((chr.hasBlockedInventory()) || (chr.getMap() == null)) {
            return;
        }
        //AttackInfo attack = DamageParse.parseRangedAttack(iPacket, chr);
        AttackInfo attack = DamageParse.OnAttack(RecvPacketOpcode.UserShootAttack, iPacket, chr);
        if (attack == null) {
            c.write(CWvsContext.enableActions());
            return;
        }
        
        if (chr.isDeveloper()) {
            chr.dropMessage(5, "[RangedAttack Debug] Skill ID : " + attack.skill);
        }
        
        int bulletCount = 1;
        int skillLevel = 0;
        MapleStatEffect effect = null;
        Skill skill = null;
        boolean AOE = attack.skill == 4111004;
        boolean noBullet = (chr.getJob() >= 3500 && chr.getJob() <= 3512)
                || (chr.getJob() >= 510 && chr.getJob() <= 512)
                || GameConstants.isCannoneer(chr.getJob())
                || GameConstants.isXenon(chr.getJob())
                || GameConstants.isJett(chr.getJob())
                || GameConstants.isEvan(chr.getJob())
                || GameConstants.isPhantom(chr.getJob())
                || GameConstants.isMercedes(chr.getJob())
                || GameConstants.isZero(chr.getJob())
                || GameConstants.isBeastTamer(chr.getJob())
                || GameConstants.isLuminous(chr.getJob())
                || attack.skill == Outlaw.BLACKBOOT_BILL;
        
        if (attack.skill != 0 && attack.skill != 1 && attack.skill != 17) {
            skill = SkillFactory.getSkill(GameConstants.getLinkedAttackSkill(attack.skill));
            if ((skill == null) || ((GameConstants.isAngel(attack.skill)) && (chr.getStat().equippedSummon % 10000 != attack.skill % 10000))) {
                c.write(CWvsContext.enableActions());
                return;
            }
            skillLevel = chr.getTotalSkillLevel(skill);
            effect = attack.getAttackEffect(chr, skillLevel <= 0 ? attack.skillLevel : skillLevel, skill);
            if (effect == null) {
                return;
            } else if ((effect.getCooldown(chr) > 0) && ((attack.skill != 35111004 && attack.skill != 35121013) || chr.getBuffSource(CharacterTemporaryStat.Mechanic) != attack.skill)) {
                if (chr.skillisCooling(attack.skill)) {
                    c.write(CWvsContext.enableActions());
                    return;
                }
                chr.addCooldown(attack.skill, System.currentTimeMillis(), effect.getCooldown(chr));
            }

            if (GameConstants.isEventMap(chr.getMapId())) {
                for (MapleEventType t : MapleEventType.values()) {
                    MapleEvent e = ChannelServer.getInstance(chr.getClient().getChannel()).getEvent(t);
                    if ((e.isRunning()) && (!chr.isGM())) {
                        for (int i : e.getType().mapids) {
                            if (chr.getMapId() == i) {
                                chr.dropMessage(5, "You may not use that here.");
                                return;
                            }
                        }
                    }
                }
            }
            if (GameConstants.isAngelicBuster(chr.getJob())) {
                int Recharge = effect.getOnActive();
                if (Recharge > -1) {
                    if (Randomizer.isSuccess(Recharge)) {
                        c.write(JobPacket.AngelicPacket.unlockSkill());
                        c.write(JobPacket.AngelicPacket.showRechargeEffect());
                    } else {
                        c.write(JobPacket.AngelicPacket.lockSkill(attack.skill));
                    }
                } else {
                    c.write(JobPacket.AngelicPacket.lockSkill(attack.skill));
                }
            }

            if (GameConstants.isWindArcher(chr.getJob())) {
                int percent = 0, count = 0, skillid = 0, type = 0;
                if (c.getPlayer().getSkillLevel(SkillFactory.getSkill(13120003)) > 0) {
                    if (Randomizer.nextInt(100) < 85) {
                        skillid = 13120003;
                        type = 1;
                    } else {
                        skillid = 13120010;
                        type = 1;
                    }
                    count = Randomizer.rand(1, 5);
                    percent = 20;
                } else if (c.getPlayer().getSkillLevel(SkillFactory.getSkill(13110022)) > 0) {
                    if (Randomizer.nextInt(100) < 90) {
                        skillid = 13110022;
                        type = 1;
                    } else {
                        skillid = 13110027;
                        type = 1;
                    }
                    count = Randomizer.rand(1, 4);
                    percent = 10;
                } else if (c.getPlayer().getSkillLevel(SkillFactory.getSkill(13100022)) > 0) {
                    if (Randomizer.nextInt(100) < 95) {
                        skillid = 13100022;
                        type = 1;
                    } else {
                        skillid = 13100027;
                        type = 1;
                    }
                    count = Randomizer.rand(1, 3);
                    percent = 5;
                }
                for (AttackMonster at : attack.allDamage) {
                    MapleMonster mob = chr.getMap().getMonsterByOid(at.getObjectId());
                    if (Randomizer.nextInt(100) < percent) {
                        if (mob != null) {
                            c.getPlayer().getMap().broadcastMessage(c.getPlayer(), JobPacket.WindArcherPacket.TrifleWind(c.getPlayer().getId(), skillid, count, mob.getObjectId(), type), false);
                            c.write(JobPacket.WindArcherPacket.TrifleWind(c.getPlayer().getId(), skillid, count, mob.getObjectId(), type));
                        }
                    }
                }
            }
            switch (attack.skill) {
                case 21001009: // Aran Smash Wave
                case 13101005:
                case 21110004: // Ranged but uses attackcount instead
                case 14101006: // Vampure
                case 21120006:
                case 11101004:
                // MIHILE
                case 51001004: //Soul Blade
                case 51111007:
                case 51121008:
                // END MIHILE
                case 1077:
                case 1078:
                case 1079:
                case 11077:
                case 11078:
                case 11079:
                case 15111007:
                case 13111007: //Wind Shot
                case 33101007:
                case 13101020://Fary Spiral
                case 33101002:
                case 33121002:
                case 33121001:
                case 21100004:
                case 21110011:
                case 21100007:
                case 21000004:
                case 5121002:
                case 5921002:
                case 4121003:
                case 4221003:
                //case 5221017:
                case 5721007:
                case 5221016:
                case 5721006:
                case 5211008:
                case 5201001:
                case 5721003:
                case 5711000:
                case 4111013:
                case 5121016:
                case 5121013:
                case 5221013:
                case 5721004:
                case 5721001:
                case 5321001:
                case 14111008:
                case 60011216://Soul Buster
                case 65001100://Star Bubble
                case 61001101: //Flame Surge
                    // case 2321054:
                    AOE = true;
                    bulletCount = effect.getAttackCount();
                    break;

                case 35121005:
                case 35111004:
                case 35121013:
                    AOE = true;
                    bulletCount = 6;
                    break;

                case 5221017: // Eight-Legs Easton
                    // Handle Bullet Consumtion for Corsair's Eight-Legs Easton skill. -Mazen
                    Item pProjectilez = chr.getInventory(MapleInventoryType.USE).getItem((short) attack.slot);
                    if ((pProjectilez == null) || (!MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, pProjectilez.getItemId(), 4, false, true))) {
                        chr.dropMessage(5, "You do not have enough bullets to use this skill.");
                        return;
                    }
                    break;

                default:
                    bulletCount = effect.getBulletCount();
                    break;
            }
            if (noBullet && effect.getBulletCount() < effect.getAttackCount()) {
                bulletCount = effect.getAttackCount();
            }
            if ((noBullet) && (effect.getBulletCount() < effect.getAttackCount())) {
                bulletCount = effect.getAttackCount();
            }
        }
        DamageParse.modifyCriticalAttack(attack, chr, 2, effect);
        Integer ShadowPartner = chr.getBuffedValue(CharacterTemporaryStat.ShadowPartner);
        boolean bMirror = chr.hasBuff(CharacterTemporaryStat.ShadowPartner) || chr.hasBuff(CharacterTemporaryStat.ShadowServant);
        if (bMirror) {
            bulletCount *= 2;
        }
        int projectile = 0;
        int visProjectile = 0;
        if ((!AOE) && (chr.getBuffedValue(CharacterTemporaryStat.SoulArrow) == null) && (!noBullet)) {
            Item ipp = chr.getInventory(MapleInventoryType.USE).getItem((short) attack.slot);
            if (ipp == null) {
                chr.dropMessage(6, "Reaching Point 3");
                return;
            }
            projectile = ipp.getItemId();

            if (attack.csstar > 0) {
                if (chr.getInventory(MapleInventoryType.CASH).getItem((short) attack.csstar) == null) {
                    chr.dropMessage(6, "Reaching Point 4");
                    return;
                }
                visProjectile = chr.getInventory(MapleInventoryType.CASH).getItem((short) attack.csstar).getItemId();
            } else {
                visProjectile = projectile;
            }

            if (chr.getBuffedValue(CharacterTemporaryStat.NoBulletConsume) == null) {
                int bulletConsume = bulletCount;
                if ((effect != null) && (effect.getBulletConsume() != 0)) {
                    bulletConsume = effect.getBulletConsume() * (bMirror ? 2 : 1);
                }
                if ((chr.getJob() == 412) && (bulletConsume > 0) && (ipp.getQuantity() < MapleItemInformationProvider.getInstance().getSlotMax(projectile))) {
                    Skill expert = SkillFactory.getSkill(4120010);
                    if (chr.getTotalSkillLevel(expert) > 0) {
                        MapleStatEffect eff = expert.getEffect(chr.getTotalSkillLevel(expert));
                        if (eff.makeChanceResult()) {
                            ipp.setQuantity((short) (ipp.getQuantity() + 1));

                            List<ModifyInventory> mod = new ArrayList<>();

                            //if (chr.getBuffedValue(CharacterTemporaryStat.NoBulletConsume) == null) {
                            mod.add(new ModifyInventory(ModifyInventoryOperation.UpdateQuantity, ipp));
                            c.write(CWvsContext.inventoryOperation(true, mod));
                            //}

                            bulletConsume = 0;
                        }
                    }
                }
                if ((bulletConsume > 0) && (!MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, projectile, bulletConsume, false, true))) {
                    chr.dropMessage(5, "You do not have enough arrows/bullets/stars.");
                    return;
                }
            }

            /*if (chr.getBuffedValue(CharacterTemporaryStat.NoBulletConsume) == null) {
            int bulletConsume = bulletCount;
            if ((effect != null) && (effect.getBulletConsume() != 0)) {
                bulletConsume = effect.getBulletConsume() * (ShadowPartner != null ? 2 : 1);
            }
            if ((chr.getJob() == 412) && (bulletConsume > 0) && (ipp.getQuantity() < MapleItemInformationProvider.getInstance().getSlotMax(projectile))) {
                Skill expert = SkillFactory.getSkill(4120010);
                if (chr.getTotalSkillLevel(expert) > 0) {
                    MapleStatEffect eff = expert.getEffect(chr.getTotalSkillLevel(expert));
                    if (eff.makeChanceResult()) {
                        ipp.setQuantity((short) (ipp.getQuantity() + 1));
                        c.write(CWvsContext.updateInventorySlot(MapleInventoryType.USE, ipp, false));
                        bulletConsume = 0;
                        c.write(CWvsContext.getInventoryStatus());
                    }
                }
            }
            if ((bulletConsume > 0) && (!MapleInventoryManipulator.removeById(c, MapleInventoryType.USE, projectile, bulletConsume, false, true))) {
                chr.dropMessage(5, "You do not have enough arrows/bullets/stars.");
                return;
            }
        }*/
        } else if ((chr.getJob() >= 3500) && (chr.getJob() <= 3512)) {
            visProjectile = 2333000;
        } else if (GameConstants.isCannoneer(chr.getJob())) {
            visProjectile = 2333001;
        }

        int projectileWatk = 0;
        if (projectile != 0) {
            projectileWatk = MapleItemInformationProvider.getInstance().getWatkForProjectile(projectile);
        }
        PlayerStats statst = chr.getStat();
        double basedamage;
        switch (attack.skill) {
            case 4001344:
            case 4121007:
            case 14001004:
            case 14111005:
                basedamage = Math.max(statst.getCurrentMaxBaseDamage(), statst.getTotalLuk() * 5.0F * (statst.getTotalWatk() + projectileWatk) / 100.0F);
                break;
            case 4111004:
                basedamage = 53000.0D;
                break;
            default:
                basedamage = statst.getCurrentMaxBaseDamage();
                switch (attack.skill) {
                    case 3101005:
                        basedamage *= effect.getX() / 100.0D;
                        break;
                }
        }
        if (effect != null) {
            basedamage *= (effect.getDamage() + statst.getDamageIncrease(attack.skill)) / 100.0D;

            long money = effect.getMoneyCon();
            if (money != 0) {
                if (money > chr.getMeso()) {
                    money = chr.getMeso();
                }
                chr.gainMeso(-money, false);
            }
        }
        chr.checkFollow();
        if (!chr.isHidden()) {
            if (attack.skill == 3211006) {
                chr.getMap().broadcastMessage(chr, CField.strafeAttack(chr.getId(), attack.tbyte, attack.skill, skillLevel, attack.display, attack.speed, visProjectile, attack.allDamage, attack.position, chr.getLevel(), chr.getStat().passive_mastery(), attack.attackFlag, chr.getTotalSkillLevel(3220010)), chr.getTruePosition());
            } else {
                chr.getMap().broadcastMessage(chr, CField.rangedAttack(chr.getId(), attack.tbyte, attack.skill, skillLevel, attack.display, attack.speed, visProjectile, attack.allDamage, attack.position, chr.getLevel(), chr.getStat().passive_mastery(), attack.attackFlag), chr.getTruePosition());
            }
        } else if (attack.skill == 3211006) {
            chr.getMap().broadcastGMMessage(chr, CField.strafeAttack(chr.getId(), attack.tbyte, attack.skill, skillLevel, attack.display, attack.speed, visProjectile, attack.allDamage, attack.position, chr.getLevel(), chr.getStat().passive_mastery(), attack.attackFlag, chr.getTotalSkillLevel(3220010)), false);
        } else {
            chr.getMap().broadcastGMMessage(chr, CField.rangedAttack(chr.getId(), attack.tbyte, attack.skill, skillLevel, attack.display, attack.speed, visProjectile, attack.allDamage, attack.position, chr.getLevel(), chr.getStat().passive_mastery(), attack.attackFlag), false);
        }
        
        DamageParse.applyAttack(attack, skill, chr, bulletCount, basedamage, effect, bMirror ? AttackType.RANGED_WITH_ShadowPartner : AttackType.RANGED);
        attack.cleanupMemory(); // Clean up memory references.
    }

}
