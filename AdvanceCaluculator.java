/**
 * this class will take a list of key words and caluculate the idf and cosine score
 *
 *@author YuKai Zeng 
 *@StudentID 23867035
 */
import java.util.*;

public class AdvanceCaluculator {
	HashMap<String,HashMap<String,Integer>> releventDocument;
	HashMap<String,HashMap<String,Integer>> noReleventDocument;

	public AdvanceCaluculator() {

	}

	// this will get the idf from a key word
	public double getIDF(HashMap<String,Double> allWordsList,String s) {
		double i = 0.0;
		if (allWordsList.containsKey(s)) {
			i = allWordsList.get(s);
			return i;
		} else {
			return i;
		}
	}

	// this will get a term frequency for a document
	public int getTF(HashMap<String,Integer> document,String s) {
		int i = 0;
		if (document.containsKey(s)) {
			i = document.get(s);
			return i;
		} else {
			return i;
		}
	}

	// this will caluculate the term frequency multiply the term idf
	public double docWordTIdf(HashMap<String,Double> allWordsList, HashMap<String,Integer> document, String s){
		return getIDF(allWordsList,s)*getTF(document,s);
	}

	// a method get vector of one document or list of search key words 
	public HashMap<String, Double> getVector(HashMap<String,Double> allWordsList, HashMap<String,Integer> document) {
		HashMap<String, Double> listWordsVector = new HashMap<String, Double>();
		for (Map.Entry<String, Integer> entry : document.entrySet()) {
			String key = entry.getKey();
			double d = docWordTIdf(allWordsList,document,key);
			listWordsVector.put(key,d);
		}
		return listWordsVector;
	}

	public void classifyDocuments(HashMap<String,HashMap<String,Integer>> documentsh,ArrayList<String> documentName) {
		releventDocument = new HashMap<String,HashMap<String,Integer>>();
		noReleventDocument = new HashMap<String,HashMap<String,Integer>>();
		for (Map.Entry<String,HashMap<String,Integer>> entry : documentsh.entrySet()) {
			if (documentName.contains(entry.getKey())) {
				releventDocument.put(entry.getKey(),entry.getValue());
			} else {
				noReleventDocument.put(entry.getKey(),entry.getValue());
			}
		}
	}
	
	public HashMap<String, Double> getNewVector(HashMap<String,Double> allWordsList, HashMap<String,Integer> searchWordList) {
		double alfa = 0.6;
		double bita = 0.2;
		double gama = 0.2;
		HashMap<String, Double> newSearchWordsVector = new HashMap<String, Double>();
		for (Map.Entry<String, Double> entry : allWordsList.entrySet()) {
			//double relevent = 0.0;
			//double noRelevent = 0.0;
			String key = entry.getKey();
			// System.out.println(key);
			// System.out.println(sumWordTfIdf(releventDocument,allWordsList,key));
			// System.out.println(sumWordTfIdf(noReleventDocument,allWordsList,key));
			double d = alfa*(docWordTIdf(allWordsList,searchWordList,key))+bita*(sumWordTfIdf(releventDocument,allWordsList,key))-gama*(sumWordTfIdf(noReleventDocument,allWordsList,key));
			newSearchWordsVector.put(key,d);
		}
		return newSearchWordsVector;
	}

	public double sumWordTfIdf(HashMap<String,HashMap<String,Integer>> documentsh, HashMap<String,Double> allWordsList, String s) {
		double d = 0.0;
		for (Map.Entry<String,HashMap<String,Integer>> entry : documentsh.entrySet()) {

			d += docWordTIdf(allWordsList,documentsh.get(entry.getKey()),s)/documentsh.size();
		}
		return d;
	}

	public double doVectorMultiplication(HashMap<String,Double> searchWords,HashMap<String,Double> allWordsList,HashMap<String,Integer> document){
		double i = 0.0;
		for (Object key : searchWords.keySet()) {
			if (document.containsKey(key)) {
				i += docWordTIdf(allWordsList,document,(String) key)*searchWords.get(key);
			}
		} 
		return i;
	}

	public double doDocSquareRoot(HashMap<String,Double> allWordsList, HashMap<String,Integer> document) {
		double i = 0.0;
		for (Object key : document.keySet()) {
			i += Math.pow(docWordTIdf(allWordsList,document,(String) key),2);
		}
		i = Math.sqrt(i);
		return i;
	}

	public double doVectorSquareRoot(HashMap<String,Double> document) {
		double i = 0.0;
		for (Object key : document.keySet()) {
			i += Math.pow(document.get(key),2);
		}
		i = Math.sqrt(i);
		return i;
	}

	public double caluculateCosineScoreForOneDoc(HashMap<String,Double> searchWords,HashMap<String,Double> allWordsList,HashMap<String,Integer> document) {
		double a = doVectorMultiplication(searchWords,allWordsList,document);
		double b = doVectorSquareRoot(searchWords)*doDocSquareRoot(allWordsList,document);
		double i = a/b;
		return i;
	}

}