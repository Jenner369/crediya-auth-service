INSERT INTO roles (id, name, description)
VALUES
    ('d4e5f6a7-8901-23bc-def4-5678901234cd', 'ADMIN', 'Acceso total'),
    ('c1a2f3e4-5678-90ab-cdef-1234567890ab', 'ADVISOR', 'Gestiona usuarios'),
    ('e7f8a9b0-1234-56cd-ef78-9012345678ef', 'CLIENT', 'Acceso limitado')
    ON DUPLICATE KEY UPDATE name = VALUES(name), description = VALUES(description);
