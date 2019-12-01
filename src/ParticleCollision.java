
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.StdDraw;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ParticleCollision {
    public static class Particle {
        private static final double INFINITY = Double.POSITIVE_INFINITY;

        private double rx, ry;        // position
        private double vx, vy;        // velocity
        private int count;            // number of collisions so far
        private final double radius;  // radius
        private final double mass;    // mass
        private final Color color;    // color

        Particle(double rx, double ry, double vx, double vy, double radius, double mass, Color color) {
            this.vx = vx;
            this.vy = vy;
            this.rx = rx;
            this.ry = ry;
            this.radius = radius;
            this.mass   = mass;
            this.color  = color;
        }

        void move(double dt) {
            rx += vx * dt;
            ry += vy * dt;
        }

        public void draw() {
            StdDraw.setPenColor(color);
            StdDraw.filledCircle(rx, ry, radius);
        }

        public int count() {
            return count;
        }

        double timeToHit(Particle that) {
            if (this == that) return INFINITY;
            double dx  = that.rx - this.rx;
            double dy  = that.ry - this.ry;
            double dvx = that.vx - this.vx;
            double dvy = that.vy - this.vy;
            double dvdr = dx*dvx + dy*dvy;
            if (dvdr > 0) return INFINITY;
            double dvdv = dvx*dvx + dvy*dvy;
            if (dvdv == 0) return INFINITY;
            double drdr = dx*dx + dy*dy;
            double sigma = this.radius + that.radius;
            double d = (dvdr*dvdr) - dvdv * (drdr - sigma*sigma);
            // if (drdr < sigma*sigma) StdOut.println("overlapping particles");
            if (d < 0) return INFINITY;
            return -(dvdr + Math.sqrt(d)) / dvdv;
        }

        double timeToHitVerticalWall() {
            if      (vx > 0) return (1.0 - rx - radius) / vx;
            else if (vx < 0) return (radius - rx) / vx;
            else             return INFINITY;
        }

        double timeToHitHorizontalWall() {
            if      (vy > 0) return (1.0 - ry - radius) / vy;
            else if (vy < 0) return (radius - ry) / vy;
            else             return INFINITY;
        }

        double timeToHitVerticalObstacle(Obstacle obstacle) {
            double east  = obstacle.xc+obstacle.length;
            double west  = obstacle.xc-obstacle.length;
            double north = obstacle.yc+obstacle.length;
            double south = obstacle.yc-obstacle.length;

            if  (vx > 0 ) {
                double time = (west - radius - rx ) / vx;
                double fy = ry + time*vy;
                if (time < 0) return INFINITY;
                if (fy >= south && fy <= north) return time;
                // corner case
                if (fy > north) {
                    time = (west - rx ) / vx;
                    fy = ry + time*vy;
                    double D = (rx-west)*(fy-north);
                    if ((((west-rx)*(west-rx)+(fy-ry)*(fy-ry))*radius*radius - D*D) >= 0) {
                        //System.out.println("左上V");
                        return timeToHitCornerObstacle(obstacle);
                    }
                }
                if (fy < south) {
                    time = (west - rx ) / vx;
                    fy = ry + time*vy;
                    double D = (rx-west)*(fy-south);
                    if ((((west-rx)*(west-rx)+(fy-ry)*(fy-ry))*radius*radius - D*D) >= 0) {
                        //System.out.println("左下V");
                        return timeToHitCornerObstacle(obstacle);
                    }
                }


            } else if (vx < 0 ) {
                double time = (east + radius - rx ) / vx;
                double fy = ry + time*vy;
                if (time < 0) return INFINITY;
                if (fy >= south && fy <= north) return time;
                // corner case
                if (fy > north) {
                    time = (east - rx ) / vx;
                    fy = ry + time*vy;
                    double D = (rx-east)*(fy-north);
                    if ((((east-rx)*(east-rx)+(fy-ry)*(fy-ry))*radius*radius - D*D) >= 0) {
                        //System.out.println("右上V");
                        return timeToHitCornerObstacle(obstacle);
                    }
                }
                if (fy < south) {
                    time = (east - rx ) / vx;
                    fy = ry + time*vy;
                    double D = (rx-east)*(fy-south);
                    if ((((east-rx)*(east-rx)+(fy-ry)*(fy-ry))*radius*radius - D*D) >= 0) {
                        //System.out.println("右下V");
                        return timeToHitCornerObstacle(obstacle);
                    }
                }
            }

            return INFINITY;
        }

        double timeToHitHorizontalObstacle(Obstacle obstacle) {
            double east  = obstacle.xc+obstacle.length;
            double west  = obstacle.xc-obstacle.length;
            double north = obstacle.yc+obstacle.length;
            double south = obstacle.yc-obstacle.length;

            if  (vy > 0) {
                double time = (south - radius - ry ) / vy;
                double fx = rx + time*vx;
                if (time < 0) return INFINITY;
                if (fx >= west && fx <= east) return time;
                // corner case
                if (fx < west) {
                    time = (south - ry ) / vy;
                    fx = rx + time*vx;
                    double D = -(fx-west)*(ry-south);
                    if ((((fx-rx)*(fx-rx)+(south-ry)*(south-ry))*radius*radius - D*D) >= 0) {
                        //System.out.println("左下H");
                        return timeToHitCornerObstacle(obstacle);
                    }
                }
                if (fx > east) {
                    time = (south - ry ) / vy;
                    fx = rx + time*vx;
                    double D = -(fx-east)*(ry-south);
                    if ((((fx-rx)*(fx-rx)+(south-ry)*(south-ry))*radius*radius - D*D) >= 0) {
                        //System.out.println("右下H");
                        return timeToHitCornerObstacle(obstacle);
                    }
                }

            } else if (vy < 0) {
                double time = (north + radius - ry ) / vy;
                double fx = rx + time*vx;
                if (time < 0) return INFINITY;
                if (fx >= west && fx <= east) return time;
                // corner case
                if (fx < west) {
                    time = (north - ry ) / vy;
                    fx = rx + time*vx;
                    double D = -(fx-west)*(ry-north);
                    if ((((fx-rx)*(fx-rx)+(north-ry)*(north-ry))*radius*radius - D*D) >= 0) {
                        //System.out.println("左上H");
                        return timeToHitCornerObstacle(obstacle);
                    }
                }
                if (fx > east) {
                    time = (north - ry ) / vy;
                    fx = rx + time*vx;
                    double D = -(fx-east)*(ry-north);
                    if ((((fx-rx)*(fx-rx)+(north-ry)*(north-ry))*radius*radius - D*D) >= 0) {
                        //System.out.println("右上H");
                        return timeToHitCornerObstacle(obstacle);
                    }
                }
            }

            return INFINITY;
        }

        private double timeToHitCornerObstacle(Obstacle obstacle) {
            double east  = obstacle.xc+obstacle.length;
            double west  = obstacle.xc-obstacle.length;
            double north = obstacle.yc+obstacle.length;
            double south = obstacle.yc-obstacle.length;

            double[] timeToHitCorner = new double[4];
            Particle p = new Particle(rx,ry,vx,vy,0,mass,color);

            timeToHitCorner[0] = p.timeToHit(new Particle(west,north,0,0,radius,mass,null));
            timeToHitCorner[1] = p.timeToHit(new Particle(west,south,0,0,radius,mass,null));
            timeToHitCorner[2] = p.timeToHit(new Particle(east,north,0,0,radius,mass,null));
            timeToHitCorner[3] = p.timeToHit(new Particle(east,south,0,0,radius,mass,null));

            double time = timeToHitCorner[0];

            // Find minimum time
            for (int i = 0; i < 4; i++) {
                if (timeToHitCorner[i] < time) {
                    time = timeToHitCorner[i];
                }
            }

            return time;
        }

        void bounceOff(Particle that) {
            double dx  = that.rx - this.rx;
            double dy  = that.ry - this.ry;
            double dvx = that.vx - this.vx;
            double dvy = that.vy - this.vy;
            double dvdr = dx*dvx + dy*dvy;             // dv dot dr
            double dist = this.radius + that.radius;   // distance between particle centers at collision

            // magnitude of normal force
            double magnitude = 2 * this.mass * that.mass * dvdr / ((this.mass + that.mass) * dist);

            // normal force, and in x and y directions
            double fx = magnitude * dx / dist;
            double fy = magnitude * dy / dist;

            // update velocities according to normal force
            this.vx += fx / this.mass;
            this.vy += fy / this.mass;
            that.vx -= fx / that.mass;
            that.vy -= fy / that.mass;

            // update collision counts
            this.count++;
            that.count++;
        }

        void bounceOffVerticalWall() {
            vx = -vx;
            count++;
        }

        void bounceOffHorizontalWall() {
            vy = -vy;
            count++;
        }
        // 45 degree corner
        void bounceOffNormalCorner() {
            vx = -vx;
            vy = -vy;
            count++;
        }

    }

    public static class ParticleSystem {
        private static final double HZ = 0.5;    // number of redraw events per clock tick
        private MinPQ<Event> pq;          // the priority queue
        private double t = 0.0;           // simulation clock time
        private Particle[] particles;     // the array of particles
        private Obstacle obstacle = null;
        private double east  = 0.0 ;
        private double west  = 0.0 ;
        private double north = 0.0 ;
        private double south = 0.0 ;

        public ParticleSystem(Particle[] particles) {
            this.particles = particles.clone();   // defensive copy
        }

        ParticleSystem(Particle[] particles, Obstacle obstacle) {
            this.particles = particles.clone();   // defensive copy
            this.obstacle  = obstacle;
            east  = obstacle.xc+obstacle.length;
            west  = obstacle.xc-obstacle.length;
            north = obstacle.yc+obstacle.length;
            south = obstacle.yc-obstacle.length;
        }

        // updates priority queue with all new events for particle a
        private void predict(Particle a, double limit) {
            if (a == null) return;

            double dtX;
            double dtY;

            // particle-particle collisions
            for (Particle particle : particles) {
                double dt = a.timeToHit(particle);
                if (t + dt <= limit)
                    pq.insert(new Event(t + dt, a, particle));
            }

            // particle-square-obstacle collisions
            if (obstacle != null) {
                dtX = a.timeToHitVerticalObstacle(obstacle);
                dtY = a.timeToHitHorizontalObstacle(obstacle);

                // corner
                if (t + dtX <= limit && dtX == dtY) {
                    // Vertical corner
                    if (a.vx > 0) {
                        double time = (west - a.radius - a.rx) / a.vx;
                        if (time > 0) {
                            double fy = a.ry + a.vy * time;
                            if (fy > south-a.radius && fy < north + a.radius) {
                                pq.insert(new Event(t + dtX, a, null));
                            }
                        }
                    }
                    if (a.vx < 0) {
                        double time = (east + a.radius - a.rx) / a.vx;
                        if (time > 0) {
                            double fy = a.ry + a.vy * time;
                            if (fy > south-a.radius && fy < north + a.radius) {
                                pq.insert(new Event(t + dtX, a, null));
                            }
                        }
                    }

                    // Horizontal corner
                    if (a.vy >0) {
                        double time = (south - a.radius - a.ry) / a.vy;
                        if (time > 0) {
                            double fx = a.rx + a.vx * time;
                            if (fx > west-a.radius && fx < east + a.radius) {
                                pq.insert(new Event(t + dtY, null, a));
                            }
                        }
                    }
                    if (a.vy <0) {
                        double time = (north + a.radius - a.ry) / a.vy;
                        if (time > 0) {
                            double fx = a.rx + a.vx * time;
                            if (fx > west-a.radius && fx < east + a.radius) {
                                pq.insert(new Event(t + dtY, null, a));
                            }
                        }
                    }

                } else {

                    // Vertical wall
                    if (t + dtX <= limit) pq.insert(new Event(t + dtX, a, null));

                    // Horizontal wall
                    if (t + dtY <= limit) pq.insert(new Event(t + dtY, null, a));
                }

            }

            // particle-boundary-wall collisions
            dtX = a.timeToHitVerticalWall();
            dtY = a.timeToHitHorizontalWall();

            if (t + dtX <= limit) pq.insert(new Event(t + dtX, a, null));
            if (t + dtY <= limit) pq.insert(new Event(t + dtY, null, a));

        }

        // redraw all particles
        private void redraw(double limit) {
            StdDraw.clear();
            StdDraw.setPenColor(StdDraw.PRINCETON_ORANGE);
            StdDraw.filledSquare(obstacle.xc,obstacle.yc,obstacle.length);
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.text(obstacle.xc,obstacle.yc,"Time :"+t);

            for (Particle particle : particles) {
                particle.draw();
            }

            StdDraw.show();
            StdDraw.pause(20);
            if (t < limit) {
                pq.insert(new Event(t + 1.0 / HZ, null, null));
            }
        }

        void simulate(double limit) {

            // initialize PQ with collision events and redraw event
            pq = new MinPQ<>();
            for (Particle value : particles) {
                predict(value, limit);
            }
            pq.insert(new Event(0, null, null));        // redraw event


            // the main event-driven simulation loop
            while (!pq.isEmpty()) {

                // get impending event, discard if invalidated
                Event e = pq.delMin();
                if (!e.isValid()) continue;
                Particle a = e.a;
                Particle b = e.b;

                // physical collision, so update positions, and then simulation clock
                for (Particle particle : particles) particle.move(e.time - t);
                t = e.time;

                // process event
                if      (a != null && b != null) {
                    if (b.mass == Double.NEGATIVE_INFINITY) a.bounceOffNormalCorner();
                    else a.bounceOff(b);              // particle-particle collision
                }
                else if (a != null && b == null) a.bounceOffVerticalWall();   // particle-wall collision
                else if (a == null && b != null) b.bounceOffHorizontalWall(); // particle-wall collision
                else if (a == null && b == null) redraw(limit);               // redraw event

                // update the priority queue with new collisions involving a or b
                predict(a, limit);
                predict(b, limit);

            }
        }


        /***************************************************************************
         *  An event during a particle collision simulation. Each event contains
         *  the time at which it will occur (assuming no supervening actions)
         *  and the particles a and b involved.
         *
         *    -  a and b both null:      redraw event
         *    -  a null, b not null:     collision with vertical wall
         *    -  a not null, b null:     collision with horizontal wall
         *    -  a and b both not null:  binary collision between a and b
         *
         ***************************************************************************/
        private static class Event implements Comparable<Event> {
            private final double time;         // time that event is scheduled to occur
            private final Particle a, b;       // particles involved in event, possibly null
            private final int countA, countB;  // collision counts at event creation

            // create a new event to occur at time t involving a and b
            Event(double t, Particle a, Particle b) {
                this.time = t;
                this.a = a;
                this.b = b;
                if (a != null) countA = a.count();
                else countA = -1;
                if (b != null) countB = b.count();
                else countB = -1;
            }

            // compare times when two events will occur
            public int compareTo(Event that) {
                return Double.compare(this.time, that.time);
            }

            // has any collision occurred between when event was created and now?
            boolean isValid() {
                if (a != null && a.count() != countA) return false;
                return b == null || b.count() == countB;
            }

        }

    }

    public static class Obstacle{
        private double xc;                // x of obstacle center
        private double yc;                // y of obstacle center
        private double length;            // half length of square

        Obstacle(double xc, double yc, double length){
            this.xc = xc;
            this.yc = yc;
            this.length = 0.5*length;
        }

    }

    public static void main(String[] args) throws FileNotFoundException {

        StdDraw.setCanvasSize(600, 600);
        StdDraw.setXscale(0, 1);
        StdDraw.setYscale(0, 1);
        StdDraw.enableDoubleBuffering();

        // the array of particles
        Particle[] particles;

        Obstacle obstacle = new Obstacle(0.5,0.5,0.2);

        StdDraw.setPenColor(StdDraw.PRINCETON_ORANGE);
        StdDraw.filledSquare(obstacle.xc,obstacle.yc,obstacle.length);
        StdDraw.show();

//        File file = new File(args[0]);
        File file = new File("input_HW5.txt") ;  // file name assigned
        Scanner in = new Scanner(file);
        int numParticle = Integer.parseInt(in.nextLine());
        double time = Double.parseDouble(in.nextLine());

        particles = new Particle[numParticle];
        for (int i = 0; i < numParticle; i++) {
            String line = in.nextLine();
            String[] particle = line.split(" ");
            double rx     = Double.parseDouble(particle[0]);
            double ry     = Double.parseDouble(particle[1]);
            double vx     = Double.parseDouble(particle[2]);
            double vy     = Double.parseDouble(particle[3]);
            double radius = Double.parseDouble(particle[4]);
            double mass   = Double.parseDouble(particle[5]);
            particles[i] = new Particle(rx,ry,vx,vy,radius,mass,Color.BLACK);
        }

        // create collision system and simulate
        ParticleSystem system = new ParticleSystem(particles,obstacle);
        system.simulate(time);
        for (int i = 0; i < numParticle; i++) {
            System.out.println(particles[i].count());
        }
    }
}

