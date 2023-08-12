CREATE TABLE roles(
    id UUID DEFAULT gen_random_uuid(),
    name varchar(255) UNIQUE NOT NULL,
    CONSTRAINT pk_roles PRIMARY KEY (id)
);

CREATE TABLE accounts_roles(
    account_id UUID,
    role_id UUID,
    CONSTRAINT fk_accounts FOREIGN KEY(account_id) REFERENCES accounts(id),
    CONSTRAINT fk_roles FOREIGN KEY(role_id) REFERENCES roles(id)
);

INSERT INTO roles(name) VALUES ('USER'), ('ADMIN');