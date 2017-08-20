# Bio-Informatique

Recherche de signaux de codes circulaires dans les gènes.

`Main.java` contient une fonction de test capable de travailler sur un unique `.gb`.

## notes :
La librairie qui sert à exporter les fichiers excels gère assez mal la mémoire : il est possible que l'écriture des .xlsx échoue si on prend imax suffisamment grand (quelques milliers). 
La solution que j'utilise pour exporter les .xlsx des trinucléotides (steaming API) peut être aisément appliqée à tout les xlsx si besoin est mais elle ne permet pas d'adapter la largeur des colonnes à leur contenus.
