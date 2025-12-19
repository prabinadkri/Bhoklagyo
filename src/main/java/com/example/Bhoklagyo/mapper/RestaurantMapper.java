package com.example.Bhoklagyo.mapper;

import com.example.Bhoklagyo.dto.RestaurantRequest;
import com.example.Bhoklagyo.dto.RestaurantResponse;
import com.example.Bhoklagyo.entity.CuisineTag;
import com.example.Bhoklagyo.entity.DietaryTag;
import com.example.Bhoklagyo.entity.Restaurant;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RestaurantMapper {
    
    public Restaurant toEntity(RestaurantRequest request) {
        Restaurant restaurant = new Restaurant();
        restaurant.setName(request.getName());
        // Convert latitude/longitude from request into a Point (SRID 4326)
        if (request.getLatitude() != null && request.getLongitude() != null) {
            GeometryFactory gf = new GeometryFactory(new PrecisionModel(), 4326);
            Point p = gf.createPoint(new Coordinate(request.getLongitude(), request.getLatitude()));
            restaurant.setLocation(p);
        }
        // Map address label if provided by frontend
        restaurant.setAddressLabel(request.getAddressLabel());
        // Parse opening/closing times if provided (expecting ISO local time, e.g. "08:30")
        if (request.getOpeningTime() != null && !request.getOpeningTime().isBlank()) {
            try {
                restaurant.setOpeningTime(LocalTime.parse(request.getOpeningTime()));
            } catch (Exception ignored) {
            }
        }
        if (request.getClosingTime() != null && !request.getClosingTime().isBlank()) {
            try {
                restaurant.setClosingTime(LocalTime.parse(request.getClosingTime()));
            } catch (Exception ignored) {
            }
        }
        if (request.getIsOpen() != null) {
            restaurant.setIsOpen(request.getIsOpen());
        }
        return restaurant;
    }
    
    public RestaurantResponse toResponse(Restaurant restaurant) {
        List<String> cuisineTagNames = restaurant.getCuisineTags() == null ? Collections.emptyList() : restaurant.getCuisineTags().stream()
            .map(CuisineTag::getName)
            .collect(Collectors.toList());

        List<String> dietaryTagNames = restaurant.getDietaryTags() == null ? Collections.emptyList() : restaurant.getDietaryTags().stream()
            .map(DietaryTag::getName)
            .collect(Collectors.toList());
        
        Double lat = null;
        Double lon = null;
        if (restaurant.getLocation() != null) {
            Point p = restaurant.getLocation();
            lat = p.getY();
            lon = p.getX();
        }

        String openingTime = restaurant.getOpeningTime() == null ? null : restaurant.getOpeningTime().toString();
        String closingTime = restaurant.getClosingTime() == null ? null : restaurant.getClosingTime().toString();
        return new RestaurantResponse(
            restaurant.getId(),
            restaurant.getName(),
            lat,
            lon,
            restaurant.getIsFeatured(),
            restaurant.getPhotoUrl(),
            cuisineTagNames,
            dietaryTagNames,
            restaurant.getRating(),
            openingTime,
            closingTime,
            restaurant.getIsOpen(),
            restaurant.getAddressLabel(),
            restaurant.getTotalCount() == null ? 0L : restaurant.getTotalCount(),
                restaurant.getAverageForOne()
        );
    }
}
