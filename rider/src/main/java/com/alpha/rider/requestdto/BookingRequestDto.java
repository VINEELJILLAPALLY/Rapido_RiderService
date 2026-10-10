package com.alpha.rider.requestdto;

public class BookingRequestDto {
	
	private int bookingId;
	private double fare;
	private double distance;
	public BookingRequestDto(int bookingId, double fare, double distance) {
		super();
		this.bookingId = bookingId;
		this.fare = fare;
		this.distance = distance;
	}
	public BookingRequestDto() {
		super();
	}
	public int getBookingId() {
		return bookingId;
	}
	public void setBookingId(int bookingId) {
		this.bookingId = bookingId;
	}
	public double getFare() {
		return fare;
	}
	public void setFare(double fare) {
		this.fare = fare;
	}
	public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
	

}
