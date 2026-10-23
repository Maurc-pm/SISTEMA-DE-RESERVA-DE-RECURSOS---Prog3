-- =====================================================================
-- Sistema de Reserva de Recursos - Proyecto 2
-- EIF206 Programación 3 - Universidad Nacional
-- Script de creación de la base de datos (MySQL 8.4)
-- Ejecutar con un usuario administrador (root).
-- Es reejecutable: no borra tablas ni datos existentes.
-- =====================================================================

CREATE DATABASE IF NOT EXISTS reservas_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;

USE reservas_db;

-- ---------------------------------------------------------------------
-- Usuarios del sistema: administradores y funcionarios.
-- Un funcionario es un usuario con rol FUNCIONARIO que además
-- tiene nombre y teléfono (igual que la herencia Funcionario -> Usuario).
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS usuario (
                                       id        VARCHAR(30)  NOT NULL,
    clave     VARCHAR(100) NOT NULL,
    rol       VARCHAR(20)  NOT NULL,
    nombre    VARCHAR(100) NULL,
    telefono  VARCHAR(30)  NULL,
    CONSTRAINT pk_usuario PRIMARY KEY (id),
    CONSTRAINT ck_usuario_rol
    CHECK (rol IN ('ADMINISTRADOR', 'FUNCIONARIO')),
    CONSTRAINT ck_usuario_funcionario_datos
    CHECK (rol <> 'FUNCIONARIO'
           OR (nombre IS NOT NULL AND telefono IS NOT NULL))
    ) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- Categorías de recursos (ID generado por la aplicación: CAT-000001).
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS categoria (
                                         id           VARCHAR(20)  NOT NULL,
    descripcion  VARCHAR(100) NOT NULL,
    CONSTRAINT pk_categoria PRIMARY KEY (id)
    ) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- Recursos. No se puede borrar una categoría que tenga recursos.
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS recurso (
                                       id            VARCHAR(30)  NOT NULL,
    categoria_id  VARCHAR(20)  NOT NULL,
    descripcion   VARCHAR(100) NOT NULL,
    CONSTRAINT pk_recurso PRIMARY KEY (id),
    CONSTRAINT fk_recurso_categoria
    FOREIGN KEY (categoria_id) REFERENCES categoria (id)
    ON UPDATE RESTRICT ON DELETE RESTRICT
    ) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- Reservas (ID generado por la aplicación: RES-000001).
-- No se puede borrar un funcionario que tenga reservas (decisión A).
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS reserva (
                                       id              VARCHAR(20)  NOT NULL,
    funcionario_id  VARCHAR(30)  NOT NULL,
    actividad       VARCHAR(150) NOT NULL,
    fecha           DATE         NOT NULL,
    hora_inicio     TIME         NOT NULL,
    hora_fin        TIME         NOT NULL,
    estado          VARCHAR(10)  NOT NULL DEFAULT 'ACTIVA',
    CONSTRAINT pk_reserva PRIMARY KEY (id),
    CONSTRAINT fk_reserva_funcionario
    FOREIGN KEY (funcionario_id) REFERENCES usuario (id)
    ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT ck_reserva_estado
    CHECK (estado IN ('ACTIVA', 'CANCELADA')),
    CONSTRAINT ck_reserva_horas
    CHECK (hora_inicio < hora_fin),
    INDEX idx_reserva_fecha (fecha)
    ) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- Recursos asignados a cada reserva (relación muchos a muchos).
-- Si se borra una reserva, se borran sus asignaciones.
-- No se puede borrar un recurso que esté en alguna reserva (decisión A).
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS reserva_recurso (
                                               reserva_id  VARCHAR(20) NOT NULL,
    recurso_id  VARCHAR(30) NOT NULL,
    CONSTRAINT pk_reserva_recurso PRIMARY KEY (reserva_id, recurso_id),
    CONSTRAINT fk_rr_reserva
    FOREIGN KEY (reserva_id) REFERENCES reserva (id)
    ON UPDATE RESTRICT ON DELETE CASCADE,
    CONSTRAINT fk_rr_recurso
    FOREIGN KEY (recurso_id) REFERENCES recurso (id)
    ON UPDATE RESTRICT ON DELETE RESTRICT,
    INDEX idx_rr_recurso (recurso_id)
    ) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- Datos iniciales: el mismo administrador que creaba el Proyecto 1
-- en su primera ejecución (Service.cargarDatosIniciales).
-- INSERT IGNORE evita error si ya existe.
-- ---------------------------------------------------------------------
INSERT IGNORE INTO usuario (id, clave, rol, nombre, telefono)
VALUES ('admin', 'admin', 'ADMINISTRADOR', NULL, NULL);