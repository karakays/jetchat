CREATE TABLE role (
	role_id INTEGER PRIMARY KEY,
	name VARCHAR(255)
);

CREATE TABLE chat_user (
	username VARCHAR(15) NOT NULL, 
	email VARCHAR(255) NOT NULL, 
	name VARCHAR(255) NOT NULL, 
	password VARCHAR(255) NOT NULL, 
	PRIMARY KEY (username)
);

CREATE TABLE user_role (
	username VARCHAR(255) NOT NULL, 
	role_id INTEGER NOT NULL, 
	PRIMARY KEY (username, role_id)
);

ALTER TABLE user_role ADD CONSTRAINT fk_user_role_role_id FOREIGN KEY (role_id) REFERENCES role (role_id);
ALTER TABLE user_role ADD CONSTRAINT fk_user_role_username FOREIGN KEY (username) REFERENCES chat_user (username);
