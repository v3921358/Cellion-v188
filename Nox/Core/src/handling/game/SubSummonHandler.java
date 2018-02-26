package handling.game;

import client.MapleClient;
import client.Skill;
import client.SkillFactory;
import constants.GameConstants;
import java.awt.Point;
import net.InPacket;
import server.MapleItemInformationProvider;
import server.MapleStatEffect;
import server.Randomizer;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.SummonMovementType;
import server.maps.objects.MapleCharacter;
import server.maps.objects.MapleSummon;
import tools.packet.CField;
import netty.ProcessPacket;

/**
 *
 * @author Lloyd Korn
 */
public class SubSummonHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        MapleCharacter chr = c.getPlayer();
        final MapleMapObject obj = chr.getMap().getMapObject(iPacket.DecodeInteger(), MapleMapObjectType.SUMMON);
        if (obj == null || !(obj instanceof MapleSummon)) {
            return;
        }
        final MapleSummon sum = (MapleSummon) obj;
        if (sum == null || sum.getOwnerId() != chr.getId() || sum.getSkillLevel() <= 0 || !chr.isAlive()) {
            return;
        }
        switch (sum.getSkill()) {
            case 35121009:
                if (!chr.canSummon(2000)) {
                    return;
                }
                final int skillId = iPacket.DecodeInteger(); // 35121009?
                if (sum.getSkill() != skillId) {
                    return;
                }
                iPacket.Skip(1); // 0E?
                chr.updateTick(iPacket.DecodeInteger());
                for (int i = 0; i < 3; i++) {
                    final MapleSummon tosummon = new MapleSummon(
                            chr,
                            SkillFactory.getSkill(35121011).getEffect(sum.getSkillLevel()),
                            new Point(sum.getTruePosition().x, sum.getTruePosition().y - 5),
                            SummonMovementType.WALK_STATIONARY,
                            0
                    );
                    chr.getMap().spawnSummon(tosummon);
                    chr.addSummon(tosummon);
                }
                break;
            case 35111011: //healing
                if (!chr.canSummon(1000)) {
                    return;
                }
                chr.addHP((int) (chr.getStat().getCurrentMaxHp() * SkillFactory.getSkill(sum.getSkill()).getEffect(sum.getSkillLevel()).getHp() / 100.0));
                chr.getClient().write(CField.EffectPacket.showOwnBuffEffect(sum.getSkill(), CField.EffectPacket.UserEffectCodes.SkillUseBySummoned, chr.getLevel(), sum.getSkillLevel()));
                chr.getMap().broadcastMessage(chr, CField.EffectPacket.showBuffeffect(chr.getId(), sum.getSkill(), CField.EffectPacket.UserEffectCodes.SkillUseBySummoned, chr.getLevel(), sum.getSkillLevel()), false);
                break;
            case 1321007: //beholder
            case 1301013: // Evil Eye
            case 1311013: // Evil Eye of Domination
                Skill bHealing = SkillFactory.getSkill(iPacket.DecodeInteger());
                final int bHealingLvl = chr.getTotalSkillLevel(bHealing);
                if (bHealingLvl <= 0 || bHealing == null) {
                    return;
                }
                final MapleStatEffect healEffect = bHealing.getEffect(bHealingLvl);
                if (bHealing.getId() == 1320009) {
                    healEffect.applyTo(chr);
                } else if (bHealing.getId() == 1320008) {
                    if (!chr.canSummon(healEffect.getX() * 1000)) {
                        return;
                    }
                    chr.addHP(healEffect.getHp());
                }
                chr.getClient().write(CField.EffectPacket.showOwnBuffEffect(sum.getSkill(), CField.EffectPacket.UserEffectCodes.SkillUseBySummoned, chr.getLevel(), bHealingLvl));
                chr.getMap().broadcastMessage(CField.SummonPacket.summonSkill(chr.getId(), sum.getSkill(), bHealing.getId() == 1320008 ? 5 : (Randomizer.nextInt(3) + 6)));
                chr.getMap().broadcastMessage(chr, CField.EffectPacket.showBuffeffect(chr.getId(), sum.getSkill(), CField.EffectPacket.UserEffectCodes.SkillUseBySummoned, chr.getLevel(), bHealingLvl), false);
                break;
        }
        if (GameConstants.isAngel(sum.getSkill())) {
            switch (sum.getSkill() % 10000) {
                case 1087:
                    MapleItemInformationProvider.getInstance().getItemEffect(2022747).applyTo(chr);
                    break;
                case 1179:
                    MapleItemInformationProvider.getInstance().getItemEffect(2022823).applyTo(chr);
                    break;
                default:
                    MapleItemInformationProvider.getInstance().getItemEffect(2022746).applyTo(chr);
                    break;
            }
            chr.getClient().write(CField.EffectPacket.showOwnBuffEffect(sum.getSkill(), CField.EffectPacket.UserEffectCodes.SkillUseBySummoned, 2, 1));
            chr.getMap().broadcastMessage(chr, CField.EffectPacket.showBuffeffect(chr.getId(), sum.getSkill(), CField.EffectPacket.UserEffectCodes.SkillUseBySummoned, 2, 1), false);
        }
    }

}
