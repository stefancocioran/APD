# Tema 3 - APD <span style="font-size:small;">&copy; Cocioran Stefan, 331 CA</span> 

### <b>main</b>
-> workers - mapeaza workerii la clusterele de care apartin  
-> N - dimensiunea array-ului generat de procesul ROOT  
-> v - array-ul generat de procesul ROOT  

Daca rank-ul procesului corespunde unui coordonator de cluster:  
#### - Stabilirea topologiei -
* se citesc rank-urile proceselor worker pentru fiecare cluster
* fiecare worker este instiintati care proces este coordonatorul sau
* se apeleaza metoda <b>find_topology</b>
#### - Realizarea calculelor -
-> sol - solutia partiala a fiecarui cluster  
-> sizes - array care contine numarul de iteratii pentru fiecare worker 
* Procesul ROOT:
    * genereaza array-ul de N elemente si il trimite celorlalti doi coordonatori
    * coordonatorul de cluster trimite tuturor proceselor worker array-ul generat  
    de procesul ROOT si dimensiunea acestuia (<b>inform_workers</b>)
    * primeste numarul de workeri ai celorlalti doi coordonatori
    * primeste solutiile partiale ale acestora (<b>receive_partial_sol_from_cluster</b>)  
    si le introduce in solutia sa locala (care este si cea finala)
    * primeste rezultatele partiale de la procesele sale worker(<b>get_workers_result</b>)  
    si le introduce in solutia sa locala
    * afiseaza rezultatul final

* Celelalte doua procese coordonator:
    * coordonatorul de cluster trimite tuturor proceselor worker array-ul generat  
    de procesul ROOT si dimensiunea acestuia (<b>inform_workers</b>)
    * primeste rezultatele partiale de la procesele sale worker(<b>get_workers_result</b>)  
    si le introduce in solutia sa locala
    * dupa ce au fost primite toate rezultatele, solutia partiala este trimisa   
    cate procesul ROOT, care va forma solutia finala

Daca rank-ul procesului corespunde unui worker:
* primeste rank-ul coordonatorului sau
* primeste topologia de la coordonator si o afiseaza
* primeste array-ul generat de procesul ROOT si dimensiunea acestuia
* in functie de rank-ul pe care il are, se stabileste portiunea de array pe care   
o prelucreaza
* fiecare element nou calculat va fi introdus intr-un rezultat partial, local
* cand termina calculul, trimite coordonatorului rank-ul sau, dimensiunea   
rezultatului partial si elementele sale 


### <b>find_topology</b>
* fiecare coordonator de cluster trimite celorlalti doi numarul de workeri ai   
sai si rank-ul acestor procese
* cand toti coordonatorii au aflat topologia (s-a completat map-ul "workers"),  
o afiseaza si este trimisa mai departe fiecarui proces worker


