CREATE TABLE email_verification_tokens (
    id UUID DEFAULT gen_random_uuid(),
    account_id UUID NOT NULL,
    expiration_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_email_verification_tokens PRIMARY KEY (id),
    CONSTRAINT fk_accounts FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
);