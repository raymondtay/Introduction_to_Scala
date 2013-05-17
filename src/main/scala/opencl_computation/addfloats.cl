__kernel void add_floats(__global const float* a, 
                         __global const float* b,
                         __global const float* out, int n) 
{
    int i = get_global_id(0); // get the thread-id from first dimension
    if (i >= n) return;

    out[i] = a[i] + b[i];
}

