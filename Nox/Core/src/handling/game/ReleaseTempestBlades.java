/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.game;

import client.MapleClient;
import client.SkillFactory;
import handling.world.PlayerHandler;
import tools.packet.CField;
import tools.packet.CWvsContext;
import net.InPacket;
import netty.ProcessPacket;
import client.CharacterTemporaryStat;
import server.MapleStatEffect;
import java.util.ArrayList;
import java.util.List;
import tools.Pair;
import server.maps.objects.User;

/**
 *
 * @author Mazen
 */
public final class ReleaseTempestBlades implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        final User chr = c.getPlayer();
        if (!chr.isAlive()) {
            chr.getClient().write(CWvsContext.enableActions());
            return;
        }
        final int mobcount = iPacket.DecodeInteger();
        final List<Integer> oids = new ArrayList<>();
        for (int i = 0; i < mobcount; i++) {
            oids.add(iPacket.DecodeInteger());
        }
        int skillid = chr.getBuffSource(CharacterTemporaryStat.StopForceAtomInfo);

        List<Pair<Integer, Integer>> forceinfo = new ArrayList<>();
        boolean advanced = skillid == 61120007 || skillid == 61121217;
        boolean transform = skillid == 61110211 || skillid == 61121217;
        for (int i = 0; i < (advanced ? 5 : 3); i++) {
            forceinfo.add(new Pair<>(i + 2, transform ? 4 : 2));
        }
        chr.getMap().broadcastMessage(CField.gainForce(false, chr, oids, 2, skillid, forceinfo, null, 0));

        MapleStatEffect skill_effect = SkillFactory.getSkill(skillid).getEffect(chr.getTotalSkillLevel(skillid));
        chr.cancelEffect(skill_effect, true, 0);
        chr.cancelTemporaryStats(CharacterTemporaryStat.StopForceAtomInfo);
    }
}
