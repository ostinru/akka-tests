package com.exactprosystems.common;

import akka.actor.UntypedActor;

public class BlackHoleActor extends UntypedActor {

	@Override
	public void onReceive(Object msg) {
		// do nothing
	}
	
}