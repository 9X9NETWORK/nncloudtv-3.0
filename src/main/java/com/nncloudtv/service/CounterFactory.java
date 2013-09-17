/* Copyright (c) 2009 Google Inc.
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

package com.nncloudtv.service;

import java.util.logging.Logger;

import com.nncloudtv.dao.ShardedCounter;
import com.nncloudtv.model.Counter;

/**
 * Finds or creates a sharded counter with the desired name.
 *
 */
public class CounterFactory {
  protected static final Logger log = Logger.getLogger(CounterFactory.class.getName());
  
  public ShardedCounter getOrCreateCounter(String name) {
    CounterFactory factory = new CounterFactory();
    ShardedCounter counter = factory.getCounter(name);
    if (counter == null) {
      // Create a counter with 0 shards.
      counter = factory.createCounter(name);
      // Add a first shard to the counter.
      counter.addShard();
      log.info("No counter named " + name + ", so we created one");
    }
    return counter;
  }

  public long getCount(String name) {      
      ShardedCounter counter = this.getOrCreateCounter(name);
      return counter.getCount();
  }
  
  public ShardedCounter getCounter(String name) {
    ShardedCounter counter = new ShardedCounter(name);
    if (counter.isInDatastore()) {
      return counter;
    } else {
      return null;
    }
  }

  public ShardedCounter createCounter(String name) {
    ShardedCounter counter = new ShardedCounter(name);
    Counter counterEntity = new Counter(name, 0);
    counter.create(counterEntity);
    return counter;
  }
}