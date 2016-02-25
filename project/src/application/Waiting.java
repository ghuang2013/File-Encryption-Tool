package application;

import javax.swing.*;
import static application.Form.fileRoot;

/**
 * Created by Guan Huang on 11/26/2015.
 */
public class Waiting {
    JProgressBar progressBar1;
    JPanel panel1;
    JFrame frame;

    public Waiting() {
        frame = new JFrame("Waiting");
        frame.setContentPane(panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setIconImage(new ImageIcon(fileRoot + "keyicon.png").getImage());
        frame.setSize(300, 300);
        frame.setVisible(true);
    }

    public static void main(String[] a) {
        new Waiting();
    }
}
