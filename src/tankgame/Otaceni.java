package tankgame;

import javafx.animation.RotateTransition;
import javafx.animation.RotateTransitionBuilder;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.util.Duration;

/**
 * Třída která otáčí objekty tank a střela.
 * Tímto povoluji duplikovat můj kod.
 * @author Josef Antoni
 */
public class Otaceni {
    
       private RotateTransition otoceniTanku, otoceniPatrony;
       private int toceniTankuX, toceniTankuY, otoceniPatronyX, otoceniPatronyY; 
/**
 * Metoda pro otáčení tanků.
 * @param toceniTankuY o kolik stupňů se má objekt otočit
 * @param mTanku otáčí se hráčův nebo soupeřův tank
 */
        public void otoceniTanku( int toceniTankuY, Parent mTanku){   
           
           otoceniTanku = RotateTransitionBuilder.create()
                .node(mTanku)
                .duration(Duration.millis(10))
                .fromAngle(toceniTankuX)
                .toAngle(toceniTankuY)
                .cycleCount(1)
                .build();
            
            otoceniTanku.play();
            toceniTankuX=toceniTankuY;// drží tank je na stejném směru
        }
    /**
     * Metoda pro otáčení patron.
     * @param otoceniPatronyY o kolik stupňů se má objekt otočit.
     * @param patrona která patrona se má otočit.
     */
    public void otoceniPatrony(int otoceniPatronyY, Group patrona){   
        
            if(!patrona.isVisible()){//pri vystrelu se patrona neotáčí s tankem
            otoceniPatrony = RotateTransitionBuilder.create()
                .node(patrona)
                .duration(Duration.millis(1))
                .fromAngle(otoceniPatronyX)
                .toAngle(otoceniPatronyY)
                    .cycleCount(1)
                    .onFinished(new EventHandler<ActionEvent>(){
                    @Override
                    public void handle(ActionEvent t){
                        
                        otoceniPatrony.stop();
                        
                    }
                })
                .build();
           
            otoceniPatrony.play();
            
            otoceniPatronyX=otoceniPatronyY;// drží patronu je na stejném směru
            }
            
           
        }
    
}
