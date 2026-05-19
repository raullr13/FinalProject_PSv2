package medicalcabinet.presentation;
import medicalcabinet.domain.dtos.UserDTO;
import java.util.List;

public interface IAdminView {
    void setPresenter(AdminPresenter presenter);
    void displayUsers(List<UserDTO> users);
    UserDTO getSelectedUser();
    void showMessage(String message);
}