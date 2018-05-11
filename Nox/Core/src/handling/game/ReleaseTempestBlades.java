/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package handling.game;

import client.Client;
import client.SkillFactory;
import handling.world.PlayerHandler;
import tools.packet.CField;
import tools.packet.CWvsContext;
import net.InPacket;
import net.ProcessPacket;
import client.CharacterTemporaryStat;
import client.jobs.Nova.KaiserHandler;
import constants.skills.Kaiser;
import java.awt.Point;
import java.awt.Rectangle;
import server.MapleStatEffect;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.css.Rect;
import server.life.Mob;
import server.maps.objects.ForceAtom;
import server.maps.objects.ForceAtomType;
import tools.Pair;
import server.maps.objects.User;

/**
 * 
 * @author Mazen Massoud
 */
public final class ReleaseTempestBlades implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return true;
    }

    @Override
    public void Process(Client c, InPacket iPacket) {
        User pPlayer = c.getPlayer();
        int nMaxCount = 3;
        if (KaiserHandler.getTempestBladeSkill(pPlayer) == Kaiser.ADVANCED_TEMPEST_BLADES_1 || KaiserHandler.getTempestBladeSkill(pPlayer) == Kaiser.ADVANCED_TEMPEST_BLADES) {
            nMaxCount = 5;
        }
        int mobCount = iPacket.DecodeInt();
        int lastMobID = 0;
        int nMobID = 0;

        for (int i = 0; i < mobCount; i++) {
            nMobID = iPacket.DecodeInt();

            Mob pMob = pPlayer.getMap().getMonsterByOid(nMobID);
            int nInc = ForceAtomType.KAISER_WEAPON_THROW_1.getInc();
            int nType = ForceAtomType.KAISER_WEAPON_THROW_1.getForceAtomType();

            switch (pPlayer.getBuffedValue(CharacterTemporaryStat.StopForceAtomInfo)) {
                case 3:
                    nInc = ForceAtomType.KAISER_WEAPON_THROW_MORPH_1.getInc();
                    nType = ForceAtomType.KAISER_WEAPON_THROW_MORPH_1.getForceAtomType();
                    break;
                case 2:
                    nInc = ForceAtomType.KAISER_WEAPON_THROW_2.getInc();
                    nType = ForceAtomType.KAISER_WEAPON_THROW_2.getForceAtomType();
                    break;
                case 4:
                    nInc = ForceAtomType.KAISER_WEAPON_THROW_MORPH_2.getInc();
                    nType = ForceAtomType.KAISER_WEAPON_THROW_MORPH_2.getForceAtomType();
                    break;
            }
            
            ForceAtom forceAtomInfo = new ForceAtom(1, nInc, 25, 30, 0, 10 * i, (int) System.currentTimeMillis(), 1, 0, new Point());
            pPlayer.getMap().broadcastMessage(CField.createForceAtom(false, 0, pPlayer.getId(), nType,
                    true, pMob.getObjectId(), KaiserHandler.getTempestBladeSkill(pPlayer), forceAtomInfo, new Rectangle(), 0, 300,
                    pMob.getPosition(), KaiserHandler.getTempestBladeSkill(pPlayer), pMob.getPosition()));

            lastMobID = nMobID;
        }

        for (int i = mobCount; i < nMaxCount; i++) {

            Mob pMob = pPlayer.getMap().getMonsterByOid(lastMobID);
            int nInc = ForceAtomType.KAISER_WEAPON_THROW_1.getInc();
            int nType = ForceAtomType.KAISER_WEAPON_THROW_1.getForceAtomType();

            switch (pPlayer.getBuffedValue(CharacterTemporaryStat.StopForceAtomInfo)) {
                case 3:
                    nInc = ForceAtomType.KAISER_WEAPON_THROW_MORPH_1.getInc();
                    nType = ForceAtomType.KAISER_WEAPON_THROW_MORPH_1.getForceAtomType();
                    break;
                case 2:
                    nInc = ForceAtomType.KAISER_WEAPON_THROW_2.getInc();
                    nType = ForceAtomType.KAISER_WEAPON_THROW_2.getForceAtomType();
                    break;
                case 4:
                    nInc = ForceAtomType.KAISER_WEAPON_THROW_MORPH_2.getInc();
                    nType = ForceAtomType.KAISER_WEAPON_THROW_MORPH_2.getForceAtomType();
                    break;
            }
            
            ForceAtom forceAtomInfo = new ForceAtom(1, nInc, 25, 30, 0, 12 * i, (int) System.currentTimeMillis(), 1, 0, new Point());
            pPlayer.getMap().broadcastMessage(CField.createForceAtom(false, 0, pPlayer.getId(), nType,
                    true, pMob.getObjectId(), KaiserHandler.getTempestBladeSkill(pPlayer), forceAtomInfo, new Rectangle(), 0, 300,
                    pMob.getPosition(), KaiserHandler.getTempestBladeSkill(pPlayer), pMob.getPosition()));
        }
        
        MapleStatEffect pEffect = SkillFactory.getSkill(KaiserHandler.getTempestBladeSkill(pPlayer)).getEffect(pPlayer.getTotalSkillLevel(KaiserHandler.getTempestBladeSkill(pPlayer)));
        pPlayer.cancelEffect(pEffect, true, 0);
        pPlayer.cancelTemporaryStats(CharacterTemporaryStat.StopForceAtomInfo);
    }
}
