CREATE SCHEMA test_db_schema;
CREATE USER test_db_user@'%'
		IDENTIFIED BY 'test_db_password';
CREATE USER test_db_user_rc@'%'
		IDENTIFIED BY 'test_db_password';
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, REFERENCES, INDEX, ALTER, CREATE VIEW
		ON test_db_schema.*
		TO test_db_user@'%';
GRANT SELECT
		ON test_db_schema.*
		TO test_db_user_rc@'%';
GRANT REPLICATION CLIENT
		ON *.*
		TO test_db_user_rc@'%';
