# trace-protocol
自定义thrift方法调用链跟踪协议，目的解决thrift方法调用链路径无法跟踪，服务依赖路径无法得知的问题

##协议说明
两种方式实现了跟踪调用链协议
TTraceStringProtocol：跟踪协议内容为字符串。
TTraceStructProtocol：跟踪协议内容为结构体，thrift定义的结构体，扩展性更强，支持添加跟多的跟踪属性。

##TTraceStringProtocol调用方式

客户端代码处理，使用跟踪调用链协议包装原有协议
```
new TTraceStringProtocol(new TBinaryProtocol(transport));

```
样例代码：

```
        TTransport transport = null;
		try {
			transport = new TSocket("127.0.0.1", 8080);
			transport.open();
			TProtocol protocol = new TTraceStringProtocol(new TBinaryProtocol(transport));
			ServiceA.Client client = new ServiceA.Client(protocol);
			System.out.println(client.HelloWorld("test A"));
			System.out.println("CALL ServiceA traceID:="+ThreadTraceID.get());
			
		} catch (TException e) {
			e.printStackTrace();
		} finally {
			transport.close();
		}
```

服务端代码处理，使用new TTraceStringProtocol.Factory包装原有协议工厂。
```
serverArgs.protocolFactory(new TTraceStringProtocol.Factory(new TBinaryProtocol.Factory()));
```

样例代码：
```
	try {
		TServerTransport strans = new TServerSocket(8080);
		ServiceA.Processor<ServiceA.Iface> processor = new ServiceA.Processor<>(new ServiceAImpl());
		TSimpleServer.Args serverArgs =new TSimpleServer.Args(strans);
		serverArgs.protocolFactory(new TTraceStringProtocol.Factory(new TBinaryProtocol.Factory()));
		serverArgs.processor(processor);
		TServer server = new TSimpleServer(serverArgs);
		server.serve();
	} catch (TException e) {
		e.printStackTrace();
	}
```
具体见test目录下面的wuyun.cnl.service.string实现


##TTraceStructProtocol调用方式
自己根据业务需要定义trace结构体，参考resource里面的trace.thrift,然后生成Trace类

客户端代码处理，使用跟踪调用链协议包装原有协议，实现Trace类生成函数
```
TProtocol protocol= new TTraceStructProtocol(new TCompactProtocol(transport),new TraceFunc(){
		@Override
		public Trace genTrace() {
			Trace trace = new Trace();
			trace.setTraceID(UUID.randomUUID().toString());
			trace.setCallInfo("Service");
			return trace;
		}
		});

```
样例代码：

```
        TTransport transport = null;
		try {
			transport = new TSocket("127.0.0.1", 8080);
			transport.open();
			TProtocol protocol= new TTraceStructProtocol(new TCompactProtocol(transport),new TraceFunc(){
					@Override
					public Trace genTrace() {
						Trace trace = new Trace();
						trace.setTraceID(UUID.randomUUID().toString());
						trace.setCallInfo("Service");
						return trace;
					}
			});
			ServiceA.Client client = new ServiceA.Client(protocol);
			System.out.println(client.HelloWorld("test A"));
			System.out.println("CALL ServiceA traceID:="+ThreadTraceID.get());
			
		} catch (TException e) {
			e.printStackTrace();
		} finally {
			transport.close();
		}
```

具体见test目录下面的wuyun.cnl.service.struct实现


