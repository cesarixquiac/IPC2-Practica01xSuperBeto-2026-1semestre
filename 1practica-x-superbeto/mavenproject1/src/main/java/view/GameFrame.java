/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

/**
 *
 * @author cesar
 */
import javax.swing.*;
import java.awt.*;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import model.EstadoPedido;

public class GameFrame extends JFrame {

    private JLabel labelJugador;
    private JLabel labelTiempo;
    private JLabel labelPuntaje;

    private JPanel panelPedidos;

    private int puntaje = 0;
    private int entregados = 0;
    private int cancelados = 0;
    private int noEntregados = 0;

    private Map<JPanel, Timer> timersPedidos = new HashMap<>();

    public GameFrame(String jugador) {
        setTitle("Pizza Express Tycoon");
        setSize(800, 600); // Un poco más grande para comodidad
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout());

        // PANEL SUPERIOR
        JPanel panelInfo = new JPanel(new GridLayout(1, 3));
        panelInfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        labelJugador = new JLabel("Jugador: " + jugador);
        labelTiempo = new JLabel("Tiempo restante: 180", SwingConstants.CENTER);
        labelPuntaje = new JLabel("Puntaje: 0", SwingConstants.RIGHT);

        panelInfo.add(labelJugador);
        panelInfo.add(labelTiempo);
        panelInfo.add(labelPuntaje);

        add(panelInfo, BorderLayout.NORTH);

        // PANEL PEDIDOS
        panelPedidos = new JPanel();
        panelPedidos.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 15));

        JScrollPane scroll = new JScrollPane(panelPedidos);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(scroll, BorderLayout.CENTER);
    }

    // CREAR PEDIDO
    public void agregarPedido(String pedidoTexto) {

        if (panelPedidos.getComponentCount() >= 5) {
            System.out.println("Máximo de pedidos activos alcanzado");
            return;
        }

        JPanel tarjeta = new JPanel();
       
        tarjeta.setPreferredSize(new Dimension(220, 160)); 
        tarjeta.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        tarjeta.setLayout(new GridLayout(4, 1, 5, 5)); 

        JLabel labelEstado = new JLabel("Estado: RECIBIDA", SwingConstants.CENTER);
        labelEstado.setFont(new Font("Arial", Font.BOLD, 12));

        JLabel labelPedido = new JLabel("<html><center>" + pedidoTexto + "</center></html>", SwingConstants.CENTER);

        JLabel labelTiempoPedido = new JLabel("Tiempo: 60", SwingConstants.CENTER);

        // BOTONES
        JButton btnAvanzar = new JButton("Avanzar");
        JButton btnCancelar = new JButton("Cancelar");
        
        
        btnAvanzar.setFont(new Font("Arial", Font.PLAIN, 11));
        btnCancelar.setFont(new Font("Arial", Font.PLAIN, 11));

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 2));
        panelBotones.add(btnAvanzar);
        panelBotones.add(btnCancelar);

        tarjeta.add(labelEstado);
        tarjeta.add(labelPedido);
        tarjeta.add(labelTiempoPedido);
        tarjeta.add(panelBotones);

        panelPedidos.add(tarjeta);
        panelPedidos.revalidate();
        panelPedidos.repaint();

        // ESTADO DEL PEDIDO
        final EstadoPedido[] estado = {EstadoPedido.RECIBIDA};

        // TIMER DEL PEDIDO
        Timer timer = new Timer(1000, null);
        timer.addActionListener(e -> {
            int tiempo = Integer.parseInt(labelTiempoPedido.getText().replace("Tiempo: ", ""));
            tiempo--;
            labelTiempoPedido.setText("Tiempo: " + tiempo);

            if (tiempo <= 0) {
                timer.stop();
                panelPedidos.remove(tarjeta);
                panelPedidos.revalidate();
                panelPedidos.repaint();
                actualizarPuntaje(-50);
                noEntregados++;
                JOptionPane.showMessageDialog(this, "Pedido vencido (NO_ENTREGADO)");
            }
        });

        timer.start();
        timersPedidos.put(tarjeta, timer);

        // ACCIÓN BOTÓN AVANZAR
        btnAvanzar.addActionListener(e -> {
            switch (estado[0]) {
                case RECIBIDA:
                    estado[0] = EstadoPedido.PREPARANDO;
                    labelEstado.setText("Estado: PREPARANDO");
                    tarjeta.setBackground(new Color(200, 255, 255)); // Cyan claro
                    break;
                case PREPARANDO:
                    estado[0] = EstadoPedido.EN_HORNO;
                    labelEstado.setText("Estado: EN_HORNO");
                    tarjeta.setBackground(new Color(255, 220, 150)); // Naranja claro
                    break;
                case EN_HORNO:
                    estado[0] = EstadoPedido.ENTREGADO;
                    labelEstado.setText("Estado: ENTREGADO");
                    tarjeta.setBackground(Color.green);
                    timer.stop();
                    panelPedidos.remove(tarjeta);
                    panelPedidos.revalidate();
                    panelPedidos.repaint();
                    actualizarPuntaje(100);
                    entregados++;
                    JOptionPane.showMessageDialog(this, "Pedido ENTREGADO (+100)");
                    break;
                default:
                    break;
            }
        });

        // ACCIÓN BOTÓN CANCELAR
        btnCancelar.addActionListener(e -> {
            timer.stop();
            panelPedidos.remove(tarjeta);
            panelPedidos.revalidate();
            panelPedidos.repaint();
            actualizarPuntaje(-30);
            cancelados++;
            JOptionPane.showMessageDialog(this, "Pedido CANCELADO (-30)");
        });
    }

    public int getCantidadPedidos(){
        return panelPedidos.getComponentCount();
    }

    public void actualizarTiempo(int tiempo){
        labelTiempo.setText("Tiempo restante: " + tiempo);
    }

    public void actualizarPuntaje(int cambio){
        puntaje += cambio;
        labelPuntaje.setText("Puntaje: " + puntaje);
    }

    public void limpiarPedidos(){
        panelPedidos.removeAll();
        panelPedidos.revalidate();
        panelPedidos.repaint();
    }

    public void mostrarResumenFinal(){
        JOptionPane.showMessageDialog(this,
                "PARTIDA FINALIZADA\n\n"
                + "Pedidos entregados: " + entregados + "\n"
                + "Pedidos cancelados: " + cancelados + "\n"
                + "Pedidos vencidos: " + noEntregados + "\n\n"
                + "Puntaje final: " + puntaje);
    }
}