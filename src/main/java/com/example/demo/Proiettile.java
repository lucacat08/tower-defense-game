package com.example.demo;

public class Proiettile {

    public double x;
    public double y;
    public double dx;
    public double dy;
    public double danno;
    public boolean attivo;
    public boolean areaEffect; // true solo per torretta bomba
    public double raggioArea;  // raggio danno area (torretta bomba)
    public int torreTipo;      // tipo della torre che ha sparato

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

