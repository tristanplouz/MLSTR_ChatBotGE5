package fr.insa.drusselcharras.chatbot;

import java.util.Arrays;

/**
 * Classe de données pour le fonctionnement de l'algorithme de KNN
 */
public class KNNDat {
    public String cat;
    public double[] num;

    /**
     * @param cat Catégorie de l'élément
     * @param num Représentation vectorielle de l'élément dans la base définie
     **/
    public KNNDat(String cat, double[] num) {
        this.cat = cat;
        this.num = num;
    }

    @Override
    public String toString() {
        return "KNNDat{" +
                "cat='" + cat + '\'' +
                ", num=" + Arrays.toString(num) +
                '}';
    }
}
