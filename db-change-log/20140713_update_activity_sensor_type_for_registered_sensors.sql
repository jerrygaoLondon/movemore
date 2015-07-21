-- update sensor types
INSERT INTO sensor_type VALUES(7, 'Activity Sensor');
update wsi_pibox.sensors set sensor_type = 7 where sensor_id = "3000022";

