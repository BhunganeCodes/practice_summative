package za.co.wethinkcode.courier.dispatch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static za.co.wethinkcode.courier.dispatch.Types.asParcel;

class DispatchCentreTest {

    private DispatchCentre centre;
    private Parcel standard;
    private Parcel express;
    private Parcel fragile;

    @BeforeEach
    void setUp() {
        centre = new DispatchCentre("Midrand Hub");
        standard = asParcel(new StandardParcel("SP-1", "Nomsa Khumalo", "Johannesburg", 2.5));     // fee 80
        express = asParcel(new ExpressParcel("SP-2", "Pieter van Wyk", "Cape Town", 2.0, 4_000));    // fee 144, premium 60
        fragile = asParcel(new FragileParcel("SP-3", "Ayesha Patel", "Durban", 3.0, 12_500, "Glass")); // fee 120, premium 375
    }

    private void addAll() {
        centre.addParcel(standard);
        centre.addParcel(express);
        centre.addParcel(fragile);
    }

    @Test
    void centreNameIsStored() {
        assertEquals("Midrand Hub", centre.getCentreName());
    }

    @Test
    void newCentreHasNoParcels() {
        assertTrue(centre.getParcels().isEmpty());
    }

    @Test
    void addAndFindParcel() {
        addAll();
        assertSame(express, centre.findParcel("SP-2"));
        assertEquals(3, centre.getParcels().size());
    }

    @Test
    void findReturnsNullWhenMissing() {
        assertNull(centre.findParcel("SP-404"));
    }

    @Test
    void addRejectsDuplicateTrackingNumber() {
        centre.addParcel(standard);
        Parcel duplicate = asParcel(new StandardParcel("SP-1", "Someone Else", "Polokwane", 1.0));
        assertThrows(IllegalArgumentException.class, () -> centre.addParcel(duplicate));
        assertEquals(1, centre.getParcels().size());
    }

    @Test
    void getParcelsIsUnmodifiable() {
        addAll();
        assertThrows(UnsupportedOperationException.class, () -> centre.getParcels().add(standard));
        assertEquals(3, centre.getParcels().size());
    }

    @Test
    void insurableParcelsExcludesStandardParcels() {
        addAll();
        List<Insurable> insurable = centre.insurableParcels();
        assertEquals(2, insurable.size());
        assertTrue(insurable.contains(express));
        assertTrue(insurable.contains(fragile));
    }

    @Test
    void totalDeliveryRevenueSumsAllFees() {
        addAll();
        assertEquals(80.0 + 144.0 + 120.0, centre.totalDeliveryRevenue(), 0.001);
    }

    @Test
    void totalsAreZeroForEmptyCentre() {
        assertEquals(0.0, centre.totalDeliveryRevenue(), 0.001);
        assertEquals(0.0, centre.totalInsurancePremiums(), 0.001);
    }

    @Test
    void totalInsurancePremiumsSumsInsurableParcelsOnly() {
        addAll();
        assertEquals(60.0 + 375.0, centre.totalInsurancePremiums(), 0.001);
    }
}
