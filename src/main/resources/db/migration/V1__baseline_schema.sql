-- =============================================
-- V1__baseline_schema.sql
-- Bhoklagyo — Initial schema (Flyway baseline)
-- Idempotent: safe to run on existing databases
-- =============================================

-- Enable PostGIS extension
CREATE EXTENSION IF NOT EXISTS postgis;

-- =============================================
-- Independent tables (no FK dependencies)
-- =============================================

CREATE TABLE IF NOT EXISTS admins (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(255) NOT NULL,
    password        VARCHAR(255) NOT NULL,
    email           VARCHAR(255) NOT NULL UNIQUE,
    phone_number    VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS categories (
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS cuisine_tags (
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS dietary_tags (
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS documents (
    doc_id              BIGSERIAL PRIMARY KEY,
    doc_type            VARCHAR(255) NOT NULL,
    file_location_url   VARCHAR(255) NOT NULL,
    expiry_date         DATE,
    document_number     VARCHAR(255),
    issued_date         DATE
);

CREATE TABLE IF NOT EXISTS vendors (
    id                  BIGSERIAL PRIMARY KEY,
    pan_number          VARCHAR(255) NOT NULL UNIQUE,
    business_name       VARCHAR(255) NOT NULL,
    account_number      VARCHAR(255),
    is_vat_registered   BOOLEAN NOT NULL,
    email               VARCHAR(255),
    phone_number        VARCHAR(255),
    address             VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS requests (
    request_id      BIGSERIAL PRIMARY KEY,
    name            VARCHAR(255) NOT NULL,
    restaurant_name VARCHAR(255) NOT NULL,
    contact_number  VARCHAR(255) NOT NULL,
    email_address   VARCHAR(255) NOT NULL,
    message         TEXT,
    status          VARCHAR(255) NOT NULL DEFAULT 'PENDING'
);

CREATE TABLE IF NOT EXISTS notifications (
    id              BIGSERIAL PRIMARY KEY,
    order_id        BIGINT,
    restaurant_id   BIGINT,
    type            VARCHAR(255),
    created_at      TIMESTAMP,
    message         VARCHAR(1000),
    user_id         BIGINT,
    is_read         BOOLEAN DEFAULT FALSE
);

-- =============================================
-- Tables with FK dependencies
-- =============================================

CREATE TABLE IF NOT EXISTS users (
    id                      BIGSERIAL PRIMARY KEY,
    name                    VARCHAR(255) NOT NULL,
    password                VARCHAR(255) NOT NULL,
    email                   VARCHAR(255) NOT NULL UNIQUE,
    phone_number            VARCHAR(255),
    role                    VARCHAR(255) NOT NULL,
    address                 VARCHAR(255),
    employed_restaurant_id  BIGINT
);

CREATE TABLE IF NOT EXISTS restaurants (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(255),
    location        geometry(Point,4326),
    contact_number  VARCHAR(255),
    is_featured     BOOLEAN NOT NULL DEFAULT FALSE,
    photo_url       VARCHAR(255),
    address_label   VARCHAR(255),
    opening_time    TIME,
    closing_time    TIME,
    is_open         BOOLEAN NOT NULL DEFAULT FALSE,
    total_rating    BIGINT DEFAULT 0,
    total_count     BIGINT DEFAULT 0,
    rating          DOUBLE PRECISION DEFAULT 0.0,
    average_for_one INTEGER DEFAULT 0,
    vendor_id       BIGINT,
    owner_id        BIGINT
);

CREATE TABLE IF NOT EXISTS restaurant_menu_items (
    id                  BIGSERIAL PRIMARY KEY,
    restaurant_id       BIGINT NOT NULL,
    category_id         BIGINT NOT NULL,
    name                VARCHAR(255),
    description         VARCHAR(255),
    price               DOUBLE PRECISION NOT NULL,
    discounted_price    DOUBLE PRECISION,
    available           BOOLEAN DEFAULT TRUE,
    is_vegan            BOOLEAN DEFAULT FALSE,
    is_vegetarian       BOOLEAN DEFAULT FALSE,
    allergy_warnings    VARCHAR(500),
    is_today_special    BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS orders (
    id                  BIGSERIAL PRIMARY KEY,
    customer_id         BIGINT NOT NULL,
    restaurant_id       BIGINT,
    status              VARCHAR(255) NOT NULL,
    total_price         DOUBLE PRECISION,
    delivery_latitude   DOUBLE PRECISION,
    delivery_longitude  DOUBLE PRECISION,
    delivery_address    VARCHAR(1000),
    estimated_time      INTEGER,
    special_request     VARCHAR(500),
    order_time          TIMESTAMP
);

CREATE TABLE IF NOT EXISTS order_items (
    id                      BIGSERIAL PRIMARY KEY,
    order_id                BIGINT NOT NULL,
    restaurant_menu_item_id BIGINT NOT NULL,
    quantity                INTEGER NOT NULL,
    price_at_order          DOUBLE PRECISION NOT NULL
);

-- =============================================
-- Join tables (ManyToMany)
-- =============================================

CREATE TABLE IF NOT EXISTS restaurant_cuisine_tags (
    restaurant_id   BIGINT NOT NULL,
    cuisine_tag_id  BIGINT NOT NULL,
    PRIMARY KEY (restaurant_id, cuisine_tag_id)
);

CREATE TABLE IF NOT EXISTS restaurant_dietary_tags (
    restaurant_id   BIGINT NOT NULL,
    dietary_tag_id  BIGINT NOT NULL,
    PRIMARY KEY (restaurant_id, dietary_tag_id)
);

CREATE TABLE IF NOT EXISTS restaurant_documents (
    doc_id          BIGINT NOT NULL,
    restaurant_id   BIGINT NOT NULL,
    PRIMARY KEY (doc_id, restaurant_id)
);

-- =============================================
-- Foreign keys (added separately for idempotency)
-- =============================================
DO $$ BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_users_employed_restaurant') THEN
        ALTER TABLE users ADD CONSTRAINT fk_users_employed_restaurant FOREIGN KEY (employed_restaurant_id) REFERENCES restaurants(id);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_restaurants_vendor') THEN
        ALTER TABLE restaurants ADD CONSTRAINT fk_restaurants_vendor FOREIGN KEY (vendor_id) REFERENCES vendors(id);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_restaurants_owner') THEN
        ALTER TABLE restaurants ADD CONSTRAINT fk_restaurants_owner FOREIGN KEY (owner_id) REFERENCES users(id);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_menu_items_restaurant') THEN
        ALTER TABLE restaurant_menu_items ADD CONSTRAINT fk_menu_items_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants(id);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_menu_items_category') THEN
        ALTER TABLE restaurant_menu_items ADD CONSTRAINT fk_menu_items_category FOREIGN KEY (category_id) REFERENCES categories(id);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_orders_customer') THEN
        ALTER TABLE orders ADD CONSTRAINT fk_orders_customer FOREIGN KEY (customer_id) REFERENCES users(id);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_orders_restaurant') THEN
        ALTER TABLE orders ADD CONSTRAINT fk_orders_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants(id);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_order_items_order') THEN
        ALTER TABLE order_items ADD CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders(id);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_order_items_menu_item') THEN
        ALTER TABLE order_items ADD CONSTRAINT fk_order_items_menu_item FOREIGN KEY (restaurant_menu_item_id) REFERENCES restaurant_menu_items(id);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_cuisine_tags_restaurant') THEN
        ALTER TABLE restaurant_cuisine_tags ADD CONSTRAINT fk_cuisine_tags_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants(id);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_cuisine_tags_tag') THEN
        ALTER TABLE restaurant_cuisine_tags ADD CONSTRAINT fk_cuisine_tags_tag FOREIGN KEY (cuisine_tag_id) REFERENCES cuisine_tags(id);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_dietary_tags_restaurant') THEN
        ALTER TABLE restaurant_dietary_tags ADD CONSTRAINT fk_dietary_tags_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants(id);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_dietary_tags_tag') THEN
        ALTER TABLE restaurant_dietary_tags ADD CONSTRAINT fk_dietary_tags_tag FOREIGN KEY (dietary_tag_id) REFERENCES dietary_tags(id);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_documents_doc') THEN
        ALTER TABLE restaurant_documents ADD CONSTRAINT fk_documents_doc FOREIGN KEY (doc_id) REFERENCES documents(doc_id);
    END IF;

    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_documents_restaurant') THEN
        ALTER TABLE restaurant_documents ADD CONSTRAINT fk_documents_restaurant FOREIGN KEY (restaurant_id) REFERENCES restaurants(id);
    END IF;
END $$;
