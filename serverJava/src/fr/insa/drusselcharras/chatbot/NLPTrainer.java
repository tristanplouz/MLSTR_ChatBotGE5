package fr.insa.drusselcharras.chatbot;

import opennlp.tools.lemmatizer.DictionaryLemmatizer;
import opennlp.tools.postag.*;
import opennlp.tools.sentdetect.*;
import opennlp.tools.tokenize.*;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe d’entraînement
 */
public class NLPTrainer {
    public String repoTraining ="trainingSet/";

    /**
     * Fonction d’entraînement du détecteur de phrases
     * @return  Le détecteur de phrase
     */
    public SentenceDetectorME sentenceTraining() {
        System.out.println("Sentence Training Started...");
        InputStreamFactory isf = () -> new FileInputStream(repoTraining+"fr-sent.train");
        ObjectStream<String> lineStream = null;
        try {
            lineStream = new PlainTextByLineStream(isf, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SentenceModel model = null;
        ObjectStream<SentenceSample> sampleStream = new SentenceSampleStream(lineStream);
        try {
            model = SentenceDetectorME.train("fr", sampleStream, true, null, TrainingParameters.defaultParams());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                sampleStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (model != null) {
            System.out.println("Sentence training ended successful");
        } else {
            System.err.println("Sentence training failed");
        }
        return new SentenceDetectorME(model);
    }

    /**
     * Fonction d’entraînement du détecteur de tokens
     * @return  Le détecteur de tokens
     */
    public Tokenizer tokenTraining() {
        System.out.println("Tokenizer Training Started...");
        InputStreamFactory isf = () -> new FileInputStream(repoTraining+"fr-token.train");
        ObjectStream<String> lineStream = null;
        try {
            lineStream = new PlainTextByLineStream(isf, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
        TokenizerModel model = null;
        ObjectStream<TokenSample> sampleStream = new TokenSampleStream(lineStream);
        try {
            model = TokenizerME.train(sampleStream,
                    new TokenizerFactory("fr",
                            null,
                            true,
                            null),
                    TrainingParameters.defaultParams());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                sampleStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (model != null) {
            System.out.println("Tokenizer training ended successful");
        } else {
            System.err.println("Tokenizer training failed");
        }

        return new TokenizerME(model);
    }

    /**
     * Fonction d’entraînement du détecteur de fonction grammaticale
     * @return  Le détecteur de fonction grammaticale
     */
    public POSTaggerME posTagTraining() {
        System.out.println("PoS Tag Training Started...");
        POSModel model = null;

        try {
            InputStreamFactory isf = () -> new FileInputStream(repoTraining+"fr-postag.train");
            ObjectStream<String> lineStream = null;
            try {
                lineStream = new PlainTextByLineStream(isf, StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ObjectStream<POSSample> sampleStream = new WordTagSampleStream(lineStream);

            model = POSTaggerME.train("fr",
                    sampleStream,
                    TrainingParameters.defaultParams(),
                    new POSTaggerFactory());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (model != null) {
            System.out.println("PoS Tag training ended successful");
        } else {
            System.err.println("PoS Tag training failed");
        }
        return new POSTaggerME(model);
    }

    /**
     * Fonction d’entraînement du détecteur de lemme
     * @return  Le détecteur de lemme
     */
    public DictionaryLemmatizer lemmaTraining() {
        System.out.println("Lemmatiser Training Started...");
        InputStream dictLemmatizer = null;
        try {
            dictLemmatizer = new FileInputStream(repoTraining+"fr-lemma.train");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        DictionaryLemmatizer lemmatizer = null;
        try {
            lemmatizer = new DictionaryLemmatizer(dictLemmatizer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (lemmatizer != null) {
            System.out.println("Lemmatiser training ended successful");
        } else {
            System.err.println("Lemmatiser training failed");
        }
        return lemmatizer;
    }

    /**
     * @param nbrKeyW   Nombre de mots clé à trouver
     * @param lines     corpus de données d’entraînement
     * @return          Liste de mots clés
     */
    public List<String> findKeyWords(int nbrKeyW, List<List<String>> lines) {
        System.out.println("Looking for " + nbrKeyW + " keyword(s) in the corpus...");
        List<String> allW = new ArrayList<>();
        List<Integer> allC = new ArrayList<>();
        for (List<String> s : lines) {
            for (String w : s) {
                allW.add(w);
                allC.add(1);
                for (int i = 0; i < allW.size(); i++) {
                    if (w.equals(allW.get(i))) {
                        allC.set(i, allC.get(i) + 1);
                        break;
                    }
                }
            }
        }
        List<String> keyW = new ArrayList<>();
        for (int i = 0; i < nbrKeyW+1; i++) {
            int max = 0;
            int imax = 0;
            for (int j = 0; j < allC.size(); j++) {
                if (allC.get(j) > max) {
                    max = allC.get(j);
                    imax = j;
                }
            }
            keyW.add(allW.get(imax));
            allC.remove(imax);
            allW.remove(imax);
        }
        keyW.remove(0);
        System.out.println(keyW);
        return keyW;
    }
}
