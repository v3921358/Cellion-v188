package handling.game;

import java.awt.Point;

import client.CharacterTemporaryStat;
import client.MapleClient;
import client.MonsterStatus;
import client.MonsterStatusEffect;
import client.PlayerStats;
import client.Skill;
import client.SkillFactory;
import client.inventory.Item;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import constants.skills.Xenon;
import server.MapleItemInformationProvider;
import server.MapleStatEffect;
import server.Randomizer;
import server.life.MapleMonster;
import server.life.MobAttackInfo;
import server.life.MobSkill;
import server.life.MobSkillFactory;
import server.maps.MapleMap;
import server.maps.objects.MapleCharacter;
import tools.Pair;
import net.InPacket;
import tools.packet.CField;
import tools.packet.CWvsContext;
import tools.packet.JobPacket;
import netty.ProcessPacket;

public final class PlayerDamageHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    public enum PlayerDamageType {
        BumpDamage((byte) -1),
        UnkDamage((byte) -2),
        MapDamage((byte) -3),
        MistDamage((byte) -4),
        Unknown(Byte.MAX_VALUE);
        private final byte type;

        private PlayerDamageType(byte type) {
            this.type = type;
        }

        public byte getType() {
            return type;
        }

        public static PlayerDamageType getTypeFromInt(byte type) {
            for (PlayerDamageType t : values()) {
                if (t.getType() == type) {
                    return t;
                }
            }
            return Unknown;
        }
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        final MapleCharacter chr = c.getPlayer();

        iPacket.Skip(4);
        chr.updateTick(iPacket.DecodeInteger());
        int type = iPacket.DecodeByte();
        PlayerDamageType type_ = PlayerDamageType.getTypeFromInt((byte) type);
        iPacket.Skip(1);
        int damage = iPacket.DecodeInteger();
        iPacket.Skip(2);
        boolean isDeadlyAttack = false;
        boolean pPhysical = false;
        int oid = 0;
        int monsteridfrom = 0;
        int fake = 0;
        int mpattack = 0;
        int skillid = 0;
        int pID = 0;
        int pDMG = 0;
        byte direction = 0;
        byte pType = 0;
        Point pPos = new Point(0, 0);
        MapleMonster attacker = null;

        if (GameConstants.isXenon(chr.getJob())) { // Making sure EazisSystem still works when a GM is hiding
            if (chr.hasBuff(CharacterTemporaryStat.XenonAegisSystem)) {
                if (Randomizer.nextInt(100) < (chr.getTotalSkillLevel(Xenon.AEGIS_SYSTEM) * 10)) {
                    chr.getMap().broadcastMessage(JobPacket.XenonPacket.EazisSystem(chr.getId(), oid));
                }
            }
        }

        if (GameConstants.isLuminous(chr.getJob())) {
            chr.applyLifeTidal();
        }
        if (chr.isHidden() || chr.getMap() == null || (chr.isGM() && chr.isInvincible())) {
            c.write(CWvsContext.enableActions());
            return;
        }

        PlayerStats stats = chr.getStat();
        if (type_ != PlayerDamageType.UnkDamage
                && type_ != PlayerDamageType.MapDamage
                && type_ != PlayerDamageType.MistDamage) {
            monsteridfrom = iPacket.DecodeInteger();
            oid = iPacket.DecodeInteger();
            attacker = chr.getMap().getMonsterByOid(oid);
            direction = iPacket.DecodeByte();

            if ((attacker == null) || (attacker.getId() != monsteridfrom) || (attacker.getLinkCID() > 0) || (attacker.isFake()) || (attacker.getStats().isFriendly())) {
                return;
            }
            if (chr.getMapId() == 915000300) {
                MapleMap to = chr.getClient().getChannelServer().getMapFactory().getMap(915000200);
                chr.dropMessage(5, "You've been found out! Retreat!");
                chr.changeMap(to, to.getPortal(1));
                return;
            } else if (attacker.getId() == 9300166 && chr.getMapId() == 910025200) {
                int rocksLost = Randomizer.rand(1, 5);
                while (chr.itemQuantity(4031469) < rocksLost) {
                    rocksLost--;
                }
                if (rocksLost > 0) {
                    chr.gainItem(4031469, -rocksLost);
                    Item toDrop = MapleItemInformationProvider.getInstance().getEquipById(4031469);
                    for (int i = 0; i < rocksLost; i++) {
                        chr.getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), toDrop, c.getPlayer().getPosition(), true, true, false);
                    }
                }
            }
            if (type_ != PlayerDamageType.BumpDamage && damage > 0) {
                MobAttackInfo attackInfo = attacker.getStats().getMobAttack(type);
                if (attackInfo != null) {
                    if ((attackInfo.isElement()) && (stats.terR > 0) && (Randomizer.nextInt(100) < stats.terR)) {
                        return;
                    }

                    if (attackInfo.isDeadlyAttack()) {
                        isDeadlyAttack = true;
                        mpattack = stats.getMp() - 1;
                    } else {
                        mpattack += attackInfo.getMpBurn();
                    }
                    MobSkill skill = MobSkillFactory.getMobSkill(attackInfo.getDiseaseSkill(), attackInfo.getDiseaseLevel());
                    if ((skill != null) && ((damage == -1) || (damage > 0))) {
                        skill.applyEffect(chr, attacker, false);
                    }
                    attacker.setMp(attacker.getMp() - attackInfo.getMpCon());
                }
            }
            skillid = iPacket.DecodeInteger();
            pDMG = iPacket.DecodeInteger();
            byte defType = iPacket.DecodeByte();
            iPacket.Skip(1);

            if (defType == 1) {
                Skill bx = SkillFactory.getSkill(31110008);
                int bof = chr.getTotalSkillLevel(bx);
                if (bof > 0) {
                    MapleStatEffect eff = bx.getEffect(bof);
                    if (Randomizer.nextInt(100) <= eff.getX()) {
                        chr.handleForceGain(oid, 31110008, eff.getZ());
                    }
                }
            }
            if (skillid != 0) {
                pPhysical = iPacket.DecodeByte() > 0;
                pID = iPacket.DecodeInteger();
                pType = iPacket.DecodeByte();
                iPacket.Skip(4);
                pPos = iPacket.DecodePosition();
            }
        }
        if (damage == -1) {
            fake = 4020002 + (chr.getJob() / 10 - 40) * 100000;
            if ((fake != 4120002) && (fake != 4220002)) {
                fake = 4120002;
            }
            if (type_ == PlayerDamageType.BumpDamage
                    && chr.getJob() == 122
                    && attacker != null
                    && chr.getInventory(MapleInventoryType.EQUIPPED).getItem((byte) -10) != null
                    && chr.getTotalSkillLevel(1220006) > 0) {

                MapleStatEffect eff = SkillFactory.getSkill(1220006).getEffect(chr.getTotalSkillLevel(1220006));
                attacker.applyStatus(chr, new MonsterStatusEffect(MonsterStatus.STUN, 1, 1220006, null, false), false, eff.getDuration(), true, eff);
                fake = 1220006;
            }

            if (chr.getTotalSkillLevel(fake) <= 0) {
                return;
            }
        } else if ((damage < -1) || (damage > 200000)) {
            c.write(CWvsContext.enableActions());
            return;
        }
        // Resist is handled by the client! 
        // anyway, TODO: Check on the server side if the player can resist, otherwise HACK
        /*if ((chr.getStat().dodgeChance > 0) && (Randomizer.nextInt(100) < chr.getStat().dodgeChance)) {
            c.write(CField.EffectPacket.showForeignEffect(UserEffectCodes.Resist));
            return;
        }*/
        if (pPhysical && skillid == 1201007 && chr.getTotalSkillLevel(1201007) > 0) {
            damage -= pDMG;
            if (damage > 0) {
                MapleStatEffect eff = SkillFactory.getSkill(1201007).getEffect(chr.getTotalSkillLevel(1201007));
                long enemyDMG = Math.min(damage * (eff.getY() / 100), attacker.getMobMaxHp() / 2L);
                if (enemyDMG > pDMG) {
                    enemyDMG = pDMG;
                }
                if (enemyDMG > 1000L) {
                    enemyDMG = 1000L;
                }
                attacker.damage(chr, enemyDMG, true, 1201007);
            } else {
                damage = 1;
            }
        }
        chr.getCheatTracker().checkTakeDamage(damage);
        Pair modify = chr.modifyDamageTaken(damage, attacker);
        damage = ((Double) modify.left).intValue();

        if (damage > 0) {
            chr.getCheatTracker().setAttacksWithoutHit(false);

            boolean mpAttack = (chr.getBuffedValue(CharacterTemporaryStat.Mechanic) != null) && (chr.getBuffSource(CharacterTemporaryStat.Mechanic) != 35121005);

            if (chr.getBuffedValue(CharacterTemporaryStat.Morph) != null) {
                chr.cancelMorphs();
            }

            if (isDeadlyAttack) { // Skills that causes HP and MP to be 1
                chr.addMPHP(stats.getHp() > 1 ? -(stats.getHp() - 1) : 0, (stats.getMp() > 1) && (!mpAttack) ? -(stats.getMp() - 1) : 0);
            } else {
                int decreaseMP = 0;

                // Calculate for magic guard and passive magic guard
                if (chr.getStat().standardMagicGuard > 0 || chr.getStat().magic_guard_rate > 0) {
                    int damageLossToMP = (int) (damage * (chr.getStat().standardMagicGuard > 0 ? chr.getStat().standardMagicGuard : chr.getStat().magic_guard_rate));
                    if (damageLossToMP > stats.getMp()) {
                        damageLossToMP = stats.getMp();
                    }
                    damage -= damageLossToMP;
                    decreaseMP -= damageLossToMP;
                } // Calculate for meso guard skill
                else if (chr.getStat().mesoGuardMeso > 0.0D) {
                    int mesoloss = (int) (damage * (chr.getStat().mesoGuardMeso / 100.0D));

                    if (chr.getMeso() < mesoloss) {
                        chr.gainMeso(-chr.getMeso(), false);
                        chr.cancelTemporaryStats(new CharacterTemporaryStat[]{CharacterTemporaryStat.MesoGuard});
                    } else {
                        chr.gainMeso(-mesoloss, false);
                    }
                    damage -= mesoloss;
                }

                if (!mpAttack) {
                    decreaseMP -= mpattack; // doesnt matter if this goes beyond char's MP or HP, char.addMPHP() will keep it within range 
                }

                // A character with infinity can't lose any MP... 
                if (chr.getBuffedValue(CharacterTemporaryStat.Infinity) != null) {
                    decreaseMP = 0;
                }
                chr.addMPHP(-damage, decreaseMP);
            }
            if (chr.inPVP() && chr.getStat().getHPPercent() <= 20) {
                chr.getStat();
                SkillFactory.getSkill(PlayerStats.getSkillByJob(93, chr.getJob())).getEffect(1).applyTo(chr);
            }
        }
        byte offset = 0;
        int offset_d = 0;
        if (iPacket.Available() == 1L) {
            offset = iPacket.DecodeByte();
            if (offset == 1 && iPacket.Available() >= 4L) {
                offset_d = iPacket.DecodeInteger();
            }
            if (offset < 0 || offset > 2) {
                offset = 0;
            }
        }
        chr.getMap().broadcastMessage(chr, CField.damagePlayer(chr.getId(), type_, damage, monsteridfrom, direction, skillid, pDMG, pPhysical, pID, pType, pPos, offset, offset_d, fake), false);
    }

}
