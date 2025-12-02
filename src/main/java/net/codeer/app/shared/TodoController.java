package net.codeer.app;

import io.javalin.Javalin;
import io.javalin.http.Context;
import net.codeer.app.todo.Todo;
import net.codeer.app.todo.TodoDAO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class TodoController {

    private final TodoDAO todoDAO;

    public TodoController(TodoDAO todoDAO) {
        this.todoDAO = todoDAO;
    }

    public void registerRoutes(Javalin app) {
        app.get("/", this::listTodos);
        app.post("/", this::createTodo);
        app.get("/{id}/delete", this::deleteTodo);
        app.get("/{id}/done", this::markAsDone);
        app.get("/{id}/undone", this::markAsNotDone);
    }

    private void listTodos(Context ctx) {
        List<Todo> todos = todoDAO.getTodos();
        Map<String, Object> model = new HashMap<>();
        model.put("completed", todos.stream().filter(Todo::getDone).toList());
        model.put("todos", todos.stream().filter(Predicate.not(Todo::getDone)).toList());
        ctx.render("todos/list.jte", model);
    }


    private void createTodo(Context ctx) {
        var todo = ctx.formParam("content");
        todoDAO.addTodo(todo);
        ctx.redirect("/");
    }

    private void deleteTodo(Context ctx) {
        var id = ctx.pathParam("id");
        todoDAO.deleteTodo(id);
        ctx.redirect("/");
    }

    private void markAsDone(Context ctx) {
        var id = ctx.pathParam("id");
        todoDAO.markAsDone(id);
        ctx.redirect("/");
    }

    private void markAsNotDone(Context ctx) {
        var id = ctx.pathParam("id");
        todoDAO.markAsNotDone(id);
        ctx.redirect("/");
    }
}
