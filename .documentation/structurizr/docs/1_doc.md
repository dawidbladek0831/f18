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

| Block               | Description                                       |
|---------------------|---------------------------------------------------|
| File Storage System | Main system responsible for storage files.        |
| Person              | Person can use REST Client to manually sent file. |
| External System     | Other system which store files.                   |


### Level 1 - File Storage System
The system architecture consists of two main components File Storage Service and supporting infrastructure.
![](embed:container_fileStorageSystem)

| System or components      | Description                                                                                                  |
|---------------------------|--------------------------------------------------------------------------------------------------------------|
| File Storage Service      | The service is implemented in Spring Framework                                                               |
| supporting infrastructure | The system uses MongoDB as the primary database. Additionally Apache Kafka is utilized as a messaging system |

To model the domain, the DDD (Domain-Driven Design) approach was applied, complemented by Event Storming. 
More information: [Event Storming](#game-system-modeling)

#### Level 2 - Backend
// TODO

## Runtime View
// TODO

## Deployment View
Deployment diagram - dev:
![](embed:development_dev)

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
