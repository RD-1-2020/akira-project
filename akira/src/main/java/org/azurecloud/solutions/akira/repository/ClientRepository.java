package org.azurecloud.solutions.akira.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.azurecloud.solutions.akira.model.entity.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, String> {
}

