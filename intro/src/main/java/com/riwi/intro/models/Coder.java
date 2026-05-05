package com.riwi.intro.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Coder {
    private Long id;
    private String name;
    private String clan;
}
