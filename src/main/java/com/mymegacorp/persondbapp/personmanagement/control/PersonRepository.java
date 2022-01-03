package com.mymegacorp.persondbapp.personmanagement.control;

import com.mymegacorp.persondbapp.personmanagement.entity.PersonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PersonRepository extends JpaRepository<PersonEntity, Long> {
    @Modifying
    @Query("DELETE FROM PersonEntity pe WHERE pe.id = :id")
    int deleteAllHavingId(@Param("id") final long id);
}
