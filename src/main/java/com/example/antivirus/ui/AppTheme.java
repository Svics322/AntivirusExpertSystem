package com.example.antivirus.ui;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import java.awt.*;

public final class AppTheme {
    public static final Color BG_1 = new Color(5, 11, 32);
    public static final Color BG_2 = new Color(17, 24, 61);
    public static final Color CARD = new Color(255, 255, 255, 246);
    public static final Color CARD_DARK = new Color(13, 24, 51, 220);
    public static final Color TEXT = new Color(241, 246, 255);
    public static final Color DARK_TEXT = new Color(18, 27, 46);
    public static final Color MUTED = new Color(119, 133, 155);
    public static final Color ACCENT = new Color(92, 124, 255);
    public static final Color ACCENT_2 = new Color(25, 211, 197);
    public static final Color DANGER = new Color(255, 95, 132);
    public static final Color SUCCESS = new Color(34, 197, 94);
    public static final Color BORDER = new Color(213, 223, 239);
    public static final Color FIELD_BG = new Color(255, 255, 255);
    public static final Color PANEL_BG = new Color(248, 251, 255);

    private AppTheme() {
    }

    public static void install() {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (UnsupportedLookAndFeelException ex) {
            throw new IllegalStateException("Не вдалося встановити FlatLaf", ex);
        }

        UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 13));
        UIManager.put("Table.rowHeight", 38);
        UIManager.put("ScrollBar.width", 12);
        UIManager.put("TabbedPane.showTabSeparators", true);
        UIManager.put("TabbedPane.tabArc", 18);
        UIManager.put("Component.arc", 16);
        UIManager.put("Button.arc", 18);
        UIManager.put("TextComponent.arc", 16);
        UIManager.put("ComboBox.arc", 16);
    }

    public static void styleTextField(JTextField field, String placeholder) {
        field.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, placeholder);
        field.putClientProperty(FlatClientProperties.STYLE, "arc: 16; borderWidth: 1; focusedBorderColor: #5c7cff; borderColor: #d5dfef; background: #ffffff; foreground: #121b2e");
        field.setForeground(DARK_TEXT);
        field.setBackground(FIELD_BG);
        field.setCaretColor(DARK_TEXT);
        field.setSelectedTextColor(DARK_TEXT);
        field.setSelectionColor(new Color(223, 232, 255));
    }

    public static void styleTextArea(JTextArea area) {
        area.putClientProperty(FlatClientProperties.STYLE, "borderWidth: 1; focusedBorderColor: #5c7cff; borderColor: #d5dfef");
        area.setForeground(DARK_TEXT);
        area.setBackground(FIELD_BG);
        area.setCaretColor(DARK_TEXT);
        area.setSelectedTextColor(DARK_TEXT);
        area.setSelectionColor(new Color(223, 232, 255));
        area.setOpaque(true);
        area.setMargin(new Insets(10, 12, 10, 12));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
    }

    public static void styleCombo(JComboBox<?> combo) {
        combo.putClientProperty(FlatClientProperties.STYLE, "arc: 18; borderWidth: 1; focusedBorderColor: #5c7cff; borderColor: #d5dfef; background: #ffffff; foreground: #121b2e; buttonBackground: #eef4ff");
        combo.setForeground(DARK_TEXT);
        combo.setBackground(FIELD_BG);
    }
}
