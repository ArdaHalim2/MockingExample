package com.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class BookingSystemTest {

    @Mock
    private TimeProvider timeProvider;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private NotificationService notificationService;

    private BookingSystem bookingSystem;

    private Room room;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bookingSystem = new BookingSystem(timeProvider, roomRepository, notificationService);

        room = new Room("1", "Room 1");
        when(roomRepository.findById("1")).thenReturn(Optional.of(room));
    }

    @Test
    void shouldBookRoomWhenValidInput() throws NotificationException {
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = startTime.plusHours(2);

        when(timeProvider.getCurrentTime()).thenReturn(LocalDateTime.now());

        boolean result = bookingSystem.bookRoom("1", startTime, endTime);

        assertThat(result).isTrue();
        verify(roomRepository).save(any(Room.class));
        verify(notificationService).sendBookingConfirmation(any(Booking.class));
    }

    @Test
    void shouldThrowExceptionWhenStartTimeIsInThePast() {
        LocalDateTime startTime = LocalDateTime.now().minusHours(1);
        LocalDateTime endTime = startTime.plusHours(2);

        when(timeProvider.getCurrentTime()).thenReturn(LocalDateTime.now());

        assertThatThrownBy(() -> bookingSystem.bookRoom("1", startTime, endTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Kan inte boka tid i dåtid");
    }

    @ParameterizedTest
    @MethodSource("invalidBookingParameters")
    void shouldThrowExceptionWhenInvalidParametersProvided(String roomId, LocalDateTime startTime, LocalDateTime endTime) {
        assertThatThrownBy(() -> bookingSystem.bookRoom(roomId, startTime, endTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Bokning kräver giltiga start- och sluttider samt rum-id");
    }

    static Stream<Arguments> invalidBookingParameters() {
        return Stream.of(
                Arguments.of(null, LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2)),
                Arguments.of("1", null, LocalDateTime.now().plusHours(2)),
                Arguments.of("1", LocalDateTime.now().plusHours(1), null)
        );
    }

    @Test
    void shouldReturnAvailableRooms() {
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = startTime.plusHours(2);

        Room room1 = mock(Room.class);
        Room room2 = mock(Room.class);
        when(roomRepository.findAll()).thenReturn(List.of(room1, room2));

        when(room1.isAvailable(startTime, endTime)).thenReturn(true);
        when(room2.isAvailable(startTime, endTime)).thenReturn(false);

        when(room1.getId()).thenReturn("1");
        when(room2.getId()).thenReturn("2");

        var availableRooms = bookingSystem.getAvailableRooms(startTime, endTime);

        assertThat(availableRooms).hasSize(1);
        assertThat(availableRooms.get(0).getId()).isEqualTo("1");
    }

    @Test
    void shouldCancelBookingSuccessfully() throws NotificationException {
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = startTime.plusHours(2);
        Booking booking = new Booking("booking-id", "1", startTime, endTime);
        room.addBooking(booking);

        when(timeProvider.getCurrentTime()).thenReturn(LocalDateTime.now());
        when(roomRepository.findAll()).thenReturn(List.of(room));

        boolean result = bookingSystem.cancelBooking("booking-id");

        assertThat(result).isTrue();
        verify(roomRepository).save(any(Room.class));
        verify(notificationService).sendCancellationConfirmation(any(Booking.class));
    }

    @Test
    void shouldThrowExceptionWhenCancellingStartedOrFinishedBooking() {
        LocalDateTime startTime = LocalDateTime.now().minusHours(1);
        LocalDateTime endTime = LocalDateTime.now().minusMinutes(30);
        Booking booking = new Booking("booking-id", "1", startTime, endTime);
        room.addBooking(booking);

        when(timeProvider.getCurrentTime()).thenReturn(LocalDateTime.now());
        when(roomRepository.findAll()).thenReturn(List.of(room));

        assertThatThrownBy(() -> bookingSystem.cancelBooking("booking-id"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Kan inte avboka påbörjad eller avslutad bokning");
    }

    @Test
    void shouldReturnFalseWhenBookingIdNotFound() {
        boolean result = bookingSystem.cancelBooking("invalid-booking-id");

        assertThat(result).isFalse();
    }
}
