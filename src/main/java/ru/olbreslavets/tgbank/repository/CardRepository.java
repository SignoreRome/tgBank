package ru.olbreslavets.tgbank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.olbreslavets.tgbank.entity.Card;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
}
