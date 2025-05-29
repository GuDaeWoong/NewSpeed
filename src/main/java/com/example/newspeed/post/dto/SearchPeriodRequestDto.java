package com.example.newspeed.post.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class SearchPeriodRequestDto {

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public SearchPeriodRequestDto(LocalDateTime startDate, LocalDateTime endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public SearchPeriodRequestDto() {
    }
}
