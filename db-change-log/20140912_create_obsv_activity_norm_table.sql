-- create table obsv_activity_norm for normalisation values for activity sensor observations

CREATE TABLE  `wsi_pibox`.`obsv_activity_norm` (
  `final_activityType` varchar(255) DEFAULT NULL,
  `finalConfidence` double DEFAULT NULL,
  `final_latitude` double DEFAULT NULL,
  `final_longitude` double DEFAULT NULL,
  `updated` datetime DEFAULT NULL,
  `activityObsv_activitySensor_obsv_id` bigint(20) NOT NULL,
  PRIMARY KEY (`activityObsv_activitySensor_obsv_id`),
  UNIQUE KEY `activityObsv_activitySensor_obsv_id` (`activityObsv_activitySensor_obsv_id`),
  KEY `FK6F7A85E3A6D9197F` (`activityObsv_activitySensor_obsv_id`),
  CONSTRAINT `FK6F7A85E3A6D9197F` FOREIGN KEY (`activityObsv_activitySensor_obsv_id`) REFERENCES `obsv_activity_detection` (`activitySensor_obsv_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;