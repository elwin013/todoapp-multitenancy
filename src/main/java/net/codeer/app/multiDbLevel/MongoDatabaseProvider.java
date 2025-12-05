package net.codeer.app.multiDbLevel;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import net.codeer.app.db.MongoMigration;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.io.IOException;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;


public class MongoDatabaseProvider {
    private static MongoClient mongoClient;
    private static String mongoConnectionString;
    /**
     * Required to correctly map the POJOs from-to Mongo.
     */
    public static final CodecRegistry pojoCodecRegistry = fromRegistries(
            MongoClientSettings.getDefaultCodecRegistry(),
            fromProviders(PojoCodecProvider.builder().automatic(true).build())
    );


    public static void init(String connectionString) {
        mongoConnectionString = connectionString;
        mongoClient = MongoClients.create(connectionString);
    }

    public static MongoDatabase getDatabase(String dbName) {
        MongoMigration.migrate(mongoConnectionString, dbName, "migration/single");

        return mongoClient.getDatabase(dbName).withCodecRegistry(pojoCodecRegistry);
    }


    public static void close() throws IOException {
        if (mongoClient != null) {mongoClient.close();}
    }
}
