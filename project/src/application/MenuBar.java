package application;

import javax.swing.*;
import java.awt.event.ActionListener;

/**
 * Created by Guan Huang on 12/2/2015.
 */
public class MenuBar extends JMenuBar {

    private Form parentForm;

    public MenuBar(Form parentForm) {
        this.parentForm = parentForm;
        parentForm.setPanelVisibility(false, true);
        JMenu fileMenu = buildFileMenu();
        JMenu algorithm = buildAlgoMenu();
        add(fileMenu);
        add(algorithm);
    }

    private JMenu buildAlgoMenu() {
        JMenu algorithm = new JMenu("Algorithm");

        JMenuItem des = new JMenuItem("DES");
        algorithm.add(des);
        des.addActionListener(algorithmSelected(false, true));

        JMenuItem rsa = new JMenuItem("RSA");
        algorithm.add(rsa);
        rsa.addActionListener(algorithmSelected(true, false));
        return algorithm;
    }

    private JMenu buildFileMenu() {
        JMenu fileMenu = new JMenu("File");

        JMenuItem select = new JMenuItem("Select");
        select.setToolTipText("File Encryption/Decryption");
        select.addActionListener(algorithmSelected(false, true));
        select.addActionListener(event -> new FileSelector(parentForm));
        fileMenu.add(select);

        JMenuItem exit = new JMenuItem("Exit");
        exit.setToolTipText("Close application");
        exit.addActionListener(event -> System.exit(0));
        fileMenu.add(exit);
        return fileMenu;
    }

    private ActionListener algorithmSelected(boolean showRSA, boolean showDES) {
        return e -> {
            parentForm.setPanelVisibility(showRSA, showDES);
            parentForm.clearTextAreas();
            if (showRSA) {
                parentForm.setTitle("RSA Crypto System");
                FormController.setAlgorithm("RSA");
            } else {
                parentForm.setTitle("DES Crypto System");
                FormController.setAlgorithm("DES");
            }
        };
    }
}
