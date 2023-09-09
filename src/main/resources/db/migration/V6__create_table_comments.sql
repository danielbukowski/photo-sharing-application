CREATE TABLE comments(
    id BIGINT GENERATED ALWAYS AS IDENTITY,
    content VARCHAR(255) NOT NULL,
    image_id UUID NOT NULL,
    comment_account_id UUID NOT NULL,
    CONSTRAINT pk_comments PRIMARY KEY (id),
    CONSTRAINT fk_images FOREIGN KEY (image_id) REFERENCES images(id),
    CONSTRAINT fk_accounts FOREIGN KEY (account_id) REFERENCES accounts(id)
);
