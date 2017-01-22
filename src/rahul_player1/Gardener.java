package rahul_player1;

import battlecode.common.*;

/**
 * Created by rahubatr on 1/22/17.
 */
public strictfp class Gardener {

    MapLocation initialStartPoint;
    MapLocation startPoint;
    int initialRoundNum;
    Direction currentDirection;
    RobotController rc;

    Gardener(RobotController rc) {
        this.rc = rc;
        initialStartPoint = rc.getLocation();
        startPoint = rc.getLocation();
        initialRoundNum = rc.getRoundNum();
        currentDirection = randomDirection();

    }

    private float getDistanceFromStartingPoint() throws GameActionException {
        MapLocation curLoc = rc.getLocation();
        return curLoc.distanceTo(startPoint);
    }

    private int getRoundsElapsedFromStartingPoint() throws GameActionException {
        return rc.getRoundNum() - initialRoundNum;
    }

    // after planting trees on a patch, moves back to water trees after some x number of rounds
    public boolean shouldTurnBack() throws GameActionException {
        if (getDistanceFromStartingPoint() > 80 || getRoundsElapsedFromStartingPoint() > 70) {
            startPoint = rc.getLocation();
            initialRoundNum = rc.getRoundNum();
            currentDirection = opposite(currentDirection);
            return true;
        }

        return false;
    }

    private Direction getCurrentDirection(int id) throws GameActionException {
        return currentDirection;
    }

    static void plantTree() throws GameActionException {
        /*
        which gardener plants which tree,
        store tree data
        */

    }

    static void waterTree() throws GameActionException {

    }


    static void buildSoldier() throws GameActionException {

    }

    static void buildTank() throws GameActionException {

    }

    static void buildScout() throws GameActionException {

    }

    static void buildLumberJack() throws GameActionException {

    }
}
