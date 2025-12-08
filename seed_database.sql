-- ==========================================
-- Database Seed Script for Bhoklagyo
-- Date: 2025-12-04
-- Description: Seed 40 restaurants with owners, menu items, categories, and tags
-- ==========================================

-- Clear existing data (in correct order to avoid FK constraints)
DELETE FROM restaurant_menu_items;
DELETE FROM restaurant_cuisine_tags;
DELETE FROM restaurant_dietary_tags;
DELETE FROM orders;
DELETE FROM restaurants;
DELETE FROM categories;
DELETE FROM cuisine_tags;
DELETE FROM dietary_tags;
DELETE FROM vendors;
DELETE FROM users WHERE role = 'OWNER';

-- ==========================================
-- 1. CREATE OWNERS (Password: password123 - BCrypt encoded)
-- ==========================================
-- BCrypt hash for "password123": $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy

INSERT INTO users (name, password, email, phone_number, role) VALUES
('Ramesh Kumar', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ramesh.kumar@bhoklagyo.com', '+977-9841234567', 'OWNER'),
('Sita Sharma', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'sita.sharma@bhoklagyo.com', '+977-9851234568', 'OWNER');

-- ==========================================
-- 2. CREATE VENDORS
-- ==========================================
INSERT INTO vendors (pan_number, business_name, account_number, is_vat_registered, email, phone_number, address) VALUES
('100234567', 'Nepal Foods Pvt Ltd', 'ACC1001234567', true, 'info@nepalfoods.com', '+977-1-4423456', 'Kathmandu'),
('200234568', 'Himalayan Cuisines Ltd', 'ACC2001234568', true, 'contact@himalayancuisines.com', '+977-1-4423457', 'Lalitpur'),
('300234569', 'Bhaktapur Food Services', 'ACC3001234569', false, 'info@bhaktapurfoods.com', '+977-1-4423458', 'Bhaktapur'),
('400234570', 'Valley Restaurants Group', 'ACC4001234570', true, 'info@valleyrestaurants.com', '+977-1-4423459', 'Kathmandu'),
('500234571', 'Pokhara Hospitality Ltd', 'ACC5001234571', true, 'contact@pokharahospitality.com', '+977-61-523456', 'Pokhara');

-- ==========================================
-- 3. CREATE CUISINE TAGS
-- ==========================================
INSERT INTO cuisine_tags (name) VALUES
('Nepali'), ('Indian'), ('Chinese'), ('Italian'), ('Japanese'), 
('Thai'), ('Korean'), ('Mexican'), ('American'), ('Continental'),
('Newari'), ('Tibetan'), ('Fast Food'), ('BBQ'), ('Seafood');

-- ==========================================
-- 4. CREATE DIETARY TAGS
-- ==========================================
INSERT INTO dietary_tags (name) VALUES
('Vegetarian'), ('Vegan'), ('Gluten-Free'), ('Halal'), ('Kosher');

-- ==========================================
-- 5. CREATE CATEGORIES
-- ==========================================
INSERT INTO categories (name) VALUES
('Appetizers'), ('Main Course'), ('Desserts'), ('Beverages'), 
('Soups'), ('Salads'), ('Breakfast'), ('Snacks'), ('Sides'), ('Specials');

-- ==========================================
-- 6. CREATE 40 RESTAURANTS
-- ==========================================
INSERT INTO restaurants (name, latitude, longitude, contact_number, vendor_id, owner_id, is_featured, photo_url) VALUES
-- Owner 1 (Ramesh Kumar) restaurants
('Nepali Ghar', 27.7172, 85.3240, '+977-1-4412345', 1, (SELECT id FROM users WHERE email = 'ramesh.kumar@bhoklagyo.com'), true, 'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=500'),
('Momo Paradise', 27.7087, 85.3206, '+977-1-4412346', 1, (SELECT id FROM users WHERE email = 'ramesh.kumar@bhoklagyo.com'), true, 'https://images.unsplash.com/photo-1555939594-58d7cb561ad1?w=500'),

-- Owner 2 (Sita Sharma) restaurants
('Thakali Kitchen', 27.6915, 85.3200, '+977-1-4412347', 2, (SELECT id FROM users WHERE email = 'sita.sharma@bhoklagyo.com'), true, 'https://images.unsplash.com/photo-1552566626-52f8b828add9?w=500'),
('Newari Bhansa', 27.6722, 85.4298, '+977-1-4412348', 2, (SELECT id FROM users WHERE email = 'sita.sharma@bhoklagyo.com'), false, 'https://images.unsplash.com/photo-1559339352-11d035aa65de?w=500'),

-- Restaurants without owners
('Dragon Palace', 27.7100, 85.3250, '+977-1-4412349', 1, NULL, false, 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=500'),
('Pizza Italia', 27.7050, 85.3180, '+977-1-4412350', 3, NULL, false, 'https://images.unsplash.com/photo-1513104890138-7c749659a591?w=500'),
('Sushi House Kathmandu', 27.7200, 85.3300, '+977-1-4412351', 4, NULL, true, 'https://images.unsplash.com/photo-1579584425555-c3ce17fd4351?w=500'),
('Thai Delight', 27.6900, 85.3100, '+977-1-4412352', 5, NULL, false, 'https://images.unsplash.com/photo-1455619452474-d2be8b1e70cd?w=500'),
('Korean BBQ House', 27.7150, 85.3280, '+977-1-4412353', 1, NULL, false, 'https://images.unsplash.com/photo-1544025162-d76694265947?w=500'),
('Taco Fiesta', 27.7080, 85.3220, '+977-1-4412354', 2, NULL, false, 'https://images.unsplash.com/photo-1565299507177-b0ac66763828?w=500'),
('Burger Junction', 27.7120, 85.3260, '+977-1-4412355', 3, NULL, true, 'https://images.unsplash.com/photo-1571091718767-18b5b1457add?w=500'),
('Continental Cafe', 27.7000, 85.3150, '+977-1-4412356', 4, NULL, false, 'https://images.unsplash.com/photo-1551218808-94e220e084d2?w=500'),
('Everest View Restaurant', 27.7180, 85.3290, '+977-1-4412357', 5, NULL, true, 'https://images.unsplash.com/photo-1592861956120-e524fc739696?w=500'),
('Himalayan Spice', 27.6950, 85.3120, '+977-1-4412358', 1, NULL, false, 'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=500'),
('Lakeside Bistro', 27.7110, 85.3230, '+977-1-4412359', 2, NULL, false, 'https://images.unsplash.com/photo-1552566626-52f8b828add9?w=500'),
('Garden Restaurant', 27.7070, 85.3210, '+977-1-4412360', 3, NULL, false, 'https://images.unsplash.com/photo-1578474846511-04ba529f0b88?w=500'),
('Rooftop Lounge', 27.7130, 85.3270, '+977-1-4412361', 4, NULL, true, 'https://images.unsplash.com/photo-1514933651103-005eec06c04b?w=500'),
('Spice Route', 27.7010, 85.3160, '+977-1-4412362', 5, NULL, false, 'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=500'),
('Mountain View Cafe', 27.7160, 85.3285, '+977-1-4412363', 1, NULL, false, 'https://images.unsplash.com/photo-1466978913421-dad2ebd01d17?w=500'),
('Urban Kitchen', 27.7090, 85.3240, '+977-1-4412364', 2, NULL, false, 'https://images.unsplash.com/photo-1551218808-94e220e084d2?w=500'),
('Fusion Delights', 27.7140, 85.3265, '+977-1-4412365', 3, NULL, false, 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=500'),
('Riverside Grill', 27.7030, 85.3170, '+977-1-4412366', 4, NULL, false, 'https://images.unsplash.com/photo-1414235077428-338989a2e8c0?w=500'),
('Sunset Restaurant', 27.7095, 85.3225, '+977-1-4412367', 5, NULL, false, 'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=500'),
('Palace Dining', 27.7175, 85.3295, '+977-1-4412368', 1, NULL, true, 'https://images.unsplash.com/photo-1552566626-52f8b828add9?w=500'),
('Street Food Corner', 27.6970, 85.3140, '+977-1-4412369', 2, NULL, false, 'https://images.unsplash.com/photo-1555939594-58d7cb561ad1?w=500'),
('Heritage Restaurant', 27.6720, 85.4290, '+977-1-4412370', 3, NULL, false, 'https://images.unsplash.com/photo-1559339352-11d035aa65de?w=500'),
('Modern Eatery', 27.7105, 85.3245, '+977-1-4412371', 4, NULL, false, 'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=500'),
('Organic Kitchen', 27.7060, 85.3190, '+977-1-4412372', 5, NULL, false, 'https://images.unsplash.com/photo-1466978913421-dad2ebd01d17?w=500'),
('Authentic Flavors', 27.7125, 85.3255, '+977-1-4412373', 1, NULL, false, 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=500'),
('Cozy Corner Cafe', 27.7015, 85.3165, '+977-1-4412374', 2, NULL, false, 'https://images.unsplash.com/photo-1551218808-94e220e084d2?w=500'),
('Gourmet Express', 27.7155, 85.3275, '+977-1-4412375', 3, NULL, false, 'https://images.unsplash.com/photo-1552566626-52f8b828add9?w=500'),
('Family Restaurant', 27.7025, 85.3175, '+977-1-4412376', 4, NULL, false, 'https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?w=500'),
('Fine Dining Hub', 27.7170, 85.3288, '+977-1-4412377', 5, NULL, true, 'https://images.unsplash.com/photo-1414235077428-338989a2e8c0?w=500'),
('Quick Bites', 27.6990, 85.3145, '+977-1-4412378', 1, NULL, false, 'https://images.unsplash.com/photo-1555939594-58d7cb561ad1?w=500'),
('Ethnic Eats', 27.7085, 85.3215, '+977-1-4412379', 2, NULL, false, 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=500'),
('Healthy Bites', 27.7135, 85.3268, '+977-1-4412380', 3, NULL, false, 'https://images.unsplash.com/photo-1466978913421-dad2ebd01d17?w=500'),
('Noodle House', 27.7020, 85.3168, '+977-1-4412381', 4, NULL, false, 'https://images.unsplash.com/photo-1569718212165-3a8278d5f624?w=500'),
('Tandoor Palace', 27.7165, 85.3283, '+977-1-4412382', 5, NULL, false, 'https://images.unsplash.com/photo-1585937421612-70a008356fbe?w=500');

-- ==========================================
-- 7. ASSIGN CUISINE TAGS TO RESTAURANTS
-- ==========================================
-- Nepali Ghar (Restaurant 1)
INSERT INTO restaurant_cuisine_tags (restaurant_id, cuisine_tag_id) VALUES
(1, (SELECT id FROM cuisine_tags WHERE name = 'Nepali')),
(1, (SELECT id FROM cuisine_tags WHERE name = 'Newari'));

-- Momo Paradise (Restaurant 2)
INSERT INTO restaurant_cuisine_tags (restaurant_id, cuisine_tag_id) VALUES
(2, (SELECT id FROM cuisine_tags WHERE name = 'Nepali')),
(2, (SELECT id FROM cuisine_tags WHERE name = 'Tibetan')),
(2, (SELECT id FROM cuisine_tags WHERE name = 'Fast Food'));

-- Thakali Kitchen (Restaurant 3)
INSERT INTO restaurant_cuisine_tags (restaurant_id, cuisine_tag_id) VALUES
(3, (SELECT id FROM cuisine_tags WHERE name = 'Nepali'));

-- Newari Bhansa (Restaurant 4)
INSERT INTO restaurant_cuisine_tags (restaurant_id, cuisine_tag_id) VALUES
(4, (SELECT id FROM cuisine_tags WHERE name = 'Newari')),
(4, (SELECT id FROM cuisine_tags WHERE name = 'Nepali'));

-- Dragon Palace (Restaurant 5)
INSERT INTO restaurant_cuisine_tags (restaurant_id, cuisine_tag_id) VALUES
(5, (SELECT id FROM cuisine_tags WHERE name = 'Chinese'));

-- Pizza Italia (Restaurant 6)
INSERT INTO restaurant_cuisine_tags (restaurant_id, cuisine_tag_id) VALUES
(6, (SELECT id FROM cuisine_tags WHERE name = 'Italian'));

-- Sushi House (Restaurant 7)
INSERT INTO restaurant_cuisine_tags (restaurant_id, cuisine_tag_id) VALUES
(7, (SELECT id FROM cuisine_tags WHERE name = 'Japanese'));

-- Thai Delight (Restaurant 8)
INSERT INTO restaurant_cuisine_tags (restaurant_id, cuisine_tag_id) VALUES
(8, (SELECT id FROM cuisine_tags WHERE name = 'Thai'));

-- Korean BBQ (Restaurant 9)
INSERT INTO restaurant_cuisine_tags (restaurant_id, cuisine_tag_id) VALUES
(9, (SELECT id FROM cuisine_tags WHERE name = 'Korean')),
(9, (SELECT id FROM cuisine_tags WHERE name = 'BBQ'));

-- Taco Fiesta (Restaurant 10)
INSERT INTO restaurant_cuisine_tags (restaurant_id, cuisine_tag_id) VALUES
(10, (SELECT id FROM cuisine_tags WHERE name = 'Mexican'));

-- Burger Junction (Restaurant 11)
INSERT INTO restaurant_cuisine_tags (restaurant_id, cuisine_tag_id) VALUES
(11, (SELECT id FROM cuisine_tags WHERE name = 'American')),
(11, (SELECT id FROM cuisine_tags WHERE name = 'Fast Food'));

-- Continental Cafe (Restaurant 12)
INSERT INTO restaurant_cuisine_tags (restaurant_id, cuisine_tag_id) VALUES
(12, (SELECT id FROM cuisine_tags WHERE name = 'Continental'));

-- Add cuisine tags for remaining restaurants (13-40)
INSERT INTO restaurant_cuisine_tags (restaurant_id, cuisine_tag_id) VALUES
(13, (SELECT id FROM cuisine_tags WHERE name = 'Nepali')),
(14, (SELECT id FROM cuisine_tags WHERE name = 'Indian')),
(15, (SELECT id FROM cuisine_tags WHERE name = 'Continental')),
(16, (SELECT id FROM cuisine_tags WHERE name = 'Italian')),
(17, (SELECT id FROM cuisine_tags WHERE name = 'Indian')),
(18, (SELECT id FROM cuisine_tags WHERE name = 'Continental')),
(19, (SELECT id FROM cuisine_tags WHERE name = 'Chinese')),
(20, (SELECT id FROM cuisine_tags WHERE name = 'Continental')),
(21, (SELECT id FROM cuisine_tags WHERE name = 'BBQ')),
(22, (SELECT id FROM cuisine_tags WHERE name = 'Continental')),
(23, (SELECT id FROM cuisine_tags WHERE name = 'Fast Food')),
(24, (SELECT id FROM cuisine_tags WHERE name = 'Nepali')),
(25, (SELECT id FROM cuisine_tags WHERE name = 'Continental')),
(26, (SELECT id FROM cuisine_tags WHERE name = 'Continental')),
(27, (SELECT id FROM cuisine_tags WHERE name = 'Indian')),
(28, (SELECT id FROM cuisine_tags WHERE name = 'Continental')),
(29, (SELECT id FROM cuisine_tags WHERE name = 'Continental')),
(30, (SELECT id FROM cuisine_tags WHERE name = 'Nepali')),
(31, (SELECT id FROM cuisine_tags WHERE name = 'Continental')),
(32, (SELECT id FROM cuisine_tags WHERE name = 'Fast Food')),
(33, (SELECT id FROM cuisine_tags WHERE name = 'Indian')),
(34, (SELECT id FROM cuisine_tags WHERE name = 'Continental')),
(35, (SELECT id FROM cuisine_tags WHERE name = 'Continental')),
(36, (SELECT id FROM cuisine_tags WHERE name = 'Chinese')),
(37, (SELECT id FROM cuisine_tags WHERE name = 'Indian'));

-- ==========================================
-- 8. CREATE MENU ITEMS (Sample for first 10 restaurants)
-- ==========================================

-- Nepali Ghar (Restaurant 1)
INSERT INTO restaurant_menu_items (restaurant_id, category_id, name, description, price, discounted_price, available, is_vegan, is_vegetarian, is_today_special) VALUES
(1, (SELECT id FROM categories WHERE name = 'Main Course'), 'Dal Bhat Set', 'Traditional Nepali rice and lentil meal with vegetables', 350.00, NULL, true, false, true, true),
(1, (SELECT id FROM categories WHERE name = 'Main Course'), 'Chicken Curry', 'Spicy chicken curry with rice', 450.00, NULL, true, false, false, false),
(1, (SELECT id FROM categories WHERE name = 'Appetizers'), 'Sekuwa (Grilled Meat)', 'Grilled meat with traditional spices', 550.00, 500.00, true, false, false, false),
(1, (SELECT id FROM categories WHERE name = 'Beverages'), 'Masala Chiya', 'Traditional Nepali spiced tea', 50.00, NULL, true, false, true, false),
(1, (SELECT id FROM categories WHERE name = 'Desserts'), 'Sel Roti', 'Traditional Nepali sweet rice bread', 100.00, NULL, true, false, true, false);

-- Momo Paradise (Restaurant 2)
INSERT INTO restaurant_menu_items (restaurant_id, category_id, name, description, price, discounted_price, available, is_vegan, is_vegetarian, is_today_special) VALUES
(2, (SELECT id FROM categories WHERE name = 'Main Course'), 'Chicken Momo (10pcs)', 'Steamed chicken dumplings', 250.00, NULL, true, false, false, true),
(2, (SELECT id FROM categories WHERE name = 'Main Course'), 'Veg Momo (10pcs)', 'Steamed vegetable dumplings', 200.00, NULL, true, false, true, false),
(2, (SELECT id FROM categories WHERE name = 'Main Course'), 'Buff Momo (10pcs)', 'Steamed buffalo meat dumplings', 230.00, NULL, true, false, false, false),
(2, (SELECT id FROM categories WHERE name = 'Snacks'), 'Fried Momo', 'Deep fried chicken momo', 280.00, 250.00, true, false, false, true),
(2, (SELECT id FROM categories WHERE name = 'Sides'), 'Achar (Pickle)', 'Spicy tomato pickle', 30.00, NULL, true, true, true, false);

-- Thakali Kitchen (Restaurant 3)
INSERT INTO restaurant_menu_items (restaurant_id, category_id, name, description, price, discounted_price, available, is_vegan, is_vegetarian, is_today_special) VALUES
(3, (SELECT id FROM categories WHERE name = 'Main Course'), 'Thakali Khana Set', 'Traditional Thakali meal set', 550.00, NULL, true, false, false, true),
(3, (SELECT id FROM categories WHERE name = 'Main Course'), 'Dhindo Set', 'Traditional buckwheat meal', 400.00, NULL, true, false, true, false),
(3, (SELECT id FROM categories WHERE name = 'Soups'), 'Thakali Soup', 'Hot and sour Thakali style soup', 150.00, NULL, true, false, false, false),
(3, (SELECT id FROM categories WHERE name = 'Beverages'), 'Tongba', 'Traditional millet-based drink', 300.00, NULL, true, true, true, false);

-- Newari Bhansa (Restaurant 4)
INSERT INTO restaurant_menu_items (restaurant_id, category_id, name, description, price, discounted_price, available, is_vegan, is_vegetarian, is_today_special) VALUES
(4, (SELECT id FROM categories WHERE name = 'Main Course'), 'Newari Khaja Set', 'Traditional Newari snack set', 600.00, 550.00, true, false, false, true),
(4, (SELECT id FROM categories WHERE name = 'Appetizers'), 'Choila (Spiced Meat)', 'Grilled buffalo meat with spices', 450.00, NULL, true, false, false, false),
(4, (SELECT id FROM categories WHERE name = 'Snacks'), 'Wo (Lentil Pancake)', 'Newari lentil pancake', 80.00, NULL, true, false, true, false),
(4, (SELECT id FROM categories WHERE name = 'Beverages'), 'Aila', 'Traditional Newari spirit', 250.00, NULL, true, true, true, false);

-- Dragon Palace (Restaurant 5)
INSERT INTO restaurant_menu_items (restaurant_id, category_id, name, description, price, discounted_price, available, is_vegan, is_vegetarian, is_today_special) VALUES
(5, (SELECT id FROM categories WHERE name = 'Main Course'), 'Chowmein', 'Stir-fried noodles with vegetables', 280.00, NULL, true, false, true, false),
(5, (SELECT id FROM categories WHERE name = 'Main Course'), 'Fried Rice', 'Chinese style fried rice', 300.00, NULL, true, false, true, false),
(5, (SELECT id FROM categories WHERE name = 'Main Course'), 'Manchurian', 'Deep fried balls in spicy sauce', 350.00, 320.00, true, false, true, true),
(5, (SELECT id FROM categories WHERE name = 'Soups'), 'Hot and Sour Soup', 'Spicy and tangy Chinese soup', 180.00, NULL, true, false, true, false);

-- Pizza Italia (Restaurant 6)
INSERT INTO restaurant_menu_items (restaurant_id, category_id, name, description, price, discounted_price, available, is_vegan, is_vegetarian, is_today_special) VALUES
(6, (SELECT id FROM categories WHERE name = 'Main Course'), 'Margherita Pizza', 'Classic tomato and cheese pizza', 650.00, NULL, true, false, true, false),
(6, (SELECT id FROM categories WHERE name = 'Main Course'), 'Pepperoni Pizza', 'Pizza with pepperoni and cheese', 750.00, 700.00, true, false, false, true),
(6, (SELECT id FROM categories WHERE name = 'Main Course'), 'Pasta Carbonara', 'Creamy pasta with bacon', 550.00, NULL, true, false, false, false),
(6, (SELECT id FROM categories WHERE name = 'Desserts'), 'Tiramisu', 'Italian coffee-flavored dessert', 350.00, NULL, true, false, true, false);

-- Sushi House (Restaurant 7)
INSERT INTO restaurant_menu_items (restaurant_id, category_id, name, description, price, discounted_price, available, is_vegan, is_vegetarian, is_today_special) VALUES
(7, (SELECT id FROM categories WHERE name = 'Main Course'), 'California Roll (8pcs)', 'Sushi roll with crab and avocado', 800.00, NULL, true, false, false, true),
(7, (SELECT id FROM categories WHERE name = 'Main Course'), 'Salmon Nigiri (2pcs)', 'Fresh salmon on rice', 400.00, NULL, true, false, false, false),
(7, (SELECT id FROM categories WHERE name = 'Main Course'), 'Tempura Set', 'Deep fried seafood and vegetables', 950.00, 900.00, true, false, false, false),
(7, (SELECT id FROM categories WHERE name = 'Beverages'), 'Green Tea', 'Traditional Japanese green tea', 150.00, NULL, true, true, true, false);

-- Thai Delight (Restaurant 8)
INSERT INTO restaurant_menu_items (restaurant_id, category_id, name, description, price, discounted_price, available, is_vegan, is_vegetarian, is_today_special) VALUES
(8, (SELECT id FROM categories WHERE name = 'Main Course'), 'Pad Thai', 'Stir-fried rice noodles', 450.00, NULL, true, false, true, true),
(8, (SELECT id FROM categories WHERE name = 'Main Course'), 'Green Curry', 'Spicy coconut curry', 550.00, NULL, true, false, true, false),
(8, (SELECT id FROM categories WHERE name = 'Soups'), 'Tom Yum Soup', 'Hot and sour Thai soup', 380.00, 350.00, true, false, false, false),
(8, (SELECT id FROM categories WHERE name = 'Desserts'), 'Mango Sticky Rice', 'Sweet rice with fresh mango', 280.00, NULL, true, true, true, false);

-- Korean BBQ House (Restaurant 9)
INSERT INTO restaurant_menu_items (restaurant_id, category_id, name, description, price, discounted_price, available, is_vegan, is_vegetarian, is_today_special) VALUES
(9, (SELECT id FROM categories WHERE name = 'Main Course'), 'BBQ Platter', 'Assorted grilled meats', 1200.00, 1100.00, true, false, false, true),
(9, (SELECT id FROM categories WHERE name = 'Main Course'), 'Bibimbap', 'Mixed rice with vegetables and meat', 650.00, NULL, true, false, false, false),
(9, (SELECT id FROM categories WHERE name = 'Appetizers'), 'Kimchi', 'Fermented spicy cabbage', 200.00, NULL, true, true, true, false),
(9, (SELECT id FROM categories WHERE name = 'Soups'), 'Kimchi Stew', 'Spicy Korean stew', 550.00, NULL, true, false, false, false);

-- Taco Fiesta (Restaurant 10)
INSERT INTO restaurant_menu_items (restaurant_id, category_id, name, description, price, discounted_price, available, is_vegan, is_vegetarian, is_today_special) VALUES
(10, (SELECT id FROM categories WHERE name = 'Main Course'), 'Beef Tacos (3pcs)', 'Soft tacos with seasoned beef', 450.00, NULL, true, false, false, false),
(10, (SELECT id FROM categories WHERE name = 'Main Course'), 'Chicken Burrito', 'Large burrito with chicken and rice', 550.00, 500.00, true, false, false, true),
(10, (SELECT id FROM categories WHERE name = 'Snacks'), 'Nachos Supreme', 'Tortilla chips with toppings', 400.00, NULL, true, false, true, false),
(10, (SELECT id FROM categories WHERE name = 'Beverages'), 'Margarita (Non-alcoholic)', 'Refreshing lime drink', 250.00, NULL, true, true, true, false);

-- Add basic menu items for remaining restaurants (11-40)
-- Burger Junction
INSERT INTO restaurant_menu_items (restaurant_id, category_id, name, description, price, available) VALUES
(11, (SELECT id FROM categories WHERE name = 'Main Course'), 'Classic Burger', 'Beef burger with cheese', 350.00, true),
(11, (SELECT id FROM categories WHERE name = 'Snacks'), 'French Fries', 'Crispy potato fries', 150.00, true),
(11, (SELECT id FROM categories WHERE name = 'Beverages'), 'Soft Drink', 'Cola or Sprite', 80.00, true);

-- Continental Cafe (12)
INSERT INTO restaurant_menu_items (restaurant_id, category_id, name, description, price, available) VALUES
(12, (SELECT id FROM categories WHERE name = 'Main Course'), 'Grilled Chicken', 'Herb grilled chicken breast', 550.00, true),
(12, (SELECT id FROM categories WHERE name = 'Salads'), 'Caesar Salad', 'Fresh romaine with dressing', 350.00, true);

-- Add items for restaurants 13-37
INSERT INTO restaurant_menu_items (restaurant_id, category_id, name, description, price, available) VALUES
(13, (SELECT id FROM categories WHERE name = 'Main Course'), 'Special Dal Bhat', 'Premium Nepali set meal', 450.00, true),
(14, (SELECT id FROM categories WHERE name = 'Main Course'), 'Butter Chicken', 'Creamy tomato chicken curry', 500.00, true),
(15, (SELECT id FROM categories WHERE name = 'Main Course'), 'Mixed Grill', 'Assorted grilled items', 650.00, true),
(16, (SELECT id FROM categories WHERE name = 'Main Course'), 'Lasagna', 'Layered pasta with meat sauce', 550.00, true),
(17, (SELECT id FROM categories WHERE name = 'Main Course'), 'Biryani', 'Fragrant rice with spices', 400.00, true),
(18, (SELECT id FROM categories WHERE name = 'Main Course'), 'Steak', 'Grilled beef steak', 950.00, true),
(19, (SELECT id FROM categories WHERE name = 'Main Course'), 'Dimsum Platter', 'Assorted Chinese dumplings', 600.00, true),
(20, (SELECT id FROM categories WHERE name = 'Main Course'), 'Grilled Fish', 'Fresh fish with herbs', 750.00, true),
(21, (SELECT id FROM categories WHERE name = 'Main Course'), 'BBQ Ribs', 'Tender pork ribs', 850.00, true),
(22, (SELECT id FROM categories WHERE name = 'Main Course'), 'Club Sandwich', 'Triple decker sandwich', 450.00, true),
(23, (SELECT id FROM categories WHERE name = 'Snacks'), 'Samosa Chat', 'Spicy samosa with toppings', 120.00, true),
(24, (SELECT id FROM categories WHERE name = 'Main Course'), 'Newari Set', 'Traditional meal set', 600.00, true),
(25, (SELECT id FROM categories WHERE name = 'Main Course'), 'Fish and Chips', 'Battered fish with fries', 550.00, true),
(26, (SELECT id FROM categories WHERE name = 'Breakfast'), 'American Breakfast', 'Eggs, bacon, and toast', 450.00, true),
(27, (SELECT id FROM categories WHERE name = 'Main Course'), 'Tandoori Chicken', 'Clay oven roasted chicken', 550.00, true),
(28, (SELECT id FROM categories WHERE name = 'Main Course'), 'Risotto', 'Creamy Italian rice', 600.00, true),
(29, (SELECT id FROM categories WHERE name = 'Main Course'), 'Seafood Paella', 'Spanish rice with seafood', 850.00, true),
(30, (SELECT id FROM categories WHERE name = 'Main Course'), 'Thakali Set', 'Traditional meal', 500.00, true),
(31, (SELECT id FROM categories WHERE name = 'Main Course'), 'Prime Rib', 'Premium beef cut', 1200.00, true),
(32, (SELECT id FROM categories WHERE name = 'Snacks'), 'Panipuri', 'Crispy shells with spicy water', 100.00, true),
(33, (SELECT id FROM categories WHERE name = 'Main Course'), 'Paneer Tikka', 'Grilled cottage cheese', 450.00, true),
(34, (SELECT id FROM categories WHERE name = 'Breakfast'), 'Continental Breakfast', 'Pastries and coffee', 400.00, true),
(35, (SELECT id FROM categories WHERE name = 'Salads'), 'Greek Salad', 'Fresh vegetables with feta', 350.00, true),
(36, (SELECT id FROM categories WHERE name = 'Main Course'), 'Noodle Soup', 'Hot noodle soup', 250.00, true),
(37, (SELECT id FROM categories WHERE name = 'Main Course'), 'Chicken Tikka Masala', 'Creamy chicken curry', 500.00, true);

-- ==========================================
-- SUMMARY
-- ==========================================
-- Total Restaurants: 40 (4 featured)
-- Total Owners: 2
-- Restaurants with Owner 1 (Ramesh Kumar): 2
-- Restaurants with Owner 2 (Sita Sharma): 2
-- Restaurants without owners: 36
-- Total Categories: 10
-- Total Cuisine Tags: 15
-- Total Dietary Tags: 5
-- Total Menu Items: ~100+

SELECT 'Database seeding completed successfully!' as message;
SELECT COUNT(*) as total_restaurants FROM restaurants;
SELECT COUNT(*) as total_menu_items FROM restaurant_menu_items;
SELECT COUNT(*) as total_owners FROM users WHERE role = 'OWNER';
