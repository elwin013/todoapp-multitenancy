package net.codeer.app.common;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.staticfiles.Location;
import io.javalin.rendering.template.JavalinJte;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public abstract class AppBase {
    private static final Logger LOG = LoggerFactory.getLogger("App");

    protected abstract String getName();
    protected abstract void onServerStopping() throws Exception;
    protected abstract void initPsql();
    protected abstract Function<Context, TodoDAO> getPsqlTodoDaoProvider();
    protected abstract void initMongo();
    protected abstract Function<Context, TodoDAO> getMongoTodoDaoProvider();


    public void run(String[] args) {
        LOG.info("Starting app {}", getName());
        LOG.info("Args are: {}", Arrays.toString(args));

        if(args.length < 1) {
            return;
        }

        var todoDaoProvider = getTodoDaoProvider(args);

        var app = Javalin.create(config -> {
            config.startup.showJavalinBanner = false;
            config.fileRenderer(new JavalinJte(JavalinJte.Companion.directoryTemplateEngine()));
            config.staticFiles.add("/static", Location.CLASSPATH);

            // Auth
            config.routes.beforeMatched(CustomAccessManager::beforeMatched);
            new LoginController(new InMemoryLoginDAO(
                    getInitialUsers()
            )).registerRoutes(config.routes);

            //
            new TodoController(todoDaoProvider).registerRoutes(config.routes);

            config.events.serverStopping(() -> {
                LOG.info("Stopping app {}", getName());
                onServerStopping();
            });
        });

        app.start(7070);
    }

    private Function<Context, TodoDAO> getTodoDaoProvider(String[] args) {
        Function<Context, TodoDAO> todoDaoProvider;
        switch (args[0]) {
            case "mongo" -> {
                LOG.info("Using Mongo!");
                initMongo();
                todoDaoProvider = getMongoTodoDaoProvider();
            }
            case "psql" -> {
                LOG.info("Using PostgreSQL!");
                initPsql();
                todoDaoProvider = getPsqlTodoDaoProvider();
            }
            default -> throw new IllegalArgumentException("No database selected");
        }
        return todoDaoProvider;
    }

    protected List<InMemoryLoginDAO.User> getInitialUsers() {
        return List.of(
                new InMemoryLoginDAO.User("kamil@example.com", "asdf1234"),
                new InMemoryLoginDAO.User("paulina@example.com", "asdf1234")
        );
    }
}
