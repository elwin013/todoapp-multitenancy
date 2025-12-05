package net.codeer.app.common;

import java.time.Instant;
import java.util.Objects;

public class Todo {
    private String id;
    private String content;
    private Boolean done = false;
    private Instant created;
    private String createdBy;

    public Todo() {
    }

    public Todo(String id, String content, Instant created, String createdBy) {
        this.id = id;
        this.content = content;
        this.created = created;
        this.createdBy = createdBy;
    }

    public Todo(String id, String content, Instant created, String createdBy, boolean done) {
        this.id = id;
        this.content = content;
        this.created = created;
        this.createdBy = createdBy;
        this.done = done;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Boolean getDone() {
        return done;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {return true;}
        if (obj == null || obj.getClass() != this.getClass()) {return false;}
        var that = (Todo) obj;
        return this.id == that.id &&
               Objects.equals(this.content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, content);
    }

    @Override
    public String toString() {
        return "Todo[" +
               "id=" + id + ", " +
               "content=" + content + ']';
    }
}
