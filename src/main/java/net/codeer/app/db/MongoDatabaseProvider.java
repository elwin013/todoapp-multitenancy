package net.codeer.app.db;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.io.IOException;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;


public class MongoDatabaseProvider {
    private static MongoClient mongoClient;
    private static MongoDatabase mongoDatabase;

    /**
     * Required to correctly map the POJOs from-to Mongo.
     */
    public static final CodecRegistry pojoCodecRegistry = fromRegistries(
            MongoClientSettings.getDefaultCodecRegistry(),
            fromProviders(PojoCodecProvider.builder().automatic(true).build())
    );

    public static void init(String connectionString, String dbName) {
        mongoClient = MongoClients.create(connectionString);
        mongoDatabase = mongoClient.getDatabase(dbName).withCodecRegistry(pojoCodecRegistry);
    }

    public static MongoDatabase getDatabase() {
        return mongoDatabase;
    }


    public static void close() throws IOException {
        mongoClient.close();
    }
}
