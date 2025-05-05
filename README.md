# job-hunter-api

**job-hunter-api** is a simple backend built as my first Spring Boot project. It allows users to browse companies and jobs, apply to positions, and manage applications. The project also implements basic user roles and permissions.

This project is a personal learning experience to explore Java backend development with Spring Boot, Spring Security, and related technologies. While it works, it still has many limitations and areas for improvement.

## Features

**Authentication**

  * User registration and login (JWT-based authentication)
**Company & Job Listings**
  * View list of companies
  * View available job postings
**Job Applications**
  * Apply to jobs with an uploaded resume (stored locally)
  * View list of jobs the user has applied to
**Role-Based Access Control (RBAC)**

  * Three roles:

    * `ADMIN`: full access to manage users and jobs
    * `HR`: can post and manage jobs, resume
    * Regular users (no role): can view/apply to jobs
**File Upload**

  * Upload resume when applying to a job (stored on local server)

**Email Notifications**

  * Send job suggestion emails to users

## Technologies Used

* Java 17
* Spring Boot
* Spring Security (JWT)
* JPA + MySQL
* Mail (JavaMailSender)
* Docker (for basic deployment)

## Known Limitations

Since this is my first Spring Boot project, there are still many things to improve and there are still bugs left.

## License

This project is for learning purposes and not intended for production use
