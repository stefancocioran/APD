#include "mpi.h"
#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <stddef.h>

typedef struct {
    int size;
    int arr[1000];
} queue;

int main (int argc, char *argv[]) {
    int numtasks, rank;

    queue q;
    MPI_Datatype mpi_queue, oldtypes[2];
    int blockcounts[2];
    MPI_Aint offsets[2];
    MPI_Status status;

    MPI_Init(&argc, &argv);
    MPI_Comm_size(MPI_COMM_WORLD, &numtasks);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);

    MPI_Request r;
    int flag;

    offsets[0] = offsetof(queue, size);
    oldtypes[0] = MPI_INT;
    blockcounts[0] = 1;

    offsets[1] = offsetof(queue, arr);
    oldtypes[1] = MPI_INT;
    blockcounts[1] = 1000;

    MPI_Type_create_struct(2, blockcounts, offsets, oldtypes, &mpi_queue);
    MPI_Type_commit(&mpi_queue);

    srand(time(NULL));
 
    // First process starts the circle.
    if (rank == 0) {
        int random_num = rank << 1;
        q.size = 0;
        q.arr[q.size++] = random_num;

        MPI_Isend(&q, 1, mpi_queue, rank + 1, 0, MPI_COMM_WORLD, &r);
        MPI_Test(&r, &flag, &status);
        if (!flag) {
            MPI_Wait(&r, &status);
        }

        MPI_Irecv(&q, 1, mpi_queue, numtasks - 1, 0, MPI_COMM_WORLD, &r);
        MPI_Test(&r, &flag, &status);
 
        if (!flag) {
            MPI_Wait(&r, &status);
        }
    } else if (rank == numtasks - 1) {
        int random_num = rank << 1;

        MPI_Irecv(&q, 1, mpi_queue, rank - 1, 0, MPI_COMM_WORLD, &r);
        MPI_Test(&r, &flag, &status);
 
        if (!flag) {
            MPI_Wait(&r, &status);
        }
        q.arr[q.size++] = random_num;
        MPI_Isend(&q, 1, mpi_queue, 0, 0, MPI_COMM_WORLD, &r);
        MPI_Test(&r, &flag, &status);
        if (!flag) {
            MPI_Wait(&r, &status);
        }
    } else {
        int random_num = rank << 1;
        MPI_Irecv(&q, 1, mpi_queue, rank - 1, 0, MPI_COMM_WORLD, &r);
        MPI_Test(&r, &flag, &status);
 
        if (!flag) {
            MPI_Wait(&r, &status);
        }

        q.arr[q.size++] = random_num;

        MPI_Isend(&q, 1, mpi_queue, rank + 1, 0, MPI_COMM_WORLD, &r);
        MPI_Test(&r, &flag, &status);
        if (!flag) {
            MPI_Wait(&r, &status);
        }
    }

    if (rank == 0) {
        printf("Queue is ");
        for (int i = 0; i < q.size; i++) {
            printf("%d ", q.arr[i]);
        }
         printf("\n");
    }
    
    MPI_Type_free(&mpi_queue);
    MPI_Finalize();
}