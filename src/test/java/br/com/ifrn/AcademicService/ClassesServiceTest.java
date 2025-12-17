package br.com.ifrn.AcademicService;

import br.com.ifrn.AcademicService.repository.ClassesRepository;
import br.com.ifrn.AcademicService.services.ClassesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.List;
import java.util.Optional;
import br.com.ifrn.AcademicService.models.Classes;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClassesServiceTest {

    @Mock
    private ClassesRepository classesRepository;

    @InjectMocks
    private ClassesService classesService;

    private Classes turma;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        turma = new Classes();
        turma.setId(1);
        turma.setName("Matemática");
        turma.setSemester("2025.2");
    }

    // ==============================
    // Testes Unitários
    // ==============================

    @Test
    void testCreate() {
        when(classesRepository.save(any(Classes.class))).thenReturn(turma);
        Classes created = classesService.create(turma);
        assertNotNull(created);
        assertEquals("Matemática", created.getName());
    }

    @Test
    void testGetAll() {
        when(classesRepository.findAll()).thenReturn(List.of(turma));
        List<Classes> all = classesService.getAll();
        assertEquals(1, all.size());
        assertEquals("Matemática", all.get(0).getName());
    }

    @Test
    void testGetById() {
        when(classesRepository.findById(1)).thenReturn(Optional.of(turma));
        Optional<Classes> result = classesService.getById(1);
        assertTrue(result.isPresent());
        assertEquals("Matemática", result.get().getName());
    }

    @Test
    void testUpdate() {
        Classes updated = new Classes();
        updated.setName("Física");
        updated.setSemester("2025.2");

        when(classesRepository.findById(1)).thenReturn(Optional.of(turma));
        when(classesRepository.save(any(Classes.class))).thenReturn(updated);

        Classes result = classesService.update(1, updated);
        assertEquals("Física", result.getName());
    }

    @Test
    void testDelete() {
        when(classesRepository.existsById(1)).thenReturn(true);
        doNothing().when(classesRepository).deleteById(1);
        assertDoesNotThrow(() -> classesService.delete(1));
        verify(classesRepository, times(1)).deleteById(1);
    }

    // ==============================
    // Testes de Valor Limite
    // ==============================

    @Test
    void testGetByIdZeroShouldReturnEmpty() {
        when(classesRepository.findById(0)).thenReturn(Optional.empty());
        Optional<Classes> result = classesService.getById(0);
        assertTrue(result.isEmpty(), "ID zero deve retornar vazio");
    }

    @Test
    void testGetByIdMaxValueShouldReturnEmpty() {
        int maxId = Integer.MAX_VALUE;
        when(classesRepository.findById(maxId)).thenReturn(Optional.empty());
        Optional<Classes> result = classesService.getById(maxId);
        assertTrue(result.isEmpty(), "ID máximo deve retornar vazio");
    }

    @Test
    void testCreateWithEmptyNameShouldFail() {
        turma.setName(""); // nome vazio
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            classesService.create(turma);
        });
        assertEquals("Nome da turma não pode ser vazio", exception.getMessage());
    }

    @Test
    void testCreateWithNullNameShouldFail() {
        turma.setName(null); // nome nulo
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            classesService.create(turma);
        });
        assertEquals("Nome da turma não pode ser nulo", exception.getMessage());
    }

    @Test
    void testCreateWithVeryLongNameShouldFail() {
        turma.setName("A".repeat(256)); // acima do limite
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            classesService.create(turma);
        });
        assertEquals("Nome da turma não pode exceder 255 caracteres", exception.getMessage());
    }
}
