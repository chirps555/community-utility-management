/*
SQLyog Community v13.2.0 (64 bit)
MySQL - 8.0.39 : Database - community_utility
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`community_utility` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `community_utility`;

/*Table structure for table `meter_reading` */

DROP TABLE IF EXISTS `meter_reading`;

CREATE TABLE `meter_reading` (
  `id` varchar(255) NOT NULL,
  `electric_reading` double DEFAULT NULL,
  `operator` varchar(255) DEFAULT NULL,
  `period` varchar(255) DEFAULT NULL,
  `read_date` varchar(255) DEFAULT NULL,
  `resident_id` varchar(255) DEFAULT NULL,
  `resident_name` varchar(255) DEFAULT NULL,
  `water_reading` double DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `meter_reading` */

insert  into `meter_reading`(`id`,`electric_reading`,`operator`,`period`,`read_date`,`resident_id`,`resident_name`,`water_reading`) values 
('M002',420.1,'管理员','2026-04','2026-04-28','R002','李四',88.3);

/*Table structure for table `payment_order` */

DROP TABLE IF EXISTS `payment_order`;

CREATE TABLE `payment_order` (
  `id` varchar(255) NOT NULL,
  `create_time` varchar(255) DEFAULT NULL,
  `electric_fee` double DEFAULT NULL,
  `pay_time` varchar(255) DEFAULT NULL,
  `period` varchar(255) DEFAULT NULL,
  `resident_id` varchar(255) DEFAULT NULL,
  `resident_name` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `total_amount` double DEFAULT NULL,
  `water_fee` double DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `payment_order` */

insert  into `payment_order`(`id`,`create_time`,`electric_fee`,`pay_time`,`period`,`resident_id`,`resident_name`,`status`,`total_amount`,`water_fee`) values 
('O_1779437029897_cis1am','2026-05-22 16:03',0,'','2026-04','R002','李四','待缴费',14.35,14.35),
('O002','2026-05-01 09:00',35.2,'2026/5/22 16:03:43','2026-04','R002','李四','已缴费',49.55,14.35);

/*Table structure for table `resident` */

DROP TABLE IF EXISTS `resident`;

CREATE TABLE `resident` (
  `id` varchar(255) NOT NULL,
  `building` varchar(255) DEFAULT NULL,
  `move_in_date` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `unit` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `resident` */

insert  into `resident`(`id`,`building`,`move_in_date`,`name`,`phone`,`type`,`unit`) values 
('R002','1栋','2022-06-01','李四','13800138002','租户','202'),
('R003','2栋','2019-11-20','王五','13800138003','业主','301');

/*Table structure for table `sys_user` */

DROP TABLE IF EXISTS `sys_user`;

CREATE TABLE `sys_user` (
  `id` varchar(255) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `resident_id` varchar(255) DEFAULT NULL,
  `role` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `sys_user` */

insert  into `sys_user`(`id`,`name`,`password`,`resident_id`,`role`,`username`) values 
('admin','系统管理员','admin123',NULL,'ADMIN','admin'),
('u001','张三','123456','R001','RESIDENT','13800138001'),
('u002','李四','123456','R002','RESIDENT','13800138002');

/*Table structure for table `usage_flow` */

DROP TABLE IF EXISTS `usage_flow`;

CREATE TABLE `usage_flow` (
  `id` varchar(255) NOT NULL,
  `amount` double DEFAULT NULL,
  `create_time` varchar(255) DEFAULT NULL,
  `period` varchar(255) DEFAULT NULL,
  `resident_id` varchar(255) DEFAULT NULL,
  `resident_name` varchar(255) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `usage_amount` double DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `usage_flow` */

insert  into `usage_flow`(`id`,`amount`,`create_time`,`period`,`resident_id`,`resident_name`,`type`,`usage_amount`) values 
('F003',14.35,'2026-04-28 10:05','2026-04','R002','李四','水',4.1);

/*Table structure for table `utility` */

DROP TABLE IF EXISTS `utility`;

CREATE TABLE `utility` (
  `id` varchar(255) NOT NULL,
  `electric_base` double DEFAULT NULL,
  `electric_price` double DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `resident_id` varchar(255) DEFAULT NULL,
  `resident_name` varchar(255) DEFAULT NULL,
  `water_base` double DEFAULT NULL,
  `water_price` double DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

/*Data for the table `utility` */

insert  into `utility`(`id`,`electric_base`,`electric_price`,`remark`,`resident_id`,`resident_name`,`water_base`,`water_price`) values 
('U002',50,0.62,'含公摊','R002','李四',15,6.5),
('U003',0,0.65,'','R003','王五',0,1.8);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
