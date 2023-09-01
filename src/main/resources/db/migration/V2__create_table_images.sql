CREATE TABLE images (
   id UUID DEFAULT gen_random_uuid(),
   account_id UUID NOT NULL,
   title VARCHAR(255) NOT NULL,
   content_type VARCHAR(10) NOT NULL,
   creation_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
   CONSTRAINT pk_images PRIMARY KEY (id),
   CONSTRAINT fk_images_accounts FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE
);