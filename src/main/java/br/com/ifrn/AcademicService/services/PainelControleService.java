package br.com.ifrn.AcademicService.services;

import br.com.ifrn.AcademicService.config.keycloak.KeycloakAdminConfig;
import br.com.ifrn.AcademicService.dto.ProfessorStatsView;
import br.com.ifrn.AcademicService.dto.response.ResponseProfessorPanelDTO;
import br.com.ifrn.AcademicService.dto.response.StudentDataDTO;
import br.com.ifrn.AcademicService.mapper.StudentPerformanceMapper;
import br.com.ifrn.AcademicService.repository.ClassesRepository;
import br.com.ifrn.AcademicService.repository.StudentPerformanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PainelControleService {
    @Autowired
    KeycloakAdminConfig keycloakAdmin;

    @Autowired
    ClassesRepository classesRepository;

    @Autowired
    StudentPerformanceRepository studentPerformanceRepository;

    @Autowired
    StudentPerformanceMapper  studentPerformanceMapper;

    public List<ResponseProfessorPanelDTO> getAllProfessors() {
        return keycloakAdmin.findUsersGroup("Teachers")
                .stream()
                .map(user -> {
                    ProfessorStatsView stats = classesRepository.countProfessorStats(user.getId());
                    ResponseProfessorPanelDTO professor = new ResponseProfessorPanelDTO();
                    professor.setName(user.getFirstName());
                    professor.setEmail(user.getEmail());
                    professor.setRegistration(user.getUsername());
                    professor.setQuantityClass(stats.getTotalTurmas());
                    professor.setQuantityStudents(stats.getTotalAlunos());
                    return professor;
                })
                .toList();
    }

    public List<StudentDataDTO> getAllStudents() {
        return keycloakAdmin.findUsersGroup("Students")
                .stream()
                .map(user -> {
                    StudentDataDTO student = studentPerformanceRepository
                            .findByStudentId(user.getId())
                            .map(studentPerformanceMapper::toStudentDataDTO)
                            .orElseGet(() -> {
                                StudentDataDTO dto = new StudentDataDTO();
                                dto.setStudentId(user.getId());
                                return dto;
                            });

                    student.setName(user.getFirstName());
                    student.setEmail(user.getEmail());
                    student.setRegistration(user.getUsername());

                    return student;
                })
                .toList();
    }

}

