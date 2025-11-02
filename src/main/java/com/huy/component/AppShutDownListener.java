package com.huy.component;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

@Component
public class AppShutDownListener implements ApplicationListener<ContextClosedEvent> {
	public static volatile boolean isServerShuttingDown = false;
	@Override
	public void onApplicationEvent(ContextClosedEvent event) {
		isServerShuttingDown = true;
	}
}
