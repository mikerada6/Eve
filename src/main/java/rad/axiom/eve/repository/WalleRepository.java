package rad.axiom.eve.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import rad.axiom.eve.mtg.Card;
import rad.axiom.eve.session.Walle;

import java.util.List;
import java.util.Optional;

@Repository
public interface WalleRepository  extends CrudRepository<Walle, String> {
    List<Walle> findAll();

    Optional<Walle> findById(String id);

}