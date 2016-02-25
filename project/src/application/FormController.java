package application;

import crypto.DES;
import crypto.OperationMode;
import crypto.OperationMode.Types;
import crypto.RSA;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.math.BigInteger;


/**
 * Created by Guan Huang on 10/19/2015.
 */
public class FormController {
    private Form formView;
    private OperationMode operationMode = new OperationMode(new DES(), Types.Cipher_Feedback);
    private RSA rsa = new RSA();
    private Task task;
    private static String algorithm = "DES";

    public FormController(Form formView) {
        this.formView = formView;
        this.formView.addButtonsActionListener(encryptionEvent(), decryptionEvent());
        this.formView.addKeyListener(keyEnteredEvent());
        this.formView.calcMessageLength(messageSizeEvent());
        this.formView.setRSAKeySize(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final Object selectedItem = ((JComboBox) e.getSource()).getSelectedItem();
                rsa.setNumOfBits(Integer.valueOf(selectedItem.toString()));
            }
        });
        this.task = new Task();
    }

    private KeyListener messageSizeEvent() {
        return new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int numOfBits = formView.getText("INPUT").getBytes().length * 8;
                formView.setMessageLength(String.format("%d bits", numOfBits));
            }
        };
    }

    private ActionListener decryptionEvent() {
        return e -> {
            String cipher = formView.getText("INPUT");
            String plainText = null;
            switch (algorithm) {
                case "DES":
                    String key = formView.keyEntered();
                    if (validateKey(key)) {
                        operationMode.setKey(key);
                        plainText = operationMode.decryption(cipher);
                        formView.setTextField(plainText, "OUTPUT");
                    } else {
                        showMessageDialog("Key must be in hexadecimal format and 64 bits long!");
                    }
                    break;
                case "RSA":
                    BigInteger intPlainText = rsa.decryption(new BigInteger(cipher));
                    plainText = new String(intPlainText.toByteArray());
                    formView.setTextField(plainText, "OUTPUT");
                    break;
            }
            task = new Task();
            task.start();
        };
    }

    public static void showMessageDialog(String error) {
        JOptionPane.showMessageDialog(null, error);
    }

    private ActionListener encryptionEvent() {
        return e -> {
            String plainText = formView.getText("INPUT");
            String cipher = null;
            switch (algorithm) {
                case "DES":
                    String key = formView.keyEntered();
                    if (validateKey(key)) {
                        operationMode.setKey(key);
                    } else {
                        showMessageDialog("Key must be in hexadecimal format and 64 bits long!");
                        String generatedKey = operationMode.randomHexKey(16);
                        operationMode.setKey(generatedKey);
                        formView.setKeyTextField(generatedKey);
                    }
                    cipher = operationMode.encryption(plainText);
                    formView.setTextField(cipher, "OUTPUT");
                    break;
                case "RSA":
                    System.out.println(plainText.getBytes().length * 8);
                    try {
                        BigInteger intCipher = rsa.encryption(new BigInteger(plainText.getBytes()));
                        cipher = String.valueOf(intCipher);
                    } catch (Exception ex) {
                        showMessageDialog(ex.getMessage());
                        return;
                    }
                    formView.setTextField(cipher, "OUTPUT");
                    formView.setRSAInfo(rsa.getP(), rsa.getQ(), rsa.getD());
                    break;
            }
            task = new Task();
            task.start();
        };
    }

    /**
     * Check to see if the key is in the hexadecimal format
     *
     * @param key input key
     * @return true if key is not empty and it is in the hexadecimal format
     */
    public static boolean validateKey(String key) {
        return !key.equals("") && key.matches("^[0-9a-fA-F]{16}$");
    }

    private KeyListener keyEnteredEvent() {
        return new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                String keyEntered = formView.keyEntered();
                int length = keyEntered.length();
                if (length == 16) {
                    formView.changeColor(Color.GREEN);
                } else if (length > 16) {
                    formView.changeColor(Color.PINK);
                } else {
                    formView.changeColor(Color.WHITE);
                }
                formView.setKeyHintText(String.format("%d bits out of 64 bits", length * 4));
            }
        };
    }

    public static void setAlgorithm(String value) {
        algorithm = value;
    }

    public class Task extends Thread {

        public Task() {
        }

        public void run() {
            formView.openProgressBarFrame();
            for (int i = 0; i <= 100; i += 10) {
                final int progress = i;

                SwingUtilities.invokeLater(() -> formView.setProgressBar(progress));

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            formView.closeProgressBarFrame();
        }
    }
}
