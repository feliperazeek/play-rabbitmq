package play.modules.rabbitmq.producer;

import java.util.List;

import play.Logger;
import play.jobs.Job;
import play.modules.rabbitmq.util.ExceptionUtil;

// TODO: Auto-generated Javadoc
/**
 * The Class RabbitMQFirehose.
 */
public abstract class RabbitMQFirehose extends Job {

	/*
	 * (non-Javadoc)
	 * 
	 * @see play.jobs.Job#doJob()
	 */
	@Override
	public void doJob() {
		while (true) {
			try {
				int itemsCount = 0;
				List<String> items = getData(batchSize());
				if (items != null && items.size() > 0) {
					itemsCount = items.size();
					for (String item : items) {
						try {
							RabbitMQPublisher.publish(queueName(), item);
						} catch (Throwable t) {
							Logger.error(ExceptionUtil.getStackTrace(t));
						}
					}
				}
				if (itemsCount < batchSize()) {
					Thread.sleep(sleepInBetweenBatches());
				}

			} catch (Throwable t) {
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
	protected abstract List<String> getData(int n) throws Exception;

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
