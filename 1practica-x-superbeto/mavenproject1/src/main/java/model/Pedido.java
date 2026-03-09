/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.List;

/**
 *
 * @author cesar
 */
import java.time.LocalDateTime;

public class Pedido {

    private int id;
    private EstadoPedido estado;
    private LocalDateTime tiempoCreacion;
    private int tiempoLimite = 60;

    public Pedido(int id) {
        this.id = id;
        this.estado = EstadoPedido.RECIBIDA;
        this.tiempoCreacion = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    public int getTiempoRestante() {

        int transcurrido =
                (int) java.time.Duration
                        .between(tiempoCreacion, LocalDateTime.now())
                        .getSeconds();

        return tiempoLimite - transcurrido;
    }

    public boolean estaVencido(){
        return getTiempoRestante() <= 0;
    }

    public void avanzarEstado(){

        switch (estado){

            case RECIBIDA:
                estado = EstadoPedido.PREPARANDO;
                break;

            case PREPARANDO:
                estado = EstadoPedido.EN_HORNO;
                break;

            case EN_HORNO:
                estado = EstadoPedido.ENTREGADO;
                break;

            default:
                System.out.println("Pedido finalizado");
        }
    }

    @Override
    public String toString() {
        return "Pedido #" + id + " | Estado: " + estado;
    }
}
