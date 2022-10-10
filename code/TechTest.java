import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.*;

// SOLUTION 2: better and follow the open-closed principal, better for future extension, since you might need to add/remove angle turns
class RobotTurnAngle {
    public RobotTurnAngle previous;
    public RobotTurnAngle next;
    public String indentifier;
    public int degree;

    public RobotTurnAngle(String indentifier, int degree) {
        this.indentifier = indentifier;
        this.degree = degree;
    }

    public RobotTurnAngle add(RobotTurnAngle robotTurnAngle) {
        this.next = robotTurnAngle;
        robotTurnAngle.previous = this;
        return this;
    }
}

class CircularAngleList extends LinkedList<RobotTurnAngle> {
    public CircularAngleList addAngle(RobotTurnAngle robotTurnAngle) {
        if (size() > 0) {
            this.getLast().next = robotTurnAngle;
            robotTurnAngle.previous = this.getLast();
            robotTurnAngle.next = this.getFirst();
            this.getFirst().previous = robotTurnAngle;
        }
        add(robotTurnAngle);
        return this;
    }
}

class Robot {

    private static final int DIMENSION_LIMIT = 5;
    private static final CircularAngleList ROBOT_TURNABLE_ANGLES = new CircularAngleList()
            .addAngle(new RobotTurnAngle("SOUTH", 270))
            .addAngle(new RobotTurnAngle("WEST", 180))
            .addAngle(new RobotTurnAngle("NORTH", 90))
            .addAngle(new RobotTurnAngle("EAST", 0));
    private final List<String> VALID_DIRECTION_TAGS = Arrays.asList("EAST", "NORTH", "WEST", "SOUTH");
    public int x;
    public int y;
    public RobotTurnAngle angle;

    public Robot(int x, int y, RobotTurnAngle angle) {
        this.x = x;
        this.y = y;
        this.angle = angle;
    }

    public void place(int x, int y, String direction) {
        if (!VALID_DIRECTION_TAGS.contains(direction))
            return;
        this.x = x;
        this.y = y;
        this.angle = ROBOT_TURNABLE_ANGLES
                .stream()
                .filter(angle -> direction.equals(angle.indentifier))
                .findFirst()
                .get();
    }

    public void move() {
        int degree = angle.degree;
        int i = (int) Math.cos(Math.toRadians(degree)); // x + i (cos is x asix)
        int j = (int) Math.sin(Math.toRadians(degree)); // y + j (sin is y asix)
        int newX = x + i;
        int newY = y + j;
        if (newX < 0 || newX >= DIMENSION_LIMIT || newY < 0 || newY >= DIMENSION_LIMIT)
            return;
        x = newX;
        y = newY;
    }

    public void left() {
        angle = angle.previous;
    }

    public void right() {
        angle = angle.next;
    }

    public void report() {
        System.out.println(String.format("\n%s,%s,%s\n", x, y, angle.indentifier));
    }
}

public class TechTest {

    private static List<String> toCommands(String filePath) {
        if (filePath == null || "".equals(filePath)) {
            URL url = TechTest.class.getResource("commands.txt");
            filePath = url.getFile();
        }
        List<String> commands = new ArrayList<>();
        Scanner sc = null;
        try {
            File file = new File(filePath);
            sc = new Scanner(file);
            String line;
            while (sc.hasNextLine()) {
                line = sc.nextLine();
                commands.add(line);
            }
        }
        catch(FileNotFoundException e) {
            e.printStackTrace();
        }
        finally {
            if (sc != null) sc.close();
        }
        return commands;
    }


    private static final List<String> VALID_COMMAND_PREFIXES = Arrays.asList("PLACE", "MOVE", "LEFT", "RIGHT", "REPORT");

    // SOLUTION 1: mark directions as indexes (or angles or sin & cos) with offset of 1 (or any value as you please), move left means (i - 1), move right means (i + 1)
    // have to handle 2 if else statements (for #1 and #4) - HARD CODED
    /*public static final Map<String, Integer> DIRECTIONS;
    static {
        DIRECTIONS = new HashMap<>();
        DIRECTIONS.put("EAST", 0);
        DIRECTIONS.put("NORTH", 90);
        DIRECTIONS.put("WEST", 180);
        DIRECTIONS.put("SOUTH", 270);
    }*/
    
    public static void main(String[] args) {
        // init
        Robot robot = new Robot(0, 0, null);
        // read from commands.txt
        List<String> commands = toCommands(""); // default file commands.txt
        commands
                .stream()
                .filter(command -> command != null && !"".equals(command) && VALID_COMMAND_PREFIXES.contains(command.split("\\s+")[0]))
                .forEach(command -> {
                    if (command.startsWith("PLACE")) {
                        String[] placeArgs = command.split("\\s+")[1].split(",");
                        robot.place(Integer.valueOf(placeArgs[0]), Integer.valueOf(placeArgs[1]), placeArgs[2]);
                    } else if (command.startsWith("MOVE") && robot.angle != null)
                        robot.move();
                    else if (command.startsWith("LEFT") && robot.angle != null)
                        robot.left();
                    else if (command.startsWith("RIGHT") && robot.angle != null)
                        robot.right();
                    else if (command.startsWith("REPORT") && robot.angle != null)
                        robot.report();
                });
    }

}
