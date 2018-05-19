package client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.script.ScriptEngine;

import client.buddy.BuddyList;
import constants.GameConstants;
import constants.ServerConstants;
import database.Database;
import handling.world.MapleMessengerCharacter;
import handling.world.MapleParty;
import handling.world.MaplePartyCharacter;
import handling.world.PartyOperation;
import handling.world.World;
import handling.world.MapleFamilyCharacter;
import handling.world.MapleGuildCharacter;
import io.netty.channel.Channel;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.OutPacket;
import service.CashShopServer;
import service.ChannelServer;
import service.LoginServer;

import service.SendPacketOpcode;
import net.Socket;
import server.CharacterCardFactory;
import server.Randomizer;
import server.farm.MapleFarm;
import server.maps.MapleMap;
import server.maps.objects.User;
import server.quest.Quest;
import server.stores.IMaplePlayerShop;
import tools.LogHelper;
import tools.Pair;
import tools.packet.CField;
import tools.packet.CLogin;

public class ClientSocket extends Socket {

    public static enum MapleClientLoginState {
        Login_NotLoggedIn(0),
        Login_ServerTransition(1),
        Login_LoggedIn(2),
        ChangeChannel(3),
        NotFound(-1);

        private final int state;

        private MapleClientLoginState(int state) {
            this.state = state;
        }

        public int getState() {
            return state;
        }

        public static MapleClientLoginState getStateByInt(int i) {
            for (MapleClientLoginState value : values()) {
                if (value.getState() == i) {
                    return value;
                }
            }
            return MapleClientLoginState.NotFound;
        }
    }

    private static final long serialVersionUID = 9179541993413738569L;

    private User player;
    private int channel = 1, accId = -1, world, birthday;
    private long LoginAuthCookie = 0;
    private boolean loggedIn = false, serverTransition = false;
    private transient Calendar tempban = null;
    private String accountName;
    private transient long lastPong = 0, lastPing = 0;
    private boolean monitored = false, receiving = true;
    private boolean gm;
    private byte greason = 1, gender = -1;
    public transient short loginAttempt = 0;
    public transient short couponAttempt = 0;
    private final transient List<Integer> allowedChar = new LinkedList<>();
    private final transient Set<String> macs = new HashSet<>();
    private final transient Map<String, ScriptEngine> engines = new HashMap<>();
    private transient ScheduledFuture<?> idleTask = null;
    private transient String secondPassword, tempIP = ""; // To be used only on login
    private final transient Lock npc_mutex = new ReentrantLock();
    private final static Lock login_mutex = new ReentrantLock(true);
    private final transient Lock encoding_mutex = new ReentrantLock(true);
    private long lastNpcClick = 0;
    private final Map<Integer, Pair<Short, Short>> charInfo = new LinkedHashMap<>();
    private int client_increnement = 1;
    private MapleFarm farm;
    private ScheduledFuture<?> ping;
    private int authID = 0;
    public String sAccountToken = "";

    public ClientSocket(Channel c, int uSendSeq, int uRcvSeq) {
        super(c, uSendSeq, uRcvSeq);
    }

    public int getAuthID() {
        return authID;
    }

    public void setAuthID(int auth) {
        this.authID = auth;
    }

    @Override
    public void SendPacket(OutPacket oPacket) {
        
        if (ServerConstants.DEVELOPER_DEBUG_MODE) {
            if (SendPacketOpcode.eOp != null) {
                switch (SendPacketOpcode.eOp) {
                    case PrivateServerPacket:
                    case MobCtrlAck:
                    case StatChanged:
                    case NpcMove:
                    case MobMove:
                        if (ServerConstants.REDUCED_DEBUG_SPAM) break;
                    default:
                        System.err.println(String.format("[Send Operation] %s (%d) : %s", SendPacketOpcode.eOp.toString(), SendPacketOpcode.eOp.getValue(), oPacket.toString()));
                        break;
                }
            }
        }

        // Have to write the packet after checking for debug
        // Just incase the pipeline encodes it before the log printed lol. (that happens)
        super.SendPacket(oPacket);
    }

    public final Lock getNPCLock() {
        return npc_mutex;
    }

    public User getPlayer() {
        return player;
    }

    public void setPlayer(User player) {
        this.player = player;
    }

    public void createdChar(final int id) {
        allowedChar.add(id);
    }

    public final boolean login_Auth(final int id) {
        return allowedChar.contains(id);
    }

    public long getLoginAuthCookie() {
        return this.LoginAuthCookie;
    }

    public long generateLoginAuthCookie() {
        this.LoginAuthCookie = Randomizer.nextInt();

        return this.LoginAuthCookie;
    }

    public void startPing(Channel c) {
        ping = c.eventLoop().scheduleAtFixedRate(()
                -> c.writeAndFlush(CLogin.AliveReq()), 5, 5, TimeUnit.SECONDS);
    }

    public void cancelPingTask() {
        if (ping != null) {
            ping.cancel(true);
        }
    }

    public final List<User> loadCharacters(final int serverId) { // TODO make this less costly zZz
        final List<User> chars = new LinkedList<>();

        try (Connection con = Database.GetConnection()) {

            final Map<Integer, CardData> cardss = CharacterCardFactory.getInstance().loadCharacterCards(accId, serverId, con);
            for (final CharNameAndId cni : loadCharactersInternal(serverId, con)) {
                final User chr = User.loadCharFromDB(cni.id, this, false, cardss);
                chars.add(chr);
                charInfo.put(chr.getId(), new Pair<>(chr.getLevel(), chr.getJob())); // to be used to update charCards
                if (!login_Auth(chr.getId())) {
                    allowedChar.add(chr.getId());
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ClientSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
        return chars;
    }

    public User loadCharacterById(int playerId) {
        return User.loadCharFromDB(playerId, this, false);
    }

    public final void updateCharacterCards(final Map<Integer, Integer> cids) {
        if (charInfo.isEmpty()) { // no characters
            return;
        }

        try (Connection con = Database.GetConnection()) {

            try (PreparedStatement ps = con.prepareStatement("DELETE FROM `character_cards` WHERE `accid` = ?")) {
                ps.setInt(1, accId);
                ps.executeUpdate();
            } catch (Exception e) {
                LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", e);
            }
            try (PreparedStatement psu = con.prepareStatement("INSERT INTO `character_cards` (accid, worldid, characterid, position) VALUES (?, ?, ?, ?)")) {
                for (final Entry<Integer, Integer> ii : cids.entrySet()) {
                    final Pair<Short, Short> info = charInfo.get(ii.getValue()); // charinfo we can use here as characters are already loaded
                    if (info == null || ii.getValue() == 0 || !CharacterCardFactory.getInstance().canHaveCard(info.getLeft(), info.getRight())) {
                        continue;
                    }
                    psu.setInt(1, accId);
                    psu.setInt(2, world);
                    psu.setInt(3, ii.getValue());
                    psu.setInt(4, ii.getKey()); // position shouldn't matter much, will reset upon login
                    psu.executeUpdate();
                }
            } catch (Exception e) {
                LogHelper.SQL.get().info("[Client] There was an issue with something from the database:\n", e);
            }
        } catch (Exception e) {
            LogHelper.SQL.get().info("[Client] There was an issue with something from the database:\n", e);
        }

    }

    public List<String> loadCharacterNames(int serverId) {
        List<String> chars = new LinkedList<>();
        try (Connection con = Database.GetConnection()) {

            for (CharNameAndId cni : loadCharactersInternal(serverId, con)) {
                chars.add(cni.name);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ClientSocket.class.getName()).log(Level.SEVERE, null, ex);
        }

        return chars;
    }

    private List<CharNameAndId> loadCharactersInternal(int serverId, Connection con) {
        List<CharNameAndId> chars = new LinkedList<>();

        try (PreparedStatement ps = con.prepareStatement("SELECT id, name, gm FROM characters WHERE accountid = ? AND world = ? AND deletedAt is null")) {
            ps.setInt(1, accId);
            ps.setInt(2, serverId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    chars.add(new CharNameAndId(rs.getString("name"), rs.getInt("id")));
                    LoginServer.getLoginAuth(rs.getInt("id"));
                }
            }
        } catch (SQLException e) {
            LogHelper.SQL.get().info("[Client] Error loading characters from database \n", e);
        }
        return chars;
    }

    public boolean isLoggedIn() {
        return loggedIn && accId >= 0;
    }

    private Calendar getTempBanCalendar(ResultSet rs) throws SQLException {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(rs.getTimestamp("tempban").getTime());
        return cal;
    }

    public Calendar getTempBanCalendar() {
        return tempban;
    }

    public byte getBanReason() {
        return greason;
    }

    public String showBanReason(String AccountID, boolean permban) {
        boolean autoban = getTrueBanReason(AccountID).toLowerCase().equals("autoban") || getTrueBanReason(AccountID) == null;
        return showBanReason((byte) 0x7F, AccountID, permban, autoban, false);
    }

    public String showBanReason(String AccountID, boolean permban, boolean autoban) {
        return showBanReason((byte) 0x7F, AccountID, permban, autoban, false);
    }

    public String showBanReason(byte type, String AccountID, boolean permban, boolean autoban, boolean showId) {
        StringBuilder reason = new StringBuilder();
        reason.append("Your account ").append(AccountID).append(" has been blocked for ");
        switch (type) {
            case 1:
                reason.append("hacking or illegal use of third-party programs.");
                break;
            case 2:
                reason.append("using macro / auto-keyboard.");
                break;
            case 3:
                reason.append("illicit promotion and advertising.");
                break;
            case 4:
                reason.append("harassment.");
                break;
            case 5:
                reason.append("using profane language.");
                break;
            case 6:
                reason.append("scamming.");
                break;
            case 7:
                reason.append("misconduct.");
                break;
            case 8:
                reason.append("illegal cash transaction.");
                break;
            case 9:
                reason.append("illegal charging/funding. Please contact customer support for further details.");
                break;
            case 10:
                reason.append("temporary request. Please contact customer support for further details.");
                break;
            case 11:
                reason.append("impersonating GM.");
                break;
            case 12:
                reason.append("using illegal programs or violating the game policy.");
                break;
            case 13:
                reason.append("one of cursing, scamming, or illegal trading via Megaphones.");
                break;
            case 16:
            case 17:
            case 18:
                reason.append("Unknown reason 1.");
                break;
            case 19:
            case 20:
            case 21:
                reason.append("Unknown reason 2.");
                break;
            default:
                if (autoban) {
                    reason.append("System has detected hacking or illegal use of third-party programs.");
                } else if (showId) {
                    reason.append("MapleGM has blocked your account ").append(AccountID).append(" for the following reason: ").append(getTrueBanReason(AccountID)); //Default reason
                } else {
                    reason.append("Your account was blocked by the MapleStory GM's for ").append(getTrueBanReason(AccountID));
                }
                break;
        }
        reason.append(permban ? "\r\n\r\nThis ban will never be lifted." : "");
        return reason.toString();
    }

    public String getTrueBanReason(String name) {
        String ret = null;
        try (Connection con = Database.GetConnection()) {
            try (PreparedStatement ps = con.prepareStatement("SELECT banreason FROM accounts WHERE name = ?")) {
                ps.setString(1, name);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        ret = rs.getString(1);
                    }
                }
            } catch (SQLException ex) {
                LogHelper.SQL.get().info("[Client] Error getting ban reasons \n", ex);
            }
        } catch (SQLException ex) {
            LogHelper.SQL.get().info("[Client] Error getting ban reasons \n", ex);
        }

        return ret;
    }

    public boolean hasBannedIP() {
        boolean ret = false;

        try (Connection con = Database.GetConnection()) {
            try (PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM ipbans WHERE ? LIKE CONCAT(ip, '%')")) {
                ps.setString(1, getSessionIPAddress());
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    if (rs.getInt(1) > 0) {
                        ret = true;
                    }
                }
            } catch (SQLException ex) {
                LogHelper.SQL.get().info("[Client] Error checking IP bans \n", ex);
            }
        } catch (SQLException ex) {
            LogHelper.SQL.get().info("[Client] Error getting ban reasons \n", ex);
        }

        return ret;
    }

    public boolean hasBannedMac() {
        if (macs.isEmpty()) {
            return false;
        }
        boolean ret = false;
        int i;

        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM macbans WHERE mac IN (");
        for (i = 0; i < macs.size(); i++) {
            sql.append("?");
            if (i != macs.size() - 1) {
                sql.append(", ");
            }
        }
        sql.append(")");
        try (Connection con = Database.GetConnection()) {
            try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
                i = 0;
                for (String mac : macs) {
                    i++;
                    ps.setString(i, mac);
                }
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    if (rs.getInt(1) > 0) {
                        ret = true;
                    }
                }
            } catch (SQLException ex) {
                LogHelper.SQL.get().info("[Client] Error getting ban reasons \n", ex);
            }

        } catch (SQLException ex) {
            LogHelper.SQL.get().info("[Client] Error checking mac bans \n", ex);
        }
        return ret;
    }

    private void loadMacsIfNescessary() {
        if (macs.isEmpty()) {

            try (Connection con = Database.GetConnection()) {
                try (PreparedStatement ps = con.prepareStatement("SELECT macs FROM accounts WHERE id = ?")) {
                    ps.setInt(1, accId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            if (rs.getString("macs") != null) {
                                String[] macData;
                                macData = rs.getString("macs").split(", ");
                                for (String mac : macData) {
                                    if (!mac.equals("")) {
                                        macs.add(mac);
                                    }
                                }
                            }
                        } else {
                            rs.close();
                            ps.close();
                            throw new RuntimeException("No valid account associated with this client.");
                        }
                    }
                } catch (SQLException exp) {
                    LogHelper.SQL.get().info("[Client] Error loading macs from accounts\n", exp);
                }
            } catch (SQLException ex) {
                LogHelper.SQL.get().info("[Client] Error getting ban reasons \n", ex);
            }

        }
    }

    public void banMacs() {
        loadMacsIfNescessary();
        if (this.macs.size() > 0) {
            String[] macBans = new String[this.macs.size()];
            int z = 0;
            for (String mac : this.macs) {
                macBans[z] = mac;
                z++;
            }
            banMacs(macBans);
        }
    }

    public static void banMacs(String[] macs) {

        try (Connection con = Database.GetConnection()) {
            List<String> filtered = new LinkedList<>();
            try (PreparedStatement ps = con.prepareStatement("SELECT filter FROM macfilters")) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    filtered.add(rs.getString("filter"));
                }
                rs.close();
                ps.close();
            } catch (SQLException ex) {
                LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", ex);
            }

            try (PreparedStatement ps = con.prepareStatement("INSERT INTO macbans (mac) VALUES (?)")) {
                for (String mac : macs) {
                    boolean matched = false;
                    for (String filter : filtered) {
                        if (mac.matches(filter)) {
                            matched = true;
                            break;
                        }
                    }
                    if (!matched) {
                        ps.setString(1, mac);
                        try {
                            ps.executeUpdate();
                        } catch (SQLException e) {
                            LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", e);
                        }
                    }
                }
                ps.close();
            } catch (SQLException ex) {
                LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", ex);
            }
        } catch (SQLException ex) {
            LogHelper.SQL.get().info("[Client] Error getting ban reasons \n", ex);
        }

    }

    /**
     * Returns 0 on success, a state to be used for {@link CField#getLoginFailed(int)} otherwise.
     *
     * @return The state of the login.
     */
    public int finishLogin() {
        login_mutex.lock();
        try {
            final MapleClientLoginState state = getLoginState();
            if (state.getState() > MapleClientLoginState.Login_LoggedIn.getState()) { // already loggedin
                loggedIn = false;
                return 7;
            }
            updateLoginState(MapleClientLoginState.Login_LoggedIn, getSessionIPAddress());
        } finally {
            login_mutex.unlock();
        }
        return 0;
    }

    public void clearInformation() {
        accountName = null;
        accId = -1;
        secondPassword = null;
        gm = false;
        loggedIn = false;
        greason = (byte) 1;
        tempban = null;
        gender = (byte) -1;
        charInfo.clear();
    }

    public int AuthLogin(String name, int authID) {
        int loginok = 5;

        try (Connection con = Database.GetConnection()) {

            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM accounts WHERE authID = ?")) {
                ps.setInt(1, authID);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    final int banned = rs.getInt("banned");
                    final String oldSession = rs.getString("SessionIP");

                    accountName = name;
                    accId = rs.getInt("id");
                    secondPassword = rs.getString("2ndpassword");
                    gm = rs.getInt("gm") > 1; 
                    greason = rs.getByte("greason");
                    tempban = getTempBanCalendar(rs);
                    gender = rs.getByte("gender");

                    if (banned > 0 && gm) {
                        loginok = 3;
                    } else {
                        if (banned == -1) {
                            unban();
                        }
                        MapleClientLoginState loginstate = getLoginState();
                        if (loginstate.getState() > MapleClientLoginState.Login_NotLoggedIn.getState()) { // already loggedin
                            if (getSessionIPAddress().equals(oldSession) && oldSession != null && getPlayer() == null && CashShopServer.getPlayerStorage().getCharacterById(accId) == null) {
                                try (PreparedStatement ps2 = con.prepareStatement("UPDATE accounts SET loggedin = 0 WHERE name = ?")) {
                                    ps2.setString(1, name);
                                    ps2.executeUpdate();
                                    ps2.close();
                                    disconnect(true, false);
                                    //write(CWvsContext.broadcastMsg(1, "Your " + ServerConstants.SERVER_REFERENCE + " account has been successfully unstuck! You may now login normally."));
                                } catch (SQLException se) {
                                    LogHelper.SQL.get().info("[Client] There was an issue with something from the database:\n", se);
                                }
                                loginok = 0;
                            } else {
                                loggedIn = false;
                                loginok = 7;
                            }
                        } else {
                            if (ServerConstants.DEVMODE && !gm) {
                                loginok = 5;
                            } else {
                                loginok = 0;
                            }
                        }
                    }
                } else {
                    try (PreparedStatement psi = con.prepareStatement("INSERT INTO accounts (name, authID, nxCredit) VALUES (?, ?, 20000)")) {
                        psi.setString(1, name);
                        psi.setInt(2, authID);
                        psi.executeUpdate();
                        loginok = 23;
                    } catch (SQLException e) {
                        LogHelper.SQL.get().info("[Client] There was an issue with something from the database:\n", e);
                    }
                }
                rs.close();
                ps.close();
            } catch (SQLException e) {
                LogHelper.SQL.get().info("[Client] There was an issue with something from the database:\n", e);
            }
        } catch (SQLException se) {
            LogHelper.SQL.get().info("[Client] There was an issue with something from the database:\n", se);
        }

        return loginok;
    }

    public int LoginPassword(String name, String password) {
        int loginok = 5;

        try (Connection con = Database.GetConnection()) {

            try (PreparedStatement ps = con.prepareStatement("SELECT * FROM accounts WHERE name = ?")) {
                ps.setString(1, name);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    final int banned = rs.getInt("banned");
                    final String oldSession = rs.getString("SessionIP");
                    final String hashedPassword = rs.getString("password");

                    accountName = name;
                    accId = rs.getInt("id");
                    secondPassword = rs.getString("2ndpassword");
                    gm = rs.getInt("gm") > 1;
                    greason = rs.getByte("greason");
                    tempban = getTempBanCalendar(rs);
                    gender = rs.getByte("gender");

                    if (banned > 0 && gm) {
                        loginok = 3;
                    } else {
                        if (banned == -1) {
                            unban();
                        }
                        MapleClientLoginState loginstate = getLoginState();
                        if (loginstate.getState() > MapleClientLoginState.Login_NotLoggedIn.getState()) { // already loggedin
                            if (getSessionIPAddress().equals(oldSession) && oldSession != null && getPlayer() == null && CashShopServer.getPlayerStorage().getCharacterById(accId) == null) {
                                try (PreparedStatement ps2 = con.prepareStatement("UPDATE accounts SET loggedin = 0 WHERE name = ?")) {
                                    ps2.setString(1, name);
                                    ps2.executeUpdate();
                                    ps2.close();
                                    disconnect(true, false);
                                    //write(CWvsContext.broadcastMsg(1, "Your " + ServerConstants.SERVER_REFERENCE + " account has been successfully unstuck! You may now login normally."));
                                } catch (SQLException se) {
                                    LogHelper.SQL.get().info("[Client] There was an issue with something from the database:\n", se);
                                }
                                loginok = 0;
                            } else {
                                loggedIn = false;
                                loginok = 7;
                            }
                        } else {
                            if (ServerConstants.DEVMODE && !gm) {
                                loginok = 5;
                            } else if (crypto.BCrypt.checkpw(password, hashedPassword)) {
                                loginok = 0;
                            }
                        }
                    }
                }
                rs.close();
                ps.close();
            } catch (SQLException e) {
                LogHelper.SQL.get().info("[Client] There was an issue with something from the database:\n", e);
            }
        } catch (SQLException se) {
            LogHelper.SQL.get().info("[Client] There was an issue with something from the database:\n", se);
        }

        return loginok;
    }

    /**
     * Compares the unhashed input with the hashed password from the database
     *
     * @param in The unhashed passowrd
     * @param fromChannelServer specifies if this function is called from a channel server
     * @return boolean If the password matches
     */
    public boolean checkSecondPassword(String in, boolean fromChannelServer) {
        if (fromChannelServer) {
            if (secondPassword == null) { // second password isn't loaded if this is called from a channel server... need to cleanup this somehow
                try (Connection con = Database.GetConnection()) {

                    try (PreparedStatement ps = con.prepareStatement("SELECT * FROM accounts WHERE id = ? LIMIT 1;")) {
                        ps.setInt(1, accId);
                        ResultSet rs = ps.executeQuery();

                        if (rs.next()) {
                            secondPassword = rs.getString("2ndpassword");
                            ps.close();
                        }
                    } catch (SQLException exp) {
                        LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", exp);
                        return false;
                    }
                } catch (SQLException exp) {
                    LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", exp);
                    return false;
                }

            }
        }

        boolean allow = false;
        boolean updatePasswordHash = false;

        if (crypto.BCrypt.checkpw(in, secondPassword)) {
            allow = true;
        }
        return allow;
    }

    /**
     * Bans an offline client by character name.
     *
     * @param reason
     * @param characterName
     * @return If the ban is successful or not.
     */
    public static boolean banOfflineCharacter(String reason, String characterName) {
        try (Connection con = Database.GetConnection()) {

            try (PreparedStatement ps_get = con.prepareStatement("SELECT accountid FROM characters WHERE name = ? LIMIT 1")) {
                ps_get.setString(1, characterName);

                ResultSet rs = ps_get.executeQuery();
                if (rs.next()) {
                    try (PreparedStatement ps = con.prepareStatement("UPDATE accounts SET banned = ?, banreason = ? WHERE id = ? LIMIT 1")) {
                        ps.setInt(1, 1);
                        ps.setString(2, reason);
                        ps.setInt(3, rs.getInt(1));
                        ps.executeUpdate();

                        return true;
                    } catch (SQLException ex) {
                        LogHelper.SQL.get().info("[Client] Error banning account\n", ex);
                    }
                }
            } catch (SQLException ex) {
                LogHelper.SQL.get().info("[Client] Error banning offline account\n", ex);
            }
        } catch (SQLException ex) {
            LogHelper.SQL.get().info("[Client] Error banning offline account\n", ex);
        }

        return false;
    }

    public void ban(String reason, boolean banMacs, boolean banIPs) {
        try (Connection con = Database.GetConnection()) {

            if (banIPs) {
                try (PreparedStatement ps = con.prepareStatement("INSERT INTO ipbans VALUES (DEFAULT, ?)")) {
                    String[] ipSplit = super.GetIP().split(".");
                    ps.setString(1, ipSplit[0]);
                    ps.executeUpdate();
                    ps.close();
                } catch (SQLException ex) {
                    LogHelper.SQL.get().info("[Client] Error banning account\n", ex);
                }
            }

            try (PreparedStatement ps = con.prepareStatement("UPDATE accounts SET banned = ?, banreason = ? WHERE id = ? LIMIT 1")) {
                ps.setInt(1, 1);
                ps.setString(2, reason);
                ps.setInt(3, this.accId);
                ps.executeUpdate();
            } catch (SQLException ex) {
                LogHelper.SQL.get().info("[Client] Error banning account\n", ex);
            }
        } catch (SQLException ex) {
            LogHelper.SQL.get().info("[Client] Error banning account\n", ex);
        }

        if (banMacs) {
            banMacs();
        }
    }

    private void unban() {
        try (Connection con = Database.GetConnection()) {

            try (PreparedStatement ps = con.prepareStatement("UPDATE accounts SET banned = 0, banreason = '' WHERE id = ?")) {
                ps.setInt(1, accId);
                ps.executeUpdate();
            } catch (SQLException e) {
                LogHelper.SQL.get().info("[Client] Error unbanning character\n", e);
            }
        } catch (SQLException ex) {
            LogHelper.SQL.get().info("[Client] Error banning account\n", ex);
        }

    }

    public static byte unban(String charname) {
        try (Connection con = Database.GetConnection()) {

            try (PreparedStatement ps = con.prepareStatement("SELECT accountid from characters where name = ? AND deletedAt is null")) {
                ps.setString(1, charname);

                ResultSet rs = ps.executeQuery();
                if (!rs.next()) {
                    rs.close();
                    ps.close();
                    return -1;
                }
                final int accid = rs.getInt(1);
                rs.close();
                ps.close();

                try (PreparedStatement ps2 = con.prepareStatement("UPDATE accounts SET banned = 0, banreason = '' WHERE id = ?")) {
                    ps2.setInt(1, accid);
                    ps2.executeUpdate();
                    ps2.close();
                } catch (SQLException e) {
                    LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", e);
                    return -2;
                }
            } catch (SQLException e) {
                LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", e);
                return -2;
            }
        } catch (SQLException e) {
            LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", e);
            return -2;
        }

        return 0;
    }

    public void updateMacs(String macData) {
        macs.addAll(Arrays.asList(macData.split(", ")));
        StringBuilder newMacData = new StringBuilder();
        Iterator<String> iter = macs.iterator();
        while (iter.hasNext()) {
            newMacData.append(iter.next());
            if (iter.hasNext()) {
                newMacData.append(", ");
            }
        }

        try (Connection con = Database.GetConnection()) {

            try (PreparedStatement ps = con.prepareStatement("UPDATE accounts SET macs = ? WHERE id = ?")) {
                ps.setString(1, newMacData.toString());
                ps.setInt(2, accId);
                ps.executeUpdate();
            } catch (SQLException e) {
                LogHelper.SQL.get().info("[Client] Error saving MACs\n", e);
            }

        } catch (SQLException e) {
            LogHelper.SQL.get().info("[Client] Error saving MACs\n", e);
        }

    }

    public void setAccID(int id) {
        this.accId = id;
    }

    public int getAccID() {
        return this.accId;
    }

    public final void updateLoginState(final MapleClientLoginState newstate, final String SessionID) { // TODO hide?
        try (Connection con = Database.GetConnection()) {

            try (PreparedStatement ps = con.prepareStatement("UPDATE accounts SET loggedin = ?, SessionIP = ?, lastlogin = CURRENT_TIMESTAMP() WHERE id = ?")) {
                ps.setInt(1, newstate.getState());
                ps.setString(2, SessionID);
                ps.setInt(3, getAccID());
                ps.executeUpdate();
            } catch (SQLException sexp) {
                LogHelper.SQL.get().info("[Client]  error updating login state\n", sexp);
            }
        } catch (SQLException sexp) {
            LogHelper.SQL.get().info("[Client]  error updating login state\n", sexp);
        }
        if (newstate == MapleClientLoginState.Login_NotLoggedIn) {
            loggedIn = false;
            serverTransition = false;
        } else {
            serverTransition = (newstate == MapleClientLoginState.Login_ServerTransition
                    || newstate == MapleClientLoginState.ChangeChannel);
            loggedIn = !serverTransition;
        }

    }

    public final void updateSecondPassword() {
        try (Connection con = Database.GetConnection()) {

            try (PreparedStatement ps = con.prepareStatement("UPDATE `accounts` SET `2ndpassword` = ? WHERE id = ?")) {
                ps.setString(1, crypto.BCrypt.hashpw(secondPassword, crypto.BCrypt.gensalt()));
                ps.setInt(2, accId);
                ps.executeUpdate();
            } catch (SQLException e) {
                LogHelper.SQL.get().info("[Client] Error updating secondary password\n", e);
            }
        } catch (SQLException e) {
            LogHelper.SQL.get().info("[Client] Error updating secondary password\n", e);
        }

    }

    public final MapleClientLoginState getLoginState() { // TODO hide?

        try (Connection con = Database.GetConnection()) {

            try (PreparedStatement ps = con.prepareStatement("SELECT loggedin, lastlogin, banned, `birthday` + 0 AS `bday` FROM accounts WHERE id = ?")) {
                ps.setInt(1, getAccID());

                MapleClientLoginState state;
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next() || rs.getInt("banned") > 0) {
                        ps.close();
                        rs.close();
                        super.Close();
                    }
                    birthday = rs.getInt("bday");
                    state = MapleClientLoginState.getStateByInt(rs.getByte("loggedin"));

                    if (state == MapleClientLoginState.Login_ServerTransition
                            || state == MapleClientLoginState.ChangeChannel) {

                        if (rs.getTimestamp("lastlogin").getTime() + 20000 < System.currentTimeMillis()) { // connecting to chanserver timeout
                            state = MapleClientLoginState.Login_NotLoggedIn;
                            updateLoginState(state, getSessionIPAddress());
                        }
                    }
                }
                ps.close();
                loggedIn = state == MapleClientLoginState.Login_LoggedIn;

                return state;
            } catch (SQLException e) {
                LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", e);
                loggedIn = false;
                return MapleClientLoginState.NotFound;
            }
        } catch (SQLException e) {

            LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", e);
            loggedIn = false;
            return MapleClientLoginState.NotFound;
        }
    }

    public final boolean checkBirthDate(final int date) {
        return birthday == date;
    }

    public final void removalTask(boolean shutdown) {
        try {
            player.cancelBuffs();
            player.cancelAllDebuffs();
            if (player.getMarriageId() > 0) {
                final QuestStatus stat1 = player.getQuestNoAdd(Quest.getInstance(160001));
                final QuestStatus stat2 = player.getQuestNoAdd(Quest.getInstance(160002));
                if (stat1 != null && stat1.getCustomData() != null && (stat1.getCustomData().equals("2_") || stat1.getCustomData().equals("2"))) {
                    //dc in process of marriage
                    if (stat2 != null && stat2.getCustomData() != null) {
                        stat2.setCustomData("0");
                    }
                    stat1.setCustomData("3");
                }
            }
            if (player.getMapId() == GameConstants.JAIL) {
                final QuestStatus stat1 = player.getQuestNAdd(Quest.getInstance(GameConstants.JAIL_TIME));
                final QuestStatus stat2 = player.getQuestNAdd(Quest.getInstance(GameConstants.JAIL_QUEST));
                if (stat1.getCustomData() == null) {
                    stat1.setCustomData(String.valueOf(System.currentTimeMillis()));
                } else if (stat2.getCustomData() == null) {
                    stat2.setCustomData("0"); //seconds of jail
                } else { //previous seconds - elapsed seconds
                    int seconds = Integer.parseInt(stat2.getCustomData()) - (int) ((System.currentTimeMillis() - Long.parseLong(stat1.getCustomData())) / 1000);
                    if (seconds < 0) {
                        seconds = 0;
                    }
                    stat2.setCustomData(String.valueOf(seconds));
                }
            }
            player.changeRemoval(true);
            if (player.getEventInstance() != null) {
                player.getEventInstance().playerDisconnected(player, player.getId());
            }
            synchronized (this) {
                final IMaplePlayerShop shop = player.getPlayerShop();
                if (shop != null) {
                    shop.removeVisitor(player);
                    if (shop.isOwner(player)) {
                        if (shop.getShopType() == 1 && shop.isAvailable() && !shutdown) {
                            shop.setOpen(true);
                        } else {
                            shop.closeShop(true, !shutdown);
                            player.setPlayerShop(null);
                        }
                    }
                }
            }
            player.setMessenger(null);
            if (player.getMap() != null) {
                if (shutdown || (getChannelServer() != null && getChannelServer().isShutdown())) {
                    int questID = -1;
                    switch (player.getMapId()) {
                        case 240060200: //HT
                            questID = 160100;
                            break;
                        case 240060201: //ChaosHT
                            questID = 160103;
                            break;
                        case 280030000: //Zakum
                            questID = 160101;
                            break;
                        case 280030001: //ChaosZakum
                            questID = 160102;
                            break;
                        case 270050100: //PB
                            questID = 160101;
                            break;
                        case 105100300: //Balrog
                        case 105100400: //Balrog
                            questID = 160106;
                            break;
                        case 211070000: //VonLeon
                        case 211070100: //VonLeon
                        case 211070101: //VonLeon
                        case 211070110: //VonLeon
                            questID = 160107;
                            break;
                        case 551030200: //scartar
                            questID = 160108;
                            break;
                        case 271040100: //cygnus
                            questID = 160109;
                            break;
                        case 262030000:
                        case 262031300: // hilla
                            questID = 160110;
                            break;
                        case 272030400:
                            questID = 160111;
                            break;
                        case 105200310:
                            questID = 160112;
                            break;
                        case 105200710:
                            questID = 160113;
                            break;
                        case 105200610:
                            questID = 160114;
                            break;
                        case 105200210:
                            questID = 160115;
                            break;
                        case 105200110:
                            questID = 160116;
                            break;
                        case 105200510:
                            questID = 160117;
                            break;
                        case 105200410:
                            questID = 160118;
                            break;
                        case 105200810:
                            questID = 160119;
                            break;
                        case 950101010:
                            questID = 160120;
                            break;
                        case 401060200:
                            questID = 160121;
                            break;
                        case 863010700:
                            questID = 160122;
                            break;
                        case 807300110:
                            questID = 160124;
                            break;
                        case 350060400:
                            questID = 160125;
                            break;
                        case 401060100: // Normal Magnus
                            questID = 160126;
                            break;
                    }
                    if (questID > 0) {
                        player.getQuestNAdd(Quest.getInstance(questID)).setCustomData("0"); //reset the time.
                    }
                } else if (player.isAlive()) {
                    switch (player.getMapId()) {
                        case 541010100: //latanica
                        case 541020800: //krexel
                        case 220080001: //pap
                            player.getMap().addDisconnected(player.getId());
                            break;
                    }
                }
                player.getMap().removePlayer(player);
            }
        } catch (Exception e) {
            LogHelper.GENERAL_EXCEPTION.get().info("There was an account is currently stuck:\n{}", e);
        }
    }

    public final void disconnect(final boolean RemoveInChannelServer, final boolean fromCS) {
        disconnect(RemoveInChannelServer, fromCS, false);
    }

    public final void disconnect(final boolean RemoveInChannelServer, final boolean fromCS, final boolean shutdown) {
        if (player != null) {
            MapleMap map = player.getMap();
            final MapleParty party = player.getParty();
            final String namez = player.getName();
            final int idz = player.getId(), messengerid = player.getMessenger() == null ? 0 : player.getMessenger().getId(), gid = player.getGuildId(), fid = player.getFamilyId();
            final BuddyList bl = player.getBuddylist();
            final MaplePartyCharacter chrp = new MaplePartyCharacter(player);
            final MapleMessengerCharacter chrm = new MapleMessengerCharacter(player);
            final MapleGuildCharacter chrg = player.getMGC();
            final MapleFamilyCharacter chrf = player.getMFC();
            player.changeRemoval();
            removalTask(shutdown);
            LoginServer.getLoginAuth(player.getId());
            player.saveToDB(true, fromCS);
            if (shutdown) {
                player = null;
                receiving = false;
                return;
            }

            if (!fromCS) {
                final ChannelServer ch = ChannelServer.getInstance(map == null ? channel : map.getChannel());
                final int chz = World.Find.findChannel(idz);
                if (chz < -1) {
                    disconnect(RemoveInChannelServer, true);//u lie
                    return;
                }
                try {
                    if (chz == -1 || ch == null || ch.isShutdown()) {
                        player = null;
                        return;//no idea
                    }
                    if (messengerid > 0) {
                        World.Messenger.leaveMessenger(messengerid, chrm);
                    }
                    if (party != null) {
                        chrp.setOnline(false);
                        World.Party.updateParty(party.getId(), PartyOperation.LOG_ONOFF, chrp);
                        if (map != null && party.getLeader().getId() == idz) {
                            MaplePartyCharacter lchr = null;
                            for (MaplePartyCharacter pchr : party.getMembers()) {
                                if (pchr != null && map.getCharacterById(pchr.getId()) != null && (lchr == null || lchr.getLevel() < pchr.getLevel())) {
                                    lchr = pchr;
                                }
                            }
                            if (lchr != null) {
                                World.Party.updateParty(party.getId(), PartyOperation.CHANGE_LEADER_DC, lchr);
                            }
                        }
                    }
                    if (bl != null) {
                        if (!serverTransition) {
                            World.WorldBuddy.loggedOff(namez, idz, channel, bl.getBuddyIds());
                        } else { // Change channel
                            World.WorldBuddy.loggedOn(namez, idz, channel, bl.getBuddyIds());
                        }
                    }
                    if (gid > 0 && chrg != null) {
                        World.Guild.setGuildMemberOnline(chrg, false, -1);
                    }
                    if (fid > 0 && chrf != null) {
                        World.Family.setFamilyMemberOnline(chrf, false, -1);
                    }
                } catch (final Exception e) {
                    LogHelper.GENERAL_EXCEPTION.get().info("There was an account is currently stuck:\n{}", e);
                } finally {
                    if (RemoveInChannelServer && ch != null) {
                        ch.removePlayer(idz, namez);
                    }
                    player = null;
                }
            } else {
                final int ch = World.Find.findChannel(idz);
                if (ch > 0) {
                    disconnect(RemoveInChannelServer, false);//u lie
                    return;
                }
                try {
                    if (party != null) {
                        chrp.setOnline(false);
                        World.Party.updateParty(party.getId(), PartyOperation.LOG_ONOFF, chrp);
                    }
                    if (!serverTransition) {
                        World.WorldBuddy.loggedOff(namez, idz, channel, bl.getBuddyIds());
                    } else { // Change channel
                        World.WorldBuddy.loggedOn(namez, idz, channel, bl.getBuddyIds());
                    }
                    if (gid > 0 && chrg != null) {
                        World.Guild.setGuildMemberOnline(chrg, false, -1);
                    }
                    if (fid > 0 && chrf != null) {
                        World.Family.setFamilyMemberOnline(chrf, false, -1);
                    }
                    if (player != null) {
                        player.setMessenger(null);
                    }
                } catch (final Exception e) {
                    LogHelper.GENERAL_EXCEPTION.get().info("There was an account is currently stuck:\n{}", e);
                } finally {
                    if (RemoveInChannelServer && ch > 0) {
                        CashShopServer.getPlayerStorage().deregisterPlayer(idz, namez);
                    }
                    player = null;
                }
            }
        }
        if (!serverTransition && isLoggedIn()) {
            updateLoginState(MapleClientLoginState.Login_NotLoggedIn, getSessionIPAddress());
        }
        engines.clear();
    }

    public final String getSessionIPAddress() {
        return super.GetIP();
    }

    public final boolean CheckIPAddress() {
        if (this.accId < 0) {
            return false;
        }
        try (Connection con = Database.GetConnection()) {

            boolean canlogin = false;
            try (PreparedStatement ps = con.prepareStatement("SELECT SessionIP, banned FROM accounts WHERE id = ?")) {
                ps.setInt(1, this.accId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        final String sessionIP = rs.getString("SessionIP");
                        if (sessionIP != null) { // Probably a login proced skipper?
                            canlogin = getSessionIPAddress().equals(sessionIP.split(":")[0]);
                        }
                        if (rs.getInt("banned") > 0) {
                            canlogin = false; //canlogin false = close client
                        }
                    }
                }
            }
            return canlogin;
        } catch (final SQLException e) {
            LogHelper.SQL.get().info("[Client] Failed checking IP adress:\n", e);
        }

        return true;
    }

    public final void DebugMessage(final StringBuilder sb) {
        sb.append(this.GetIP());
        sb.append(" loggedin: ");
        sb.append(isLoggedIn());
        sb.append(" has char: ");
        sb.append(getPlayer() != null);
    }

    public final int getChannel() {
        return channel;
    }

    public final ChannelServer getChannelServer() {
        return ChannelServer.getInstance(channel);
    }

    public final int deleteCharacter(final int cid) {
        try (Connection con = Database.GetConnection()) {

            try (PreparedStatement ps = con.prepareStatement("UPDATE characters SET deletedAt = NOW() WHERE id = ? AND accountid = ? LIMIT 1")) {
                ps.setInt(1, cid);
                ps.setInt(2, accId);

                ps.execute();
            } catch (SQLException exp) {
                LogHelper.SQL.get().info("There seems to be a issue deleting the character:\n{}", exp);
                return 10;
            }

        } catch (SQLException exp) {

            LogHelper.SQL.get().info("There seems to be a issue deleting the character:\n{}", exp);
            return 10;
        }
        return 0;
    }

    public final byte getGender() {
        return gender;
    }

    public final void setGender(final byte gender) {
        this.gender = gender;
    }

    public final String getSecondPassword() {
        return secondPassword;
    }

    public final void setSecondPassword(final String secondPassword) {
        this.secondPassword = secondPassword;
    }

    public final String getAccountName() {
        return accountName;
    }

    public final void setAccountName(final String accountName) {
        this.accountName = accountName;
    }

    public final void setChannel(final int channel) {
        this.channel = channel;
    }

    public final int getWorld() {
        return world;
    }

    public final void setWorld(final int world) {
        this.world = world;
    }

    public final int getLatency() {
        //return (int) (lastPong - lastPing);
        return (int) (lastPong / lastPing);
    }

    public final long getLastPong() {
        return lastPong;
    }

    public final long getLastPing() {
        return lastPing;
    }

    public final void pongReceived() {
        lastPong = System.currentTimeMillis();
    }

    public static String getLogMessage(final ClientSocket cfor, final String message) {
        return getLogMessage(cfor, message, new Object[0]);
    }

    public static String getLogMessage(final User cfor, final String message) {
        return getLogMessage(cfor == null ? null : cfor.getClient(), message);
    }

    public static String getLogMessage(final User cfor, final String message, final Object... parms) {
        return getLogMessage(cfor == null ? null : cfor.getClient(), message, parms);
    }

    public static String getLogMessage(final ClientSocket cfor, final String message, final Object... parms) {
        final StringBuilder builder = new StringBuilder();
        if (cfor != null) {
            if (cfor.getPlayer() != null) {
                builder.append("<");
                builder.append(MapleCharacterUtil.makeMapleReadable(cfor.getPlayer().getName()));
                builder.append(" (cid: ");
                builder.append(cfor.getPlayer().getId());
                builder.append(")> ");
            }
            if (cfor.getAccountName() != null) {
                builder.append("(Account: ");
                builder.append(cfor.getAccountName());
                builder.append(") ");
            }
        }
        builder.append(message);
        int start;
        for (final Object parm : parms) {
            start = builder.indexOf("{}");
            builder.replace(start, start + 2, parm.toString());
        }
        return builder.toString();
    }

    public static int findAccIdForCharacterName(final String charName) {
        int ret = -1;
        try (Connection con = Database.GetConnection()) {

            try (PreparedStatement ps = con.prepareStatement("SELECT accountid FROM characters WHERE name = ? AND deletedAt is null")) {
                ps.setString(1, charName);
                try (ResultSet rs = ps.executeQuery()) {
                    ret = -1;
                    if (rs.next()) {
                        ret = rs.getInt("accountid");
                    }
                }
            } catch (SQLException ex) {
                LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", ex);
            }
        } catch (SQLException ex) {
            LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", ex);
        }

        return ret;
    }

    public final Set<String> getMacs() {
        return Collections.unmodifiableSet(macs);
    }

    public final boolean isGm() {
        return gm;
    }

    public final void setScriptEngine(final String name, final ScriptEngine e) {
        engines.put(name, e);
    }

    public final ScriptEngine getScriptEngine(final String name) {
        return engines.get(name);
    }

    public final void removeScriptEngine(final String name) {
        engines.remove(name);
    }

    public final ScheduledFuture<?> getIdleTask() {
        return idleTask;
    }

    public final void setIdleTask(final ScheduledFuture<?> idleTask) {
        this.idleTask = idleTask;

    }

    protected static final class CharNameAndId {

        public final String name;
        public final int id;

        public CharNameAndId(final String name, final int id) {
            super();
            this.name = name;
            this.id = id;
        }
    }

    public static byte unbanIPMacs(String charname) {
        try (Connection con = Database.GetConnection()) {

            PreparedStatement ps = con.prepareStatement("SELECT accountid from characters where name = ? AND deletedAt is null");
            ps.setString(1, charname);

            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
                return -1;
            }
            final int accid = rs.getInt(1);
            rs.close();
            ps.close();

            ps = con.prepareStatement("SELECT * FROM accounts WHERE id = ?");
            ps.setInt(1, accid);
            rs = ps.executeQuery();
            if (!rs.next()) {
                rs.close();
                ps.close();
                return -1;
            }
            final String sessionIP = rs.getString("sessionIP");
            final String macs = rs.getString("macs");
            rs.close();
            ps.close();
            byte ret = 0;
            if (sessionIP != null) {
                try (PreparedStatement psa = con.prepareStatement("DELETE FROM ipbans WHERE ip like ?")) {
                    psa.setString(1, sessionIP);
                    psa.execute();
                }
                ret++;
            }
            if (macs != null) {
                String[] macz;
                macz = macs.split(", ");
                for (String mac : macz) {
                    if (!mac.equals("")) {
                        try (PreparedStatement psa = con.prepareStatement("DELETE FROM macbans WHERE mac = ?")) {
                            psa.setString(1, mac);
                            psa.execute();
                        }
                    }
                }
                ret++;
            }

            return ret;
        } catch (SQLException e) {
            LogHelper.SQL.get().info("[SQL] There was an issue with something from the database:\n", e);

            return -2;
        }
    }

    public boolean isMonitored() {
        return monitored;
    }

    public void setMonitored(boolean m) {
        this.monitored = m;
    }

    public boolean isReceiving() {
        return receiving;
    }

    public void setReceiving(boolean m) {
        this.receiving = m;
    }

    public boolean canClickNPC() {
        return lastNpcClick + 500 < System.currentTimeMillis();
    }

    public void setClickedNPC() {
        lastNpcClick = System.currentTimeMillis();
    }

    public void removeClickedNPC() {
        lastNpcClick = 0;
    }

    public final Timestamp getCreated() { // TODO hide?
        try (Connection con = Database.GetConnection()) {

            PreparedStatement ps;
            ps = con.prepareStatement("SELECT createdat FROM accounts WHERE id = ?");
            ps.setInt(1, getAccID());
            Timestamp ret;
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    rs.close();
                    ps.close();
                    return null;
                }
                ret = rs.getTimestamp("createdat");
            }
            ps.close();

            return ret;
        } catch (SQLException e) {

            LogHelper.SQL.get().info("There seems to be a issue deleting the character:\n{}", e);
            return null;
        }
    }

    public String getTempIP() {
        return tempIP;
    }

    public void setTempIP(String s) {
        this.tempIP = s;
    }

    public void setUsername(String what) {
        this.accountName = what;
    }

    public int getNextClientIncrenement() {
        int result = client_increnement;
        client_increnement++;
        return result;
    }

    public void setFarm(MapleFarm farm) {
        this.farm = farm;
    }

    public MapleFarm getFarm() {
        if (farm == null) {
            return MapleFarm.getDefault(35549721, this, "Creating...");
            //MapleFarm farm2 = MapleFarm.getDefault(35549721, this, "AstralMS");
            //farm2.setLevel(1);
            //return farm2;
        }
        return farm;
    }
}
