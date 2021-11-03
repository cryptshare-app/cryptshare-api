 CREATE TABLE t_users(
  id DECIMAL(19,0) NOT NULL,
  version DECIMAL(19,0) NOT NULL,
  user_name VARCHAR(32) NOT NULL UNIQUE ,
  email VARCHAR(100) NOT NULL UNIQUE ,
  password_hash VARCHAR(64) NOT NULL,
  password_salt VARCHAR(32) NOT NULL,
  created_at TIMESTAMP NOT NULL,
  updated_at TIMESTAMP NOT NULL,
  CONSTRAINT pk_users PRIMARY KEY (id),
  CONSTRAINT u_user_salt UNIQUE (password_salt,password_hash)
 );

CREATE SEQUENCE sq_users
  START 1
  INCREMENT 1;