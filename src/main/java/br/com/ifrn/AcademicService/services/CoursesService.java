package br.com.ifrn.AcademicService.services;

import br.com.ifrn.AcademicService.models.Courses;
import br.com.ifrn.AcademicService.repository.CoursesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CoursesService {

    @Autowired
    private CoursesRepository coursesRepository;

    @Cacheable(value = "coursesCacheAll")
    public List<Courses> getAll() {
        return coursesRepository.findAll();
    }

    @Cacheable(value = "coursesCache", key = "#id")
    public Optional<Courses> getById(Integer id) {
        return coursesRepository.findById(id);
    }

    @CacheEvict(value = "coursesCacheAll", allEntries = true)
    public Courses create(Courses course) {

        if (course.getName() == null ||
                course.getName().isEmpty() ||
                course.getName().length() > 255 ||
                course.getDescription() == null ||
                course.getDescription().length() > 500) {

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

        Courses course = coursesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Curso não encontrado"));

        course.setName(courseDetails.getName());
        course.setDescription(courseDetails.getDescription());

        return coursesRepository.save(course);
    }

    @CacheEvict(value = {"coursesCacheAll", "coursesCache"}, allEntries = true)
    public boolean delete(Integer id) {
        if (!coursesRepository.existsById(id)) {
            throw new RuntimeException("Curso não encontrado");
        }

        coursesRepository.deleteById(id);
        return true;
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
}
