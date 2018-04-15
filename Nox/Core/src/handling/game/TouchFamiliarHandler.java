package handling.game;

import client.MapleClient;
import server.life.Mob;
import server.maps.objects.User;
import net.InPacket;
import tools.packet.CField;
import net.ProcessPacket;

public final class TouchFamiliarHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        final User chr = c.getPlayer();
        if (chr.getSummonedFamiliar() == null) {
            return;
        }
        iPacket.Skip(6);
        byte unk = iPacket.DecodeByte();

        Mob target = chr.getMap().getMonsterByOid(iPacket.DecodeInt());
        if (target == null) {
            return;
        }
        int type = iPacket.DecodeInt();
        iPacket.Skip(4);
        int damage = iPacket.DecodeInt();
        int maxDamage = chr.getSummonedFamiliar().getStats().getPADamage() * 5;
        if (damage < maxDamage) {
            damage = maxDamage;
        }
        if ((!target.getStats().isFriendly()) && (chr.getCheatTracker().checkFamiliarAttack(chr))) {
            chr.getMap().broadcastMessage(chr, CField.touchFamiliar(chr.getId(), unk, target.getObjectId(), type, 600, damage), chr.getTruePosition());
            target.damage(chr, damage, true);
            chr.getSummonedFamiliar().addFatigue(chr);
        }
    }

}
