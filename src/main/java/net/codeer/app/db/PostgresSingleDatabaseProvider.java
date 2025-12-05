package net.codeer.app.db;

import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.IOException;

public class PostgresSingleDatabaseProvider {
    private static HikariDataSource hikari;

    /**
     * url examples:
     *   jdbc:postgresql://localhost:5442/mydb
     *   jdbc:postgresql://localhost:5442/mydb?user=admin&password=secret
     *
     * If user/password are present in the URL, they are used.
     * If they are not present, we do not set any credentials at all.
     */
    public static void init(String url) {
        hikari = HikariDataSourceBuilder.buildHikariDataSource(url);
    }

    public static DataSource getDataSource() {
        return hikari;
    }

    public static void close() throws IOException {
        if (hikari != null) {
            hikari.close();
            hikari = null;
        }
    }
}
