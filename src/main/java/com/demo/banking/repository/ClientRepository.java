package com.demo.banking.repository;

import com.demo.banking.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, String> {
    // The primary key type is String because we're using CNP as the ID
}