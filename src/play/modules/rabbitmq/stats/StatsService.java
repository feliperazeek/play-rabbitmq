package play.modules.rabbitmq.stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("/rabbitmq")
public class StatsService {

	// Keep track of all tasks we've seen executed.
	protected static final ArrayList<String> instrumentedTasks = new ArrayList<String>();

	// Keep track of average task throughput (last 10k runs per task).
	protected static final HashMap<String, LinkedList<Long>> executionTimes = new HashMap<String, LinkedList<Long>>();

	// Keep track of total successes by task.
	protected static final HashMap<String, Integer> consumerSuccessCount = new HashMap<String, Integer>();

	// Keep track of total failures by task.
	protected static final HashMap<String, Integer> consumerFailedCount = new HashMap<String, Integer>();

	// Keep track of total successes by task.
	protected static final HashMap<String, Integer> producerSuccessCount = new HashMap<String, Integer>();

	// Keep track of total failures by task.
	protected static final HashMap<String, Integer> producerFailedCount = new HashMap<String, Integer>();

	protected static final Object metricsLock = new Object();

	@GET
	@Path("/queue/{queue}/stats")
	@Produces("application/json")
	public Stats queueStats(@PathParam("queue") String queueName) {
		return null;
	}

	// Updates internal metrics following task execution.
	public static void consumerUpdate(String queue, long time, boolean status,
			int retries) {

		synchronized (metricsLock) {
			if (!instrumentedTasks.contains(queue))
				instrumentedTasks.add(queue);

			updateExecutionTimes(queue, time);
			updateConsumerResults(queue, status);
		}
	}

	public static void producerUpdate(String queue, long time, boolean status,
			int retries) {

		synchronized (metricsLock) {
			if (!instrumentedTasks.contains(queue))
				instrumentedTasks.add(queue);

			updateExecutionTimes(queue, time);
			updateProducerResults(queue, status);
		}
	}

	// Update the list of execution times, keeping the last 10,000 per task.
	private static void updateExecutionTimes(String queue, long time) {
		if (!executionTimes.containsKey(queue)) {
			LinkedList<Long> timeList = new LinkedList<Long>();
			timeList.addFirst(time);
			executionTimes.put(queue, timeList);
		} else {
			LinkedList<Long> timeList = executionTimes.get(queue);
			if (timeList.size() == 10000)
				timeList.removeLast();
			timeList.addFirst(time);
			executionTimes.put(queue, timeList);
		}
	}

	// Update the number of times this task has succeeded or failed.
	private static void updateProducerResults(String queue, boolean status) {
		if (status == true) {
			if (!producerSuccessCount.containsKey(queue)) {
				producerSuccessCount.put(queue, 1);
			} else {
				int success = producerSuccessCount.get(queue);
				producerSuccessCount.put(queue, success + 1);
			}
		} else {
			if (!producerFailedCount.containsKey(queue)) {
				producerFailedCount.put(queue, 1);
			} else {
				int failure = producerFailedCount.get(queue);
				producerFailedCount.put(queue, failure + 1);
			}
		}
	}

	// Update the number of times this task has succeeded or failed.
	private static void updateConsumerResults(String queue, boolean status) {
		if (status == true) {
			if (!consumerSuccessCount.containsKey(queue)) {
				consumerSuccessCount.put(queue, 1);
			} else {
				int success = consumerSuccessCount.get(queue);
				consumerSuccessCount.put(queue, success + 1);
			}
		} else {
			if (!consumerFailedCount.containsKey(queue)) {
				consumerFailedCount.put(queue, 1);
			} else {
				int failure = consumerFailedCount.get(queue);
				consumerFailedCount.put(queue, failure + 1);
			}
		}
	}

}
