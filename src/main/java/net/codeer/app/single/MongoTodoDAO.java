package net.codeer.app.single;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import net.codeer.app.common.Todo;
import net.codeer.app.common.TodoDAO;
import net.codeer.app.common.UserInfo;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class MongoTodoDAO implements TodoDAO {
    private final MongoCollection<Todo> collection;

    public MongoTodoDAO(MongoDatabase db) {
        this.collection = db.getCollection("todos", Todo.class);
    }


    @Override
    public String addTodo(String content, UserInfo user) {
        var id = UUID.randomUUID().toString();
        collection.insertOne(new Todo(id, content, Instant.now(), user.email()));
        return id;
    }

    @Override
    public List<Todo> getTodos() {
        return collection.find().sort(Sorts.descending("created")).into(new ArrayList<>());
    }

    @Override
    public void deleteTodo(String id) {
        collection.deleteOne(Filters.eq(id));
    }

    @Override
    public void markAsDone(String id) {
        collection.updateOne(Filters.eq(id), Updates.set("done", true));
    }

    @Override
    public void markAsNotDone(String id) {
        collection.updateOne(Filters.eq(id), Updates.set("done", false));
    }
}
