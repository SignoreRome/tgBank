package ru.olbreslavets.tgbank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.olbreslavets.tgbank.entity.Bank;

@Repository
public interface BankRepository extends JpaRepository<Bank, Long> {

    @Query("""
            select b from Bank b
            join fetch b.contacts
            where b.name = :name
            """
    )
    Bank findByName(@Param("name") String name);

}
