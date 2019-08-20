package com.acme.service.minsystemservice.controller;

import com.acme.service.Dto.Expression;
import com.acme.service.Dto.ExpressionResult;
import com.acme.service.Enums.OPERATE;
import com.acme.service.service.TCalculate;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.springframework.web.bind.annotation.*;

/**
 * @author acme
 * @date 2019/7/26 12:06 AM
 */
@RestController
@RequestMapping("/calculate")
public class CalculateController {

    @GetMapping("/add/{num1}/{num2}")
    public Object add(@PathVariable("num1") double num1, @PathVariable("num2") double num2) throws TException {
        return calculate(new Expression(OPERATE.ADD, num1, num2));
    }

    @GetMapping("/sub/{num1}/{num2}")
    public Object sub(@PathVariable("num1") double num1, @PathVariable("num2") double num2) throws TException {
        return calculate(new Expression(OPERATE.SUB, num1, num2));
    }

    @GetMapping("/mul")
    public Object mul(@RequestParam("num1") double num1, @RequestParam("num2") double num2) throws TException {
        return calculate(new Expression(OPERATE.MUL, num1, num2));
    }

    @GetMapping("/div")
    public Object div(@RequestParam("num1") double num1, @RequestParam("num2") double num2) throws TException {
        return calculate(new Expression(OPERATE.DIV, num1, num2));
    }

    private double calculate(Expression expression) throws TException {
        // 创建 TTransport
        TTransport transport = new TSocket("192.168.0.103", 9000, 2000);
        // 创建 TProtocol
        TProtocol protocol = new TBinaryProtocol(transport);

        // 创建客户端.
        TCalculate.Client client = new TCalculate.Client(protocol);

        // 打开 TTransport
        transport.open();

        // 调用服务方法
        ExpressionResult result = client.calculate(expression);
        transport.close();
        return result.getResult();
    }
}
