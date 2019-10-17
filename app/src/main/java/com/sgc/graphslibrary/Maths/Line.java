package com.sgc.graphslibrary.Maths;

public class Line {
    private double X1;
    private double Y1;
    private double X2;
    private double Y2;

    public Line(double X1, double Y1, double X2, double Y2) {
        this.X1 = X1;
        this.Y1 = Y1;
        this.X2 = X2;
        this.Y2 = Y2;
    }

    public double getX1() {
        return X1;
    }

    public void setX1(double x1) {
        X1 = x1;
    }

    public double getY1() {
        return Y1;
    }

    public void setY1(double y1) {
        Y1 = y1;
    }

    public double getX2() {
        return X2;
    }

    public void setX2(double x2) {
        X2 = x2;
    }

    public double getY2() {
        return Y2;
    }

    public void setY2(double y2) {
        Y2 = y2;
    }
}
