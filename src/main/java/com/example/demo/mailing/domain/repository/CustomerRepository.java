package com.example.demo.mailing.domain.repository;

import com.example.demo.mailing.domain.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

}
