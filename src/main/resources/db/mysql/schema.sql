-- =============================================================================
-- DROP old uppercase Java 2 tables (replaced by lowercase versions)
-- Must drop in reverse dependency order to avoid FK constraint errors
-- =============================================================================
DROP TABLE IF EXISTS usercollection;
DROP TABLE IF EXISTS userroles;
DROP TABLE IF EXISTS rolepermissions;
DROP TABLE IF EXISTS Permissions;
DROP TABLE IF EXISTS Roles;
DROP TABLE IF EXISTS Users;

-- =============================================================================
-- PETCLINIC TABLES (teacher skeleton)
-- =============================================================================

CREATE TABLE IF NOT EXISTS vets (
                                  id         INT(4) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                  first_name VARCHAR(30),
                                  last_name  VARCHAR(30),
                                  INDEX(last_name)
) engine=InnoDB;

CREATE TABLE IF NOT EXISTS specialties (
                                         id   INT(4) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                         name VARCHAR(80),
                                         INDEX(name)
) engine=InnoDB;

CREATE TABLE IF NOT EXISTS vet_specialties (
                                             vet_id       INT(4) UNSIGNED NOT NULL,
                                             specialty_id INT(4) UNSIGNED NOT NULL,
                                             FOREIGN KEY (vet_id) REFERENCES vets(id),
                                             FOREIGN KEY (specialty_id) REFERENCES specialties(id),
                                             UNIQUE (vet_id, specialty_id)
) engine=InnoDB;

CREATE TABLE IF NOT EXISTS types (
                                   id   INT(4) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                   name VARCHAR(80),
                                   INDEX(name)
) engine=InnoDB;

CREATE TABLE IF NOT EXISTS owners (
                                    id         INT(4) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                    first_name VARCHAR(30),
                                    last_name  VARCHAR(30),
                                    address    VARCHAR(255),
                                    city       VARCHAR(80),
                                    telephone  VARCHAR(20),
                                    INDEX(last_name)
) engine=InnoDB;

CREATE TABLE IF NOT EXISTS pets (
                                  id         INT(4) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                  name       VARCHAR(30),
                                  birth_date DATE,
                                  type_id    INT(4) UNSIGNED NOT NULL,
                                  owner_id   INT(4) UNSIGNED,
                                  INDEX(name),
                                  FOREIGN KEY (owner_id) REFERENCES owners(id),
                                  FOREIGN KEY (type_id) REFERENCES types(id)
) engine=InnoDB;

CREATE TABLE IF NOT EXISTS visits (
                                    id          INT(4) UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                    pet_id      INT(4) UNSIGNED,
                                    visit_date  DATE,
                                    description VARCHAR(255),
                                    FOREIGN KEY (pet_id) REFERENCES pets(id)
) engine=InnoDB;

-- =============================================================================
-- AUTH / USER TABLES (teacher AthlEagues system — lowercase)
-- =============================================================================

CREATE TABLE IF NOT EXISTS roles (
                                   id          INT AUTO_INCREMENT PRIMARY KEY,
                                   name        VARCHAR(50)  NOT NULL,
                                   description VARCHAR(255) NULL,
                                   CONSTRAINT name UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS permissions (
                                         id          INT AUTO_INCREMENT PRIMARY KEY,
                                         name        VARCHAR(100) NOT NULL,
                                         description VARCHAR(255) NULL,
                                         CONSTRAINT name UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS users (
                                   id                  INT AUTO_INCREMENT PRIMARY KEY,
                                   first_name          VARCHAR(50)                        NULL,
                                   last_name           VARCHAR(50)                        NULL,
                                   nickname            VARCHAR(50)                        NULL,
                                   nickname_is_flagged TINYINT  DEFAULT 0                 NULL,
                                   email               VARCHAR(255)                       NOT NULL,
                                   public_email        TINYINT  DEFAULT 0                 NULL,
                                   phone               VARCHAR(255)                       NULL,
                                   public_phone        TINYINT  DEFAULT 0                 NULL,
                                   preferred_language  VARCHAR(50)                        NULL,
                                   password_hash       VARCHAR(255)                       NULL,
                                   created_at          DATETIME DEFAULT CURRENT_TIMESTAMP NULL,
                                   updated_at          DATETIME DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
                                   deleted_at          DATETIME                           NULL,
                                   CONSTRAINT idx_users_email UNIQUE (email),
                                   INDEX idx_users_name (last_name, first_name)
);

CREATE TABLE IF NOT EXISTS user_roles (
                                        user_id INT NOT NULL,
                                        role_id INT NOT NULL,
                                        PRIMARY KEY (user_id, role_id),
                                        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                                        FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS permission_role (
                                             permission_id INT NOT NULL,
                                             role_id       INT NOT NULL,
                                             PRIMARY KEY (permission_id, role_id),
                                             CONSTRAINT permission_role_ibfk_1 FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE,
                                             CONSTRAINT permission_role_ibfk_2 FOREIGN KEY (role_id)       REFERENCES roles(id)       ON DELETE CASCADE,
                                             INDEX role_id (role_id)
);

-- =============================================================================
-- SCHOOL / LOCATION TABLES (teacher AthlEagues system)
-- =============================================================================

CREATE TABLE IF NOT EXISTS schools (
                                     id         INT AUTO_INCREMENT PRIMARY KEY,
                                     name       VARCHAR(255)                                              NOT NULL,
                                     domain     VARCHAR(255)                                              NOT NULL,
                                     status_id  ENUM('ACTIVE', 'INACTIVE', 'SUSPENDED') DEFAULT 'ACTIVE' NULL,
                                     created_at DATETIME DEFAULT CURRENT_TIMESTAMP                        NULL,
                                     updated_at DATETIME DEFAULT CURRENT_TIMESTAMP                        NULL ON UPDATE CURRENT_TIMESTAMP,
                                     deleted_at DATETIME                                                  NULL,
                                     CONSTRAINT idx_schools_domain UNIQUE (domain)
);

CREATE TABLE IF NOT EXISTS locations (
                                       id                 INT AUTO_INCREMENT PRIMARY KEY,
                                       school_id          INT NOT NULL,
                                       parent_location_id INT NULL,
                                       name               VARCHAR(255) NOT NULL,
                                       description        TEXT,
                                       address            VARCHAR(255),
                                       latitude           DECIMAL(8,4),
                                       longitude          DECIMAL(8,4),
                                       status_id          ENUM('DRAFT', 'ACTIVE', 'CLOSED', 'COMING_SOON') DEFAULT 'ACTIVE',
                                       created_at         DATETIME DEFAULT CURRENT_TIMESTAMP,
                                       updated_at         DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                       deleted_at         DATETIME DEFAULT NULL,
                                       CONSTRAINT fk_locations_school FOREIGN KEY (school_id)          REFERENCES schools(id)   ON DELETE CASCADE,
                                       CONSTRAINT fk_locations_parent FOREIGN KEY (parent_location_id) REFERENCES locations(id) ON DELETE SET NULL,
                                       UNIQUE KEY uk_school_location (school_id, name)
);

-- =============================================================================
-- GAMEFINDER TABLES (your project)
-- =============================================================================

CREATE TABLE IF NOT EXISTS publishers (
                                        ID                    INT AUTO_INCREMENT PRIMARY KEY,
                                        publisher_name        VARCHAR(200) NOT NULL,
                                        publisher_country     VARCHAR(100) NULL,
                                        founded_year          INT          NULL,
                                        publisher_site        VARCHAR(255) NULL,
                                        publisher_description VARCHAR(500) NULL
);

CREATE TABLE IF NOT EXISTS genres (
                                    ID                INT AUTO_INCREMENT PRIMARY KEY,
                                    genre_name        VARCHAR(100)                    NOT NULL,
                                    genre_type        ENUM('VideoGame', 'BoardGame')  NOT NULL,
                                    genre_description VARCHAR(300)                    NULL,
                                    CONSTRAINT ak_genres_genrename UNIQUE (genre_name)
);

CREATE TABLE IF NOT EXISTS gameseries (
                                        ID                 INT AUTO_INCREMENT PRIMARY KEY,
                                        series_name        VARCHAR(200)                       NOT NULL,
                                        series_description VARCHAR(500)                       NULL,
                                        created_at         DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                        CONSTRAINT ak_gameseries_seriesname UNIQUE (series_name)
);

CREATE TABLE IF NOT EXISTS manufacturers (
                                           ID                   INT AUTO_INCREMENT PRIMARY KEY,
                                           manufacturer_name    VARCHAR(200)                          NOT NULL,
                                           country              VARCHAR(100)                          NULL,
                                           founded_year         INT                                   NULL,
                                           manufacturer_website VARCHAR(255)                          NULL,
                                           manufacturer_type    ENUM('Console', 'BoardGame', 'Both')  NOT NULL,
                                           CONSTRAINT ak_manufacturers_manufacturername UNIQUE (manufacturer_name)
);

CREATE TABLE IF NOT EXISTS conditions (
                                        ID                    INT AUTO_INCREMENT PRIMARY KEY,
                                        condition_name        VARCHAR(50)  NOT NULL,
                                        condition_description VARCHAR(200) NULL,
                                        condition_grade       INT          NOT NULL,
                                        CONSTRAINT ak_conditions_conditionname  UNIQUE (condition_name),
                                        CONSTRAINT ck_conditions_conditiongrade CHECK (condition_grade BETWEEN 1 AND 10)
);

CREATE TABLE IF NOT EXISTS sellers (
                                     ID                 INT AUTO_INCREMENT PRIMARY KEY,
                                     seller_name        VARCHAR(200)                                                          NOT NULL,
                                     seller_url         VARCHAR(255)                                                          NULL,
                                     seller_type        ENUM('OnlineStore', 'PhysicalLocation', 'MarketPlace', 'Individual') NOT NULL,
                                     reliability_rating DECIMAL(3,2)                                                          NULL,
                                     country            VARCHAR(100)                                                          NULL,
                                     seller_description VARCHAR(500)                                                          NULL,
                                     is_verified        TINYINT(1) DEFAULT 0                                                  NOT NULL,
                                     contact_email      VARCHAR(255)                                                          NULL,
                                     created_at         DATETIME   DEFAULT CURRENT_TIMESTAMP                                  NOT NULL,
                                     CONSTRAINT ak_sellers_sellername     UNIQUE (seller_name),
                                     CONSTRAINT ck_sellers_reliabilityrating CHECK (reliability_rating BETWEEN 0.00 AND 5.00)
);

CREATE TABLE IF NOT EXISTS games (
                                   ID                    INT AUTO_INCREMENT PRIMARY KEY,
                                   game_title            VARCHAR(150)                                                          NOT NULL,
                                   game_type             ENUM('VideoGame', 'BoardGame')                                        NOT NULL,
                                   release_year          INT                                                                   NULL,
                                   publishers_id         INT                                                                   NULL,
                                   description           VARCHAR(500)                                                          NULL,
                                   rarity_score          ENUM('UltraRare', 'VeryRare', 'Rare', 'Uncommon', 'Common')           NULL,
                                   estimated_copies_made DOUBLE                                                                NULL,
                                   avg_value             DECIMAL(10,2)                                                         NULL,
                                   game_image            LONGBLOB                                                              NULL,
                                   created_at            DATETIME DEFAULT CURRENT_TIMESTAMP                                    NOT NULL,
                                   updated_at            DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP        NOT NULL,
                                   CONSTRAINT ak_games_gametitle  UNIQUE (game_title),
                                   CONSTRAINT fk_games_publishers_id FOREIGN KEY (publishers_id) REFERENCES publishers(ID)
);

CREATE TABLE IF NOT EXISTS gamegenres (
                                        games_id  INT NOT NULL,
                                        genres_id INT NOT NULL,
                                        PRIMARY KEY (games_id, genres_id),
                                        CONSTRAINT fk_gamegenres_games_id  FOREIGN KEY (games_id)  REFERENCES games(ID),
                                        CONSTRAINT fk_gamegenres_genres_id FOREIGN KEY (genres_id) REFERENCES genres(ID)
);

CREATE TABLE IF NOT EXISTS gameseriesmapping (
                                               games_id        INT NOT NULL,
                                               game_series_id  INT NOT NULL,
                                               order_in_series INT NULL,
                                               PRIMARY KEY (games_id, game_series_id),
                                               CONSTRAINT fk_gameseriesmapping_games_id      FOREIGN KEY (games_id)       REFERENCES games(ID),
                                               CONSTRAINT fk_gameseriesmapping_gameseries_id FOREIGN KEY (game_series_id) REFERENCES gameseries(ID)
);

CREATE TABLE IF NOT EXISTS boardgamedetails (
                                              ID                INT AUTO_INCREMENT PRIMARY KEY,
                                              games_id          INT                                                   NOT NULL,
                                              player_count_min  INT                                                   NULL,
                                              player_count_max  INT                                                   NULL,
                                              avg_play_time     INT                                                   NULL,
                                              complexity_rating DECIMAL(3,2)                                          NULL,
                                              edition           VARCHAR(100)                                          NULL,
                                              expansion_status  ENUM('BaseGame', 'Expansion', 'StandaloneExpansion')  NULL,
                                              language          VARCHAR(50)                                           NULL,
                                              age_rating        INT                                                   NULL,
                                              CONSTRAINT ak_boardgamedetails_games_id        UNIQUE (games_id),
                                              CONSTRAINT fk_boardgamedetails_games_id        FOREIGN KEY (games_id) REFERENCES games(ID),
                                              CONSTRAINT ck_boardgamedetails_complexityrating CHECK (complexity_rating BETWEEN 0.00 AND 5.00)
);

CREATE TABLE IF NOT EXISTS listings (
                                      ID             INT AUTO_INCREMENT PRIMARY KEY,
                                      games_id       INT                                  NOT NULL,
                                      sellers_id     INT                                  NOT NULL,
                                      listing_url    VARCHAR(500)                         NULL,
                                      price          DECIMAL(10,2)                        NOT NULL,
                                      currency       CHAR(3)                              NOT NULL,
                                      conditions_id  INT                                  NOT NULL,
                                      in_stock       TINYINT(1) DEFAULT 1                 NOT NULL,
                                      stock_quantity INT                                  NULL,
                                      shipping_info  VARCHAR(300)                         NULL,
                                      created_at     DATETIME   DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                      updated_at     DATETIME   DEFAULT CURRENT_TIMESTAMP NOT NULL ON UPDATE CURRENT_TIMESTAMP,
                                      CONSTRAINT fk_listings_conditions_id FOREIGN KEY (conditions_id) REFERENCES conditions(ID),
                                      CONSTRAINT fk_listings_games_id      FOREIGN KEY (games_id)      REFERENCES games(ID),
                                      CONSTRAINT fk_listings_sellers_id   FOREIGN KEY (sellers_id)    REFERENCES sellers(ID)
);

CREATE TABLE IF NOT EXISTS usercollection (
                                            ID              INT AUTO_INCREMENT PRIMARY KEY,
                                            Users_ID        INT                                NOT NULL,
                                            Games_ID        INT                                NOT NULL,
                                            Conditions_ID   INT                                NOT NULL,
                                            AcquisitionDate DATE                               NULL,
                                            PersonalNotes   VARCHAR(500)                       NULL,
                                            CreatedAt       DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                            CONSTRAINT fk_usercollection_users_id      FOREIGN KEY (Users_ID)      REFERENCES users(id),
                                            CONSTRAINT fk_usercollection_games_id      FOREIGN KEY (Games_ID)      REFERENCES games(ID),
                                            CONSTRAINT fk_usercollection_conditions_id FOREIGN KEY (Conditions_ID) REFERENCES conditions(ID)
);

-- =============================================================================
-- RECIPE TABLE (Week 10 CodeSignal exercise)
-- =============================================================================

CREATE TABLE IF NOT EXISTS recipes (
                                     id                 BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                                     recipe_ingredients VARCHAR(255) NULL,
                                     instructions       VARCHAR(255) NOT NULL,
                                     type               VARCHAR(50)  NULL,
                                     category           VARCHAR(50)  NULL,
                                     dietary_preference VARCHAR(50)  NULL,
                                     internal_notes     VARCHAR(255) NOT NULL,
                                     CONSTRAINT recipes_id_unique            UNIQUE (id),
                                     CONSTRAINT recipes_internal_notes_unique UNIQUE (internal_notes)
);
