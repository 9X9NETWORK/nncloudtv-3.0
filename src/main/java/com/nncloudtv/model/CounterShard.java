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

package com.nncloudtv.model;

import javax.jdo.annotations.Column;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

/**
 * One shard belonging to the named counter.
 *
 * An individual shard is written to infrequently to allow the counter in
 * aggregate to be incremented rapidly.
 *
 */
@PersistenceCapable(table="counter_shard", identityType = IdentityType.APPLICATION)
public class CounterShard {
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Long id;
  
    @Persistent
    private Integer shardNumber;
  
    @Persistent
    @Column(jdbcType="VARCHAR", length=255)
    private String counterName;
  
    @Persistent
    private Long count;
  
    public CounterShard(String counterName, int shardNumber) {
      this(counterName, shardNumber, 0);
    }
  
    public CounterShard(String counterName, int shardNumber, int count) {
      this.counterName = counterName;
      this.shardNumber = new Integer(shardNumber);
      this.count = new Long(count);
    }
  
    public Long getId() {
      return id;
    }
  
    public String getCounterName() {
      return counterName;
    }
  
    public Integer getShardNumber() {
      return shardNumber;
    }
  
    public Long getCount() {
      return count;
    }
  
    public void setCount(Long count) {
      this.count = count;
    }
  
    public void increment(int amount) {
      count = new Long(count.intValue() + amount);
    }
}