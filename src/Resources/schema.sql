
CREATE DATABASE IF NOT EXISTS mmorpgDB;
USE mmorpgDB;

CREATE TABLE `Rank` (
    rank_id INT PRIMARY KEY,
    title ENUM('Newbie', 'Veteran', 'Legend') NOT NULL
);

CREATE TABLE Player (
    player_id INT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    rank_id INT,
    FOREIGN KEY (rank_id) REFERENCES `Rank`(rank_id)
);

CREATE TABLE Race (
    race_id INT PRIMARY KEY,
    name ENUM('Nord', 'Vaegir', 'Svadya', 'Rodok', 'Kergit') NOT NULL,
    strength INT,
    intelligence INT,
    agility INT
);

CREATE TABLE Clan (
    clan_id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    member_limit INT
);

CREATE TABLE `Character` (
	character_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    level INT,
    health_point INT,
    mana_point INT,
    player_id INT,
    race_id INT,
    clan_id INT,
    FOREIGN KEY (player_id) REFERENCES Player(player_id),
    FOREIGN KEY (race_id) REFERENCES Race(race_id),
    FOREIGN KEY (clan_id) REFERENCES Clan(clan_id)
);


CREATE TABLE Mount (
    mount_id INT PRIMARY KEY,
    type ENUM('Flying', 'Tank', 'Titan') NOT NULL,
    speed INT,
    health INT
);

CREATE TABLE Weapon (
    weapon_id INT PRIMARY KEY,
    type ENUM('Sword', 'Axe', 'Bow') NOT NULL,
    damage INT,
    weight INT
);

CREATE TABLE GuildEvent (
    clan_id INT,
    event_id INT,
    title VARCHAR(100),
    date DATE,
    difficulty ENUM('Easy', 'Normal', 'Hard') NOT NULL,
    max_participants INT,
    PRIMARY KEY (clan_id, event_id),
    FOREIGN KEY (clan_id) REFERENCES Clan(clan_id)
);

CREATE TABLE Dungeon (
    dungeon_id INT PRIMARY KEY,
    name VARCHAR(100),
    difficulty ENUM('Easy', 'Normal', 'Hard') NOT NULL,
    min_level INT
);

CREATE TABLE Loot (
    loot_id INT,
    dungeon_id INT,
    rarity ENUM('Common', 'Rare', 'Epic', 'Legendary') NOT NULL,
    name VARCHAR(100),
    PRIMARY KEY (dungeon_id, loot_id),
    FOREIGN KEY (dungeon_id) REFERENCES Dungeon(dungeon_id)
);

CREATE TABLE Quest (
    quest_id INT PRIMARY KEY,
    description VARCHAR(255),
    xp INT
);

CREATE TABLE Boss (
    boss_id INT,
    dungeon_id INT,
    name VARCHAR(100),
    level INT,
    health_point INT,
    attack_power INT,
    PRIMARY KEY (dungeon_id, boss_id),
    FOREIGN KEY (dungeon_id) REFERENCES Dungeon(dungeon_id)
);

CREATE TABLE character_mounts (
    character_id INT,
    mount_id INT,
    PRIMARY KEY (character_id, mount_id),
    FOREIGN KEY (character_id) REFERENCES `Character`(character_id),
    FOREIGN KEY (mount_id) REFERENCES Mount(mount_id)
);

CREATE TABLE character_weapon (
    character_id INT,
    weapon_id INT,
    PRIMARY KEY (character_id, weapon_id),
    FOREIGN KEY (character_id) REFERENCES `Character`(character_id),
    FOREIGN KEY (weapon_id) REFERENCES Weapon(weapon_id)
);

CREATE TABLE character_quest (
    character_id INT,
    quest_id INT,
    PRIMARY KEY (character_id, quest_id),
    FOREIGN KEY (character_id) REFERENCES `Character`(character_id),
    FOREIGN KEY (quest_id) REFERENCES Quest(quest_id)
);

CREATE TABLE Runs (
    character_id INT,
    dungeon_id INT,
    result ENUM('Win', 'Lose') NOT NULL,
    PRIMARY KEY (character_id, dungeon_id),
    FOREIGN KEY (character_id) REFERENCES `Character`(character_id),
    FOREIGN KEY (dungeon_id) REFERENCES Dungeon(dungeon_id)
);

CREATE TABLE required_dungeon (
    dungeon_id INT,
    required_dungeon_id INT,
    PRIMARY KEY (dungeon_id, required_dungeon_id),
    FOREIGN KEY (dungeon_id) REFERENCES Dungeon(dungeon_id),
    FOREIGN KEY (required_dungeon_id) REFERENCES Dungeon(dungeon_id)
);


-- When a new player is added, the email address is converted to lowercase.
DELIMITER $$
CREATE TRIGGER before_insert_player_email
BEFORE INSERT ON Player
FOR EACH ROW
BEGIN
    SET NEW.email = LOWER(NEW.email);
END $$
DELIMITER ;



-- When a character is added, if the level is entered as less than 1, it is automatically assigned as 1.
DELIMITER $$
CREATE TRIGGER before_character_level_limit
BEFORE INSERT ON `Character`
FOR EACH ROW
BEGIN
    IF NEW.level <1 THEN
        SET NEW.level = 1;
    END IF;
END $$
DELIMITER ;



-- when a character is added a default wapon, mount and quest assigned.
DELIMITER $$
CREATE TRIGGER after_character_start_equipment
AFTER INSERT ON `Character`
FOR EACH ROW
BEGIN
    INSERT INTO character_weapon (character_id, weapon_id)
    VALUES (NEW.character_id, 1);

    INSERT INTO character_mounts (character_id, mount_id)
    VALUES (NEW.character_id, 1);


    INSERT INTO character_quest (character_id, quest_id)
    VALUES (NEW.character_id, 1);
END $$
DELIMITER ;


-- If the character wins the dungeon, his level is increased.
DELIMITER $$
CREATE TRIGGER after_runs_insert_level_up
AFTER INSERT ON Runs
FOR EACH ROW
BEGIN
    IF NEW.result = 'Win' THEN
        UPDATE `Character`
        SET level = level + 1
        WHERE character_id = NEW.character_id;
    END IF;
END $$
DELIMITER ;

-- When a character is created their stats are adjusted based on their race.
DELIMITER $$
CREATE TRIGGER before_character_start_stats
BEFORE INSERT ON `Character`
FOR EACH ROW
BEGIN
    DECLARE str INT;
    DECLARE agi INT;
    DECLARE intl INT;

    SELECT strength, agility, intelligence
    INTO str, agi, intl
    FROM race
    WHERE race_id = NEW.race_id;

    SET NEW.health_point = NEW.health_point + (str * 2) + (agi * 1.5);
    SET NEW.mana_point = NEW.health_point + (intl * 2) + (agi * 1.5);
END $$
DELIMITER ;

-- When a character levels up their health and mana go up by 10.
DELIMITER $$
CREATE TRIGGER level_up_stats
BEFORE UPDATE ON `Character`
FOR EACH ROW
BEGIN
    IF NEW.level = OLD.level + 1 THEN
        SET NEW.health_point = OLD.health_point + 10, NEW.mana_point = OLD.mana_point + 10;
    END IF;
END $$
DELIMITER ;


INSERT INTO `Rank` (rank_id, title) VALUES
(1, 'Newbie'),
(2, 'Veteran'),
(3, 'Legend');

INSERT INTO Player (player_id, username, email, password, rank_id) VALUES
(1, 'Ozan', 'ozan@gmail.com', '123', 1),
(2, 'Serkan', 'serkan@gmail.com', '123', 2),
(3, 'Salih', 'salih@gmail.com', '123', 3),
(4, 'Okay', 'okay@gmail.com', '123', 1);

INSERT INTO Race (race_id, name, strength, intelligence, agility) VALUES
(1, 'Nord', 10, 5, 3),
(2, 'Vaegir', 7, 8, 4),
(3, 'Svadya', 6, 6, 6),
(4,'Rodok', 5,3,9),
(5,'Kergit',12,10,10);

INSERT INTO Clan (clan_id, name, description,member_limit) VALUES
(1, 'Warriors', 'Clan of real warriors',15),
(2, 'Assasins', 'Clan of Assasins',10);

INSERT INTO Mount (mount_id, type, speed, health) VALUES
(1, 'Flying', 80, 100),
(2, 'Tank', 40, 200),
(3, 'Flying', 120, 20),
(4, 'Titan', 200, 200),
(5, 'Tank', 35, 500);

INSERT INTO Weapon (weapon_id, type, damage, weight) VALUES
(1, 'Sword', 50, 10),
(2, 'Bow', 35, 5),
(3, 'Axe', 40, 8),
(4, 'Axe', 25, 1),
(5, 'Bow', 60, 15);

INSERT INTO Quest (quest_id, description, xp) VALUES
(1, 'Spend an hour in the game', 100),
(2, 'Equip a new weapon', 250),
(3, 'Enter your first dungeon', 300),
(4, 'Join a clan event', 400),
(5, 'Defeat 3 bosses without taking damage',1000);

INSERT INTO Dungeon (dungeon_id, name, difficulty, min_level) VALUES
(1, 'Frost Hollow', 'Easy', 5),
(2, 'Volcanic Depths', 'Hard', 15),
(3, 'Whispering Cavern', 'Normal', 10),
(4, 'Sunken Temple', 'Hard', 20),
(5, 'Forgotten Mines', 'Easy', 3),
(6, 'Nuketown', 'Easy',2),
(7, 'Jagged Mountains','Hard',25),
(8, 'Shinny Flowers','Normal',10),
(9, 'Sunken Deep','Easy',5),
(10, 'Demolished Goblin Inn','Normal',11);

INSERT INTO Boss (boss_id, dungeon_id, name, level, health_point, attack_power) VALUES
(1, 1, 'Frost Troll', 6, 300, 25),              -- Frost Hollow
(2, 2, 'Lava Behemoth', 18, 800, 65),           -- Volcanic Depths
(3, 3, 'Whisper Wraith', 11, 450, 35),          -- Whispering Cavern
(4, 4, 'Sunken Leviathan', 22, 1000, 70),       -- Sunken Temple
(5, 5, 'Tunnel Serpent', 4, 200, 20),           -- Forgotten Mines
(6, 6, 'Radiated Raider', 3, 150, 15),          -- Nuketown
(7, 7, 'Mountain Colossus', 26, 1200, 80),      -- Jagged Mountains
(8, 8, 'Blossom Dryad', 12, 400, 30),           -- Shinny Flowers
(9, 9, 'Deepwater Ghoul', 6, 250, 25),          -- Sunken Deep
(10, 10, 'Goblin Warlord', 13, 500, 40);         -- Demolished Goblin Inn

INSERT INTO Loot (loot_id, dungeon_id, rarity, name) VALUES
(1, 1, 'Common', 'Healing Potion'),
(2, 2, 'Epic', 'Golden Apple'),
(3, 9, 'Common', 'Ice Crystal'),
(4, 10, 'Epic', 'Magma Blade'),
(5, 3, 'Rare', 'Echo Bow'),
(6, 4, 'Legendary', 'Trident of the Deep'),
(7, 5, 'Common', 'Miners Ring'),
(8 ,6, 'Epic', 'Broken Shield'),
(9, 8, 'Common', 'Druids Armour'),
(10,7, 'Legendary','Miner Axe'),
(11,10, 'Common', 'Goblin Bow'),
(12,4,'Epic','Fishmens Trumpet'),
(13,7,'Common','Totally Normal Stone'),
(14,2,'Common','Book of Fire'),
(15,1,'Legendary','Icicle');

INSERT INTO GuildEvent (clan_id, event_id, title, date, difficulty, max_participants) VALUES
(1, 1, 'War Practice', '2025-06-01', 'Easy', 10),
(1, 2, 'Collection Event', '2025-06-15', 'Hard', 5),
(2, 3, 'Tournament of Risers','2025-06-16','Hard',15),
(2, 4, 'Dungeon Finder','2025-06-03','Easy',10),
(1, 5, 'Which team will kill the boss first ?','2025-06-03','Hard',15);

INSERT INTO `Character` (character_id, name, level, health_point, mana_point, player_id, race_id, clan_id) VALUES
(1, 'Thorin', 12, 150, 70, 1, 5, 1),  -- Ozan
(2, 'Arya', 20, 120, 90, 2, 2, 2),    -- Serkan
(3, 'Kael', 22, 200, 120, 3, 4, 2),   -- Salih
(4, 'Mira', 15, 130, 85, 4, 1, 1);    -- Okay

INSERT INTO Runs (character_id, dungeon_id, result) VALUES
(1, 1, 'Win'),
(1, 2, 'Lose'),
(1, 3, 'Win'),

(2, 2, 'Win'),
(2, 4, 'Win'),
(2, 7, 'Lose'),

(3, 6, 'Win'),
(3, 7, 'Win'),
(3, 8, 'Win'),
(3, 9, 'Win'),

(4, 1, 'Lose'),
(4, 5, 'Win'),
(4, 10, 'Win');

INSERT INTO required_dungeon (dungeon_id, required_dungeon_id) VALUES
(2, 1),   -- Volcanic Depths requires Frost Hollow
(3, 1),   -- Whispering Cavern requires Frost Hollow
(4, 3),   -- Sunken Temple requires Whispering Cavern
(5, 1),   -- Forgotten Mines requires Frost Hollow
(7, 2),   -- Jagged Mountains requires Volcanic Depths
(8, 3),   -- Shinny Flowers requires Whispering Cavern
(9, 5),   -- Sunken Deep requires Forgotten Mines
(10, 5),  -- Demolished Goblin Inn requires Forgotten Mines
(4, 2),   -- Sunken Temple also requires Volcanic Depths (multi-prerequisite)
(7, 4);   -- Jagged Mountains also requires Sunken Temple

INSERT INTO character_mounts (character_id, mount_id) VALUES
(1, 2),
(2, 4),
(3, 3),
(4, 5);

INSERT INTO character_weapon (character_id, weapon_id) VALUES
(1, 3),  -- Thorin: Axe
(2, 4),  -- Arya: Bow
(2, 5),  -- Arya: Bow
(4, 4);  -- Mira: Axe

INSERT INTO character_quest (character_id, quest_id) VALUES
(1, 2),
(1, 3),
(2, 3),
(2, 4),
(2, 5),
(3, 2),
(3, 3),
(3, 4),
(3, 5),
(4, 2);

CREATE VIEW view_easy_dungeons AS
SELECT *
FROM Dungeon
WHERE difficulty = 'Easy';

CREATE VIEW view_normal_dungeons AS
SELECT *
FROM Dungeon
WHERE difficulty = 'Normal';

CREATE VIEW view_hard_dungeons AS
SELECT *
FROM Dungeon
WHERE difficulty = 'Hard';


DELIMITER //
CREATE FUNCTION login(param_name VARCHAR(32),param_password VARCHAR(1024))
RETURNS INT
READS SQL DATA
BEGIN
    DECLARE temp_id BIGINT UNSIGNED;
    DECLARE success BOOLEAN;

    SET success = FALSE;

    SELECT p.player_id INTO temp_id
    FROM player p
    WHERE p.username = param_name AND p.password = param_password;

    IF temp_id IS NOT NULL THEN
        SET success = TRUE;
    END IF;

    RETURN success;
END//
DELIMITER ;


DELIMITER $$
CREATE TRIGGER after_player_delete
BEFORE DELETE ON Player
FOR EACH ROW
BEGIN
	DELETE FROM `Character` WHERE player_id = OLD.player_id;
    DELETE cm FROM character_mounts cm  JOIN `Character` ch ON ch.character_id = cm.character_id WHERE ch.player_id = OLD.player_id;
    DELETE cw FROM character_weapon cw  JOIN `Character` ch ON ch.character_id = cw.character_id WHERE ch.player_id = OLD.player_id;
    DELETE cq FROM character_quest cq  JOIN `Character` ch ON ch.character_id = cq.character_id WHERE ch.player_id = OLD.player_id;
    DELETE r FROM runs r  JOIN `Character` ch ON ch.character_id = r.character_id WHERE ch.player_id = OLD.player_id;
    DELETE FROM `Character` WHERE player_id = OLD.player_id;
END $$
DELIMITER ;


DELIMITER $$
CREATE TRIGGER before_character_delete
BEFORE DELETE ON `Character`
FOR EACH ROW
BEGIN
    DELETE FROM character_mounts WHERE character_id = OLD.character_id;
    DELETE FROM character_weapon WHERE character_id = OLD.character_id;
    DELETE FROM character_quest WHERE character_id = OLD.character_id;
    DELETE FROM runs WHERE character_id = OLD.character_id;
END $$
DELIMITER ;



-- Procedures for Queries

DELIMITER $$

CREATE PROCEDURE CharactersWithAxeInAssasinsClan()
BEGIN
    SELECT c.name AS character_name
    FROM `Character` c
    JOIN Clan cl ON c.clan_id = cl.clan_id
    WHERE cl.name = 'Assasins'
      AND c.character_id IN (
        SELECT character_id
        FROM character_weapon cw
        JOIN Weapon w ON cw.weapon_id = w.weapon_id
        WHERE w.type = 'Axe'
      );
END$$

CREATE PROCEDURE DungeonsBeatenbyCharacters()
BEGIN
    SELECT d.name AS dungeon_name
    FROM Runs r
    JOIN Dungeon d ON r.dungeon_id = d.dungeon_id
    WHERE r.result = 'Win';
END$$


CREATE PROCEDURE CharactersCompletedAllQuests()
BEGIN
    SELECT c.name AS character_name
    FROM `Character` c
    WHERE NOT EXISTS (
        SELECT q.quest_id
        FROM Quest q
        WHERE NOT EXISTS (
            SELECT 1
            FROM character_quest cq
            WHERE cq.character_id = c.character_id
              AND cq.quest_id = q.quest_id
        )
    );
END$$


CREATE PROCEDURE ClansWithAvgLevelGreaterThan10()
BEGIN
    SELECT cl.name AS clan_name
    FROM Clan cl
    JOIN `Character` c ON cl.clan_id = c.clan_id
    GROUP BY cl.clan_id
    HAVING AVG(c.level) > 10;
END$$

CREATE PROCEDURE CharactersLostWithHighLevel()
BEGIN
    SELECT DISTINCT c.name
    FROM `Character` c
    JOIN Runs r ON c.character_id = r.character_id
    WHERE r.result = 'Lose' AND c.level > 10;
END$$

CREATE PROCEDURE CharactersWithManyWeaponsAndMounts()
BEGIN
    SELECT c.name
    FROM `Character` c
    JOIN character_weapon cw ON c.character_id = cw.character_id
    WHERE c.character_id IN (
        SELECT cm.character_id
        FROM character_mounts cm
        GROUP BY cm.character_id
        HAVING COUNT(DISTINCT cm.mount_id) > 1
    )
    GROUP BY c.character_id
    HAVING COUNT(DISTINCT cw.weapon_id) > 1;
END$$

CREATE PROCEDURE UpcomingEvents()
BEGIN
    SELECT ge.title AS event_title
    FROM GuildEvent ge
    JOIN Clan cl ON ge.clan_id = cl.clan_id
    WHERE ge.date >= CURDATE()
    ORDER BY ge.date;
END$$

CREATE PROCEDURE MostPopularDungeon()
BEGIN
    SELECT d.name AS dungeon_name
    FROM Runs r
    JOIN Dungeon d ON r.dungeon_id = d.dungeon_id
    GROUP BY d.dungeon_id
    ORDER BY COUNT(*) DESC
    LIMIT 1;
END$$


CREATE PROCEDURE ShowAllLoots()
BEGIN
    SELECT * FROM Loot;
END$$

CREATE PROCEDURE ShowAllBosses()
BEGIN
    SELECT * FROM Boss;
END$$

DELIMITER ;