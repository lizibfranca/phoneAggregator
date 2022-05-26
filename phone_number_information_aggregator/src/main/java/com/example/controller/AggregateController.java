package com.example.controller;

import com.example.dto.AggregateDTO;
import com.example.rest.AggregatePOJO;
import com.example.service.AggregateService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RestController
@RequestMapping("aggregate")
@RequiredArgsConstructor
public class AggregateController {

    @Autowired
    private AggregateService aggregateService;

    @PostMapping
    public ResponseEntity<HashMap<String, HashMap<String, Integer>>> list(@RequestParam List<String> list) {
        return new ResponseEntity<>(aggregateService.readFile(list), HttpStatus.OK);
    }

}
