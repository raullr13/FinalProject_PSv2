package medicalcabinet.presentation;

import medicalcabinet.domain.dtos.DoctorDTO;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class PublicAppView extends JFrame implements IPublicView {
    private PublicPresenter presenter;

    private JList<String> specList;
    private DefaultListModel<String> specModel;

    private JList<DoctorDTO> doctorList;
    private DefaultListModel<DoctorDTO> doctorModel;

    private JTextArea detailsArea;
    private JTextField searchField;

    public PublicAppView() {
        setTitle("Medical Cabinet - Public Portal");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top Panel: Search & Login
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchField = new JTextField(15);
        JButton searchBtn = new JButton("Search Doctor");
        JButton loginBtn = new JButton("Staff Login");

        topPanel.add(new JLabel("Search Name: "));
        topPanel.add(searchField);
        topPanel.add(searchBtn);
        topPanel.add(Box.createHorizontalStrut(50));
        topPanel.add(loginBtn);
        add(topPanel, BorderLayout.NORTH);

        // Center Panel: Split into three columns
        JPanel centerPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Column 1: Specializations
        specModel = new DefaultListModel<>();
        specList = new JList<>(specModel);
        specList.setBorder(BorderFactory.createTitledBorder("Specializations"));
        centerPanel.add(new JScrollPane(specList));

        // Column 2: Doctors List
        doctorModel = new DefaultListModel<>();
        doctorList = new JList<>(doctorModel);
        doctorList.setBorder(BorderFactory.createTitledBorder("Doctors"));
        centerPanel.add(new JScrollPane(doctorList));

        // Column 3: Doctor Details (CV, Schedule)
        detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setBorder(BorderFactory.createTitledBorder("Doctor Details"));
        centerPanel.add(new JScrollPane(detailsArea));

        add(centerPanel, BorderLayout.CENTER);

        // --- Action Listeners ---

        // When clicking a specialization, load doctors
        specList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && specList.getSelectedValue() != null) {
                presenter.onSpecializationSelected(specList.getSelectedValue());
            }
        });

        // When clicking a doctor, show CV details
        doctorList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && doctorList.getSelectedValue() != null) {
                displayDoctorDetails(doctorList.getSelectedValue());
            }
        });

        // Search Button
        searchBtn.addActionListener(e -> presenter.onSearchClicked());

        // Login Button - Opens your existing Auth system!
        loginBtn.addActionListener(e -> {
            this.dispose(); // Close the public view

            // 1. Create the View
            LoginView loginView = new LoginView();

            // 2. Create the Auth Client (so the presenter can talk to the backend/database)
            medicalcabinet.services.AuthRestClient authClient = new medicalcabinet.services.AuthRestClient();

            // 3. Pass BOTH the view and the client into the Presenter
            LoginPresenter loginPresenter = new LoginPresenter(loginView, authClient);

            // 4. Link it up and show it!
            loginView.setPresenter(loginPresenter);
            loginView.setVisible(true);
        });

        setLocationRelativeTo(null);
    }

    public void setPresenter(PublicPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void displaySpecializations(List<String> specs) {
        specModel.clear();
        for (String s : specs) specModel.addElement(s);
    }

    @Override
    public void displayDoctors(List<DoctorDTO> doctors) {
        doctorModel.clear();
        for (DoctorDTO d : doctors) doctorModel.addElement(d);
        detailsArea.setText(""); // Clear details
    }

    @Override
    public void displayDoctorDetails(DoctorDTO doctor) {
        detailsArea.setText("Name: " + doctor.getFullName() + "\n");
        detailsArea.append("Specialization: " + doctor.getSpecialization() + "\n");
        detailsArea.append("----------------------------------\n");
        detailsArea.append("Schedule:\n" + doctor.getSchedule() + "\n");
        detailsArea.append("----------------------------------\n");
        detailsArea.append("CV / Biography:\n" + doctor.getCvText() + "\n\n");
        detailsArea.append("[Photo Path: " + doctor.getPhotoPath() + "]");
    }

    @Override
    public String getSearchQuery() { return searchField.getText(); }

    @Override
    public void showMessage(String message) { JOptionPane.showMessageDialog(this, message); }
}