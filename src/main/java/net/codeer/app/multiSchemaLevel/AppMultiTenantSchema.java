package net.codeer.app.multiSchemaLevel;

import io.javalin.http.Context;
import net.codeer.app.common.AppBase;
import net.codeer.app.common.TodoDAO;
import net.codeer.app.db.EnsurePsqlDatabases;
import net.codeer.app.db.MongoDatabaseProvider;
import net.codeer.app.db.PostgresSingleDatabaseProvider;
import net.codeer.app.common.CustomAccessManager;
import net.codeer.app.common.InMemoryLoginDAO;

import java.util.List;
import java.util.function.Function;

public class AppMultiTenantSchema extends AppBase {
    static void main(String[] args) {
        new AppMultiTenantSchema().run(args);
    }

    @Override
    protected String getName() {
        return "Multitenant Tenant TodoApp - Schema";
    }

    @Override
    protected void onServerStopping() throws Exception {
        MongoDatabaseProvider.close();
        PostgresSingleDatabaseProvider.close();
    }

    @Override
    protected void initPsql() {
        EnsurePsqlDatabases.ensureDatabaseExists(System.getenv("ADMIN_PSQL_CONNECTION_STRING"), "todoapp_schemaLevel");
        PostgresSingleDatabaseProvider.init(System.getenv("PSQL_CONNECTION_STRING"));
    }

    @Override
    protected Function<Context, TodoDAO> getPsqlTodoDaoProvider() {
        return  context -> new PsqlTodoDAO(PostgresSingleDatabaseProvider.getDataSource(), CustomAccessManager.getUserInfo(context).tenantId());
    }

    @Override
    protected void initMongo() {
        MongoDatabaseProvider.init(System.getenv("MONGO_CONNECTION_STRING"));
    }

    @Override
    protected Function<Context, TodoDAO> getMongoTodoDaoProvider() {
        return context ->
                new MongoTodoDAO(MongoDatabaseProvider.getDatabase("todoapp_schemaLevel"),
                        CustomAccessManager.getUserInfo(context).tenantId());
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
