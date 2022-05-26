package com.example.dto;

import lombok.Data;

import java.util.Map;

@Data
public class AggregateDTO {

    String prefix;
    Map<String, Integer> map;

}
