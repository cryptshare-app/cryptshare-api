CREATE TABLE t_group_memberships (
  id         DECIMAL(19, 0) NOT NULL,
  group_id   DECIMAL(19, 0) NOT NULL,
  user_id    DECIMAL(19, 0) NOT NULL,
  role       VARCHAR(32)    NOT NULL,
  status     VARCHAR(32)    NOT NULL,
  created_at TIMESTAMP      NOT NULL,
  updated_at TIMESTAMP      NOT NULL,
  auto_pay   BOOLEAN,


  CONSTRAINT pk_group_memberships PRIMARY KEY (id),
  CONSTRAINT fk_group_memberships_group_id FOREIGN KEY (group_id) REFERENCES t_groups (id),
  CONSTRAINT fk_group_memberships_user_id FOREIGN KEY (user_id) REFERENCES t_users (id)

);

CREATE SEQUENCE sq_group_memberships
  START 1
  INCREMENT 1;