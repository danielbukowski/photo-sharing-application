
CREATE TABLE accounts (
   id UUID,
   email VARCHAR(255) UNIQUE NOT NULL,
   password VARCHAR(255) NOT NULL,
   CONSTRAINT pk_accounts PRIMARY KEY (id)
);