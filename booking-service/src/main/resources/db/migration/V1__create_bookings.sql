-- Create bookings and booking_items tables
CREATE TABLE IF NOT EXISTS bookings (
    id UUID PRIMARY KEY,
    user_id UUID,
    event_id UUID,
    status VARCHAR(50),
    payment_status VARCHAR(50),
    total_amount NUMERIC(18,2),
    currency VARCHAR(10),
    created_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS booking_items (
    id UUID PRIMARY KEY,
    booking_id UUID,
    ticket_type_id UUID,
    quantity INT,
    unit_price NUMERIC(18,2),
    total_price NUMERIC(18,2),
    CONSTRAINT fk_booking FOREIGN KEY (booking_id) REFERENCES bookings(id)
);

