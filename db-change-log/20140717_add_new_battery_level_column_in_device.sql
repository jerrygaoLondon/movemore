-- add a new sensor type to categorise all registered sensors
ALTER TABLE wsi_pibox.device ADD batteryLevel float DEFAULT NULL;