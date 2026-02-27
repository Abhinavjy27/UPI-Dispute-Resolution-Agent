# Quick Start Guide - Frontend

## Start the Frontend (TypeScript/React)

### Step 1: Install Node.js
- Ensure Node.js v18+ is installed
- Check: `node --version`

### Step 2: Install Dependencies
```bash
cd frontend
npm install
```

### Step 3: Start Development Server
```bash
npm run dev
```

**Access at**: http://localhost:5173

---

## If Frontend Won't Start

### Clean reinstall:
```powershell
cd frontend
Remove-Item -Recurse -Force node_modules, package-lock.json -ErrorAction SilentlyContinue
npm install
npm run dev
```

---

## Note
This project uses the **TypeScript/React/Vite frontend** (not the simple JSX version).
All dependencies are in `frontend/package.json`.
