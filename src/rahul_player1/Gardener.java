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
    public boolean turnBackIfRequired() throws GameActionException {
        if (getDistanceFromStartingPoint() > 80 || getRoundsElapsedFromStartingPoint() > 70) {
            startPoint = rc.getLocation();
            initialRoundNum = rc.getRoundNum();
            currentDirection = currentDirection.opposite();
            return true;
        }

        return false;
    }

    private Direction getClockwiseRightAngleTo(Direction currentDirection) {
        return currentDirection.rotateRightDegrees(90f);
    }

    public boolean plantTree() throws GameActionException {
        Direction plantDir = getClockwiseRightAngleTo(currentDirection);
        if (rc.plantTree(plantDir) && )

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
