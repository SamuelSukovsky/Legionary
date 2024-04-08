package dkit;
import robocode.*;
import java.util.Random;
import java.awt.Color;

// API help : https://robocode.sourceforge.io/docs/robocode/robocode/Robot.html

/**
 * Legionary - a robot by (your name here)
 */
public class Legionary extends Robot
{
	/**
	 * run: Legionary's default behaviour
	 */
    boolean lookingForSentry = true;
    int sentryQuadrant = 0;
    int direction = 1;
    int corner = -1;
    double sentryX;
    double sentryY;
    
    double targetX;
    double targetY;
    int tar;
    
    double power;
    boolean dodge = false;
    boolean go = false;
    boolean tLocked;
    
    
	
    public void run() 
    {
        setColors(Color.red,Color.white,Color.red);
            
          
        if(getNumSentries() > 0)
        {
            while (lookingForSentry)
            {
                turnRadarRight(360);
            }
        }
		lookingForSentry = false;
            
        double dir = getHeading();
        dir = dir - (90 * ((sentryQuadrant + 2) % 4));
        
        if (dir < -180)
        {              
            turnLeft(360 + dir);     
        }
        else if (dir < 180)
        {
            turnLeft(dir);             
        } 
        else
        {              
            turnRight(360 - dir);
        }
        dodge = true;
            
        while(!go)
        {
            ahead(800);
        }
        
        dodge = false;
        while(go) 
        {
            if ((getX() > 650 || getX() < 150) && (getY() > 650 || getY() < 150))
            {
                ahead(150 * direction);
            }
            else
            {
                Random rand = new Random();
                ahead((75 + 75 * rand.nextDouble()) * direction);
                tLocked = false;
                switch ((int)getHeading() / 90)
                {
                    case 0 -> 
                    {
                        if (targetY > getY())
                        {
                            tar = 1;
                        }
                        else
                        {
                            tar = -1;
                        }
                    }
                    case 1 -> 
                    {
                        if (targetX > getX())
                        {
                            tar = 1;
                        }
                        else
                        {
                            tar = -1;
                        }
                    }
                    case 2 -> 
                    {
                        if (targetY > getY())
                        {
                            tar = -1;
                        }
                        else
                        {
                            tar = 1;
                        }
                    }
                    case 3 -> 
                    {
                        if (targetX > getX())
                        {
                            tar = -1;
                        }
                        else
                        {
                            tar = 1;
                        }
                    }
                }
                turnGunLeft(tar * 90);
                if (!tLocked)
                {
                    turnGunRight(tar * 180);
                }
            }
        }
    }

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
    public void onScannedRobot(ScannedRobotEvent e) 
    {
            // Replace the next line with any behavior you would like
        if (lookingForSentry)
        {
            if (e.isSentryRobot())
            {
                double distance = e.getDistance();
                double raDir = getRadarHeading();
                raDir = Math.toRadians(raDir);
                sentryX = getX() + Math.sin(raDir) * distance;
                sentryY = getY() + Math.cos(raDir) * distance;
                
                if(sentryY > 400)
                {
                    if(sentryX > 400)
                    {
                        sentryQuadrant = 0;
                    }
                    else
                    {
                        sentryQuadrant = 3;
                    }
                }
                else
                {
                    if(sentryX > 400)
                    {
                        sentryQuadrant = 1;
                    }
                    else
                    {
                        sentryQuadrant = 2;
                    }
                }
                    
                lookingForSentry = false;
            }
        }
        else
        {
            if (!e.isSentryRobot())
            {
                double distance = e.getDistance();
                double raDir = getRadarHeading();
                raDir = Math.toRadians(raDir);
                targetX = getX() + Math.sin(raDir) * distance;
                targetY = getY() + Math.cos(raDir) * distance;
                
                power = 1800 / (400 + distance);
                if (getGunHeat() == 0)
                {
                    fire(power);
                }
                
                tLocked = true;
                if (go)
                {
                    raDir = (getHeading() + 90 - getGunHeading()) % 360;
                    if (raDir < 180)
                    {
                        turnGunRight(raDir);
                    }
                    else
                    {
                        turnGunRight(raDir - 360);
                    }
                }
            }
        }
    }
	
    public void onHitWall(HitWallEvent e)
    {
        go = false;
        if (corner == -1)
        {                
            turnGunRight(90);
            turnRight(90);
        }
        else if (corner % 2 == 0)
        {
            turnRight(direction * 90);
        }
        else
        {
            direction = -direction;
        }
        corner++;
        go = true;
    }
    
    public void onHitRobot(HitRobotEvent e)
    {
        if (dodge)
        {
            turnRight(90);
            ahead(50);
            turnLeft(90);
        }
    }
}
