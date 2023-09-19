CREATE TABLE images_likes (
    image_id UUID NOT NULL,
    account_id UUID NOT NULL,
    CONSTRAINT fk_images_images_likes FOREIGN KEY(image_id) REFERENCES images(image_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_accounts_images_likes FOREIGN KEY(account_id) REFERENCES accounts(account_id) ON DELETE CASCADE ON UPDATE CASCADE
);