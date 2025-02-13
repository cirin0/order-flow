package org.flow.orderflow.repository;

import org.flow.orderflow.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
