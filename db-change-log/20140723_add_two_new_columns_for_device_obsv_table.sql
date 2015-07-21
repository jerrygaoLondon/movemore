-- add a new sensor type to categorise all registered sensors
ALTER TABLE wsi_pibox.obsv_device_detection ADD first_obsv_time datetime DEFAULT NULL;
ALTER TABLE wsi_pibox.obsv_device_detection ADD last_obsv_time datetime DEFAULT NULL;