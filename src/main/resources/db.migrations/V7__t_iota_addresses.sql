CREATE TABLE t_iota_addresses (
  id                   DECIMAL(19, 0),
  version              DECIMAL(19, 0) NOT NULL,
  created_at           TIMESTAMP      NOT NULL,
  updated_at           TIMESTAMP      NOT NULL,
  address              CHAR(81)       NOT NULL,
  balance              DECIMAL(30, 0),
  balance_before_order DECIMAL(30, 0),
  index                DECIMAL(20, 0),
  user_id              DECIMAL(19, 0),
  used_in_order        DECIMAL(19, 0),
  CONSTRAINT pk_iota_addresses PRIMARY KEY (id),
  CONSTRAINT fk_iota_addresses FOREIGN KEY (user_id) REFERENCES t_users (id),
  CONSTRAINT uc_iota_addresses_unique_address UNIQUE (address),
  CONSTRAINT uc_iota_addresses_unique_index_for_user_id UNIQUE (user_id, index)
);

CREATE SEQUENCE sq_iota_addresses
  START 1
  INCREMENT 1;