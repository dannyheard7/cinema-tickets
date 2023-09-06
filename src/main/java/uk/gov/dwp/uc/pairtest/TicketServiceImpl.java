package uk.gov.dwp.uc.pairtest;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.Arrays;

public class TicketServiceImpl implements TicketService {
    /**
     * Should only have private methods other than the one below.
     */

    private final int MAX_TICKETS_PER_TRANSACTION = 20;

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {
        if(accountId == null) {
            throw new IllegalArgumentException("accountId cannot be null");
        }

        if(ticketTypeRequests == null) {
            throw new IllegalArgumentException("ticketTypeRequests cannot be null");
        }

        if(ticketTypeRequests.length == 0) {
            throw InvalidPurchaseException.NoTickets;
        }

        if(getTotalNumberOfTickets(ticketTypeRequests) > MAX_TICKETS_PER_TRANSACTION) {
            throw InvalidPurchaseException.TooManyTickets;
        }

        var totalNumberOfAdultTickets = getTotalNumberOfAdultTickets(ticketTypeRequests);
        var totalNumberOfChildTickets = getTotalNumberOfChildTickets(ticketTypeRequests);
        var totalNumberOfInfantTickets = getTotalNumberOfInfantTickets(ticketTypeRequests);

        if(totalNumberOfInfantTickets > totalNumberOfAdultTickets) {
            throw InvalidPurchaseException.TooManyInfantTickets;
        }

        if(totalNumberOfChildTickets > 0 && totalNumberOfAdultTickets == 0) {
            throw InvalidPurchaseException.TooManyChildTickets;
        }
    }


    // TODO: merge into a single method to reduce the number of iterations over the array
    private int getTotalNumberOfTickets(TicketTypeRequest[] ticketTypeRequests) {
        return Arrays.stream(ticketTypeRequests)
                .mapToInt(TicketTypeRequest::getNoOfTickets)
                .sum();
    }

    private int getTotalNumberOfAdultTickets(TicketTypeRequest[] ticketTypeRequests) {
        return Arrays.stream(ticketTypeRequests)
                .filter(ticketTypeRequest -> ticketTypeRequest.getTicketType() == TicketTypeRequest.Type.ADULT)
                .mapToInt(TicketTypeRequest::getNoOfTickets)
                .sum();
    }

    private int getTotalNumberOfChildTickets(TicketTypeRequest[] ticketTypeRequests) {
        return Arrays.stream(ticketTypeRequests)
                .filter(ticketTypeRequest -> ticketTypeRequest.getTicketType() == TicketTypeRequest.Type.CHILD)
                .mapToInt(TicketTypeRequest::getNoOfTickets)
                .sum();
    }

    private int getTotalNumberOfInfantTickets(TicketTypeRequest[] ticketTypeRequests) {
        return Arrays.stream(ticketTypeRequests)
                .filter(ticketTypeRequest -> ticketTypeRequest.getTicketType() == TicketTypeRequest.Type.INFANT)
                .mapToInt(TicketTypeRequest::getNoOfTickets)
                .sum();
    }
}
