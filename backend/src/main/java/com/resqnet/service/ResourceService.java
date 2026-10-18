package com.resqnet.service;

import com.resqnet.entity.Resource;
import com.resqnet.exception.ResourceNotFoundException;
import com.resqnet.repository.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceService {

    private final ResourceRepository resourceRepository;

    public Resource create(Resource resource) {
        if (resource.getAvailableQuantity() == null) {
            resource.setAvailableQuantity(resource.getQuantity());
        }
        return resourceRepository.save(resource);
    }

    public List<Resource> getAll() {
        return resourceRepository.findAll();
    }

    public Resource getById(Long id) {
        return resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found: " + id));
    }

    public Resource update(Long id, Resource updated) {
        Resource existing = getById(id);
        existing.setResourceName(updated.getResourceName());
        existing.setResourceType(updated.getResourceType());
        existing.setQuantity(updated.getQuantity());
        existing.setAvailableQuantity(updated.getAvailableQuantity());
        existing.setLocation(updated.getLocation());
        existing.setLatitude(updated.getLatitude());
        existing.setLongitude(updated.getLongitude());
        existing.setStatus(updated.getStatus());
        return resourceRepository.save(existing);
    }

    public void delete(Long id) {
        resourceRepository.deleteById(id);
    }
}
