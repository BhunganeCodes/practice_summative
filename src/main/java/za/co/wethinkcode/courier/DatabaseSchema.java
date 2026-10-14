package za.co.wethinkcode.courier;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSchema {

    private DatabaseSchema() {
    }

    public static void createSchema(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            // TODO: create the `customers`, `parcels` and `delivery_attempts`
            // tables described in resources/erd.png, including the primary key
            // on each table, the NOT NULL and UNIQUE constraints, and both
            // foreign keys.
            //
            // Execute one CREATE TABLE statement per table, like this:
            //
            //     statement.execute("""
            //             CREATE TABLE ...
            //             """);
        }
    }
}
