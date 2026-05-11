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
        if (cx > 120 && cx < 220) torreTipoSelezionato = Torretta.magica;
        if (cx > 230 && cx < 330) torreTipoSelezionato = Torretta.bomba;
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
        if (torreTipoSelezionato == Torretta.magica) costo = 100;
        if (torreTipoSelezionato == Torretta.bomba) costo = 150;
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
                proiettili.add(new Proiettile(t.x + 20, t.y + 20, vx, vy, t.danno, t.tipo == Torretta.bomba, t.tipo));
            }
        }
    }
    void aggiornaProiettili() {
        for (int i = proiettili.size() - 1; i >= 0; i--) {
            Proiettile p = proiettili.get(i);
            if (!p.attivo) { proiettili.remove(i); continue; }

            p.x = p.x + p.dx;
            p.y = p.y + p.dy;

            if (p.x < 0 || p.x > LARGHEZZA || p.y < 0 || p.y > ALTEZZA) {
                proiettili.remove(i);
                continue;
            }

            for (int j = 0; j < nemici.size(); j++) {
                Nemico n = nemici.get(j);
                if (!n.vivo) continue;

                double dx = p.x - n.x;
                double dy = p.y - n.y;
                double dist = Math.sqrt(dx * dx + dy * dy);

                if (dist < 20) {
                    p.attivo = false;

                    boolean mancato = false;
                    if (n.tipo == Nemico.OMBRA && p.torreTipo == Torretta.freccia) {
                        if (Math.random() < n.missChance) mancato = true;
                    }

                    if (!mancato) {
                        if (p.areaEffect) {
                            for (int k = 0; k < nemici.size(); k++) {
                                Nemico altro = nemici.get(k);
                                if (!altro.vivo) continue;
                                double ax = p.x - altro.x;
                                double ay = p.y - altro.y;
                                if (Math.sqrt(ax * ax + ay * ay) < p.raggioArea) {
                                    altro.hp = altro.hp - p.danno;
                                    if (altro.hp <= 0) altro.vivo = false;
                                }
                            }
                        } else {
                            n.hp = n.hp - p.danno;
                            if (p.torreTipo == Torretta.magica) {
                                n.velocita = n.velocita * 0.5;
                                if (n.velocita < 0.3) n.velocita = 0.3;
                            }
                            if (n.hp <= 0) n.vivo = false;
                        }
                    }
                    break;
                }
            }
        }
    }

    void controllaVittoria() {
        if (ondate.ondataCorrente == 5 && !ondate.ondataInCorso && nemici.isEmpty()) {
            vittoria = true;
            giocoAttivo = false;
        }
    }

    void disegna(GraphicsContext gc) {
        gc.setFill(Color.rgb(34, 85, 34));
        gc.fillRect(0, 0, LARGHEZZA, ALTEZZA);

        gc.setFill(Color.rgb(160, 130, 90));
        for (int i = 0; i < Percorso.puntiX.length - 1; i++) {
            double x1 = Percorso.puntiX[i];
            double y1 = Percorso.puntiY[i];
            double x2 = Percorso.puntiX[i + 1];
            double y2 = Percorso.puntiY[i + 1];
            gc.fillRect(Math.min(x1, x2), Math.min(y1, y2),
                    Math.abs(x2 - x1) + 40, Math.abs(y2 - y1) + 40);
        }

        gc.setFill(Color.GOLD);
        gc.fillOval(750, 430, 30, 30);
        gc.setFill(Color.WHITE);
        gc.fillText("Ember", 745, 425);

        for (int i = 0; i < torrette.size(); i++) {
            Torretta t = torrette.get(i);
            if (t.tipo == Torretta.freccia) gc.setFill(Color.SADDLEBROWN);
            if (t.tipo == Torretta.magica)  gc.setFill(Color.MEDIUMPURPLE);
            if (t.tipo == Torretta.bomba)   gc.setFill(Color.DARKGRAY);
            gc.fillRect(t.x + 5, t.y + 5, 30, 30);
            gc.setFill(Color.BLACK);
            gc.fillRect(t.x + 17, t.y, 6, 10);
        }

        for (int i = 0; i < nemici.size(); i++) {
            Nemico n = nemici.get(i);
            if (!n.vivo) continue;
            if (n.tipo == Nemico.GOBLIN) gc.setFill(Color.LIMEGREEN);
            if (n.tipo == Nemico.ORCO)   gc.setFill(Color.DARKGREEN);
            if (n.tipo == Nemico.OMBRA)  gc.setFill(Color.DARKVIOLET);
            gc.fillOval(n.x - 10, n.y - 10, 22, 22);
            double barraW = 24 * (n.hp / n.hpMax);
            gc.setFill(Color.DARKRED);
            gc.fillRect(n.x - 12, n.y - 18, 24, 5);
            gc.setFill(Color.RED);
            gc.fillRect(n.x - 12, n.y - 18, barraW, 5);
        }

        for (int i = 0; i < proiettili.size(); i++) {
            Proiettile p = proiettili.get(i);
            if (!p.attivo) continue;
            if (p.torreTipo == Torretta.freccia) gc.setFill(Color.YELLOW);
            if (p.torreTipo == Torretta.magica)  gc.setFill(Color.VIOLET);
            if (p.torreTipo == Torretta.bomba)   gc.setFill(Color.ORANGERED);
            gc.fillOval(p.x - 5, p.y - 5, 10, 10);
        }

        gc.setFill(Color.color(0, 0, 0, 0.6));
        gc.fillRect(0, 0, LARGHEZZA, 36);
        gc.setFont(Font.font(14));
        gc.setFill(Color.GOLD);
        gc.fillText("Oro: " + oro, 10, 22);
        gc.setFill(Color.WHITE);
        gc.fillText("Ondata: " + ondate.ondataCorrente + "/5", 130, 22);
        gc.setFill(Color.TOMATO);
        gc.fillText("Nemici passati: " + nemiciPassati + "/" + maxNemiciPassati, 280, 22);

        gc.setFill(Color.color(0, 0, 0, 0.75));
        gc.fillRect(0, ALTEZZA - 75, LARGHEZZA, 75);

        gc.setFill(torreTipoSelezionato == Torretta.freccia ? Color.WHITE : Color.GRAY);
        gc.fillRect(10, ALTEZZA - 65, 100, 55);
        gc.setFill(Color.BLACK);
        gc.fillText("FRECCIA", 20, ALTEZZA - 35);
        gc.fillText("50 oro", 28, ALTEZZA - 18);

        gc.setFill(torreTipoSelezionato == Torretta.magica ? Color.WHITE : Color.GRAY);
        gc.fillRect(120, ALTEZZA - 65, 100, 55);
        gc.setFill(Color.BLACK);
        gc.fillText("MAGICA", 132, ALTEZZA - 35);
        gc.fillText("100 oro", 135, ALTEZZA - 18);

        gc.setFill(torreTipoSelezionato == Torretta.bomba ? Color.WHITE : Color.GRAY);
        gc.fillRect(230, ALTEZZA - 65, 100, 55);
        gc.setFill(Color.BLACK);
        gc.fillText("BOMBA", 245, ALTEZZA - 35);
        gc.fillText("150 oro", 245, ALTEZZA - 18);

        gc.setFill(Color.LIGHTGRAY);
        gc.fillText("[ click sulla mappa per piazzare ]   [ click sui pulsanti per selezionare ]", 340, ALTEZZA - 35);

        if (!giocoAttivo) {
            gc.setFill(Color.color(0, 0, 0, 0.75));
            gc.fillRect(0, 0, LARGHEZZA, ALTEZZA);
            gc.setFont(Font.font(48));
            if (vittoria) {
                gc.setFill(Color.GOLD);
                gc.fillText("VITTORIA!", 270, 300);
            } else {
                gc.setFill(Color.RED);
                gc.fillText("GAME OVER", 240, 300);
            }
            gc.setFont(Font.font(18));
            gc.setFill(Color.WHITE);
            gc.fillText("Nemici passati: " + nemiciPassati, 340, 350);
        }
    }

    public static void main(String[] args) { launch(args); }
}
