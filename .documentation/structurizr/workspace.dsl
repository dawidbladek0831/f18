workspace {
    name "File storage system"
    description "description"

    !docs docs
    !adrs docs/adrs
    !identifiers hierarchical

    model {
        iam = softwareSystem "Identity and Access Management (IAM)" {
                keycloak = container "Keycloak"
                keycloakDatabase = container "Keycloak Database"
        }
        iam.keycloak -> iam.keycloakDatabase "Uses"


        fileStorageSystem = softwareSystem "File storage system" {
            fileStorageService = container "File storage service" {
                description "description"
                technology "Spring"
                containerComponent = component "Container"
                objectComponent = component "Object"
                storageComponent = component "Storage"
            }
            databaseContainer = container "MongoDB"
            kafkaContainer = container "Kafka"
            localFileSystemContainer = container "Local File System"
        }
        fileStorageSystem.fileStorageService.containerComponent -> fileStorageSystem.fileStorageService.storageComponent "Uses"
        fileStorageSystem.fileStorageService.containerComponent -> fileStorageSystem.databaseContainer "Uses"
        fileStorageSystem.fileStorageService.containerComponent -> fileStorageSystem.kafkaContainer "Uses"
        fileStorageSystem.fileStorageService.objectComponent -> fileStorageSystem.fileStorageService.storageComponent "Uses"
        fileStorageSystem.fileStorageService.objectComponent -> fileStorageSystem.databaseContainer "Uses"
        fileStorageSystem.fileStorageService.objectComponent -> fileStorageSystem.kafkaContainer "Uses"
        fileStorageSystem.fileStorageService.storageComponent -> fileStorageSystem.localFileSystemContainer "Uses"
        fileStorageSystem.fileStorageService -> iam.keycloak "Take keys"


        person = person "Person"
        externalSystem =  person "External System"
        actor = person "Actor"
        person -> actor "Extends"
        externalSystem -> actor "Extends"
        actor -> iam.keycloak "Authenticate"

        actor -> fileStorageSystem.fileStorageService.containerComponent "Uses"
        actor -> fileStorageSystem.fileStorageService.objectComponent "Uses"

        dev = deploymentEnvironment "Development" {
            deploymentNode "Developer Laptop" {
                containerInstance fileStorageSystem.fileStorageService
                deploymentNode "Docker" {
                    containerInstance fileStorageSystem.databaseContainer
                    containerInstance fileStorageSystem.kafkaContainer
                }
            }
        }
    }

    views {
        theme default
        systemContext fileStorageSystem "systemContext_fileStorageSystem" {
            include *
            include person
            include externalSystem
        }
        container fileStorageSystem "container_fileStorageSystem" {
            include *
        }
        component fileStorageSystem.fileStorageService "component_fileStorageService" {
            include *
            include "->fileStorageSystem.fileStorageService->"
        }
        container iam "container_iam" {
            include "element.parent==iam"
            include actor
        }
        deployment * dev "development_dev"{
            include *
        }
    }
}