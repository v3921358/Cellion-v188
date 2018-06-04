package handling.game;

import java.awt.Point;

import client.CharacterTemporaryStat;
import client.ClientSocket;
import enums.Stat;
import client.MonsterStatus;
import client.MonsterStatusEffect;
import client.PlayerStats;
import client.Skill;
import client.SkillFactory;
import client.inventory.Item;
import enums.InventoryType;
import constants.GameConstants;
import constants.skills.Xenon;
import client.jobs.Kinesis;
import client.jobs.Kinesis.KinesisHandler;
import client.jobs.Resistance.BlasterHandler;
import server.MapleItemInformationProvider;
import server.StatEffect;
import server.Randomizer;
import server.life.Mob;
import server.life.MobAttackInfo;
import server.life.MobSkill;
import server.life.MobSkillFactory;
import server.maps.MapleMap;
import server.maps.objects.User;
import tools.Pair;
import net.InPacket;
import tools.packet.CField;
import tools.packet.WvsContext;
import tools.packet.JobPacket;
import net.ProcessPacket;
import server.life.mob.BuffedMob;
import server.maps.objects.User;

public final class PlayerDamageHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
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
    public void Process(ClientSocket c, InPacket iPacket) {
        final User pPlayer = c.getPlayer();
        if (pPlayer.hasGodMode()) {
            return; // Godmode
        }

        iPacket.Skip(4);
        pPlayer.updateTick(iPacket.DecodeInt());
        int type = iPacket.DecodeByte();
        PlayerDamageType type_ = PlayerDamageType.getTypeFromInt((byte) type);
        iPacket.Skip(1);
        int damage = iPacket.DecodeInt();
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
        Mob attacker = null;

        if (GameConstants.isXenon(pPlayer.getJob())) { // Making sure EazisSystem still works when a GM is hiding
            if (pPlayer.hasBuff(CharacterTemporaryStat.XenonAegisSystem)) {
                if (Randomizer.nextInt(100) < (pPlayer.getTotalSkillLevel(Xenon.AEGIS_SYSTEM) * 10)) {
                    pPlayer.getMap().broadcastPacket(JobPacket.XenonPacket.EazisSystem(pPlayer.getId(), oid));
                }
            }
        }

        if (GameConstants.isKinesis(pPlayer.getJob())) {
            if (pPlayer.hasBuff(CharacterTemporaryStat.KinesisPsychicEnergeShield)) {
                KinesisHandler.psychicPointResult(pPlayer, pPlayer.getPrimaryStack() - 1); // Consume PP
                //damage *= 0.4; // Apply 60% Damage Reduction
            } else if (pPlayer.getPrimaryStack() > 0) {
                KinesisHandler.requestMentalShield(pPlayer); // Give Shield
            } else if (pPlayer.getPrimaryStack() < 1) {
                pPlayer.cancelEffectFromTemporaryStat(CharacterTemporaryStat.KinesisPsychicEnergeShield); //Remove Shield
            }
        }
        if (GameConstants.isBlaster(pPlayer.getJob())) {
            if (pPlayer.hasSkill(constants.skills.Blaster.BLAST_SHIELD)) {
                BlasterHandler.requestBlastShield(pPlayer); // Give Shield
            }
        }

        if (GameConstants.isLuminous(pPlayer.getJob())) {
            pPlayer.applyLifeTidal();
        }
        if (pPlayer.isHidden() || pPlayer.getMap() == null || (pPlayer.isGM() && pPlayer.isInvincible())) {
            c.SendPacket(WvsContext.enableActions());
            return;
        }

        PlayerStats stats = pPlayer.getStat();
        if (type_.getType() > PlayerDamageType.UnkDamage.getType()) {
            oid = iPacket.DecodeInt(); // dwMobID
            monsteridfrom = iPacket.DecodeInt(); // dwTemplateID
            iPacket.DecodeInt(); // dwMobIDForMissCheck  (oid repeat)
            attacker = pPlayer.getMap().getMonsterByOid(oid);
            direction = iPacket.DecodeByte();

            if ((attacker == null) || (attacker.getId() != monsteridfrom) || (attacker.getLinkCID() > 0) || (attacker.isFake()) || (attacker.getStats().isFriendly())) {
                return;
            }
            if (pPlayer.getMapId() == 915000300) {
                MapleMap to = pPlayer.getClient().getChannelServer().getMapFactory().getMap(915000200);
                pPlayer.dropMessage(5, "You've been found out! Retreat!");
                pPlayer.changeMap(to, to.getPortal(1));
                return;
            } else if (attacker.getId() == 9300166 && pPlayer.getMapId() == 910025200) {
                int rocksLost = Randomizer.rand(1, 5);
                while (pPlayer.itemQuantity(4031469) < rocksLost) {
                    rocksLost--;
                }
                if (rocksLost > 0) {
                    pPlayer.gainItem(4031469, -rocksLost);
                    Item toDrop = MapleItemInformationProvider.getInstance().getEquipById(4031469);
                    for (int i = 0; i < rocksLost; i++) {
                        pPlayer.getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), toDrop, c.getPlayer().getPosition(), true, true, false);
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
                        skill.applyEffect(pPlayer, attacker, false);
                    }
                    attacker.setMp(attacker.getMp() - attackInfo.getMpCon());
                }
            }
            skillid = iPacket.DecodeInt();
            pDMG = iPacket.DecodeInt();
            byte defType = iPacket.DecodeByte();
            iPacket.Skip(1);
            if (defType == 1) {
                Skill bx = SkillFactory.getSkill(31110008);
                int bof = pPlayer.getTotalSkillLevel(bx);
                if (bof > 0) {
                    StatEffect eff = bx.getEffect(bof);
                    if (Randomizer.nextInt(100) <= eff.getX()) {
                        pPlayer.handleForceGain(oid, 31110008, eff.getZ());
                    }
                }
            }
            if (skillid != 0) {
                pPhysical = iPacket.DecodeByte() > 0;
                pID = iPacket.DecodeInt();
                pType = iPacket.DecodeByte();
                iPacket.Skip(4);
                pPos = iPacket.DecodePosition();
            }
        }
        if (damage == -1) {
            fake = 4020002 + (pPlayer.getJob() / 10 - 40) * 100000;
            if ((fake != 4120002) && (fake != 4220002)) {
                fake = 4120002;
            }
            if (type_ == PlayerDamageType.BumpDamage
                    && pPlayer.getJob() == 122
                    && attacker != null
                    && pPlayer.getInventory(InventoryType.EQUIPPED).getItem((byte) -10) != null
                    && pPlayer.getTotalSkillLevel(1220006) > 0) {

                StatEffect eff = SkillFactory.getSkill(1220006).getEffect(pPlayer.getTotalSkillLevel(1220006));
                attacker.applyStatus(pPlayer, new MonsterStatusEffect(MonsterStatus.STUN, 1, 1220006, null, false), false, eff.getDuration(), true, eff);
                fake = 1220006;
            }

            if (pPlayer.getTotalSkillLevel(fake) <= 0) {
                return;
            }
        } else if ((damage < -1) || (damage > 200000)) {
            c.SendPacket(WvsContext.enableActions());
            return;
        }
        // Resist is handled by the client! 
        // anyway, TODO: Check on the server side if the player can resist, otherwise HACK
        /*if ((chr.getStat().dodgeChance > 0) && (Randomizer.nextInt(100) < chr.getStat().dodgeChance)) {
            c.write(CField.EffectPacket.showForeignEffect(UserEffectCodes.Resist));
            return;
        }*/
        if (pPhysical && skillid == 1201007 && pPlayer.getTotalSkillLevel(1201007) > 0) {
            damage -= pDMG;
            if (damage > 0) {
                StatEffect eff = SkillFactory.getSkill(1201007).getEffect(pPlayer.getTotalSkillLevel(1201007));
                long enemyDMG = Math.min(damage * (eff.getY() / 100), attacker.getMobMaxHp() / 2L);
                if (enemyDMG > pDMG) {
                    enemyDMG = pDMG;
                }
                if (enemyDMG > 1000L) {
                    enemyDMG = 1000L;
                }
                attacker.damage(pPlayer, enemyDMG, true, 1201007);
            } else {
                damage = 1;
            }
        }
        pPlayer.getCheatTracker().checkTakeDamage(damage);
        Pair modify = pPlayer.modifyDamageTaken(damage, attacker);
        damage = ((Double) modify.left).intValue();
        
        if (BuffedMob.OnBuffedChannel(pPlayer.getClient().getChannel())) damage *= BuffedMob.DAMAGE_BUFF; // Buffed Channel Damage 

        if (damage > 0) {
            pPlayer.getCheatTracker().setAttacksWithoutHit(false);

            boolean mpAttack = (pPlayer.getBuffedValue(CharacterTemporaryStat.Mechanic) != null) && (pPlayer.getBuffSource(CharacterTemporaryStat.Mechanic) != 35121005);

            if (pPlayer.getBuffedValue(CharacterTemporaryStat.Morph) != null) {
                pPlayer.cancelMorphs();
            }

            if (isDeadlyAttack) { // Skills that causes HP and MP to be 1
                pPlayer.addMPHP(stats.getHp() > 1 ? -(stats.getHp() - 1) : 0, (stats.getMp() > 1) && (!mpAttack) ? -(stats.getMp() - 1) : 0);
            } else {
                int decreaseMP = 0;

                // Calculate for magic guard and passive magic guard
                if (pPlayer.getStat().standardMagicGuard > 0 || pPlayer.getStat().magic_guard_rate > 0) {
                    int damageLossToMP = (int) (damage * (pPlayer.getStat().standardMagicGuard > 0 ? pPlayer.getStat().standardMagicGuard : pPlayer.getStat().magic_guard_rate));
                    if (damageLossToMP > stats.getMp()) {
                        damageLossToMP = stats.getMp();
                    }
                    damage -= damageLossToMP;
                    decreaseMP -= damageLossToMP;
                } // Calculate for meso guard skill
                else if (pPlayer.getStat().mesoGuardMeso > 0.0D) {
                    int mesoloss = (int) (damage * (pPlayer.getStat().mesoGuardMeso / 100.0D));

                    if (pPlayer.getMeso() < mesoloss) {
                        pPlayer.gainMeso(-pPlayer.getMeso(), false);
                        pPlayer.cancelTemporaryStats(new CharacterTemporaryStat[]{CharacterTemporaryStat.MesoGuard});
                    } else {
                        pPlayer.gainMeso(-mesoloss, false);
                    }
                    damage -= mesoloss;
                }

                if (!mpAttack) {
                    decreaseMP -= mpattack; // doesnt matter if this goes beyond char's MP or HP, char.addMPHP() will keep it within range 
                }

                // A character with infinity can't lose any MP... 
                if (pPlayer.getBuffedValue(CharacterTemporaryStat.Infinity) != null) {
                    decreaseMP = 0;
                }
                pPlayer.addMPHP(-damage, decreaseMP);
            }
            if (pPlayer.inPVP() && pPlayer.getStat().getHPPercent() <= 20) {
                pPlayer.getStat();
                SkillFactory.getSkill(PlayerStats.getSkillByJob(93, pPlayer.getJob())).getEffect(1).applyTo(pPlayer);
            }
        }
        byte offset = 0;
        int offset_d = 0;
        if (iPacket.GetRemainder() == 1L) {
            offset = iPacket.DecodeByte();
            if (offset == 1 && iPacket.GetRemainder() >= 4L) {
                offset_d = iPacket.DecodeInt();
            }
            if (offset < 0 || offset > 2) {
                offset = 0;
            }
        }
        pPlayer.getMap().broadcastPacket(pPlayer, CField.damagePlayer(pPlayer.getId(), type_, damage, monsteridfrom, direction, skillid, pDMG, pPhysical, pID, pType, pPos, offset, offset_d, fake), false);
        // Revive Passives
        if (!pPlayer.isAlive()) {
            if (pPlayer.hasBuff(CharacterTemporaryStat.ReviveOnce)) {
                pPlayer.getStat().setHp(pPlayer.getStat().getMaxHp(), pPlayer);
                pPlayer.updateSingleStat(Stat.HP, pPlayer.getStat().getMaxHp());
                pPlayer.getStat().setMp(pPlayer.getStat().getMaxMp(), pPlayer);
                pPlayer.updateSingleStat(Stat.MP, pPlayer.getStat().getMaxMp());
                pPlayer.dispelDebuffs();
                pPlayer.cancelAllBuffs();
            }
        }
    }

}
