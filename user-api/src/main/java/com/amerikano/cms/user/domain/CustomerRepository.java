package com.amerikano.cms.user.domain;

import com.amerikano.cms.user.domain.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
