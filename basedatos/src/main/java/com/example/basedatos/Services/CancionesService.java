package com.example.basedatos.Services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.basedatos.Repositories.CancionRepository;
import com.example.basedatos.entities.CancionesEntity;

import java.util.*;

@Service
public class CancionesService {

    private final CancionRepository cancionRepository;

    public CancionesService(CancionRepository cancionRepository) {
        this.cancionRepository = cancionRepository;
    }

    public ResponseEntity<?> getAllCanciones(Pageable pageable) {
        Page<CancionesEntity> canciones = cancionRepository.findAll(pageable);
        return getResponseEntity(canciones);
    }

    public ResponseEntity<?> getCancionById(UUID id) {
        Optional<CancionesEntity> cancion = cancionRepository.findById(id);
        if (cancion.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("Status", String.format("Song with ID %s not found.", id));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.ok(Collections.singletonMap("Song", cancion.get()));
    }

    public ResponseEntity<?> getCancionesByTitle(String titulo, Pageable pageable) {
        Page<CancionesEntity> canciones = cancionRepository.findAllByTituloContaining(titulo, pageable);
        return getResponseEntity(canciones);
    }

    private ResponseEntity<?> getResponseEntity(Page<CancionesEntity> canciones) {
        Map<String, Object> response = new HashMap<>();
        response.put("TotalElements", canciones.getTotalElements());
        response.put("TotalPages", canciones.getTotalPages());
        response.put("CurrentPage", canciones.getNumber());
        response.put("NumberOfElements", canciones.getNumberOfElements());
        response.put("Canciones", canciones.getContent());
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> addCancion(CancionesEntity cancionToAdd) {
        Page<CancionesEntity> canciones = cancionRepository.findAllByTituloContaining(
                cancionToAdd.getTitulo(),
                Pageable.unpaged());
        if (canciones.getTotalElements() > 0) {
            return new ResponseEntity<>(Collections.singletonMap("Status", "Song already exists"), HttpStatus.CONFLICT);
        } else {
            CancionesEntity savedCancion = cancionRepository.save(cancionToAdd);
            return new ResponseEntity<>(Collections.singletonMap("Status", "Added song with ID " + savedCancion.getId()), HttpStatus.CREATED);
        }
    }

    public ResponseEntity<?> updateCancion(UUID id, CancionesEntity cancionToUpdate) {
        Optional<CancionesEntity> cancion = cancionRepository.findById(id);
        if (cancion.isEmpty()) {
            return new ResponseEntity<>(Collections.singletonMap("Status", "Song not found"), HttpStatus.NOT_FOUND);
        }
        CancionesEntity existingCancion = cancion.get();

        existingCancion.setTitulo(cancionToUpdate.getTitulo());
        existingCancion.setArtista(cancionToUpdate.getArtista());
        existingCancion.setGenero(cancionToUpdate.getGenero());
        existingCancion.setDuracion(cancionToUpdate.getDuracion());

        cancionRepository.save(existingCancion);

        return ResponseEntity.ok(Collections.singletonMap("Status", "Updated song with ID " + existingCancion.getId()));
    }

    public ResponseEntity<?> deleteCancion(UUID id) {
        Optional<CancionesEntity> cancion = cancionRepository.findById(id);
        if (cancion.isEmpty()) {
            return new ResponseEntity<>(Collections.singletonMap("Status", "Song not found"), HttpStatus.NOT_FOUND);
        }
        cancionRepository.deleteById(id);
        return ResponseEntity.ok(Collections.singletonMap("Status", "Deleted song with ID " + id));
    }
}
