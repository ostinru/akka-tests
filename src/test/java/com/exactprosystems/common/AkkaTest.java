package com.exactprosystems.common;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.TestActorRef;
import scala.concurrent.duration.Duration;


public abstract class AkkaTest {

	private static final Duration TERMINATION_DURATION = Duration.create(250.0, TimeUnit.MILLISECONDS);

	private int testIndex;

	private ActorSystem system;

	private TestActorRef<DeadLetterActor> deadLetters;

	private TestActorRef<UnhandledActor> unhandledMessages;

	private TestActorRef<BlackHoleActor> blackHole;

	@Before
	public final void initAkkaSystem() {
		this.system = this.createActorSystem("test" + this.testIndex++);
		this.deadLetters = TestActorRef.create(
				this.system, Props.create(DeadLetterActor.class), "deadLetters");
		this.unhandledMessages = TestActorRef.create(
				this.system, Props.create(UnhandledActor.class), "unhandledMessages");
	}
	
	@After
	public final void shutdownAkkaSystem() {
		try {
			if (this.system != null) {
				checkUnhandled();
				this.system.terminate().value();
			}
		}
		finally {
			this.deadLetters = null;
			this.unhandledMessages = null;
			this.system = null;
		}
	}
	
	public ActorSystem getSystem() {
		return this.system;
	}
	
	public ActorRef getBlackHole() {
		if (this.blackHole == null) {
			this.blackHole = TestActorRef.create(this.system, Props.create(BlackHoleActor.class));
		}
		return this.blackHole;
	}
	
	protected ActorSystem createActorSystem(final String name) {
		return ActorSystem.create(name);
	}

	public void checkUnhandled() {
		Assert.assertEquals(0, this.unhandledMessages.underlyingActor().getUnhandled().size());
		Assert.assertEquals(0, this.deadLetters.underlyingActor().getDeadLetters().size());
	}
}
