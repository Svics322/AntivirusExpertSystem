package com.example.antivirus.ui;

import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

public final class UiFactory {
    private UiFactory() {
    }

    public static JLabel title(String text, int size) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, size));
        label.setForeground(AppTheme.DARK_TEXT);
        return label;
    }

    public static JLabel smallMuted(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(AppTheme.MUTED);
        return label;
    }

    public static JButton primaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.putClientProperty(FlatClientProperties.STYLE, "arc: 20; background: #5c7cff; foreground: #ffffff; borderWidth: 0; hoverBackground: #4f70f0; pressedBackground: #405fe0");
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    public static JButton secondaryButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.putClientProperty(FlatClientProperties.STYLE, "arc: 20; background: #edf2ff; foreground: #121b2e; borderWidth: 0; hoverBackground: #dfe8ff; pressedBackground: #d2dcfb");
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    public static JButton dangerButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.putClientProperty(FlatClientProperties.STYLE, "arc: 20; background: #ff5f84; foreground: #ffffff; borderWidth: 0; hoverBackground: #ef4d74; pressedBackground: #dc4267");
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    public static JTextField textField(String placeholder) {
        JTextField field = new JTextField();
        AppTheme.styleTextField(field, placeholder);
        field.setPreferredSize(new Dimension(100, 38));
        return field;
    }

    public static JTextArea textArea() {
        JTextArea area = new JTextArea(4, 20);
        AppTheme.styleTextArea(area);
        return area;
    }

    public static JScrollPane textAreaScroll(JTextArea area) {
        AppTheme.styleTextArea(area);
        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER));
        scroll.setOpaque(true);
        scroll.setBackground(AppTheme.FIELD_BG);
        scroll.getViewport().setOpaque(true);
        scroll.getViewport().setBackground(AppTheme.FIELD_BG);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    public static <T> JComboBox<T> comboBox() {
        JComboBox<T> combo = new JComboBox<>();
        AppTheme.styleCombo(combo);
        combo.setPreferredSize(new Dimension(100, 38));
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setBorder(new EmptyBorder(8, 12, 8, 12));
                label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                if (isSelected) {
                    label.setBackground(AppTheme.ACCENT);
                    label.setForeground(Color.WHITE);
                } else {
                    label.setBackground(Color.WHITE);
                    label.setForeground(AppTheme.DARK_TEXT);
                }
                return label;
            }
        });
        return combo;
    }

    public static JTable table() {
        JTable table = new JTable();
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setForeground(AppTheme.DARK_TEXT);
        table.setBackground(Color.WHITE);
        table.setSelectionBackground(new Color(232, 239, 255));
        table.setSelectionForeground(AppTheme.DARK_TEXT);
        table.setGridColor(new Color(232, 238, 248));
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setForeground(new Color(84, 99, 123));
        header.setBackground(new Color(246, 249, 255));
        header.setPreferredSize(new Dimension(100, 38));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                if (!isSelected) {
                    component.setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 252, 255));
                    component.setForeground(AppTheme.DARK_TEXT);
                }
                return component;
            }
        });
        return table;
    }

    public static JScrollPane scroll(Component component) {
        JScrollPane scroll = new JScrollPane(component);
        scroll.setBorder(BorderFactory.createLineBorder(AppTheme.BORDER));
        scroll.setOpaque(true);
        scroll.setBackground(AppTheme.FIELD_BG);
        scroll.getViewport().setOpaque(true);
        scroll.getViewport().setBackground(AppTheme.FIELD_BG);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    public static JScrollPane formScroll(Component component) {
        JScrollPane scroll = new JScrollPane(component);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    public static JPanel transparent() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        return panel;
    }

    public static JPanel transparent(LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setOpaque(false);
        return panel;
    }
}
