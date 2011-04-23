package play.modules.rabbitmq.executor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

public class TaskExecutor {

	private static final HashMap<String, Method> taskCache = new HashMap<String, Method>();

	@SuppressWarnings("unchecked")
	public static void execute(String taskName, String message)
			throws ClassNotFoundException, NoSuchMethodException,
			IllegalAccessException, InvocationTargetException {

		Method method = null;

		if (taskCache.containsKey(taskName)) {
			method = taskCache.get(taskName);
		} else {
			Class task = Class.forName(taskName);
			method = task.getMethod("run", new Class[] { String.class });
			taskCache.put(taskName, method);
		}

		method.invoke(null, new Object[] { message });
	}

}