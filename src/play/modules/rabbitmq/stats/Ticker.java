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

import java.util.concurrent.atomic.AtomicLong;

/**
 * The Class Tick.
 */
public class Ticker {

	/** The tick. */
	private final AtomicLong tick = new AtomicLong();

	/**
	 * Gets the and increment.
	 * 
	 * @param modulo
	 *            the modulo
	 * @return the and increment
	 */
	public final long tick() {
		for (;;) {
			long current = this.tick.get();
			long next = (current + 1);
			if (this.tick.compareAndSet(current, next)) {
				return current;
			}
		}
	}

	/**
	 * Gets the current number.
	 * 
	 * @return the long
	 */
	public final long current() {
		for (;;) {
			long current = this.tick.get();
			return current;
		}
	}

}