CREATE TABLE t_payments (
  id          DECIMAL(19, 0) NOT NULL,
  version     DECIMAL(19, 0) NOT NULL,
  created_at  TIMESTAMP      NOT NULL,
  updated_at  TIMESTAMP      NOT NULL,

  sender_id   DECIMAL(19, 0) NOT NULL,
  receiver_id DECIMAL(19, 0) NOT NULL,
  product_id  DECIMAL(19, 0) NOT NULL,
  group_id    DECIMAL(19, 0) NOT NULL,
  status      VARCHAR(32)    NOT NULL,
  amount      NUMERIC(19, 4) NOT NULL,


  CONSTRAINT pk_payments PRIMARY KEY (id)
);

CREATE SEQUENCE sq_payments
  START 1
  INCREMENT 1;