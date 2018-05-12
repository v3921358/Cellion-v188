package handling.world;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import constants.*;
import client.CharacterTemporaryStat;
import client.ClientSocket;
import client.MapleCoolDownValueHolder;
import client.MapleDiseaseValueHolder;
import client.Stat;
import client.MonsterStatusEffect;
import client.buddy.Buddy;
import client.buddy.BuddyFlags;
import client.buddy.BuddyList;
import client.buddy.BuddyOperation;
import client.buddy.BuddyResult;
import client.buddy.BuddylistEntry;
import client.inventory.MapleInventoryType;
import client.inventory.PetDataFactory;
import constants.WorldConstants.WorldOption;
import constants.skills.BattleMage;
import constants.skills.DemonSlayer;
import constants.skills.Mihile;
import database.Database;
import handling.game.PlayerStorage;
import net.OutPacket;
import service.CashShopServer;
import service.ChannelServer;
import service.FarmServer;

import server.Timer.WorldTimer;
import server.life.Mob;
import server.maps.MapleMap;
import server.maps.MapleMapItem;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.objects.User;
import server.maps.objects.Pet;
import tools.CollectionUtil;
import tools.LogHelper;
import tools.Pair;
import tools.packet.CField;
import tools.packet.WvsContext;
import tools.packet.WvsContext.AlliancePacket;
import tools.packet.WvsContext.ExpeditionPacket;
import tools.packet.WvsContext.GuildPacket;
import tools.packet.WvsContext.PartyPacket;
import tools.packet.PetPacket;

public class World {

    //Touch everything...
    public static void init() {
        World.Find.findChannel(0);
        World.Alliance.lock.toString();
        World.Messenger.getMessenger(0);
        World.Party.getParty(0);
    }

    /*Server Automated Save Handling*/
    private static long nLastSaveTime;
    private static long nSaveInterval = 15 * 1000 * 60; // Converts minutes to milliseconds.

    public static void saveAllCharacters() {
        for (int i = 1; i <= ChannelServer.getChannelCount(); i++) {
            for (ChannelServer cServer : ChannelServer.getAllInstances()) {
                for (User oPlayer : cServer.getPlayerStorage().getAllCharacters()) {
                    oPlayer.saveToDB(false, false);
                    if (oPlayer.isGM()) {
                        oPlayer.yellowMessage("[Server] All character data has automatically been saved.");
                    }
                }
            }
        }
        nLastSaveTime = System.currentTimeMillis();
    }

    public static boolean saveInterval() {
        if (System.currentTimeMillis() > nLastSaveTime + nSaveInterval) {
            return true;
        }
        return false;
    }

    /*End of Automated Save Handling*/
    public static String getStatus() {
        StringBuilder ret = new StringBuilder();
        int totalUsers = 0;
        for (ChannelServer cs : ChannelServer.getAllInstances()) {
            ret.append("Channel ");
            ret.append(cs.getChannel());
            ret.append(": ");
            int channelUsers = cs.getConnectedClients();
            totalUsers += channelUsers;
            ret.append(channelUsers);
            ret.append(" users\n");
        }
        ret.append("Total users online: ");
        ret.append(totalUsers);
        ret.append("\n");
        return ret.toString();
    }

    public static Map<Integer, Integer> getConnected() {
        Map<Integer, Integer> ret = new HashMap<>();
        int total = 0;
        for (ChannelServer cs : ChannelServer.getAllInstances()) {
            int curConnected = cs.getConnectedClients();
            ret.put(cs.getChannel(), curConnected);
            total += curConnected;
        }
        ret.put(0, total);
        return ret;
    }

    public static List<CheaterData> getCheaters() {
        List<CheaterData> allCheaters = new ArrayList<>();
        for (ChannelServer cs : ChannelServer.getAllInstances()) {
            allCheaters.addAll(cs.getCheaters());
        }
        Collections.sort(allCheaters);
        return CollectionUtil.copyFirst(allCheaters, 20);
    }

    public static List<CheaterData> getReports() {
        List<CheaterData> allCheaters = new ArrayList<>();
        for (ChannelServer cs : ChannelServer.getAllInstances()) {
            allCheaters.addAll(cs.getReports());
        }
        Collections.sort(allCheaters);
        return CollectionUtil.copyFirst(allCheaters, 20);
    }

    public static boolean isConnected(String charName) {
        return Find.findChannel(charName) > 0;
    }

    public static void toggleMegaphoneMuteState() {
        for (ChannelServer cs : ChannelServer.getAllInstances()) {
            cs.toggleMegaphoneMuteState();
        }
    }

    public static void changeChannelData(CharacterTransfer Data, int characterid, int toChannel) {
        getStorage(toChannel).registerPendingPlayer(Data, characterid);
    }

    public static boolean isCharacterListConnected(List<String> charName) {
        for (ChannelServer cs : ChannelServer.getAllInstances()) {
            for (final String c : charName) {
                if (cs.getPlayerStorage().getCharacterByName(c) != null) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean hasMerchant(int accountID, int characterID) {
        for (ChannelServer cs : ChannelServer.getAllInstances()) {
            if (cs.containsMerchant(accountID, characterID)) {
                return true;
            }
        }
        return false;
    }

    public static PlayerStorage getStorage(int channel) {
        if (channel == -10) {
            return CashShopServer.getPlayerStorage();
        } else if (channel == -30) {
            return FarmServer.getPlayerStorage();
        }
        return ChannelServer.getInstance(channel).getPlayerStorage();
    }

    public static boolean isChannelAvailable(final int ch, final int server) {
        if (ChannelServer.getInstance(ch) == null || ChannelServer.getInstance(ch).getPlayerStorage() == null) {
            return false;
        }
        if (WorldOption.getById(server).getChannelCount() < ch) {
            return false;
        }
        return ChannelServer.getInstance(ch).getPlayerStorage().getConnectedClients() < (ch == 1 ? 600 : 400);
    }

    public static class Party {

        private static final Map<Integer, MapleParty> parties = new HashMap<>();
        private static final Map<Integer, MapleExpedition> expeds = new HashMap<>();
        private static final Map<PartySearchType, List<PartySearch>> searches = new EnumMap<>(PartySearchType.class);
        private static final AtomicInteger runningPartyId = new AtomicInteger(1), runningExpedId = new AtomicInteger(1);

        static {
            try (Connection con = Database.GetConnection()) {

                try (PreparedStatement ps = con.prepareStatement("UPDATE characters SET party = -1, fatigue = 0")) {
                    ps.executeUpdate();
                } catch (SQLException e) {
                    LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", e);
                }
            } catch (SQLException e) {
                LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", e);
            }

            for (PartySearchType pst : PartySearchType.values()) {
                searches.put(pst, new ArrayList<PartySearch>()); //according to client, max 10, even though theres page numbers ?!
            }
        }

        public static void partyChat(int partyid, String chattext, String namefrom) {
            partyChat(partyid, chattext, namefrom, 1);
        }

        public static void expedChat(int expedId, String chattext, String namefrom) {
            MapleExpedition party = getExped(expedId);
            if (party == null) {
                return;
            }
            for (int i : party.getParties()) {
                partyChat(i, chattext, namefrom, 4);
            }
        }

        public static void expedPacket(int expedId, OutPacket packet, MaplePartyCharacter exception) {
            MapleExpedition party = getExped(expedId);
            if (party == null) {
                return;
            }
            for (int i : party.getParties()) {
                partyPacket(i, packet, exception);
            }
        }

        public static void partyPacket(int partyid, OutPacket oPacket, MaplePartyCharacter exception) {
            MapleParty party = getParty(partyid);
            if (party == null) {
                return;
            }

            short nPacketID = oPacket.nPacketID;
            byte[] aData = oPacket.CloneData();

            for (MaplePartyCharacter partychar : party.getMembers()) {
                int ch = Find.findChannel(partychar.getName());
                if (ch > 0 && (exception == null || partychar.getId() != exception.getId())) {
                    User chr = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(partychar.getName());
                    if (chr != null) { //Extra check just in case
                        chr.getClient().SendPacket((new OutPacket(nPacketID)).Encode(aData));
                    }
                }
            }
        }

        public static void partyChat(int partyid, String chattext, String namefrom, int mode) {
            MapleParty party = getParty(partyid);
            if (party == null) {
                return;
            }

            for (MaplePartyCharacter partychar : party.getMembers()) {
                int ch = Find.findChannel(partychar.getName());
                if (ch > 0) {
                    User chr = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(partychar.getName());
                    if (chr != null && !chr.getName().equalsIgnoreCase(namefrom)) { //Extra check just in case
                        //chr.getClient().write(CField.multiChat(namefrom, chattext, mode));
                        if (chr.getClient().isMonitored()) {
                            World.Broadcast.broadcastGMMessage(WvsContext.broadcastMsg(6, "[GM Message] " + namefrom + " said to " + chr.getName() + " (Party): " + chattext));
                        }
                    }
                }
            }
        }

        public static void partyMessage(int partyid, String chattext) {
            MapleParty party = getParty(partyid);
            if (party == null) {
                return;
            }

            for (MaplePartyCharacter partychar : party.getMembers()) {
                int ch = Find.findChannel(partychar.getName());
                if (ch > 0) {
                    User chr = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(partychar.getName());
                    if (chr != null) { //Extra check just in case
                        chr.dropMessage(5, chattext);
                    }
                }
            }
        }

        public static void expedMessage(int expedId, String chattext) {
            MapleExpedition party = getExped(expedId);
            if (party == null) {
                return;
            }
            for (int i : party.getParties()) {
                partyMessage(i, chattext);
            }
        }

        public static void updateParty(int partyid, PartyOperation operation, MaplePartyCharacter target) {
            MapleParty party = getParty(partyid);
            if (party == null) {
                return; //Don't update, just return. And definitely don't throw a damn exception.
                //throw new IllegalArgumentException("no party with the specified partyid exists");
            }
            final int oldExped = party.getExpeditionId();
            int oldInd = -1;
            if (oldExped > 0) {
                MapleExpedition exped = getExped(oldExped);
                if (exped != null) {
                    oldInd = exped.getIndex(partyid);
                }
            }
            switch (operation) {
                case JOIN:
                    party.addMember(target);
                    if (party.getMembers().size() >= 6) {
                        PartySearch toRemove = getSearchByParty(partyid);
                        if (toRemove != null) {
                            removeSearch(toRemove, "The Party Listing was removed because the party is full.");
                        } else if (party.getExpeditionId() > 0) {
                            MapleExpedition exped = getExped(party.getExpeditionId());
                            if (exped != null && exped.getAllMembers() >= exped.getType().maxMembers) {
                                toRemove = getSearchByExped(exped.getId());
                                if (toRemove != null) {
                                    removeSearch(toRemove, "The Party Listing was removed because the party is full.");
                                }
                            }
                        }
                    }
                    break;
                case EXPEL:
                case LEAVE:
                    System.out.println("hey");
                    party.removeMember(target);
                    break;
                case DISBAND:
                    disbandParty(partyid);
                    break;
                case SILENT_UPDATE:
                case LOG_ONOFF:
                    party.updateMember(target);
                    break;
                case CHANGE_LEADER:
                case CHANGE_LEADER_DC:
                    party.setLeader(target);
                    break;
                default:
                    throw new RuntimeException("Unhandeled updateParty operation " + operation.name());
            }
            if (operation == PartyOperation.LEAVE || operation == PartyOperation.EXPEL) {
                int chz = Find.findChannel(target.getName());
                if (chz > 0) {
                    User chr = getStorage(chz).getCharacterByName(target.getName());
                    if (chr != null) {
                        chr.setParty(null);
                        if (oldExped > 0) {
                            chr.getClient().SendPacket(ExpeditionPacket.expeditionMessage(80));
                        }
                        chr.getClient().SendPacket(PartyPacket.updateParty(chr.getClient().getChannel(), party, operation, target));
                    }
                }
                if (target.getId() == party.getLeader().getId() && party.getMembers().size() > 0) { //pass on lead
                    MaplePartyCharacter lchr = null;
                    for (MaplePartyCharacter pchr : party.getMembers()) {
                        if (pchr != null && (lchr == null || lchr.getLevel() < pchr.getLevel())) {
                            lchr = pchr;
                        }
                    }
                    if (lchr != null) {
                        updateParty(partyid, PartyOperation.CHANGE_LEADER_DC, lchr);
                    }
                }
            }
            if (party.getMembers().size() <= 0) { //no members left, plz disband
                disbandParty(partyid);
            }
            for (MaplePartyCharacter partychar : party.getMembers()) {
                if (partychar == null) {
                    continue;
                }
                int ch = Find.findChannel(partychar.getName());
                if (ch > 0) {
                    User chr = getStorage(ch).getCharacterByName(partychar.getName());
                    if (chr != null) {
                        if (operation == PartyOperation.DISBAND) {
                            chr.setParty(null);
                            if (oldExped > 0) {
                                chr.getClient().SendPacket(ExpeditionPacket.expeditionMessage(79));//83
                            }
                        } else {
                            chr.setParty(party);
                        }
                        chr.getClient().SendPacket(PartyPacket.updateParty(chr.getClient().getChannel(), party, operation, target));
                    }
                }
            }
            if (oldExped > 0) {
                expedPacket(oldExped, ExpeditionPacket.expeditionUpdate(oldInd, party), operation == PartyOperation.LOG_ONOFF || operation == PartyOperation.SILENT_UPDATE ? target : null);
            }
        }

        public static MapleParty createParty(MaplePartyCharacter leader, String partyName, boolean isPrivate) {
            MapleParty party = new MapleParty(runningPartyId.getAndIncrement(), leader, partyName, isPrivate);
            parties.put(party.getId(), party);
            return party;
        }

        public static MapleParty createParty(MaplePartyCharacter chrfor, int expedId) {
            ExpeditionType ex = ExpeditionType.getById(expedId);
            MapleParty party = new MapleParty(runningPartyId.getAndIncrement(), chrfor, ex != null ? runningExpedId.getAndIncrement() : -1);
            parties.put(party.getId(), party);
            if (ex != null) {
                final MapleExpedition exp = new MapleExpedition(ex, chrfor.getId(), party.getExpeditionId());
                exp.getParties().add(party.getId());
                expeds.put(party.getExpeditionId(), exp);
            }
            return party;
        }

        public static MapleParty createPartyAndAdd(MaplePartyCharacter chrfor, int expedId) {
            MapleExpedition ex = getExped(expedId);
            if (ex == null) {
                return null;
            }
            MapleParty party = new MapleParty(runningPartyId.getAndIncrement(), chrfor, expedId);
            parties.put(party.getId(), party);
            ex.getParties().add(party.getId());
            return party;
        }

        public static MapleParty getParty(int partyid) {
            return parties.get(partyid);
        }

        public static MapleExpedition getExped(int partyid) {
            return expeds.get(partyid);
        }

        public static MapleExpedition disbandExped(int partyid) {
            PartySearch toRemove = getSearchByExped(partyid);
            if (toRemove != null) {
                removeSearch(toRemove, "The Party Listing was removed because the party disbanded.");
            }
            final MapleExpedition ret = expeds.remove(partyid);
            if (ret != null) {
                for (int p : ret.getParties()) {
                    MapleParty pp = getParty(p);
                    if (pp != null) {
                        updateParty(p, PartyOperation.DISBAND, pp.getLeader());
                    }
                }
            }
            return ret;
        }

        public static MapleParty disbandParty(int partyid) {
            PartySearch toRemove = getSearchByParty(partyid);
            if (toRemove != null) {
                removeSearch(toRemove, "The Party Listing was removed because the party disbanded.");
            }
            final MapleParty ret = parties.remove(partyid);
            if (ret == null) {
                return null;
            }
            if (ret.getExpeditionId() > 0) {
                MapleExpedition me = getExped(ret.getExpeditionId());
                if (me != null) {
                    final int ind = me.getIndex(partyid);
                    if (ind >= 0) {
                        me.getParties().remove(ind);
                        expedPacket(me.getId(), ExpeditionPacket.expeditionUpdate(ind, null), null);
                    }
                }
            }
            ret.disband();
            return ret;
        }

        public static List<PartySearch> searchParty(PartySearchType pst) {
            return searches.get(pst);
        }

        public static void removeSearch(PartySearch ps, String text) {
            List<PartySearch> ss = searches.get(ps.getType());
            if (ss.contains(ps)) {
                ss.remove(ps);
                ps.cancelRemoval();
                if (ps.getType().exped) {
                    expedMessage(ps.getId(), text);
                } else {
                    partyMessage(ps.getId(), text);
                }
            }
        }

        public static void addSearch(PartySearch ps) {
            searches.get(ps.getType()).add(ps);
        }

        public static PartySearch getSearch(MapleParty party) {
            for (List<PartySearch> ps : searches.values()) {
                for (PartySearch p : ps) {
                    if ((p.getId() == party.getId() && !p.getType().exped) || (p.getId() == party.getExpeditionId() && p.getType().exped)) {
                        return p;
                    }
                }
            }
            return null;
        }

        public static PartySearch getSearch(MapleExpedition exped) {
            for (List<PartySearch> ps : searches.values()) {
                for (PartySearch p : ps) {
                    if (p.getId() == exped.getId() && p.getType().exped) {
                        return p;
                    }
                }
            }
            return null;
        }

        public static PartySearch getSearchByParty(int partyId) {
            for (List<PartySearch> ps : searches.values()) {
                for (PartySearch p : ps) {
                    if (p.getId() == partyId && !p.getType().exped) {
                        return p;
                    }
                }
            }
            return null;
        }

        public static PartySearch getSearchByExped(int partyId) {
            for (List<PartySearch> ps : searches.values()) {
                for (PartySearch p : ps) {
                    if (p.getId() == partyId && p.getType().exped) {
                        return p;
                    }
                }
            }
            return null;
        }

        public static boolean partyListed(MapleParty party) {
            return getSearchByParty(party.getId()) != null;
        }
    }

    public static class WorldBuddy {

        public static void buddyChat(int[] recipientCharacterIds, int cidFrom, String nameFrom, String chattext) {
            for (int characterId : recipientCharacterIds) {
                int ch = Find.findChannel(characterId);
                if (ch > 0) {
                    User chr = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterById(characterId);
                    if (chr != null && chr.getBuddylist().containsVisible(cidFrom)) {
                        //chr.getClient().write(CField.multiChat(nameFrom, chattext, 0));
                        if (chr.getClient().isMonitored()) {
                            World.Broadcast.broadcastGMMessage(WvsContext.broadcastMsg(6, "[GM Message] " + nameFrom + " said to " + chr.getName() + " (Buddy): " + chattext));
                        }
                    }
                }
            }
        }

        private static void updateBuddies(int characterId, int channel, List<Integer> buddies, boolean offline) {
            for (int buddy : buddies) {
                int ch = Find.findChannel(buddy);
                if (ch > 0) {
                    User chr = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterById(buddy);
                    if (chr != null) {
                        BuddylistEntry ble = chr.getBuddylist().get(characterId);
                        if (ble != null) {
                            if (!offline && !ble.isPending()) {
                                ble.setChannel(channel - 1);
                            } else {
                                ble.setChannel(-1);
                            }
                            ble.setFlag(BuddyFlags.AccountFriendOnline.getFlag(ble));
                            Buddy bud = new Buddy(BuddyResult.NOTIFY);
                            bud.setEntry(ble);
                            chr.getClient().SendPacket(WvsContext.buddylistMessage(bud));
                        }
                    }
                }
            }
        }

        public static void buddyChanged(int cid, int cidFrom, String name, int channel, BuddyOperation operation, boolean accountFriend, String nickname) {
            int buddyChannel = Find.findChannel(cid);
            if (buddyChannel > 0) {
                final User addChar = ChannelServer.getInstance(buddyChannel).getPlayerStorage().getCharacterById(cid);
                if (addChar != null) {
                    final BuddyList buddylist = addChar.getBuddylist();
                    Buddy b = new Buddy(BuddyResult.NOTIFY);
                    BuddylistEntry entry;
                    switch (operation) {
                        case ADD:
                            if (buddylist.contains(cidFrom)) {
                                entry = new BuddylistEntry(name, cidFrom, "Default Group", channel, false, "", accountFriend, nickname);
                                entry.setFlag(BuddyFlags.AccountFriendOnline.getFlag(entry));
                                buddylist.put(entry);
                                b.setEntry(entry);
                                addChar.getClient().SendPacket(WvsContext.buddylistMessage(b));
                            }
                            break;
                        case DELETE:
                            if (buddylist.contains(cidFrom)) {
                                entry = new BuddylistEntry(name, cidFrom, "Default Group", channel, false, "", accountFriend, "");
                                entry.setFlag(BuddyFlags.FriendOffline.getFlag());
                                buddylist.put(entry);
                                b.setEntry(entry);
                                addChar.getClient().SendPacket(WvsContext.buddylistMessage(b));
                            }
                            break;
                        default:
                            break;
                    }
                    b.setResult(BuddyResult.LOAD_FRIENDS);
                    b.setEntries(new ArrayList<>(buddylist.getBuddies()));
                    addChar.getClient().SendPacket(WvsContext.buddylistMessage(b));
                }
            }
        }

        public static BuddyOperation requestBuddyAdd(User chr, boolean accountFriend, User otherChar) {
            if (otherChar != null) {
                final BuddyList buddylist = otherChar.getBuddylist();
                if (buddylist.isFull()) {
                    return BuddyOperation.BUDDYLIST_FULL;
                }
                if (!buddylist.contains(chr.getId())) {
                    buddylist.addBuddyRequest(otherChar.getClient(), chr.getClient().getChannel(), chr, accountFriend);
                } else if (buddylist.containsVisible(chr.getId())) {
                    return BuddyOperation.ALREADY_ON_LIST;
                }
            }
            return BuddyOperation.OK;
        }

        public static void loggedOn(String name, int characterId, int channel, List<Integer> buddies) {
            updateBuddies(characterId, channel, buddies, false);
        }

        public static void loggedOff(String name, int characterId, int channel, List<Integer> buddies) {
            updateBuddies(characterId, channel, buddies, true);
        }
    }

    public static class Messenger {

        private static final Map<Integer, MapleMessenger> messengers = new HashMap<>();
        private static final AtomicInteger runningMessengerId = new AtomicInteger();

        static {
            runningMessengerId.set(7);
        }

        public static MapleMessenger createMessenger(MapleMessengerCharacter chrfor) {
            int messengerid = runningMessengerId.getAndIncrement();
            MapleMessenger messenger = new MapleMessenger(messengerid, chrfor);
            messengers.put(messenger.getId(), messenger);
            return messenger;
        }

        public static void declineChat(String target, String namefrom) {
            int ch = Find.findChannel(target);
            if (ch > 0) {
                ChannelServer cs = ChannelServer.getInstance(ch);
                User chr = cs.getPlayerStorage().getCharacterByName(target);
                if (chr != null) {
                    MapleMessenger messenger = chr.getMessenger();
                    if (messenger != null) {
                        chr.getClient().SendPacket(CField.messengerNote(namefrom, 5, 0));
                    }
                }
            }
        }

        public static MapleMessenger getMessenger(int messengerid) {
            return messengers.get(messengerid);
        }

        public static void leaveMessenger(int messengerid, MapleMessengerCharacter target) {
            MapleMessenger messenger = getMessenger(messengerid);
            if (messenger == null) {
                throw new IllegalArgumentException("No messenger with the specified messengerid exists");
            }
            int position = messenger.getPositionByName(target.getName());
            messenger.removeMember(target);

            for (MapleMessengerCharacter mmc : messenger.getMembers()) {
                if (mmc != null) {
                    int ch = Find.findChannel(mmc.getId());
                    if (ch > 0) {
                        User chr = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(mmc.getName());
                        if (chr != null) {
                            chr.getClient().SendPacket(CField.removeMessengerPlayer(position));
                        }
                    }
                }
            }
        }

        public static void silentLeaveMessenger(int messengerid, MapleMessengerCharacter target) {
            MapleMessenger messenger = getMessenger(messengerid);
            if (messenger == null) {
                throw new IllegalArgumentException("No messenger with the specified messengerid exists");
            }
            messenger.silentRemoveMember(target);
        }

        public static void silentJoinMessenger(int messengerid, MapleMessengerCharacter target) {
            MapleMessenger messenger = getMessenger(messengerid);
            if (messenger == null) {
                throw new IllegalArgumentException("No messenger with the specified messengerid exists");
            }
            messenger.silentAddMember(target);
        }

        public static void updateMessenger(int messengerid, String namefrom, int fromchannel) {
            MapleMessenger messenger = getMessenger(messengerid);
            int position = messenger.getPositionByName(namefrom);

            for (MapleMessengerCharacter messengerchar : messenger.getMembers()) {
                if (messengerchar != null && !messengerchar.getName().equals(namefrom)) {
                    int ch = Find.findChannel(messengerchar.getName());
                    if (ch > 0) {
                        User chr = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(messengerchar.getName());
                        if (chr != null) {
                            User from = ChannelServer.getInstance(fromchannel).getPlayerStorage().getCharacterByName(namefrom);
                            chr.getClient().SendPacket(CField.updateMessengerPlayer(namefrom, from, position, fromchannel - 1));
                        }
                    }
                }
            }
        }

        public static void joinMessenger(int messengerid, MapleMessengerCharacter target, String from, int fromchannel) {
            MapleMessenger messenger = getMessenger(messengerid);
            if (messenger == null) {
                throw new IllegalArgumentException("No messenger with the specified messengerid exists");
            }
            messenger.addMember(target);
            int position = messenger.getPositionByName(target.getName());
            for (MapleMessengerCharacter messengerchar : messenger.getMembers()) {
                if (messengerchar != null) {
                    int mposition = messenger.getPositionByName(messengerchar.getName());
                    int ch = Find.findChannel(messengerchar.getName());
                    if (ch > 0) {
                        User chr = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(messengerchar.getName());
                        if (chr != null) {
                            if (!messengerchar.getName().equals(from)) {
                                User fromCh = ChannelServer.getInstance(fromchannel).getPlayerStorage().getCharacterByName(from);
                                if (fromCh != null) {
                                    chr.getClient().SendPacket(CField.addMessengerPlayer(from, fromCh, position, fromchannel - 1));
                                    fromCh.getClient().SendPacket(CField.addMessengerPlayer(chr.getName(), chr, mposition, messengerchar.getChannel() - 1));
                                }
                            } else {
                                chr.getClient().SendPacket(CField.joinMessenger(mposition));
                            }
                        }
                    }
                }
            }
        }

        public static void messengerChat(int messengerid, String charname, String text, String namefrom) {
            MapleMessenger messenger = getMessenger(messengerid);
            if (messenger == null) {
                throw new IllegalArgumentException("No messenger with the specified messengerid exists");
            }

            for (MapleMessengerCharacter messengerchar : messenger.getMembers()) {
                if (messengerchar != null && !messengerchar.getName().equals(namefrom)) {
                    int ch = Find.findChannel(messengerchar.getName());
                    if (ch > 0) {
                        User chr = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(messengerchar.getName());
                        if (chr != null) {
                            chr.getClient().SendPacket(CField.messengerChat(charname, text));
                        }
                    }
                }
            }
        }

        public static void messengerInvite(String sender, int messengerid, String target, int fromchannel, boolean gm) {

            if (isConnected(target)) {

                int ch = Find.findChannel(target);
                if (ch > 0) {
                    User from = ChannelServer.getInstance(fromchannel).getPlayerStorage().getCharacterByName(sender);
                    User targeter = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(target);
                    if (targeter != null && targeter.getMessenger() == null) {
                        if (!targeter.isIntern() || gm) {
                            targeter.getClient().SendPacket(CField.messengerInvite(sender, messengerid));
                            from.getClient().SendPacket(CField.messengerNote(target, 4, 1));
                        } else {
                            from.getClient().SendPacket(CField.messengerNote(target, 4, 0));
                        }
                    } else {
                        from.getClient().SendPacket(CField.messengerChat(sender, " : " + target + " is already using Maple Messenger"));
                    }
                }
            }

        }
    }

    public static class Guild {

        private static final Map<Integer, MapleGuild> guilds = new LinkedHashMap<>();
        private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

        public static void addLoadedGuild(MapleGuild f) {
            if (f.isProper()) {
                guilds.put(f.getId(), f);
            }
        }

        public static int createGuild(int leaderId, String name) {
            return MapleGuild.createGuild(leaderId, name);
        }

        public static MapleGuild getGuild(int id) {
            MapleGuild ret = null;
            lock.readLock().lock();
            try {
                ret = guilds.get(id);
            } finally {
                lock.readLock().unlock();
            }
            if (ret == null) {
                lock.writeLock().lock();
                try {
                    ret = new MapleGuild(id);
                    if (ret == null || ret.getId() <= 0 || !ret.isProper()) { //failed to load
                        return null;
                    }
                    guilds.put(id, ret);
                } finally {
                    lock.writeLock().unlock();
                }
            }
            return ret; //Guild doesn't exist?
        }

        public static MapleGuild getGuildByName(String guildName) {
            lock.readLock().lock();
            try {
                for (MapleGuild g : guilds.values()) {
                    if (g.getName().equalsIgnoreCase(guildName)) {
                        return g;
                    }
                }
                return null;
            } finally {
                lock.readLock().unlock();
            }
        }

        public static MapleGuild getLoadedGuildById(int guildId) {
            lock.readLock().lock();
            try {
                for (MapleGuild g : guilds.values()) {
                    if (g.getId() == guildId) {
                        return g;
                    }
                }
                return null;
            } finally {
                lock.readLock().unlock();
            }
        }

        public static Map<Integer, MapleGuild> getGuildByNummericalSearch(byte minGuildLevel, byte maxGuildLevel, byte minGuildSize, byte maxGuildSize, byte minAvgMemberLevel, byte maxAvgMemberLevel) {
            Map<Integer, MapleGuild> acceptableGuilds = new LinkedHashMap<>();
            lock.readLock().lock();
            try {
                for (MapleGuild guild : guilds.values()) {
                    int guildLevel = guild.getLevel();
                    java.util.Collection<MapleGuildCharacter> members = guild.getMembers();
                    int guildSize = members.size();
                    double avgLevel = members.stream().mapToInt(MapleGuildCharacter::getLevel).average().getAsDouble();
                    if (guildLevel >= minGuildLevel
                            && guildLevel <= maxGuildLevel
                            && guildSize >= minGuildSize
                            && guildSize <= maxGuildSize
                            && avgLevel >= minAvgMemberLevel
                            && avgLevel <= maxAvgMemberLevel) {
                        acceptableGuilds.put((int) avgLevel, guild);
                    }
                }
                return acceptableGuilds;
            } finally {
                lock.readLock().unlock();
            }
        }

        public static Map<Integer, MapleGuild> getGuildByOwnerSearch(ClientSocket c, String guildMasterName, boolean exactWord) {
            Map<Integer, MapleGuild> acceptableGuilds = new LinkedHashMap<>();
            lock.readLock().lock();
            try {
                for (MapleGuild guild : guilds.values()) {
                    int masterId = guild.getLeaderId();
                    User master = c.loadCharacterById(masterId);
                    String masterName = master.getName();
                    java.util.Collection<MapleGuildCharacter> members = guild.getMembers();
                    double avgLevel = members.stream().mapToInt(MapleGuildCharacter::getLevel).average().getAsDouble();
                    if (exactWord && masterName.equals(guildMasterName)) {
                        acceptableGuilds.put((int) avgLevel, guild);
                    } else if (!exactWord && masterName.contains(guildMasterName)) {
                        acceptableGuilds.put((int) avgLevel, guild);
                    }
                }
                return acceptableGuilds;
            } finally {
                lock.readLock().unlock();
            }
        }

        public static Map<Integer, MapleGuild> getGuildByNameSearch(ClientSocket c, String guildName, boolean exactWord) {
            Map<Integer, MapleGuild> acceptableGuilds = new LinkedHashMap<>();
            lock.readLock().lock();
            try {
                for (MapleGuild guild : guilds.values()) {
                    java.util.Collection<MapleGuildCharacter> members = guild.getMembers();
                    double avgLevel = members.stream().mapToInt(MapleGuildCharacter::getLevel).average().getAsDouble();
                    if (exactWord && guild.getName().equals(guildName)) {
                        acceptableGuilds.put((int) avgLevel, guild);
                    } else if (!exactWord && guild.getName().contains(guildName)) {
                        acceptableGuilds.put((int) avgLevel, guild);
                    }
                }
                return acceptableGuilds;
            } finally {
                lock.readLock().unlock();
            }
        }

        public static MapleGuild getGuild(User mc) {
            return getGuild(mc.getGuildId());
        }

        public static void setGuildMemberOnline(MapleGuildCharacter mc, boolean bOnline, int channel) {
            MapleGuild g = getGuild(mc.getGuildId());
            if (g != null) {
                g.setOnline(mc.getId(), bOnline, channel);
            }
        }

        public static void guildPacket(int gid, OutPacket message) {
            MapleGuild g = getGuild(gid);
            if (g != null) {
                g.broadcast(message);
            }
        }

        public static int addGuildMember(MapleGuildCharacter mc) {
            MapleGuild g = getGuild(mc.getGuildId());
            if (g != null) {
                return g.addGuildMember(mc);
            }
            return 0;
        }

        public static void leaveGuild(MapleGuildCharacter mc) {
            MapleGuild g = getGuild(mc.getGuildId());
            if (g != null) {
                g.leaveGuild(mc);
            }
        }

        public static void guildChat(int gid, String name, int cid, String msg) {
            MapleGuild g = getGuild(gid);
            if (g != null) {
                g.guildChat(name, cid, msg);
            }
        }

        public static void changeRank(int gid, int cid, int newRank) {
            MapleGuild g = getGuild(gid);
            if (g != null) {
                g.changeRank(cid, newRank);
            }
        }

        public static void expelMember(MapleGuildCharacter initiator, String name, int cid) {
            MapleGuild g = getGuild(initiator.getGuildId());
            if (g != null) {
                g.expelMember(initiator, name, cid);
            }
        }

        public static void setGuildNotice(int gid, String notice) {
            MapleGuild g = getGuild(gid);
            if (g != null) {
                g.setGuildNotice(notice);
            }
        }

        public static void setGuildLeader(int gid, int cid) {
            MapleGuild g = getGuild(gid);
            if (g != null) {
                g.changeGuildLeader(cid);
            }
        }

        public static int getGuildSkillLevel(int gid, int sid) {
            MapleGuild g = getGuild(gid);
            if (g != null) {
                return g.getSkillLevel(sid);
            }
            return 0;
        }

        public static int getGuildLevel(int gid) {
            MapleGuild g = getGuild(gid);
            if (g != null) {
                return g.getLevel();
            }
            return 0;
        }

        public static boolean purchaseGuildSkill(int gid, int sid, String name, int cid) {
            MapleGuild g = getGuild(gid);
            if (g != null) {
                return g.purchaseSkill(sid, name, cid);
            }
            return false;
        }

        public static boolean activateSkill(int gid, int sid, String name) {
            MapleGuild g = getGuild(gid);
            if (g != null) {
                return g.activateSkill(sid, name);
            }
            return false;
        }

        public static void memberLevelJobUpdate(MapleGuildCharacter mc) {
            MapleGuild g = getGuild(mc.getGuildId());
            if (g != null) {
                g.memberLevelJobUpdate(mc);
            }
        }

        public static void changeRankTitle(int gid, String[] ranks) {
            MapleGuild g = getGuild(gid);
            if (g != null) {
                g.changeRankTitle(ranks);
            }
        }

        public static void setGuildEmblem(int gid, short bg, byte bgcolor, short logo, byte logocolor) {
            MapleGuild g = getGuild(gid);
            if (g != null) {
                g.setGuildEmblem(bg, bgcolor, logo, logocolor);
            }
        }

        public static void disbandGuild(int gid) {
            MapleGuild g = getGuild(gid);
            lock.writeLock().lock();
            try {
                if (g != null) {
                    g.disbandGuild();
                    guilds.remove(gid);
                }
            } finally {
                lock.writeLock().unlock();
            }
        }

        public static void deleteGuildCharacter(int guildid, int charid) {

            //ensure it's loaded on world server
            //setGuildMemberOnline(mc, false, -1);
            MapleGuild g = getGuild(guildid);
            if (g != null) {
                MapleGuildCharacter mc = g.getMGC(charid);
                if (mc != null) {
                    if (mc.getGuildRank() > 1) //not leader
                    {
                        g.leaveGuild(mc);
                    } else {
                        g.disbandGuild();
                    }
                }
            }
        }

        public static boolean increaseGuildCapacity(int gid, boolean b) {
            MapleGuild g = getGuild(gid);
            if (g != null) {
                return g.increaseCapacity(b);
            }
            return false;
        }

        public static void gainGP(int gid, int amount) {
            MapleGuild g = getGuild(gid);
            if (g != null) {
                g.gainGP(amount);
            }
        }

        public static void gainGP(int gid, int amount, int cid) {
            MapleGuild g = getGuild(gid);
            if (g != null) {
                g.gainGP(amount, false, cid);
            }
        }

        public static int getGP(final int gid) {
            final MapleGuild g = getGuild(gid);
            if (g != null) {
                return g.getGP();
            }
            return 0;
        }

        public static int getInvitedId(final int gid) {
            final MapleGuild g = getGuild(gid);
            if (g != null) {
                return g.getInvitedId();
            }
            return 0;
        }

        public static void setInvitedId(final int gid, final int inviteid) {
            final MapleGuild g = getGuild(gid);
            if (g != null) {
                g.setInvitedId(inviteid);
            }
        }

        public static int getGuildLeader(final int guildName) {
            final MapleGuild mga = getGuild(guildName);
            if (mga != null) {
                return mga.getLeaderId();
            }
            return 0;
        }

        public static int getGuildLeader(final String guildName) {
            final MapleGuild mga = getGuildByName(guildName);
            if (mga != null) {
                return mga.getLeaderId();
            }
            return 0;
        }

        public static void save() {
            System.out.println("Saving guilds...");
            lock.writeLock().lock();
            try {
                for (MapleGuild a : guilds.values()) {
                    a.writeToDB(false);
                }
            } finally {
                lock.writeLock().unlock();
            }
        }

        public static List<MapleBBSThread> getBBS(final int gid) {
            final MapleGuild g = getGuild(gid);
            if (g != null) {
                return g.getBBS();
            }
            return null;
        }

        public static int addBBSThread(final int guildid, final String title, final String text, final int icon, final boolean bNotice, final int posterID) {
            final MapleGuild g = getGuild(guildid);
            if (g != null) {
                return g.addBBSThread(title, text, icon, bNotice, posterID);
            }
            return -1;
        }

        public static final void editBBSThread(final int guildid, final int localthreadid, final String title, final String text, final int icon, final int posterID, final int guildRank) {
            final MapleGuild g = getGuild(guildid);
            if (g != null) {
                g.editBBSThread(localthreadid, title, text, icon, posterID, guildRank);
            }
        }

        public static final void deleteBBSThread(final int guildid, final int localthreadid, final int posterID, final int guildRank) {
            final MapleGuild g = getGuild(guildid);
            if (g != null) {
                g.deleteBBSThread(localthreadid, posterID, guildRank);
            }
        }

        public static final void addBBSReply(final int guildid, final int localthreadid, final String text, final int posterID) {
            final MapleGuild g = getGuild(guildid);
            if (g != null) {
                g.addBBSReply(localthreadid, text, posterID);
            }
        }

        public static final void deleteBBSReply(final int guildid, final int localthreadid, final int replyid, final int posterID, final int guildRank) {
            final MapleGuild g = getGuild(guildid);
            if (g != null) {
                g.deleteBBSReply(localthreadid, replyid, posterID, guildRank);
            }
        }

        public static void changeEmblem(int gid, int affectedPlayers, MapleGuild mgs) {
            Broadcast.sendGuildPacket(affectedPlayers, GuildPacket.setMark(gid, (short) mgs.getLogoBG(), (byte) mgs.getLogoBGColor(), (short) mgs.getLogo(), (byte) mgs.getLogoColor()), -1, gid);
            setGuildAndRank(affectedPlayers, -1, -1, -1, -1);	//respawn player
        }

        public static void setGuildAndRank(int cid, int guildid, int rank, int contribution, int alliancerank) {
            int ch = User.getChannel(cid)/*Find.findChannel(cid)*/;
            if (ch == -1) {
                // System.out.println("ERROR: cannot find player in given channel");
                return;
            }
            User mc = getStorage(ch).getCharacterById(cid);
            if (mc == null) {
                return;
            }
            boolean bDifferentGuild;
            if (guildid == -1 && rank == -1) { //just need a respawn
                bDifferentGuild = true;
            } else {
                bDifferentGuild = guildid != mc.getGuildId();
                mc.setGuildId(guildid);
                mc.setGuildRank((byte) rank);
                mc.setGuildContribution(contribution);
                mc.setAllianceRank((byte) alliancerank);
                mc.saveGuildStatus();
            }
            if (bDifferentGuild && ch > 0) {
                mc.write(GuildPacket.guildDisbanded(guildid));
                mc.getMap().broadcastPacket(mc, GuildPacket.sendSetGuildNameMsg(mc), false);
                mc.getMap().broadcastPacket(mc, GuildPacket.sendSetGuildMarkMsg(mc), false);
            }
        }
    }

    public static class Broadcast {

        public static void broadcastSmega(OutPacket message) {
            for (ChannelServer cs : ChannelServer.getAllInstances()) {
                cs.broadcastSmega(message);
            }
        }

        public static void broadcastGMMessage(OutPacket message) {
            for (ChannelServer cs : ChannelServer.getAllInstances()) {
                cs.broadcastGMMessage(message);
            }
        }

        public static void broadcastMessage(OutPacket message) {
            for (ChannelServer cs : ChannelServer.getAllInstances()) {
                cs.broadcastMessage(message);
            }
        }

        public static void broadcastWhisper(OutPacket message, String msgDestination) {
            for (ChannelServer cs : ChannelServer.getAllInstances()) {
                cs.broadcastWhisper(message, msgDestination);
            }
        }

        public static void sendGuildPacket(int targetIds, OutPacket oPacket, int exception, int guildid) {
            if (targetIds == exception) {
                return;
            }
            short nPacketID = oPacket.nPacketID;
            byte[] aData = oPacket.CloneData();
            int ch = Find.findChannel(targetIds);
            if (ch < 0) {
                return;
            }
            final User c = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterById(targetIds);
            if (c != null && c.getGuildId() == guildid) {
                c.getClient().SendPacket((new OutPacket(nPacketID)).Encode(aData));
            }
        }

        public static void sendFamilyPacket(int targetIds, OutPacket oPacket, int exception, int guildid) {
            if (targetIds == exception) {
                return;
            }
            short nPacketID = oPacket.nPacketID;
            byte[] aData = oPacket.CloneData();
            int ch = Find.findChannel(targetIds);
            if (ch < 0) {
                return;
            }
            final User c = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterById(targetIds);
            if (c != null && c.getFamilyId() == guildid) {
                c.getClient().SendPacket((new OutPacket(nPacketID)).Encode(aData));
            }
        }
    }

    public static class Find {

        private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        private static final HashMap<Integer, Integer> idToChannel = new HashMap<>();
        private static final HashMap<String, Integer> nameToChannel = new HashMap<>();

        public static void register(int id, String name, int channel) {
            lock.writeLock().lock();
            try {
                idToChannel.put(id, channel);
                nameToChannel.put(name.toLowerCase(), channel);
            } finally {
                lock.writeLock().unlock();
            }
            //System.out.println("Char added: " + id + " " + name + " to channel " + channel);
        }

        public static void forceDeregister(int id) {
            lock.writeLock().lock();
            try {
                idToChannel.remove(id);
            } finally {
                lock.writeLock().unlock();
            }
            //System.out.println("Char removed: " + id);
        }

        public static void forceDeregister(String id) {
            lock.writeLock().lock();
            try {
                nameToChannel.remove(id.toLowerCase());
            } finally {
                lock.writeLock().unlock();
            }
            //System.out.println("Char removed: " + id);
        }

        public static void forceDeregister(int id, String name) {
            lock.writeLock().lock();
            try {
                idToChannel.remove(id);
                nameToChannel.remove(name.toLowerCase());
            } finally {
                lock.writeLock().unlock();
            }
            //System.out.println("Char removed: " + id + " " + name);
        }

        public static int findChannel(int id) {
            Integer ret;
            lock.readLock().lock();
            try {
                ret = idToChannel.get(id);
            } finally {
                lock.readLock().unlock();
            }
            if (ret != null) {
                if (ret != -10 && ret != -20 && ChannelServer.getInstance(ret) == null) { //wha
                    forceDeregister(id);
                    return -1;
                }
                return ret;
            }
            return -1;
        }

        public static int findChannel(String st) {
            Integer ret;
            lock.readLock().lock();
            try {
                ret = nameToChannel.get(st.toLowerCase());
            } finally {
                lock.readLock().unlock();
            }
            if (ret != null) {
                if (ret != -10 && ret != -20 && ChannelServer.getInstance(ret) == null) { //wha
                    forceDeregister(st);
                    return -1;
                }
                return ret;
            }
            return -1;
        }

        public static CharacterIdChannelPair[] multiBuddyFind(int charIdFrom, List<Integer> characterIds) {
            List<CharacterIdChannelPair> foundsChars = new ArrayList<>(characterIds.size());
            for (int i : characterIds) {
                int channel = findChannel(i);
                if (channel > 0) {
                    foundsChars.add(new CharacterIdChannelPair(i, channel));
                }
            }
            Collections.sort(foundsChars);
            return foundsChars.toArray(new CharacterIdChannelPair[foundsChars.size()]);
        }
    }

    public static class Alliance {

        private static final Map<Integer, MapleGuildAlliance> alliances = new LinkedHashMap<>();
        private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

        static {
            Collection<MapleGuildAlliance> allGuilds = MapleGuildAlliance.loadAll();
            for (MapleGuildAlliance g : allGuilds) {
                alliances.put(g.getId(), g);
            }
        }

        public static MapleGuildAlliance getAlliance(final int allianceid) {
            MapleGuildAlliance ret = null;
            lock.readLock().lock();
            try {
                ret = alliances.get(allianceid);
            } finally {
                lock.readLock().unlock();
            }
            if (ret == null) {
                lock.writeLock().lock();
                try {
                    ret = new MapleGuildAlliance(allianceid);
                    if (ret == null || ret.getId() <= 0) { //failed to load
                        return null;
                    }
                    alliances.put(allianceid, ret);
                } finally {
                    lock.writeLock().unlock();
                }
            }
            return ret;
        }

        public static int getAllianceLeader(final int allianceid) {
            final MapleGuildAlliance mga = getAlliance(allianceid);
            if (mga != null) {
                return mga.getLeaderId();
            }
            return 0;
        }

        public static void updateAllianceRanks(final int allianceid, final String[] ranks) {
            final MapleGuildAlliance mga = getAlliance(allianceid);
            if (mga != null) {
                mga.setRank(ranks);
            }
        }

        public static void updateAllianceNotice(final int allianceid, final String notice) {
            final MapleGuildAlliance mga = getAlliance(allianceid);
            if (mga != null) {
                mga.setNotice(notice);
            }
        }

        public static boolean canInvite(final int allianceid) {
            final MapleGuildAlliance mga = getAlliance(allianceid);
            if (mga != null) {
                return mga.getCapacity() > mga.getNoGuilds();
            }
            return false;
        }

        public static boolean changeAllianceLeader(final int allianceid, final int cid) {
            final MapleGuildAlliance mga = getAlliance(allianceid);
            if (mga != null) {
                return mga.setLeaderId(cid);
            }
            return false;
        }

        public static boolean changeAllianceLeader(final int allianceid, final int cid, final boolean sameGuild) {
            final MapleGuildAlliance mga = getAlliance(allianceid);
            if (mga != null) {
                return mga.setLeaderId(cid, sameGuild);
            }
            return false;
        }

        public static boolean changeAllianceRank(final int allianceid, final int cid, final int change) {
            final MapleGuildAlliance mga = getAlliance(allianceid);
            if (mga != null) {
                return mga.changeAllianceRank(cid, change);
            }
            return false;
        }

        public static boolean changeAllianceCapacity(final int allianceid) {
            final MapleGuildAlliance mga = getAlliance(allianceid);
            if (mga != null) {
                return mga.setCapacity();
            }
            return false;
        }

        public static boolean disbandAlliance(final int allianceid) {
            final MapleGuildAlliance mga = getAlliance(allianceid);
            if (mga != null) {
                return mga.disband();
            }
            return false;
        }

        public static boolean addGuildToAlliance(final int allianceid, final int gid) {
            final MapleGuildAlliance mga = getAlliance(allianceid);
            if (mga != null) {
                return mga.addGuild(gid);
            }
            return false;
        }

        public static boolean removeGuildFromAlliance(final int allianceid, final int gid, final boolean expelled) {
            final MapleGuildAlliance mga = getAlliance(allianceid);
            if (mga != null) {
                return mga.removeGuild(gid, expelled);
            }
            return false;
        }

        public static void sendGuild(final int allianceid) {
            final MapleGuildAlliance alliance = getAlliance(allianceid);
            if (alliance != null) {
                sendGuild(AlliancePacket.getAllianceUpdate(alliance), -1, allianceid);
                sendGuild(AlliancePacket.getGuildAlliance(alliance), -1, allianceid);
            }
        }

        public static void sendGuild(final OutPacket packet, final int exceptionId, final int allianceid) {
            final MapleGuildAlliance alliance = getAlliance(allianceid);
            if (alliance != null) {
                for (int i = 0; i < alliance.getNoGuilds(); i++) {
                    int gid = alliance.getGuildId(i);
                    if (gid > 0 && gid != exceptionId) {
                        Guild.guildPacket(gid, packet);
                    }
                }
            }
        }

        public static boolean createAlliance(final String alliancename, final int cid, final int cid2, final int gid, final int gid2) {
            final int allianceid = MapleGuildAlliance.createToDb(cid, alliancename, gid, gid2);
            if (allianceid <= 0) {
                return false;
            }
            final MapleGuild g = Guild.getGuild(gid), g_ = Guild.getGuild(gid2);
            g.setAllianceId(allianceid);
            g_.setAllianceId(allianceid);
            g.changeARank(true);
            g_.changeARank(false);

            final MapleGuildAlliance alliance = getAlliance(allianceid);

            sendGuild(AlliancePacket.createGuildAlliance(alliance), -1, allianceid);
            sendGuild(AlliancePacket.getAllianceInfo(alliance), -1, allianceid);
            sendGuild(AlliancePacket.getGuildAlliance(alliance), -1, allianceid);
            sendGuild(AlliancePacket.changeAlliance(alliance, true), -1, allianceid);
            return true;
        }

        public static void allianceChat(final int gid, final String name, final int cid, final String msg) {
            final MapleGuild g = Guild.getGuild(gid);
            if (g != null) {
                final MapleGuildAlliance ga = getAlliance(g.getAllianceId());
                if (ga != null) {
                    for (int i = 0; i < ga.getNoGuilds(); i++) {
                        final MapleGuild g_ = Guild.getGuild(ga.getGuildId(i));
                        if (g_ != null) {
                            g_.allianceChat(name, cid, msg);
                        }
                    }
                }
            }
        }

        public static void setNewAlliance(final int gid, final int allianceid) {
            final MapleGuildAlliance alliance = getAlliance(allianceid);
            final MapleGuild guild = Guild.getGuild(gid);
            if (alliance != null && guild != null) {
                for (int i = 0; i < alliance.getNoGuilds(); i++) {
                    if (gid == alliance.getGuildId(i)) {
                        guild.setAllianceId(allianceid);
                        guild.broadcast(AlliancePacket.getAllianceInfo(alliance));
                        guild.broadcast(AlliancePacket.getGuildAlliance(alliance));
                        guild.broadcast(AlliancePacket.changeAlliance(alliance, true));
                        guild.changeARank();
                        guild.writeToDB(false);
                    } else {
                        final MapleGuild g_ = Guild.getGuild(alliance.getGuildId(i));
                        if (g_ != null) {
                            g_.broadcast(AlliancePacket.addGuildToAlliance(alliance, guild));
                            g_.broadcast(AlliancePacket.changeGuildInAlliance(alliance, guild, true));
                        }
                    }
                }
            }
        }

        public static void setOldAlliance(final int gid, final boolean expelled, final int allianceid) {
            final MapleGuildAlliance alliance = getAlliance(allianceid);
            final MapleGuild g_ = Guild.getGuild(gid);
            if (alliance != null) {
                for (int i = 0; i < alliance.getNoGuilds(); i++) {
                    final MapleGuild guild = Guild.getGuild(alliance.getGuildId(i));
                    if (guild == null) {
                        if (gid != alliance.getGuildId(i)) {
                            alliance.removeGuild(gid, false, true);
                        }
                        continue; //just skip
                    }
                    if (g_ == null || gid == alliance.getGuildId(i)) {
                        guild.changeARank(5);
                        guild.setAllianceId(0);
                        guild.broadcast(AlliancePacket.disbandAlliance(allianceid));
                    } else if (g_ != null) {
                        guild.broadcast(WvsContext.broadcastMsg(5, "[" + g_.getName() + "] Guild has left the alliance."));
                        guild.broadcast(AlliancePacket.changeGuildInAlliance(alliance, g_, false));
                        guild.broadcast(AlliancePacket.removeGuildFromAlliance(alliance, g_, expelled));
                    }

                }
            }

            if (gid == -1) {
                lock.writeLock().lock();
                try {
                    alliances.remove(allianceid);
                } finally {
                    lock.writeLock().unlock();
                }
            }
        }

        public static List<OutPacket> getAllianceInfo(final int allianceid, final boolean start) {
            List<OutPacket> ret = new ArrayList<>();
            final MapleGuildAlliance alliance = getAlliance(allianceid);
            if (alliance != null) {
                if (start) {
                    ret.add(AlliancePacket.getAllianceInfo(alliance));
                    ret.add(AlliancePacket.getGuildAlliance(alliance));
                }
                ret.add(AlliancePacket.getAllianceUpdate(alliance));
            }
            return ret;
        }

        public static void save() {
            System.out.println("Saving alliances...");
            lock.writeLock().lock();
            try {
                for (MapleGuildAlliance a : alliances.values()) {
                    a.saveToDb();
                }
            } finally {
                lock.writeLock().unlock();
            }
        }
    }

    public static class Family {

        private static final Map<Integer, MapleFamily> families = new LinkedHashMap<>();
        private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

        public static void addLoadedFamily(MapleFamily f) {
            if (f.isProper()) {
                families.put(f.getId(), f);
            }
        }

        public static MapleFamily getFamily(int id) {
            MapleFamily ret = null;
            lock.readLock().lock();
            try {
                ret = families.get(id);
            } finally {
                lock.readLock().unlock();
            }
            if (ret == null) {
                lock.writeLock().lock();
                try {
                    ret = new MapleFamily(id);
                    if (ret == null || ret.getId() <= 0 || !ret.isProper()) { //failed to load
                        return null;
                    }
                    families.put(id, ret);
                } finally {
                    lock.writeLock().unlock();
                }
            }
            return ret;
        }

        public static void memberFamilyUpdate(MapleFamilyCharacter mfc, User mc) {
            MapleFamily f = getFamily(mfc.getFamilyId());
            if (f != null) {
                f.memberLevelJobUpdate(mc);
            }
        }

        public static void setFamilyMemberOnline(MapleFamilyCharacter mfc, boolean bOnline, int channel) {
            MapleFamily f = getFamily(mfc.getFamilyId());
            if (f != null) {
                f.setOnline(mfc.getId(), bOnline, channel);
            }
        }

        public static int setRep(int fid, int cid, int addrep, int oldLevel, String oldName) {
            MapleFamily f = getFamily(fid);
            if (f != null) {
                return f.setRep(cid, addrep, oldLevel, oldName);
            }
            return 0;
        }

        public static void save() {
            System.out.println("Saving families...");
            lock.writeLock().lock();
            try {
                for (MapleFamily a : families.values()) {
                    a.writeToDB(false);
                }
            } finally {
                lock.writeLock().unlock();
            }
        }

        public static void setFamily(int familyid, int seniorid, int junior1, int junior2, int currentrep, int totalrep, int cid) {
            int ch = Find.findChannel(cid);
            if (ch == -1) {
                // System.out.println("ERROR: cannot find player in given channel");
                return;
            }
            User mc = getStorage(ch).getCharacterById(cid);
            if (mc == null) {
                return;
            }
            boolean bDifferent = mc.getFamilyId() != familyid || mc.getSeniorId() != seniorid || mc.getJunior1() != junior1 || mc.getJunior2() != junior2;
            mc.setFamily(familyid, seniorid, junior1, junior2);
            mc.setCurrentRep(currentrep);
            mc.setTotalRep(totalrep);
            if (bDifferent) {
                mc.saveFamilyStatus();
            }
        }

        public static void familyPacket(int gid, OutPacket message, int cid) {
            MapleFamily f = getFamily(gid);
            if (f != null) {
                f.broadcast(message, -1, f.getMFC(cid).getPedigree());
            }
        }

        public static void disbandFamily(int gid) {
            MapleFamily g = getFamily(gid);
            if (g != null) {
                lock.writeLock().lock();
                try {
                    families.remove(gid);
                } finally {
                    lock.writeLock().unlock();
                }
                g.disbandFamily();
            }
        }
    }
    private final static int CHANNELS_PER_THREAD = 3;

    public static void registerRespawn() {
        Integer[] chs = ChannelServer.getAllInstance().toArray(new Integer[0]);
        for (int i = 0; i < chs.length; i += CHANNELS_PER_THREAD) {
            WorldTimer.getInstance().register(new Respawn(chs, i), 2500); //divisible by 9000 if possible.
        }
        //3000 good or bad? ive no idea >_>
        //buffs can also be done, but eh
    }

    public static class Respawn implements Runnable { //is putting it here a good idea?

        private int numTimes = 0;
        private final List<ChannelServer> cservs = new ArrayList<>(CHANNELS_PER_THREAD);

        public Respawn(Integer[] chs, int c) {
            //StringBuilder s = new StringBuilder("Respawn Worker is registered for channels ");
            for (int i = 1; i <= CHANNELS_PER_THREAD && chs.length >= (c + i); i++) {
                cservs.add(ChannelServer.getInstance(c + i));
                //s.append(c + i).append(" ");
            }
            //s.append(".");
            //System.out.println(s.toString());
        }

        @Override
        public void run() {
            numTimes++;
            final long currentTime = System.currentTimeMillis();

            for (ChannelServer cserv : cservs) {
                if (!cserv.hasFinishedShutdown()) {
                    for (MapleMap map : cserv.getMapFactory().getAllLoadedMaps()) { //iterating through each map o_x
                        handleMap(map, numTimes, map.getCharactersSize(), currentTime);
                    }
                }
            }
        }
    }

    public static void handleMap(final MapleMap map, final int numTimes, final int size, final long currentTime) {
        if (saveInterval()) {
            //    saveAllCharacters();
        }

        //TODO: test the achievement reset
        if (getHour() == 0 && getMinute() >= 0 && getMinute() <= 1) { //not sure if put it here
            try (Connection con = Database.GetConnection()) {

                try (PreparedStatement ps = con.prepareStatement("DELETE FROM `moonlightachievements` where achievementid > 0;")) {
                    ps.executeUpdate();
                    ps.close();
                } catch (SQLException ex) {
                    LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", ex);
                }
            } catch (SQLException ex) {
                LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", ex);
            }

        }
        if (map.getAllMapObjectSize(MapleMapObjectType.ITEM) > 0) {
            for (MapleMapObject o : map.getAllMapObjects(MapleMapObjectType.ITEM)) {
                MapleMapItem item = (MapleMapItem) o;
                if (item.shouldExpire(currentTime)) {
                    item.expire(map);
                } else if (item.shouldFFA(currentTime)) {
                    item.setDropType((byte) 2);
                }
            }
        }
        if (map.getCharactersSize() > 0 || map.getId() == 931000500) { //jaira hack
            // Respawns
            if (map.canSpawn(currentTime)) {
                map.respawn(false, currentTime);
                map.respawnRune(false);
            }

            // Burning field
            map.updateBurningField(currentTime);

            // Hurt
            boolean hurt = map.canHurt(currentTime);

            // Character cooldowns
            for (MapleMapObject o : map.getAllMapObjects(MapleMapObjectType.PLAYER)) {
                final User chr = (User) o;

                handleCooldowns(chr, numTimes, hurt, currentTime);
            }
            if (map.getAllMapObjectSize(MapleMapObjectType.MONSTER) > 0) {
                for (MapleMapObject o : map.getAllMapObjects(MapleMapObjectType.MONSTER)) {
                    final Mob mons = (Mob) o;

                    if (mons.isAlive() && mons.shouldKill(currentTime)) {
                        map.killMonster(mons);
                    } else if (mons.isAlive() && mons.shouldDrop(currentTime)) {
                        mons.doDropItem(currentTime);
                    } else if (mons.isAlive() && mons.getStatiSize() > 0) {
                        for (MonsterStatusEffect mse : mons.getAllBuffs()) {
                            if (mse.shouldCancel(currentTime)) {
                                mons.cancelSingleStatus(mse);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Handles the cooldown for this character, as executed periodically by the World server
     *
     * @param pPlayer
     * @param nNumTimes
     * @param bHurt
     * @param tNow
     */
    public static void handleCooldowns(final User pPlayer, final int nNumTimes, final boolean bHurt, final long tNow) { //is putting it here a good idea? expensive?
        if (pPlayer.getCooldownSize() > 0) {
            List<Pair<Integer, Integer>> cooldowns = new ArrayList<>();

            for (MapleCoolDownValueHolder m : pPlayer.getCooldowns()) {
                if (m.startTime + m.length < tNow) {
                    final int skil = m.skillId;
                    pPlayer.removeCooldown(skil);

                    cooldowns.add(new Pair<>(skil, 0));
                }
            }
            if (!cooldowns.isEmpty()) {
                pPlayer.getClient().SendPacket(CField.skillCooldown(cooldowns));
            }
        }
        if (pPlayer.isAlive()) {
            if (pPlayer.getJob() == 131 || pPlayer.getJob() == 132) {
                if (pPlayer.canBlood(tNow)) {
                    pPlayer.doDragonBlood();
                }
            } else if (GameConstants.isDemonAvenger(pPlayer.getJob())) {
                if (pPlayer.exceedTimeout(tNow)) { // Demon Avenger Overload timeout
                    pPlayer.setExceed((short) 0);
                }
            } else if (GameConstants.isNightWalkerCygnus(pPlayer.getJob())) {
                if (pPlayer.darkElementalTimeout(tNow)) { // Mark of Darkness timeout
                    pPlayer.setDarkElementalCombo(0);
                }
            } else if (GameConstants.isDemonSlayer(pPlayer.getJob())) {
                if (pPlayer.hasSkill(DemonSlayer.MAX_FURY)) {
                    pPlayer.getStat().setMp(pPlayer.getStat().getMp() + 10, pPlayer);
                    pPlayer.updateSingleStat(Stat.MP, pPlayer.getStat().getMp());
                }
            }
            if (pPlayer.canRecover(tNow)) {
                pPlayer.doRecovery();
            }
            if (pPlayer.canHPRecover(tNow)) {
                pPlayer.addHP((int) pPlayer.getStat().getHealHP());
            }
            if (pPlayer.canMPRecover(tNow)) {
                pPlayer.addMP((int) pPlayer.getStat().getHealMP());
            }
            if (pPlayer.canFairy(tNow)) {
                pPlayer.doFairy();
            }
            if (pPlayer.canFish(tNow)) {
                pPlayer.doFish(tNow);
            }
            if (pPlayer.canDOT(tNow)) {
                pPlayer.doDOT();
            }

            // Handle MP Cost per Second here -Mazen
            // Battle Mage Auras
            if (pPlayer.getBuffedValue(CharacterTemporaryStat.BMageAura) != null) {
                if (pPlayer.getBuffSource(CharacterTemporaryStat.Speed) == BattleMage.HASTY_AURA) {
                    pPlayer.addMP(-8);
                }
                if (pPlayer.getBuffSource(CharacterTemporaryStat.ComboDrain) == BattleMage.DRAINING_AURA) {
                    pPlayer.addMP(-28);
                }
                if (pPlayer.getBuffSource(CharacterTemporaryStat.AsrR) == BattleMage.BLUE_AURA) {
                    pPlayer.addMP(-12);
                }
                if (pPlayer.getBuffSource(CharacterTemporaryStat.DamR) == BattleMage.DARK_AURA) {
                    pPlayer.addMP(-16);
                }
                if (pPlayer.getBuffSource(CharacterTemporaryStat.BMageAura) == BattleMage.WEAKENING_AURA) {
                    pPlayer.addMP(-26);
                }
            }

            // Mihile
            if (pPlayer.getBuffSource(CharacterTemporaryStat.MichaelSoulLink) == Mihile.SOUL_LINK) {
                pPlayer.addMP(-12);
            }
        }

        if (pPlayer.getDiseaseSize() > 0) {
            for (MapleDiseaseValueHolder m : pPlayer.getAllDiseases()) {
                if (m != null && m.startTime + m.length < tNow) {
                    pPlayer.dispelDebuff(m.disease);
                }
            }
        }
        if (nNumTimes % 7 == 0 && pPlayer.getMount() != null && pPlayer.getMount().canTire(tNow)) {
            pPlayer.getMount().increaseFatigue();
        }
        if (nNumTimes % 13 == 0) { //we're parsing through the characters anyway (:
            pPlayer.doFamiliarSchedule(tNow);
            for (Pet pet : pPlayer.getSummonedPets()) {
                if (pet.getItem().getItemId() == 5000054 && pet.getSecondsLeft() > 0) {
                    pet.setSecondsLeft(pet.getSecondsLeft() - 1);
                    if (pet.getSecondsLeft() <= 0) {
                        pPlayer.unequipPet(pet, true, true);
                        return;
                    }
                }
                int newFullness = pet.getFullness() - PetDataFactory.getHunger(pet.getItem().getItemId());
                if (newFullness <= 5) {
                    pet.setFullness(15);
                    pPlayer.unequipPet(pet, true, true);
                } else {
                    pet.setFullness(newFullness);
                    //chr.forceUpdateItem(pet);
                    pPlayer.getClient().SendPacket(PetPacket.updatePet(pet, pPlayer.getInventory(MapleInventoryType.CASH).getItem((byte) pet.getItem().getPosition()), false));
                }
            }
        }
        if (bHurt && pPlayer.isAlive()) {
            if (pPlayer.getInventory(MapleInventoryType.EQUIPPED).findById(pPlayer.getMap().getSharedMapResources().protectItem) == null) {
                int hpDec = pPlayer.getMap().getSharedMapResources().decHP;

                if (pPlayer.getMapId() == 749040100 && pPlayer.getInventory(MapleInventoryType.CASH).findById(5451000) == null) { //minidungeon
                    pPlayer.addHP(-hpDec);
                } else if (pPlayer.getMapId() != 749040100) {
                    pPlayer.addHP(-(hpDec - (pPlayer.getBuffedValue(CharacterTemporaryStat.Thaw) == null ? 0 : pPlayer.getBuffedValue(CharacterTemporaryStat.Thaw))));
                }
            }
        }
    }

    public static final int getHour() {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    }

    public static final int getMinute() {
        return Calendar.getInstance().get(Calendar.MINUTE);
    }
}
