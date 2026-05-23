package medicalcabinet.presentation;

import medicalcabinet.domain.dtos.UserDTO;
import medicalcabinet.presentation.utils.I18nManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminDashboardView extends JFrame implements IAdminView {
    private AdminPresenter presenter;
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> roleFilterCombo;
    private JButton btnUpdate;
    private JButton exportBtn;
    private JButton notifyBtn;
    private JButton addBtn;
    private JButton deleteBtn;
    private JPanel topPanel;

    public AdminDashboardView() {
        setSize(800, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        add(topPanel, BorderLayout.NORTH);

        roleFilterCombo = new JComboBox<>(new String[]{"ALL", "ADMINISTRATOR", "DOCTOR", "ASSISTANT", "PATIENT"});
        topPanel.add(roleFilterCombo);

        String[] columns = {"ID", "Username", "Role", "Email"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        userTable = new JTable(tableModel);
        add(new JScrollPane(userTable), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        exportBtn = new JButton();
        notifyBtn = new JButton();
        addBtn = new JButton();
        deleteBtn = new JButton();
        btnUpdate = new JButton();

        bottomPanel.add(exportBtn);
        bottomPanel.add(notifyBtn);
        bottomPanel.add(addBtn);
        bottomPanel.add(deleteBtn);
        bottomPanel.add(btnUpdate);
        add(bottomPanel, BorderLayout.SOUTH);

        roleFilterCombo.addActionListener(e -> {
            if (presenter != null) {
                presenter.onFilterRoleChanged((String) roleFilterCombo.getSelectedItem());
            }
        });

        exportBtn.addActionListener(e -> {
            if (presenter != null) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle(I18nManager.getString("admin.export.title", "Salvează lista ca CSV"));
                int userSelection = fileChooser.showSaveDialog(this);
                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    String path = fileChooser.getSelectedFile().getAbsolutePath();
                    if (!path.toLowerCase().endsWith(".csv")) path += ".csv";
                    presenter.onExportCsvClicked(path);
                }
            }
        });

        btnUpdate.addActionListener(e -> {
            if (presenter != null) {
                UserDTO selectedUser = getSelectedUser();
                if (selectedUser == null) {
                    showMessage(I18nManager.getString("admin.select.error", "Select a user."));
                    return;
                }

                JTextField usernameField = new JTextField(selectedUser.getUsername(), 10);
                JPasswordField passwordField = new JPasswordField(10);
                JComboBox<String> roleCombo = new JComboBox<>(new String[]{"ADMINISTRATOR", "DOCTOR", "ASSISTANT", "PATIENT"});
                roleCombo.setSelectedItem(selectedUser.getRole().name());
                JTextField emailField = new JTextField(selectedUser.getEmail(), 15);

                JPanel myPanel = new JPanel(new GridLayout(4, 2, 5, 5));
                myPanel.add(new JLabel(I18nManager.getString("admin.username", "Username:")));
                myPanel.add(usernameField);
                myPanel.add(new JLabel(I18nManager.getString("admin.pwd.new", "New Password:")));
                myPanel.add(passwordField);
                myPanel.add(new JLabel(I18nManager.getString("admin.role", "Role:")));
                myPanel.add(roleCombo);
                myPanel.add(new JLabel(I18nManager.getString("admin.email", "Email:")));
                myPanel.add(emailField);

                int result = JOptionPane.showConfirmDialog(this, myPanel,
                        I18nManager.getString("admin.update.title", "Update User"), JOptionPane.OK_CANCEL_OPTION);

                if (result == JOptionPane.OK_OPTION) {
                    presenter.onUpdateUserClicked(
                            usernameField.getText(),
                            new String(passwordField.getPassword()),
                            (String) roleCombo.getSelectedItem(),
                            emailField.getText()
                    );
                }
            }
        });

        addBtn.addActionListener(e -> {
            if (presenter != null) {
                JTextField usernameField = new JTextField(10);
                JPasswordField passwordField = new JPasswordField(10);
                JComboBox<String> roleCombo = new JComboBox<>(new String[]{"ADMINISTRATOR", "DOCTOR", "ASSISTANT", "PATIENT"});
                JTextField emailField = new JTextField(15);

                JPanel myPanel = new JPanel(new GridLayout(4, 2, 5, 5));
                myPanel.add(new JLabel(I18nManager.getString("admin.username", "Username:")));
                myPanel.add(usernameField);
                myPanel.add(new JLabel(I18nManager.getString("admin.password", "Password:")));
                myPanel.add(passwordField);
                myPanel.add(new JLabel(I18nManager.getString("admin.role", "Role:")));
                myPanel.add(roleCombo);
                myPanel.add(new JLabel(I18nManager.getString("admin.email", "Email:")));
                myPanel.add(emailField);

                int result = JOptionPane.showConfirmDialog(null, myPanel,
                        I18nManager.getString("admin.add.title", "Add User"), JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    presenter.onAddUserClicked(
                            usernameField.getText(),
                            new String(passwordField.getPassword()),
                            (String) roleCombo.getSelectedItem(),
                            emailField.getText()
                    );
                }
            }
        });

        notifyBtn.addActionListener(e -> { if (presenter != null) presenter.onNotifyClicked(); });
        deleteBtn.addActionListener(e -> { if (presenter != null) presenter.onDeleteClicked(); });

        updateUITexts();
        setLocationRelativeTo(null);
    }

    private void updateUITexts() {
        setTitle(I18nManager.getString("admin.title", "Medical Cabinet - Administrator Dashboard"));
        topPanel.setBorder(BorderFactory.createTitledBorder(I18nManager.getString("admin.filter.title", "User Filtering")));
        exportBtn.setText(I18nManager.getString("admin.btn.export", "Export CSV"));
        notifyBtn.setText(I18nManager.getString("admin.btn.notify", "Notify User"));
        addBtn.setText(I18nManager.getString("admin.btn.add", "Add User"));
        deleteBtn.setText(I18nManager.getString("admin.btn.delete", "Delete User"));
        btnUpdate.setText(I18nManager.getString("admin.btn.update", "Update User"));
    }

    @Override
    public void setPresenter(AdminPresenter presenter) { this.presenter = presenter; }

    @Override
    public void displayUsers(List<UserDTO> users) {
        tableModel.setRowCount(0);
        for (UserDTO u : users) {
            tableModel.addRow(new Object[]{u.getId(), u.getUsername(), u.getRole(), u.getEmail()});
        }
    }

    @Override
    public UserDTO getSelectedUser() {
        int row = userTable.getSelectedRow();
        if (row >= 0) {
            int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
            String user = tableModel.getValueAt(row, 1).toString();
            String roleStr = tableModel.getValueAt(row, 2).toString();
            medicalcabinet.domain.dtos.UserRole role = medicalcabinet.domain.dtos.UserRole.valueOf(roleStr);
            String email = tableModel.getValueAt(row, 3).toString();
            return new UserDTO(id, user, role, email);
        }
        return null;
    }

    @Override
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
}