package net.codeer.app.multiRowLevel;

import java.time.Instant;

public class Todo extends net.codeer.app.common.Todo {
    Long tenantId;

    public Todo() {}

    public Todo(String id, String content, Instant now, String email, Long tenantId) {
        super(id, content, now, email);
        this.tenantId = tenantId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }
}
