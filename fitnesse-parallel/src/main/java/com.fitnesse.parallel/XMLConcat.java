package com.fitnesse.parallel;

import org.apache.commons.io.comparator.LastModifiedFileComparator;

import javax.xml.stream.*;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/* needed two argument
 * args[0]= location of result dir ex:/Users/git/new/fitnesse-framework/FitNesseRoot/files/testResults
 * args[1]= env
 * */

 public class XMLConcat {
    public static void main(String[] args) throws Throwable {

        ArrayList xmlfiles=null;
        XMLConcat test = new XMLConcat();
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        File files = new File(s+"/merge/mergedFile.xml");
        if (!files.exists()) {
            files.delete();
        }
        File dir = new File(args[0]);
        String env = args[1];

        List<File> allTests= test.listf(dir.getAbsolutePath().toString(),env);
        System.out.println(allTests.size());


        Path path = Paths.get(s+"/merge");
        Files.createDirectories(path);
        //System.out.println("path: "+s);
        Writer outputWriter = new FileWriter(s+"/merge/mergedFile.xml");

        XMLOutputFactory xmlOutFactory = XMLOutputFactory.newFactory();
        XMLEventWriter xmlEventWriter = xmlOutFactory.createXMLEventWriter(outputWriter);
        XMLEventFactory xmlEventFactory = XMLEventFactory.newFactory();

        xmlEventWriter.add(xmlEventFactory.createStartDocument());
        xmlEventWriter.add(xmlEventFactory.createStartElement("", null, "reports"));

        XMLInputFactory xmlInFactory = XMLInputFactory.newFactory();
        for (File rootFile : allTests) {
            //System.out.println("yogesh: "+rootFile);
            String[] resultDate=rootFile.getName().split("_");

            Path p= Paths.get(rootFile.getAbsolutePath());
          Map<String,String> elementsMap = new HashMap<String, String>();
          elementsMap.put("PageHistoryLink", p.getParent().getFileName()+"?pageHistory&resultDate="+resultDate[0]);
            //System.out.println(p.getParent().getFileName()+"?pageHistory&resultDate="+resultDate[0]);
            Set<String> elementNodes = elementsMap.keySet();

            XMLEventReader xmlEventReader = xmlInFactory.createXMLEventReader(new StreamSource(rootFile));
            XMLEvent event = xmlEventReader.nextEvent();
            // Skip ahead in the input to the opening document element
            while (event.getEventType() != XMLEvent.START_ELEMENT) {
                event = xmlEventReader.nextEvent();
            }
            do {
                xmlEventWriter.add(event);
                event = xmlEventReader.nextEvent();
                if(event.toString().contains("<instructions>")){

                        XMLEvent end = xmlEventFactory.createDTD("\n");
                        XMLEvent tab = xmlEventFactory.createDTD("\t");
                        //Create Start node
                        StartElement sElement = xmlEventFactory.createStartElement("", "", "pageHistoryLink");
                        xmlEventWriter.add(tab);
                        xmlEventWriter.add(sElement);
                        //Create Content
                        Characters characters = xmlEventFactory.createCharacters(p.getParent().getFileName()+"?pageHistory&resultDate="+resultDate[0]);
                        xmlEventWriter.add(characters);
                        // Create End node
                        EndElement eElement = xmlEventFactory.createEndElement("", "", "pageHistoryLink");
                        xmlEventWriter.add(eElement);
                        xmlEventWriter.add(end);

                }
            } while (event.getEventType() != XMLEvent.END_DOCUMENT);

            xmlEventReader.close();
        }

        xmlEventWriter.add(xmlEventFactory.createEndElement("", null, "reports"));
        xmlEventWriter.add(xmlEventFactory.createEndDocument());

        xmlEventWriter.close();
        outputWriter.close();
    }

    public static List<File> listf(String directoryName, String env) {
        File directory = new File(directoryName);
        File theNewestFile = null;
        List<File> resultList = new ArrayList<File>();

        // get all the files from a directory
       // System.out.println("directory: "+directoryName);

            File[] fList = directory.listFiles();
       // System.out.println("file: "+fList[0]);
            ArrayList<File> filess = new ArrayList<File>();
            if (fList.length > 0) {
                Arrays.sort(fList, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
                theNewestFile = fList[0];

                if (theNewestFile.isFile()) {

                    if(theNewestFile.getAbsolutePath().contains(env)) {
                        filess.add(theNewestFile);
                    }

                }

            }
            resultList.addAll(filess);
            for (File file : fList) {
                if (file.isFile()) {
                    //System.out.println(file.getAbsolutePath());
                } else if (file.isDirectory()) {
                    resultList.addAll(listf(file.getAbsolutePath(), env));
                }
            }

        //System.out.println(fList);
        return resultList;
    }

}
