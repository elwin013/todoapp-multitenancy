package net.codeer.app.common;

import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import static net.codeer.app.common.CustomAccessManager.SESSION_USER;

public class TodoController {

    private final Function<Context, TodoDAO> todoDaoProvider;

    public TodoController(Function<Context, TodoDAO> todoDaoProvider) {
        this.todoDaoProvider = todoDaoProvider;
    }

    public void registerRoutes(Javalin app) {
        app.get("/", this::listTodos);
        app.post("/", this::createTodo);
        app.get("/{id}/delete", this::deleteTodo);
        app.get("/{id}/done", this::markAsDone);
        app.get("/{id}/undone", this::markAsNotDone);
    }

    private void listTodos(Context ctx) {
        List<Todo> todos = todoDaoProvider.apply(ctx).getTodos();
        Map<String, Object> model = new HashMap<>();
        model.put("completed", todos.stream().filter(Todo::getDone).toList());
        model.put("todos", todos.stream().filter(Predicate.not(Todo::getDone)).toList());
        model.put("user", ctx.sessionAttribute(SESSION_USER));
        ctx.render("list-todo.jte", model);
    }


    private void createTodo(Context ctx) {
        var todo = ctx.formParam("content");
        todoDaoProvider.apply(ctx).addTodo(todo, ctx.sessionAttribute(SESSION_USER));
        ctx.redirect("/");
    }

    private void deleteTodo(Context ctx) {
        var id = ctx.pathParam("id");
        todoDaoProvider.apply(ctx).deleteTodo(id);
        ctx.redirect("/");
    }

    private void markAsDone(Context ctx) {
        var id = ctx.pathParam("id");
        todoDaoProvider.apply(ctx).markAsDone(id);
        ctx.redirect("/");
    }

    private void markAsNotDone(Context ctx) {
        var id = ctx.pathParam("id");
        todoDaoProvider.apply(ctx).markAsNotDone(id);
        ctx.redirect("/");
    }
}
