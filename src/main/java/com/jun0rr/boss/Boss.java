/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss;

import com.jun0rr.binj.mapping.AnnotationConstructStrategy;
import com.jun0rr.binj.mapping.AnnotationGetStrategy;
import com.jun0rr.binj.mapping.AnnotationSetStrategy;
import com.jun0rr.binj.mapping.DefaultConstructStrategy;
import com.jun0rr.binj.mapping.FieldGetStrategy;
import com.jun0rr.binj.mapping.FieldMethodGetStrategy;
import com.jun0rr.binj.mapping.FieldMethodSetStrategy;
import com.jun0rr.binj.mapping.FieldSetStrategy;
import com.jun0rr.binj.mapping.FieldsOrderConstructStrategy;
import com.jun0rr.binj.mapping.GetterMethodStrategy;
import com.jun0rr.binj.mapping.SetterMethodStrategy;
import com.jun0rr.boss.config.BossConfig;
import com.jun0rr.boss.config.BufferConfig;
import com.jun0rr.boss.store.DefaultObjectStore;
import java.nio.file.Path;

/**
 *
 * @author F6036477
 */
public class Boss {
  
  public static ObjectStore memoryObjectStore() {
    return objectStore(
        setDefaultStrategies(BossConfig.builder())
        .setBufferType(BufferConfig.Type.DIRECT)
        .setBufferSize(1024)
        .setMaxCacheSize(2L*1024*1024*1024)
        .setVolumeId("DefaultMemoryObjectStore")
        .build());
  }
  
  public static ObjectStore fileObjectStore(Path filepath) {
    return objectStore(
        setDefaultStrategies(BossConfig.builder())
        .setBufferType(BufferConfig.Type.DIRECT)
        .setBufferSize(1024)
        .setMaxCacheSize(2L*1024*1024*1024)
        .setVolumeId("DefaultFileObjectStore")
        .setVolumeStorePath(filepath)
        .build());
  }
  
  public static ObjectStore objectStore(BossConfig cfg) {
    return new DefaultObjectStore(cfg);
  }
  
  private static BossConfig.Builder setDefaultStrategies(BossConfig.Builder builder) {
    return builder.addConstructStrategy(new FieldsOrderConstructStrategy())
        .addConstructStrategy(new AnnotationConstructStrategy())
        .addConstructStrategy(new DefaultConstructStrategy())
        .addExtractStrategy(new FieldMethodGetStrategy())
        .addExtractStrategy(new AnnotationGetStrategy())
        .addExtractStrategy(new GetterMethodStrategy())
        .addExtractStrategy(new FieldGetStrategy())
        .addInjectStrategy(new FieldMethodSetStrategy())
        .addInjectStrategy(new AnnotationSetStrategy())
        .addInjectStrategy(new SetterMethodStrategy())
        .addInjectStrategy(new FieldSetStrategy());
  }
  
}
