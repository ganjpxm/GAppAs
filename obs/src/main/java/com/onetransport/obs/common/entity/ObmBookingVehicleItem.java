/**
 * $Id: ObmBookingVehicleItem.java,v 1.0 2012/08/19 00:16:55 GanJianping Exp $
 *
 * Copyright (c) 2012 Gan Jianping. All rights reserved
 * Jpw Project
 *
 */
package com.onetransport.obs.common.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import org.ganjp.glib.core.util.StringUtil;

/**
 * <p>ObmBookingVehicleItem</p>
 * 
 * @author GanJianping
 * @since 1.0
 */

public class ObmBookingVehicleItem extends BaseModel implements Serializable {
	private static final long serialVersionUID = 1L;
	private String bookingVehicleItemId;
	private String bookingVehicleId;
	private String bookingNumber;
	private String bookingService;
	private String bookingServiceCd;
	private Date pickupDate;
	private String pickupTime;
	private Timestamp pickupDateTime;
	private Float bookingHours;
	private String flightNumber;
	private String pickupCity;
	private String pickupCityCd;
	private String pickupAddress;
	private String pickupPoint;
	private String destination;
	private String vehicle;
	private String vehicleCd;
	private Float price;
	private String driverUserId;
	private String leadPassengerFirstName;
	private String leadPassengerLastName;
	private String leadPassengerMobileNumber;
	private String bookingStatus;
	private String bookingStatusCd;
	private String paymentStatus;
	private String paymentStatusCd;
	private Integer displayNo;
	private String remark;
	
	private String driverUserName;
	private String driverMobileNumber;
	private String driverVehicle;
	private String leadPassengerGender;
	private String leadPassengerGenderCd;
	private String orgId;
	private String numberOfPassenger;
	private String paymentMode;
	private String paymentModeCd;
	private String bookingUserName;
	private String bookingUserMobileNumber;
	private String bookingUserEmail;
	private String bookingUserSurname;
	private String orgEmail;
	private String orgName;
	private String assignOrgId;
	private String assignOrgName;
	private String assignOrgEmail;
	private String assignDriverUserId;
	private String assignDriverUserName;
	private String assignDriverMobilePhone;
	private String assignDriverEmail;
	private String bookingUserGender;
	private String bookingUserGenderCd;
	private String agentUserId;
	private String agentUserName;
	private String agentMobileNumber;
	private String agentEmail;
	private String operatorMobileNumber;
	private String operatorEmail;
	
	private boolean isNew = false;
	
	   
	//----------------------------------------------- default constructor --------------------------
    public ObmBookingVehicleItem() {
    	super();
    }
    
    //------------------------------------------------ Property accessors --------------------------
/**
	 * @return String
	 */
	public String getBookingVehicleItemId() {
        return this.bookingVehicleItemId;
    }
    
    /**
	 * @param bookingVehicleItemId
	 */
    public void setBookingVehicleItemId(String bookingVehicleItemId) {
		this.bookingVehicleItemId = bookingVehicleItemId;
    }
    /**
	 * @return String
	 */
	public String getBookingVehicleId() {
        return this.bookingVehicleId;
    }
    
    /**
	 * @param bookingVehicleId
	 */
    public void setBookingVehicleId(String bookingVehicleId) {
		this.bookingVehicleId = bookingVehicleId;
    }
    /**
	 * @return String
	 */
	public String getBookingNumber() {
        return this.bookingNumber;
    }
    
    /**
	 * @param bookingNumber
	 */
    public void setBookingNumber(String bookingNumber) {
		this.bookingNumber = bookingNumber;
    }
    /**
	 * @return String
	 */
	public String getBookingService() {
        return this.bookingService;
    }
    
    /**
	 * @param bookingService
	 */
    public void setBookingService(String bookingService) {
		this.bookingService = bookingService;
    }
    /**
	 * @return String
	 */
	public String getBookingServiceCd() {
        return this.bookingServiceCd;
    }
    
    /**
	 * @param bookingServiceCd
	 */
    public void setBookingServiceCd(String bookingServiceCd) {
		this.bookingServiceCd = bookingServiceCd;
    }
    /**
	 * @return Date
	 */
	public Date getPickupDate() {
        return this.pickupDate;
    }
    
    /**
	 * @param pickupDate
	 */
    public void setPickupDate(Date pickupDate) {
		this.pickupDate = pickupDate;
    }
   
    public String getPickupTime() {
    	if (StringUtil.isNotEmpty(pickupTime) && pickupTime.length()>5) {
    		pickupTime = pickupTime.substring(0, 5);
    	}
		return pickupTime;
	}

	public void setPickupTime(String pickupTime) {
		this.pickupTime = pickupTime;
	}

	/**
	 * @return BigDecimal
	 */
	public Float getBookingHours() {
        return this.bookingHours;
    }
    
    /**
	 * @param bookingHours
	 */
    public void setBookingHours(Float bookingHours) {
		this.bookingHours = bookingHours;
    }
    /**
	 * @return String
	 */
	public String getFlightNumber() {
		if (this.flightNumber==null || this.flightNumber.equalsIgnoreCase("null")) {
			return "";
		}
        return this.flightNumber;
    }
    
    /**
	 * @param flightNumber
	 */
    public void setFlightNumber(String flightNumber) {
		this.flightNumber = flightNumber;
    }
    /**
	 * @return String
	 */
	public String getPickupCity() {
        return this.pickupCity;
    }
    
    /**
	 * @param pickupCity
	 */
    public void setPickupCity(String pickupCity) {
		this.pickupCity = pickupCity;
    }
    /**
	 * @return String
	 */
	public String getPickupCityCd() {
        return this.pickupCityCd;
    }
    
    /**
	 * @param pickupCityCd
	 */
    public void setPickupCityCd(String pickupCityCd) {
		this.pickupCityCd = pickupCityCd;
    }
    /**
	 * @return String
	 */
	public String getPickupAddress() {
		if (this.pickupAddress==null) {
			return "";
		} else {
			return this.pickupAddress;
		}
    }
    
    /**
	 * @param pickupAddress
	 */
    public void setPickupAddress(String pickupAddress) {
		this.pickupAddress = pickupAddress;
    }
    /**
	 * @return String
	 */
	public String getPickupPoint() {
        return this.pickupPoint;
    }
    
    /**
	 * @param pickupPoint
	 */
    public void setPickupPoint(String pickupPoint) {
		this.pickupPoint = pickupPoint;
    }
    /**
	 * @return String
	 */
	public String getDestination() {
		if (this.destination==null) {
			return "";
		} else {
			return this.destination;
		}
    }
    
    /**
	 * @param destination
	 */
    public void setDestination(String destination) {
		this.destination = destination;
    }
    /**
	 * @return String
	 */
	public String getVehicle() {
		if (this.vehicle==null) {
			return "";
		} else {
			return this.vehicle;
		}
    }
    
    /**
	 * @param vehicle
	 */
    public void setVehicle(String vehicle) {
		this.vehicle = vehicle;
    }
    /**
	 * @return String
	 */
	public String getVehicleCd() {
        return this.vehicleCd;
    }
    
    /**
	 * @param vehicleCd
	 */
    public void setVehicleCd(String vehicleCd) {
		this.vehicleCd = vehicleCd;
    }
    /**
	 * @return BigDecimal
	 */
	public Float getPrice() {
        return this.price;
    }
    
    /**
	 * @param price
	 */
    public void setPrice(Float price) {
		this.price = price;
    }
    /**
	 * @return String
	 */
	public String getDriverUserId() {
        return this.driverUserId;
    }
    
    /**
	 * @param driverUserId
	 */
    public void setDriverUserId(String driverUserId) {
		this.driverUserId = driverUserId;
    }
    /**
	 * @return String
	 */
	public String getLeadPassengerFirstName() {
		if (this.leadPassengerFirstName==null) {
			return "";
		} else {
			return this.leadPassengerFirstName;
		}
    }
    
    /**
	 * @param leadPassengerFirstName
	 */
    public void setLeadPassengerFirstName(String leadPassengerFirstName) {
		this.leadPassengerFirstName = leadPassengerFirstName;
    }
    /**
	 * @return String
	 */
	public String getLeadPassengerLastName() {
		if (this.leadPassengerLastName==null) {
			return "";
		} else {
			return this.leadPassengerLastName;
		}
    }
    
    /**
	 * @param leadPassengerLastName
	 */
    public void setLeadPassengerLastName(String leadPassengerLastName) {
		this.leadPassengerLastName = leadPassengerLastName;
    }
    /**
	 * @return String
	 */
	public String getLeadPassengerMobileNumber() {
		if (this.leadPassengerMobileNumber==null) {
			return "";
		} else {
			return this.leadPassengerMobileNumber;
		}
    }
    
    /**
	 * @param leadPassengerMobileNumber
	 */
    public void setLeadPassengerMobileNumber(String leadPassengerMobileNumber) {
		this.leadPassengerMobileNumber = leadPassengerMobileNumber;
    }
    /**
	 * @return String
	 */
	public String getBookingStatus() {
        return this.bookingStatus;
    }
    
    /**
	 * @param bookingStatus
	 */
    public void setBookingStatus(String bookingStatus) {
		this.bookingStatus = bookingStatus;
    }
    /**
	 * @return String
	 */
	public String getBookingStatusCd() {
        return this.bookingStatusCd;
    }
    
    /**
	 * @param bookingStatusCd
	 */
    public void setBookingStatusCd(String bookingStatusCd) {
		this.bookingStatusCd = bookingStatusCd;
    }
    /**
	 * @return String
	 */
	public String getPaymentStatus() {
        return this.paymentStatus;
    }
    
    /**
	 * @param paymentStatus
	 */
    public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
    }
    /**
	 * @return String
	 */
	public String getPaymentStatusCd() {
        return this.paymentStatusCd;
    }
    
    /**
	 * @param paymentStatusCd
	 */
    public void setPaymentStatusCd(String paymentStatusCd) {
		this.paymentStatusCd = paymentStatusCd;
    }
    /**
	 * @return Integer
	 */
	public Integer getDisplayNo() {
        return this.displayNo;
    }
    
    /**
	 * @param displayNo
	 */
    public void setDisplayNo(Integer displayNo) {
		this.displayNo = displayNo;
    }
    /**
	 * @return String
	 */
	public String getRemark() {
		if (StringUtil.isEmpty(remark)) {
			return "";
		}
        return this.remark;
    }
    
    /**
	 * @param remark
	 */
    public void setRemark(String remark) {
		this.remark = remark;
    }

	public String getDriverUserName() {
		if (StringUtil.isEmpty(driverUserName)) {
			return "";
		} else {
			return driverUserName;
		}
	}

	public void setDriverUserName(String driverUserName) {
		this.driverUserName = driverUserName;
	}

	public String getDriverMobileNumber() {
		if (driverMobileNumber==null) {
			return "";
		} else {
			return driverMobileNumber;
		}
	}

	public void setDriverMobileNumber(String driverMobileNumber) {
		this.driverMobileNumber = driverMobileNumber;
	}

	public String getDriverVehicle() {
		if (driverVehicle==null) {
			return "";
		} else {
			return driverVehicle;
		}
	}

	public void setDriverVehicle(String driverVehicle) {
		this.driverVehicle = driverVehicle;
	}

	public String getLeadPassengerGender() {
		return leadPassengerGender;
	}

	public void setLeadPassengerGender(String leadPassengerGender) {
		this.leadPassengerGender = leadPassengerGender;
	}

	public String getLeadPassengerGenderCd() {
		return leadPassengerGenderCd;
	}

	public void setLeadPassengerGenderCd(String leadPassengerGenderCd) {
		this.leadPassengerGenderCd = leadPassengerGenderCd;
	}

	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getNumberOfPassenger() {
		return numberOfPassenger;
	}

	public void setNumberOfPassenger(String numberOfPassenger) {
		this.numberOfPassenger = numberOfPassenger;
	}

	public String getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}

	public String getPaymentModeCd() {
		return paymentModeCd;
	}

	public void setPaymentModeCd(String paymentModeCd) {
		this.paymentModeCd = paymentModeCd;
	}

	public String getBookingUserName() {
		if (StringUtil.isEmpty(bookingUserName)) {
			return "";
		}
		return bookingUserName;
	}

	public void setBookingUserName(String bookingUserName) {
		this.bookingUserName = bookingUserName;
	}

	public String getBookingUserMobileNumber() {
		if (StringUtil.isEmpty(bookingUserMobileNumber)) {
			return "";
		}
		return bookingUserMobileNumber;
	}

	public void setBookingUserMobileNumber(String bookingUserMobileNumber) {
		this.bookingUserMobileNumber = bookingUserMobileNumber;
	}

	public String getBookingUserEmail() {
		if (StringUtil.isEmpty(bookingUserEmail)) {
			return "";
		}
		return bookingUserEmail;
	}

	public void setBookingUserEmail(String bookingUserEmail) {
		this.bookingUserEmail = bookingUserEmail;
	}

	public String getBookingUserSurname() {
		if (StringUtil.isEmpty(bookingUserSurname)) {
			return "";
		}
		return bookingUserSurname;
	}

	public void setBookingUserSurname(String bookingUserSurname) {
		this.bookingUserSurname = bookingUserSurname;
	}

	public String getOrgName() {
		if (StringUtil.isEmpty(orgName)) {
			return "";
		}
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getAssignOrgId() {
		return assignOrgId;
	}

	public void setAssignOrgId(String assignOrgId) {
		this.assignOrgId = assignOrgId;
	}

	public String getAssignOrgName() {
		return assignOrgName;
	}

	public void setAssignOrgName(String assignOrgName) {
		this.assignOrgName = assignOrgName;
	}

	public String getAssignDriverUserId() {
		return assignDriverUserId;
	}

	public void setAssignDriverUserId(String assignDriverUserId) {
		this.assignDriverUserId = assignDriverUserId;
	}

	public String getAssignDriverUserName() {
		return assignDriverUserName;
	}

	public void setAssignDriverUserName(String assignDriverUserName) {
		this.assignDriverUserName = assignDriverUserName;
	}

	public String getAssignDriverMobilePhone() {
		return assignDriverMobilePhone;
	}

	public void setAssignDriverMobilePhone(String assignDriverMobilePhone) {
		this.assignDriverMobilePhone = assignDriverMobilePhone;
	}

	public String getOrgEmail() {
		return orgEmail;
	}

	public void setOrgEmail(String orgEmail) {
		this.orgEmail = orgEmail;
	}

	public String getAssignOrgEmail() {
		return assignOrgEmail;
	}

	public void setAssignOrgEmail(String assignOrgEmail) {
		this.assignOrgEmail = assignOrgEmail;
	}

	public String getAssignDriverEmail() {
		return assignDriverEmail;
	}

	public void setAssignDriverEmail(String assignDriverEmail) {
		this.assignDriverEmail = assignDriverEmail;
	}

	public String getBookingUserGender() {
		if (StringUtil.isEmpty(bookingUserGender)) {
			return "";
		}
		return bookingUserGender;
	}

	public void setBookingUserGender(String bookingUserGender) {
		this.bookingUserGender = bookingUserGender;
	}

	public String getBookingUserGenderCd() {
		if (StringUtil.isEmpty(bookingUserGenderCd)) {
			return "";
		}
		return bookingUserGenderCd;
	}

	public void setBookingUserGenderCd(String bookingUserGenderCd) {
		this.bookingUserGenderCd = bookingUserGenderCd;
	}

	public String getAgentUserId() {
		return agentUserId;
	}

	public void setAgentUserId(String agentUserId) {
		this.agentUserId = agentUserId;
	}

	public String getAgentUserName() {
		if (StringUtil.isEmpty(agentUserName)) {
			return "";
		}
		return agentUserName;
	}

	public void setAgentUserName(String agentUserName) {
		this.agentUserName = agentUserName;
	}

	public String getAgentMobileNumber() {
		if (StringUtil.isEmpty(agentMobileNumber)) {
			return "";
		}
		return agentMobileNumber;
	}

	public void setAgentMobileNumber(String agentMobileNumber) {
		this.agentMobileNumber = agentMobileNumber;
	}

	public String getAgentEmail() {
		if (StringUtil.isEmpty(agentEmail)) {
			return "";
		}
		return agentEmail;
	}

	public void setAgentEmail(String agentEmail) {
		this.agentEmail = agentEmail;
	}

	public String getOperatorMobileNumber() {
		if (StringUtil.isEmpty(operatorMobileNumber)) {
			return "";
		}
		return operatorMobileNumber;
	}

	public void setOperatorMobileNumber(String operatorMobileNumber) {
		this.operatorMobileNumber = operatorMobileNumber;
	}

	public String getOperatorEmail() {
		if (StringUtil.isEmpty(operatorEmail)) {
			return "";
		}
		return operatorEmail;
	}

	public void setOperatorEmail(String operatorEmail) {
		this.operatorEmail = operatorEmail;
	}

	public Timestamp getPickupDateTime() {
		return pickupDateTime;
	}

	public void setPickupDateTime(Timestamp pickupDateTime) {
		this.pickupDateTime = pickupDateTime;
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}
	
}