package net.codeer.app.common;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.staticfiles.Location;
import io.javalin.rendering.template.JavalinJte;
import org.jetbrains.annotations.NotNull;
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

        var app = Javalin.create(config -> {
            config.showJavalinBanner = false;
            config.fileRenderer(new JavalinJte());
            config.staticFiles.add("/static", Location.CLASSPATH);
        });

        addAuth(app);

        Function<Context, TodoDAO> todoDaoProvider = null;

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

        new TodoController(todoDaoProvider).registerRoutes(app);

        app.events(event -> event.serverStopping(() -> {
            LOG.info("Stopping app {}", getName());
            onServerStopping();

        }));

        app.start(7070);
    }



    private void addAuth(Javalin app) {
        app.beforeMatched(CustomAccessManager::beforeMatched);

        new LoginController(new InMemoryLoginDAO(
                getInitialUsers()
        )).registerRoutes(app);
    }

    protected List<InMemoryLoginDAO.User> getInitialUsers() {
        return List.of(
                new InMemoryLoginDAO.User("kamil@example.com", "asdf1234"),
                new InMemoryLoginDAO.User("paulina@example.com", "asdf1234")
        );
    }
}
