package handling.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import client.CharacterUtil;
import handling.world.CharacterTransfer;
import handling.world.CheaterData;
import handling.world.World;
import net.OutPacket;

import server.Timer.PingTimer;
import server.maps.objects.User;

public class PlayerStorage {

    private final ReentrantReadWriteLock mutex = new ReentrantReadWriteLock();
    private final Lock rL = mutex.readLock(), wL = mutex.writeLock();
    private final ReentrantReadWriteLock mutex2 = new ReentrantReadWriteLock();
    private final Lock wL2 = mutex2.writeLock();
    private final Map<String, User> nameToChar = new HashMap<>();
    private final Map<Integer, User> idToChar = new HashMap<>();
    private final Map<Integer, CharacterTransfer> PendingCharacter = new HashMap<>();
    // Channel it is stored
    private final int channel;

    public PlayerStorage(int channel) {
        this.channel = channel;
        // Prune once every 15 minutes
        PingTimer.getInstance().register(new PersistingTask(), 60000);
    }

    public final ArrayList<User> getAllCharacters() {
        rL.lock();
        try {
            return new ArrayList<>(idToChar.values());
        } finally {
            rL.unlock();
        }
    }

    public final void registerPlayer(final User chr) {
        wL.lock();
        try {
            nameToChar.put(chr.getName().toLowerCase(), chr);
            idToChar.put(chr.getId(), chr);
        } finally {
            wL.unlock();
        }
        World.Find.register(chr.getId(), chr.getName(), channel);
    }

    public final void registerPendingPlayer(final CharacterTransfer chr, final int playerid) {
        wL2.lock();
        try {
            PendingCharacter.put(playerid, chr);//new Pair(System.currentTimeMillis(), chr));
        } finally {
            wL2.unlock();
        }
    }

    public final void deregisterPlayer(final User chr) {
        wL.lock();
        try {
            nameToChar.remove(chr.getName().toLowerCase());
            idToChar.remove(chr.getId());
        } finally {
            wL.unlock();
        }
        World.Find.forceDeregister(chr.getId(), chr.getName());
    }

    public final void deregisterPlayer(final int idz, final String namez) {
        wL.lock();
        try {
            nameToChar.remove(namez.toLowerCase());
            idToChar.remove(idz);
        } finally {
            wL.unlock();
        }
        World.Find.forceDeregister(idz, namez);
    }

    public final void deregisterPendingPlayer(final int charid) {
        wL2.lock();
        try {
            PendingCharacter.remove(charid);
        } finally {
            wL2.unlock();
        }
    }

    public final CharacterTransfer getPendingCharacter(final int charid) {
        wL2.lock();
        try {
            return PendingCharacter.remove(charid);
        } finally {
            wL2.unlock();
        }
    }

    public final User getCharacterByName(final String name) {
        rL.lock();
        try {
            return nameToChar.get(name.toLowerCase());
        } finally {
            rL.unlock();
        }
    }

    public final User getCharacterById(final int id) {
        rL.lock();
        try {
            return idToChar.get(id);
        } finally {
            rL.unlock();
        }
    }

    public final int getConnectedClients() {
        return idToChar.size();
    }

    public final List<CheaterData> getCheaters() {
        final List<CheaterData> cheaters = new ArrayList<>();

        rL.lock();
        try {
            final Iterator<User> itr = nameToChar.values().iterator();
            User chr;
            while (itr.hasNext()) {
                chr = itr.next();

                if (chr.getCheatTracker().getPoints() > 0) {
                    cheaters.add(new CheaterData(chr.getCheatTracker().getPoints(), CharacterUtil.makeMapleReadable(chr.getName()) + " (" + chr.getCheatTracker().getPoints() + ") " + chr.getCheatTracker().getSummary()));
                }
            }
        } finally {
            rL.unlock();
        }
        return cheaters;
    }

    public final List<CheaterData> getReports() {
        final List<CheaterData> cheaters = new ArrayList<>();

        rL.lock();
        try {
            final Iterator<User> itr = nameToChar.values().iterator();
            User chr;
            while (itr.hasNext()) {
                chr = itr.next();

                if (chr.getReportPoints() > 0) {
                    cheaters.add(new CheaterData(chr.getReportPoints(), CharacterUtil.makeMapleReadable(chr.getName()) + " (" + chr.getReportPoints() + ") " + chr.getReportSummary()));
                }
            }
        } finally {
            rL.unlock();
        }
        return cheaters;
    }

    public final void disconnectAll() {
        disconnectAll(false);
    }

    public final void disconnectAll(final boolean checkGM) {
        wL.lock();
        try {
            final Iterator<User> itr = nameToChar.values().iterator();
            User chr;
            while (itr.hasNext()) {
                chr = itr.next();

                if (!chr.isGM() || !checkGM) {
                    chr.getClient().disconnect(false, false, true);
                    chr.getClient().Close();
                    World.Find.forceDeregister(chr.getId(), chr.getName());
                    itr.remove();
                }
            }
        } finally {
            wL.unlock();
        }
    }

    public final String getOnlinePlayers(final boolean byGM) {
        final StringBuilder sb = new StringBuilder();

        if (byGM) {
            rL.lock();
            try {
                final Iterator<User> itr = nameToChar.values().iterator();
                while (itr.hasNext()) {
                    sb.append(CharacterUtil.makeMapleReadable(itr.next().getName()));
                    sb.append(", ");
                }
            } finally {
                rL.unlock();
            }
        } else {
            rL.lock();
            try {
                final Iterator<User> itr = nameToChar.values().iterator();
                User chr;
                while (itr.hasNext()) {
                    chr = itr.next();

                    if (!chr.isGM()) {
                        sb.append(CharacterUtil.makeMapleReadable(chr.getName()));
                        sb.append(", ");
                    }
                }
            } finally {
                rL.unlock();
            }
        }
        return sb.toString();
    }

    public final String formatOnlinePlayers(final boolean bNewLine) {
        final StringBuilder sb = new StringBuilder();

        rL.lock();
        try {
            final Iterator<User> itr = nameToChar.values().iterator();
            User chr;
            while (itr.hasNext()) {
                chr = itr.next();

                sb.append(CharacterUtil.makeMapleReadable(chr.getName()));
                if (itr.hasNext()) {
                    if (bNewLine) {
                        sb.append(", \r\n");
                    } else {
                        sb.append(", ");
                    }
                } else {
                    if (bNewLine) {
                        sb.append(". \r\n");
                    } else {
                        sb.append(".");
                    }
                }
            }
        } finally {
            rL.unlock();
        }
        return sb.toString();
    }

    public final void broadcastPacket(final OutPacket data) {
        rL.lock();
        try {
            final Iterator<User> itr = nameToChar.values().iterator();
            while (itr.hasNext()) {
                itr.next().getClient().SendPacket(data);
            }
        } finally {
            rL.unlock();
        }
    }

    public final void broadcastSmegaPacket(final OutPacket data) {
        rL.lock();
        try {
            final Iterator<User> itr = nameToChar.values().iterator();
            User chr;
            while (itr.hasNext()) {
                chr = itr.next();

                if (chr.getClient().isLoggedIn() && chr.getSmega()) {
                    chr.getClient().SendPacket(data);
                }
            }
        } finally {
            rL.unlock();
        }
    }

    public final void broadcastGMPacket(final OutPacket data) {
        rL.lock();
        try {
            final Iterator<User> itr = nameToChar.values().iterator();
            User chr;
            while (itr.hasNext()) {
                chr = itr.next();
                if (chr.getClient().isLoggedIn() && chr.isIntern()) {
                    chr.getClient().SendPacket(data);
                }
            }
        } finally {
            rL.unlock();
        }
    }

    public final void broadcastWhisperPacket(final OutPacket data, String msgDestination) {
        rL.lock();
        try {
            final Iterator<User> itr = nameToChar.values().iterator();
            User chr;
            while (itr.hasNext()) {
                chr = itr.next();
                if (chr.getClient().isLoggedIn() && chr == chr.getClient().getChannelServer().getPlayerStorage().getCharacterByName(msgDestination)) {
                    chr.getClient().SendPacket(data);
                }
            }
        } finally {
            rL.unlock();
        }
    }

    public class PersistingTask implements Runnable {

        @Override
        public void run() {
            wL2.lock();
            try {
                final long currenttime = System.currentTimeMillis();
                final Iterator<Map.Entry<Integer, CharacterTransfer>> itr = PendingCharacter.entrySet().iterator();

                while (itr.hasNext()) {
                    if (currenttime - itr.next().getValue().TranferTime > 40000) { // 40 sec
                        itr.remove();
                    }
                }
            } finally {
                wL2.unlock();
            }
        }
    }
}
