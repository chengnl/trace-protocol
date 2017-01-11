package wuyun.cnl.service;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import wuyun.cnl.TTraceProtocol;
import wuyun.cnl.ThreadTraceID;

public class ServiceBImpl implements ServiceA.Iface{

	@Override
	public String HelloWorld(String name) throws TException {
		System.out.println("ServiceB traceID:="+ThreadTraceID.get()+",args："+name);
		return "hello,world:"+name;
	}
}
