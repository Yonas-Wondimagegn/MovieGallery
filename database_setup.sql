-- Movies Gallery Database Setup
-- Run this in phpMyAdmin or MySQL CLI

CREATE DATABASE IF NOT EXISTS movies_gallery;
USE movies_gallery;

CREATE TABLE IF NOT EXISTS Producer (
    producerID  INT AUTO_INCREMENT PRIMARY KEY,
    fullName    VARCHAR(100) NOT NULL,
    phoneNumber VARCHAR(20)  NOT NULL
);

CREATE TABLE IF NOT EXISTS Category (
    categoryID   INT AUTO_INCREMENT PRIMARY KEY,
    categoryName VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS Movie (
    movieID         INT AUTO_INCREMENT PRIMARY KEY,
    movieCode       VARCHAR(20) NOT NULL UNIQUE,   -- e.g. M001
    title           VARCHAR(150) NOT NULL,
    type            VARCHAR(50),
    lengthHours     FLOAT,
    numberOfActors  INT,
    producerID      INT,
    categoryID      INT,
    FOREIGN KEY (producerID) REFERENCES Producer(producerID),
    FOREIGN KEY (categoryID) REFERENCES Category(categoryID)
);

CREATE TABLE IF NOT EXISTS MovieContent (
    contentID      INT AUTO_INCREMENT PRIMARY KEY,
    movieID        INT NOT NULL,
    fullVersion    TEXT,   -- video quality / full content descriptor
    highlight1     TEXT,   -- first highlight/trailer description
    highlight2     TEXT,   -- second highlight/trailer description
    FOREIGN KEY (movieID) REFERENCES Movie(movieID)
);

CREATE TABLE IF NOT EXISTS Member (
    memberID   INT AUTO_INCREMENT PRIMARY KEY,
    fullName   VARCHAR(100) NOT NULL,
    cellPhone  VARCHAR(20)  NOT NULL,
    subscribed BOOLEAN      NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS Admin (
    adminID     INT AUTO_INCREMENT PRIMARY KEY,
    fullName    VARCHAR(100) NOT NULL,
    userAccount VARCHAR(50)  UNIQUE NOT NULL,
    password    VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS Review (
    reviewID   INT AUTO_INCREMENT PRIMARY KEY,
    memberID   INT  NOT NULL,
    movieID    INT  NOT NULL,
    reviewText TEXT NOT NULL,
    reviewDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (memberID) REFERENCES Member(memberID),
    FOREIGN KEY (movieID)  REFERENCES Movie(movieID)
);

-- Default admin account (login: admin / 1234)
INSERT IGNORE INTO Admin (fullName, userAccount, password)
VALUES ('Super Admin', 'admin', '1234');
