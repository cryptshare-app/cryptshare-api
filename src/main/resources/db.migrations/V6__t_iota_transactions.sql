CREATE TABLE t_iota_transactions (
  id               DECIMAL(19, 0),
  version          DECIMAL(19, 0) NOT NULL,
  created_at       TIMESTAMP      NOT NULL,
  updated_at       TIMESTAMP      NOT NULL,
  receiver_address CHAR(81)       NOT NULL,
  amount           DECIMAL(30, 0),
  user_id          DECIMAL(19, 0),
  index            DECIMAL(20, 0),
  status           DECIMAL(5, 0),
  times_checked    DECIMAL(15, 0) DEFAULT 0,
  is_remainder     BOOLEAN,
  sender_id        DECIMAL(19, 0),
  receiver_id      DECIMAL(19, 0),
  group_title      VARCHAR(32),
  CONSTRAINT pk_iota_transactions PRIMARY KEY (id),
  CONSTRAINT fk_iota_transactions FOREIGN KEY (user_id) REFERENCES t_users (id)
);
CREATE SEQUENCE sq_iota_transactions
  START 1
  INCREMENT 1;