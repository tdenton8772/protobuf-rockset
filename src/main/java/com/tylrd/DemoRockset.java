package com.tylrd;

import com.rockset.client.RocksetClient;
import com.rockset.client.model.AddDocumentsRequest;
import com.rockset.client.model.AddDocumentsResponse;
import com.rockset.client.model.CreateCollectionRequest;
import com.rockset.client.model.CreateCollectionResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;

public class DemoRockset {
  private static Properties m_props;
  private static File propFile;
  public static AddDocumentsResponse documentsResponse;

  public static AddDocumentsResponse main() throws Exception {
    m_props = new Properties();
    propFile = new File("configuration.properties");

    try {
      LoadProperties(propFile);
    } catch (Exception e) {
      System.out.println(e);
    }

    RocksetClient rs =
        new RocksetClient(m_props.getProperty("api_key"), m_props.getProperty("api_server"));
    CreateCollectionRequest request =
        new CreateCollectionRequest().name(m_props.getProperty("collection"));
    LinkedList<Object> list = new LinkedList<>();
    Map<String, Object> json = new LinkedHashMap<>();
    json.put("name", "foo");
    json.put("address", "bar");
    list.add(json);

    AddDocumentsRequest documentsRequest = new AddDocumentsRequest().data(list);
    CreateCollectionResponse response =
        rs.collections.create(m_props.getProperty("workspace"), request);
    documentsResponse =
        rs.documents.add(
            m_props.getProperty("workspace"), response.getData().getName(), documentsRequest);
    return documentsResponse;
  }

  public static void LoadProperties(File f) throws IOException {
    FileInputStream propStream = null;
    propStream = new FileInputStream(f);
    m_props.load(propStream);
    propStream.close();
  }
}
