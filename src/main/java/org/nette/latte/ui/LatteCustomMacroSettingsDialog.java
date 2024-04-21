package org.nette.latte.ui;

import com.intellij.openapi.project.Project;
import com.intellij.ui.table.TableView;
import org.nette.latte.config.LatteConfiguration;
import org.nette.latte.settings.LatteTagSettings;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.*;

public class LatteCustomMacroSettingsDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textVarName;
    private JComboBox<String> macroType;
    private JCheckBox checkBoxAllowedModifiers;
    private JCheckBox multiLineOnlyUsedCheckBox;
    private JCheckBox deprecatedCheckBox;
    private JTextField deprecatedMessageTextField;
    private JTextField argumentsTextField;
    private LatteTagSettings latteTagSettings;
    private final TableView<LatteTagSettings> tableView;
    private final Project project;

    public LatteCustomMacroSettingsDialog(TableView<LatteTagSettings> tableView, Project project) {
        this.tableView = tableView;
        this.project = project;

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        this.textVarName.getDocument().addDocumentListener(new ChangeDocumentListener());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        attachComboBoxValues();

        this.deprecatedCheckBox.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                deprecatedMessageTextField.setEnabled(deprecatedCheckBox.isSelected());
            }
        });
        deprecatedMessageTextField.setEnabled(false);
    }

    public LatteCustomMacroSettingsDialog(TableView<LatteTagSettings> tableView, Project project, LatteTagSettings latteTagSettings) {
        this(tableView, project);

        this.latteTagSettings = latteTagSettings;
        this.textVarName.setText(latteTagSettings.getMacroName());
        this.macroType.getModel().setSelectedItem(latteTagSettings.getMacroType());
        this.argumentsTextField.setText(latteTagSettings.getArguments());
        this.checkBoxAllowedModifiers.setSelected(latteTagSettings.isAllowedModifiers());
        this.multiLineOnlyUsedCheckBox.setSelected(latteTagSettings.isMultiLine());
        this.deprecatedCheckBox.setSelected(latteTagSettings.isDeprecated());
        this.deprecatedMessageTextField.setText(latteTagSettings.getDeprecatedMessage());

        deprecatedMessageTextField.setEnabled(latteTagSettings.isDeprecated());

        attachComboBoxValues();
    }

    private void attachComboBoxValues() {
        LatteTagSettings.Type[] values = LatteTagSettings.Type.values();
        if (macroType.getItemCount() == values.length) {
            return;
        }

        for(LatteTagSettings.Type type: values) {
            macroType.addItem(type.toString());
        }
    }

    private void onOK() {
        LatteTagSettings settings = new LatteTagSettings(
                this.textVarName.getText(),
                LatteTagSettings.Type.valueOf((String) this.macroType.getSelectedItem())
        );
        settings.setVendor(LatteConfiguration.Vendor.CUSTOM);

        if (this.latteTagSettings != null) {
            int row = this.tableView.getSelectedRows()[0];
            this.tableView.getListTableModel().removeRow(row);
            this.tableView.getListTableModel().insertRow(row, settings);
            this.tableView.setRowSelectionInterval(row, row);
        } else {
            int row = this.tableView.getRowCount();
            this.tableView.getListTableModel().addRow(settings);
            this.tableView.setRowSelectionInterval(row, row);
        }

        settings.setArguments(this.argumentsTextField.getText());
        settings.setAllowedModifiers(this.checkBoxAllowedModifiers.isSelected());
        settings.setMultiLine(this.multiLineOnlyUsedCheckBox.isSelected());
        settings.setDeprecated(this.deprecatedCheckBox.isSelected());
        settings.setDeprecatedMessage(this.deprecatedMessageTextField.getText());
    }

    private void setOkState() {
        this.buttonOK.setEnabled(
            this.textVarName.getText().length() > 0
        );
    }

    private class ChangeDocumentListener implements DocumentListener {
        @Override
        public void insertUpdate(DocumentEvent e) {
            setOkState();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            setOkState();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            setOkState();
        }
    }

    private void onCancel() {
        dispose();
    }

}
