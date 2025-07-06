package ru.shift.userimporter.core.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.shift.userimporter.core.model.Client;
import org.springframework.data.domain.Pageable;

import java.util.List;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Integer> {
    Optional<Client> findByPhone(String phone);

    @Query("""
           SELECT c
           FROM   Client c
           WHERE  (:phone      IS NULL OR c.phone = :phone)
             AND  (COALESCE(:name,      '') = '' OR c.firstName ILIKE CONCAT('%', :name,      '%'))
             AND  (COALESCE(:lastName,  '') = '' OR c.lastName  ILIKE CONCAT('%', :lastName,  '%'))
             AND  (COALESCE(:email,     '') = '' OR c.email     ILIKE CONCAT('%', :email,     '%'))
           """)
    Page<Client> search(@Param("phone")    String   phone,
                        @Param("name")     String name,
                        @Param("lastName") String lastName,
                        @Param("email")    String email,
                        Pageable pageable);
}