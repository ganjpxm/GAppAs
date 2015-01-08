package com.onetransport.obs.common.entity;

public class BookingVehicleItem {
	private String bookingVehicleItemUuid = "";
	private String pickupTime = "";
	private String bookingNumber = "";
	private String pickupAddress = "";
	private String destination = "";
	private String flyNumber = "";
	private String bookingState = "";
	
	public BookingVehicleItem () {}
	
	public BookingVehicleItem(String bookingVehicleItemId, String pickupTime, String bookingNumber, String pickupAddress, 
			String destination, String flyNumber, String bookingState) {
		this.bookingVehicleItemUuid = bookingVehicleItemId;
        this.pickupTime = pickupTime;
        this.pickupAddress = pickupAddress;
        this.bookingNumber = bookingNumber;
        this.destination = destination;
        this.flyNumber = flyNumber;
        this.bookingState = bookingState;
    }

	public String getBookingVehicleItemUuid() {
		return bookingVehicleItemUuid;
	}

	public void setBookingVehicleItemUuid(String bookingVehicleItemUuid) {
		this.bookingVehicleItemUuid = bookingVehicleItemUuid;
	}

	public String getPickupTime() {
		return pickupTime;
	}

	public void setPickupTime(String pickupTime) {
		this.pickupTime = pickupTime;
	}

	public String getBookingNumber() {
		return bookingNumber;
	}

	public void setBookingNumber(String bookingNumber) {
		this.bookingNumber = bookingNumber;
	}

	public String getPickupAddress() {
		return pickupAddress;
	}

	public void setPickupAddress(String pickupAddress) {
		this.pickupAddress = pickupAddress;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getFlyNumber() {
		return flyNumber;
	}

	public void setFlyNumber(String flyNumber) {
		this.flyNumber = flyNumber;
	}

	public String getBookingState() {
		return bookingState;
	}

	public void setBookingState(String bookingState) {
		this.bookingState = bookingState;
	}

}