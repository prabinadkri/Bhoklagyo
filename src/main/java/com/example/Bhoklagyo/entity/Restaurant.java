package com.example.Bhoklagyo.entity;

import java.util.*;
import java.time.LocalTime;

import jakarta.persistence.*;
import org.locationtech.jts.geom.Point;
import org.hibernate.annotations.Type;
@Entity
@Table(name = "restaurants")
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    @Column(columnDefinition = "geometry(Point,4326)")
    private Point location;
    
    @Column(name = "contact_number")
    private String contactNumber;
    
    @Column(name = "is_featured", nullable = false, columnDefinition = "boolean default false")
    private Boolean isFeatured = false;
    
    @Column(name = "photo_url")
    private String photoUrl;
    
    @Column(name = "address_label")
    private String addressLabel;
    @Column(name = "opening_time")
    private LocalTime openingTime;

    @Column(name = "closing_time")
    private LocalTime closingTime;

    @Column(name = "is_open", nullable = false, columnDefinition = "boolean default false")
    private Boolean isOpen = false;
    @Column(name = "total_rating", columnDefinition = "bigint default 0")
    private Long totalRating = 0L;

    @Column(name = "total_count", columnDefinition = "bigint default 0")
    private Long totalCount = 0L;

    @Column(name = "rating", columnDefinition = "double precision default 0")
    private Double rating = 0.0;

    @Column(name="average_for_one", columnDefinition = "integer default 0")
    private int averageForOne=0;
    @ManyToOne
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;
    
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    private List<RestaurantMenuItem> restaurantMenuItems = new ArrayList<>();
    
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;
    
    @OneToMany(mappedBy = "employedRestaurant", cascade = CascadeType.ALL)
    private List<User> employees = new ArrayList<>();
    
    @ManyToMany
    @JoinTable(
        name = "restaurant_cuisine_tags",
        joinColumns = @JoinColumn(name = "restaurant_id"),
        inverseJoinColumns = @JoinColumn(name = "cuisine_tag_id")
    )
    private Set<CuisineTag> cuisineTags = new HashSet<>();
    
    @ManyToMany
    @JoinTable(
        name = "restaurant_dietary_tags",
        joinColumns = @JoinColumn(name = "restaurant_id"),
        inverseJoinColumns = @JoinColumn(name = "dietary_tag_id")
    )
    private Set<DietaryTag> dietaryTags = new HashSet<>();
    
    @ManyToMany(mappedBy = "restaurants")
    private Set<Document> documents = new HashSet<>();


    public Long getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    public Point getLocation() {
        return location;
    }

    public void setLocation(Point location) {
        this.location = location;
    }
    
    public String getContactNumber() {
        return contactNumber;
    }
    
    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
    
    public Boolean getIsFeatured() {
        return isFeatured;
    }
    
    public void setIsFeatured(Boolean isFeatured) {
        this.isFeatured = isFeatured;
    }
    
    public String getPhotoUrl() {
        return photoUrl;
    }
    
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getAddressLabel() {
        return addressLabel;
    }

    public void setAddressLabel(String addressLabel) {
        this.addressLabel = addressLabel;
    }

    public LocalTime getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(LocalTime openingTime) {
        this.openingTime = openingTime;
    }

    public LocalTime getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(LocalTime closingTime) {
        this.closingTime = closingTime;
    }

    public Boolean getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(Boolean isOpen) {
        this.isOpen = isOpen;
    }

    public Long getTotalRating() {
        return totalRating;
    }

    public void setTotalRating(Long totalRating) {
        this.totalRating = totalRating;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }
    
    public Vendor getVendor() {
        return vendor;
    }
    
    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }
    
    public List<RestaurantMenuItem> getRestaurantMenuItems() {
        return restaurantMenuItems;
    }
    
    public void setRestaurantMenuItems(List<RestaurantMenuItem> restaurantMenuItems) {
        this.restaurantMenuItems = restaurantMenuItems;
    }
    
    public User getOwner() {
        return owner;
    }
    
    public void setOwner(User owner) {
        this.owner = owner;
    }
    
    public List<User> getEmployees() {
        return employees;
    }
    
    public void setEmployees(List<User> employees) {
        this.employees = employees;
    }
    
    public Set<CuisineTag> getCuisineTags() {
        return cuisineTags;
    }
    
    public void setCuisineTags(Set<CuisineTag> cuisineTags) {
        this.cuisineTags = cuisineTags;
    }
    
    public Set<DietaryTag> getDietaryTags() {
        return dietaryTags;
    }
    
    public void setDietaryTags(Set<DietaryTag> dietaryTags) {
        this.dietaryTags = dietaryTags;
    }
    
    public Set<Document> getDocuments() {
        return documents;
    }
    
    public void setDocuments(Set<Document> documents) {
        this.documents = documents;
    }
    public int getAverageForOne() {
        return averageForOne;
    }
    public void setAverageForOne(int averageForOne) {this.averageForOne = averageForOne;}
}