package paris.shingling;

import java.io.*;
import java.util.*;

/**
 * This class is part of the PARIS ontology matching project at INRIA
 * Saclay/France.
 * 
 * It is licensed under a Creative Commons Attribution Non-Commercial License
 * by the author Mayur Garg. For all further information, see
 * http://webdam.inria.fr/paris
 */

public class ShinglingTable implements Serializable{

   public int noHashFunctions;
    public int shinglingLength;
    public int hashTableSize;
    
    public List<Integer>[][] hashtables;
      public int[][] hashValues;
      public Vector<String> indexed;
      public int indexedStrings;
    
    
    
    public ShinglingTable(int shinglingLength1, int noHashFunctions1, int
     hashTableSize1){
        this.noHashFunctions=noHashFunctions1;
        this.shinglingLength=shinglingLength1;
        this.hashTableSize=hashTableSize1;
        indexedStrings=0;
        hashValues=CreateHashFunctions(noHashFunctions,hashTableSize);
        hashtables=(LinkedList<Integer>[][]) new LinkedList[noHashFunctions][hashTableSize];
               
//             for(int cnt=0;cnt<noHashFunctions;cnt++){
//            
//            for(int cnt1=0;cnt1<hashTableSize;cnt1++){
//        hashtables[cnt][cnt1]=new LinkedList<Integer>();
//        
//
//
//        }}
    
    
         indexed=new Vector<String>();
        
    }
      
    
        
         public static int[][] CreateHashFunctions(int n, int hs){
    
        int[][] result=new int[n][hs];
        
    for(int i=0;i<n;i++){
        List<Integer> list = new ArrayList<Integer>();
    
        for(int j=0;j<hs;j++){
        list.add(j);
    }
    java.util.Collections.shuffle(list);
    for(int j=0;j<hs;j++){
        result[i][j]=(list.get(j)).intValue();
     //   System.out.print(result[i][j]+" ");
    }
   //System.out.println();
    }
    return result;
    
    }

        public static int goldStandard(String a, String b, int k){
        
            int res=0;
            
            HashMap<Integer, Integer> hm1=new HashMap<Integer, Integer>();
            HashMap<Integer, Integer> hm2=new HashMap<Integer, Integer>();
            
            byte[] a1=a.getBytes();
            byte[] b1=b.getBytes();
            
            int l1=a1.length;
            int l2=b1.length;
            
            for(int c1=0; c1<(l1-k+1);c1++){
            
            
            int rep1=0;
            
            
           for(int iks=0;iks<k;iks++){
            
               rep1=rep1*256+(((int)(a1[c1+iks]))%256);
            
           }
               //Integer key1=new Integer(rep1);
               //Integer key2=new Integer(rep2);
               
               if(hm1.containsKey(rep1)){
              	 hm1.put(rep1, hm1.get(rep1) + 1);
               }else{ 
                 hm1.put(rep1, 1);}
               
                              
           }
        
            
         
               for(int c1=0; c1<(l2-k+1);c1++){
            
            
            
            int rep2=0;
            
           for(int iks=0;iks<k;iks++){
            
            
               rep2=rep2*256+(((int)(b1[c1+iks]))%256);
               
               //Integer key1=new Integer(rep1);
               //Integer key2=new Integer(rep2);
               
           }     
               if(hm2.containsKey(rep2)){
                 int val = hm2.get(rep2) + 1;
                 hm2.put(rep2, val);
                 
                 if(hm1.containsKey(rep2)){
                 if(val<=(hm1.get(rep2))){res++;}
                 
                 }
                 
                 
                 
                 }else{ 
                                  hm2.put(rep2, 1);
               if(hm1.containsKey(rep2)){
                 res++;
                 
                 }
               }
               
           }
               return (200*res/(hm1.size()+hm2.size()));
        }
            
    
        public void index(String strTmp){
        
          
        
        
         
        
        
        
//int[] varS=new int[ks];
           int tmpry;
byte[] ba;
//String shingling;
        
        
        
         
    
    
    
    int[] min=new int[noHashFunctions];
    int l;
    int rep;
    int cnt2;
    int c1;
    int iks;
    int cnt3;
    int cnt4;
    
    int[] hash=new int[noHashFunctions];
    
    
        indexed.add(indexedStrings,strTmp);
        //System.out.println(strTmp+" "+indexed.get(strTmp));
        //int hbb=Integer.parseInt(indexed.get(strTmp).toString());
        //System.out.println(strTmp+" "+hbb);
        //str=new StringBuilder(strTmp);
         ba=strTmp.getBytes();
        
        
        for(cnt2=0;cnt2<noHashFunctions;cnt2++){
            min[cnt2]=hashTableSize-1;
        }
        
   
                l=ba.length;
               //String[] arr=new String[l-1];
        for(c1=0; c1<(l-shinglingLength+1);c1++){
            
            //shingling= ""+str.charAt(c1)+""+str.charAt(c1+1)+""+str.charAt(c1+2);
        
       //     arr[c1]=shingling;
          //  System.out.print(shingling+"\t");
            rep=0;
           for(iks=0;iks<shinglingLength;iks++){
            
               tmpry=((int)(ba[c1+iks]))%256;
if(tmpry<0){tmpry=tmpry+256;}

               //varS[iks]=((int)(ba[c1+iks]))%256;
               rep=(rep*256)+tmpry;
           }
//            System.out.println(((shingling.charAt(0)*256*256)+(shingling.charAt(1)*256)+(shingling.charAt(2))));
  
//          int rep= ((shingling.charAt(0)*256*256)+(shingling.charAt(1)*256)+((int)(shingling.charAt(2))))%(16777216);

rep= (rep)%(hashTableSize);
if(rep<0){rep=hashTableSize+rep;}

            //if (rep<0){
              //  System.out.println("\nINDEX NEGATIVE\n"); 
            //System.out.println("Problem in "+shingling);
            //}
//System.out.print(rep+"\t");
            
            
            for(cnt3=0;cnt3<noHashFunctions;cnt3++){
                hash[cnt3]=(hashValues[cnt3][rep]);
                if(hash[cnt3]<min[cnt3])min[cnt3]=hash[cnt3];
                          }}
  //      System.out.println(str);
        
            for(cnt4=0;cnt4<noHashFunctions;cnt4++){
            	if (hashtables[cnt4][(min[cnt4])] == null) {
            		hashtables[cnt4][(min[cnt4])] = new LinkedList<Integer>();
            	}
              hashtables[cnt4][(min[cnt4])].add(indexedStrings);
            }
        
        
        // System.out.println(strTmp+" is indexed");
         indexedStrings=indexedStrings+1;   
                
        }
        
        
        
        public Iterable<QueryResult> query(String str1Tmp, double threshold){
        
            int tmpry;
byte[] ba;
//String shingling;
      
    
    int[] min=new int[noHashFunctions];
    int l;
    int rep;
    int cnt2;
    int c1;
    int iks;
    int cnt3;
    int cnt4;
    List<Integer> sl;
    //StringBuilder stmp;
    int j;
    int ValTmp;
    int val;
    Iterator iter;
    Map.Entry pairs;
    double score;
    double goldScore;
    int stmp;
    
    int[] hash=new int[noHashFunctions];              
            
             Comparator<QueryResult> comp = new Comparator<QueryResult>(){
 
            @Override
            public int compare(QueryResult arg0, QueryResult arg1){
                if (arg0.trueScore<arg1.trueScore){return 1;}
                else if (arg0.trueScore>arg1.trueScore){return -1;}
                else {return 0;}
            };
        };
 
            
            List<QueryResult> resultList = new ArrayList<QueryResult>();
            QueryResult res;
            
            //str1=new StringBuilder(str1Tmp);
    
         HashMap hm=new HashMap();
       ba=str1Tmp.getBytes();
        
        //min=new int[n];
        for(cnt2=0;cnt2<noHashFunctions;cnt2++){
            min[cnt2]=hashTableSize-1;
        }
        
   
                l=ba.length;
               //String[] arr=new String[l-1];
        for(c1=0; c1<(l-shinglingLength+1);c1++){
            
            //shingling= ""+str.charAt(c1)+""+str.charAt(c1+1)+""+str.charAt(c1+2);
        
       //     arr[c1]=shingling;
          //  System.out.print(shingling+"\t");
            rep=0;
           for(iks=0;iks<shinglingLength;iks++){
            tmpry=((int)(ba[c1+iks]))%256;
if(tmpry<0){tmpry=tmpry+256;}

               //varS[iks]=((int)(ba[c1+iks]))%256;
               rep=(rep*256)+tmpry;

               //varS[iks]=((int)(ba[c1+iks]))%256;
             
           }
//            System.out.println(((shingling.charAt(0)*256*256)+(shingling.charAt(1)*256)+(shingling.charAt(2))));
  
//          int rep= ((shingling.charAt(0)*256*256)+(shingling.charAt(1)*256)+((int)(shingling.charAt(2))))%(16777216);

rep= (rep)%(hashTableSize);
if(rep<0){rep=hashTableSize+rep;}
//System.out.print(rep+"\t");

           // if (rep<0){System.out.println("\nINDEX NEGATIVE 2\n"); System.out.println("Problem in "+shingling);}
            
           // int[] hash=new int[n];
            for(cnt3=0;cnt3<noHashFunctions;cnt3++){
                hash[cnt3]=(hashValues[cnt3][rep]);
                if(hash[cnt3]<min[cnt3])min[cnt3]=hash[cnt3];
                          }}
  //      System.out.println(str1);
        
            for(cnt4=0;cnt4<noHashFunctions;cnt4++){
             sl=hashtables[cnt4][min[cnt4]];
             if (sl != null) {
	             Iterator<Integer> it = sl.iterator();
	           	 while(it.hasNext()) {
	           		 		
	                 stmp=it.next();
	                // System.out.println(str1 + "\tand\t"+s1);
	                 
	                 if(hm.containsKey(stmp)){
	                     ValTmp= Integer.parseInt((hm.get(stmp)).toString());
	                     val=ValTmp+1;
	                    /* if(val>(n*thres)){
	                     //System.out.println("Occurences = "+val);
	                     //System.out.println(str1 + "\t = \t"+s1);
	                     }*/
	                 hm.put(stmp, val);
	                 }else{ 
	                 
	                 hm.put(stmp, 1);}
	             }
             }
            }
        
         iter = hm.entrySet().iterator();
    while (iter.hasNext()) {
        pairs = (Map.Entry)iter.next();
        score=((Integer.parseInt((pairs.getValue()).toString()))*100.0)/noHashFunctions;
        if(score>=(threshold*100.0)){
            
    //  System.out.println(str1 + "\t=\t"+pairs.getKey() + " with score " + score);
       goldScore=goldStandard(str1Tmp, indexed.elementAt(Integer.parseInt((pairs.getKey()).toString())),shinglingLength);
       // System.out.println("Gold Standard -> "+indexed.elementAt(Integer.parseInt((pairs.getKey()).toString()))+"\t=\t"+str1Tmp+ " with score " + goldScore);
       
       if(goldScore>=(threshold*100.0)){
       res = new QueryResult(indexed.elementAt(Integer.parseInt((pairs.getKey()).toString())),score, goldScore);
       resultList.add(res);
       
       }
       
        }
    }
            
        Collections.sort(resultList, comp);
        
        return resultList;
        
        
        }

public static void main(String args[]){
ShinglingTable st1=new ShinglingTable(2, 10, 65536);
st1.index("David Copperfield");
st1.index("Two cities");
st1.index("The story of troy");
st1.index("Othello");
st1.index("Copper Field");
st1.index("Copperfield");

System.out.println("Query : "+"D. Copperfield");
Iterator<QueryResult> i1 = st1.query("D. Copperfield", 0.4).iterator();

                while (i1.hasNext()) {
                    QueryResult qr=i1.next();
                        System.out.println(qr.result + "\t"+qr.estimatedScore+"\t"+qr.trueScore);
                }
                
System.out.println("Query : "+"Troy story");                
Iterator<QueryResult> i3 = st1.query("Troy story", 0.4).iterator();

                while (i3.hasNext()) {
                    QueryResult qr=i3.next();
                        System.out.println(qr.result + "\t"+qr.estimatedScore+"\t"+qr.trueScore);
                }
                
                System.out.println("Query : "+"2 cities");                
Iterator<QueryResult> i2= st1.query("2 cities", 0.4).iterator();

                while (i2.hasNext()) {
                    QueryResult qr=i2.next();
                        System.out.println(qr.result + "\t"+qr.estimatedScore+"\t"+qr.trueScore);
                }
                
                System.out.println("Query : "+"The Othello");                
Iterator<QueryResult> i4= st1.query("The Othello", 0.4).iterator();

                while (i4.hasNext()) {
                    QueryResult qr=i4.next();
                        System.out.println(qr.result + "\t"+qr.estimatedScore+"\t"+qr.trueScore);
                }
                

}}
    
