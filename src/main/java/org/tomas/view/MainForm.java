package org.tomas.view;

import org.tomas.dao.MySqlTaskDao;
import org.tomas.todolist.Task;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class MainForm {
    private JPanel rootPanel;
    private JButton addTaskBtn;
    private JButton deleteTaskBtn;
    private JButton editTaskBtn;
    private JTable table1;
    private JButton showCompletedTasksBtn;
    private JButton showIncompletedTasksBtn;


    private List<Task> tasks;
    private TaskTableModel taskTableModel;
    private MySqlTaskDao mySqlTaskDao;

    public MainForm() {

        // Inicialización
        tasks = new ArrayList<>();
        mySqlTaskDao = new MySqlTaskDao();
        taskTableModel = new TaskTableModel(tasks, mySqlTaskDao);
        table1.setModel(taskTableModel);

        loadAllTasks(); // Llama al método para cargar todas las tareas

        // Configuración de botones
        addTaskBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addNewTask();
            }
        });

        deleteTaskBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteSelectedTask();
            }
        });

        editTaskBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editSelectedTask();
            }
        });
        showCompletedTasksBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Acción para mostrar tareas completadas en una ventana
                List<Task> completedTasks = getCompletedTasks();
                displayTasksInDialog(completedTasks, "Tareas Completadas");

            }
        });
        showIncompletedTasksBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Acción para mostrar tareas incompletas en una ventana
                List<Task> incompleteTasks = getIncompleteTasks();
                displayTasksInDialog(incompleteTasks, "Tareas Incompletas");

            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("MainForm");
        frame.setContentPane(new MainForm().rootPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);//para que inicie en el centro
    }

    /**
     * Método para obtener tareas completadas
     */
    private List<Task> getCompletedTasks() {
        List<Task> completedTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task.isCompleted()) {
                completedTasks.add(task);
            }
        }
        return completedTasks;
    }

    /**
     * Método para obtener tareas incompletas
     */
    private List<Task> getIncompleteTasks() {
        List<Task> incompleteTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (!task.isCompleted()) {
                incompleteTasks.add(task);
            }
        }
        return incompleteTasks;
    }

    /**
     * * Método para mostrar tareas completas o incompletas en una ventana
     */
    private void displayTasksInDialog(List<Task> tasksToShow, String title) {
        if (tasksToShow.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay tareas para mostrar.", title, JOptionPane.INFORMATION_MESSAGE);
        } else {
            StringBuilder message = new StringBuilder();
            for (Task task : tasksToShow) {
                message.append("Nombre: ").append(task.getName()).append("\n");
                message.append("Descripción: ").append(task.getDescription()).append("\n\n");
            }
            JOptionPane.showMessageDialog(null, message.toString(), title, JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Metodo para cargar todas las tareas
     */
    private void loadAllTasks() {
        // Llama al método de MySqlTaskDao para cargar todas las tareas
        List<Task> loadedTasks = mySqlTaskDao.getAllTasks();

        // Actualiza la lista de tareas y la tabla
        tasks.clear();
        tasks.addAll(loadedTasks);
        taskTableModel.fireTableDataChanged();
    }

    /**
     * metodo para cargar una tarea
     */
    private void addNewTask() {
        do {
            // Utiliza el método createTaskInputPanel para obtener los datos de la nueva tarea
            Task newTask = createTaskInputPanel("Agregar Tarea", "", "");

            if (newTask != null) { // Verifica si se creo una nueva tarea
                try {

                    // Agregar la tarea a la lista
                    tasks.add(newTask);

                    // Agregar la tarea a la base de datos utilizando MySqlTaskDao
                    mySqlTaskDao.addTask(newTask);

                    //recarga las tareas desde la bd para que se sincronice al instante y no tenga que cerrar y volver a ejecutar el mainform si se desea realizar otra accion
                    //que no sea agregar otra tarea, antes hacia fireTableDataChanged() y tenia ese problema
                    loadAllTasks();

                    // Mostrar una ventana de diálogo para preguntar si desea agregar otra tarea
                    int repeatResult = JOptionPane.showConfirmDialog(null, "¿Desea agregar otra tarea?", "Tarea agregada con éxito!", JOptionPane.YES_NO_OPTION);

                    if (repeatResult == JOptionPane.NO_OPTION) {
                        // Si el usuario hace clic en "No", se detiene el bucle
                        break;
                    }
                } catch (IllegalArgumentException e) {
                    JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Error de entrada", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // Si el usuario hizo clic en "Cancelar" o cerró el diálogo, se detiene el bucle
                break;
            }
        } while (true);
    }

    /**
     * metodo para eliminar una tarea seleccionada
     */
    private void deleteSelectedTask() {
        // Lógica para eliminar una tarea
        int selectedRow = table1.getSelectedRow();
        if (tasks.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay ninguna tarea cargada para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
        } else if (selectedRow != -1) {
            Task selectedTask = tasks.get(selectedRow); // Obtiene la tarea seleccionada

            // Llama al método de eliminación en la instancia de MySqlTaskDao usando el ID de la tarea seleccionada
            int taskIdToDelete = selectedTask.getId();
            mySqlTaskDao.deleteTask(taskIdToDelete);

            tasks.remove(selectedRow);
            // Actualiza la tabla
            taskTableModel.fireTableDataChanged();

            // Mostrar un mensaje de éxito
            JOptionPane.showMessageDialog(null, "Tarea eliminada con éxito.", "Tarea eliminada", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Por favor, selecciona una tarea para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * metodo para editar una tarea seleccionada
     */
    private void editSelectedTask() {
        int selectedRow = table1.getSelectedRow();

        if (tasks.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay ninguna tarea cargada para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
        } else if (selectedRow != -1) {
            Task taskToEdit = tasks.get(selectedRow);

            // Utiliza el método createTaskInputPanel para obtener los datos editados
            Task editedTask = createTaskInputPanel("Editar Tarea", taskToEdit.getName(), taskToEdit.getDescription());

            if (editedTask != null) { // Verifica si se editó la tarea
                try {
                    // Actualiza la tarea con los nuevos valores
                    taskToEdit.setName(editedTask.getName());
                    taskToEdit.setDescription(editedTask.getDescription());

                    // Actualiza la tarea en la base de datos
                    mySqlTaskDao.updateTask(taskToEdit);

                    // Actualiza la tabla
                    taskTableModel.fireTableDataChanged();
                    // Muestra un mensaje de éxito
                    JOptionPane.showMessageDialog(null, "Tarea editada con éxito.", "Tarea editada", JOptionPane.INFORMATION_MESSAGE);
                } catch (IllegalArgumentException e) {
                    JOptionPane.showMessageDialog(null, "Error: " + e.getMessage(), "Error de entrada", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Por favor, selecciona una tarea para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Metodo para crear un panel personalizado con campos de entrada
     */
    private Task createTaskInputPanel(String title, String defaultName, String defaultDescription) {
        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        JTextField nombreField = new JTextField(defaultName, 20);
        JTextArea descripcionArea = new JTextArea(defaultDescription, 5, 20);

        inputPanel.add(new JLabel("Nombre de la Tarea:"));
        inputPanel.add(nombreField);
        inputPanel.add(new JLabel("Descripción:"));
        inputPanel.add(new JScrollPane(descripcionArea));

        int result = JOptionPane.showConfirmDialog(null, inputPanel, title, JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            // Leer los valores ingresados
            String nombre = nombreField.getText();
            String descripcion = descripcionArea.getText();

            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(null, "El nombre de la tarea no puede estar vacío.", "Error", JOptionPane.ERROR_MESSAGE);
                return null; // Devuelve null para indicar que no se debe crear o editar una tarea
            }

            return new Task(nombre, descripcion); // Devuelve los datos ingresados
        } else {
            return null; // Devuelve null para indicar que no se debe crear o editar una tarea
        }
    }
}
