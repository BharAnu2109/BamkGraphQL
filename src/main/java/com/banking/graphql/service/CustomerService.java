package com.banking.graphql.service;

import com.banking.graphql.exception.DuplicateResourceException;
import com.banking.graphql.exception.ResourceNotFoundException;
import com.banking.graphql.model.Customer;
import com.banking.graphql.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerService {
    
    private final CustomerRepository customerRepository;
    
    public List<Customer> getAllCustomers() {
        log.info("Fetching all customers");
        return customerRepository.findAll();
    }
    
    public Customer getCustomerById(Long id) {
        log.info("Fetching customer with id: {}", id);
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
    }
    
    public Customer getCustomerByEmail(String email) {
        log.info("Fetching customer with email: {}", email);
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with email: " + email));
    }
    
    @Transactional
    public Customer createCustomer(Customer customer) {
        log.info("Creating new customer with email: {}", customer.getEmail());
        
        if (customerRepository.existsByEmail(customer.getEmail())) {
            throw new DuplicateResourceException("Customer already exists with email: " + customer.getEmail());
        }
        
        return customerRepository.save(customer);
    }
    
    @Transactional
    public Customer updateCustomer(Long id, Customer customerDetails) {
        log.info("Updating customer with id: {}", id);
        
        Customer customer = getCustomerById(id);
        
        if (customerDetails.getFirstName() != null) {
            customer.setFirstName(customerDetails.getFirstName());
        }
        if (customerDetails.getLastName() != null) {
            customer.setLastName(customerDetails.getLastName());
        }
        if (customerDetails.getEmail() != null && !customerDetails.getEmail().equals(customer.getEmail())) {
            if (customerRepository.existsByEmail(customerDetails.getEmail())) {
                throw new DuplicateResourceException("Email already in use: " + customerDetails.getEmail());
            }
            customer.setEmail(customerDetails.getEmail());
        }
        if (customerDetails.getPhone() != null) {
            customer.setPhone(customerDetails.getPhone());
        }
        if (customerDetails.getAddress() != null) {
            customer.setAddress(customerDetails.getAddress());
        }
        
        return customerRepository.save(customer);
    }
    
    @Transactional
    public boolean deleteCustomer(Long id) {
        log.info("Deleting customer with id: {}", id);
        
        Customer customer = getCustomerById(id);
        customerRepository.delete(customer);
        return true;
    }
}
