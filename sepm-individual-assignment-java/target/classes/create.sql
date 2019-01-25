CREATE TABLE IF NOT EXISTS Vehicle (id BIGINT AUTO_INCREMENT PRIMARY KEY, label VARCHAR(30) NOT NULL, year INTEGER NOT NULL, description VARCHAR(100), nrOfSeats INTEGER, registrationNr VARCHAR(30), type VARCHAR(12) NOT NULL CHECK (type IN ('motorized','muscle power')), power INTEGER, price DOUBLE NOT NULL, date DATE, picture VARCHAR, drivingLicence VARCHAR(3), dateChanged DATE, isDeleted BOOLEAN NOT NULL);
CREATE TABLE IF NOT EXISTS Reservation (id BIGINT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(30) NOT NULL, cardNr VARCHAR(30) NOT NULL, dateFrom TIMESTAMP NOT NULL, dateTo TIMESTAMP NOT NULL, sum DOUBLE, date DATE, dateBill TIMESTAMP, billNr INTEGER, status VARCHAR(9) NOT NULL CHECK (status IN ('canceled', 'paid', 'open')));
CREATE TABLE IF NOT EXISTS Bill (id BIGINT AUTO_INCREMENT PRIMARY KEY, vehicleId BIGINT NOT NULL, reservationId BIGINT NOT NULL, model VARCHAR(30) NOT NULL, year INTEGER NOT NULL, description VARCHAR(100), nrOfSeats INTEGER, registrationNr VARCHAR(30), type VARCHAR(12) CHECK (type IN ('motorized','muscle power')), power INTEGER, price DOUBLE NOT NULL, picture VARCHAR, drivingLicence VARCHAR(3), licenceNr VARCHAR(30), licenceDate DATE);