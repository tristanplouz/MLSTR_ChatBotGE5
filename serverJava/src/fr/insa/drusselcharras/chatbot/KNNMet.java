package fr.insa.drusselcharras.chatbot;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe de mise en oeuvre de l'algorithme de KNN (K-Nearest neighbors)
 */
public class KNNMet {
    private final List<KNNDat> workingData = new ArrayList<>(); //jeu de données avec le quel l'algorithme travail

    /**
     * @param lines Corpus de données de référence
     * @param keyW  Mots clés définissant la base dans laquelle l'algorithme fonctionne
     * @param cat   Liste de catégories définies dans les données d’entraînement
     */
    public KNNMet(List<List<String>> lines, List<String> keyW, List<String> cat) {
        for (int i = 0; i < lines.size(); i++) {
            double[] d = new double[keyW.size()];
            for (int j = 0; j < keyW.size(); j++) {
                d[j] = TfIdf.tfIdf(lines.get(i), lines, keyW.get(j));
            }
            this.workingData.add(new KNNDat(cat.get(i), d));

        }
    }

    /**
     * Fonction de calcul de la distance Euclidienne entre deux vecteurs
     * dist = sqrt( sum( a_i^2 - b_i^2 ) )
     *
     * @param a Premier vecteur
     * @param b Second vecteur
     * @return La distance euclidienne calculée entre les deux vecteurs
     */
    public static double distanceEuc(double[] a, double[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += Math.pow(a[i] - b[i], 2);
        }
        return Math.sqrt(sum);
    }

    /**
     * Méthode afin de catégoriser un élément en utilisant l'algorithme KNN
     *
     * @param req Représentation vectorielle de l'élément à catégoriser
     * @return Catégorie retrouvée par l'algorithme
     */
    public String categorize(double[] req) {
        double s = 0;
        int knnLim = 3; //Nombre de voisins à détecter

        //Vérification que le vecteur d'entrée est non nul
        for (double d : req) {
            s += d;
        }

        if (s != 0) {
            List<KNNDat> prevCats = this.findCategories(req, knnLim);//Détection des k voisins.
            List<String> cat = new ArrayList<>();
            List<Integer> cnt = new ArrayList<>();

            //Détection des catégories des K voisins
            cat.add(prevCats.get(0).cat);
            cnt.add(1);
            for (int i = 1; i < prevCats.size(); i++) {
                for (int j = 0; j < cat.size(); j++) {
                    if (cat.get(j).equals(prevCats.get(i).cat)) {
                        cnt.set(j, cnt.get(j) + 1);
                    }
                }
            }
            int max = 0;
            int imax = 0;
            for (int i = 0; i < cnt.size(); i++) {
                if (max < cnt.get(i)) {
                    imax = i;
                    max = cnt.get(i);
                }
            }
            if(imax>1.5){
                return "uncategorized";
            }else {
                return cat.get(imax);
            }
        } else {
            return "uncategorized";
        }
    }

    /**
     * Méthode d'application de l'algorithme de KNN
     *
     * @param req    Représentation vectorielle de la donnée
     * @param knnLim Nombre de voisin de l'algorithme.
     * @return Les données correspondantes aux voisins détectés
     */
    private List<KNNDat> findCategories(double[] req, int knnLim) {
        List<KNNDat> ret = new ArrayList<>();
        List<KNNDat> localData = this.workingData;
        //Calcul des knnLim distances euclidiennes les plus courtes
        for (int i = 0; i <= knnLim; i++) {
            KNNDat opti = null;
            double min = Double.POSITIVE_INFINITY;
            int jmin = 0;
            for (int j = 0; j < localData.size(); j++) {
                double dist = distanceEuc(localData.get(j).num, req);
                if (dist < min) {
                    min = dist;
                    jmin = j;
                    opti = localData.get(j);

                }
            }
            ret.add(opti);
            System.out.println(min);
            localData.remove(jmin);
        }
        return ret;
    }
}
