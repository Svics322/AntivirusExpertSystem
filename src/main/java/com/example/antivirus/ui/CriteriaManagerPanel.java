package com.example.antivirus.ui;

import com.example.antivirus.db.CriterionRepository;
import com.example.antivirus.model.Criterion;
import com.example.antivirus.model.CriterionOption;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CriteriaManagerPanel extends JPanel {
    private final CriterionRepository repository = new CriterionRepository();
    private final Runnable onDataChanged;
    private final DefaultTableModel criteriaModel = new DefaultTableModel(new Object[]{"ID", "Критерій", "Порядок", "Активний"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final DefaultTableModel optionsModel = new DefaultTableModel(new Object[]{"ID", "Варіант", "Порядок", "Активний"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable criteriaTable = UiFactory.table();
    private final JTable optionsTable = UiFactory.table();
    private final JTextField criteriaSearch = UiFactory.textField("Пошук критеріїв...");
    private final JTextField optionSearch = UiFactory.textField("Пошук варіантів...");

    private final JTextField criterionId = UiFactory.textField("Напр.: scale");
    private final JTextField criterionTitle = UiFactory.textField("Назва критерію");
    private final JTextField criterionOrder = UiFactory.textField("10");
    private final JCheckBox criterionActive = new JCheckBox("Активний", true);

    private final JTextField optionId = UiFactory.textField("Напр.: small");
    private final JTextField optionLabel = UiFactory.textField("Текст варіанта");
    private final JTextField optionOrder = UiFactory.textField("10");
    private final JCheckBox optionActive = new JCheckBox("Активний", true);

    public CriteriaManagerPanel(Runnable onDataChanged) {
        this.onDataChanged = onDataChanged;
        setOpaque(false);
        setLayout(new GridLayout(1, 2, 18, 0));
        setBorder(new EmptyBorder(18, 0, 0, 0));
        add(createCriteriaCard());
        add(createOptionsCard());
        reloadCriteria();
    }

    private JComponent createCriteriaCard() {
        RoundedPanel card = new RoundedPanel(AppTheme.CARD, 30);
        card.setLayout(new BorderLayout(0, 12));
        card.setBorder(new EmptyBorder(22, 22, 22, 22));

        JPanel top = UiFactory.transparent(new BorderLayout(0, 2));
        top.add(UiFactory.title("Критерії вибору", 22), BorderLayout.NORTH);
        top.add(UiFactory.smallMuted("CRUD і пошук критеріїв експертної системи"), BorderLayout.SOUTH);
        card.add(top, BorderLayout.NORTH);

        criteriaTable.setModel(criteriaModel);
        criteriaTable.getSelectionModel().addListSelectionListener(e -> selectCriterion());
        card.add(UiFactory.scroll(criteriaTable), BorderLayout.CENTER);

        JPanel bottom = UiFactory.transparent();
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        JPanel searchRow = UiFactory.transparent(new BorderLayout(8, 0));
        searchRow.add(criteriaSearch, BorderLayout.CENTER);
        JButton searchBtn = UiFactory.primaryButton("Пошук");
        searchBtn.addActionListener(e -> reloadCriteria());
        searchRow.add(searchBtn, BorderLayout.EAST);
        bottom.add(searchRow);
        bottom.add(Box.createVerticalStrut(12));

        bottom.add(formRow("ID", criterionId));
        bottom.add(formRow("Назва", criterionTitle));
        bottom.add(formRow("Порядок", criterionOrder));
        criterionActive.setOpaque(false);
        criterionActive.setForeground(AppTheme.DARK_TEXT);
        bottom.add(criterionActive);
        bottom.add(Box.createVerticalStrut(10));

        JPanel buttons = UiFactory.transparent(new GridLayout(1, 3, 8, 0));
        JButton save = UiFactory.primaryButton("Зберегти");
        save.addActionListener(e -> saveCriterion());
        JButton delete = UiFactory.dangerButton("Видалити");
        delete.addActionListener(e -> deleteCriterion());
        JButton clear = UiFactory.secondaryButton("Очистити");
        clear.addActionListener(e -> clearCriterion());
        buttons.add(save);
        buttons.add(delete);
        buttons.add(clear);
        bottom.add(buttons);
        card.add(bottom, BorderLayout.SOUTH);
        return card;
    }

    private JComponent createOptionsCard() {
        RoundedPanel card = new RoundedPanel(AppTheme.CARD, 30);
        card.setLayout(new BorderLayout(0, 12));
        card.setBorder(new EmptyBorder(22, 22, 22, 22));

        JPanel top = UiFactory.transparent(new BorderLayout(0, 2));
        top.add(UiFactory.title("Варіанти відповідей", 22), BorderLayout.NORTH);
        top.add(UiFactory.smallMuted("Оберіть критерій зліва, щоб редагувати його значення"), BorderLayout.SOUTH);
        card.add(top, BorderLayout.NORTH);

        optionsTable.setModel(optionsModel);
        optionsTable.getSelectionModel().addListSelectionListener(e -> selectOption());
        card.add(UiFactory.scroll(optionsTable), BorderLayout.CENTER);

        JPanel bottom = UiFactory.transparent();
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        JPanel searchRow = UiFactory.transparent(new BorderLayout(8, 0));
        searchRow.add(optionSearch, BorderLayout.CENTER);
        JButton searchBtn = UiFactory.primaryButton("Пошук");
        searchBtn.addActionListener(e -> reloadOptions());
        searchRow.add(searchBtn, BorderLayout.EAST);
        bottom.add(searchRow);
        bottom.add(Box.createVerticalStrut(12));

        bottom.add(formRow("ID", optionId));
        bottom.add(formRow("Текст", optionLabel));
        bottom.add(formRow("Порядок", optionOrder));
        optionActive.setOpaque(false);
        optionActive.setForeground(AppTheme.DARK_TEXT);
        bottom.add(optionActive);
        bottom.add(Box.createVerticalStrut(10));

        JPanel buttons = UiFactory.transparent(new GridLayout(1, 3, 8, 0));
        JButton save = UiFactory.primaryButton("Зберегти");
        save.addActionListener(e -> saveOption());
        JButton delete = UiFactory.dangerButton("Видалити");
        delete.addActionListener(e -> deleteOption());
        JButton clear = UiFactory.secondaryButton("Очистити");
        clear.addActionListener(e -> clearOption());
        buttons.add(save);
        buttons.add(delete);
        buttons.add(clear);
        bottom.add(buttons);
        card.add(bottom, BorderLayout.SOUTH);
        return card;
    }

    private JPanel formRow(String label, JTextField field) {
        JPanel row = UiFactory.transparent(new BorderLayout(8, 0));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        JLabel l = UiFactory.title(label, 13);
        l.setPreferredSize(new Dimension(80, 38));
        row.add(l, BorderLayout.WEST);
        row.add(field, BorderLayout.CENTER);
        row.setBorder(new EmptyBorder(0, 0, 8, 0));
        return row;
    }

    private void reloadCriteria() {
        criteriaModel.setRowCount(0);
        for (Criterion item : repository.searchCriteria(criteriaSearch.getText())) {
            criteriaModel.addRow(new Object[]{item.id(), item.title(), item.sortOrder(), item.active() ? "так" : "ні"});
        }
    }

    private void reloadOptions() {
        optionsModel.setRowCount(0);
        String selectedCriterion = criterionId.getText().trim();
        if (selectedCriterion.isBlank()) return;
        for (CriterionOption item : repository.searchOptions(selectedCriterion, optionSearch.getText())) {
            optionsModel.addRow(new Object[]{item.id(), item.label(), item.sortOrder(), item.active() ? "так" : "ні"});
        }
    }

    private void selectCriterion() {
        int row = criteriaTable.getSelectedRow();
        if (row < 0) return;
        criterionId.setText(String.valueOf(criteriaModel.getValueAt(row, 0)));
        criterionTitle.setText(String.valueOf(criteriaModel.getValueAt(row, 1)));
        criterionOrder.setText(String.valueOf(criteriaModel.getValueAt(row, 2)));
        criterionActive.setSelected("так".equals(criteriaModel.getValueAt(row, 3)));
        clearOption();
        reloadOptions();
    }

    private void selectOption() {
        int row = optionsTable.getSelectedRow();
        if (row < 0) return;
        optionId.setText(String.valueOf(optionsModel.getValueAt(row, 0)));
        optionLabel.setText(String.valueOf(optionsModel.getValueAt(row, 1)));
        optionOrder.setText(String.valueOf(optionsModel.getValueAt(row, 2)));
        optionActive.setSelected("так".equals(optionsModel.getValueAt(row, 3)));
    }

    private void saveCriterion() {
        if (criterionId.getText().isBlank() || criterionTitle.getText().isBlank()) return;
        repository.saveCriterion(new Criterion(
                criterionId.getText().trim(),
                criterionTitle.getText().trim(),
                parseInt(criterionOrder.getText(), 0),
                criterionActive.isSelected()
        ));
        reloadCriteria();
        onDataChanged.run();
    }

    private void saveOption() {
        if (criterionId.getText().isBlank() || optionId.getText().isBlank() || optionLabel.getText().isBlank()) return;
        repository.saveOption(new CriterionOption(
                criterionId.getText().trim(),
                optionId.getText().trim(),
                optionLabel.getText().trim(),
                parseInt(optionOrder.getText(), 0),
                optionActive.isSelected()
        ));
        reloadOptions();
        onDataChanged.run();
    }

    private void deleteCriterion() {
        if (criterionId.getText().isBlank()) return;
        int answer = JOptionPane.showConfirmDialog(this, "Видалити критерій, його варіанти та пов'язані умови правил?", "Підтвердження", JOptionPane.YES_NO_OPTION);
        if (answer == JOptionPane.YES_OPTION) {
            repository.deleteCriterion(criterionId.getText().trim());
            clearCriterion();
            reloadCriteria();
            reloadOptions();
            onDataChanged.run();
        }
    }

    private void deleteOption() {
        if (criterionId.getText().isBlank() || optionId.getText().isBlank()) return;
        repository.deleteOption(criterionId.getText().trim(), optionId.getText().trim());
        clearOption();
        reloadOptions();
        onDataChanged.run();
    }

    private void clearCriterion() {
        criterionId.setText("");
        criterionTitle.setText("");
        criterionOrder.setText("0");
        criterionActive.setSelected(true);
    }

    private void clearOption() {
        optionId.setText("");
        optionLabel.setText("");
        optionOrder.setText("0");
        optionActive.setSelected(true);
    }

    private int parseInt(String value, int fallback) {
        try { return Integer.parseInt(value.trim()); } catch (Exception ex) { return fallback; }
    }
}
