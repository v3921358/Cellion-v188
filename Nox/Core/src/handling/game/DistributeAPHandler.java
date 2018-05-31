package handling.game;

import client.ClientSocket;
import enums.Stat;
import client.PlayerStats;
import constants.GameConstants;
import java.util.EnumMap;
import java.util.Map;
import server.Randomizer;
import server.maps.objects.User;
import net.InPacket;
import tools.packet.WvsContext;
import net.ProcessPacket;

/**
 *
 * @author
 */
public class DistributeAPHandler implements ProcessPacket<ClientSocket> {

    @Override
    public boolean ValidateState(ClientSocket c) {
        return true;
    }

    @Override
    public void Process(ClientSocket c, InPacket iPacket) {
        User chr = c.getPlayer();

        Map<Stat, Long> statupdate = new EnumMap<>(Stat.class);
        chr.updateTick(iPacket.DecodeInt());
        final long statmask = iPacket.DecodeLong();
        final PlayerStats stat = chr.getStat();
        final int job = chr.getJob();
        if (chr.getRemainingAp() > 0) {
            if ((statmask & Stat.STR.getValue()) != 0) { // Str
                stat.setStr((short) (stat.getStr() + 1), chr);
                statupdate.put(Stat.STR, (long) stat.getStr());
            }
            if ((statmask & Stat.DEX.getValue()) != 0) {  // Dex
                stat.setDex((short) (stat.getDex() + 1), chr);
                statupdate.put(Stat.DEX, (long) stat.getDex());
            }
            if ((statmask & Stat.INT.getValue()) != 0) {  // Int
                stat.setInt((short) (stat.getInt() + 1), chr);
                statupdate.put(Stat.INT, (long) stat.getInt());
            }
            if ((statmask & Stat.LUK.getValue()) != 0) {  // Luk
                stat.setLuk((short) (stat.getLuk() + 1), chr);
                statupdate.put(Stat.LUK, (long) stat.getLuk());
            }
            if ((statmask & Stat.MaxHP.getValue()) != 0) { // HP
                int maxhp = stat.getMaxHp();
                if (chr.getHpApUsed() >= 10000 || maxhp >= 500000) {
                    return;
                }
                if (GameConstants.isBeginnerJob(job)) { // Beginner
                    maxhp += Randomizer.rand(8, 12);
                } else if ((job >= 100 && job <= 132) || (job >= 3200 && job <= 3212) || (job >= 1100 && job <= 1112) || (job >= 3100 && job <= 3112)) { // Warrior
                    maxhp += Randomizer.rand(36, 42);
                } else if ((job >= 200 && job <= 232) || (GameConstants.isEvan(job))) { // Magician
                    maxhp += Randomizer.rand(10, 20);
                } else if ((job >= 300 && job <= 322) || (job >= 400 && job <= 434) || (job >= 1300 && job <= 1312) || (job >= 1400 && job <= 1412) || (job >= 3300 && job <= 3312) || (job >= 2300 && job <= 2312)) { // Bowman
                    maxhp += Randomizer.rand(16, 20);
                } else if ((job >= 510 && job <= 512) || (job >= 1510 && job <= 1512)) {
                    maxhp += Randomizer.rand(28, 32);
                } else if ((job >= 500 && job <= 532) || (job >= 3500 && job <= 3512) || job == 1500) { // Pirate
                    maxhp += Randomizer.rand(18, 22);
                } else if (job >= 1200 && job <= 1212) { // Flame Wizard
                    maxhp += Randomizer.rand(15, 21);
                } else if (job >= 2000 && job <= 2112) { // Aran
                    maxhp += Randomizer.rand(38, 42);
                } else if (GameConstants.isDemonAvenger(job)) { // Demon Avenger
                    maxhp += Randomizer.rand(36, 42);
                } else { // GameMaster
                    maxhp += Randomizer.rand(50, 100);
                }
                maxhp = Math.min(500000, Math.abs(maxhp));
                chr.setHpApUsed((short) (chr.getHpApUsed() + 1));
                stat.setMaxHp(maxhp, chr);
                statupdate.put(Stat.MaxHP, (long) maxhp);
            }
            if ((statmask & Stat.MaxMP.getValue()) != 0) {  // MP
                int maxmp = stat.getMaxMp();
                if (chr.getHpApUsed() >= 10000 || stat.getMaxMp() >= 500000) {
                    return;
                }
                if (GameConstants.isBeginnerJob(job)) { // Beginner
                    maxmp += Randomizer.rand(6, 8);
                } else if (GameConstants.isDemonSlayer(job) || GameConstants.isAngelicBuster(job) || GameConstants.isDemonAvenger(job)) { // Demon, Angelic Buster
                    return;
                } else if ((job >= 200 && job <= 232) || (GameConstants.isEvan(job)) || (job >= 3200 && job <= 3212) || (job >= 1200 && job <= 1212)) { // Magician
                    maxmp += Randomizer.rand(38, 40);
                } else if ((job >= 300 && job <= 322) || (job >= 400 && job <= 434) || (job >= 500 && job <= 532) || (job >= 3200 && job <= 3212) || (job >= 3500 && job <= 3512) || (job >= 1300 && job <= 1312) || (job >= 1400 && job <= 1412) || (job >= 1500 && job <= 1512) || (job >= 2300 && job <= 2312)) { // Bowman
                    maxmp += Randomizer.rand(10, 12);
                } else if ((job >= 100 && job <= 132) || (job >= 1100 && job <= 1112) || (job >= 2000 && job <= 2112)) { // Soul Master
                    maxmp += Randomizer.rand(6, 9);
                } else { // GameMaster
                    maxmp += Randomizer.rand(50, 100);
                }
                maxmp = Math.min(500000, Math.abs(maxmp));
                chr.setHpApUsed((short) (chr.getHpApUsed() + 1));
                stat.setMaxMp(maxmp, chr);
                statupdate.put(Stat.MaxMP, (long) maxmp);
            }
            chr.setRemainingAp((short) (chr.getRemainingAp() - 1));
            statupdate.put(Stat.AP, (long) chr.getRemainingAp());
            c.SendPacket(WvsContext.OnPlayerStatChanged(chr, statupdate));
        }
        
        c.SendPacket(WvsContext.enableActions());
    }

}
