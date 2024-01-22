CREATE TABLE comments (
    comment_id UUID DEFAULT gen_random_uuid(),
    content VARCHAR(255) NOT NULL,
    image_id UUID NOT NULL,
    account_id UUID NOT NULL,
    CONSTRAINT pk_comments PRIMARY KEY (comment_id),
    CONSTRAINT fk_images_comments FOREIGN KEY (image_id) REFERENCES images(image_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_accounts_comments FOREIGN KEY (account_id) REFERENCES accounts(account_id) ON DELETE CASCADE ON UPDATE CASCADE
);