package ru.olbreslavets.tgbank.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.olbreslavets.tgbank.entity.Payment;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("""
            from Payment p
            join fetch p.payer payer
            join fetch p.payee payee
            where payer.owner.tgId = :tgId 
                or payee.owner.tgId = :tgId
            order by p.operDate desc
            """)
    List<Payment> getAllByUser(@Param("tgId") Long tgId, Pageable pageable);

}
