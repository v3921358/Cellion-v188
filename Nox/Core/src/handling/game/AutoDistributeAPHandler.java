package handling.game;

import client.Client;
import client.MapleStat;
import client.PlayerStats;
import constants.GameConstants;
import java.util.EnumMap;
import java.util.Map;
import server.maps.objects.User;
import net.InPacket;
import tools.packet.CWvsContext;
import net.ProcessPacket;
import server.Randomizer;

/**
 *
 * @author
 */
public class AutoDistributeAPHandler implements ProcessPacket<Client> {

    @Override
    public boolean ValidateState(Client c) {
        return true;
    }

    @Override
    public void Process(Client c, InPacket iPacket) {
        User chr = c.getPlayer();

        chr.updateTick(iPacket.DecodeInt());
        iPacket.DecodeInt();
        long primaryStat = iPacket.DecodeLong();
        int amount = iPacket.DecodeInt();
        long secondaryStat = 0;
        int amount2 = 0;

        PlayerStats playerst = chr.getStat();
        Map<MapleStat, Long> statupdate = new EnumMap<>(MapleStat.class);

        if (!GameConstants.isXenon(chr.getJob())) {
            secondaryStat = iPacket.DecodeLong();
            amount2 = iPacket.DecodeInt();
        }

        if (GameConstants.isDemonAvenger(chr.getJob()) && MapleStat.MAXHP.getValue() != 0) {
            if (chr.getRemainingAp() >= amount + amount2) {
                int maxhp = playerst.getMaxHp();
                if (chr.getHpApUsed() >= 10000 || maxhp >= 500000) {
                    return;
                }
                maxhp += Randomizer.rand(36, 42) * chr.getRemainingAp();
                maxhp = Math.min(500000, Math.abs(maxhp));
                chr.setHpApUsed((short) (chr.getHpApUsed() + chr.getRemainingAp()));
                playerst.setMaxHp(maxhp, chr);
                statupdate.put(MapleStat.MAXHP, (long) maxhp);

                chr.setRemainingAp((short) (chr.getRemainingAp() - (amount)));
                statupdate.put(MapleStat.AVAILABLEAP, (long) chr.getRemainingAp());

                c.SendPacket(CWvsContext.updatePlayerStats(statupdate, true, chr));
            }
        }

        if ((amount < 0) || (amount2 < 0)) {
            return;
        }

        if (chr.getRemainingAp() >= amount + amount2) {
            if ((primaryStat & MapleStat.STR.getValue()) != 0) {
                playerst.setStr((short) (playerst.getStr() + amount), chr);
                statupdate.put(MapleStat.STR, Long.valueOf(playerst.getStr()));
            }
            if ((primaryStat & MapleStat.DEX.getValue()) != 0) {
                playerst.setDex((short) (playerst.getDex() + amount), chr);
                statupdate.put(MapleStat.DEX, Long.valueOf(playerst.getDex()));
            }
            if ((primaryStat & MapleStat.INT.getValue()) != 0) {
                playerst.setInt((short) (playerst.getInt() + amount), chr);
                statupdate.put(MapleStat.INT, Long.valueOf(playerst.getInt()));
            }
            if ((primaryStat & MapleStat.LUK.getValue()) != 0) {
                playerst.setLuk((short) (playerst.getLuk() + amount), chr);
                statupdate.put(MapleStat.LUK, Long.valueOf(playerst.getLuk()));
            }
            if ((primaryStat & MapleStat.MAXHP.getValue()) != 0) {
                if (playerst.getMaxHp() + (amount * 30) > 500000) {
                    return;
                }
                playerst.setMaxHp((short) (playerst.getMaxHp() + amount * 30), chr);
                statupdate.put(MapleStat.MAXHP, Long.valueOf(playerst.getMaxHp()));
            }
            if ((secondaryStat & MapleStat.STR.getValue()) != 0) {
                playerst.setStr((short) (playerst.getStr() + amount2), chr);
                statupdate.put(MapleStat.STR, Long.valueOf(playerst.getStr()));
            }
            if ((secondaryStat & MapleStat.DEX.getValue()) != 0) {
                playerst.setDex((short) (playerst.getDex() + amount2), chr);
                statupdate.put(MapleStat.DEX, Long.valueOf(playerst.getDex()));
            }
            if ((secondaryStat & MapleStat.INT.getValue()) != 0) {
                playerst.setInt((short) (playerst.getInt() + amount2), chr);
                statupdate.put(MapleStat.INT, Long.valueOf(playerst.getInt()));
            }
            if ((secondaryStat & MapleStat.LUK.getValue()) != 0) {
                playerst.setLuk((short) (playerst.getLuk() + amount2), chr);
                statupdate.put(MapleStat.LUK, Long.valueOf(playerst.getLuk()));
            }
            if ((secondaryStat & MapleStat.MAXHP.getValue()) != 0) {
                if (playerst.getMaxHp() + (amount2 * 30) > 500000) {
                    return;
                }
                playerst.setMaxHp((short) (playerst.getMaxHp() + amount2 * 30), chr);
                statupdate.put(MapleStat.MAXHP, Long.valueOf(playerst.getMaxHp()));
            }
            chr.setRemainingAp((short) (chr.getRemainingAp() - (amount + amount2)));
            statupdate.put(MapleStat.AVAILABLEAP, (long) chr.getRemainingAp());
            c.SendPacket(CWvsContext.updatePlayerStats(statupdate, true, chr));
        }
    }

}
