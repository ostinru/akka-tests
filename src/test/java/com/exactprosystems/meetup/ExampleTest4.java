package com.exactprosystems.meetup;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.exactprosystems.common.DeadLetterActor;
import com.exactprosystems.common.UnhandledActor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.testkit.TestActorRef;

public class ExampleTest4 {

	private static class MyActor extends UntypedActor {
		public void onReceive(Object msg) throws Exception {
			if (msg instanceof String) {
				getSender().tell(msg, getSelf());
			} else {
				unhandled(msg);
			}
		}
	}

	private ActorSystem system;
	private TestActorRef<DeadLetterActor> deadLetters;
	private TestActorRef<UnhandledActor> unhandled;

	@Before
	public void init() {
		system = ActorSystem.create();
		deadLetters = TestActorRef.create(system, Props.create(DeadLetterActor.class), "deadLetters");
		unhandled = TestActorRef.create(system, Props.create(UnhandledActor.class), "unhandledMessages");
	}

	@After
	public void destroy() {
		system.terminate().value();
		system = null;
		deadLetters = null;
		unhandled = null;
	}
	
	@Test
	public void testDeadLetters() {
		// Use CallingThreadDispatcher 
		ActorRef actor = TestActorRef.create(system, Props.create(MyActor.class), "pingpong");

		actor.tell("Hello!", ActorRef.noSender());
		
		Assert.assertEquals(0, unhandled.underlyingActor().getUnhandled().size());
		Assert.assertEquals(1, deadLetters.underlyingActor().getDeadLetters().size());
		
		Assert.assertEquals("Check DeadLetter message",
				"Hello!",
				deadLetters.underlyingActor().getDeadLetters().get(0).message());
	}
	
	@Test
	public void testUnhandled() {
		// Use CallingThreadDispatcher 
		ActorRef actor = TestActorRef.create(system, Props.create(MyActor.class), "pingpong");

		Object msg = new Object();
		actor.tell(msg, ActorRef.noSender());
		
		Assert.assertEquals(1, unhandled.underlyingActor().getUnhandled().size());
		Assert.assertEquals(0, deadLetters.underlyingActor().getDeadLetters().size());
		
		Assert.assertEquals("Check DeadLetter message",
				msg,
				unhandled.underlyingActor().getUnhandled().get(0).message());
	}
}
