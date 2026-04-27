package com.example.antivirus.ui;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class AppFrame extends JFrame {
    public AppFrame() {
        setTitle("Експертна система вибору антивірусу");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1180, 780));
        setSize(1320, 860);
        setLocationRelativeTo(null);
        setIconImage(loadIcon());
        setContentPane(new MainPanel());
    }

    private Image loadIcon() {
        URL url = getClass().getResource("/icons/app-icon.png");
        if (url == null) {
            return null;
        }
        return new ImageIcon(url).getImage();
    }
}
