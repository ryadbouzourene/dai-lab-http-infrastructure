# Rapport DAI
### Auteurs : Ryad Bouzourène, Anthony Christen, Louis Haye

---

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

---

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

Pour cette étape, nous avons développé une API REST complète en Java avec le framework Javalin, connectée à une base de données PostgreSQL. Cette API fait partie d'une application de suivi diététique permettant la gestion des patients, des diététiciens, des repas et des données de santé. La base de données stocke toutes les informations nécessaires au suivi nutritionnel des patients.

### Architecture et implémentation

Notre API est structurée selon une architecture MVC (Modèle-Vue-Contrôleur) et comprend :

- **Contrôleurs** : Gèrent les routes et les requêtes HTTP
  - Routes authentifiées (nécessitant une connexion)
  - Routes non authentifiées (publiques)
- **Services** : Contiennent la logique métier
- **Modèles** : Représentent les entités de données
- **DAO** : Gèrent l'accès aux données

### Base de données PostgreSQL

La base de données est structurée autour d'un schéma `suivi_dietetique` qui comprend :
- Tables principales : `personne`, `employe`, `patient`, `dieteticien`, `infirmier`
- Tables de gestion : `service`, `consommable`, `repas`, `donnee_sante`
- Types énumérés personnalisés pour la gestion des rôles, statuts et types divers
- Vues et triggers pour maintenir l'intégrité des données

L'initialisation de la base de données est automatisée via des scripts SQL qui :
1. Créent les tables et les contraintes
2. Mettent en place les vues et les triggers
3. Insèrent les données initiales de test

La configuration Docker de la base de données utilise l'image `bitnami/postgresql:17` avec la configuration suivante :
```yaml
postgresql:
    image: 'bitnami/postgresql:17'
    environment:
      - POSTGRESQL_USERNAME=bdr
      - POSTGRESQL_PASSWORD=bdr
      - POSTGRESQL_DATABASE=bdr
      - POSTGRESQL_POSTGRES_PASSWORD=root
    volumes:
      - ./init-scripts:/docker-entrypoint-initdb.d    # Scripts d'initialisation
    ports:
      - "5432:5432"                                   # Port PostgreSQL
```

Cette configuration :
- Crée une base de données nommée `bdr`
- Configure un utilisateur avec les accès nécessaires
- Monte automatiquement les scripts d'initialisation
- Expose le port standard de PostgreSQL

### Points d'accès principaux

L'API expose plusieurs endpoints REST pour gérer :
- L'authentification des utilisateurs (`/api/login`, `/api/logout`)
- Les patients et leurs données de santé
- Les diététiciens et leurs suivis
- Les repas et les consommables
- Les allergènes et restrictions alimentaires

### Configuration Docker

Le `Dockerfile` pour l'API est configuré comme suit :
```yaml
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/*with-dependencies.jar app.jar
EXPOSE 80
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Sécurité et Authentification

L'API implémente un système de sécurité complet avec :
- Gestion des sessions utilisateur
- Différents niveaux d'accès en fonction du role (ADMIN, PATIENT, DIETETICIEN)
- Protection des routes sensibles
- Hachage des mots de passe

### Validation

Pour tester l'API :
1. Construire l'image Docker : `docker build -t api-server .`
2. Lancer le conteneur : `docker run -p 80:80 api-server`
3. Accéder à l'API via `http://localhost:80/api`
4. Tester les endpoints avec un client HTTP (comme Postman)

---

## Etape 4: Proxy inverse (Traefik)

Pour améliorer la gestion et la sécurité de notre infrastructure Docker, nous avons mis en place un proxy inverse 
(reverse proxy) à l'aide de **Traefik**. Voici les explications concernant son implémentation, son utilité,
la manière d’y accéder et son fonctionnement.


### 1. Implémentation de la solution et configuration

#### Utilisation de Traefik comme proxy inverse
 
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

#### Routage pour le site statique et l’API
Dans les services static-web (site statique) et api-server (API), nous avons ajouté des labels Traefik. Ces labels indiquent à Traefik comment router le trafic vers le bon conteneur :

#### Pour le site statique (static-web) :

```yaml
labels:
- "traefik.enable=true"
- "traefik.http.routers.static.rule=Host(`static-website.localhost`)"
- "traefik.http.routers.static.entrypoints=web"
- "traefik.http.services.static.loadbalancer.server.port=80"
```
Cela signifie que toutes les requêtes visant http://static-website.localhost seront redirigées vers le conteneur static-web, qui écoute en interne sur le port 80.


#### Pour l’API (api-server) :

```yaml
labels:
- "traefik.enable=true"
- "traefik.http.routers.api.rule=Host(`localhost`) && PathPrefix(`/api`)"
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

## Etape 5 : Scalabilité et Load Balancing

L'objectif de cette étape est de permettre la scalabilité de l'infrastructure en déployant plusieurs instances de chaque service (site statique et API) et de s'assurer que le proxy inverse **Traefik** effectue correctement l'équilibrage de charge (load balancing) entre ces instances.



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
Pour supprimer la pile de services : 
```bash
docker stack rm <nom_de_stack>
```



### Ajuster dynamiquement le nombre d'instances d'un service

Pour ajouter ou réduire dynamiquement le nombre de réplicas d’un service sans redémarrer l'infrastructure,
utilisez la commande suivante :
```bash
docker service scale <nom_du_service>=<X>
```
- **`<nom_du_service>`** : Nom du service à mettre à jour (par exemple, `my_stack_static-web`).
- **`<X>`** : Nombre d'instances souhaitées.

#### Vérification :
- Vérifiez le nombre de réplicas avec la commande :
  ```bash
  docker service ls
  ```
- Vous pouvez également consulter le tableau de bord de Traefik dans l'onglet **HTTP Services** pour voir le nombre
d'instances actives dans la colonne "Servers".


### Vérification du Load Balancing

#### Étape 1 : Observer les logs des services

Exécutez la commande suivante pour afficher les logs des instances d'un service spécifique :
```bash
docker service logs my_stack_static-web --follow
```

Chaque log indique l'instance qui traite les requêtes. Les IDs de conteneurs dans les logs permettent de différencier
les instances.

#### Étape 2 : Tester l'équilibrage de charge avec des requêtes

Envoyez plusieurs requêtes au service pour observer comment elles sont distribuées entre les instances. Par exemple,
pour tester le site statique avec `curl` :
```bash
for i in {1..10}; do
  curl -s "http://static-webiste.localhost?unique=$RANDOM"
done
```

- **Pourquoi ajouter `?unique=$RANDOM` ?**  
  Cela permet de contourner le cache géré par Traefik en générant une requête unique à chaque itération.

#### Résultat attendu :
Dans la console des logs, vous verrez que les requêtes sont distribuées entre les différentes instances. Chaque instance traite certaines requêtes, prouvant que **Traefik effectue correctement l'équilibrage de charge**.

### Conclusion

Grâce à cette configuration, notre infrastructure est désormais scalable et utilise un équilibrage de charge automatique via **Traefik**. Le mode Swarm permet d'ajuster dynamiquement le nombre d'instances des services en fonction des besoins, et **Traefik détecte automatiquement les instances disponibles pour répartir le trafic sans interruption**. Les tests réalisés montrent que les requêtes sont bien distribuées entre les différentes instances, assurant une haute disponibilité et une montée en charge efficace.

--- 

## Etape 6 : Load balancing avec round-robin et sticky sessions
### Objectifs
L'objectif de cette étape est de configurer Traefik pour :

- Utiliser le round-robin pour le site statique.
- Activer les sticky sessions pour l'API dynamique.
Les **sticky sessions** permettent d'associer un utilisateur ou une session spécifique à une instance particulière d'un 
- service. Cela signifie que toutes les requêtes provenant du même utilisateur sont toujours envoyées à la même instance.

Ce mécanisme est essentiel pour les applications **stateful** (avec état), comme notre API, qui gèrent des 
sessions utilisateurs ou des connexions à une base de données. Dans le cas présent, Traefik utilise un cookie 
(nommé `api-session`) pour identifier et associer chaque session à une instance précise. 
Ce cookie est généré automatiquement lors de la première requête et est utilisé pour router les requêtes suivantes
vers la même instance.

Les sticky sessions permettent d'éviter des problèmes liés à la répartition des requêtes entre plusieurs instances 
pour une même session utilisateur.

### Configuration
#### Site statique (round-robin par défaut)

La configuration pour le site statique n’a pas été modifiée, car Traefik utilise le round-robin par défaut.
Les requêtes sont distribuées équitablement entre toutes les instances du service.

#### API dynamique (sticky sessions)

Voici la configuration ajoutée dans docker-compose.yml pour activer les sticky sessions sur l'API :

```yaml
api-server:
  image: api-server:latest
  deploy:
    replicas: 3
    restart_policy:
      condition: on-failure
  networks:
    - bdr-net
  labels:
    - "traefik.enable=true"
    - "traefik.http.routers.api.rule=Host(`localhost`) && PathPrefix(`/api`)"
    - "traefik.http.routers.api.entrypoints=web"
    - "traefik.http.services.api.loadbalancer.server.port=80"
    - "traefik.http.services.api.loadbalancer.sticky.cookie=true"
    - "traefik.http.services.api.loadbalancer.sticky.cookie.name=api-session"
``` 
### Vérification des fonctionnalités
#### Round-robin pour le site statique :

En envoyant plusieurs requêtes à http://static-webiste.localhost, nous avons constaté que les réponses proviennent de différentes
instances du site statique (voir étape 5: Scalabilité et Load balancing).

**Résultat attendu** : Les réponses montrent une distribution équitable entre les instances.

#### Sticky sessions pour l'API :

Avant l'implémentation des sticky sessions, notre API rencontrait des problèmes de connexion. Chaque requête était
potentiellement envoyée à une instance différente, ce qui perturbait la gestion des sessions et des connexions
utilisateur.

Après avoir configuré le fichier `docker-compose.yml` avec les labels activant les sticky sessions, les problèmes de
connexion ont été résolus. Cela montre que le mécanisme de sticky sessions fonctionne correctement, car chaque session
est maintenant associée à une instance spécifique.

Pour valider davantage, nous avons remarqué que le cookie de session, généré par Traefik, reste constant lors
des multiples requêtes d'un même utilisateur, garantissant que toutes les requêtes d'une session sont acheminées vers
la même instance.

### Conclusion
Grâce à cette configuration :

- Round-robin est utilisé pour le site statique, ce qui assure une répartition équitable des requêtes entre les instances.
- Sticky sessions sont activées pour l'API, garantissant que les requêtes d'une même session utilisateur sont
acheminées vers la même instance.
- Les tests réalisés confirment le fonctionnement attendu, validant l'utilisation correcte des deux méthodes d'équilibrage.

--- 

## Etape 7: Sécuriser Traefik avec HTTPS

Dans cette étape, nous avons configuré **Traefik** pour communiquer en HTTPS avec les clients (navigateurs), tout en conservant une communication interne en HTTP entre le proxy et les services (site statique et API).
L’objectif est de **chiffrer** toutes les connexions provenant de l’extérieur, même si le certificat utilisé est **autosigné** (self-signed).


### Génération d’un certificat autosigné
Ne pouvant pas (dans notre cas) utiliser des certificats publics (ex. Let’s Encrypt), nous avons généré un certificat autosigné. Pour cela, nous avons utilisé la commande openssl :

```bash
mkdir certificates
cd certificates
openssl req -x509 -newkey rsa:4096 -sha256 -days 365 -nodes \
  -keyout server.key -out server.crt \
  -subj "/CN=localhost"
```
- ``server.crt`` : Certificat public autosigné.
- ``server.key`` : Clé privée associée.

Ces deux fichiers sont montés dans le conteneur Traefik pour lui permettre de gérer TLS.

### Configuration de Traefik
#### Fichier traefik.yaml
Pour activer TLS, nous avons ajouté un bloc tls pointant vers le dossier contenant server.crt et server.key. Voici un extrait :

```yaml
api:
  dashboard: true

entryPoints:
  web:
    address: ":80"       # HTTP
  websecure:
    address: ":443"      # HTTPS
  dashboard:
    address: ":8080"     # HTTPS pour le dashboard

providers:
  docker:
    exposedByDefault: false

tls:
  certificates:
    - certFile: "/etc/traefik/certificates/server.crt"
      keyFile:  "/etc/traefik/certificates/server.key"
```
- `entryPoints.websecure` : Définit l’écoute HTTPS sur le port :443.
- `tls` : Localise les certificats chargés par Traefik.
- `dashboard` : Gère un entrypoint dédié au dashboard en HTTPS (port 8080).

#### Labels Docker pour activer HTTPS
Dans le docker-compose.yml, nous avons ajouté des labels pour chaque service (site statique et API) afin d’activer l’entrée HTTPS :

```yaml
static-web:
  labels:
    - "traefik.enable=true"
    - "traefik.http.routers.static.rule=Host(`static-website.localhost`)"
    - "traefik.http.routers.static.entrypoints=web"
    - "traefik.http.routers.static.tls=true"
    - "traefik.http.routers.static.entrypoints=websecure"
    - "traefik.http.services.static.loadbalancer.server.port=80"
```
- traefik.http.routers.static.tls=true : Active TLS pour ce router (donc l’entrée websecure).
- websecure (port 443) et web (port 80) sont tous deux déclarés, permettant au service d’être disponible sur HTTP et HTTPS.

Le même principe est appliqué à l’API :

```yaml
api-server:
  labels:
    - "traefik.enable=true"
    - "traefik.http.routers.api.rule=Host(`localhost`) && PathPrefix(`/api`)"
    - "traefik.http.routers.api.entrypoints=web"
    - "traefik.http.routers.api.tls=true"
    - "traefik.http.routers.api.entrypoints=websecure"
    - "traefik.http.services.api.loadbalancer.server.port=80"
    - "traefik.http.services.api.loadbalancer.sticky.cookie=true"
    - "traefik.http.services.api.loadbalancer.sticky.cookie.name=api-session"
```
Ainsi, on dispose de HTTP et HTTPS pour l’API.

### 3. Accès au Dashboard en HTTPS sur :8080
Au lieu d’utiliser api.insecure=true, nous avons configuré un entrypoint nommé dashboard écoutant en HTTPS sur le port 8080. Dans les labels du service Traefik :

```yaml
reverse_proxy:
  ports:
    - "80:80"
    - "443:443"
    - "8080:8080"
  labels:
    - "traefik.enable=true"
    - "traefik.http.routers.traefik-dashboard.rule=Host(`localhost`)"
    - "traefik.http.routers.traefik-dashboard.entrypoints=dashboard"
    - "traefik.http.routers.traefik-dashboard.tls=true"
    - "traefik.http.routers.traefik-dashboard.service=api@internal"
```
De cette façon, on peut accéder au dashboard via https://localhost:8080.

Le certificat autosigné générera un avertissement de sécurité que l’on peut ignorer.

### Tests et validation
#### Connexion HTTPS au site statique et à l’API
- **Site statique** :
Accéder à https://static-webiste.localhost (ou le domaine/host configuré).
Le navigateur indique « connexion non sécurisée » (puisqu’il s’agit d’un certificat autosigné), ce qui est normal.

- **API** :
Vérifier les routes https://localhost/api/... pour confirmer que l’API répond en HTTPS.

#### Dashboard Traefik
En tapant https://localhost:8080, on obtient le dashboard, montrant les routers et services configurés.
Cela permet de confirmer la bonne prise en charge de TLS et le routage correct.

### 5. Conclusion
Grâce à cette configuration :

- Le trafic externe (navigateur → proxy Traefik) est entièrement chiffré en HTTPS.
- Les certificats autosignés permettent de sécuriser le développement local, malgré l’avertissement du navigateur.
- Le dashboard est lui aussi disponible en HTTPS, évitant le mode insecure par défaut.
- Les services (site statique et API) continuent de communiquer en HTTP à l’intérieur du réseau Docker.
- Cette approche garantit la sécurisation des échanges entre les utilisateurs et nos services, répondant ainsi à l’objectif de chiffrement des données en transit.

---

## Etape optionnelle 1: Interface de gestion

### Objectif

L’objectif de cette étape est de déployer une interface de gestion (Management UI) pour superviser et contrôler l’infrastructure Docker de manière dynamique. Cela inclut :
- Lister les conteneurs en cours d’exécution.
- Démarrer/arrêter des conteneurs.
- Ajouter ou supprimer des instances.
- Superviser les logs et les ressources.

Nous avons choisi d’utiliser **Portainer**, une solution existante et robuste, pour répondre à ces besoins.


### Configuration de Portainer

#### 1. Ajout de Portainer dans `docker-compose.yml`

Voici la configuration ajoutée au fichier `docker-compose.yml`:

```yaml
  # ========================
  # Portainer (Management UI)
  # ========================
  portainer:
    image: portainer/portainer-ce:latest  # Utilise l'édition communautaire de Portainer
    ports:
      - "9000:9000"                       # Port exposé pour accéder à l'interface Web de Portainer
    volumes:
      - /var/run/docker.sock:/var/run/docker.sock  # Permet à Portainer de communiquer avec Docker via le socket local
      - portainer_data:/data                      # Volume pour stocker les données internes de Portainer
    networks:
      - bdr-net                                   # Utilise le même réseau que les autres services

volumes:
  portainer_data:  # Volume nommé pour stocker les données de Portainer
```
### 2. Déploiement et accès à Portainer
#### 2.1. Déploiement
Lancer l'ensemble du Docker Compose:

```bash
docker stack deploy --compose-file docker-compose.yml <nom_de_stack>
```

- Vérifier que le conteneur Portainer est en cours d’exécution:
```bash
docker ps
```
#### 2.2. Accéder à l’interface Web de Portainer:
Ouvrez un navigateur et rendez-vous sur http://localhost:9000.


#### 2.3. Configuration initiale
- Créer un compte administrateur

- Connecter Portainer à l’environnement Docker local :
  - Cliquez sur le bouton `Home` sur la gauche.
  - Sélectionnez Local comme environnement Docker.

### 3. Utilisation de Portainer

#### Fonctionnalités principales

1. **Lister les services** :
    - Cliquez du l'onglet `Services`
   
   Vous y trouverez les informations concernants chaques services (nom, image, ports, nombre de répliques, etc...).
      

2. **Démarrer ou arrêter un conteneur** :
   - Allez dans la section `Containers`.
   - Cliquez sur la coche à côté du conteneur que vous souhaitez démarrer / arrêter.
   - Cliquez sur le bouton `Start` pour démarrer un conteneur arrêté.
   - Cliquez sur `Stop` pour arrêter un conteneur en cours d’exécution.


3. **Supprimer un conteneur** :
    - Allez dans la section `Containers`.
    - Cliquez sur la coche à côté du conteneur que vous souhaitez supprimer.
    - Cliquez sur `Remove` à côté du conteneur que vous souhaitez supprimer.

    (Même chose dans la section `Service` si vous voulez supprimer un service).


4. **Ajuster le nombre de répliques d'un service** :
    - Allez dans la section `Services`.
    - Sur la ligne du service auquel vous voulez modifier le nombre de répliques, cliquez sur le bouton `Scale`.
    - Entrez le nombre de répliques souhaité.
    - Cliquez sur le bouton sur la droite pour appliquer le changement.


5. **Superviser les logs des conteneurs** :
    - Sélectionnez un conteneur, puis cliquez sur l’onglet **Logs** pour visualiser les journaux en temps réel.

### 4. Conclusion
- Portainer a été déployé et configuré avec succès, offrant une interface intuitive pour gérer l’infrastructure Docker.
- Les conteneurs/instances peuvent être listés, démarrés/arrêtés, dupliqués, ou supprimés via l’interface.
- Des instances dans Docker Compose peuvent être déployées et mises à jour dynamiquement.

---

## Etape optionnelle 2: Integration API - Static Web site

### Objectif

L'objectif de cette section est de connecter un frontend à notre API afin d'effectuer des requêtes sur les endpoints de cette dernière. Etant donné que nous avons développé un frontend pour notre projet de BDR, nous l'avons réutilisé et avons décidé de conserver le site web statique de l'étape 1 tel quel.

### Fonctionnalités du Frontend

Le frontend est une application React permettant à trois types d'utilisateurs de se connecter et d'accéder aux fonctionnalités spécifiques à leurs rôles. Les utilisateurs peuvent se connecter via l'URL suivante :

- **URL de connexion** : https://localhost
- **Identifiants de test** :

| Rôle        | Email               | Mot de passe |
|-------------|---------------------|--------------|
| Admin       | admin@test.com      | pwd          |
| Diététicien | dieteticen@test.com | pwd          |
| Infirmier   | infirmier@test.com  | pwd          |
| Patient     | patient@test.com    | pwd          |


Différentes opérations peuvent être réalisées selon son rôle dans le but de suivre l'évolution diététique de patients. Il est par exemple possible d'effectuer les actions suivantes :

- Accéder à son dashboard en ayant un affichage correspondant à son rôle
- Lister les patients, les diétéiciens et les infirmiers
- Lister les consommables et allergènes.
- Créer, supprimer ou mettre à jour un patient
- Créer ou supprimer un repas
- Créer ou supprimer des données de santé d'un patient
- Consulter les objectifs d'un patient ou d'un diététicien
- Consulter un repas particulier et ses statistques nutritionnelles
- Avoir accès aux repas d'un patient sur une période donnée (un jour, une semaine, un mois, une année, tout) et visualiser des statistiques sur l'évolution des apports nutritionnels.
- Consulter l'historique des données de santé d'un patient et des statistiques.

Nous vous recommandons de vous connecter avec le rôle de `Diététicien` ou celui de `Patient` pour avoir accés aux fonctionnalités les plus intéressantes et représentatives de la réalité.

### Technologies utilisées

- **React** : Framework pour la construction de l'interface utilisateur.
- **Material-UI (MUI)** : Bibliothèque de composants pour un design moderne.
- **Axios** : Client HTTP pour interagir avec l'API Javalin.
- **ABAC/RBAC** : Gestion des droits d'accès utilisateurs.
- **Cookies** : Stockage des sessions utilisateurs pour maintenir l'état.

## Faire fonctionner le projet dans son entièreté

Pour faire fonctionner toutes les étapes du projet, éxécutez ces commandes :

```bash
# Construire les images (si nécessaire)
docker build -t static-web:latest ./static-website
docker build -t api-server:latest ./api-server
docker build -t react-frontend:latest ./react-frontend 

docker swarm init
docker stack deploy --compose-file docker-compose.yml <nom_de_stack>
```
> [!IMPORTANT]
> Changez le champs `<nom_de_stack>` par le nom que vous souhaitez donner à la stack

## Résumé des commandes principales

| Action                                  | Commande                                                                                                                                                               |
|-----------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Construire les images Docker            | `docker build -t static-web:latest ./static-website`<br>`docker build -t api-server:latest ./api-server` <br> `docker build -t react-frontend:latest ./react-frontend` |
| Activer Docker Swarm                    | `docker swarm init`                                                                                                                                                    |
| Déployer les services avec Docker Stack | `docker stack deploy --compose-file docker-compose.yml <nom_de_stack>`                                                                                                 |
| Vérifier les services actifs            | `docker service ls`                                                                                                                                                    |
| Ajuster dynamiquement les réplicas      | `docker service scale <nom_de_stack>_<nom_du_service>=<X>`                                                                                                             |
| Arrêter la stack                        | `docker stack rm <nom_de_stack>`                                                                                                                                       |
| Arrêter le mode Swarm                   | `docker swarm leave --force`                                                                                                                                           |


| Site                        | URL                              |
|-----------------------------|----------------------------------|
| Site web statique           | https://static-website.localhost |
| API                         | https://localhost/api            |
| Frontend connecté à l'api   | https://localhost                |
| Dashboard Traefik           | https://localhost:8080           |
| Portainer                   | http://localhost:9000            |

