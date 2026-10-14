package za.co.wethinkcode.courier.dispatch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;
import static za.co.wethinkcode.courier.dispatch.Types.asParcel;

class ParcelTest {

    private Parcel parcel;

    @BeforeEach
    void setUp() {
        parcel = asParcel(new StandardParcel("SP-1001", "Nomsa Khumalo", "12 Jan Smuts Ave", 2.5));
    }

    @Test
    void parcelIsAbstract() {
        assertTrue(Modifier.isAbstract(Parcel.class.getModifiers()),
                "Parcel should be abstract - every parcel is a specific type");
    }

    @Test
    void parcelDeclaresAbstractPricingMethods() throws NoSuchMethodException {
        assertTrue(Modifier.isAbstract(Parcel.class.getDeclaredMethod("deliveryFee").getModifiers()));
        assertTrue(Modifier.isAbstract(Parcel.class.getDeclaredMethod("deliveryDays").getModifiers()));
        assertTrue(Modifier.isAbstract(Parcel.class.getDeclaredMethod("serviceLevel").getModifiers()));
    }

    @Test
    void allParcelFieldsArePrivate() throws NoSuchFieldException {
        for (String name : new String[]{"trackingNumber", "senderName", "recipientAddress", "weightKg", "delivered"}) {
            assertTrue(Modifier.isPrivate(Parcel.class.getDeclaredField(name).getModifiers()), name + " should be private");
        }
    }

    @Test
    void trackingNumberAndSenderAreImmutable() throws NoSuchFieldException {
        assertTrue(Modifier.isFinal(Parcel.class.getDeclaredField("trackingNumber").getModifiers()));
        assertTrue(Modifier.isFinal(Parcel.class.getDeclaredField("senderName").getModifiers()));
    }

    @Test
    void constructorStoresValues() {
        assertEquals("SP-1001", parcel.getTrackingNumber());
        assertEquals("Nomsa Khumalo", parcel.getSenderName());
        assertEquals("12 Jan Smuts Ave", parcel.getRecipientAddress());
        assertEquals(2.5, parcel.getWeightKg(), 0.001);
    }

    @Test
    void newParcelIsNotDelivered() {
        assertFalse(parcel.isDelivered());
    }

    @Test
    void markDeliveredMarksParcelAsDelivered() {
        parcel.markDelivered();
        assertTrue(parcel.isDelivered());
    }

    @Test
    void constructorRejectsZeroOrNegativeWeight() {
        assertThrows(IllegalArgumentException.class,
                () -> new StandardParcel("SP-1", "A", "B", 0));
        assertThrows(IllegalArgumentException.class,
                () -> new StandardParcel("SP-2", "A", "B", -1));
    }

    @Test
    void updateWeightRejectsZeroOrNegativeWeight() {
        assertThrows(IllegalArgumentException.class, () -> parcel.updateWeightKg(0));
        assertEquals(2.5, parcel.getWeightKg(), 0.001, "a rejected update must not change the weight");
    }

    @Test
    void updateWeightChangesWeight() {
        parcel.updateWeightKg(4.0);
        assertEquals(4.0, parcel.getWeightKg(), 0.001);
    }

    @Test
    void updateRecipientAddressChangesAddress() {
        parcel.updateRecipientAddress("7 Oxford Rd");
        assertEquals("7 Oxford Rd", parcel.getRecipientAddress());
    }

    @Test
    void cannotChangeAddressOnceDelivered() {
        parcel.markDelivered();
        assertThrows(IllegalStateException.class, () -> parcel.updateRecipientAddress("7 Oxford Rd"));
        assertEquals("12 Jan Smuts Ave", parcel.getRecipientAddress());
    }

    @Test
    void toStringIncludesTrackingNumberAndSender() {
        String text = parcel.toString();
        assertTrue(text.contains("SP-1001"));
        assertTrue(text.contains("Nomsa Khumalo"));
    }
}
