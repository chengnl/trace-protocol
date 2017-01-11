# trace-protocol
自定义thrift方法调用链跟踪协议，目的解决thrift方法调用链路径无法跟踪，服务依赖路径无法得知的问题


##调用方式

客户端代码处理，使用TTraceProtocol包装原有协议
```
new TTraceProtocol(new TBinaryProtocol(transport));

```
样例代码：

```
        TTransport transport = null;
		try {
			transport = new TSocket("127.0.0.1", 8080);
			transport.open();
			TProtocol protocol = new TTraceProtocol(new TBinaryProtocol(transport));
			ServiceA.Client client = new ServiceA.Client(protocol);
			System.out.println(client.HelloWorld("test A"));
			System.out.println("CALL ServiceA traceID:="+ThreadTraceID.get());
			
		} catch (TException e) {
			e.printStackTrace();
		} finally {
			transport.close();
		}
```

服务端代码处理，使用new TTraceProtocol.Factory包装原有协议工厂。
```
serverArgs.protocolFactory(new TTraceProtocol.Factory(new TBinaryProtocol.Factory()));
```

样例代码：
```
	try {
		TServerTransport strans = new TServerSocket(8080);
		ServiceA.Processor<ServiceA.Iface> processor = new ServiceA.Processor<>(new ServiceAImpl());
		TSimpleServer.Args serverArgs =new TSimpleServer.Args(strans);
		serverArgs.protocolFactory(new TTraceProtocol.Factory(new TBinaryProtocol.Factory()));
		serverArgs.processor(processor);
		TServer server = new TSimpleServer(serverArgs);
		server.serve();
	} catch (TException e) {
		e.printStackTrace();
	}
```
