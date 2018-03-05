import MyExceptions.*;

public class ExceptionsMain {
    private static Exception[] exceptions = new Exception[10];

    public static void main(String[] args) {
        try {
            throw new MyException1();
        } catch (MyException1 e) {
            exceptions[0] = e;
        }
        try {
            throw new MyException2();
        } catch (MyException2 e) {
            exceptions[1] = e;
        }
        try {
            throw new MyException3();
        } catch (MyException3 e) {
            exceptions[2] = e;
        }
        try {
            throw new MyException4();
        } catch (MyException4 e) {
            exceptions[3] = e;
        }
        try {
            throw new MyException5();
        } catch (MyException5 e) {
            exceptions[4] = e;
        }
        try {
            throw new MyException6();
        } catch (MyException6 e) {
            exceptions[5] = e;
        }
        try {
            throw new MyException7();
        } catch (MyException7 e) {
            exceptions[6] = e;
        }
        try {
            throw new MyException8();
        } catch (MyException8 e) {
            exceptions[7] = e;
        }
        try {
            throw new MyException9();
        } catch (MyException9 e) {
            exceptions[8] = e;
        }
        try {
            throw new MyException10();
        } catch (MyException10 e) {
            exceptions[9] = e;
        }

        System.out.println("Array is filled");
    }
}
