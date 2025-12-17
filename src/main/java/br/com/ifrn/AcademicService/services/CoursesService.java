package br.com.ifrn.AcademicService.services;

import br.com.ifrn.AcademicService.models.Courses;
import br.com.ifrn.AcademicService.repository.CoursesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CoursesService {

    @Autowired
    private CoursesRepository coursesRepository;




    public List<Courses> getAll() { return coursesRepository.findAll(); }
    public Optional<Courses> getById(Integer id) { return coursesRepository.findById(id); }
    public Courses create(Courses course) {
        if (course.getName() == null) {
            throw new IllegalArgumentException("Nome do curso não pode ser nulo");
        }
        if (course.getName().isEmpty()) {
            throw new IllegalArgumentException("Nome do curso não pode ser vazio");
        }
        if (course.getName().length() > 255) {
            throw new IllegalArgumentException("Nome do curso não pode exceder 255 caracteres");
        }
        if (course.getDescription().length() > 500) {
            throw new IllegalArgumentException("Descrição do curso não pode exceder 500 caracteres");
        }
        return coursesRepository.save(course);
    }

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
    public boolean delete(Integer id) { coursesRepository.deleteById(id);
        return false;
    }

    public Courses findOrCreateByName(String name) {
        Courses course = coursesRepository.findByName(name);
        if (course == null) {
            course = new Courses();          // ← você esqueceu isso
            course.setName(name);
            course = coursesRepository.save(course);
        }
        return course;
    }


}
