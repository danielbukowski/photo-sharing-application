INSERT INTO Accounts(id, email, password)
VALUES ('4e280c33-518e-444f-a541-0cc4b14b5b05', 'iLoveDogs@gmail.com', 'D00ogs!!!');

INSERT INTO Images(id, content_type, title, creation_date, account_id)
VALUES ('bf008884-3492-4ead-94e9-8e674ea8cfca', 'image/jpeg', 'dog1', NOW(), '4e280c33-518e-444f-a541-0cc4b14b5b05');

INSERT INTO Images(id, content_type, title, creation_date, account_id)
VALUES ('d238dbc4-ac33-4675-a044-c1761a92f1d4', 'image/png', 'dog2', NOW(), '4e280c33-518e-444f-a541-0cc4b14b5b05');

INSERT INTO Accounts(id, email, password)
VALUES ('6d1afd8a-f8cf-4dd9-b105-fd4b9f81a8eb','iLoveCats@gmail.com"', 'ca44tSs!!');

INSERT INTO Images(id, content_type, title, creation_date, account_id)
VALUES ('7e5cb8bf-a089-4d47-9dc0-24c3b628533f', 'image/jpeg', 'cat1', NOW(), '6d1afd8a-f8cf-4dd9-b105-fd4b9f81a8eb');

INSERT INTO Images(id, content_type, title, creation_date, account_id)
VALUES ('8d47029a-e269-40c8-bf32-91fcfe4e1176', 'image/png', 'cat2', NOW(), '6d1afd8a-f8cf-4dd9-b105-fd4b9f81a8eb');
