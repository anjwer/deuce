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
[ok]- web.xml 
    - package des controllers dans appli de test (FC appelle cet attribut) 

- listener dans application java
    - a declarer dans web.xml du client 

[ok]- creer des classes controllers avec l'annotation

## S2 : url mapping

FW
[ok] - annotation urlmapping 
[ok]    - s applique aux methodes
[ok]    - valeur = string qui contient url

[ok]- quand j'appelle l'url j'affiche l url + classe + methode 
[ok]- si je trouve pas j'affiche toutes les urls + classes + methodes 

[ok] classe mapping
    - classe
    - methode 

[ok] Fc : creation du map <url, mapping>

init
[ok]    - url => classe associee + methode associee 

    [ok] - par classe
        - verifier si il y a un urlMapping
        - si oui le faire entrer dans le map
            - recuperer sa classe + sa methode en utilisant le type 
            - afficher le mapping 
        - si non
            - afficher tout

[ok] TEST 
- annoter une methode avec l'urlmapping  

## S3 : unicite url

FW:
[ok] - classe UrlMethod
        - url
        - methode 

    - surdefinir methode equals 

    - surdefinir fonction hashcode 

[ok] - urlmapping
    - ajouter attribut methode 

[ok] FC :
    - changer la cle de la map String -> UrlMethod 

[ok]Utilitaire :
    - si la cle existe deja -> lever une exception 
  
cleo :
changer le url mapping en url method pour test

## S3 Bis : application de S3
[ok]- invoquer la fonction si l'url est connu 
test : essayer les mauvaises url pour lever une exception 

## S4 : context listener
- a prendre chez quelqu'un

## S5 : affichage de liste en dur
- ModelAndView 
    - map <String, object>
    - url

- dnas le controller on retourne un MAV 
    - setURL

- prefixe et suffixe dans initparam
- concatener l'url

- recup MAV -> request setAttribute 