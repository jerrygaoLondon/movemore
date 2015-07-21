-- add a new column to provide location accuracy in activity observation
ALTER TABLE wsi_pibox.obsv_obd_sensor ADD engineCoolantTemperature float DEFAULT NULL;
ALTER TABLE wsi_pibox.obsv_obd_sensor ADD engineLoad decimal(5,3) DEFAULT NULL;
ALTER TABLE wsi_pibox.obsv_obd_sensor ADD engineRuntime TIME DEFAULT NULL;
ALTER TABLE wsi_pibox.obsv_obd_sensor ADD barometricPressure float DEFAULT NULL;
ALTER TABLE wsi_pibox.obsv_obd_sensor ADD fuelConsumption float DEFAULT NULL;
ALTER TABLE wsi_pibox.obsv_obd_sensor ADD fuelPressure float DEFAULT NULL;
ALTER TABLE wsi_pibox.obsv_obd_sensor ADD massAirFlow float DEFAULT NULL;
ALTER TABLE wsi_pibox.obsv_obd_sensor ADD intakeManifoldPressure float DEFAULT NULL;