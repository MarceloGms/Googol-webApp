# How to Run

- Make sure you have `make` installed.
- Make sure you have `JDK 17` installed.

### Run Gateway

- Open a terminal.
- Navigate to the primary directory (googol).
- Run the command: `make gw`

### Run Barrels

- Open a terminal (or multiple terminals).
- Navigate to the primary directory (googol).
- Run the command: `make brl`

### Run Downloader

- Open a terminal.
- Navigate to the primary directory (googol).
- Run the command: `make dl`

### Run Clients (Meta 1)

- Open a terminal (or multiple terminals for multiple clients).
- Navigate to the primary directory (googol).
- Run the command for each client: `make cli`

### Run Web App (Meta 2)

- Open a terminal.
- Navigate to the primary directory (googol).
- Run the command: `make web`
- Open your browser on `localhost:8080`

### Final considerations

- Ensure all of the server's components are running before starting to perform client operations.
- Modify the `assets/config.properties` file to change the server ip address, number of barrels or number of downloaders.
