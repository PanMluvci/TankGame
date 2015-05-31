package tankgame;

import java.util.Random;

/**
 * Třída která se stará o náhodný směr tanku. Jeho pohyb tímto směrem a zjišťování stavu
 * směru.
 * Tímto povoluji duplikovat můj kod.
 * @author Josef Antoni
 */
public class AI{
    
    public double x = 400;
    public double y = 300;
    private final Random r = new Random();
    private double newX,newY;
     
        public void jizda(){

    int linkaXneboY=r.nextInt(10);
    int smerPohybu = r.nextInt(5);
    if(smerPohybu==2){
    if(linkaXneboY>5){
        
        int pohybKladny = r.nextInt(10);
        //pohyb po kladných přímkách X nebo Y
        if(pohybKladny>5){
            newX=5;
            newY=0;
        }else{
            newX=0;
            newY=5;
        }
        
    }else{
        
        int pohybZaporny = r.nextInt(10);
        //pohyb po záporných přímkách X nebo Y
        if(pohybZaporny>=5){
             newX=-5;
            newY=0;
        }else{
             newX=0;
            newY=-5;
    }
    }  
    }
    }

    /**
     * Tank se pohybuje vpřed nebo vzad po Xsové přímce.
     * Ytá hodnota je 0.
     * @return 
     */
    public double runX() {
        
         x = x+newX;
       return x;
        }
    /**
     * Tank se pohybuje vpřed nebo vzad po Yté přímce.
     * Xtá hodnota je 0.
     * @return 
     */
    public double runY() {
         y = y+newY;
       return y;
        }
    /**
     * Informační metoda o stavu X.
     * @return 
     */
    public double getX() {
       return newX;
        }
    /**
     * Informační metoda o stavu Y.
     * @return 
     */
    public double getY() {
       return newY;
        }

    
}
