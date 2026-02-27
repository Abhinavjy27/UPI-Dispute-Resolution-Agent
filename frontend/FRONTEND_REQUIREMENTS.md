# Frontend Requirements & Setup

## System Requirements

### Prerequisites
- **Node.js**: v18.x or higher (recommended: v20.x)
- **npm**: v9.x or higher (comes with Node.js)

To check your versions:
```bash
node --version
npm --version
```

### Install Node.js
If you don't have Node.js installed:
- Download from: https://nodejs.org/
- Recommended: Download LTS (Long Term Support) version

## Project Setup

### 1. Navigate to Frontend Directory
```bash
cd frontend
```

### 2. Install Dependencies
```bash
npm install
```

This will install all dependencies listed in `package.json`:
- React 18.3.1
- TypeScript 5.9.3
- Vite 8.0 (build tool)
- Tailwind CSS 3.4.1
- Axios (HTTP client)
- React Router DOM
- Framer Motion (animations)
- Lucide React (icons)
- Radix UI components

### 3. Start Development Server
```bash
npm run dev
```

The frontend will start on: **http://localhost:5173**

## Available Scripts

- `npm run dev` - Start development server with hot reload
- `npm run build` - Build production bundle
- `npm run preview` - Preview production build locally
- `npm run lint` - Run ESLint to check code quality

## Environment Variables

Create a `.env` file in the `frontend` directory if needed:
```env
VITE_API_URL=http://localhost:8080/api
```

## Troubleshooting

### Port Already in Use
If port 5173 is busy:
```bash
# Vite will automatically try the next available port
# Or kill the process using the port on Windows:
netstat -ano | findstr :5173
taskkill /PID <PID> /F
```

### Clear Cache and Reinstall
```bash
# Remove node_modules and package-lock.json
Remove-Item -Recurse -Force node_modules, package-lock.json -ErrorAction SilentlyContinue

# Reinstall dependencies
npm install
```

### Build Errors
If you see TypeScript errors:
```bash
# Check TypeScript configuration
npm run build
```

## Project Structure
```
frontend/
├── src/
│   ├── components/      # React components
│   ├── contexts/        # React contexts (Auth, etc.)
│   ├── lib/            # Utilities and API client
│   ├── assets/         # Images, fonts, etc.
│   ├── App.tsx         # Main app component
│   └── main.tsx        # Entry point
├── public/             # Static assets
├── index.html          # HTML template
├── vite.config.ts      # Vite configuration
├── tailwind.config.js  # Tailwind CSS config
└── tsconfig.json       # TypeScript config
```

## Tech Stack
- **Framework**: React 18 with TypeScript
- **Build Tool**: Vite 8
- **Styling**: Tailwind CSS
- **Routing**: React Router DOM
- **HTTP Client**: Axios
- **Animations**: Framer Motion
- **Icons**: Lucide React
- **UI Components**: Radix UI
