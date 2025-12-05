package net.codeer.app.multiRowLevel;

import net.codeer.app.multiRowLevel.jooq.tables.Todos;
import net.codeer.app.common.Todo;
import net.codeer.app.common.TodoDAO;
import net.codeer.app.common.UserInfo;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

class PsqlTodoDAO implements TodoDAO {
    protected final DSLContext ctx;
    private final Todos table;
    private final Long tenantId;

    public PsqlTodoDAO(DataSource dataSource, Long tenantId) {
        this.table = Todos.TODOS;
        this.ctx = DSL.using(dataSource, SQLDialect.POSTGRES);
        this.tenantId = tenantId;
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
           .set(table.TENANT_ID, user.tenantId())
           .set(table.DONE, false)
           .execute();

        return id;
    }

    @Override
    public List<Todo> getTodos() {
        return ctx.selectFrom(table)
                  .where(table.TENANT_ID.eq(this.tenantId))
                  .orderBy(table.CREATED.desc())
                  .fetchInto(Todo.class);
    }

    @Override
    public void deleteTodo(String id) {
        ctx.deleteFrom(table)
           .where(table.TENANT_ID.eq(this.tenantId).and(table.ID.eq(id)))
           .execute();
    }

    @Override
    public void markAsDone(String id) {
        ctx.update(table)
           .set(table.DONE, true)
           .where(table.TENANT_ID.eq(this.tenantId).and(table.ID.eq(id)))
           .execute();
    }

    @Override
    public void markAsNotDone(String id) {
        ctx.update(table)
           .set(table.DONE, false)
           .where(table.TENANT_ID.eq(this.tenantId).and(table.ID.eq(id)))
           .execute();
    }
}