package za.co.wethinkcode.courier.dispatch;

public class Parcel {

    // TODO: declare private fields

    public Parcel(String trackingNumber, String senderName, String recipientAddress, double weightKg) {
        // TODO: implement constructor (a new parcel is not delivered, weight must be greater than zero)
    }

    public String getTrackingNumber() {
        throw new UnsupportedOperationException("TODO");
    }

    public String getSenderName() {
        throw new UnsupportedOperationException("TODO");
    }

    public String getRecipientAddress() {
        throw new UnsupportedOperationException("TODO");
    }

    public void updateRecipientAddress(String recipientAddress) {
        throw new UnsupportedOperationException("TODO");
    }

    public double getWeightKg() {
        throw new UnsupportedOperationException("TODO");
    }

    public void updateWeightKg(double weightKg) {
        throw new UnsupportedOperationException("TODO");
    }

    public boolean isDelivered() {
        throw new UnsupportedOperationException("TODO");
    }

    public void markDelivered() {
        throw new UnsupportedOperationException("TODO");
    }

    // TODO: override toString()

    /**
     * Returns the delivery fee for this parcel in Rands.
     */
    public double deliveryFee() {
        throw new UnsupportedOperationException("TODO");
    }

    /**
     * Returns the number of working days this parcel takes to be delivered.
     */
    public int deliveryDays() {
        throw new UnsupportedOperationException("TODO");
    }

    /**
     * Returns a short label for this parcel's service level, e.g. "Standard".
     */
    public String serviceLevel() {
        throw new UnsupportedOperationException("TODO");
    }
}
