package com.example.basedatos.controllers;

import com.example.basedatos.entities.CancionesEntity;
import com.example.basedatos.Services.CancionesService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/canciones")
@Validated
public class CancionesController {

    private final CancionesService cancionesService;

    public CancionesController(CancionesService cancionesService) {
        this.cancionesService = cancionesService;
    }

    @GetMapping
    public ResponseEntity<?> getAllCanciones(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size,
        @RequestParam(defaultValue = "titulo,asc") String[] sort) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(parseSort(sort)));
            return cancionesService.getAllCanciones(pageable);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid sorting direction. Use 'asc' or 'desc'.");
        }
    }

    private Sort.Order parseSort(String[] sort) {
        if (sort.length < 2) {
            throw new IllegalArgumentException("Sort parameter must have both field and direction (e.g., 'titulo,desc').");
        }

        String property = sort[0];
        String direction = sort[1].toLowerCase();

        List<String> validDirections = Arrays.asList("asc", "desc");
        if (!validDirections.contains(direction)) {
            throw new IllegalArgumentException("Invalid sort direction. Use 'asc' or 'desc'.");
        }

        return new Sort.Order(Sort.Direction.fromString(direction), property);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCancionById(@PathVariable UUID id) {
        return cancionesService.getCancionById(id);
    }

    @GetMapping("/search")
    public ResponseEntity<?> getCancionesByTitle(
        @RequestParam String titulo,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size,
        @RequestParam(defaultValue = "titulo,asc") String[] sort) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(parseSort(sort)));
        return cancionesService.getCancionesByTitle(titulo, pageable);
    }

    @PostMapping
    public ResponseEntity<?> insertCancion(@Valid @RequestBody CancionesEntity cancionesEntity) {
        return cancionesService.addCancion(cancionesEntity);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCancion(@PathVariable UUID id, @Valid @RequestBody CancionesEntity cancionesEntity) {
        return cancionesService.updateCancion(id, cancionesEntity);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCancion(@PathVariable UUID id) {
        return cancionesService.deleteCancion(id);
    }
}

