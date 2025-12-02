package net.codeer.app;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.rendering.template.JavalinJte;
import net.codeer.app.db.MongoDatabaseProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class App {
    private static final Logger LOG = LoggerFactory.getLogger("App");

    static void main(String[] args) {
        LOG.info("Hello!");
        LOG.info("Args are: {}", Arrays.toString(args));

        var app = Javalin.create(config -> {
            config.showJavalinBanner = false;
            // Precompile templates so they will be available in container
            // replace with new JavalinJte() for local hot reload
            config.fileRenderer(new JavalinJte());
            config.staticFiles.add("/static", Location.CLASSPATH);
        });

        if (args.length > 0 && args[0].equals("mongo")) {
            LOG.info("Using Mongo DAO!");
            MongoDatabaseProvider.init(System.getenv("MONGO_CONNECTION_STRING"), "todos");
            new TodoController(new MongoTodoDAO(MongoDatabaseProvider.getDatabase())).registerRoutes(app);

            app.events(event -> {
                event.serverStopping(MongoDatabaseProvider::close);
            });
        } else if (args.length > 0 && args[0].equals("psql")) {
            LOG.info("Using PSQL DAO!");
        } else {
            throw new IllegalArgumentException("No database selected");
        }


        app.start(7070);
    }
}
