package za.co.wethinkcode.courier.dispatch;

import java.util.List;

public class DispatchCentre {

    // TODO: declare private fields

    public DispatchCentre(String centreName) {
        // TODO: implement constructor
    }

    public String getCentreName() {
        throw new UnsupportedOperationException("TODO");
    }

    public void addParcel(Parcel parcel) {
        throw new UnsupportedOperationException("TODO");
    }

    public Parcel findParcel(String trackingNumber) {
        throw new UnsupportedOperationException("TODO");
    }

    public List<Parcel> getParcels() {
        throw new UnsupportedOperationException("TODO");
    }

    public List<Insurable> insurableParcels() {
        throw new UnsupportedOperationException("TODO");
    }

    public double totalDeliveryRevenue() {
        throw new UnsupportedOperationException("TODO");
    }

    public double totalInsurancePremiums() {
        throw new UnsupportedOperationException("TODO");
    }
}
