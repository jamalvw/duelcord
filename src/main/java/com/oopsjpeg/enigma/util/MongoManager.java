package com.oopsjpeg.enigma.util;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.oopsjpeg.enigma.Enigma;
import com.oopsjpeg.enigma.storage.Player;
import org.bson.Document;

import java.util.function.Consumer;

public class MongoManager {
    private final MongoClient client;
    private final String database;

    public MongoManager(String host, String database) {
        client = MongoClients.create(host);
        this.database = database;
    }

    public MongoCollection<Document> getPlayers() {
        return client.getDatabase(database).getCollection("players");
    }

    public void loadPlayers() {
        getPlayers().find().forEach((Consumer<Document>) this::loadPlayer);
    }

    public void savePlayers() {
        //Enigma.getInstance().getPlayers().values().forEach(this::savePlayer);
    }

    public void loadPlayer(Document d) {
        //Enigma.getInstance().getPlayers().put(d.getLong("_id"), Enigma.GSON.fromJson(JSON.serialize(d), Player.class));
    }

    public void savePlayer(Player p) {
        getPlayers().replaceOne(Filters.eq(p.getId()), Document.parse(Enigma.GSON.toJson(p)), new ReplaceOptions().upsert(true));
    }
}
