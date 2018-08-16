/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.mongodb.core.query;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.lang.Nullable;

/**
 * The context a {@link Query} is run in with potential influence to how the query should be rendered. <br />
 * 
 * @author Christoph Strobl
 * @since 2.1
 */
public interface QueryContext {

	/**
	 * Get the {@link MongoCriteriaOperator mongo operator} of the {@link RawCriteria} identified by the given
	 * {@literal operator} suitable for in the given {@link QueryContext}.
	 *
	 * @param criteria the {@link RawCriteria} object.
	 * @param operator the MongoDB operator (eg. {@literal $near}) to process
	 * @return never {@literal null}.
	 */
	default MongoCriteriaOperator getMongoOperator(RawCriteria criteria, String operator) {
		return criteria.markProcessed(operator, criteria.valueOf(operator));
	}

	/**
	 * {@link Criteria} encapsulation within the {@link QueryContext} that allows to mark entries / operators as already
	 * processed so that they may not be rendered multiple times. This is useful when combining multiple operators into
	 * one.
	 * 
	 * @author Christoph Strobl
	 * @since 2.1
	 */
	class RawCriteria {

		final Map<String, Object> entries;
		final Set<String> processed;

		public RawCriteria(Map<String, Object> entries) {

			this.entries = entries;
			processed = new HashSet<>(entries.size());
		}

		public boolean contains(String operator) {
			return entries.keySet().contains(operator);
		}

		@Nullable
		public Object valueOf(String operator) {
			return entries.get(operator);
		}

		public boolean alreadyProcessed(String operator) {
			return processed.contains(operator);
		}

		public void markProcessed(String operator) {
			processed.add(operator);
		}

		public MongoCriteriaOperator markProcessed(String operator, Object value) {

			markProcessed(operator);
			return new MongoCriteriaOperator(operator, value);
		}

	}

	/**
	 * Mongo operator abstraction holding both the operator (eg. $near) the the associated value.
	 * 
	 * @author Christoph Strobl
	 * @since 2.1
	 */
	class MongoCriteriaOperator {

		private final String operator;
		private final @Nullable Object value;

		public MongoCriteriaOperator(String operator, @Nullable Object value) {

			this.operator = operator;
			this.value = value;
		}

		public String getOperator() {
			return operator;
		}

		public Object getValue() {
			return value;
		}
	}

	static QueryContext defaultContext() {
		return new QueryContext() {};
	}
}
