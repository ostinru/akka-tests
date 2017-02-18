package com.exactprosystems.meetup;

import java.util.Arrays;
import java.util.List;
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
import akka.testkit.TestProbe;
import scala.collection.JavaConverters;
import scala.collection.Seq;
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
		ActorRef actor = system.actorOf(Props.create(MyActor.class), "pingpong");
		
		TestProbe probe = TestProbe.apply(system);
		JavaTestKit tk = new JavaTestKit(system);
		
		String response = null;
		
		// Expect Msg
		actor.tell("Hello!", probe.ref());
		response = probe.expectMsg(TIMEOUT, "Hello!");
		Assert.assertEquals("Hello!", response);
		
		// Expect Msg Any Of
		actor.tell("Hello!", probe.ref());
		
		List<String> expectedValues = Arrays.asList("Hello!", "World!");
		Seq<String> expectedValuesScala = JavaConverters.asScalaBufferConverter(expectedValues).asScala();
		
		response = probe.expectMsgAnyOf(TIMEOUT, expectedValuesScala);
		Seq<Object> resp = probe.receiveN(5);

		Assert.assertEquals("Hello!", response);
	}

}
