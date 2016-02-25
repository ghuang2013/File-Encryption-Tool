package application;

import crypto.DES;
import crypto.OperationMode;
import static application.Form.fileRoot;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.util.Scanner;

/**
 * Created by Guan Huang on 11/26/2015.
 */
public class FileSelector {
    private JTextField inputFile;
    private JButton inputFileButton;
    private JTextField outputFile;
    private JButton outputFileButton;
    private JButton OKButton;
    private JButton cancelButton;

    private JTextField key;
    private JPanel panel;
    private JFileChooser fileChooser;
    private JFrame frame;

    private JRadioButton encryption;
    private JRadioButton decryption;
    private boolean encryptionMode = true;

    private Form parentForm;

    public FileSelector(Form parent) {
        parentForm = parent;
        frame = new JFrame("FileSelector");
        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 280);
        frame.setIconImage(new ImageIcon(fileRoot + "keyicon.png").getImage());
        frame.setVisible(true);
        fileSelectorEvents();
    }

    private void fileSelectorEvents() {
        inputFileButton.addActionListener(fileListener("INPUT", inputFile));
        outputFileButton.addActionListener(fileListener("OUTPUT", outputFile));
        OKButton.addActionListener(fileCrypt());
        cancelButton.addActionListener(closeFrame());
        key.addKeyListener(keyListener());
        encryption.addActionListener(setFlag(true));
        decryption.addActionListener(setFlag(false));
    }

    private ActionListener setFlag(boolean b) {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                encryptionMode = b;
                if (encryptionMode) {
                    decryption.setSelected(false);
                } else {
                    encryption.setSelected(false);
                }
            }
        };
    }

    private ActionListener fileCrypt() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!FileSelector.this.validation()) {
                    return;
                }
                String outputText = "";

                try {
                    Scanner scanner = new Scanner(new File(inputFile.getText()));
                    //read input into a string
                    String inputText = scanner.useDelimiter("\\Z").next();
                    OperationMode operationMode = new OperationMode(new DES(),
                            OperationMode.Types.Cipher_Feedback);
                    operationMode.setKey(FileSelector.this.key.getText());

                    outputText = encryptionMode ? operationMode.encryption(inputText) :
                            operationMode.decryption(inputText);
                    parentForm.setTextField(inputText, "INPUT");
                    parentForm.setTextField(outputText, "OUTPUT");
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                BufferedWriter writer = null;
                try {
                    writer = new BufferedWriter(new FileWriter(outputFile.getText()));
                    writer.write(outputText);
                    FormController.showMessageDialog("Success");
                } catch (IOException e1) {
                    e1.printStackTrace();
                } finally {
                    try {
                        writer.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        };
    }

    boolean validation() {
        if (!FormController.validateKey(key.getText())) {
            FormController.showMessageDialog("Key must be in hex format!");
            return false;
        } else if (inputFile.getText().equals("") && outputFile.getText().equals("")) {
            FormController.showMessageDialog("Please select input and output text files!");
            return false;
        }
        return true;
    }

    private ActionListener closeFrame() {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.setVisible(false);
            }
        };
    }

    public ActionListener fileListener(String operation, JTextField fileLabel) {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileChooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                        "txt files", "txt");
                fileChooser.setFileFilter(filter);

                int userOption = fileChooser.showOpenDialog(frame);
                if (userOption == JFileChooser.APPROVE_OPTION) {
                    fileLabel.setText(fileChooser.getSelectedFile().getPath());
                }
            }
        };
    }

    private KeyListener keyListener() {
        return new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                String keyEntered = key.getText();
                int length = keyEntered.length();
                if (length == 16) {
                    key.setBackground(Color.GREEN);
                } else if (length > 16) {
                    key.setBackground(Color.PINK);
                } else {
                    key.setBackground(Color.WHITE);
                }
            }
        };
    }
}