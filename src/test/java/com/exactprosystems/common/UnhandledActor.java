package com.exactprosystems.common;

import java.util.LinkedList;
import java.util.List;

import akka.actor.UnhandledMessage;
import akka.actor.UntypedActor;

public class UnhandledActor extends UntypedActor {

	private final List<UnhandledMessage> unhandled = new LinkedList<>();

	@Override
	public void preStart() throws Exception {
		getContext().system().eventStream().subscribe(getSelf(), UnhandledMessage.class);
	}

	@Override
	public void postStop() throws Exception {
		getContext().system().eventStream().unsubscribe(getSelf(), UnhandledMessage.class);
	}

	@Override
	public void onReceive(Object message) {
		if (message instanceof UnhandledMessage) {
			unhandled.add((UnhandledMessage) message);
		}
	}

	public List<UnhandledMessage> getUnhandled() {
		return unhandled;
	}
	
	public void clear() {
		unhandled.clear();
	}
}
