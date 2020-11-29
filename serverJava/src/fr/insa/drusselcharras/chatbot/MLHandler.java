package fr.insa.drusselcharras.chatbot;

import opennlp.tools.lemmatizer.DictionaryLemmatizer;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.tokenize.Tokenizer;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Classe supervisant la partie Machine Learning du projet
 */
public class MLHandler {

    private final NLPTrainer trainer;
    private final SentenceDetectorME sentenceDetector;
    private final Tokenizer tokenizer;
    private final POSTaggerME tagger;
    private final DictionaryLemmatizer lemmatizer;
    private final List<String> stopWords = Arrays.asList(".", "?", ",", "!", "'","je", "tu", "un", "des", "la", "le","il","une");
    private final List<String> stopTags = Arrays.asList("PUNCT", "D", "SYM", "CLS");
    private List<List<String>> corpus;
    private final List<String> keyWord;
    private List<String> cat;
    private final KNNMet KNNHandler;

    /**
     * Constructeur de la classe
     */
    public MLHandler() {
        this.trainer = new NLPTrainer();
        this.sentenceDetector =  this.trainer.sentenceTraining();
        this.tokenizer =  this.trainer.tokenTraining();
        this.tagger =  this.trainer.posTagTraining();
        this.lemmatizer =  this.trainer.lemmaTraining();
        this.createCorpus();
        this.keyWord =  this.trainer.findKeyWords(80, this.corpus);
        this.KNNHandler = new KNNMet(this.corpus, this.keyWord, this.cat);
    }

    /**
     * Méthode permettant de créer les données à partir du corpus
     */
    private void createCorpus() {
        this.corpus = new ArrayList<>();
        this.cat = new ArrayList<>();
        Scanner sc = null;
        try {
            sc = new Scanner(new File("trainingSet/fr-cat.train"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while (sc.hasNextLine()) {
            String l = sc.nextLine();
            this.cat.add(l.split("\t")[0]);
            String s = l.toLowerCase().split("\t")[1];
            String[] tk = this.tokenizer.tokenize(s);
            String[] tag = this.tagger.tag(tk);

            String[] lemma = this.lemmatizer.lemmatize(tk, tag);
            String[] lemmaCleaned = this.lemmaCleaner(lemma, tk, tag);
            List<String> d = Arrays.asList(lemmaCleaned);
            this.corpus.add(d);
        }

    }

    /**
     * Méthode permettant de supprimer les éléments indésirables
     * @param lemma     Liste de mots lématiser
     * @param tokens    Liste de mots détecter dans le message
     * @param tag       Liste des tags de chaque mot du message
     * @return          Liste de mot représentant l'idée du message
     */
    private String[] lemmaCleaner(String[] lemma, String[] tokens,String[] tag) {
        for (int i = 0; i < tokens.length; i++) {
            //retire les stops words
            for (String sw : this.stopWords) {
                if (sw.equals(tokens[i])) {
                    tokens[i] = "";
                    tag[i] = "";
                }
            }
            //retire les stops tags
            for (String tg : this.stopTags) {
                if (tg.equals(tag[i])) {
                    tokens[i] = "";
                    tag[i] = "";
                }
            }
            //unifie le lemmatizer
            if (lemma[i].equals("O")) {
                lemma[i] = tokens[i];
            }
        }
        return lemma;
    }

    /**
     * Méthode permettant la compréhension d'un message
     * @param dataIn    message d'entrée
     * @return          liste de message catégorisé
     */
    public List<Message> processData(String dataIn) {
        System.out.println("Data processing started");
        dataIn = dataIn.toLowerCase();
        List<Message> msg=new ArrayList<>();
        String[] sentences = this.sentenceDetector.sentDetect(dataIn);
        for (String sent : sentences) {
            System.out.println(sent);
            String[] tokens = this.tokenizer.tokenize(sent);
            String[] tags = this.tagger.tag(tokens);
            String[] lemma = this.lemmatizer.lemmatize(tokens, tags);
            lemma = this.lemmaCleaner(lemma, tokens,tags);
            for (String t : tokens
            ) {
                System.out.print(t + "\t|");
            }
            System.out.println();
            for (String t : tags) {
                System.out.print(t + "\t|");
            }
            System.out.println();
            for (String l : lemma) {
                System.out.print(l + "\t|");
            }
            System.out.println();

            System.out.println();
            String tmpcat = this.KNNHandler.categorize(this.calculation(lemma));
            switch (tmpcat){
                case "RD":
                    LocalDateTime[] infos = this.findTimeInfo(tokens,tags);
                    msg.add(new Message(tmpcat,infos[0],infos[1]));
                    break;
                case "N":
                    String name = this.findNameInfo(tokens,tags);
                    msg.add(new Message(tmpcat,name));
                    break;
                default:
                    msg.add(new Message(tmpcat));
            }
            System.out.println("Catégorisé en: " + msg.get(msg.size()-1).category);


        }
        return msg;
    }

    /**
     * Méthode permettant de trouver des infos temporelles
     * @param tk    Liste de mots du messages
     * @param tags  Liste de tags du messages
     * @return      Date de début et date de fin
     */
    private LocalDateTime[] findTimeInfo(String[] tk, String[] tags) {
        LocalDateTime[] ret = new LocalDateTime[2];
        LocalDate date = LocalDate.now();
        int s = 0;
        for (int i = 1; i < tk.length; i++) {
            if(tags[i - 1].equals("P") && tags[i].equals("CD")){
                for (String kw:tk) {
                    if(kw.equals("demain")){
                        //TODO
                        date = date.plusDays(1);
                    }
                }
                ret[s] = LocalDateTime.of(date, LocalTime.of(Integer.parseInt(tk[i].substring(0,2)),0));
                s+=1;
            }
        }

        return ret;
    }

    /**
     * Méthode permettant de calculer notre représentation vectorielle de la phrase.
     * @param dataIn    Phrase à transformer
     * @return          Vecteur représentatif
     */
    private double[] calculation(String[] dataIn){
        double[] d = new double[this.keyWord.size()];
        for (int j = 0; j < this.keyWord.size(); j++) {
            d[j] = TfIdf.tfIdf(Arrays.asList(dataIn), this.corpus, this.keyWord.get(j));
        }
        for (double a : d
        ) {
            System.out.print(a + ",");
        }
        return d;
    }

    /**
     * Méthode permettant de trouver des infos temporelles
     * @param tk    Liste de mots du messages
     * @param tags  Liste de tags du messages
     * @return      Nom détecté
     */
    private String findNameInfo(String[] tk, String[] tags) {
        //TODO
        return "";
    }
}
