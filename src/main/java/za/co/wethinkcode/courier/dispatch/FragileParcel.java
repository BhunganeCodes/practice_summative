package za.co.wethinkcode.courier.dispatch;

public class FragileParcel {

    public static final double BASE_FEE = 90.00;
    public static final double FEE_PER_KG = 10.00;
    public static final int DELIVERY_DAYS = 3;
    public static final double INSURANCE_RATE = 0.03;

    // TODO: declare private field(s)

    public FragileParcel(String trackingNumber, String senderName, String recipientAddress, double weightKg,
                         double declaredValue, String handlingNotes) {
        // TODO: implement constructor
    }

    public String getHandlingNotes() {
        throw new UnsupportedOperationException("TODO");
    }

    public double getDeclaredValue() {
        throw new UnsupportedOperationException("TODO");
    }

    public double insurancePremium() {
        throw new UnsupportedOperationException("TODO");
    }

    public double deliveryFee() {
        throw new UnsupportedOperationException("TODO");
    }

    public int deliveryDays() {
        throw new UnsupportedOperationException("TODO");
    }

    public String serviceLevel() {
        throw new UnsupportedOperationException("TODO");
    }

    // TODO: override toString()
}
