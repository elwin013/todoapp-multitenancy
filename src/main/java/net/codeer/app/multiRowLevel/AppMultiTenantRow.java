package net.codeer.app.multiRowLevel;

import io.javalin.http.Context;
import net.codeer.app.common.AppBase;
import net.codeer.app.common.TodoDAO;
import net.codeer.app.db.EnsurePsqlDatabases;
import net.codeer.app.db.MongoDatabaseProvider;
import net.codeer.app.db.MongoMigration;
import net.codeer.app.db.PostgresSingleDatabaseProvider;
import net.codeer.app.common.CustomAccessManager;
import net.codeer.app.common.InMemoryLoginDAO;
import net.codeer.app.db.PostgresMigration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Function;

public class AppMultiTenantRow extends AppBase {
    private static final Logger LOG = LoggerFactory.getLogger("App");

    static void main(String[] args) {
        new AppMultiTenantRow().run(args);
    }

    @Override
    protected String getName() {
        return "Multitenant Tenant TodoApp - Row";
    }

    @Override
    protected void onServerStopping() throws Exception {
        MongoDatabaseProvider.close();
        PostgresSingleDatabaseProvider.close();
    }

    @Override
    protected void initPsql() {
        // FIXME: Add example with Row Level Security options for PSQL
        EnsurePsqlDatabases.ensureDatabaseExists(System.getenv("ADMIN_PSQL_CONNECTION_STRING"), "todoapp_rowLevel");
        PostgresSingleDatabaseProvider.init(System.getenv("PSQL_CONNECTION_STRING"));
        PostgresMigration.migrate(PostgresSingleDatabaseProvider.getDataSource(), "public", "migration/rowLevel");
    }

    @Override
    protected Function<Context, TodoDAO> getPsqlTodoDaoProvider() {
        return context -> new PsqlTodoDAO(PostgresSingleDatabaseProvider.getDataSource(), CustomAccessManager.getUserInfo(context).tenantId());
    }

    @Override
    protected void initMongo() {
        var connectionString = System.getenv("MONGO_CONNECTION_STRING");
        MongoDatabaseProvider.init(connectionString);
        MongoMigration.migrate(connectionString, "todoapp_single", "migration/rowLevel");

    }

    @Override
    protected Function<Context, TodoDAO> getMongoTodoDaoProvider() {
        return context ->
                new MongoTodoDAO(MongoDatabaseProvider.getDatabase("todoapp_rowLevel"), CustomAccessManager.getUserInfo(context).tenantId());
    }

    @Override
    protected List<InMemoryLoginDAO.User> getInitialUsers() {
        return List.of(
                new InMemoryLoginDAO.User("kamil@signaturky.com", "asdf1234", 1L),
                new InMemoryLoginDAO.User("paulina@codeer.net", "asdf1234", 2L),
                new InMemoryLoginDAO.User("lusia@codeer.net", "asdf1234", 2L)
        );
    }
}
