package com.example.demo;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;

public class GameLoop extends AnimationTimer {

    App app;
    GraphicsContext gc;

    public GameLoop(App app, GraphicsContext gc) {
        this.app = app;
        this.gc = gc;
    }

    public void handle(long now) {
        if (!app.giocoAttivo) {
            app.disegna(gc);
            return;
        }
        app.aggiornaSpawnNemici(now);
        app.aggiornaNemici();
        app.aggiornaTorrette(now);
        app.aggiornaProiettili();
        app.controllaVittoria();
        app.disegna(gc);
    }
}
