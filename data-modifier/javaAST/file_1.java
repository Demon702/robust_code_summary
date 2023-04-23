package java.code.sample;

public class fibonacci {

    static int func_1(int n) {
        if (n <= 1)
            return n;
        return func_1(n - 1) + func_1(n - 2);
    }

    public static void main(String[] args) {
        int var_1 = 10;
        for (int var_2 = 0; var_2 < var_1; var_2++) {
            System.out.print(func_1(var_2) + " ");
        }
    }
}
