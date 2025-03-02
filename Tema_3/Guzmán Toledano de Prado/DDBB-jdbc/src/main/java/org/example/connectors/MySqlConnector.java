package org.example.connectors;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


@Slf4j
@Getter
public class MySqlConnector {
    private final Connection connection;

    /**
     * @param database
     */

    public MySqlConnector(String database) {
        try {
            this.connection = DriverManager.getConnection(
                    "jdbc:mysql://" + System.getenv("MYSQL_HOST") + ":3306/" + database,
                    System.getenv("MYSQL_USER"),
                    System.getenv("MYSQL_PASSWORD")
            );
        } catch (SQLException e) {
            log.error("Failed to connect to MySql database", e);
            throw new RuntimeException(e);
        }
    }

}
