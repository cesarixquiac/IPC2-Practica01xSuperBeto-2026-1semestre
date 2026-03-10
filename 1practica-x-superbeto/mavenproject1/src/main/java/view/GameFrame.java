/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

/**
 *
 * @author cesar
 */
import dao.NivelDAO;
import dao.PedidoDAO;
import javax.swing.*;
import java.awt.*;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import model.EstadoPedido;


public class GameFrame extends JFrame {

    private int idPartida;
    private boolean partidaActiva = true;

    private int nivelActual = 1;
    private int tiempoBaseActual = 60; 
    private Map<Integer, int[]> reglasNiveles; 
    private JLabel labelNivel; 

    public void setIdPartida(int idPartida) {
        this.idPartida = idPartida;
    }

    public int getPuntaje() {
        return puntaje;
    }


    public int getNivelActual() {
        return nivelActual;
    }
  

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
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        NivelDAO nivelDAO = new NivelDAO();
        reglasNiveles = nivelDAO.obtenerReglasNiveles();
        
        if (reglasNiveles.containsKey(1)) {
            tiempoBaseActual = reglasNiveles.get(1)[0];
        }

        JPanel panelInfo = new JPanel(new GridLayout(1, 4));
        panelInfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        labelJugador = new JLabel("Jugador: " + jugador);
        labelNivel = new JLabel("Nivel: " + nivelActual, SwingConstants.CENTER); 
        labelTiempo = new JLabel("Tiempo: 180", SwingConstants.CENTER);
        labelPuntaje = new JLabel("Puntos: 0", SwingConstants.RIGHT);

        panelInfo.add(labelJugador);
        panelInfo.add(labelNivel); 
        panelInfo.add(labelTiempo);
        panelInfo.add(labelPuntaje);

        add(panelInfo, BorderLayout.NORTH);

        panelPedidos = new JPanel();
        panelPedidos.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 15));

        JScrollPane scroll = new JScrollPane(panelPedidos);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(scroll, BorderLayout.CENTER);
    }

    public void agregarPedido(int idPedido, String pedidoTexto, int tiempoLimiteBD) {

        if(!partidaActiva){
            return;
        }

        if (panelPedidos.getComponentCount() >= 5) {
            System.out.println("Máximo de pedidos activos alcanzado");
            return;
        }

        PedidoDAO pedidoDAO = new PedidoDAO();

        JPanel tarjeta = new JPanel();
        tarjeta.setPreferredSize(new Dimension(220, 160));
        tarjeta.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        tarjeta.setLayout(new GridLayout(4, 1, 5, 5)); 

        JLabel labelEstado = new JLabel("Estado: RECIBIDA", SwingConstants.CENTER);
        JLabel labelPedido = new JLabel("<html><center>" + pedidoTexto + "</center></html>", SwingConstants.CENTER);
        JLabel labelTiempoPedido = new JLabel("Tiempo: " + tiempoLimiteBD, SwingConstants.CENTER);

        JButton btnAvanzar = new JButton("Avanzar");
        JButton btnCancelar = new JButton("Cancelar");

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        panelBotones.add(btnAvanzar);
        panelBotones.add(btnCancelar);

        tarjeta.add(labelEstado);
        tarjeta.add(labelPedido);
        tarjeta.add(labelTiempoPedido);
        tarjeta.add(panelBotones);

        panelPedidos.add(tarjeta);
        panelPedidos.revalidate();
        panelPedidos.repaint();

        final EstadoPedido[] estado = {EstadoPedido.RECIBIDA};

        Timer timer = new Timer(1000, null);

        timer.addActionListener(e -> {
            if(!partidaActiva){
                timer.stop();
                return;
            }

            int tiempo = Integer.parseInt(labelTiempoPedido.getText().replace("Tiempo: ", ""));
            tiempo--;

            labelTiempoPedido.setText("Tiempo: " + tiempo);

            if (tiempo <= 0) {
                timer.stop();
                
                pedidoDAO.finalizarPedido(idPedido, 6, -50, false);
                
                panelPedidos.remove(tarjeta);
                panelPedidos.revalidate();
                panelPedidos.repaint();

                actualizarPuntaje(-50);
                noEntregados++;
                JOptionPane.showMessageDialog(this, "Pedido vencido (-50 Pts)");
            }
        });

        timer.start();
        timersPedidos.put(tarjeta, timer);

        btnAvanzar.addActionListener(e -> {
            if(!partidaActiva){
                return;
            }

            switch (estado[0]) {
                case RECIBIDA:
                    estado[0] = EstadoPedido.PREPARANDO;
                    labelEstado.setText("Estado: PREPARANDO");
                    tarjeta.setBackground(Color.cyan);
                    pedidoDAO.actualizarEstadoPedido(idPedido, 2);
                    break;
                case PREPARANDO:
                    estado[0] = EstadoPedido.EN_HORNO;
                    labelEstado.setText("Estado: EN_HORNO");
                    tarjeta.setBackground(Color.orange);
                    pedidoDAO.actualizarEstadoPedido(idPedido, 3);
                    break;
                case EN_HORNO:
                    estado[0] = EstadoPedido.ENTREGADO;
                    labelEstado.setText("Estado: ENTREGADO");
                    tarjeta.setBackground(Color.green);
                    timer.stop();

                    int tiempoSobrante = Integer.parseInt(labelTiempoPedido.getText().replace("Tiempo: ", ""));
                    int puntosGanados = 100; 
                    boolean bonoAplicado = false;
                    String mensaje = "Pedido entregado (+100 Pts)";

                    if (tiempoSobrante >= (tiempoLimiteBD / 2)) {
                        puntosGanados += 50; 
                        bonoAplicado = true;
                        mensaje = "¡RÁPIDO! Bono de eficiencia aplicado (+150 Pts)";
                    }

                    pedidoDAO.finalizarPedido(idPedido, 4, puntosGanados, bonoAplicado);
                  
                    panelPedidos.remove(tarjeta);
                    panelPedidos.revalidate();
                    panelPedidos.repaint();

                    actualizarPuntaje(puntosGanados);
                    entregados++;
                    
                    evaluarNivel();

                    JOptionPane.showMessageDialog(this, mensaje);
                    break;
            }
        });

        btnCancelar.addActionListener(e -> {
            if(!partidaActiva){
                return;
            }

            if (estado[0] == EstadoPedido.EN_HORNO || estado[0] == EstadoPedido.ENTREGADO) {
                JOptionPane.showMessageDialog(this, "No puedes cancelar un pedido que ya está en el horno.", "Acción no permitida", JOptionPane.WARNING_MESSAGE);
                return;
            }

            timer.stop();
          
            pedidoDAO.finalizarPedido(idPedido, 5, -30, false);
            
            panelPedidos.remove(tarjeta);
            panelPedidos.revalidate();
            panelPedidos.repaint();
            
            actualizarPuntaje(-30);
            cancelados++;
            
            JOptionPane.showMessageDialog(this, "Pedido cancelado manualmente (-30 Pts)");
        });
    }

    private void evaluarNivel() {
        int proximoNivel = nivelActual + 1;
        
        if (reglasNiveles.containsKey(proximoNivel)) {
            int metaPedidos = reglasNiveles.get(proximoNivel)[1]; 
            if (entregados >= metaPedidos) {
                nivelActual = proximoNivel;
                tiempoBaseActual = reglasNiveles.get(nivelActual)[0]; 
                
                labelNivel.setText("Nivel: " + nivelActual);
                JOptionPane.showMessageDialog(this, "¡Felicidades! Avanzaste al Nivel " + nivelActual + ". Tiempos reducidos.");
            }
        }
    }

    public int getTiempoBaseActual() {
        return tiempoBaseActual;
    }
   
    public int getCantidadPedidos() {
        return panelPedidos.getComponentCount();
    }
    
    public void actualizarTiempo(int tiempo) {
        labelTiempo.setText("Tiempo restante: " + tiempo);
    }

    public void actualizarPuntaje(int cambio) {
        puntaje += cambio;
        labelPuntaje.setText("Puntaje: " + puntaje);
    }

    public void limpiarPedidos() {
        panelPedidos.removeAll();
        panelPedidos.revalidate();
        panelPedidos.repaint();
    }

    public void finalizarPartida() {
        partidaActiva = false;
        for (Timer t : timersPedidos.values()) {
            t.stop();
        }
        timersPedidos.clear();
        mostrarResumenFinal();
        limpiarPedidos();
    }

    public void mostrarResumenFinal() {
        JOptionPane.showMessageDialog(this,
                "PARTIDA FINALIZADA\n\n"
                + "Nivel Alcanzado: " + nivelActual + "\n"
                + "Entregados: " + entregados + "\n"
                + "Cancelados: " + cancelados + "\n"
                + "Vencidos: " + noEntregados + "\n\n"
                + "Puntaje Final: " + puntaje);
    }
}


    
    

