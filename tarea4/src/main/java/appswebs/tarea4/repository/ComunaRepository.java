package appswebs.tarea4.repository;

import appswebs.tarea4.model.Comuna;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ComunaRepository extends JpaRepository<Comuna, Long> {
    Optional<Comuna> findByNombre(String nombre);
    List<Comuna> findByRegionIdOrderByNombreAsc(Long regionId);
}