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

import client.CharacterTemporaryStat;
import client.ClientSocket;
import enums.Stat;
import client.Skill;
import client.SkillFactory;
import constants.skills.DualBlade;
import constants.skills.WildHunter;
import server.maps.objects.User;
import net.InPacket;
import tools.packet.CField;
import tools.packet.WvsContext;
import net.ProcessPacket;

/**
 * UserSkillCancelRequest
 * @author Mazen Massoud
 */
public final class BuffCancel implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        final User pPlayer = c.getPlayer();
        int nSourceID = iPacket.DecodeInt();
        Skill pSkill = SkillFactory.getSkill(nSourceID);
        
        if ((pPlayer == null) || (pPlayer.getMap() == null)) {
            return;
        }
        
        if (nSourceID == DualBlade.ASURAS_ANGER) {
            pPlayer.getStat().setHp(0, pPlayer);
            pPlayer.updateSingleStat(Stat.HP, 0);
            pPlayer.getClient().SendPacket(WvsContext.enableActions());
        }
        
        if (pSkill.isChargeSkill()) {
            pPlayer.setKeyDownSkillTime(0L);
            pPlayer.getMap().broadcastPacket(pPlayer, CField.skillCancel(pPlayer, nSourceID), false);
        } else {
            if (pPlayer.hasBuff(CharacterTemporaryStat.RideVehicle)) {
                pPlayer.setUnmountState(true); // Allows for unmounting.
                switch (pSkill.getId()) {
                    case WildHunter.SUMMON_JAGUAR:
                    case WildHunter.SUMMON_JAGUAR_1:
                    case WildHunter.SUMMON_JAGUAR_2:
                    case WildHunter.SUMMON_JAGUAR_3:
                    case WildHunter.SUMMON_JAGUAR_4:
                    case WildHunter.SUMMON_JAGUAR_5:
                    case WildHunter.SUMMON_JAGUAR_6:
                    case WildHunter.SUMMON_JAGUAR_7:
                    case WildHunter.SUMMON_JAGUAR_8:
                    case WildHunter.SUMMON_JAGUAR_9:
                    case WildHunter.SUMMON_JAGUAR_10:
                        pSkill = SkillFactory.getSkill(WildHunter.JAGUAR_RIDER); // Unmount Jaguar for Summon
                        break;
                }
            }
            pPlayer.cancelEffect(pSkill.getEffect(1), false, -1L);
        }
    }
}
