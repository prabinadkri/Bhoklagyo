-- Migration: Add is_featured column to restaurants table
-- Date: 2025-12-04
-- Description: Add boolean field to mark restaurants as featured

-- Add is_featured column with default value false
ALTER TABLE restaurants ADD COLUMN IF NOT EXISTS is_featured BOOLEAN NOT NULL DEFAULT false;

-- Verify the changes
\d restaurants
