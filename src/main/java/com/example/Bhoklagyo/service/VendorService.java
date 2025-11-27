package com.example.Bhoklagyo.service;

import com.example.Bhoklagyo.dto.VendorRequest;
import com.example.Bhoklagyo.dto.VendorResponse;
import java.util.List;

public interface VendorService {
    VendorResponse createVendor(VendorRequest vendorRequest);
    VendorResponse getVendorById(Long id);
    List<VendorResponse> getAllVendors();
    VendorResponse updateVendor(Long id, VendorRequest vendorRequest);
    void deleteVendor(Long id);
}
