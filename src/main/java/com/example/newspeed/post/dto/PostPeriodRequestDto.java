package com.example.newspeed.post.dto;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class PostPeriodRequestDto {

    // yyyy-mm-dd 형식
    private LocalDate startDate;
    private LocalDate endDate;

    public PostPeriodRequestDto(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public PostPeriodRequestDto() {
    }
}
