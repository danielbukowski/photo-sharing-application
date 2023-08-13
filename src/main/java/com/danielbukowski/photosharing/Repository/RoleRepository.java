package com.danielbukowski.photosharing.Repository;

import com.danielbukowski.photosharing.Entity.Role;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;

import java.util.UUID;

public interface RoleRepository extends ListCrudRepository<Role, UUID> {

    @Query(
            "SELECT r FROM Role r " +
            "WHERE r.name = :name"
    )
    Role getByName(String name);

}
