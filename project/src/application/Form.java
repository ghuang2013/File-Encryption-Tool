package application;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;

/**
 * Created by Guan Huang on 10/18/2015.
 */
public class Form {

    final public static String fileRoot = "src/application/icons/";

    public Form() {
        JMenuBar menuBar = new MenuBar(this);
        mainFrame = new JFrame("Form");
        mainFrame.setJMenuBar(menuBar);
        mainFrame.setContentPane(panel1);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(600, 700);
        mainFrame.setIconImage(new ImageIcon(fileRoot + "keyicon.png").getImage());
        mainFrame.setVisible(true);
    }

    public void openProgressBarFrame() {
        waiting = new Waiting();
    }

    public void closeProgressBarFrame() {
        waiting.frame.setVisible(false);
    }

    public void setTextField(String value, String target) {
        switch (target) {
            case "INPUT":
                inputText.setText(value);
                return;
            case "OUTPUT":
                outputText.setText(value);
                return;
            default:
                throw new IllegalArgumentException("INTERNAL ERROR");
        }
    }

    public String getText(String target) {
        switch (target) {
            case "INPUT":
                return inputText.getText();
            case "OUTPUT":
                return outputText.getText();
            default:
                throw new IllegalArgumentException("INTERNAL ERROR");
        }
    }

    public void setKeyHintText(String text) {
        keyHint.setText(text);
    }

    public void addButtonsActionListener(ActionListener encListener,
                                         ActionListener decListener) {
        encryptButton.addActionListener(encListener);
        decryptButton.addActionListener(decListener);
    }

    public void setKeyTextField(String value) {
        keyTextField.setText(value);
    }

    public void addKeyListener(KeyListener actionListener) {
        keyTextField.addKeyListener(actionListener);
    }

    public void changeColor(Color color) {
        keyTextField.setBackground(color);
    }

    public void setProgressBar(int value) {
        waiting.progressBar1.setValue(value);
    }

    public String keyEntered() {
        return keyTextField.getText();
    }

    public void clearTextAreas(){
        inputText.setText("");
        outputText.setText("");
    }

    public void setPanelVisibility(boolean showRSA, boolean showDES){
        RSAInfo.setVisible(showRSA);
        DESInfo.setVisible(showDES);
    }

    public void setTitle(String title){
        this.title.setText(title);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        panel1 = new JPanel();
    }

    public void setRSAInfo(String p, String q, String d) {
        this.p.setText(p);
        this.q.setText(q);
        this.privateKey.setText(d);
    }

    public void setRSAKeySize(ActionListener actionListener){
        rsaKeySize.addActionListener(actionListener);
    }

    public void setMessageLength(String content){
        messageLength.setText(content);
    }

    public void calcMessageLength(KeyListener keyListener){
        inputText.addKeyListener(keyListener);
    }

    private JButton encryptButton;
    private JTextArea inputText;
    private JTextArea outputText;
    private JPanel panel1;
    private JButton decryptButton;
    private JFormattedTextField keyTextField;
    private JLabel keyHint;
    private JTextArea p;
    private JTextArea q;
    private JTextArea privateKey;
    private JPanel RSAInfo;
    private JPanel DESInfo;
    private JLabel title;
    private JLabel messageLength;
    private JComboBox rsaKeySize;
    private JFrame mainFrame;
    private Waiting waiting;
}