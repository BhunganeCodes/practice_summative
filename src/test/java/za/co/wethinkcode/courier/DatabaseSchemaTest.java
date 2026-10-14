package za.co.wethinkcode.courier;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DatabaseSchemaTest {

    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        connection = Database.connect(":memory:");
        DatabaseSchema.createSchema(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        connection.close();
    }

    // --- Q5.1 columns and types ---

    @Test
    void customersTable_hasCorrectColumnsAndTypes() throws SQLException {
        assertEquals(Map.of(
                "id", "INTEGER",
                "full_name", "TEXT",
                "phone", "TEXT",
                "email", "TEXT"), columnTypes("customers"));
    }

    @Test
    void parcelsTable_hasCorrectColumnsAndTypes() throws SQLException {
        assertEquals(Map.of(
                "id", "INTEGER",
                "tracking_no", "TEXT",
                "weight_kg", "REAL",
                "service_level", "TEXT",
                "customer_id", "INTEGER"), columnTypes("parcels"));
    }

    @Test
    void deliveryAttemptsTable_hasCorrectColumnsAndTypes() throws SQLException {
        assertEquals(Map.of(
                "id", "INTEGER",
                "parcel_id", "INTEGER",
                "attempted_at", "TEXT",
                "outcome", "TEXT",
                "driver_note", "TEXT"), columnTypes("delivery_attempts"));
    }

    // --- Q5.2 primary keys and NOT NULL ---

    @Test
    void everyTable_hasIdAsPrimaryKey() throws SQLException {
        assertEquals(true, columnFlags("customers", "pk").get("id"), "customers.id should be the primary key");
        assertEquals(true, columnFlags("parcels", "pk").get("id"), "parcels.id should be the primary key");
        assertEquals(true, columnFlags("delivery_attempts", "pk").get("id"), "delivery_attempts.id should be the primary key");
    }

    @Test
    void customersTable_notNullColumnsMatchErd() throws SQLException {
        Map<String, Boolean> notNull = columnFlags("customers", "notnull");
        assertEquals(true, notNull.get("full_name"));
        assertEquals(true, notNull.get("phone"));
        assertEquals(false, notNull.get("email"), "email is optional in the ERD");
    }

    @Test
    void parcelsTable_notNullColumnsMatchErd() throws SQLException {
        Map<String, Boolean> notNull = columnFlags("parcels", "notnull");
        assertEquals(true, notNull.get("tracking_no"));
        assertEquals(true, notNull.get("weight_kg"));
        assertEquals(true, notNull.get("service_level"));
        assertEquals(true, notNull.get("customer_id"));
    }

    @Test
    void deliveryAttemptsTable_notNullColumnsMatchErd() throws SQLException {
        Map<String, Boolean> notNull = columnFlags("delivery_attempts", "notnull");
        assertEquals(true, notNull.get("parcel_id"));
        assertEquals(true, notNull.get("attempted_at"));
        assertEquals(true, notNull.get("outcome"));
        assertEquals(false, notNull.get("driver_note"), "driver_note is optional in the ERD");
    }

    // --- Q5.3 foreign keys ---

    @Test
    void parcelsTable_customerIdReferencesCustomers() throws SQLException {
        assertEquals(List.of("customer_id->customers.id"), foreignKeys("parcels"));
    }

    @Test
    void deliveryAttemptsTable_parcelIdReferencesParcels() throws SQLException {
        assertEquals(List.of("parcel_id->parcels.id"), foreignKeys("delivery_attempts"));
    }

    // --- Q5.4 unique tracking number ---

    @Test
    void parcelsTable_trackingNoIsUnique() throws SQLException {
        assertTrue(uniqueColumns("parcels").contains("tracking_no"),
                "parcels.tracking_no should be declared UNIQUE");
    }

    private Map<String, String> columnTypes(String table) throws SQLException {
        Map<String, String> types = new HashMap<>();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("PRAGMA table_info(" + table + ")")) {
            while (resultSet.next()) {
                types.put(resultSet.getString("name"), resultSet.getString("type").toUpperCase());
            }
        }
        return types;
    }

    private Map<String, Boolean> columnFlags(String table, String flagColumn) throws SQLException {
        Map<String, Boolean> flags = new HashMap<>();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("PRAGMA table_info(" + table + ")")) {
            while (resultSet.next()) {
                flags.put(resultSet.getString("name"), resultSet.getInt(flagColumn) > 0);
            }
        }
        return flags;
    }

    private List<String> foreignKeys(String table) throws SQLException {
        List<String> keys = new ArrayList<>();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("PRAGMA foreign_key_list(" + table + ")")) {
            while (resultSet.next()) {
                keys.add(resultSet.getString("from") + "->" + resultSet.getString("table") + "." + resultSet.getString("to"));
            }
        }
        return keys;
    }

    private List<String> uniqueColumns(String table) throws SQLException {
        List<String> indexes = new ArrayList<>();
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("PRAGMA index_list(" + table + ")")) {
            while (resultSet.next()) {
                if (resultSet.getInt("unique") == 1 && !"pk".equals(resultSet.getString("origin"))) {
                    indexes.add(resultSet.getString("name"));
                }
            }
        }

        List<String> columns = new ArrayList<>();
        for (String index : indexes) {
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery("PRAGMA index_info('" + index + "')")) {
                while (resultSet.next()) {
                    columns.add(resultSet.getString("name"));
                }
            }
        }
        return columns;
    }
}
