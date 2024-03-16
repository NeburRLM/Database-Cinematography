-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Versión del servidor:         11.4.0-MariaDB - mariadb.org binary distribution
-- SO del servidor:              Win64
-- HeidiSQL Versión:             12.3.0.6589
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

-- Volcando estructura para tabla practica1_sio.age
CREATE TABLE IF NOT EXISTS `age` (
  `id_age` int(11) NOT NULL AUTO_INCREMENT,
  `age` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id_age`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- La exportación de datos fue deseleccionada.

-- Volcando estructura para tabla practica1_sio.charact
CREATE TABLE IF NOT EXISTS `charact` (
  `id_character` int(11) NOT NULL AUTO_INCREMENT,
  `charact` varchar(150) DEFAULT NULL,
  PRIMARY KEY (`id_character`)
) ENGINE=InnoDB AUTO_INCREMENT=191761 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- La exportación de datos fue deseleccionada.

-- Volcando estructura para tabla practica1_sio.country
CREATE TABLE IF NOT EXISTS `country` (
  `id_country` int(11) NOT NULL AUTO_INCREMENT,
  `country` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id_country`),
  UNIQUE KEY `id_country` (`id_country`)
) ENGINE=InnoDB AUTO_INCREMENT=156 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- La exportación de datos fue deseleccionada.

-- Volcando estructura para tabla practica1_sio.genres
CREATE TABLE IF NOT EXISTS `genres` (
  `id_genres` int(11) NOT NULL AUTO_INCREMENT,
  `genres` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id_genres`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- La exportación de datos fue deseleccionada.

-- Volcando estructura para tabla practica1_sio.info_persons
CREATE TABLE IF NOT EXISTS `info_persons` (
  `id_info` int(11) NOT NULL AUTO_INCREMENT,
  `id_title` varchar(50) DEFAULT NULL,
  `id_person` int(11) DEFAULT NULL,
  PRIMARY KEY (`id_info`),
  KEY `FK_info_persons_titles` (`id_title`),
  KEY `FK_info_persons_person` (`id_person`),
  CONSTRAINT `FK_info_persons_person` FOREIGN KEY (`id_person`) REFERENCES `person` (`id_person`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_info_persons_titles` FOREIGN KEY (`id_title`) REFERENCES `titles` (`id_title`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=343957 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- La exportación de datos fue deseleccionada.

-- Volcando estructura para tabla practica1_sio.person
CREATE TABLE IF NOT EXISTS `person` (
  `id_person` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id_person`)
) ENGINE=InnoDB AUTO_INCREMENT=2462819 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- La exportación de datos fue deseleccionada.

-- Volcando estructura para tabla practica1_sio.provider
CREATE TABLE IF NOT EXISTS `provider` (
  `id_provider` int(11) NOT NULL AUTO_INCREMENT,
  `provider` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id_provider`) USING BTREE,
  UNIQUE KEY `id_proveidor` (`id_provider`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- La exportación de datos fue deseleccionada.

-- Volcando estructura para tabla practica1_sio.role
CREATE TABLE IF NOT EXISTS `role` (
  `id_info` int(11) DEFAULT NULL,
  `id_character` int(11) DEFAULT NULL,
  `role` varchar(50) DEFAULT NULL,
  KEY `FK_role_info_persons` (`id_info`),
  KEY `FK_role_character` (`id_character`),
  CONSTRAINT `FK_role_character` FOREIGN KEY (`id_character`) REFERENCES `charact` (`id_character`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_role_info_persons` FOREIGN KEY (`id_info`) REFERENCES `info_persons` (`id_info`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- La exportación de datos fue deseleccionada.

-- Volcando estructura para tabla practica1_sio.titles
CREATE TABLE IF NOT EXISTS `titles` (
  `id_title` varchar(50) NOT NULL DEFAULT '',
  `title` varchar(150) DEFAULT NULL,
  `type` enum('SHOW','MOVIE') DEFAULT NULL,
  `description` text DEFAULT NULL,
  `release_year` int(11) DEFAULT NULL,
  `runtime` int(11) DEFAULT NULL,
  `seasons` int(11) DEFAULT NULL,
  `imdb_score` decimal(3,1) DEFAULT NULL,
  `imdb_votes` int(11) DEFAULT NULL,
  `tmdb_popularity` decimal(9,4) DEFAULT NULL,
  `tmdb_score` decimal(3,1) DEFAULT NULL,
  PRIMARY KEY (`id_title`) USING BTREE,
  UNIQUE KEY `title_id` (`id_title`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- La exportación de datos fue deseleccionada.

-- Volcando estructura para tabla practica1_sio.title_age
CREATE TABLE IF NOT EXISTS `title_age` (
  `id_title` varchar(50) DEFAULT NULL,
  `id_age` int(11) DEFAULT NULL,
  KEY `FK_title_age_age` (`id_age`),
  KEY `FK_title_age_titles` (`id_title`),
  CONSTRAINT `FK_title_age_age` FOREIGN KEY (`id_age`) REFERENCES `age` (`id_age`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_title_age_titles` FOREIGN KEY (`id_title`) REFERENCES `titles` (`id_title`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- La exportación de datos fue deseleccionada.

-- Volcando estructura para tabla practica1_sio.title_country
CREATE TABLE IF NOT EXISTS `title_country` (
  `id_title` varchar(50) DEFAULT NULL,
  `id_country` int(11) DEFAULT NULL,
  KEY `FK_title_country_titles` (`id_title`),
  KEY `FK_title_country_country` (`id_country`),
  CONSTRAINT `FK_title_country_country` FOREIGN KEY (`id_country`) REFERENCES `country` (`id_country`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_title_country_titles` FOREIGN KEY (`id_title`) REFERENCES `titles` (`id_title`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- La exportación de datos fue deseleccionada.

-- Volcando estructura para tabla practica1_sio.title_genres
CREATE TABLE IF NOT EXISTS `title_genres` (
  `id_title` varchar(50) DEFAULT NULL,
  `id_genres` int(11) DEFAULT NULL,
  KEY `FK_title_genres_titles` (`id_title`),
  KEY `FK_title_genres_genres` (`id_genres`),
  CONSTRAINT `FK_title_genres_genres` FOREIGN KEY (`id_genres`) REFERENCES `genres` (`id_genres`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_title_genres_titles` FOREIGN KEY (`id_title`) REFERENCES `titles` (`id_title`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- La exportación de datos fue deseleccionada.

-- Volcando estructura para tabla practica1_sio.title_provider
CREATE TABLE IF NOT EXISTS `title_provider` (
  `id_title` varchar(50) DEFAULT NULL,
  `id_provider` int(11) DEFAULT NULL,
  KEY `FK_title_provider_titles` (`id_title`),
  KEY `FK_title_provider_provider` (`id_provider`),
  CONSTRAINT `FK_title_provider_provider` FOREIGN KEY (`id_provider`) REFERENCES `provider` (`id_provider`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `FK_title_provider_titles` FOREIGN KEY (`id_title`) REFERENCES `titles` (`id_title`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- La exportación de datos fue deseleccionada.

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
