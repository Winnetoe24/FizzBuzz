import javax.xml.stream.*;
import javax.xml.stream.events.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * this is a FizzBuzz-Game inspired by Tom Scotts Video "FizzBuzz: One Simple Interview Question"
 * video: https://youtu.be/QPZ0pIK_wsc
 *
 * License in GitHub repository
 *
 *
 * @author Alexander Brand
 * @date 08.10.2020
 */
public class FizzBuzz {

    public static void main(String[] args){
        FizzBuzz fizzBuzz = new FizzBuzz();
    }

    private Scanner sc;
    private HashMap<Integer,String> hs;
    private HashMap<String, Object> settings;
    public FizzBuzz(){
        this.menu();
        sc = new Scanner(System.in);
        hs = new HashMap<>();
        settings = new HashMap<>();
        this.reset();
        this.input();
    }

    private void menu(){
        System.out.println("-        FizzBuzz        -");
        System.out.println("");
        System.out.println("Commands:");
        System.out.println("    newgame - starts a new Game");
        System.out.println("    menu - shows this");
        System.out.println("    rules - shows the rules of FizzBuzz");
        System.out.println("");
        System.out.println("    highscore - shows highscores");
        System.out.println("    save [path to Directory] - saves highscores in the given directory to a file named 'highscores.xml'");
        System.out.println("    load [path to file] - loads highscores from the given file");
        System.out.println("");
        System.out.println("    answers [number] - shows all answers from 1 to the given number");
        System.out.println("    answer [number] - shows the answer of the given number BUT ends the current game");
        System.out.println("");
        System.out.println("    set [setting] [value] - set a setting to the given value");
        System.out.println("    settings - shows all settings and their values");
        System.out.println("    savesettings [path to directory] - saves the settings in the given directory to a file named 'settings.xml'");
        System.out.println("    loadsettings [path to file] - loads settings from the given file");
        System.out.println("    reset - resets the settings to default");
        System.out.println("");
        System.out.println("    quit - stops the programm");

    }

    private void rules(){
        System.out.println("--------------------");
        System.out.println("        rules      ");
        System.out.println("--------------------");
        System.out.println("You are in a conversation with the computer.");
        System.out.println("Every turn a higher number is said, starting with 1,");
        System.out.println("BUT for every number which is multible of 3 you say fizz and of 5 you say buzz.");
        System.out.println("Is a number a multiple of 3 and 5 you say fizzbuzz.");
        System.out.println("");
        System.out.println("Your goal is to get the highest score.");
        System.out.println("To get a higher score, you have say more numbers/words .");
        System.out.println("You can activate a Timer, which will limit the time you have and is checked when you give an answer.");
        System.out.println("The timer will appear in your highscores and is counted in milliseconds.");
    }

    private boolean run = true;

    private void input(){
        while(run){
            String s = sc.nextLine();
            switch(s) {
                case "newgame":
                    endGame();
                    turn = 0;
                    System.out.println("--------------------");
                    System.out.println("starting a new game");
                    System.out.println("--------------------");

                    lastTurn = System.currentTimeMillis();
                    if (Math.random() > 0.5) {
                        //programms turn
                        turn++;
                        System.out.println("    " + this.getResult(turn));
                    }

                    break;
                case "highscore":
                    System.out.println("");
                    System.out.println("--------------------");
                    System.out.println("    highscores");
                    System.out.println("--------------------");
                    LinkedList<Integer> keys = new LinkedList<>(hs.keySet());
                    Collections.sort(keys);
                    for (int i = keys.size() - 1; i >= 0; i--) {
                        System.out.println(hs.get(keys.get(i)));
                    }
                    break;
                case "menu":
                    this.menu();
                    break;
                case "rules":
                    this.rules();
                    break;

                case "settings":
                    System.out.println("--------------------");
                    System.out.println("    Settings");
                    for (String name : settings.keySet()) {
                        System.out.println(name + "=" + settings.get(name).toString());
                    }
                    break;
                case "reset":
                    this.reset();
                    System.out.println("--------------------");
                    System.out.println("  settings reseted");
                    System.out.println("--------------------");
                    break;
                case "quit":
                    System.out.println("--------------------");
                    System.out.println("    Bye Bye :)      ");
                    System.out.println("--------------------");
                    System.exit(-1);
                    break;
                default:

                    if (s.startsWith("answers")) {
                        this.answers(Integer.valueOf(s.substring(8)));
                        break;
                    } else if (s.startsWith("answer")) {
                        this.endGame();
                        System.out.println("--------------------");
                        System.out.println("answer for " + s.substring(7) + " is " + this.getResult(Integer.valueOf(s.substring(7))));
                        System.out.println("--------------------");
                        break;
                    } else if(s.startsWith("savesettings")){
                        System.out.println("--------------------");
                        System.out.println("saving to "+s.substring(13)+"/"+settings.get("settingsFileName"));
                        System.out.println("--------------------");
                        try {
                            this.writeSettings(s.substring(13));
                        } catch (IOException e) {
                            System.out.println("seems like your computer has bad handwriting (IO didn't work) - saving stopped");
                        } catch (XMLStreamException e) {
                            System.out.println("something went from with the XML-stream - saving stopped");
                        }
                        break;
                    } else if(s.startsWith("save")){
                        System.out.println("--------------------");
                        System.out.println("saving to "+s.substring(5)+"/"+settings.get("highscoreFileName"));
                        System.out.println("--------------------");
                        try {
                            this.writeHighscore(s.substring(5));
                        } catch (FileNotFoundException e) {
                            System.out.println("File not found - saving stopped");
                        } catch (XMLStreamException e) {
                            System.out.println("something went from with the XML-stream - saving stopped");
                        } catch (IOException e) {
                            System.out.println("seems like your computer has bad handwriting (IO didn't work) - saving stopped");
                        }
                        break;
                    }else if(s.startsWith("loadsettings")){
                        System.out.println("--------------------");
                        System.out.println("loading from "+s.substring(13));
                        System.out.println("--------------------");
                        try {
                            this.readSettings(s.substring(13));
                        } catch (IOException e) {
                            System.out.println("seems like your computer has bad handwriting (IO didn't work) - loading stopped");
                        } catch (XMLStreamException e) {
                            System.out.println("something went from with the XML-stream - loading stopped");
                        } catch (ClassNotFoundException e) {
                            System.out.println("there are no settings, maybe we should set sails to another file - loading stopped");
                        }
                        break;
                    }else if(s.startsWith("load")){
                        System.out.println("--------------------");
                        System.out.println("loading from "+s.substring(5));
                        System.out.println("--------------------");
                        try {
                            this.readHighscores(s.substring(5));
                        } catch (IOException e) {
                            System.out.println("seems like your computer has bad handwriting (IO didn't work) - loading stopped");
                        } catch (XMLStreamException e) {
                            System.out.println("something went from with the XML-stream - loading stopped");
                        } catch (ClassNotFoundException e) {
                            System.out.println("there is no map, maybe you can show us the way? - loading stopped");
                        }
                        break;
                    }else if(s.startsWith("set") && s.contains(" ")){
                        String arg = s.substring(4);
                        String name = "";
                        String value = "";
                        for(int i = 0; i < arg.length(); i++){
                            if(arg.charAt(i) == ' '){
                                name = arg.substring(0,i);
                                value = arg.substring(i+1);
                                break;
                            }
                        }
                        if(name == "" || value == ""){
                            System.out.println("we couldn't understand you, you will be redierected to the next free employee");
                        }else{
                            settings.put(name,value);
                            System.out.println("setting '"+name+"' was set to '"+value+"'");
                        }
                        break;
                    }

                    if(turn != -1) {
                        this.game(s);
                    }else{
                        this.menu();
                    }
                    break;
            }
        }
    }

    private int turn = -1;
    private long lastTurn = 0;
    private void game(String input){
        long ms = System.currentTimeMillis()-lastTurn;
        if(settings.get("timer") != "-1" && isNumeric(settings.get("timer").toString())){
            if (ms > Long.parseLong(settings.get("timer").toString())){
                System.out.println("time is up");
                this.endGame();
                return;
            }
        }
        //players turn
        turn++;
        if(!input.equalsIgnoreCase(this.getResult(turn))){
            this.endGame();
            return;
        }
        //programms turn
        turn++;
        System.out.println("    "+this.getResult(turn));
        lastTurn = System.currentTimeMillis();
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    private void endGame(){
        if(turn != -1) {

            String timer = "";
            if(settings.get("timer") != "-1" && isNumeric(settings.get("timer").toString())){
                timer = "with a timer of "+settings.get("timer")+"ms ";
            }
            this.hs.put(turn,settings.get("name")+" at turn " + turn+" " +timer+"on "+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
            System.out.println("--------------------");
            System.out.println("game finished in turn " + turn);
            System.out.println("--------------------");
            turn = -1;
        }
    }

    private String getResult(int zug){
        String re = "";
        if((zug % 3) == 0){
            re = "fizz";
        }
        if((zug % 5) == 0){
            re = re + "buzz";
        }
        if(re == ""){
            re = String.valueOf(zug);
        }
        return re;
    }


    private void answers(int end){
        System.out.println("--------------------");
        System.out.println("answers from 1 to "+end);
        System.out.println("--------------------");
        for(int i = 1; i <= end; i++){
            System.out.println("["+i+"] "+this.getResult(i));
        }
    }

    private void reset(){
        settings.clear();
        settings.put("name","noone");
        settings.put("highscoreFileName","highscores.xml");
        settings.put("settingsFileName","settings.xml");
        settings.put("timer","-1");
    }

    private void writeHighscore(String path) throws IOException, XMLStreamException {
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        FileOutputStream stream = new FileOutputStream(path +"/"+settings.get("highscoreFileName"));
        XMLEventWriter eventWriter = outputFactory.createXMLEventWriter(stream);

        XMLEventFactory eventFactory = XMLEventFactory.newInstance();
        XMLEvent end = eventFactory.createDTD("\n");

        StartDocument startDocument = eventFactory.createStartDocument();
        eventWriter.add(startDocument);

        StartElement configStartElement = eventFactory.createStartElement("", "", "highscores");
        eventWriter.add(configStartElement);
        eventWriter.add(end);

        writeMap(eventWriter);


        EndElement eElement = eventFactory.createEndElement("", "", "highscores");
        eventWriter.add(eElement);
        eventWriter.add(end);

        eventWriter.add(eventFactory.createEndDocument());
        eventWriter.close();
        stream.close();
    }

    private void writeMap(XMLEventWriter eventWriter) throws XMLStreamException, IOException {
        XMLEventFactory eventFactory = XMLEventFactory.newInstance();
        XMLEvent end = eventFactory.createDTD("\n");
        XMLEvent tab = eventFactory.createDTD("\t");

        StartElement sElement = eventFactory.createStartElement("", "", "map");
        eventWriter.add(tab);
        eventWriter.add(sElement);

        Characters characters = eventFactory.createCharacters(objectToString(hs));
        eventWriter.add(characters);

        EndElement eElement = eventFactory.createEndElement("", "", "map");
        eventWriter.add(eElement);
        eventWriter.add(end);
    }

    /** Read the object from Base64 string. */
    private static Object objectFromString(String s ) throws IOException, ClassNotFoundException {
        byte [] data = Base64.getDecoder().decode( s );
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
        Object o  = ois.readObject();
        ois.close();
        return o;
    }

    /** Write the object to a Base64 string. */
    private static String objectToString(Serializable o ) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(o);
        oos.close();
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    private void readHighscores(String path) throws IOException, XMLStreamException, ClassNotFoundException, ClassCastException{
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        InputStream in = new FileInputStream(path);
        XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
        while (eventReader.hasNext()) {
            XMLEvent event = eventReader.nextEvent();
            if (event.isStartElement()) {
                StartElement startElement = event.asStartElement();
                String elementName = startElement.getName().getLocalPart();
                if(elementName == "map"){
                    event = eventReader.nextEvent();
                    String s = event.asCharacters().getData();
                    System.out.println(s);
                    Object o = objectFromString(s);
                    HashMap<Integer,String> map = (HashMap<Integer, String>) o;
                    hs.putAll(map);



                }
            }
        }
        in.close();
    }

    private void writeSettings(String path) throws IOException, XMLStreamException {
        XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
        FileOutputStream stream = new FileOutputStream(path +"/"+settings.get("settingsFileName"));
        XMLEventWriter eventWriter = outputFactory.createXMLEventWriter(stream);

        XMLEventFactory eventFactory = XMLEventFactory.newInstance();
        XMLEvent end = eventFactory.createDTD("\n");

        StartDocument startDocument = eventFactory.createStartDocument();
        eventWriter.add(startDocument);

        StartElement configStartElement = eventFactory.createStartElement("", "", "settings");
        eventWriter.add(configStartElement);
        eventWriter.add(end);

        LinkedList<String> keys = new LinkedList<>(settings.keySet());
        for(int i = 0; i < keys.size(); i++){
            writeSetting(eventWriter, keys.get(i));
        }


        EndElement eElement = eventFactory.createEndElement("", "", "settings");
        eventWriter.add(eElement);
        eventWriter.add(end);

        eventWriter.add(eventFactory.createEndDocument());
        eventWriter.close();
        stream.close();
    }

    private void writeSetting(XMLEventWriter eventWriter,String name) throws XMLStreamException, IOException {
        XMLEventFactory eventFactory = XMLEventFactory.newInstance();
        XMLEvent end = eventFactory.createDTD("\n");
        XMLEvent tab = eventFactory.createDTD("\t");

        StartElement sElement = eventFactory.createStartElement("", "", name);
        eventWriter.add(tab);
        eventWriter.add(sElement);

        Characters characters = eventFactory.createCharacters((String) settings.get(name));
        eventWriter.add(characters);

        EndElement eElement = eventFactory.createEndElement("", "", name);
        eventWriter.add(eElement);
        eventWriter.add(end);
    }


    private void readSettings(String path) throws IOException, XMLStreamException, ClassNotFoundException, ClassCastException{
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        InputStream in = new FileInputStream(path);
        XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
        while (eventReader.hasNext()) {
            XMLEvent event = eventReader.nextEvent();
            if (event.isStartElement()) {
                StartElement startElement = event.asStartElement();
                String elementName = startElement.getName().getLocalPart();
                if(elementName != "settings") {
                    event = eventReader.nextEvent();
                    String s = event.asCharacters().getData();
                    settings.put(elementName, s);
                }
            }
        }
        in.close();
    }
}
