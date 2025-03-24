# BTL1_MAP - Android Application with Backend Integration

This project is an Android application that demonstrates user authentication with a Node.js backend server running in Docker.

## Project Structure

```
BTL1_MAP/
├── app/                    # Android application
│   └── src/main/
│       ├── java/          # Java source files
│       └── res/           # Android resources
├── backend/               # Node.js backend server
│   ├── server.js         # Main server file
│   ├── package.json      # Node.js dependencies
│   └── Dockerfile        # Docker configuration
└── README.md             # This file
```

## Prerequisites

- Android Studio
- Docker Desktop
- Node.js (for local development)
- Postman (for API testing)

## Setup Instructions

### 1. Backend Setup

1. Navigate to the backend directory:
   ```bash
   cd backend
   ```

2. Build the Docker image:
   ```bash
   docker build -t btl1-map-backend .
   ```

3. Run the Docker container:
   ```bash
   docker run -p 3000:3000 btl1-map-backend
   ```

4. Test the backend API using Postman:
   - Create a new POST request to `http://localhost:3000/api/login`
   - Set the request body to JSON:
     ```json
     {
         "email": "test@example.com",
         "pid": "123456"
     }
     ```

### 2. Android App Setup

1. Open the project in Android Studio
2. Run the app on an Android emulator or physical device



## Testing

### Test Credentials
- Email: test@example.com
- PID: 123456

