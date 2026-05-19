package medicalcabinet.domain.plugincontracts;

import java.util.List;
import medicalcabinet.domain.dtos.ConsultationDTO;

public interface IExportPlugin {
    String getFormatName();
    boolean exportConsultations(String filePath, List<ConsultationDTO> consultations);
}