package client;

import client.MapleQuestStatus.MapleQuestState;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import client.inventory.Equip;
import client.inventory.EquipSlotType;
import client.inventory.MapleInventoryType;
import constants.GameConstants;
import constants.ItemConstants;
import database.Database;
import net.OutPacket;
import server.MapleItemInformationProvider;
import server.maps.objects.User;
import server.quest.MapleQuest;
import tools.LogHelper;
import tools.Pair;
import tools.Triple;
import tools.packet.CField;
import tools.packet.CField.EffectPacket.UserEffectCodes;

public final class MonsterBook
        implements Serializable {

    private static final long serialVersionUID = 7179541993413738569L;
    private boolean changed = false;
    private int currentSet = -1;
    private int level = 0;
    private int setScore;
    private int finishedSets;
    private final Map<Integer, Integer> cards;
    private final List<Integer> cardItems = new ArrayList<>();
    private final Map<Integer, Pair<Integer, Boolean>> sets = new HashMap<>();

    public MonsterBook(Map<Integer, Integer> cards, User chr) {
        this.cards = cards;
        calculateItem();
        calculateScore();

        MapleQuestStatus stat = chr.getQuestNoAdd(MapleQuest.getInstance(122800));
        if ((stat != null) && (stat.getCustomData() != null)) {
            this.currentSet = Integer.parseInt(stat.getCustomData());
            if ((!this.sets.containsKey(this.currentSet)) || (!((Boolean) this.sets.get(this.currentSet).right).booleanValue())) {
                this.currentSet = -1;
            }
        }
        applyBook(chr, true);
    }

    public void applyBook(User chr, boolean first_login) {
        Equip item = (Equip) chr.getInventory(MapleInventoryType.EQUIPPED).getItem((short) EquipSlotType.MonsterBook.getSlot());
        if (item == null) {
            item = (Equip) MapleItemInformationProvider.getInstance().getEquipById(ItemConstants.CRUSADER_CODEX);
            item.setPosition((short) EquipSlotType.MonsterBook.getSlot());
        }
        modifyBook(item);
        if (first_login) {
            chr.getInventory(MapleInventoryType.EQUIPPED).addFromDB(item);
        } else {
            chr.forceReAddItemBook(item, MapleInventoryType.EQUIPPED);
            chr.equipChanged();
        }
    }

    public byte calculateScore() {
        byte returnval = 0;
        sets.clear();
        int oldLevel = level, oldSetScore = setScore;
        setScore = 0;
        finishedSets = 0;
        final MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        for (int i : cardItems) {
            //we need the card id but we store the mob id lol
            final Integer x = ii.getSetId(i);
            if (x != null && x > 0) {
                final Triple<Integer, List<Integer>, List<Integer>> set = ii.getMonsterBookInfo(x);
                if (set != null) {
                    if (!sets.containsKey(x)) {
                        Pair<Integer, Boolean> put = sets.put(x, new Pair<>(1, Boolean.FALSE));
                    } else {
                        sets.get(x).left++;
                    }
                    if (sets.get(x).left == set.mid.size()) {
                        sets.get(x).right = Boolean.TRUE;
                        setScore += set.left;
                        if (currentSet == -1) {
                            currentSet = x;
                            returnval = 2;
                        }
                        finishedSets++;
                    }
                }
            }
        }
        level = 10;
        for (byte i = 0; i < 10; i++) {
            if (GameConstants.getSetExpNeededForLevel(i) > setScore) {
                level = (byte) i;
                break;
            }
        }
        if (level > oldLevel) {
            returnval = 2;
        } else if (setScore > oldSetScore) {
            returnval = 1;
        }
        return returnval;
    }

    public void writeCharInfoPacket(OutPacket oPacket) {
        List<Integer> cardSize = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            cardSize.add(0);
        }
        for (Iterator<Integer> i$ = this.cardItems.iterator(); i$.hasNext();) {
            int x = i$.next();
            cardSize.set(0, (cardSize.get(0)) + 1);
            cardSize.set(x / 1000 % 10 + 1, cardSize.get(x / 1000 % 10 + 1) + 1);
        }
        for (Iterator<Integer> i$ = cardSize.iterator(); i$.hasNext();) {
            int i = i$.next();
            oPacket.EncodeInt(i);
        }
        oPacket.EncodeInt(this.setScore);
        oPacket.EncodeInt(this.currentSet);
        oPacket.EncodeInt(this.finishedSets);
    }

    public void writeFinished(OutPacket oPacket) {
        MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
        oPacket.EncodeByte(1);
        oPacket.EncodeShort(this.cardItems.size());
        List<Integer> mbList = new ArrayList<>(ii.getMonsterBookList());
        Collections.sort(mbList);
        int fullCards = mbList.size() / 8 + (mbList.size() % 8 > 0 ? 1 : 0);
        oPacket.EncodeShort(fullCards);

        for (int i = 0; i < fullCards; i++) {
            int currentMask = 1;
            int maskToWrite = 0;
            for (int y = i * 8; (y < i * 8 + 8)
                    && (mbList.size() > y); y++) {
                if (this.cardItems.contains(mbList.get(y))) {
                    maskToWrite |= currentMask;
                }
                currentMask *= 2;
            }
            oPacket.EncodeByte(maskToWrite);
        }

        int fullSize = this.cardItems.size() / 2 + (this.cardItems.size() % 2 > 0 ? 1 : 0);
        oPacket.EncodeShort(fullSize);
        for (int i = 0; i < fullSize; i++) {
            oPacket.EncodeByte(i == this.cardItems.size() / 2 ? 1 : 17);
        }
    }

    public void writeUnfinished(OutPacket oPacket) {
        oPacket.EncodeByte(0);
        oPacket.EncodeShort(this.cardItems.size());
        for (Iterator<Integer> i$ = this.cardItems.iterator(); i$.hasNext();) {
            int i = i$.next();
            oPacket.EncodeShort(i % 10000);
            oPacket.EncodeByte(1);
        }
    }

    public void calculateItem() {
        this.cardItems.clear();
        for (Entry s : this.cards.entrySet()) {
            addCardItem(((Integer) s.getKey()), ((Integer) s.getValue()));
        }
    }

    public void addCardItem(int key, int value) {
        if (value >= 2) {
            Integer x = MapleItemInformationProvider.getInstance().getItemIdByMob(key);
            if ((x != null) && (x.intValue() > 0)) {
                this.cardItems.add(x);
            }
        }
    }

    public void modifyBook(Equip eq) {
        eq.setStr((short) this.level);
        eq.setDex((short) this.level);
        eq.setInt((short) this.level);
        eq.setLuk((short) this.level);
        eq.setPotential1(0);
        eq.setPotential2(0);
        eq.setPotential3(0);

        if (this.currentSet > -1) {
            Triple<?, ?, ?> set = MapleItemInformationProvider.getInstance().getMonsterBookInfo(this.currentSet);
            if (set != null) {
                OUTER:
                for (int i = 0; i < ((List<?>) set.right).size(); i++) {
                    switch (i) {
                        case 0:
                            eq.setPotential1(((Integer) ((List<?>) set.right).get(i)));
                            break;
                        case 1:
                            eq.setPotential2(((Integer) ((List<?>) set.right).get(i)));
                            break;
                        case 2:
                            eq.setPotential3(((Integer) ((List<?>) set.right).get(i)));
                            break;
                        default:
                            if (i == 3) {
                                break OUTER;
                            }
                            break;
                    }
                }
            } else {
                this.currentSet = -1;
            }
        }
    }

    public int getSetScore() {
        return this.setScore;
    }

    public int getLevel() {
        return this.level;
    }

    public int getSet() {
        return this.currentSet;
    }

    public boolean changeSet(int c) {
        if ((this.sets.containsKey(c)) && (((Boolean) this.sets.get(c).right))) {
            this.currentSet = c;
            return true;
        }
        return false;
    }

    public void changed() {
        this.changed = true;
    }

    public Map<Integer, Integer> getCards() {
        return this.cards;
    }

    public final int getSeen() {
        return this.cards.size();
    }

    public final int getCaught() {
        int ret = 0;
        for (Iterator<Integer> i$ = this.cards.values().iterator(); i$.hasNext();) {
            int i = i$.next();
            if (i >= 2) {
                ret++;
            }
        }
        return ret;
    }

    public final int getLevelByCard(int cardid) {
        return this.cards.get(cardid) == null ? 0 : this.cards.get(cardid);
    }

    public static final MonsterBook loadCards(int charid, User chr, Connection con) throws SQLException {
        Map<Integer, Integer> cards;
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM monsterbook WHERE charid = ? ORDER BY cardid ASC")) {
            ps.setInt(1, charid);
            try (ResultSet rs = ps.executeQuery()) {
                cards = new LinkedHashMap<Integer, Integer>();
                while (rs.next()) {
                    cards.put(rs.getInt("cardid"), rs.getInt("level"));
                }
            }
        }
        return new MonsterBook(cards, chr);
    }

    public final void saveCards(int charid) throws SQLException {
        if (!this.changed) {
            return;
        }
        try (Connection con = Database.GetConnection()) {
            System.out.println("[" + Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName() + "] " + Database.GetPoolStats() + " Opening");

            PreparedStatement ps = con.prepareStatement("DELETE FROM monsterbook WHERE charid = ?");
            ps.setInt(1, charid);
            ps.execute();
            ps.close();
            this.changed = false;
            if (this.cards.isEmpty()) {
                return;
            }

            boolean first = true;
            StringBuilder query = new StringBuilder();

            for (Map.Entry all : this.cards.entrySet()) {
                if (first) {
                    first = false;
                    query.append("INSERT INTO monsterbook VALUES (DEFAULT,");
                } else {
                    query.append(",(DEFAULT,");
                }
                query.append(charid);
                query.append(",");
                query.append(all.getKey());
                query.append(",");
                query.append(all.getValue());
                query.append(")");
            }
            ps = con.prepareStatement(query.toString());
            ps.execute();
            ps.close();
        } catch (SQLException e) {
            LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", e);
        }
        System.out.println("[" + Thread.currentThread().getStackTrace()[2].getClassName() + "." + Thread.currentThread().getStackTrace()[2].getMethodName() + "] " + Database.GetPoolStats() + " Closing");

    }

    public final boolean monsterCaught(MapleClient c, int cardid, String cardname) {
        if ((!this.cards.containsKey(cardid)) || (this.cards.get(cardid) < 2)) {
            this.changed = true;
            c.getPlayer().dropMessage(-6, new StringBuilder().append("Book entry updated - ").append(cardname).toString());
            c.SendPacket(CField.EffectPacket.showForeignEffect(UserEffectCodes.MonsterBookCardGet));
            this.cards.put(cardid, 2);

            if (c.getPlayer().getQuestStatus(50195) != MapleQuestState.Started) {
                MapleQuest.getInstance(50195).forceStart(c.getPlayer(), 9010000, "1");
            }
            if (c.getPlayer().getQuestStatus(50196) != MapleQuestState.Started) {
                MapleQuest.getInstance(50196).forceStart(c.getPlayer(), 9010000, "1");
            }
            addCardItem(cardid, 2);
            byte rr = calculateScore();
            if (rr > 0) {
                if (c.getPlayer().getQuestStatus(50197) != MapleQuestState.Started) {
                    MapleQuest.getInstance(50197).forceStart(c.getPlayer(), 9010000, "1");
                }
                c.SendPacket(CField.EffectPacket.showForeignEffect(UserEffectCodes.BiteAttack_ReceiveSuccess));//was43
                if (rr > 1) {
                    applyBook(c.getPlayer(), false);
                }
            }

            return true;
        }
        return false;
    }

    public boolean hasCard(int cardid) {
        return this.cardItems == null ? false : this.cardItems.contains(cardid);
    }

    public final void monsterSeen(MapleClient c, int cardid, String cardname) {
        if (this.cards.containsKey(cardid)) {
            return;
        }
        this.changed = true;

        c.getPlayer().dropMessage(-6, new StringBuilder().append("New book entry - ").append(cardname).toString());
        this.cards.put(cardid, 1);
        c.SendPacket(CField.EffectPacket.showForeignEffect(UserEffectCodes.MonsterBookCardGet));
    }
}
