package angryflappybird;

import static org.junit.Assert.assertEquals;
//import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class SpriteTest {
	
	Sprite birdy = new Sprite();
	
	@BeforeEach
	
	void setUp() throws Exception {
        //new initialization
		Sprite birdy = new Sprite();
}


	@Test
	void testPositionY() {
		birdy.setPositionXY(10.0, 13.0);
		System.out.println(birdy.getPositionY());
		//absolutely the same so error tolerance is 0
		assertEquals(13.0, birdy.getPositionY(), 0);
	}
	
	@Test
	void testVelocityX() {
		birdy.setVelocity(50, 40);
		
		assertEquals(50, birdy.getVelocityX(), 0);
	}

}
