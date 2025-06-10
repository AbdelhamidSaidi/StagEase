# 🎓 Système de Gestion des Stages (StagEASE)

Une application JavaFX complète pour la gestion des stages en entreprise, développée avec Maven et MySQL.

![Java](https://img.shields.io/badge/Java-17-orange)
![JavaFX](https://img.shields.io/badge/JavaFX-21-blue)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)
![Maven](https://img.shields.io/badge/Maven-3.6+-green)
![License](https://img.shields.io/badge/License-MIT-yellow)

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

### Prérequis

Assurez-vous d'avoir installé :

```bash
# Java 17 ou supérieur
java -version

# Maven 3.6 ou supérieur
mvn -version

# MySQL 8.0 ou supérieur
mysql --version
