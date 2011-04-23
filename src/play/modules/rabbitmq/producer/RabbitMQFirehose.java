package play.modules.rabbitmq.producer;

import java.util.List;

import play.Logger;
import play.jobs.Job;
import play.modules.rabbitmq.util.ExceptionUtil;

public abstract class RabbitMQFirehose extends Job {

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

	protected abstract List<String> getData(int n) throws Exception;

	protected abstract int batchSize();

	protected abstract String queueName();

	protected long sleepInBetweenBatches() {
		long l = 1000l; // 1 sec
		l = l * 60; // 1 minute
		l = l * 5; // 5 minutes
		return l;
	}

}
