# 

## Introduction and Goals
This document provides an architectural overview of the File Storage Service (FSS),
a solution designed to store and manage files, such as images, videos, and other media, for external applications.





### Requirements Overview
Features:
- Upload, download, and delete files via RESTful APIs.
- Metadata management for stored files.
- Access control mechanisms to manage file visibility and permissions.
- Versioning for stored files.
- Storage files on local file system or in AWS S3.





## Architecture Constraints

| Constraint             | Description                                                                           |
|------------------------|---------------------------------------------------------------------------------------|
| API Protocols          | The service must use REST APIs                                                        | 
| File Storage Mechanism | Files must be stored using an object storage solution on local files system or AWS S3 |





## Context and Scope
![](embed:systemContext_fileStorageSystem)

## Solution Strategy

| Goal     | Description                                          |
|----------|------------------------------------------------------|
| Security | OAuth2 for secure user/externalSystem authentication |





## Building Block View
Diagram from 3.1. section displaying all systems:
![](embed:systemContext_fileStorageSystem)

| Block                                       | Description                                                |
|---------------------------------------------|------------------------------------------------------------|
| File Storage System                         | Main system responsible for storage files.                 |
| Identity and Access Management (IAM) system | System responsible for authentication and user management. |
| Person                                      | Person can use REST Client to manually sent file.          |
| External System                             | Other system which store files.                            |

<br />

### Level 1 - File Storage System
The system architecture consists of two main components File Storage Service and supporting infrastructure.
![](embed:container_fileStorageSystem)

| System or components      | Description                                                                                                  |
|---------------------------|--------------------------------------------------------------------------------------------------------------|
| File Storage Service      | The service is implemented in Spring Framework                                                               |
| supporting infrastructure | The system uses MongoDB as the primary database. Additionally Apache Kafka is utilized as a messaging system |

To model the domain, the DDD (Domain-Driven Design) approach was applied, complemented by Event Storming. 
More information: [Event Storming](#system-modeling)

<br />

#### Level 2 - File Storage Service
The service breaks down in 3 components:
- container - component is responsible for containers management;
- object - component is responsible for objects management;
- storage - component is responsible for storing files;
![](embed:component_fileStorageService)

<br />

#### File Storage Service - REST API documentation
Comprehensive documentation for the REST API can be accessed via the following [link](https://documenter.getpostman.com/view/8964498/2sAYBaBA93).

<br />

#### File Storage Service - Security
The service functions as a Resource Server. It utilizes scopes to determine whether the user has sufficient permissions to perform a specific action.
The table below presents the basic roles required to obtain the corresponding scopes. For example, to obtain the scope `fss.containers:read`, the `fss.containers-read` role must be assigned.

| Base role            | Scope                | Claims | Access Granted                                                                                                        |
| -------------------- | -------------------- | ------ | --------------------------------------------------------------------------------------------------------------------- |
| fss.containers-read  | fss.containers:read  | -      | Permission to read any container.                                                                                     |
| fss.containers-write | fss.containers:write | -      | Permission to create, change or delete container.                                                                     |
| fss.objects-read     | fss.objects:read     | -      | Permission to read any object in container(PUBLIC or PRIVATE) and owns objects in PRIVATE containers.                 |
| fss.objects-write    | fss.objects:write    | -      | Permission to create, change or delete object in container(PUBLIC or PRIVATE) and owns objects in PRIVATE containers. |
| fss.objects-manage   | fss.objects:manage   | -      | Permission to read, create, change or delete any object in container.                                                 |

Managing such basic roles individually would be cumbersome; therefore, two additional roles have been created to aggregate the basic roles. Role Mapping Matrix:

| Role\Base role | fss.containers-read | fss.containers-write | fss.objects-read | fss.objects-write | fss.objects-manage |
| -------------- | ------------------- | -------------------- | ---------------- | ----------------- | ------------------ |
| fss.admin      | +                   | +                    | +                | +                 | +                  |
| fss.user       | -                   | -                    | +                | +                 | -                  |

Example decoded token for user with `fss.admin` role:
```json
{
  "exp": 1735338196,
  "iat": 1735337896,
  "auth_time": 1735336999,
  "jti": "4509bfe4-9436-4874-8a74-de511e9c0232",
  "iss": "http://localhost:8080/realms/f18",
  "aud": "account",
  "sub": "9bb82d81-f938-43ef-8aa8-c84706deb6db",
  "typ": "Bearer",
  "azp": "fss",
//....
  "scope": "openid fss.containers:write profile fss.objects:read fss.objects:write email fss.objects:manage fss.containers:read",
  "email_verified": true,
  "name": "Ala Kot",
  "preferred_username": "Ala Kot",
  "given_name": "Ala",
  "family_name": "Kot",
  "email": "ala@ma.kota"
}
```
The table below describe the permissions required to perform actions on objects in container.

| method \ container type | PUBLIC            | PROTECTED         | PRIVATE                     |
| ----------------------- | ----------------- | ----------------- | --------------------------- |
| fetch one object        |                   | fss.objects-read  | owner or fss.objects-manage |
| fetch all objects       | fss.objects-read  | fss.objects-read  | fss.objects-manage          |
| edit object             | fss.objects-write | fss.objects-write | owner or fss.objects-manage |

<br />

#### File Storage Service - Container Components
Implemented in Layered architectural patter as Active Record.

The component enables:
- creation,
- modification, and
- deletion of containers.

Each container is:
- identified by a unique name
- access types - define access of objects in container:
    - Public: All objects within the container are accessible to anyone without requiring authentication.
    - Protected: Objects are accessible only to authenticated actors.
    - Private: Objects are restricted to the object owner or actor with specific authority.
- revision Policy - define revision policy for objects in container:
    - REVISION_ON - revision enable
    - REVISION_OFF - revision disable

Example container:
```json
{
"containerId": "676f243e808bd90f188658a8",
"name": "custom_container_name",
"type": "PUBLIC",
"revisionPolicyType": "REVISION_ON"
}
```

<br />

#### File Storage Service - Object Components
Implemented in Port & Adapters architectural patter as Domain Model.

The component enables:
- creation of objects,
- modification of objects,
- modification of objects by restoring previous object's revision if revision enable,
- deletion of objects,
- deletion of object's revisions if revision enable.

Example object when revision Policy is enabled(revision 3 was deleted):
```json
{
  "objectId": "676f2666808bd90f188658a9",
  "key": "god.jpg",
  "containerId": "676f243e808bd90f188658a8",
  "revisions": [
    {
      "revisionId": 1,
      "revisionType": "CREATED",
      "size": 621697,
      "storageId": "676f2666808bd90f188658a9_1.jpg"
    },
    {
      "revisionId": 2,
      "revisionType": "UPDATED",
      "size": 621697,
      "storageId": "676f2666808bd90f188658a9_2.jpg"
    },
    {
      "revisionId": 4,
      "revisionType": "DELETED",
      "size": 0,
      "storageId": null
    },
    {
      "revisionId": 5,
      "revisionType": "RESTORED",
      "size": 621697,
      "storageId": "676f2666808bd90f188658a9_5.jpg"
    }
  ],
  "metadata": {
    "extension": "jpg",
    "ownerId": "9bb82d81-f938-43ef-8aa8-c84706deb6db"
  }
}
```

<br />

#### File Storage Service - Storage Components
Implemented in Layered architectural patter as Transaction Script.

The component is responsible for file storage in Local File System.

<br />

### Level 1 - Identity and Access Management (IAM) system
The system integrates Keycloak as the Identity and Access Management (IAM) solution, eliminating the need to develop custom authentication and authorization mechanisms.
This choice reduces development effort, enhances security, and ensures compliance with modern authentication standards like OAuth2 and OpenID Connect.

![](embed:container_iam)



## Runtime View
// TODO







## Deployment View
### DEV
Deployment diagram - dev:
![](embed:development_dev)
#### IAM
1. Run keycloak and postgresDB:

        docker-compose -p auth -f .\.docker\docker-compose-auth.yaml up -d

2. Config realm 
   1. Create realm using file `realm-export.json`
   2. Create user
   3. Regenerate `Client Secret` in `fss` Client

#### File Service
##### Run
1. Setup dev env:

        docker-compose -p f18 -f .\.docker\docker-compose.yaml up -d

2. Build everything:

       .\gradlew service:file-storage:build

3. Start application:

        .\gradlew service:file-storage:bootRun

##### Test

1. Execute tests:

        .\gradlew service:file-storage:test






## Cross-cutting Concepts

### Clean Architecture
Architecture is described in book: *Get Your Hands Dirty on Clean Architecture, by Tom Hombergs*.





## Architecture Decisions
[Link](/workspace/decisions)





## Quality Requirements





## Risks and Technical Debts





## Glossary

| Term        | Definition                |
|-------------|---------------------------|
