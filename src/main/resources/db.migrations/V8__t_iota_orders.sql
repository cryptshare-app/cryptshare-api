CREATE TABLE t_iota_orders (
  id                  DECIMAL(19, 0),
  version             DECIMAL(19, 0) NOT NULL,
  created_at          TIMESTAMP      NOT NULL,
  updated_at          TIMESTAMP      NOT NULL,
  seed                CHAR(81)       NOT NULL,
  receiver_address    CHAR(81),
  remainder_address   CHAR(81),
  amount              DECIMAL(30, 0),
  user_id             DECIMAL(19, 0),
  currently_processed BOOLEAN,
  CONSTRAINT pk_iota_orders PRIMARY KEY (id)
);

CREATE SEQUENCE sq_iota_orders
  START 1
  INCREMENT 1;