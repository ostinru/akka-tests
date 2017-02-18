package com.exactprosystems.meetup;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.pattern.Patterns;
import akka.testkit.TestActorRef;
import akka.util.Timeout;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

public class ExampleTest2 {

	private static final Timeout TIMEOUT = new Timeout(Duration.apply(1, TimeUnit.SECONDS));
	
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
	public void testResponse() throws Exception {
		TestActorRef<MyActor> ref = TestActorRef.create(system, Props.create(MyActor.class), "testA");

		String msg = "ping";
		
		Future<Object> future =  Patterns.ask(ref, msg, TIMEOUT);
		Object responce = Await.result(future, TIMEOUT.duration());
		
		Assert.assertEquals(msg, responce);
	}
}
