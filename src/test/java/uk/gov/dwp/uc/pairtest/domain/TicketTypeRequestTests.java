package uk.gov.dwp.uc.pairtest.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TicketTypeRequestTests {

    @Test
    public void Constructor_WhenCalledWithNullType_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new TicketTypeRequest(null, 1);
        });
    }

    @Test
    public void Constructor_WhenCalledWithNegativeNoOfTickets_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new TicketTypeRequest(TicketTypeRequest.Type.ADULT, -1);
        });
    }

    @Test
    public void Constructor_WhenCalledWithZeroNoOfTickets_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 0);
        });
    }
}
