package farming1;
import battlecode.common.*;

public strictfp class RobotPlayer {
    private static final int ARCHON_X_CHANNEL = 0;
    private static final int ARCHON_Y_CHANNEL = 1;
    private static final int CURRENT_NUM_TREES_CHANNEL = 2;
    private static final int PLANTER1_CHANNEL = 3;
    private static final int TREE_PLANTER2_CHANNEL = 4;
    private static final int TREE_PLANTER3_CHANNEL = 5;
    private static final int TREE_PLANTER4_CHANNEL = 6;


    private static final int NUM_OF_TREEPLANTERS = 4;
    private static int currentNumTreePlanters;

    static RobotController rc;

    static int fieldSize = 0;
    static boolean isMapLengthKnown = false;
    static float distanceShifted = 0;
    static float mapSideLength = 0;
    static MapLocation initPosition;
    static boolean isInitArchPositionSaved = false;

    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
     **/
    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {

        // This is the RobotController object. You use it to perform actions from this robot,
        // and to get information on its current status.
        RobotPlayer.rc = rc;

        // Here, we've separated the controls into a different method for each RobotType.
        // You can add the missing ones or rewrite this into your own control structure.
        switch (rc.getType()) {
            case ARCHON:
                runArchon();
                break;
            case GARDENER:
                runGardener();
                break;
            case SOLDIER:
                runSoldier();
                break;
            case LUMBERJACK:
                runLumberjack();
                break;
        }
    }

    static void runArchon() throws GameActionException {
        System.out.println("I'm an archon!");

        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {

                // getInitialArchonPositions(whichTeam); // just prints enemy's archon locations

                // Broadcast archon's location for other robots on the team to know
                // put the next 3 lines in an if loop ? need to calculate location only once.

                if(!isInitArchPositionSaved) {
                    MapLocation myLocation = rc.getLocation();
                    System.out.println("myLocation: myLocation.x=" + myLocation.x + ", myLocation.y="+myLocation.y);
                    initPosition = myLocation;
                    isInitArchPositionSaved = true;
                }

                if (!isMapLengthKnown) {
                    // does not take care of objects in the way!
                    if (initPosition.y > initPosition.x)
                        calculateMapLength(Direction.getNorth());
                    else
                        calculateMapLength(Direction.getEast());
                }
                else {

                    // Generate a random direction
                    //MapLocation myLocation = rc.getLocation();

                    //rc.broadcast(0,(int)myLocation.x);
                    //rc.broadcast(1,(int)myLocation.y);

                    Direction dir = randomDirection();

                    // Randomly attempt to build a gardener in this direction
                    //if (Math.random() < .01 && rc.canHireGardener(dir)) {
                    if (rc.readBroadcast(CURRENT_NUM_TREES_CHANNEL) < NUM_OF_TREEPLANTERS && rc.canHireGardener(dir))
                        rc.hireGardener(dir);

                    // Move in the opposite direction of hiring a gardener.
                    tryMove(dir.opposite());

                }
                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();
            } catch (Exception e) {
                System.out.println("Archon Exception");
                e.printStackTrace();
            }
        }
    }

    static void runGardener() throws GameActionException {
        System.out.println("I'm a gardener!");

        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {

                // Listen for home archon's location
                // not using this for now
                /*
                int xPos = rc.readBroadcast(ARCHON_X_CHANNEL);
                int yPos = rc.readBroadcast(ARCHON_Y_CHANNEL);
                MapLocation archonLoc = new MapLocation(xPos,yPos);
                */
                System.out.println("rc.getID() = " + rc.getID());


                if (rc.readBroadcast(CURRENT_NUM_TREES_CHANNEL) < NUM_OF_TREEPLANTERS && rc.readBroadcast(PLANTER1_CHANNEL) == 0) {
                    System.out.println("CURRENT_NUM_TREES_CHANNEL...");
                    rc.broadcast(CURRENT_NUM_TREES_CHANNEL, currentNumTreePlanters++);
                    rc.broadcast(PLANTER1_CHANNEL, rc.getID());
                }

                if (rc.getID() == rc.readBroadcast(PLANTER1_CHANNEL)) {
                    System.out.println("PLANTER1_CHANNEL...");
                    Direction dir = rc.getLocation().directionTo(new MapLocation(1,1));
                    boolean moved = tryMove(dir);
                    if (!moved && rc.getLocation().equals(new MapLocation(1,1))) {
                        if (rc.canPlantTree(Direction.getEast())) { // Bytecode cost: 10
                            rc.plantTree(Direction.getEast());
                        }
                    }
                }

                else {
                    Direction dir2 = randomDirection();
                    // Randomly attempt to build a soldier or lumberjack in this direction
                    if (rc.canBuildRobot(RobotType.SOLDIER, dir2) && Math.random() < .01) {
                        rc.buildRobot(RobotType.SOLDIER, dir2);
                    } else if (rc.canBuildRobot(RobotType.LUMBERJACK, dir2) && Math.random() < .01 && rc.isBuildReady()) {
                        rc.buildRobot(RobotType.LUMBERJACK, dir2);
                    }

                    // Move randomly
                    tryMove(dir2);

                }
                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println("Gardener Exception");
                e.printStackTrace();
            }
        }
    }

    static void runSoldier() throws GameActionException {
        System.out.println("I'm an soldier!");
        Team enemy = rc.getTeam().opponent();

        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
                MapLocation myLocation = rc.getLocation();

                // See if there are any nearby enemy robots
                RobotInfo[] robots = rc.senseNearbyRobots(-1, enemy);

                // If there are some...
                if (robots.length > 0) {
                    // And we have enough bullets, and haven't attacked yet this turn...
                    if (rc.canFireSingleShot()) {
                        // ...Then fire a bullet in the direction of the enemy.
                        rc.fireSingleShot(rc.getLocation().directionTo(robots[0].location));
                    }
                }

                // Move randomly
                tryMove(randomDirection());

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println("Soldier Exception");
                e.printStackTrace();
            }
        }
    }

    static void runLumberjack() throws GameActionException {
        System.out.println("I'm a lumberjack!");
        Team enemy = rc.getTeam().opponent();

        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {

                // See if there are any enemy robots within striking range (distance 1 from lumberjack's radius)
                RobotInfo[] robots = rc.senseNearbyRobots(RobotType.LUMBERJACK.bodyRadius+GameConstants.LUMBERJACK_STRIKE_RADIUS, enemy);

                if(robots.length > 0 && !rc.hasAttacked()) {
                    // Use strike() to hit all nearby robots!
                    rc.strike();
                } else {
                    // No close robots, so search for robots within sight radius
                    robots = rc.senseNearbyRobots(-1,enemy);

                    // If there is a robot, move towards it
                    if(robots.length > 0) {
                        MapLocation myLocation = rc.getLocation();
                        MapLocation enemyLocation = robots[0].getLocation();
                        Direction toEnemy = myLocation.directionTo(enemyLocation);

                        tryMove(toEnemy);
                    } else {
                        // Move Randomly
                        tryMove(randomDirection());
                    }
                }

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println("Lumberjack Exception");
                e.printStackTrace();
            }
        }
    }

    /**
     * Returns a random Direction
     * @return a random Direction
     */
    static Direction randomDirection() {
        return new Direction((float)Math.random() * 2 * (float)Math.PI);
    }

    /**
     * Attempts to move in a given direction, while avoiding small obstacles directly in the path.
     *
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryMove(Direction dir) throws GameActionException {
        return tryMove(dir,20,3);
    }

    /**
     * Attempts to move in a given direction, while avoiding small obstacles direction in the path.
     *
     * @param dir The intended direction of movement
     * @param degreeOffset Spacing between checked directions (degrees)
     * @param checksPerSide Number of extra directions checked on each side, if intended direction was unavailable
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryMove(Direction dir, float degreeOffset, int checksPerSide) throws GameActionException {

        // First, try intended direction
        if (rc.canMove(dir)) {
            rc.move(dir);
            return true;
        }

        // Now try a bunch of similar angles
        boolean moved = false;
        int currentCheck = 1;

        while(currentCheck<=checksPerSide) {
            // Try the offset of the left side
            if(rc.canMove(dir.rotateLeftDegrees(degreeOffset*currentCheck))) {
                rc.move(dir.rotateLeftDegrees(degreeOffset*currentCheck));
                return true;
            }
            // Try the offset on the right side
            if(rc.canMove(dir.rotateRightDegrees(degreeOffset*currentCheck))) {
                rc.move(dir.rotateRightDegrees(degreeOffset*currentCheck));
                return true;
            }
            // No move performed, try slightly further
            currentCheck++;
        }

        // A move never happened, so return false.
        return false;
    }

    /**
     * A slightly more complicated example function, this returns true if the given bullet is on a collision
     * course with the current robot. Doesn't take into account objects between the bullet and this robot.
     *
     * @param bullet The bullet in question
     * @return True if the line of the bullet's path intersects with this robot's current position.
     */
    static boolean willCollideWithMe(BulletInfo bullet) {
        MapLocation myLocation = rc.getLocation();

        // Get relevant bullet information
        Direction propagationDirection = bullet.dir;
        MapLocation bulletLocation = bullet.location;

        // Calculate bullet relations to this robot
        Direction directionToRobot = bulletLocation.directionTo(myLocation);
        float distToRobot = bulletLocation.distanceTo(myLocation);
        float theta = propagationDirection.radiansBetween(directionToRobot);

        // If theta > 90 degrees, then the bullet is traveling away from us and we can break early
        if (Math.abs(theta) > Math.PI/2) {
            return false;
        }

        // distToRobot is our hypotenuse, theta is our angle, and we want to know this length of the opposite leg.
        // This is the distance of a line that goes from myLocation and intersects perpendicularly with propagationDirection.
        // This corresponds to the smallest radius circle centered at our location that would intersect with the
        // line that is the path of the bullet.
        float perpendicularDist = (float)Math.abs(distToRobot * Math.sin(theta)); // soh cah toa :)

        return (perpendicularDist <= rc.getType().bodyRadius);
    }

    /**
     * Method to calculate the map length.
     * Takes into account Archon's starting X coordinate & Y coordinate values to move shorter distance to the edge
     * @throws GameActionException
     */
    static void calculateMapLength(Direction dir) throws GameActionException {
        try {
            if (!rc.hasMoved() && rc.canMove(dir)) rc.move(dir);
            else {
                distanceShifted = rc.getLocation().distanceTo(initPosition);
                float initDistFromOrigin;

                if (dir.equals(Direction.getEast())) initDistFromOrigin = initPosition.x;
                else initDistFromOrigin = initPosition.y;

                mapSideLength = initDistFromOrigin + distanceShifted + RobotType.ARCHON.bodyRadius;
                isMapLengthKnown = true;
            }
            //Clock.yield();
        } catch (Exception e) {
            System.out.println("calculateMapLength Exception");
            e.printStackTrace();
        }

    }
}

