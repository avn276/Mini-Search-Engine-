package lse;

import java.io.*;
import java.util.*;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages in
 * which it occurs, with frequency of occurrence in each page.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in 
	 * DESCENDING order of frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashSet<String>(100,2.0f);
	}
	
	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeywordsFromDocument(String docFile) 
	throws FileNotFoundException {
		/** COMPLETE THIS METHOD **/
		if(docFile == null) {
			throw new FileNotFoundException();
		}
		
		Scanner scan= new Scanner(new File(docFile));
		HashMap<String, Occurrence> kws= new HashMap<String, Occurrence>();
		
		while(scan.hasNext()) {
			String keyword= getKeyword(scan.next());

			if(keyword != null) {
				keyword=keyword.trim();
				int frequency=1;

				if(keyword.isEmpty() == false) {
					if(kws.containsKey(keyword) == false) {
						Occurrence ocurrence= new Occurrence(docFile, frequency++);
						
						kws.put(keyword, ocurrence);
						
					} else { 
						
						kws.get(keyword).frequency++; 
					}
				}
			}
		}	
		scan.close();
		return kws;
	}
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String,Occurrence> kws) {
		/** COMPLETE THIS METHOD **/
		ArrayList <Occurrence> newoccurence= new ArrayList<Occurrence>();
		
		for(String ky : kws.keySet()) {			
			Occurrence current= kws.get(ky); 
			newoccurence=keywordsIndex.get(ky);

			if(keywordsIndex.containsKey(ky) == false) {
				if(newoccurence == null) {	
					newoccurence= new ArrayList<Occurrence>();
					newoccurence.add(current);
					keywordsIndex.put(ky, newoccurence); 
				}

			} else {
				
				newoccurence.add(current); 
			}
			
			ArrayList<Integer> array= insertLastOccurrence(newoccurence);

		}	
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * trailing punctuation(s), consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * 
	 * If a word has multiple trailing punctuation characters, they must all be stripped
	 * So "word!!" will become "word", and "word?!?!" will also become "word"
	 * 
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyword(String word) {
		/** COMPLETE THIS METHOD **/
		word = word.toLowerCase();
		String subw="";
		
		if(word != null) {		
			for(int i = 0; i < word.length(); i++) {				
				if(!Character.isAlphabetic(word.charAt(i))) {					
					if(word.charAt(i) == ('.') || word.charAt(i) == (',') || word.charAt(i) == ('?') || word.charAt(i) == (':') || word.charAt(i) == (';') || word.charAt(i) == ('!')) {
						subw = word.substring(i);				
						if(letterCheck(subw) == true) {
							return null;
							
						} else { 
							
							subw = word.substring(0, i);

							if(!noiseWords.contains(subw)) {
								return subw;
								
							} else {
								
								return null;
							}
						}
						
					} else {
						
						return null;
					}	
				} 
			}
		}
		
		if(noiseWords.contains(word)) {
			word = null;
		}
		
		return word;
	}
	
	private boolean letterCheck(String wod) {
		for(int i=0; i < wod.length(); i++) {
			if(Character.isAlphabetic(wod.charAt(i))) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion is done by
	 * first finding the correct spot using binary search, then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		/** COMPLETE THIS METHOD **/

		ArrayList <Integer> mfs= new ArrayList <Integer>();
		int f = 0;
		int l = occs.size()-2;
		int lf = occs.get(occs.size()-1).frequency;
		int mf = 0;
		int mid = 0;
		
		if(occs.size() <= 1) {
			return null;
			
		} else {
			
			while(l >= f) {
				mid= (f + l)/2;

				mfs.add(mid);
				
				mf= occs.get(mid).frequency;
				
					
				if(mf == lf) {
					break;
				}
				
				else if(mf < lf) {
					
					l = mid-1;

				} else {
					f = mid+1; 
				}
				
			}	
				if(mf >= lf) {
					occs.add(mid+1, occs.get(occs.size()-1));
					occs.remove(occs.size()-1);
					
				} else {
					occs.add(mid, occs.get(occs.size()-1));
					occs.remove(occs.size()-1);
				}			
		}
		
		System.out.println("occs is: " + occs);
	
		return mfs;
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeywordsFromDocument(docFile);
			mergeKeywords(kws);
		}
		sc.close();
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of document frequencies. 
	 * 
	 * Note that a matching document will only appear once in the result. 
	 * 
	 * Ties in frequency values are broken in favor of the first keyword. 
	 * That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2 also with the same 
	 * frequency f1, then doc1 will take precedence over doc2 in the result. 
	 * 
	 * The result set is limited to 5 entries. If there are no matches at all, result is null.
	 * 
	 * See assignment description for examples
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matches, 
	 *         returns null or empty array list.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		/** COMPLETE THIS METHOD **/
		ArrayList <String> tF= new ArrayList <String>();
		ArrayList <Occurrence> kwc1= new ArrayList <Occurrence>();
		ArrayList <Occurrence> kwc2= new ArrayList <Occurrence>();
			
		if(keywordsIndex.containsKey(kw1)) {
			kwc1 = keywordsIndex.get(kw1); 
		}
		if(keywordsIndex.containsKey(kw2)) {
			kwc2 = keywordsIndex.get(kw2);
		}
		if(kwc1.isEmpty() && kwc2.isEmpty()){
			return null;
			
		} 
		Iterator<Occurrence> iteration1 = kwc1.iterator();
		Iterator<Occurrence> iteration2 = kwc2.iterator();

		Occurrence current1= null;
		try {
			current1 = iteration1.next();
		}
		catch(NoSuchElementException e) {
			current1=null;
		}
		Occurrence current2= null;
		try
		{
			current2 = iteration2.next();
		}
		catch(NoSuchElementException e) {
			current2=null;
		}
		
		if(current2 == null){			
			while(current1 != null && tF.size() != 5) {

				if(!tF.contains(current1.document))
				{
					tF.add(current1.document);
				}
			
				if(iteration1.hasNext()) {
					current1= iteration1.next();
					
				} else {
					
					current1 = null;
				}
			}
		}
		
		else if(current1 == null)
		{			
			while(current2 != null && tF.size() != 5)
			{

				if(!tF.contains(current2.document))
				{
					tF.add(current2.document);
				}
				
				if(iteration2.hasNext())
				{
					current2 = iteration2.next();
					
				} else {
					
					current2 = null;
				}
				

			}
			
		} 
		
		else if(current1 != null && current2 != null) {
			while(current1 != null && current2 != null && tF.size() != 5) {
				 if(current1.frequency > current2.frequency) {
					 if(tF.contains(current1.document) == false) {
						 tF.add(current1.document);
					 }
					 
					 if (iteration1.hasNext()) {
					 	 current1 = iteration1.next();
					 	 
					 } else {
						 
						 current1 = null;
					 }
				 }

				 else if(current1.frequency < current2.frequency) {
					 if(tF.contains(current2.document) == false) {
						 tF.add(current2.document);
					 }
					 
					 if(iteration2.hasNext()){
					 	 current2 = iteration2.next();
					 	 
					 } else {
						 
						 current2 = null;
					 }
					 
				 }	

				 else if(current1.frequency == current2.frequency) {
					 if(tF.contains(current1.document) == false) {	
						 tF.add(current1.document);
					 }
					 
					 if(tF.contains(current2.document) == false) {
						 tF.add(current2.document);
					 }
					 if (iteration1.hasNext())
					 {
					 	 current1 = iteration1.next();
					 	 
					 } else {
						 
						 current1 = null;
					 }
					 
					 if(iteration2.hasNext())
					 {
					 	 current2 = iteration2.next();
					 	 
					 } else {
						 
						 current2 = null;
					 }
				 } 	 
			}
		}
				
		return tF;
	
	}
}
