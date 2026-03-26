package appswebs.tarea4.repository;

import appswebs.tarea4.model.AvisoAdopcion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AvisoAdopcionRepository extends JpaRepository<AvisoAdopcion, Long> {
    
    List<AvisoAdopcion> findTop5ByOrderByFechaIngresoDesc();
    
    Page<AvisoAdopcion> findAllByOrderByFechaIngresoDesc(Pageable pageable);
    
    @Query("SELECT DATE(a.fechaIngreso) as fecha, COUNT(a.id) as cantidad " +
           "FROM AvisoAdopcion a " +
           "GROUP BY DATE(a.fechaIngreso) " +
           "ORDER BY fecha")
    List<Object[]> countByDia();
    
    @Query("SELECT a.tipo as tipo, COUNT(a.id) as cantidad " +
           "FROM AvisoAdopcion a " +
           "GROUP BY a.tipo")
    List<Object[]> countByTipo();
    
    @Query("SELECT MONTH(a.fechaIngreso) as mes, a.tipo as tipo, COUNT(a.id) as cantidad " +
           "FROM AvisoAdopcion a " +
           "GROUP BY MONTH(a.fechaIngreso), a.tipo " +
           "ORDER BY mes")
    List<Object[]> countByMesAndTipo();
}