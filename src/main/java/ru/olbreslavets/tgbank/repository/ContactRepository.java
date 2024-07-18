package ru.olbreslavets.tgbank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.olbreslavets.tgbank.entity.Contact;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
}
