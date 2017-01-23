package SpartanPlayer;

import battlecode.common.*;

/**
 * Created by viru on 1/22/17.
 */
public class common {

    static int ARCHON_DIRECTION = 1;

    /**
     * Attempts to move in a given direction, while avoiding small obstacles directly in the path.
     *
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryMove(RobotController rc, Direction dir) throws GameActionException {
        return tryMove(rc, dir, 20, 3);
    }

    /**
     * Attempts to move in a given direction, while avoiding small obstacles direction in the path.
     *
     * @param dir           The intended direction of movement
     * @param degreeOffset  Spacing between checked directions (degrees)
     * @param checksPerSide Number of extra directions checked on each side, if intended direction was unavailable
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryMove(RobotController rc, Direction dir, float degreeOffset, int checksPerSide) throws GameActionException {

        // First, try intended direction
        if (!rc.hasMoved() && rc.canMove(dir)) {
            System.out.println("rc.canMove in tryMove... ");
            rc.move(dir);
            return true;
        }

        // Now try a bunch of similar angles
        boolean moved = false;
        int currentCheck = 1;

        while (currentCheck <= checksPerSide) {
            // Try the offset of the left side
            if (!rc.hasMoved() && rc.canMove(dir.rotateLeftDegrees(degreeOffset * currentCheck))) {
                rc.move(dir.rotateLeftDegrees(degreeOffset * currentCheck));
                return true;
            }
            // Try the offset on the right side
            if (!rc.hasMoved() && rc.canMove(dir.rotateRightDegrees(degreeOffset * currentCheck))) {
                rc.move(dir.rotateRightDegrees(degreeOffset * currentCheck));
                return true;
            }
            // No move performed, try slightly further
            currentCheck++;
        }

        // A move never happened, so return false.
        return false;
    }

    /**
     * Returns a random Direction
     * @return a random Direction
     */
    static Direction randomDirection() {
        return new Direction((float)Math.random() * 2 * (float)Math.PI);
    }

    /**
     * Attempts to move the robot continuously in a rectangle (anticlockwise direction)
     *
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryMoveInARectangle(RobotController rc) throws GameActionException {
        if (ARCHON_DIRECTION == 1) {
            if (canMoveNWSE(rc, Direction.getNorth(), Direction.getWest(), 2)) return true;
        } else if (ARCHON_DIRECTION == 2) {
            if (canMoveNWSE(rc, Direction.getWest(), Direction.getSouth(), 3)) return true;
        } else if (ARCHON_DIRECTION == 3) {
            if (canMoveNWSE(rc, Direction.getSouth(), Direction.getEast(), 4)) return true;
        } else {
            if (canMoveNWSE(rc, Direction.getEast(), Direction.getNorth(), 1)) return true;
        }
        return false;
    }

    /**
     * Helper method to move in NWSE method (anticlockwise direction)
     *
     * @throws GameActionException
     */

    static boolean canMoveNWSE(RobotController rc, Direction currDir, Direction newDir, int newDirCode) throws GameActionException {
        if (!rc.hasMoved() && rc.canMove(currDir)) {
            rc.move(currDir);
            return true;
        } else {
            if (!rc.hasMoved() && rc.canMove(newDir)) {
                rc.move(newDir);
                ARCHON_DIRECTION = newDirCode;
                return true;
            }
        }
        return false;
    }

     /**
     * Helper method to return Archon's current position by checking value of ARCHON_DIRECTION
     *
     * @throws GameActionException
     */

    static Direction getArchonDirection() throws GameActionException {
        if (ARCHON_DIRECTION == 1) return Direction.getNorth();
        else if (ARCHON_DIRECTION == 2) return Direction.getWest();
        else if (ARCHON_DIRECTION == 3) return Direction.getSouth();
        else return Direction.getEast();
    }
}