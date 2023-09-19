CREATE TABLE roles (
    role_id UUID DEFAULT gen_random_uuid(),
    name varchar(255) UNIQUE NOT NULL,
    CONSTRAINT pk_roles PRIMARY KEY (role_id)
);

CREATE TABLE accounts_roles (
    account_id UUID NOT NULL,
    role_id UUID NOT NULL,
    CONSTRAINT fk_accounts_accounts_roles FOREIGN KEY (account_id) REFERENCES accounts(account_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_roles_accounts_roles FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE CASCADE ON UPDATE CASCADE
);

INSERT INTO roles(name) VALUES ('USER'), ('ADMIN');