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

import java.util.concurrent.ConcurrentHashMap;


/**
 * The Interface Action.
 * 
 * @param <StatisticsAction>
 *            the generic type
 */
public interface StatisticsAction<T> extends play.libs.F.Action<T> {

	/**
	 * The Class TickX.
	 */
	public static final class Tick implements StatisticsAction<StatisticsEvent> {

		/** The Constant map. */
		protected static ConcurrentHashMap<StatisticsEvent, Ticker> map = new ConcurrentHashMap<StatisticsEvent, Ticker>();

		/**
		 * Find T for X and Tick it!
		 * 
		 * @see play.libs.F.Action#invoke(java.lang.Object)
		 */
		@Override
		public void invoke(final StatisticsEvent key) {
			Ticker t = new Ticker();
			map.putIfAbsent(key, t);
			t = map.get(key);
			t.tick();
		}
	}

}
