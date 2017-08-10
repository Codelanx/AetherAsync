package com.codelanx.aether.construction.mission.branch.setup;

import com.codelanx.aether.construction.Interactables;
import com.runemate.game.api.hybrid.entities.GameObject;
import com.runemate.game.api.hybrid.local.House;
import com.runemate.game.api.hybrid.location.navigation.cognizant.RegionPath;
import com.runemate.game.api.hybrid.queries.results.LocatableEntityQueryResults;
import com.runemate.game.api.hybrid.region.Players;
import com.runemate.game.api.hybrid.util.calculations.Distance;
import com.runemate.game.api.hybrid.util.calculations.Distance.Algorithm;
import com.runemate.game.api.script.framework.tree.BranchTask;
import com.runemate.game.api.script.framework.tree.LeafTask;
import com.runemate.game.api.script.framework.tree.TreeTask;

public class GetToHouseTask extends BranchTask {

    private final TreeTask success;
    private final TreeTask failure;

    public GetToHouseTask(TreeTask success) {
        this.success = new CheckBuildModeTask(success);
        this.failure = new LeafTask() {
            @Override
            public void execute() {
                { //portal check
                    LocatableEntityQueryResults<GameObject> res = Interactables.PORTAL.queryGameObjects();
                    if (!res.isEmpty()) {
                        GameObject obj = res.first();
                        double dist = Distance.between(Players.getLocal(), obj, Algorithm.EUCLIDEAN_SQUARED);
                        if (dist > 5) {
                            RegionPath path = RegionPath.buildTo(obj);
                            path.step(); //rebuilding every time yolo
                        } else {
                            obj.interact("Build mode");
                        }
                        //no portal
                    }
                }
                { //TODO: teleportation
                }
                { //TODO: bank check
                }
                //TODO: Webpath
            }
        };
    }

    @Override
    public boolean validate() {
        return House.isInside();
    }

    @Override
    public TreeTask failureTask() {
        return this.failure;
    }

    @Override
    public TreeTask successTask() {
        return this.success;
    }
}
