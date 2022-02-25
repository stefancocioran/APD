#include "mpi.h"
#include <stdio.h>
#include <stdlib.h>

#define MULTI 5 // chunk dimension
#define ROOT 0

int main (int argc, char *argv[])
{
    int  numtasks, rank, len;
    char hostname[MPI_MAX_PROCESSOR_NAME];

    MPI_Init(&argc, &argv);
    MPI_Comm_size(MPI_COMM_WORLD, &numtasks);
    MPI_Comm_rank(MPI_COMM_WORLD,&rank);
    MPI_Get_processor_name(hostname, &len);

    int num_elements = MULTI * numtasks; // total elements
    int *v_send = NULL; // full vector
    int *v_recv = (int *)malloc(MULTI * sizeof(int)); // partial vector
    int* result_arr;

    // ROOT process generates the values for the full vector.
    // Scatter the vector to all processes.
    if (rank == ROOT) {
        v_send = malloc (MULTI * numtasks * sizeof(int));
        for (int i = 0; i < numtasks * MULTI; ++i) {
            v_send[i] = 0;
        }
    }

    v_recv = malloc (MULTI * sizeof(int));
    MPI_Scatter(v_send, MULTI, MPI_INT, v_recv, MULTI, MPI_INT, ROOT, MPI_COMM_WORLD);

    /*
     * Prints the values received after scatter.
     * NOTE: If MULTI changed, also change this line.
     */
    printf("BEFORE: Process [%d]: have elements %d %d %d %d %d.\n", rank, v_recv[0],
            v_recv[1], v_recv[2], v_recv[3], v_recv[4]);

    // Each process increments the values of the partial vector received.
    // Gathers the values from all the processes.
    // The ROOT process prints the elements received.

    for (int i = 0; i < MULTI; ++i) {
        v_recv[i]+= rank;
    }

    printf("AFTER: Process [%d]: have elements %d %d %d %d %d.\n", rank, v_recv[0],
            v_recv[1], v_recv[2], v_recv[3], v_recv[4]);

    if (rank == ROOT) {
        result_arr = malloc (MULTI * numtasks * sizeof(int));
    }

    MPI_Gather(v_recv, MULTI, MPI_INT, result_arr, MULTI, MPI_INT, ROOT, MPI_COMM_WORLD);

    if (rank == ROOT) {
        printf("\nThe ROOT process prints the elements received\n");
        for (int i = 0; i < MULTI * numtasks; i++) {
            printf("%d ", result_arr[i]);
        }
        printf("\n\n");
    }

    if (rank == ROOT) {
        free(v_send);
        free(result_arr);
    }

    free(v_recv);

    MPI_Finalize();

}

