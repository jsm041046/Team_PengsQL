package com.example.vept.pl.L4;

public class Diagram {
    private float x;
    private float y;
    private String name;

    public Diagram(String name, float x, float y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public String getName() { return name; }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
