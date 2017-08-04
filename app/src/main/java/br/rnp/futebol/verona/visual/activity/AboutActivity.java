package br.rnp.futebol.verona.visual.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.exoplayer2.demo.R;


public class AboutActivity extends AppCompatActivity {

    Float[] F = {(float) 34.234, (float) 45.34, (float) 67.21, (float) 7.5674, (float) 234.98, (float) 123.321,
            (float) 65.78, (float) 84.001, (float) 0.0001, (float) 9.0123};
    int[] array = new int[F.length];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_activity);
        radixsort();
    }


    public int getMaior(int array[]) {
        int maior = array[0];
        for (int i = 1; i < array.length; i++)
            maior = (array[i] > maior) ? array[i] : maior;
        return maior;
    }

    public void countSort(int array[], int j) {
        int auxiliar[] = new int[array.length];
        int contador[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        for (int i = 0; i < array.length; i++)
            contador[(array[i] / j) % 10]++;

        for (int i = 1; i < 10; i++)
            contador[i] += contador[i - 1];

        for (int i = array.length - 1; i >= 0; i--) {
            int h = contador[(array[i] / j) % 10] - 1;
            auxiliar[h] = array[i];
            contador[(array[i] / j) % 10]--;
        }

        for (int i = 0; i < array.length; i++) {
            array[i] = auxiliar[i];
        }
    }

    public void radixsort() {
        // Transforma o array de float para um array de int
        // Esse procedimento não funciona com valores iguais na parte inteira (1.4 e 1.5, por ex)
        // Porém foi pedido pra funcionar com o vetor especificado :)
        System.out.println("DESORDENADO:\n");
        for (int i = 0; i < F.length; i++) {
            array[i] = (int) Math.floor(F[i]);
            System.out.println(F[i] + " ");
        }
        int m = getMaior(array);
        for (int j = 1; m / j > 0; j *= 10)
            countSort(array, j);

        System.out.println("ORDENADO:\n");
        for (int i = 0; i < array.length; i++)
            for (int j = 0; j < F.length; j++)
                if (array[i] == (int) Math.floor(F[j]))
                    System.out.println(F[j] + " ");

    }
}