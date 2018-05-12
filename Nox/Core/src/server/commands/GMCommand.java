package server.commands;

import client.*;
import client.anticheat.ReportType;
import client.inventory.*;
import constants.GameConstants;
import constants.InventoryConstants;
import constants.ServerConstants;
import constants.ServerConstants.PlayerGMRank;
import handling.world.CheaterData;
import handling.world.World;
import scripting.provider.*;
import server.*;
import server.Timer.*;
import server.life.LifeFactory;
import server.life.Mob;
import server.maps.*;
import server.maps.objects.User;
import server.maps.objects.Pet;
import service.ChannelServer;
import tools.Pair;
import tools.StringUtil;
import tools.packet.CField;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;
import static tools.StringUtil.getOptionalIntArg;
import tools.Utility;
import tools.packet.WvsContext;

/**
 * GameMaster Commands
 * @author Mazen Massoud
 */
public class GMCommand {

    public static PlayerGMRank getPlayerLevelRequired() {
        return PlayerGMRank.GM;
    }
    
    public static class ReloadScript extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            for (ScriptType type : ScriptType.values()) {
                AbstractScriptManager.reloadCachedScript(type);
            }
            c.getPlayer().dropMessage(5, "Scripts have been reloaded.");
            return 1;
        }
    }

    public static class SetPlayer extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            c.getPlayer().setGM((byte) 0);
            c.getPlayer().dropMessage(6, "You have removed the Game Master privileges from your current character.");
            return 1;
        }
    }

    public static class GodMode extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            User pPlayer = c.getPlayer();

            if (pPlayer.hasGodMode()) {
                pPlayer.toggleGodMode(false);
                pPlayer.dropMessage(5, "God mode has been disabled.");
            } else {
                pPlayer.toggleGodMode(true);
                pPlayer.dropMessage(5, "God mode is now enabled.");
            }
            return 0;
        }
    }

    public static class Info extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            User pUser = null;
            int nOnline = 0;

            for (int i = 1; i <= ChannelServer.getChannelCount(); i++) {
                pUser = ChannelServer.getInstance(i).getPlayerStorage().getCharacterByName(splitted[1]);
                if (pUser != null) {
                    break;
                }
            }
            if (pUser == null) {
                c.getPlayer().dropMessage(5, "Sorry, the specified user can't be found.");
            }
            for (int i = 1; i <= ChannelServer.getChannelCount(); i++) {
                nOnline += ChannelServer.getInstance(i).getPlayerStorage().getAllCharacters().size();
            }
            if (pUser.getGMLevel() > c.getPlayer().getGMLevel()) {
                c.SendPacket(CField.NPCPacket.getNPCTalk(9010000, NPCChatType.OK, "You do not have permission to perform this action.", NPCChatByType.NPC_Cancellable));
                return 0;
            }
            
            String sMessage = "#dMazen's Super " + ServerConstants.SERVER_NAME + " Dox System#k\t\t\t\t\t\t\t#b(" + nOnline + " ONLINE)#k\r\n"
                + "Our technologies have been working to provide you with the following #rsecret#k information about the victim.\r\n\r\n"
                + "Account Username: #r" + pUser.getClient().getAccountName() + "#k\r\n" // Ping (" + oUser.getClient().getLatency() + ")" 
                + "IP Address: #r" + pUser.getClient().getSessionIPAddress() + "#k\r\n\r\n"
                + "Character: #r" + pUser.getName() + "#k\r\n"
                + "Job: #r" + MapleJob.getName(MapleJob.getById(pUser.getJob())) + " (" + pUser.getJob() + ")#k\r\n\r\n"
                + "Location: #r#m" + pUser.getMap().getId() + "##k\r\n"
                + "Map ID: #r" + pUser.getMap().getId() + "#k\r\n"
                + "Pos. X: #r" + pUser.getPosition().x + "#k / Pos. Y: #r" + pUser.getPosition().y + "#k\r\n\r\n"
                + "HP: #r" + pUser.getStat().getHp() + "#k / #r" + pUser.getStat().getCurrentMaxHp() + "#k\r\n"
                + "MP: #r" + pUser.getStat().getMp() + "#k / #r" + pUser.getStat().getCurrentMaxMp(pUser.getJob()) + "#k\r\n"
                + "Strength: #r" + pUser.getStat().getStr() + "#k\r\n"
                + "Dexterity: #r" + pUser.getStat().getDex() + "#k\r\n"
                + "Intelligence: #r" + pUser.getStat().getInt() + "#k\r\n"
                + "Luck: #r" + pUser.getStat().getLuk() + "#k\r\n\r\n"
                + "Weapon Attack: #r" + pUser.getStat().getTotalWatk() + "#k / Magic Attack: #r" + pUser.getStat().getTotalMagic() + "#k\r\n"
                + "Damager Percent: #r" + pUser.getStat().dam_r + "#k / Avoid Rate: #r" + pUser.getStat().avoidabilityRate + "#k\r\n\r\n"
                + "Experience: #r" + pUser.getExp() + "#k\r\n"
                + "Mesos: #r" + pUser.getMeso() + "#k\r\n"
                + "Vote Points: #r" + pUser.getVPoints() + "#k / Donor Credits: #r" + pUser.getDPoints() + "#k\r\n"
                + "Maple Points: #r" + pUser.getCSPoints(2) + "#k / Currently Trading: #r" + (pUser.getTrade() != null) + "#k\r\n";
            c.SendPacket(CField.NPCPacket.getNPCTalk(9010000, NPCChatType.OK, sMessage, NPCChatByType.NPC_Cancellable));
            return 1;
        }
    }

    public static class GiveEP extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            if (splitted.length < 3) {
                c.getPlayer().dropMessage(6, "Need playername and amount.");
                return 0;
            }
            User chrs = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (chrs == null) {
                c.getPlayer().dropMessage(6, "Make sure they are in the correct channel");
            } else {
                chrs.setPoints(chrs.getPoints() + Integer.parseInt(splitted[2]));
                c.getPlayer().dropMessage(6, splitted[1] + " has " + chrs.getPoints() + " points, after giving " + splitted[2] + ".");
            }
            return 1;
        }
    }

    public static class GiveSP extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().setRemainingSp(CommandProcessorUtil.getOptionalIntArg(splitted, 1, 1));
                c.getPlayer().updateSingleStat(MapleStat.AVAILABLESP, 0);
                c.getPlayer().dropMessage(5, "Character (" + splitted[1] + ") has been given " + Integer.parseInt(splitted[2]) + "Skill Points.");
            } else {
                User pPlayer = Utility.requestCharacter(splitted[1]);
                if (pPlayer != null) {
                    pPlayer.setRemainingSp(CommandProcessorUtil.getOptionalIntArg(splitted, 1, 1));
                    pPlayer.updateSingleStat(MapleStat.AVAILABLESP, 0);
                    c.getPlayer().dropMessage(5, "Character (" + splitted[1] + ") has been given " + Integer.parseInt(splitted[2]) + "Skill Points.");
                } else {
                    c.getPlayer().dropMessage(5, "Character (" + splitted[1] + ") could not be found.");
                }
            }
            return 1;
        }
    }

    public static class HyperSkills extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            User oPlayer;
            if (splitted.length > 1) {
                oPlayer = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            } else {
                oPlayer = c.getPlayer();
            }

            oPlayer.giveHyperSkills();
            c.getPlayer().dropMessage(5, "Character (" + splitted[1] + ") has had all their hyper skills maximized.");
            return 1;
        }
    }

    public static class MaxSkills extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            User oPlayer;
            if (splitted.length > 1 && c.getPlayer().isAdmin()) {
                oPlayer = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            } else {
                oPlayer = c.getPlayer();
            }

            HashMap<Skill, SkillEntry> skillBook = new HashMap<>();
            for (Skill xSkill : SkillFactory.getAllSkills()) {
                if (GameConstants.isApplicableSkill(xSkill.getId()) && xSkill.canBeLearnedBy(oPlayer.getJob()) && !xSkill.isInvisible()) { // No additional skills.
                    skillBook.put(xSkill, new SkillEntry((byte) xSkill.getMaxLevel(), (byte) xSkill.getMaxLevel(), SkillFactory.getDefaultSExpiry(xSkill)));
                }
            }

            c.getPlayer().changeSkillsLevel(skillBook);
            c.getPlayer().dropMessage(5, "Character (" + splitted[1] + ") has had all their applicable skills maximized.");
            return 1;
        }
    }
    
    public static class KillMap extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            for (User map : c.getPlayer().getMap().getCharacters()) {
                if (map != null && !map.isIntern()) {
                    map.getStat().setHp((short) 0, map);
                    map.getStat().setMp((short) 0, map);
                    map.updateSingleStat(MapleStat.HP, 0);
                    map.updateSingleStat(MapleStat.MP, 0);
                }
            }
            return 1;
        }
    }

    public static class LevelTo extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            while (c.getPlayer().getLevel() < Integer.parseInt(splitted[1])) {
                if (c.getPlayer().getLevel() < 255) {
                    c.getPlayer().levelUp();
                }
            }
            return 1;
        }
    }
    
    public static class Reports extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            List<CheaterData> cheaters = World.getReports();
            for (int x = cheaters.size() - 1; x >= 0; x--) {
                CheaterData cheater = cheaters.get(x);
                c.getPlayer().dropMessage(6, cheater.getInfo());
            }
            return 1;
        }
    }

    public static class ClearReport extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            if (splitted.length < 3) {
                StringBuilder ret = new StringBuilder("report [ign] [all/");
                for (ReportType type : ReportType.values()) {
                    ret.append(type.theId).append('/');
                }
                ret.setLength(ret.length() - 1);
                c.getPlayer().dropMessage(6, ret.append(']').toString());
                return 0;
            }
            final User victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (victim == null) {
                c.getPlayer().dropMessage(5, "Does not exist");
                return 0;
            }
            final ReportType type = ReportType.getByString(splitted[2]);
            if (type != null) {
                victim.clearReports(type);
            } else {
                victim.clearReports();
            }
            c.getPlayer().dropMessage(5, "Done.");
            return 1;
        }
    }

    public static class ClearDrops extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            c.getPlayer().getMap().removeDrops();
            return 1;
        }
    }

    public static class Job extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            User oPlayer;
            int nJob;
            if (splitted.length == 3) {
                oPlayer = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
                nJob = Integer.parseInt(splitted[2]);
            } else {
                oPlayer = c.getPlayer();
                nJob = Integer.parseInt(splitted[1]);
            }

            if (oPlayer == null) {
                c.getPlayer().dropMessage(5, "The character name you have entered was not found.");
                return 0;
            } else if (!MapleJob.isExist(nJob)) {
                c.getPlayer().dropMessage(5, "You have entered an invalid Job ID.");
                return 0;
            }

            oPlayer.changeJob((short) nJob);
            oPlayer.setSubcategory(oPlayer.getSubcategory());
            c.getPlayer().dropMessage(5, "Character (" + oPlayer.getName() + ") has had their job changed to " + MapleJob.getName(MapleJob.getById(nJob)) + ".");
            return 1;
        }
    }

    public static class KillNear extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            MapleMap map = c.getPlayer().getMap();
            List<MapleMapObject> players = map.getMapObjectsInRange(c.getPlayer().getPosition(), (double) 25000, Arrays.asList(MapleMapObjectType.PLAYER));
            for (MapleMapObject closeplayers : players) {
                User playernear = (User) closeplayers;
                if (playernear.isAlive() && playernear != c.getPlayer() && playernear.getJob() != 910) {
                    playernear.setHp(0);
                    playernear.updateSingleStat(MapleStat.HP, 0);
                    playernear.dropMessage(5, "You were too close to the MapleGM.");
                }
            }
            return 1;
        }
    }

    public static class Heal extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            c.getPlayer().getStat().heal(c.getPlayer());
            c.getPlayer().dispelDebuffs();
            return 0;
        }
    }

    public static class HealMap extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            User player = c.getPlayer();
            for (User mch : player.getMap().getCharacters()) {
                if (mch != null) {
                    mch.getStat().setHp(mch.getStat().getMaxHp(), mch);
                    mch.updateSingleStat(MapleStat.HP, mch.getStat().getMaxHp());
                    mch.getStat().setMp(mch.getStat().getMaxMp(), mch);
                    mch.updateSingleStat(MapleStat.MP, mch.getStat().getMaxMp());
                    mch.dispelDebuffs();
                }
            }
            return 1;
        }
    }

    public static class ClearInv extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            User player = c.getPlayer();
            if (splitted.length < 2 || player.hasBlockedInventory()) {
                c.getPlayer().dropMessage(5, "!clearinv <eq / use / setup / etc / cash / all >");
                return 0;
            } else {
                MapleInventoryType type;
                if (splitted[1].equalsIgnoreCase("eq")) {
                    type = MapleInventoryType.EQUIP;
                } else if (splitted[1].equalsIgnoreCase("use")) {
                    type = MapleInventoryType.USE;
                } else if (splitted[1].equalsIgnoreCase("setup")) {
                    type = MapleInventoryType.SETUP;
                } else if (splitted[1].equalsIgnoreCase("etc")) {
                    type = MapleInventoryType.ETC;
                } else if (splitted[1].equalsIgnoreCase("cash")) {
                    type = MapleInventoryType.CASH;
                } else if (splitted[1].equalsIgnoreCase("all")) {
                    type = null;
                } else {
                    c.getPlayer().dropMessage(5, "Invalid. @clearslot <eq / use / setup / etc / cash / all >");
                    return 0;
                }
                if (type == null) { //All, a bit hacky, but it's okay 
                    MapleInventoryType[] invs = {MapleInventoryType.EQUIP, MapleInventoryType.USE, MapleInventoryType.SETUP, MapleInventoryType.ETC, MapleInventoryType.CASH};
                    for (MapleInventoryType t : invs) {
                        type = t;
                        MapleInventory inv = c.getPlayer().getInventory(type);
                        byte start = -1;
                        for (byte i = 0; i < inv.getSlotLimit(); i++) {
                            if (inv.getItem(i) != null) {
                                start = i;
                                break;
                            }
                        }
                        if (start == -1) {
                            c.getPlayer().dropMessage(5, "There are no items in that inventory.");
                            return 0;
                        }
                        int end = 0;
                        for (byte i = start; i < inv.getSlotLimit(); i++) {
                            if (inv.getItem(i) != null) {
                                MapleInventoryManipulator.removeFromSlot(c, type, i, inv.getItem(i).getQuantity(), true);
                            } else {
                                end = i;
                                break;//Break at first empty space. 
                            }
                        }
                        c.getPlayer().dropMessage(5, "Cleared slots " + start + " to " + end + ".");
                    }
                } else {
                    MapleInventory inv = c.getPlayer().getInventory(type);
                    byte start = -1;
                    for (byte i = 0; i < inv.getSlotLimit(); i++) {
                        if (inv.getItem(i) != null) {
                            start = i;
                            break;
                        }
                    }
                    if (start == -1) {
                        c.getPlayer().dropMessage(5, "There are no items in that inventory.");
                        return 0;
                    }
                    byte end = 0;
                    for (byte i = start; i < inv.getSlotLimit(); i++) {
                        if (inv.getItem(i) != null) {
                            MapleInventoryManipulator.removeFromSlot(c, type, i, inv.getItem(i).getQuantity(), true);
                        } else {
                            end = i;
                            break;//Break at first empty space. 
                        }
                    }
                    c.getPlayer().dropMessage(5, "Cleared slots " + start + " to " + end + ".");
                }
                return 1;
            }
        }
    }

    public static class WarpMap extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            try {
                final MapleMap target = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(splitted[1]));
                if (target == null) {
                    c.getPlayer().dropMessage(6, "Map does not exist");
                    return 0;
                }
                final MapleMap from = c.getPlayer().getMap();
                for (User chr : from.getCharacters()) {
                    chr.changeMap(target, target.getPortal(0));
                }
            } catch (NumberFormatException e) {
                c.getPlayer().dropMessage(5, "Error: " + e.getMessage());
                return 0; //assume drunk GM
            }
            return 1;
        }
    }

    public static class KillAll extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            MapleMap map = c.getPlayer().getMap();
            double range = Double.POSITIVE_INFINITY;

            if (splitted.length > 1) {
                int irange = Integer.parseInt(splitted[1]);
                if (splitted.length <= 2) {
                    range = irange * irange;
                } else {
                    map = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(splitted[2]));
                }
            }
            if (map == null) {
                c.getPlayer().dropMessage(6, "Map does not exist");
                return 0;
            }
            Mob mob;
            for (MapleMapObject monstermo : map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.MONSTER))) {
                mob = (Mob) monstermo;
                if (!mob.getStats().isBoss() || mob.getStats().isPartyBonus() || c.getPlayer().isGM()) {
                    map.killMonster(mob, c.getPlayer(), false, false, (byte) 1);
                }
            }
            return 1;
        }
    }

    public static class WarpHere extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            User pTarget = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (!c.getPlayer().isAdmin() && pTarget.getGMLevel() > c.getPlayer().getGMLevel()) {
                c.getPlayer().dropMessage(5, "You do not have permission to warp this character to your location.");
                return 0;
            }
            if (pTarget != null) {
                if (c.getPlayer().inPVP() || (!c.getPlayer().isGM() && (pTarget.isInBlockedMap() || pTarget.isGM()))) {
                    c.getPlayer().dropMessage(5, "Please try again later.");
                    return 0;
                }
                pTarget.changeMap(c.getPlayer().getMap(), c.getPlayer().getMap().findClosestPortal(c.getPlayer().getTruePosition()));
            } else {
                int ch = World.Find.findChannel(splitted[1]);
                if (ch < 0) {
                    c.getPlayer().dropMessage(5, "Specified character could not be found.");
                    return 0;
                }
                pTarget = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(splitted[1]);
                if (pTarget == null || pTarget.inPVP() || (!c.getPlayer().isGM() && (pTarget.isInBlockedMap() || pTarget.isGM()))) {
                    c.getPlayer().dropMessage(5, "Please try again later.");
                    return 0;
                }
                c.getPlayer().dropMessage(5, "The specified character is cross changing channel.");
                pTarget.dropMessage(5, "[" + ServerConstants.SERVER_NAME + "] Changing Channels.");
                if (pTarget.getMapId() != c.getPlayer().getMapId()) {
                    final MapleMap mapp = pTarget.getClient().getChannelServer().getMapFactory().getMap(c.getPlayer().getMapId());
                    pTarget.changeMap(mapp, mapp.findClosestPortal(c.getPlayer().getTruePosition()));
                }
                pTarget.changeChannel(c.getChannel());
            }
            return 1;
        }
    }

    public static class Warp extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            User victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (victim != null && c.getPlayer().getGMLevel() >= victim.getGMLevel() && !victim.inPVP() && !c.getPlayer().inPVP()) {
                if (splitted.length == 2) {
                    c.getPlayer().changeMap(victim.getMap(), victim.getMap().findClosestSpawnpoint(victim.getTruePosition()));
                } else {
                    MapleMap target = ChannelServer.getInstance(c.getChannel()).getMapFactory().getMap(Integer.parseInt(splitted[2]));
                    if (target == null) {
                        c.getPlayer().dropMessage(6, "Map does not exist");
                        return 0;
                    }
                    MaplePortal targetPortal = null;
                    if (splitted.length > 3) {
                        try {
                            targetPortal = target.getPortal(Integer.parseInt(splitted[3]));
                        } catch (IndexOutOfBoundsException e) {
                            // noop, assume the gm didn't know how many portals there are
                            c.getPlayer().dropMessage(5, "Invalid portal selected.");
                        } catch (NumberFormatException a) {
                            // noop, assume that the gm is drunk
                        }
                    }
                    if (targetPortal == null) {
                        targetPortal = target.getPortal(0);
                    }
                    victim.changeMap(target, targetPortal);
                }
            } else {
                try {
                    int ch = World.Find.findChannel(splitted[1]);
                    if (ch < 0) {
                        MapleMap target = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(splitted[1]));
                        if (target == null) {
                            c.getPlayer().dropMessage(6, "Map does not exist");
                            return 0;
                        }
                        MaplePortal targetPortal = null;
                        if (splitted.length > 2) {
                            try {
                                targetPortal = target.getPortal(Integer.parseInt(splitted[2]));
                            } catch (IndexOutOfBoundsException e) {
                                // noop, assume the gm didn't know how many portals there are
                                c.getPlayer().dropMessage(5, "Invalid portal selected.");
                            } catch (NumberFormatException a) {
                                // noop, assume that the gm is drunk
                            }
                        }
                        if (targetPortal == null) {
                            targetPortal = target.getPortal(0);
                        }
                        c.getPlayer().changeMap(target, targetPortal);
                    } else {
                        victim = ChannelServer.getInstance(ch).getPlayerStorage().getCharacterByName(splitted[1]);
                        c.getPlayer().dropMessage(6, "Cross changing channel. Please wait.");
                        if (victim.getMapId() != c.getPlayer().getMapId()) {
                            final MapleMap mapp = c.getChannelServer().getMapFactory().getMap(victim.getMapId());
                            c.getPlayer().changeMap(mapp, mapp.findClosestPortal(victim.getTruePosition()));
                        }
                        c.getPlayer().changeChannel(ch);
                    }
                } catch (NumberFormatException e) {
                    c.getPlayer().dropMessage(6, "Something went wrong " + e.getMessage());
                    return 0;
                }
            }
            return 1;
        }
    }

    public static class Ban extends CommandExecute {

        protected boolean hellban = false, ipBan = false;

        private String getCommand() {
            if (hellban) {
                return "HellBan";
            } else {
                return "Ban";
            }
        }

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            if (splitted.length < 3) {
                c.getPlayer().dropMessage(5, "[Syntax] !" + getCommand() + " <IGN> <Reason>");
                c.getPlayer().dropMessage(5, "If you want to consider this ban as an autoban, set the reason \"AutoBan\"");
                return 0;
            }
            StringBuilder sb = new StringBuilder();
            if (hellban) {
                sb.append(StringUtil.joinStringFrom(splitted, 2));
            } else {
                sb.append(StringUtil.joinStringFrom(splitted, 2));
            }
            User target = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (target != null) {
                if ((c.getPlayer().getGMLevel() > target.getGMLevel() || c.getPlayer().isAdmin()) && !target.getClient().isGm() && !target.isAdmin()) {
                    //sb.append(" (IP: ").append(target.getClient().getSessionIPAddress()).append(")");
                    if (target.ban(sb.toString(), hellban || ipBan, false, hellban)) {
                        c.getPlayer().dropMessage(6, "[" + getCommand() + "] Successfully banned " + splitted[1] + ".");
                        return 1;
                    } else {
                        c.getPlayer().dropMessage(6, "[" + getCommand() + "] Failed to ban.");
                        return 0;
                    }
                } else {
                    c.getPlayer().dropMessage(6, "[" + getCommand() + "] May not ban GMs...");
                    return 1;
                }
            } else if (User.ban(splitted[1], sb.toString(), false, c.getPlayer().isAdmin() ? 250 : c.getPlayer().getGMLevel(), hellban)) {
                c.getPlayer().dropMessage(6, "[" + getCommand() + "] Successfully offline banned " + splitted[1] + ".");
                return 1;
            } else {
                c.getPlayer().dropMessage(6, "[" + getCommand() + "] Failed to ban " + splitted[1]);
                return 0;
            }
        }
    }

    public static class DC extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            User victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[splitted.length - 1]);
            if (victim != null && c.getPlayer().getGMLevel() >= victim.getGMLevel()) {
                victim.getClient().Close();
                victim.getClient().disconnect(true, false);
                return 1;
            } else {
                c.getPlayer().dropMessage(6, "The victim does not exist.");
                return 0;
            }
        }
    }

    public static class Kill extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            User player = c.getPlayer();
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(6, "Syntax: !kill <list player names>");
                return 0;
            }
            
            User pTarget = null;
            long nCurrentExp = pTarget.getExp();
            for (int i = 1; i < splitted.length; i++) {
                try {
                    pTarget = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[i]);
                } catch (Exception e) {
                    c.getPlayer().dropMessage(6, "Player " + splitted[i] + " not found.");
                }
                if (player.allowedToTarget(pTarget) && player.getGMLevel() >= pTarget.getGMLevel()) {
                    pTarget.getStat().setHp((short) 0, pTarget);
                    pTarget.getStat().setMp((short) 0, pTarget);
                    pTarget.updateSingleStat(MapleStat.HP, 0);
                    pTarget.updateSingleStat(MapleStat.MP, 0);
                }
            }
            pTarget.gainExp(nCurrentExp, true, true, true); // Give back exp lost.
            return 1;
        }
    }

    public static class Fame extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            User player = c.getPlayer();
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(6, "Syntax: !fame <player> <amount>");
                return 0;
            }
            User victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            int fame;
            try {
                fame = Integer.parseInt(splitted[2]);
            } catch (NumberFormatException nfe) {
                c.getPlayer().dropMessage(6, "Invalid Number...");
                return 0;
            }
            if (victim != null && player.allowedToTarget(victim)) {
                victim.addFame(fame);
                victim.updateSingleStat(MapleStat.FAME, victim.getFame());
            }
            return 1;
        }
    }
    
    public static class ITEM extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            final int itemId = Integer.parseInt(splitted[1]);
            final short quantity = (short) CommandProcessorUtil.getOptionalIntArg(splitted, 2, 1);

            if (!c.getPlayer().isAdmin()) {
                for (int i : GameConstants.itemBlock) {
                    if (itemId == i) {
                        c.getPlayer().dropMessage(5, "Sorry but this item is blocked for your GM level.");
                        return 0;
                    }
                }
            }
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            if (!ii.itemExists(itemId)) {
                c.getPlayer().dropMessage(5, itemId + " does not exist");
            } else {
                Item item;
                if (GameConstants.getInventoryType(itemId) == MapleInventoryType.EQUIP) {
                    item = ii.getEquipById(itemId);
                } else {
                    item = new Item(itemId, (byte) 0, quantity, (byte) 0);
                }
                item.setOwner(c.getPlayer().getName());
                item.setGMLog(c.getPlayer().getName() + " used !getitem");
                if (MapleItemInformationProvider.getInstance().isCash(itemId)) {
                    if (InventoryConstants.isPet(item.getItemId())) {
                        final Pet pet = Pet.createPet(item.getItemId(), MapleInventoryManipulator.getUniqueId(itemId, null));
                        if (pet != null) {
                            item.setExpiration(System.currentTimeMillis() + (20000 * 24 * 60 * 60 * 1000));
                            item.setPet(pet);
                        }
                    }
                }
                MapleInventoryManipulator.addbyItem(c, item);
            }
            return 1;
        }
    }

    public static class ToggleEvent extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {

            if (!c.getPlayer().getClient().getChannelServer().eventOn) {
                int mapid = getOptionalIntArg(splitted, 1, c.getPlayer().getMapId());
                c.getPlayer().getClient().getChannelServer().eventOn = true;
                c.getPlayer().getClient().getChannelServer().eventMap = mapid;
                World.Broadcast.broadcastMessage(WvsContext.broadcastMsg(6, "[" + ServerConstants.SERVER_NAME + " Event] An event has started in Channel " + c.getChannel() + ", type @event to join now!"));
            } else {
                c.getPlayer().getClient().getChannelServer().eventOn = false;
                World.Broadcast.broadcastMessage(WvsContext.broadcastMsg(6, "[" + ServerConstants.SERVER_NAME + " Event] The event has ended, thank you for your participation!"));
            }

            return 1;
        }
    }

    public static class RemoveItem extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            if (splitted.length < 3) {
                c.getPlayer().dropMessage(6, "Syntax: !removeitem <character> <itemid>");
                return 0;
            }
            User pUser = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (pUser == null) {
                c.getPlayer().dropMessage(5, "Character (" + splitted[1] + ") could not be found.");
                return 0;
            }
            if (pUser.getGMLevel() > c.getPlayer().getGMLevel()) {
                c.getPlayer().dropMessage(5, "You do not have permission to perform this action.");
                return 0;
            }
            pUser.removeAll(Integer.parseInt(splitted[2]), false);
            c.getPlayer().dropMessage(6, "All items with the ID (" + splitted[2] + ") has been removed from the inventory of Character (" + splitted[1] + ").");
            return 1;
        }
    }

    public static class LockItem extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            if (splitted.length < 3) {
                c.getPlayer().dropMessage(6, "Need <name> <itemid>");
                return 0;
            }
            User chr = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (chr == null) {
                c.getPlayer().dropMessage(6, "This player does not exist");
                return 0;
            }
            int itemid = Integer.parseInt(splitted[2]);
            MapleInventoryType type = GameConstants.getInventoryType(itemid);
            for (Item item : chr.getInventory(type).listById(itemid)) {
                item.setFlag((byte) (item.getFlag() | ItemFlag.LOCK.getValue()));

                List<ModifyInventory> mod = new ArrayList<>();
                mod.add(new ModifyInventory(ModifyInventoryOperation.Remove, item));
                mod.add(new ModifyInventory(ModifyInventoryOperation.AddItem, item));
                c.SendPacket(WvsContext.inventoryOperation(true, mod));
            }
            if (type == MapleInventoryType.EQUIP) {
                type = MapleInventoryType.EQUIPPED;
                for (Item item : chr.getInventory(type).listById(itemid)) {
                    item.setFlag((byte) (item.getFlag() | ItemFlag.LOCK.getValue()));
                    //chr.getClient().write(CField.updateSpecialItemUse(item, type.getType()));
                }
            }
            c.getPlayer().dropMessage(6, "All items with the ID (" + splitted[2] + ") has been locked from the inventory of Character (" + splitted[1] + ").");
            return 1;
        }
    }

    public static class Smega extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            World.Broadcast.broadcastSmega(WvsContext.broadcastMsg(3, c.getPlayer() == null ? c.getChannel() : c.getPlayer().getClient().getChannel(), c.getPlayer() == null ? c.getPlayer().getName() : c.getPlayer().getName() + " : " + StringUtil.joinStringFrom(splitted, 1), true));
            return 1;
        }
    }

    public static class WhosThere extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            StringBuilder builder = new StringBuilder("The players on the current map are : ").append(c.getPlayer().getMap().getCharactersSize()).append(", ");
            for (User chr : c.getPlayer().getMap().getCharacters()) {
                if (!chr.isAdmin()) {
                if (builder.length() > 150) { // wild guess :o
                    builder.setLength(builder.length() - 2);
                    c.getPlayer().dropMessage(6, builder.toString());
                    builder = new StringBuilder();
                }
                builder.append(MapleCharacterUtil.makeMapleReadable(chr.getName()));
                builder.append(", ");
                }
            }
            builder.setLength(builder.length() - 2);
            c.getPlayer().dropMessage(6, builder.toString());
            return 1;
        }
    }

    public static class BanIP extends Ban {

        public BanIP() {
            ipBan = true;
        }
    }

    public static class ToggleDrops extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            c.getPlayer().getMap().toggleDrops();
            return 1;
        }
    }

    public static class Spawn extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            final int mid = Integer.parseInt(splitted[1]);
            final int num = Math.min(CommandProcessorUtil.getOptionalIntArg(splitted, 2, 1), 500);
            Integer level = CommandProcessorUtil.getNamedIntArg(splitted, 1, "lvl");
            Long hp = CommandProcessorUtil.getNamedLongArg(splitted, 1, "hp");
            Integer exp = CommandProcessorUtil.getNamedIntArg(splitted, 1, "exp");
            Double php = CommandProcessorUtil.getNamedDoubleArg(splitted, 1, "php");
            Double pexp = CommandProcessorUtil.getNamedDoubleArg(splitted, 1, "pexp");

            Mob onemob;
            try {
                onemob = LifeFactory.getMonster(mid);
            } catch (RuntimeException e) {
                c.getPlayer().dropMessage(5, "Error: " + e.getMessage());
                return 0;
            }
            if (onemob == null) {
                c.getPlayer().dropMessage(5, "Mob does not exist");
                return 0;
            }
            long newhp;
            int newexp;
            if (hp != null) {
                newhp = hp.longValue();
            } else if (php != null) {
                newhp = (long) (onemob.getMobMaxHp() * (php.doubleValue() / 100));
            } else {
                newhp = onemob.getMobMaxHp();
            }
            if (exp != null) {
                newexp = exp.intValue();
            } else if (pexp != null) {
                newexp = (int) (onemob.getMobExp() * (pexp.doubleValue() / 100));
            } else {
                newexp = onemob.getMobExp();
            }
            if (newhp < 1) {
                newhp = 1;
            }

            // final OverrideMonsterStats overrideStats = new OverrideMonsterStats(newhp, onemob.getMobMaxMp(), newexp, false);
            for (int i = 0; i < num; i++) {
                Mob mob = LifeFactory.getMonster(mid);
                mob.setHp(newhp);
                if (level != null) {
                    mob.changeLevel(level.intValue(), false);
                } //else {
                //  mob.setOverrideStats(overrideStats);
                //}
                c.getPlayer().getMap().spawnMonsterOnGroundBelow(mob, c.getPlayer().getPosition());
            }
            return 1;
        }
    }

    public static class Mute extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String splitted[]) {
            User victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            victim.canTalk(false);
            return 1;
        }
    }

    public static class UnMute extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String splitted[]) {
            User victim = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            victim.canTalk(true);
            return 1;
        }
    }

    public static class MuteMap extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String splitted[]) {
            for (User chr : c.getPlayer().getMap().getCharacters()) {
                chr.canTalk(false);
            }
            return 1;
        }
    }

    public static class UnMuteMap extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String splitted[]) {
            for (User chr : c.getPlayer().getMap().getCharacters()) {
                chr.canTalk(true);
            }
            return 1;
        }
    }

    public static class MapChangeTimer extends CommandExecute {

        @Override
        public int execute(final ClientSocket c, String splitted[]) {
            final int map = Integer.parseInt(splitted[1]);
            final int nextmap = Integer.parseInt(splitted[2]);
            final int time = Integer.parseInt(splitted[3]);
            c.getChannelServer().getMapFactory().getMap(map).broadcastMessage(CField.getClock(time));
            c.getChannelServer().getMapFactory().getMap(map).startMapEffect("You will be moved out of the map when the timer ends.", 5120041);
            EventTimer.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    for (User mch : c.getChannelServer().getMapFactory().getMap(map).getCharacters()) {
                        if (mch == null) {
                            return;
                        } else {
                            mch.changeMap(nextmap, 0);
                        }
                    }
                }
            }, time * 1000); // seconds
            return 1;
        }
    }

    public static class HellB extends HellBan {
    }

    public static class HellBan extends Ban {

        public HellBan() {
            hellban = true;
        }
    }

    public static class UnHellB extends UnHellBan {
    }

    public static class UnHellBan extends UnBan {

        public UnHellBan() {
            hellban = true;
        }
    }

    public static class UnB extends UnBan {
    }

    public static class UnBan extends CommandExecute {

        protected boolean hellban = false;

        private String getCommand() {
            if (hellban) {
                return "UnHellBan";
            } else {
                return "UnBan";
            }
        }

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(6, "[Syntax] !" + getCommand() + " <IGN>");
                return 0;
            }
            byte ret;
            ret = ClientSocket.unban(splitted[1]);
            if (ret == -2) {
                c.getPlayer().dropMessage(6, "[" + getCommand() + "] SQL error.");
                return 0;
            } else if (ret == -1) {
                c.getPlayer().dropMessage(6, "[" + getCommand() + "] The character does not exist.");
                return 0;
            } else {
                c.getPlayer().dropMessage(6, "[" + getCommand() + "] Successfully unbanned!");

            }
            byte ret_ = ClientSocket.unbanIPMacs(splitted[1]);
            if (ret_ == -2) {
                c.getPlayer().dropMessage(6, "[UnbanIP] SQL error.");
            } else if (ret_ == -1) {
                c.getPlayer().dropMessage(6, "[UnbanIP] The character does not exist.");
            } else if (ret_ == 0) {
                c.getPlayer().dropMessage(6, "[UnbanIP] No IP or Mac with that character exists!");
            } else if (ret_ == 1) {
                c.getPlayer().dropMessage(6, "[UnbanIP] IP/Mac -- one of them was found and unbanned.");
            } else if (ret_ == 2) {
                c.getPlayer().dropMessage(6, "[UnbanIP] Both IP and Macs were unbanned.");
            }
            return ret_ > 0 ? 1 : 0;
        }
    }

    public static class UnbanIP extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            if (splitted.length < 2) {
                c.getPlayer().dropMessage(6, "[Syntax] !unbanip <IGN>");
                return 0;
            }
            byte ret = ClientSocket.unbanIPMacs(splitted[1]);
            if (ret == -2) {
                c.getPlayer().dropMessage(6, "[UnbanIP] SQL error.");
            } else if (ret == -1) {
                c.getPlayer().dropMessage(6, "[UnbanIP] The character does not exist.");
            } else if (ret == 0) {
                c.getPlayer().dropMessage(6, "[UnbanIP] No IP or Mac with that character exists!");
            } else if (ret == 1) {
                c.getPlayer().dropMessage(6, "[UnbanIP] IP/Mac -- one of them was found and unbanned.");
            } else if (ret == 2) {
                c.getPlayer().dropMessage(6, "[UnbanIP] Both IP and Macs were unbanned.");
            }
            if (ret > 0) {
                return 1;
            }
            return 0;
        }
    }

    public static class UnlockInv extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            java.util.Map<Item, MapleInventoryType> eqs = new HashMap<>();
            boolean add = false;
            if (splitted.length < 2 || splitted[1].equals("all")) {
                for (MapleInventoryType type : MapleInventoryType.values()) {
                    for (Item item : c.getPlayer().getInventory(type)) {
                        if (ItemFlag.LOCK.check(item.getFlag())) {
                            item.setFlag((byte) (item.getFlag() - ItemFlag.LOCK.getValue()));
                            add = true;
                            //c.write(CField.updateSpecialItemUse(item, type.getType()));
                        }
                        if (ItemFlag.UNTRADABLE.check(item.getFlag())) {
                            item.setFlag((byte) (item.getFlag() - ItemFlag.UNTRADABLE.getValue()));
                            add = true;
                            //c.write(CField.updateSpecialItemUse(item, type.getType()));
                        }
                        if (add) {
                            eqs.put(item, type);
                        }
                        add = false;
                    }
                }
            } else if (splitted[1].equals("eqp")) {
                for (Item item : c.getPlayer().getInventory(MapleInventoryType.EQUIPPED).newList()) {
                    if (ItemFlag.LOCK.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.LOCK.getValue()));
                        add = true;
                        //c.write(CField.updateSpecialItemUse(item, type.getType()));
                    }
                    if (ItemFlag.UNTRADABLE.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.UNTRADABLE.getValue()));
                        add = true;
                        //c.write(CField.updateSpecialItemUse(item, type.getType()));
                    }
                    if (add) {
                        eqs.put(item, MapleInventoryType.EQUIP);
                    }
                    add = false;
                }
            } else if (splitted[1].equals("eq")) {
                for (Item item : c.getPlayer().getInventory(MapleInventoryType.EQUIP)) {
                    if (ItemFlag.LOCK.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.LOCK.getValue()));
                        add = true;
                        //c.write(CField.updateSpecialItemUse(item, type.getType()));
                    }
                    if (ItemFlag.UNTRADABLE.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.UNTRADABLE.getValue()));
                        add = true;
                        //c.write(CField.updateSpecialItemUse(item, type.getType()));
                    }
                    if (add) {
                        eqs.put(item, MapleInventoryType.EQUIP);
                    }
                    add = false;
                }
            } else if (splitted[1].equals("u")) {
                for (Item item : c.getPlayer().getInventory(MapleInventoryType.USE)) {
                    if (ItemFlag.LOCK.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.LOCK.getValue()));
                        add = true;
                        //c.write(CField.updateSpecialItemUse(item, type.getType()));
                    }
                    if (ItemFlag.UNTRADABLE.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.UNTRADABLE.getValue()));
                        add = true;
                        //c.write(CField.updateSpecialItemUse(item, type.getType()));
                    }
                    if (add) {
                        eqs.put(item, MapleInventoryType.USE);
                    }
                    add = false;
                }
            } else if (splitted[1].equals("s")) {
                for (Item item : c.getPlayer().getInventory(MapleInventoryType.SETUP)) {
                    if (ItemFlag.LOCK.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.LOCK.getValue()));
                        add = true;
                        //c.write(CField.updateSpecialItemUse(item, type.getType()));
                    }
                    if (ItemFlag.UNTRADABLE.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.UNTRADABLE.getValue()));
                        add = true;
                        //c.write(CField.updateSpecialItemUse(item, type.getType()));
                    }
                    if (add) {
                        eqs.put(item, MapleInventoryType.SETUP);
                    }
                    add = false;
                }
            } else if (splitted[1].equals("e")) {
                for (Item item : c.getPlayer().getInventory(MapleInventoryType.ETC)) {
                    if (ItemFlag.LOCK.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.LOCK.getValue()));
                        add = true;
                        //c.write(CField.updateSpecialItemUse(item, type.getType()));
                    }
                    if (ItemFlag.UNTRADABLE.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.UNTRADABLE.getValue()));
                        add = true;
                        //c.write(CField.updateSpecialItemUse(item, type.getType()));
                    }
                    if (add) {
                        eqs.put(item, MapleInventoryType.ETC);
                    }
                    add = false;
                }
            } else if (splitted[1].equals("c")) {
                for (Item item : c.getPlayer().getInventory(MapleInventoryType.CASH)) {
                    if (ItemFlag.LOCK.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.LOCK.getValue()));
                        add = true;
                        //c.write(CField.updateSpecialItemUse(item, type.getType()));
                    }
                    if (ItemFlag.UNTRADABLE.check(item.getFlag())) {
                        item.setFlag((byte) (item.getFlag() - ItemFlag.UNTRADABLE.getValue()));
                        add = true;
                        //c.write(CField.updateSpecialItemUse(item, type.getType()));
                    }
                    if (add) {
                        eqs.put(item, MapleInventoryType.CASH);
                    }
                    add = false;
                }
            } else {
                c.getPlayer().dropMessage(6, "[all/eqp/eq/u/s/e/c]");
            }

            for (Entry<Item, MapleInventoryType> eq : eqs.entrySet()) {
                c.getPlayer().forceReAddItemNoUpdate(eq.getKey().copy(), eq.getValue());
            }
            return 1;
        }
    }

    public static class Drop extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            final int itemId = Integer.parseInt(splitted[1]);
            final short quantity = (short) CommandProcessorUtil.getOptionalIntArg(splitted, 2, 1);
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            if (!ii.itemExists(itemId)) {
                c.getPlayer().dropMessage(5, itemId + " does not exist");
            } else {
                Item toDrop;
                if (GameConstants.getInventoryType(itemId) == MapleInventoryType.EQUIP) {

                    toDrop = ii.randomizeStats((Equip) ii.getEquipById(itemId));
                } else {
                    toDrop = new client.inventory.Item(itemId, (byte) 0, (short) quantity, (byte) 0);
                }
                if (!c.getPlayer().isAdmin()) {
                    toDrop.setGMLog(c.getPlayer().getName() + " used !drop");
                    toDrop.setOwner(c.getPlayer().getName());
                }
                c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), toDrop, c.getPlayer().getPosition(), true, true, false);
            }
            return 1;
        }
    }

    public static class DropItem extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            final String itemName = StringUtil.joinStringFrom(splitted, 2);
            final short quantity = (short) CommandProcessorUtil.getOptionalIntArg(splitted, 1, 1);
            int itemId = 0;

            for (Map.Entry<Integer, Pair<String, String>> item : MapleStringInformationProvider.getAllitemsStringCache().entrySet()) {
                final String name = item.getValue().getLeft();

                if (name.toLowerCase().equals(itemName.toLowerCase())) {
                    itemId = item.getKey();
                    break;
                }
            }
            MapleItemInformationProvider ii = MapleItemInformationProvider.getInstance();
            if (!ii.itemExists(itemId)) {
                c.getPlayer().dropMessage(5, itemName + " does not exist");
            } else {
                Item toDrop;
                if (GameConstants.getInventoryType(itemId) == MapleInventoryType.EQUIP) {

                    toDrop = (Equip) ii.getEquipById(itemId);
                } else {
                    toDrop = new client.inventory.Item(itemId, (byte) 0, (short) quantity, (byte) 0);
                }
                if (!c.getPlayer().isAdmin()) {
                    toDrop.setGMLog(c.getPlayer().getName() + " used !drop");
                    toDrop.setOwner(c.getPlayer().getName());
                }
                c.getPlayer().getMap().spawnItemDrop(c.getPlayer(), c.getPlayer(), toDrop, c.getPlayer().getPosition(), true, true, false);
            }
            return 1;
        }
    }

    public static class Monitor extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            User target = c.getChannelServer().getPlayerStorage().getCharacterByName(splitted[1]);
            if (target != null) {
                if (target.getClient().isMonitored()) {
                    target.getClient().setMonitored(false);
                    c.getPlayer().dropMessage(5, "Not monitoring " + target.getName() + " anymore.");
                } else {
                    target.getClient().setMonitored(true);
                    c.getPlayer().dropMessage(5, "Monitoring " + target.getName() + ".");
                }
            } else {
                c.getPlayer().dropMessage(5, "Target not found on channel.");
                return 0;
            }
            return 1;
        }
    }

    public static class KillAllExp extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            MapleMap map = c.getPlayer().getMap();
            double range = Double.POSITIVE_INFINITY;

            if (splitted.length > 1) {
                int irange = Integer.parseInt(splitted[1]);
                if (splitted.length <= 2) {
                    range = irange * irange;
                } else {
                    map = c.getChannelServer().getMapFactory().getMap(Integer.parseInt(splitted[2]));
                }
            }
            if (map == null) {
                c.getPlayer().dropMessage(6, "Map does not exist");
                return 0;
            }
            Mob mob;
            for (MapleMapObject monstermo : map.getMapObjectsInRange(c.getPlayer().getPosition(), range, Arrays.asList(MapleMapObjectType.MONSTER))) {
                mob = (Mob) monstermo;
                mob.damage(c.getPlayer(), mob.getHp(), false);
            }
            return 1;
        }
    }
}
