#include<mpi.h>
#include<stdio.h>
#include<stdlib.h>
#include<math.h>

#define N 20
#define MASTER 0

void compareVectors(int * a, int * b) {
	// DO NOT MODIFY
	int i;
	for(i = 0; i < N; i++) {
		if(a[i]!=b[i]) {
			printf("Sorted incorrectly\n");
			return;
		}
	}
	printf("Sorted correctly\n");
}

void displayVector(int * v) {
	// DO NOT MODIFY
	int i;
	for(i = 0; i < N; i++) {
		printf("%i ", v[i]);
	}
	printf("\n");
}

int cmp(const void *a, const void *b) {
	// DO NOT MODIFY
	int A = *(int*)a;
	int B = *(int*)b;
	return A-B;
}
 
int main(int argc, char * argv[]) {
	int rank, i, j, aux;
	int nProcesses;
	MPI_Init(&argc, &argv);
	int pos[N];
	int sorted = 0;
	int *v = (int*)malloc(sizeof(int)*N);
	int *sol = (int*)calloc(N, sizeof(int));
	int *vQSort = (int*)malloc(sizeof(int)*N);

	for (i = 0; i < N; i++)
		pos[i] = 0;

	MPI_Comm_rank(MPI_COMM_WORLD, &rank);
	MPI_Comm_size(MPI_COMM_WORLD, &nProcesses);
	printf("Hello from %i/%i\n", rank, nProcesses);

    if (rank == MASTER) {
        // generate random vector
		for (i = 0; i < N; ++i) {
			v[i] = N - i;
		}
    }

	int dim_proc = N / nProcesses;

    for (i = 0; i < N; i++) {
        pos[i] = 0;
    }

    // send the vector to all processes
   	MPI_Bcast(v, N, MPI_INT, MASTER, MPI_COMM_WORLD);

	if(rank == 0) {
		// DO NOT MODIFY
		displayVector(v);

		// make copy to check it against qsort
		// DO NOT MODIFY
		for(i = 0; i < N; i++)
			vQSort[i] = v[i];
		qsort(vQSort, N, sizeof(int), cmp);

		// sort the vector v
		/*while(!sorted) {
			sorted = 1;
			for(i = 0; i < N-1; i++) {
				if(v[i] > v[i + 1]) {
					aux = v[i];
					v[i] = v[i + 1];
					v[i + 1] = aux;
					sorted = 0;
				}
			}
		}*/

		for(int i = 0; i < dim_proc; i++) {
			for(int j = 0; j < N; j++) {
				if (v[rank * dim_proc + i] > v[j]) {
					pos[rank * dim_proc + i]++;
				}
			}
		}

		for(int j = 0; j < N; j++) {
			sol[pos[j]] = v[j];
		}	

        // recv the new pozitions
       	for(i = 0; i < nProcesses - 1; i++){
			MPI_Status status;
		 	MPI_Recv(&pos, N, MPI_INT, MPI_ANY_SOURCE, 0, MPI_COMM_WORLD, &status);

			for(int j = 0; j < N; j++) {
				if (pos[j] != 0) {
					sol[pos[j]] = v[j];
				}
			}
		}
		displayVector(sol);
		compareVectors(sol, vQSort);
	} else {
		
        // compute the positions
        // send the new positions to process MASTER
		for(int i = 0; i < dim_proc; i++) {
			for(int j = 0; j < N; j++) {
				if (v[rank * dim_proc + i] > v[j]) {
					pos[rank * dim_proc + i]++;
				}
			}	
		}
		
		MPI_Send(&pos, N, MPI_INT, MASTER, 0, MPI_COMM_WORLD);
	}

	MPI_Finalize();
	return 0;
}
