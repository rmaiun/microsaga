package io.github.simpleservice.repository;

import io.github.simpleservice.domain.Account;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends CrudRepository<Account, Long> {

  Optional<Account> findByCode(String code);
}
