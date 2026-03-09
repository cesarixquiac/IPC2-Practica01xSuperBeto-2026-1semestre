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

public class GameBoardPanel extends JPanel {

    public JPanel colRecibida = new JPanel();
    public JPanel colPreparando = new JPanel();
    public JPanel colHorno = new JPanel();
    public JPanel colListo = new JPanel();

    public GameBoardPanel() {

        setLayout(new GridLayout(1,4,10,10));

        colRecibida.setLayout(new BoxLayout(colRecibida, BoxLayout.Y_AXIS));
        colPreparando.setLayout(new BoxLayout(colPreparando, BoxLayout.Y_AXIS));
        colHorno.setLayout(new BoxLayout(colHorno, BoxLayout.Y_AXIS));
        colListo.setLayout(new BoxLayout(colListo, BoxLayout.Y_AXIS));

        add(crearColumna("RECIBIDA", colRecibida));
        add(crearColumna("PREPARANDO", colPreparando));
        add(crearColumna("EN HORNO", colHorno));
        add(crearColumna("ENTREGADO", colListo));
    }

    private JPanel crearColumna(String titulo, JPanel panelPedidos){

        JPanel contenedor = new JPanel(new BorderLayout());

        JLabel lbl = new JLabel(titulo, SwingConstants.CENTER);
        lbl.setFont(new Font("Arial", Font.BOLD, 16));

        contenedor.add(lbl, BorderLayout.NORTH);
        contenedor.add(new JScrollPane(panelPedidos), BorderLayout.CENTER);

        return contenedor;
    }

    public void agregarPedido(PedidoCard card){
        colRecibida.add(card);
        revalidate();
        repaint();
    }
}
