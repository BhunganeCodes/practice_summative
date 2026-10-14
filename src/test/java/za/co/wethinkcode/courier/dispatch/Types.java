package za.co.wethinkcode.courier.dispatch;

/**
 * Until you add `extends Parcel` and `implements Insurable`, Java will not let the tests treat
 * your parcels as a Parcel or an Insurable. Converting through Object lets the whole project
 * compile before you start; the conversion only succeeds once your class hierarchy is right.
 */
final class Types {

    private Types() {
    }

    static Parcel asParcel(Object parcel) {
        return (Parcel) parcel;
    }

    static Insurable asInsurable(Object parcel) {
        return (Insurable) parcel;
    }
}
