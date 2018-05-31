package client;

import enums.Stat;
import java.io.Serializable;

import constants.GameConstants;
import server.maps.objects.User;
import server.messages.TraitMessage;
import tools.packet.WvsContext;

public class Trait {

    public static enum MapleTraitType implements Serializable { //in order

        charisma(500, Stat.CharismaEXP), //ambition
        insight(500, Stat.InsightEXP),
        will(500, Stat.WillEXP), //willpower
        craft(500, Stat.CraftEXP), //diligence
        sense(500, Stat.SenseEXP), //empathy
        charm(5000, Stat.CharmEXP);
        
        final int limit;
        final Stat stat;

        private MapleTraitType(int type, Stat theStat) {
            this.limit = type;
            this.stat = theStat;
        }

        public int getLimit() {
            return limit;
        }

        public Stat getStat() {
            return stat;
        }

        public static MapleTraitType getByQuestName(String q) {
            String qq = q.substring(0, q.length() - 3); //e.g. charmEXP, charmMin
            for (MapleTraitType t : MapleTraitType.values()) {
                if (t.name().equals(qq)) {
                    return t;
                }
            }
            return null;
        }
    }
    private final MapleTraitType type;
    private int totalExp = 0, localTotalExp = 0;
    private short exp = 0;
    private byte level = 0;

    public Trait(MapleTraitType t) {
        this.type = t;
    }

    public void setExp(int e) {
        this.totalExp = e;
        this.localTotalExp = e;
        recalcLevel();
    }

    public void addExp(int e) {
        this.totalExp += e;
        this.localTotalExp += e;
        if (e != 0) {
            recalcLevel();
        }
    }

    public void addExp(int e, User c) {
        addTrueExp((int) (e * c.getClient().getChannelServer().getTraitRate()), c);
    }

    public void addTrueExp(int e, User c) {
        if (e != 0) {
            this.totalExp += e;
            this.localTotalExp += e;
            c.updateSingleStat(type.stat, totalExp);
            TraitMessage t = new TraitMessage(type.getStat().getValue(), e);
            c.write(WvsContext.messagePacket(t));
            recalcLevel();
        }
    }

    public boolean recalcLevel() {
        if (totalExp < 0) {
            totalExp = 0;
            localTotalExp = 0;
            level = 0;
            exp = 0;
            return false;
        }
        final int oldLevel = level;
        for (byte i = 0; i < 100; i++) {
            if (GameConstants.getTraitExpNeededForLevel(i) > localTotalExp) {
                exp = (short) (GameConstants.getTraitExpNeededForLevel(i) - localTotalExp);
                level = (byte) (i - 1);
                return level > oldLevel;
            }
        }
        exp = 0;
        level = 100;
        totalExp = GameConstants.getTraitExpNeededForLevel(level);
        localTotalExp = totalExp;
        return level > oldLevel;
    }

    public int getLevel() {
        return level;
    }

    public int getExp() {
        return exp;
    }

    public int getTotalExp() {
        return totalExp;
    }

    public int getLocalTotalExp() {
        return localTotalExp;
    }

    public void addLocalExp(int e) {
        this.localTotalExp += e;
    }

    public void clearLocalExp() {
        this.localTotalExp = totalExp;
    }

    public MapleTraitType getType() {
        return type;
    }
}
