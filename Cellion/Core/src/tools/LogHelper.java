package tools;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 *
 * @author Tyler
 */
public enum LogHelper {
    GACHAPON(LogManager.getLogger("players.gachapon")),
    XPSPENT(LogManager.getLogger("players.xp")),
    GMCALL(LogManager.getLogger("staff.gmcall")),
    BUGREPORT(LogManager.getLogger("staff.bugreport")),
    COMMAND(LogManager.getLogger("staff.commands")),
    ANTI_HACK(LogManager.getLogger("antihack.triggered")),
    PACKET_EDIT_HACK(LogManager.getLogger("antihack.packetedit")),
    PACKET_THROTTLE(LogManager.getLogger("antihack.throttle")),
    SESSION(LogManager.getLogger("general.sessions")), // Check
    ANTISPAM_BLACKLISTFILTER(LogManager.getLogger("mina.network")),
    CONSOLE(LogManager.getLogger("general.console")), // Check
    GENERAL_EXCEPTION(LogManager.getLogger("general.exception")),
    SQL(LogManager.getLogger("general.sql")),
    PACKET_HANDLER(LogManager.getLogger("errors.packethandler")), // Check
    PORTALS_ERROR(LogManager.getLogger("errors.portal")), // Check
    NPCS_ERROR(LogManager.getLogger("errors.npc")), // Check!
    QUESTS_ERROR(LogManager.getLogger("errors.quest")), // Check
    REACTORS_ERROR(LogManager.getLogger("errors.reactor")), // Check
    MAPS_ERROR(LogManager.getLogger("errors.map")), // Check
    UNCODED(LogManager.getLogger("errors.uncoded")), // Check
    INVOCABLE(LogManager.getLogger("errors.invocable"));

    private Logger logger;

    private LogHelper(Logger log) {
        logger = log;
    }

    public Logger get() {
        return logger;
    }
}
