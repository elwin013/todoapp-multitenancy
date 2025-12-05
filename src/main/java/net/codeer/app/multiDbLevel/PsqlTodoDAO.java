package net.codeer.app.multiDbLevel;

import net.codeer.app.common.Todo;
import net.codeer.app.common.TodoDAO;
import net.codeer.app.common.UserInfo;
import net.codeer.app.multiDbLevel.jooq.tables.Todos;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

class PsqlTodoDAO implements TodoDAO {
    protected final DSLContext ctx;
    private final Todos table;

    private static final ConcurrentHashMap<Long, Long> migrations = new ConcurrentHashMap<>();

    public PsqlTodoDAO(DataSource dataSource) {
        this.table = Todos.TODOS;

        this.ctx = DSL.using(
                dataSource,
                SQLDialect.POSTGRES
        );
    }

    @Override
    public String addTodo(String content, UserInfo user) {
        String id = UUID.randomUUID().toString();
        LocalDateTime created = LocalDateTime.now(ZoneOffset.UTC);

        ctx.insertInto(table)
           .set(table.ID, id)
           .set(table.CONTENT, content)
           .set(table.CREATED, created)
           .set(table.CREATED_BY, user.email())
           .set(table.DONE, false)
           .execute();

        return id;
    }

    @Override
    public List<Todo> getTodos() {
        return ctx.selectFrom(table)
                  .orderBy(table.CREATED.desc())
                  .fetchInto(Todo.class);
    }

    @Override
    public void deleteTodo(String id) {
        ctx.deleteFrom(table)
           .where(table.ID.eq(id))
           .execute();
    }

    @Override
    public void markAsDone(String id) {
        ctx.update(table)
           .set(table.DONE, true)
           .where(table.ID.eq(id))
           .execute();
    }

    @Override
    public void markAsNotDone(String id) {
        ctx.update(table)
           .set(table.DONE, false)
           .where(table.ID.eq(id))
           .execute();
    }
}