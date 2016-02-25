package application;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

public class Application {

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(NimbusLookAndFeel.class.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                new FormController(new Form());
            }
        });
    }
}
