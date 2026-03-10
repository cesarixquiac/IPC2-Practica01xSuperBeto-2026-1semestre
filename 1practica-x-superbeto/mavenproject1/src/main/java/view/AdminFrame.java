/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;
import dao.EstadisticaDAO;
import dao.InventarioDAO;
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
public class AdminFrame extends JFrame {

    private Usuario admin;
    private int idSucursal;

    private JLabel lblEntregados;
    private JLabel lblCancelados;
    private JLabel lblVencidos;
    private JTable tablaRanking;
    private DefaultTableModel modeloRanking;

    private JTable tablaMenu;
    private DefaultTableModel modeloMenu;

    public AdminFrame(Usuario admin) {
        this.admin = admin;
        this.idSucursal = admin.getIdSucursal();

        setTitle("Pizza Express Tycoon - Panel de Administración");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel lblTitulo = new JLabel("Panel de Sucursal: " + admin.getNombre(), SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(lblTitulo, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("Ranking y Estadísticas", crearPanelEstadisticas());
        tabbedPane.addTab("Gestión de Menú", crearPanelMenu());

        add(tabbedPane, BorderLayout.CENTER);

        cargarDatosEstadisticas();
        cargarDatosMenu();
    }

    private JPanel crearPanelEstadisticas() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel panelContadores = new JPanel(new GridLayout(1, 3, 15, 0));

        lblEntregados = crearLabelEstadistica("Entregados: 0", new Color(200, 255, 200));
        lblCancelados = crearLabelEstadistica("Cancelados: 0", new Color(255, 200, 200));
        lblVencidos = crearLabelEstadistica("Vencidos: 0", new Color(255, 230, 150));

        panelContadores.add(lblEntregados);
        panelContadores.add(lblCancelados);
        panelContadores.add(lblVencidos);

        panel.add(panelContadores, BorderLayout.NORTH);

        String[] columnas = {"Posición", "Jugador", "Mejor Puntaje", "Nivel Alcanzado"};
        modeloRanking = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        
        tablaRanking = new JTable(modeloRanking);
        tablaRanking.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tablaRanking.setRowHeight(25);
        
        JScrollPane scrollTabla = new JScrollPane(tablaRanking);
        scrollTabla.setBorder(BorderFactory.createTitledBorder("Top 10 Jugadores"));
        panel.add(scrollTabla, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnExportar = new JButton("Exportar Ranking a CSV");
        btnExportar.setBackground(new Color(50, 150, 250));
        btnExportar.setForeground(Color.WHITE);
        btnExportar.addActionListener(e -> exportarACSV());
        
        panelBotones.add(btnExportar);
        panel.add(panelBotones, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearPanelMenu() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

  
        String[] columnas = {"ID", "Producto", "Descripción", "Precio (Q)", "Estado en Sucursal"};
        modeloMenu = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };

        tablaMenu = new JTable(modeloMenu);
        tablaMenu.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        tablaMenu.setRowHeight(25);

        JScrollPane scrollTabla = new JScrollPane(tablaMenu);
        scrollTabla.setBorder(BorderFactory.createTitledBorder("Catálogo de Productos"));
        panel.add(scrollTabla, BorderLayout.CENTER);

     
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        
        JButton btnCrear = new JButton("Nuevo Producto");
        JButton btnEditar = new JButton("Editar Producto");
        JButton btnActivar = new JButton("Activar en Sucursal");
        JButton btnDesactivar = new JButton("Desactivar en Sucursal");

        btnActivar.setBackground(new Color(200, 255, 200));
        btnDesactivar.setBackground(new Color(255, 200, 200));

   
        btnCrear.addActionListener(e -> mostrarDialogoCrear());
        btnEditar.addActionListener(e -> mostrarDialogoEditar());
        btnActivar.addActionListener(e -> cambiarEstadoSeleccionado(true));
        btnDesactivar.addActionListener(e -> cambiarEstadoSeleccionado(false));

        panelBotones.add(btnCrear);
        panelBotones.add(btnEditar);
        panelBotones.add(new JLabel(" | "));
        panelBotones.add(btnActivar);
        panelBotones.add(btnDesactivar);
        
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
        EstadisticaDAO dao = new EstadisticaDAO();

        Map<String, Integer> stats = dao.obtenerEstadisticasSucursal(idSucursal);
        lblEntregados.setText("Entregados: " + stats.getOrDefault("ENTREGADOS", 0));
        lblCancelados.setText("Cancelados: " + stats.getOrDefault("CANCELADOS", 0));
        lblVencidos.setText("Vencidos: " + stats.getOrDefault("VENCIDOS", 0));

        modeloRanking.setRowCount(0); 
        List<Object[]> ranking = dao.obtenerRankingPorSucursal(idSucursal);
        for (Object[] fila : ranking) {
            modeloRanking.addRow(fila);
        }
    }

    private void cargarDatosMenu() {
        InventarioDAO dao = new InventarioDAO();
        modeloMenu.setRowCount(0);
        
        List<Object[]> menu = dao.obtenerMenuSucursal(idSucursal);
        for (Object[] fila : menu) {
            modeloMenu.addRow(fila);
        }
    }

 
    private void mostrarDialogoCrear() {
        JTextField txtNombre = new JTextField(15);
        JTextField txtDesc = new JTextField(15);
        JTextField txtPrecio = new JTextField(15);

        Object[] formulario = {
            "Nombre del Producto:", txtNombre,
            "Descripción:", txtDesc,
            "Precio Base (Q):", txtPrecio
        };

        int opcion = JOptionPane.showConfirmDialog(this, formulario, "Crear Nuevo Producto", JOptionPane.OK_CANCEL_OPTION);
        
        if (opcion == JOptionPane.OK_OPTION) {
            try {
                String nombre = txtNombre.getText();
                String desc = txtDesc.getText();
                double precio = Double.parseDouble(txtPrecio.getText());

                InventarioDAO dao = new InventarioDAO();
                if(dao.crearProducto(nombre, desc, precio)) {
                    JOptionPane.showMessageDialog(this, "Producto creado exitosamente.");
                    cargarDatosMenu();
                } else {
                    JOptionPane.showMessageDialog(this, "Ocurrió un error al crear.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "El precio debe ser un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

 
    private void mostrarDialogoEditar() {
        int fila = tablaMenu.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto de la tabla para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

      
        int idProducto = (int) tablaMenu.getValueAt(fila, 0);
        String nombreActual = (String) tablaMenu.getValueAt(fila, 1);
        String descActual = (String) tablaMenu.getValueAt(fila, 2);
        double precioActual = Double.parseDouble(tablaMenu.getValueAt(fila, 3).toString());

        JTextField txtNombre = new JTextField(nombreActual, 15);
        JTextField txtDesc = new JTextField(descActual, 15);
        JTextField txtPrecio = new JTextField(String.valueOf(precioActual), 15);

        Object[] formulario = {
            "Nombre del Producto:", txtNombre,
            "Descripción:", txtDesc,
            "Precio Base (Q):", txtPrecio
        };

        int opcion = JOptionPane.showConfirmDialog(this, formulario, "Editar Producto (ID: " + idProducto + ")", JOptionPane.OK_CANCEL_OPTION);
        
        if (opcion == JOptionPane.OK_OPTION) {
            try {
                String nombre = txtNombre.getText();
                String desc = txtDesc.getText();
                double precio = Double.parseDouble(txtPrecio.getText());

                InventarioDAO dao = new InventarioDAO();
                if(dao.modificarProducto(idProducto, nombre, desc, precio)) {
                    JOptionPane.showMessageDialog(this, "Producto modificado exitosamente.");
                    cargarDatosMenu(); 
                } else {
                    JOptionPane.showMessageDialog(this, "Ocurrió un error al modificar.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "El precio debe ser un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cambiarEstadoSeleccionado(boolean activar) {
        int filaSeleccionada = tablaMenu.getSelectedRow();
        
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto de la tabla primero.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idProducto = (int) tablaMenu.getValueAt(filaSeleccionada, 0);
        String nombre = (String) tablaMenu.getValueAt(filaSeleccionada, 1);

        InventarioDAO dao = new InventarioDAO();
        dao.cambiarEstadoProducto(idSucursal, idProducto, activar);

        cargarDatosMenu();

        String accion = activar ? "ACTIVADO" : "DESACTIVADO";
        JOptionPane.showMessageDialog(this, "Producto '" + nombre + "' " + accion + " en tu sucursal.");
    }

    private void exportarACSV() {
        if (modeloRanking.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No hay datos para exportar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Ranking como CSV");
        fileChooser.setSelectedFile(new File("Ranking_Sucursal_" + idSucursal + ".csv"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File archivoGuardar = fileChooser.getSelectedFile();
            
            if (!archivoGuardar.getName().toLowerCase().endsWith(".csv")) {
                archivoGuardar = new File(archivoGuardar.getParentFile(), archivoGuardar.getName() + ".csv");
            }

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

                JOptionPane.showMessageDialog(this, "Archivo CSV exportado exitosamente en:\n" + archivoGuardar.getAbsolutePath());

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al guardar el archivo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
}
