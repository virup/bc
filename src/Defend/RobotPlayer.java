package Defend;

import battlecode.common.*;

public strictfp class RobotPlayer {

    static RobotController rc;
    static int ARCHON1_ID_CHANNEL = 99; // id of the Archon
    static int ARCHON_X_CHANNEL = 100; // x coordinate of Archon .. what if there is more than 1 Archon
    static int ARCHON_Y_CHANNEL = 101; // x coordinate of Archon .. what if there is more than 1 Archon
    static int GARDENER_NUMBER_CHANNEL = 102; // number of gardeners that are alive
    static int ARCHON_GUARD_CHANNEL = 103; // stores ID of Archon's protector
    static int MAIN_GARDENER_CHANNEL = 104; // stores ID of the designated main gardener to be guarded
    static int GARDENER_GUARD_CHANNEL = 105; // stores ID of Gardener's protector
    static int GARDENER_X_CHANNEL = 106;
    static int GARDENER_Y_CHANNEL = 107;
    static int NUM_OF_GARDENERS = 2; // Maximum number of gardeners that are alive


    // archon_id, archon_x, archon_y, current_num_gard, main_gardnr_id, gardnr_x, gardnr_y, arch_guard, gardnr_guard
    static int[][] ARCHON_CONSTANTS = { {0, 0, 0, 0, 0, 0, 0, 0, 0},
                                        {0, 0, 0, 0, 0, 0, 0, 0, 0},
                                        {0, 0, 0, 0, 0, 0, 0, 0, 0}
                                      };

    static void setArchonID() throws GameActionException {
        System.out.println("initArchon..." + rc.getRoundNum());
        if (rc.readBroadcast(0) == 0) {
            rc.broadcast(0, rc.getID());
        }
        else if (rc.readBroadcast(10) == 0) {
            rc.broadcast(10, rc.getID());
        }
        else if (rc.readBroadcast(20) == 0) {
            rc.broadcast(20, rc.getID());
        }
    }

    static int checkArchonIndex(int myID) throws GameActionException {
        if (rc.readBroadcast(0) == myID) {
            return 0;
        }
        else if (rc.readBroadcast(10) == myID) {
            return 10;
        }
        else if (rc.readBroadcast(20) == myID) {
            return 20;
        }
        return -1;
    }

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

        setArchonID();

        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {

                // Broadcast archon's location for other robots on the team to know

                int myID = rc.getID();
                int archonIndex = checkArchonIndex(myID);

                MapLocation myLocation = rc.getLocation();
                rc.broadcast(archonIndex+1, (int)myLocation.x);
                rc.broadcast(archonIndex+2, (int)myLocation.y);

                System.out.println("Archon, myLocation: " + myLocation);

                Direction dir = randomDirection();

                /*
                if (rc.canHireGardener(dir)) {
                    int currGardeners = rc.readBroadcast(archonIndex+3);
                    rc.broadcast(archonIndex+3, currGardeners+1);
                    rc.broadcast(archonIndex+4, myID); // stamping ID of hiring Archon in main Gardener's array loc
                    rc.hireGardener(dir);
                }
                */
                /*
                if (Math.random() < .01 && rc.getBuildCooldownTurns() == 0 && rc.readBroadcast(GARDENER_NUMBER_CHANNEL) < NUM_OF_GARDENERS) {
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

                */

                //if (!rc.hasMoved()) {
                //    wander();
                //}

                /*
                int check = 0;
                Direction dir = Direction.getNorth();
                while (check < 10) {
                    System.out.println("RoundNum = " + rc.getRoundNum());

                    if (!rc.hasMoved() && rc.canMove(dir)) {
                        System.out.println("dir = " + dir);
                        rc.move(dir);
                        Clock.yield();
                    }
                    if (check % 2 == 0) dir = dir.rotateLeftDegrees(60);
                    else dir = dir.rotateLeftDegrees(-60);
                    check++;
                }


                MapLocation currLoc = rc.getLocation();
                Direction requestedDirection = Direction.getNorth(); // example
                while (check < 10) {
                    if (rc.canMove(requestedDirection)) {
                        if(!rc.hasMoved()) rc.move(requestedDirection);
                    }
                    else {
                        MapLocation newLoc = currLoc.add(requestedDirection, 5);
                    }
                    check++;
                }

                //tryMoveWithAvoidance(Direction.getNorth().rotateLeftDegrees(45));
//                tryMoveWithAvoidance(Direction.getSouth());
                */
                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                //tryMoveInStraightLine((float)(3*Math.PI/4));
                Clock.yield();
            } catch (Exception e) {
                System.out.println("Archon Exception");
                e.printStackTrace();
            }
        }
    }

    static void isMainGardenerCheck() throws GameActionException {

        int myID = rc.getID();

        /*if (rc.readBroadcast(0) > 0 && rc.readBroadcast(4) == rc.readBroadcast()) {
            return 0;
        }
        else if (rc.readBroadcast(10) == myID) {
            return 10;
        }
        else if (rc.readBroadcast(20) == myID) {
            return 20;
        }
        return -1;
        // A Gardener starts at 40 HP
        if (rc.readBroadcast(MAIN_GARDENER_CHANNEL) == 0 && rc.getHealth() >= 30) {
            rc.broadcast(MAIN_GARDENER_CHANNEL, rc.getID());
        }

        // if it is, tracking its (x, y) coordinates for it's guard to follow
        if (rc.readBroadcast(MAIN_GARDENER_CHANNEL) == rc.getID()) {
            MapLocation myLocation = rc.getLocation();
            rc.broadcast(GARDENER_X_CHANNEL, (int)myLocation.x);
            rc.broadcast(GARDENER_Y_CHANNEL, (int)myLocation.y);
        }
        */

    }

    static void isCloseToBeingKilled() throws GameActionException {
        // assume this gardener got killed
        if (rc.getHealth() < 20.0) {
            int currGardeners = rc.readBroadcast(GARDENER_NUMBER_CHANNEL);
            rc.broadcast(GARDENER_NUMBER_CHANNEL, currGardeners - 1);

            if (rc.readBroadcast(MAIN_GARDENER_CHANNEL) == rc.getID()) {
                rc.broadcast(MAIN_GARDENER_CHANNEL,0);
                rc.broadcast(GARDENER_X_CHANNEL, -1);
                rc.broadcast(GARDENER_Y_CHANNEL, -1);
            }
        }
    }

    static void runGardener() throws GameActionException {
        System.out.println("I'm a gardener!");

        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
                MapLocation myLocation = rc.getLocation();

                System.out.println("gardener, myLocation: " + myLocation);

                Direction dir = randomDirection();
                if (rc.canBuildRobot(RobotType.SOLDIER, dir) && Math.random() < .01) {
                    rc.buildRobot(RobotType.SOLDIER, dir);
                }


                isMainGardenerCheck(); // check method definition above
                isCloseToBeingKilled(); // check method definition above
                buildGuards(); // guards for archon and main gardener



                if (rc.canBuildRobot(RobotType.SOLDIER, dir) && Math.random() < .01) {
                    rc.buildRobot(RobotType.SOLDIER, dir);
                } else if (rc.canBuildRobot(RobotType.LUMBERJACK, dir) && Math.random() < .01 && rc.isBuildReady()) {
                    rc.buildRobot(RobotType.LUMBERJACK, dir);
                }



                if (rc.canInteractWithTree(rc.getLocation()) && rc.canWater(rc.getLocation())) {
                    rc.water(rc.getLocation());
                }
                else if (Math.random() < .01 && rc.canPlantTree(dir)) {
                    // canPlantTree has Bytecode cost: 10
                    // plantTree has Bytecode cost: 0
                    rc.plantTree(dir);
                }
                else {
                    // Randomly attempt to build a soldier or lumberjack in this direction
                    if (rc.canBuildRobot(RobotType.SOLDIER, dir) && Math.random() < .01) {
                        rc.buildRobot(RobotType.SOLDIER, dir);
                    } else if (rc.canBuildRobot(RobotType.LUMBERJACK, dir) && Math.random() < .01 && rc.isBuildReady()) {
                        rc.buildRobot(RobotType.LUMBERJACK, dir);
                    }

                }


                if (!rc.hasMoved()) {
                    tryMoveInStraightLine((float)(Math.PI * (3/4)));
                }

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println("Gardener Exception");
                e.printStackTrace();
            }
        }
    }

    static void buildGuards() throws GameActionException {

        float teamBullets = rc.getTeamBullets();
        float minBullets = 3/2 * RobotType.SOLDIER.bulletCost;

        if (teamBullets >= minBullets && rc.readBroadcast(ARCHON_GUARD_CHANNEL) == 0) {
            buildSoldierImmediately();
        }

        if (teamBullets >= minBullets && rc.readBroadcast(GARDENER_GUARD_CHANNEL) == 0) {
            buildSoldierImmediately();
        }
    }

    static void buildSoldierImmediately() throws GameActionException {
        int count = 0;
        Direction dir = randomDirection();

        if (rc.hasRobotBuildRequirements(RobotType.SOLDIER)) {
            while (count < 4) {
                if (rc.canBuildRobot(RobotType.SOLDIER, dir)) {
                    rc.buildRobot(RobotType.SOLDIER, dir);
                    break;
                }
                dir = dir.rotateLeftDegrees(90);
                count++;
            }
        }
    }

    // name change ?
    static void helperAssignGuards(int robotType) throws GameActionException {

        int guardID;
        int targetX;
        int targetY;

        if (robotType == 1) {
            guardID = ARCHON_GUARD_CHANNEL;
            targetX = ARCHON_X_CHANNEL;
            targetY = ARCHON_Y_CHANNEL;
        }
        else {
            guardID = GARDENER_GUARD_CHANNEL;
            targetX = GARDENER_X_CHANNEL;
            targetY = GARDENER_Y_CHANNEL;
        }

//        System.out.println("robotType: " + robotType + ", rc.getID(): " + rc.getID() + ", rc.readBroadcast(guardID): " + rc.readBroadcast(guardID));

        if (rc.getID() == rc.readBroadcast(guardID)) {
            if (rc.getHealth() < 20.0) {
                rc.broadcast(guardID, 0); // assign a new soldier to protect archon
            }
            else {
                int xPos = rc.readBroadcast(targetX);
                int yPos = rc.readBroadcast(targetY);
//                System.out.println("robotType:" + robotType + ",xPos: " + xPos + ", yPos: " + yPos);
                follow(xPos, yPos, robotType); // follow the archon/gardener protect it
            }
        }
    }

    static boolean anActiveArchonGuard() throws GameActionException {
        if (rc.readBroadcast(ARCHON_GUARD_CHANNEL) > 0)
            return true;
        return false;
    }

    static boolean anActiveGardenerGuard() throws GameActionException {
        if (rc.readBroadcast(GARDENER_GUARD_CHANNEL) > 0)
            return true;
        return false;
    }

    static void initGuards() throws GameActionException {
        // one soldier can guard either the archon or main gardener
        float minSoldierHealth = 40; // random pick, max HP of a soldier is 50
        int archonGuardId = rc.readBroadcast(ARCHON_GUARD_CHANNEL);
        int gardenerGuardId = rc.readBroadcast(GARDENER_GUARD_CHANNEL);
        int myId = rc.getID();

        if (!anActiveArchonGuard() && rc.getHealth() >= minSoldierHealth && myId != gardenerGuardId) {
//            System.out.println("ARCHON_GUARD: " + rc.getID());
            rc.broadcast(ARCHON_GUARD_CHANNEL, rc.getID());
        }
        else if (!anActiveGardenerGuard() && rc.getHealth() >= minSoldierHealth && myId != archonGuardId) {
//            System.out.println("GARDENER_GUARD: " + rc.getID());
            rc.broadcast(GARDENER_GUARD_CHANNEL, rc.getID());
        }
    }

    static boolean performGuardTask() throws GameActionException {
        int archonGuardId = rc.readBroadcast(ARCHON_GUARD_CHANNEL);
        int gardenerGuardId = rc.readBroadcast(GARDENER_GUARD_CHANNEL);
        int myId = rc.getID();

        initGuards();
        if (myId == archonGuardId) {
            helperAssignGuards(1);
            return true;
        }
        else if (myId == gardenerGuardId) {
            helperAssignGuards(2);
            return true;
        }
        return false;

    }

    static void soldierAttack() throws GameActionException {
        Team enemy = rc.getTeam().opponent();
        // changed the chase code...do not run after the attacker
        RobotInfo[] robots = rc.senseNearbyRobots(-1, enemy);

        // If there are some...
        if (robots.length > 0) {
            // And we have enough bullets, and haven't attacked yet this turn...
            if (rc.canFireSingleShot()) {
                // ...Then fire a bullet in the direction of the enemy.
                rc.fireSingleShot(rc.getLocation().directionTo(robots[0].location));
            }
        }

    }

    static void runSoldier() throws GameActionException {
        System.out.println("I'm an soldier!");

        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {

                /*
                if (performGuardTask()) {
                    if (!rc.hasAttacked()) {
                        soldierAttack();
                    }
                }
                else {
                    wander();
                    if (!rc.hasAttacked()) {
                        soldierAttack();
                    }
                }
                */
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
    static void follow(int target_x, int target_y, int robotType) throws GameActionException {
        try {
            MapLocation targetLocation = new MapLocation(target_x, target_y);
            MapLocation myLocation = rc.getLocation();
            //System.out.println("myLocation = " + myLocation);
            //System.out.println("In follow: target_x = " + target_x + ", target_y = " + target_y + ", robotType: " + robotType);

            float distance = (robotType == 1) ? RobotType.ARCHON.bodyRadius : RobotType.GARDENER.bodyRadius;
            //System.out.println("distance = " + distance);

            if (myLocation.isWithinDistance(targetLocation, 2*distance)) {
                // if (!rc.hasMoved()) tryMoveInACircle(targetLocation);
                // no need to do this
                return;
            }

            if (!rc.hasMoved()) tryMove(myLocation.directionTo(targetLocation), 30, 6);

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
     * Attempts to move in a circle around a center
     *
     * @param center Center around which to move
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryMoveInACircle(MapLocation center) throws GameActionException {
        float x_center = center.x;
        float y_center = center.y;
        MapLocation currLoc = rc.getLocation();
        float curr_x = currLoc.x;
        float curr_y = currLoc.y;
        float x_shift = (curr_x - x_center);
        float y_shift = (curr_y - y_center);
        double theta = Math.PI/6;

        float new_x = (float) (Math.cos(theta) * x_shift - Math.sin(theta) * y_shift + x_center);
        float new_y = (float) (Math.sin(theta) * x_shift + Math.cos(theta) * y_shift + y_center);
        Direction dir = currLoc.directionTo(new MapLocation(new_x, new_y));
        if (tryMove(dir,0,1)) return true;
        else {
            float new_x2 = (float) (Math.cos(-1*theta) * x_shift - Math.sin(-1*theta) * y_shift + x_center);
            float new_y2 = (float) (Math.sin(-1*theta) * x_shift + Math.cos(-1*theta) * y_shift + y_center);
            Direction dir2 = currLoc.directionTo(new MapLocation(new_x2, new_y2));
            if (tryMove(dir2,0,1)) return true;
        }
        return false;
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
     * Senses nearby enemy robots or bullets
     * @return true if enemy robots or bullets can be sensed.
     * @throws GameActionException
     */
    static boolean senseEnemyRobotsAndBullets() throws GameActionException {
        Team enemy = rc.getTeam().opponent();
        RobotInfo[] robots = rc.senseNearbyRobots(-1, enemy);
        BulletInfo[] bullets = rc.senseNearbyBullets(rc.getLocation(), -1);
        if (robots.length > 0 || bullets.length > 0) {
            return true;
        }
        return false;
    }

    /**
     * Attempts to move in a given direction, while avoiding obstacles, enemy robots and bullets.
     * @return true if a move was performed
     * @param requestedDirection The intended direction of movement
     * @throws GameActionException
     */

    static boolean tryMoveWithAvoidance(Direction requestedDirection) throws GameActionException {
        float degreeOffset = 30;
        int checksPerSide = 1;
        float degreesBetween;
        Direction currDirection = requestedDirection;
        Direction newDirection;
        MapLocation currLocation = rc.getLocation();
        boolean moved = false;

        while (checksPerSide <= 3) {

            // if enemy robots or bullets can be sensed, shift course by 30 degrees
            if (senseEnemyRobotsAndBullets()) {
                currDirection = currDirection.rotateLeftDegrees(degreeOffset);
            }

            // tryMove will either continue in current direction (which is initially requestedDirection)
            // or shift in new direction. Need to calculate the current direction if it moved
            // can only use map locations
            if (!rc.hasMoved() && tryMove(currDirection, degreeOffset, checksPerSide)) {
                newDirection = rc.getLocation().directionTo(currLocation);
                degreesBetween = newDirection.degreesBetween(currDirection);
                currDirection = currDirection.rotateLeftDegrees(-1*degreesBetween);
                currLocation = rc.getLocation();
                moved = true;
            }
            else checksPerSide++;
        }

        if (!moved) {
            if (!rc.hasMoved()) {
                tryMove(currDirection, 90, 2);
            }
        }
        return moved;
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

    /**
     * Attempts to move in a straight line, while avoiding small obstacles direction in the path.
     *
     * @param radians The radians at which this direction is facing based off of the unit circle; i.e. facing right would
     *                have 0.0 radians, up would have PI/2 radians, etc. Note: radians = (-Math.PI, Math.PI]
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryMoveInStraightLine(float radians) throws GameActionException {
        return tryMove(new Direction(radians), 30, 6);
    }
}

