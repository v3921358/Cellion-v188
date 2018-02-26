/*
    This file is part of Desu: MapleStory v62 Server Emulator
    Copyright (C) 2014  Zygon <watchmystarz@hotmail.com>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Artifact brought over from Invictus. Used to manage the amount of possible
 * sessions with a database that can exist for an instance of one of the
 * services. Mirrors how OdinMS used to initialize connections, except that the
 * availability of these connections is not ThreadLocal and therefore is
 * contained far more effectively.
 *
 * @author Zygon
 */
public final class Database {

    /**
     * The connection URL for the database. Connections will be attempted to be
     * made to this URL.
     */
    private String URL;
    /**
     * Manages the amount of sessions that can exist within a service/process
     * rather than using something like ThreadLocal which can leak.
     */
    private final Semaphore s;
    /**
     * Connection properties that are used for the database credentials.
     */
    private final Properties cProp;
    /**
     * Safety variable for preventing new connections after closing.
     */
    private boolean closed = false;
    /**
     * Queue that contains all the connections (references to this collection
     * are thread-safe).
     */
    private final ConcurrentLinkedQueue<Connection> queue;

    static {
        try {
            Class.forName("com.mysql.jdbc.Driver"); // touch the driver first
        } catch (Exception e) {
            System.err.println("SEVERE : SQL Driver has not been located during runtime. Please make sure it is in the correct path.");
            System.exit(0);
        }
    }

    /**
     * Creates a new Database object using specific credentials and information
     * for maintaining the server.
     *
     * @param url the URL for the database.
     * @param user the user credential for the database.
     * @param pass the password credential for the database.
     * @param connections the amount of connections that can exist in this
     * service at one time.
     * @return a new Database object for usage in a service.
     */
    public static Database newDatabase(String url, String user, String pass, int connections) {
        Database db = new Database(connections);
        db.URL = url;
        db.cProp.setProperty("user", user);
        db.cProp.setProperty("password", pass);
        return db;
    }

    /**
     * @see Database#newDatabase(java.lang.String, java.lang.String,
     * java.lang.String, int)
     */
    private Database(int connections) {
        cProp = new Properties();
        s = new Semaphore(connections, true);
        queue = new ConcurrentLinkedQueue();
    }
    
    /**
     * Closes this database and closes all connections.
     */
    public final void close() {
        try {
            s.tryAcquire(10, 1, TimeUnit.HOURS); // ya just need to make sure all that crap gets set
        } catch (Exception e) {
            return;
        }
        for (Connection c : queue) {
            try {
                c.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        closed = true;
    }

    /**
     * Acquires a new connection using a default timeout for the Semaphore to
     * acquire a connection. If the time has passed, an exception will be thrown
     * and no connection will be given.
     *
     * @return ideally a connection to the desired database; if nothing pans out
     * and the Semaphore has to wait too long, no connection will be returned.
     */
    public final Connection getConnection() {
        return getConnection(60000);
    }

    /**
     *
     * @param timeout time-out for the Semaphore.
     * @return a connection to the database.
     * @see Database#getConnection()
     */
    public final Connection getConnection(long timeout) {
        if (URL == null || cProp.isEmpty() || closed) {
            return null;
        }
        try {
            s.tryAcquire(timeout, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            return null;
        }
        Connection c = queue.poll();
        if (c == null) {
            try {
                c = DriverManager.getConnection(URL, cProp);
            } catch (Exception e) {
                e.printStackTrace();
                s.release();
                return null;
            }
        } else { // validates connection is still active
            try {
                c.getMetaData();
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    c = DriverManager.getConnection(URL, cProp);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    s.release();
                    return null;
                }
            }
        }
        return c;
    }

    /**
     * Releases a connection back into the queue for usage later. This is a
     * critical step in this model since if the semaphore is blocked forever,
     * then the server cannot possibly function properly.
     *
     * @param c the connection to be released back into the queue.
     */
    public final void release(Connection c) {
        try {
            if (!c.isClosed()) {
                queue.add(c);
            }
        } catch (Exception e) {
        } finally {
            s.release();
        }
    }
}
