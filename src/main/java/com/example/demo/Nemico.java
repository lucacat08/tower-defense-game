package com.example.demo;

public class Nemico {

    // ==============================
    // TIPI DI NEMICO
    // ==============================
    public static final int GOBLIN = 0;
    public static final int ORCO   = 1;
    public static final int OMBRA  = 2;

    // ==============================
    // ATTRIBUTI
    // ==============================
    public int tipo;
    public double x;
    public double y;
    public double hp;
    public double hpMax;
    public double velocita;
    public int oroValore;
    public boolean vivo;
    public int puntoPercorso; // indice del prossimo punto del percorso
    public double missChance; // probabilita di schivare (solo Ombra)

    // ==============================
    // COSTRUTTORE
    // ==============================
    public Nemico(int tipo, double startX, double startY) {
        this.tipo = tipo;
        this.x = startX;
        this.y = startY;
        this.vivo = true;
        this.puntoPercorso = 0;

        if (tipo == GOBLIN) {
            this.hp       = 50;
            this.hpMax    = 50;
            this.velocita = 2.0;
            this.oroValore = 10;
            this.missChance = 0;
        }
        if (tipo == ORCO) {
            this.hp       = 200;
            this.hpMax    = 200;
            this.velocita = 0.8;
            this.oroValore = 25;
            this.missChance = 0;
        }
        if (tipo == OMBRA) {
            this.hp       = 80;
            this.hpMax    = 80;
            this.velocita = 3.0;
            this.oroValore = 20;
            this.missChance = 0.2;
        }
    }
}
