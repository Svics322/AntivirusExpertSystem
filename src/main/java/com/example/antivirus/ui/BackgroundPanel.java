package com.example.antivirus.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;

public class BackgroundPanel extends JPanel {
    public BackgroundPanel() {
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        GradientPaint gradient = new GradientPaint(0, 0, AppTheme.BG_1, w, h, AppTheme.BG_2);
        g2.setPaint(gradient);
        g2.fillRect(0, 0, w, h);

        paintBlob(g2, w * 0.08, h * 0.12, 420, new Color(92, 124, 255, 46));
        paintBlob(g2, w * 0.78, h * 0.10, 360, new Color(25, 211, 197, 38));
        paintBlob(g2, w * 0.60, h * 0.78, 520, new Color(122, 92, 255, 34));

        g2.setColor(new Color(255, 255, 255, 14));
        int step = 48;
        for (int x = 0; x < w; x += step) {
            g2.drawLine(x, 0, x, h);
        }
        for (int y = 0; y < h; y += step) {
            g2.drawLine(0, y, w, y);
        }

        g2.dispose();
        super.paintComponent(g);
    }

    private void paintBlob(Graphics2D g2, double x, double y, double size, Color color) {
        g2.setColor(color);
        g2.fill(new Ellipse2D.Double(x - size / 2, y - size / 2, size, size));
    }
}
