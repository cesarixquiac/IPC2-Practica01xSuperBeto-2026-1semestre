/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

/**
 *
 * @author cesar
 */
import model.Pedido;

import javax.swing.*;
import java.awt.*;

public class PedidoCard extends JPanel {

    private Pedido pedido;
    private JLabel lblProducto;
    private JLabel lblTiempo;

    public PedidoCard(Pedido pedido) {
        this.pedido = pedido;

        setLayout(new GridLayout(2,1));
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setBackground(new Color(255,230,200));

        lblProducto = new JLabel("Pedido #" + pedido.getId(), SwingConstants.CENTER);
        lblTiempo = new JLabel("Tiempo: " + pedido.getTiempoRestante(), SwingConstants.CENTER);

        add(lblProducto);
        add(lblTiempo);

        setPreferredSize(new Dimension(120,60));
    }

    public Pedido getPedido(){
        return pedido;
    }

    public void actualizarTiempo(){
        lblTiempo.setText("Tiempo: " + pedido.getTiempoRestante());
    }
}
