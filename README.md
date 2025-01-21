# Rapport à compléter

## Step 1

Pour cette partie, nous avons utilisé un template de site Web statique nommé Medi-Plus. Ce template a été récupéré gratuitement sur une plateforme de partage de templates tels que Free-CSS. Il s'agit d'un site à page unique avec un design attractif, adapté à des sites médicaux ou similaires.

### Objectifs

L'objectif principal était de construire une image Docker contenant un serveur HTTP statique basé sur Nginx, qui sert le contenu de ce site. Voici les étapes et configurations réalisées :

### Organisation du projet

Nous avons organisé le projet comme suit :
- Un dossier `src` contenant tous les fichiers du site Web statique (HTML, CSS, JS, images, etc.).
- Un fichier `Dockerfile` pour créer l'image Docker.
- Un fichier `nginx.conf` configurant le serveur Nginx.

### Contenu des fichiers de configuration

1. **nginx.conf** :  
   La configuration du serveur est simple et vise à servir le contenu statique à partir de la racine `/usr/share/nginx/html`. Voici les détails :
   - `worker_connections 1024` : Configure le nombre maximum de connexions simultanées par worker.
   - `include /etc/nginx/mime.types` : Permet de définir les types MIME pour les fichiers.
   - `root /usr/share/nginx/html` : Définit la racine où sont stockés les fichiers statiques.
   - `index index.html` : Spécifie le fichier à charger par défaut.
   - `error_page 404 /404.html` : Définit une page d'erreur personnalisée pour les 404.

2. **Dockerfile** :  
   Nous avons utilisé l'image officielle `nginx:latest` comme base. Voici les principales instructions :
   - `COPY src /usr/share/nginx/html` : Copie les fichiers du site dans le dossier prévu pour Nginx.
   - `COPY nginx.conf /etc/nginx/nginx.conf` : Remplace la configuration par défaut de Nginx par notre configuration personnalisée.
   - `EXPOSE 80` : Expose le port 80 pour que le serveur soit accessible.

### Validation

Nous avons construit et lancé l'image Docker. Une fois en cours d'exécution, le serveur est accessible via un navigateur à l'adresse `http://localhost:NUMPORT`.