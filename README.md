# 🎓 Système de Gestion des Stages

Une application JavaFX complète pour la gestion des stages en entreprise, développée avec Maven et MySQL.

![Java](https://img.shields.io/badge/Java-17-orange)
![JavaFX](https://img.shields.io/badge/JavaFX-21-blue)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)
![Maven](https://img.shields.io/badge/Maven-3.6+-green)

## 📋 Table des matières

- [Aperçu](#aperçu)
- [Fonctionnalités](#fonctionnalités)
- [Technologies](#technologies)
- [Installation](#installation)
- [Configuration](#configuration)
- [Utilisation](#utilisation)
- [Comptes de test](#comptes-de-test)
- [Structure du projet](#structure-du-projet)
- [Contribution](#contribution)
- [Licence](#licence)

## 🎯 Aperçu

Ce système permet de gérer efficacement le processus complet des stages en entreprise, de la création des offres jusqu'à l'évaluation des stagiaires. Il offre deux interfaces distinctes selon le type d'utilisateur :

- *Responsable du Personnel* : Gestion administrative des candidatures et stagiaires
- *Responsable de Stages* : Création des stages et évaluation des stagiaires

## ✨ Fonctionnalités

### 👤 Responsable du Personnel
- ✅ Gestion des candidats et candidatures
- ✅ Signature et impression des conventions
- ✅ Promotion des candidats en stagiaires
- ✅ Gestion administrative des stagiaires
- ✅ Attribution des badges et gestion des congés
- ✅ Consultation du catalogue des stages

### 👨‍💼 Responsable de Stages
- ✅ Création et suppression de stages (max 5)
- ✅ Consultation des candidatures pour ses stages
- ✅ Sélection et annulation de candidats
- ✅ Évaluation des stagiaires (notes sur 20)
- ✅ Suivi des performances

### 🔧 Fonctionnalités techniques
- ✅ Interface graphique moderne avec JavaFX
- ✅ Base de données MySQL robuste
- ✅ Architecture MVC claire
- ✅ Gestion des sessions utilisateur
- ✅ Validation des données
- ✅ Gestion d'erreurs complète

## 🛠 Technologies

| Technologie | Version | Usage |
|-------------|---------|-------|
| *Java* | 17+ | Langage principal |
| *JavaFX* | 21.0.1 | Interface utilisateur |
| *Maven* | 3.6+ | Gestion des dépendances |
| *MySQL* | 8.0+ | Base de données |
| *JDBC* | 8.0.33 | Connecteur base de données |

## 🚀 Installation
## 🚀 Installation

### Prérequis

Assurez-vous d'avoir installé :

```bash
# Java 17 ou supérieur
java -version

# Maven 3.6 ou supérieur
mvn -version

# MySQL 8.0 ou supérieur
mysql --version 
```

### Étapes d'installation

1. *Cloner le repository*
```bash
git clone https://github.com/AbdelhamidSaidi/StagEase
cd gestion-stages
```

2. *Configurer MySQL*

```bash
# Démarrer MySQL
sudo systemctl start mysql  # Linux
brew services start mysql   # macOS

# Se connecter à MySQL
mysql -u root -p
```

3. *Créer la base de données*
```bash
# Exécuter les scripts de création
mysql -u root -p < scripts/create-database-final.sql
mysql -u root -p < scripts/insert-data-final.sql
```

4. *Configurer la connexion* (si nécessaire)

Modifier src/main/java/com/gestionstages/util/DatabaseConnection.java :
```java
private static final String URL = "jdbc:mysql://localhost:3306/gestion_stages";
private static final String USERNAME = "root";
private static final String PASSWORD = "votre_mot_de_passe";
```

5. *Compiler et lancer*
```bash
# Compiler le projet
mvn clean compile

# Lancer l'application
mvn javafx:run
```

## ⚙ Configuration

### Variables d'environnement (optionnel)

```bash
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=gestion_stages
export DB_USER=root
export DB_PASSWORD=votre_mot_de_passe
```

### Configuration IDE

Pour *IntelliJ IDEA* :
1. Importer le projet Maven
2. Configurer le SDK Java 17+
3. Ajouter les modules JavaFX dans les VM options :
```
--module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml
```

## 🎮 Utilisation

### Démarrage

1. Lancer l'application avec mvn javafx:run
2. Se connecter avec un des comptes de test
3. Naviguer dans l'interface selon votre rôle

### Navigation

- *Dashboard* : Vue d'ensemble et navigation principale
- *Candidatures* : Gestion des candidats et candidatures
- *Stages* : Création et gestion des stages
- *Stagiaires* : Suivi des stagiaires en cours

## 👥 Comptes de test

### Responsable du Personnel

Email: marie.dupont@entreprise.com
Mot de passe: password123


### Responsables de Stages

Email: pierre.martin@entreprise.com
Mot de passe: password123

Email: sophie.durand@entreprise.com
Mot de passe: password123

Email: jean.bernard@entreprise.com
Mot de passe: password123


## 📁 Structure du projet

\\\`
gestion-stages/
├── 📄 pom.xml                          # Configuration Maven
├── 📄 README.md                        # Ce fichier
├── 📁 scripts/                         # Scripts SQL
│   ├── create-database-final.sql       # Création de la base
│   └── insert-data-final.sql           # Données de test
├── 📁 src/
│   └── 📁 main/
│       ├── 📁 java/
│       │   └── 📁 com/gestionstages/
│       │       ├── 📄 MainApplication.java     # Point d'entrée
│       │       ├── 📁 controller/              # Contrôleurs JavaFX
│       │       ├── 📁 dao/                     # Accès aux données
│       │       ├── 📁 model/                   # Modèles de données
│       │       └── 📁 util/                    # Utilitaires
│       └── 📁 resources/
│           └── 📁 fxml/                        # Fichiers FXML
└── 📁 target/                          # Fichiers compilés (généré)
\\\`

## 🐛 Dépannage

### Problèmes courants

*Erreur de connexion MySQL*
\\\`bash
# Vérifier que MySQL est démarré
sudo systemctl status mysql

# Recréer la base de données
mysql -u root -p < scripts/create-database-final.sql
\\\`

*Erreur JavaFX*
\\\`bash
# Ajouter les modules JavaFX
mvn javafx:run -Djavafx.args="--add-modules javafx.controls,javafx.fxml"
\\\`

*Erreur de compilation*
\\\`bash
# Nettoyer et recompiler
mvn clean compile
\\\`

*Fichiers FXML non trouvés*
\\\`bash
# Forcer la copie des ressources
mvn clean resources:resources compile
\\\`

## 📊 Base de données

### Tables principales

- *utilisateurs* : Comptes et authentification
- *ecoles* : Établissements partenaires  
- *stages* : Offres de stage
- *candidats* : Étudiants candidats
- *candidatures* : Candidatures aux stages
- *stagiaires* : Stagiaires acceptés

### Sauvegarde

\\\`bash
# Créer une sauvegarde
mysqldump -u root -p gestion_stages > backup_$(date +%Y%m%d).sql

# Restaurer une sauvegarde
mysql -u root -p gestion_stages < backup_20240101.sql
\\\`

## 🤝 Contribution

1. Fork le projet
2. Créer une branche feature (git checkout -b feature/nouvelle-fonctionnalite)
3. Commiter les changements (git commit -am 'Ajout nouvelle fonctionnalité')
4. Push vers la branche (git push origin feature/nouvelle-fonctionnalite)
5. Créer une Pull Request

### Standards de développement

- Respecter l'architecture MVC
- Commenter le code complexe
- Tester les nouvelles fonctionnalités
- Suivre les conventions Java

## 📈 Roadmap

- [ ] Interface web avec Spring Boot
- [ ] API REST
- [ ] Notifications par email
- [ ] Génération de rapports PDF
- [ ] Tableau de bord avec statistiques
- [ ] Module de gestion des entreprises
- [ ] Système de notation avancé

## 🏆 Auteurs

- Abdelhamid SAIDI 
- Anas RIFAK - [@anas_rifak](https://github.com/TheStriked)
- Safaa MOUFKI - [@safaa_moufki](https://github.com/TheStriked)
