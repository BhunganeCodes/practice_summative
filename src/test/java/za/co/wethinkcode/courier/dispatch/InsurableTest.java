package za.co.wethinkcode.courier.dispatch;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static za.co.wethinkcode.courier.dispatch.Types.asInsurable;

class InsurableTest {

    @Test
    void onlyExpressAndFragileAreInsurable() {
        assertTrue(Insurable.class.isAssignableFrom(ExpressParcel.class));
        assertTrue(Insurable.class.isAssignableFrom(FragileParcel.class));
        assertFalse(Insurable.class.isAssignableFrom(StandardParcel.class),
                "StandardParcel does not carry declared-value cover");
        assertFalse(Insurable.class.isAssignableFrom(Parcel.class),
                "Parcel itself should not be Insurable");
    }

    @Test
    void expressPremiumIsRateTimesDeclaredValue() {
        Insurable express = asInsurable(new ExpressParcel("SP-2", "A", "B", 1.0, 4_000));
        assertEquals(4_000, express.getDeclaredValue(), 0.001);
        assertEquals(60.0, express.insurancePremium(), 0.001);
    }

    @Test
    void fragilePremiumIsRateTimesDeclaredValue() {
        Insurable fragile = asInsurable(new FragileParcel("SP-4", "A", "B", 1.0, 12_500, ""));
        assertEquals(12_500, fragile.getDeclaredValue(), 0.001);
        assertEquals(375.0, fragile.insurancePremium(), 0.001);
    }

    @Test
    void negativeDeclaredValueIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new ExpressParcel("SP-6", "A", "B", 1.0, -1));
        assertThrows(IllegalArgumentException.class,
                () -> new FragileParcel("SP-7", "A", "B", 1.0, -1, ""));
    }
}
