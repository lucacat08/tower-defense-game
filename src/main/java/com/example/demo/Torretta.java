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
    public long cooldownNs;     // tempo tra un colpo e l altro in nanosecondi
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
        }

    }
}
