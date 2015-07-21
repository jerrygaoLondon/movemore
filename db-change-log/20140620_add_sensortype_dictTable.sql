-- add sensor type dictionary table
create table sensor_type (id bigint not null, name varchar(255), primary key (id)) ENGINE=InnoDB;

INSERT INTO sensor_type VALUES(0, 'Unclassified');
INSERT INTO sensor_type VALUES(1, 'Bluetooth Device Sensor - Bluetooth Scanner');
INSERT INTO sensor_type VALUES(2, 'WIFI Device Sensor - Wifi counter');
INSERT INTO sensor_type VALUES(3, 'People Counter - Number of Faces');
INSERT INTO sensor_type VALUES(4, 'Car Registration Plates Detector');
INSERT INTO sensor_type VALUES(5, 'OBD car speed detector');