package Defend;

import battlecode.common.*;

public strictfp class RobotPlayer {

    static RobotController rc;
    static int ARCHON_X_CHANNEL = 100;
    static int ARCHON_Y_CHANNEL = 101;
    static int GARDENER_NUMBER_CHANNEL = 102;
    static int PROTECTOR_NUMBER_CHANNEL = 103; // stores ID of Archon's protector
    static int NUM_OF_GARDENERS = 4;

    // will use these constants to assign a soldier to follow 2 gardeners for now
    // in case a gardener gets killed, will need to reassign the soldier
    // same if a soldier gets killed
    static int GARDENER1_ID_CHANNEL = 200;
    static int GARDENER1_X_CHANNEL = 201;
    static int GARDENER1_Y_CHANNEL = 202;
    static int GARDENER2_ID_CHANNEL = 203;
    static int GARDENER2_X_CHANNEL = 204;
    static int GARDENER2_Y_CHANNEL = 205;

    /*
    static int PROTECTOR_1_ID_CHANNEL = 102;
    static int PROTECTOR_2_ID_CHANNEL = 103;
    static int PROTECTOR_3_ID_CHANNEL = 104;
    static int PROTECTOR_4_ID_CHANNEL = 105;
    */
    //static int[] PROTECTOR_IDS = {PROTECTOR_1_ID_CHANNEL, PROTECTOR_2_ID_CHANNEL, PROTECTOR_3_ID_CHANNEL, PROTECTOR_4_ID_CHANNEL};


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

                // Broadcast archon's location for other robots on the team to know
                MapLocation myLocation = rc.getLocation();
                rc.broadcast(ARCHON_X_CHANNEL, (int)myLocation.x);
                rc.broadcast(ARCHON_Y_CHANNEL, (int)myLocation.y);

                if (rc.getBuildCooldownTurns() == 0 && rc.readBroadcast(GARDENER_NUMBER_CHANNEL) < NUM_OF_GARDENERS) {
                    Direction dir = randomDirection();
                    int tryCount = 0;
                    // try 5 times to constructing a gardener
                    while (tryCount < 5) {
                        if (rc.canHireGardener(dir)) {
                            int currGardeners = rc.readBroadcast(GARDENER_NUMBER_CHANNEL);
                            rc.broadcast(GARDENER_NUMBER_CHANNEL, currGardeners+1);
                            rc.hireGardener(dir);
                            break;
                        }
                        else dir = randomDirection();
                        tryCount++;
                    }
                }

                if (!rc.hasMoved()) {
                    wander();
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

                if (rc.getHealth() <= 20) {
                    int currGardeners = rc.readBroadcast(GARDENER_NUMBER_CHANNEL);
                    rc.broadcast(GARDENER_NUMBER_CHANNEL, currGardeners - 1);
                }

                // Listen for home archon's location
                int xPos = rc.readBroadcast(ARCHON_X_CHANNEL);
                int yPos = rc.readBroadcast(ARCHON_Y_CHANNEL);
                System.out.println("rc.getID() = " + rc.getID());
                Direction dir = randomDirection();

                int protectorID = rc.readBroadcast(PROTECTOR_NUMBER_CHANNEL);
                if (protectorID != 0 && !buildSoldierImmediately()) {
                    wander();
                    Clock.yield(); // review
                }

                if (rc.canBuildRobot(RobotType.SOLDIER, dir) && Math.random() < .5) {
                    rc.buildRobot(RobotType.SOLDIER, dir);
                } else if (rc.canBuildRobot(RobotType.LUMBERJACK, dir) && Math.random() < .1 && rc.isBuildReady()) {
                    rc.buildRobot(RobotType.LUMBERJACK, dir);
                }

                follow(xPos, yPos);
                if (!rc.hasMoved()) {
                    wander();
                }

                /*
                if (rc.canInteractWithTree(rc.getLocation()) && rc.canWater(rc.getLocation())) {
                    rc.water(rc.getLocation());
                }
                else if (Math.random() < .1 && rc.canPlantTree(dir)) {
                    // canPlantTree has Bytecode cost: 10
                    // plantTree has Bytecode cost: 0
                    rc.plantTree(dir);
                }
                else {
                    // Randomly attempt to build a soldier or lumberjack in this direction
                    if (rc.canBuildRobot(RobotType.SOLDIER, dir) && Math.random() < .5) {
                        rc.buildRobot(RobotType.SOLDIER, dir);
                    } else if (rc.canBuildRobot(RobotType.LUMBERJACK, dir) && Math.random() < .1 && rc.isBuildReady()) {
                        rc.buildRobot(RobotType.LUMBERJACK, dir);
                    }

                }
                */

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println("Gardener Exception");
                e.printStackTrace();
            }
        }
    }

    static boolean buildSoldierImmediately() throws GameActionException {
        int count = 0;
        while (count < 5) {
            Direction dir = randomDirection();
            if (rc.canBuildRobot(RobotType.SOLDIER, dir)) {
                rc.buildRobot(RobotType.SOLDIER, dir);
                return true;
            }
            count++;
        }
        return false;
    }

    static boolean anActiveProtector() throws GameActionException {
        int protectorID = rc.readBroadcast(PROTECTOR_NUMBER_CHANNEL);
        if (protectorID == 0) return false;
        return true;
    }

    static void runSoldier() throws GameActionException {
        System.out.println("I'm an soldier!");
        Team enemy = rc.getTeam().opponent();

        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {

                if (!anActiveProtector()) {
                    rc.broadcast(PROTECTOR_NUMBER_CHANNEL, rc.getID());
                }

                if (rc.getID() == rc.readBroadcast(PROTECTOR_NUMBER_CHANNEL)) {
                    int xPos = rc.readBroadcast(ARCHON_X_CHANNEL);
                    int yPos = rc.readBroadcast(ARCHON_Y_CHANNEL);
                    follow(xPos, yPos); // go towards Archon to protect it
                }


                // See if there are any nearby enemy robots
                RobotInfo[] robots = rc.senseNearbyRobots(-1, enemy);

                // If there are some...
                // added rc.hasAttacked()
                if (!rc.hasAttacked() && robots.length > 0) {
                    // And we have enough bullets, and haven't attacked yet this turn...
                    if (robots.length > 1 && rc.canFireTriadShot()) {
                        rc.fireTriadShot(rc.getLocation().directionTo(robots[1].location)); // review
                        Direction chase = rc.getLocation().directionTo(robots[1].location);
                        tryMove(chase);
                    }
                    else if (rc.canFireSingleShot()) {
                        // ...Then fire a bullet in the direction of the enemy.
                        rc.fireSingleShot(rc.getLocation().directionTo(robots[0].location));
                        Direction chase = rc.getLocation().directionTo(robots[0].location);
                        tryMove(chase);
                    }
                }

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println("Soldier Exception");
                e.printStackTrace();
            }
        }
    }

    // from lecture code.
    static void runLumberjack() throws GameActionException {
        while (true) {
            try {
                RobotInfo[] bots = rc.senseNearbyRobots();
                for (RobotInfo b : bots) {
                    if (b.getTeam() != rc.getTeam() && rc.canStrike()) {
                        rc.strike();
                        Direction chase = rc.getLocation().directionTo(b.getLocation());
                        tryMove(chase);
                        break;
                    }
                }
                TreeInfo[] trees = rc.senseNearbyTrees();
                for (TreeInfo t : trees) {
                    if (rc.canChop(t.getLocation())) {
                        rc.chop(t.getLocation());
                        break;
                    }
                }
                if (!rc.hasAttacked()) {
                    wander();
                }
                Clock.yield();
            } catch (Exception e) {
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
     * Try to follow another robot which is currently at (x, y) location on the map
     */
    static void follow(int target_x, int target_y) throws GameActionException {
        try {
            tryMove(rc.getLocation().directionTo(new MapLocation(target_x, target_y)));
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    static boolean trySidestep(BulletInfo bullet) throws GameActionException{

        Direction towards = bullet.getDir();
        //MapLocation leftGoal = rc.getLocation().add(towards.rotateLeftDegrees(90), rc.getType().bodyRadius);
        //MapLocation rightGoal = rc.getLocation().add(towards.rotateRightDegrees(90), rc.getType().bodyRadius);
        return(tryMove(towards.rotateRightDegrees(90)) || tryMove(towards.rotateLeftDegrees(90)));
    }


    public static void wander() throws GameActionException {
        Direction dir = randomDirection();
        tryMove(dir);
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

}

