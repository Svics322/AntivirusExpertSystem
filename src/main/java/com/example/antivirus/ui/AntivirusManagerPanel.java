package com.example.antivirus.ui;

import com.example.antivirus.db.AntivirusRepository;
import com.example.antivirus.model.Antivirus;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AntivirusManagerPanel extends JPanel {
    private final AntivirusRepository repository = new AntivirusRepository();
    private final Runnable onDataChanged;
    private final DefaultTableModel model = new DefaultTableModel(new Object[]{"ID", "Назва", "Активний"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = UiFactory.table();
    private final JTextField search = UiFactory.textField("Пошук антивірусу...");
    private final JTextField idField = UiFactory.textField("Напр.: defender");
    private final JTextField nameField = UiFactory.textField("Назва рішення");
    private final JTextArea shortAdviceArea = UiFactory.textArea();
    private final JTextArea fullAdviceArea = UiFactory.textArea();
    private final JCheckBox activeBox = new JCheckBox("Активний запис", true);

    public AntivirusManagerPanel(Runnable onDataChanged) {
        this.onDataChanged = onDataChanged;
        setOpaque(false);
        setLayout(new BorderLayout(18, 0));
        setBorder(new EmptyBorder(18, 0, 0, 0));
        add(createListCard(), BorderLayout.CENTER);
        add(createEditorCard(), BorderLayout.EAST);
        reload();
    }

    private JComponent createListCard() {
        RoundedPanel card = new RoundedPanel(AppTheme.CARD, 30);
        card.setLayout(new BorderLayout(0, 14));
        card.setBorder(new EmptyBorder(22, 22, 22, 22));

        JPanel top = UiFactory.transparent(new BorderLayout(12, 0));
        JPanel title = UiFactory.transparent(new BorderLayout(0, 2));
        title.add(UiFactory.title("Антивірусні рішення", 22), BorderLayout.NORTH);
        title.add(UiFactory.smallMuted("Додавання, оновлення, видалення та пошук записів SQLite"), BorderLayout.SOUTH);
        top.add(title, BorderLayout.CENTER);
        JButton refresh = UiFactory.secondaryButton("Оновити");
        refresh.addActionListener(e -> reload());
        top.add(refresh, BorderLayout.EAST);
        card.add(top, BorderLayout.NORTH);

        JPanel searchPanel = UiFactory.transparent(new BorderLayout(10, 0));
        searchPanel.add(search, BorderLayout.CENTER);
        JButton searchBtn = UiFactory.primaryButton("Пошук");
        searchBtn.addActionListener(e -> reload());
        searchPanel.add(searchBtn, BorderLayout.EAST);
        card.add(searchPanel, BorderLayout.SOUTH);

        table.setModel(model);
        table.getSelectionModel().addListSelectionListener(e -> selectRow());
        card.add(UiFactory.scroll(table), BorderLayout.CENTER);
        return card;
    }

    private JComponent createEditorCard() {
        RoundedPanel card = new RoundedPanel(AppTheme.CARD, 30);
        card.setLayout(new BorderLayout(0, 12));
        card.setPreferredSize(new Dimension(440, 100));
        card.setBorder(new EmptyBorder(22, 22, 22, 22));

        JPanel title = UiFactory.transparent(new BorderLayout(0, 2));
        title.add(UiFactory.title("Редагування рішення", 22), BorderLayout.NORTH);
        title.add(UiFactory.smallMuted("Зміни одразу зберігаються у SQLite"), BorderLayout.SOUTH);
        card.add(title, BorderLayout.NORTH);

        JPanel form = UiFactory.transparent();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        addField(form, "ID", idField);
        addField(form, "Назва", nameField);
        addField(form, "Коротка порада", UiFactory.textAreaScroll(shortAdviceArea));
        addField(form, "Повна порада", UiFactory.textAreaScroll(fullAdviceArea));
        activeBox.setOpaque(false);
        activeBox.setForeground(AppTheme.DARK_TEXT);
        form.add(activeBox);
        card.add(form, BorderLayout.CENTER);

        JPanel buttons = UiFactory.transparent(new GridLayout(2, 2, 10, 10));
        JButton save = UiFactory.primaryButton("Додати / оновити");
        save.addActionListener(e -> save());
        JButton delete = UiFactory.dangerButton("Видалити");
        delete.addActionListener(e -> delete());
        JButton clear = UiFactory.secondaryButton("Очистити");
        clear.addActionListener(e -> clear());
        JButton seed = UiFactory.secondaryButton("Пошук усіх");
        seed.addActionListener(e -> { search.setText(""); reload(); });
        buttons.add(save);
        buttons.add(delete);
        buttons.add(clear);
        buttons.add(seed);
        card.add(buttons, BorderLayout.SOUTH);
        return card;
    }

    private void addField(JPanel form, String label, JComponent component) {
        JLabel l = UiFactory.title(label, 13);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        component.setAlignmentX(Component.LEFT_ALIGNMENT);
        component.setMaximumSize(new Dimension(Integer.MAX_VALUE, component instanceof JScrollPane ? 92 : 40));
        form.add(l);
        form.add(Box.createVerticalStrut(6));
        form.add(component);
        form.add(Box.createVerticalStrut(10));
    }

    private void reload() {
        model.setRowCount(0);
        List<Antivirus> items = repository.search(search.getText());
        for (Antivirus item : items) {
            model.addRow(new Object[]{item.id(), item.name(), item.active() ? "так" : "ні"});
        }
    }

    private void selectRow() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        String id = String.valueOf(model.getValueAt(row, 0));
        repository.search(id).stream().filter(item -> item.id().equals(id)).findFirst().ifPresent(item -> {
            idField.setText(item.id());
            nameField.setText(item.name());
            shortAdviceArea.setText(item.shortAdvice());
            fullAdviceArea.setText(item.fullAdvice());
            activeBox.setSelected(item.active());
        });
    }

    private void save() {
        if (idField.getText().isBlank() || nameField.getText().isBlank()) {
            showError("ID і назва обов'язкові.");
            return;
        }
        repository.save(new Antivirus(
                idField.getText().trim(),
                nameField.getText().trim(),
                shortAdviceArea.getText().trim(),
                fullAdviceArea.getText().trim(),
                activeBox.isSelected()
        ));
        reload();
        onDataChanged.run();
    }

    private void delete() {
        if (idField.getText().isBlank()) return;
        int answer = JOptionPane.showConfirmDialog(this, "Видалити антивірус і пов'язані правила?", "Підтвердження", JOptionPane.YES_NO_OPTION);
        if (answer == JOptionPane.YES_OPTION) {
            repository.delete(idField.getText().trim());
            clear();
            reload();
            onDataChanged.run();
        }
    }

    private void clear() {
        idField.setText("");
        nameField.setText("");
        shortAdviceArea.setText("");
        fullAdviceArea.setText("");
        activeBox.setSelected(true);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Помилка", JOptionPane.ERROR_MESSAGE);
    }
}
