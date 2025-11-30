package com.example.haus.repository;

import com.example.haus.domain.entity.address.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {

    @Query("SELECT u FROM Address u WHERE u.user.id = :userId AND u.isDeleted = false")
    List<Address> getAddressByUserId(String userId);

    Optional<Address> findByIdAndIsDeletedFalse(Long id);

    Optional<Address> findByIdAndUserUsernameAndIsDeletedFalse(Long id, String username);
}
