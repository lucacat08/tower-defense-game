package com.example.demo;

import java.util.ArrayList;

public class GestioneOndate {
    public int ondataCorrente = 0;
    public boolean ondataInCorso = false;
    public int nemiciRimasti = 0;
    public long ultimoSpawnNemico = 0;
    public int indiceSpawn = 0;
    public ArrayList<int[]> codaSpawn = new ArrayList<>();


    private void aggiungi(int quantita, int tipo, int ritardo) {
        for (int i = 0; i < quantita; i++) {
            codaSpawn.add(new int[]{tipo, ritardo});
        }
    }

    public void preparaOndata(int numero) {
        codaSpawn.clear();
        indiceSpawn = 0;

        switch (numero) {
            case 1 -> aggiungi(6, Nemico.GOBLIN, 1500);
            case 2 -> aggiungi(8, Nemico.GOBLIN, 1200);
            case 3 -> {
                aggiungi(5, Nemico.GOBLIN, 1200);
                aggiungi(3, Nemico.ORCO, 2000);
            }
            case 4 -> {
                aggiungi(4, Nemico.GOBLIN, 1000);
                aggiungi(4, Nemico.OMBRA, 800);
                aggiungi(2, Nemico.ORCO, 2000);
            }
            case 5 -> {
                aggiungi(5, Nemico.GOBLIN, 1000);
                aggiungi(3, Nemico.OMBRA, 800);
                aggiungi(3, Nemico.ORCO, 1500);
                aggiungi(1, Nemico.ORCO, 3000); // Boss
            }
        }

        nemiciRimasti = codaSpawn.size();
        ondataInCorso = true;
    }
}



