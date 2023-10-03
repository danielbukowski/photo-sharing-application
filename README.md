# Photo Sharing Application
The project is an API which allows to create accounts on which you can save your images as available only to you or available to other users. 


## Status
In Progess


## Technologies used

- Java 17
- Maven
- Spring Boot
- Spring Data JPA
- Spring Security
- Spring Validation
- Spring Cache
- Spring Mail
- PostgreSQL
- AWS SDK
- Redis
- Docker
- SeaweedFS
- GreenMail
- Flyway
- Mockito
- JUnit
- Testcontainers


## Implemented Features

- Registering an account
- Email verification
- Caching images in Redis
- Saving images in S3
- Image validation
- CRUD operations on the account entity
- CRD operations on the image entity
- Changing password without authentication
- Comments to the image entity
- Likes to the image entity
- Roles and permissions
- Sessions in Redis
- Encryption with AES 
- Image compression and decompression
- Sending messages via email
- Simple logging


## Screenshots

[database_schema](https://github.com/danielbukowski/photo-sharing-application/assets/82054911/a74ee6c0-481d-47f0-b92d-74e1f2dec55d)


## Running Tests!


To run tests, run the following command

```bash
  ./mvnw clean test
```
