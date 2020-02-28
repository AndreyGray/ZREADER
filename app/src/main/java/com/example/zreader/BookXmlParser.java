package com.example.zreader;

import com.example.zreader.model.Book;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
/*



This class uses as  XML parser and creator to list books.



 */
public class BookXmlParser {

    private ArrayList<Book> books;

    public BookXmlParser() {
        this.books = new ArrayList<>();
    }

    public ArrayList<Book> getBooks() {
        return books;
    }
    public  boolean parse(String xmlData){
        boolean status = true;
        Book currentBook = null;
        boolean inEntry = false;
        String textValue = "";

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xmlPullParser = factory.newPullParser();

            xmlPullParser.setInput(new StringReader(xmlData));
            int eventType =xmlPullParser.getEventType();
            while (eventType!= XmlPullParser.END_DOCUMENT){

                String tagName = xmlPullParser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if ("book".equalsIgnoreCase(tagName)) {
                            inEntry = true;
                            currentBook = new Book();
                        }
                        break;
                    case XmlPullParser.TEXT:
                        textValue = xmlPullParser.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        if (inEntry) {
                            if ("book".equalsIgnoreCase(tagName)) {
                                books.add(currentBook);
                                inEntry = false;
                            } else if ("ID".equalsIgnoreCase(tagName)) {
                                currentBook.setID(textValue);
                            } else if ("title".equalsIgnoreCase(tagName)) {
                                currentBook.setTitle(textValue);
                            } else if ("thumbnail".equalsIgnoreCase(tagName)) {
                                currentBook.setThumbnail(textValue);
                            } else if ("new".equalsIgnoreCase(tagName)) {
                                currentBook.setNew(textValue);
                            } else if ("thumb_ext".equalsIgnoreCase(tagName)) {
                                currentBook.setThumb_ext(textValue);
                            }
                        }
                        break;
                    default:
                }
                eventType = xmlPullParser.next();
                }
            } catch (XmlPullParserException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return status;
    }
}
