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
import server.Randomizer;

/**
 *
 * @author
 */
public class AutoDistributeAPHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        User chr = c.getPlayer();

        chr.updateTick(iPacket.DecodeInt());
        iPacket.DecodeInt();
        long primaryStat = iPacket.DecodeLong();
        int amount = iPacket.DecodeInt();
        long secondaryStat = 0;
        int amount2 = 0;

        PlayerStats playerst = chr.getStat();
        Map<Stat, Long> statupdate = new EnumMap<>(Stat.class);

        if (!GameConstants.isXenon(chr.getJob())) {
            secondaryStat = iPacket.DecodeLong();
            amount2 = iPacket.DecodeInt();
        }

        if (GameConstants.isDemonAvenger(chr.getJob()) && Stat.MaxHP.getValue() != 0) {
            if (chr.getRemainingAp() >= amount + amount2) {
                int maxhp = playerst.getMaxHp();
                if (chr.getHpApUsed() >= 10000 || maxhp >= 500000) {
                    return;
                }
                maxhp += Randomizer.rand(36, 42) * chr.getRemainingAp();
                maxhp = Math.min(500000, Math.abs(maxhp));
                chr.setHpApUsed((short) (chr.getHpApUsed() + chr.getRemainingAp()));
                playerst.setMaxHp(maxhp, chr);
                statupdate.put(Stat.MaxHP, (long) maxhp);

                chr.setRemainingAp((short) (chr.getRemainingAp() - (amount)));
                statupdate.put(Stat.AP, (long) chr.getRemainingAp());

                c.SendPacket(WvsContext.OnPlayerStatChanged(chr, statupdate));
            }
        }

        if ((amount < 0) || (amount2 < 0)) {
            return;
        }

        if (chr.getRemainingAp() >= amount + amount2) {
            if ((primaryStat & Stat.STR.getValue()) != 0) {
                playerst.setStr((short) (playerst.getStr() + amount), chr);
                statupdate.put(Stat.STR, Long.valueOf(playerst.getStr()));
            }
            if ((primaryStat & Stat.DEX.getValue()) != 0) {
                playerst.setDex((short) (playerst.getDex() + amount), chr);
                statupdate.put(Stat.DEX, Long.valueOf(playerst.getDex()));
            }
            if ((primaryStat & Stat.INT.getValue()) != 0) {
                playerst.setInt((short) (playerst.getInt() + amount), chr);
                statupdate.put(Stat.INT, Long.valueOf(playerst.getInt()));
            }
            if ((primaryStat & Stat.LUK.getValue()) != 0) {
                playerst.setLuk((short) (playerst.getLuk() + amount), chr);
                statupdate.put(Stat.LUK, Long.valueOf(playerst.getLuk()));
            }
            if ((primaryStat & Stat.MaxHP.getValue()) != 0) {
                if (playerst.getMaxHp() + (amount * 30) > 500000) {
                    return;
                }
                playerst.setMaxHp((short) (playerst.getMaxHp() + amount * 30), chr);
                statupdate.put(Stat.MaxHP, Long.valueOf(playerst.getMaxHp()));
            }
            if ((secondaryStat & Stat.STR.getValue()) != 0) {
                playerst.setStr((short) (playerst.getStr() + amount2), chr);
                statupdate.put(Stat.STR, Long.valueOf(playerst.getStr()));
            }
            if ((secondaryStat & Stat.DEX.getValue()) != 0) {
                playerst.setDex((short) (playerst.getDex() + amount2), chr);
                statupdate.put(Stat.DEX, Long.valueOf(playerst.getDex()));
            }
            if ((secondaryStat & Stat.INT.getValue()) != 0) {
                playerst.setInt((short) (playerst.getInt() + amount2), chr);
                statupdate.put(Stat.INT, Long.valueOf(playerst.getInt()));
            }
            if ((secondaryStat & Stat.LUK.getValue()) != 0) {
                playerst.setLuk((short) (playerst.getLuk() + amount2), chr);
                statupdate.put(Stat.LUK, Long.valueOf(playerst.getLuk()));
            }
            if ((secondaryStat & Stat.MaxHP.getValue()) != 0) {
                if (playerst.getMaxHp() + (amount2 * 30) > 500000) {
                    return;
                }
                playerst.setMaxHp((short) (playerst.getMaxHp() + amount2 * 30), chr);
                statupdate.put(Stat.MaxHP, Long.valueOf(playerst.getMaxHp()));
            }
            chr.setRemainingAp((short) (chr.getRemainingAp() - (amount + amount2)));
            statupdate.put(Stat.AP, (long) chr.getRemainingAp());
            c.SendPacket(WvsContext.OnPlayerStatChanged(chr, statupdate));
        }
    }

}
