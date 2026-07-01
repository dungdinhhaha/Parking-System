package com.parking.system.dto.request;

import com.parking.system.enums.SubscriptionCycleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
public class CreateParkingSubscriptionRequest {

    @NotNull
    private Long vehicleId;

    @NotBlank
    private String rfidCardId;

    @NotBlank
    private String subscriberName;

    @NotBlank
    private String subscriberPhone;

    @NotNull
    private SubscriptionCycleType subscriptionType;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startAt;

    private Long assignedZoneId;

    private Long assignedSlotId;

    private String notes;
}
