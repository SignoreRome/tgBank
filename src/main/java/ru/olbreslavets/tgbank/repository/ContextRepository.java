package ru.olbreslavets.tgbank.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.olbreslavets.tgbank.entity.Context;

@Repository
public interface ContextRepository extends CrudRepository<Context, String> {
}
