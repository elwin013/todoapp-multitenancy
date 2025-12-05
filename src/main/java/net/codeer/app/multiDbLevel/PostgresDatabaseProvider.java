package net.codeer.app.multiDbLevel;

import com.zaxxer.hikari.HikariDataSource;
import net.codeer.app.db.PostgresMigration;
import net.codeer.app.db.EnsurePsqlDatabases;
import net.codeer.app.db.HikariDataSourceBuilder;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PostgresDatabaseProvider {
    private static Map<String, HikariDataSource> databaseCache;
    private static String jdbcUrl;
    private static String adminJdbcUrl;

    public static void init(String adminJdbc, String tenantJdbc) {
        adminJdbcUrl = adminJdbc;
        jdbcUrl = tenantJdbc;
        databaseCache = new ConcurrentHashMap<>();
    }

    public static DataSource getDatasource(String databaseName) {
        return databaseCache.computeIfAbsent(databaseName, dbName -> {
            EnsurePsqlDatabases.ensureDatabaseExists(adminJdbcUrl, dbName);
            var ds = HikariDataSourceBuilder.buildHikariDataSource(getUrl(jdbcUrl, dbName));
            PostgresMigration.migrate(ds, "public", "migration/dbLevel");
            return ds;
        });
    }

    private static String getUrl(String jdbcUrl, String databaseName) {
        return jdbcUrl.replace("{DATABASE}", databaseName);
    }

    public static void close() {
        if (databaseCache != null) {
            databaseCache.forEach((string, dataSource) -> dataSource.close());
        }
    }
}
