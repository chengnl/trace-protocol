package wuyun.cnl;

public class ThreadTraceID {
	//线程本地变量，子线程也能读取父线程设置变量，服务调用子线程调用其他的服务场景
	private static ThreadLocal<String> threadLocal =new InheritableThreadLocal<String>();
	public static void set(String traceID){
		threadLocal.set(traceID);
	}
	public static String get(){
		return threadLocal.get();
	}
	public static String genTraceID(TraceIDFunc func){
		return func.genTraceID();
	}
	
}
