package net.codeer.app.db;

import org.flywaydb.core.Flyway;

import javax.sql.DataSource;
import java.util.Map;

public final class PostgresMigration {
    public static void migrate(DataSource dataSource, String schema, String location) {
        Flyway flyway = Flyway.configure()
                              .dataSource(dataSource)
                              .schemas(schema)
                              .placeholders(Map.of("schema", schema))
                              .locations(location)
                              .load();

        flyway.migrate();
    }
}
