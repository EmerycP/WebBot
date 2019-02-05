package primeau.bot;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.WritePendingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PrimeauBot
{
    public static List<String> emailValid = new ArrayList<String>();

    public static void main( String[] args )
    {
        //Exemple: 3 http://jorisdeguet.github.io/420406-Applications/testbot/ /Users/emerycp/Desktop


        //Check si les arguments sont respectés
        checkNumberArguments(args.length);

        //Check Arguments
        checkProfondeur(args[0]);

        //Check URL
        //checkURL(args[1]);

        //Check Directory
        checkDirectory(args[2]);

        //Save pages
        savePage(args[1], args[2]);

    }

    private static void checkNumberArguments(int nombre)
    {
        if (nombre != 3)
        {
            System.out.println("Il n'y a pas trois arguments d'entrés");
            System.out.println("Vous devez entrer la profondeur de l'exploration en étant un nombre entier positif.");
            System.out.println("Ensuite, l'URL de départ sous un format correct et une connection possible à celle-ci.");
            System.out.println("Finalement, l'endroit où écrire la copie des fichiers explorer. En s'assurant qu'il est accessible et possible d'écrire.");
            System.exit(0);
        }
    }

    private static void checkProfondeur(String profondeur)
    {
        try{
            Integer test = Integer.parseInt(profondeur);
            if (test < 0 || test > 999)
            {
                System.out.println("Veuillez entrer un nombre entre 0 et 999");
                System.exit(0);
            }
        }
        catch (Exception e)
        {
            System.out.println("Impossible de convertir le nombre entré.");
            System.exit(0);
        }


    }

//    private static void checkURL(String unURL)
//    {
//        try{
//            URL toFind = new URL(unURL);
//            HttpURLConnection urlC = (HttpURLConnection) toFind.openConnection();
//            urlC.getResponseCode();
//        }
//        catch (MalformedURLException e){
//            System.out.println("Le URL est invalide");
//            System.exit(0);
//        }
//        catch (IOException e)
//        {
//            System.out.println("La connection n'a pas aboutie");
//            System.exit(0);
//        }
//
//    }

    private static void checkDirectory(String path)
    {

        File chemin = new File(path);
        File ecritureFichier = new File(chemin+"/test.txt");


        if (!chemin.isDirectory())
        {
            System.out.println("Le dossier spécifié n'en est pas un.");
            System.exit(0);
        }

        //Si le fichier existe
        if (!chemin.exists())
        {
            System.out.println("Le dossier écrit n'existe pas");
            System.exit(0);
        }

        //Essaie d'ecrire
        try {
            ecritureFichier.createNewFile();
            ecritureFichier.delete();
        } catch (Exception e)
        {
            System.out.println("Impossible d'écrire sur le dossier spécifié");
        }

    }

    private static void savePage(String pURL, String path){
        try{

            URL urlT = new URL(pURL);
            URLConnection urlC = urlT.openConnection();
            BufferedReader readURL = new BufferedReader(new InputStreamReader(
                    urlC.getInputStream()));

            String Line;
            String fileName = "index.html";

            //Get le path et cree le dossier
            String[] urlName = urlT.getPath().split(File.separator);

            if (urlName[urlName.length - 1].contains(".html"))
                fileName = urlName[urlName.length - 1];

            FileWriter folder = new FileWriter(path + "/" + fileName);

            //Ecrit la ligne
            while ((Line = readURL.readLine()) != null)
            {
                folder.write(Line + '\n');
                if(Line.contains("@") && extraire(Line) != null){
                    emailValid.add(extraire(Line));
                    java.util.Collections.sort(emailValid);
                }
            }


            folder.close();
            readURL.close();
            System.out.println("La page suivante a été explorée et sauvegardée - " + pURL);
            System.out.println("Voici la liste des mails collectés en ordre");
            System.out.println(emailValid);


        }catch (MalformedURLException e)
        {
            System.out.println("L'url suivant est mal formé - " + pURL);
        }
        catch (IOException e)
        {
            System.out.println("L'url suivant ne se connecte pas - " + pURL);
        }

    }
    public static String extraire(String s){
        Scanner scanner = new Scanner(s);
        String input = scanner.nextLine();

        Pattern pattern = Pattern.compile("([a-z0-9_.-]+)@([a-z0-9_.-]+)\\.([a-z]+)");
        Matcher matcher = pattern.matcher(input);

        while(matcher.find()){
            return matcher.group();
        }
        return null;
    }
}
