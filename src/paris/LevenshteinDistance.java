package paris;

/** This class is part of the PARIS ontology matching project at INRIA Saclay/France.
 * 
 * It is licensed under a Creative Commons Attribution Non-Commercial License
 * by the author Fabian M. Suchanek (http://suchanek.name). For all further information,
 * see http://webdam.inria.fr/paris
 *
 * This class computes the Levenshtein distance and the Levenshtein similarity.
 * */

public class LevenshteinDistance {
  public static int get(int[][] tab, int x, int y) {
  	if (x == -1 && y == -1)
  		return 0;
  	if (x == -1)
  		return y;
  	if (y == -1)
  		return x;
  	return tab[x%2][y];
  }
    
  public static int distance(CharSequence x, CharSequence y) {
    int[][] matrix = new int[2][y.length()];

    for (int i = 0; i < x.length(); i++)
    	for (int j = 0; j < y.length(); j++)
        matrix[i%2][j] = Math.min(get(matrix, i-1, j) + 1,
            Math.min(get(matrix, i, j-1) + 1,
                get(matrix, i-1, j-1) + (x.charAt(i) == y.charAt(j) ? 0 : 1)));

    return get(matrix, x.length()-1, y.length()-1);
  }
    
  public static double similarity(CharSequence x, CharSequence y) {
  	if (x.length() == 0 && y.length() == 0) return 1.;
  	return 1.-((double) distance(x, y)) / Math.max(x.length(), y.length());
  }
}
