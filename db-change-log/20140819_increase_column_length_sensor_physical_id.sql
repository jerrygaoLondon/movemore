-- increase column length for `sensor_physical_id`
ALTER TABLE `wsi_pibox`.`sensors` MODIFY COLUMN `sensor_physical_id` VARCHAR(100) CHARACTER SET latin1 COLLATE latin1_swedish_ci;