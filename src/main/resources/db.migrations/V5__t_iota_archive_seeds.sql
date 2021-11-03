CREATE TABLE t_iota_archive_seeds (
  id         DECIMAL(19, 0),
  version    DECIMAL(19, 0) NOT NULL,
  created_at TIMESTAMP      NOT NULL,
  updated_at TIMESTAMP      NOT NULL,
  seed       CHAR(81)       NOT NULL,
  user_id    DECIMAL(19, 0),
  CONSTRAINT pk_iota_archive_seeds PRIMARY KEY (id),
  CONSTRAINT fk_iota_archive_seeds FOREIGN KEY (user_id) REFERENCES t_users (id),
  CONSTRAINT uc_iota_archive_seeds_unique_seed UNIQUE (user_id, seed)
);
CREATE SEQUENCE sq_iota_archive_seeds
  START 1
  INCREMENT 1;