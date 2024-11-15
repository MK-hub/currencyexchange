package com.example.currencyexchange.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.currencyexchange.model.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsByFirstNameAndLastName(String firstName, String lastName);
}
