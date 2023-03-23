public class SumatorioMPrimos {
    public static long calcularSumaPrimos(long begin,long end) {
        long sumaPrimos = 0;
        long contador = 0;
        long numero;

        for (numero=begin; numero<end; numero++)
        {
            if (numero % 2 != 0) {
                if (esPrimo(numero)) {
                    sumaPrimos += numero;
                    ++contador;
                }
            }
        }

        return sumaPrimos;
    }

    public static boolean esPrimo(long numero) {
        for (int i = 3; i * i <= numero; i += 2) {
            if (numero % i == 0) {
                return false;
            }
        }

        return true;
    }
}
