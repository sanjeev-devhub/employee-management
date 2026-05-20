package com.company.employeemanagement.repository;

import com.company.employeemanagement.entity.Title;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TitleRepository extends JpaRepository<Title, String> {

    boolean existsByTitle(String title);

    Optional<Title> findByTitle(String title);
}
