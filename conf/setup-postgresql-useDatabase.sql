CREATE SCHEMA test_db_schema
		AUTHORIZATION test_db_user;

GRANT ALL PRIVILEGES
		ON SCHEMA public
		TO test_db_user_public;

-- loading pgcrypto extension
CREATE SCHEMA test_db_pgcrypto_schema;
CREATE EXTENSION pgcrypto
		WITH SCHEMA test_db_pgcrypto_schema;
GRANT USAGE
		ON SCHEMA test_db_pgcrypto_schema
		TO test_db_user, test_db_user_public;
GRANT EXECUTE
		ON ALL FUNCTIONS IN SCHEMA test_db_pgcrypto_schema
		TO test_db_user, test_db_user_public;
