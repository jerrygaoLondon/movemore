-- add a new column to provide location accuracy in activity observation
ALTER TABLE wsi_pibox.obsv_activity_detection ADD locAccuracy float DEFAULT NULL;