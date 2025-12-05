package net.codeer.app.single;

import io.javalin.http.Context;
import net.codeer.app.common.AppBase;
import net.codeer.app.common.TodoDAO;
import net.codeer.app.db.EnsurePsqlDatabases;
import net.codeer.app.db.MongoMigration;
import net.codeer.app.db.PostgresMigration;
import net.codeer.app.db.PostgresSingleDatabaseProvider;
import net.codeer.app.db.MongoDatabaseProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

public class AppSingle extends AppBase {
    private static final Logger LOG = LoggerFactory.getLogger("App");

    static void main(String[] args) {
        new AppSingle().run(args);
    }

    @Override
    protected String getName() {
        return "Single Tenant TodoApp";
    }

    @Override
    protected void onServerStopping() throws Exception {
        MongoDatabaseProvider.close();
        PostgresSingleDatabaseProvider.close();
    }

    @Override
    protected void initPsql() {
        EnsurePsqlDatabases.ensureDatabaseExists(System.getenv("ADMIN_PSQL_CONNECTION_STRING"), "todoapp_single");
        PostgresSingleDatabaseProvider.init(System.getenv("PSQL_CONNECTION_STRING"));
        PostgresMigration.migrate(PostgresSingleDatabaseProvider.getDataSource(), "public","migration/single");
    }

    @Override
    protected Function<Context, TodoDAO> getPsqlTodoDaoProvider() {
        return (_) -> new PsqlTodoDAO(PostgresSingleDatabaseProvider.getDataSource());
    }

    @Override
    protected void initMongo() {
        var connectionString = System.getenv("MONGO_CONNECTION_STRING");
        MongoDatabaseProvider.init(connectionString);
        MongoMigration.migrate(connectionString, "todoapp_single", "migration/single");
    }

    @Override
    protected Function<Context, TodoDAO> getMongoTodoDaoProvider() {
        return (_) -> new MongoTodoDAO(MongoDatabaseProvider.getDatabase("todoapp_single"));
    }
}
