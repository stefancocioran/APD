#include "mpi.h"
#include <stdio.h>
#include <stdlib.h>

#define GROUP_SIZE 4

int main(int argc, char *argv[])
{
        int old_size, new_size;
        int old_rank, new_rank;
        int recv_rank;
        MPI_Comm custom_group;
        MPI_Status status;

        MPI_Init(&argc, &argv);
        MPI_Comm_size(MPI_COMM_WORLD, &old_size); // Total number of processes.
        MPI_Comm_rank(MPI_COMM_WORLD, &old_rank); // The current process ID / Rank.

        // Split the MPI_COMM_WORLD in small groups.
        new_size = old_rank/GROUP_SIZE;
        new_rank = old_rank % GROUP_SIZE;
        MPI_Comm_split(MPI_COMM_WORLD, new_size, new_rank, &custom_group);

        MPI_Comm_size(custom_group, &new_size);
        MPI_Comm_rank(custom_group, &new_rank);
        
        printf("Rank [%d] / size [%d] in MPI_COMM_WORLD and rank [%d] / size [%d] in custom group.\n",
                old_rank, old_size, new_rank, new_size);

        if (new_rank == 0) {
                // First process starts the circle.
                MPI_Send(&new_rank, 1, MPI_INT, 1, 0, custom_group);
                // Receive the rank.
                MPI_Status status;
                MPI_Recv(&recv_rank, 1, MPI_INT, new_size - 1, 0, custom_group, &status);
                printf("Process [%d] from group [%d] received [%d].\n", new_rank,
                        new_size, recv_rank);
        } else if (new_rank == new_size - 1) {
                // Last process close the circle.
                // Receive the rank.
                MPI_Status status;
                MPI_Recv(&recv_rank, 1, MPI_INT, new_rank - 1, 0, custom_group, &status);
                printf("Process [%d] from group [%d] received [%d].\n", new_rank,
                        new_size, recv_rank);
                // Sends the number to the first process.
                recv_rank++;
                MPI_Send(&recv_rank, 1, MPI_INT, 0, 0, custom_group);

        } else {
                // Middle process.
                // Receive the rank.
                MPI_Status status;        
                MPI_Recv(&recv_rank, 1, MPI_INT, new_rank - 1, 0, custom_group, &status);
                printf("Process [%d] from group [%d] received [%d].\n", new_rank,
                        new_size, recv_rank);
                // Sends the number to the next process.
                recv_rank++;
                MPI_Send(&recv_rank, 1, MPI_INT, old_rank + 1, 0, custom_group);
        }

        MPI_Finalize();
}
