package com.epam.finaltask.dto;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoucherRequest {
    @NotNull
    private String voucherId;
    private List<?> additionalDetails;
}