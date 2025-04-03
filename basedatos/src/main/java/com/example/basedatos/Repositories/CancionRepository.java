package com.example.basedatos.Repositories;

import com.example.basedatos.entities.CancionesEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CancionRepository extends JpaRepository<CancionesEntity, UUID> {

    // Este método personalizado está bien, porque realiza una búsqueda específica
    Page<CancionesEntity> findAllByTituloContaining(String titulo, Pageable pageable);

    // No es necesario sobrecargar findAll(Pageable pageable) porque ya existe en JpaRepository
    // Puedes eliminar la siguiente línea:
    // Page<CancionesEntity> findAll(Pageable pageable);
}


