import javax.swing.*;
import java.awt.*;

class cylinder extends JPanel {

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        //
        g.drawOval(80, 30, 200, 40);

        //
        g.drawLine(80, 55, 80, 205);
        g.drawLine(280, 55, 280, 205);

        //
        g.drawOval(80, 185, 200, 40);
        
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Cylinder");
        cylinder panel = new cylinder();

        frame.add(panel);
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
