package com.codelanx.aether.common.event;

/**
 * Stores relevant information for bot listeners
 */
public class RegisteredListener {
    private final Listener listener;
    private final EventPriority priority;
    private final EventExecutor executor;
    private final boolean ignoreCancelled;
    private final String[] world;

    @Deprecated
    public RegisteredListener(final Listener listener, final EventExecutor executor, final EventPriority priority, final boolean ignoreCancelled) {
        this(listener, executor, priority, ignoreCancelled, new String[]{});
    }

    public RegisteredListener(final Listener listener, final EventExecutor executor, final EventPriority priority, final boolean ignoreCancelled, String[] world) {
        this.listener = listener;
        this.priority = priority;
        this.executor = executor;
        this.ignoreCancelled = ignoreCancelled;
        this.world = world;
    }

    /**
     * Gets the listener for this registration
     *
     * @return Registered Listener
     */
    public Listener getListener() {
        return listener;
    }

    /**
     * Gets the priority for this registration
     *
     * @return Registered Priority
     */
    public EventPriority getPriority() {
        return priority;
    }

    /**
     * Calls the event executor
     *
     * @param event The event
     * @throws EventException If an event handler throws an exception.
     */
    public void callEvent(final Event event) throws EventException {
        if (event instanceof Cancellable) {
            if (((Cancellable) event).isCancelled() && isIgnoringCancelled()) {
                return;
            }
        }
        executor.execute(listener, event);
    }

    public String getWorld() {
        if (world == null || world.length == 0) {
            return null;
        } else {
            return world[0];
        }
    }

    /**
     * Whether this listener accepts cancelled events
     *
     * @return True when ignoring cancelled events
     */
    public boolean isIgnoringCancelled() {
        return ignoreCancelled;
    }

    @Override
    public String toString() {
        return "RegisteredListener{" +
                "listener=" + listener +
                "listener-class=" + (listener != null ? listener.getClass() : "null") +
                ", priority=" + priority +
                ", executor=" + executor +
                ", ignoreCancelled=" + ignoreCancelled +
                ", world=" + getWorld() == null ? "null" : getWorld() +
                '}';
    }
}