CREATE TABLE t_groups (
  id          DECIMAL(19, 0) NOT NULL,
  owner_id    DECIMAL(19, 0) NOT NULL,
  title       VARCHAR(32)    NOT NULL,
  description VARCHAR(512)   NOT NULL,
  image_url   VARCHAR(512),
  created_at  TIMESTAMP      NOT NULL,
  updated_at  TIMESTAMP      NOT NULL,


  CONSTRAINT pk_groups PRIMARY KEY (id),
  CONSTRAINT u_groups UNIQUE (title),
  CONSTRAINT fk_groups_owner_id FOREIGN KEY (owner_id) REFERENCES t_users (id)
);

CREATE SEQUENCE sq_groups
  START 1
  INCREMENT 1;