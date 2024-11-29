# Shard-Based Database Example

This project demonstrates **logical sharding** with multiple **physical shards** using H2 in-memory databases. The goal is to distribute and manage user data across shards based on **odd-even ID logic**, showcasing the benefits of sharding for scalability and performance.

<img width="650" alt="Screenshot 2024-11-30 at 12 49 38 AM" src="https://github.com/user-attachments/assets/3a85b0ae-0627-4fc7-8878-ce61dc799265">
---

## Features

1. **Multiple Shards**:
   - Two physical shards (`shard1` and `shard2`) are implemented using H2 in-memory databases.
   
2. **Logical Sharding**:
   - User data is distributed based on odd or even user IDs:
     - **Odd IDs** go to `shard1`.
     - **Even IDs** go to `shard2`.

3. **Efficient Data Distribution**:
   - Ensures balanced data load between shards.
   - Allows scalability by adding more shards as needed.

4. **Data Querying Across Shards**:
   - Each shard is queried independently to retrieve user data.

---

## Prerequisites

- **Java 8+**
- **Maven**

---

## Getting Started

### 1. Clone the Repository
```bash
git clone <repository-url>
cd <repository-folder>
```

### 2. Build the Project
Use Maven to build the project:
```bash
mvn clean compile
```

### 3. Run the Project
Run the main program to see sharding in action:
```bash
mvn exec:java -Dexec.mainClass="org.example.Shard"
```

---

## How It Works

### Sharding Logic
- User IDs are distributed as follows:
  - **Odd IDs**: Inserted into **`shard1`**.
  - **Even IDs**: Inserted into **`shard2`**.

### Steps in the Program
1. **Setup Shards**:
   - Creates a `users` table in each shard.
2. **Insert Users**:
   - Inserts 20 users with IDs and Ages into appropriate shards.
3. **Query Data**:
   - Queries and displays data from each shard independently.

---

## Example Output

```text
Setting up shards...
Shard 1 setup complete.
Shard 2 setup complete.

Inserting data into shards...
Inserted User 1 into Shard 1
Inserted User 2 into Shard 2
Inserted User 3 into Shard 1
...

Querying data from all shards...
Shard 1 data:
User ID: 1, Age: 21
User ID: 3, Age: 23
...

Shard 2 data:
User ID: 2, Age: 22
User ID: 4, Age: 24
...
```

---

## Code Highlights

### 1. Shard Setup
Creates tables in each physical shard:
```java
stmt.execute("CREATE TABLE IF NOT EXISTS users (ID INT PRIMARY KEY, Age INT)");
```

### 2. Data Distribution Logic
Distributes user data based on odd-even ID logic:
```java
String shardUrl = (userId % 2 == 0) ? shardUrls[1] : shardUrls[0];
stmt.execute("INSERT INTO users (ID, Age) VALUES (" + userId + ", " + age + ")");
```

### 3. Querying Shards
Fetches data from each shard independently:
```java
ResultSet rs = stmt.executeQuery("SELECT * FROM users");
while (rs.next()) {
    System.out.println("User ID: " + rs.getInt("ID") + ", Age: " + rs.getInt("Age"));
}
```

---

## Benefits of Sharding

1. **Scalability**: 
   - Distributes load across multiple shards for better performance.
2. **Efficiency**: 
   - Queries smaller datasets, reducing overhead.
3. **Extensibility**: 
   - Logic can be extended for additional shards or dynamic distribution.

---

## Future Enhancements

- **Dynamic Sharding**: Add logic for dynamic shard mapping.
- **Shard Router**: Implement a centralized router for shard identification.
- **Resharding**: Add functionality to redistribute data when shards grow unevenly.
