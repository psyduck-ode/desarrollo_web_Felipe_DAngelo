package appswebs.tarea4.repository;

import appswebs.tarea4.model.ContactarPor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactarPorRepository extends JpaRepository<ContactarPor, Long> {
}