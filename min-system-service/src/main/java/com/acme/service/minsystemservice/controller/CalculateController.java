package com.acme.service.minsystemservice.controller;

import com.acme.service.Dto.Expression;
import com.acme.service.Enums.OPERATE;
import com.acme.service.service.TCalculate;
import org.apache.thrift.TException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author acme
 * @date 2019/7/26 12:06 AM
 */
@RestController
@RequestMapping("/calculate")
public class CalculateController {

    @Autowired
    private TCalculate.Client tCalculateClient;

    @GetMapping("/add/{num1}/{num2}")
    public Object add(@PathVariable("num1") double num1, @PathVariable("num2") double num2) throws TException {
        return tCalculateClient.calculate(new Expression(OPERATE.ADD, num1, num2));
    }

    @GetMapping("/sub/{num1}/{num2}")
    public Object sub(@PathVariable("num1") double num1, @PathVariable("num2") double num2) throws TException {
        return tCalculateClient.calculate(new Expression(OPERATE.SUB, num1, num2));
    }

    @GetMapping("/mul")
    public Object mul(@RequestParam("num1") double num1, @RequestParam("num2") double num2) throws TException {
        return tCalculateClient.calculate(new Expression(OPERATE.MUL, num1, num2));
    }

    @GetMapping("/div")
    public Object div(@RequestParam("num1") double num1, @RequestParam("num2") double num2) throws TException {
        return tCalculateClient.calculate(new Expression(OPERATE.DIV, num1, num2));
    }
}
