package rahul_player1;

import battlecode.common.*;

/**
 * Created by rahubatr on 1/22/17.
 */
public strictfp class Gardener {

    private MapLocation initialStartPoint;
    private MapLocation startPoint;
    private int initialRoundNum;
    private Direction currentDirection;
    private RobotController rc;
    private boolean hasInitiallyMoved;

    Gardener(RobotController rc) {
        this.rc = rc;
        initialStartPoint = rc.getLocation();
        startPoint = rc.getLocation();
        initialRoundNum = rc.getRoundNum();
        currentDirection = common.randomDirection();
        hasInitiallyMoved = false;
    }

    private float getDistanceFromStartingPoint() throws GameActionException {
        MapLocation curLoc = rc.getLocation();
        return curLoc.distanceTo(startPoint);
    }

    private int getRoundsElapsedFromStartingPoint() throws GameActionException {
        return rc.getRoundNum() - initialRoundNum;
    }

    private void initialize() {
        initialStartPoint = rc.getLocation();
        startPoint = rc.getLocation();
        initialRoundNum = rc.getRoundNum();
    }

    private void turnBack() {
        startPoint = rc.getLocation();
        initialRoundNum = rc.getRoundNum();
        currentDirection = currentDirection.opposite();
    }

    // after planting trees on a patch, moves back to water trees after some x number of rounds
    public boolean turnBackIfRequired() throws GameActionException {
        if (getDistanceFromStartingPoint() > 10 * GameConstants.BULLET_TREE_RADIUS ||
                getRoundsElapsedFromStartingPoint() > 10) {
            turnBack();
            return true;
        }

        return false;
    }

    private Direction getClockwiseRightAngleTo(Direction currentDirection) {
        return currentDirection.rotateRightDegrees(90f);
    }

    public boolean plantTree() throws GameActionException {
        Direction plantDir = getClockwiseRightAngleTo(currentDirection);
        if (rc.canPlantTree(plantDir)) {
            rc.plantTree(plantDir);
            return true;
        }
        return false;
    }

    public boolean waterTree() throws GameActionException {
        TreeInfo[] treeInfos = rc.senseNearbyTrees();
        int max_tree_to_water = 3;

        int noOfTreesWatered = 0;
        for (TreeInfo tree : treeInfos) {
            if (rc.canWater(tree.getID())) {
                rc.water(tree.getID());
                noOfTreesWatered++;
                if (noOfTreesWatered >= max_tree_to_water) {
                    break;
                }
            }
        }

        return noOfTreesWatered > 0;
    }


    static void buildSoldier() throws GameActionException {

    }

    static void buildTank() throws GameActionException {

    }

    static void buildScout() throws GameActionException {

    }

    static void buildLumberJack() throws GameActionException {

    }

    public void orchestrate() throws GameActionException {

        // Keep moving in the current direction for some time before planting trees
        moveInitiallyIfRequired();

        turnBackIfRequired();
        if (!plantTree()) {
            waterTree();
        }
        tryMove(currentDirection);
    }

    private void moveInitiallyIfRequired() throws GameActionException {
        if (!hasInitiallyMoved && getRoundsElapsedFromStartingPoint() < 100) {
            tryMove(currentDirection);
        } else {
            initialize();
            hasInitiallyMoved = true;
        }
    }


    private boolean tryMove(Direction dir) {
        try {
            return common.tryMove(rc, dir);
        } catch (Exception e) {
            System.out.println("Exception " + e.getMessage());
            return false;
        }
    }
}
