package ru.olbreslavets.tgbank.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.olbreslavets.tgbank.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByPhone(String phone);

    Optional<User> findByTgId(Long tgId);

    @Query("""
            from User u
            join Account a on a.owner.id = u.id
            where a.number = :number
            """)
    Optional<User> findByAccountNumber(@Param("number") Long number);

}
