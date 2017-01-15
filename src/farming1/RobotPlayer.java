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

                    // Randomly attempt to build a gardener in this direction
                    //if (Math.random() < .1 && rc.canHireGardener(randomDirection())) {
                    dodge();
                    //dodgeEnemies();
                    Direction dir = randomDirection();
                    if (rc.getBuildCooldownTurns() == 0 && rc.canHireGardener(dir)) {
                        rc.hireGardener(dir);
                    }
                    System.out.println("Arch D0: getroundnum : " + rc.getRoundNum());
                    System.out.println("Arch D1: getroundnum : " + rc.getRoundNum());
                    if (!rc.hasMoved()) wander();
                    System.out.println("Arch D2: getroundnum : " + rc.getRoundNum());
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

                dodge(); // dodge bullets
                dodgeEnemies(); // dodge Enemies
                // Listen for home archon's location
                int xPos = rc.readBroadcast(ARCHON_X_CHANNEL);
                int yPos = rc.readBroadcast(ARCHON_Y_CHANNEL);
                MapLocation archonLoc = new MapLocation(xPos,yPos);
                System.out.println("rc.getID() = " + rc.getID());

                Direction dir = randomDirection();

                if (Math.random() < .1 && rc.canInteractWithTree(rc.getLocation()) && rc.canWater(rc.getLocation())) {
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

                if (!rc.hasMoved()) wander();
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
                dodge();
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

    static void dodge() throws GameActionException {
        BulletInfo[] bullets = rc.senseNearbyBullets();
        for (BulletInfo bi : bullets) {
            if (willCollideWithMe(bi)) {
                if (trySidestep(bi)) return;
            }
        }
        wander();
    }

    // currently used by a gardener only
    static boolean dodgeEnemies() throws GameActionException {
        RobotInfo[] bots = rc.senseNearbyRobots();
        for (RobotInfo b : bots) {
            if (b.getTeam() != rc.getTeam()) {
                Direction towards = rc.getLocation().directionTo(b.getLocation());
                return tryMove(towards.rotateRightDegrees(90)) || tryMove(towards.rotateLeftDegrees(90));
            }
        }
        return false;
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

