package handling.game;

import java.util.HashMap;
import java.util.Map;

import client.Client;
import server.maps.objects.User;
import net.InPacket;
import server.maps.MapleMapObjectType;
import tools.packet.CField;
import net.ProcessPacket;

public final class CraftEffect implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return true;
    }

    private static final Map<String, Integer> craftingEffects = new HashMap<>();

    static {
        craftingEffects.put("Effect/BasicEff.img/professions/herbalism", 92000000);
        craftingEffects.put("Effect/BasicEff.img/professions/mining", 92010000);
        craftingEffects.put("Effect/BasicEff.img/professions/herbalismExtract", 92000000);
        craftingEffects.put("Effect/BasicEff.img/professions/miningExtract", 92010000);

        craftingEffects.put("Effect/BasicEff.img/professions/equip_product", 92020000);
        craftingEffects.put("Effect/BasicEff.img/professions/acc_product", 92030000);
        craftingEffects.put("Effect/BasicEff.img/professions/alchemy", 92040000);
    }

    @Override
    public void Process(Client c, InPacket iPacket) {
        final User chr = c.getPlayer();
        if (chr.getMapId() != 910001000 && chr.getMap().getAllMapObjectSize(MapleMapObjectType.EXTRACTOR) <= 0) {
            return; //ardent mill
        }
        final String effect = iPacket.DecodeString();
        final Integer profession = craftingEffects.get(effect);
        if (profession != null && (c.getPlayer().getProfessionLevel(profession) > 0 || (profession == 92040000 && chr.getMap().getAllMapObjectSize(MapleMapObjectType.EXTRACTOR) > 0))) {
            int time = iPacket.DecodeInt();
            if (time > 6000 || time < 3000) {
                time = 4000;
            }
            c.SendPacket(CField.EffectPacket.showWZUOLEffect(effect, chr.getDirection() == 1, -1, time, effect.endsWith("Extract") ? 1 : 0));
            chr.getMap().broadcastMessage(chr, CField.EffectPacket.showWZUOLEffect(effect, chr.getDirection() == 1, chr.getId(), time, effect.endsWith("Extract") ? 1 : 0), false);
        }
    }

}
