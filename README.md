# Report - DAI Project - Dietary monitoring
### Authors : Ryad Bouzourène, Anthony Christen, Louis Haye
---
## Step 1 : Static Web Site
### Configuration of "nginx.conf" :
````
events {
worker_connections 1024;
}

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
````

### Configuration of "Dockerfile" :
````
FROM nginx:latest

COPY src /usr/share/nginx/html

COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 80
````

### How to run the Docker : 
To build it : 
````
docker build -t static-website:latest .
````
To run it on the port 8080 :
````
docker run -d -p 8080:80 static-website:latest
````
To test it : 
Open your web browser and search : http://localhost:8080

## Step 2 : Docker Compose