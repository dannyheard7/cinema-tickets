package uk.gov.dwp.uc.pairtest;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;


public class TicketServiceImplTests {
    private final TicketServiceImpl ticketService;
    private final Long VALID_ACCOUNT_ID = 1L;

    public TicketServiceImplTests()
    {
        ticketService = new TicketServiceImpl();
    }

    @Test
    public void purchaseTickets_WhenCalledWithNullAccountId_ThrowsIllegalArgumentException()
    {
        var ticketTypeRequests = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ticketService.purchaseTickets(null, ticketTypeRequests);
        });
    }

    @Test
    public void purchaseTickets_WhenCalledWithNullTicketTypeRequests_ThrowsIllegalArgumentException()
    {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ticketService.purchaseTickets(VALID_ACCOUNT_ID, null);
        });
    }

    @Test
    public void purchaseTickets_WheCalledWithNoTicketTypeRequests_ThrowsInvalidPurchaseException()
    {
        Assertions.assertThrows(InvalidPurchaseException.class, () -> {
            ticketService.purchaseTickets(VALID_ACCOUNT_ID);
        });
    }
}
