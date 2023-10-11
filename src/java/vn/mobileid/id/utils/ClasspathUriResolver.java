/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package vn.mobileid.id.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

public class ClasspathUriResolver implements URIResolver {

  @Override
  public Source resolve(String href, String base) throws TransformerException {
      System.out.println("Href:"+href);
      System.out.println("Base:"+base);
    Source source = null;
    InputStream inputStream = getClass().getResourceAsStream("resources/hello.bmp");
    if (inputStream != null) {
      source = new StreamSource(inputStream);
    }
    return source;
  }   
}
