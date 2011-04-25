package play.modules.rabbitmq.stats;

import java.io.Serializable;

// TODO: Auto-generated Javadoc
/**
 * The Class Stats.
 */
public class Stats implements Serializable {

	/** The consumer success count. */
	private long consumerSuccessCount = 0l;

	/** The consumer failed count. */
	private long consumerFailedCount = 0l;

	/** The consumer total count. */
	private long consumerTotalCount = 0l;

	/** The producer success count. */
	private long producerSuccessCount = 0l;

	/** The producer failed count. */
	private long producerFailedCount = 0l;

	/** The producer total count. */
	private long producerTotalCount = 0l;

	/**
	 * Instantiates a new stats.
	 */
	public Stats() {

	}

	/**
	 * Instantiates a new stats.
	 * 
	 * @param consumerSuccessCount
	 *            the consumer success count
	 * @param consumerFailedCount
	 *            the consumer failed count
	 * @param consumerTotalCount
	 *            the consumer total count
	 * @param producerSuccessCount
	 *            the producer success count
	 * @param producerFailedCount
	 *            the producer failed count
	 * @param producerTotalCount
	 *            the producer total count
	 */
	public Stats(long consumerSuccessCount, long consumerFailedCount,
			long consumerTotalCount, long producerSuccessCount,
			long producerFailedCount, long producerTotalCount) {
		super();
		this.consumerSuccessCount = consumerSuccessCount;
		this.consumerFailedCount = consumerFailedCount;
		this.consumerTotalCount = consumerTotalCount;
		this.producerSuccessCount = producerSuccessCount;
		this.producerFailedCount = producerFailedCount;
		this.producerTotalCount = producerTotalCount;
	}

	/**
	 * Gets the consumer success count.
	 * 
	 * @return the consumer success count
	 */
	public long getConsumerSuccessCount() {
		return consumerSuccessCount;
	}

	/**
	 * Sets the consumer success count.
	 * 
	 * @param consumerSuccessCount
	 *            the new consumer success count
	 */
	public void setConsumerSuccessCount(long consumerSuccessCount) {
		this.consumerSuccessCount = consumerSuccessCount;
	}

	/**
	 * Gets the consumer failed count.
	 * 
	 * @return the consumer failed count
	 */
	public long getConsumerFailedCount() {
		return consumerFailedCount;
	}

	/**
	 * Sets the consumer failed count.
	 * 
	 * @param consumerFailedCount
	 *            the new consumer failed count
	 */
	public void setConsumerFailedCount(long consumerFailedCount) {
		this.consumerFailedCount = consumerFailedCount;
	}

	/**
	 * Gets the consumer total count.
	 * 
	 * @return the consumer total count
	 */
	public long getConsumerTotalCount() {
		return consumerTotalCount;
	}

	/**
	 * Sets the consumer total count.
	 * 
	 * @param consumerTotalCount
	 *            the new consumer total count
	 */
	public void setConsumerTotalCount(long consumerTotalCount) {
		this.consumerTotalCount = consumerTotalCount;
	}

	/**
	 * Gets the producer success count.
	 * 
	 * @return the producer success count
	 */
	public long getProducerSuccessCount() {
		return producerSuccessCount;
	}

	/**
	 * Sets the producer success count.
	 * 
	 * @param producerSuccessCount
	 *            the new producer success count
	 */
	public void setProducerSuccessCount(long producerSuccessCount) {
		this.producerSuccessCount = producerSuccessCount;
	}

	/**
	 * Gets the producer failed count.
	 * 
	 * @return the producer failed count
	 */
	public long getProducerFailedCount() {
		return producerFailedCount;
	}

	/**
	 * Sets the producer failed count.
	 * 
	 * @param producerFailedCount
	 *            the new producer failed count
	 */
	public void setProducerFailedCount(long producerFailedCount) {
		this.producerFailedCount = producerFailedCount;
	}

	/**
	 * Gets the producer total count.
	 * 
	 * @return the producer total count
	 */
	public long getProducerTotalCount() {
		return producerTotalCount;
	}

	/**
	 * Sets the producer total count.
	 * 
	 * @param producerTotalCount
	 *            the new producer total count
	 */
	public void setProducerTotalCount(long producerTotalCount) {
		this.producerTotalCount = producerTotalCount;
	}

}
