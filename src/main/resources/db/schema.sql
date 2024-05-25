CREATE TABLE IF NOT EXISTS users(
    account_id varchar(50) primary key not null,
    password varchar(255)
);

INSERT INTO users (account_id, password)
SELECT 'Karlo', '$2a$12$dqPWR0oMl2c/4hWUb5qRkOCPsKyypzf5PxIoqGSHyjLTeazq382Iu'
WHERE NOT EXISTS (SELECT 1 FROM users);
-->password je password1, hashan sa bcryptom