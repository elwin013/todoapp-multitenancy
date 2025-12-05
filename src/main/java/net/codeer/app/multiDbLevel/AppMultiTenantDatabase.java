package net.codeer.app.multiDbLevel;

import io.javalin.http.Context;
import net.codeer.app.common.AppBase;
import net.codeer.app.common.CustomAccessManager;
import net.codeer.app.common.InMemoryLoginDAO;
import net.codeer.app.common.TodoDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Function;

public class AppMultiTenantDatabase extends AppBase {
    private static final Logger LOG = LoggerFactory.getLogger("App");

    static void main(String[] args) {
        new AppMultiTenantDatabase().run(args);
    }

    @Override
    protected String getName() {
        return "Multitenant Tenant TodoApp - Database";
    }

    @Override
    protected void onServerStopping() throws Exception {
        MongoDatabaseProvider.close();
        PostgresDatabaseProvider.close();
    }

    @Override
    protected void initPsql() {
        PostgresDatabaseProvider.init(System.getenv("ADMIN_PSQL_CONNECTION_STRING"),
                System.getenv("PSQL_CONNECTION_STRING"));

    }

    @Override
    protected Function<Context, TodoDAO> getPsqlTodoDaoProvider() {
        return context -> new PsqlTodoDAO(PostgresDatabaseProvider.getDatasource(
                "t" + CustomAccessManager.getUserInfo(context).tenantId()));
    }

    @Override
    protected void initMongo() {
        MongoDatabaseProvider.init(System.getenv("MONGO_CONNECTION_STRING"));
    }

    @Override
    protected Function<Context, TodoDAO> getMongoTodoDaoProvider() {
        return context -> new MongoTodoDAO(MongoDatabaseProvider.getDatabase(
                "t" + CustomAccessManager.getUserInfo(context).tenantId()));
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
