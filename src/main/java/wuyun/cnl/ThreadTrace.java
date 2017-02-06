package wuyun.cnl;

public class ThreadTrace {
	//线程本地变量，子线程也能读取父线程设置变量，服务调用子线程调用其他的服务场景
	@SuppressWarnings("rawtypes")
	private static ThreadLocal threadLocal =new InheritableThreadLocal();
	@SuppressWarnings("unchecked")
	public static <T> void set(T traceID){
		threadLocal.set(traceID);
	}
	@SuppressWarnings("unchecked")
	public static <T> T get(){
		return (T) threadLocal.get();
	}
	public static <T> T genTraceID(TraceFunc func){
		return func.genTrace();
	}
	
}
