#include "mpi.h"
#include <stdio.h>
#include <stdlib.h>

int main (int argc, char *argv[])
{
    int numtasks, rank;

    MPI_Init(&argc, &argv);
    MPI_Comm_size(MPI_COMM_WORLD, &numtasks);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);

    int recv_num, flag;
    MPI_Request r;
    MPI_Status status;

    if (rank == 0) {
        int rand_num = 0;
        MPI_Isend(&rand_num, 1, MPI_INT, rank + 1, 0, MPI_COMM_WORLD, &r);
        MPI_Test(&r, &flag, &status);
        if (!flag) {
            MPI_Wait(&r, &status);
        }

        MPI_Irecv(&recv_num, 1, MPI_INT, numtasks - 1, 0, MPI_COMM_WORLD, &r);
        MPI_Test(&r, &flag, &status);
 
        if (!flag) {
            MPI_Wait(&r, &status);
        }

        printf("Process [%d] with recv-value %d\n", rank, recv_num);
    } else if (rank == numtasks - 1) {
        MPI_Irecv(&recv_num, 1, MPI_INT, rank - 1, 0, MPI_COMM_WORLD, &r);
        MPI_Test(&r, &flag, &status);
 
        if (!flag) {
            MPI_Wait(&r, &status);
        }

        printf("Process [%d] with recv-value %d\n", rank, recv_num);
        recv_num += 2;
        MPI_Isend(&recv_num, 1, MPI_INT, 0, 0, MPI_COMM_WORLD, &r);
        MPI_Test(&r, &flag, &status);
        if (!flag) {
            MPI_Wait(&r, &status);
        }
    } else {
        MPI_Irecv(&recv_num, 1, MPI_INT, rank - 1, 0, MPI_COMM_WORLD, &r);
        MPI_Test(&r, &flag, &status);
 
        if (!flag) {
            MPI_Wait(&r, &status);
        }

        printf("Process [%d] with recv-value %d\n", rank, recv_num);
        recv_num += 2;
        MPI_Isend(&recv_num, 1, MPI_INT, rank + 1, 0, MPI_COMM_WORLD, &r);
        MPI_Test(&r, &flag, &status);
         if (!flag) {
            MPI_Wait(&r, &status);
        }
    }

    MPI_Finalize();
}

