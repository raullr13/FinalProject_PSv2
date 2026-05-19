package medicalcabinet.presentation;

import medicalcabinet.domain.dtos.UserDTO;
import medicalcabinet.domain.dtos.UserRole;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminDashboardView extends JFrame implements IAdminView {
    private AdminPresenter presenter;
    private JTable userTable;
    private DefaultTableModel tableModel;

    public AdminDashboardView() {
        setTitle("Medical Cabinet - Administrator Dashboard");
        setSize(700, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Center Table
        String[] columns = {"ID", "Username", "Role", "Email"};
        tableModel = new DefaultTableModel(columns, 0);
        userTable = new JTable(tableModel);
        add(new JScrollPane(userTable), BorderLayout.CENTER);

        // Buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton exportBtn = new JButton("Export to CSV");
        JButton notifyBtn = new JButton("Notify User (Email/SMS)");
        JButton addBtn = new JButton("Add User");
        JButton deleteBtn = new JButton("Delete User");

        bottomPanel.add(exportBtn);
        bottomPanel.add(notifyBtn);
        bottomPanel.add(addBtn);
        bottomPanel.add(deleteBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        // Action Listeners
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
            tableModel.addRow(new Object[]{u.getId(), u.getUsername(), u.getRole().name(), u.getEmail()});
        }
    }

    @Override
    public UserDTO getSelectedUser() {
        int row = userTable.getSelectedRow();
        if (row >= 0) {
            int id = (int) tableModel.getValueAt(row, 0);
            String user = (String) tableModel.getValueAt(row, 1);
            UserRole role = UserRole.valueOf((String) tableModel.getValueAt(row, 2));
            String email = (String) tableModel.getValueAt(row, 3);
            return new UserDTO(id, user, role, email);
        }
        return null;
    }

    @Override
    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message);
    }
}