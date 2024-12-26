# Documentation
This project uses [Structurizr](https://structurizr.com) for system documentation. 
The documentation is:
- served locally using Docker Compose, or
- using offline HTML page in this folder(`offline-documentation.html`).

## Getting Started

### Running the Documentation Viewer
To start the documentation viewer locally, use the following command:

      docker-compose -p f18 -f .\.documentation\docker-compose-documentation.yaml up -d

This will start a local server hosting the documentation. Once the server is up, you can access the documentation at:
   
      http://localhost:9050

### Editing and Exporting Documentation
After editing u need to export Documentation, by using the Export to offline HTML page feature in Structurizr(button **"Export to offline HTML page"**). Replace the exported HTML file in this folder.

### Stopping the Documentation Viewer
To stop the local documentation server, use:

      docker-compose -p f18 -f .\.documentation\docker-compose-documentation.yaml down

## Notes
If you encounter issues, verify that no other service is running on port `9050`