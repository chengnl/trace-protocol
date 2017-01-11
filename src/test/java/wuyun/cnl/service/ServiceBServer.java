package wuyun.cnl.service;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

import wuyun.cnl.TTraceProtocol;

public class ServiceBServer {
public static void main(String[] args) {
	try {
		TServerTransport strans = new TServerSocket(8081);
		ServiceA.Processor<ServiceA.Iface> processor = new ServiceA.Processor<>(new ServiceBImpl());
		TSimpleServer.Args serverArgs =new TSimpleServer.Args(strans);
		serverArgs.protocolFactory(new TTraceProtocol.Factory(new TBinaryProtocol.Factory()));
		serverArgs.processor(processor);
		TServer server = new TSimpleServer(serverArgs);
		server.serve();
	} catch (TException e) {
		e.printStackTrace();
	}
	
}
}
