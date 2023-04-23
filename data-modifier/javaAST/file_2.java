package java.code.sample;

public class BinarySearch {

    int func_1(int[] var_5, int var_7) {
        int var_1 = 0, var_2 = var_5.length - 1;
        while (var_1 <= var_2) {
            int var_3 = var_1 + (var_2 - var_1) / 2;
            if (var_5[var_3] == var_7)
                return var_3;
            if (var_5[var_3] < var_7)
                var_1 = var_3 + 1;
            else
                var_2 = var_3 - 1;
        }
        return -1;
    }

    public static void main(String[] args) {
        BinarySearch var_4 = new BinarySearch();
        int[] var_5 = { 2, 3, 4, 10, 40 };
        int var_6 = var_5.length;
        int var_7 = 10;
        int var_8 = var_4.func_1(var_5, var_7);
        //        int ans = ob.binarySearch(arr, 4);
        if (var_8 == -1)
            System.out.println("Element is not present in array");
        else
            System.out.println("Element is present at " + "index " + var_8);
    }
}
