-- Users table
DROP INDEX IF EXISTS index_1;
CREATE INDEX index_1 ON Users USING BTREE (userID, userType);

-- Hotel table
DROP INDEX IF EXISTS index_2;
CREATE INDEX index_2 ON Hotel USING BTREE (latitude, longitude);

-- Rooms table
DROP INDEX IF EXISTS index_3;
CREATE INDEX index_3 ON Rooms USING BTREE (hotelID, roomNumber);

-- RoomBookings table
DROP INDEX IF EXISTS index_4;
CREATE INDEX index_4 ON RoomBookings USING BTREE (hotelID, roomNumber, bookingDate);

DROP INDEX IF EXISTS index_5;
CREATE INDEX index_5 ON RoomBookings USING BTREE (customerID, bookingDate);

-- RoomUpdatesLog table
DROP INDEX IF EXISTS index_6;
CREATE INDEX index_6 ON RoomUpdatesLog USING BTREE (hotelID, updatedOn);

-- RoomRepairs table
DROP INDEX IF EXISTS index_7;
CREATE INDEX index_7 ON RoomRepairs USING BTREE (companyID, hotelID, roomNumber, repairDate);

-- RoomRepairRequests table
DROP INDEX IF EXISTS index_8;
CREATE INDEX index_8 ON RoomRepairRequests USING BTREE (managerID, repairID);