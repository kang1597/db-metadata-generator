package com.portfolio.dbmetadatagenerator.connection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DbConnectionRepository extends JpaRepository<DbConnection, Long> {
}