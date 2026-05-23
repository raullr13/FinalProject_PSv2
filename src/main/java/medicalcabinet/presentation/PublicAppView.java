package medicalcabinet.presentation;

import medicalcabinet.domain.dtos.DoctorDTO;
// Import the I18nManager!
import medicalcabinet.presentation.utils.I18nManager;

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
        setTitle(I18nManager.getString("public.title", "Medical Cabinet - Public Portal"));
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchField = new JTextField(15);

        JButton searchBtn = new JButton(I18nManager.getString("public.btn.search", "Search Doctor"));
        JButton loginBtn = new JButton(I18nManager.getString("public.btn.login", "Staff Login"));

        topPanel.add(new JLabel(I18nManager.getString("public.label.search", "Search Name: ")));
        topPanel.add(searchField);
        topPanel.add(searchBtn);
        topPanel.add(Box.createHorizontalStrut(50));
        topPanel.add(loginBtn);
        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        specModel = new DefaultListModel<>();
        specList = new JList<>(specModel);
        specList.setBorder(BorderFactory.createTitledBorder(I18nManager.getString("public.border.specs", "Specializations")));
        centerPanel.add(new JScrollPane(specList));

        doctorModel = new DefaultListModel<>();
        doctorList = new JList<>(doctorModel);
        doctorList.setBorder(BorderFactory.createTitledBorder(I18nManager.getString("public.border.doctors", "Doctors")));
        centerPanel.add(new JScrollPane(doctorList));

        detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        detailsArea.setBorder(BorderFactory.createTitledBorder(I18nManager.getString("public.border.details", "Doctor Details")));
        centerPanel.add(new JScrollPane(detailsArea));

        add(centerPanel, BorderLayout.CENTER);

        specList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && specList.getSelectedValue() != null) {
                presenter.onSpecializationSelected(specList.getSelectedValue());
            }
        });

        doctorList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && doctorList.getSelectedValue() != null) {
                displayDoctorDetails(doctorList.getSelectedValue());
            }
        });

        searchBtn.addActionListener(e -> presenter.onSearchClicked());

        loginBtn.addActionListener(e -> {
            this.dispose();
            LoginView loginView = new LoginView();
            medicalcabinet.services.AuthRestClient authClient = new medicalcabinet.services.AuthRestClient();
            LoginPresenter loginPresenter = new LoginPresenter(loginView, authClient);
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
        detailsArea.setText("");
    }

    @Override
    public void displayDoctorDetails(DoctorDTO doctor) {
        String nameLbl = I18nManager.getString("public.details.name", "Name: ");
        String specLbl = I18nManager.getString("public.details.spec", "Specialization: ");
        String schedLbl = I18nManager.getString("public.details.schedule", "Schedule:\n");
        String cvLbl = I18nManager.getString("public.details.cv", "CV / Biography:\n");

        detailsArea.setText(nameLbl + doctor.getFullName() + "\n");
        detailsArea.append(specLbl + doctor.getSpecialization() + "\n");
        detailsArea.append("----------------------------------\n");
        detailsArea.append(schedLbl + doctor.getSchedule() + "\n");
        detailsArea.append("----------------------------------\n");
        detailsArea.append(cvLbl + doctor.getCvText() + "\n\n");
    }

    @Override
    public String getSearchQuery() { return searchField.getText(); }

    @Override
    public void showMessage(String message) { JOptionPane.showMessageDialog(this, message); }
}