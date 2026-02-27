# ============================================
# Frontend Dockerfile - React Vite App
# ============================================

FROM node:18-alpine

WORKDIR /app

# Install dependencies
COPY package*.json ./
RUN npm install

# Copy source
COPY . .

# Set API URL from environment
ARG VITE_API_URL=http://localhost:8000
ENV VITE_API_URL=${VITE_API_URL}

# Build
RUN npm run build

# Serve with node server
EXPOSE 5173

# Run dev server
CMD ["npm", "run", "dev", "--", "--host"]

# ============ CONFIGURATION ============
# Build:
#   docker build -t disputes-frontend:latest .
#
# Build with custom API URL:
#   docker build --build-arg VITE_API_URL=http://backend:8000 \
#     -t disputes-frontend:latest .
#
# Run:
#   docker run -p 5173:5173 disputes-frontend:latest
