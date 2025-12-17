package br.com.ifrn.AcademicService.services;


import br.com.ifrn.AcademicService.models.Courses;
import br.com.ifrn.AcademicService.repository.ClassesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import br.com.ifrn.AcademicService.models.Classes;
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

    @Cacheable(value = "classesCacheAll")
    public List<Classes> getAll() { return classesRepository.findAll(); }

    @Cacheable(value = "classesCache", key = "#id")
    public Optional<Classes> getById(Integer id) { return classesRepository.findById(id); }

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


        return classesRepository.save(turma); }

    @CacheEvict(value = {"classesCacheAll", "classesCache"}, allEntries = true)
    @Transactional
    public Classes update(Integer id, Classes turmaDetails) {
        Classes turma = classesRepository.findById(id).orElseThrow();
        turma.setName(turmaDetails.getName());
        turma.setCourse(turmaDetails.getCourse());
        return classesRepository.save(turma);
    }

    @CacheEvict(value = {"classesCacheAll", "classesCache"}, allEntries = true)
    public boolean delete(Integer id) { classesRepository.deleteById(id);
        return false;
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
     * @param semester     the semester in which the class is scheduled (e.g. "1º", "2º", etc.).
     * @param gradleLevel  the gradle level of the class; if {@code null}, defaults to 0.
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
            String courseName, String semester, Integer gradleLevel,
            String classId, String shift, String userId) {

        // Validar dados obrigatórios
        if (classId == null)
            throw new IllegalArgumentException("classId não pode ser nulo");

        Classes classes = classesRepository.findByClassId(classId);

        if (classes == null) {
            classes = new Classes();

            // Recupera curso ou cria se não existe
            Courses course = coursesService.findOrCreateByName(courseName);

            classes.setUserId(new ArrayList<>(List.of(userId)));
            classes.setComments(new ArrayList<>());
            classes.setCourse(course);
            classes.setClassId(classId);
            classes.setSemester(semester);
            classes.setGradleLevel(gradleLevel != null ? gradleLevel : 0);
            classes.setShift(shift);
            classes.setName(course.getName() + "_" + classId);

            return classesRepository.save(classes);
        }

        // Update logic
        List<String> userIds = classes.getUserId();
        if (!userIds.contains(userId)) {
            userIds.add(userId);
        }

        if (!classes.getSemester().equals(semester)) {
            classes.setSemester(semester);
        }
        if (!Objects.equals(classes.getGradleLevel(), gradleLevel)) {
            classes.setGradleLevel(gradleLevel);
        }
        if (!classes.getShift().equals(shift)) {
            classes.setShift(shift);
        }

        return classesRepository.save(classes);
    }


}
