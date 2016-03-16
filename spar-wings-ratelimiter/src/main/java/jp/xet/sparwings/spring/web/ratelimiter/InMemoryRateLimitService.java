/*
 * Copyright 2015-2016 Miyamoto Daisuke.
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
 */
package jp.xet.sparwings.spring.web.ratelimiter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import jp.xet.baseunits.timeutil.Clock;
import lombok.Getter;
import lombok.Setter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link RateLimitService} implementation to store values in memory.
 * 
 * @since 0.8
 * @author daisuke
 */
public class InMemoryRateLimitService implements RateLimitService {
	
	private static Logger logger = LoggerFactory.getLogger(InMemoryRateLimitService.class);
	
	private Map<String, RateLimitSpec> specs = new ConcurrentHashMap<>();
	
	@Setter
	private Function<String, RateLimitRecovery> recoveryRepos = limitationUnit -> new RateLimitRecovery(10, 1000000);
	
	
	@Override
	public synchronized RateLimitDescriptor consume(String limitationUnit, long consumption) {
		RateLimitRecovery recovery = recoveryRepos.apply(limitationUnit);
		long fillRate = recovery.getFillRate();
		long maxBudget = recovery.getMaxBudget();
		
		long now = Clock.now().toEpochSec();
		RateLimitSpec spec =
				specs.computeIfAbsent(limitationUnit, p -> new RateLimitSpec(fillRate, maxBudget, maxBudget, now));
		long secSinceLastUpdate = now - spec.getLastUpdateTime();
		logger.info("Time (sec) since last update = {}", secSinceLastUpdate);
		long fill = secSinceLastUpdate * spec.getFillRate();
		long budget = Math.min(spec.getMaxBudget(), spec.getCurrentBudget() + fill) - consumption;
		spec.lastUpdateTime = now;
		
		logger.info("Current budget and consumption: (filled {}) and {} - {}", fill, budget + consumption, consumption);
		spec.setCurrentBudget(budget);
		
		return spec;
	}
	
	@Override
	public RateLimitDescriptor get(String limitationUnit) {
		RateLimitRecovery recovery = recoveryRepos.apply(limitationUnit);
		long fillRate = recovery.getFillRate();
		long maxBudget = recovery.getMaxBudget();
		
		long now = Clock.now().toEpochSec();
		RateLimitSpec spec =
				specs.computeIfAbsent(limitationUnit, p -> new RateLimitSpec(fillRate, maxBudget, maxBudget, now));
		long secSinceLastUpdate = now - spec.getLastUpdateTime();
		logger.info("Time (sec) since last update = {}", secSinceLastUpdate);
		long fill = secSinceLastUpdate * spec.getFillRate();
		long budget = Math.min(spec.getMaxBudget(), spec.getCurrentBudget() + fill);
		spec.lastUpdateTime = now;
		
		spec.setCurrentBudget(budget);
		logger.info("Current budget: (filled {}) and {}", fill, spec.getCurrentBudget());
		return spec;
	}
	
	
	private static class RateLimitSpec extends RateLimitDescriptor {
		
		@Getter
		private long lastUpdateTime;
		
		
		public RateLimitSpec(long fillRate, long maxBudget, long currentBudget, long lastUpdateTime) {
			super(fillRate, maxBudget, currentBudget);
			this.lastUpdateTime = lastUpdateTime;
		}
	}
}