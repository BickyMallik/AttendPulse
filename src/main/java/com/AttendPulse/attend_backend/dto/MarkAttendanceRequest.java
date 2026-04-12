package com.AttendPulse.attend_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MarkAttendanceRequest {

    @NotBlank
    private String otpCode;

    @NotBlank
    private String deviceFingerprint;
}