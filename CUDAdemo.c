__global__ void dot( int *a, int *b, int *sum ) {
   __shared__ int temp[THREADS_PER_BLOCK];
   int index = threadIdx.x + blockIdx.x * blockDim.x;
   temp[threadIdx.x] = a[index] * b[index];
   
   __syncthreads();
   if( 0 == threadIdx.x ) {
       int sum_local = 0;
       for( int i= 0; i< THREADS_PER_BLOCK; i++ )
           sum_local += temp[i];
       atomicAdd( sum , sum_local );
   }
}

#define N (2048*2048)
#define THREADS_PER_BLOCK 512
int main( void ) {


    int *a, *b, *c; // tablice a, b, c w pamieci komputera
    int *dev_a, *dev_b, *dev_c; // kopie a, b, c w GPU
    intsize = N * sizeof( int); //

   //  alokacja miejsca na tablice a, b, c w pamieci komputera
    a = (int*)malloc( size );
    b = (int*)malloc( size );
    c = (int*)malloc( sizeof(int) );

    //  alokacja miejsca na kopie tablic a, b, c w pamiêci GPU
    cudaMalloc( (void**)&dev_a, size );
    cudaMalloc( (void**)&dev_b, size );
    cudaMalloc( (void**)&dev_c, sizeof(int) );

    //  wyznaczenie tablic a i b

    random_ints( a, N );
    random_ints( b, N );

    // przepisanie zawartoœci tablic a, b do tablic GPU
    cudaMemcpy( dev_a, a, size, cudaMemcpyHostToDevice);
    cudaMemcpy( dev_b, b, size, cudaMemcpyHostToDevice);

    // wywolanie jadra
    dot<<< N/THREADS_PER_BLOCK,THREADS_PER_BLOCK>>>( dev_a, dev_b, dev_c);

    // skopiowanie wyniku z urzadzenia do zmiennej c
    cudaMemcpy( c, dev_c, sizeof(int) , cudaMemcpyDeviceToHost);
    
     return 0;
}
