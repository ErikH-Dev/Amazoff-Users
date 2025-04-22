-- Insert Users
INSERT INTO App_User (oauthId, oauthProvider, user_type) VALUES
(101, 1, 'BUYER'),
(102, 2, 'BUYER'),
(103, 3, 'VENDOR'),
(104, 4, 'VENDOR'),
-- (105, 5, 'VENDOR'),
(106, 5, 'ADMIN');

-- Insert Buyers
INSERT INTO App_Buyer (oauthId, firstName, lastName, email) VALUES
(101, 'John', 'Doe', 'john.doe@example.com'),
(102, 'Jane', 'Smith', 'jane.smith@example.com');

-- Insert Vendors
INSERT INTO App_Vendor (oauthId, storeName) VALUES
(103, 'Tech Store'),
(104, 'Gadget Hub');
(106, 'Dummy Vendor');

-- Insert Addresses (for Buyers)
INSERT INTO App_Address (id, oauthId, street, city, state, postal_code, country) VALUES 
(201, 101, '123 Main St', 'Springfield', 'IL', '62704', 'USA'),
(202, 102, '456 Elm St', 'Springfield', 'IL', '62705', 'USA');