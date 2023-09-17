CREATE TABLE image_likes (
    image_id UUID,
    account_id UUID,
    CONSTRAINT fk_images FOREIGN KEY(image_id) REFERENCES images(id),
    CONSTRAINT fk_accounts FOREIGN KEY(account_id) REFERENCES accounts(id)
);