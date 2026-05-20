package com.mycompany.assingnment3;

import java.util.Objects;

public class Pos {
    public final int x;
    public final int y;

    public Pos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pos)) return false;
        Pos pos = (Pos) o;
        return x == pos.x && y == pos.y;
    }

    @Override public int hashCode() {
        return Objects.hash(x, y);
    }
}
