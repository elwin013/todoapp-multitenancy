package net.codeer.app.db;

import org.flywaydb.core.Flyway;

import javax.sql.DataSource;
import java.util.Map;

public final class MongoMigration {
    public static void migrate(String connectionString, String databaseName, String migrationLocations) {
        Flyway flyway = Flyway.configure()
                .configuration(Map.of("flyway.url", withDatabase(connectionString, databaseName)))
                              .locations(migrationLocations)
                              .sqlMigrationSuffixes(".json")
                              .load();

        flyway.migrate();
    }

    private static String withDatabase(String mongoUri, String db) {
        int q = mongoUri.indexOf('?');
        String base = (q >= 0) ? mongoUri.substring(0, q) : mongoUri;
        String query = (q >= 0) ? mongoUri.substring(q) : "";

        int schemeEnd = base.indexOf("://");
        int slash = base.indexOf('/', (schemeEnd >= 0 ? schemeEnd + 3 : 0));

        String prefix = (slash >= 0) ? base.substring(0, slash) : base;
        return prefix + "/" + db + query;
    }
}
