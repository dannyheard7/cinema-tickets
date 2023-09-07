package uk.gov.dwp.uc.pairtest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.Mockito;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import java.util.stream.Stream;

import static org.mockito.Mockito.verifyNoMoreInteractions;


public class TicketServiceImplTests {
    private final TicketServiceImpl ticketService;
    private final Long VALID_ACCOUNT_ID = 1L;

    private final SeatReservationService mockedSeatReservationService = Mockito.mock(SeatReservationService.class);
    private final TicketPaymentService mockedTicketPaymentService = Mockito.mock(TicketPaymentService.class);

    public TicketServiceImplTests() {
        ticketService = new TicketServiceImpl(mockedSeatReservationService, mockedTicketPaymentService);
    }

    @Test
    public void purchaseTickets_WhenCalledWithNullAccountId_ThrowsIllegalArgumentException() {
        var ticketTypeRequests = new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1);

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ticketService.purchaseTickets(null, ticketTypeRequests);
        });

        verifyNoMoreInteractions(mockedSeatReservationService);
        verifyNoMoreInteractions(mockedTicketPaymentService);
    }

    @Test
    public void purchaseTickets_WhenCalledWithNullTicketTypeRequests_ThrowsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            ticketService.purchaseTickets(VALID_ACCOUNT_ID, null);
        });

        verifyNoMoreInteractions(mockedSeatReservationService);
        verifyNoMoreInteractions(mockedTicketPaymentService);
    }

    @Test
    public void purchaseTickets_WheCalledWithNoTicketTypeRequests_ThrowsInvalidPurchaseException() {
        var thrownException = Assertions.assertThrows(InvalidPurchaseException.class, () -> {
            ticketService.purchaseTickets(VALID_ACCOUNT_ID);
        });

        Assertions.assertEquals(InvalidPurchaseException.NoTickets, thrownException);
        verifyNoMoreInteractions(mockedSeatReservationService);
        verifyNoMoreInteractions(mockedTicketPaymentService);
    }

    private static class TooManyTicketsArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    (Object) new TicketTypeRequest[]{
                            new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 21)
                    },
                    (Object) new TicketTypeRequest[]{
                            new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1),
                            new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 20)
                    },
                    (Object) new TicketTypeRequest[]{
                            new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 20),
                            new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1)
                    },
                    (Object) new TicketTypeRequest[]{
                            new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1),
                            new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1),
                            new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 19)
                    },
                    (Object) new TicketTypeRequest[]{
                            new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 10),
                            new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 11)
                    }
            ).map(Arguments::of);
        }
    }

    @ParameterizedTest
    @ArgumentsSource(TooManyTicketsArgumentsProvider.class)
    public void purchaseTickets_WhenCalledWithTooManyTickets_ThrowsInvalidPurchaseException(TicketTypeRequest[] ticketTypeRequests) {
        var thrownException = Assertions.assertThrows(InvalidPurchaseException.class, () -> {
            ticketService.purchaseTickets(VALID_ACCOUNT_ID, ticketTypeRequests);
        });

        Assertions.assertEquals(InvalidPurchaseException.TooManyTickets, thrownException);
        verifyNoMoreInteractions(mockedSeatReservationService);
        verifyNoMoreInteractions(mockedTicketPaymentService);
    }

    private static class TooManyInfantTicketsArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    (Object) new TicketTypeRequest[]{
                            new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1)
                    },
                    (Object) new TicketTypeRequest[]{
                            new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1),
                            new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 2)
                    },
                    (Object) new TicketTypeRequest[]{
                            new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1),
                            new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1),
                            new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 2)
                    }
            ).map(Arguments::of);
        }
    }

    @ParameterizedTest
    @ArgumentsSource(TooManyInfantTicketsArgumentsProvider.class)
    public void purchaseTickets_WhenCalledWithMoreInfantsThanAdults_ThrowsInvalidPurchaseException(TicketTypeRequest[] ticketTypeRequests) {
        var thrownException = Assertions.assertThrows(InvalidPurchaseException.class, () -> {
            ticketService.purchaseTickets(VALID_ACCOUNT_ID, ticketTypeRequests);
        });

        Assertions.assertEquals(InvalidPurchaseException.TooManyInfantTickets, thrownException);
        verifyNoMoreInteractions(mockedSeatReservationService);
        verifyNoMoreInteractions(mockedTicketPaymentService);
    }

    private static class TooManyChildTicketsArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    (Object) new TicketTypeRequest[]{
                            new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1)
                    },
                    (Object) new TicketTypeRequest[]{
                            new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1),
                            new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 7)
                    }
            ).map(Arguments::of);
        }
    }

    @ParameterizedTest
    @ArgumentsSource(TooManyChildTicketsArgumentsProvider.class)
    public void purchaseTickets_WhenCalledWithChildTicketsWithoutAdultTickets_ThrowsInvalidPurchaseException(TicketTypeRequest[] ticketTypeRequests) {
        var thrownException = Assertions.assertThrows(InvalidPurchaseException.class, () -> {
            ticketService.purchaseTickets(VALID_ACCOUNT_ID, ticketTypeRequests);
        });

        Assertions.assertEquals(InvalidPurchaseException.TooManyChildTickets, thrownException);
        verifyNoMoreInteractions(mockedSeatReservationService);
        verifyNoMoreInteractions(mockedTicketPaymentService);
    }

    private static class SeatsReservationArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of(
                            (Object) new TicketTypeRequest[]{
                                    new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1)
                            },
                            1),
                    Arguments.of(
                            (Object) new TicketTypeRequest[]{
                                    new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1),
                                    new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1)
                            },
                            2),
                    Arguments.of(
                            (Object) new TicketTypeRequest[]{
                                    new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1),
                                    new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1),
                                    new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1)
                            },
                            2),
                    Arguments.of(
                            (Object) new TicketTypeRequest[]{
                                    new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1),
                                    new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1)
                            },
                            1)
            );
        }
    }

    @ParameterizedTest
    @ArgumentsSource(SeatsReservationArgumentsProvider.class)
    public void purchaseTickets_WhenCalledWithValidTicketTypeRequests_ReservesTheCorrectNumberOfSeats(
            TicketTypeRequest[] ticketTypeRequests,
            int expectedNumberOfSeatsToReserve) {
        ticketService.purchaseTickets(VALID_ACCOUNT_ID, ticketTypeRequests);

        Mockito.verify(mockedSeatReservationService, Mockito.times(1))
                .reserveSeat(VALID_ACCOUNT_ID, expectedNumberOfSeatsToReserve);
        verifyNoMoreInteractions(mockedSeatReservationService);
    }

    private static class PaymentArgumentsProvider implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of(
                            (Object) new TicketTypeRequest[]{
                                    new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1)
                            },
                            20),
                    Arguments.of(
                            (Object) new TicketTypeRequest[]{
                                    new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1),
                                    new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1)
                            },
                            30),
                    Arguments.of(
                            (Object) new TicketTypeRequest[]{
                                    new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1),
                                    new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1),
                                    new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1)
                            },
                            30),
                    Arguments.of(
                            (Object) new TicketTypeRequest[]{
                                    new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1),
                                    new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1)
                            },
                            20),
                    Arguments.of(
                            (Object) new TicketTypeRequest[]{
                                    new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 20)
                            },
                            400),
                    Arguments.of(
                            (Object) new TicketTypeRequest[]{
                                    new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 10),
                                    new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 10)
                            },
                            300)
            );
        }
    }

    @ParameterizedTest
    @ArgumentsSource(PaymentArgumentsProvider.class)
    public void purchaseTickets_WhenCalledWithValidTicketTypeRequests_ChargesTheCorrectAmount(
            TicketTypeRequest[] ticketTypeRequests,
            int expectedAmountToCharge) {
        ticketService.purchaseTickets(VALID_ACCOUNT_ID, ticketTypeRequests);

        Mockito.verify(mockedTicketPaymentService, Mockito.times(1))
                .makePayment(VALID_ACCOUNT_ID, expectedAmountToCharge);
        verifyNoMoreInteractions(mockedTicketPaymentService);
    }
}
