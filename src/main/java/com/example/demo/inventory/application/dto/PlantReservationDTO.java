package com.example.demo.inventory.application.dto;

import com.example.demo.common.application.dto.BusinessPeriodDTO;
import lombok.Data;

@Data
public class PlantReservationDTO {
    Long _id;
    PlantInventoryItemDTO plantInfo;
    BusinessPeriodDTO schedule;
}