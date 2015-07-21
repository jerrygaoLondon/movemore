-- modify sensor description column length from 150 to 500
ALTER TABLE wsi_pibox.sensors modify description VARCHAR(500);