-- SQL script to remove the 'enabled' column from the users table
-- Run this script on your database after removing the field from the User model

-- Drop the enabled column
ALTER TABLE users DROP COLUMN enabled;

-- Verify the column has been removed
-- SELECT * FROM users LIMIT 1;

