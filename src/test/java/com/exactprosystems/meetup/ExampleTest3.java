package com.exactprosystems.meetup;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.testkit.JavaTestKit;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

public class ExampleTest3 {

	private static final FiniteDuration TIMEOUT = Duration.apply(1, TimeUnit.SECONDS);

	private static class MyActor extends UntypedActor {
		public void onReceive(Object msg) throws Exception {
			getSender().tell(msg, getSelf());
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
	public void testPingPong() {
		JavaTestKit tk = new JavaTestKit(this.system);

		// Create Actor
		ActorRef actor = system.actorOf(Props.create(MyActor.class), "pingpong");

		// 1. expectMsgEquals()
		actor.tell("Hello!", tk.getRef());

		String strResponse = tk.expectMsgEquals(TIMEOUT, "Hello!");

		Assert.assertEquals("Hello!", strResponse);

		// 2. expectMsgAnyOf()
		actor.tell("Hello!", tk.getRef());

		Object response = tk.expectMsgAnyOf(TIMEOUT, "Hello!", "World!");

		Assert.assertEquals("Hello!", response);

		// 3. expectMsgAllOf()
		actor.tell("Hello!", tk.getRef());
		actor.tell("World!", tk.getRef());

		Object[] responses = tk.expectMsgAllOf(TIMEOUT, "Hello!", "World!");

		Assert.assertEquals("Hello!", responses[0]);
		Assert.assertEquals("World!", responses[1]);

		// 4. expectMsgEquals()
		actor.tell("Hello!", tk.getRef());

		strResponse = tk.expectMsgClass(TIMEOUT, String.class);

		Assert.assertEquals("Hello!", strResponse);

		// 5. Expect No Msg
		tk.expectNoMsg(TIMEOUT);

		// Expect No Msg
		actor.tell("Hello!", tk.getRef());
		actor.tell("World!", tk.getRef());

		responses = tk.receiveN(2);
	}

}
