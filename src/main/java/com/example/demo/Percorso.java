package com.example.demo;

public class Percorso {

    public static double [] puntiX = {-40, 80, 80, 300, 300, 520, 520, 740, 840};
    public static double [] puntiY = {150, 150,450, 450, 150, 150, 450, 450, 450};

    public static boolean isSulPercorso(double cx, double cy) {

        int col = (int) (cx / 40);
        int riga = (int) (cy / 40);

        for (int i = 0; i < puntiX.length - 1; i++) {

            int cMin = (int) (Math.min(puntiX[i], puntiX[i+1]) / 40);
            int cMax = (int) (Math.max(puntiX[i], puntiX[i+1]) / 40);
            int rMin = (int) (Math.min(puntiY[i], puntiY[i+1]) / 40);
            int rMax = (int) (Math.max(puntiY[i], puntiY[i+1]) / 40);


            if (col >= cMin && col <= cMax && riga >= rMin && riga <= rMax) {
                return true;
            }
        }
        return false;
    }
}

