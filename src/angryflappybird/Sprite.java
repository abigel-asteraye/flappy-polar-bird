package angryflappybird;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Sprite {  
	
    private Image image;
    private double positionX;
    private double positionY;
    private double velocityX;
    private double velocityY;
    private double width;
    private double height;
    private String IMAGE_DIR = "../resources/images/";

    
    
    /***
     * Set positions and velocity for both x and y directions
     */
    public Sprite() {
        this.positionX = 0;
        this.positionY = 0;
        this.velocityX = 0;
        this.velocityY = 0;
    }
    
    /***
     * Set image position
     */
    public Sprite(double pX, double pY, Image image) {
    	setPositionXY(pX, pY);
        setImage(image);
        this.velocityX = 0;
        this.velocityY = 0;
    }

    /***
     * Set the image 
     * @param image
     */
    public void setImage(Image image) {
        this.image = image;
        this.width = image.getWidth();
        this.height = image.getHeight();
    }
    /***
     * Set the image 
     * @param positionX
     * @param positionY
     */
    public void setPositionXY(double positionX, double positionY) {
        this.positionX = positionX;
        this.positionY = positionY;
    }
    
    /***
     * @return positionY
     */
    public void setPositionY(double positionY) {
        this.positionY = positionY;
    }
    
    /***
     * @return positionX
     */
    public double getPositionX() {
        return positionX;
    }

    /***
     * @return positionY
     */
    public double getPositionY() {
        return positionY;
    }
    

    /**
     * @Param velocity of x direction
     * @Param velocity of y direction
     * 
     */
    public void setVelocity(double velocityX, double velocityY) {
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }

    /**
     * @param velocity of x direction
     * @param velocity of y direction
     * set the velocity of x and y
     */
    public void addVelocity(double x, double y) {
        this.velocityX += x;
        this.velocityY += y;
    }

    /**
     * @return velocity of x direction
     */
    public double getVelocityX() {
        return velocityX;
    }

    /**
     * @return velocity
     */
    public double getVelocityY() {
        return velocityY;
    }

    /**
     * @return width
     */
    public double getWidth() {
        return width;
    }

    /**
     * Display selected image
     * @param gc
     */
    public void render(GraphicsContext gc) {
        gc.drawImage(image, positionX, positionY);
    }

    
    /**
     * @return boundary position of 2D images
     */
    public Rectangle2D getBoundary() {
        return new Rectangle2D(positionX, positionY, width, height);
    }

    /**
     * 
     * @param s
     * @return
     */
    public boolean intersectsSprite(Sprite s) {
        return s.getBoundary().intersects(this.getBoundary());
    }

    /***
     * Update the x and y position
     * @param time
     */
    public void update(double time) {
        positionX += velocityX * time;
        positionY += velocityY * time;
    }
}
