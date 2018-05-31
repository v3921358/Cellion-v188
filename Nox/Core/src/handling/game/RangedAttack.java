package handling.game;

import client.*;
import client.inventory.Item;
import enums.InventoryType;
import client.inventory.ModifyInventory;
import enums.ModifyInventoryOperation;
import client.jobs.Cygnus;
import client.jobs.Cygnus.WindArcherHandler;
import client.jobs.Nova;
import constants.GameConstants;
import constants.skills.Outlaw;
import constants.skills.Ranger;
import constants.skills.Xenon;
import handling.world.AttackInfo;
import handling.world.AttackMonster;
import handling.world.AttackType;
import handling.world.DamageParse;
import net.InPacket;
import net.ProcessPacket;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.StatEffect;
import server.Randomizer;
import server.events.MapleEvent;
import server.events.MapleEventType;
import server.life.Mob;
import server.maps.objects.User;
import service.ChannelServer;
import tools.packet.CField;
import tools.packet.WvsContext;
import tools.packet.JobPacket;

import java.util.ArrayList;
import java.util.List;
import service.RecvPacketOpcode;

public final class RangedAttack implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        final User pPlayer = c.getPlayer();
        if (pPlayer == null || pPlayer.hasBlockedInventory() || pPlayer.getMap() == null) {
            return;
        }

        AttackInfo pAttack = DamageParse.OnAttack(RecvPacketOpcode.UserShootAttack, iPacket, pPlayer);
        if (pAttack == null) {
            c.SendPacket(WvsContext.enableActions());
            return;
        }

        if (pPlayer.isDeveloper()) pPlayer.dropMessage(5, "[RangedAttack Debug] Skill ID : " + pAttack.skill);

        int nBulletCount = 1;
        int nSLV = 0;
        StatEffect pEffect = null;
        Skill pSkill = null;
        boolean bAOE = pAttack.skill == 4111004;
        boolean bNoBullet = (pPlayer.getJob() >= 3500 && pPlayer.getJob() <= 3512)
                || (pPlayer.getJob() >= 510 && pPlayer.getJob() <= 512)
                || GameConstants.isCannoneer(pPlayer.getJob())
                || GameConstants.isXenon(pPlayer.getJob())
                || GameConstants.isJett(pPlayer.getJob())
                || GameConstants.isEvan(pPlayer.getJob())
                || GameConstants.isPhantom(pPlayer.getJob())
                || GameConstants.isMercedes(pPlayer.getJob())
                || GameConstants.isZero(pPlayer.getJob())
                || GameConstants.isBeastTamer(pPlayer.getJob())
                || GameConstants.isLuminous(pPlayer.getJob())
                || pAttack.skill == Outlaw.BLACKBOOT_BILL;

        if (pAttack.skill != 0 && pAttack.skill != 1 && pAttack.skill != 17) {
            pSkill = SkillFactory.getSkill(GameConstants.getLinkedAttackSkill(pAttack.skill));
            if ((pSkill == null) || ((GameConstants.isAngel(pAttack.skill)) && (pPlayer.getStat().equippedSummon % 10000 != pAttack.skill % 10000))) {
                c.SendPacket(WvsContext.enableActions());
                return;
            }
            nSLV = pPlayer.getTotalSkillLevel(pSkill);
            pEffect = pAttack.getAttackEffect(pPlayer, nSLV <= 0 ? pAttack.skillLevel : nSLV, pSkill);
            if (pEffect == null) {
                return;
            } else if ((pEffect.getCooldown(pPlayer) > 0) && ((pAttack.skill != 35111004 && pAttack.skill != 35121013) || pPlayer.getBuffSource(CharacterTemporaryStat.Mechanic) != pAttack.skill)) {
                if (pPlayer.skillisCooling(pAttack.skill)) {
                    c.SendPacket(WvsContext.enableActions());
                    return;
                }
                pPlayer.addCooldown(pAttack.skill, System.currentTimeMillis(), pEffect.getCooldown(pPlayer));
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
            if (GameConstants.isAngelicBuster(pPlayer.getJob())) {
                Nova.AngelicBusterHandler.handleRecharge(pPlayer, pAttack.skill);
            }

            if (GameConstants.isWindArcher(pPlayer.getJob())) {
                WindArcherHandler.OnTriflingWind(pPlayer, pAttack);
            }
            
            switch (pAttack.skill) {
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
                case 61001101: { //Flame Surge
                    // case 2321054:
                    bAOE = true;
                    nBulletCount = pEffect.getAttackCount();
                    break;
                }
                case 35121005:
                case 35111004:
                case 35121013: {
                    bAOE = true;
                    nBulletCount = 6;
                    break;
                }
                case 5221017: {// Eight-Legs Easton
                    // Handle Bullet Consumtion for Corsair's Eight-Legs Easton skill. -Mazen
                    Item pProjectilez = pPlayer.getInventory(InventoryType.USE).getItem((short) pAttack.slot);
                    if ((pProjectilez == null) || (!MapleInventoryManipulator.removeById(c, InventoryType.USE, pProjectilez.getItemId(), 4, false, true))) {
                        pPlayer.dropMessage(5, "You do not have enough bullets to use this skill.");
                        return;
                    }
                    break;
                }
                default:
                    nBulletCount = pEffect.getBulletCount();
                    break;
            }
            if (bNoBullet && pEffect.getBulletCount() < pEffect.getAttackCount()) {
                nBulletCount = pEffect.getAttackCount();
            }
            if ((bNoBullet) && (pEffect.getBulletCount() < pEffect.getAttackCount())) {
                nBulletCount = pEffect.getAttackCount();
            }
        }
        DamageParse.OnCriticalAttack(pAttack, pPlayer, 2, pEffect);
        Integer ShadowPartner = pPlayer.getBuffedValue(CharacterTemporaryStat.ShadowPartner);
        boolean bMirror = pPlayer.hasBuff(CharacterTemporaryStat.ShadowPartner) || pPlayer.hasBuff(CharacterTemporaryStat.ShadowServant);
        if (bMirror) {
            nBulletCount *= 2;
        }
        int projectile = 0;
        int visProjectile = 0;
        if ((!bAOE) && (pPlayer.getBuffedValue(CharacterTemporaryStat.SoulArrow) == null) && (!bNoBullet)) {
            Item ipp = pPlayer.getInventory(InventoryType.USE).getItem((short) pAttack.slot);
            if (ipp == null) {
                pPlayer.dropMessage(6, "Reaching Point 3");
                return;
            }
            projectile = ipp.getItemId();

            if (pAttack.csstar > 0) {
                if (pPlayer.getInventory(InventoryType.CASH).getItem((short) pAttack.csstar) == null) {
                    pPlayer.dropMessage(6, "Reaching Point 4");
                    return;
                }
                visProjectile = pPlayer.getInventory(InventoryType.CASH).getItem((short) pAttack.csstar).getItemId();
            } else {
                visProjectile = projectile;
            }

            if (pPlayer.getBuffedValue(CharacterTemporaryStat.NoBulletConsume) == null) {
                int bulletConsume = nBulletCount;
                if ((pEffect != null) && (pEffect.getBulletConsume() != 0)) {
                    bulletConsume = pEffect.getBulletConsume() * (bMirror ? 2 : 1);
                }
                if ((pPlayer.getJob() == 412) && (bulletConsume > 0) && (ipp.getQuantity() < MapleItemInformationProvider.getInstance().getSlotMax(projectile))) {
                    Skill expert = SkillFactory.getSkill(4120010);
                    if (pPlayer.getTotalSkillLevel(expert) > 0) {
                        StatEffect eff = expert.getEffect(pPlayer.getTotalSkillLevel(expert));
                        if (eff.makeChanceResult()) {
                            ipp.setQuantity((short) (ipp.getQuantity() + 1));

                            List<ModifyInventory> mod = new ArrayList<>();

                            //if (chr.getBuffedValue(CharacterTemporaryStat.NoBulletConsume) == null) {
                            mod.add(new ModifyInventory(ModifyInventoryOperation.UpdateQuantity, ipp));
                            c.SendPacket(WvsContext.inventoryOperation(true, mod));
                            //}

                            bulletConsume = 0;
                        }
                    }
                }
                if ((bulletConsume > 0) && (!MapleInventoryManipulator.removeById(c, InventoryType.USE, projectile, bulletConsume, false, true))) {
                    pPlayer.dropMessage(5, "You do not have enough arrows/bullets/stars.");
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
        } else if ((pPlayer.getJob() >= 3500) && (pPlayer.getJob() <= 3512)) {
            visProjectile = 2333000;
        } else if (GameConstants.isCannoneer(pPlayer.getJob())) {
            visProjectile = 2333001;
        }

        int projectileWatk = 0;
        if (projectile != 0) {
            projectileWatk = MapleItemInformationProvider.getInstance().getWatkForProjectile(projectile);
        }
        PlayerStats statst = pPlayer.getStat();
        double basedamage;
        switch (pAttack.skill) {
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
                switch (pAttack.skill) {
                    case 3101005:
                        basedamage *= pEffect.getX() / 100.0D;
                        break;
                }
        }
        if (pEffect != null) {
            basedamage *= (pEffect.getDamage() + statst.getDamageIncrease(pAttack.skill)) / 100.0D;

            long money = pEffect.getMoneyCon();
            if (money != 0) {
                if (money > pPlayer.getMeso()) {
                    money = pPlayer.getMeso();
                }
                pPlayer.gainMeso(-money, false);
            }
        }
        pPlayer.checkFollow();
        if (!pPlayer.isHidden()) {
            if (pAttack.skill == 3211006) {
                pPlayer.getMap().broadcastPacket(pPlayer, CField.strafeAttack(pPlayer.getId(), pAttack.tbyte, pAttack.skill, nSLV, pAttack.display, pAttack.speed, visProjectile, pAttack.allDamage, pAttack.position, pPlayer.getLevel(), pPlayer.getStat().passive_mastery(), pAttack.attackFlag, pPlayer.getTotalSkillLevel(3220010)), pPlayer.getTruePosition());
            } else {
                pPlayer.getMap().broadcastPacket(pPlayer, CField.rangedAttack(pPlayer.getId(), pAttack.tbyte, pAttack.skill, nSLV, pAttack.display, pAttack.speed, visProjectile, pAttack.allDamage, pAttack.position, pPlayer.getLevel(), pPlayer.getStat().passive_mastery(), pAttack.attackFlag), pPlayer.getTruePosition());
            }
        } else if (pAttack.skill == 3211006) {
            pPlayer.getMap().broadcastGMMessage(pPlayer, CField.strafeAttack(pPlayer.getId(), pAttack.tbyte, pAttack.skill, nSLV, pAttack.display, pAttack.speed, visProjectile, pAttack.allDamage, pAttack.position, pPlayer.getLevel(), pPlayer.getStat().passive_mastery(), pAttack.attackFlag, pPlayer.getTotalSkillLevel(3220010)), false);
        } else {
            pPlayer.getMap().broadcastGMMessage(pPlayer, CField.rangedAttack(pPlayer.getId(), pAttack.tbyte, pAttack.skill, nSLV, pAttack.display, pAttack.speed, visProjectile, pAttack.allDamage, pAttack.position, pPlayer.getLevel(), pPlayer.getStat().passive_mastery(), pAttack.attackFlag), false);
        }

        DamageParse.OnWeaponAttackRequest(pAttack, pSkill, pPlayer, nBulletCount, basedamage, pEffect, bMirror ? AttackType.RANGED_WITH_ShadowPartner : AttackType.RANGED);
        pAttack.cleanupMemory(); // Clean up memory references.
        
        pPlayer.OnSkillCostRequest(pEffect);
    }

}
