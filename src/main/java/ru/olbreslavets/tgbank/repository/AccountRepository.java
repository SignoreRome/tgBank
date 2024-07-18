package ru.olbreslavets.tgbank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.olbreslavets.tgbank.entity.Account;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query("""
            from Account as a
            join a.owner owner
            where owner.tgId = :tgId
            order by a.dateBegin desc
            """)
    List<Account> getAllByUser(@Param("tgId") Long tgId);

    @Query("""
            select count(*)
            from Account as a
            join a.owner owner
            where owner.tgId = :tgId
            """)
    int countByUser(@Param("tgId") Long tgId);

    Optional<Account> findByNumber(Long number);

    boolean existsByNumber(Long number);

    @Query("""
            from Account as a
            join a.owner owner
            where owner.tgId = :tgId
            and a.isMain = true
            order by a.dateBegin desc
            """)
    List<Account> getMainByUser(@Param("tgId") Long tgId);

    @Query("""
            from Account as a
            join a.owner owner
            where owner.phone = :phone
            and a.isMain = true
            order by a.dateBegin desc
            """)
    List<Account> getMainByUserPhone(@Param("phone") String phone);

    @Query("""
            from Account as a
            join a.owner owner
            where owner.phone = :phone
            order by a.dateBegin desc
            """)
    List<Account> getAllByUserPhone(@Param("phone") String phone);

}
