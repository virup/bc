package rahul_player1;
import battlecode.common.*;

public strictfp class RobotPlayer {
    static RobotController rc;
    static int BULLET_CHANNEL = 100;
    static int ARCHON_X = 0;
    static int ARCHON_Y = 0;

    static int ARCHON_DIRECTION = 1;
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


                // Generate a random direction
                // Direction dir = randomDirection();

                // Randomly attempt to build a gardener in this direction
                // if (rc.canHireGardener(dir) && Math.random() < .01) {
                //    rc.hireGardener(dir);
                // }

                /*
                // hire gardener always, will this be expensive to do ?
                if (rc.canHireGardener(dir)) {
                    rc.hireGardener(dir);
                }
                */

                // printing out useful info to track archon's movement
                //System.out.println("dir.getAngleDegrees: " + dir.getAngleDegrees());
                //System.out.println("dir.radians: " + dir.radians);
                //System.out.println("dir.opposite().getAngleDegrees(): " + dir.opposite().getAngleDegrees());
                //System.out.println("dir: " + dir.);

                // Dodge bullets
                // Bullet sensing is different from regular sensing. All units have a bullet
                // sensing range that is larger than their sight range, meaning bullets
                // can be sensed at a further distance than the units that fire them.

                // Move randomly
                //boolean didMove = tryMove(dir, 90, 4);
                //System.out.println(didMove);
                //tryMoveInARectangle();
                // Broadcast archon's location for other robots on the team to know
                tryMoveInARectangle();
                MapLocation myLocation = rc.getLocation();
                rc.broadcast(ARCHON_X,(int)myLocation.x);
                rc.broadcast(ARCHON_Y,(int)myLocation.y);

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
                int xPos = rc.readBroadcast(ARCHON_X);
                int yPos = rc.readBroadcast(ARCHON_Y);
                MapLocation archonLoc = new MapLocation(xPos,yPos);

                // Generate a random direction
                Direction dir = randomDirection();

                MapLocation archonLocX = new MapLocation(xPos,yPos);
                MapLocation archonLocY = new MapLocation(xPos,yPos);


                if (rc.canPlantTree(dir)) { // Bytecode cost: 10
                    // don't want the gardener to be stuck in the space between planted trees
                    rc.plantTree(dir); // Bytecode cost 0
                    // donate
                    exchangeBulletsForVPs();
                }

                // Move randomly
                wander();

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
        if (!rc.hasMoved() && rc.canMove(dir)) {
            System.out.println("rc.canMove in tryMove... ");
            rc.move(dir);
            return true;
        }

        // Now try a bunch of similar angles
        boolean moved = false;
        int currentCheck = 1;

        while(currentCheck<=checksPerSide) {
            // Try the offset of the left side
            if(!rc.hasMoved() && rc.canMove(dir.rotateLeftDegrees(degreeOffset*currentCheck))) {
                rc.move(dir.rotateLeftDegrees(degreeOffset*currentCheck));
                return true;
            }
            // Try the offset on the right side
            if(!rc.hasMoved() && rc.canMove(dir.rotateRightDegrees(degreeOffset*currentCheck))) {
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
     * Attempts to move the robot continuously in a rectangle (anticlockwise direction)
     * @return true if a move was performed
     * @throws GameActionException
     */

    static boolean tryMoveInARectangle() throws GameActionException {
        //System.out.println("ARCHON_DIRECTION: " + ARCHON_DIRECTION);
        if (ARCHON_DIRECTION == 1) {
                if (canMoveNWSE(Direction.getNorth(), Direction.getWest(), 2)) return true;
            }
            else if (ARCHON_DIRECTION == 2) {
                if (canMoveNWSE(Direction.getWest(), Direction.getSouth(), 3)) return true;
            }
            else if (ARCHON_DIRECTION == 3) {
                if (canMoveNWSE(Direction.getSouth(), Direction.getEast(), 4)) return true;
            }
            else {
                if (canMoveNWSE(Direction.getEast(), Direction.getNorth(), 1)) return true;
            }
        //System.out.println("Returning false...");
        return false;
        }

    /**
     * Helper method to move in NWSE method (anticlockwise direction)
     *
     * @throws GameActionException
     */

    static boolean canMoveNWSE(Direction currDir, Direction newDir, int newDirCode) throws GameActionException {
        if (!rc.hasMoved() && rc.canMove(currDir)) {
            rc.move(currDir);
            return true;
        }
        else {
            if (!rc.hasMoved() && rc.canMove(newDir)) {
                rc.move(newDir);
                ARCHON_DIRECTION = newDirCode;
                return true;
            }
        }
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

    public static void wander() throws GameActionException {
        try {
            Direction dir = randomDirection();
            if (rc.canMove(dir)) {
                rc.move(dir);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void checkNearbyBulletsBeforeMoving() throws GameActionException {
        try {
            BulletInfo[] bulletArray = rc.senseNearbyBullets();
            Direction dir = randomDirection();
            for (BulletInfo b : bulletArray) {
                if (b.getDir().equals(dir))
                    dir = randomDirection();
            }

            if (rc.canMove(dir)) {
                rc.move(dir);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void exchangeBulletsForVPs() throws GameActionException {
        try {
            //int victory_points = rc.getTeamVictoryPoints();

            // broadcast number of bullets in a channel...read it back...
            // if the number has increased by a certain amount (?)
            // then donate (how much ?)
            int prev_bullets = rc.readBroadcast(BULLET_CHANNEL);
            int current_bullets = (int)rc.getTeamBullets();
            rc.broadcast(BULLET_CHANNEL, current_bullets);

            // if number of bullets increase by 10%, then donate
            if (prev_bullets == 0) return;
            float percentageIncrease = ((current_bullets - prev_bullets) / prev_bullets) * 100;
            System.out.println("percentageIncrease: " + percentageIncrease);

            if (percentageIncrease > 10.0) {
                rc.donate(current_bullets-prev_bullets);
                System.out.println("VP: " + rc.getTeamVictoryPoints());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

