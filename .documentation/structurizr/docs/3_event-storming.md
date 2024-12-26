### File Storage System modeling
#### introduction
To model the domain, the DDD (Domain-Driven Design) approach was applied, complemented by Event Storming. Event Storming is an excellent tool for supporting DDD processes, particularly during the exploratory and design phases. It facilitates the identification of domain events, the modeling of business processes, and the development of a shared language among all stakeholders.

Event Storming is created using draw.io, and the corresponding file is located in the designated folder(`./f17.drawio`).
Below, an exported image of the documentation is provided for reference:
![](images/f17-event_storming.drawio.svg)

#### Domain Description
The File Storage System is designed to manage and store files along with their associated metadata. 
It consists of two primary entities: Containers and the Objects they contain.

A Container is a logical grouping of Objects and serves as the primary organizational unit in the system. 
Each container is identified by a unique name and is assigned one of the following access types:
- Public: All files within the container are accessible to anyone without requiring authentication.
- Protected: Files are accessible only to authenticated actors. 
- Private: Files are restricted to the container owner or users with specific roles as defined in the IAM (Identity and Access Management) system.

An Object represents a single file stored in the system. It consists of the following attributes:
- ID: Derived from the file name and version, ensuring unique identification for each version of a file. 
- Metadata: Includes additional information about the file, such as size, creation date, and custom attributes.

Objects are versioned, meaning that updates to a file result in the creation of a new version rather than overwriting the existing file. This ensures a complete history of changes and allows for the retrieval of previous versions.

#### Strategic Design (ES: Pig Picture & Process Modeling)
During modeling, the following **definitions** were established as universal concepts, applicable across all bounded contexts within the domain:
- player - represent user in game;

#### Legend:

#### ES. Pig Picture, examples:

#### ES. Process Modeling, examples:

#### **Tactical Design (ES: Design Modeling)**
After documenting and describing the processes, the following **bounded contexts** were identified:

##### 1. player context
Implemented in Layered architectural patter as Active Record.
Objects:
- Player Entity
