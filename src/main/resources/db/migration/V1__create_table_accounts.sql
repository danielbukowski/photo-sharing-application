CREATE TABLE accounts (
   id UUID DEFAULT gen_random_uuid(),
   email VARCHAR(255) UNIQUE NOT NULL,
   password VARCHAR(255) NOT NULL,
   CONSTRAINT pk_accounts PRIMARY KEY (id)
);