package angryflappybird;

import java.util.HashMap;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.AudioClip;
import javafx.scene.text.Text;


public class Defines {
    
	// dimension of the GUI application
    final int APP_HEIGHT = 600;
    final int APP_WIDTH = 600;
    final int SCENE_HEIGHT = 570;
    final int SCENE_WIDTH = 400;

    // coefficients related to the blob
    final int BLOB_WIDTH = 70;
    final int BLOB_HEIGHT = 70;
    final int BLOB_POS_X = 70;
    final int BLOB_POS_Y = 200;
    final int BLOB_DROP_TIME = 300000000;  	// the elapsed time threshold before the blob starts dropping
    final int BLOB_DROP_VEL = 300;    		// the blob drop velocity
    final int BLOB_FLY_VEL = -40;
    final int BLOB_IMG_LEN = 4;
    final int BLOB_IMG_PERIOD = 5;
    
    //coefficients for snowman
    final int SNOWMAN_WIDTH = 70;
    final int SNOWMAN_HEIGHT = 70;
    final int SNOWMAN_POS_X = 70;
    final int SNOWMAN_POS_Y = 0;

    //coefficients for pipe    
    final int PIPE_WIDTH = 100;
    final int PIPE_HEIGHT = 210;
    final int PIPE_MAX_HEIGHT = 0;
    final int PIPE_MIN_HEIGHT = -120;
    final int PIPE_GAP = 300;
    final int PIPE_COUNT = 2;
    int PIPE_VELOCITY=  5;
    
       
    //coefficients for golden egg
    final int GOLDEN_EGG_WIDTH = 70;
    final int GOLDEN_EGG_HEIGHT = 70;
    
    //coefficients for points egg
    final int POINTS_EGG_WIDTH = 70;
    final int POINTS_EGG_HEIGHT = 70;
    
//    final int EGG_POS_X = 70;
//    final int EGG_POS_Y = 200;  //define in angry flappy bird
    
    
    // coefficients related to the floors
    final int FLOOR_WIDTH = 400;
    final int FLOOR_HEIGHT = 100;
    final int FLOOR_COUNT = 2;
    
    // coefficients related to time
    final int SCENE_SHIFT_TIME = 5;
    final double SCENE_SHIFT_INCR = -0.4;
    final double NANOSEC_TO_SEC = 1.0 / 1000000000.0;
    final double TRANSITION_TIME = 0.1;
    final int TRANSITION_CYCLE = 2;
    
    
 // coefficients related to media display
    final String STAGE_TITLE = "Angry Flappy Bird";
    private final String IMAGE_DIR = "../resources/images/";
    private final String AUDIO_DIR = "../resources/audios/";
    final String[] IMAGE_FILES = {"background","blob0", "blob1", "blob2", "blob3", "floor", "goldenEgg", "pipeUp","pipeDown","pointsEgg", "snowman","backgroundNight"};
    final String[] AUDIO_FILES= {"collision", "flying", "pointsEgg", "snooze"};

    final HashMap<String, ImageView> IMVIEW = new HashMap<String, ImageView>();
    final HashMap<String, Image> IMAGE = new HashMap<String, Image>();
    final HashMap<String, AudioClip> AUDIO = new HashMap<String, AudioClip>();
    //Score and life number
     Text scoreText;
     Text lifeText;
     
     Label gameOver;
     Label startNewGame;
    
     //Instruction texts
     Text bonusPoints;
     Text snoozeEgg;
     Text avoidSnowman;
     
     
     
     //Automode text
     Text automodeText;
    
    //nodes on the scene graph
    Button startButton;
    RadioButton easyButton;
    RadioButton mediumButton;
    RadioButton hardButton;
    ToggleGroup level_group;
    
   
    
    // constructor
	Defines() {
		
		// initialize images
		for(int i=0; i<IMAGE_FILES.length; i++) {
			Image img;
			if (i == 5) {
				img = new Image(pathImage(IMAGE_FILES[i]), FLOOR_WIDTH, FLOOR_HEIGHT, false, false);
			}
			else if (i == 1 || i == 2 || i == 3 || i == 4){
				img = new Image(pathImage(IMAGE_FILES[i]), BLOB_WIDTH, BLOB_HEIGHT, false, false);
			}
			else if(i == 6){
			    img = new Image(pathImage(IMAGE_FILES[i]),GOLDEN_EGG_WIDTH,GOLDEN_EGG_HEIGHT, false, false);
			}
			else if(i == 9){
                img = new Image(pathImage(IMAGE_FILES[i]),POINTS_EGG_WIDTH,POINTS_EGG_HEIGHT, false, false);
            }
			else if(i == 7 || i == 8){
                img = new Image(pathImage(IMAGE_FILES[i]),PIPE_WIDTH,PIPE_HEIGHT, false, false);
            }
			else if(i == 10){
                img = new Image(pathImage(IMAGE_FILES[i]),SNOWMAN_WIDTH,SNOWMAN_HEIGHT, false, false);
            }
			else {
				img = new Image(pathImage(IMAGE_FILES[i]), SCENE_WIDTH, SCENE_HEIGHT, false, false);
			}
    		IMAGE.put(IMAGE_FILES[i],img);
    		//System.out.println(i + " " + IMAGE_FILES[i]);
    		
    	}
		resizeImage(IMAGE_FILES[6], 130, 130);
		resizeImage(IMAGE_FILES[9], 70, 70);
		
		resizeImage(IMAGE_FILES[1], 50, 50);
		resizeImage(IMAGE_FILES[2], 50, 50);
		resizeImage(IMAGE_FILES[3], 50, 50);
		resizeImage(IMAGE_FILES[4], 50, 50);
		// initialize image views
		for(int i=0; i<IMAGE_FILES.length; i++) {
    		ImageView imgView = new ImageView(IMAGE.get(IMAGE_FILES[i]));
    		IMVIEW.put(IMAGE_FILES[i],imgView);
    	}
		
		  // initialize audios
        for(int i=0; i<AUDIO_FILES.length; i++) {
            AudioClip sound;
            sound = new AudioClip(pathAudio(AUDIO_FILES[i]));       
            AUDIO.put(AUDIO_FILES[i],sound);
        }
		
		// initialize scene nodes
		startButton = new Button("Go!");
		
		//initialize the game levels as Radio buttons and adding them to a toggle group
		level_group = new ToggleGroup();
		easyButton = new RadioButton("Easy");
		mediumButton = new RadioButton("Medium");
		hardButton = new RadioButton("Hard");
		
		gameOver = new Label ("Game over");
		startNewGame = new Label("Start Game");
		
		easyButton.setToggleGroup(level_group);
		mediumButton.setToggleGroup(level_group);
	    hardButton.setToggleGroup(level_group);
	    
		//Setting the default level to easy
		easyButton.setSelected(true);	
		scoreText = new Text (10, 20, "This is a text sample");
		lifeText = new Text (10, 20, "this is life text ");
		automodeText = new Text(10,20, "");
		
		// Setting up the instruction description
		bonusPoints= new Text(10, 20,"Bonus points");
		snoozeEgg= new Text(10, 20,"Lets you snooze");
		avoidSnowman= new Text (10, 20,"Avoid snowman");
	}
	
	public String pathImage(String filepath) {
    	String fullpath = getClass().getResource(IMAGE_DIR+filepath+".png").toExternalForm();
    	return fullpath;
    }
	
	public String pathAudio(String filepath) {
        String fullpath = getClass().getResource(AUDIO_DIR+filepath+".mp3").toExternalForm();
        return fullpath;
    }
	
	public Image resizeImage(String filepath, int width, int height) {
    	IMAGE.put(filepath, new Image(pathImage(filepath), width, height, false, false));
    	return IMAGE.get(filepath);
    }
	

	
	
}
