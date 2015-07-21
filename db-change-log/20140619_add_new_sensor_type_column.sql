-- add a new sensor type to categorise all registered sensors
ALTER TABLE wsi_pibox.sensors ADD sensor_type INT(2) DEFAULT 0;