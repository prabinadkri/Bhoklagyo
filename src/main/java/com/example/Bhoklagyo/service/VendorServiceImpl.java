package com.example.Bhoklagyo.service;

import com.example.Bhoklagyo.dto.VendorRequest;
import com.example.Bhoklagyo.dto.VendorResponse;
import com.example.Bhoklagyo.entity.Vendor;
import com.example.Bhoklagyo.exception.ResourceNotFoundException;
import com.example.Bhoklagyo.exception.DuplicateResourceException;
import com.example.Bhoklagyo.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VendorServiceImpl implements VendorService {

    @Autowired
    private VendorRepository vendorRepository;

    @Override
    @Transactional
    public VendorResponse createVendor(VendorRequest vendorRequest) {
        // Check if PAN number already exists
        if (vendorRepository.existsByPanNumber(vendorRequest.getPanNumber())) {
            throw new DuplicateResourceException("Vendor with PAN number " + vendorRequest.getPanNumber() + " already exists");
        }

        // Check if email already exists
        if (vendorRequest.getEmail() != null && vendorRepository.existsByEmail(vendorRequest.getEmail())) {
            throw new DuplicateResourceException("Vendor with email " + vendorRequest.getEmail() + " already exists");
        }

        Vendor vendor = new Vendor();
        vendor.setPanNumber(vendorRequest.getPanNumber());
        vendor.setBusinessName(vendorRequest.getBusinessName());
        vendor.setAccountNumber(vendorRequest.getAccountNumber());
        vendor.setIsVatRegistered(vendorRequest.getIsVatRegistered());
        vendor.setEmail(vendorRequest.getEmail());
        vendor.setPhoneNumber(vendorRequest.getPhoneNumber());
        vendor.setAddress(vendorRequest.getAddress());

        Vendor savedVendor = vendorRepository.save(vendor);
        return mapToResponse(savedVendor);
    }

    @Override
    public VendorResponse getVendorById(Long id) {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with id: " + id));
        return mapToResponse(vendor);
    }

    @Override
    public List<VendorResponse> getAllVendors() {
        return vendorRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public VendorResponse updateVendor(Long id, VendorRequest vendorRequest) {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with id: " + id));

        // Check if updating PAN number to an existing one
        if (!vendor.getPanNumber().equals(vendorRequest.getPanNumber()) && 
            vendorRepository.existsByPanNumber(vendorRequest.getPanNumber())) {
            throw new DuplicateResourceException("Vendor with PAN number " + vendorRequest.getPanNumber() + " already exists");
        }

        // Check if updating email to an existing one
        if (vendorRequest.getEmail() != null && 
            !vendorRequest.getEmail().equals(vendor.getEmail()) &&
            vendorRepository.existsByEmail(vendorRequest.getEmail())) {
            throw new DuplicateResourceException("Vendor with email " + vendorRequest.getEmail() + " already exists");
        }

        vendor.setPanNumber(vendorRequest.getPanNumber());
        vendor.setBusinessName(vendorRequest.getBusinessName());
        vendor.setAccountNumber(vendorRequest.getAccountNumber());
        vendor.setIsVatRegistered(vendorRequest.getIsVatRegistered());
        vendor.setEmail(vendorRequest.getEmail());
        vendor.setPhoneNumber(vendorRequest.getPhoneNumber());
        vendor.setAddress(vendorRequest.getAddress());

        Vendor updatedVendor = vendorRepository.save(vendor);
        return mapToResponse(updatedVendor);
    }

    @Override
    @Transactional
    public void deleteVendor(Long id) {
        if (!vendorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Vendor not found with id: " + id);
        }
        vendorRepository.deleteById(id);
    }

    private VendorResponse mapToResponse(Vendor vendor) {
        VendorResponse response = new VendorResponse();
        response.setId(vendor.getId());
        response.setPanNumber(vendor.getPanNumber());
        response.setBusinessName(vendor.getBusinessName());
        response.setAccountNumber(vendor.getAccountNumber());
        response.setIsVatRegistered(vendor.getIsVatRegistered());
        response.setEmail(vendor.getEmail());
        response.setPhoneNumber(vendor.getPhoneNumber());
        response.setAddress(vendor.getAddress());
        return response;
    }
}
