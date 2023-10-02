CREATE TABLE password_reset_tokens (
    password_reset_token_id UUID,
    account_id UUID NOT NULL,
    expiration_date TIMESTAMP NOT NULL,
    is_already_used BOOLEAN DEFAULT FALSE,
    CONSTRAINT pk_password_reset_tokens PRIMARY KEY (password_reset_token_id),
    CONSTRAINT fk_accounts_password_reset_tokens FOREIGN KEY (account_id) REFERENCES accounts(account_id) ON DELETE CASCADE ON UPDATE CASCADE
);