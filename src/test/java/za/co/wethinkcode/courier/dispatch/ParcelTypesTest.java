package za.co.wethinkcode.courier.dispatch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;
import static za.co.wethinkcode.courier.dispatch.Types.asParcel;

class ParcelTypesTest {

    private StandardParcel standard;
    private ExpressParcel express;
    private FragileParcel fragile;

    @BeforeEach
    void setUp() {
        standard = new StandardParcel("SP-1", "Nomsa Khumalo", "Johannesburg", 2.5);
        express = new ExpressParcel("SP-2", "Pieter van Wyk", "Cape Town", 2.0, 4_000);
        fragile = new FragileParcel("SP-4", "Ayesha Patel", "Durban", 3.0, 12_500, "Glassware");
    }

    @Test
    void allTypesAreParcels() {
        assertInstanceOf(Parcel.class, standard);
        assertInstanceOf(Parcel.class, express);
        assertInstanceOf(Parcel.class, fragile);
    }

    // --- StandardParcel ---

    @Test
    void standardFeeIsBasePlusPerKg() {
        assertEquals(StandardParcel.BASE_FEE + 2.5 * StandardParcel.FEE_PER_KG, standard.deliveryFee(), 0.001);
        assertEquals(80.0, standard.deliveryFee(), 0.001);
    }

    @Test
    void standardDeliveryDaysAndServiceLevel() {
        assertEquals(5, standard.deliveryDays());
        assertEquals("Standard", standard.serviceLevel());
    }

    @Test
    void standardFeeFollowsWeightChanges() {
        asParcel(standard).updateWeightKg(10);
        assertEquals(140.0, standard.deliveryFee(), 0.001);
    }

    @Test
    void standardToStringIncludesServiceLevel() {
        assertTrue(standard.toString().contains("Standard"));
        assertTrue(standard.toString().contains("SP-1"));
    }

    // --- ExpressParcel ---

    @Test
    void expressFeeIsBasePlusPerKg() {
        assertEquals(144.0, express.deliveryFee(), 0.001);
    }

    @Test
    void expressDeliveryDaysAndServiceLevel() {
        assertEquals(1, express.deliveryDays());
        assertEquals("Express", express.serviceLevel());
    }

    @Test
    void expressDeclaredValueIsPrivateAndImmutable() throws NoSuchFieldException {
        int modifiers = ExpressParcel.class.getDeclaredField("declaredValue").getModifiers();
        assertTrue(Modifier.isPrivate(modifiers) && Modifier.isFinal(modifiers));
    }

    @Test
    void expressToStringIncludesServiceLevel() {
        assertTrue(express.toString().contains("Express"));
        assertTrue(express.toString().contains("SP-2"));
    }

    // --- FragileParcel ---

    @Test
    void fragileFeeIsBasePlusPerKg() {
        assertEquals(120.0, fragile.deliveryFee(), 0.001);
    }

    @Test
    void fragileDeliveryDaysAndServiceLevel() {
        assertEquals(3, fragile.deliveryDays());
        assertEquals("Fragile", fragile.serviceLevel());
    }

    @Test
    void fragileHandlingNotesAreStored() {
        assertEquals("Glassware", fragile.getHandlingNotes());
    }

    @Test
    void fragileToStringIncludesHandlingNotes() {
        assertTrue(fragile.toString().contains("Glassware"));
    }

    // --- Through a Parcel reference ---

    @Test
    void serviceLevelThroughParcelReference() {
        Parcel[] parcels = {asParcel(standard), asParcel(express), asParcel(fragile)};
        String[] expected = {"Standard", "Express", "Fragile"};
        for (int i = 0; i < parcels.length; i++) {
            assertEquals(expected[i], parcels[i].serviceLevel());
        }
    }
}
