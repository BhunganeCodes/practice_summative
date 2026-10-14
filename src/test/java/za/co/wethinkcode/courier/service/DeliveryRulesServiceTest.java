package za.co.wethinkcode.courier.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import za.co.wethinkcode.courier.model.AttemptOutcome;
import za.co.wethinkcode.courier.model.Consignment;
import za.co.wethinkcode.courier.model.DeliveryAttempt;
import za.co.wethinkcode.courier.model.Driver;
import za.co.wethinkcode.courier.model.ServiceLevel;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DeliveryRulesServiceTest {

    private DeliveryRulesService rules;
    private Driver driver;
    private Consignment consignment;

    @BeforeEach
    void setUp() {
        rules = new DeliveryRulesService();
        driver = new Driver("DRV-01", "Lerato Dube", "JHB-NORTH", 500);
        driver.setCurrentLoadKg(100);
        consignment = new Consignment("SP-000001", 20, ServiceLevel.STANDARD, "JHB-NORTH");
    }

    private void attempt(Consignment target, AttemptOutcome outcome) {
        target.recordAttempt(new DeliveryAttempt(LocalDateTime.of(2026, 10, 1, 9, 0), outcome, ""));
    }

    // --- canLoadOntoVehicle ---

    @Test
    void allowsLoadingWhenAllConditionsAreMet() {
        assertTrue(rules.canLoadOntoVehicle(consignment, driver));
    }

    @Test
    void deniesLoadingWhenDriverIsOffShift() {
        driver.endShift();

        assertFalse(rules.canLoadOntoVehicle(consignment, driver));
    }

    @Test
    void deniesLoadingWhenConsignmentIsOnHold() {
        consignment.placeOnHold();

        assertFalse(rules.canLoadOntoVehicle(consignment, driver));
    }

    @Test
    void deniesLoadingWhenAddressIsNotVerified() {
        consignment.setAddressVerified(false);

        assertFalse(rules.canLoadOntoVehicle(consignment, driver));
    }

    @Test
    void deniesLoadingWhenVehicleWouldBeOverCapacity() {
        driver.setCurrentLoadKg(490);

        assertFalse(rules.canLoadOntoVehicle(consignment, driver));
    }

    @Test
    void allowsLoadingWhenVehicleWouldBeExactlyAtCapacity() {
        driver.setCurrentLoadKg(480);

        assertTrue(rules.canLoadOntoVehicle(consignment, driver));
    }

    @Test
    void deniesFragileConsignmentForUncertifiedDriver() {
        consignment.setFragile(true);

        assertFalse(rules.canLoadOntoVehicle(consignment, driver));
    }

    @Test
    void allowsFragileConsignmentForCertifiedDriver() {
        consignment.setFragile(true);
        driver.setFragileCertified(true);

        assertTrue(rules.canLoadOntoVehicle(consignment, driver));
    }

    @Test
    void deniesCashOnDeliveryForDriverNotApprovedToCollectCash() {
        consignment.setCashOnDelivery(true);

        assertFalse(rules.canLoadOntoVehicle(consignment, driver));
    }

    @Test
    void allowsCashOnDeliveryForApprovedDriver() {
        consignment.setCashOnDelivery(true);
        driver.setCashCollectionApproved(true);

        assertTrue(rules.canLoadOntoVehicle(consignment, driver));
    }

    @Test
    void deniesLoadingWhenZoneDoesNotMatch() {
        Consignment otherZone = new Consignment("SP-000002", 20, ServiceLevel.EXPRESS, "PTA-EAST");

        assertFalse(rules.canLoadOntoVehicle(otherZone, driver));
    }

    @Test
    void allowsSameDayConsignmentOutsideDriversZone() {
        Consignment sameDay = new Consignment("SP-000003", 20, ServiceLevel.SAME_DAY, "PTA-EAST");

        assertTrue(rules.canLoadOntoVehicle(sameDay, driver));
    }

    @Test
    void loadAddsWeightToDriversCurrentLoad() {
        rules.load(consignment, driver);

        assertEquals(120, driver.getCurrentLoadKg(), 0.001);
    }

    @Test
    void loadRejectsConsignmentThatCannotBeLoaded() {
        driver.endShift();

        assertThrows(IllegalStateException.class, () -> rules.load(consignment, driver));
    }

    // --- decideNextStep ---

    @Test
    void nothingToDoOnceDelivered() {
        attempt(consignment, AttemptOutcome.NO_ONE_HOME);
        attempt(consignment, AttemptOutcome.DELIVERED);

        assertEquals("NONE", rules.decideNextStep(consignment));
    }

    @Test
    void awaitsFirstAttemptWhenNoAttemptsMade() {
        assertEquals("AWAITING_FIRST_ATTEMPT", rules.decideNextStep(consignment));
    }

    @Test
    void returnsToSenderWhenRecipientRefuses() {
        attempt(consignment, AttemptOutcome.REFUSED);

        assertEquals("RETURN_TO_SENDER", rules.decideNextStep(consignment));
    }

    @Test
    void contactsRecipientWhenVerifiedAddressWasWrong() {
        attempt(consignment, AttemptOutcome.WRONG_ADDRESS);

        assertEquals("CONTACT_RECIPIENT", rules.decideNextStep(consignment));
    }

    @Test
    void holdsAtDepotWhenUnverifiedAddressWasWrong() {
        consignment.setAddressVerified(false);
        attempt(consignment, AttemptOutcome.WRONG_ADDRESS);

        assertEquals("HOLD_AT_DEPOT", rules.decideNextStep(consignment));
    }

    @Test
    void wrongAddressTakesPriorityOverAttemptLimit() {
        attempt(consignment, AttemptOutcome.NO_ONE_HOME);
        attempt(consignment, AttemptOutcome.NO_ONE_HOME);
        attempt(consignment, AttemptOutcome.WRONG_ADDRESS);

        assertEquals("CONTACT_RECIPIENT", rules.decideNextStep(consignment));
    }

    @Test
    void returnsToSenderAfterMaximumFailedAttempts() {
        attempt(consignment, AttemptOutcome.NO_ONE_HOME);
        attempt(consignment, AttemptOutcome.NO_ONE_HOME);
        attempt(consignment, AttemptOutcome.NO_ONE_HOME);

        assertEquals("RETURN_TO_SENDER", rules.decideNextStep(consignment));
    }

    @Test
    void schedulesRedeliveryForStandardConsignment() {
        attempt(consignment, AttemptOutcome.NO_ONE_HOME);

        assertEquals("SCHEDULE_REDELIVERY", rules.decideNextStep(consignment));
    }

    @Test
    void redeliversTodayForExpressConsignment() {
        Consignment express = new Consignment("SP-000004", 5, ServiceLevel.EXPRESS, "JHB-NORTH");
        attempt(express, AttemptOutcome.NO_ONE_HOME);
        attempt(express, AttemptOutcome.NO_ONE_HOME);

        assertEquals("REDELIVER_TODAY", rules.decideNextStep(express));
    }

    @Test
    void redeliversTodayForSameDayConsignment() {
        Consignment sameDay = new Consignment("SP-000005", 5, ServiceLevel.SAME_DAY, "JHB-NORTH");
        attempt(sameDay, AttemptOutcome.NO_ONE_HOME);

        assertEquals("REDELIVER_TODAY", rules.decideNextStep(sameDay));
    }
}
