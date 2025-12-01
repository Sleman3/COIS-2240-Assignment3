import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

public class VehicleRentalTest {

	// ============================
	// Task 2.1 – License plate test
	// ============================
	@Test
	public void testLicensePlate_vai() {

	    // use a simple concrete Vehicle (Car)
	    Vehicle v = new Car("Toyota", "Corolla", 2019, 5);

	    // ---------- valid plates ----------
	    assertDoesNotThrow(() -> v.setLicensePlate("AAA100"));
	    assertEquals("AAA100", v.getLicensePlate());

	    assertDoesNotThrow(() -> v.setLicensePlate("ABC567"));
	    assertEquals("ABC567", v.getLicensePlate());

	    assertDoesNotThrow(() -> v.setLicensePlate("ZZZ999"));
	    assertEquals("ZZZ999", v.getLicensePlate());

	    // ---------- invalid plates ----------
	    assertThrows(IllegalArgumentException.class,
	            () -> v.setLicensePlate(""));
	    assertThrows(IllegalArgumentException.class,
	            () -> v.setLicensePlate(null));
	    assertThrows(IllegalArgumentException.class,
	            () -> v.setLicensePlate("AAA1000"));
	    assertThrows(IllegalArgumentException.class,
	            () -> v.setLicensePlate("ZZZ99"));
	}


    // =======================================
    // Task 2.2 – Rent / Return behavior test
    // =======================================
    @Test
    public void testRentAndReturnVehicle_vai() {
        RentalSystem system_vai = RentalSystem.getInstance();

        // fresh vehicle + customer just for the test
        Vehicle vehicle_vai = new Car("Honda", "Civic", 2020, 4);
        vehicle_vai.setLicensePlate("TST123"); // valid format

        Customer customer_vai = new Customer(9999, "Test User");

        system_vai.addVehicle(vehicle_vai);
        system_vai.addCustomer(customer_vai);

        // initially available
        assertEquals(Vehicle.VehicleStatus.Available, vehicle_vai.getStatus());

        // first rent should succeed
        boolean firstRent_vai =
                system_vai.rentVehicle(vehicle_vai, customer_vai, LocalDate.now(), 100.0);
        assertTrue(firstRent_vai);
        assertEquals(Vehicle.VehicleStatus.Rented, vehicle_vai.getStatus());

        // second rent should fail (already rented)
        boolean secondRent_vai =
                system_vai.rentVehicle(vehicle_vai, customer_vai, LocalDate.now(), 50.0);
        assertFalse(secondRent_vai);

        // first return should succeed
        boolean firstReturn_vai =
                system_vai.returnVehicle(vehicle_vai, customer_vai, LocalDate.now(), 0.0);
        assertTrue(firstReturn_vai);
        assertEquals(Vehicle.VehicleStatus.Available, vehicle_vai.getStatus());

        // second return should fail (already available)
        boolean secondReturn_vai =
                system_vai.returnVehicle(vehicle_vai, customer_vai, LocalDate.now(), 0.0);
        assertFalse(secondReturn_vai);
    }

    // ======================================
    // Task 2.3 – Singleton validation (done)
    // ======================================
    @Test
    public void testSingletonRentalSystem() throws Exception {
        // constructor should be private
        Constructor<RentalSystem> cons =
                RentalSystem.class.getDeclaredConstructor();
        int mods = cons.getModifiers();
        assertTrue(Modifier.isPrivate(mods), "Constructor must be PRIVATE");

        // getInstance must always return same object
        RentalSystem rs1 = RentalSystem.getInstance();
        RentalSystem rs2 = RentalSystem.getInstance();

        assertNotNull(rs1, "Singleton instance should not be null");
        assertSame(rs1, rs2, "Both calls must return the SAME instance");
    }
}
