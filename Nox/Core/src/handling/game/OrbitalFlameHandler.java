/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
		       Matthias Butz <matze@odinms.de>
		       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation version 3 as published by
    the Free Software Foundation. You may not use, modify or distribute
    this program under any other version of the GNU Affero General Public
    License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package handling.game;

import client.ClientSocket;
import client.Skill;
import client.SkillFactory;
import constants.skills.BlazeWizard;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import server.maps.objects.User;
import net.InPacket;
import net.ProcessPacket;
import server.StatEffect;
import enums.StatInfo;
import server.maps.objects.ForceAtom;
import enums.ForceAtomType;
import tools.packet.CField;

public final class OrbitalFlameHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        User pPlayer = c.getPlayer();
        int nSkillID = iPacket.DecodeInt();
        byte nSLV = iPacket.DecodeByte();
        short nDirection = iPacket.DecodeShort();
        Skill pSkill = SkillFactory.getSkill(nSkillID);
        StatEffect pEffect = pSkill.getEffect(nSLV);
        int nRange = pEffect.info.get(StatInfo.range);

        ForceAtomType fae;
        switch (nSkillID) {
            case BlazeWizard.FINAL_ORBITAL_FLAME:
                fae = ForceAtomType.ORBITAL_FLAME_4;
                nSkillID = BlazeWizard.FINAL_ORBITAL_FLAME_1;
                break;
            case BlazeWizard.GRAND_ORBITAL_FLAME:
                fae = ForceAtomType.ORBITAL_FLAME_3;
                nSkillID = BlazeWizard.GRAND_ORBITAL_FLAME_1;
                break;
            case BlazeWizard.GREATER_ORBITAL_FLAME:
                fae = ForceAtomType.ORBITAL_FLAME_2;
                nSkillID = BlazeWizard.GREATER_ORBITAL_FLAME_1;
                break;
            default:
                fae = ForceAtomType.ORBITAL_FLAME_1;
                nSkillID = BlazeWizard.ORBITAL_FLAME_1;
                break;
        }
        int curTime = (int) System.currentTimeMillis();
        int angle = 0;
        switch (nDirection) {
            case 1:
                angle = 180;
                break;
            case 2:
                angle = 270;
                break;
            case 3:
                angle = 90;
                break;
        }
        ForceAtom fai = new ForceAtom(1, fae.getInc(), 11, 13, angle, 0, curTime, pEffect.info.get(StatInfo.mobCount), BlazeWizard.ORBITAL_FLAME_1, new Point(0, 0));
        List<ForceAtom> faiList = new ArrayList<>();
        faiList.add(fai);
        pPlayer.getMap().broadcastPacket(CField.createForceAtom(false, 0, pPlayer.getId(), fae.getForceAtomType(), false,
                new ArrayList<>(), nSkillID, faiList, null, nDirection, nRange, null, 0, null));

        //PlayerHandler.OrbitalFlame(iPacket, c);
    }
}
