package configuration;

import java.util.HashSet;

public class CacheManager {
	
	private HashSet<Integer> cachedTerms;
	
	public CacheManager(){
		cachedTerms = new HashSet<>();
	}
	
	public void addTerm(Integer termId){
		cachedTerms.add(termId);
	}
	
	public boolean isCached(Integer termId){
//		return cachedTerms.contains(termId);
		return false;
	}
	
	public int getNbTermCached(){
		return cachedTerms.size();
	}
	

}
