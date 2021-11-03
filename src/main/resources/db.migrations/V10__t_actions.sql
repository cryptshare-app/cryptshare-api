CREATE TABLE t_actions (
  id         DECIMAL(19, 0) NOT NULL,
  version    DECIMAL(19, 0) NOT NULL,
  created_at TIMESTAMP      NOT NULL,
  updated_at TIMESTAMP      NOT NULL,
  category   VARCHAR(32),
  type       VARCHAR(32),
  message    VARCHAR(255),
  link_to    VARCHAR(255),
  user_id    DECIMAL(19, 0) NOT NULL,

  CONSTRAINT pk_actions PRIMARY KEY (id)
);

CREATE SEQUENCE sq_actions
  START 1
  INCREMENT 1;