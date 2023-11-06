package org.tomas.connection;

import javax.swing.*;
import java.sql.*;


/**
 * Clase que gestiona la conexion con la base de datos.
 */
public class DataBaseConnection {

    private final String user = "root";
    private final String password = "123456789";
    private final String db = "todolistdb";
    private final String ip = "localhost";
    private final String port = "3306";
    private final String string = "jdbc:mysql://" + ip + ":" + port + "/" + db;
    // Atributos de conexión
    private Connection connection = null;


    /**
     * establece la conexion a la base de datos
     */
    public Connection establishConnection() {
        try {
            connection = DriverManager.getConnection(string, user, password);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "No se pudo conectar" + e.toString());
        }
        return connection;
    }

}
