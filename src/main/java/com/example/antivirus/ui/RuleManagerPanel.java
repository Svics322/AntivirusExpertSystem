package com.example.antivirus.ui;

import com.example.antivirus.db.AntivirusRepository;
import com.example.antivirus.db.CriterionRepository;
import com.example.antivirus.db.RuleRepository;
import com.example.antivirus.model.Antivirus;
import com.example.antivirus.model.AnswerOption;
import com.example.antivirus.model.Criterion;
import com.example.antivirus.model.Question;
import com.example.antivirus.model.Rule;
import com.example.antivirus.model.RuleCondition;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RuleManagerPanel extends JPanel {
    private final RuleRepository ruleRepository = new RuleRepository();
    private final AntivirusRepository antivirusRepository = new AntivirusRepository();
    private final CriterionRepository criterionRepository = new CriterionRepository();
    private final Runnable onDataChanged;

    private final DefaultTableModel model = new DefaultTableModel(new Object[]{"ID", "Антивірус", "Назва правила", "Вага", "Умови", "Активне"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = UiFactory.table();
    private final JTextField search = UiFactory.textField("Пошук правила...");
    private final JTextField idField = UiFactory.textField("Напр.: R13");
    private final JComboBox<Antivirus> antivirusCombo = UiFactory.comboBox();
    private final JTextField titleField = UiFactory.textField("Назва правила");
    private final JTextField weightField = UiFactory.textField("25");
    private final JTextArea explanationArea = UiFactory.textArea();
    private final JCheckBox activeBox = new JCheckBox("Активне правило", true);
    private final JPanel conditionsPanel = UiFactory.transparent();
    private final Map<String, JComboBox<AnswerOption>> conditionControls = new LinkedHashMap<>();

    private List<Rule> visibleRules = new ArrayList<>();

    public RuleManagerPanel(Runnable onDataChanged) {
        this.onDataChanged = onDataChanged;
        setOpaque(false);
        setLayout(new BorderLayout(18, 0));
        setBorder(new EmptyBorder(18, 0, 0, 0));
        add(createListCard(), BorderLayout.CENTER);
        add(createEditorCard(), BorderLayout.EAST);
        reloadExternalData();
        reload();
    }

    public void reloadExternalData() {
        antivirusCombo.removeAllItems();
        for (Antivirus antivirus : antivirusRepository.findActive()) {
            antivirusCombo.addItem(antivirus);
        }
        buildConditionControls();
    }

    private JComponent createListCard() {
        RoundedPanel card = new RoundedPanel(AppTheme.CARD, 30);
        card.setLayout(new BorderLayout(0, 14));
        card.setBorder(new EmptyBorder(22, 22, 22, 22));

        JPanel top = UiFactory.transparent(new BorderLayout(0, 2));
        top.add(UiFactory.title("Правила бази знань", 22), BorderLayout.NORTH);
        top.add(UiFactory.smallMuted("Продукційні правила експерта зберігаються у SQLite"), BorderLayout.SOUTH);
        card.add(top, BorderLayout.NORTH);

        table.setModel(model);
        table.getSelectionModel().addListSelectionListener(e -> selectRule());
        card.add(UiFactory.scroll(table), BorderLayout.CENTER);

        JPanel bottom = UiFactory.transparent(new BorderLayout(10, 0));
        bottom.add(search, BorderLayout.CENTER);
        JButton searchBtn = UiFactory.primaryButton("Пошук");
        searchBtn.addActionListener(e -> reload());
        bottom.add(searchBtn, BorderLayout.EAST);
        card.add(bottom, BorderLayout.SOUTH);
        return card;
    }

    private JComponent createEditorCard() {
        RoundedPanel card = new RoundedPanel(AppTheme.CARD, 30);
        card.setLayout(new BorderLayout(0, 12));
        card.setPreferredSize(new Dimension(470, 100));
        card.setBorder(new EmptyBorder(22, 22, 22, 22));

        JPanel title = UiFactory.transparent(new BorderLayout(0, 2));
        title.add(UiFactory.title("Редактор правила", 22), BorderLayout.NORTH);
        title.add(UiFactory.smallMuted("Умови правила обираються з критеріїв БД"), BorderLayout.SOUTH);
        card.add(title, BorderLayout.NORTH);

        JPanel form = UiFactory.transparent();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        addField(form, "ID", idField, 40);
        addField(form, "Антивірус", antivirusCombo, 40);
        addField(form, "Назва", titleField, 40);
        addField(form, "Вага", weightField, 40);
        addField(form, "Пояснення", UiFactory.textAreaScroll(explanationArea), 95);
        activeBox.setOpaque(false);
        activeBox.setForeground(AppTheme.DARK_TEXT);
        form.add(activeBox);
        form.add(Box.createVerticalStrut(10));

        JLabel conditionTitle = UiFactory.title("Умови правила", 16);
        conditionTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(conditionTitle);
        form.add(Box.createVerticalStrut(8));
        conditionsPanel.setLayout(new BoxLayout(conditionsPanel, BoxLayout.Y_AXIS));
        form.add(conditionsPanel);
        card.add(UiFactory.formScroll(form), BorderLayout.CENTER);

        JPanel buttons = UiFactory.transparent(new GridLayout(1, 3, 10, 0));
        JButton save = UiFactory.primaryButton("Зберегти");
        save.addActionListener(e -> save());
        JButton delete = UiFactory.dangerButton("Видалити");
        delete.addActionListener(e -> delete());
        JButton clear = UiFactory.secondaryButton("Очистити");
        clear.addActionListener(e -> clear());
        buttons.add(save);
        buttons.add(delete);
        buttons.add(clear);
        card.add(buttons, BorderLayout.SOUTH);
        return card;
    }

    private void addField(JPanel form, String label, JComponent component, int height) {
        JLabel l = UiFactory.title(label, 13);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        component.setAlignmentX(Component.LEFT_ALIGNMENT);
        component.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
        form.add(l);
        form.add(Box.createVerticalStrut(6));
        form.add(component);
        form.add(Box.createVerticalStrut(10));
    }

    private void buildConditionControls() {
        if (conditionsPanel == null) return;
        conditionControls.clear();
        conditionsPanel.removeAll();
        List<Question> questions = criterionRepository.findActiveQuestions();
        for (Question question : questions) {
            JPanel row = UiFactory.transparent(new BorderLayout(8, 0));
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            JLabel label = UiFactory.title(question.text(), 12);
            label.setPreferredSize(new Dimension(160, 38));
            JComboBox<AnswerOption> combo = UiFactory.comboBox();
            combo.addItem(new AnswerOption("", "— не враховувати —"));
            for (AnswerOption option : question.options()) {
                combo.addItem(option);
            }
            conditionControls.put(question.id(), combo);
            row.add(label, BorderLayout.WEST);
            row.add(combo, BorderLayout.CENTER);
            row.setBorder(new EmptyBorder(0, 0, 8, 0));
            conditionsPanel.add(row);
        }
        conditionsPanel.revalidate();
        conditionsPanel.repaint();
    }

    private void reload() {
        model.setRowCount(0);
        visibleRules = ruleRepository.search(search.getText());
        for (Rule rule : visibleRules) {
            model.addRow(new Object[]{rule.id(), rule.antivirusName(), rule.title(), rule.weight(), rule.conditionText(), rule.active() ? "так" : "ні"});
        }
    }

    private void selectRule() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= visibleRules.size()) return;
        Rule rule = visibleRules.get(row);
        idField.setText(rule.id());
        titleField.setText(rule.title());
        weightField.setText(String.valueOf(rule.weight()));
        explanationArea.setText(rule.explanation());
        activeBox.setSelected(rule.active());
        selectAntivirus(rule.antivirusId());
        clearConditions();
        for (RuleCondition condition : rule.conditions()) {
            JComboBox<AnswerOption> combo = conditionControls.get(condition.criterionId());
            if (combo != null) {
                for (int i = 0; i < combo.getItemCount(); i++) {
                    AnswerOption option = combo.getItemAt(i);
                    if (option.id().equals(condition.optionId())) {
                        combo.setSelectedIndex(i);
                        break;
                    }
                }
            }
        }
    }

    private void selectAntivirus(String antivirusId) {
        for (int i = 0; i < antivirusCombo.getItemCount(); i++) {
            Antivirus antivirus = antivirusCombo.getItemAt(i);
            if (antivirus.id().equals(antivirusId)) {
                antivirusCombo.setSelectedIndex(i);
                return;
            }
        }
    }

    private void save() {
        Antivirus selected = (Antivirus) antivirusCombo.getSelectedItem();
        if (selected == null || idField.getText().isBlank() || titleField.getText().isBlank()) return;
        List<RuleCondition> conditions = new ArrayList<>();
        for (Map.Entry<String, JComboBox<AnswerOption>> entry : conditionControls.entrySet()) {
            AnswerOption option = (AnswerOption) entry.getValue().getSelectedItem();
            if (option != null && !option.id().isBlank()) {
                conditions.add(new RuleCondition(entry.getKey(), option.id(), null, null));
            }
        }
        if (conditions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Додайте хоча б одну умову правила.", "Помилка", JOptionPane.ERROR_MESSAGE);
            return;
        }
        ruleRepository.save(new Rule(
                idField.getText().trim(),
                selected.id(),
                selected.name(),
                titleField.getText().trim(),
                parseInt(weightField.getText(), 10),
                explanationArea.getText().trim(),
                activeBox.isSelected(),
                conditions
        ));
        reload();
        onDataChanged.run();
    }

    private void delete() {
        if (idField.getText().isBlank()) return;
        ruleRepository.delete(idField.getText().trim());
        clear();
        reload();
        onDataChanged.run();
    }

    private void clear() {
        idField.setText("");
        titleField.setText("");
        weightField.setText("10");
        explanationArea.setText("");
        activeBox.setSelected(true);
        clearConditions();
    }

    private void clearConditions() {
        for (JComboBox<AnswerOption> combo : conditionControls.values()) {
            if (combo.getItemCount() > 0) combo.setSelectedIndex(0);
        }
    }

    private int parseInt(String value, int fallback) {
        try { return Integer.parseInt(value.trim()); } catch (Exception ex) { return fallback; }
    }
}
