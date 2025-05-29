package com.example.newspeed.post.dto;

import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class SearchPeriodRequestDto {

    // yyyy-mm-dd 형식
    private LocalDate startDate;
    private LocalDate endDate;

    public SearchPeriodRequestDto(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public SearchPeriodRequestDto() {
    }
}
