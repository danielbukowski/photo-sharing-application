package com.danielbukowski.photosharing.Repository;

import com.danielbukowski.photosharing.Entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {

    @Query(
            "SELECT r FROM Role r " +
            "WHERE r.name = :name"
    )
    Role getByName(String name);

}
