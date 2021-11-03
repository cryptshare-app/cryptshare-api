package test.uni.share.iota.boundary;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.uni.share.iota.boundary.IotaBF;
import com.uni.share.user.types.UserBE;
import static org.junit.Assert.*;

public class IotaBFTest {

    private String seed;


    @Mock
    UserBE userBE;


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        seed = new IotaBF().generateSeed();
    }


    @Test
    public void testGenSeed() {
        assertEquals(seed.length(), 81); //length needs to be 81 chars
        assertTrue(seed.matches("[A-Z9]*")); //containing only uppercase letters and 9
        assertTrue(seed.matches("[A-Z9]{81}")); //both tests combined
    }


}
