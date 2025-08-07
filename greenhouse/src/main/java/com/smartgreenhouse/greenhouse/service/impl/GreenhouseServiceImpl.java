package com.smartgreenhouse.greenhouse.service.impl;

import com.smartgreenhouse.greenhouse.dto.greenhouse.CreateGreenhouseDTO;
import com.smartgreenhouse.greenhouse.dto.greenhouse.GreenhouseDTO;
import com.smartgreenhouse.greenhouse.dto.greenhouse.UpdateGreenhouseDTO;
import com.smartgreenhouse.greenhouse.entity.Greenhouse;
import com.smartgreenhouse.greenhouse.exceptions.NameAlreadyExistsException;
import com.smartgreenhouse.greenhouse.exceptions.ObjectNotFoundException;
import com.smartgreenhouse.greenhouse.repository.GreenhouseRepository;
import com.smartgreenhouse.greenhouse.service.GreenhouseService;
import com.smartgreenhouse.greenhouse.util.greenhouseMapper.GreenhouseMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GreenhouseServiceImpl implements GreenhouseService {

    private final GreenhouseRepository greenhouseRepository;
    private final GreenhouseMapper greenhouseMapper;

    public GreenhouseServiceImpl(GreenhouseRepository greenhouseRepository,
                                 GreenhouseMapper greenhouseMapper) {
        this.greenhouseRepository = greenhouseRepository;
        this.greenhouseMapper = greenhouseMapper;
    }

    @Override
    public GreenhouseDTO getGreenhouseById(Long id) {
        Greenhouse greenhouse = greenhouseRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Greenhouse not found with ID: " + id));
        return greenhouseMapper.toDto(greenhouse);
    }

    @Override
    public List<GreenhouseDTO> getAllGreenhouses() {
        return greenhouseRepository.findAll().stream()
                .map(greenhouseMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public GreenhouseDTO createGreenhouse(CreateGreenhouseDTO dto) {
        String name = dto.getName();
        if (greenhouseRepository.existsByName(name)) {
            throw new NameAlreadyExistsException("Greenhouse with this name: " + name + ", already exists");
        }
        Greenhouse greenhouse = greenhouseMapper.toEntity(dto);
        greenhouseRepository.save(greenhouse);
        return greenhouseMapper.toDto(greenhouse);
    }

    @Override
    public GreenhouseDTO updateGreenhouse(Long id, UpdateGreenhouseDTO dto) {
        Greenhouse greenhouse = greenhouseRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException("Greenhouse not found with ID: " + id));
        String updatedName = dto.getName();
        if (greenhouseRepository.existsByNameAndIdNot(updatedName, id)) {
            throw new NameAlreadyExistsException("Greenhouse with this name: " + updatedName + " already exists");
        }
        greenhouseMapper.updateEntity(dto, greenhouse);
        Greenhouse updated = greenhouseRepository.save(greenhouse);
        return greenhouseMapper.toDto(updated);
    }

    @Override
    public void deleteGreenhouse(Long id) {
        if (!greenhouseRepository.existsById(id)) {
            throw new ObjectNotFoundException("Greenhouse not found with ID: " + id);
        }
        greenhouseRepository.deleteById(id);
    }
}
