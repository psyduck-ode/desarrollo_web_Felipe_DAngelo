package appswebs.tarea4.repository;

import appswebs.tarea4.model.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
    List<Comentario> findByAvisoIdOrderByFechaDesc(Long avisoId);
}