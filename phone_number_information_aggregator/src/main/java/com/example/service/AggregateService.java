package com.example.service;

import com.example.rest.AggregatePOJO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AggregateService {

    @Autowired
    private RestTemplate restTemplate;

    public  HashMap<String, HashMap<String, Integer>> readFile(List<String> inputList){
        HashMap<String, HashMap<String, Integer>> map = new HashMap<>();

        List<String> validNumberList = filterList(inputList);

        try (FileReader file = new FileReader ("prefixes.txt"); BufferedReader buffer = new BufferedReader(file)){
            String prefix;

            while( (prefix = buffer.readLine() ) != null) {
                HashMap<String, Integer> innerMap = new HashMap<>();
                for (var inputNumber: validNumberList) {
                    if (inputNumber.startsWith(prefix)) {
                        AggregatePOJO response = getSector(inputNumber);

                        if (response != null) {
                            if (!innerMap.containsKey(response.getSector())) {
                                innerMap.put(response.getSector(), 1);
                            } else {
                                AtomicInteger val
                                        = new AtomicInteger(innerMap.get(response.getSector()));
                                innerMap.put(response.getSector(), val.incrementAndGet());
                            }
                        }

                    }

                }
                if(!innerMap.isEmpty())
                    map.put(prefix, innerMap);
            }

        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return map;
    }

    private List<String> filterList(List<String> inputList) {
        inputList = inputList.stream().map(i -> i.trim()).collect(Collectors.toList());

        Predicate<String> p1 = (i -> (i.startsWith("\\+") && (i.substring(1).trim().length() == 3 ||
                (i.substring(1).trim().length() > 6 && i.substring(1).trim().length()< 13))));

        Predicate<String> p2 = (i -> ((i.charAt(0) == '0' && i.charAt(1) == '0') && (i.substring(2).trim().length() == 3 ||
                (i.substring(2).trim().length() > 6 && i.substring(2).trim().length()< 13))));

        Predicate<String> p3 = (i -> ((!i.startsWith("\\+") && !(i.charAt(0) == '0' && i.charAt(1) == '0')) && (i.length() == 3 || i.length() > 6 && i.length() <13)));

        List<String> validNumberList = new ArrayList<>();

        validNumberList.addAll(inputList.stream().filter(p1).map(i -> i.substring(1).trim()).collect(Collectors.toList()));
        validNumberList.addAll(inputList.stream().filter(p2).map(i -> i.substring(2).trim()).collect(Collectors.toList()));
        validNumberList.addAll(inputList.stream().filter(p3).collect(Collectors.toList()));
        return validNumberList;
    }

    private AggregatePOJO getSector(String number) {
        final String URI = "https://challenge-business-sector-api.meza.talkdeskstg.com/sector/{id}";

        Map<String, String> params = new HashMap<String, String>();
        params.put("id", number);

        try {

            return restTemplate.getForObject(URI, AggregatePOJO.class, params);
        } catch (HttpClientErrorException.BadRequest e) {
            return null;
        }
    }
}
