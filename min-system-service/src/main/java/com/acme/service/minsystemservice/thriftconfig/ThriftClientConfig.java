package com.acme.service.minsystemservice.thriftconfig;

import com.acme.rc.service.TRegistCenter;
import com.acme.service.minsystemservice.util.NetWorkUtil;
import com.acme.service.service.TCalculate;
import com.acme.sg.Dto.MachineInfo;
import com.acme.sg.Dto.MachineInfoParam;
import com.acme.sg.Dto.MachineInfoResult;
import com.acme.sg.service.TServiceGovernance;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransportException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author acme
 * @date 2019/12/1 9:10 PM
 */
@Slf4j
@Configuration
public class ThriftClientConfig {

    @Value("${RegistCenter.host}")
    private String registCenterHost;
    @Value("${RegistCenter.port}")
    private Integer registCenterPort;
    @Value("${ServiceGovernanc.host}")
    private String serviceGovernancHost;
    @Value("${ServiceGovernanc.port}")
    private Integer serviceGovernancPort;
    @Value("${local.appKey}")
    private String appKey;
    @Value("${local.port}")
    private Integer port;
    @Value("${msts.appKey}")
    private String mstsAppKey;
    private final static String IP = NetWorkUtil.getInet4Address();
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    @Autowired
    private TServiceGovernance.Client tServiceGovernanceClient;


    @Bean
    public TRegistCenter.Client tRegistCenterClient() throws TTransportException {
        TSocket socket = new TSocket(registCenterHost, registCenterPort, 20000);
        TFramedTransport transport = new TFramedTransport(socket);
        TProtocol protocol = new TBinaryProtocol(transport);
        TRegistCenter.Client client = new TRegistCenter.Client(protocol);
        transport.open();
        return client;
    }

    @Bean
    public TCalculate.Client tCalculateClient() throws TException {
        MachineInfoResult machineList = tServiceGovernanceClient.getMachineList(mstsAppKey);
        if(machineList == null || machineList.getMachineInfo().isEmpty()){
            throw new RuntimeException(String.format("appKey=%s,无可用机器", mstsAppKey));
        }
        //为了方便，暂时只要第一个
        TSocket socket = new TSocket(machineList.getMachineInfo().get(0).getIp(), machineList.getMachineInfo().get(0).getPort(), 20000);
        TFramedTransport transport = new TFramedTransport(socket);
        TProtocol protocol = new TBinaryProtocol(transport);
        TCalculate.Client client = new TCalculate.Client(protocol);
        transport.open();
        return client;
    }

    @Bean
    public TServiceGovernance.Client tServiceGovernanceClient() throws TException {
        TSocket socket = new TSocket(serviceGovernancHost, serviceGovernancPort, 20000);
        TFramedTransport transport = new TFramedTransport(socket);
        TProtocol protocol = new TBinaryProtocol(transport);
        TServiceGovernance.Client client = new TServiceGovernance.Client(protocol);
        transport.open();
        client.start(new MachineInfoParam(appKey, Arrays.asList(new MachineInfo(IP, port))));
        executorService.scheduleWithFixedDelay(() -> {
            try {
                client.heartbeat(new MachineInfoParam(appKey, Arrays.asList(new MachineInfo(IP, port))));
            } catch (TException e) {
                log.warn("TServiceGovernance offline");
            }
        }, 0, 10, TimeUnit.SECONDS);        return client;
    }
}
