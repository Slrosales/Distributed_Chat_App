# Distributed Chat Application (Java RMI)
**Authors:** UMER Muhammad and GOMEZ ROSALES Laura Sofía

## 1. Introduction
This project implements a distributed chat application using Java RMI (Remote Method Invocation). With the main objective of design and implementing a distributed client-server application with proper architectural structure, synchronization, and remote communication.

The application enables multiple users to communicate in real time through a centralized server. The system supports user join and leave notifications, message broadcasting, persistent chat history, and controlled server shutdown.


## 2. Global Functionality of the Application

*   Users can connect to a central server using a username
*   When a user joins, a system notification is generated and broadcast
*   Clients with duplicate usernames are not allowed
*   Users can send messages that are broadcast to all connected clients
*   Messages include timestamps
*   The server maintains a persistent history of chat messages
*   When a user leaves, a system message is recorded and broadcast
* The server handles unexpected disconnections
* On server shutdown, connected clients are notified and exit gracefully
* Empty messages are ignored and not broadcast.


## 3. Software Architecture
The application is designed as a **Coordination-centered system**. It hides the complexities of distribution while ensuring a consistent global state across all participants.

### 3.1 Architectural Style
The system follows a **Client-Server** architecture where the Server acts as a **Central Coordinator**.
*  There is no direct communication between clients. By using the Server as an intermediary, we achieve spatial decoupling; Client A does not need to know the identity or location of Client B.
*   Through the RMI Registry, the system hides the physical location of the server object. Clients interact with a symbolic name: `"ChatService"`.

### 3.2 Component Responsibilities
| Component | Responsibilities |
| :--- | :--- |
| **Server** | Managing users, receiving/broadcasting messages, generating timestamps, maintaining history. |
| **Client** | Connecting to service, sending messages, receiving callbacks, displaying chronological UI. |

### 3.3 The "Safety-First" Handshake
To ensure consistency and reliability, we implement a specific 3-step handshake when a new client connects:

1.  **Identity Verification & Liveness Check:** The server checks if the username is taken. If found, it "pings" the existing stub. If the old client is unresponsive (**Zombie**), it cleans the stale reference to allow the new connection.
2.  **State Synchronization (History):** The new client invokes `getHistory()` to catch up with all past coordination events stored in the server's memory and disk (**Temporal Decoupling**).
3.  **Group Integration:** The server adds the client to the active group and broadcasts the "Join" event. This ensures that the global sequence of events is never duplicated and is perfectly ordered.

## 4. Key Architectural Decisions
*   By using the server as a **Sequencer**, we achieve Total Ordering of messages. Synchronized methods force a **FIFO (First-In-First-Out)** queue, ensuring all clients see the same sequence of events.
*  Instead of inefficient "polling," the server holds a remote reference to each client and "pushes" notifications. This is an implementation of the **Publish-Subscribe** model.
*  Every message is serialized to `history.bin`. This ensures the chat state outlives the server process.

## 5. Setup and Installation

### 5.1 Clone the Repository
```bash
git clone https://github.com/Slrosales/p_chat.git
cd p_chat
```

### 5.2 Prerequisites
*   Linux environment (Ubuntu recommended)
*   Java Development Kit (JDK 17+)
*   Port **3000** must be available
*   *Note: If you switch Java versions, delete `history.bin` to avoid `InvalidClassException`*

## 6. Execution

### 6.1 Compilation
Use the provided build script to clean and compile the project:
```bash
chmod +x build.sh
./build.sh
```

### 6.2 Start the Server
Run the server script to initialize the RMI Registry and the Chat Service:
```bash
./run_server.sh
```

### 6.3 Start the Clients
You can run the client in two different modes:

**Terminal Mode:**
```bash
./run_client.sh <username>
```

**Graphical Mode (GUI):**
```bash
./run_gui.sh <username> [port]
# Or manually:
java -cp classes src.ChatGUI <username> localhost 3000
```

## 7. Commands
*   **Client:** Type any text to send. Type `/leave` to exit gracefully.
*   **Server:** Type `/exit` or press `Ctrl+C` to stop (both trigger the persistence save).

## 8. Conclusion
The application implements a distributed chat system using a centralized client-server architecture. The server coordinates communication, maintains consistency, and ensures reliable message broadcasting. The design separates client and server responsibilities clearly while supporting multiple concurrent users.