package fr.insa.drusselcharras.chatbot;

import java.util.List;

/**
 * Classe de calcul de la transformation Tf-IDF (Term Frequency - Inverse Document Frequency
 * Récupérée ici: https://gist.github.com/guenodz/d5add59b31114a3a3c66
 */
public class TfIdf {
    /**
     * Fonction de calcul de la fréquence d'un mot
     *
     * @param phrase données sur lesquelles appliquer le processus
     * @param kw     Mot clé sur lequel appliquer le processus
     * @return la valeur de la TF
     */
    public static double tf(List<String> phrase, String kw) {
        double result = 0;
        for (String word : phrase) {
            if (kw.equalsIgnoreCase(word))
                result++;
        }

        return result / phrase.size();
    }

    /**
     * Fonction de calcul de la fréquence inverse de document
     *
     * @param corpus Corpus de données sur lequel appliquer le processus
     * @param kw     Mot clé sur lequel appliquer le processus
     * @return la valeur de l'IdF
     */
    public static double idf(List<List<String>> corpus, String kw) {
        double n = 0;
        for (List<String> doc : corpus) {
            for (String word : doc) {
                if (kw.equalsIgnoreCase(word)) {
                    n++;
                    break;
                }
            }
        }
        return Math.log(corpus.size() / n);
    }

    /**
     * Fonction de calcul de la TF-IDF
     *
     * @param phrase données sur lesquelles appliquer le processus
     * @param corpus Corpus de données sur lequel appliquer le processus
     * @param kw     Mot clé sur lequel appliquer le processus
     * @return La valeur calculée
     */
    public static double tfIdf(List<String> phrase, List<List<String>> corpus, String kw) {
        return tf(phrase, kw) * idf(corpus, kw);

    }
}
