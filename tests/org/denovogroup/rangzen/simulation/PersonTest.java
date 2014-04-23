/*
 * Copyright (c) 2014, De Novo Group
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.denovogroup.rangzen.simulation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Set;

/**
 * Set of meaningless tests that demonstrate how to write JUnit tests.
 */
@RunWith(JUnit4.class)
public class PersonTest {
  MessagePropagationSimulation sim;
  Person person;

  @Before
  public void setUp() {
    sim = new MessagePropagationSimulation(System.currentTimeMillis());
    sim.start();
    person = new Person(0, sim);
  }
  /**
   * This test always passes, because it has no asserts to fail.
   */
  @Test
  public void thisAlwaysPasses() {

  }

  /**
   * Ensure that get friends returns all the friends in the graph.
   */
  @Test
  public void testGetFriends() {
    Person p1 = new Person(1, sim);
    Person p2 = new Person(2, sim);
    Person p3 = new Person(3, sim);

    sim.socialNetwork.addEdge(person, p1, new Double(1.0));
    sim.socialNetwork.addEdge(person, p2, new Double(1.0));

    Set<Person> friends = person.getFriends(); 

    assertTrue("Added friend not in set returned by getFriends()", friends.contains(p1));
    assertTrue("Added friend not in set returned by getFriends()", friends.contains(p2));
    assertFalse("Non-added friend in set returned by getFriends()", friends.contains(p3));

  }

  @Test
  public void testFriendIntersection() {
  }

  /**
   * This test shouldn't show up as a pass or a fail in results, since it is
   * ignored.
   */
  @Test
  @Ignore
  public void thisIsIgnored() {
  }

}
