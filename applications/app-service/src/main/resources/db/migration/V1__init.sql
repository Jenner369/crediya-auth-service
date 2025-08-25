CREATE TABLE roles (
                       id CHAR(36) NOT NULL PRIMARY KEY,
                       name VARCHAR(50) NOT NULL UNIQUE,
                       description VARCHAR(255) NULL
);

CREATE TABLE users (
                       id CHAR(36) NOT NULL PRIMARY KEY,
                       name VARCHAR(100) NOT NULL,
                       last_name VARCHAR(100),
                       email VARCHAR(150) NOT NULL UNIQUE,
                       identity_document VARCHAR(20) NOT NULL UNIQUE,
                       telephone VARCHAR(20),
                       role_id CHAR(36) NOT NULL,
                       base_salary DECIMAL(15,2),
                       CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES roles(id)
);
