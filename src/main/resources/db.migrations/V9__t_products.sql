CREATE TABLE t_products (
  id                  DECIMAL(19, 0) NOT NULL,
  version             DECIMAL(19, 0) NOT NULL,
  created_at          TIMESTAMP      NOT NULL,
  updated_at          TIMESTAMP      NOT NULL,

  image_url           VARCHAR(255),
  price               FLOAT          NOT NULL DEFAULT 0,
  group_id            DECIMAL(19, 0) NOT NULL,
  creator_id          DECIMAL(19, 0) NOT NULL,
  product_name        VARCHAR(32)    NOT NULL,
  product_description VARCHAR(255),

  CONSTRAINT pk_products PRIMARY KEY (id),
  CONSTRAINT u_products UNIQUE (group_id, product_name)
);

CREATE SEQUENCE sq_products
  START 1
  INCREMENT 1;