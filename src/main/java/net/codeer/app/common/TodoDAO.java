package net.codeer.app.common;

import java.util.List;

public interface TodoDAO {
    String addTodo(String content, UserInfo user);

    List<Todo> getTodos();

    void deleteTodo(String id);

    void markAsDone(String id);

    void markAsNotDone(String id);

}