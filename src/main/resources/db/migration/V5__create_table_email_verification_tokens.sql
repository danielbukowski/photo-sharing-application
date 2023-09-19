CREATE TABLE email_verification_tokens (
    email_verification_token_id UUID DEFAULT gen_random_uuid(),
    account_id UUID NOT NULL,
    expiration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT pk_email_verification_tokens PRIMARY KEY (email_verification_token_id),
    CONSTRAINT fk_accounts_email_verification_tokens FOREIGN KEY (account_id) REFERENCES accounts(account_id) ON DELETE CASCADE ON UPDATE CASCADE
);