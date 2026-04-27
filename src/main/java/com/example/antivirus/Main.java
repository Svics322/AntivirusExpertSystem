package com.example.antivirus;

import com.example.antivirus.db.DatabaseManager;
import com.example.antivirus.ui.AppFrame;
import com.example.antivirus.ui.AppTheme;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AppTheme.install();
            DatabaseManager.initialize();
            new AppFrame().setVisible(true);
        });
    }
}
