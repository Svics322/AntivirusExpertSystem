package com.example.antivirus.ui;

import com.example.antivirus.db.AntivirusRepository;
import com.example.antivirus.db.CriterionRepository;
import com.example.antivirus.db.RuleRepository;
import com.example.antivirus.expert.InferenceEngine;
import com.example.antivirus.model.AnswerOption;
import com.example.antivirus.model.EvaluationResult;
import com.example.antivirus.model.Question;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ConsultationPanel extends JPanel {
    private final CriterionRepository criterionRepository = new CriterionRepository();
    private final AntivirusRepository antivirusRepository = new AntivirusRepository();
    private final RuleRepository ruleRepository = new RuleRepository();
    private final InferenceEngine engine = new InferenceEngine();

    private final JPanel formPanel = UiFactory.transparent();
    private final Map<String, JComboBox<AnswerOption>> controls = new LinkedHashMap<>();
    private final DefaultTableModel resultModel = new DefaultTableModel(new Object[]{"Антивірус", "Оцінка", "Порада"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JLabel bestLabel = new JLabel("Очікується консультація");
    private final JLabel summaryLabel = new JLabel("Заповніть параметри мережі та натисніть кнопку отримання поради.");
    private final JProgressBar confidenceBar = new JProgressBar(0, 100);
    private final JTextArea explanationArea = UiFactory.textArea();

    public ConsultationPanel() {
        setOpaque(false);
        setLayout(new BorderLayout(18, 0));
        setBorder(new EmptyBorder(18, 0, 0, 0));
        add(createFormCard(), BorderLayout.WEST);
        add(createResultCard(), BorderLayout.CENTER);
        reload();
    }

    public void reload() {
        controls.clear();
        formPanel.removeAll();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        List<Question> questions = criterionRepository.findActiveQuestions();
        for (Question question : questions) {
            JPanel group = UiFactory.transparent(new BorderLayout(0, 7));
            group.setBorder(new EmptyBorder(7, 0, 9, 0));
            group.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel label = UiFactory.title(question.text(), 13);
            JComboBox<AnswerOption> comboBox = UiFactory.comboBox();
            for (AnswerOption option : question.options()) {
                comboBox.addItem(option);
            }
            controls.put(question.id(), comboBox);

            group.add(label, BorderLayout.NORTH);
            group.add(comboBox, BorderLayout.CENTER);
            formPanel.add(group);
        }

        formPanel.revalidate();
        formPanel.repaint();
    }

    private JComponent createFormCard() {
        RoundedPanel card = new RoundedPanel(AppTheme.CARD, 30);
        card.setLayout(new BorderLayout(0, 14));
        card.setPreferredSize(new Dimension(440, 620));
        card.setBorder(new EmptyBorder(22, 22, 22, 22));

        JPanel top = UiFactory.transparent(new BorderLayout(0, 2));
        top.add(UiFactory.title("Параметри мережі", 22), BorderLayout.NORTH);
        top.add(UiFactory.smallMuted("Оберіть умови, за якими експерт формує пораду"), BorderLayout.SOUTH);
        card.add(top, BorderLayout.NORTH);

        JScrollPane scroll = UiFactory.formScroll(formPanel);
        card.add(scroll, BorderLayout.CENTER);

        JPanel buttons = UiFactory.transparent(new GridLayout(1, 2, 10, 0));
        JButton reset = UiFactory.secondaryButton("Очистити");
        reset.addActionListener(e -> reset());
        JButton calculate = UiFactory.primaryButton("Отримати пораду");
        calculate.addActionListener(e -> calculate());
        buttons.add(reset);
        buttons.add(calculate);
        card.add(buttons, BorderLayout.SOUTH);
        return card;
    }

    private JComponent createResultCard() {
        RoundedPanel card = new RoundedPanel(AppTheme.CARD, 30);
        card.setLayout(new BorderLayout(0, 16));
        card.setBorder(new EmptyBorder(22, 22, 22, 22));

        RoundedPanel hero = new RoundedPanel(new Color(238, 244, 255), 26);
        hero.setLayout(new BorderLayout(18, 0));
        hero.setBorder(new EmptyBorder(18, 20, 18, 20));

        JPanel text = UiFactory.transparent();
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel small = new JLabel("Найкраща рекомендація");
        small.setForeground(new Color(57, 83, 179));
        small.setFont(new Font("Segoe UI", Font.BOLD, 12));
        bestLabel.setFont(new Font("Segoe UI", Font.BOLD, 23));
        bestLabel.setForeground(AppTheme.DARK_TEXT);
        summaryLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        summaryLabel.setForeground(AppTheme.MUTED);
        text.add(small);
        text.add(Box.createVerticalStrut(5));
        text.add(bestLabel);
        text.add(Box.createVerticalStrut(3));
        text.add(summaryLabel);
        hero.add(text, BorderLayout.CENTER);

        confidenceBar.setStringPainted(true);
        confidenceBar.setValue(0);
        confidenceBar.setString("0% відповідності");
        confidenceBar.setPreferredSize(new Dimension(230, 28));
        hero.add(confidenceBar, BorderLayout.EAST);
        card.add(hero, BorderLayout.NORTH);

        JTable table = UiFactory.table();
        table.setModel(resultModel);
        table.getColumnModel().getColumn(0).setPreferredWidth(250);
        table.getColumnModel().getColumn(1).setPreferredWidth(90);
        table.getColumnModel().getColumn(2).setPreferredWidth(520);
        card.add(UiFactory.scroll(table), BorderLayout.CENTER);

        RoundedPanel explanation = new RoundedPanel(new Color(248, 251, 255), 24);
        explanation.setLayout(new BorderLayout(0, 8));
        explanation.setBorder(new EmptyBorder(14, 16, 14, 16));
        explanation.setPreferredSize(new Dimension(100, 190));
        explanation.add(UiFactory.title("Пояснення спрацьованих правил", 16), BorderLayout.NORTH);
        explanationArea.setEditable(false);
        explanationArea.setText("Тут буде пояснення, які правила бази знань спрацювали і чому обрано конкретний антивірус.");
        JScrollPane explanationScroll = UiFactory.textAreaScroll(explanationArea);
        explanation.add(explanationScroll, BorderLayout.CENTER);
        card.add(explanation, BorderLayout.SOUTH);
        return card;
    }

    private void calculate() {
        List<EvaluationResult> results = engine.evaluate(
                antivirusRepository.findActive(),
                ruleRepository.findActive(),
                collectAnswers()
        );

        resultModel.setRowCount(0);
        for (EvaluationResult result : results) {
            resultModel.addRow(new Object[]{
                    result.antivirus().name(),
                    result.percentage() + "%",
                    result.antivirus().shortAdvice()
            });
        }

        if (!results.isEmpty()) {
            EvaluationResult best = results.get(0);
            bestLabel.setText(best.antivirus().name());
            summaryLabel.setText(best.antivirus().shortAdvice());
            confidenceBar.setValue(best.percentage());
            confidenceBar.setString(best.percentage() + "% відповідності");

            StringBuilder text = new StringBuilder();
            text.append(best.antivirus().fullAdvice()).append("\n\n");
            text.append("Пояснення роботи бази знань:\n");
            if (best.evidence().isEmpty()) {
                text.append("- Повних збігів правил не знайдено. Варто уточнити параметри мережі або звернутися до фахівця.\n");
            } else {
                for (String item : best.evidence()) {
                    text.append("- ").append(item).append("\n");
                }
            }
            text.append("\nУвага: програма не замінює експерта, а лише моделює його попередню консультацію.");
            explanationArea.setText(text.toString());
        }
    }

    private Map<String, String> collectAnswers() {
        Map<String, String> answers = new LinkedHashMap<>();
        for (Map.Entry<String, JComboBox<AnswerOption>> entry : controls.entrySet()) {
            AnswerOption selected = (AnswerOption) entry.getValue().getSelectedItem();
            if (selected != null) {
                answers.put(entry.getKey(), selected.id());
            }
        }
        return answers;
    }

    private void reset() {
        for (JComboBox<AnswerOption> combo : controls.values()) {
            if (combo.getItemCount() > 0) {
                combo.setSelectedIndex(0);
            }
        }
        resultModel.setRowCount(0);
        bestLabel.setText("Очікується консультація");
        summaryLabel.setText("Заповніть параметри мережі та натисніть кнопку отримання поради.");
        confidenceBar.setValue(0);
        confidenceBar.setString("0% відповідності");
        explanationArea.setText("Тут буде пояснення, які правила бази знань спрацювали і чому обрано конкретний антивірус.");
    }
}
