import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.mongodb.*;
import com.sun.javafx.collections.MappingChange;
import org.bson.types.ObjectId;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sina on 11/17/14.
 */
public class STTScraper {

    MongoClient mongoClient;

    public STTScraper(String url) throws UnknownHostException{
            //mongoClient = new MongoClient();
            //System.out.println("Mongo Connection Successful");
            scrapeTopic(url);
    }

    /*
    public void migrateDB(){
        DB db = mongoClient.getDB("stt");
        DBCollection foodRecipes = db.getCollection("recipe0");
    }

    public void removeItem(String name){
        DB db = mongoClient.getDB("stt");
        DBCollection foodRecipes = db.getCollection("recipe0");
        BasicDBObject searchQuery = new BasicDBObject();
        searchQuery.put("name",name);
        foodRecipes.remove(foodRecipes.findOne(searchQuery));
    }

    public void initDoc(){
        int
    }
    */

    public static void scrapeTopic(String url){
        String html = getUrl(url);
        Document doc = Jsoup.parse(html);
        //String contentText = doc.select("head").first().text();
        String contentText = doc.getAllElements().toString();
        //System.out.println(contentText);
        Document doc2 = Jsoup.parse(contentText);
        Element div = doc2.select("head").first();
        int i = 0;

        for(Node node : div.childNodes()){
            i++;
                /*System.out.println(String.format("%d %s",
                        i,
                        node.toString()))*/
            if(node.toString().contains("meta") && node.toString().contains("title")){

            };
        }
        Document doc3 = Jsoup.parse(contentText);
        Elements links = doc3.select("div[class = bd-full]").first().select("ul.list").select("li").select("a[href]");
        ArrayList<String> urls = new ArrayList<String>();
        for(Element recipeLink: links){
            String recipeURL = recipeLink.attr("abs:href").toString();
            urls.add(recipeURL);
        }

        // Testing mechanism that prints
        /*for(String rURL: urls){
            System.out.println(rURL);
        }*/

        System.out.println(urls.get(0));
        String html2 = getUrl(urls.get(0));
        Document doc4 = Jsoup.parse(html2);
        String contentText2 = doc4.getAllElements().toString();
        String nameOfRecipe = doc4.select("span.item").select("h1.fn").text().toString();
        System.out.println("Recipe: " + nameOfRecipe); // Gets just the name of the recipe

        Elements ingredientComponents = doc4.select("div.pod.ingredients").select("ul").select("li");
        for(Element component: ingredientComponents){
            String nameOfComponent = component.select("span.name").text().toString();
            String amountOfComponent = component.select("span.value").text().toString();
            String measurementUnitOfComponent = component.select("span.type").text().toString();
            System.out.println("Name: " + nameOfComponent);
            System.out.println("Amount: " + amountOfComponent);
            if(measurementUnitOfComponent.length()!= 0 && measurementUnitOfComponent.charAt(0) == '(')
                System.out.println("UnitOfMeasurement: " + measurementUnitOfComponent.substring(measurementUnitOfComponent.indexOf("(")+1,measurementUnitOfComponent.lastIndexOf(")")));
            else System.out.println("UnitOfMeasurement: " + measurementUnitOfComponent);
        }

        String servingSize = doc4.select("div.hd.clrfix").select("div").select("p").select("span").text().toString();
        String pattern = "\\(([^)]+)\\)";
        Pattern regex = Pattern.compile(pattern);
        Matcher m = regex.matcher(servingSize);
        while(m.find()) servingSize = m.group(1);
        pattern = "-?\\d+";
        regex = Pattern.compile(pattern);
        m = regex.matcher(servingSize);
        while(m.find()) servingSize = m.group();
        System.out.println("Serving size: " + servingSize);

        String numberOfServings = doc4.select("div.hd.clrfix").select("div").select("p").get(1).text().toString();
        pattern = "-?\\d+";
        regex = Pattern.compile(pattern);
        m = regex.matcher(numberOfServings);
        while(m.find()) numberOfServings = m.group();
        System.out.println("Number of Servings: " + numberOfServings);

        String calories = doc4.select("dt.cals.nutrition").select("span.calories").text().toString();
        System.out.println("Calories: " + calories);

        String fatCalories = doc4.select("dt.fat-cals.nutrition").text().toString();
        m = regex.matcher(fatCalories);
        while(m.find()) fatCalories = m.group();
        System.out.println("Fat Calories: " + fatCalories);

        String percentFatCalories = doc4.select("dd.fat-cals").text().toString();
        pattern = "-?\\d+";
        regex = Pattern.compile(pattern);
        m = regex.matcher(percentFatCalories);
        while(m.find()) percentFatCalories = m.group();
        System.out.println("PercentFatCalories: " + percentFatCalories);

        String totalFat = doc4.select("dt.sub.nutrition").first().select("span[itemprop=fatContent]").text().toString();
        System.out.println("Total Fat: " + totalFat);

        String percentTotalFat = doc4.select("div.bd.clrfix").select("dl").select("dd.sub").first().text().toString();
        pattern = "-?\\d+";
        regex = Pattern.compile(pattern);
        m = regex.matcher(percentTotalFat);
        while(m.find()) percentTotalFat = m.group();
        System.out.println("PercentTotalFat: " + percentTotalFat);

        String saturatedFat = doc4.select("dt.sub.nutrition").get(1).select("span[itemprop=saturatedfatContent]").text().toString();
        System.out.println("Saturated Fat: " + saturatedFat);

        String percentSaturatedFat = doc4.select("div.bd.clrfix").select("dd.sub").get(0).text().toString();
        pattern = "-?\\d+";
        regex = Pattern.compile(pattern);
        m = regex.matcher(percentSaturatedFat);
        while(m.find()) percentSaturatedFat = m.group();
        System.out.println("PercentSaturatedFat: " + percentSaturatedFat);

        String cholesterol = doc4.select("dt.nutrition").select("span[itemprop=cholesterolContent]").text().toString();
        System.out.println("Cholesterol: " + cholesterol);

        String percentCholesterol = doc4.select("div.bd.clrfix").select("dl").select("dd").get(5).text().toString();
        pattern = "-?\\d+";
        regex = Pattern.compile(pattern);
        m = regex.matcher(percentCholesterol);
        while(m.find()) percentCholesterol = m.group();
        System.out.println("PercentCholesterol: " + percentCholesterol);

        String sodium = doc4.select("dt.nutrition").select("span[itemprop=sodiumContent]").text().toString();
        System.out.println("Sodium: " + sodium);

        String percentSodium = doc4.select("div.bd.clrfix").select("dl").select("dd").get(6).text().toString();
        pattern = "-?\\d+";
        regex = Pattern.compile(pattern);
        m = regex.matcher(percentSodium);
        while(m.find()) percentSodium = m.group();
        System.out.println("percentSodium: " + percentSodium);

        String totalCarbohydrate = doc4.select("dt.nutrition").select("span[itemprop=carbohydrateContent]").text().toString();
        System.out.println("TotalCarbohydrate: " + totalCarbohydrate);

        String percentTotalCarbohydrate = doc4.select("div.bd.clrfix").select("dl").select("dd").get(7).text().toString();
        pattern = "-?\\d+";
        regex = Pattern.compile(pattern);
        m = regex.matcher(percentTotalCarbohydrate);
        while(m.find()) percentTotalCarbohydrate = m.group();
        System.out.println("percentTotalCarbohydrate: " + percentTotalCarbohydrate);

        String dietaryFiber = doc4.select("dt.nutrition").select("span[itemprop=fiberContent]").text().toString();
        System.out.println("dietaryFiber: " + dietaryFiber);

        String percentDietaryFiber = doc4.select("div.bd.clrfix").select("dl").select("dd").get(8).text().toString();
        pattern = "-?\\d+";
        regex = Pattern.compile(pattern);
        m = regex.matcher(percentDietaryFiber);
        while(m.find()) percentDietaryFiber = m.group();
        System.out.println("percentDietaryFiber: " + percentDietaryFiber);

        String sugars = doc4.select("dt.nutrition").select("span[itemprop=sugarContent]").text().toString();
        System.out.println("Sugars: " + sugars);

        String percentSugar = doc4.select("div.bd.clrfix").select("dl").select("dd").get(9).text().toString();
        pattern = "-?\\d+";
        regex = Pattern.compile(pattern);
        m = regex.matcher(percentSugar);
        while(m.find()) percentSugar = m.group();
        System.out.println("Percent Sugar: " + percentSugar);

        String protein = doc4.select("dt.nutrition").select("span[itemprop=proteinContent]").text().toString();
        System.out.println("Protein: " + protein);

        String percentProtein = doc4.select("div.bd.clrfix").select("dl").select("dd").get(10).text().toString();
        pattern = "-?\\d+";
        regex = Pattern.compile(pattern);
        m = regex.matcher(percentProtein);
        while(m.find()) percentProtein = m.group();
        System.out.println("Percent Protein: " + percentProtein);
    }

    public static String getUrl(String url){
        URL urlObj = null;
        try{
            urlObj = new URL(url);
        }
        catch(MalformedURLException e){
            System.out.println("The url was malformed!");
            return "";
        }
        URLConnection urlCon = null;
        BufferedReader in = null;
        String outputText = "";
        try{
            urlCon = urlObj.openConnection();
            in = new BufferedReader(new
                    InputStreamReader(urlCon.getInputStream()));
            String line = "";
            while((line = in.readLine()) != null){
                outputText += line;
            }
            in.close();
        }catch(IOException e){
            System.out.println("There was an error connecting to the URL");
            return "";
        }
        return outputText;
    }
}
