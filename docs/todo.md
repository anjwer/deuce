# Deuce : my framework fait maison 

## S0 : front controller

[ok]- creation du projet de test (cleo)
[ok]- implementation des fichiers d'initialisation 
[ok]- creation du fichier frontController 
[ok]- void doGet
[ok]- void doPost
[ok]- transformer en .jar 
[ok]- appeler dans cleo

[ok]- apprendre les annotations 
    - creer ses annotations
    - classe, methode, attribut 


## S1 : annotation + controller 

1 er appel de FS - parcourir les classes, test si c'est controller ou pas (package de controllers)
                    si oui on ajoute dans la liste 
                    on print apres 

FW
[ok]- creer une classe annotation.Controller 
[ok]- ajouter attribut List<String> controllers dans FC
[ok]- creation classe Utilitaire
[ok]- methode qui prend le package + nom annotation + au niveau classe/attribut ou methode en param
[ok]    - verifier si l'annotation est la 
[ok]- scan 
[ok]- methode init dans FC
[ok]    - initialise la liste de controllers

Projet TEST
[wip]- web.xml 
    - package des controllers dans appli de test (FC appelle cet attribut) 

- listener dans application java
    - a declarer dans web.xml du client 

[ok]- creer des classes controllers avec l'annotation