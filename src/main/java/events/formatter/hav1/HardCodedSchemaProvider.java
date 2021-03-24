package events.formatter.hav1;

import events.formatter.IProvideSchema;

public class HardCodedSchemaProvider implements IProvideSchema {

  public String get() {
    return "{\n"
        + "    \"type\" : \"record\",\n"
        + "    \"name\" : \"hav1\",\n"
        + "    \"namespace\" : \"com.worldpay.poc\",\n"
        + "    \"fields\" : [{\"name\" : \"id\", \n"
        + "                \"type\" : \"string\", \n"
        + "                \"logicalType\" : \"uuid\"},\n"
        + "\n"
        + "\t\t\t\t{\"name\" : \"name\", \n"
        + "                \"type\" : \"string\", \n"
        + "                \"default\" : \"NONE\"},\t\t\t\t\n"
        + "\n"
        + "                {\"name\" : \"payload\", \n"
        + "                \"type\" : {\n"
        + "\t\t\t\t\t\"type\": \"map\",\n"
        + "\t\t\t\t\t\"values\": \"string\" }\n"
        + "\t\t\t\t},\n"
        + "\n"
        + "                {\"name\" : \"category\", \n"
        + "                \"type\" : \"string\" \n"
        + "\t\t\t\t},\n"
        + "\n"
        + "                {\"name\" : \"occurred_at\", \n"
        + "                \"type\" : \"string\"},\n"
        + "\n"
        + "                {\"name\" : \"version\", \n"
        + "                \"type\" : \"int\"}]\n"
        + "} ";
  }
}
