package uk.gov.dwp.uc.pairtest;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

public class TicketServiceImpl implements TicketService {
    private final int MAX_TICKETS_PER_TRANSACTION = 20;

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        if(accountId == null) {
            throw new IllegalArgumentException("accountId cannot be null");
        }

        if(ticketTypeRequests == null) {
            throw new IllegalArgumentException("ticketTypeRequests cannot be null");
        }

        var ticketsRequest = new TicketPurchaseRequest(ticketTypeRequests);

        if(ticketsRequest.getTotalNumberOfTickets() == 0) {
            throw InvalidPurchaseException.NoTickets;
        }

        if(ticketsRequest.getTotalNumberOfTickets() > MAX_TICKETS_PER_TRANSACTION) {
            throw InvalidPurchaseException.TooManyTickets;
        }

        if(ticketsRequest.getNumberOfInfantTickets() > ticketsRequest.getNumberOfAdultTickets()) {
            throw InvalidPurchaseException.TooManyInfantTickets;
        }

        if(ticketsRequest.getNumberOfChildTickets() > 0 && ticketsRequest.getNumberOfAdultTickets() == 0) {
            throw InvalidPurchaseException.TooManyChildTickets;
        }
    }

    private static class TicketPurchaseRequest {
        public TicketPurchaseRequest(TicketTypeRequest[] ticketTypeRequests) {
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
