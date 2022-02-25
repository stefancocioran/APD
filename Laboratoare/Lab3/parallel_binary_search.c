#include <math.h>
#include <pthread.h>
#include <stdio.h>
#include <stdlib.h>

#define MIN(x, y) ((x < y) ? x : y)

#define INSIDE -1   // daca numarul cautat este in interiorul intervalului
#define OUTSIDE -2  // daca numarul cautat este in afara intervalului

struct my_arg {
    int id;
    int N;
    int P;
    int number;
    int *left;
    int *right;
    int *keep_running;
    int *v;
    int *found;
    int *not_found;
};

pthread_barrier_t barrier;

/*
void binary_search() {
        while (keep_running) {
                int mid = left + (right - left) / 2;
                if (left > right) {
                        printf("Number not found\n");
                        break;
                }

                if (v[mid] == number) {
                        keep_running = 0;
                        printf("Number found at position %d\n", mid);
                } else if (v[mid] > number) {
                        left = mid + 1;
                } else {
                        right = mid - 1;
                }
        }
}
*/

void *f(void *arg) {
    struct my_arg *data = (struct my_arg *)arg;

    while (*data->keep_running) {
        int size = *data->right - *data->left;
        // TODO: implementati parallel binary search

        int start = *data->left + data->id * (double)size / data->P;
        int end =
            MIN((data->id + 1) * (double)size / data->P, size) + *data->left;

        if (data->number == data->v[start]) {
            data->found[data->id] = start;
        } else if (data->number == data->v[end - 1]) {
            data->found[data->id] = end;
        } else if (data->number > data->v[start] &&
                   data->number < data->v[end - 1]) {
            data->found[data->id] = INSIDE;
        } else {
            data->found[data->id] = OUTSIDE;
        }

        pthread_barrier_wait(&barrier);

        if (data->id == 0) {
            int found = 0;

            for (int i = 0; i < data->P; i++) {
                if (data->found[i] != INSIDE && data->found[i] != OUTSIDE) {
                    *data->keep_running = 0;
                    found = 1;
                    printf("Number found at position %d\n", data->found[i]);
                    *data->not_found = 1;
                    break;
                } else if (data->found[i] == INSIDE) {
                    *data->right = MIN((i + 1) * (double)size / data->P, size) +
                                   *data->left;
                    *data->left = *data->left + i * (double)size / data->P;
                    found = 1;
                    break;
                }
            }

            if (found == 0) {
                *data->keep_running = 0;
            }
        }

        pthread_barrier_wait(&barrier);
    }

    if (*data->not_found == 0) {
        *data->not_found = 1;
        printf("Number not found\n");
    }

    return NULL;
}

void display_vector(int *v, int size) {
    int i;

    for (i = 0; i < size; i++) {
        printf("%d ", v[i]);
    }

    printf("\n");
}

int main(int argc, char *argv[]) {
    int r, N, P, number, keep_running, left, right;
    int *v;
    int *found, *not_found;
    void *status;
    pthread_t *threads;
    struct my_arg *arguments;

    if (argc < 4) {
        printf("Usage:\n\t./ex N P number\n");
        return 1;
    }

    N = atoi(argv[1]);
    P = atoi(argv[2]);
    number = atoi(argv[3]);

    pthread_barrier_init(&barrier, NULL, P);

    keep_running = 1;
    left = 0;
    right = N;

    v = (int *)malloc(N * sizeof(int));
    threads = (pthread_t *)malloc(P * sizeof(pthread_t));
    arguments = (struct my_arg *)malloc(P * sizeof(struct my_arg));
    found = (int *)malloc(P * sizeof(int));
    not_found = (int *)malloc(sizeof(int));

    for (int i = 0; i < N; i++) {
        v[i] = i * 2;
    }

    // display_vector(v, N);

    for (int i = 0; i < P; i++) {
        arguments[i].id = i;
        arguments[i].N = N;
        arguments[i].P = P;
        arguments[i].number = number;
        arguments[i].left = &left;
        arguments[i].right = &right;
        arguments[i].keep_running = &keep_running;
        arguments[i].v = v;
        arguments[i].found = found;
        arguments[i].not_found = not_found;

        r = pthread_create(&threads[i], NULL, f, &arguments[i]);

        if (r) {
            printf("Eroare la crearea thread-ului %d\n", i);
            exit(-1);
        }
    }

    for (int i = 0; i < P; i++) {
        r = pthread_join(threads[i], &status);

        if (r) {
            printf("Eroare la asteptarea thread-ului %d\n", i);
            exit(-1);
        }
    }

    free(v);
    free(threads);
    free(arguments);

    return 0;
}
