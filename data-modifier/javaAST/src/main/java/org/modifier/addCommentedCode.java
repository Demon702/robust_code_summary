package org.modifier;

public class addCommentedCode {
    String code1 = "/* for(int i=0;i<100;i++){ if(i%2) System.out.println(\"odd\"); else System.out.println(\"even\");} */";
    String code2 = "/* public static int factorial(int n)\n" +
            "\t{\tint result = 1;\n" +
            "\t\tfor(int i = 2; i <= n; i++)\n" +
            "\t\t\tresult *= i;\n" +
            "\t\treturn result;\n" +
            "\t} */";
    String code3 = "/* try {\n" +
            "      int[] myNumbers = {1, 2, 3};\n" +
            "      System.out.println(myNumbers[10]);\n" +
            "    } catch (Exception e) {\n" +
            "      System.out.println(\"Something went wrong.\");\n" +
            "    } */";
    String code4 = "/* static double plusMethodDouble(double x, double y) {\n" +
            "  return x + y;\n" +
            "} */";
    String code5 = "/* public Main(int year, String name) {\n" +
            "    modelYear = year;\n" +
            "    modelName = name;\n" +
            "  } */";
    String code6 = "/* public static void time() {\n" +
            "    LocalDate myObj = LocalDate.now(); // Create a date object\n" +
            "    System.out.println(myObj); // Display the current date\n" +
            "  } */";
    String code7 = "/* try {\n" +
            "      int[] myNumbers = {1, 2, 3};\n" +
            "      System.out.println(myNumbers[10]);\n" +
            "    } catch (Exception e) {\n" +
            "      System.out.println(\"Something went wrong.\");\n" +
            "    } finally {\n" +
            "      System.out.println(\"The 'try catch' is finished.\");\n" +
            "    } */";
    String code8 = "/* static int fib(int n)\n" +
            "    {\n" +
            "        if (n <= 1)\n" +
            "            return n;\n" +
            " \n" +
            "        return fib(n - 1)\n" +
            "            + fib(n - 2);\n" +
            "    } */";
    String code9 = "/*  public static boolean isPrime(int n) {  \n" +
            "       if (n <= 1) {  \n" +
            "           return false;  \n" +
            "       }  \n" +
            "       for (int i = 2; i < Math.sqrt(n); i++) {  \n" +
            "           if (n % i == 0) {  \n" +
            "               return false;  \n" +
            "           }  \n" +
            "       }  \n" +
            "       return true;  \n" +
            "   }   */";
    String code10 = "/* public class Main {\n" +
            "  int x = 5;\n" +
            "} */";


    String code11 = "/* static void checkAge(int age) {\n" +
            "    if (age < 18) {\n" +
            "      throw new ArithmeticException(\"Access denied - You must be at least 18 years old.\");\n" +
            "    }\n" +
            "    else {\n" +
            "      System.out.println(\"Access granted - You are old enough!\");\n" +
            "    }\n" +
            "  } */";
    String code12 = "/* switch (day) {\n" +
            "  case 6:\n" +
            "    System.out.println(\"Today is Saturday\");\n" +
            "    break;\n" +
            "  case 7:\n" +
            "    System.out.println(\"Today is Sunday\");\n" +
            "    break;\n" +
            "  default:\n" +
            "    System.out.println(\"Looking forward to the Weekend\");\n" +
            "} */";
    String code13 = "/* public static int sum(int k) {\n" +
            "    if (k > 0) {\n" +
            "      return k + sum(k - 1);\n" +
            "    } else {\n" +
            "      return 0;\n" +
            "    }\n" +
            "  } */";
    String code14 = "/* public void run() {\n" +
            "    System.out.println(\"This code is running in a thread\");\n" +
            "  } */";
    String code15 = "/* public static void fDelete() { \n" +
            "    File myObj = new File(\"filename.txt\"); \n" +
            "    if (myObj.delete()) { \n" +
            "      System.out.println(\"Deleted the file: \" + myObj.getName());\n" +
            "    } else {\n" +
            "      System.out.println(\"Failed to delete the file.\");\n" +
            "    } \n" +
            "  }  */";
    String code16 = "/* try {\n" +
            "      File myObj = new File(\"filename.txt\");\n" +
            "      if (myObj.createNewFile()) {\n" +
            "        System.out.println(\"File created: \" + myObj.getName());\n" +
            "      } else {\n" +
            "        System.out.println(\"File already exists.\");\n" +
            "      }\n" +
            "    } catch (IOException e) {\n" +
            "      System.out.println(\"An error occurred.\");\n" +
            "      e.printStackTrace();\n" +
            "    } */";
    String code17 = "/* enum Level {\n" +
            "  LOW,\n" +
            "  MEDIUM,\n" +
            "  HIGH\n" +
            "} */";

    private  String [] trainComment = {code1, code2, code3, code4, code5, code6, code7, code8, code9, code10};
    private String []testComment = {code11, code12, code13, code14, code15, code16, code17};
    String commentedCode;

    public addCommentedCode(String mode)
    {
        if(mode=="test"){
            commentedCode = testComment[(int)Math.floor(Math.random() * 7)];
        }
        else{
            commentedCode = trainComment[(int)Math.floor(Math.random() * 10)];
        }
    }
}
