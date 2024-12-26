workspace {
    name "File storage system"
    description "description"

    !docs docs
    !adrs docs/adrs
    !identifiers hierarchical

    model {
        person = person "Person"
        externalSystem =  person "External System"
        fileStorageSystem = softwareSystem "File storage system" {
            fileStorageService = container "File storage service" {
                description "description"
                technology "Spring"
            }
            database = container "MongoDB"
            kafka = container "Kafka"

            fileStorageService -> database "Uses"
            fileStorageService -> kafka "Uses"
        }

        person -> fileStorageSystem.fileStorageService "Uses"
        externalSystem -> fileStorageSystem.fileStorageService "Uses"

        dev = deploymentEnvironment "Development" {
            deploymentNode "Developer Laptop" {
                containerInstance fileStorageSystem.fileStorageService
                deploymentNode "Docker" {
                    containerInstance fileStorageSystem.database
                    containerInstance fileStorageSystem.kafka
                }
            }
        }
    }

    views {
        theme default
        systemContext fileStorageSystem "systemContext_fileStorageSystem" {
            include *
        }
        container fileStorageSystem "container_fileStorageSystem" {
            include *
        }
        deployment * dev "development_dev"{
            include *
        }
    }
}