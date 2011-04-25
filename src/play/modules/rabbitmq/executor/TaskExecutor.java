package play.modules.rabbitmq.executor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

// TODO: Auto-generated Javadoc
/**
 * The Class TaskExecutor.
 */
public class TaskExecutor {

	/** The Constant taskCache. */
	private static final HashMap<String, Method> taskCache = new HashMap<String, Method>();

	/**
	 * Execute.
	 * 
	 * @param taskName
	 *            the task name
	 * @param message
	 *            the message
	 * @throws ClassNotFoundException
	 *             the class not found exception
	 * @throws NoSuchMethodException
	 *             the no such method exception
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 * @throws InvocationTargetException
	 *             the invocation target exception
	 */
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