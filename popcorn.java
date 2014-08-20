import java.io.*;
import java.util.*;

/**
 *
 * @author YuKai Zeng 
 * @StudentID 23867035
 */

public class popcorn {
	 /**
     * @param args the command line arguments
     */
     private static String arguments[];
     private Scanner scan;
     private ArrayList<String> list;
     private ArrayList<String> tokens;
     private ArrayList<String> stopWordsList = new ArrayList<String>();
     private ArrayList<String> reDoc = new ArrayList<String>();
     private Set<String> allWords = new HashSet<String>();
     private Caluculator c = new Caluculator();
     private NewTokenization t = new NewTokenization();
     private AdvanceCaluculator ac = new AdvanceCaluculator();

     private HashMap<String,Double> allWordsList = new HashMap<String,Double>();
     private HashMap<String,HashMap<String,Integer>> documentsh = new HashMap<String,HashMap<String,Integer>>();
     private HashMap<String,Integer> wordsList;
     private HashMap<String,Double> documentsCosineScore = new HashMap<String,Double>();
     private HashMap<String,Double> newDocumentsCosineScore = new HashMap<String,Double>();

     public static void main(String[] args) {
        // TODO code application logic here
        popcorn p = new popcorn();
        arguments = new String[args.length];
        for (int i = 0; i < args.length; i++) {
            arguments[i] = args[i];
        }
        if (args[0].equals("index")) {
            p.endWithForwardSlash(arguments,1);
            p.endWithForwardSlash(arguments,2);
            p.doIndex();
        }else if (args[0].equals("search")){
            p.endWithForwardSlash(arguments,1);
            p.search();
        }
     }

     public popcorn() {

     }

     public void doIndex() {
        //System.out.println("do the index");
        getStopWords();
        File[] listOfFiles = fileReader(arguments[1]);
        int fileLength = listOfFiles.length;
        for (int i = 0; i < fileLength; i++) {
            File file = listOfFiles[i];
            if (file.isFile() && file.getName().endsWith(".txt")) {
                String s = file.getName();
                //System.out.println(s);
                list = new ArrayList<String>();
                tokens = new ArrayList<String>();
                String[] temp;
                try { 
                    scan = new Scanner(new BufferedReader(new FileReader(file)));
                    while(scan.hasNextLine()) {
                        tokens.addAll(Arrays.asList(scan.nextLine().split("\\s+")));
                    }
                    scan.close(); 
                    t.joinLineBreak(tokens);
                    t.joinAllCaptialInAnArray(tokens);
                    for (String token: tokens) {
                        if (!stopWordsList.contains(token) && !t.emailValidator(token) && !t.urlValidator(token)) {
                            token = t.removeSymbolCharacters(token);
                            token = token.toLowerCase();
                            token = doStem(token);
                            if ( !token.equals("") && !stopWordsList.contains(token)&& !t.sigleCharacterValidator(token) ) {
                                list.add(token);
                                allWords.add(token);
                            }  
                        } else if (t.emailValidator(token)) {
                            list.add(token);
                            allWords.add(token);
                        } else if (t.urlValidator(token)) {
                            list.add(token);
                            allWords.add(token);
                        }
                    }
                    createDocument(list,s);
                }
                catch(Exception e) {
                    //System.err.println(e.getMessage());
                }
            }
        }

        for (String s : allWords) {
            addKeyWordsMap(s);
        }
        putIndexInToAFile();

    }

    public void search() {
        //System.out.println("do the search");
        list = new ArrayList<String>();
        String[] searchKeyWords = new String[arguments.length-3];
        int a = 0;
        int argu = arguments.length;
        for (int i = 3; i < argu; i++) {
            searchKeyWords[a] = arguments[i];
            a++;
        }
        try{
            for (String s : searchKeyWords) {
                if (!stopWordsList.contains(s)) {
                s = t.removeSymbolCharacters(s);
                s = s.toLowerCase();
                s = doStem(s);
                list.add(s);
                }
            }
        }
        catch(Exception e) {
            System.err.println(e.getMessage());
        }
        Set<String> unqiue = new HashSet<String>(list);
        HashMap<String,Integer> searchWordsMap = new HashMap<String,Integer>();
        for (String s : unqiue) {
            searchWordsMap.put(s, new Integer(Collections.frequency(list, s)));
        }
        //System.out.println(searchWordsMap);
        createDocumentMap();
        createKeyWordsMap();

        for (Map.Entry<String, HashMap<String, Integer>> entry : documentsh.entrySet()) {
            double i = 0.0;
            String key = entry.getKey();
            Object value = entry.getValue();
            i = c.caluculateCosineScoreForOneDoc(searchWordsMap,allWordsList,documentsh.get(key));
            documentsCosineScore.put(key,(double) Math.round(i*1000)/1000);
        }
        //System.out.println(sortHashMap(documentsCosineScore,Integer.parseInt(arguments[2])));
        //System.out.println(documentsCosineScore);

        searchPrint(sortHashMap(documentsCosineScore,Integer.parseInt(arguments[2])));

        typeReleventDocument();
        ac.classifyDocuments(documentsh,reDoc);
        // System.out.println(ac.releventDocument);
        // System.out.println(ac.noReleventDocument);
        //System.out.println(ac.getNewVector(allWordsList,searchWordsMap));
        for (Map.Entry<String, HashMap<String, Integer>> entry : documentsh.entrySet()) {
            double i = 0.0;
            String key = entry.getKey();
            Object value = entry.getValue();
            i = ac.caluculateCosineScoreForOneDoc(ac.getNewVector(allWordsList,searchWordsMap),allWordsList,documentsh.get(key));
            newDocumentsCosineScore.put(key,(double) Math.round(i*1000)/1000);
        }
        //System.out.println(newDocumentsCosineScore);
        searchPrint(sortHashMap(newDocumentsCosineScore,Integer.parseInt(arguments[2])));
    }

    public void searchPrint(HashMap<String, Double> sortHashMap) {
        for (Map.Entry<String, Double> entry : sortHashMap.entrySet()) {
            System.out.println("Document Name: "+entry.getKey()+" Cosine Score: "+entry.getValue());   
        }
    }

    private void typeReleventDocument() {
        try {
            String releventDocument = "";
            System.out.println("Please enter a search term: ");
            System.out.println("It should be in form:documentname documentname ~~~~");
            BufferedReader bReader = new BufferedReader(new InputStreamReader(System.in));
            releventDocument = bReader.readLine();
            String[] document = releventDocument.split("\\s+");
            reDoc.addAll(Arrays.asList(document));
        } catch (IOException e) {
        }
    }
    private HashMap<String, Double> sortHashMap(HashMap<String, Double> input, int number){
        Map<String, Double> tempMap = new HashMap<String, Double>();
        for (String wsState : input.keySet()){
            tempMap.put(wsState,input.get(wsState));
        }

        List<String> mapKeys = new ArrayList<String>(tempMap.keySet());
        List<Double> mapValues = new ArrayList<Double>(tempMap.values());
        HashMap<String, Double> sortedMap = new LinkedHashMap<String, Double>();
        TreeSet<Double> sortedSet = new TreeSet<Double>(mapValues);
        Object[] sortedArray = sortedSet.toArray();
        int size = sortedArray.length;
        for (int i=size; i>size-number-1; --i){
            try{
            sortedMap.put(mapKeys.get(mapValues.indexOf(sortedArray[i])), (Double)sortedArray[i]);
            } catch(Exception e) {
            }
        }
        return sortedMap;
    }


    public String[] joinTwoStringArray(String[] sa,String[] sr) {
        ArrayList<String> temp = new ArrayList<String>();
        temp.addAll(Arrays.asList(sa));
        temp.addAll(Arrays.asList(sr));
        sa = temp.toArray(new String[sa.length+sr.length]);
        return sa;
    }

    public String[] readStringFromFile(File f) {
        String[] tokens = new String[0];
        try { 
            scan = new Scanner(new BufferedReader(new FileReader(f)));
            while(scan.hasNextLine()) {
                String[] time = scan.nextLine().split("\\s+");
                tokens = joinTwoStringArray(tokens,time);
            }
        }
        catch(Exception e) {
            System.err.println(e.getMessage());
        }
        return tokens;
    }

    public File[] fileReader(String s) {
        File folder = new File(s);
        File[] listOfFiles = folder.listFiles();
        return listOfFiles;
    }


    public void createKeyWordsMap() {
        File[] listOfFiles = fileReader(arguments[1]);
        for (int i = 0; i < listOfFiles.length; i++) {
            File file = listOfFiles[i];
            if (file.isFile() && file.getName().endsWith(".txt")) {
                try { 
                    scan = new Scanner(new BufferedReader(new FileReader(file)));
                    while(scan.hasNextLine()) {
                        String[] tokens = scan.nextLine().split(",");
                        String keyWord = tokens[0];
                        double idf = Double.parseDouble(tokens[tokens.length-1]);
                        allWordsList.put(keyWord,idf);
                    }
                }
                catch(Exception e) {
                    //System.err.println(e.getMessage());
                }
            }
        }
    }

    public void createDocumentMap() {
        File[] listOfFiles = fileReader(arguments[1]);
        for (int i = 0; i < listOfFiles.length; i++) {
            File file = listOfFiles[i];
            if (file.isFile() && file.getName().endsWith(".txt")) {
                try { 
                    scan = new Scanner(new BufferedReader(new FileReader(file)));
                    while(scan.hasNextLine()) {
                        String[] tokens = scan.nextLine().split(",");
                        for (i = 1 ; i < tokens.length ; i++) {
                            if (tokens[i].endsWith(".txt")) {
                                if (documentsh.containsKey(tokens[i])) {
                                    documentsh.get(tokens[i]).put(tokens[0],Integer.parseInt(tokens[i+1]));
                                }else{
                                    wordsList = new HashMap<String,Integer>();
                                    wordsList.put(tokens[0],Integer.parseInt(tokens[i+1]));
                                    documentsh.put(tokens[i],wordsList);
                                }
                            }
                        }
                    }
                }
                catch(Exception e) {
                    //System.err.println(e.getMessage());
                }
            }
        }
    }




    public void createDocument(ArrayList<String> wordList, String docName) {
        Set<String> unqiue = new HashSet<String>(wordList);

        wordsList = new HashMap<String,Integer>();
        for (String s : unqiue) {
            wordsList.put(s, new Integer(Collections.frequency(wordList, s)));
        }
        documentsh.put(docName,wordsList);
    }

    public String doStem(String word) {
        char[] w = new char[word.length()];
        porterstemming p = new porterstemming();
        w = word.toCharArray();
        for (int i = 0; i < word.length(); i ++ ) {
            p.add(w[i]);
        }
        p.stem();
        word = p.toString();
        return word;
    }

    public void addKeyWordsMap(String s) {
        int j = 0;
        double noDocs = (double)documentsh.size();
        for (Object value : documentsh.values()) {
            //System.out.println(value);
            //HashMap hm = (HashMap) value;
            if (((HashMap) value).containsKey(s)) {
                j++;
            }
        }
        double idf = c.log(noDocs/j);
        allWordsList.put(s,(double) Math.round(idf*1000)/1000);
    }

    public void putIndexInToAFile() {
        try{
            PrintWriter writer = new PrintWriter("./"+ arguments[2]+"/index.txt", "UTF-8");
            writer.println(printIndex());
            writer.close();
        }
        catch(IOException iox) {
            iox.printStackTrace();
        }
    }

    public String printIndex() {
        String outPut = "";
        for (Map.Entry<String, Double> entry : allWordsList.entrySet()) {
            outPut += entry.getKey();
            outPut += termFrequencyDetail(entry.getKey());
            outPut += String.valueOf(entry.getValue()) + "\n";
        }
        return outPut;
    }

    public String termFrequencyDetail(String s) {
        String outPut = ",";
        for (Map.Entry<String, HashMap<String, Integer>> entry : documentsh.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (((HashMap) value).containsKey(s)) {
                outPut += key + "," + String.valueOf(((HashMap) value).get(s))+",";
            }
        }
        //outPut += " ";
        return outPut;
    }

    public void endWithForwardSlash(String[] s,int i) { 
        if (!s[1].endsWith("/")) {
         s[1] = s[1]+"/";   
        }
    }

    
    public void getStopWords() {
        try {
            File stopWords = new File(arguments[3]);
            scan = new Scanner(new BufferedReader(new FileReader(stopWords)));
            while(scan.hasNextLine()) {
                stopWordsList.add(scan.nextLine());
            } 
        }
        catch(Exception e) {
            System.err.println(e.getMessage());
        }
    }
}