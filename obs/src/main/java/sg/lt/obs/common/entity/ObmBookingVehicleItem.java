/**
 * $Id: ObmBookingVehicleItem.java,v 1.0 2012/08/19 00:16:55 GanJianping Exp $
 *
 * Copyright (c) 2012 Gan Jianping. All rights reserved
 * Jpw Project
 *
 */
package sg.lt.obs.common.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/**
 * <p>ObmBookingVehicleItem</p>
 * 
 * @author GanJianping
 * @since 1.0
 */

public class ObmBookingVehicleItem extends BaseModel implements Serializable {
	private static final long serialVersionUID = 1L;

    public String bookingVehicleItemId;
    public String bookingNumber;
    public Date pickupDate;
    public String pickupTime;
    public Timestamp pickupDateTime;
    public String bookingService;
    public String bookingServiceCd;
    public Float bookingHours;
    public String flightNumber;
    public String pickupAddress;
    public String destination;
    public String stop1Address;
    public String stop2Address;
    public String remark;

    public String vehicle;
    public Float price;
    public String paymentStatus;
    public String paymentMode;
    public String bookingStatus;

    public String bookingUserFirstName;
    public String bookingUserLastName;
    public String bookingUserGender;
    public String bookingUserMobileNumber;
    public String bookingUserEmail;

    public String leadPassengerFirstName;
    public String leadPassengerLastName;
    public String leadPassengerGender;
    public String leadPassengerMobileNumber;
    public String leadPassengerEmail;
    public String numberOfPassenger;

    public String driverUserName;
    public String driverLoginUserId;
    public String driverMobileNumber;
    public String driverVehicle;
    public String driverClaimCurrency;
    public Float driverClaimPrice;
    public String driverAction;

    private boolean isNew = false;

	   
	//----------------------------------------------- default constructor --------------------------
    public ObmBookingVehicleItem() {
    	super();
    }
    
    //------------------------------------------------ Property accessors --------------------------

    public String getBookingVehicleItemId() {
        return bookingVehicleItemId;
    }

    public void setBookingVehicleItemId(String bookingVehicleItemId) {
        this.bookingVehicleItemId = bookingVehicleItemId;
    }

    public String getBookingNumber() {
        return bookingNumber;
    }

    public void setBookingNumber(String bookingNumber) {
        this.bookingNumber = bookingNumber;
    }

    public Date getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(Date pickupDate) {
        this.pickupDate = pickupDate;
    }

    public String getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(String pickupTime) {
        this.pickupTime = pickupTime;
    }

    public Timestamp getPickupDateTime() {
        return pickupDateTime;
    }

    public void setPickupDateTime(Timestamp pickupDateTime) {
        this.pickupDateTime = pickupDateTime;
    }

    public String getBookingService() {
        return bookingService;
    }

    public void setBookingService(String bookingService) {
        this.bookingService = bookingService;
    }

    public String getBookingServiceCd() {
        return bookingServiceCd;
    }

    public void setBookingServiceCd(String bookingServiceCd) {
        this.bookingServiceCd = bookingServiceCd;
    }

    public Float getBookingHours() {
        return bookingHours;
    }

    public void setBookingHours(Float bookingHours) {
        this.bookingHours = bookingHours;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
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

    public String getStop1Address() {
        return stop1Address;
    }

    public void setStop1Address(String stop1Address) {
        this.stop1Address = stop1Address;
    }

    public String getStop2Address() {
        return stop2Address;
    }

    public void setStop2Address(String stop2Address) {
        this.stop2Address = stop2Address;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public String getBookingUserFirstName() {
        return bookingUserFirstName;
    }

    public void setBookingUserFirstName(String bookingUserFirstName) {
        this.bookingUserFirstName = bookingUserFirstName;
    }

    public String getBookingUserLastName() {
        return bookingUserLastName;
    }

    public void setBookingUserLastName(String bookingUserLastName) {
        this.bookingUserLastName = bookingUserLastName;
    }

    public String getBookingUserGender() {
        return bookingUserGender;
    }

    public void setBookingUserGender(String bookingUserGender) {
        this.bookingUserGender = bookingUserGender;
    }

    public String getBookingUserMobileNumber() {
        return bookingUserMobileNumber;
    }

    public void setBookingUserMobileNumber(String bookingUserMobileNumber) {
        this.bookingUserMobileNumber = bookingUserMobileNumber;
    }

    public String getBookingUserEmail() {
        return bookingUserEmail;
    }

    public void setBookingUserEmail(String bookingUserEmail) {
        this.bookingUserEmail = bookingUserEmail;
    }

    public String getLeadPassengerFirstName() {
        return leadPassengerFirstName;
    }

    public void setLeadPassengerFirstName(String leadPassengerFirstName) {
        this.leadPassengerFirstName = leadPassengerFirstName;
    }

    public String getLeadPassengerLastName() {
        return leadPassengerLastName;
    }

    public void setLeadPassengerLastName(String leadPassengerLastName) {
        this.leadPassengerLastName = leadPassengerLastName;
    }

    public String getLeadPassengerGender() {
        return leadPassengerGender;
    }

    public void setLeadPassengerGender(String leadPassengerGender) {
        this.leadPassengerGender = leadPassengerGender;
    }

    public String getLeadPassengerMobileNumber() {
        return leadPassengerMobileNumber;
    }

    public void setLeadPassengerMobileNumber(String leadPassengerMobileNumber) {
        this.leadPassengerMobileNumber = leadPassengerMobileNumber;
    }

    public String getLeadPassengerEmail() {
        return leadPassengerEmail;
    }

    public void setLeadPassengerEmail(String leadPassengerEmail) {
        this.leadPassengerEmail = leadPassengerEmail;
    }

    public String getNumberOfPassenger() {
        return numberOfPassenger;
    }

    public void setNumberOfPassenger(String numberOfPassenger) {
        this.numberOfPassenger = numberOfPassenger;
    }

    public String getDriverUserName() {
        return driverUserName;
    }

    public void setDriverUserName(String driverUserName) {
        this.driverUserName = driverUserName;
    }

    public String getDriverLoginUserId() {
        return driverLoginUserId;
    }

    public void setDriverLoginUserId(String driverLoginUserId) {
        this.driverLoginUserId = driverLoginUserId;
    }

    public String getDriverMobileNumber() {
        return driverMobileNumber;
    }

    public void setDriverMobileNumber(String driverMobileNumber) {
        this.driverMobileNumber = driverMobileNumber;
    }

    public String getDriverVehicle() {
        return driverVehicle;
    }

    public void setDriverVehicle(String driverVehicle) {
        this.driverVehicle = driverVehicle;
    }

    public String getDriverClaimCurrency() {
        return driverClaimCurrency;
    }

    public void setDriverClaimCurrency(String driverClaimCurrency) {
        this.driverClaimCurrency = driverClaimCurrency;
    }

    public Float getDriverClaimPrice() {
        return driverClaimPrice;
    }

    public void setDriverClaimPrice(Float driverClaimPrice) {
        this.driverClaimPrice = driverClaimPrice;
    }

    public String getDriverAction() {
        return driverAction;
    }

    public void setDriverAction(String driverAction) {
        this.driverAction = driverAction;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }
}