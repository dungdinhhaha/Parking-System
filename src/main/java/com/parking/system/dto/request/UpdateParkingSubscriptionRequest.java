package com.parking.system.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateParkingSubscriptionRequest {
    private Long vehicleId;
    private String rfidCardId;
    private String subscriberName;
    private String subscriberPhone;
    private Long assignedZoneId;
    private Long assignedSlotId;
    private String notes;
}
