package handling.game;

import client.ClientSocket;
import client.Skill;
import client.SkillFactory;
import constants.GameConstants;
import java.awt.Point;
import net.InPacket;
import server.MapleItemInformationProvider;
import server.StatEffect;
import server.Randomizer;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import enums.SummonMovementType;
import server.maps.objects.User;
import server.maps.objects.Summon;
import tools.packet.CField;
import net.ProcessPacket;
import tools.packet.CField.EffectPacket;

/**
 *
 * @author Lloyd Korn
 */
public class SubSummonHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        User chr = c.getPlayer();
        final MapleMapObject obj = chr.getMap().getMapObject(iPacket.DecodeInt(), MapleMapObjectType.SUMMON);
        if (obj == null || !(obj instanceof Summon)) {
            return;
        }
        final Summon sum = (Summon) obj;
        if (sum == null || sum.getOwnerId() != chr.getId() || sum.getSkillLevel() <= 0 || !chr.isAlive()) {
            return;
        }
        switch (sum.getSkill()) {
            case 35121009:
                if (!chr.canSummon(2000)) {
                    return;
                }
                final int skillId = iPacket.DecodeInt(); // 35121009?
                if (sum.getSkill() != skillId) {
                    return;
                }
                iPacket.Skip(1); // 0E?
                chr.updateTick(iPacket.DecodeInt());
                for (int i = 0; i < 3; i++) {
                    final Summon tosummon = new Summon(
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
                chr.getClient().SendPacket(CField.EffectPacket.showOwnBuffEffect(sum.getSkill(), EffectPacket.SkillUseBySummoned, chr.getLevel(), sum.getSkillLevel()));
                chr.getMap().broadcastPacket(chr, CField.EffectPacket.showBuffEffect(chr.getId(), sum.getSkill(), EffectPacket.SkillUseBySummoned, chr.getLevel(), sum.getSkillLevel()), false);
                break;
            case 1321007: //beholder
            case 1301013: // Evil Eye
            case 1311013: // Evil Eye of Domination
                Skill bHealing = SkillFactory.getSkill(iPacket.DecodeInt());
                final int bHealingLvl = chr.getTotalSkillLevel(bHealing);
                if (bHealingLvl <= 0 || bHealing == null) {
                    return;
                }
                final StatEffect healEffect = bHealing.getEffect(bHealingLvl);
                if (bHealing.getId() == 1320009) {
                    healEffect.applyTo(chr);
                } else if (bHealing.getId() == 1320008) {
                    if (!chr.canSummon(healEffect.getX() * 1000)) {
                        return;
                    }
                    chr.addHP(healEffect.getHp());
                }
                chr.getClient().SendPacket(CField.EffectPacket.showOwnBuffEffect(sum.getSkill(), EffectPacket.SkillUseBySummoned, chr.getLevel(), bHealingLvl));
                chr.getMap().broadcastPacket(CField.SummonPacket.summonSkill(chr.getId(), sum.getSkill(), bHealing.getId() == 1320008 ? 5 : (Randomizer.nextInt(3) + 6)));
                chr.getMap().broadcastPacket(chr, CField.EffectPacket.showBuffEffect(chr.getId(), sum.getSkill(), EffectPacket.SkillUseBySummoned, chr.getLevel(), bHealingLvl), false);
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
            chr.getClient().SendPacket(CField.EffectPacket.showOwnBuffEffect(sum.getSkill(), EffectPacket.SkillUseBySummoned, 2, 1));
            chr.getMap().broadcastPacket(chr, CField.EffectPacket.showBuffEffect(chr.getId(), sum.getSkill(), EffectPacket.SkillUseBySummoned, 2, 1), false);
        }
    }

}
