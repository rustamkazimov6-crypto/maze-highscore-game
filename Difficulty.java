package com.mycompany.assingnment3;

public enum Difficulty {
    EASY(21, 15, 0.08),
    MEDIUM(31, 21, 0.12),
    HARD(41, 27, 0.18);

    public final int w;
    public final int h;
    public final double loopPercent;

    Difficulty(int w, int h, double loopPercent) {
        this.w = w;
        this.h = h;
        this.loopPercent = loopPercent;
    }
}
