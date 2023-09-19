CREATE TABLE accounts (
   account_id UUID DEFAULT gen_random_uuid(),
   email VARCHAR(255) NOT NULL,
   password VARCHAR(255) NOT NULL,
   is_email_verified BOOLEAN DEFAULT FALSE NOT NULL,
   CONSTRAINT pk_accounts PRIMARY KEY (account_id),
   CONSTRAINT uq_accounts_email UNIQUE (email)
);