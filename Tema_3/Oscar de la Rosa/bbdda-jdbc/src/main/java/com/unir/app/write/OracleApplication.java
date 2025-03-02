package com.unir.app.write;

import com.unir.config.OracleDatabaseConnector;
import com.unir.model.OracleCountry;
import com.unir.model.OracleDepartments;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * La version para MySQL seria muy similar a esta, cambiando únicamente el Driver.
 */
@Slf4j
public class OracleApplication {

    private static final String SERIVCE_NAME = "orcl";

    public static void main(String[] args) {

        //Creamos conexion. No es necesario indicar puerto en host si usamos el default, 1521
        //Try-with-resources. Se cierra la conexión automáticamente al salir del bloque try
        try(Connection connection = new OracleDatabaseConnector("localhost", SERIVCE_NAME).getConnection()) {

            log.info("Conexión establecida con la base de datos Oracle");
            OracleCountry spain = new OracleCountry("ES", 1, "Spain");
            upsert(connection, spain);
            OracleDepartments quality = new OracleDepartments(280, "Quality Assurance", 205, 1700);
            upsert_departments(connection, quality);
        } catch (Exception e) {
            log.error("Error al tratar con la base de datos", e);
        }
    }


    /**
     * Función que nos permite insertar o actualizar un país en la base de datos.
     * Si el país ya existe, se actualiza. Si no existe, se inserta.
     * OJO! No es lo mismo que un MERGE aunque funcionalmente sea parecido.
     * Mas info (1): https://www.sqlservercentral.com/articles/performance-of-the-sql-merge-vs-insertupdate
     * Mas info (2): https://www.mssqltips.com/sqlservertip/7590/sql-merge-performance-vs-insert-update-delete/
     * Mas info (3): https://www.linkedin.com/pulse/sql-load-performance-optimization-part-1-replace-merge-kragh/
     *
     * @param connection - Conexión a la base de datos.
     * @param country - País a insertar o actualizar.
     * @throws SQLException - Si ocurre algún error al ejecutar la consulta.
     */
    public static void upsert(Connection connection, OracleCountry country) throws SQLException {

        String selectSql = "SELECT COUNT(*) FROM countries WHERE country_id = ?";
        String updateSql = "UPDATE countries SET country_name = ?, region_id = ? WHERE country_id = ?";
        String insertSql = "INSERT INTO countries (country_id, country_name, region_id) VALUES (?, ?, ?)";

        PreparedStatement selectStatement = connection.prepareStatement(selectSql);
        selectStatement.setString(1, country.getCountryId()); // Código del país
        ResultSet resultSet = selectStatement.executeQuery();
        resultSet.next(); // Nos movemos a la primera fila
        int rowCount = resultSet.getInt(1);

        if(rowCount > 0) {
            PreparedStatement updateStatement = connection.prepareStatement(updateSql);
            updateStatement.setString(1, country.getCountryName()); // Nuevo nombre del país
            updateStatement.setInt(2, country.getRegionId()); // Nuevo codigo de región
            updateStatement.setString(3, country.getCountryId()); // Código del país
            int filasActualizadas = updateStatement.executeUpdate();
            log.debug("Filas Actualizadas Country: {}", filasActualizadas);

        } else {
            PreparedStatement insertStatement = connection.prepareStatement(insertSql);
            insertStatement.setString(1, country.getCountryId()); // Nuevo código del país.
            insertStatement.setString(2, country.getCountryName()); // Nuevo nombre del país
            insertStatement.setInt(3, country.getRegionId()); // Código de región
            int filasInsertadas = insertStatement.executeUpdate();
            log.debug("Filas Insertadas Country: {}", filasInsertadas);
        }
    }
    public static void upsert_departments(Connection connection, OracleDepartments department) throws SQLException {

        String selectSql = "SELECT COUNT(*) FROM departments WHERE department_id = ?";
        String updateSql = "UPDATE departments SET department_name = ?, manager_id = ?, location_id = ? WHERE department_id = ?";
        String insertSql = "INSERT INTO departments (department_id, department_name, manager_id , location_id) VALUES (?, ?, ?, ?)";
        PreparedStatement selectStatement = connection.prepareStatement(selectSql);
        selectStatement.setInt(1, department.getDepartment_Id()); // Código del departamento
        ResultSet resultSet = selectStatement.executeQuery();
        resultSet.next(); // Nos movemos a la primera fila
        int rowCount = resultSet.getInt(1);

        if(rowCount > 0) {
            PreparedStatement updateStatement = connection.prepareStatement(updateSql);
            updateStatement.setString(1, department.getDepartmentName()); // Nuevo codigo de región
            updateStatement.setInt(2, department.getManager_Id()); // Código del país
            updateStatement.setInt(3, department.getLocation_Id());
            updateStatement.setInt(4, department.getDepartment_Id()); // Nuevo nombre del país
            int filasActualizadas = updateStatement.executeUpdate();
            log.debug("Filas Actualizadas Departments: {}", filasActualizadas);

        } else {
            PreparedStatement insertStatement = connection.prepareStatement(insertSql);
            insertStatement.setInt(1, department.getDepartment_Id()); // Nuevo código del país.
            insertStatement.setString(2, department.getDepartmentName()); // Nuevo nombre del país
            insertStatement.setInt(3, department.getManager_Id()); // Código de región
            insertStatement.setInt(4, department.getLocation_Id());
            int filasInsertadas = insertStatement.executeUpdate();
            log.debug("Filas Insertadas Departments: {}", filasInsertadas);
        }
    }


}
