## How to run the code

### Prerequisites
*   A Linux environment (or similar Unix-based OS).
*   Java Development Kit (JDK) installed.
*   Port **3000** must be free.


### 1. Compilation
Use the provided build script to clean and compile all sources:
```bash
./build.sh
```

### 2. Start the Server
Starts the RMI Registry and the Server application.
```bash
./run_server.sh
```
*   *Note:* The server creates `history.bin` in the working directory.

### 3. Start Clients
Open multiple terminals mode:
```bash
./run_client.sh Laura
./run_client.sh Umar
```
To run on GUI mode:
```bash
./run_gui.sh Laura
./run_gui.sh Umar
```
or, if you want to specify the port:
```bash
./run_gui.sh Laura 3000
./run_gui.sh Umar 3000
```
This will connect to the server running on `localhost` at port `3000`. Make sure the server is running in the same port before starting the clients.

### 4. Commands
*   **Client:** Type any text to send. Type `/leave` to exit gracefully.
*   **Server:** Type `/exit` or press `Ctrl+C` to stop (both trigger the persistence save).