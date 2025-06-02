package com.fintara.services;

import com.fintara.exceptions.CustomException;
import com.fintara.models.Plafond;
import com.fintara.repositories.PlafondRepository;
import com.fintara.utils.NameNormalizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlafondServiceTest {

    @Mock
    private PlafondRepository plafondRepository;

    private NameNormalizer nameNormalizer;

    private PlafondService plafondService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        nameNormalizer = new NameNormalizer(); // pakai instance asli
        plafondService = new PlafondService(plafondRepository, nameNormalizer);
    }

    @Test
    void createPlafond_shouldSaveSuccessfully() {
        // Arrange
        Plafond request = new Plafond();
        request.setName("  test plafOnd ");
        request.setMinTenor(6);
        request.setMaxTenor(12);
        request.setMaxAmount(BigDecimal.valueOf(1000000));
        request.setInterestRate(BigDecimal.valueOf(5.5));

        when(plafondRepository.findByName("Test Plafond")).thenReturn(Optional.empty());
        when(plafondRepository.save(any(Plafond.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Plafond result = plafondService.createPlafond(request);

        // Assert
        assertEquals("Test Plafond", result.getName()); // normalized name
        assertEquals(6, result.getMinTenor());
        assertEquals(12, result.getMaxTenor());
        verify(plafondRepository).findByName("Test Plafond");
        verify(plafondRepository).save(any(Plafond.class));
    }

    @Test
    void createPlafond_shouldThrow_whenNameAlreadyExists() {
        // Arrange
        Plafond request = new Plafond();
        request.setName("Existing Name");
        request.setMinTenor(6);
        request.setMaxTenor(12);

        when(plafondRepository.findByName("Existing Name")).thenReturn(Optional.of(new Plafond()));

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class,
                () -> plafondService.createPlafond(request));

        assertEquals("Nama plafond sudah digunakan", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void createPlafond_shouldThrow_whenMinTenorGreaterThanMaxTenor() {
        // Arrange
        Plafond request = new Plafond();
        request.setName("New Name");
        request.setMinTenor(13);
        request.setMaxTenor(12);

        when(plafondRepository.findByName("New Name")).thenReturn(Optional.empty());

        // Act & Assert
        CustomException exception = assertThrows(CustomException.class,
                () -> plafondService.createPlafond(request));

        assertEquals("Minimal tenor tidak boleh lebih besar dari maksimal tenor", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }
}
