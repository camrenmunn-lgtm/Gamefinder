-- =============================================================================
-- PETCLINIC SEED DATA (teacher skeleton)
-- =============================================================================

INSERT IGNORE INTO vets VALUES (1, 'James', 'Carter');
INSERT IGNORE INTO vets VALUES (2, 'Helen', 'Leary');
INSERT IGNORE INTO vets VALUES (3, 'Linda', 'Douglas');
INSERT IGNORE INTO vets VALUES (4, 'Rafael', 'Ortega');
INSERT IGNORE INTO vets VALUES (5, 'Henry', 'Stevens');
INSERT IGNORE INTO vets VALUES (6, 'Sharon', 'Jenkins');

INSERT IGNORE INTO specialties VALUES (1, 'radiology');
INSERT IGNORE INTO specialties VALUES (2, 'surgery');
INSERT IGNORE INTO specialties VALUES (3, 'dentistry');

INSERT IGNORE INTO vet_specialties VALUES (2, 1);
INSERT IGNORE INTO vet_specialties VALUES (5, 1);
INSERT IGNORE INTO vet_specialties VALUES (3, 2);
INSERT IGNORE INTO vet_specialties VALUES (4, 2);
INSERT IGNORE INTO vet_specialties VALUES (3, 3);

INSERT IGNORE INTO types VALUES (1, 'cat');
INSERT IGNORE INTO types VALUES (2, 'dog');
INSERT IGNORE INTO types VALUES (3, 'lizard');
INSERT IGNORE INTO types VALUES (4, 'snake');
INSERT IGNORE INTO types VALUES (5, 'bird');
INSERT IGNORE INTO types VALUES (6, 'hamster');

INSERT IGNORE INTO owners VALUES (1, 'George', 'Franklin', '110 W. Liberty St.', 'Madison', '6085551023');
INSERT IGNORE INTO owners VALUES (2, 'Betty', 'Davis', '638 Cardinal Ave.', 'Sun Prairie', '6085551749');
INSERT IGNORE INTO owners VALUES (3, 'Eduardo', 'Rodriquez', '2693 Commerce St.', 'McFarland', '6085558763');
INSERT IGNORE INTO owners VALUES (4, 'Harold', 'Davis', '563 Friendly St.', 'Windsor', '6085553198');
INSERT IGNORE INTO owners VALUES (5, 'Peter', 'McTavish', '2387 S. Fair Way', 'Madison', '6085552765');
INSERT IGNORE INTO owners VALUES (6, 'Jean', 'Coleman', '105 N. Lake St.', 'Monona', '6085552654');
INSERT IGNORE INTO owners VALUES (7, 'Jeff', 'Black', '1450 Oak Blvd.', 'Monona', '6085555387');
INSERT IGNORE INTO owners VALUES (8, 'Maria', 'Escobito', '345 Maple St.', 'Madison', '6085557683');
INSERT IGNORE INTO owners VALUES (9, 'David', 'Schroeder', '2749 Blackhawk Trail', 'Madison', '6085559435');
INSERT IGNORE INTO owners VALUES (10, 'Carlos', 'Estaban', '2335 Independence La.', 'Waunakee', '6085555487');

INSERT IGNORE INTO pets VALUES (1, 'Leo', '2000-09-07', 1, 1);
INSERT IGNORE INTO pets VALUES (2, 'Basil', '2002-08-06', 6, 2);
INSERT IGNORE INTO pets VALUES (3, 'Rosy', '2001-04-17', 2, 3);
INSERT IGNORE INTO pets VALUES (4, 'Jewel', '2000-03-07', 2, 3);
INSERT IGNORE INTO pets VALUES (5, 'Iggy', '2000-11-30', 3, 4);
INSERT IGNORE INTO pets VALUES (6, 'George', '2000-01-20', 4, 5);
INSERT IGNORE INTO pets VALUES (7, 'Samantha', '1995-09-04', 1, 6);
INSERT IGNORE INTO pets VALUES (8, 'Max', '1995-09-04', 1, 6);
INSERT IGNORE INTO pets VALUES (9, 'Lucky', '1999-08-06', 5, 7);
INSERT IGNORE INTO pets VALUES (10, 'Mulligan', '1997-02-24', 2, 8);
INSERT IGNORE INTO pets VALUES (11, 'Freddy', '2000-03-09', 5, 9);
INSERT IGNORE INTO pets VALUES (12, 'Lucky', '2000-06-24', 2, 10);
INSERT IGNORE INTO pets VALUES (13, 'Sly', '2002-06-08', 1, 10);

INSERT IGNORE INTO visits VALUES (1, 7, '2010-03-04', 'rabies shot');
INSERT IGNORE INTO visits VALUES (2, 8, '2011-03-04', 'rabies shot');
INSERT IGNORE INTO visits VALUES (3, 8, '2009-06-04', 'neutered');
INSERT IGNORE INTO visits VALUES (4, 7, '2008-09-04', 'spayed');

-- =============================================================================
-- ROLES & PERMISSIONS SEED DATA
-- =============================================================================

INSERT IGNORE INTO roles (id, name, description) VALUES
  (1, 'SCHOOL_ADMIN', 'Rec Center Admin: Can manage facilities, leagues, scores, and users.'),
  (2, 'STUDENT', 'Student: Can join leagues, create teams, and view schedules.');

INSERT IGNORE INTO permissions (id, name, description) VALUES
  (1,  'MANAGE_OWN_PROFILE',     'Allows user to update their personal info and password.'),
  (2,  'USE_MESSAGING',          'Allows user to send/receive messages with other participants.'),
  (3,  'VIEW_LEAGUES',           'Allows user to browse and search available leagues and activities.'),
  (4,  'REGISTER_FOR_LEAGUE',    'Allows user to register as an individual for a league.'),
  (5,  'CREATE_TEAM',            'Allows user to create a new team as a captain.'),
  (6,  'MANAGE_TEAM_INVITATIONS','Allows user to accept or decline invitations to a team.'),
  (7,  'VIEW_OWN_SCHEDULE',      'Allows user to view their personal and team game schedule.'),
  (8,  'VIEW_STANDINGS',         'Allows user to view league standings and team statistics.'),
  (9,  'MANAGE_FACILITIES',      'Allows user to C/R/U/D locations, fields, and courts.'),
  (10, 'MANAGE_SCHEDULES',       'Allows user to C/R/U/D leagues, activities, and games.'),
  (11, 'MANAGE_REGISTRATIONS',   'Allows user to view and approve team registrations.'),
  (12, 'MANAGE_SCORES',          'Allows user to enter and confirm game scores.'),
  (13, 'SEND_ANNOUNCEMENTS',     'Allows user to send messages to individuals, teams, and leagues.');

-- SCHOOL_ADMIN gets all 13 permissions
INSERT IGNORE INTO permission_role (permission_id, role_id) VALUES
  (1,  1), (2,  1), (3,  1), (4,  1), (5,  1), (6,  1), (7,  1),
  (8,  1), (9,  1), (10, 1), (11, 1), (12, 1), (13, 1);

-- STUDENT gets 8 permissions (no management permissions)
INSERT IGNORE INTO permission_role (permission_id, role_id) VALUES
  (1, 2), (2, 2), (3, 2), (4, 2), (5, 2), (6, 2), (7, 2), (8, 2);

-- =============================================================================
-- USERS SEED DATA
-- =============================================================================

INSERT IGNORE INTO users (id, first_name, last_name, nickname, nickname_is_flagged, email, public_email, phone, public_phone, preferred_language, password_hash, created_at, updated_at, deleted_at) VALUES
  (1, 'Brett', 'School Admin', null, 0, 'brett.baumgart@kirkwood.edu',        0, null, 0, null, 'hashed_password_for_brett', '2026-03-05 00:37:34', '2026-03-05 00:37:34', null),
  (2, 'Alex',  'Student',      null, 0, 'alex.student@student.kirkwood.edu',  0, null, 0, null, 'hashed_password_for_alex',  '2026-03-05 00:37:34', '2026-03-05 00:37:34', null);

INSERT IGNORE INTO user_roles (user_id, role_id) VALUES
  (1, 1),  -- Brett -> SCHOOL_ADMIN
  (2, 2);  -- Alex  -> STUDENT

-- =============================================================================
-- SCHOOLS SEED DATA
-- =============================================================================

INSERT IGNORE INTO schools (id, name, domain, status_id, created_at, updated_at, deleted_at) VALUES
  (1,  'Kirkwood Community College', 'kirkwood.edu',       'ACTIVE',    '2026-02-23 02:08:24', '2026-02-23 02:08:24', null),
  (2,  'University of Iowa',         'uiowa.edu',          'ACTIVE',    '2026-02-23 02:08:24', '2026-02-23 02:08:24', null),
  (3,  'Iowa State University',      'iastate.edu',        'ACTIVE',    '2026-02-23 02:08:24', '2026-02-23 02:08:24', null),
  (4,  'University of Northern Iowa','uni.edu',            'ACTIVE',    '2026-02-23 02:08:24', '2026-02-23 02:08:24', null),
  (5,  'Coe College',                'coe.edu',            'ACTIVE',    '2026-02-23 02:08:24', '2026-02-23 02:08:24', null),
  (6,  'Mount Mercy University',     'mtmercy.edu',        'ACTIVE',    '2026-02-23 02:08:24', '2026-02-23 02:08:24', null),
  (7,  'Drake University',           'drake.edu',          'ACTIVE',    '2026-02-23 02:08:24', '2026-02-23 02:08:24', null),
  (8,  'Grinnell College',           'grinnell.edu',       'ACTIVE',    '2026-02-23 02:08:24', '2026-02-23 02:08:24', null),
  (9,  'Luther College',             'luther.edu',         'ACTIVE',    '2026-02-23 02:08:24', '2026-02-23 02:08:24', null),
  (10, 'Simpson College',            'simpson.edu',        'INACTIVE',  '2026-02-23 02:08:24', '2026-02-23 02:08:24', null),
  (11, 'Wartburg College',           'wartburg.edu',       'ACTIVE',    '2026-02-23 02:08:24', '2026-02-23 02:08:24', null),
  (12, 'Cornell College',            'cornellcollege.edu', 'ACTIVE',    '2026-02-23 02:08:24', '2026-02-23 02:08:24', null),
  (13, 'Loras College',              'loras.edu',          'ACTIVE',    '2026-02-23 02:08:24', '2026-02-23 02:08:24', null),
  (14, 'Clarke University',          'clarke.edu',         'SUSPENDED', '2026-02-23 02:08:24', '2026-02-23 02:08:24', null),
  (15, 'St. Ambrose University',     'sau.edu',            'ACTIVE',    '2026-02-23 02:08:24', '2026-02-23 02:08:24', null);

-- =============================================================================
-- LOCATIONS SEED DATA
-- =============================================================================

INSERT IGNORE INTO locations (school_id, name, description, address, status_id) VALUES
  (1, 'Main Campus', 'The primary campus in Cedar Rapids', '6301 Kirkwood Blvd SW, Cedar Rapids, IA', 'ACTIVE');

INSERT IGNORE INTO locations (school_id, name, description, address, status_id) VALUES
  (2, 'Carver-Hawkeye Arena', 'Main sports arena', '1 Elliott Dr, Iowa City, IA', 'ACTIVE');

INSERT IGNORE INTO locations (school_id, parent_location_id, name, description, status_id) VALUES
  (1, 1, 'Michael J Gould Rec Center', 'Student recreation facility', 'ACTIVE'),
  (1, 1, 'Johnson Hall',               'Athletics building and gymnasium', 'ACTIVE');

INSERT IGNORE INTO locations (school_id, parent_location_id, name, description, status_id) VALUES
  (2, 2, 'Main Court',  'The primary basketball court', 'ACTIVE'),
  (2, 2, 'Weight Room', 'Athlete training facility',    'COMING_SOON');

INSERT IGNORE INTO locations (school_id, parent_location_id, name, description, status_id) VALUES
  (1, 3, 'Basketball Court 1', 'North court', 'ACTIVE'),
  (1, 3, 'Basketball Court 2', 'South court', 'ACTIVE');

-- =============================================================================
-- GAMEFINDER SEED DATA
-- =============================================================================

INSERT IGNORE INTO publishers (ID, publisher_name, publisher_country, founded_year, publisher_site, publisher_description) VALUES
  (1, 'Nintendo',     'Japan',         1889, 'https://www.nintendo.com',     'Japanese multinational video game company known for iconic franchises such as Mario, Zelda and Pokemon.'),
  (2, 'Hasbro',       'United States', 1923, 'https://www.hasbro.com',       'American multinational toy and board game company behind classics like Monopoly, Scrabble and Risk.'),
  (3, 'Sega',         'Japan',         1945, 'https://www.sega.com',         'Japanese video game developer and publisher known for Sonic the Hedgehog and numerous arcade classics.'),
  (4, 'Ravensburger', 'Germany',       1883, 'https://www.ravensburger.com', 'German game and puzzle manufacturer renowned for high quality board games and puzzles.');

INSERT IGNORE INTO games (ID, game_title, game_type, release_year, publishers_id, description, rarity_score, estimated_copies_made, avg_value, game_image, created_at, updated_at) VALUES
  (1,  'The Legend of Zelda',  'VideoGame', 1986, 1, 'An action-adventure game following Link on his quest to rescue Princess Zelda and defeat Ganon.',                              'VeryRare',  6500000,    85.00,      null, '2026-02-25 16:17:21', '2026-02-25 16:17:21'),
  (2,  'Super Mario Bros',     'VideoGame', 1985, 1, 'The iconic platformer that defined a generation, following Mario as he rescues Princess Peach.',                              'Rare',      40240000,   45.00,      null, '2026-02-25 16:17:21', '2026-02-25 16:17:21'),
  (3,  'Monopoly',             'BoardGame', 1935, 2, 'The classic property trading board game where players buy, sell and trade to bankrupt their opponents.',                      'Common',    275000000,  25.00,      null, '2026-02-25 16:17:21', '2026-02-25 16:17:21'),
  (4,  'Sonic the Hedgehog',   'VideoGame', 1991, 3, 'A high speed platformer following Sonic as he battles the evil Dr. Robotnik to free captured animals.',                      'Uncommon',  15000000,   35.00,      null, '2026-02-25 16:17:21', '2026-02-25 16:17:21'),
  (5,  'Scrabble',             'BoardGame', 1948, 2, 'A word game where players score points by placing tiles with letters onto a game board.',                                     'Common',    150000000,  20.00,      null, '2026-02-25 16:17:21', '2026-02-25 16:17:21'),
  (6,  'Metroid',              'VideoGame', 1986, 1, 'A sci-fi action adventure game following bounty hunter Samus Aran as she explores the dangerous planet Zebes.',               'VeryRare',  2730000,    120.00,     null, '2026-02-25 16:17:21', '2026-02-25 16:17:21'),
  (7,  'Risk',                 'BoardGame', 1957, 2, 'A strategy board game of diplomacy, conflict and conquest where players battle for world domination.',                        'Common',    80000000,   30.00,      null, '2026-02-25 16:17:21', '2026-02-25 16:17:21'),
  (8,  'Streets of Rage',      'VideoGame', 1991, 3, 'A side scrolling beat em up following ex-police officers fighting to reclaim their city from a crime syndicate.',            'Uncommon',  5000000,    55.00,      null, '2026-02-25 16:17:21', '2026-02-25 16:17:21'),
  (9,  'Cluedo',               'BoardGame', 1949, 2, 'A classic murder mystery board game where players deduce the killer, weapon and location of a crime.',                        'Common',    150000000,  22.00,      null, '2026-02-25 16:17:21', '2026-02-25 16:17:21'),
  (10, 'Donkey Kong',          'VideoGame', 1981, 1, 'An arcade classic where Mario must climb scaffolding to rescue Pauline from the giant ape Donkey Kong.',                     'Rare',      132000000,  60.00,      null, '2026-02-25 16:17:21', '2026-02-25 16:17:21'),
  (11, 'Ticket to Ride',       'BoardGame', 2004, 4, 'A railway themed board game where players collect cards and claim railway routes across a map.',                              'Uncommon',  10000000,   45.00,      null, '2026-02-25 16:17:21', '2026-02-25 16:17:21'),
  (12, 'Labyrinth',            'BoardGame', 1986, 4, 'A shifting maze board game where players move through a changing labyrinth to collect treasures.',                            'Uncommon',  25000000,   35.00,      null, '2026-02-25 16:17:21', '2026-02-25 16:17:21'),
  (13, 'The Legend of Marc',   'VideoGame', 2026, null, 'the best game ever made!',                                                                                               'UltraRare', 1,          1000000.00, null, '2026-02-28 02:31:48', '2026-02-28 02:31:48'),
  (14, 'Marc''s New Game',     'VideoGame', 1999, null, 'This is the description of Marc''s New Game.',                                                                           'Common',    999,        99.00,      null, '2026-03-01 06:31:07', '2026-03-01 06:31:07'),
  (15, '1232',                 'VideoGame', 1999, null, '2',                                                                                                                      'UltraRare', 1,          2.00,       null, '2026-03-02 05:26:43', '2026-03-02 05:26:43'),
  (17, '1552a',                'VideoGame', 1999, 1,    '2',                                                                                                                      'VeryRare',  1,          2.00,       null, '2026-03-12 14:27:21', '2026-03-12 14:27:21'),
  (18, 'Marc''s Game March 16','BoardGame', 2027, 3,    'Coming soon',                                                                                                            'VeryRare',  -1,         -1.00,      null, '2026-03-16 16:37:24', '2026-03-16 16:37:24');

-- =============================================================================
-- RECIPE SEED DATA (Week 10 CodeSignal)
-- =============================================================================

INSERT IGNORE INTO recipes (recipe_ingredients, instructions, type, category, dietary_preference, internal_notes) VALUES
  ('Chickpeas, Tahini, Lemon',        'Blend until smooth.',          'Appetizer',   'Mediterranean', 'Vegan',       'Classic hummus'),
  ('Pasta, Tomato, Basil',            'Boil pasta, add sauce.',       'Main Course', 'Italian',       'Vegetarian',  'Simple Pomodoro'),
  ('Beef, Tortilla, Salsa',           'Cook beef, assemble taco.',    'Main Course', 'Mexican',       'Meat-based',  'Street style'),
  ('Lentils, Carrots, Curry Powder',  'Simmer until soft.',           'Soup',        'Indian',        'Vegan',       'Healthy Dal'),
  ('Zucchini, Garlic, Olive Oil',     'Sauté zucchini noodles.',      'Main Course', 'Italian',       'Vegan',       'Low carb option');
