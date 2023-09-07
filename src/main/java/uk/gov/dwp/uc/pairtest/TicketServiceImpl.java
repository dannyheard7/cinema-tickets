package uk.gov.dwp.uc.pairtest;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class TicketServiceImpl implements TicketService {
    private final int MAX_TICKETS_PER_TRANSACTION = 20;
    private final int ADULT_TICKET_PRICE = 20;
    private final int CHILD_TICKET_PRICE = 10;
    private final int INFANT_TICKET_PRICE = 0;

    private final SeatReservationService seatReservationService;
    private final TicketPaymentService ticketPaymentService;

    public TicketServiceImpl(SeatReservationService seatReservationService, TicketPaymentService ticketPaymentService) {
        this.seatReservationService = seatReservationService;
        this.ticketPaymentService = ticketPaymentService;
    }

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        if(accountId == null) {
            throw new IllegalArgumentException("accountId cannot be null");
        }

        if(ticketTypeRequests == null) {
            throw new IllegalArgumentException("ticketTypeRequests cannot be null");
        }

        var ticketsRequest = new TicketPurchaseRequest(ticketTypeRequests);
        validateTicketPurchaseRequest(ticketsRequest);

        // Ideally this would be wrapped in a transaction
        seatReservationService.reserveSeat(accountId, getNumberOfSeats(ticketsRequest));
        ticketPaymentService.makePayment(accountId, getTotalPrice(ticketsRequest));
    }

    private void validateTicketPurchaseRequest(TicketPurchaseRequest ticketPurchaseRequest) throws InvalidPurchaseException {
        if(ticketPurchaseRequest.getTotalNumberOfTickets() == 0) {
            throw InvalidPurchaseException.NoTickets;
        }

        if(ticketPurchaseRequest.getTotalNumberOfTickets() > MAX_TICKETS_PER_TRANSACTION) {
            throw InvalidPurchaseException.TooManyTickets;
        }

        if(ticketPurchaseRequest.getNumberOfInfantTickets() > ticketPurchaseRequest.getNumberOfAdultTickets()) {
            throw InvalidPurchaseException.TooManyInfantTickets;
        }

        if(ticketPurchaseRequest.getNumberOfChildTickets() > 0 && ticketPurchaseRequest.getNumberOfAdultTickets() == 0) {
            throw InvalidPurchaseException.TooManyChildTickets;
        }
    }

    private static int getNumberOfSeats(TicketPurchaseRequest ticketPurchaseRequest)
    {
        return ticketPurchaseRequest.getNumberOfAdultTickets() + ticketPurchaseRequest.getNumberOfChildTickets();
    }

    private int getTotalPrice(TicketPurchaseRequest ticketPurchaseRequest)
    {
        return (ticketPurchaseRequest.getNumberOfAdultTickets() * ADULT_TICKET_PRICE) +
                (ticketPurchaseRequest.getNumberOfChildTickets() * CHILD_TICKET_PRICE) +
                (ticketPurchaseRequest.getNumberOfInfantTickets() * INFANT_TICKET_PRICE);
    }

    private static class TicketPurchaseRequest {
        public TicketPurchaseRequest(TicketTypeRequest[] ticketTypeRequests) throws InvalidPurchaseException {
            for (TicketTypeRequest ticketTypeRequest : ticketTypeRequests) {
                switch (ticketTypeRequest.getTicketType())
                {
                    case ADULT:
                        numberOfAdultTickets += ticketTypeRequest.getNoOfTickets();
                        break;
                    case CHILD:
                        numberOfChildTickets += ticketTypeRequest.getNoOfTickets();
                        break;
                    case INFANT:
                        numberOfInfantTickets += ticketTypeRequest.getNoOfTickets();
                        break;
                    default:
                        throw new InvalidPurchaseException("Unknown ticket type: " + ticketTypeRequest.getTicketType());
                }
            }
        }

        private int numberOfAdultTickets;

        private int numberOfChildTickets;

        private int numberOfInfantTickets;

        public int getNumberOfAdultTickets() {
            return numberOfAdultTickets;
        }

        public int getNumberOfChildTickets() {
            return numberOfChildTickets;
        }

        public int getNumberOfInfantTickets() {
            return numberOfInfantTickets;
        }

        public int getTotalNumberOfTickets() {
            return numberOfAdultTickets + numberOfChildTickets + numberOfInfantTickets;
        }
    }
}
