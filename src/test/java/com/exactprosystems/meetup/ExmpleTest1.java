package com.exactprosystems.meetup;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.testkit.TestActorRef;

public class ExmpleTest1 {

	private static class MyActor extends UntypedActor {
		public void onReceive(Object msg) throws Exception {
			throw new RuntimeException("Ooops");
		}

		public boolean testMe() {
			return true;
		}
	}

	private ActorSystem system;
	
	@Before
	public void init() {
		system = ActorSystem.create();
	}

	@After
	public void destroy() {
		system.terminate().value();
		system = null;
	}

	@Test
	public void testUnderlyingActor() {
		TestActorRef<MyActor> ref = TestActorRef.create(system, Props.create(MyActor.class), "testA");

		MyActor actor = (MyActor) ref.underlyingActor();

		Assert.assertTrue(actor.testMe());
	}

	@Test
	public void testReceive() {
		TestActorRef<MyActor> ref = TestActorRef.create(system, Props.create(MyActor.class), "testA");

		try {
			ref.receive(new String("hello, actor!"));
			Assert.fail("expected an exception to be thrown");
		} catch (Exception e) {
			// Ok
		}
	}

}
