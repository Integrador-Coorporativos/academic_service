package br.com.ifrn.AcademicService.services;

import br.com.ifrn.AcademicService.config.keycloak.KeycloakAdminConfig;
import br.com.ifrn.AcademicService.dto.response.ClassPanelResponseDTO;
import br.com.ifrn.AcademicService.dto.response.ResponseClassByIdDTO;
import br.com.ifrn.AcademicService.dto.response.ResponseClassDTO;
import br.com.ifrn.AcademicService.dto.response.StudentDataDTO;
import br.com.ifrn.AcademicService.mapper.ClassMapper;
import br.com.ifrn.AcademicService.mapper.StudentPerformanceMapper;
import br.com.ifrn.AcademicService.models.Classes;
import br.com.ifrn.AcademicService.models.Courses;
import br.com.ifrn.AcademicService.repository.ClassesRepository;
import jakarta.persistence.EntityNotFoundException;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ClassesService {

    @Autowired
    ClassesRepository classesRepository;

    @Autowired
    CoursesService coursesService;

    @Autowired
    StudentPerformanceService studentPerformanceService;

    @Autowired
    ClassMapper classsMapper;

    @Autowired
    StudentPerformanceMapper studentPerformanceMapper;

    @Autowired
    KeycloakAdminConfig keycloakAdminConfig;

    @Transactional(readOnly = true)
    @Cacheable(value = "classesCacheAll")
    public List<ResponseClassDTO> getAll() {
        return classsMapper.toResponseClassDTO(classesRepository.findAll());
    }

    @Cacheable(value = "classesCache", key = "#id")
    public Optional<Classes> getById(Integer id) { return classesRepository.findById(id); }

    @Transactional(readOnly = true)
    public List<ClassPanelResponseDTO> getClassesForPanel() {
        List<Classes> allClasses = classesRepository.findAll();
        List<ClassPanelResponseDTO> panelList = new ArrayList<>();

        for (Classes c : allClasses) {
            panelList.add(new ClassPanelResponseDTO(
                    c.getId(),
                    c.getName(),
                    c.getShift(),
                    c.getCourse() != null ? c.getCourse().getId() : 0,
                    c.getCourse() != null ? c.getCourse().getName() : "-"
            ));
        }

        return panelList;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "classesCache", key = "#id")
    public ResponseClassByIdDTO getByClassId(Integer id) throws Exception {
        Classes classe = classesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Classe not found"));

        List<StudentDataDTO> classStudents = new ArrayList<>();

        List<String> userIds = classe.getUserId();
        if (userIds == null || userIds.isEmpty()) {
            ResponseClassByIdDTO responseEmpty = classsMapper.toResponseClassByDTO(classe);
            responseEmpty.setStudents(classStudents);
            return responseEmpty;
        }

        for (String studentId : userIds) {
            UserRepresentation user = keycloakAdminConfig.findKeycloakUser(studentId);

            // CHECK DE SEGURANÇA: Só prossegue se o usuário existir no Keycloak
            if (user != null && user.getId() != null) {

                StudentDataDTO performance = studentPerformanceMapper.toStudentDataDTO(
                        studentPerformanceService.getStudentPerformanceByStudentId(user.getId())
                );

                // se mapper retornou null, ignora
                if (performance != null) {
                    performance.setName(user.getFirstName());
                    performance.setRegistration(user.getUsername());
                    classStudents.add(performance);
                }
            } else {
                // Opcional: Logar que o usuário X não foi encontrado no Keycloak
                System.out.println("Aviso: Usuário " + studentId + " não encontrado no Keycloak.");
            }
        }

        ResponseClassByIdDTO responseClassByIdDTO = classsMapper.toResponseClassByDTO(classe);
        responseClassByIdDTO.setStudents(classStudents);

        return responseClassByIdDTO;
    }

    @CacheEvict(value = "classesCacheAll", allEntries = true)
    public Classes create(Classes turma) {

        if (turma.getName() == null) {
            throw new IllegalArgumentException("Nome da turma não pode ser nulo");
        }
        if (turma.getName().isEmpty()) {
            throw new IllegalArgumentException("Nome da turma não pode ser vazio");
        }
        if (turma.getName().length() > 255) {
            throw new IllegalArgumentException("Nome da turma não pode exceder 255 caracteres");
        }

        return classesRepository.save(turma);
    }

    @CacheEvict(value = {"classesCacheAll", "classesCache"}, allEntries = true)
    @Transactional
    public Classes update(Integer id, Classes turmaDetails) {
        Classes turma = classesRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Classe not found"));

        turma.setName(turmaDetails.getName());
        turma.setCourse(turmaDetails.getCourse());

        return classesRepository.save(turma);
    }

    @CacheEvict(value = {"classesCacheAll", "classesCache"}, allEntries = true)
    public boolean delete(Integer id) {
        if (!classesRepository.existsById(id)) {
            return false;
        }
        classesRepository.deleteById(id);
        return true;
    }


    /**
     *
     * Creates a new {@link Classes} entity or updates an existing one based on the provided classId.
     * <p>
     * This method performs the following operations:
     * <ul>
     *     <li>If no class exists with the provided classId:
     *         <ul>
     *             <li>A new {@link Classes} is instantiated.</li>
     *             <li>The course is retrieved or created using {@code courseName}.</li>
     *             <li>The userId is added as the creator/owner of the class.</li>
     *             <li>Comments list and other class properties are initialized.</li>
     *             <li>The class is persisted in the database.</li>
     *         </ul>
     *     </li>
     *     <li>If a class already exists:
     *         <ul>
     *             <li>The userId is added to the class only if not already present.</li>
     *             <li>Semester, gradle level and shift are updated if different from the current values.</li>
     *             <li>The updated entity is persisted in the database.</li>
     *         </ul>
     *     </li>
     * </ul>
     * </p>
     *
     * @param courseName   the name of the course associated with the class. If the course does not exist,
     *                     it will be created automatically.
     * @param classId      the unique identifier used to locate or create the class. Cannot be {@code null}.
     * @param shift        the shift of the class (e.g. "Matutino", "Vespertino").
     * @param userId       the identifier of the user associated with the class.
     *
     * @return the created or updated {@link Classes} entity.
     *
     * @throws IllegalArgumentException if {@code classId} is {@code null}.
     */
    @CacheEvict(value = {"classesCacheAll", "classesCache"}, allEntries = true)
    @Transactional
    public Classes createOrUpdateClassByClassId(
            String courseName,
            String classId, String shift, String userId) {

        if (classId == null)
            throw new IllegalArgumentException("classId não pode ser nulo");

        Classes classes = classesRepository.findByClassId(classId);

        if (classes == null) {
            classes = new Classes();

            Courses course = coursesService.findOrCreateByName(courseName);

            classes.setUserId(new ArrayList<>(List.of(userId)));
            classes.setComments(new ArrayList<>());
            classes.setCourse(course);
            classes.setClassId(classId);
            classes.setShift(shift);
            classes.setName(course.getName() + "_" + classId);

            return classesRepository.save(classes);
        }

        // garante lista inicializada
        if (classes.getUserId() == null) {
            classes.setUserId(new ArrayList<>());
        }

        // Update logic
        List<String> userIds = classes.getUserId();
        if (!userIds.contains(userId)) {
            userIds.add(userId);
        }

        if (!Objects.equals(classes.getShift(), shift)) {
            classes.setShift(shift);
        }

        return classesRepository.save(classes);
    }
}
