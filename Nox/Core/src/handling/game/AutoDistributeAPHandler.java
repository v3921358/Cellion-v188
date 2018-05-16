package handling.game;

import client.ClientSocket;
import client.Stat;
import client.PlayerStats;
import constants.GameConstants;
import java.util.EnumMap;
import java.util.Map;
import server.maps.objects.User;
import net.InPacket;
import tools.packet.WvsContext;
import net.ProcessPacket;

/**
 * UserAbilityMassUpRequest
 * @author Mazen Massoud
 */
public class AutoDistributeAPHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        User pPlayer = c.getPlayer();
        pPlayer.updateTick(iPacket.DecodeInt()); // tTick
        int nType = iPacket.DecodeInt();
        Stat pStat = null;
        
        if (nType == 1) {
            pStat = Stat.getByValue(iPacket.DecodeShort());
        } else if (nType == 2) {
            iPacket.DecodeInt();
            iPacket.DecodeInt();
            iPacket.DecodeInt();
            pStat = Stat.getByValue(iPacket.DecodeShort());
        }
        
        iPacket.DecodeInt();
        iPacket.DecodeShort();
        short nAmount = iPacket.DecodeShort();
        short addStat = nAmount;
        
        if (pPlayer.getRemainingAp() < nAmount) {
            return;
        }
        
        if (pStat == Stat.MaxMP || pStat == Stat.MaxHP) {
            addStat *= 20;
            
            if (GameConstants.isDemonAvenger(pPlayer.getJob())) {
                pPlayer.setHpApUsed((short) (pPlayer.getHpApUsed() + pPlayer.getRemainingAp()));
            }
        }
        
        Map<Stat, Long> pStatUpdate = new EnumMap<>(Stat.class);
        PlayerStats pCharStat = pPlayer.getStat();
        
        if (pPlayer.getRemainingAp() >= nAmount) {
            if (pStat == Stat.STR) {
                pCharStat.setStr((short) (pCharStat.getStr() + nAmount), pPlayer);
                pStatUpdate.put(Stat.STR, Long.valueOf(pCharStat.getStr()));
            }
            if (pStat == Stat.DEX) {
                pCharStat.setDex((short) (pCharStat.getDex() + nAmount), pPlayer);
                pStatUpdate.put(Stat.DEX, Long.valueOf(pCharStat.getDex()));
            }
            if (pStat == Stat.INT) {
                pCharStat.setInt((short) (pCharStat.getInt() + nAmount), pPlayer);
                pStatUpdate.put(Stat.INT, Long.valueOf(pCharStat.getInt()));
            }
            if (pStat == Stat.LUK) {
                pCharStat.setLuk((short) (pCharStat.getLuk() + nAmount), pPlayer);
                pStatUpdate.put(Stat.LUK, Long.valueOf(pCharStat.getLuk()));
            }
            if (pStat == Stat.MaxHP) {
                if (pCharStat.getMaxHp() + (nAmount * 30) > 500000) {
                    return;
                }
                pCharStat.setMaxHp((short) (pCharStat.getMaxHp() + nAmount * 30), pPlayer);
                pStatUpdate.put(Stat.MaxHP, Long.valueOf(pCharStat.getMaxHp()));
            }
            
            pPlayer.setRemainingAp(pPlayer.getRemainingAp() - nAmount);
            //pStatUpdate.put(pStat, (long) addStat);
            pStatUpdate.put(Stat.AP, (long) pPlayer.getRemainingAp());
            c.SendPacket(WvsContext.OnPlayerStatChanged(pPlayer, pStatUpdate));
            c.SendPacket(WvsContext.enableActions());
        }
    }
}
