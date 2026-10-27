package com.example.zensai.service;

import com.example.zensai.domain.Customer;
import com.example.zensai.dto.CustomerProfileDto;
import com.example.zensai.exception.ResourceNotFoundException;
import com.example.zensai.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerService extends SecuredService {

    private final CustomerRepository customerRepository;

    @Transactional(readOnly = true)
    public CustomerProfileDto getProfile() {

        Customer customer = getCurrentCustomer();

        CustomerProfileDto dto = new CustomerProfileDto();
        dto.setPhoneNumber(customer.getPhoneNumber());
        dto.setShippingAddress(customer.getShippingAddress());
        dto.setBillingAddress(customer.getBillingAddress());

        return dto;
    }

    public CustomerProfileDto updateProfile(CustomerProfileDto request) {

        Customer customer = getCurrentCustomer();

        customer.setPhoneNumber(request.getPhoneNumber());
        customer.setShippingAddress(request.getShippingAddress());
        customer.setBillingAddress(request.getBillingAddress());

        Customer saved = customerRepository.save(customer);

        CustomerProfileDto response = new CustomerProfileDto();
        response.setPhoneNumber(saved.getPhoneNumber());
        response.setShippingAddress(saved.getShippingAddress());
        response.setBillingAddress(saved.getBillingAddress());

        return response;
    }

    private Customer getCurrentCustomer() {

        return customerRepository.findByUserId(currentUserId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer profile not found"
                        ));
    }
}