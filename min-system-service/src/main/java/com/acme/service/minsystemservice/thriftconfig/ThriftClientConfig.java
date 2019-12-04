package com.acme.service.minsystemservice.thriftconfig;

import com.acme.rc.service.TRegistCenter;
import com.acme.service.minsystemservice.util.NetWorkUtil;
import com.acme.service.service.TCalculate;
import com.acme.sg.Dto.MachineInfoParam;
import com.acme.sg.service.TServiceGovernance;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransportException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author acme
 * @date 2019/12/1 9:10 PM
 */
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
    @Value("${msts.host}")
    private String mstsHost;
    @Value("${msts.port}")
    private Integer mstsPort;
    @Value("${local.appKey}")
    private String appKey;
    @Value("${local.port}")
    private Integer port;
    private final static String IP = NetWorkUtil.getInet4Address();


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
    public TCalculate.Client tCalculateClient() throws TTransportException {
        TSocket socket = new TSocket(mstsHost, mstsPort, 20000);
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
        client.start(new MachineInfoParam(appKey, IP, port));
        return client;
    }
}
