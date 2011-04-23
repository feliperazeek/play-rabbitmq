package play.modules.rabbitmq.sample;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.codehaus.jettison.json.JSONObject;

import play.jobs.OnApplicationStart;
import play.modules.rabbitmq.producer.RabbitMQFirehose;

// TODO: Auto-generated Javadoc
/**
 * The Class RabbitMQSampleFirehose.
 */
@OnApplicationStart(async = true)
public class RabbitMQSampleFirehose extends RabbitMQFirehose {

	/*
	 * (non-Javadoc)
	 * 
	 * @see play.modules.rabbitmq.producer.RabbitMQFirehose#getData(int)
	 */
	@Override
	protected List<String> getData(int n) throws Exception {
		List<String> results = new ArrayList<String>();
		for (int i = 0; i < n; i++) {
			results.add(new JSONObject().put("field1",
					"Hello World (" + new Date().getTime() + ")").toString());
		}
		return results;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see play.modules.rabbitmq.producer.RabbitMQFirehose#batchSize()
	 */
	@Override
	protected int batchSize() {
		return 2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see play.modules.rabbitmq.producer.RabbitMQFirehose#queueName()
	 */
	@Override
	protected String queueName() {
		return "myQueue";
	}

}
