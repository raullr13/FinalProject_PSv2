package medicalcabinet.domain.plugincontracts;

import medicalcabinet.domain.dtos.ConsultationDTO;
import medicalcabinet.domain.dtos.PatientDTO;
import java.util.List;

public interface IStatisticsPlugin {
    String getChartType();
    void generatePatientStatisticsChart(List<PatientDTO> patients, List<ConsultationDTO> allConsultations);
}