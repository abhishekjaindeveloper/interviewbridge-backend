package com.interviewbridge.serviceImpl;

import com.interviewbridge.entity.ExperienceMaster;
import com.interviewbridge.exception.DuplicateResourceException;
import com.interviewbridge.exception.ResourceNotFoundException;
import com.interviewbridge.repository.ExperienceMasterRepository;
import com.interviewbridge.request.ExperienceCreateRequest;
import com.interviewbridge.request.ExperienceUpdateRequest;
import com.interviewbridge.response.ExperienceResponse;
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
class ExperienceServiceImplTest {

    @Mock
    private ExperienceMasterRepository repository;

    @InjectMocks
    private ExperienceServiceImpl service;

    private ExperienceMaster mockExp;
    private UUID expId;
    private String label;

    @BeforeEach
    void setUp() {
        expId = UUID.randomUUID();
        label = "0-2 Years";
        mockExp = ExperienceMaster.builder()
                .id(expId)
                .experienceLabel(label)
                .isActive(true)
                .build();
    }

    @Test
    void createExperience_Success() {
        ExperienceCreateRequest request = new ExperienceCreateRequest(label);
        when(repository.existsByExperienceLabelIgnoreCase(label)).thenReturn(false);
        when(repository.save(any(ExperienceMaster.class))).thenReturn(mockExp);

        ExperienceResponse response = service.createExperience(request);

        assertNotNull(response);
        assertEquals(expId, response.id());
        assertEquals(label, response.experienceLabel());
        assertTrue(response.isActive());
        verify(repository, times(1)).save(any(ExperienceMaster.class));
    }

    @Test
    void createExperience_DuplicateLabel_ThrowsException() {
        ExperienceCreateRequest request = new ExperienceCreateRequest(label);
        when(repository.existsByExperienceLabelIgnoreCase(label)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.createExperience(request));
        verify(repository, never()).save(any(ExperienceMaster.class));
    }

    @Test
    void updateExperience_Success() {
        ExperienceUpdateRequest request = new ExperienceUpdateRequest("3-5 Years");
        when(repository.findById(expId)).thenReturn(Optional.of(mockExp));
        when(repository.existsByExperienceLabelIgnoreCaseAndIdNot("3-5 Years", expId)).thenReturn(false);
        when(repository.save(mockExp)).thenReturn(mockExp);

        ExperienceResponse response = service.updateExperience(expId, request);

        assertNotNull(response);
        assertEquals(expId, response.id());
        assertEquals("3-5 Years", mockExp.getExperienceLabel());
    }

    @Test
    void updateExperience_NotFound_ThrowsException() {
        ExperienceUpdateRequest request = new ExperienceUpdateRequest("3-5 Years");
        when(repository.findById(expId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.updateExperience(expId, request));
    }

    @Test
    void updateExperience_DuplicateLabel_ThrowsException() {
        ExperienceUpdateRequest request = new ExperienceUpdateRequest("3-5 Years");
        when(repository.findById(expId)).thenReturn(Optional.of(mockExp));
        when(repository.existsByExperienceLabelIgnoreCaseAndIdNot("3-5 Years", expId)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.updateExperience(expId, request));
    }

    @Test
    void getExperienceById_Success() {
        when(repository.findById(expId)).thenReturn(Optional.of(mockExp));

        ExperienceResponse response = service.getExperienceById(expId);

        assertNotNull(response);
        assertEquals(expId, response.id());
    }

    @Test
    void getExperienceById_NotFound_ThrowsException() {
        when(repository.findById(expId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getExperienceById(expId));
    }

    @Test
    void getAllExperiences_Success() {
        when(repository.findAll()).thenReturn(List.of(mockExp));

        List<ExperienceResponse> responses = service.getAllExperiences();

        assertNotNull(responses);
        assertEquals(1, responses.size());
    }

    @Test
    void getActiveExperiences_Success() {
        when(repository.findByIsActiveTrue()).thenReturn(List.of(mockExp));

        List<ExperienceResponse> responses = service.getActiveExperiences();

        assertNotNull(responses);
        assertEquals(1, responses.size());
    }

    @Test
    void activateExperience_Success() {
        mockExp.setIsActive(false);
        when(repository.findById(expId)).thenReturn(Optional.of(mockExp));

        service.activateExperience(expId);

        assertTrue(mockExp.getIsActive());
        verify(repository, times(1)).save(mockExp);
    }

    @Test
    void deactivateExperience_Success() {
        when(repository.findById(expId)).thenReturn(Optional.of(mockExp));

        service.deactivateExperience(expId);

        assertFalse(mockExp.getIsActive());
        verify(repository, times(1)).save(mockExp);
    }
}
