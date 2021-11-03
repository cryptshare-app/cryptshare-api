CREATE TABLE t_iota (
  id                      DECIMAL(19, 0),
  version                 DECIMAL(19, 0) NOT NULL,
  created_at              TIMESTAMP      NOT NULL,
  updated_at              TIMESTAMP      NOT NULL,
  current_seed            CHAR(81)       NOT NULL,
  current_balance         DECIMAL(30, 0),
  user_id                 DECIMAL(19, 0),
  lowest_unused_index_wob DECIMAL(19, 0),
  CONSTRAINT pk_iota PRIMARY KEY (id),
  CONSTRAINT fk_iota FOREIGN KEY (user_id) REFERENCES t_users (id)
);
CREATE SEQUENCE sq_iota
  START 1
  INCREMENT 1;