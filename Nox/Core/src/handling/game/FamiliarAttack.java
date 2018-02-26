package handling.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import client.MapleClient;
import client.MonsterStatus;
import client.MonsterStatusEffect;
import client.SkillFactory;
import client.SkillFactory.FamiliarEntry;
import client.anticheat.CheatingOffense;
import constants.GameConstants;
import server.life.MapleMonster;
import server.life.MapleMonsterStats;
import server.maps.objects.MapleCharacter;
import tools.Triple;
import net.InPacket;
import tools.packet.CField;
import netty.ProcessPacket;

public final class FamiliarAttack implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        final MapleCharacter chr = c.getPlayer();
        if (chr.getSummonedFamiliar() == null) {
            return;
        }
        iPacket.Skip(6);
        int skillid = iPacket.DecodeInteger();

        FamiliarEntry f = SkillFactory.getFamiliar(skillid);
        if (f == null) {
            return;
        }
        byte unk = iPacket.DecodeByte();
        byte size = iPacket.DecodeByte();
        List<Triple<Integer, Integer, List<Integer>>> attackPair = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            int oid = iPacket.DecodeInteger();
            int type = iPacket.DecodeInteger();
            iPacket.Skip(10);
            byte si = iPacket.DecodeByte();
            List<Integer> attack = new ArrayList<>(si);
            for (int x = 0; x < si; x++) {
                attack.add(iPacket.DecodeInteger());
            }
            attackPair.add(new Triple<>(oid, type, attack));
        }
        if ((attackPair.isEmpty()) || (!chr.getCheatTracker().checkFamiliarAttack(chr)) || (attackPair.size() > f.targetCount)) {
            return;
        }
        MapleMonsterStats oStats = chr.getSummonedFamiliar().getStats();
        chr.getMap().broadcastMessage(chr, CField.familiarAttack(chr.getId(), unk, attackPair), chr.getTruePosition());
        for (Triple attack : attackPair) {
            MapleMonster mons = chr.getMap().getMonsterByOid(((Integer) attack.left).intValue());
            if ((mons != null) && (mons.isAlive()) && (!mons.getStats().isFriendly()) && (mons.getLinkCID() <= 0) && (((List) attack.right).size() <= f.attackCount)) {
                if ((chr.getTruePosition().distanceSq(mons.getTruePosition()) > 640000.0D) || (chr.getSummonedFamiliar().getTruePosition().distanceSq(mons.getTruePosition()) > GameConstants.getAttackRange(f.lt, f.rb))) {
                    chr.getCheatTracker().registerOffense(CheatingOffense.ATTACK_FARAWAY_MONSTER_SUMMON);
                }
                for (Iterator i$ = ((List) attack.right).iterator(); i$.hasNext();) {
                    int damage = (Integer) i$.next();
                    if (damage <= oStats.getPADamage() * 4) {
                        mons.damage(chr, damage, true);
                    }
                }
                if ((f.makeChanceResult()) && (mons.isAlive())) {
                    for (MonsterStatus s : f.status) {
                        mons.applyStatus(chr, new MonsterStatusEffect(s, Integer.valueOf(f.speed), MonsterStatusEffect.genericSkill(s), null, false), false, f.time * 1000, false, null);
                    }
                    if (f.knockback) {
                        mons.switchController(chr, true);
                    }
                }
            }
        }
        chr.getSummonedFamiliar().addFatigue(chr, attackPair.size());
    }

}
