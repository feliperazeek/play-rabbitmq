package play.modules.rabbitmq.stats;

import java.io.Serializable;

public class Stats implements Serializable {

	private long consumerSuccessCount;

	private long consumerFailedCount;

	private long consumerTotalCount;

	private long producerSuccessCount;

	private long producerFailedCount;

	private long producerTotalCount;
	
	public Stats() {
		
	}
	
	

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



	public long getConsumerSuccessCount() {
		return consumerSuccessCount;
	}

	public void setConsumerSuccessCount(long consumerSuccessCount) {
		this.consumerSuccessCount = consumerSuccessCount;
	}

	public long getConsumerFailedCount() {
		return consumerFailedCount;
	}

	public void setConsumerFailedCount(long consumerFailedCount) {
		this.consumerFailedCount = consumerFailedCount;
	}

	public long getConsumerTotalCount() {
		return consumerTotalCount;
	}

	public void setConsumerTotalCount(long consumerTotalCount) {
		this.consumerTotalCount = consumerTotalCount;
	}

	public long getProducerSuccessCount() {
		return producerSuccessCount;
	}

	public void setProducerSuccessCount(long producerSuccessCount) {
		this.producerSuccessCount = producerSuccessCount;
	}

	public long getProducerFailedCount() {
		return producerFailedCount;
	}

	public void setProducerFailedCount(long producerFailedCount) {
		this.producerFailedCount = producerFailedCount;
	}

	public long getProducerTotalCount() {
		return producerTotalCount;
	}

	public void setProducerTotalCount(long producerTotalCount) {
		this.producerTotalCount = producerTotalCount;
	}

}
