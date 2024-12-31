import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.StringBuilder;

class shape {
    private int height;
    private int width;

    shape () {
      height = 0 ;
      width = 0 ;
    }
    shape(int h, int w) {
        height = h;
        width = w;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

   float get_area()
   {
      return height*width;
   }
}

class circle extends shape {
    private float radius;

    circle(float r) {
        radius = r;
    }

    circle(int h, int w, float r) {
        super(h, w);
        radius = r;
    }

    @Override
    float get_area() {
        return (float)(Math.PI * radius * radius);
    }
}

class test {

    public static void main(String[] args) {
        circle c = new circle(5);
        System.out.println("Circle area: " + c.get_area());
        double x = 4f ;
        System.out.println(x/0);
    }
}
