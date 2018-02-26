package handling.game;

import client.CharacterTemporaryStat;
import client.MapleClient;
import client.Skill;
import client.SkillFactory;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import constants.skills.DemonAvenger;
import constants.skills.NightWalker;
import constants.skills.Page;
import constants.skills.Zero;
import handling.world.AttackInfo;
import handling.world.AttackType;
import handling.world.DamageParse;
import handling.world.PlayerHandler;
import net.InPacket;
import server.MapleStatEffect;
import server.MapleStatInfo;
import server.Randomizer;
import server.events.MapleEvent;
import server.events.MapleEventType;
import server.events.MapleSnowball;
import server.maps.objects.MapleCharacter;
import service.ChannelServer;
import service.RecvPacketOpcode;
import tools.packet.CField;
import tools.packet.CWvsContext;
import tools.packet.JobPacket;

public final class CloseRangeAttack {

    public static void closeRangeAttack(InPacket iPacket, MapleClient c, MapleCharacter chr, boolean passiveAttack) {

        if (chr == null || chr.hasBlockedInventory() || chr.getMap() == null
                || (passiveAttack
                && (chr.getBuffedValue(CharacterTemporaryStat.EnergyCharged) == null
                && chr.getBuffedValue(CharacterTemporaryStat.BodyPressure) == null
                && chr.getBuffedValue(CharacterTemporaryStat.BMageAura) == null
                && chr.getBuffedValue(CharacterTemporaryStat.SUMMON) == null
                && chr.getBuffedValue(CharacterTemporaryStat.Asura) == null
                && chr.getBuffedValue(CharacterTemporaryStat.TeleportMasteryOn) == null))) {
            return;
        }

        //AttackInfo attack = DamageParse.parseCloseRangeAttack(iPacket, chr, passiveAttack);
        AttackInfo attack = DamageParse.OnAttack(RecvPacketOpcode.UserMeleeAttack, iPacket, chr);
        
        /*if (passiveAttack) {
            attack = DamageParse.OnAttack(RecvPacketOpcode.UserBodyAttack, iPacket, chr);
        } else {
            attack = DamageParse.OnAttack(RecvPacketOpcode.UserMeleeAttack, iPacket, chr);
        }*/
        
        if (chr.isDeveloper()) {
            c.getPlayer().dropMessage(5, "[Debug] CloseRangeAttack: Skill ID (" + attack.skill + ")");
        }
        
        final boolean mirror = (chr.hasBuff(CharacterTemporaryStat.ShadowPartner) || chr.hasBuff(CharacterTemporaryStat.ShadowServant));
        double maxdamage = chr.getStat().getCurrentMaxBaseDamage();
        Item shield = c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -10);
        int attackCount = shield != null && (shield.getItemId() / 10000 == 134) ? 2 : 1;
        int skillLevel = 0;
        MapleStatEffect effect = null;
        Skill skill = null;

        if (attack.skill != 0) {
            skill = SkillFactory.getSkill(GameConstants.getLinkedAttackSkill(attack.skill));
            if (skill == null || (GameConstants.isAngel(attack.skill) && chr.getStat().equippedSummon % 10000 != attack.skill % 10000)) {
                c.write(CWvsContext.enableActions());
                return;
            }
            
            if (GameConstants.isDemonAvenger(c.getPlayer().getJob())) {

                // 1% of Maximum HP as Skill Cost.
                int hpCost = (c.getPlayer().getMaxHP() / 150);
                c.getPlayer().addHP(-hpCost);

                // Demon Avenger Overload Stacks
                int exceedMax = c.getPlayer().getSkillLevel(31220044) > 0 ? 18 : 20;
                if (c.getPlayer().getExceed() + 1 > exceedMax) {
                    c.getPlayer().setExceed((short) exceedMax);
                } else {
                    c.getPlayer().gainExceed((short) 1);
                }
                if (GameConstants.isExceedAttack(skill.getId())) {
                    chr.handleExceedAttack(skill.getId());
                }
            }

            switch (attack.skill) {
                case Zero.RISING_SLASH:
                case Zero.FLASH_CUT:
                case Zero.SPIN_DRIVER:
                case Zero.GIGA_CRASH:
                    chr.zeroChange(true);
                    break;
                case Zero.MOON_STRIKE_1:
                case Zero.FLASH_ASSAULT:
                case Zero.ROLLING_CROSS:
                case Zero.WIND_CUTTER:
                    chr.zeroChange(false);
                    break;
            }
            skillLevel = chr.getTotalSkillLevel(skill);
            effect = attack.getAttackEffect(chr, skillLevel <= 0 ? attack.skillLevel : skillLevel, skill);
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

            if (effect != null) {
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
            }
            if (effect != null) {
                maxdamage *= (effect.getDamage() + chr.getStat().getDamageIncrease(attack.skill)) / 100.0D;
                attackCount = effect.getAttackCount();

                // Handle cooldown
                if (effect.getCooldown(chr) > 0 && !passiveAttack) {
                    if (chr.skillisCooling(attack.skill)) {
                        c.write(CWvsContext.enableActions());
                        return;
                    }
                    chr.addCooldown(attack.skill, System.currentTimeMillis(), effect.getCooldown(chr));
                }
            }
        }

        DamageParse.modifyCriticalAttack(attack, chr, 1, effect);

        attackCount *= (mirror ? 2 : 1);
        if (!passiveAttack) {
            if ((chr.getMapId() == 109060000 || chr.getMapId() == 109060002 || chr.getMapId() == 109060004) && attack.skill == 0) {
                MapleSnowball.MapleSnowballs.hitSnowball(chr);
            }

            int numFinisherOrbs = 0;
            Integer comboBuff = chr.getBuffedValue(CharacterTemporaryStat.ComboCounter);

            if (PlayerHandler.isFinisher(attack.skill) > 0) {
                if (comboBuff != null) {
                    numFinisherOrbs = comboBuff - 1;
                }
                if (numFinisherOrbs <= 0) {
                    return;
                }
                chr.handleOrbconsume(PlayerHandler.isFinisher(attack.skill));
            }
        }
        chr.checkFollow();
        if (!chr.isHidden()) {
            chr.getMap().broadcastMessage(chr, CField.closeRangeAttack(chr.getId(), attack.tbyte, attack.skill, skillLevel, attack.display, attack.speed, attack.allDamage, passiveAttack, chr.getLevel(), chr.getStat().passive_mastery(), attack.attackFlag, attack.charge), chr.getTruePosition());
        } else {
            chr.getMap().broadcastGMMessage(chr, CField.closeRangeAttack(chr.getId(), attack.tbyte, attack.skill, skillLevel, attack.display, attack.speed, attack.allDamage, passiveAttack, chr.getLevel(), chr.getStat().passive_mastery(), attack.attackFlag, attack.charge), false);
        }
        DamageParse.applyAttack(attack, skill, c.getPlayer(), attackCount, maxdamage, effect, mirror ? AttackType.NON_RANGED_WITH_MIRROR : AttackType.NON_RANGED);
        int bulletCount = 1;
        switch (attack.skill) {
            case Page.FLAME_CHARGE:
                bulletCount = effect.getAttackCount();
                DamageParse.applyAttack(attack, skill, chr, skillLevel, maxdamage, effect, AttackType.NON_RANGED);//applyAttack(attack, skill, chr, bulletCount, effect, AttackType.RANGED);
                break;
            default:
                DamageParse.applyAttack(attack, skill, chr, skillLevel, maxdamage, effect, AttackType.NON_RANGED);
                //DamageParse.applyAttackMagic(attack, skill, chr, effect, maxdamage);//applyAttackMagic(attack, skill, c.getPlayer(), effect);
                break;
        }

        // Cleanup memory refs
        attack.cleanupMemory();
    }

}
