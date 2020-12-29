package rad.axiom.eve.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import rad.axiom.eve.mtg.Card;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends CrudRepository<Card, String> {
    List<Card> findAll();

    Optional<Card> findById(String id);

    List<Card> findAllBySetCode(String set);

    @Query(value = "SELECT DISTINCT set_name FROM card", nativeQuery = true)
    List<String> findAllSets();
}