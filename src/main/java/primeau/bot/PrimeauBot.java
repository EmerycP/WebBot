package primeau.bot;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.WritePendingException;

public class PrimeauBot
{
    public static void main( String[] args )
    {

        //Check si les arguments sont respectés
        checkNumberArguments(args.length);

        //Check Arguments
        checkProfondeur(args[0]);

        //Check URL
        checkURL(args[1]);

        //Check Directory
        checkDirectory(args[2]);
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
        Integer test = Integer.parseInt(profondeur);
        if (test < 0 || test > 999)
        {
            System.out.println("Veuillez entrer un nombre entre 0 et 999");
            System.exit(0);
        }

    }

    private static void checkURL(String unURL)
    {
        try{
            URL toFind = new URL(unURL);
            HttpURLConnection urlC = (HttpURLConnection) toFind.openConnection();
            urlC.getResponseCode();
        }
        catch (MalformedURLException e){
            System.out.println("Le URL est invalide");
            System.exit(0);
        }
        catch (IOException e)
        {
            System.out.println("La connection n'a pas aboutie");
            System.exit(0);
        }

    }
    private static void checkDirectory(String path)
    {

        File chemin = new File(path);
        File ecritureFichier = new File(chemin+"/log.txt");


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
}
