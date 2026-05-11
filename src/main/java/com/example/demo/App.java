package com.example.demo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.util.ArrayList;

public class App extends Application {

    static final int LARGHEZZA = 800;
    static final int ALTEZZA = 600;

    boolean giocoAttivo = true;
    boolean vittoria = false;
    int oro = 150;
    int nemiciPassati = 0;
    int maxNemiciPassati = 10;
    int torreTipoSelezionato = Torretta.freccia;

    ArrayList<Nemico> nemici = new ArrayList<Nemico>();
    ArrayList<Torretta> torrette = new ArrayList<Torretta>();
    ArrayList<Proiettile> proiettili = new ArrayList<Proiettile>();

    GestioneOndate ondate = new GestioneOndate();

    public void start(Stage stage) {
        Canvas canvas = new Canvas(LARGHEZZA, ALTEZZA);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Scene scene = new Scene(new StackPane(canvas), LARGHEZZA, ALTEZZA);

        canvas.setOnMouseClicked(e -> {
            if (!giocoAttivo) return;
            if (e.getButton() == MouseButton.PRIMARY) {
                double cx = e.getX();
                double cy = e.getY();
                if (cy > ALTEZZA - 80) {
                    selezionaTorre(cx, cy);
                    return;
                }
                piazzaTorretta(cx, cy);
            }
        });

        ondate.ondataCorrente = 1;
        ondate.preparaOndata(1);

        new GameLoop(this, gc).start();

        stage.setScene(scene);
        stage.setTitle("Last Ember");
        stage.show();
    }

    void selezionaTorre(double cx, double cy) {
        if (cx > 10 && cx < 110) torreTipoSelezionato = Torretta.freccia;
        if (cx > 120 && cx < 220) torreTipoSelezionato = Torretta.MAGICA;
        if (cx > 230 && cx < 330) torreTipoSelezionato = Torretta.BOMBA;
    }

    void piazzaTorretta(double cx, double cy) {
        double gx = (int) (cx / 40) * 40;
        double gy = (int) (cy / 40) * 40;

        if (Percorso.isSulPercorso(cx, cy)) return;

        for (int i = 0; i < torrette.size(); i++) {
            Torretta t = torrette.get(i);
            if (Math.abs(t.x - gx) < 10 && Math.abs(t.y - gy) < 10) return;
        }

        int costo = 50;
        if (torreTipoSelezionato == Torretta.MAGICA) costo = 100;
        if (torreTipoSelezionato == Torretta.BOMBA) costo = 150;
        if (oro < costo) return;

        oro = oro - costo;
        torrette.add(new Torretta(torreTipoSelezionato, gx, gy));
    }

    void aggiornaSpawnNemici(long now) {
        if (!ondate.ondataInCorso) return;
        if (ondate.indiceSpawn >= ondate.codaSpawn.size()) return;

        int[] prossimo = ondate.codaSpawn.get(ondate.indiceSpawn);
        if (now - ondate.ultimoSpawnNemico >= prossimo[1] * 1_000_000L) {
            ondate.ultimoSpawnNemico = now;
            nemici.add(new Nemico(prossimo[0], Percorso.puntiX[0], Percorso.puntiY[0]));
            ondate.indiceSpawn = ondate.indiceSpawn + 1;
        }
    }

    void aggiornaNemici() {
        for (int i = nemici.size() - 1; i >= 0; i--) {
            Nemico n = nemici.get(i);

            if (!n.vivo) {
                oro = oro + n.oroValore;
                nemici.remove(i);
                continue;
            }

            if (n.puntoPercorso >= Percorso.puntiX.length) {
                nemici.remove(i);
                nemiciPassati = nemiciPassati + 1;
                if (nemiciPassati >= maxNemiciPassati) giocoAttivo = false;
                continue;
            }

            double tx = Percorso.puntiX[n.puntoPercorso];
            double ty = Percorso.puntiY[n.puntoPercorso];
            double dx = tx - n.x;
            double dy = ty - n.y;
            double dist = Math.sqrt(dx * dx + dy * dy);

            if (dist < 3) {
                n.puntoPercorso = n.puntoPercorso + 1;
            } else {
                n.x = n.x + (dx / dist) * n.velocita;
                n.y = n.y + (dy / dist) * n.velocita;
            }
        }

        if (ondate.ondataInCorso && ondate.indiceSpawn >= ondate.codaSpawn.size() && nemici.isEmpty()) {
            ondate.ondataInCorso = false;
            if (ondate.ondataCorrente < 5) {
                ondate.ondataCorrente = ondate.ondataCorrente + 1;
                ondate.preparaOndata(ondate.ondataCorrente);
            }
        }
    }

    void aggiornaTorrette(long now) {
        for (int i = 0; i < torrette.size(); i++) {
            Torretta t = torrette.get(i);
            if (now - t.ultimoSparo < t.cooldownSecondi) continue;

            Nemico bersaglio = null;
            double distMinima = t.gittata;

            for (int j = 0; j < nemici.size(); j++) {
                Nemico n = nemici.get(j);
                if (!n.vivo) continue;
                double dx = n.x - t.x;
                double dy = n.y - t.y;
                double dist = Math.sqrt(dx * dx + dy * dy);
                if (dist < distMinima) {
                    distMinima = dist;
                    bersaglio = n;
                }
            }

            if (bersaglio != null) {
                t.ultimoSparo = now;
                double dx = bersaglio.x - t.x;
                double dy = bersaglio.y - t.y;
                double dist = Math.sqrt(dx * dx + dy * dy);
                double vx = (dx / dist) * 5;
                double vy = (dy / dist) * 5;
                proiettili.add(new Proiettile(t.x + 20, t.y + 20, vx, vy, t.danno, t.tipo == Torretta.BOMBA, t.tipo));
            }
        }
    }
}
