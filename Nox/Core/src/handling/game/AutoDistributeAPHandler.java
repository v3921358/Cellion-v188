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
        
        Map<Stat, Long> pStatUpdate = new EnumMap<>(Stat.class);
        PlayerStats pCharStat = pPlayer.getStat();
        
        if (pPlayer.getRemainingAp() >= nAmount) {
            if (pStat == Stat.STR) {
                pCharStat.setStr((short) (pCharStat.getStr() + addStat), pPlayer);
                pStatUpdate.put(Stat.STR, Long.valueOf(pCharStat.getStr()));
            }
            if (pStat == Stat.DEX) {
                pCharStat.setDex((short) (pCharStat.getDex() + addStat), pPlayer);
                pStatUpdate.put(Stat.DEX, Long.valueOf(pCharStat.getDex()));
            }
            if (pStat == Stat.INT) {
                pCharStat.setInt((short) (pCharStat.getInt() + addStat), pPlayer);
                pStatUpdate.put(Stat.INT, Long.valueOf(pCharStat.getInt()));
            }
            if (pStat == Stat.LUK) {
                pCharStat.setLuk((short) (pCharStat.getLuk() + addStat), pPlayer);
                pStatUpdate.put(Stat.LUK, Long.valueOf(pCharStat.getLuk()));
            }
            if (pStat == Stat.MaxHP) {
                addStat *= 30;
                if (pCharStat.getMaxHp() + addStat > 500000) {
                    c.SendPacket(WvsContext.enableActions());
                    return;
                }
                pCharStat.setMaxHp(pCharStat.getMaxHp() + addStat, pPlayer);
                pStatUpdate.put(Stat.MaxHP, Long.valueOf(pCharStat.getMaxHp()));
                if (GameConstants.isDemonAvenger(pPlayer.getJob())) {
                    pPlayer.setHpApUsed((short) (pPlayer.getHpApUsed() + pPlayer.getRemainingAp()));
                }
            }
            
            pPlayer.setRemainingAp(pPlayer.getRemainingAp() - nAmount);
            pStatUpdate.put(Stat.AP, (long) pPlayer.getRemainingAp());
            c.SendPacket(WvsContext.OnPlayerStatChanged(pPlayer, pStatUpdate));
            c.SendPacket(WvsContext.enableActions());
        }
    }
}
