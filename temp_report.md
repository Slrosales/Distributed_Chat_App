

# Distributed Chat Application - Documentation
> by Laura Gómez and Umar Sivia

## System Overview
This project implements a **Coordination-based Distributed System** using **Java RMI**. It follows the **Client-Server** architectural pattern but incorporates **Group Communication** principles. The system allows multiple users to exchange messages in real-time, maintains a persistent history, and manages dynamic group membership (Join/Leave/Fail).

---

## Shared Resources
### Message (`Message.java`)
Represents the data unit exchanged between entities.
*   **Attributes:** Sender, Content, Timestamp (automatically generated in format  [HH:mm:ss]).
*   **Role:** Implements `Serializable`. This allows the object to be converted into bytes to travel across the network between different Java Virtual Machines (JVMs).

---

## Server-Side Architecture
The server acts as the **Coordinator** and **Single Source of Truth** for the system state.

### Server Interface (`ChatServer_itf.java`)
Defines the remote contract. It facilitates **Indirect Communication** by decoupling the sender from the receivers.
*   **`join()`**: Registers a client callback. Handles **Group Management**.
*   **`sendMessage()`**: Receives a message and triggers a **Broadcast** to all active members.
*   **`getHistory()`**: Retrieves past messages, enabling new users to see messages sent before they joined.

### Server Implementation (`ChatServerImpl.java`)
It manages the state of Connected Clients and Message History.

**Key Implementation:**
1.  All methods (`join`, `leave`, `sendMessage`) are `synchronized`. This ensures **FIFO (First-In-First-Out)** ordering and prevents **Race Conditions** when multiple clients act simultaneously.
2.  In `join()`, if a username exists, the server attempts to "Ping" the old reference. If the ping fails, the server treats it as a crash, removes the zombie, and allows the new connection.
3.  Iterates through all clients to deliver messages. Uses a `try-catch` block inside the loop to detect and remove disconnected clients on the fly without interrupting the service for others.
4.  **Persistence:**
    *   Uses `ObjectOutputStream` to save the history to a `.bin` file on every message.
    *   Ensures data survives server restarts.

### Server Main (`ChatServerMain.java`)
The entry point that sets up the RMI Registry.
For shutdown Hook, the registers a thread with `Runtime.getRuntime().addShutdownHook`. This ensures that even if the server is killed via `SIGINT` (Ctrl+C), it performs a final save of the history and unbinds from the registry cleanly.

---

## Client-Side Architecture
The client acts as both a sender of requests and a receiver of events (**Callback**).

### Client Interface (`ChatClient_itf.java`)
This is a **Callback Interface**. It allows the server to invoke methods on the client, effectively turning the client into a server for the purpose of receiving messages. This enables the **Push Model** (Publish-Subscribe) behavior.

### Client Implementation (`ChatClientImpl.java`)
Implements the logic for processing incoming messages.
*   **Filtering:** Ignores internal system "Pings" ("Checking if you are still alive...") so they don't clutter the user's screen.
*   **Shutdown Handling:** Listens for the specific "Server is shutting down" message to exit gracefully.

### Client Main (`ChatClientMain.java`)
Manages the user lifecycle and UI (Terminal).
*   **Flow:**
    1.  Connects to Registry -> Looks up `ChatService`.
    2.  **Export:** Exports itself (`UnicastRemoteObject.exportObject`) to create the stub required for callbacks.
    3.  Joins the chat and immediately fetches History.
    4.  **Command Loop:** Listens for user input. Intercepts `/leave` to trigger a graceful exit (calling `server.leave()`) instead of a crash.
---

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
Open multiple terminals to simulate a distributed environment:
```bash
./run_client.sh Laura
./run_client.sh Umar
```
To run on GUI
```bash
java -cp classes src.ChatGUI Laura
java -cp classes src.ChatGUI Umar
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