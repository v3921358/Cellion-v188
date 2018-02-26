package database;

//import constants.DatabaseConstants;
import constants.ServerConstants;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;

/**
 * All servers maintain a Database Connection. This class therefore "singletonices" the connection per process.
 *
 *
 * @author Frz (original method)
 * @author BlackRabbit (explaining the "too many connections" problem, and showing an example to fix it)
 * @author Novak (Implementing BlackRabbit's fix in this source's connection)
 */
public class DatabaseConnection {

    private static final ThreadLocal<Connection> con = new DatabaseConnection.ThreadLocalConnection();
    public static final int CLOSE_CURRENT_RESULT = 1;
    /**
     * The constant indicating that the current <code>ResultSet</code> object should not be closed when calling <code>getMoreResults</code>.
     *
     * @since 1.4
     */
    public static final int KEEP_CURRENT_RESULT = 2;
    /**
     * The constant indicating that all <code>ResultSet</code> objects that have previously been kept open should be closed when calling
     * <code>getMoreResults</code>.
     *
     * @since 1.4
     */
    public static final int CLOSE_ALL_RESULTS = 3;
    /**
     * The constant indicating that a batch statement executed successfully but that no count of the number of rows it affected is
     * available.
     *
     * @since 1.4
     */
    public static final int SUCCESS_NO_INFO = -2;
    /**
     * The constant indicating that an error occured while executing a batch statement.
     *
     * @since 1.4
     */
    public static final int EXECUTE_FAILED = -3;
    /**
     * The constant indicating that generated keys should be made available for retrieval.
     *
     * @since 1.4
     */
    public static final int RETURN_GENERATED_KEYS = 1;
    /**
     * The constant indicating that generated keys should not be made available for retrieval.
     *
     * @since 1.4
     */
    public static final int NO_GENERATED_KEYS = 2;

    static {
        try {
            Class.forName("com.mysql.jdbc.Driver"); // touch the mysql driver
        } catch (ClassNotFoundException e) {
            System.out.println("[SEVERE] SQL Driver Not Found. Consider death by clams.");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() {
        Connection c = con.get();
        try {
            c.getMetaData();
        } catch (SQLException e) { // connection is dead, therefore discard old object
            con.remove();
            c = con.get();
        }
        return c;
    }

    public static void closeAll() throws SQLException {
        for (final Connection connection : DatabaseConnection.ThreadLocalConnection.allConnections) {
            if (connection != null) {
                connection.close();
            }
        }
    }

    private static class ThreadLocalConnection extends ThreadLocal<Connection> {

        public static final Collection<Connection> allConnections = new LinkedList<>();

        @Override
        protected Connection initialValue() {
            try {
                return DriverManager.getConnection(String.format("jdbc:mysql://%s:%s/%s?autoReconnect=true&useSSL=false", ServerConstants.SQL_HOST, ServerConstants.SQL_PORT, ServerConstants.SQL_DATABASE),
                        ServerConstants.SQL_USER, ServerConstants.SQL_PASSWORD);
            } catch (SQLException e) {
                System.out.println("[SEVERE] Unable to make database connection.");
                e.printStackTrace();
                return null;
            }
        }
    }
}
