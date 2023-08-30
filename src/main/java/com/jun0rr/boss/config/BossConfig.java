/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.jun0rr.boss.config;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.jun0rr.binj.BinCodec;
import com.jun0rr.binj.BinContext;
import com.jun0rr.binj.mapping.CombinedStrategy;
import com.jun0rr.binj.mapping.ConstructFunction;
import com.jun0rr.binj.mapping.ExtractFunction;
import com.jun0rr.binj.mapping.InjectFunction;
import com.jun0rr.binj.mapping.InvokeStrategy;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author F6036477
 */
public record BossConfig(VolumeConfig volume, MapperConfig mapper) {

  public static BossConfig from(Map map) {
    //System.out.printf("BossConfig.from: %s%n", map);
    Map vc = (Map) map.get("volume");
    if(vc == null) {
      throw new BossConfigException("Bad null BossConfig.volume");
    }
    Map mc = (Map)map.get("mapper");
    if(mc == null) {
      throw new BossConfigException("Bad null BossConfig.mapper");
    }
    return new BossConfig(VolumeConfig.from(vc), MapperConfig.from(mc));
  }
  
  public static BossConfig from(InputStream is) {
    try {
      YamlReader yr = new YamlReader(new InputStreamReader(is));
      return from((Map)yr.read());
    }
    catch(YamlException e) {
      throw new BossConfigException(e);
    }
  }
  
  public static Builder builder() {
    return new Builder();
  }
  
  
  
  public static class Builder {
    
    private BufferConfig.Type bufferType;
    
    private int bufferSize;
    
    private long maxCacheSize;
    
    private final List<BinCodec> codecs;
    
    private InvokeStrategy<ConstructFunction> mappingConstructStrategy;
    
    private InvokeStrategy<ExtractFunction> mappingExtractStrategy;
    
    private InvokeStrategy<InjectFunction> mappingInjectStrategy;
    
    private String volumeId;
    
    private Path volumeStorePath;
    
    public Builder() {
      this.codecs = new LinkedList<>();
      this.bufferType = BufferConfig.Type.HEAP;
    }
    
    public Builder addCodec(BinCodec codec) {
      codecs.add(Objects.requireNonNull(codec));
      return this;
    }
    
    public List<BinCodec> getCodecs() {
      return codecs;
    }

    public BufferConfig.Type getBufferType() {
      return bufferType;
    }

    public Builder setBufferType(BufferConfig.Type bufferType) {
      this.bufferType = bufferType;
      return this;
    }

    public int getBufferSize() {
      return bufferSize;
    }

    public Builder setBufferSize(int bufferSize) {
      this.bufferSize = bufferSize;
      return this;
    }

    public long getMaxCacheSize() {
      return maxCacheSize;
    }

    public Builder setMaxCacheSize(long maxCacheSize) {
      this.maxCacheSize = maxCacheSize;
      return this;
    }

    public InvokeStrategy<ConstructFunction> getMappingConstructStrategy() {
      return mappingConstructStrategy;
    }

    public Builder setMappingConstructStrategy(InvokeStrategy<ConstructFunction> mappingConstructStrategy) {
      this.mappingConstructStrategy = mappingConstructStrategy;
      return this;
    }
    
    public Builder addCombinedConstructStrategy(InvokeStrategy<ConstructFunction> is) {
      Objects.requireNonNull(is);
      if(mappingConstructStrategy == null 
          || !CombinedStrategy.class.isAssignableFrom(mappingConstructStrategy.getClass())) {
        mappingConstructStrategy = new CombinedStrategy<>();
      }
      CombinedStrategy<ConstructFunction> cs = (CombinedStrategy<ConstructFunction>)mappingConstructStrategy;
      int maxWeigth = cs.strategies().keySet().stream()
          .mapToInt(Integer::intValue).max().orElse(0);
      cs.put(maxWeigth+1, is);
      return this;
    }

    public InvokeStrategy<ExtractFunction> getMappingExtractStrategy() {
      return mappingExtractStrategy;
    }

    public Builder setMappingExtractStrategy(InvokeStrategy<ExtractFunction> mappingExtractStrategy) {
      this.mappingExtractStrategy = mappingExtractStrategy;
      return this;
    }

    public Builder addCombinedExtractStrategy(InvokeStrategy<ExtractFunction> is) {
      Objects.requireNonNull(is);
      if(mappingExtractStrategy == null 
          || !CombinedStrategy.class.isAssignableFrom(mappingExtractStrategy.getClass())) {
        mappingExtractStrategy = new CombinedStrategy<>();
      }
      CombinedStrategy<ExtractFunction> cs = (CombinedStrategy<ExtractFunction>)mappingExtractStrategy;
      int maxWeigth = cs.strategies().keySet().stream()
          .mapToInt(Integer::intValue).max().orElse(0);
      cs.put(maxWeigth+1, is);
      return this;
    }

    public InvokeStrategy<InjectFunction> getMappingInjectStrategy() {
      return mappingInjectStrategy;
    }

    public Builder setMappingInjectStrategy(InvokeStrategy<InjectFunction> mappingInjectStrategy) {
      this.mappingInjectStrategy = mappingInjectStrategy;
      return this;
    }

    public Builder addCombinedInjectStrategy(InvokeStrategy<InjectFunction> is) {
      Objects.requireNonNull(is);
      if(mappingInjectStrategy == null 
          || !CombinedStrategy.class.isAssignableFrom(mappingInjectStrategy.getClass())) {
        mappingInjectStrategy = new CombinedStrategy<>();
      }
      CombinedStrategy<InjectFunction> cs = (CombinedStrategy<InjectFunction>)mappingInjectStrategy;
      int maxWeigth = cs.strategies().keySet().stream()
          .mapToInt(Integer::intValue).max().orElse(0);
      cs.put(maxWeigth+1, is);
      return this;
    }

    public String getVolumeId() {
      return volumeId;
    }

    public Builder setVolumeId(String volumeId) {
      this.volumeId = volumeId;
      return this;
    }

    public Path getVolumeStorePath() {
      return volumeStorePath;
    }

    public Builder setVolumeStorePath(Path volumeStorePath) {
      this.volumeStorePath = volumeStorePath;
      return this;
    }
    
    public BossConfig build() {
      BufferConfig bc = new BufferConfig(bufferType, bufferSize, maxCacheSize);
      VolumeConfig vc = new VolumeConfig(volumeId, bc, volumeStorePath);
      BinContext ctx = BinContext.newContext();
      codecs.forEach(c->ctx.putCodec(c));
      ctx.mapper().constructStrategies().add(mappingConstructStrategy);
      ctx.mapper().extractStrategies().add(mappingExtractStrategy);
      ctx.mapper().injectStrategies().add(mappingInjectStrategy);
      MapperConfig mc = new MapperConfig(mappingConstructStrategy, mappingExtractStrategy, mappingInjectStrategy, ctx);
      return new BossConfig(vc, mc);
    }

    @Override
    public int hashCode() {
      int hash = 5;
      hash = 31 * hash + Objects.hashCode(this.bufferType);
      hash = 31 * hash + this.bufferSize;
      hash = 31 * hash + (int) (this.maxCacheSize ^ (this.maxCacheSize >>> 32));
      hash = 31 * hash + Objects.hashCode(this.codecs);
      hash = 31 * hash + Objects.hashCode(this.mappingConstructStrategy);
      hash = 31 * hash + Objects.hashCode(this.mappingExtractStrategy);
      hash = 31 * hash + Objects.hashCode(this.mappingInjectStrategy);
      hash = 31 * hash + Objects.hashCode(this.volumeId);
      hash = 31 * hash + Objects.hashCode(this.volumeStorePath);
      return hash;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      final Builder other = (Builder) obj;
      if (this.bufferSize != other.bufferSize) {
        return false;
      }
      if (this.maxCacheSize != other.maxCacheSize) {
        return false;
      }
      if (!Objects.equals(this.volumeId, other.volumeId)) {
        return false;
      }
      if (this.bufferType != other.bufferType) {
        return false;
      }
      if (!Objects.equals(this.codecs, other.codecs)) {
        return false;
      }
      if (!Objects.equals(this.mappingConstructStrategy, other.mappingConstructStrategy)) {
        return false;
      }
      if (!Objects.equals(this.mappingExtractStrategy, other.mappingExtractStrategy)) {
        return false;
      }
      if (!Objects.equals(this.mappingInjectStrategy, other.mappingInjectStrategy)) {
        return false;
      }
      return Objects.equals(this.volumeStorePath, other.volumeStorePath);
    }

    @Override
    public String toString() {
      return "BossConfig.Builder{" + "bufferType=" + bufferType + ", bufferSize=" + bufferSize + ", maxCacheSize=" + maxCacheSize + ", codecs=" + codecs + ", mappingConstructStrategy=" + mappingConstructStrategy + ", mappingExtractStrategy=" + mappingExtractStrategy + ", mappingInjectStrategy=" + mappingInjectStrategy + ", volumeId=" + volumeId + ", volumeStorePath=" + volumeStorePath + '}';
    }
    
  }
  
}
