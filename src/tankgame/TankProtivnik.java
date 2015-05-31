package tankgame;

import javafx.animation.KeyFrame;
import javafx.animation.TimelineBuilder;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

/**
 * Třída pro vykreslení protivníkova Tanku a jeho exploze.
 * Tímto povoluji duplikovat můj kod.
 * @author Josef Antoni
 */
public class TankProtivnik extends Parent{

    private final static Image VYBUCH1 = new Image(TankGame.class.getResource("imgs/1.png").toString()); // vytvoří se img + načte se ze souboru 
    private final static Image VYBUCH2 = new Image(TankGame.class.getResource("imgs/2.png").toString()); // vytvoří se img + načte se ze souboru
    private final static Image VYBUCH3 = new Image(TankGame.class.getResource("imgs/3.png").toString()); // vytvoří se img + načte se ze souboru
    private final static Image VYBUCH4 = new Image(TankGame.class.getResource("imgs/4.png").toString()); // vytvoří se img + načte se ze souboru
    private final static Image VYBUCH5 = new Image(TankGame.class.getResource("imgs/5.png").toString()); // vytvoří se img + načte se ze souboru
    private final static Image VYBUCH6 = new Image(TankGame.class.getResource("imgs/6.png").toString()); // vytvoří se img + načte se ze souboru
    private final static Image VYBUCH7 = new Image(TankGame.class.getResource("imgs/7.png").toString()); // vytvoří se img + načte se ze souboru
    private final static Image VYBUCH8 = new Image(TankGame.class.getResource("imgs/8.png").toString()); // vytvoří se img + načte se ze souboru
    private final static Image VYBUCH9 = new Image(TankGame.class.getResource("imgs/9.png").toString()); // vytvoří se img + načte se ze souboru
    private final static Image VYBUCH10 = new Image(TankGame.class.getResource("imgs/10.png").toString());
    private final static Image TANK_MODEL_PROTIVNIK = new Image(TankGame.class.getResource("imgs/Tank_model2.png").toString()); // vytvoří se img + načte se ze souboru
    
    private final Group group;
    
    
    public TankProtivnik(){
         final ImageView tankProtivnik = new ImageView(TANK_MODEL_PROTIVNIK);
         group = new Group(tankProtivnik);
         getChildren().add(group);
         
    }
    /**
     * Metoda boom() proběhne pokud je tank zasažen.
     * Exploze proběhne vykreslením 10 obrázků pro reálnější vzhled.
     */
    public void boom(){
     final ImageView vybuch1 = new ImageView(VYBUCH1);
     final ImageView vybuch2 = new ImageView(VYBUCH2);
     final ImageView vybuch3 = new ImageView(VYBUCH3);
     final ImageView vybuch4 = new ImageView(VYBUCH4);
     final ImageView vybuch5 = new ImageView(VYBUCH5);
     final ImageView vybuch6 = new ImageView(VYBUCH6);
     final ImageView vybuch7 = new ImageView(VYBUCH7);
     final ImageView vybuch8 = new ImageView(VYBUCH8);
     final ImageView vybuch9 = new ImageView(VYBUCH9);
     final ImageView vybuch10 = new ImageView(VYBUCH10);
     
     
      TimelineBuilder.create().cycleCount(1)
                .keyFrames(new KeyFrame(Duration.millis(150), new EventHandler<ActionEvent>(){
                @Override
                public void handle(ActionEvent t){
                    group.getChildren().setAll(vybuch1);
                }
                }), new KeyFrame(Duration.millis(300), new EventHandler<ActionEvent>(){
                @Override
                public void handle(ActionEvent te){
                    group.getChildren().setAll(vybuch2);
                }
                }), new KeyFrame(Duration.millis(450), new EventHandler<ActionEvent>(){
                @Override
                public void handle(ActionEvent te){
                    group.getChildren().setAll(vybuch3);
                }
                }), new KeyFrame(Duration.millis(650), new EventHandler<ActionEvent>(){
                @Override
                public void handle(ActionEvent te){
                    group.getChildren().setAll(vybuch4);
                }
                }), new KeyFrame(Duration.millis(800), new EventHandler<ActionEvent>(){
                @Override
                public void handle(ActionEvent te){
                    group.getChildren().setAll(vybuch5);
                }
                }), new KeyFrame(Duration.millis(950), new EventHandler<ActionEvent>(){
                @Override
                public void handle(ActionEvent te){
                    group.getChildren().setAll(vybuch6);
                }
                }), new KeyFrame(Duration.millis(1100), new EventHandler<ActionEvent>(){
                @Override
                public void handle(ActionEvent te){
                    group.getChildren().setAll(vybuch7);
                }
                }), new KeyFrame(Duration.millis(1250), new EventHandler<ActionEvent>(){
                @Override
                public void handle(ActionEvent te){
                    group.getChildren().setAll(vybuch8);
                }
                }), new KeyFrame(Duration.millis(1400), new EventHandler<ActionEvent>(){
                @Override
                public void handle(ActionEvent te){
                    group.getChildren().setAll(vybuch9);
                }
                }), new KeyFrame(Duration.millis(1500), new EventHandler<ActionEvent>(){
                @Override
                public void handle(ActionEvent te){
                    group.getChildren().setAll(vybuch10);
                }
                }))
               .build().play();
     }
       

}
