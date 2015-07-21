-- add a new column to provide attachment file relative path for observations
ALTER TABLE wsi_pibox.observations ADD attachment varchar(500) DEFAULT NULL;
ALTER TABLE wsi_pibox.obsv_voice_sensor ADD attachment varchar(500) DEFAULT NULL;