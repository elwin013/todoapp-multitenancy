package net.codeer.app.multiSchemaLevel;

import net.codeer.app.multiSchemaLevel.jooq.tables.Todos;
import net.codeer.app.db.PostgresMigration;
import net.codeer.app.common.Todo;
import net.codeer.app.common.TodoDAO;
import net.codeer.app.common.UserInfo;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.MappedSchema;
import org.jooq.conf.RenderMapping;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

import javax.sql.DataSource;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

class PsqlTodoDAO implements TodoDAO {
    protected final DSLContext ctx;
    private final Todos table;

    private static final ConcurrentHashMap<String, Long> migrations = new ConcurrentHashMap<>();

    public PsqlTodoDAO(DataSource dataSource, Long tenantId) {
        this.table = Todos.TODOS;
        var schemaName = "t" + tenantId;

        this.ctx = DSL.using(
                dataSource,
                SQLDialect.POSTGRES,
                getTenantSettings(schemaName)
        );

        migrations.computeIfAbsent(schemaName, s -> {
            PostgresMigration.migrate(dataSource, schemaName, "migration/schemaLevel");
            return Instant.now().getEpochSecond();
        });

    }

    private static Settings getTenantSettings(String schemaName) {
        return new Settings().withRenderMapping(new RenderMapping().withSchemata(
                new MappedSchema().withInput("public").withOutput(schemaName))
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