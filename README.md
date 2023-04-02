# Fonctionnalités du projet

Nous avons implémenté un ensemble de fonctionnalité pour le bon fonctionnement du projet :
- Tout d'abord, nous avons la recherche séquentielle pour voir le fonctionnement de la recherche séquentielle et voir les feuilles en fonction de celle-ci
- Nous avons un bouton "générer 10k lignes" qui permet de générer un fichier comprenant 10 000 lignes dont par ligne, on a un ensemble d'information : le numéro de sécurité sociale, le prénom puis le nom. 
- Nous avons implémenté le système de pointeur, avec une classe qui est nommée "Ligne" qui comprend le numéro de sécurité : le numéro dans l'arbre qui est le pointeur visible sur l'arbre puis les différentes informations complémentaires (prénom + nom)
- Enfin, il est possible d'effectuer une série de statistique pour connaître la performance entre plusieurs algorithmes de recherche. De manière globale, on remarque que la recherche par indexation est beaucoup plus rapide que par la recherche séquentielle (qui elle parcourt tout l'arbre et toutes les feuilles).

# Information Projet Java

Il s'agit d'une implémentation en Java d'un arbre balancé.

Plus précisément il s'agit d'une classe de gestion d'un BTree+ , ce qui signifie que TOUTES les données sont stockées dans les feuilles de l'arbre, les noeuds intermédiaires permettant uniquement de trier les données.

Les arbres BTree et BTree+ sont fréquemment utilisés, en particulier dans le stockage de données car ils permettent d'avoir toujours un temps de recherche maitrisable.

On les retrouve principalement dans les systèmes de fichier (NTFS par exemple) et dans les SGBD (Oracle...)

Ici sont gérés :
- la création d'un arbre de n'importe quel ordre
- l'ajout de données
- la recherche d'une valeur dans l'arbre

La classe de traitement est générique, c'est à dire que l'on peut stocker n'importe quel type de données dans l'arbre, mais comme il s'agit d'un tri, il faut définir et préciser une procédure de comparaison de ces données.

Grâce à la serialization, on peut aussi sauvegarder et charger un arbre.
