package medicalcabinet.presentation;

import medicalcabinet.domain.dtos.UserDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminDashboardView extends JFrame implements IAdminView {
    private AdminPresenter presenter;
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> roleFilterCombo;
    private javax.swing.JButton btnUpdate;

    public AdminDashboardView() {
        setTitle("Medical Cabinet - Administrator Dashboard");
        setSize(800, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBorder(BorderFactory.createTitledBorder("Filtrare Utilizatori"));
        topPanel.add(new JLabel("Alege Tip Utilizator:"));

        roleFilterCombo = new JComboBox<>(new String[]{"ALL", "ADMINISTRATOR", "DOCTOR", "ASSISTANT", "PATIENT"});
        topPanel.add(roleFilterCombo);
        add(topPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Username", "Role", "Email"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        userTable = new JTable(tableModel);
        add(new JScrollPane(userTable), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton exportBtn = new JButton("Export to CSV");
        JButton notifyBtn = new JButton("Notify User (Email/SMS)");
        JButton addBtn = new JButton("Add User");
        JButton deleteBtn = new JButton("Delete User");
        btnUpdate = new javax.swing.JButton("Update User");

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

        // Export Action
        exportBtn.addActionListener(e -> {
            if (presenter != null) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Salvează lista ca CSV");
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
                    showMessage("Vă rugăm să selectați un utilizator din tabel pentru actualizare.");
                    return;
                }

                JTextField usernameField = new JTextField(selectedUser.getUsername(), 10);
                JPasswordField passwordField = new JPasswordField(10); // Parola rămâne goală (nu o afișăm)
                JComboBox<String> roleCombo = new JComboBox<>(new String[]{"ADMINISTRATOR", "DOCTOR", "ASSISTANT", "PATIENT"});
                roleCombo.setSelectedItem(selectedUser.getRole().name()); // Setează rolul curent
                JTextField emailField = new JTextField(selectedUser.getEmail(), 15);

                JPanel myPanel = new JPanel(new GridLayout(4, 2, 5, 5));
                myPanel.add(new JLabel("Username:"));
                myPanel.add(usernameField);
                myPanel.add(new JLabel("Parolă nouă (lasă gol pt a nu modifica):"));
                myPanel.add(passwordField);
                myPanel.add(new JLabel("Rol:"));
                myPanel.add(roleCombo);
                myPanel.add(new JLabel("Email:"));
                myPanel.add(emailField);

                int result = JOptionPane.showConfirmDialog(this, myPanel,
                        "Actualizare Utilizator", JOptionPane.OK_CANCEL_OPTION);

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

        // Add User Action
        addBtn.addActionListener(e -> {
            if (presenter != null) {
                JTextField usernameField = new JTextField(10);
                JPasswordField passwordField = new JPasswordField(10);
                JComboBox<String> roleCombo = new JComboBox<>(new String[]{"ADMINISTRATOR", "DOCTOR", "ASSISTANT", "PATIENT"});
                JTextField emailField = new JTextField(15);

                JPanel myPanel = new JPanel(new GridLayout(4, 2, 5, 5));
                myPanel.add(new JLabel("Username:"));
                myPanel.add(usernameField);
                myPanel.add(new JLabel("Password:"));
                myPanel.add(passwordField);
                myPanel.add(new JLabel("Role:"));
                myPanel.add(roleCombo);
                myPanel.add(new JLabel("Email:"));
                myPanel.add(emailField);

                int result = JOptionPane.showConfirmDialog(null, myPanel,
                        "Introduceți datele noului utilizator", JOptionPane.OK_CANCEL_OPTION);
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

        setLocationRelativeTo(null);
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