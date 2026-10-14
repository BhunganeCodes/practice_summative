package za.co.wethinkcode.courier.dispatch;

public class ExpressParcel {

    public static final double BASE_FEE = 120.00;
    public static final double FEE_PER_KG = 12.00;
    public static final int DELIVERY_DAYS = 1;
    public static final double INSURANCE_RATE = 0.015;

    // TODO: declare private field(s)

    public ExpressParcel(String trackingNumber, String senderName, String recipientAddress, double weightKg,
                         double declaredValue) {
        // TODO: implement constructor
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
