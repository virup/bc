package SpartanPlayer;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public strictfp class Soldier {

    private void moveRandomly() {

    }

    private void moveInADirection(Direction dir) {

    }

    private void moveInARectangle() {

    }

    private boolean shouldFireBullets() {
        return false;
    }

    private boolean shouldFireSingleBullet() {
        return false;
    }

    // Triads are a set of three bullets offset by 20 degree angles. Triads cost 4x the standard shot cost.
    private boolean shouldFireTriadShot() {
        return false;
    }

    // Pentads are a set of five bullets offset by 15 degree angles. Pentads cost 6x the standard shot cost.
    private boolean shouldFirePentadShot() {
        return false;
    }

    private void fireSingleBullet() {
    }

    // Triads are a set of three bullets offset by 20 degree angles. Triads cost 4x the standard shot cost.
    private void fireTriadShot() {
    }

     // Pentads are a set of five bullets offset by 15 degree angles. Pentads cost 6x the standard shot cost.
    private void firePentadShot() {
    }

    private void exchangeBulletsForVictoryPoints() {

    }

    private void shouldExchangeBulletsForVictoryPoints() {
    }

    // is under attack
    private boolean isUnderAttack() {
        return false;
    }

    // Sight Radius = 7 for Soldier
    // Can broadcast to other robots in the team
    private MapLocation isEnemyCloseBy(RobotController rc) {
        return rc.getLocation();
    }

}

