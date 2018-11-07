package Utils;

import java.util.Collection;
import java.util.Map;

public class Utils {

	public static <K, V> long MapSize(Map<K,Collection<V>> map) {
		 if(map == null)
			 return 0;
        int i = 0;
        for(K key : map.keySet()){
            i +=map.get(key).size();
        }
        return i;
	}
}
