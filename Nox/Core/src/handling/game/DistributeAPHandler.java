package handling.game;

import client.MapleClient;
import client.MapleStat;
import client.PlayerStats;
import constants.GameConstants;
import java.util.EnumMap;
import java.util.Map;
import server.Randomizer;
import server.maps.objects.MapleCharacter;
import net.InPacket;
import tools.packet.CWvsContext;
import netty.ProcessPacket;

/**
 *
 * @author
 */
public class DistributeAPHandler implements ProcessPacket<MapleClient> {

    @Override
    public boolean ValidateState(MapleClient c) {
        return true;
    }

    @Override
    public void Process(MapleClient c, InPacket iPacket) {
        MapleCharacter chr = c.getPlayer();

        Map<MapleStat, Long> statupdate = new EnumMap<>(MapleStat.class);
        chr.updateTick(iPacket.DecodeInteger());
        final long statmask = iPacket.DecodeLong();
        final PlayerStats stat = chr.getStat();
        final int job = chr.getJob();
        if (chr.getRemainingAp() > 0) {
            if ((statmask & MapleStat.STR.getValue()) != 0) { // Str
                stat.setStr((short) (stat.getStr() + 1), chr);
                statupdate.put(MapleStat.STR, (long) stat.getStr());
            }
            if ((statmask & MapleStat.DEX.getValue()) != 0) {  // Dex
                stat.setDex((short) (stat.getDex() + 1), chr);
                statupdate.put(MapleStat.DEX, (long) stat.getDex());
            }
            if ((statmask & MapleStat.INT.getValue()) != 0) {  // Int
                stat.setInt((short) (stat.getInt() + 1), chr);
                statupdate.put(MapleStat.INT, (long) stat.getInt());
            }
            if ((statmask & MapleStat.LUK.getValue()) != 0) {  // Luk
                stat.setLuk((short) (stat.getLuk() + 1), chr);
                statupdate.put(MapleStat.LUK, (long) stat.getLuk());
            }
            if ((statmask & MapleStat.MAXHP.getValue()) != 0) { // HP
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
                statupdate.put(MapleStat.MAXHP, (long) maxhp);
            }
            if ((statmask & MapleStat.IndieMMP.getValue()) != 0) {  // MP
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
                statupdate.put(MapleStat.IndieMMP, (long) maxmp);
            }
            chr.setRemainingAp((short) (chr.getRemainingAp() - 1));
            statupdate.put(MapleStat.AVAILABLEAP, (long) chr.getRemainingAp());
            c.write(CWvsContext.updatePlayerStats(statupdate, true, chr));
        }

    }

}
