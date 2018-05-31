package handling.game;

import client.CharacterTemporaryStat;
import client.ClientSocket;
import client.MonsterStatus;
import client.MonsterStatusEffect;
import client.Skill;
import client.SkillFactory;
import client.SummonSkillEntry;
import client.anticheat.CheatingOffense;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.InPacket;
import server.StatEffect;
import server.life.Mob;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import enums.SummonMovementType;
import server.maps.objects.User;
import server.maps.objects.Summon;
import tools.Pair;
import tools.packet.CField;
import tools.packet.MobPacket;
import net.ProcessPacket;

/**
 *
 * @author Lloyd Korn
 */
public class SummonAttackHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        /**
         * Damage 13 5A 02 2D A1 07 00 C1 D0 2C 2E 00 00 00 00 84 11 EA 03 9A 00 A8 03 99 00 00 00 00 00 *
         *
         * 2C A1 07 00 //oid 04 87 01 00 07 00 * 00 80 01 04 87 01 00 01 93 03 9A 00 93 03 9A 00 84 03 * 0D 00 00 00 //damage 00 00 00 00 AC
         * 34 D5 A0
         *
         */
        User chr = c.getPlayer();

        if (chr == null || !chr.isAlive() || chr.getMap() == null) {
            return;
        }
        MapleMap map = chr.getMap();
        MapleMapObject obj = map.getMapObject(iPacket.DecodeInt(), MapleMapObjectType.SUMMON);
        if (obj == null) {
            return;
        }
        Summon summon = (Summon) obj;
        SummonSkillEntry sse = SkillFactory.getSummonData(summon.getSkill());
        if (summon.getSkill() / 1000000 != 35 && summon.getSkill() != 33101008 && sse == null) {
            chr.dropMessage(5, "Error in processing attack.");
            return;
        }
        int tick = iPacket.DecodeInt();
        if (sse != null && sse.delay > 0) {
            chr.updateTick(tick);
            //summon.CheckSummonAttackFrequency(chr, tick);
            //chr.getCheatTracker().checkSummonAttack();
        }
        int skillId = iPacket.DecodeInt();
        iPacket.DecodeInt(); // v176
        byte animation = iPacket.DecodeByte();
        short tbyte = (short) iPacket.DecodeByte();
        short nMobCount = (short) (tbyte >>> 4 & 0xF);
        short nAttackCount = (short) (tbyte & 0xF);

        iPacket.DecodeByte(); // 0

        // ptUserPos
        iPacket.DecodeShort(); // 0x81 / 129
        iPacket.DecodeShort(); // 0x8E / 142

        // ptSummoned
        iPacket.DecodeShort();
        iPacket.DecodeShort();

        iPacket.DecodeInt(); // 0x94 / 148
        iPacket.DecodeShort(); // 0x8E / 142
        iPacket.DecodeInt(); // -1
        iPacket.DecodeInt(); // 0

        iPacket.DecodeInt();

        if (sse != null && nMobCount > sse.mobCount) {
            chr.dropMessage(5, "Warning: Attacking more monster than summon can do");
            chr.getCheatTracker().registerOffense(CheatingOffense.SUMMON_HACK_MOBS);
            //AutobanManager.getInstance().autoban(c, "Attacking more monster that summon can do (Skillid : "+summon.getSkill()+" Count : " + numAttacked + ", allowed : " + sse.mobCount + ")");
            return;
        }
        /*if (summon.getSkill() == 35111002) {
            iPacket.Skip(24);
        } else {
            iPacket.DecodePosition();
            iPacket.DecodeInt(); //*(TSingleton<CUserLocal>::ms_pInstance._m_pStr + 13929)
            iPacket.DecodeInt();
        }*/
        List<Pair<Integer, Long>> allDamage = new ArrayList<>();
        for (int i = 0; i < nMobCount; i++) {
            int oid = iPacket.DecodeInt();
            int monsterId = iPacket.DecodeInt();//mobId
            iPacket.DecodeByte();
            iPacket.DecodeByte();
            iPacket.DecodeByte();
            iPacket.DecodeByte();
            iPacket.DecodeByte();
            int monsterId2 = iPacket.DecodeInt();//mobId
            iPacket.DecodeByte();//m_nCalcDamageStatIndex
            final Point posNow = iPacket.DecodePosition(); //pos
            final Point posPrev = iPacket.DecodePosition();//previous pos
            iPacket.DecodeShort();

            for (int j = 0; j < nAttackCount; j++) {
                long damage = iPacket.DecodeLong();
                allDamage.add(new Pair<>(oid, damage));
            }
            iPacket.DecodeInt(); //yDown
            int nSkeletonResult = iPacket.DecodeByte();
            if (nSkeletonResult == 1) {
                iPacket.DecodeString();
                iPacket.DecodeString();
                iPacket.DecodeInt();
                for (int s = 0; s < iPacket.DecodeInt(); s++) {
                    iPacket.DecodeString();
                }
            } else if (nSkeletonResult == 2) {
                iPacket.DecodeString();
                iPacket.DecodeString();
                iPacket.DecodeInt();
            }
            iPacket.DecodeByte();
        }
        iPacket.DecodeInt(); //crc

        map.broadcastPacket(chr, CField.SummonPacket.summonAttack(summon.getOwnerId(), summon.getObjectId(), animation, allDamage, chr.getLevel(), false), summon.getTruePosition());
        Skill summonSkill = SkillFactory.getSkill(summon.getSkill());
        StatEffect summonEffect = summonSkill.getEffect(summon.getSkillLevel());
        if (summonEffect == null) {
            return;
        }
        for (Pair<Integer, Long> attackEntry : allDamage) {
            long toDamage = attackEntry.right;
            Mob mob = map.getMonsterByOid(attackEntry.left);

            if (mob == null) {
                continue;
            }

            if (sse != null && sse.delay > 0 && summon.getMovementType() != SummonMovementType.STATIONARY && summon.getMovementType() != SummonMovementType.CIRCLE_STATIONARY && summon.getMovementType() != SummonMovementType.WALK_STATIONARY && chr.getTruePosition().distanceSq(mob.getTruePosition()) > 400000.0) {
                //chr.getCheatTracker().registerOffense(CheatingOffense.ATTACK_FARAWAY_MONSTER_SUMMON);
            }
            if (toDamage > 0 && summonEffect.getMonsterStati().size() > 0) {
                if (summonEffect.makeChanceResult()) {
                    for (Map.Entry<MonsterStatus, Integer> z : summonEffect.getMonsterStati().entrySet()) {
                        mob.applyStatus(chr, new MonsterStatusEffect(z.getKey(), z.getValue(), summonSkill.getId(), null, false), summonEffect.isPoison(), 4000, true, summonEffect);
                    }
                }
            }
            if (chr.isGM() || toDamage < (chr.getStat().getCurrentMaxBaseDamage() * 5.0 * (summonEffect.getSelfDestruction() + summonEffect.getDamage() + chr.getStat().getDamageIncrease(summonEffect.getSourceId())) / 100.0)) { //10 x dmg.. eh
                mob.damage(chr, toDamage, true);
                chr.checkMonsterAggro(mob);
                if (!mob.isAlive()) {
                    chr.getClient().SendPacket(MobPacket.killMonster(mob.getObjectId(), 1, false));
                }
            } else {
                //chr.dropMessage(5, "Warning - high damage.");
                //AutobanManager.getInstance().autoban(c, "High Summon Damage (" + toDamage + " to " + attackEntry.right + ")");
                // TODO : Check player's stat for damage checking.
                break;
            }
        }
        if (!summon.isMultiAttack()) {
            chr.getMap().broadcastPacket(CField.SummonPacket.removeSummon(summon, true));
            chr.getMap().removeMapObject(summon);
            chr.removeVisibleMapObject(summon);
            chr.removeSummon(summon);
            if (summon.getSkill() != 35121011) {
                chr.cancelEffectFromTemporaryStat(CharacterTemporaryStat.SUMMON);
            }
        }
    }

}
