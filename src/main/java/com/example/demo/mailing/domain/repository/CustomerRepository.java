package com.example.demo.mailing.domain.repository;

import com.example.demo.mailing.domain.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    List<Customer> findByConsumerURI(String URI);
}
