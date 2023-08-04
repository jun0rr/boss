/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.test;

import com.jun0rr.boss.Stored;
import com.jun0rr.boss.config.BufferConfig;
import com.jun0rr.boss.config.VolumeConfig;
import com.jun0rr.boss.json.JsonStore;
import com.jun0rr.boss.store.DefaultJsonStore;
import io.vertx.core.json.JsonObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;


/**
 *
 * @author F6036477
 */
public class TestJsonStore {
  
  @Test public void test() throws IOException {
    Path p = Paths.get("./TestJsonStore.db").toAbsolutePath();
    System.out.printf("=> %s%n", p);
    Map<String,String> mb = new HashMap();
    mb.put("size", "1K");
    mb.put("max_cache_size", "4G");
    mb.put("type", "direct");
    VolumeConfig vc = new VolumeConfig("TestJsonStore", BufferConfig.from(mb), p);
    JsonStore store = new DefaultJsonStore(vc);
    try { //(JsonStore store = new DefaultJsonStore(vc)) {
      List<JsonObject> ls = new LinkedList();
      for(int i = 0; i < 20; i++) {
        ls.add(new JsonObject()
            .put("name", "John" + (i + 100))
            .put("age", 15 + i)
            .put("address", new JsonObject()
                .put("street", (10 + i) + " Avenue")
                .put("number", 200 + i)));
      }
      ls.stream()
          .map(j->store.store("person", j))
          .forEach(s->System.out.printf("=> %s%n", s));
      //{"name":"John105","age":20,"address":"Street 205"}
      Assertions.assertEquals(ls.get(5), store.find("person", j->j.getInteger("age") == 20).findFirst().get().object());
      store.createIndex("person", "age", j->j.getInteger("age"));
      store.createIndex("person", "address.number", j->j.getJsonObject("address").getInteger("number"));
      Stored<JsonObject> s = store.find("person", "age", 20).findFirst().get();
      System.out.printf("=> find(\"person\", \"age\", 20): %s%n", s);
      Assertions.assertEquals(ls.get(5), s.object());
      store.delete(s.id());
      Stored<JsonObject> ss = store.find("person", "address.number", 214).findFirst().get();
      System.out.printf("=> find(\"person\", \"address.number\", 214): %s%n", ss);
      JsonObject jo = ss.object();
      jo.getJsonObject("address").put("number", 50);
      ss = store.update(ss.id(), jo);
      System.out.printf("=> store.update: %s%n", ss);
      store.close();
      JsonStore st = new DefaultJsonStore(vc);
      Optional<Stored<JsonObject>> opt = st.find("person", "age", 20).findFirst();
      Assertions.assertTrue(opt.isEmpty());
      ss = st.find("person", "address.number", 50).findFirst().get();
      Assertions.assertEquals(ls.get(14).getString("name"), ss.object().getString("name"));
    }
    catch(Throwable th) {
      th.printStackTrace();
    }
    finally {
      Files.delete(p);
    }
  }
  
}
