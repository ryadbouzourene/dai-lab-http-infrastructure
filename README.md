# Report - DAI Project - Dietary Monitoring

### Authors: Ryad Bouzourène, Anthony Christen, Louis Haye

---

## Step 1: Static Web Site

The goal of this step was to create a Docker container running Nginx to serve a static website. Below are the configuration files and the steps taken to achieve this.

### Configuration of `nginx.conf`:
```nginx
http {
    include /etc/nginx/mime.types;

    server {
        listen 80;
        server_name localhost;

        location / {
            root /usr/share/nginx/html;
            index index.html;
        }

        error_page 404 /404.html;
    }
}
```

### Configuration of `Dockerfile`:
```dockerfile
FROM nginx:latest

COPY src /usr/share/nginx/html

COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 80
```

### How to run the Docker:
To build the Docker image:
```bash
docker build -t static-website:latest .
```

To run it on port 8080:
```bash
docker run -d -p 8080:80 static-website:latest
```

To test it:
1. Open your web browser and go to [http://localhost:8080](http://localhost:8080).
2. Verify that the website loads correctly.
3. Test the 404 page by navigating to a non-existent URL (e.g., `http://localhost:8080/hello`).

### Website Description:

The static website is built using a free template from Start Bootstrap. It includes a landing page with placeholders for images and text, designed for a clean and modern appearance.

---

## Step 2: Docker Compose

