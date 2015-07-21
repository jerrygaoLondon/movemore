-- index for obsv_time to optimise time-range query and aggregation
ALTER TABLE `wsi_pibox`.`obsv_device_detection` DROP INDEX `FK80B2798537C0404D`, ADD INDEX `FK80B2798537C0404D` USING BTREE(`devDetect_obsv_id`, `obsv_time`);
 
ALTER TABLE `wsi_pibox`.`obsv_activity_detection` DROP PRIMARY KEY, ADD PRIMARY KEY  USING BTREE(`activitySensor_obsv_id`, `obsv_time`);
 
ALTER TABLE `wsi_pibox`.`obsv_carregplate_detection` DROP PRIMARY KEY, ADD PRIMARY KEY  USING BTREE(`carRegPlate_obsv_id`, `obsv_time`);
 
ALTER TABLE `wsi_pibox`.`obsv_obd_sensor` DROP PRIMARY KEY, ADD PRIMARY KEY  USING BTREE(`obdSensor_obsv_id`, `obsv_time`);
 
ALTER TABLE `wsi_pibox`.`obsv_people_counter` DROP PRIMARY KEY, ADD PRIMARY KEY  USING BTREE(`pCount_obsv_id`, `obsv_time`);
 
ALTER TABLE `wsi_pibox`.`obsv_voice_sensor` DROP PRIMARY KEY, ADD PRIMARY KEY  USING BTREE(`voiceSensor_obsv_id`, `obsv_time`);
 
ALTER TABLE `wsi_pibox`.`observations` DROP PRIMARY KEY, ADD PRIMARY KEY  USING BTREE(`obsv_id`, `obsv_time`);