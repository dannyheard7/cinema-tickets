package uk.gov.dwp.uc.pairtest.exception;

public class InvalidPurchaseException extends RuntimeException {
    public InvalidPurchaseException(String message) {
        super(message);
    }

    public static InvalidPurchaseException NoTickets = new InvalidPurchaseException("At least one ticket must be purchased");

    public static InvalidPurchaseException TooManyTickets = new InvalidPurchaseException("Cannot purchase more than 20 tickets in one transaction");

    public static InvalidPurchaseException TooManyInfantTickets = new InvalidPurchaseException("Cannot purchase more infant tickets than adult tickets");

    public static InvalidPurchaseException TooManyChildTickets = new InvalidPurchaseException("Cannot purchase child tickets without adult tickets");
}
