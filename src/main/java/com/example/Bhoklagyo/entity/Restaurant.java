package com.example.Bhoklagyo.entity;
import java.util.*;

import jakarta.persistence.*;

@Entity
@Table(name = "restaurants")
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    private Double latitude;
    
    private Double longitude;
    
    @Column(name = "contact_number")
    private String contactNumber;
    
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
    
    public Double getLatitude() {
        return latitude;
    }
    
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    
    public Double getLongitude() {
        return longitude;
    }
    
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
    
    public String getContactNumber() {
        return contactNumber;
    }
    
    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
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
}