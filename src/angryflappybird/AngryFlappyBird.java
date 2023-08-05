package angryflappybird;

import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.effect.DropShadow;

import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Random;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;

//import java.awt.Color;
import java.awt.Label;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.time.temporal.ChronoUnit;

//The Application layer
public class AngryFlappyBird extends Application {

    private Defines DEF = new Defines();

    // time related attributes
    private long clickTime, startTime, elapsedTime;
    private AnimationTimer timer;

    // game components:
    private Sprite blob; // the bird in our game
    private Sprite snowman; // the attacker
    private ArrayList<Sprite> pipes;
    private ArrayList<Sprite> goldenEggs;
    private Sprite point_egg; // the bonus point egg
    private Sprite golden_egg; // the egg with the snooze feature
    private ArrayList<Sprite> floors;
    public int score = 0;
    public int life = 3;
    private int selectedLevel = 1;

    private long startGame;

    // game flags
    private boolean CLICKED, GAME_START, GAME_OVER;
    private boolean PointEggCollision;
    private boolean pipeCollision;
    private boolean GoldenEggCollision;
    private boolean autoMode;
    private boolean SnowmanEggCollision;
    
    // scene graphs
    private Group gameScene; // the left half of the scene
    private VBox gameControl; // the right half of the GUI (control)
    private GraphicsContext gc;

    // golden egg autopilot
    private Timeline trackTime;
    private long snoozeStart;
    private int remain;

    // the mandatory main method
    public static void main(String[] args) {
        launch(args);
    }

    //
    /***
     * the start method sets the Stage layer
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        // initialize scene graphs and UIs
        resetGameControl(); // resets the gameControl
        resetGameScene(true); // resets the gameScene

        HBox root = new HBox();
        HBox.setMargin(gameScene, new Insets(0, 0, 0, 15));

        DropShadow ds = new DropShadow();
        ds.setOffsetY(3.0f);
        ds.setColor(Color.color(0.4f, 0.4f, 0.4f));
        DEF.startNewGame.setText("Press Go to Start!");
        DEF.startNewGame.setFont(
                Font.font("Impact", FontWeight.BOLD, FontPosture.REGULAR, 40));
        DEF.startNewGame.setEffect(ds);
        DEF.startNewGame.setCache(true);
        DEF.startNewGame.setTextFill(Color.WHITE);
        DEF.startNewGame.setLayoutX(65);
        DEF.startNewGame.setLayoutY(245);

        root.getChildren().add(gameScene);
        root.getChildren().add(gameControl);
        gameScene.getChildren().add(DEF.startNewGame);

        // add scene graphs to scene
        Scene scene = new Scene(root, DEF.APP_WIDTH, DEF.APP_HEIGHT);

        // finalize and show the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle(DEF.STAGE_TITLE);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /***
     * @Param MouseEvent the getContent method sets the Scene layer
     */
    private void resetGameControl() {

        DEF.startButton.setOnMouseClicked(this::mouseClickHandler);

        // Sets the buttons for choosing the level of the game
        gameControl = new VBox();
        gameControl.getChildren().addAll(DEF.startButton);
        gameControl.getChildren().addAll(DEF.easyButton, DEF.mediumButton,
                DEF.hardButton);

        gameControl.getChildren().addAll(DEF.IMVIEW.get("pointsEgg"),
                DEF.bonusPoints);
        gameControl.getChildren().addAll(DEF.IMVIEW.get("goldenEgg"),
                DEF.snoozeEgg);
        gameControl.getChildren().addAll(DEF.IMVIEW.get("snowman"),
                DEF.avoidSnowman);

        // .addAll(DEF.resizeImage(DEF.pathImage("pointsEgg"), DEF.bonusPoints))
    }

    /***
     * @Param MouseEvent Manage game over and game start
     */
    private void mouseClickHandler(MouseEvent e) {
        gameScene.setCursor(Cursor.HAND);
        if (GAME_OVER) {
            if (life == 0) {
                life = 3;
                score = 0;
                DEF.scoreText.setText("Score is: " + score);
                DEF.scoreText.setFont(Font.font("Verdana", 20));
                DEF.lifeText.setText(life + " lives left");
                DEF.lifeText.setFont(Font.font("Verdana", 20));
            }
            
            resetGameScene(false);
        } else if (GAME_START) {
            clickTime = System.nanoTime();
        }
        GAME_START = true;
        CLICKED = true;
    }

    /***
     * Reset the game scene. Initializes the blob, snowman, two types of eggs,
     * and pipes.
     */
    private void resetGameScene(boolean firstEntry) {

        // reset variables
        CLICKED = false;
        GAME_OVER = false;
        GAME_START = false;
        floors = new ArrayList<>();
        pipes = new ArrayList<>();
        goldenEggs = new ArrayList<>();
        autoMode = false;

        if (firstEntry) {
            // create two canvases
            Canvas canvas = new Canvas(DEF.SCENE_WIDTH, DEF.SCENE_HEIGHT);
            gc = canvas.getGraphicsContext2D();

            // create a background
            ImageView background = DEF.IMVIEW.get("background");
            ImageView backgroundNight = DEF.IMVIEW.get("backgroundNight");

            // create the game scene
            gameScene = new Group();
            DEF.scoreText.setText("Score is: " + score);
            DEF.scoreText.setFont(Font.font("Verdana", 20));
            DEF.lifeText.setText(life + " lives left");
            DEF.lifeText.setFont(Font.font("Verdana", 20));
            // DEF.lifeText.setFill(Color.RED);
            DEF.lifeText.setX(DEF.scoreText.getX() + 280);
            DEF.lifeText.setY(DEF.scoreText.getY() + 530);

            gameScene.getChildren().addAll(DEF.IMVIEW.get("background"),
                    DEF.IMVIEW.get("backgroundNight"), canvas, DEF.scoreText,
                    DEF.lifeText, DEF.automodeText);
            DEF.IMVIEW.get("backgroundNight").setVisible(false);
            // click anywhere on the screen to fly the penguin
            gameScene.setOnMouseClicked(this::mouseClickHandler);

        }

        // initialize floor
        for (int i = 0; i < DEF.FLOOR_COUNT; i++) {

            int posX = i * DEF.FLOOR_WIDTH;
            int posY = DEF.SCENE_HEIGHT - DEF.FLOOR_HEIGHT;

            Sprite floor = new Sprite(posX, posY, DEF.IMAGE.get("floor"));
            floor.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
            floor.render(gc);

            floors.add(floor);
        }

        // initialize the pipes
        for (int i = 0; i < DEF.PIPE_COUNT; i++) {

            int posX = i * DEF.PIPE_WIDTH * 2 + DEF.SCENE_WIDTH;
            int posY = new Random()
                    .nextInt(DEF.PIPE_MAX_HEIGHT - DEF.PIPE_MIN_HEIGHT + 1)
                    + DEF.PIPE_MIN_HEIGHT;

            Sprite pipeUp = new Sprite(posX, posY, DEF.IMAGE.get("pipeUp"));
            Sprite pipeDown = new Sprite(posX, posY + 400,
                    DEF.IMAGE.get("pipeDown"));

            pipeUp.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
            pipeUp.render(gc);
            pipes.add(pipeUp);

            pipeDown.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
            pipeDown.render(gc);
            pipes.add(pipeDown);
        }

        // initialize the points egg
        for (int i = 0; i < DEF.PIPE_COUNT; i++) {

            int posX = i * DEF.PIPE_WIDTH * 2 + DEF.SCENE_WIDTH;
            int posY = 0;
            point_egg = new Sprite(posX, posY, DEF.IMAGE.get("pointsEgg"));
            point_egg.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
            point_egg.render(gc);
        }

        // initialize the golden egg
        for (int i = 0; i < DEF.PIPE_COUNT; i++) {

            int posX = i * DEF.PIPE_WIDTH * 2 + DEF.SCENE_WIDTH;
            int posY = 0;
            golden_egg = new Sprite(posX, posY, DEF.IMAGE.get("goldenEgg"));
            golden_egg.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
            golden_egg.render(gc);

        }

        // initialize the snowman
        snowman = new Sprite(DEF.SNOWMAN_POS_X, DEF.SNOWMAN_POS_Y,
                DEF.IMAGE.get("snowman"));
        snowman.setVelocity(DEF.SCENE_SHIFT_INCR, DEF.BLOB_DROP_VEL);
        snowman.render(gc);

        // initialize blob
        blob = new Sprite(DEF.BLOB_POS_X, DEF.BLOB_POS_Y,
                DEF.IMAGE.get("blob0"));
        blob.render(gc);

        // initialize timer
        startTime = System.nanoTime();
        timer = new MyTimer();
        timer.start();
        startGame = System.nanoTime();
    }

   
    class MyTimer extends AnimationTimer {

        int counter = 0;

        @Override
        public void handle(long now) {
            // time keeping
            elapsedTime = now - startTime;
            startTime = now;

            long timeSinceGame = System.nanoTime() - startGame;
            for (int i = 1; i < 50; i += 2) {
                if (timeSinceGame * DEF.NANOSEC_TO_SEC > 4 * i
                        && timeSinceGame * DEF.NANOSEC_TO_SEC < 4 * (i + 1)) {
                    DEF.IMVIEW.get("background").setVisible(false);
                    DEF.IMVIEW.get("backgroundNight").setVisible(true);
                } else if (timeSinceGame * DEF.NANOSEC_TO_SEC >= 4 * (i + 1)
                        && timeSinceGame * DEF.NANOSEC_TO_SEC < 4 * (i + 2)) {
                    DEF.IMVIEW.get("backgroundNight").setVisible(false);
                    DEF.IMVIEW.get("background").setVisible(true);
                    break;
                }
            }

            // clear current scene
            gc.clearRect(0, 0, DEF.SCENE_WIDTH, DEF.SCENE_HEIGHT);

            if (GAME_START && !autoMode) {
                gameScene.getChildren().remove(DEF.startNewGame);
                gameScene.getChildren().remove(DEF.gameOver);
                
                // sets the level of the game
                setLevel();
                // step1: update floor
                moveFloor();

                // step3: update pipe
                movePipe();

                // step 4: places the extra points egg
                placeBonusEgg();
                placeGoldenEgg();
                // step 5: places the snowman
                placeSnowman();

                // step2: update blo
                moveBlob();

                // step 6: checks for collision
                checkCollision();
            }
            if (GAME_START && autoMode) {
                gameScene.getChildren().remove(DEF.startNewGame);
                gameScene.getChildren().remove(DEF.gameOver);
                // sets the level of the game
                setLevel();
                // step1: update floor
                moveFloor();

                // step2: autoPilot blob
                autoModeBlob();
                autoMode();

                // step3: update pipe
                movePipe();

                // step 4: places the extra points egg
                placeBonusEgg();
                placeGoldenEgg();

            }
        }

        /***
         * Sets the level of the game. sets the speeds for the different levels
         * of the game
         */
        private void setLevel() {
            if (DEF.easyButton.isSelected()) {
                selectedLevel = 1;
                DEF.PIPE_VELOCITY = DEF.SCENE_SHIFT_TIME;
            } else if (DEF.mediumButton.isSelected()) {
                selectedLevel = 2;
                DEF.PIPE_VELOCITY = DEF.SCENE_SHIFT_TIME + 2;
            } else {
                selectedLevel = 3;
                DEF.PIPE_VELOCITY = DEF.SCENE_SHIFT_TIME + 8;
            }
        }

        // step1: update floor
        /***
         * Floor moves at a certain speed
         */
        private void moveFloor() {
            for (int i = 0; i < DEF.FLOOR_COUNT; i++) {
                if (floors.get(i).getPositionX() <= -DEF.FLOOR_WIDTH) {
                    double nextX = floors.get((i + 1) % DEF.FLOOR_COUNT)
                            .getPositionX() + DEF.FLOOR_WIDTH;
                    double nextY = DEF.SCENE_HEIGHT - DEF.FLOOR_HEIGHT;
                    floors.get(i).setPositionXY(nextX, nextY);
                }
                floors.get(i).render(gc);
                floors.get(i).update(DEF.SCENE_SHIFT_TIME);
            }
        }

        /***
         * step2: update blob blob flies upward with animation if the user
         * clicked. blob drops after a period of time without bottun click
         */
        private void moveBlob() {
            long diffTime = System.nanoTime() - clickTime;

            if (pipeCollision) {
                // blob fly backwards
                blob.setVelocity(-1000, 1000);
            } else {
                // blob flies upward with animation
                if (CLICKED && diffTime <= DEF.BLOB_DROP_TIME) {
                    int imageIndex = Math.floorDiv(counter++,
                            DEF.BLOB_IMG_PERIOD);
                    imageIndex = Math.floorMod(imageIndex, DEF.BLOB_IMG_LEN);
                    blob.setImage(
                            DEF.IMAGE.get("blob" + String.valueOf(imageIndex)));
                    blob.setVelocity(0, DEF.BLOB_FLY_VEL);
                }

                // blob drops after a period of time without button click
                else {
                    blob.setVelocity(0, DEF.BLOB_DROP_VEL);
                    CLICKED = false;

                }
            }
            // render blob on GUI
            blob.update(elapsedTime * DEF.NANOSEC_TO_SEC);
            blob.render(gc);
        }

        /***
         * step3: update pipe Place pipe in even intervals, and move them
         * backward according to the difficulty level. When the blob passes
         * through the pipes, the blob gets two points
         */
        private void movePipe() {
            for (int i = 0; i < DEF.PIPE_COUNT; i++) {
                Sprite pipeUp = pipes.get(i * 2);
                Sprite pipeDown = pipes.get(i * 2 + 1);
                if (pipeUp.getPositionX() <= -DEF.PIPE_WIDTH) {
                    double nextX = pipes.get((i + 1) % DEF.PIPE_COUNT * 2)
                            .getPositionX() + 300;
                    double nextY = new Random().nextInt(
                            DEF.PIPE_MAX_HEIGHT - DEF.PIPE_MIN_HEIGHT + 1)
                            + DEF.PIPE_MIN_HEIGHT;
                    pipeUp.setPositionXY(nextX, nextY);
                    pipeDown.setPositionXY(nextX, nextY + 400);
                }
                if (pipeDown.getPositionY() < 360) {
                    pipeDown.setPositionY(370);
                }
                pipeDown.render(gc);
                pipeUp.render(gc);
                pipeDown.update(DEF.PIPE_VELOCITY);
                pipeUp.update(DEF.PIPE_VELOCITY);

                // sets the speeds for the different levels of the game
//                if (selectedLevel == 1) {
//                    pipeDown.update(DEF.SCENE_SHIFT_TIME);
//                    pipeUp.update(DEF.SCENE_SHIFT_TIME);}
//                else if (selectedLevel == 2) {
//                    pipeDown.update(DEF.SCENE_SHIFT_TIME + 2);
//                    pipeUp.update(DEF.SCENE_SHIFT_TIME + 2);}
//                else if (selectedLevel == 3){
//                    pipeDown.update(-DEF.SCENE_SHIFT_TIME + 4);
//                    pipeUp.update(-DEF.SCENE_SHIFT_TIME + 4);}  

                // passing the pipes and updating the score
                if (blob.getPositionX() >= pipeUp.getPositionX()
                        && blob.getPositionX() <= (pipeUp.getPositionX() + 1)) {
                    score = score + 2;
                    DEF.scoreText.setText("Score is: " + score);
                    DEF.AUDIO.get("pointsEgg").play();

                }

            }
        }

        // step 4: places the eggs
        /***
         * Place bonus egg on the even number pipe. If the blob hits the egg,
         * score increases by six points.
         */
        private void placeBonusEgg() {
            for (int i = 0; i < DEF.PIPE_COUNT; i++) {

                Sprite pipeDown = pipes.get(i * 2 + 1);
                if (i % 2 == 0) {
                    point_egg.setPositionXY(pipeDown.getPositionX(),
                            -50 + pipeDown.getPositionY());
                    // PointEggCollisionx = false;
                }
                point_egg.render(gc);
                point_egg.update(DEF.SCENE_SHIFT_TIME);

                if (point_egg.getPositionX() < 0) {
                    PointEggCollision = false;
                    // System.out.println("false");
                }
                // updating the score
                if (blob.intersectsSprite(point_egg) && !PointEggCollision) {
                    // System.out.println("hit the egg");
                    score = score + 4;
                    DEF.scoreText.setText("Score is: " + score);
                    // point_egg.setPositionY(1000);
                    PointEggCollision = true;
                    DEF.AUDIO.get("pointsEgg").play();

                }
            }
        }

        /***
         * Place golden egg on the pipe of odd numbers.
         */
        private void placeGoldenEgg() {
            for (int i = 1; i < DEF.PIPE_COUNT; i++) {
                Sprite pipeDown = pipes.get(i * 2 + 1);
                if (i % 2 == 1) {
                    golden_egg.setPositionXY(pipeDown.getPositionX(),
                            pipeDown.getPositionY() - 90);
                }
                golden_egg.setPositionXY(pipeDown.getPositionX(),
                        pipeDown.getPositionY() - 90);
                golden_egg.render(gc);
                golden_egg.update(DEF.SCENE_SHIFT_TIME);

//                         if (golden_egg.getPositionX() < 0) {
//                             GoldenEggCollision = false;
//                         }
                // Automode
                if (blob.intersectsSprite(golden_egg) && !GoldenEggCollision) {
                    // System.out.println("hit the egg");
                    GoldenEggCollision = true;
                    snoozeStart = System.nanoTime(); // when the bird hits a
                                                     // golden egg
                    // System.out.println(snoozeStart);
                    // autoMode();
                    autoMode = true;
                    DEF.AUDIO.get("snooze").play();

                }
            }
        }
    }

    /***
     * Snooze for six seconds and display counting down until the automode ends
     */
    private void autoMode() {
        int timeSinceSnooze = (int) ((System.nanoTime() - snoozeStart)
                * DEF.NANOSEC_TO_SEC);
        // System.out.println(timeSinceSnooze);

        DEF.automodeText.setFont(Font.font("Verdana", 30));
        DEF.automodeText.setX(DEF.scoreText.getX());
        DEF.automodeText.setY(DEF.scoreText.getY() + 40);
        if (timeSinceSnooze == 6) {

            DEF.automodeText.setText(String.valueOf(0) + " seconds left!!");
            // System.out.println("should finish" + timeSinceSnooze);
            autoMode = false;
            GoldenEggCollision = false;
            DEF.automodeText.setText("");
        } else {
            DEF.automodeText.setText(
                    String.valueOf(6 - timeSinceSnooze) + " seconds left!!");
        }

    }

    /***
     * move blob in automode. blob doesn't flap
     */
    private void autoModeBlob() {
        blob.setImage(DEF.IMAGE.get("blob1"));
        blob.setPositionXY(DEF.BLOB_POS_X, 250);
        blob.setVelocity(DEF.BLOB_FLY_VEL, 0);
        blob.update(elapsedTime * DEF.NANOSEC_TO_SEC);
        blob.render(gc);
    }

    /***
     * Place snowman falling from the upper pipe. If the blob hits the snowman,
     * the score goes to zero. If the snowman disappears from the display,
     * snowman's position is set to the top of the screen.
     */
    private void placeSnowman() {
        for (int i = 0; i < pipes.size(); i += 10) {
            double nextY = snowman.getPositionY() - DEF.SCENE_SHIFT_INCR;
            snowman.setPositionXY(pipes.get(i).getPositionX(), nextY);
            snowman.render(gc);
        }

        if (blob.intersectsSprite(snowman)) {
            pipeCollision = true;
            score = 0;
            DEF.scoreText.setText("Score is: " + score);
        }

        if (snowman.getPositionX() < -30) {
            snowman.setPositionXY(DEF.SNOWMAN_POS_X, DEF.SNOWMAN_POS_Y);
        }

        if (snowman.intersectsSprite(point_egg) && !SnowmanEggCollision) {

            SnowmanEggCollision = false;

            if (point_egg.getPositionY() - snowman.getPositionY() >= 69.8) {
                score = score - 5;
                DEF.scoreText.setText("Score is: " + score);

            }
        }

        if (snowman.intersectsSprite(golden_egg) && !SnowmanEggCollision) {
            SnowmanEggCollision = false;

            if (golden_egg.getPositionY() - snowman.getPositionY() >= 69.8) {
                score = score - 5;
                DEF.scoreText.setText("Score is: " + score);

            }
        }

    }

    /***
     * Check collision between blob and floor, snowmans, and pipes. if the blob
     * hits with floor, blob immediately die. if the blob hits with pipes and
     * snowman, change the boolean value that's change the speed in moveblob
     * methods
     */
    public void checkCollision() {

        // check collision
        for (Sprite floor : floors) {
            if (blob.intersectsSprite(floor)){
                score = 0;
                DEF.scoreText.setText("Score is: " + score);
                DEF.scoreText.setFont(Font.font("Verdana", 20));
                
            }
            
            GAME_OVER = GAME_OVER || blob.intersectsSprite(floor);
        }
        for (Sprite pipe : pipes) {
            if (!PointEggCollision && !GoldenEggCollision) {
                // bouncing back
                if (blob.intersectsSprite(pipe)) {
                    pipeCollision = true;
                }
                if (blob.getPositionX() < 0 && pipeCollision) {
                    GAME_OVER = true;
                }
            }
        }
        if (blob.intersectsSprite(snowman)) {
            pipeCollision = true;
        }
        // end the game when blob hit stuff
        if (GAME_OVER) {

            DropShadow ds = new DropShadow();
            ds.setOffsetY(3.0f);
            ds.setColor(Color.color(0.4f, 0.4f, 0.4f));
            DEF.gameOver.setText("GAME OVER");
            DEF.gameOver.setEffect(ds);
            DEF.gameOver.setCache(true);
            DEF.gameOver.setFont(Font.font("Impact", FontWeight.BOLD,
                    FontPosture.REGULAR, 40));
            DEF.gameOver.setTextFill(Color.WHITE);
            DEF.gameOver.setLayoutX(95);
            DEF.gameOver.setLayoutY(260);
            gameScene.getChildren().add(DEF.gameOver);

            // decrease the number of life
            life -= 1;
            pipeCollision = false;
            DEF.lifeText.setText(life + " lives left");
            if (life < 1) {
                // message saying you used all your lives
                // must reset the game with 3 new lives again
                gameScene.getChildren().remove(DEF.gameOver);
                DropShadow ds1 = new DropShadow();
                ds1.setOffsetY(3.0f);
                ds1.setColor(Color.color(0.4f, 0.4f, 0.4f));
                DEF.startNewGame.setText("Lost All Lives. Press Go to Start!");
                DEF.startNewGame.setFont(Font.font("Impact", FontWeight.BOLD,
                        FontPosture.REGULAR, 28));
                DEF.startNewGame.setEffect(ds1);
                DEF.startNewGame.setCache(true);
                DEF.startNewGame.setTextFill(Color.WHITE);
                DEF.startNewGame.setLayoutX(32);
                DEF.startNewGame.setLayoutY(245);
                gameScene.getChildren().add(DEF.startNewGame);
                
            }

            DEF.AUDIO.get("collision").play();
            showHitEffect();
            for (Sprite floor : floors) {
                floor.setVelocity(0, 0);
            }
            timer.stop();
        }
    }

    /***
     * When Gameover = true, the screen flashes
     */
    private void showHitEffect() {
        ParallelTransition parallelTransition = new ParallelTransition();
        FadeTransition fadeTransition = new FadeTransition(
                Duration.seconds(DEF.TRANSITION_TIME), gameScene);
        fadeTransition.setToValue(0);
        fadeTransition.setCycleCount(DEF.TRANSITION_CYCLE);
        fadeTransition.setAutoReverse(true);
        parallelTransition.getChildren().add(fadeTransition);
        parallelTransition.play();
    }
} // End of MyTimer class

// End of AngryFlappyBird Class
