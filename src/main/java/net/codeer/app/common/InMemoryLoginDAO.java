package net.codeer.app.common;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class InMemoryLoginDAO implements LoginDAO {

    private final Map<String, User> users;

    public InMemoryLoginDAO(List<User> initialUsers) {
        if (initialUsers != null) {
            users = initialUsers.stream().collect(Collectors.toMap(User::email, user -> user));
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public Optional<UserInfo> authenticate(String email, String password) {
        if (email == null || password == null) return Optional.empty();
        var user = users.get(email);

        if (user != null && Objects.equals(user.password, password)) {
            return Optional.of(new UserInfo(user.email, user.tenantId));
        }
        return Optional.empty();
    }

    public record User(String email, String password, Long tenantId) {
        public User(String email, String password) {
            this(email, password, null);
        }
    }
}
