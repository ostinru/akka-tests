package com.exactprosystems.common;

import java.util.LinkedList;
import java.util.List;

import akka.actor.DeadLetter;
import akka.actor.UntypedActor;

public class DeadLetterActor extends UntypedActor {

	private final List<DeadLetter> dead = new LinkedList<>();

	@Override
	public void preStart() throws Exception {
		getContext().system().eventStream().subscribe(getSelf(), DeadLetter.class);
	}

	@Override
	public void postStop() throws Exception {
		getContext().system().eventStream().unsubscribe(getSelf(), DeadLetter.class);
	}

	@Override
	public void onReceive(Object message) {
		if (message instanceof DeadLetter) {
			dead.add((DeadLetter) message);
		}
	}

	public List<DeadLetter> getDeadLetters() {
		return dead;
	}
	
	public void clear() {
		dead.clear();
	}
}
