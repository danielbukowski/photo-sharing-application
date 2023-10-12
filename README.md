# Photo Sharing Application
The project is an API which allows to create accounts on which you can save your images as available only to you or available to other users. 


## Status
In Progess


## Technologies used

### Back-end
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
- OpenAPI

### Front-end

- Angular 16
- Tailwind CSS



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

![database_schema](https://github.com/danielbukowski/photo-sharing-application/assets/82054911/27a50a13-d2ef-4df1-a57e-0697bb512ced)


## Running the application
Requirements to run:
- Docker
- Bash

Clone the project

```bash
  git clone danielbukowski/photo-sharing-application
```

Go to the project directory

```bash
  cd photo-sharing-application
```

Start the project

```bash
  bash start-dev.bash
```

![init-bash-script](https://github.com/danielbukowski/photo-sharing-application/assets/82054911/9ffd787e-7a6a-4eec-ad2f-36ffbfe716f0)


## Running Tests!

To run tests, run the following command

```bash
  ./mvnw clean test
```
