
package tools;

import net.codeer.app.db.PostgresSingleDatabaseProvider;
import net.codeer.app.db.PostgresMigration;
import org.jooq.codegen.GenerationTool;
import org.jooq.meta.jaxb.Configuration;
import org.jooq.meta.jaxb.Database;
import org.jooq.meta.jaxb.Generate;
import org.jooq.meta.jaxb.Generator;
import org.jooq.meta.jaxb.Jdbc;
import org.jooq.meta.jaxb.Target;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

public class JooqGenerateFromTestcontainers {

    public static void main(String[] args) throws Exception {
        try (var pg = new PostgreSQLContainer(DockerImageName.parse("postgres:17"))) {
            pg.withUrlParam("user", "test");
            pg.withUrlParam("password", "test");
            pg.start();

            var pkg = args.length > 0 ? args[0] : "net.codeer.app.db.jooq";
            var migration = args.length > 1 ? args[1] : "migration/single";

            PostgresSingleDatabaseProvider.init(pg.getJdbcUrl());
            PostgresMigration.migrate(PostgresSingleDatabaseProvider.getDataSource(), "public", migration);

            var jooqConfig = new Configuration()
                    .withJdbc(new Jdbc()
                            .withDriver("org.postgresql.Driver")
                            .withUrl(pg.getJdbcUrl())
                            .withUser(pg.getUsername())
                            .withPassword(pg.getPassword()))
                    .withGenerator(new Generator()
                            .withDatabase(new Database()
                                    .withName("org.jooq.meta.postgres.PostgresDatabase")
                                    .withInputSchema("public")
                                    .withIncludes(".*")
                                    .withExcludes("flyway_schema_history"))
                            .withGenerate(new Generate()
                                    .withRecords(true)
                                    .withPojos(false)
                                    .withDaos(false))
                            .withTarget(new Target()
                                    .withPackageName(pkg)
                                    .withDirectory("src/main/java"))
                    );
//                                    .withDirectory("target/generated-sources/jooq")));

            GenerationTool.generate(jooqConfig);
        }
    }
}
