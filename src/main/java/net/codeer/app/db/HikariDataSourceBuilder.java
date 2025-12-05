package net.codeer.app.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.postgresql.ds.PGSimpleDataSource;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class HikariDataSourceBuilder {
    public static HikariDataSource buildHikariDataSource(String url) {
        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setUrl(url);

        // Only set creds if they exist in the URL, otherwise do nothing.
        Creds creds = extractCredentials(url);
        if (creds != null) {
            ds.setUser(creds.user);
            ds.setPassword(creds.pass);
        }

        HikariConfig hc = new HikariConfig();
        hc.setPoolName("psql-pool");
        hc.setDataSource(ds);
        hc.setMaximumPoolSize(10);

        return new HikariDataSource(hc);
    }

    /**
     * Extracts user/password from JDBC URL query string, for example:
     *   ...?user=foo&password=bar
     * Returns null if either is missing.
     */
    private static Creds extractCredentials(String url) {
        int q = url.indexOf('?');
        if (q < 0) return null;

        String query = url.substring(q + 1);
        String user = null;
        String pass = null;

        for (String part : query.split("&")) {
            int eq = part.indexOf('=');
            if (eq <= 0) continue;

            String k = part.substring(0, eq);
            String v = decode(part.substring(eq + 1));

            if ("user".equals(k)) user = v;
            else if ("password".equals(k)) pass = v;
        }

        if (user == null || pass == null) return null;
        return new Creds(user, pass);
    }

    private static String decode(String s) {
        return URLDecoder.decode(s, StandardCharsets.UTF_8);
    }

    private static final class Creds {
        final String user;
        final String pass;

        Creds(String user, String pass) {
            this.user = user;
            this.pass = pass;
        }
    }
}
