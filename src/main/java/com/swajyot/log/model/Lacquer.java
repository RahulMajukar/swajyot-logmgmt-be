package com.swajyot.log.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Lacquer {
    private Long id;
    private String name;
    private String weight;
    private String batchNo;
    private LocalDate expiryDate;
}