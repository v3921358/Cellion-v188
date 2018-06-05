/*
 * Copyright (C) 2018 Kaz Voeten
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author Kaz Voeten
 */
public class Database {

    private static HikariConfig pConfig; //Hikari database config.
    private static HikariDataSource pDataSource; //Hikari datasource based on config.

    public static void Initialize() {
        //Check if file exists, if not: create and use default file.
        File properties = new File("database.properties");
        if (!properties.exists()) {
            try (FileOutputStream fout = new FileOutputStream(properties)) {
                PrintStream out = new PrintStream(fout);
                out.println("dataSourceClassName=org.mariadb.jdbc.MariaDbDataSource");
                out.println("dataSource.user=root");
                out.println("dataSource.password=");
                out.println("dataSource.databaseName=nox");
                out.println("dataSource.portNumber=3306");
                out.println("dataSource.serverName=localhost");
                fout.flush();
                fout.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("No database.properties file found. A default one has been generated.");
        }
        pConfig = new HikariConfig("database.properties");
        pDataSource = new HikariDataSource(pConfig);
    }

    public static Connection GetConnection() throws SQLException {
        return pDataSource.getConnection();
    }

    public static String GetPoolStats() {
        return "Connections: " + pDataSource.getHikariPoolMXBean().getActiveConnections()
                + " | " + pDataSource.getHikariPoolMXBean().getIdleConnections();
    }

    public static void Excecute(Connection con, PreparedStatement ps, Object... args) throws SQLException {
        for (int i = 0; i < args.length; ++i) {
            ps.setObject(i, args[i]);
        }

        ps.executeUpdate();
    }
    
    public static Connection GetOfflineConnection() throws SQLException {

        String sDatabase = "nox";
        String sUsername = "root";
        String sPassword = "?r3xionism3me!";
        
        Connection pConnection = DriverManager.getConnection("jdbc:mysql://localhost/"+sDatabase+"?user="+sUsername+"&password="+sPassword);
        return pConnection;
    }
}
