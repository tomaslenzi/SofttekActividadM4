package org.tomas.dao;

import org.tomas.connection.DataBaseConnection;
import org.tomas.todolist.Task;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySqlTaskDao implements TaskDao {
    private final DataBaseConnection databaseConnection;

    public MySqlTaskDao() {
        this.databaseConnection = new DataBaseConnection();
    }

    /**
     * Obtiene todas las tareas cargadas en la base de datos.
     *
     * @return Una lista de tareas recuperadas de la base de datos.
     */

    @Override
    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        Connection con = this.databaseConnection.establishConnection();

        if (con != null) {
            try {
                Statement statement = con.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM task");

                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    String description = resultSet.getString("description");
                    boolean completed = resultSet.getBoolean("completed");
                    Task task = new Task(id, name, description, completed);
                    tasks.add(task);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return tasks;
    }

    /**
     * Agrega una nueva tarea a la base de datos.
     *
     * @param task La tarea que se va a agregar.
     */

    @Override
    public void addTask(Task task) {
        Connection con = null;
        PreparedStatement preparedStatement = null;

        try {
            con = this.databaseConnection.establishConnection();
            if (con != null) {
                String insertQuery = "INSERT INTO task (name, description, completed) VALUES (?, ?, 0)";
                preparedStatement = con.prepareStatement(insertQuery);
                preparedStatement.setString(1, task.getName());
                preparedStatement.setString(2, task.getDescription());
                preparedStatement.executeUpdate();
                System.out.println("Tarea insertada correctamente.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {

            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Actualiza los datos de una tarea en la base de datos.
     *
     * @param task La tarea con los datos actualizados.
     */
    @Override
    public void updateTask(Task task) {
        Connection con = null;
        PreparedStatement preparedStatement = null;

        try {
            con = this.databaseConnection.establishConnection();
            if (con != null) {
                String updateQuery = "UPDATE task SET name = ?, description = ? WHERE id = ?";
                preparedStatement = con.prepareStatement(updateQuery);
                preparedStatement.setString(1, task.getName());
                preparedStatement.setString(2, task.getDescription());
                preparedStatement.setInt(3, task.getId());
                preparedStatement.executeUpdate();

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // se utiliza finally para asegurarse de cerrar las conexiones y recursos en el bloque.
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Elimina una tarea de la base de datos.
     *
     * @param taskId El ID de la tarea que se va a eliminar.
     */

    @Override
    public void deleteTask(int taskId) {
        Connection con = null;
        PreparedStatement preparedStatement = null;

        try {
            con = this.databaseConnection.establishConnection();
            if (con != null) {
                String deleteQuery = "DELETE FROM task WHERE id = ?";
                preparedStatement = con.prepareStatement(deleteQuery);
                preparedStatement.setInt(1, taskId);
                preparedStatement.executeUpdate();

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void updateTaskCompletion(int taskId, boolean isCompleted) {
        Connection con = this.databaseConnection.establishConnection();

        if (con != null) {
            try {
                String query = "UPDATE task SET completed = ? WHERE id = ?";
                PreparedStatement preparedStatement = con.prepareStatement(query);
                preparedStatement.setBoolean(1, isCompleted);
                preparedStatement.setInt(2, taskId);
                preparedStatement.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (con != null) {
                    try {
                        con.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
