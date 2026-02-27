package com.upi.service;

import com.upi.dto.DisputeRequest;
import com.upi.dto.DisputeResponse;
import com.upi.model.Dispute;
import com.upi.repository.DisputeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DisputeServiceTest {

    @Mock
    private DisputeRepository disputeRepository;

    @InjectMocks
    private DisputeService disputeService;

    private DisputeRequest validRequest;
    private Dispute mockDispute;

    @BeforeEach
    void setUp() {
        validRequest = new DisputeRequest(
            "TXN123456",
            "merchant@upi",
            1000.0,
            "9876543210",
            "Transaction not received"
        );

        mockDispute = new Dispute();
        mockDispute.setId(1L);
        mockDispute.setTransactionId("TXN123456");
        mockDispute.setMerchantUPI("merchant@upi");
        mockDispute.setAmount(1000.0);
        mockDispute.setPhone("9876543210");
        mockDispute.setStatus(Dispute.DisputeStatus.PENDING);
    }

    @Test
    void testFileDisputeSuccess() {
        // Arrange
        when(disputeRepository.findByTransactionId("TXN123456")).thenReturn(Optional.empty());
        when(disputeRepository.save(any(Dispute.class))).thenReturn(mockDispute);

        // Act
        DisputeResponse response = disputeService.fileDispute(validRequest);

        // Assert
        assertNotNull(response);
        assertEquals("TXN123456", response.getTransactionId());
        assertEquals("merchant@upi", response.getMerchantUPI());
        assertEquals(1000.0, response.getAmount());
        assertNotNull(response.getDisputeId());
        assertNotNull(response.getStatus()); // Status will be set during filing

        // Verify repository was called
        verify(disputeRepository, times(1)).findByTransactionId("TXN123456");
        verify(disputeRepository, times(1)).save(any(Dispute.class));
    }

    @Test
    void testFileDisputeDuplicate() {
        // Arrange
        when(disputeRepository.findByTransactionId("TXN123456")).thenReturn(Optional.of(mockDispute));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> disputeService.fileDispute(validRequest));

        // Verify save was not called
        verify(disputeRepository, never()).save(any(Dispute.class));
    }

    @Test
    void testGetDisputeStatusSuccess() {
        // Arrange
        when(disputeRepository.findById(1L)).thenReturn(Optional.of(mockDispute));

        // Act
        DisputeResponse response = disputeService.getDisputeStatus(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("TXN123456", response.getTransactionId());
        assertEquals("merchant@upi", response.getMerchantUPI());

        // Verify repository was called
        verify(disputeRepository, times(1)).findById(1L);
    }

    @Test
    void testGetDisputeStatusNotFound() {
        // Arrange
        when(disputeRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> disputeService.getDisputeStatus(999L));

        // Verify repository was called
        verify(disputeRepository, times(1)).findById(999L);
    }

    @Test
    void testFileDisputeWithNullRequest() {
        // Act & Assert
        assertThrows(Exception.class, () -> disputeService.fileDispute(null));
    }

    @Test
    void testDisputeStatusMappingCorrect() {
        // Arrange
        mockDispute.setStatus(Dispute.DisputeStatus.VERIFIED_FAILURE);
        mockDispute.setNeftReference("NEFT1234567890");
        mockDispute.setRemarks("Transaction verified as failed");

        when(disputeRepository.findById(1L)).thenReturn(Optional.of(mockDispute));

        // Act
        DisputeResponse response = disputeService.getDisputeStatus(1L);

        // Assert
        assertEquals("VERIFIED_FAILURE", response.getStatus());
        assertEquals("NEFT1234567890", response.getNeftReference());
        assertEquals("Transaction verified as failed", response.getRemarks());
    }

    @Test
    void testDisputeIdFormattingCorrect() {
        // Arrange
        when(disputeRepository.findByTransactionId("TXN123456")).thenReturn(Optional.empty());
        when(disputeRepository.save(any(Dispute.class))).thenReturn(mockDispute);

        // Act
        DisputeResponse response = disputeService.fileDispute(validRequest);

        // Assert
        assertNotNull(response.getDisputeId());
        assertTrue(response.getDisputeId().startsWith("DIS_"));
        assertTrue(response.getDisputeId().length() >= 9); // DIS_XXXXXX format
    }
}
