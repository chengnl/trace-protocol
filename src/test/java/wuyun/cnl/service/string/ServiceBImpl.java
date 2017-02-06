package wuyun.cnl.service.string;

import org.apache.thrift.TException;
import wuyun.cnl.ThreadTrace;

public class ServiceBImpl implements ServiceA.Iface{

	@Override
	public String HelloWorld(String name) throws TException {
		System.out.println("ServiceB traceID:="+ThreadTrace.get()+",args："+name);
		return "hello,world:"+name;
	}
}
