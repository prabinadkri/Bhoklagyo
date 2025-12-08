-- Migration: Add photo_url column to restaurants table
-- Date: 2025-12-04
-- Description: Add string field to store restaurant photo URL

-- Add photo_url column
ALTER TABLE restaurants ADD COLUMN IF NOT EXISTS photo_url VARCHAR(500);

-- Verify the changes
\d restaurants
