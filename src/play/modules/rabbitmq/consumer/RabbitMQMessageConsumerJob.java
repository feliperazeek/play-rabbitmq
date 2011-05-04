package play.modules.rabbitmq.consumer;

import play.Logger;
import play.jobs.Job;
import play.modules.rabbitmq.stats.StatisticsEvent;
import play.modules.rabbitmq.stats.StatisticsStream;
import play.modules.rabbitmq.util.ExceptionUtil;

import com.rabbitmq.client.Channel;

/**
 * The Class RabbitMQMessageConsumerJob.
 * 
 * @param <T>
 *            the generic type
 */
public class RabbitMQMessageConsumerJob<T> extends Job<T> {

	/** The message. */
	private T message;

	/** The consumer. */
	private RabbitMQConsumer consumer;

	/** The retries. */
	private int retries;

	/** The channel. */
	private Channel channel;

	/** The delivery tag. */
	private long deliveryTag;

	/** The queue. */
	private String queue;

	/**
	 * Instantiates a new rabbit mq message consumer job.
	 * 
	 * @param consumer
	 *            the consumer
	 * @param message
	 *            the message
	 */
	public RabbitMQMessageConsumerJob(Channel channel, long deliveryTag, String queue, RabbitMQConsumer consumer, T message, int retries) {
		this.consumer = consumer;
		this.message = message;
		this.retries = retries;
		this.channel = channel;
		this.deliveryTag = deliveryTag;
		this.queue = queue;
	}

	/**
	 * Consumer Message
	 * 
	 * @see play.jobs.Job#doJob()
	 */
	@Override
	public void doJob() {
		// Keeps track number of times message has been tried to get
		// re-delivered
		int retryCount = 0;

		// Flag that indicates if the message was consumed successfully
		boolean success = false;

		// Define Exception
		Throwable exception = null;

		// Loop until it's done retrying
		while (retryCount < this.retries + 1) {
			// Log Debug
			if (retryCount > 0) {
				Logger.info("Retrying to process message (%s) by consumer (%s) on queue (%s). Attempt %s of %s total retries.", this.message, this.consumer, this.queue, retryCount, this.retries);
			}

			// Process Message
			try {
				// Start Timer
				long start = System.nanoTime();

				// Call Consumer
				this.consumer.consume(this.message);
				success = true;

				// Now tell Daddy everything is cool
				this.channel.basicAck(this.deliveryTag, false);

				// Execution Time
				long executionTime = System.nanoTime() - start;
				Logger.info("Message %s from queue %s has been processed by consumer %s (execution time: %s ms)", this.message, this.queue, this.consumer, executionTime);

				// Update Stats
				StatisticsStream.add(new StatisticsEvent(this.queue, StatisticsEvent.Type.CONSUMER, StatisticsEvent.Status.SUCCESS));

			} catch (Throwable t) {
				// Log Exception
				exception = t;
				Logger.error("Error processing message (%s) with consumer (%s). Exception: %s", this.message, this.consumer, ExceptionUtil.getStackTrace(t));

				// Update Stats
				StatisticsStream.add(new StatisticsEvent(this.queue, StatisticsEvent.Type.CONSUMER, StatisticsEvent.Status.ERROR));
			}

			// Check Successful Execution
			if (success) {
				break;
			} else {
				retryCount++;
			}
		}

		// Log Debug
		if (!success) {
			Logger.error("Final error processing message (%s) with consumer (%s). Last Exception: %s", this.message, this.consumer, exception);
		}
	}

}
