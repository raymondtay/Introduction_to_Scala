/*
class Bakery implements Lock {
    boolean [] flag;
    Label[] label;
    public Bakery(int n ) {
        flag = new boolean[n];
        label = new Label[n];
        for(int i = 0; i < n ; ++i) {
            flag[i] = false;
            label[i] = 0;
        }
    }
    public void lock() {
        int i = ThreadID.get();
        flag[i] = true;
        label[i] = max(label[0], ..., label[n-1]) + 1;
        while ((someK != i)(flag[someK] && (label[someK], someK) << (label[i], i))) {};
    }
    public void unlock() {
        flag[ThreadID.get()] = false;
    }
}
*/
