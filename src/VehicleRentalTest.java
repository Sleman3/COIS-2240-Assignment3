import static org.junit.jupiter.api.Assertions.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import org.junit.jupiter.api.Test;

public class VehicleRentalTest {

    @Test
    public void testSingletonRentalSystem() throws Exception {

        // get declared constructor
        Constructor<RentalSystem> cons = RentalSystem.class.getDeclaredConstructor();

        // check if private
        int mods = cons.getModifiers();
        assertTrue(Modifier.isPrivate(mods), "Constructor must be PRIVATE");

        // test getInstance
        RentalSystem rs1 = RentalSystem.getInstance();
        RentalSystem rs2 = RentalSystem.getInstance();

        assertNotNull(rs1, "Singleton instance should not be null");
        assertSame(rs1, rs2, "Both calls must return the SAME instance");
    }
}
