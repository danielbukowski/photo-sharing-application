CREATE TABLE images (
   id UUID,
   account_id UUID,
   path VARCHAR(255) UNIQUE,
   title VARCHAR(255) NOT NULL ,
   file_extension VARCHAR(4) NOT NULL,
   created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
   CONSTRAINT pk_images PRIMARY KEY (id),
   CONSTRAINT fk_images_on_account FOREIGN KEY (account_id) REFERENCES accounts(id)
);

