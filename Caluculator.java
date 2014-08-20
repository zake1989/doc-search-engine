/**
 * this class will take a list of key words and caluculate the idf and cosine score
 *
 *@author YuKai Zeng 
 *@StudentID 23867035
 */
import java.util.*;

public class Caluculator {

	public Caluculator(){

	}

	/**
	 * caluculate the nature log for a double
	 */
	public double log(double a){
		return Math.log(a);
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

	public double doVectorMultiplication(HashMap<String,Integer> searchWords,HashMap<String,Double> allWordsList,HashMap<String,Integer> document){
		double i = 0.0;
		for (Object key : searchWords.keySet()) {
			if (document.containsKey(key)) {
				i += docWordTIdf(allWordsList,document,(String) key)*docWordTIdf(allWordsList,searchWords,(String) key);
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


	public double caluculateCosineScoreForOneDoc(HashMap<String,Integer> searchWords,HashMap<String,Double> allWordsList,HashMap<String,Integer> document) {
		double a = doVectorMultiplication(searchWords,allWordsList,document);
		double b = doDocSquareRoot(allWordsList,searchWords)*doDocSquareRoot(allWordsList,document);
		double i = a/b;
		return i;
	}
}