package za.co.wethinkcode.courier.dispatch;

public class StandardParcel {

    public static final double BASE_FEE = 60.00;
    public static final double FEE_PER_KG = 8.00;
    public static final int DELIVERY_DAYS = 5;

    public StandardParcel(String trackingNumber, String senderName, String recipientAddress, double weightKg) {
        // TODO: implement constructor
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
