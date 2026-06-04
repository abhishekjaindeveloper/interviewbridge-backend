package com.interviewbridge.serviceImpl;

import com.interviewbridge.entity.TechnologyMaster;
import com.interviewbridge.exception.DuplicateResourceException;
import com.interviewbridge.exception.ResourceNotFoundException;
import com.interviewbridge.repository.TechnologyMasterRepository;
import com.interviewbridge.request.TechnologyCreateRequest;
import com.interviewbridge.request.TechnologyUpdateRequest;
import com.interviewbridge.response.TechnologyResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TechnologyServiceImplTest {

    @Mock
    private TechnologyMasterRepository repository;

    @InjectMocks
    private TechnologyServiceImpl service;

    private TechnologyMaster mockTech;
    private UUID techId;
    private String techName;

    @BeforeEach
    void setUp() {
        techId = UUID.randomUUID();
        techName = "Java";
        mockTech = TechnologyMaster.builder()
                .id(techId)
                .technologyName(techName)
                .description("Java Programming Language")
                .isActive(true)
                .build();
    }

    @Test
    void createTechnology_Success() {
        TechnologyCreateRequest request = new TechnologyCreateRequest(techName, "Java Programming Language");
        when(repository.existsByTechnologyNameIgnoreCase(techName)).thenReturn(false);
        when(repository.save(any(TechnologyMaster.class))).thenReturn(mockTech);

        TechnologyResponse response = service.createTechnology(request);

        assertNotNull(response);
        assertEquals(techId, response.id());
        assertEquals(techName, response.technologyName());
        assertTrue(response.isActive());
        verify(repository, times(1)).save(any(TechnologyMaster.class));
    }

    @Test
    void createTechnology_DuplicateName_ThrowsException() {
        TechnologyCreateRequest request = new TechnologyCreateRequest(techName, "Java Programming Language");
        when(repository.existsByTechnologyNameIgnoreCase(techName)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.createTechnology(request));
        verify(repository, never()).save(any(TechnologyMaster.class));
    }

    @Test
    void updateTechnology_Success() {
        TechnologyUpdateRequest request = new TechnologyUpdateRequest("Python", "Python programming language");
        when(repository.findById(techId)).thenReturn(Optional.of(mockTech));
        when(repository.existsByTechnologyNameIgnoreCaseAndIdNot("Python", techId)).thenReturn(false);
        when(repository.save(mockTech)).thenReturn(mockTech);

        TechnologyResponse response = service.updateTechnology(techId, request);

        assertNotNull(response);
        assertEquals(techId, response.id());
        assertEquals("Python", mockTech.getTechnologyName());
        assertEquals("Python programming language", mockTech.getDescription());
    }

    @Test
    void updateTechnology_NotFound_ThrowsException() {
        TechnologyUpdateRequest request = new TechnologyUpdateRequest("Python", "Python programming language");
        when(repository.findById(techId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.updateTechnology(techId, request));
    }

    @Test
    void updateTechnology_DuplicateName_ThrowsException() {
        TechnologyUpdateRequest request = new TechnologyUpdateRequest("Python", "Python programming language");
        when(repository.findById(techId)).thenReturn(Optional.of(mockTech));
        when(repository.existsByTechnologyNameIgnoreCaseAndIdNot("Python", techId)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.updateTechnology(techId, request));
    }

    @Test
    void getTechnologyById_Success() {
        when(repository.findById(techId)).thenReturn(Optional.of(mockTech));

        TechnologyResponse response = service.getTechnologyById(techId);

        assertNotNull(response);
        assertEquals(techId, response.id());
    }

    @Test
    void getTechnologyById_NotFound_ThrowsException() {
        when(repository.findById(techId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getTechnologyById(techId));
    }

    @Test
    void getAllTechnologies_Success() {
        when(repository.findAll()).thenReturn(List.of(mockTech));

        List<TechnologyResponse> responses = service.getAllTechnologies();

        assertNotNull(responses);
        assertEquals(1, responses.size());
    }

    @Test
    void getActiveTechnologies_Success() {
        when(repository.findByIsActiveTrue()).thenReturn(List.of(mockTech));

        List<TechnologyResponse> responses = service.getActiveTechnologies();

        assertNotNull(responses);
        assertEquals(1, responses.size());
    }

    @Test
    void activateTechnology_Success() {
        mockTech.setIsActive(false);
        when(repository.findById(techId)).thenReturn(Optional.of(mockTech));

        service.activateTechnology(techId);

        assertTrue(mockTech.getIsActive());
        verify(repository, times(1)).save(mockTech);
    }

    @Test
    void deactivateTechnology_Success() {
        when(repository.findById(techId)).thenReturn(Optional.of(mockTech));

        service.deactivateTechnology(techId);

        assertFalse(mockTech.getIsActive());
        verify(repository, times(1)).save(mockTech);
    }
}
