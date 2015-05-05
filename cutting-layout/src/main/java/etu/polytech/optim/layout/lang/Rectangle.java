package etu.polytech.optim.layout.lang;

/**
 * Created by Morgan on 10/04/2015.
 */
public class Rectangle implements Cloneable {
    public double x;
    public double y;
    public double width;
    public double height;

    public Rectangle(double x, double y, final double width, final double height) {
        this.x = x;
        this.y = y;

        this.width = width;
        this.height = height;
    }

    public double x() {
        return x;
    }
    public double x(final double x) { return this.x = x;}

    public double y() { return y; }
    public double y(final double y){ return this.y = y; }

    public double width() {
        return width;
    }

    public double height() {
        return height;
    }

    public double area(){
        return width * height;
    }

    public Rectangle rotate(){
        double temp = width;
        width = height;
        height = temp;

        return this;
    }

    @Override
    public final Rectangle clone() {
        return new Rectangle(x, y, width, height);
    }

    @Override
    public String toString() {
        return "Rectangle{" +
                "x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
