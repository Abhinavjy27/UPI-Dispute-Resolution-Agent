# ============================================
# ML Service Dockerfile - Python Flask
# ============================================

FROM python:3.11-slim

WORKDIR /app

# Install system dependencies
RUN apt-get update && apt-get install -y \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Copy requirements
COPY requirements.txt .

# Install Python dependencies
RUN pip install --no-cache-dir -r requirements.txt

# Copy application
COPY . .

# Expose port
EXPOSE 5000

# Health check
HEALTHCHECK --interval=15s --timeout=5s --retries=3 --start-period=30s \
    CMD curl -f http://localhost:5000/health || exit 1

# Set environment
ENV FLASK_APP=app.py
ENV PYTHONUNBUFFERED=1

# Run Flask app
CMD ["flask", "run", "--host=0.0.0.0", "--port=5000"]

# ============ CONFIGURATION ============
# Build:
#   docker build -t disputes-ml:latest .
#
# Run:
#   docker run -p 5000:5000 disputes-ml:latest
#
# With environment:
#   docker run -p 5000:5000 \
#     -e FLASK_ENV=development \
#     disputes-ml:latest
