package primeau.bot;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.WritePendingException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PrimeauBot
{
    public static List<String> emailValid = new ArrayList<String>();
    public static List<String> liensVisité = new ArrayList<String>();


    public static void main( String[] args ) throws IOException
    {
        //Exemple: 3 http://jorisdeguet.github.io/420406-Applications/testbot/ /Users/emerycp/Desktop


        //Check si les arguments sont respectés
        checkNumberArguments(args.length);

        //Check Arguments
        checkProfondeur(args[0]);



        //Check Directory
        checkDirectory(args[2]);


        //Parcours les pages && CheckURL
        liensVisité.add(args[1]);
        int indexL = 0;
        int Longueur = 0;
        for (int profondeur = 0; profondeur < Integer.parseInt(args[0]); profondeur++)
        {
            int Difference = liensVisité.size() - Longueur;
            Longueur = liensVisité.size();
            for(int i = 0; i < Difference; i++)
            {
                String url = liensVisité.get(indexL);
                indexL++;
                parcourirPage(url, args[2]);
                liensVisité = checkA(liensVisité, indexL - 1);
            }
        }

        //Print Email
        System.out.println("Voici la liste des mails collectés en ordre:");
        for(String s : emailValid)
            System.out.println("->" + s);


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

    private static boolean checkURL(String unURL)
    {
        try{
            URL toFind = new URL(unURL);
            HttpURLConnection urlC = (HttpURLConnection) toFind.openConnection();
            urlC.getResponseCode();
        }
        catch (MalformedURLException e){
            System.out.println("Le URL est invalide - " + unURL);
            return false;
        }
        catch (IOException e)
        {
            System.out.println("La connection n'a pas aboutie - " + unURL);
            return false;
        }
        return true;
    }

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

    private static void parcourirPage(String pURL, String path){
        try{
            //Etablie la connection
            URL urlT = new URL(pURL);
            URLConnection urlC = urlT.openConnection();
            BufferedReader readURL = new BufferedReader(new InputStreamReader(
                    urlC.getInputStream()));

            String Line;

            //Cree le path des fichiers
            String chemin= path + urlT.getPath();
            File folderr = new File(chemin);
            folderr.getParentFile().mkdirs();


            //Regarde le nom du .html
            String fileName = "index.html";
            String[] urlName = chemin.split("/");
            if (urlName[urlName.length - 1].contains(".html"))
                fileName = urlName[urlName.length - 1];
            else if(urlName[urlName.length - 1].contains(""))
                fileName = "";

            FileWriter folder = new FileWriter(folderr.getParentFile() +"/"  + fileName);

            /////////////////////////////////////////////////////////////////////////////


            Pattern pattern = Pattern.compile("([A-z0-9_.-]+)@([A-z0-9_.-]+)\\.([a-z]+)");
            //Ecrit la ligne en remplacant par mon mail.
            while ((Line = readURL.readLine()) != null)
            {

                Matcher matcher = pattern.matcher(Line);

                if(matcher.find()){
                    if (!emailValid.contains(matcher.group()))
                        emailValid.add(matcher.group());
                    java.util.Collections.sort(emailValid, String.CASE_INSENSITIVE_ORDER);
                }
                folder.write(matcher.replaceAll("emeryc.primeau@gmail.com") + '\n');
            }
            folder.close();
            readURL.close();
            System.out.println("La page suivante a été explorée - " + pURL);

        }catch (MalformedURLException e)
        {
            System.out.println("L'url suivant est mal formé - " + pURL);
        }
        catch (IOException e)
        {
            System.out.println("L'url suivant ne se connecte pas - " + pURL);
        }

    }

    public static List<String> checkA (List<String> s, int i) throws IOException
    {

        //Regarde chacun des href dans les a.
        Document doc = Jsoup.connect(s.get(i)).get();
        Elements link = doc.select("a");
        for (int index = 0; index < link.size(); index++)
        {
            String absHref = link.get(index).attr("abs:href");

            if (absHref.isEmpty())
                absHref = link.get(index).attr("href");

            if (!s.contains(absHref))
            {
                if (checkURL(absHref))
                    s.add(absHref);
                else
                    System.out.println("Le URL est invalide - " + absHref);
            }
        }
        return s;
    }
}
