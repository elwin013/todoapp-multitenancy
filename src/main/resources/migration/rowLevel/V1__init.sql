create schema if not exists ${schema};

create table ${schema}.todos
(
    id         varchar(36),
    content    text         NOT NULL,
    done       bool         NOT NULL DEFAULT FALSE,
    created    timestamp    NOT NULL DEFAULT now(),
    created_by varchar(255) NOT NULL,
    tenant_id  bigint       NOT NULL,

    primary key (id)
);

CREATE INDEX IF NOT EXISTS idx_todo_created ON ${schema}.todos (created DESC);
CREATE INDEX IF NOT EXISTS idx_todo_created_by ON ${schema}.todos (created_by);
CREATE INDEX IF NOT EXISTS idx_todo_tenant_id ON ${schema}.todos (tenant_id);