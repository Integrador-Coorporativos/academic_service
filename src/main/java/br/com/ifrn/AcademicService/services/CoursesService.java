package br.com.ifrn.AcademicService.services;

import br.com.ifrn.AcademicService.dto.CoursePanelResponseDTO;
import br.com.ifrn.AcademicService.models.Classes;
import br.com.ifrn.AcademicService.models.Courses;
import br.com.ifrn.AcademicService.repository.ClassesRepository;
import br.com.ifrn.AcademicService.repository.CoursesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CoursesService {

    @Autowired
    private CoursesRepository coursesRepository;

    @Autowired
    private ClassesRepository classesRepository;

    @Cacheable(value = "coursesCacheAll")
    public List<Courses> getAll() { return coursesRepository.findAll(); }

    @Cacheable(value = "coursesCache", key = "#id")


    public Optional<Courses> getById(Integer id) {
        return coursesRepository.findById(id); }

    @CacheEvict(value = "coursesCacheAll", allEntries = true)
    public Courses create(Courses course) {
        if (course.getName() == null || course.getName().isEmpty() || course.getName().length() > 255 || course.getDescription().length() > 500) {
            throw new IllegalArgumentException("Nome ou Descrição do curso inválido!");
        }
        return findOrCreateByName(course.getName());
    }

    @CacheEvict(value = {"coursesCacheAll", "coursesCache"}, allEntries = true)
    public Courses update(Integer id, Courses courseDetails) {
        if (courseDetails.getName() == null) {
            throw new IllegalArgumentException("Nome do curso não pode ser nulo");
        }
        if (courseDetails.getName().isEmpty()) {
            throw new IllegalArgumentException("Nome do curso não pode ser vazio");
        }
        if (courseDetails.getName().length() > 255) {
            throw new IllegalArgumentException("Nome do curso não pode exceder 255 caracteres");
        }
        Courses course = coursesRepository.findById(id).orElseThrow();
        course.setName(courseDetails.getName());
        course.setDescription(courseDetails.getDescription());
        return coursesRepository.save(course);
    }

    @CacheEvict(value = {"coursesCacheAll", "coursesCache"}, allEntries = true)
    public boolean delete(Integer id) {
        if (coursesRepository.existsById(id)) {
            coursesRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @CacheEvict(value = "coursesCacheAll", allEntries = true)
    public Courses findOrCreateByName(String name) {
        Courses course = coursesRepository.findByName(name);
        if (course == null) {
            course = new Courses();
            course.setName(name);
            course = coursesRepository.save(course);
        }
        return course;
    }

    public List<CoursePanelResponseDTO> getCoursesPanel() {
        List<Courses> allCourses = coursesRepository.findAll();
        List<CoursePanelResponseDTO> panelList = new ArrayList<>();

        for (Courses course : allCourses) {
            List<Classes> classes = classesRepository.findByCourse(course);

            int quantClasses = classes.size();
            int quantStudent = 0;

            String shift = classes.stream()
                    .map(Classes::getShift)
                    .distinct()
                    .sorted()
                    .reduce((a, b) -> a + " e " + b)
                    .orElse("Não definido");

            panelList.add(new CoursePanelResponseDTO(
                    course.getId(),
                    course.getName(),
                    quantClasses,
                    quantStudent,
                    shift
            ));
        }
        return panelList;
    }

}
