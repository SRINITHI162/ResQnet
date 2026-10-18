package com.resqnet.service;

import com.resqnet.entity.Incident;
import com.resqnet.entity.Resource;
import com.resqnet.entity.ResourceAllocation;
import com.resqnet.enums.ResourceStatus;
import com.resqnet.exception.BadRequestException;
import com.resqnet.repository.ResourceAllocationRepository;
import com.resqnet.repository.ResourceRepository;
import jakarta.persistence.LockModeType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AllocationServiceTest {

    @Mock
    private ResourceRepository resourceRepository;
    @Mock
    private ResourceAllocationRepository allocationRepository;
    @Mock
    private AllocationEngine allocationEngine;
    @Mock
    private IncidentService incidentService;
    @Mock
    private IncidentTimelineService timelineService;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private AllocationService allocationService;

    @Test
    void rejectsNonPositiveAllocationLimitWithoutAccessingRepositories() {
        assertThrows(BadRequestException.class,
                () -> allocationService.allocateBestResources(1L, 0));
        verifyNoInteractions(resourceRepository, allocationRepository, allocationEngine,
                incidentService, timelineService, notificationService);
    }

    @Test
    void locksAvailableResourcesBeforePersistingAllocations() {
        Incident incident = Incident.builder()
                .id(1L)
                .latitude(10.0)
                .longitude(20.0)
                .build();
        Resource resource = Resource.builder()
                .id(2L)
                .resourceName("Rescue vehicle")
                .availableQuantity(1)
                .latitude(10.1)
                .longitude(20.1)
                .build();

        when(incidentService.getById(incident.getId())).thenReturn(incident);
        when(resourceRepository.findByStatusForUpdate(ResourceStatus.AVAILABLE))
                .thenReturn(List.of(resource));
        when(allocationEngine.rankCandidates(incident, List.of(resource))).thenReturn(List.of(resource));
        when(allocationEngine.score(incident, resource)).thenReturn(90.0);
        when(allocationRepository.save(any(ResourceAllocation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(resourceRepository.save(resource)).thenReturn(resource);

        List<ResourceAllocation> result = allocationService.allocateBestResources(incident.getId(), 1);

        assertEquals(1, result.size());
        assertEquals(0, resource.getAvailableQuantity());
        verify(resourceRepository).findByStatusForUpdate(ResourceStatus.AVAILABLE);
        InOrder order = inOrder(resourceRepository, allocationRepository);
        order.verify(resourceRepository).findByStatusForUpdate(ResourceStatus.AVAILABLE);
        order.verify(resourceRepository).save(resource);
        order.verify(allocationRepository).save(any(ResourceAllocation.class));
    }

    @Test
    void repositoryUsesPessimisticWriteLockForAllocationCandidates() throws Exception {
        Lock lock = ResourceRepository.class
                .getMethod("findByStatusForUpdate", ResourceStatus.class)
                .getAnnotation(Lock.class);

        assertEquals(LockModeType.PESSIMISTIC_WRITE, lock.value());
    }
}
