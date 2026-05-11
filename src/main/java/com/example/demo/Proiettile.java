package com.example.demo;

public class Proiettile {

    public double x;
    public double y;
    public double dx;
    public double dy;
    public double danno;
    public boolean attivo;
    public boolean areaEffect;
    public double raggioArea;
    public int torreTipo;

    public Proiettile(double x, double y, double dx, double dy,
                      double danno, boolean areaEffect, int torreTipo) {
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.danno = danno;
        this.attivo = true;
        this.areaEffect = areaEffect;
        this.raggioArea = 60;
        this.torreTipo  = torreTipo;
    }
}

