package net.codeer.app.todo;

import java.util.List;

public interface TodoDAO {
    String addTodo(String content);

    List<Todo> getTodos();

    void deleteTodo(String id);

    void markAsDone(String id);

    void markAsNotDone(String id);

}