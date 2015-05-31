package tankgame;

 /**
  * Tato třída je hlavní třída programu, který slouží jako semestrální práce.
  * Program je hra, která simuluje bitevní pole, jako herní plochu a dva tanky,
  * které se snaží navzájem porazit. 
  * Tímto povoluji duplikovat můj kod.
  * 
  * @author Josef Antoni 2013-2014
  */

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.animation.Animation;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.animation.TranslateTransitionBuilder;
import static javafx.application.Application.launch;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.layout.VBox;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class TankGame extends Application{
    
    private final static int WIDTH = 800;
    private final static int HEIGHT = 600;    
    private final static Image BACKGROUND_IMAGE = new Image(TankGame.class.getResource("imgs/Tank_back.png").toString()); // vytvoří se img + načte se ze souboru
    private final static Image PATRONA = new Image(TankGame.class.getResource("imgs/Tank_patrona.png").toString()); // vytvoří se img + načte se ze souboru
    private final static Image PATRONA_PROTIVNIKA = new Image(TankGame.class.getResource("imgs/Tank_patrona.png").toString()); // vytvoří se img + načte se ze souboru
    private Animation modelAnimacePatrony, modelAnimaceKruhu, modelAnimacePatronyProtivnika, modelAnimaceKruhuProtivnika;
    private Group patrona,patronaProtivnika;
    private double smerStrelyX, smerStrelyY, smerStrelyXSouperX, smerStrelyXSouperY;
    private final Otaceni otaceni = new Otaceni();
    private final TankHrac tankHrac = new TankHrac();
    private final AI ai = new AI();
    private final TankProtivnik tankProtivnik = new TankProtivnik();
    private boolean nastalaKolize = false;
    private final Random r = new Random();
    private double aiTankProtivnik;
    private Circle r1,r2,r3,r4;
    private VBox hud;    
    private Scene scene;
    
    /**
    * Metoda start slouží jako hlavní metoda která se stará o celkové vykreslení
    * programu na plátno. Vytváří vlákna která se starají o kolize a běh umělé inteligence.
    * Zde je umožněno hráči střílení klávesou "Mezerník".
    * @param primaryStage 
    */
    @Override
    public void start(Stage primaryStage) {
 
        ImageView background = new ImageView(BACKGROUND_IMAGE);
        final ImageView bullet = new ImageView(PATRONA);
        final ImageView bulletProtivnik = new ImageView(PATRONA_PROTIVNIKA);
        
        patrona = new Group(bullet);
        patronaProtivnika = new Group(bulletProtivnik);
       //vytvoreni kruhu pro kolize, kruhy budou vloženy do tanku
        r1 = new Circle(25);    
        r2 = new Circle(25);
        r3 = new Circle(10);
        r4 = new Circle(10);
        r1.setVisible(false);   
        r2.setVisible(false);
        r3.setVisible(false);
        r4.setVisible(false);
        
        
        //text který zobrazí konec hry
        final Text kolizeText =new Text();
        kolizeText.textProperty().bind(Bindings.concat("Konec hry!"));
        hud = VBoxBuilder.create().children(kolizeText).translateX(300)
                .translateY(200).build();
        kolizeText.setFont(Font.font("Arial", FontWeight.BOLD, 30));

        
        Group root = new Group(background,tankHrac, tankProtivnik, patrona, patronaProtivnika, hud,r1, r2, r3);//deti
        hud.setVisible(false);
        patrona.setVisible(false);
        patronaProtivnika.setVisible(false);
        
        scene = new Scene(root, WIDTH, HEIGHT);
        
        tankHrac.setTranslateX(250);//defaultni vyskyt modelu
        tankHrac.setTranslateY(200);//defaultni vyskyt modelu
        tankProtivnik.setTranslateX(400);//defaultni vyskyt modeluProtivnika
        tankProtivnik.setTranslateY(300);//defaultni vyskyt modeluProtivnika 
        smerStrelyX = tankHrac.getTranslateX();
        smerStrelyY = tankHrac.getTranslateY()-230;
        smerStrelyXSouperX = tankProtivnik.getTranslateX();
        smerStrelyXSouperY = tankProtivnik.getTranslateY()-230;
        
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() { //pohybování na mapě pomocí šipek
            @Override
            public void handle(KeyEvent ke) {
                /**
                 * Zde je umožněno hráči střílení klávesou Mezerník
                 */
                if(nastalaKolize==false){
                    
                if( ke.getCode() == KeyCode.SPACE ) { 
                    if(!patrona.isVisible()){
                    strileni(smerStrelyX,smerStrelyY, tankHrac);
                    }
                }

                jezdeniPoPlatnu(ke);
                nastaveniKruhuVTancich();
                zajisteniTankuNaPlatnu();
                }          
            }
        });
       
        //nové vlákno pro kontrolu kolize
        Task<Void> task = new Task<Void>() {
        @Override 
        protected Void call() throws Exception {
        
        while(nastalaKolize == false) {
            
            checkKolize();
         
        }
        return null; 
        }};
         
        Thread th = new Thread(task);
        th.setDaemon(true); 
        th.start(); 
       
       
        //zajištění volání metody každých 150 milisekund
        final ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(new Runnable() {
        @Override
        public void run() {
           pohybNepritele();
            if(nastalaKolize==true){
                exec.shutdown();
            }
        }
        }, 0, 150, TimeUnit.MILLISECONDS);
        
         
        nastaveniKruhuVTancich();
        primaryStage.setTitle("Tank 1.0");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setResizable(false);
    }
       
    /**
     * Metoda pro Animaci výstřelu z tanku hráče.
     * Animace je aktivkní pouze čas který je vidět(1 sekunda) a nelze zrušit.
     * Spolu s ní je vystřelen kruh (pro kontrolu kolize). Oba objekty
     * mají stejnou dráhu skterá je za potřebí pro dosažení přesnosti výsledku střely.
     * 
     * @param smerStrelyX pozice na Xsové úsečce kterou urazí střela
     * @param smerStrelyY pozice na Yté úsečce kterou urazí střela
     * @param modelTanku objekt ze kterého střela bude vycházet. Typ TankHrac
     */
    public void strileni(double smerStrelyX, double smerStrelyY, TankHrac modelTanku){
        
        // jedna pro patronu druha pro kruh na kolize
        patrona.setVisible(true);
        
        modelAnimacePatrony = TranslateTransitionBuilder.create()
                .node(patrona)
                .fromX(modelTanku.getTranslateX()+30)
                .toX(smerStrelyX+30)
                .fromY(modelTanku.getTranslateY()+30)
                .toY(smerStrelyY+30)
                .duration(Duration.seconds(1))
                .onFinished(new EventHandler<ActionEvent>(){
                    @Override
                    public void handle(ActionEvent t){
                        
                        modelAnimacePatrony.stop();
                        patrona.setVisible(false);
                    }
                })
                .build();
        modelAnimacePatrony.play();
        
         modelAnimaceKruhu = TranslateTransitionBuilder.create()
                .node(r3)
                .fromX(modelTanku.getTranslateX()+35)
                .toX(smerStrelyX+35)
                .fromY(modelTanku.getTranslateY()+35)
                .toY(smerStrelyY+35)
                .duration(Duration.seconds(1))
                .onFinished(new EventHandler<ActionEvent>(){
                    @Override
                    public void handle(ActionEvent t){
                        
                        modelAnimaceKruhu.stop();
                        
                    }
                })
                .build();
        modelAnimaceKruhu.play();
         
    }  
    
    /**
     * Metoda pro Animaci výstřelu z tanku protivníka, pomocí umělé inteligence.
     * Animace je aktivkní pouze čas který
     * je vidět(1 sekunda) a nelze zrušit. Spolu s ní je vystřelen kruh (pro kontrolu kolize).Oba objekty
     * mají stejnou dráhu skterá je za potřebí pro dosažení přesnosti výsledku střely.
     * 
     * @param smerStrelyX pozice na Xsové úsečce kterou urazí střela
     * @param smerStrelyY pozice na Yté úsečce kterou urazí střela
     * @param modelTanku objekt ze kterého střela bude vycházet. Typ TankProtivnik
     */
    public void strileniProtivnika(double smerStrelyX, double smerStrelyY, TankProtivnik modelTanku){
        
        // jedna pro patronu druha pro kruh na kolize
        patronaProtivnika.setVisible(true);
        
        modelAnimacePatronyProtivnika = TranslateTransitionBuilder.create()
                .node(patronaProtivnika)
                .fromX(modelTanku.getTranslateX()+30)
                .toX(smerStrelyX+30)
                .fromY(modelTanku.getTranslateY()+30)
                .toY(smerStrelyY+30)
                .duration(Duration.seconds(1))
                .onFinished(new EventHandler<ActionEvent>(){
                    @Override
                    public void handle(ActionEvent t){
                        
                        modelAnimacePatronyProtivnika.stop();
                        patronaProtivnika.setVisible(false);
                    }
                })
                .build();
        modelAnimacePatronyProtivnika.play();
        
         modelAnimaceKruhuProtivnika = TranslateTransitionBuilder.create()
                .node(r4)
                .fromX(modelTanku.getTranslateX()+35)
                .toX(smerStrelyX+35)
                .fromY(modelTanku.getTranslateY()+35)
                .toY(smerStrelyY+35)
                .duration(Duration.seconds(1))
                .onFinished(new EventHandler<ActionEvent>(){
                    @Override
                    public void handle(ActionEvent t){
                        
                        modelAnimaceKruhuProtivnika.stop();
                        
                    }
                })
                .build();
        modelAnimaceKruhuProtivnika.play();
         
    }  
    
    /**
     * Metoda pro ježdění s modelem tanku po plátnu. Pomocí šipek (UP,DOWN,LEFT,RIGHT)
     * se model pohybuje. Zároveň otáčí s modelem ve směru jízdy ("čumák" tanku je vždy
     * ve správné pozici). To samé platí i o patroně. Rychlost pohybu je -5 pixelů za stisk.
     * 
     * @param ke uchovává hodnotu šipky která je zmáčknutá, podle toho se uživatel pohybuje
     */
    public void jezdeniPoPlatnu(KeyEvent ke){
        if( ke.getCode() == KeyCode.LEFT ) {
            tankHrac.setTranslateX(tankHrac.getTranslateX() - 5);//nastavení hodnot  bude model ukazovat
            
            otaceni.otoceniTanku(-90, tankHrac);
            otaceni.otoceniPatrony(-90, patrona);
            smerStrelyX = tankHrac.getTranslateX()-230;
            smerStrelyY = tankHrac.getTranslateY();
            
        }

        if( ke.getCode() == KeyCode.RIGHT ) {
            tankHrac.setTranslateX(tankHrac.getTranslateX() + 5);//nastavení hodnot kde se bude model ukazovat
            otaceni.otoceniTanku(90, tankHrac);
            otaceni.otoceniPatrony(90, patrona);
            smerStrelyX = tankHrac.getTranslateX()+230;
            smerStrelyY = tankHrac.getTranslateY();
            
        }

        if( ke.getCode() == KeyCode.UP ) {
            tankHrac.setTranslateY(tankHrac.getTranslateY() - 5);//nastavení hodnot kde se bude model ukazovat
            otaceni.otoceniTanku(0, tankHrac);
            otaceni.otoceniPatrony(0, patrona);
            smerStrelyX = tankHrac.getTranslateX();
            smerStrelyY = tankHrac.getTranslateY()-230;
            
        }

        if( ke.getCode() == KeyCode.DOWN ) {
            tankHrac.setTranslateY(tankHrac.getTranslateY() + 5);//nastavení hodnot kde se bude model ukazovat
            otaceni.otoceniTanku(180, tankHrac);
            otaceni.otoceniPatrony(180, patrona);
            smerStrelyX = tankHrac.getTranslateX();
            smerStrelyY = tankHrac.getTranslateY()+230;
            
        }
    }
    
    /**
    * Metoda pro test kolize, pomocí kruhů v každém pohybujícím se objektu.
    * Pokud je kolize potvrzena nastává výbuch objektu a ohlášení konce hry.
    * Možnosti:
    * 1.Výbuch hráče
    * 2.Výbuch soupeře
    * 3.Výbuch obou tanků
    */
    private void checkKolize() { 
        //kontrola kolize mezi tanky
        Shape intersectMeziTankama = Shape.intersect(r1, r2);
        if (intersectMeziTankama.getBoundsInLocal().getWidth() != -1) {
            tankHrac.boom();
            tankProtivnik.boom();
            nastalaKolize = true;
            hud.setVisible(true);
            
        }
        //kontrola kolize mezi Protivnikem a Hracovo Strelou
        Shape intersectMeziProtivnikemAStrelou = Shape.intersect(r3, r2);
        if (intersectMeziProtivnikemAStrelou.getBoundsInLocal().getWidth() != -1) {
            
            tankProtivnik.boom();
            nastalaKolize = true;
            patrona.setVisible(false);
            hud.setVisible(true);
            
        }
        //kontrola kolize mezi Hracem a protivnikovo Strelou
         Shape intersectMeziStrelouProtivnikaAHracem = Shape.intersect(r4, r1);
        if (intersectMeziStrelouProtivnikaAHracem.getBoundsInLocal().getWidth() != -1) {
            
            tankHrac.boom();
            nastalaKolize = true;
            patronaProtivnika.setVisible(false);
            hud.setVisible(true);
            
        }
        
      }
     
    /**
    *  Metoda pro modely Tanků, aby nevyjely z plátna o velikosti 800x600.
    * 
    */   
    public void zajisteniTankuNaPlatnu(){
            if(tankHrac.getTranslateX()>WIDTH-100){
                tankHrac.setTranslateX(WIDTH-100);
            }
            if(tankHrac.getTranslateX()<30){
                tankHrac.setTranslateX(30);
            }
            if(tankHrac.getTranslateY()<30){
                tankHrac.setTranslateY(30);
            }
            if(tankHrac.getTranslateY()>=HEIGHT-100){
                tankHrac.setTranslateY(HEIGHT-100);
            }
            if(tankProtivnik.getTranslateX()>WIDTH-100){
                tankProtivnik.setTranslateX(WIDTH-100);
            }
            if(tankProtivnik.getTranslateX()<30){
                tankProtivnik.setTranslateX(30);
            }
            if(tankProtivnik.getTranslateY()<30){
                tankProtivnik.setTranslateY(30);
            }
            if(tankProtivnik.getTranslateY()>=HEIGHT-100){
                tankProtivnik.setTranslateY(HEIGHT-100);
            }
        }
                 
    /**
     * Metoda pro umělou inteligenci protivníka. Ze třídy AI je požadován směr pohybu.
     * Model a patrona jsou následně otočeny do správněho směru a jsou upraveny
     * hodnoty objektu střely, se kterou pracuje soupeřovo tank.
     * Tato metoda se opakuje v metodě start() každých 150 milisekund.
     * 
     */
    public void pohybNepritele(){
        
        //extra metoda na toceni
        aiTankProtivnik = r.nextInt(8);
        
        ai.jizda(); //ruzne promene
           
       if(aiTankProtivnik >= 5 && ai.getX()==-5 && aiTankProtivnik>0){
            // rotace 
            tankProtivnik.setTranslateX(ai.runX());    
            otaceni.otoceniTanku(-90, tankProtivnik);
            otaceni.otoceniPatrony(-90, patronaProtivnika);
            smerStrelyXSouperX = tankProtivnik.getTranslateX()-230;
            smerStrelyXSouperY = tankProtivnik.getTranslateY();
     
       }
       
         if(aiTankProtivnik >= 5  && ai.getY()==-5 && aiTankProtivnik>0){
           // rotace 
            tankProtivnik.setTranslateY(ai.runY());
            otaceni.otoceniTanku(0, tankProtivnik);//rotace
             otaceni.otoceniPatrony(0, patronaProtivnika);
            smerStrelyXSouperX = tankProtivnik.getTranslateX();
            smerStrelyXSouperY = tankProtivnik.getTranslateY()-230;
            
        }
         
       //pro plus
        if(aiTankProtivnik <= 5  && ai.getX()==+5 && aiTankProtivnik>0){
            tankProtivnik.setTranslateX(ai.runX());
            otaceni.otoceniTanku(+90, tankProtivnik);//rotace
            otaceni.otoceniPatrony(+90, patronaProtivnika);
            smerStrelyXSouperX = tankProtivnik.getTranslateX()+230;
            smerStrelyXSouperY = tankProtivnik.getTranslateY();
        } 

        if(aiTankProtivnik <= 5  && ai.getY()==+5 && aiTankProtivnik>0){
            tankProtivnik.setTranslateY(ai.runY());       
            otaceni.otoceniTanku(-180, tankProtivnik);//rotace
            otaceni.otoceniPatrony(-180, patronaProtivnika);
            smerStrelyXSouperX = tankProtivnik.getTranslateX();
            smerStrelyXSouperY = tankProtivnik.getTranslateY()+230;
        } 
        zajisteniTankuNaPlatnu();  
        if(aiTankProtivnik==0){
            if(!patronaProtivnika.isVisible()){
                
               strileniProtivnika(smerStrelyXSouperX,smerStrelyXSouperY, tankProtivnik);
            }
        }
    }
    
    /**
     * Metoda pro zajištění kruhu r1 a r2 v tancích. Pozice kruhu se vždy schoduje s pozicí tanku.
     * Díky kruhům je docílena kontrola kolize.
     */
    public void nastaveniKruhuVTancich(){
    
        r1.setTranslateX(tankHrac.getTranslateX()+34);
        r1.setTranslateY(tankHrac.getTranslateY()+34);

        r2.setTranslateX(tankProtivnik.getTranslateX()+34);
        r2.setTranslateY(tankProtivnik.getTranslateY()+34);
        
    }
    /**
     * Netbeams vyžadovaná metoda.
     * @param args 
     */
    public static void main(String[] args) {
        launch(args);
        
    }  
}
