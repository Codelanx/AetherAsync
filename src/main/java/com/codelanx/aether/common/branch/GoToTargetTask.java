package com.codelanx.aether.common.branch;

import com.codelanx.aether.common.bot.Aether;
import com.codelanx.aether.common.bot.Invalidator;
import com.codelanx.aether.common.bot.Invalidators;
import com.codelanx.aether.common.bot.task.AetherTask;
import com.codelanx.aether.common.cache.Caches;
import com.codelanx.aether.common.cache.GameCache;
import com.codelanx.aether.common.cache.Queryable;
import com.codelanx.aether.common.cache.query.Inquiry;
import com.codelanx.aether.common.cache.query.LocatableInquiry;
import com.codelanx.aether.common.input.UserInput;
import com.codelanx.aether.common.json.locatable.Findable;
import com.codelanx.aether.common.json.locatable.NpcRef;
import com.codelanx.aether.common.json.locatable.SerializableGameObject;
import com.codelanx.aether.common.json.locatable.SerializableLocatable;
import com.codelanx.commons.util.Reflections;
import com.codelanx.commons.util.ref.Box;
import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.entities.LocatableEntity;
import com.runemate.game.api.hybrid.entities.Npc;
import com.runemate.game.api.hybrid.entities.details.Locatable;
import com.runemate.game.api.hybrid.local.Camera;
import com.runemate.game.api.hybrid.location.Coordinate;
import com.runemate.game.api.hybrid.location.navigation.Path;
import com.runemate.game.api.hybrid.location.navigation.Traversal;
import com.runemate.game.api.hybrid.location.navigation.cognizant.RegionPath;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.calculations.Distance;
import com.runemate.game.api.hybrid.util.calculations.Distance.Algorithm;
import static com.runemate.game.api.hybrid.entities.GameObject.Direction.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GoToTargetTask<T extends LocatableEntity, I extends Inquiry> extends AetherTask<Double> {

    private static final int INTERACTION_DISTANCE = 3;
    private static final int INTERACTION_DISTANCE_SQ = INTERACTION_DISTANCE * INTERACTION_DISTANCE;
    private final Supplier<T> target;
    private T lastTarget;

    public GoToTargetTask(Supplier<T> target, AetherTask<?> interaction) {
        this.target = target;
        this.registerRunemateCall(Double.NEGATIVE_INFINITY, () -> Camera.turnTo(this.target.get()));
        this.register(d -> Math.abs(d) < INTERACTION_DISTANCE_SQ, interaction);
        this.registerDefault(new MoveToTargetTask(target));
    }

    public GoToTargetTask(Supplier<T> target, Consumer<T> action) {
        this.target = target;
        this.registerRunemateCall(Double.NEGATIVE_INFINITY, () -> Camera.turnTo(this.target.get()));
        this.register(d -> Math.abs(d) < INTERACTION_DISTANCE_SQ, () -> action.accept(this.lastTarget));
        this.registerDefault(new MoveToTargetTask(target));
    }

    public GoToTargetTask(Queryable<T, I> target, Consumer<T> action) {
        this(() -> target.queryGlobal().findFirst().orElse(null), action);
    }

    public GoToTargetTask(Queryable<T, I> target, AetherTask<?> child) {
        this(() -> target.queryGlobal().findFirst().orElse(null), child);
    }

    @Override
    protected void onInvalidate() {
        this.lastTarget = null;
    }

    @Override
    public Supplier<Double> getStateNow() {
        return () -> {
            T obj = this.target.get();
            this.lastTarget = obj;
            if (obj != null) {
                double dist = Distance.between(obj, Players.getLocal(), Algorithm.EUCLIDEAN_SQUARED);
                if (!obj.isVisible() && dist < INTERACTION_DISTANCE_SQ) {
                    return Double.NEGATIVE_INFINITY;
                }
                return dist;
            } else {
                Environment.getLogger().severe("null target");
            }
            return Double.POSITIVE_INFINITY;
        };
    }

    @Override
    public boolean isExecutable() {
        return super.isExecutable();
    }

    @Override
    public Invalidator execute(Double state) {
        return super.execute(state);
    }

    private class MoveToTargetTask extends AetherTask<PathWrapper> {

        private final Supplier<? extends LocatableEntity> target;
        private LocatableEntity cached;
        private Coordinate cachedCoordinate;
        private AtomicLong lastClickMs = new AtomicLong();
        private final Box<Locatable> lastTarget = new Box<>();
        private final ReadWriteLock targetLock = new ReentrantReadWriteLock();

        public MoveToTargetTask(Supplier<? extends LocatableEntity> target) {
            this.target = target;
            this.register(Objects::isNull, AetherTask.of(() -> {}));//Environment.getLogger().warn("Null path for target")));
            this.registerDefault(p -> this.execute(p));
        }

        @Override
        public Supplier<PathWrapper> getStateNow() {
            return () -> {
                if (this.lastClickMs.get() > System.currentTimeMillis() - UserInput.getMinimumClick()) {
                    return new PathWrapper(Invalidators.SELF);
                }
                if (this.cached == null || !this.cached.isValid()) {
                    this.cached = this.target.get();
                    this.cachedCoordinate = null;
                }
                if (this.cached == null) {
                    throw new IllegalStateException("No retrievable or cached locatable objects found");
                }
                Locatable locatable = this.cached;
                if (locatable != null) {
                    Coordinate origin = this.cachedCoordinate == null ? this.cached.getPosition() : this.cachedCoordinate;
                    Locatable last = Reflections.operateLock(this.targetLock.readLock(), () -> this.lastTarget.value);
                    if (last != null && Distance.between(origin, last, Algorithm.EUCLIDEAN_SQUARED) <= INTERACTION_DISTANCE_SQ) {
                        return new PathWrapper(Invalidators.SELF);
                    }
                    double dist = Distance.between(this.cached, Players.getLocal(), Distance.Algorithm.EUCLIDEAN_SQUARED);
                    if (dist > INTERACTION_DISTANCE_SQ) {
                        //TODO: Cache
                        Path path = this.createPath(origin);
                        if (path == null && this.cachedCoordinate == null) {
                            path = this.getAdjacencyPath(origin);
                            if (path == null) {
                                Environment.getLogger().severe("No possible pathing found, shutting down bot...");
                                Aether.getBot().stop();
                                return new PathWrapper(Invalidators.SELF);
                            }
                        } else if (path == null) {
                            return new PathWrapper(Invalidators.SELF);
                        }
                    /*Locatable next = path.getNext();
                    dist = Distance.between(this.cached, next);
                    if (dist > INTERACTION_DISTANCE_SQ) {
                        //continue to path
                        path.step();
                        return false;
                    } else if (Players.getLocal().getAnimationId() != -1) { //is running
                        path.step();
                    }*/
                        return new PathWrapper(path);
                    }
                }
                return null;
            };
        }
        

        @Override
        public Invalidator execute(PathWrapper path) {
            if (this.lastClickMs.get() > System.currentTimeMillis() - UserInput.getMinimumClick()) {
                return Invalidators.NONE;
            }
            if (path.getPath() == null) {
                return path.getInvalidationSuggestion();
            }
            this.lastClickMs.set(System.currentTimeMillis());
            Locatable loc = path.getPath().getNext();
            Reflections.operateLock(this.targetLock.writeLock(), () -> this.lastTarget.value = loc);
            UserInput.runemateInput(path.getPath()::step);
            return Invalidators.SELF;
            /*return path.getVertices().size() - path.getVertices().indexOf(loc) < INTERACTION_DISTANCE
                    ? Invalidators.ALL
                    : Invalidators.SELF;*/
        }

        @Override
        public boolean isExecutable() {
            return this.lastClickMs.get() < System.currentTimeMillis() - UserInput.getMinimumClick() || super.isExecutable();
        }

        @Override
        public boolean isExecutable(PathWrapper state) {
            return state != null;
        }

        private Path getAdjacencyPath(Locatable loc) {
            GameObject.Direction dir; //init direction
            if (loc instanceof GameObject) {
                dir = ((GameObject) loc).getDirection();
            } else {
                dir = GameObject.Direction.SOUTH;
            }
            Environment.getLogger().info("Getting adjacency path (preferred: " + dir.name() + ")");
            List<GameObject.Direction> list = new LinkedList<>(Arrays.asList(SOUTH, WEST, EAST, NORTH));
            list.remove(dir);
            list.add(0, dir);
            //hmm, parallel stream?
            return list.stream().map(d -> {
                Coordinate c = this.apply(loc.getPosition(), d);
                Path p = RegionPath.buildTo(c);
                if (p == null) {
                    p = Traversal.getDefaultWeb().getPathBuilder().buildTo(c);
                }
                if (p != null) {
                    this.cachedCoordinate = c;
                }
                return p;
            }).filter(Objects::nonNull).findAny().orElseThrow(() -> new IllegalStateException("No path found to target"));
        }
        
        private Path createPath(Locatable loc) {
            Environment.getLogger().info("Pathing to: " + loc.getPosition());
            Path path = RegionPath.buildTo(loc);
            if (path == null) {
                path = Traversal.getDefaultWeb().getPathBuilder().buildTo(loc);
                if (path == null) {
                    Environment.getLogger().warn("Null path returned");
                }
            }
            return path;
        }
        
        private Coordinate apply(Coordinate origin, GameObject.Direction dir) {
            switch (dir) {
                case NORTH:
                    return new Coordinate(origin.getX(), origin.getY() + 1, origin.getPlane());
                case SOUTH:
                    return new Coordinate(origin.getX(), origin.getY() - 1, origin.getPlane());
                case EAST:
                    return new Coordinate(origin.getX(), origin.getY(), origin.getPlane() + 1);
                case WEST:
                    return new Coordinate(origin.getX(), origin.getY(), origin.getPlane() - 1);
            }
            return null;
        }
    }
    
    private static class PathWrapper {
        
        private final Path path;
        private final Invalidator suggestion;
        
        public PathWrapper(Path path) {
            this(path, null);
        }
        
        public PathWrapper(Invalidator suggestion) {
            this(null, suggestion);
        }
        
        public PathWrapper(Path path, Invalidator suggestion) {
            this.path = path;
            this.suggestion = suggestion;
        }
        
        public Path getPath() {
            return this.path;
        }
        
        public Invalidator getInvalidationSuggestion() {
            return this.suggestion;
        }
    }
}
