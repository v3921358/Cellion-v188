package tools;

import org.apache.logging.log4j.ThreadContext;

import client.inventory.Item;
import handling.world.MapleParty;
import handling.world.MaplePartyCharacter;
import service.ChannelServer;
import server.MapleItemInformationProvider;
import server.MapleTrade;
import server.maps.objects.MapleCharacter;

public class BigBrother {

    public static void general(String message, MapleCharacter source) {
        for (ChannelServer ch : ChannelServer.getAllInstances()) {
            for (MapleCharacter player : ch.getPlayerStorage().getAllCharacters()) {
                if (player.isGM() && player.bigBrother()) {
                    if (player.getClient().getChannel() == source.getClient().getChannel() && player.getMapId() == source.getMapId()) {
                        return;//If GM and source is on same map AND same channel, ignore to avoid double messages
                    }
                    //player.announce(MaplePacketCreator.serverNotice(5, "[" + source.getMap().getMapName() + "] " + source.getName() + ":" + message));
                }
            }
        }
    }

    public static void whisper(String message, MapleCharacter source, MapleCharacter destination) {
        for (ChannelServer ch : ChannelServer.getAllInstances()) {
            for (MapleCharacter player : ch.getPlayerStorage().getAllCharacters()) {
                if (player.isGM() && player.bigBrother()) {
                    if (player.equals(destination) || player.equals(source)) {
                        return; //To avoid double whispers
                    }
                    //player.announce(MaplePacketCreator.getWhisper(source.getName(), source.getClient().getChannel(), message));
                }
            }
        }
    }

    public static void buddy(int[] recipientCharacterIds, int cidFrom, String nameFrom, String chattext) {
        for (ChannelServer ch : ChannelServer.getAllInstances()) {
            for (MapleCharacter player : ch.getPlayerStorage().getAllCharacters()) {
                if (player.isGM() && player.bigBrother()) {
                    if (cidFrom == player.getId()) {
                        return; //To avoid double buddy chats
                    }
                    for (int id : recipientCharacterIds) {
                        if (id == player.getId()) {
                            return; //To avoid double buddy chats
                        }
                    }
                    //player.announce(MaplePacketCreator.multiChat(nameFrom, "[BB]: " + chattext, 0));
                }
            }
        }
    }

    public static void guild(String guildName, String source, String message) {
        for (ChannelServer ch : ChannelServer.getAllInstances()) {
            for (MapleCharacter player : ch.getPlayerStorage().getAllCharacters()) {
                if (player.isGM() && player.bigBrother()) {
                    if (player.getGuild() != null && player.getGuild().getName().equalsIgnoreCase(guildName)) {
                        return; //Avoid same-guild messages
                    }
                    //player.announce(MaplePacketCreator.serverNotice(5, "[" + guildName + "] " + source + ":" + message));
                }
            }
        }
    }

    public static void party(MapleParty party, String chattext, String namefrom) {
        for (ChannelServer ch : ChannelServer.getAllInstances()) {
            for (MapleCharacter player : ch.getPlayerStorage().getAllCharacters()) {
                if (player.isGM() && player.bigBrother()) {
                    for (MaplePartyCharacter partyMember : party.getMembers()) {
                        if (partyMember.getId() == player.getId()) { //Prevent same-party messages
                            return;
                        }
                    }
                    //player.announce(MaplePacketCreator.multiChat(namefrom, "[PB]: " + chattext, 1));
                }
            }
        }
    }

    public static void trade(MapleCharacter chr, MapleCharacter chr2, String message) {
        for (ChannelServer ch : ChannelServer.getAllInstances()) {
            for (MapleCharacter player : ch.getPlayerStorage().getAllCharacters()) {
                if (player.isGM() && player.bigBrother()) {
                    if (player.equals(chr) || player.equals(chr2)) {
                        return; //You're in the trade itself
                    }
                    //player.announce(MaplePacketCreator.serverNotice(5, "[TRADE]: [" + chr.getName() + ">" + chr2.getName() + "]: " + message));
                }
            }
        }
    }

    public static void messenger(String from, String to1, String to2, String message) {
        if (message.equals(from + "0") || message.equals(from + "1")) {
            return;
        }
        for (ChannelServer ch : ChannelServer.getAllInstances()) {
            for (MapleCharacter player : ch.getPlayerStorage().getAllCharacters()) {
                if (player.isGM() && player.bigBrother()) {
                    if (player.getName().equals(from) || player.getName().equals(to1) || player.getName().equals(to2)) {
                        return; //You're in the chat itself
                    }
                    //player.announce(MaplePacketCreator.serverNotice(5, "[MESSENGER]: [" + to1 + "|" + to2 + "]: " + message));
                }
            }
        }
    }

    public static void logTrade(MapleTrade trade1, MapleTrade trade2) {
        String name1 = trade1.getCharacter().getName();
        String name2 = trade2.getCharacter().getName();
        String log = "TRADE BETWEEN " + name1 + " AND " + name2 + "\r\n";
        //Trade 1 to trade 2
        log += trade1.getExchangeMesos() + " mesos from " + name1 + " to " + name2 + " \r\n";
        for (Item item : trade1.getItems()) {
            String itemName = MapleItemInformationProvider.getInstance().getName(item.getItemId()) + "(" + item.getItemId() + ")";
            log += item.getQuantity() + " " + itemName + " from " + name1 + " to " + name2 + " \r\n";;
        }
        //Trade 2 to trade 1
        log += trade2.getExchangeMesos() + " mesos from " + name2 + " to " + name1 + " \r\n";
        for (Item item : trade2.getItems()) {
            String itemName = MapleItemInformationProvider.getInstance().getName(item.getItemId()) + "(" + item.getItemId() + ")";
            log += item.getQuantity() + " " + itemName + " from " + name2 + " to " + name1 + " \r\n";;
        }
        log += "\r\n\r\n";
        ThreadContext.put("TRADE", name1 + " " + name2);
        LogHelper.ANTI_HACK.get().info(log);
        ThreadContext.clearMap();
    }

    public static String getTimeString(long then) {
        long duration = System.currentTimeMillis() - then;
        int seconds = (int) (duration / 1000) % 60;
        int minutes = (int) ((duration / (1000 * 60)) % 60);
        return minutes + " Minutes and " + seconds + " Seconds";
    }

    public static void logGacha(MapleCharacter player, int itemid, String map) {
        String itemName = MapleItemInformationProvider.getInstance().getName(itemid);
        LogHelper.GACHAPON.get().info(player.getName() + " got a " + itemName + " (" + itemid + ") from the " + map + " gachapon.\r\n");
    }
}
