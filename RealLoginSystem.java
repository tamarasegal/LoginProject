import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

/*
Utilizes a File and FileWriter but doesn't continuously update it,
instead storing locally in an ArrayList and updating at the end.
Keeps data from previous runs as long as session is ended by logging out.
 */
public class RealLoginSystem {
    private static String hashingScheme = "MD5";

    public static void run() throws IOException {
        File f = new File("info.txt");
        Scanner input = new Scanner(System.in);
        Scanner scan = null;
        FileWriter fw = null;
        ArrayList<ArrayList<String>> accounts = new ArrayList<>();
        boolean done = false;
        try {
            scan = new Scanner(f);
            //loading existing accounts from file
            while (scan.hasNext()) {
                ArrayList<String> list = new ArrayList(Arrays.asList(scan.nextLine().split(":")));
                accounts.add(list);
            }
            fw = new FileWriter(f);
        }
        catch (IOException e) {
            System.out.println("File could not be found. Exiting.");
            System.exit(1);
        }

        while (!done) {
            //prompt action
            System.out.println("Create an account (1), log into existing account(2), change password(3), or log out(4)");
            String action = input.nextLine();

            switch (action) {

                //create account
                case "1": {
                    //prompting for name
                    ArrayList<String> newAccount = new ArrayList<>();
                    System.out.println("Please enter your name");
                    newAccount.add(input.nextLine());

                    //prompting for userID
                    System.out.println("Please enter your desired user ID");
                    newAccount.add(input.nextLine());

                    //creating the salt
                    String salt = "";
                    String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890!@#$%^&*()";
                    Random r = new Random();
                    for (int i = 0; i < 5; i++) {
                        salt += chars.charAt(r.nextInt(chars.length()));
                    }

                    //prompting for password and hashing
                    System.out.println("Please enter your desired password");
                    newAccount.add(hash(salt, input.nextLine()));

                    //filing the information
                    newAccount.add(salt);
                    newAccount.add(hashingScheme);
                    accounts.add(newAccount);
                    System.out.println("Success! Account Created");
                    break;
                }

                case "2" : {
                    //prompting for user ID
                    System.out.println("Please enter your user ID");
                    String userID = input.nextLine();
                    ArrayList<String> info = null;
                    //finding user information in file
                    for (int i = 0; i < accounts.size(); i++) {
                        if (accounts.get(i).get(1).equals(userID))
                            info = accounts.get(i);
                    }

                    //if user info is not found: breaks out of switch statement
                    if (info == null) {
                        System.out.println("User ID not found. Please try again.");
                        break;
                    }
                    //prompting the password
                    String salt = info.get(3);
                    String storedPassword = info.get(2);
                    System.out.println("Please enter your password");
                    String enteredPassword = hash(salt, input.nextLine());
                    if (storedPassword.equals(enteredPassword))
                        System.out.println("Login successful!");
                    else
                        System.out.println("Incorrect password");
                    break;
                }

                case "3": {
                    //prompting for user I
                    System.out.println("Please enter your user ID");
                    String userID = input.nextLine();
                    ArrayList<String> info = null;
                    //finding user information in file
                    for (int i = 0; i < accounts.size(); i++) {
                        if (accounts.get(i).get(1).equals(userID))
                            info = accounts.get(i);
                    }

                    //if user info is not found: breaks out of switch statement
                    if (info == null) {
                        System.out.println("User ID not found. Please try again.");
                        break;
                    }
                    //prompting the password
                    String salt = info.get(3);
                    String storedPassword = info.get(2);
                    System.out.println("Please enter your password");
                    String enteredPassword = hash(salt, input.nextLine());
                    if (!storedPassword.equals(enteredPassword)) {
                        System.out.println("Incorrect password");
                        break;
                    }
                    System.out.println("Please enter your new password");
                    info.set(2, hash(info.get(3), input.nextLine()));
                    System.out.println("Password successfully changed!");
                    break;

                }

                case "4": {
                    for (ArrayList<String> a : accounts) {
                        String line = "";
                        for (String s : a) {
                            line += s + ":";
                        }
                        fw.write(line.substring(0, line.length()-1) + System.getProperty("line.separator"));
                    }
                    fw.close();
                    System.out.println("Log out successful");
                    done = true;
                }
            }
        }

    }
    public static String hash(String salt, String password) {
        try {
            MessageDigest md = MessageDigest.getInstance(hashingScheme);
            byte[] bytes = md.digest((salt + password).getBytes(StandardCharsets.UTF_8));
            return new String(bytes);
        }

        catch (NoSuchAlgorithmException e) {
            System.out.println(hashingScheme + " is invalid");
            return null;
        }

    }
}