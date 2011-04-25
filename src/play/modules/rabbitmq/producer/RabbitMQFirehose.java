package play.modules.rabbitmq.producer;

import java.util.List;

import play.Logger;
import play.jobs.Job;
import play.modules.rabbitmq.stats.StatsService;
import play.modules.rabbitmq.util.ExceptionUtil;

// TODO: Auto-generated Javadoc
/**
 * The Class RabbitMQFirehose.
 */
public abstract class RabbitMQFirehose<T> extends Job {

	/**
	 * Gets data to be loaded, loop on each one and publish them to RabbitMQ
	 * 
	 * @see play.jobs.Job#doJob()
	 */
	@Override
	public void doJob() {
		// Start Daemon
		while (true) {
			// Do Work               
			try {
				// Init counter that will keep track of each message published
				int itemsCount = 0;
				
				// Get Data
				List<T> items = getData(batchSize());
				
				// Check List
				if (items != null && items.size() > 0) {
					// Set count on item
					itemsCount = items.size();
					
					// Publish each message
					for (T item : items) {
						try {
							RabbitMQPublisher.publish(queueName(), item);
							
						} catch (Throwable t) {
							Logger.error(ExceptionUtil.getStackTrace(t));
						}
					}
				}
				
				// If null stop process
				if ( items == null ) {
					Logger.warn("No data available from firehose %s - quitting process...", this);
					return;
				}
				
				// If this batch didn't return the max number of entries put the process to sleep for a litle while
				if (itemsCount < batchSize()) {
					Thread.sleep(sleepInBetweenBatches());
				}

			} catch (Throwable t) {
				// Handle Exception
				Logger.error(ExceptionUtil.getStackTrace(t));
			}
		}
	}

	/**
	 * Gets the data.
	 * 
	 * @param n
	 *            the n
	 * @return the data
	 * @throws Exception
	 *             the exception
	 */
	protected abstract List<T> getData(int n) throws Exception;

	/**
	 * Batch size.
	 * 
	 * @return the int
	 */
	protected abstract int batchSize();

	/**
	 * Queue name.
	 * 
	 * @return the string
	 */
	protected abstract String queueName();

	/**
	 * Sleep in between batches.
	 * 
	 * @return the long
	 */
	protected long sleepInBetweenBatches() {
		long l = 1000l; // 1 sec
		l = l * 60; // 1 minute
		l = l * 5; // 5 minutes
		return l;
	}

}
