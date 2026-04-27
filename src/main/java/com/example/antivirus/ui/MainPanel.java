package com.example.antivirus.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainPanel extends BackgroundPanel {
    private final ConsultationPanel consultationPanel = new ConsultationPanel();
    private final AntivirusManagerPanel antivirusManagerPanel = new AntivirusManagerPanel(this::refreshConsultation);
    private final CriteriaManagerPanel criteriaManagerPanel = new CriteriaManagerPanel(this::refreshConsultation);
    private final RuleManagerPanel ruleManagerPanel = new RuleManagerPanel(this::refreshConsultation);

    public MainPanel() {
        setLayout(new BorderLayout(0, 18));
        setBorder(new EmptyBorder(22, 24, 24, 24));
        add(createHeader(), BorderLayout.NORTH);
        add(createTabs(), BorderLayout.CENTER);
    }

    private JComponent createHeader() {
        RoundedPanel header = new RoundedPanel(new Color(255, 255, 255, 24), 34);
        header.setLayout(new BorderLayout());
        header.setBorder(new EmptyBorder(24, 28, 24, 28));
        header.setPreferredSize(new Dimension(100, 104));

        JPanel text = UiFactory.transparent();
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Вибір антивірусу для комп'ютерної мережі");
        title.setFont(new Font("Segoe UI", Font.BOLD, 31));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Експертна система аналізує умови використання та формує рекомендацію з бази знань");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitle.setForeground(new Color(214, 226, 255));
        subtitle.setBorder(new EmptyBorder(8, 0, 0, 0));
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        text.add(title);
        text.add(subtitle);
        header.add(text, BorderLayout.CENTER);

        return header;
    }

    private JTabbedPane createTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabs.addTab("  Консультація  ", consultationPanel);
        tabs.addTab("  Антивіруси  ", antivirusManagerPanel);
        tabs.addTab("  Критерії  ", criteriaManagerPanel);
        tabs.addTab("  Правила БЗ  ", ruleManagerPanel);
        return tabs;
    }

    public void refreshConsultation() {
        consultationPanel.reload();
        ruleManagerPanel.reloadExternalData();
    }
}
