/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author cesar
 */
public enum EstadoPedido {

    RECIBIDA(1),
    PREPARANDO(2),
    EN_HORNO(3),
    ENTREGADO(4),
    CANCELADA(5),
    NO_ENTREGADO(6);

    private final int id;

    EstadoPedido(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static EstadoPedido fromId(int id){
        for(EstadoPedido e : values()){
            if(e.id == id){
                return e;
            }
        }
        return null;
    }
}