package com.eam.capacitaciones.service;

import com.eam.capacitaciones.domain.entity.Curso;
import com.eam.capacitaciones.domain.entity.Curso.NivelEnum;
import com.eam.capacitaciones.domain.entity.Usuario;
import com.eam.capacitaciones.dto.request.CursoCreateRequest;
import com.eam.capacitaciones.dto.response.CursoDTO;
import com.eam.capacitaciones.exception.BadRequestException;
import com.eam.capacitaciones.exception.ForbiddenException;
import com.eam.capacitaciones.exception.ResourceNotFoundException;
import com.eam.capacitaciones.mapper.CursoMapper;
import com.eam.capacitaciones.repository.CursoRepository;
import com.eam.capacitaciones.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CursoServiceTest {

    @Mock
    private CursoRepository cursoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private CursoMapper cursoMapper;

    @InjectMocks
    private CursoService cursoService;

    private Curso curso;
    private CursoDTO cursoDTO;
    private CursoCreateRequest createRequest;
    private Usuario instructor;

    @BeforeEach
    void setUp() {
        instructor = Usuario.builder()
                .idUsuario(1L)
                .email("instructor@example.com")
                .nombre("Instructor Name")
                .build();

        curso = Curso.builder()
                .idCurso(1L)
                .titulo("Test Course")
                .descripcion("Test Description")
                .duracionEstim("10 horas")
                .nivel(NivelEnum.BASICO)
                .instructorId(1L)
                .instructor(instructor)
                .activo(true)
                .imagenUrl("test.jpg")
                .build();

        cursoDTO = CursoDTO.builder()
                .idCurso(1L)
                .titulo("Test Course")
                .descripcion("Test Description")
                .duracionEstim("10 horas")
                .nivel(NivelEnum.BASICO)
                .instructorId(1L)
                .instructorNombre("Instructor Name")
                .activo(true)
                .imagenUrl("test.jpg")
                .totalModulos(0)
                .totalInscritos(0)
                .build();

        createRequest = CursoCreateRequest.builder()
                .titulo("Test Course")
                .descripcion("Test Description")
                .duracionEstim("10 horas")
                .nivel(NivelEnum.BASICO)
                .instructorId(1L)
                .imagenUrl("test.jpg")
                .build();
    }

    @Test
    void getAllCursos_ShouldReturnAllCursos() {
        // Arrange
        when(cursoRepository.findAll()).thenReturn(List.of(curso));
        when(cursoMapper.toDTOList(List.of(curso))).thenReturn(List.of(cursoDTO));

        // Act
        List<CursoDTO> result = cursoService.getAllCursos();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitulo()).isEqualTo("Test Course");
        verify(cursoRepository).findAll();
        verify(cursoMapper).toDTOList(List.of(curso));
    }

    @Test
    void getCursosActivos_ShouldReturnActiveCursos() {
        // Arrange
        when(cursoRepository.findByActivoTrue()).thenReturn(List.of(curso));
        when(cursoMapper.toDTOList(List.of(curso))).thenReturn(List.of(cursoDTO));

        // Act
        List<CursoDTO> result = cursoService.getCursosActivos();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getActivo()).isTrue();
        verify(cursoRepository).findByActivoTrue();
        verify(cursoMapper).toDTOList(List.of(curso));
    }

    @Test
    void getCursoById_ShouldReturnCurso_WhenExists() {
        // Arrange
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));
        when(cursoMapper.toDTO(curso)).thenReturn(cursoDTO);

        // Act
        CursoDTO result = cursoService.getCursoById(1L);

        // Assert
        assertThat(result.getIdCurso()).isEqualTo(1L);
        assertThat(result.getTitulo()).isEqualTo("Test Course");
        verify(cursoRepository).findById(1L);
        verify(cursoMapper).toDTO(curso);
    }

    @Test
    void getCursoById_ShouldThrowException_WhenNotExists() {
        // Arrange
        when(cursoRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> cursoService.getCursoById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Curso no encontrado con ID: 1");
        verify(cursoRepository).findById(1L);
    }

    @Test
    void getCursosByInstructor_ShouldReturnCursos() {
        // Arrange
        when(cursoRepository.findByInstructorId(1L)).thenReturn(List.of(curso));
        when(cursoMapper.toDTOList(List.of(curso))).thenReturn(List.of(cursoDTO));

        // Act
        List<CursoDTO> result = cursoService.getCursosByInstructor(1L);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getInstructorId()).isEqualTo(1L);
        verify(cursoRepository).findByInstructorId(1L);
        verify(cursoMapper).toDTOList(List.of(curso));
    }

    @Test
    void getCursosByNivel_ShouldReturnCursos() {
        // Arrange
        when(cursoRepository.findByNivel(NivelEnum.BASICO)).thenReturn(List.of(curso));
        when(cursoMapper.toDTOList(List.of(curso))).thenReturn(List.of(cursoDTO));

        // Act
        List<CursoDTO> result = cursoService.getCursosByNivel(NivelEnum.BASICO);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNivel()).isEqualTo(NivelEnum.BASICO);
        verify(cursoRepository).findByNivel(NivelEnum.BASICO);
        verify(cursoMapper).toDTOList(List.of(curso));
    }

    @Test
    void searchCursosByTitulo_ShouldReturnMatchingCursos() {
        // Arrange
        when(cursoRepository.searchByTitulo("Test")).thenReturn(List.of(curso));
        when(cursoMapper.toDTOList(List.of(curso))).thenReturn(List.of(cursoDTO));

        // Act
        List<CursoDTO> result = cursoService.searchCursosByTitulo("Test");

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitulo()).isEqualTo("Test Course");
        verify(cursoRepository).searchByTitulo("Test");
        verify(cursoMapper).toDTOList(List.of(curso));
    }

    @Test
    void createCurso_ShouldCreateAndReturnCurso_WhenValid() {
        // Arrange
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        when(cursoMapper.toEntity(createRequest)).thenReturn(curso);
        when(cursoRepository.save(any(Curso.class))).thenReturn(curso);
        when(cursoMapper.toDTO(any(Curso.class))).thenReturn(cursoDTO);

        // Act
        CursoDTO result = cursoService.createCurso(createRequest);

        // Assert
        assertThat(result.getTitulo()).isEqualTo("Test Course");
        assertThat(result.getInstructorId()).isEqualTo(1L);
        verify(usuarioRepository).existsById(1L);
        verify(cursoMapper).toEntity(createRequest);
        verify(cursoRepository).save(any(Curso.class));
        verify(cursoMapper).toDTO(any(Curso.class));
    }

    @Test
    void createCurso_ShouldThrowException_WhenInstructorNotExists() {
        // Arrange
        when(usuarioRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> cursoService.createCurso(createRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("El instructor no existe");
        verify(usuarioRepository).existsById(1L);
        verify(cursoRepository, never()).save(any(Curso.class));
    }

    @Test
    void updateCurso_ShouldUpdateAndReturnCurso_WhenValid() {
        // Arrange
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));
        when(cursoRepository.save(any(Curso.class))).thenReturn(curso);
        when(cursoMapper.toDTO(any(Curso.class))).thenReturn(cursoDTO);

        // Act
        CursoDTO result = cursoService.updateCurso(1L, createRequest, 1L);

        // Assert
        assertThat(result.getIdCurso()).isEqualTo(1L);
        verify(cursoRepository).findById(1L);
        verify(cursoMapper).updateEntityFromRequest(createRequest, curso);
        verify(cursoRepository).save(curso);
        verify(cursoMapper).toDTO(curso);
    }

    @Test
    void updateCurso_ShouldThrowException_WhenCursoNotExists() {
        // Arrange
        when(cursoRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> cursoService.updateCurso(1L, createRequest, 1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Curso no encontrado");
        verify(cursoRepository).findById(1L);
        verify(cursoRepository, never()).save(any(Curso.class));
    }

    @Test
    void updateCurso_ShouldThrowException_WhenNotAuthorized() {
        // Arrange
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));

        // Act & Assert
        assertThatThrownBy(() -> cursoService.updateCurso(1L, createRequest, 2L))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage("No tiene permisos para actualizar este curso");
        verify(cursoRepository).findById(1L);
        verify(cursoRepository, never()).save(any(Curso.class));
    }

    @Test
    void deleteCurso_ShouldDeleteCurso_WhenExists() {
        // Arrange
        when(cursoRepository.existsById(1L)).thenReturn(true);

        // Act
        cursoService.deleteCurso(1L);

        // Assert
        verify(cursoRepository).existsById(1L);
        verify(cursoRepository).deleteById(1L);
    }

    @Test
    void deleteCurso_ShouldThrowException_WhenNotExists() {
        // Arrange
        when(cursoRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> cursoService.deleteCurso(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Curso no encontrado");
        verify(cursoRepository).existsById(1L);
        verify(cursoRepository, never()).deleteById(anyLong());
    }

    @Test
    void activarCurso_ShouldActivateCurso() {
        // Arrange
        curso.setActivo(false);
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));
        when(cursoRepository.save(any(Curso.class))).thenReturn(curso);

        // Act
        cursoService.activarCurso(1L);

        // Assert
        assertThat(curso.getActivo()).isTrue();
        verify(cursoRepository).findById(1L);
        verify(cursoRepository).save(curso);
    }

    @Test
    void activarCurso_ShouldThrowException_WhenNotExists() {
        // Arrange
        when(cursoRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> cursoService.activarCurso(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Curso no encontrado");
        verify(cursoRepository).findById(1L);
        verify(cursoRepository, never()).save(any(Curso.class));
    }

    @Test
    void desactivarCurso_ShouldDeactivateCurso() {
        // Arrange
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));
        when(cursoRepository.save(any(Curso.class))).thenReturn(curso);

        // Act
        cursoService.desactivarCurso(1L);

        // Assert
        assertThat(curso.getActivo()).isFalse();
        verify(cursoRepository).findById(1L);
        verify(cursoRepository).save(curso);
    }

    @Test
    void desactivarCurso_ShouldThrowException_WhenNotExists() {
        // Arrange
        when(cursoRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> cursoService.desactivarCurso(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Curso no encontrado");
        verify(cursoRepository).findById(1L);
        verify(cursoRepository, never()).save(any(Curso.class));
    }
}
