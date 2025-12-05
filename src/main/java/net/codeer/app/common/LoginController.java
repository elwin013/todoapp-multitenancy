package net.codeer.app.common;

import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static net.codeer.app.common.CustomAccessManager.*;

public class LoginController {

    private final LoginDAO loginDAO;

    public LoginController(LoginDAO loginDAO) {
        this.loginDAO = loginDAO;
    }

    public void registerRoutes(Javalin app) {
        app.get("/login", this::showLogin);
        app.post("/login", this::doLogin);
        app.get("/logout", this::logout);
    }

    private void showLogin(Context ctx) {
        Map<String, Object> model = new HashMap<>();
        model.put("error", "");
        ctx.render("login.jte", model);
    }

    private void doLogin(Context ctx) {
        String email = Optional.ofNullable(ctx.formParam("email")).orElse("").trim();
        String password = Optional.ofNullable(ctx.formParam("password")).orElse("");

        if (email.isBlank() || password.isBlank()) {
            Map<String, Object> model = new HashMap<>();
            model.put("error", "Email and password are required.");
            ctx.status(400).render("login.jte", model);
            return;
        }

        var loginResult = loginDAO.authenticate(email, password);

        if (loginResult.isEmpty()) {
            Map<String, Object> model = new HashMap<>();
            model.put("error", "Invalid email or password.");
            ctx.status(401).render("login.jte", model);
            return;
        }

        ctx.sessionAttribute(SESSION_USER, loginResult.get());

        ctx.redirect("/");
    }

    private void logout(Context ctx) {
        ctx.req().getSession().invalidate();
        ctx.redirect("/login");
    }
}
