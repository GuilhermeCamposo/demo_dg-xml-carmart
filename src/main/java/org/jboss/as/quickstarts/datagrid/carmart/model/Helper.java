package org.jboss.as.quickstarts.datagrid.carmart.model;

import javax.inject.Singleton;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

@Singleton
public class Helper {

    private JAXBContext context;

    public Helper() throws JAXBException {
        context = JAXBContext.newInstance(Car.class);
    }

    public String marshal(Car car) throws JAXBException {

        StringWriter writer = new StringWriter();

        Marshaller mar= context.createMarshaller();
        mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        mar.marshal(car, writer);

        return writer.toString();
    }
    public Car unmarshall(String value) throws JAXBException {

        InputStream stream = new ByteArrayInputStream(value.getBytes(StandardCharsets.UTF_8));
        return (Car) context.createUnmarshaller().unmarshal(stream);
    }

}
