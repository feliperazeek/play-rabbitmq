package play.modules.rabbitmq.stats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

// TODO: Auto-generated Javadoc
/**
 * The Class StatsService.
 */
public class StatsService {
	
	/** The Constant instrumentedTasks. */
	protected static final ArrayList<String> instrumentedTasks = new ArrayList<String>();

	/** The Constant executionTimes. */
	protected static final HashMap<String, LinkedList<Long>> executionTimes = new HashMap<String, LinkedList<Long>>();

	/** The Constant consumerSuccessCount. */
	protected static final HashMap<String, Integer> consumerSuccessCount = new HashMap<String, Integer>();

	/** The Constant consumerFailedCount. */
	protected static final HashMap<String, Integer> consumerFailedCount = new HashMap<String, Integer>();

	/** The Constant producerSuccessCount. */
	protected static final HashMap<String, Integer> producerSuccessCount = new HashMap<String, Integer>();

	/** The Constant producerFailedCount. */
	protected static final HashMap<String, Integer> producerFailedCount = new HashMap<String, Integer>();

	/** The Constant metricsLock. */
	protected static final Object metricsLock = new Object();

	/**
	 * Queue stats.
	 * 
	 * @param queueName
	 *            the queue name
	 * @return the stats
	 */
	public static Stats queueStats(String queueName) {
		Stats stats = new Stats();
		if ( consumerSuccessCount != null && consumerSuccessCount.containsKey(queueName) ) {
			stats.setConsumerSuccessCount(new Long(consumerSuccessCount.get(queueName)));
		}
		if ( consumerFailedCount != null && consumerFailedCount.containsKey(queueName) ) {
			stats.setConsumerFailedCount(new Long(consumerFailedCount.get(queueName)));
		}
		if ( producerSuccessCount != null && producerSuccessCount.containsKey(queueName) ) {
			stats.setProducerSuccessCount(new Long(producerSuccessCount.get(queueName)));
		}
		if ( producerFailedCount != null && producerFailedCount.containsKey(queueName) ) {
			stats.setProducerFailedCount(new Long(producerFailedCount.get(queueName)));
		}
		return stats;
	}

	/**
	 * Consumer update.
	 * 
	 * @param queue
	 *            the queue
	 * @param time
	 *            the time
	 * @param status
	 *            the status
	 * @param retries
	 *            the retries
	 */
	public static void consumerUpdate(String queue, long time, boolean status,
			int retries) {

		synchronized (metricsLock) {
			if (!instrumentedTasks.contains(queue))
				instrumentedTasks.add(queue);

			updateExecutionTimes(queue, time);
			updateConsumerResults(queue, status);
		}
	}

	/**
	 * Producer update.
	 * 
	 * @param queue
	 *            the queue
	 * @param time
	 *            the time
	 * @param status
	 *            the status
	 * @param retries
	 *            the retries
	 */
	public static void producerUpdate(String queue, long time, boolean status,
			int retries) {

		synchronized (metricsLock) {
			if (!instrumentedTasks.contains(queue))
				instrumentedTasks.add(queue);

			updateExecutionTimes(queue, time);
			updateProducerResults(queue, status);
		}
	}

	/**
	 * Update execution times.
	 * 
	 * @param queue
	 *            the queue
	 * @param time
	 *            the time
	 */
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

	/**
	 * Update producer results.
	 * 
	 * @param queue
	 *            the queue
	 * @param status
	 *            the status
	 */
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

	/**
	 * Update consumer results.
	 * 
	 * @param queue
	 *            the queue
	 * @param status
	 *            the status
	 */
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
