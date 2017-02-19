package com.exactprosystems.meetup;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.testkit.TestProbe;

public class ExampleTest5 {

	private static class MyActor extends UntypedActor {
		public void onReceive(Object msg) throws Exception {
			getSelf().tell(PoisonPill.getInstance(), getSelf());
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
	public void testWatch() {
		
		ActorRef actor = system.actorOf(Props.create(MyActor.class), "LiveFastDieYoung");

		TestProbe probe = new TestProbe(system);

		probe.watch(actor);

		actor.tell(new Object(), probe.ref());

		probe.expectMsgClass(Terminated.class);
	}
}
