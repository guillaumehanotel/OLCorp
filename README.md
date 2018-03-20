# OLCorp

- Lister et administrer les utilisateurs, les groupes et les unités organisationnelles
- Visualiser le compte d'un utilisateur qui procède à une authentification par badge sur un dispositif IOT
- Activer ou désactiver le compte d'un utilisateur dont le badge s'authentifie sur un dispositif IOT


Le projet sera divisé en quatre phases :

 - Phase 1 - Déploiement de l'architecture système
 - Phase 2 - Développement d'un service Web pour procéder à l'administration basique Active Directory
 - Phase 3 - Développement d'une application mobile pour procéder à l'administration basique Active Directory
 - Phase 4 - Mettre en place une interaction avec un module IOT


## Phase 1 : livraison de l’infrastructure

#### Objectifs
- Maîtriser un environnement Windows Server (DHCP / DNS)
- Construire et organiser un annuaire Active Directory
- Mesurer les impacts de la disponibilité du service Active Directory au sein d’une entreprise

#### Travail à faire et livrables
Installer et configurer un environnement Windows Server 2016 (avec RODC)
Déterminer une configuration réseau et une convention de nommage des serveurs optimisées
Vous procèderez à l'installation de deux serveurs Active Directory sous Windows Server 2016, l’un principal et l’autre répliqua RODC. Le serveur principal sera localisé sur le site principal, tandis que le serveur répliqua sera localisé sur un site déporté. Les rôles DHCP et DNS devront être installés et configurés.

Vous rédigerez une documentation contenant toutes les informations pertinentes décrivant l'infrastructure choisie, et notamment les noms des serveurs, leurs identifiants et mots de passe, le plan d'adressage, la convention de nommage, et les systèmes d'exploitation utilisés.

L'application Web décrite en phase 2 sera à réaliser en langage Java ; vous devrez mettre en place une infrastructure permettant d'héberger des applicatifs Java et réaliser une documentation complète de son processus d'installation. Les choix techniques liés à cette infrastructure d'hébergement Java sont laissés à votre libre choix.


## Phase 2 : livraison de l’API Java

#### Objectifs
- Comprendre les avantages de l’utilisation d’une API (REST)
- Sécuriser et protéger des données accessibles par le Web

#### Travail à faire et livrables
Réaliser une API en JAVA pour récupérer des informations pour appliquer des modifications sur Active Directory
Vous réaliser une API, en utilisant éventuellement un framework de votre choix, afin de proposer des fonctionnalités pour la future application mobile. Cette API devra, a minima, prendre en charge les fonctionnalités suivantes : 

- Lister l'ensemble des unités organisationnelles, des groupes et des utilisateurs
- Créer, modifier ou supprimer une unité organisationnelle
- Créer ou modifier un groupe en choisissant son unité organisationnelle
- Supprimer un groupe
- Créer ou modifier un utilisateur en choisissant son groupe et son unité organisationnelle
- Supprimer un utilisateur
- Ajouter un utilisateur existant à un groupe
- Enlever un utilisateur existant d'un groupe
- Ajouter/enlever un utilisateur à un groupe

## Phase 3 : livraison de l’application mobile

#### Objectifs
- Comprendre la notion d’application hybride et native
- Sécuriser les échanges entre l’API et l’application par le biais d’une authentification
- Utiliser un langage mobile (Swift, Java, AngularJS, ReactJS,…)
- Piloter des applicatifs métiers à l’aide d’une application mobile

#### Travail à faire et livrables
Réaliser une application mobile multiplateforme (iOS et Android) permettant la réalisation de toutes les fonctionnalités décrites précédemment
Vous expliquerez votre choix de procéder à un mode de développement hybride ou natif, et mettrez à disposition sur Git l'ensemble des sources du projet.


## Phase 4 : Livraison d’un module IOT

#### Objectifs
- Comprendre l'intérêt des technologies IOT
- Utiliser des technologies orientées IOT (nodeJS, lecteurs de badges MiFare, arduino)

#### Travail à faire et livrables
Réaliser une application console permettant de récupérer le profil LDAP d'un utilisateur associé à un badge MiFare
Vous développerez une application console permettant, lorsqu'un utilisateur passe son badge sur un lecteur MiFare, de récupérer et d'afficher les informations relatives à son compte, stockées dans Active Directory (nom d'affichage, adresse, numéro de téléphone,…). L'application devra être réalisée en nodeJS et échanger des données avec l'API de façon sécurisée.

L'application devra également permettre de désactiver ou de réactiver le compte d'un utilisateur après qu'il ait passé son badge sur un lecteur.
