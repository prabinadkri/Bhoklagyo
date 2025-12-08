package com.example.Bhoklagyo.service;

import com.example.Bhoklagyo.dto.RestaurantRequestDto;
import com.example.Bhoklagyo.dto.RestaurantRequestResponse;
import com.example.Bhoklagyo.entity.Request;
import com.example.Bhoklagyo.entity.RequestStatus;
import com.example.Bhoklagyo.entity.User;
import com.example.Bhoklagyo.exception.ResourceNotFoundException;
import com.example.Bhoklagyo.repository.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.Bhoklagyo.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RestaurantRequestServiceImpl implements RestaurantRequestService {

    @Autowired
    private RequestRepository requestRepository;
   
    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public RestaurantRequestResponse createRequest(RestaurantRequestDto requestDto) {
        if(requestDto.getEmailAddress() == null || requestDto.getEmailAddress().isEmpty()) {
            User currentUser = getCurrentUser();
            requestDto.setEmailAddress(currentUser.getEmail());
        }
        Request request = new Request();
        request.setName(requestDto.getName());
        request.setRestaurantName(requestDto.getRestaurantName());
        request.setContactNumber(requestDto.getContactNumber());
        request.setEmailAddress(requestDto.getEmailAddress());
        request.setDetails(requestDto.getDetails());
        request.setStatus(RequestStatus.PENDING);

        Request savedRequest = requestRepository.save(request);
        return mapToResponse(savedRequest);
    }

    @Override
    public List<RestaurantRequestResponse> getAllRequests() {
        return requestRepository.findAllByOrderByRequestIdDesc().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<RestaurantRequestResponse> getRequestsByStatus(RequestStatus status) {
        return requestRepository.findByStatusOrderByRequestIdDesc(status).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public RestaurantRequestResponse getRequestById(Long id) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found with id: " + id));
        return mapToResponse(request);
    }

    @Override
    @Transactional
    public RestaurantRequestResponse updateRequestStatus(Long id, RequestStatus status) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found with id: " + id));
        
        request.setStatus(status);
        
        // If status is COMPLETED, delete the request
        if (status == RequestStatus.COMPLETED) {
            RestaurantRequestResponse response = mapToResponse(request);
            requestRepository.delete(request);
            return response;
        }
        
        Request updatedRequest = requestRepository.save(request);
        return mapToResponse(updatedRequest);
    }

    @Override
    @Transactional
    public void deleteRequest(Long id) {
        if (!requestRepository.existsById(id)) {
            throw new ResourceNotFoundException("Request not found with id: " + id);
        }
        requestRepository.deleteById(id);
    }

    private RestaurantRequestResponse mapToResponse(Request request) {
        RestaurantRequestResponse response = new RestaurantRequestResponse();
        response.setRequestId(request.getRequestId());
        response.setName(request.getName());
        response.setRestaurantName(request.getRestaurantName());
        response.setContactNumber(request.getContactNumber());
        response.setEmailAddress(request.getEmailAddress());
        response.setDetails(request.getDetails());
        response.setStatus(request.getStatus());
        return response;
    }
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }
}
