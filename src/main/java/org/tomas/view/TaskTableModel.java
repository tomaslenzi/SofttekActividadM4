package org.tomas.view;

import org.tomas.dao.MySqlTaskDao;
import org.tomas.todolist.Task;

import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * Clase que implementa un modelo de tabla personalizado para mostrar y gestionar las tareas.
 */
public class TaskTableModel extends AbstractTableModel {

    private final String[] COLUMNS = {"Nombre", "Descripcion", "Completada"};
    private List<Task> tasks;

    private MySqlTaskDao mySqlTaskDao;

    public TaskTableModel(List<Task> tasks, MySqlTaskDao mySqlTaskDao) {
        this.tasks = tasks;
        this.mySqlTaskDao = mySqlTaskDao;
    }

    @Override
    public int getRowCount() {
        return tasks.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNS.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return switch (columnIndex) {
            // case 0 -> tasks.get(rowIndex).getId();
            case 0 -> tasks.get(rowIndex).getName();
            case 1 -> tasks.get(rowIndex).getDescription();
            case 2 -> tasks.get(rowIndex).isCompleted();
            default -> "-";
        };
    }

    @Override
    public String getColumnName(int column) {
        return COLUMNS[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 2) { // Para la columna "Completada"
            return Boolean.class; // Utiliza Boolean para las casillas de verificación
        } else {
            return super.getColumnClass(columnIndex);
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == 2; // Hace que la columna "Completada" sea editable
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        if (columnIndex == 2) { // Si se edita la columna "Completada"
            boolean isCompleted = (boolean) value; // Obtiene el valor de la casilla de verificación

            // Obtiene la tarea correspondiente
            Task task = tasks.get(rowIndex);

            // Establece el estado de la tarea como completada o no completada segun el valor de la casilla de verificación
            task.setCompleted(isCompleted);

            // Actualiza la base de datos con el nuevo estado completado
            int taskId = task.getId();
            mySqlTaskDao.updateTaskCompletion(taskId, isCompleted);

            // notifica a la tabla que los datos cambiaron
            fireTableCellUpdated(rowIndex, columnIndex);
        }
    }
}
