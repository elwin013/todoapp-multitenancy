package net.codeer.app.db;

import java.sql.*;

public final class EnsurePsqlDatabases {
    // Connect to an existing DB like "postgres"
    // Example adminUrl:
    // jdbc:postgresql://localhost:5442/postgres?user=admin&password=secret
    public static void ensureDatabaseExists(String adminUrl, String dbName) {
        try {
            if (!dbName.matches("[a-zA-Z0-9_]+")) {
                throw new IllegalArgumentException("Invalid database name: " + dbName);
            }

            try (Connection c = DriverManager.getConnection(adminUrl)) {
                c.setAutoCommit(true);

                if (databaseExists(c, dbName)) {return;}

                String sql = "CREATE DATABASE " + quoteIdent(dbName);
                try (Statement st = c.createStatement()) {
                    st.executeUpdate(sql);
                }
            }
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    private static boolean databaseExists(Connection c, String dbName) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "select 1 from pg_database where datname = ?")) {
            ps.setString(1, dbName);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private static String quoteIdent(String ident) {
        return "\"" + ident.replace("\"", "\"\"") + "\"";
    }
}
