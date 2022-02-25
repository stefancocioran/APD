#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>

#define NUM_THREADS 2

int a = 0;
pthread_mutex_t lock;

void *f(void *arg) {
    if (pthread_mutex_lock(&lock) != 0) {
        printf("Eroare la realizarea lock-ului\n");
        exit(-1);
    }

    a += 2;

    if (pthread_mutex_unlock(&lock) != 0) {
        printf("Eroare la deschiderea lock-ului\n");
        exit(-1);
    }

    pthread_exit(NULL);
}

int main(int argc, char *argv[]) {
    int i, r;
    void *status;
    pthread_t threads[NUM_THREADS];
    int arguments[NUM_THREADS];

    if (pthread_mutex_init(&lock, NULL) != 0) {
        printf("Eroare la initializarea mutex-ului\n");
        return 1;
    }

    for (i = 0; i < NUM_THREADS; i++) {
        arguments[i] = i;
        r = pthread_create(&threads[i], NULL, f, &arguments[i]);

        if (r) {
            printf("Eroare la crearea thread-ului %d\n", i);
            exit(-1);
        }
    }

    for (i = 0; i < NUM_THREADS; i++) {
        r = pthread_join(threads[i], &status);

        if (r) {
            printf("Eroare la asteptarea thread-ului %d\n", i);
            exit(-1);
        }
    }

    if (pthread_mutex_destroy(&lock) != 0) {
        printf("Eroare la dezalocarea mutex-ului\n");
        exit(-1);
    }

    printf("a = %d\n", a);

    return 0;
}
