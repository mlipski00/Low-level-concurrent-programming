/*****************  PLIK: posix_pi.c ***********************************
*
*   Kompilacja
*         gcc -pthread posix_pi.c -lm
*
*  Uruchomienie z pomiarem czasu wykonania
*         timex ./a.out p N
*    lub
*         time ./a.out p N
*
*    gdzie:
*         p - liczba watkow (tutaj przyjeto < 25)
*         N - liczba podprzedzialow (przedzialu [0,1])
*             przy liczeniu calki (im wieksze N, tym wieksza dokladnosc)
*
*  Na przyklad:
*         timex ./a.out 1 2000000
*         timex ./a.out 6 3000000
*         timex ./a.out 5 1000000
*
***********************************************************************/

#include <pthread.h>
#include <stdlib.h>
#include <stdio.h>
#include <math.h>
#define f(x) ((float)(4.0/(1.0+x*x)))
#define pi ((float)(4.0*atan(1.0)))

/* Definicja zmiennych globalnych oraz zamka */

double    sumaG;
int p, N;
pthread_t callThd[25];
pthread_mutex_t zamek;

void* czesc_cal(void* i) {
    int  j, start, end;
    double sumaL, argm, wf;

    start = *(int*) i; end   = N;
    sumaL = 0;
      /*
       * Watek zaczyna obliczenia od podprzedzialu
       * i-tego z N przedzialu [0,1], a potem
       * liczy kolejne podprzedzialy modulo p az do N-tego.
       */
    for (j = start; j < end; j += p) {
        sumaL += f(((float)j - 0.5) / N);
    }
              /*
               * Dodanie wlasnego wyniku do sumy lacznej
               * przyblizajacej calke
               *
               */
    pthread_mutex_lock (&zamek);
    sumaG += sumaL/N;
    pthread_mutex_unlock (&zamek);
}

int main (int argc, char* argv[]) {
    int i;
    int status;

    p = atoi(argv[1]);
    N = atoi(argv[2]);

    sumaG=0;

    pthread_mutex_init(&zamek, NULL);

    for(i = 0; i < p; i++) {
           /*
           * Kazdy watek policzy inna sume (czesc calki)
           * zaczynajac od podprzedzialu i -tego (z N)
           */
        pthread_create(&callThd[i], NULL, czesc_cal, (void*) &i);
    }

/* Oczekiwanie na kolejne watki */

    for(i = 0; i < p; i++) {
        pthread_join(callThd[i], (void **)&status);
    }
/* Po polaczeniu, jeszcze nalezy wypisac wynik na ekranie*/

    printf ("\n Suma =  %20.18lf, pi dokladne =% 20.18f\n", sumaG, pi);
    return 0;
}

