#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <math.h>

#define MIN(x,y) ((x < y) ? x : y)

int N;
int P;
int *v;
int *vQSort;

int sorted;
pthread_barrier_t barrier;

void compare_vectors(int *a, int *b) {
	int i;

	for (i = 0; i < N; i++) {
		if (a[i] != b[i]) {
			printf("Sortare incorecta\n");
			return;
		}
	}

	printf("Sortare corecta\n");
}

void display_vector(int *v) {
	int i;
	int display_width = 2 + log10(N);

	for (i = 0; i < N; i++) {
		printf("%*i", display_width, v[i]);
	}

	printf("\n");
}

int cmp(const void *a, const void *b) {
	int A = *(int*)a;
	int B = *(int*)b;
	return A - B;
}

void get_args(int argc, char **argv)
{
	if(argc < 3) {
		printf("Numar insuficient de parametri: ./oets N P\n");
		exit(1);
	}

	N = atoi(argv[1]);
	P = atoi(argv[2]);
}

void init()
{
	int i;
	v = malloc(sizeof(int) * N);
	vQSort = malloc(sizeof(int) * N);

	if (v == NULL || vQSort == NULL) {
		printf("Eroare la malloc!");
		exit(1);
	}

	srand(42);

	for (i = 0; i < N; i++)
		v[i] = rand() % N;
}

void print()
{
	printf("v:\n");
	display_vector(v);
	printf("vQSort:\n");
	display_vector(vQSort);
	compare_vectors(v, vQSort);
}

void *thread_function(void *arg)
{
	int thread_id = *(int *)arg;

	// implementati aici OETS paralel

	int i, aux;
	int start = thread_id * ceil((double)N/P);
	int end = MIN ((thread_id + 1) * ceil((double)N/P), N);
	 
	int start_odd, start_even;
	int end_odd, end_even;

	
	if (start % 2 == 0) {         
		start_even = start;
		start_odd = start + 1;
	} else {
		start_even = start + 1;
		start_odd = start;
	}

	if (end % 2 == 0) {
		end_even = end;
		end_odd = end + 1;
	} else {
		end_even = end + 1;
		end_odd = end;
	}

	end_even = MIN(end_even, N - 1);
	end_odd = MIN(end_odd, N - 1);

	sorted = 0;
	int sorted_thread = 0;

	while (!sorted) {
		pthread_barrier_wait(&barrier);

		sorted = 1;
		sorted_thread = 1;

		for (i = start_even; i < end_even; i += 2) {
			if (v[i] > v[i + 1]) {
				aux = v[i];
				v[i] = v[i + 1];
				v[i + 1] = aux;
				sorted_thread = 0;
			}
		}

		pthread_barrier_wait(&barrier);

		for (i = start_odd; i < end_odd; i += 2) {
			if (v[i] > v[i + 1]) {
				aux = v[i];
				v[i] = v[i + 1];
				v[i + 1] = aux;
				sorted_thread = 0;
			}
		}

		if (!sorted_thread) {
			sorted = 0;
		}

		pthread_barrier_wait(&barrier);
	}

	pthread_exit(NULL);
}


int main(int argc, char *argv[])
{
	get_args(argc, argv);
	init();

	int i;
	// int aux;
	pthread_t tid[P];
	int thread_id[P];

	pthread_barrier_init(&barrier, NULL, P);

	// se sorteaza vectorul etalon
	for (i = 0; i < N; i++)
		vQSort[i] = v[i];
	qsort(vQSort, N, sizeof(int), cmp);

	// se creeaza thread-urile
	for (i = 0; i < P; i++) {
		thread_id[i] = i;
		pthread_create(&tid[i], NULL, thread_function, &thread_id[i]);
	}

	// se asteapta thread-urile
	for (i = 0; i < P; i++) {
		pthread_join(tid[i], NULL);
	}

	// bubble sort clasic - trebuie transformat in OETS si paralelizat

/*		
	int sorted = 0;
	while (!sorted) {
		sorted = 1;

		for (i = 0; i < N-1; i++) {
			if(v[i] > v[i + 1]) {
				aux = v[i];
				v[i] = v[i + 1];
				v[i + 1] = aux;
				sorted = 0;
			}
		}
	} 
*/
	
	if (pthread_barrier_destroy(&barrier) != 0) {
		printf("Eroare la dezalocarea barierei\n");
		exit(-1);
	}

	// se afiseaza vectorul etalon
	// se afiseaza vectorul curent
	// se compara cele doua
	print();

	free(v);
	free(vQSort);

	return 0;
}
