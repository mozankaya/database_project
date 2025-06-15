
# MMORPG Database Management System

## Authors

- **Mehmed Okay Aslan**
- **Mehmet Ozan Kaya**
- **Salih Ağralı**
- **Serkan Acar**

## Description

This project is a full-fledged database management system designed for an MMORPG (Massively Multiplayer Online Role-Playing Game). The system allows managing players, characters, clans, races, weapons, dungeons, quests, loot, bosses, and events.

The backend is powered by **MySQL**, containing a well-structured relational schema with stored procedures, triggers, and views. The frontend is implemented using **Java Swing**, providing a graphical user interface for interacting with the database.

## Technologies Used

- **Database**: MySQL
- **Frontend**: Java Swing (GUI)
- **Programming Language**: Java
- **SQL Features**: 
  - Stored Procedures
  - Triggers
  - Views
  - Functions
- **IDE**: IntelliJ IDEAs

## Features

- Complete MMORPG data model with multiple entity relationships.
- Automated business logic via triggers and stored procedures.
- Input validation and data consistency enforced at database level.
- Login functionality via stored SQL function.
- Fully functional Java Swing GUI for database interaction (CRUD operations).
- Rich dataset with sample data for testing.
- Advanced queries via stored procedures:
  - Upcoming Events
  - Characters who completed all quests
  - Clans with high average level
  - Most popular dungeon
  - And many more.

## Database Schema Overview

The database consists of multiple interconnected tables:
- `Player`
- `Rank`
- `Character`
- `Race`
- `Clan`
- `Weapon`
- `Mount`
- `Quest`
- `Dungeon`
- `Loot`
- `Boss`
- `GuildEvent`
- `Runs`
- `RequiredDungeon`
- Many-to-many mapping tables for characters' equipment and quests

## Installation

### 1. MySQL Database Setup

- Install MySQL Server (version 8.0 or above recommended).
- Use MySQL Workbench (or any SQL client).
- Run the provided SQL script to create the database, tables, triggers, views, functions, and stored procedures.
- The full SQL script is located inside the project directory.

### 2. Java Swing Application Setup

- Install Java JDK 8 or above.
- Open the Java Swing project in your preferred IDE.
- Configure the database connection string with your MySQL server credentials.
- Build and run the application.

### Database Connection Example (in Java):
```java
String url = "jdbc:mysql://localhost:3306/mmorpgDB";
String username = "your_mysql_username";
String password = "your_mysql_password";
Connection conn = DriverManager.getConnection(url, username, password);

