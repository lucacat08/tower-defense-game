package com.example.demo;

public class Torretta {
    //TIPI TORRETTA

    public static final int freccia = 0;
    public static final int MAGICA  = 1;
    public static final int BOMBA   = 2;

    //ATTRIBUTI

    public int tipo;
    public double x;
    public double y;
    public double danno;
    public double gittata;
    public long cooldownSecondi;     // tempo tra un colpo e l altro in nanosecondi
    public long ultimoSparo;    // timestamp dell ultimo sparo
    public int costo;
    public boolean rallenta;    // solo torretta magica

    public Torretta(int tipo, double x, double y){
        this.tipo = tipo;
        this.x = x;
        this.y = y;
        this.ultimoSparo = 0;

        if (tipo == freccia){
            this.danno = 15;
            this.gittata = 120;
            this.costo = 50;
            this.cooldownSecondi = (long) 0.8;
            this.rallenta = false;
        }
        else if (tipo == MAGICA) {
            this.danno           = 30;
            this.gittata         = 180;
            this.cooldownSecondi = (long) 2.0;
            this.costo           = 100;
            this.rallenta        = true;
        }
        else if (tipo == BOMBA) {
            this.danno           = 50;
            this.gittata         = 100;
            this.cooldownSecondi = (long) 3.5;
            this.costo           = 150;
            this.rallenta        = false;
        }

        // Calcolo automatico dei nanosecondi: 1 secondo = 1.000.000.000 ns
        this.cooldownSecondi = this.cooldownSecondi * 1_000_000_000;
    }
}

