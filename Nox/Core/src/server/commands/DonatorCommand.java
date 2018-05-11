package server.commands;

import java.awt.Point;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import client.MapleCharacterUtil;
import client.ClientSocket;
import client.MapleJob;
import client.MapleStat;
import client.Skill;
import client.SkillFactory;
import client.anticheat.ReportType;
import client.inventory.Item;
import client.inventory.ItemFlag;
import client.inventory.MapleInventoryIdentifier;
import client.inventory.MapleInventoryType;
import client.inventory.ModifyInventory;
import client.inventory.ModifyInventoryOperation;
import constants.GameConstants;
import constants.InventoryConstants;
import constants.ServerConstants;
import constants.ServerConstants.PlayerGMRank;
import handling.world.CheaterData;
import handling.world.World;
import service.ChannelServer;

import scripting.EventInstanceManager;
import scripting.EventManager;
import scripting.provider.NPCScriptManager;
import server.MapleInventoryManipulator;
import server.MapleItemInformationProvider;
import server.MaplePortal;
import server.events.MapleEvent;
import server.events.MapleEventType;
import server.life.MapleLifeFactory;
import server.life.Mob;
import server.life.MapleMonsterInformationProvider;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import server.maps.MapleMapObjectType;
import server.maps.objects.User;
import server.maps.objects.MapleNPC;
import server.maps.objects.Pet;
import server.maps.objects.MapleReactor;
import server.quest.Quest;
import server.shops.MapleShopFactory;
import tools.Pair;
import tools.StringUtil;
import tools.packet.CField;
import tools.packet.WvsContext;
import tools.packet.MobPacket;

/**
 *
 * @author Mazen Massoud
 */
public class DonatorCommand {

    public static PlayerGMRank getPlayerLevelRequired() {
        return PlayerGMRank.DONATOR;
    }

    public static class DonorHelp extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            c.getPlayer().dropMessage(6, "$donorhelp - Display availible donator commands.");
            c.getPlayer().dropMessage(6, "$togglesmega - Enable or disable player Super Megaphones.");
            c.getPlayer().dropMessage(6, "The availible " + ServerConstants.SERVER_NAME + " donator commands have been listed above.");
            return 1;
        }
    }

    public static class ToggleSmega extends CommandExecute {

        @Override
        public int execute(ClientSocket c, String[] splitted) {
            c.getPlayer().setSmega();
            return 1;
        }
    }
}
