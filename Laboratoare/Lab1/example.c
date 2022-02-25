#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

#define NUM_THREADS 10

/*
   Task 2
   #define NUM_THREADS sysconf(_SC_NPROCESSORS_CONF)
*/

void* f(void* arg) {
    long id = *(long*)arg;

    printf("Hello World din thread-ul %ld!\n", id);

    /*
    Task 3
        for (int i = 0; i < 100; i++) {
            printf("%d Hello World din thread-ul %ld!\n", i, id);
        }
    */

    pthread_exit(NULL);
}

void* f1(void* arg) {
    long id = *(long*)arg;

    printf("First Hello World din thread-ul %ld!\n", id);

    pthread_exit(NULL);
}

void* f2(void* arg) {
    long id = *(long*)arg;

    printf("Second Hello World din thread-ul %ld!\n", id);

    pthread_exit(NULL);
}

int main(int argc, char* argv[]) {
    pthread_t threads[NUM_THREADS];
    int r;
    long id;
    void* status;
    long ids[NUM_THREADS];

    /*
    Task 4
        ids[0] = 0; ids[1] = 1;
        pthread_create(&threads[0], NULL, f1, &ids[0]);
        pthread_create(&threads[1], NULL, f2, &ids[1]);
    */

    for (id = 0; id < NUM_THREADS; id++) {
        ids[id] = id;
        r = pthread_create(&threads[id], NULL, f, &ids[id]);

        if (r) {
            printf("Eroare la crearea thread-ului %ld\n", id);
            exit(-1);
        }
    }

    for (id = 0; id < NUM_THREADS; id++) {
        r = pthread_join(threads[id], &status);

        if (r) {
            printf("Eroare la asteptarea thread-ului %ld\n", id);
            exit(-1);
        }
    }

    pthread_exit(NULL);
}