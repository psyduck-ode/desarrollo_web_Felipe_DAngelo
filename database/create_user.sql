-- Active: 1759191830955@@127.0.0.1@3306@tarea2
-- Crear usuario
CREATE USER 'cc5002'@'localhost' IDENTIFIED BY 'programacionweb';

--Conceder privilegios
GRANT ALL PRIVILEGES ON tarea2.* TO 'cc5002'@'localhost';
FLUSH PRIVILEGES;
-- Eliminar usuario de ser necesario
DROP USER 'cc5002'@'localhost';
