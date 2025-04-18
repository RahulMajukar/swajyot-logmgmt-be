package com.swajyot.log.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Characteristic {
    private Long id;
    private String name;
    private String observation;
    private String bodyThickness;
    private String bottomThickness;
    private String comments;
}
