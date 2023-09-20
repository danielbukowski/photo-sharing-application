CREATE TABLE images (
   image_id UUID DEFAULT gen_random_uuid(),
   account_id UUID NOT NULL,
   title VARCHAR(255) NOT NULL,
   content_type VARCHAR(10) NOT NULL,
   creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
   is_private BOOLEAN DEFAULT FALSE NOT NULL,
   CONSTRAINT pk_images PRIMARY KEY (image_id),
   CONSTRAINT fk_accounts_images FOREIGN KEY (account_id) REFERENCES accounts(account_id) ON DELETE CASCADE ON UPDATE CASCADE
);