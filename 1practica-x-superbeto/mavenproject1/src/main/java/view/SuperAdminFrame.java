/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;
import dao.SuperAdminDAO;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Usuario;

/**
 *
 * @author cesar
 */
public class SuperAdminFrame extends JFrame {

    private Usuario superAdmin;

    private JLabel lblEntregados;
    private JLabel lblCancelados;
    private JLabel lblVencidos;
    private JTable tablaRanking;
    private DefaultTableModel modeloRanking;

    private JTable tablaSucursales;
    private DefaultTableModel modeloSucursales;

    private JTable tablaUsuarios;
    private DefaultTableModel modeloUsuarios;

    
    private JTable tablaParametros;
    private DefaultTableModel modeloParametros;

    public SuperAdminFrame(Usuario superAdmin) {
        this.superAdmin = superAdmin;

        setTitle("Pizza Express Tycoon - Panel de SUPER ADMINISTRADOR");
        setSize(950, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

       
        JPanel panelNorte = new JPanel(new BorderLayout());
        
        JLabel lblTitulo = new JLabel("Panel de Control Global - Bienvenido " + superAdmin.getNombre(), SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        
        JButton btnCerrarSesion = new JButton("Cerrar Sesión");
        btnCerrarSesion.setBackground(new Color(255, 100, 100)); 
        btnCerrarSesion.setForeground(Color.WHITE);
        btnCerrarSesion.addActionListener(e -> cerrarSesion());
        
        JPanel panelBotonSalir = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        panelBotonSalir.add(btnCerrarSesion);

        panelNorte.add(lblTitulo, BorderLayout.CENTER);
        panelNorte.add(panelBotonSalir, BorderLayout.EAST);
        
        add(panelNorte, BorderLayout.NORTH);
     

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Ranking y Estadísticas Globales", crearPanelEstadisticas());
        tabbedPane.addTab("Gestión de Sucursales", crearPanelSucursales());
        tabbedPane.addTab("Gestión de Usuarios", crearPanelUsuarios()); 
        tabbedPane.addTab("Parámetros del Juego", crearPanelParametros()); 

        add(tabbedPane, BorderLayout.CENTER);

        cargarDatosEstadisticas();
        cargarDatosSucursales();
        cargarDatosUsuarios();
        cargarDatosParametros(); 
    }

    private JPanel crearPanelEstadisticas() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelContadores = new JPanel(new GridLayout(1, 3, 15, 0));
        lblEntregados = crearLabelEstadistica("Entregados Totales: 0", new Color(200, 255, 200));
        lblCancelados = crearLabelEstadistica("Cancelados Totales: 0", new Color(255, 200, 200));
        lblVencidos = crearLabelEstadistica("Vencidos Totales: 0", new Color(255, 230, 150));
        panelContadores.add(lblEntregados);
        panelContadores.add(lblCancelados);
        panelContadores.add(lblVencidos);
        panel.add(panelContadores, BorderLayout.NORTH);

        String[] columnas = {"Posición", "Jugador", "Sucursal", "Mejor Puntaje", "Nivel Alcanzado"};
        modeloRanking = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaRanking = new JTable(modeloRanking);
        tablaRanking.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tablaRanking.setRowHeight(25);
        JScrollPane scrollTabla = new JScrollPane(tablaRanking);
        panel.add(scrollTabla, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnExportar = new JButton("Exportar Ranking Global a CSV");
        btnExportar.setBackground(new Color(50, 150, 250));
        btnExportar.setForeground(Color.WHITE);
        btnExportar.addActionListener(e -> exportarACSV());
        panelBotones.add(btnExportar);
        panel.add(panelBotones, BorderLayout.SOUTH);

        return panel;
    }

   private JPanel crearPanelSucursales() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columnas = {"ID Sucursal", "Nombre", "Dirección", "Estado"};
        modeloSucursales = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaSucursales = new JTable(modeloSucursales);
        tablaSucursales.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tablaSucursales.setRowHeight(25);
        JScrollPane scrollTabla = new JScrollPane(tablaSucursales);
        panel.add(scrollTabla, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton btnCrear = new JButton("Crear");
        JButton btnEditar = new JButton("Editar");
        JButton btnActivar = new JButton("Activar");
        JButton btnDesactivar = new JButton("Desactivar");

        btnCrear.setBackground(new Color(150, 255, 150));
        btnEditar.setBackground(new Color(255, 255, 150));
        btnActivar.setBackground(new Color(200, 255, 200));
        btnDesactivar.setBackground(new Color(255, 200, 200));

        btnCrear.addActionListener(e -> mostrarDialogoCrearSucursal());
        btnEditar.addActionListener(e -> mostrarDialogoEditarSucursal());
        btnActivar.addActionListener(e -> cambiarEstadoSucursalBD(true));
        btnDesactivar.addActionListener(e -> cambiarEstadoSucursalBD(false));

        panelBotones.add(btnCrear);
        panelBotones.add(btnEditar);
        panelBotones.add(new JLabel(" | "));
        panelBotones.add(btnActivar);
        panelBotones.add(btnDesactivar);
        
        panel.add(panelBotones, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearPanelUsuarios() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columnas = {"ID", "Username", "Nombre Completo", "Rol", "Sucursal Asignada", "Estado"};
        modeloUsuarios = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaUsuarios = new JTable(modeloUsuarios);
        tablaUsuarios.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tablaUsuarios.setRowHeight(25);
        JScrollPane scrollTabla = new JScrollPane(tablaUsuarios);
        panel.add(scrollTabla, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton btnCrear = new JButton("Registrar");
        JButton btnEditar = new JButton("Editar");
        JButton btnActivar = new JButton("Activar");
        JButton btnDesactivar = new JButton("Desactivar");

        btnCrear.setBackground(new Color(150, 255, 150));
        btnEditar.setBackground(new Color(255, 255, 150));
        btnActivar.setBackground(new Color(200, 255, 200));
        btnDesactivar.setBackground(new Color(255, 200, 200));

        btnCrear.addActionListener(e -> mostrarDialogoCrearUsuario());
        btnEditar.addActionListener(e -> mostrarDialogoEditarUsuario());
        btnActivar.addActionListener(e -> cambiarEstadoUsuarioBD(true));
        btnDesactivar.addActionListener(e -> cambiarEstadoUsuarioBD(false));

        panelBotones.add(btnCrear);
        panelBotones.add(btnEditar);
        panelBotones.add(new JLabel(" | "));
        panelBotones.add(btnActivar);
        panelBotones.add(btnDesactivar);
        
        panel.add(panelBotones, BorderLayout.SOUTH);

        return panel;
    }

    
    private JPanel crearPanelParametros() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columnas = {"ID", "Nivel", "Tiempo Base (s)", "Pedidos para Subir", "Puntos para Subir"};
        modeloParametros = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaParametros = new JTable(modeloParametros);
        tablaParametros.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tablaParametros.setRowHeight(25);
        
        JScrollPane scrollTabla = new JScrollPane(tablaParametros);
        scrollTabla.setBorder(BorderFactory.createTitledBorder("Configuración de Dificultad por Nivel"));
        panel.add(scrollTabla, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton btnEditarParametros = new JButton("Editar Parámetros del Nivel");
        btnEditarParametros.setBackground(new Color(255, 255, 150));
        btnEditarParametros.addActionListener(e -> mostrarDialogoEditarParametro());
        
        panelBotones.add(btnEditarParametros);
        panel.add(panelBotones, BorderLayout.SOUTH);

        return panel;
    }

    private JLabel crearLabelEstadistica(String texto, Color fondo) {
        JLabel label = new JLabel(texto, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setOpaque(true);
        label.setBackground(fondo);
        label.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        label.setPreferredSize(new Dimension(0, 50));
        return label;
    }

    private void cargarDatosEstadisticas() {
        SuperAdminDAO dao = new SuperAdminDAO();
        Map<String, Integer> stats = dao.obtenerEstadisticasGlobales();
        lblEntregados.setText("Entregados Totales: " + stats.getOrDefault("ENTREGADOS", 0));
        lblCancelados.setText("Cancelados Totales: " + stats.getOrDefault("CANCELADOS", 0));
        lblVencidos.setText("Vencidos Totales: " + stats.getOrDefault("VENCIDOS", 0));
        modeloRanking.setRowCount(0); 
        for (Object[] fila : dao.obtenerRankingGlobal()) modeloRanking.addRow(fila);
    }

    private void cargarDatosSucursales() {
        SuperAdminDAO dao = new SuperAdminDAO();
        modeloSucursales.setRowCount(0);
        for (Object[] fila : dao.obtenerTodasLasSucursales()) modeloSucursales.addRow(fila);
    }

    private void cargarDatosUsuarios() {
        SuperAdminDAO dao = new SuperAdminDAO();
        modeloUsuarios.setRowCount(0);
        for (Object[] fila : dao.obtenerTodosLosUsuarios()) modeloUsuarios.addRow(fila);
    }

   
    private void cargarDatosParametros() {
        SuperAdminDAO dao = new SuperAdminDAO();
        modeloParametros.setRowCount(0);
        for (Object[] fila : dao.obtenerParametrosGlobales()) {
            modeloParametros.addRow(fila);
        }
    }

    private void mostrarDialogoCrearSucursal() {
        JTextField txtNombre = new JTextField(15);
        JTextField txtDireccion = new JTextField(15);
        Object[] formulario = {
            "Nombre de la Sucursal:", txtNombre,
            "Dirección:", txtDireccion
        };
        int opcion = JOptionPane.showConfirmDialog(this, formulario, "Crear Nueva Sucursal", JOptionPane.OK_CANCEL_OPTION);
        if (opcion == JOptionPane.OK_OPTION) {
            String nombre = txtNombre.getText().trim();
            String direccion = txtDireccion.getText().trim();
            if (nombre.isEmpty() || direccion.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe llenar todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            SuperAdminDAO dao = new SuperAdminDAO();
            if(dao.crearSucursal(nombre, direccion)) {
                JOptionPane.showMessageDialog(this, "Sucursal creada exitosamente.");
                cargarDatosSucursales(); 
            } else {
                JOptionPane.showMessageDialog(this, "Ocurrió un error al crear la sucursal.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void mostrarDialogoCrearUsuario() {
        SuperAdminDAO dao = new SuperAdminDAO();
        
        Map<String, Integer> mapaRoles = dao.obtenerMapaRoles();
        Map<String, Integer> mapaSucursales = dao.obtenerMapaSucursales();

        JComboBox<String> comboRoles = new JComboBox<>(mapaRoles.keySet().toArray(new String[0]));
        JComboBox<String> comboSucursales = new JComboBox<>(mapaSucursales.keySet().toArray(new String[0]));

        JTextField txtUsername = new JTextField(15);
        JPasswordField txtPassword = new JPasswordField(15);
        JTextField txtNombre = new JTextField(15);

        Object[] formulario = {
            "Nombre Completo:", txtNombre,
            "Username (Login):", txtUsername,
            "Contraseña:", txtPassword,
            "Rol del Sistema:", comboRoles,
            "Asignar a Sucursal:", comboSucursales
        };

        int opcion = JOptionPane.showConfirmDialog(this, formulario, "Registrar y Asignar Usuario", JOptionPane.OK_CANCEL_OPTION);
        
        if (opcion == JOptionPane.OK_OPTION) {
            String username = txtUsername.getText().trim();
            String password = new String(txtPassword.getPassword()).trim();
            String nombre = txtNombre.getText().trim();
            
            if (username.isEmpty() || password.isEmpty() || nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Todos los campos de texto son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String rolSeleccionado = (String) comboRoles.getSelectedItem();
            int idRol = mapaRoles.get(rolSeleccionado);
            
            String sucursalSeleccionada = (String) comboSucursales.getSelectedItem();
            int idSucursal = mapaSucursales.get(sucursalSeleccionada);

            if(dao.crearUsuario(username, password, nombre, idRol, idSucursal)) {
                JOptionPane.showMessageDialog(this, "Usuario creado y asignado exitosamente a " + sucursalSeleccionada + ".");
                cargarDatosUsuarios(); 
            } else {
                JOptionPane.showMessageDialog(this, "Ocurrió un error (¿Username duplicado?).", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportarACSV() {
        if (modeloRanking.getRowCount() == 0) return;
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Ranking Global como CSV");
        fileChooser.setSelectedFile(new File("Ranking_Global_Tycoon.csv"));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File archivoGuardar = fileChooser.getSelectedFile();
            if (!archivoGuardar.getName().toLowerCase().endsWith(".csv")) 
                archivoGuardar = new File(archivoGuardar.getParentFile(), archivoGuardar.getName() + ".csv");
            try (PrintWriter pw = new PrintWriter(new FileWriter(archivoGuardar))) {
                for (int i = 0; i < modeloRanking.getColumnCount(); i++) {
                    pw.print(modeloRanking.getColumnName(i));
                    if (i < modeloRanking.getColumnCount() - 1) pw.print(",");
                }
                pw.println();
                for (int i = 0; i < modeloRanking.getRowCount(); i++) {
                    for (int j = 0; j < modeloRanking.getColumnCount(); j++) {
                        pw.print(modeloRanking.getValueAt(i, j));
                        if (j < modeloRanking.getColumnCount() - 1) pw.print(",");
                    }
                    pw.println();
                }
                JOptionPane.showMessageDialog(this, "Archivo exportado en:\n" + archivoGuardar.getAbsolutePath());
            } catch (Exception ex) { ex.printStackTrace(); }
        }
    }
    
    private void cambiarEstadoSucursalBD(boolean activar) {
        int fila = tablaSucursales.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una sucursal de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int idSucursal = (int) tablaSucursales.getValueAt(fila, 0);
        String nombre = (String) tablaSucursales.getValueAt(fila, 1);

        SuperAdminDAO dao = new SuperAdminDAO();
        if(dao.cambiarEstadoSucursal(idSucursal, activar)) {
            cargarDatosSucursales();
            String accion = activar ? "ACTIVADA" : "DESACTIVADA";
            JOptionPane.showMessageDialog(this, "Sucursal '" + nombre + "' " + accion + ".");
        }
    }

    private void mostrarDialogoEditarUsuario() {
        int fila = tablaUsuarios.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idUsuario = (int) tablaUsuarios.getValueAt(fila, 0);
        String nombreActual = (String) tablaUsuarios.getValueAt(fila, 2);

        SuperAdminDAO dao = new SuperAdminDAO();
        Map<String, Integer> mapaRoles = dao.obtenerMapaRoles();
        Map<String, Integer> mapaSucursales = dao.obtenerMapaSucursales();

        JComboBox<String> comboRoles = new JComboBox<>(mapaRoles.keySet().toArray(new String[0]));
        JComboBox<String> comboSucursales = new JComboBox<>(mapaSucursales.keySet().toArray(new String[0]));
        JTextField txtNombre = new JTextField(nombreActual, 15);

        Object[] formulario = {
            "Nuevo Nombre Completo:", txtNombre,
            "Nuevo Rol:", comboRoles,
            "Nueva Sucursal:", comboSucursales
        };

        int opcion = JOptionPane.showConfirmDialog(this, formulario, "Editar Usuario (ID: " + idUsuario + ")", JOptionPane.OK_CANCEL_OPTION);
        
        if (opcion == JOptionPane.OK_OPTION) {
            String nombre = txtNombre.getText().trim();
            int idRol = mapaRoles.get((String) comboRoles.getSelectedItem());
            int idSucursal = mapaSucursales.get((String) comboSucursales.getSelectedItem());

            if(dao.modificarUsuario(idUsuario, nombre, idRol, idSucursal)) {
                JOptionPane.showMessageDialog(this, "Usuario editado correctamente.");
                cargarDatosUsuarios();
            } else {
                JOptionPane.showMessageDialog(this, "Error al editar usuario.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cambiarEstadoUsuarioBD(boolean activar) {
        int fila = tablaUsuarios.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int idUsuario = (int) tablaUsuarios.getValueAt(fila, 0);
        String username = (String) tablaUsuarios.getValueAt(fila, 1);

        if (idUsuario == this.superAdmin.getIdUsuario() && !activar) {
            JOptionPane.showMessageDialog(this, "¡No puedes desactivar tu propia cuenta de Super Administrador!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        SuperAdminDAO dao = new SuperAdminDAO();
        if(dao.cambiarEstadoUsuario(idUsuario, activar)) {
            cargarDatosUsuarios();
            String accion = activar ? "ACTIVADO" : "DESACTIVADO";
            JOptionPane.showMessageDialog(this, "Usuario '" + username + "' " + accion + ".");
        }
    }
    
    private void mostrarDialogoEditarSucursal() {
        int fila = tablaSucursales.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una sucursal de la tabla para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idSucursal = (int) tablaSucursales.getValueAt(fila, 0);
        String nombreActual = (String) tablaSucursales.getValueAt(fila, 1);
        String dirActual = (String) tablaSucursales.getValueAt(fila, 2);

        JTextField txtNombre = new JTextField(nombreActual, 15);
        JTextField txtDireccion = new JTextField(dirActual, 15);

        Object[] formulario = {
            "Nuevo Nombre de la Sucursal:", txtNombre,
            "Nueva Dirección:", txtDireccion
        };

        int opcion = JOptionPane.showConfirmDialog(this, formulario, "Editar Sucursal (ID: " + idSucursal + ")", JOptionPane.OK_CANCEL_OPTION);
        
        if (opcion == JOptionPane.OK_OPTION) {
            String nombre = txtNombre.getText().trim();
            String direccion = txtDireccion.getText().trim();

            if (nombre.isEmpty() || direccion.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe llenar todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            SuperAdminDAO dao = new SuperAdminDAO();
            if(dao.modificarSucursal(idSucursal, nombre, direccion)) {
                JOptionPane.showMessageDialog(this, "Sucursal modificada exitosamente.");
                cargarDatosSucursales(); 
            } else {
                JOptionPane.showMessageDialog(this, "Ocurrió un error al modificar la sucursal.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

  
    private void mostrarDialogoEditarParametro() {
        int fila = tablaParametros.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un nivel de la tabla para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idNivel = (int) tablaParametros.getValueAt(fila, 0);
        int numeroNivel = (int) tablaParametros.getValueAt(fila, 1);
        int tiempoActual = (int) tablaParametros.getValueAt(fila, 2);
        int pedidosActual = (int) tablaParametros.getValueAt(fila, 3);
        int puntosActual = (int) tablaParametros.getValueAt(fila, 4);

        JTextField txtTiempo = new JTextField(String.valueOf(tiempoActual), 10);
        JTextField txtPedidos = new JTextField(String.valueOf(pedidosActual), 10);
        JTextField txtPuntos = new JTextField(String.valueOf(puntosActual), 10);

        Object[] formulario = {
            "Editando Configuración del Nivel " + numeroNivel,
            " ", 
            "Tiempo Base (segundos):", txtTiempo,
            "Pedidos requeridos para subir:", txtPedidos,
            "Puntos otorgados al subir:", txtPuntos
        };

        int opcion = JOptionPane.showConfirmDialog(this, formulario, "Editar Dificultad - Nivel " + numeroNivel, JOptionPane.OK_CANCEL_OPTION);

        if (opcion == JOptionPane.OK_OPTION) {
            try {
                int nuevoTiempo = Integer.parseInt(txtTiempo.getText().trim());
                int nuevosPedidos = Integer.parseInt(txtPedidos.getText().trim());
                int nuevosPuntos = Integer.parseInt(txtPuntos.getText().trim());

                if (nuevoTiempo <= 0 || nuevosPedidos <= 0 || nuevosPuntos <= 0) {
                    JOptionPane.showMessageDialog(this, "Todos los valores deben ser mayores a 0.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                SuperAdminDAO dao = new SuperAdminDAO();
                if(dao.modificarNivel(idNivel, nuevoTiempo, nuevosPedidos, nuevosPuntos)) {
                    JOptionPane.showMessageDialog(this, "Parámetros del Nivel " + numeroNivel + " actualizados correctamente.");
                    cargarDatosParametros(); 
                } else {
                    JOptionPane.showMessageDialog(this, "Error al actualizar en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Por favor, ingrese solo números enteros válidos.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    
    private void cerrarSesion() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Estás seguro que deseas cerrar sesión?", 
            "Confirmar", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            
            LoginFrame login = new LoginFrame();
            login.setVisible(true);
            this.dispose();
        }
    }
}

