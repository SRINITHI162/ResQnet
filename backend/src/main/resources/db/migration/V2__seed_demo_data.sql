-- Demo account passwords are BCrypt hashes of Password@123.
INSERT IGNORE INTO users (id, name, email, phone, password, role, location, latitude, longitude, active, created_at, updated_at) VALUES
(1, 'System Admin', 'admin@resqnet.in', '9000000001', '$2a$10$yu.q7ULKT1IPB6vUOXutne9i9hT9irbXP1rI0yprFDijDoTj8LtV6', 'ADMIN', 'Karur HQ', 10.9601, 78.0766, 1, NOW(), NOW()),
(2, 'Coordinator Ravi', 'ravi.coordinator@resqnet.in', '9000000002', '$2a$10$yu.q7ULKT1IPB6vUOXutne9i9hT9irbXP1rI0yprFDijDoTj8LtV6', 'COORDINATOR', 'Karur Control Room', 10.9550, 78.0800, 1, NOW(), NOW()),
(3, 'Coordinator Meena', 'meena.coordinator@resqnet.in', '9000000003', '$2a$10$yu.q7ULKT1IPB6vUOXutne9i9hT9irbXP1rI0yprFDijDoTj8LtV6', 'COORDINATOR', 'Trichy Control Room', 10.7905, 78.7047, 1, NOW(), NOW()),
(4, 'Team Alpha Lead', 'alpha.team@resqnet.in', '9000000004', '$2a$10$yu.q7ULKT1IPB6vUOXutne9i9hT9irbXP1rI0yprFDijDoTj8LtV6', 'RESCUE_TEAM', 'Karur North', 10.9700, 78.0700, 1, NOW(), NOW()),
(5, 'Team Bravo Lead', 'bravo.team@resqnet.in', '9000000005', '$2a$10$yu.q7ULKT1IPB6vUOXutne9i9hT9irbXP1rI0yprFDijDoTj8LtV6', 'RESCUE_TEAM', 'Karur South', 10.9400, 78.0850, 1, NOW(), NOW()),
(6, 'Citizen Arjun', 'arjun@example.com', '9000000006', '$2a$10$yu.q7ULKT1IPB6vUOXutne9i9hT9irbXP1rI0yprFDijDoTj8LtV6', 'CITIZEN', 'Karur Town', 10.9601, 78.0766, 1, NOW(), NOW()),
(7, 'Citizen Divya', 'divya@example.com', '9000000007', '$2a$10$yu.q7ULKT1IPB6vUOXutne9i9hT9irbXP1rI0yprFDijDoTj8LtV6', 'CITIZEN', 'Aravakurichi', 10.7833, 78.0000, 1, NOW(), NOW()),
(8, 'Citizen Karthik', 'karthik@example.com', '9000000008', '$2a$10$yu.q7ULKT1IPB6vUOXutne9i9hT9irbXP1rI0yprFDijDoTj8LtV6', 'CITIZEN', 'Kulithalai', 10.9333, 78.4167, 1, NOW(), NOW()),
(9, 'Citizen Lakshmi', 'lakshmi@example.com', '9000000009', '$2a$10$yu.q7ULKT1IPB6vUOXutne9i9hT9irbXP1rI0yprFDijDoTj8LtV6', 'CITIZEN', 'Krishnarayapuram', 10.8167, 78.0833, 1, NOW(), NOW()),
(10, 'Citizen Suresh', 'suresh@example.com', '9000000010', '$2a$10$yu.q7ULKT1IPB6vUOXutne9i9hT9irbXP1rI0yprFDijDoTj8LtV6', 'CITIZEN', 'Manmangalam', 10.9167, 78.1167, 1, NOW(), NOW());

INSERT IGNORE INTO resources (id, resource_name, resource_type, quantity, available_quantity, location, latitude, longitude, status, last_updated) VALUES
(1, 'Ambulance A11', 'AMBULANCE', 1, 1, 'Karur General Hospital', 10.9585, 78.0783, 'AVAILABLE', NOW()),
(2, 'Ambulance A12', 'AMBULANCE', 1, 1, 'Karur Government Hospital', 10.9620, 78.0740, 'AVAILABLE', NOW()),
(3, 'Ambulance A13', 'AMBULANCE', 1, 1, 'Aravakurichi PHC', 10.7833, 78.0000, 'AVAILABLE', NOW()),
(4, 'Ambulance A14', 'AMBULANCE', 1, 1, 'Kulithalai PHC', 10.9333, 78.4167, 'AVAILABLE', NOW()),
(5, 'Ambulance A15', 'AMBULANCE', 1, 1, 'Krishnarayapuram PHC', 10.8167, 78.0833, 'AVAILABLE', NOW()),
(6, 'Boat B01', 'BOAT', 1, 1, 'Amaravathi River Station', 10.9450, 78.0900, 'AVAILABLE', NOW()),
(7, 'Boat B02', 'BOAT', 1, 1, 'Cauvery River Station', 10.9700, 78.0600, 'AVAILABLE', NOW()),
(8, 'Fire Truck F01', 'FIRE_TRUCK', 1, 1, 'Karur Fire Station', 10.9601, 78.0766, 'AVAILABLE', NOW()),
(9, 'Fire Truck F02', 'FIRE_TRUCK', 1, 1, 'Kulithalai Fire Station', 10.9333, 78.4167, 'AVAILABLE', NOW()),
(10, 'Medical Team M01', 'MEDICAL_TEAM', 5, 5, 'Karur Medical College', 10.9550, 78.0700, 'AVAILABLE', NOW()),
(11, 'Medical Team M02', 'MEDICAL_TEAM', 5, 5, 'Trichy Medical College', 10.7905, 78.7047, 'AVAILABLE', NOW()),
(12, 'Rescue Vehicle V01', 'RESCUE_VEHICLE', 2, 2, 'Karur Control Room', 10.9550, 78.0800, 'AVAILABLE', NOW()),
(13, 'Rescue Vehicle V02', 'RESCUE_VEHICLE', 2, 2, 'Aravakurichi Control Room', 10.7833, 78.0000, 'AVAILABLE', NOW()),
(14, 'Food Supply Unit FS01', 'FOOD_SUPPLY', 500, 500, 'Karur Warehouse', 10.9500, 78.0750, 'AVAILABLE', NOW()),
(15, 'Water Supply Unit WS01', 'WATER_SUPPLY', 1000, 1000, 'Karur Warehouse', 10.9500, 78.0750, 'AVAILABLE', NOW()),
(16, 'Medical Supply Unit MS01', 'MEDICAL_SUPPLY', 300, 300, 'Karur Medical College', 10.9550, 78.0700, 'AVAILABLE', NOW()),
(17, 'Shelter Equipment SE01', 'TEMPORARY_SHELTER_EQUIPMENT', 200, 200, 'Karur Warehouse', 10.9500, 78.0750, 'AVAILABLE', NOW()),
(18, 'Ambulance A16', 'AMBULANCE', 1, 1, 'Manmangalam PHC', 10.9167, 78.1167, 'AVAILABLE', NOW()),
(19, 'Boat B03', 'BOAT', 1, 1, 'Amaravathi Dam Station', 10.9300, 78.1200, 'AVAILABLE', NOW()),
(20, 'Rescue Vehicle V03', 'RESCUE_VEHICLE', 2, 2, 'Krishnarayapuram Control Room', 10.8167, 78.0833, 'AVAILABLE', NOW());

INSERT IGNORE INTO rescue_teams (id, team_name, specialization, members_count, location, latitude, longitude, availability_status, contact_number) VALUES
(1, 'Team Alpha', 'Flood Rescue', 8, 'Karur North', 10.9700, 78.0700, 'AVAILABLE', '9111100001'),
(2, 'Team Bravo', 'Search & Rescue', 6, 'Karur South', 10.9400, 78.0850, 'AVAILABLE', '9111100002'),
(3, 'Team Charlie', 'Fire Rescue', 10, 'Karur Central', 10.9601, 78.0766, 'AVAILABLE', '9111100003'),
(4, 'Team Delta', 'Medical', 5, 'Aravakurichi', 10.7833, 78.0000, 'AVAILABLE', '9111100004'),
(5, 'Team Echo', 'Disaster Relief', 12, 'Kulithalai', 10.9333, 78.4167, 'AVAILABLE', '9111100005');

INSERT IGNORE INTO shelters (id, name, address, latitude, longitude, total_capacity, occupied_capacity, food_available, water_available, medical_support, accessibility_support, status) VALUES
(1, 'Karur Govt School Shelter', 'Karur Town', 10.9610, 78.0790, 300, 40, 1, 1, 1, 1, 'OPEN'),
(2, 'Aravakurichi Community Hall', 'Aravakurichi', 10.7833, 78.0000, 200, 20, 1, 1, 0, 0, 'OPEN'),
(3, 'Kulithalai Marriage Hall', 'Kulithalai', 10.9333, 78.4167, 250, 0, 1, 1, 1, 0, 'OPEN'),
(4, 'Krishnarayapuram Relief Camp', 'Krishnarayapuram', 10.8167, 78.0833, 150, 10, 1, 0, 0, 1, 'OPEN'),
(5, 'Manmangalam College Shelter', 'Manmangalam', 10.9167, 78.1167, 400, 380, 1, 1, 1, 1, 'OPEN');

INSERT IGNORE INTO roads (id, source, source_lat, source_lng, destination, dest_lat, dest_lng, distance_km, estimated_time_min, traffic_level, road_condition, flood_level, blocked) VALUES
(1, 'Karur-Center', 10.9601, 78.0766, 'Karur-North', 10.9700, 78.0700, 3.5, 8, 2, 'NORMAL', 0, 0),
(2, 'Karur-Center', 10.9601, 78.0766, 'Karur-South', 10.9400, 78.0850, 4.2, 10, 3, 'TRAFFIC', 0, 0),
(3, 'Karur-Center', 10.9601, 78.0766, 'Karur-Hospital', 10.9585, 78.0783, 1.2, 4, 1, 'NORMAL', 0, 0),
(4, 'Karur-North', 10.9700, 78.0700, 'Amaravathi-Station', 10.9450, 78.0900, 5.0, 12, 2, 'NORMAL', 0, 0),
(5, 'Karur-South', 10.9400, 78.0850, 'Amaravathi-Station', 10.9450, 78.0900, 2.8, 7, 2, 'NORMAL', 0, 0),
(6, 'Karur-Center', 10.9601, 78.0766, 'Aravakurichi', 10.7833, 78.0000, 22.0, 35, 2, 'NORMAL', 0, 0),
(7, 'Karur-Center', 10.9601, 78.0766, 'Krishnarayapuram', 10.8167, 78.0833, 18.5, 28, 2, 'NORMAL', 0, 0),
(8, 'Krishnarayapuram', 10.8167, 78.0833, 'Manmangalam', 10.9167, 78.1167, 12.0, 20, 1, 'NORMAL', 0, 0),
(9, 'Karur-Center', 10.9601, 78.0766, 'Manmangalam', 10.9167, 78.1167, 15.0, 22, 3, 'TRAFFIC', 0, 0),
(10, 'Karur-Center', 10.9601, 78.0766, 'Kulithalai', 10.9333, 78.4167, 38.0, 50, 2, 'NORMAL', 0, 0),
(11, 'Aravakurichi', 10.7833, 78.0000, 'Kulithalai', 10.9333, 78.4167, 45.0, 60, 2, 'NORMAL', 0, 0),
(12, 'Amaravathi-Station', 10.9450, 78.0900, 'Manmangalam', 10.9167, 78.1167, 8.0, 14, 1, 'NORMAL', 0, 0);
