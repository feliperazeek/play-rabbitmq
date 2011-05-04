/** 
 * Copyright 2011 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * @author Felipe Oliveira (http://mashup.fm)
 * 
 */
package play.modules.rabbitmq.stats;

import play.Logger;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.libs.F.Promise;
import play.modules.rabbitmq.stats.StatisticsAction.Tick;
import play.modules.rabbitmq.util.ExceptionUtil;

/**
 * The Class MrT.
 */
@OnApplicationStart(async = true)
public class StatisticsStream extends Job {

	/** The Stream. */
	public static play.libs.F.EventStream<StatisticsEvent> stream = new play.libs.F.EventStream<StatisticsEvent>();

	/** The live stream. */
	public static play.libs.F.EventStream<StatisticsEvent> liveStream = new play.libs.F.EventStream<StatisticsEvent>();

	/**
	 * Adds the.
	 * 
	 * @param activity
	 *            the activity
	 */
	public static void add(StatisticsEvent activity) {
		stream.publish(activity);
		liveStream.publish(activity);
	}

	/**
	 * Consume Activities
	 */
	@Override
	public void doJob() {
		while (true) {
			await(stream.nextEvent());
		}
	}

	/**
	 * Await.
	 * 
	 * @param future
	 *            the future
	 * @param c
	 *            the c
	 */
	protected static void await(Promise<StatisticsEvent> promise) {
		try {
			StatisticsEvent item = promise.get();
			new Tick().invoke(item);

		} catch (Throwable t) {
			Logger.error(ExceptionUtil.getStackTrace(t));
		}
	}
}
