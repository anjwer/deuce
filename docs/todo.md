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

à mettre dans projet test web.xml
<listener>
    <listener-class>com.deuce.listener.ApplicationListener</listener-class>
</listener>


## S5 : affichage de liste en dur
- ModelAndView 
    - map <String, object>
    - url

- dnas le controller on retourne un MAV 
    - setURL

- prefixe et suffixe dans initparam
- concatener l'url

- recup MAV -> request setAttribute 

## S6 : 
tsy mandeha vue fa mamerina JSON 
pas de request dispatcher mais retourne JSON

[ok] cote FW : on ajoute une annotation de methode @WebApi 
(attributs true/false si c dj json ou pas mais à ajouter plus tard )
[ok] sur frontservlet : tester l'exitence de cette anno sur la methode avant dispatch 
si oui = retourne JSON 
changer le content type en JSON => retourner un object depuis test et transforner cet objet en json et envoyer en print writter (different de string va en json)
sinon pas de transfo cote fw fa on change direct en json depuis dev (on obtient un string)
bloc if else 

## S7 (11 oct) :
binding de formulaire 
donnees venant de la vue vers controller (ex formulaire)
click bouton -> url -> mapper avec une methode du controller (save par exemple)
click -> front servlet -> verifier la methode si c'est save par exemple -> verifier si cette methode a des parametres 
on cree une fonction save cote developpeur, par defaut les params sont nulls

cas 1 : pas objet 
    - on match les params du formulaire avec les attributs
    - invoke 
cas 2 : objet 
    - null
  
debug 
    - conversion apiana types bdb
    - cleo : web.xml = copier aaron 
  
erreur : gestion du save 
7 bis 
- methode d'action (constroller)
binding objet -> argement direct objet Employe 

## S8
creer notre classe MapFile(filename, extension, byte[] au lieu de byte[], size, contentType)
- 
- rajouter une confition au binding (si le params est MapFile)
    - recuperer les part de la requete
    - creer une instance de MapFile 

on instancie cote FW

## S9 : session 
session de l'utilisateur dispo dans le controlleur du developpeur 
- tsy miasa saina autorisation aloha 
    - controlleur asiana anno quand on va faire authorisation 
    - gestion des roles 

*Amboarina fa tsy atao zao e*
- 1 controlleur = 1 instance (singleton)
    - mettre une condition a l'instanciation 
    - atao anaty contexte 
    - cle : controlleur valeur : instance

Classe session a nous cote FW
[ok]- attributs map String Object 
[ok] - mettre les fonctions de session dans la classe 
- fonction qui va transformer les attributs de request.getSession dans le map de Session
[ok]- mamorona map, clé id session et valeur la session (Session) à placer dans le context
[ok]- verifier si efa misy session avec ce id raha misy dia alaina sinon mamorona vaovao
[ok] - verifier si dans les parametres de la fonction cote dev hoe misy session ve 
[ok] - si oui = injecte la session avy any @ Front controller
[ok] - apres avoir injecte, il faut synchroniser les session Session vers HTTP session


- recup, ajouter, supp des variables dans les sessions en utils dans la classe
atao anaty methode d'action pour jouer avec la session
- (FW) : traitement de params (session en params) pour verifier si misy type session 
    - oui -> instance -> remplir la map de HttpSession
    - mettre a jour httpsession apres invoke 
    - objectif : pas de http session direct cote dev 

servlet context on peut appeler partout mais pas tjs dans init

## S10 : 
- methode d'action cote controlleur dev => tsy maintsy co/ authentifie vao afaka mi execute azy
- nouvelle anno Auth 
cote fw
- verifier si authetifie ou pas le mec 
- config pour verifier (web.xml = definir frontservlet, init params : nom de la variable session a verifier si connecte, 1 fois alaina donc init apetraka anaty servlet context)

## S10bis 
connecte + role specifique 
créer une annotation qui va contenir les roles 
- on dépose dnas la session le profil de l'user connecté 
- variable pour garder le role de l'user
- le dev va déposer le role de l'user connecté
  - on peut mettre dans web.xml
  - misy on prend le nom de varibale puis on vérifie dans la session
  - si role existe on compare avec le rolde dans l'annotation 
