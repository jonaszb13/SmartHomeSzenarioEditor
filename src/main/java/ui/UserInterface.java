package ui;

public class UserInterface {


    //private static boolean gui = true;

    //public static void setGui(boolean gui) {UserInterface.gui = gui;}

    public static void out(Message message) {
    //    if (gui) {
    //        ui.GUI.GraphicalUserInterface.start();
    //    } else {
            ui.ConsoleLineInterface.print(message);
    //    }
    }

    //public static String input() {
    //    if (gui) {
    //        //TODO GUI Anbinden
    //        return "";
    //    } else {
    //        return UI.CLI.ConsoleLineInterface.consoleListener();
    //    }
    //}
}
