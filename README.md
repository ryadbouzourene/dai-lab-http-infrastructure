# Rapport DAI
### Auteurs : Ryad Bouzourène, Anthony Christen, Louis Haye

## Etape 1: Site web statique

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

## Etape 2: Docker Compose

Pour simplifier de déploiement de notre serveur web statique, nous avons utilisé Docker Compose.

### Configuration du `docker-compose.yml`:
```yaml
services:
  static-web:
    build:
      context: ./static-website
    ports:
      - "8080:80"
    container_name: static-website
```

### Comment lancer le Docker Compose:
1. **Construire et lancer le service**:
```bash
docker-compose up --build
```

2. **Arrêter le service**:
```bash
docker-compose down
```

3. **Tester le serveur**:
Ouvrir son navigateur internet et aller à l'adresse [http://localhost:8080](http://localhost:8080)
pour vérifier que le site web statique est en cours.

### Validation:
- Le site web statique est à accessible à l'adresse `http://localhost:8080`.
- On peut facilement effectuer des changement sur le site web et reconstruire l'image Docker en utilisant: 
```bash
docker-compose build
```

---

## Etape 3: API Serveur HTTP


## Etape 4: Proxy inverse (Traefik)

Pour améliorer la gestion et la sécurité de notre infrastructure Docker, nous avons mis en place un proxy inverse 
(reverse proxy) à l'aide de **Traefik**. Voici les explications concernant son implémentation, son utilité,
la manière d’y accéder et son fonctionnement.

---

### 1. Implémentation de la solution et configuration

#### 1.1. Utilisation de Traefik comme proxy inverse
 
Dans le fichier `docker-compose.yml`, nous avons ajouté un service nommé `reverse_proxy` 
(basé sur l’image `traefik:v3.3`).

Voici un aperçu de la configuration :
```yaml
reverse_proxy:
  image: traefik:v3.3
  container_name: traefik
  command:
    - "--entrypoints.web.address=:80"
    - "--providers.docker=true"
    - "--api.insecure=true"
  ports:
    - "80:80"        # Accès principal (HTTP)
    - "8080:8080"    # Dashboard Traefik
  volumes:
    - /var/run/docker.sock:/var/run/docker.sock:ro
  networks:
    - bdr-net
```
`image` : Nous utilisons la dernière version de Traefik, la `v3.3`

`Command` :
- `--entrypoints.web.address=:80` : Définit un point d’entrée pour le trafic HTTP sur le port 80.

- `--providers.docker=true` : Indique à Traefik d’utiliser la configuration des conteneurs Docker 
(via leurs labels) pour définir les règles de routage.

- `--api.insecure=true` : Active l’interface de monitoring/d’administration de Traefik sur un port dédié (ici, 8080).
L’option insecure est utile pour des tests ou en environnement de développement, mais n’est pas recommandée en 
production sans sécurisation supplémentaire.

`ports` :
- `80:80` permet d’exposer le port 80 du conteneur vers l’hôte.
- `8080:8080` permet d’accéder au Tableau de bord (dashboard) de Traefik.

`volumes` : 
- Le montage du **socket Docker** (`/var/run/docker.sock`) en lecture seule (`:ro`) permet à Traefik de lire les informations 
de configuration de tous les conteneurs qui s’exécutent sur le même réseau Docker 
et ainsi de découvrir automatiquement les services.

`networks` :
Nous utilisons un réseau Docker `bdr-net` pour permettre la communication interne entre Traefik et les autres conteneurs.

#### 1.2. Routage pour le site statique et l’API
Dans les services static-web (site statique) et api-server (API), nous avons ajouté des labels Traefik. Ces labels indiquent à Traefik comment router le trafic vers le bon conteneur :

#### Pour le site statique (static-web) :

```yaml
labels:
- "traefik.enable=true"
- "traefik.http.routers.static.rule=Host(`localhost`)"
- "traefik.http.routers.static.entrypoints=web"
- "traefik.http.services.static.loadbalancer.server.port=80"
```
Cela signifie que toutes les requêtes visant http://localhost seront redirigées vers le conteneur static-web, qui écoute en interne sur le port 80.


#### Pour l’API (api-server) :

```yaml
labels:
- "traefik.enable=true"
- "traefik.http.routers.api.rule=PathPrefix(`/api`)"
- "traefik.http.routers.api.entrypoints=web"
- "traefik.http.services.api.loadbalancer.server.port=80"
```
  Ainsi, les requêtes arrivant sur http://localhost/api (tout chemin commençant par /api) seront redirigées vers le conteneur api-server, lui aussi à l’écoute sur le port 80 en interne.

Cette configuration nous permet donc d’avoir un point d’accès unique (sur le port 80 de l’hôte) pour le site statique et l’API, et de diriger le trafic selon l’URL demandée.

### 2. Pourquoi un proxy inverse améliore la sécurité ?
Un reverse proxy comme Traefik présente plusieurs avantages en matière de sécurité :

- **Isolation des services** : Le proxy inverse agit comme couche intermédiaire entre les utilisateurs et nos services 
(site statique, API, etc.). Ainsi, les applications internes n'exposent pas directement leurs ports à l’extérieur,
réduisant la surface d’attaque potentielle.


- **Gestion centralisée des accès** : On peut configurer des règles d’accès, de filtrage ou d’authentification 
directement dans le reverse proxy. Traefik gère lui-même le trafic, et permet d’ajouter des mécanismes de sécurité 
(TLS/SSL, redirection HTTP→HTTPS, protection par mot de passe, etc.).


- **Suivi et journalisation** : Le reverse proxy permet de centraliser les logs de requêtes.
On peut ainsi analyser plus facilement le trafic, identifier des tentatives d’intrusion ou de surcharge,
et prendre des mesures adaptées.


- **Flexibilité et scalabilité** : Avec Traefik, le routage et l’équilibrage de charge (load balancing)
peuvent être configurés pour répartir la charge sur plusieurs instances d’un même service,
renforçant la disponibilité et la stabilité de l’infrastructure.

### 3. Accès au dashboard de Traefik
Grâce à l’option `--api.insecure=true` et l’ouverture du port 8080, nous pouvons accéder au dashboard de Traefik à l’adresse :
http://localhost:8080 après avoir lancer les containers.

Ce tableau de bord permet de :
- Visualiser l’état des routers, services et middlewares configurés.
- Contrôler si les routes définies (telles que Host(localhost), PathPrefix(/api), etc.) sont bien reconnues et fonctionnelles.
- Identifier et résoudre facilement d’éventuels problèmes de configuration.

---

## Étape 5 : Scalabilité et Load Balancing

L'objectif de cette étape est de permettre la scalabilité de l'infrastructure en déployant plusieurs instances de chaque service (site statique et API) et de s'assurer que le proxy inverse **Traefik** effectue correctement l'équilibrage de charge (load balancing) entre ces instances.

---

### Configuration pour plusieurs instances d'un même service

Pour configurer plusieurs instances, il est nécessaire de modifier le fichier `docker-compose.yml` en ajoutant les paramètres suivants pour chaque service (par exemple, `static-web` et `api-server`) :

```yaml
deploy:
  replicas: <X>
  restart_policy:
    condition: on-failure
```

#### Explications :
- **`replicas: <X>`** : Définit le nombre d'instances (réplicas) à déployer pour le service.
- **`restart_policy: condition: on-failure`** : Assure que les conteneurs redémarrent automatiquement en cas d'échec.

---

### Étapes pour lancer et gérer l'infrastructure

#### 1. Pré-construire les images Docker

Avant de déployer les services, il est nécessaire de construire les images Docker pour le site statique et l'API :
- **Construire l'image du site statique** :
  ```bash
  docker build -t static-web:latest ./static-website
  ```
- **Construire l'image de l'API** :
  ```bash
  docker build -t api-server:latest ./api-server
  ```

#### 2. Activer le mode Docker Swarm

Le mode Swarm est requis pour utiliser l'option `deploy.replicas`. Pour activer le mode Swarm :
```bash
docker swarm init
```

- **Vérifier que Swarm est activé** :
  ```bash
  docker info
  ```
  Si Swarm est activé, la section **Swarm** affichera "active".

- **Désactiver le mode Swarm** (si nécessaire) :
  ```bash
  docker swarm leave --force
  ```

#### 3. Déployer l'infrastructure avec plusieurs instances

Une fois le mode Swarm activé, déployez les services définis dans `docker-compose.yml` :
```bash
docker stack deploy --compose-file docker-compose.yml <nom_de_stack>
```
- `<nom_de_stack>` : Nom de l'ensemble des services (par exemple, `my_stack`).

Pour vérifier que les services sont bien déployés et que toutes les instances sont actives :
```bash
docker service ls
```

---

### Ajuster dynamiquement le nombre d'instances d'un service

Pour ajouter ou réduire dynamiquement le nombre de réplicas d’un service sans redémarrer l'infrastructure, utilisez la commande suivante :
```bash
docker service scale <nom_du_service>=<X>
```

#### Paramètres :
- **`<nom_du_service>`** : Nom du service à mettre à jour (par exemple, `my_stack_static-web`).
- **`<X>`** : Nombre d'instances souhaitées.

#### Vérification :
- Vérifiez le nombre de réplicas avec la commande :
  ```bash
  docker service ls
  ```
- Vous pouvez également consulter le tableau de bord de Traefik dans l'onglet **HTTP Services** pour voir le nombre d'instances actives dans la colonne "Servers".

---

### Vérification du Load Balancing

#### Étape 1 : Observer les logs des services

Exécutez la commande suivante pour afficher les logs des instances d'un service spécifique :
```bash
docker service logs my_stack_static-web --follow
```

Chaque log indique l'instance qui traite les requêtes. Les IDs de conteneurs dans les logs permettent de différencier les instances.

#### Étape 2 : Tester l'équilibrage de charge avec des requêtes

Envoyez plusieurs requêtes au service pour observer comment elles sont distribuées entre les instances. Par exemple, pour tester le site statique avec `curl` :
```bash
for i in {1..10}; do
  curl -s "http://localhost?unique=$RANDOM"
done
```

- **Pourquoi ajouter `?unique=$RANDOM` ?**  
  Cela permet de contourner le cache en générant une requête unique à chaque itération.

#### Résultat attendu :
Dans la console des logs, vous verrez que les requêtes sont distribuées entre les différentes instances. Chaque instance traite certaines requêtes, prouvant que **Traefik effectue correctement l'équilibrage de charge**.

---

### Résumé des commandes essentielles

| Action                                    | Commande                                                                                                |
|------------------------------------------|--------------------------------------------------------------------------------------------------------|
| Construire les images Docker             | `docker build -t static-web:latest ./static-website`<br>`docker build -t api-server:latest ./api-server` |
| Activer Docker Swarm                     | `docker swarm init`                                                                                     |
| Déployer les services avec Docker Stack  | `docker stack deploy --compose-file docker-compose.yml my_stack`                                       |
| Vérifier les services actifs             | `docker service ls`                                                                                     |
| Ajuster dynamiquement les réplicas       | `docker service scale my_stack_static-web=5`                                                           |
| Observer les logs                        | `docker service logs my_stack_static-web --follow`                                                     |
| Tester l'équilibrage avec des requêtes   | `for i in {1..10}; do curl -s "http://localhost?unique=$RANDOM"; done`                                 |

---

### Conclusion

Grâce à cette configuration, notre infrastructure est désormais scalable et utilise un équilibrage de charge automatique via **Traefik**. Le mode Swarm permet d'ajuster dynamiquement le nombre d'instances des services en fonction des besoins, et **Traefik détecte automatiquement les instances disponibles pour répartir le trafic sans interruption**. Les tests réalisés montrent que les requêtes sont bien distribuées entre les différentes instances, assurant une haute disponibilité et une montée en charge efficace.

